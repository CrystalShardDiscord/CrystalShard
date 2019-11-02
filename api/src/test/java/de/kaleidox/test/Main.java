package de.kaleidox.test;

import de.comroid.crystalshard.api.Discord;
import de.comroid.crystalshard.api.entity.channel.Channel;
import de.comroid.crystalshard.api.event.channel.ChannelEvent;
import de.comroid.crystalshard.api.event.message.MessageSentEvent;
import de.comroid.crystalshard.api.listener.message.MessageSentListener;
import de.comroid.crystalshard.api.listener.model.ListenerAttachable;
import de.comroid.crystalshard.api.model.message.TextDecoration;

public class Main {
    private static final Discord API;

    static {
        API = Discord.builder()
                .setToken(System.getenv("token"))
                .build()
                .join();
    }

    public static void main(String[] args) {
        API.attachListener((MessageSentListener) event -> {
            if (event.getTriggeringMessage().getContent().equals("Hello CrystalShard!"))
                event.getTriggeringChannel()
                        .composeMessage()
                        .addText("Hello "+event.getTriggeringUser().getDiscriminatedName())
                        .send()
                        .join();
        });
    }
}
