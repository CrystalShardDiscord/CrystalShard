package de.kaleidox.crystalshard.api.entity.guild;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.swing.plaf.synth.Region;

import de.kaleidox.crystalshard.adapter.Adapter;
import de.kaleidox.crystalshard.api.Discord;
import de.kaleidox.crystalshard.api.entity.Snowflake;
import de.kaleidox.crystalshard.api.entity.channel.Channel;
import de.kaleidox.crystalshard.api.entity.channel.GuildChannel;
import de.kaleidox.crystalshard.api.entity.channel.GuildTextChannel;
import de.kaleidox.crystalshard.api.entity.channel.GuildVoiceChannel;
import de.kaleidox.crystalshard.api.entity.emoji.CustomEmoji;
import de.kaleidox.crystalshard.api.entity.guild.webhook.Webhook;
import de.kaleidox.crystalshard.api.entity.user.GuildMember;
import de.kaleidox.crystalshard.api.entity.user.User;
import de.kaleidox.crystalshard.api.listener.guild.GuildAttachableListener;
import de.kaleidox.crystalshard.api.listener.model.ListenerAttachable;
import de.kaleidox.crystalshard.api.model.guild.ban.Ban;
import de.kaleidox.crystalshard.api.model.guild.invite.Invite;
import de.kaleidox.crystalshard.api.model.user.Presence;
import de.kaleidox.crystalshard.api.model.voice.VoiceRegion;
import de.kaleidox.crystalshard.api.model.voice.VoiceState;
import de.kaleidox.crystalshard.core.api.cache.CacheManager;
import de.kaleidox.crystalshard.core.api.cache.Cacheable;
import de.kaleidox.crystalshard.core.api.rest.DiscordEndpoint;
import de.kaleidox.crystalshard.core.api.rest.HTTPStatusCodes;
import de.kaleidox.crystalshard.core.api.rest.RestMethod;
import de.kaleidox.crystalshard.util.Util;
import de.kaleidox.crystalshard.util.annotation.IntroducedBy;
import de.kaleidox.crystalshard.util.model.FileType;
import de.kaleidox.crystalshard.util.model.ImageHelper;
import de.kaleidox.crystalshard.util.model.serialization.JsonDeserializable;
import de.kaleidox.crystalshard.util.model.serialization.JsonTrait;
import de.kaleidox.crystalshard.util.model.serialization.JsonTraits;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.jetbrains.annotations.Nullable;

import static de.kaleidox.crystalshard.util.annotation.IntroducedBy.ImplementationSource.API;
import static de.kaleidox.crystalshard.util.annotation.IntroducedBy.ImplementationSource.GETTER;
import static de.kaleidox.crystalshard.util.annotation.IntroducedBy.ImplementationSource.PRODUCTION;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.api;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.cache;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.identity;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.simple;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.underlying;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.underlyingCollective;

@JsonTraits(Guild.Trait.class)
public interface Guild extends Snowflake, ListenerAttachable<GuildAttachableListener>, Cacheable {
    @IntroducedBy(API)
    CompletableFuture<Collection<Webhook>> requestWebhooks();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/emoji#list-guild-emojis")
    CompletableFuture<Collection<CustomEmoji>> requestEmojis();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/emoji#get-guild-emoji")
    default CompletableFuture<CustomEmoji> requestEmoji(long id) {
        return Adapter.<CustomEmoji>request(getAPI())
                .endpoint(DiscordEndpoint.CUSTOM_EMOJI_SPECIFIC, getID(), id)
                .method(RestMethod.GET)
                .executeAs(data -> getAPI().getCacheManager().updateOrCreateAndGet(CustomEmoji.class, id, data));
    }

    @IntroducedBy(GETTER)
    default String getName() {
        return getTraitValue(Trait.NAME);
    }

    @IntroducedBy(GETTER)
    default Optional<URL> getIconURL() {
        return wrapTraitValue(Trait.ICON_HASH)
                .map(hash -> ImageHelper.GUILD_ICON.url(FileType.PNG, getID(), hash));
    }

    @IntroducedBy(GETTER)
    default Optional<URL> getSplashURL() {
        return wrapTraitValue(Trait.SPLASH_HASH)
                .map(hash -> ImageHelper.GUILD_SPLASH.url(FileType.PNG, getID(), hash));
    }

    @IntroducedBy(GETTER)
    default Optional<GuildMember> getOwner() {
        return wrapTraitValue(Trait.OWNER)
                .flatMap(this::getMember);
    }

    @IntroducedBy(GETTER)
    default Region getRegion() {
        return getTraitValue(Trait.REGION);
    }

    @IntroducedBy(GETTER)
    default Optional<GuildVoiceChannel> getAFKChannel() {
        return wrapTraitValue(Trait.AFK_CHANNEL);
    }

    @IntroducedBy(GETTER)
    default Duration getAFKTimeout() {
        return getTraitValue(Trait.AFK_TIMEOUT);
    }

    @IntroducedBy(GETTER)
    default boolean isEmbeddable() {
        return getTraitValue(Trait.EMBEDDABLE);
    }

    @IntroducedBy(GETTER)
    default Optional<GuildChannel> getEmbedChannel() {
        return wrapTraitValue(Trait.EMBED_CHANNEL);
    }

    @IntroducedBy(GETTER)
    default VerificationLevel getVerificationLevel() {
        return getTraitValue(Trait.VERIFICATION_LEVEL);
    }

    @IntroducedBy(GETTER)
    default DefaultMessageNotificationLevel getDefaultMessageNotificationLevel() {
        return getTraitValue(Trait.DEFAULT_MESSAGE_NOTIFICATION_LEVEL);
    }

    @IntroducedBy(GETTER)
    default ExplicitContentFilterLevel getExplicitContentFilterLevel() {
        return getTraitValue(Trait.EXPLICIT_CONTENT_FILTER_LEVEL);
    }

    @IntroducedBy(GETTER)
    default Collection<Role> getRoles() {
        return getTraitValue(Trait.ROLES);
    }

    @IntroducedBy(GETTER)
    default Collection<CustomEmoji> getEmojis() {
        return getTraitValue(Trait.EMOJIS);
    }

    @IntroducedBy(GETTER)
    default Collection<Feature> getGuildFeatures() {
        return getTraitValue(Trait.FEATURES);
    }

    @IntroducedBy(GETTER)
    default MFALevel getMFALevel() {
        return getTraitValue(Trait.MFA_LEVEL);
    }

    @IntroducedBy(GETTER)
    default Optional<Snowflake> getOwnerApplicationID() {
        return wrapTraitValue(Trait.OWNER_APPLICATION_ID);
    }

    @IntroducedBy(GETTER)
    default boolean isWidgetable() {
        return getTraitValue(Trait.WIDGETABLE);
    }

    @IntroducedBy(GETTER)
    default Optional<GuildChannel> getWidgetChannel() {
        return wrapTraitValue(Trait.WIDGET_CHANNEL);
    }

    @IntroducedBy(GETTER)
    default Optional<GuildTextChannel> getSystemChannel() {
        return wrapTraitValue(Trait.SYSTEM_CHANNEL);
    }

    @IntroducedBy(GETTER)
    default Optional<Instant> getJoinedAt() {
        return wrapTraitValue(Trait.JOINED_AT);
    }

    @IntroducedBy(GETTER)
    default boolean isConsideredLarge() {
        return getTraitValue(Trait.LARGE);
    }

    @IntroducedBy(GETTER)
    default boolean isUnavailable() {
        return getTraitValue(Trait.UNAVAILABLE);
    }

    @IntroducedBy(GETTER)
    default int getMemberCount() {
        return getTraitValue(Trait.MEMBER_COUNT);
    }

    @IntroducedBy(GETTER)
    default Collection<VoiceState> getCurrentVoiceStates() {
        return getTraitValue(Trait.VOICE_STATES);
    }

    @IntroducedBy(GETTER)
    default Collection<GuildMember> getMembers() {
        return getTraitValue(Trait.MEMBERS);
    }

    @IntroducedBy(GETTER)
    default Collection<GuildChannel> getChannels() {
        return getTraitValue(Trait.CHANNELS);
    }

    @IntroducedBy(GETTER)
    default Collection<Presence> getPresences() {
        return getTraitValue(Trait.PRESENCES);
    }

    @IntroducedBy(GETTER)
    default Optional<Integer> getMaximumPresences() {
        return wrapTraitValue(Trait.MAXIMUM_PRESENCES);
    }

    @IntroducedBy(GETTER)
    default Optional<Integer> getMaximumMembers() {
        return wrapTraitValue(Trait.MAXIMUM_MEMBERS);
    }

    @IntroducedBy(GETTER)
    default Optional<URL> getVanityInviteURL() {
        return wrapTraitValue(Trait.VANITY_INVITE_URL);
    }

    @IntroducedBy(GETTER)
    default Optional<String> getDescription() {
        return wrapTraitValue(Trait.DESCRIPTION);
    }

    @IntroducedBy(GETTER)
    default Optional<URL> getBannerURL() {
        return wrapTraitValue(Trait.BANNER_HASH)
                .map(hash -> ImageHelper.GUILD_BANNER.url(FileType.PNG, getID(), hash));
    }

    @IntroducedBy(GETTER)
    default PremiumTier getPremiumTier() {
        return getTraitValue(Trait.PREMIUM_TIER);
    }

    @IntroducedBy(GETTER)
    default int getPremiumSubscriptionCount() {
        return wrapTraitValue(Trait.PREMIUM_SUB_COUNT)
                .orElse(0);
    }

    @IntroducedBy(GETTER)
    default Optional<Locale> getPreferredLocale() {
        return wrapTraitValue(Trait.PREFERRED_LOCALE);
    }

    default Optional<GuildMember> getMember(User user) {
        return user.asGuildMember(this);
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#delete-guild")
    default CompletableFuture<Void> delete() {
        return Adapter.<Void>request(getAPI())
                .endpoint(DiscordEndpoint.GUILD_SPECIFIC)
                .method(RestMethod.DELETE)
                .expectCode(HTTPStatusCodes.NO_CONTENT)
                .executeAs(data -> getAPI().getCacheManager().delete(Guild.class, getID()));
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-channels")
    CompletableFuture<Collection<GuildChannel>> requestGuildChannels();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-member")
    default CompletableFuture<GuildMember> requestGuildMember(User user) {
        return Adapter.<GuildMember>request(getAPI())
                .endpoint(DiscordEndpoint.GUILD_MEMBER, getID(), user.getID())
                .method(RestMethod.GET)
                .executeAs(data -> getAPI().getCacheManager()
                        .updateOrCreateAndGet(GuildMember.class, user.getID(), data));
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#list-guild-members")
    CompletableFuture<Collection<GuildMember>> requestGuildMembers();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#add-guild-member")
    CompletableFuture<GuildMember> addMember(User user);

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-bans")
    CompletableFuture<Collection<Ban>> requestBans();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-ban")
    default CompletableFuture<Optional<Ban>> requestBan(User user) {
        return Adapter.<Ban>request(getAPI())
                .endpoint(DiscordEndpoint.BAN_SPECIFIC, getID(), user.getID())
                .method(RestMethod.GET)
                .executeAs(data -> getAPI().getCacheManager()
                        .updateOrCreateMemberAndGet(Guild.class, Ban.class, getID(), user.getID(), data))
                .thenApply(Optional::ofNullable);
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-prune-count")
    CompletableFuture<Integer> requestPruneCount(int days);

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#begin-guild-prune")
    CompletableFuture<Integer> requestPruneWithCount(int days);

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#begin-guild-prune")
    CompletableFuture<Void> requestPruneWithoutCount(int days);

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-voice-regions")
    CompletableFuture<Collection<VoiceRegion>> requestVoiceRegions();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-invites")
    CompletableFuture<Collection<Invite>> requestInvites();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-integrations")
    CompletableFuture<Collection<Integration>> requestIntegrations();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-embed")
    default CompletableFuture<Guild.Embed> requestGuildEmbed() {
        return Adapter.<Embed>request(getAPI())
                .endpoint(DiscordEndpoint.GUILD_EMBED, getID())
                .method(RestMethod.GET)
                .executeAs(data -> getAPI().getCacheManager()
                        .updateOrCreateSingletonMemberAndGet(Guild.class, Embed.class, getID(), data));
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-vanity-url")
    CompletableFuture<URL> requestVanityInviteURL();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#get-guild-widget-image")
    default URL getWidgetImageURL(WidgetImageStyle style) {
        try {
            return new URL(DiscordEndpoint.GUILD_WIDGET.uri(getID()) + "?style=" + style.value);
        } catch (MalformedURLException e) {
            throw new AssertionError("Unexpected MalformedURLException", e);
        }
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/user#leave-guild")
    default CompletableFuture<Void> leave() {
        return Adapter.<Void>request(getAPI())
                .endpoint(DiscordEndpoint.GUILD_SELF, getID())
                .method(RestMethod.DELETE)
                .expectCode(HTTPStatusCodes.NO_CONTENT)
                .executeAs(data -> getAPI().getCacheManager()
                        .delete(Guild.class, getID()));
    }

    static Builder builder(Discord api) {
        return Adapter.create(Builder.class, api);
    }

    interface Trait extends Snowflake.Trait {
        JsonTrait<String, String> NAME = identity(JsonNode::asText, "name");
        JsonTrait<String, String> ICON_HASH = identity(JsonNode::asText, "icon");
        JsonTrait<String, String> SPLASH_HASH = identity(JsonNode::asText, "splash");
        JsonTrait<Long, User> OWNER = cache("owner_id", CacheManager::getUserByID);
        JsonTrait<String, Region> REGION = simple(JsonNode::asText, "region", null/*todo find voice region from str*/);
        JsonTrait<Long, GuildVoiceChannel> AFK_CHANNEL = cache("afk_channel_id", (cache, id) -> cache.getChannelByID(id).flatMap(Channel::asGuildVoiceChannel));
        JsonTrait<Integer, Duration> AFK_TIMEOUT = simple(JsonNode::asInt, "afk_timeout", Duration::ofSeconds);
        JsonTrait<Boolean, Boolean> EMBEDDABLE = identity(JsonNode::asBoolean, "embed_enabled");
        JsonTrait<Long, GuildChannel> EMBED_CHANNEL = cache("embed_channel_id", (cache, id) -> cache.getChannelByID(id).flatMap(Channel::asGuildChannel));
        JsonTrait<Integer, VerificationLevel> VERIFICATION_LEVEL = simple(JsonNode::asInt, "verification_level", VerificationLevel::valueOf);
        JsonTrait<Integer, DefaultMessageNotificationLevel> DEFAULT_MESSAGE_NOTIFICATION_LEVEL = simple(JsonNode::asInt, "default_message_notifications", DefaultMessageNotificationLevel::valueOf);
        JsonTrait<Integer, ExplicitContentFilterLevel> EXPLICIT_CONTENT_FILTER_LEVEL = simple(JsonNode::asInt, "explicit_content_filter", ExplicitContentFilterLevel::valueOf);
        JsonTrait<ArrayNode, Collection<Role>> ROLES = underlyingCollective("roles", Role.class);
        JsonTrait<ArrayNode, Collection<CustomEmoji>> EMOJIS = underlyingCollective("emojis", CustomEmoji.class);
        JsonTrait<ArrayNode, Collection<Feature>> FEATURES = underlyingCollective("features", Feature.class, (api, data) -> Feature.valueOf(data.asText()));
        JsonTrait<Integer, MFALevel> MFA_LEVEL = simple(JsonNode::asInt, "mfa_level", MFALevel::valueOf);
        JsonTrait<Long, Snowflake> OWNER_APPLICATION_ID = api(JsonNode::asLong, "application_id", (api, id) -> Adapter.create(Snowflake.class, api, id));
        JsonTrait<Boolean, Boolean> WIDGETABLE = identity(JsonNode::asBoolean, "widget_enabled");
        JsonTrait<Long, GuildChannel> WIDGET_CHANNEL = cache("widget_channel_id", (cache, id) -> cache.getChannelByID(id).flatMap(Channel::asGuildChannel));
        JsonTrait<Long, GuildTextChannel> SYSTEM_CHANNEL = cache("system_channel_id", (cache, id) -> cache.getChannelByID(id).flatMap(Channel::asGuildTextChannel));
        JsonTrait<String, Instant> JOINED_AT = simple(JsonNode::asText, "joined_at", Instant::parse);
        JsonTrait<Boolean, Boolean> LARGE = identity(JsonNode::asBoolean, "large");
        JsonTrait<Boolean, Boolean> UNAVAILABLE = identity(JsonNode::asBoolean, "unavailable");
        JsonTrait<Integer, Integer> MEMBER_COUNT = identity(JsonNode::asInt, "member_count");
        JsonTrait<ArrayNode, Collection<VoiceState>> VOICE_STATES = underlyingCollective("voice_states", VoiceState.class);
        JsonTrait<ArrayNode, Collection<GuildMember>> MEMBERS = underlyingCollective("members", GuildMember.class);
        JsonTrait<ArrayNode, Collection<GuildChannel>> CHANNELS = underlyingCollective("channels", GuildChannel.class);
        JsonTrait<ArrayNode, Collection<Presence>> PRESENCES = underlyingCollective("presences", Presence.class);
        JsonTrait<Integer, Integer> MAXIMUM_PRESENCES = identity(JsonNode::asInt, "max_presences");
        JsonTrait<Integer, Integer> MAXIMUM_MEMBERS = identity(JsonNode::asInt, "max_members");
        JsonTrait<String, URL> VANITY_INVITE_URL = simple(JsonNode::asText, "vanity_url_code", code -> Util.url_rethrow("https://discord.gg/" + code));
        JsonTrait<String, String> DESCRIPTION = identity(JsonNode::asText, "description");
        JsonTrait<String, String> BANNER_HASH = identity(JsonNode::asText, "banner");
        JsonTrait<Integer, PremiumTier> PREMIUM_TIER = simple(JsonNode::asInt, "premium_tier", PremiumTier::valueOf);
        JsonTrait<Integer, Integer> PREMIUM_SUB_COUNT = identity(JsonNode::asInt, "premium_subscription_count");
        JsonTrait<String, Locale> PREFERRED_LOCALE = simple(JsonNode::asText, "preferred_locale", Locale::forLanguageTag);
    }

    @JsonTraits(Embed.Trait.class)
    interface Embed extends Cacheable, JsonDeserializable {
        Guild getGuild();

        default boolean isEnabled() {
            return getTraitValue(Trait.ENABLED);
        }

        default Optional<GuildChannel> getChannel() {
            return wrapTraitValue(Trait.CHANNEL);
        }

        interface Trait {
            JsonTrait<Boolean, Boolean> ENABLED = identity(JsonNode::asBoolean, "enabled");
            JsonTrait<Long, GuildChannel> CHANNEL = cache("channel_id", (cache, id) -> cache.getChannelByID(id).flatMap(Channel::asGuildChannel));
        }

        @Override
        default Optional<Long> getCacheParentID() {
            return Optional.of(getGuild().getID());
        }

        @Override
        default Optional<Class<? extends Cacheable>> getCacheParentType() {
            return Optional.of(Guild.class);
        }

        @Override
        default boolean isSingletonType() {
            return true;
        }

        interface Updater {
            Optional<GuildChannel> getChannel();

            Updater setChannel(GuildChannel channel);

            boolean isEnabled();

            Updater setEnabled(boolean enabled);

            @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#modify-guild-embed")
            CompletableFuture<Embed> update();
        }
    }

    @JsonTraits(Integration.Trait.class)
    interface Integration extends Snowflake, JsonDeserializable, Cacheable {
        Guild getGuild();

        @IntroducedBy(GETTER)
        default String getName() {
            return getTraitValue(Trait.NAME);
        }

        @IntroducedBy(GETTER)
        default String getType() {
            return getTraitValue(Trait.TYPE);
        }

        @IntroducedBy(GETTER)
        default boolean isEnabled() {
            return getTraitValue(Trait.ENABLED);
        }

        @IntroducedBy(GETTER)
        default boolean isSyncing() {
            return getTraitValue(Trait.IS_SYNCING);
        }

        @IntroducedBy(GETTER)
        default Role getSubscriberRole() {
            return getTraitValue(Trait.PREMIUM_ROLE);
        }

        @IntroducedBy(GETTER)
        default int getExpireBehavior() {
            return getTraitValue(Trait.EXPIRE_BEHAVIOR);
        }

        @IntroducedBy(GETTER)
        default int getExpireGracePeriod() {
            return getTraitValue(Trait.EXPIRE_GRACE_PERIOD);
        }

        @IntroducedBy(GETTER)
        default User getUser() {
            return getTraitValue(Trait.USER);
        }

        @IntroducedBy(GETTER)
        default Account getAccount() {
            return getTraitValue(Trait.ACCOUNT);
        }

        @IntroducedBy(GETTER)
        default Instant getSyncedAtTimestamp() {
            return getTraitValue(Trait.SYNCED_AT);
        }

        interface Trait extends Snowflake.Trait {
            JsonTrait<String, String> NAME = identity(JsonNode::asText, "name");
            JsonTrait<String, String> TYPE = identity(JsonNode::asText, "type");
            JsonTrait<Boolean, Boolean> ENABLED = identity(JsonNode::asBoolean, "enabled");
            JsonTrait<Boolean, Boolean> IS_SYNCING = identity(JsonNode::asBoolean, "syncing");
            JsonTrait<Long, Role> PREMIUM_ROLE = cache("role_id", CacheManager::getRoleByID);
            JsonTrait<Integer, Integer> EXPIRE_BEHAVIOR = identity(JsonNode::asInt, "expire_behavior");
            JsonTrait<Integer, Integer> EXPIRE_GRACE_PERIOD = identity(JsonNode::asInt, "expire_grace_period");
            JsonTrait<JsonNode, User> USER = underlying("user", User.class);
            JsonTrait<JsonNode, Account> ACCOUNT = underlying("account", Account.class);
            JsonTrait<String, Instant> SYNCED_AT = simple(JsonNode::asText, "synced_at", Instant::parse);
        }

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#delete-guild-integration")
        default CompletableFuture<Void> delete() {
            return Adapter.<Void>request(getAPI())
                    .endpoint(DiscordEndpoint.INTEGRATION_SPECIFIC, getGuild().getID(), getID())
                    .method(RestMethod.DELETE)
                    .expectCode(HTTPStatusCodes.NO_CONTENT)
                    .executeAs(data -> getAPI().getCacheManager()
                            .deleteMember(Guild.class, Integration.class, getGuild().getID(), getID()));
            // todo add thenCompose waiting for deletion listener?
        }

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#sync-guild-integration")
        default CompletableFuture<Void> sync() {
            return Adapter.<Void>request(getAPI())
                    .endpoint(DiscordEndpoint.INTEGRATION_SPECIFIC_SYNC, getGuild().getID(), getID())
                    .method(RestMethod.POST)
                    .expectCode(HTTPStatusCodes.NO_CONTENT)
                    .executeAs(data -> null);
        }

        @Override
        default Optional<Long> getCacheParentID() {
            return Optional.of(getGuild().getID());
        }

        @Override
        default Optional<Class<? extends Cacheable>> getCacheParentType() {
            return Optional.of(Guild.class);
        }

        @Override
        default boolean isSingletonType() {
            return true;
        }

        @JsonTraits(Account.Trait.class)
        interface Account extends JsonDeserializable {
            default String getID() {
                return getTraitValue(Trait.ID);
            }

            default String getName() {
                return getTraitValue(Trait.NAME);
            }

            interface Trait {
                JsonTrait<String, String> ID = identity(JsonNode::asText, "id");
                JsonTrait<String, String> NAME = identity(JsonNode::asText, "name");
            }
        }

        interface Builder { // todo
            @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#create-guild-integration")
            CompletableFuture<Integration> build();
        }

        interface Updater { // todo
            @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#modify-guild-integration")
            CompletableFuture<Integration> update();
        }
    }

    @IntroducedBy(PRODUCTION)
    interface Builder {
        Optional<String> getName();

        Builder setName(String name);

        Optional<VoiceRegion> getVoiceRegion();

        Builder setVoiceRegion(VoiceRegion region);

        Optional<InputStream> getIconInputStream();

        Builder setIcon(InputStream inputStream);

        Builder setIcon(URL url);

        VerificationLevel getVerificationLevel();

        Builder setVerificationLevel(VerificationLevel verificationLevel);

        DefaultMessageNotificationLevel getDefaultMessageNotificationLevel();

        Builder setDefaultMessageNotificationLevel(DefaultMessageNotificationLevel defaultMessageNotificationLevel);

        ExplicitContentFilterLevel getExplicitContentFilterLevel();

        Builder setExplicitContentFilterLevel(ExplicitContentFilterLevel explicitContentFilterLevel);

        Builder modifyEveryoneRole(Consumer<Role.Builder> everyoneRoleModifier);

        Collection<Role.Builder> getAdditionalRoles();

        Builder addAdditionalRole(Role.Builder role);

        Builder removeAdditionalRoleIf(Predicate<Role.Builder> tester);

        Collection<GuildChannel.Builder<? extends GuildChannel, ? extends GuildChannel.Builder>> getChannels();

        Builder addChannel(GuildChannel.Builder<? extends GuildChannel, ? extends GuildChannel.Builder> channel);

        Builder removeChannelIf(Predicate<GuildChannel.Builder<? extends GuildChannel, ? extends GuildChannel.Builder>> tester);

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#create-guild")
        CompletableFuture<Guild> build();
    }

    @IntroducedBy(PRODUCTION)
    interface Updater {
        String getName();

        Updater setName(String name);

        VoiceRegion getVoiceRegion();

        Updater setVoiceRegion(VoiceRegion voiceRegion);

        VerificationLevel getVerificationLevel();

        Updater setVerificationLevel(VerificationLevel verificationLevel);

        DefaultMessageNotificationLevel getDefaultMessageNotificationLevel();

        Updater setDefaultMessageNotificationLevel(DefaultMessageNotificationLevel defaultMessageNotificationLevel);

        ExplicitContentFilterLevel getExplicitContentFilterLevel();

        Updater setExplicitContentFilterLevel(ExplicitContentFilterLevel explicitContentFilterLevel);

        Optional<GuildVoiceChannel> getAFKChannel();

        Updater setAFKChannel(GuildVoiceChannel afkChannel);

        Optional<Duration> getAFKTimeout();

        Updater setAFKTimeout(long time, TimeUnit unit);

        InputStream getIconInputStream();

        Updater setIcon(InputStream inputStream);

        Updater setIcon(URL url);

        GuildMember getOwner();

        Updater setOwner(GuildMember owner);

        InputStream getSplashImageInputStream();

        Updater setSplashImage(InputStream inputStream);

        Updater setSplashImage(URL url);

        Optional<GuildTextChannel> getSystemChannel();

        Updater setSystemChannel(GuildTextChannel systemChannel);

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#modify-guild")
        CompletableFuture<Guild> update();
    }

    enum WidgetImageStyle {
        /**
         * shield style widget with Discord icon and guild members online count
         * <p>
         * Example: https://discordapp.com/api/guilds/81384788765712384/widget.png?style=shield
         */
        SHIELD("shield"),

        /**
         * large image with guild icon, name and online count.
         * "POWERED BY DISCORD" as the footer of the widget
         * <p>
         * Example: https://discordapp.com/api/guilds/81384788765712384/widget.png?style=banner1
         */
        POWERED_BY_DISCORD("banner1"),

        /**
         * smaller widget style with guild icon, name and online count.
         * Split on the right with Discord logo
         * <p>
         * Example: https://discordapp.com/api/guilds/81384788765712384/widget.png?style=banner2
         */
        SMALL("banner2"),

        /**
         * large image with guild icon, name and online count. In the footer,
         * Discord logo on the left and "Chat Now" on the right
         * <p>
         * Example: https://discordapp.com/api/guilds/81384788765712384/widget.png?style=banner3
         */
        CHAT_NOW("banner3"),

        /**
         * large Discord logo at the top of the widget. Guild icon, name and online count in the middle
         * portion of the widget and a "JOIN MY SERVER" button at the bottom
         * <p>
         * Example: https://discordapp.com/api/guilds/81384788765712384/widget.png?style=banner4
         */
        JOIN_MY_SERVER("banner4");

        public final String value;

        WidgetImageStyle(String value) {
            this.value = value;
        }
    }

    enum DefaultMessageNotificationLevel {
        ALL_MESSAGES(0),

        ONLY_MENTIONS(1);

        public final int value;

        DefaultMessageNotificationLevel(int value) {
            this.value = value;
        }

        public static @Nullable DefaultMessageNotificationLevel valueOf(int value) {
            for (DefaultMessageNotificationLevel level : values())
                if (level.value == value)
                    return level;

            return null;
        }
    }

    enum ExplicitContentFilterLevel {
        DISABLED(0),

        MEMBERS_WITHOUT_ROLES(1),

        ALL_MEMBERS(2);

        public final int value;

        ExplicitContentFilterLevel(int value) {
            this.value = value;
        }

        public static @Nullable ExplicitContentFilterLevel valueOf(int value) {
            for (ExplicitContentFilterLevel level : values())
                if (level.value == value)
                    return level;

            return null;
        }
    }

    enum MFALevel {
        NONE(0),

        ELEVATED(1);

        public final int value;

        MFALevel(int value) {
            this.value = value;
        }

        public static @Nullable MFALevel valueOf(int value) {
            for (MFALevel level : values())
                if (level.value == value)
                    return level;

            return null;
        }
    }

    enum VerificationLevel {
        /**
         * unrestricted
         */
        NONE(0),

        /**
         * must have verified email on account
         */
        LOW(1),

        /**
         * must be registered on Discord for longer than 5 minutes
         */
        MEDIUM(2),

        /**
         * (╯°□°）╯︵ ┻━┻ - must be a member of the server for longer than 10 minutes
         */
        HIGH(3),

        /**
         * ┻━┻ ミヽ(ಠ 益 ಠ)ﾉ彡 ┻━┻ - must have a verified phone number
         */
        VERY_HIGH(4);

        public final int value;

        VerificationLevel(int value) {
            this.value = value;
        }

        public static @Nullable VerificationLevel valueOf(int value) {
            for (VerificationLevel level : values())
                if (level.value == value)
                    return level;

            return null;
        }
    }

    enum PremiumTier {
        NONE(0),
        TIER_1(1),
        TIER_2(2),
        TIER_3(3);

        public final int value;

        PremiumTier(int value) {
            this.value = value;
        }

        public static @Nullable PremiumTier valueOf(int value) {
            for (PremiumTier tier : values())
                if (tier.value == value)
                    return tier;

            return null;
        }
    }

    enum Feature {
        /**
         * guild has access to set an invite splash background
         */
        INVITE_SPLASH,

        /**
         * guild has access to set 320kbps bitrate in voice (previously VIP voice servers)
         */
        VIP_REGIONS,

        /**
         * guild has access to set a vanity URL
         */
        VANITY_URL,

        /**
         * guild is verified
         */
        VERIFIED,

        /**
         * guild is partnered
         */
        PARTNERED,

        /**
         * guild is lurkable
         */
        LURKABLE,

        /**
         * guild has access to use commerce features (i.e. create store channels)
         */
        COMMERCE,

        /**
         * guild has access to create news channels
         */
        NEWS,

        /**
         * guild is able to be discovered in the directory
         */
        DISCOVERABLE,

        /**
         * guild is able to be featured in the directory
         */
        FEATURABLE,

        /**
         * guild has access to set an animated guild icon
         */
        ANIMATED_ICON,

        /**
         * guild has access to set a guild banner image
         */
        BANNER
    }
}
