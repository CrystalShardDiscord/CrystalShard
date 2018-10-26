package de.kaleidox.crystalshard.core.net.request;

import de.kaleidox.crystalshard.main.CrystalShard;
import de.kaleidox.crystalshard.main.items.DiscordItem;
import de.kaleidox.crystalshard.util.helpers.ArrayHelper;
import de.kaleidox.crystalshard.util.helpers.UrlHelper;
import de.kaleidox.crystalshard.util.objects.markers.IDPair;

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
    private final static String                                BASE_URL       = "https://discordapp.com/api/v";
    private final        Location                              location;
    private final        URL                                   url;
    private final        String                                firstParam;
    
    private Endpoint(Location location, URL url, String[] params) {
        this.location = location;
        this.url = url;
        this.firstParam = (params.length == 0 ? null : params[0]);
    }
    
    // Override Methods
    @Override
    public String toString() {
        return getUrl().toExternalForm();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Endpoint) {
            Endpoint target = (Endpoint) obj;
            return target.url.toExternalForm().equalsIgnoreCase(this.url.toExternalForm());
        }
        return false;
    }
    
    public boolean sameRatelimit(Object obj) {
        if (obj instanceof Endpoint) {
            Endpoint target = (Endpoint) obj;
            if (Objects.nonNull(this.firstParam)) return target.firstParam.equals(this.firstParam);
            else return target.location == this.location;
        }
        return false;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public URL getUrl() {
        return url;
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
        CUSTOM_EMOJI_SPECIFIC("/guilds/%s/emoji/%s"),
        GATEWAY("/gateway"),
        GATEWAY_BOT("/gateway/bot"),
        GUILD("/guilds"),
        GUILD_CHANNEL("/guilds/%s/channels"),
        GUILD_INVITE("/guilds/%s/invites"),
        GUILD_MEMBER("/guilds/%s/members/%s"),
        GUILD_MEMBER_ROLE("/guilds/%s/members/%s/roles/%s"),
        GUILD_PRUNE("/guilds/%s/prune"),
        GUILD_SPECIFIC("/guilds/%s"),
        GUILD_WEBHOOK("/guilds/%s/webhooks"),
        GUILD_VANITY_INVITE("/guilds/%s/vanity-url"),
        INVITE("/invites/%s"),
        GUILD_INTEGRATIONS("/guilds/%s/integrations"),
        MESSAGE("/channels/%s/messages"),
        MESSAGES_BULK_DELETE("/channels/%s/messages/bulk-delete"),
        MESSAGE_DELETE("/channels/%s/messages"),
        MESSAGE_SPECIFIC("/channels/%s/messages/%s"),
        PINS("/channels/%s/pins"),
        PIN_MESSAGE("/channels/%s/pins/%s"),
        REACTIONS("/channels/%s/messages/%s/reactions", 250),
        REACTION_OWN("/channels/%s/messages/%s/reactions/%s/@me", 250),
        REACTION_USER("/channels/%s/messages/%s/reactions/%s/%s", 250),
        GUILD_ROLES("/guilds/%s/roles"),
        GUILD_ROLE_SPECIFIC("/guilds/%s/role/%s"),
        SELF_CHANNELS("/users/@me/channels"),
        SELF_GUILD("/users/@me/guilds/%s"),
        SELF_INFO("/oauth2/applications/@me"),
        SELF_NICKNAME("/guilds/%s/members/@me/nick"),
        USER("/users/%s"),
        USER_CHANNEL("/users/@me/channels"),
        WEBHOOK("/webhooks/%s");
        private final String location;
        private final int    hardcodedRatelimit;
        
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
            int end = (location.substring(location.length() - 2).equalsIgnoreCase("%s") ? 1 : 0);
            return splitted + end;
        }
        
        public Endpoint toEndpoint(Object... parameter) {
            String[] params = new String[parameter.length];
            int parameterCount = getParameterCount();
            if (parameter.length == 1 && parameterCount == 2) {
                if (parameter[0] instanceof IDPair) {
                    IDPair pair = (IDPair) parameter[0];
                    return toEndpoint(pair.getOne(), pair.getTwo());
                }
            }
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
                boolean olderInstanceExists = olderInstances.entrySet().stream().anyMatch(entry -> ArrayHelper.compare(entry.getKey(), params) == 0);
                if (olderInstanceExists) {
                    for (Map.Entry<String[], Endpoint> entry : olderInstances.entrySet()) {
                        if (ArrayHelper.compare(entry.getKey(), params) == 0) {
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
            } else throw new IllegalArgumentException("Too " + (parameterCount > params.length ? "few" : "many") + " parameters!");
        }
    }
    
// Static membe
    public static Endpoint of(Location location, Object... parameter) {
        return location.toEndpoint(parameter);
    }
}
