package de.kaleidox.crystalshard.core.concurrent;

import de.kaleidox.crystalshard.internal.DiscordInternal;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.logging.Logger;
import de.kaleidox.util.CompletableFutureExtended;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.function.*;

/**
 * This class is the main concurrent implementation.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ThreadPool {
    public final static Supplier<IllegalCallerException> BOT_THREAD_EXCEPTION = () ->
            new IllegalCallerException("That method may only be called from a bot-own " +
                    "thread, such as in listeners or scheduler tasks. You may not use it from contexts like " +
                    "CompletableFuture#thenAcceptAsync or such. User ThreadPool#isBotOwnThread to check if the current " +
                    "Thread belongs to the Bot.");
    private final static Logger logger = new Logger(ThreadPool.class);
    private final ConcurrentHashMap<Worker, AtomicBoolean> threads;
    private final DiscordInternal discord;
    private final int maxSize;
    private final LinkedBlockingQueue<Task> queue;
    private final AtomicInteger busyThreads = new AtomicInteger(0);
    private final Factory factory;
    private Executor executor;
    private ScheduledExecutorService scheduler;
    private String name;
    private List<Worker> factoriedThreads = new ArrayList<>();

    /**
     * Creates a new, unlimited ThreadPool for the specified discord object.
     *
     * @param discord The discord object to attach to this ThreadPool.
     */
    public ThreadPool(Discord discord) {
        this(discord, -1, "CrystalShard Main Worker");
        this.executor = new BotOwn(this);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(factory);

        scheduler.scheduleAtFixedRate(this::cleanupThreads, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * Creates a new limited ThreadPool for the specified discord object.
     *
     * @param discordObject The discord object to attach to this ThreadPool.
     * @param maxSize       The maximum capacity of workers for this thread. -1 for infinite.
     * @param name          The name for this ThreadPool. New Workers will get this name attached.
     */
    public ThreadPool(Discord discordObject, int maxSize, String name) {
        this.discord = (DiscordInternal) discordObject;
        this.maxSize = maxSize;
        this.threads = new ConcurrentHashMap<>();
        this.queue = new LinkedBlockingQueue<>();
        this.name = name;
        this.factory = new Factory();

        execute(() -> logger.deeptrace("New ThreadPool created: " + name));
    }

    /**
     * Returns whether the current thread is a BotOwn thread; meaning the current thread is a {@link Worker} Thread.
     * This method is required if you statically want to get a Discord instance using {@link #getThreadDiscord()}.
     *
     * @return Whether the current thread is a bot own thread.
     */
    public static boolean isBotOwnThread() {
        return Thread.currentThread() instanceof Worker;
    }

    /**
     * Checks if the current thread is a BotOwn thread (see {@link #isBotOwnThread()}, and if so, returns the
     * {@link Worker} Thread.
     *
     * @return The worker thread.
     * @throws IllegalCallerException If the thread is not a Bot-Own thread.
     */
    public static Worker requireBotOwnThread() {
        Thread thread = Thread.currentThread();
        if (thread instanceof Worker) {
            return (Worker) thread;
        } else throw BOT_THREAD_EXCEPTION.get();
    }

    /**
     * Checks if the current thread is a BotOwn thread (see {@link #isBotOwnThread()}, and if so, returns the
     * {@link Worker} Thread.
     *
     * @param customMessage A custom message to show in the possible exception.
     * @return The worker thread.
     * @throws IllegalCallerException If the thread is not a Bot-Own thread.
     */
    public static Worker requireBotOwnThread(String customMessage) {
        Thread thread = Thread.currentThread();
        if (thread instanceof Worker) {
            return (Worker) thread;
        } else throw new IllegalCallerException(customMessage);
    }

    /**
     * Gets the Discord object attached to the current Thread, if the current thread is a {@link Worker} thread.
     * Otherwise throws a {@link IllegalCallerException}.
     *
     * @return The discord object.
     * @throws IllegalCallerException If the thread is not a Bot-Own thread.
     * @see #requireBotOwnThread()
     */
    public static Discord getThreadDiscord() {
        return requireBotOwnThread().getDiscord();
    }

    /**
     * Used to exclude deeptracing of tasks that come from CompletableFuture async methods.
     *
     * @param task The task to check.
     * @return Whether the task is most likely from an async stage.
     */
    private static boolean nonFutureTask(Runnable task) {
        return !task.toString().toLowerCase().contains("future");
    }

    /**
     * Gets the executor for this ThreadPool.
     * This will be useful for CompletableFuture async methods that use an executor.
     *
     * @return The executor.
     * @see CompletableFutureExtended
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     * @see CompletableFuture#acceptEitherAsync(CompletionStage, Consumer, Executor)
     * @see CompletableFuture#applyToEitherAsync(CompletionStage, Function, Executor)
     * @see CompletableFuture#completeAsync(Supplier, Executor)
     * @see CompletableFuture#handleAsync(BiFunction, Executor)
     * @see CompletableFuture#runAfterBothAsync(CompletionStage, Runnable, Executor)
     * @see CompletableFuture#runAfterEitherAsync(CompletionStage, Runnable, Executor)
     * @see CompletableFuture#runAsync(Runnable, Executor)
     * @see CompletableFuture#thenAcceptAsync(Consumer, Executor)
     * @see CompletableFuture#thenAcceptBothAsync(CompletionStage, BiConsumer, Executor)
     * @see CompletableFuture#thenApplyAsync(Function, Executor)
     * @see CompletableFuture#thenCombineAsync(CompletionStage, BiFunction, Executor)
     * @see CompletableFuture#thenComposeAsync(Function, Executor)
     * @see CompletableFuture#thenRunAsync(Runnable, Executor)
     * @see CompletableFuture#whenCompleteAsync(BiConsumer, Executor)
     */
    public Executor getExecutor() {
        return executor;
    }

    public DiscordInternal getDiscord() {
        return discord;
    }

    /**
     * This method is used internally to start heartbeating to discord.
     * Do not call this method on your own.
     *
     * @param heartbeat The heartbeat interval.
     */
    public void startHeartbeat(long heartbeat) {
        scheduler.scheduleAtFixedRate(() ->
                discord.getWebSocket().heartbeat(), heartbeat, heartbeat, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedules a new task.
     * If there is no limit on the ThreadPool or the limit is not hit, ensures that there is a Worker available.
     *
     * @param task        The task to execute.
     * @param description A short description on what this task does.
     */
    public void execute(Runnable task, String... description) {
        synchronized (queue) {
            if (threads.size() < maxSize || maxSize == -1) {
                if (busyThreads.get() <= queue.size())
                    factory.getOrCreateWorker(); // Ensure there is a worker available, if the limit is not hit.
            }
            queue.add(new Task(task, description));
            queue.notify();
        }
    }

    /**
     * Gets the Scheduler for this ThreadPool.
     * This will be useful for e.g. updating the discord status.
     *
     * @return The ScheduledExecutorService for this ThreadPool.
     */
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    /**
     * Removes terminated Threads from the {@code factoriedThreads} list,
     * and decrements the name counter for each thread.
     */
    void cleanupThreads() {
        factoriedThreads.stream()
                .filter(worker -> worker.getState() == Thread.State.TERMINATED) // only exited threads
                .peek(Worker::interrupt) // interrupt the thread
                .peek(worker -> factory.nameCounter.decrementAndGet()) // decrement the id counter by one each thread
                .forEach(factoriedThreads::remove); // remove the thread from the list
    }

    public class Factory implements ThreadFactory {
        private final AtomicInteger nameCounter = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Worker worker = new Worker(r, discord, nameCounter.getAndIncrement());
            factoriedThreads.add(worker);
            return worker;
        }

        /**
         * Checks if an older {@link Worker} threads is available, otherwise creates a new Worker thread and returns it.
         *
         * @return A {@link Worker} thread.
         */
        public Worker getOrCreateWorker() {
            return threads.entrySet()
                    .stream()
                    .filter(entry -> !entry.getValue().get())
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElseGet(() -> {
                        Worker worker = new Worker(discord, nameCounter.getAndIncrement());
                        threads.put(worker, worker.isBusy);
                        //logger.deeptrace("New worker created: " + worker.getName());
                        if (!worker.isAlive()) {
                            worker.start();
                            //logger.deeptrace("Worker Thread \"" + worker.getName() + "\" started!");
                        }
                        return worker;
                    });
        }
    }

    /**
     * This class represents an implementation of an executor interface to this current ThreadPool.
     */
    public class BotOwn implements Executor {
        private final ThreadPool pool;

        /**
         * Creates a new executor instance.
         *
         * @param pool The thread pool to execute in.
         */
        BotOwn(ThreadPool pool) {
            this.pool = pool;
        }

        @Override
        public void execute(Runnable command) {
            pool.execute(command);
        }
    }

    /**
     * This class represents a bot-own worker thread.
     */
    public class Worker extends Thread {
        private final DiscordInternal discord;
        private final AtomicMarkableReference<Task> nextTask =
                new AtomicMarkableReference<>(null, false);
        private final AtomicBoolean isBusy;
        private final boolean runnableAttachedThread;

        /**
         * Creates a new {@link Worker} thread.
         *
         * @param discord The discord object to attach this worker to.
         * @param id      The id of this worker thread. If the pool's maximum size is {@code 1}, this gets ignored.
         */
        Worker(DiscordInternal discord, int id) {
            super(name == null ? ("Worker Thread #" + id) : name + " Thread" + (maxSize == 1 ? "" : " #" + id));
            this.discord = discord;
            this.isBusy = new AtomicBoolean(false);
            this.runnableAttachedThread = false;
        }

        Worker(Runnable initTask, DiscordInternal discord, int id) {
            super(initTask, name == null ?
                    ("Worker Thread #" + id) : name + " Thread" + (maxSize == 1 ? "" : " #" + id));
            this.discord = discord;
            this.isBusy = new AtomicBoolean(true);
            this.runnableAttachedThread = true;
        }

        /**
         * Attaches a new Runnable to this worker.
         * This method should not be used for chaining tasks, but for attaching runnables to worker threads in the
         * {@code java.lang.Thread.State.RUNNABLE} state.
         * For chaining tasks, use {@link #execute(Runnable, String...)} instead.
         *
         * @param task The task to attach.
         */
        void attachTask(Task task) {
            synchronized (queue) {
                //noinspection StatementWithEmptyBody
                if (isBusy.get()) {
                    execute(task); // if current worker is busy, add task to the queue
                } else {
                    this.nextTask.set(task, true);
                    queue.notify();
                }
            }
        }

        /**
         * Gets the discord instance attached to the current worker thread.
         *
         * @return The discord object.
         * @see #getThreadDiscord()
         */
        public DiscordInternal getDiscord() {
            return discord;
        }

        @Override
        public void run() {
            if (!runnableAttachedThread) {
                Task task = null;
                //noinspection InfiniteLoopStatement
                while (true) {
                    synchronized (queue) {
                        try {
                            while (queue.isEmpty() && !nextTask.isMarked()) {
                                try {
                                    queue.wait();
                                } catch (InterruptedException e) {
                                    logger.exception(e);
                                }
                            }
                            task = nextTask.isMarked() ? nextTask.getReference() : queue.poll();
                            assert task != null;
                            busy();
                            /*if (nonFutureTask(task))
                                logger.deeptrace("Running " + (nextTask.isMarked() ? "attached" : "scheduled") +
                                        " task #" + task.hashCode() + (task.hasDescription() ?
                                        " with description: " + task.getDescription() : ""));*/
                            task.run();
                            /*if (nonFutureTask(task))
                                logger.deeptrace((nextTask.isMarked() ? "Attached" : "Scheduled") +
                                        " task #" + task.hashCode() + " finished.");*/
                            unbusy();
                            if (nextTask.isMarked())
                                nextTask.set(null, false); // if nextTask is set, unset it, because its being run
                        } catch (Throwable e) {
                            assert task != null;
                            /*if (nonFutureTask(task))
                                logger.exception(e, (nextTask.isMarked() ? "Attached" : "Scheduled") +
                                        " task " + ("#" + task.hashCode()) + " finished with an exception:");*/
                        }
                    }
                }
            } else {
                super.run();
            }
        }

        private void busy() {
            isBusy.set(true);
            busyThreads.incrementAndGet();
        }

        private void unbusy() {
            isBusy.set(false);
            busyThreads.decrementAndGet();
        }
    }

    private class Task implements Runnable {
        private final Runnable runnable;
        private final String[] description;

        Task(Runnable runnable, String... description) {
            this.runnable = runnable;
            this.description = description;
        }

        public boolean hasDescription() {
            return description.length != 0;
        }

        public String getDescription() {
            return description.length == 0 ? "No task description." : String.join(" ", description);
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                logger.exception(e);
            }
        }
    }
}
