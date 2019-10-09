package de.kaleidox.crystalshard.api.entity.user;

import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import de.kaleidox.crystalshard.adapter.Adapter;
import de.kaleidox.crystalshard.api.Discord;
import de.kaleidox.crystalshard.api.entity.Snowflake;
import de.kaleidox.crystalshard.api.entity.channel.PrivateTextChannel;
import de.kaleidox.crystalshard.api.entity.guild.Guild;
import de.kaleidox.crystalshard.api.event.user.UserEvent;
import de.kaleidox.crystalshard.api.listener.model.ListenerAttachable;
import de.kaleidox.crystalshard.api.listener.user.UserAttachableListener;
import de.kaleidox.crystalshard.api.model.Mentionable;
import de.kaleidox.crystalshard.api.model.message.MessageAuthor;
import de.kaleidox.crystalshard.api.model.message.Messageable;
import de.kaleidox.crystalshard.core.api.cache.Cacheable;
import de.kaleidox.crystalshard.core.api.rest.DiscordEndpoint;
import de.kaleidox.crystalshard.core.api.rest.RestMethod;
import de.kaleidox.crystalshard.util.annotation.IntroducedBy;
import de.kaleidox.crystalshard.util.model.FileType;
import de.kaleidox.crystalshard.util.model.ImageHelper;
import de.kaleidox.crystalshard.util.model.serialization.JsonDeserializable;
import de.kaleidox.crystalshard.util.model.serialization.JsonTrait;
import de.kaleidox.crystalshard.util.model.serialization.JsonTraits;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;

import static de.kaleidox.crystalshard.util.annotation.IntroducedBy.ImplementationSource.API;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.identity;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.simple;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.underlyingCollective;

@JsonTraits(User.Trait.class)
public interface User extends Messageable, MessageAuthor, Mentionable, Snowflake, Cacheable, ListenerAttachable<UserAttachableListener<? extends UserEvent>>, JsonDeserializable {
    default String getUsername() {
        return getTraitValue(Trait.USERNAME);
    }

    default String getDiscriminator() {
        return getTraitValue(Trait.DISCRIMINATOR);
    }

    default URL getAvatarURL() {
        return wrapTraitValue(Trait.AVATAR_HASH)
                .map(hash -> ImageHelper.USER_AVATAR.url(FileType.PNG, getID(), hash))
                .orElseGet(() -> ImageHelper.DEFAULT_USER_AVATAR.url(FileType.PNG, getDiscriminator()));
    }

    default boolean isBot() {
        return getTraitValue(Trait.BOT);
    }

    default boolean hasMFA() {
        return getTraitValue(Trait.MFA);
    }

    default Optional<Locale> getLocale() {
        return wrapTraitValue(Trait.LOCALE);
    }

    default Optional<Boolean> isVerified() {
        return wrapTraitValue(Trait.VERIFIED);
    }

    default Optional<String> getEMailAddress() {
        return wrapTraitValue(Trait.EMAIL);
    }

    default @MagicConstant(flagsFromClass = Flags.class) int getFlags() {
        return getTraitValue(Trait.FLAGS);
    }

    default Optional<PremiumType> getPremiumType() {
        return wrapTraitValue(Trait.PREMIUM_TYPE);
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/user#create-dm")
    CompletableFuture<PrivateTextChannel> openPrivateMessageChannel();

    Optional<GuildMember> asGuildMember(Guild guild);

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/user#get-user")
    static CompletableFuture<User> requestUser(Discord api, long id) {
        return Adapter.<User>request(api)
                .endpoint(DiscordEndpoint.USER, id)
                .method(RestMethod.GET)
                .executeAs(data -> api.getCacheManager()
                        .updateOrCreateAndGet(User.class, id, data));
    }

    interface Trait extends Snowflake.Trait {
        JsonTrait<String, String> USERNAME = identity(JsonNode::asText, "username");
        JsonTrait<String, String> DISCRIMINATOR = identity(JsonNode::asText, "discriminator");
        JsonTrait<String, String> AVATAR_HASH = identity(JsonNode::asText, "avatar");
        JsonTrait<Boolean, Boolean> BOT = identity(JsonNode::asBoolean, "bot");
        JsonTrait<Boolean, Boolean> MFA = identity(JsonNode::asBoolean, "mfa_enabled");
        JsonTrait<String, Locale> LOCALE = simple(JsonNode::asText, "locale", Locale::forLanguageTag);
        JsonTrait<Boolean, Boolean> VERIFIED = identity(JsonNode::asBoolean, "verified");
        JsonTrait<String, String> EMAIL = identity(JsonNode::asText, "email");
        JsonTrait<Integer, Integer> FLAGS = identity(JsonNode::asInt, "flags");
        JsonTrait<Integer, PremiumType> PREMIUM_TYPE = simple(JsonNode::asInt, "premium_type", PremiumType::valueOf);
    }

    @JsonTraits(Connection.Trait.class)
    interface Connection extends JsonDeserializable {
        default String getID() {
            return getTraitValue(Trait.ID);
        }

        default String getName() {
            return getTraitValue(Trait.NAME);
        }

        default String getType() {
            return getTraitValue(Trait.TYPE);
        }

        default boolean isRevoked() {
            return getTraitValue(Trait.REVOKED);
        }

        default Collection<Guild.Integration> getIntegrations() {
            return getTraitValue(Trait.INTEGRATIONS);
        }

        default boolean isVerified() {
            return getTraitValue(Trait.VERIFIED);
        }

        default boolean hasFriendSync() {
            return getTraitValue(Trait.FRIEND_SYNC);
        }

        default boolean showActivities() {
            return getTraitValue(Trait.SHOW_ACTIVITY);
        }

        default Visibility getVisibility() {
            return getTraitValue(Trait.VISIBILITY);
        }

        interface Trait {
            JsonTrait<String, String> ID = identity(JsonNode::asText, "id");
            JsonTrait<String, String> NAME = identity(JsonNode::asText, "name");
            JsonTrait<String, String> TYPE = identity(JsonNode::asText, "type");
            JsonTrait<Boolean, Boolean> REVOKED = identity(JsonNode::asBoolean, "revoked");
            JsonTrait<ArrayNode, Collection<Guild.Integration>> INTEGRATIONS = underlyingCollective("integrations", Guild.Integration.class);
            JsonTrait<Boolean, Boolean> VERIFIED = identity(JsonNode::asBoolean, "verified");
            JsonTrait<Boolean, Boolean> FRIEND_SYNC = identity(JsonNode::asBoolean, "friend_sync");
            JsonTrait<Boolean, Boolean> SHOW_ACTIVITY = identity(JsonNode::asBoolean, "show_activity");
            JsonTrait<Integer, Visibility> VISIBILITY = simple(JsonNode::asInt, "visibility", Visibility::valueOf);
        }

        enum Visibility {
            NONE(0),

            EVERYONE(1);

            public final int value;

            Visibility(int value) {
                this.value = value;
            }

            public static @Nullable Visibility valueOf(int value) {
                for (Visibility visibility : values())
                    if (visibility.value == value)
                        return visibility;

                return null;
            }
        }
    }

    @SuppressWarnings("PointlessBitwiseExpression") final class Flags {
        public static final int NONE = 0;

        public static final int DISCORD_EMPLOYEE = 1 << 0;

        public static final int DISCORD_PARTNER = 1 << 1;

        public static final int HYPESQUAD_EVENTS = 1 << 2;

        public static final int BUG_HUNTER = 1 << 3;

        public static final int HOUSE_BRAVERY = 1 << 6;

        public static final int HOUSE_BRILLIANCE = 1 << 7;

        public static final int HOUSE_BALANCE = 1 << 8;

        public static final int EARLY_SUPPORTER = 1 << 9;

        public static final int TEAM_USER = 1 << 10;
    }

    enum PremiumType {
        NITRO_CLASSIC(1),

        NITRO(2);

        public final int value;

        PremiumType(int value) {
            this.value = value;
        }

        public static @Nullable PremiumType valueOf(int value) {
            for (PremiumType type : values())
                if (type.value == value)
                    return type;

            return null;
        }
    }
}
