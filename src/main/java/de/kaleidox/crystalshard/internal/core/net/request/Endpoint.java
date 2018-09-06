package de.kaleidox.crystalshard.internal.core.net.request;

import de.kaleidox.crystalshard.main.CrystalShard;
import de.kaleidox.crystalshard.main.items.DiscordItem;
import de.kaleidox.util.helpers.UrlHelper;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This enum contains all endpoints which we may use.
 */
public class Endpoint {
    private final static ConcurrentHashMap<String[], Endpoint> olderInstances = new ConcurrentHashMap<>();
    private final static String BASE_URL = "https://discordapp.com/api/v";
    private final Location location;
    private final URL url;
    private final String[] params;
    private final String firstParam;

    private Endpoint(Location location, URL url, String[] params) {
        this.location = location;
        this.url = url;
        this.params = params;
        this.firstParam = (params.length == 0 ? null : params[0]);
    }

    public Location getLocation() {
        return location;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Endpoint) {
            Endpoint target = (Endpoint) obj;
            if (Objects.nonNull(this.firstParam))
                return target.url.equals(url) &&
                        target.firstParam.equals(this.firstParam);
            else
                return target.url.equals(url);
        }
        return false;
    }

    @Override
    public String toString() {
        return getUrl().toExternalForm();
    }

    public static Endpoint of(Location location, Object... parameter) {
        return location.toEndpoint(parameter);
    }

    public enum Location {
        AUDIT_LOG("/guilds/%s/audit-logs"),
        BAN("/guilds/%s/bans"),
        CHANNEL("/channels/%s"),
        CHANNEL_INVITE("/channels/%s/invites"),
        CHANNEL_TYPING("/channels/%s/typing"),
        CHANNEL_WEBHOOK("/channels/%s/webhooks"),
        CURRENT_USER("/users/@me"),
        CUSTOM_EMOJI("/guilds/%s/emojis"),
        GATEWAY("/gateway"),
        GATEWAY_BOT("/gateway/bot"),
        GUILD("/guilds"),
        GUILDS("/guilds/%s"),
        GUILD_CHANNEL("/guilds/%s/channels"),
        GUILD_INVITE("/guilds/%s/invites"),
        GUILD_MEMBER("/guilds/%s/members/%s"),
        GUILD_MEMBER_ROLE("/guilds/%s/members/%s/roles/%s"),
        GUILD_PRUNE("/guilds/%s/prune"),
        GUILD_WEBHOOK("/guilds/%s/webhooks"),
        INVITE("/invites/%s"),
        MESSAGE("/channels/%s/messages"),
        MESSAGES_BULK_DELETE("/channels/%s/messages/bulk-delete"),
        MESSAGE_DELETE("/channels/%s/messages"),
        OWN_NICKNAME("/guilds/%s/members/@me/nick"),
        PINS("/channels/%s/pins"),
        REACTION("/channels/%s/messages/%s/reactions", 250),
        ROLE("/guilds/%s/roles"),
        SELF_GUILD("/users/@me/guilds/%s"),
        SELF_CHANNELS("/users/@me/channels"),
        SELF_INFO("/oauth2/applications/@me"),
        USER("/users/%s"),
        USER_CHANNEL("/users/@me/channels"),
        WEBHOOK("/webhooks/%s");

        private final String location;
        private final int hardcodedRatelimit;

        Location(String location) {
            this(location, -1);
        }

        Location(String location, int hardcodedRatelimit) {
            this.location = location;
            this.hardcodedRatelimit = hardcodedRatelimit;
        }

        public String getLocation() {
            return location;
        }

        public Optional<Integer> getHardcodedRatelimit() {
            return Optional.ofNullable(hardcodedRatelimit == -1 ? null : hardcodedRatelimit);
        }

        public int getParameterCount() {
            int splitted = location.split("%s").length - 1;
            int end = (location.indexOf("%s") == location.length() - 2 ? 1 : 0);
            return splitted + end;
        }

        public Endpoint toEndpoint(Object... parameter) {
            String[] params = new String[parameter.length];
            int parameterCount = getParameterCount();

            for (int i = 0; i < parameter.length; i++) {
                Object x = parameter[i];

                if (x instanceof DiscordItem) {
                    params[i] = Long.toUnsignedString(((DiscordItem) x).getId());
                } else if (x instanceof Long) {
                    params[i] = Long.toUnsignedString((Long) x);
                } else {
                    params[i] = x.toString();
                }
            }
            if (parameterCount == params.length) {
                boolean olderInstanceExists = olderInstances.entrySet()
                        .stream()
                        .anyMatch(entry -> Arrays.compare(entry.getKey(), params) == 0);
                if (olderInstanceExists) {
                    for (Map.Entry<String[], Endpoint> entry : olderInstances.entrySet()) {
                        if (Arrays.compare(entry.getKey(), params) == 0) {
                            return entry.getValue();
                        }
                    }
                    // no instance could be found
                }
                String of = String.format(BASE_URL + CrystalShard.API_VERSION + location, (Object[]) params);
                URL url = UrlHelper.require(of);
                Endpoint endpoint = new Endpoint(this, url, params);
                olderInstances.putIfAbsent(params, endpoint);
                return endpoint;
            } else throw new IllegalArgumentException("Too " + (parameterCount > params.length ? "few" : "many") +
                    " parameters!");
        }
    }
}
