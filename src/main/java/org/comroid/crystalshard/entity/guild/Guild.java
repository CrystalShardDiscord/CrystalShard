package org.comroid.crystalshard.entity.guild;

import org.comroid.api.Polyfill;
import org.comroid.common.info.Described;
import org.comroid.api.Named;
import org.comroid.crystalshard.DiscordBot;
import org.comroid.crystalshard.entity.DiscordEntity;
import org.comroid.crystalshard.entity.Snowflake;
import org.comroid.crystalshard.entity.channel.GuildChannel;
import org.comroid.crystalshard.entity.channel.GuildTextChannel;
import org.comroid.crystalshard.entity.channel.GuildVoiceChannel;
import org.comroid.crystalshard.entity.user.User;
import org.comroid.crystalshard.model.emoji.Emoji;
import org.comroid.crystalshard.model.guild.*;
import org.comroid.crystalshard.model.permission.PermissionSet;
import org.comroid.crystalshard.model.user.UserPresence;
import org.comroid.crystalshard.util.DiscordImage;
import org.comroid.crystalshard.util.ImageType;
import org.comroid.crystalshard.voice.VoiceRegion;
import org.comroid.crystalshard.voice.VoiceState;
import org.comroid.mutatio.ref.ReferenceIndex;
import org.comroid.uniform.ValueType;
import org.comroid.uniform.node.UniObjectNode;
import org.comroid.varbind.annotation.Location;
import org.comroid.varbind.annotation.RootBind;
import org.comroid.varbind.bind.GroupBind;
import org.comroid.varbind.bind.VarBind;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.time.Instant;
import java.util.*;

@Location(Guild.Bind.class)
public interface Guild extends DiscordEntity, Named, Described {
    @Override
    default Type<? extends Snowflake> getType() {
        return Type.GUILD;
    }

    @Override
    default String getName() {
        return requireNonNull(Bind.Name);
    }

    @Override
    default String getDescription() {
        return wrap(Bind.Description).orElse("No Description");
    }

    default Optional<User> getOwner() {
        return wrap(Bind.Owner);
    }

    default VoiceRegion getVoiceRegion() {
        return requireNonNull(Bind.Region);
    }

    default Optional<GuildVoiceChannel> getAfkChannel() {
        return wrap(Bind.AfkChannel);
    }

    default int getAfkTimeout() {
        return requireNonNull(Bind.AfkTimeout);
    }

    default boolean isEmbedEnabled() {
        return wrap(Bind.GuildEmbedEnabled).orElse(false);
    }

    default Optional<GuildChannel> getEmbedChannel() {
        return wrap(Bind.GuildEmbedChannel);
    }

    default GuildVerificationLevel getVerificationLevel() {
        return requireNonNull(Bind.VerificationLevel);
    }

    default DefaultMessageNotificationLevel getDefaultMessageNotificationLevel() {
        return requireNonNull(Bind.DefaultMessageNotifications);
    }

    default ExplicitContentFilter getExplicitContentFilter() {
        return requireNonNull(Bind.ExplicitContentFilterLevel);
    }

    default Collection<Role> getRoles() {
        return requireNonNull(Bind.Roles);
    }

    default Collection<CustomEmoji> getCustomEmojis() {
        return requireNonNull(Bind.Emojis);
    }

    default Set<GuildFeature> getGuildFeatures() {
        return requireNonNull(Bind.Features);
    }

    default MFALevel getMFALevel() {
        return requireNonNull(Bind.MfaLevel);
    }

    default Optional<User> getOwnerApplication() {
        return wrap(Bind.OwnerApplication);
    }

    default boolean isWidgetEnabled() {
        return wrap(Bind.WidgetEnabled).orElse(false);
    }

    default Optional<GuildChannel> getWidgetChannel() {
        return wrap(Bind.WidgetChannel);
    }

    default Optional<GuildChannel> getSystemChannel() {
        return wrap(Bind.SystemChannel);
    }

    default Set<GuildSystemChannelFlag> getSystemChannelFlags() {
        return requireNonNull(Bind.SystemChannelFlags);
    }

    default Optional<GuildTextChannel> getRulesChannel() {
        return wrap(Bind.RulesChannel);
    }

    default boolean isLarge() {
        return wrap(Bind.Large).orElse(false);
    }

    default boolean isUnavailable() {
        return wrap(Bind.Unavailable).orElse(false);
    }

    default Set<VoiceState> getVoiceStates() {
        return requireNonNull(Bind.VoiceStates);
    }

    default Collection<GuildMember> getGuildMembers() {
        return requireNonNull(Bind.Members);
    }

    default Collection<GuildChannel> getChannels() {
        return requireNonNull(Bind.Channels);
    }

    default Optional<URL> getVanityInvite() {
        return wrap(Bind.VanityUrl);
    }

    default PremiumTierLevel getBoostLevel() {
        return requireNonNull(Bind.BoostLevel);
    }

    default int getBoostCount() {
        return wrap(Bind.BoostCount).orElse(0);
    }

    default Locale getPreferredLanguage() {
        return get(Bind.Locale);
    }

    default Optional<GuildChannel> getPublicUpdatesChannel() {
        return wrap(Bind.PublicUpdatesChannel);
    }

    default Optional<Integer> getMaximumVideoChannelUserCount() {
        return wrap(Bind.MaxVideoChannelUsers);
    }

    default Optional<Integer> getApproximateMemberCount() {
        return wrap(Bind.ApproximateMemberCount);
    }

    default Optional<Integer> getApproximatePresenceCount() {
        return wrap(Bind.ApproximatePresenceCount);
    }

    default Optional<URL> getIconURL(ImageType type) {
        return wrap(Bind.IconHash).map(hash -> DiscordImage.GUILD_ICON.url(getID(), hash, type));
    }

    default Optional<URL> getSplashURL(ImageType type) {
        return wrap(Bind.Splash).map(hash -> DiscordImage.GUILD_SPLASH.url(getID(), hash, type));
    }

    default Optional<URL> getDiscoverySplashURL(ImageType type) {
        return wrap(Bind.DiscoverySplash).map(hash -> DiscordImage.GUILD_DISCOVERY_SPLASH.url(getID(), hash, type));
    }

    default Optional<URL> getBannerURL(ImageType type) {
        return wrap(Bind.BannerHash).map(hash -> DiscordImage.GUILD_BANNER.url(getID(), hash, type));
    }

    @Internal
    boolean addUser(GuildMember guildMember);

    @Internal
    boolean removeUser(User user);

    @Internal
    boolean banUser(User user);

    @Internal
    boolean unbanUser(User user);

    @Internal
    int updateEmojis(ReferenceIndex<Emoji> otherReferences);

    interface Bind extends DiscordEntity.Bind {
        @RootBind
        GroupBind<Guild, DiscordBot> Root = DiscordEntity.Bind.Root.subGroup("guild", Guild.Base.class);
        VarBind<Object, String, String, String> Name
                = Root.createBind("name")
                .extractAs(ValueType.STRING)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, String, String, String> IconHash
                = Root.createBind("icon")
                .extractAs(ValueType.STRING)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, String, String, String> Splash
                = Root.createBind("splash")
                .extractAs(ValueType.STRING)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, String, String, String> DiscoverySplash
                = Root.createBind("discovery_splash")
                .extractAs(ValueType.STRING)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Boolean, Boolean, Boolean> AreYouOwner
                = Root.createBind("owner")
                .extractAs(ValueType.BOOLEAN)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Long, User, User> Owner
                = Root.createBind("owner_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.USER, id).get())
                .onceEach()
                .build();
        VarBind<Object, Integer, PermissionSet, PermissionSet> YourPermissions
                = Root.createBind("permissions")
                .extractAs(ValueType.INTEGER)
                .andRemap(PermissionSet::ofMask)
                .onceEach()
                .build();
        VarBind<Object, String, VoiceRegion, VoiceRegion> Region
                = Root.createBind("region")
                .extractAs(ValueType.STRING)
                .andRemap(VoiceRegion::valueOf)
                .onceEach()
                .build();
        VarBind<Object, Long, GuildVoiceChannel, GuildVoiceChannel> AfkChannel
                = Root.createBind("afk_channel_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.GUILD_VOICE_CHANNEL, id).get())
                .onceEach()
                .build();
        VarBind<Object, Integer, Integer, Integer> AfkTimeout
                = Root.createBind("afk_timeout")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Boolean, Boolean, Boolean> GuildEmbedEnabled
                = Root.createBind("embed_enabled")
                .extractAs(ValueType.BOOLEAN)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Long, GuildChannel, GuildChannel> GuildEmbedChannel
                = Root.createBind("embed_channel_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.GUILD_CHANNEL, id).get())
                .onceEach()
                .build();
        VarBind<Object, Integer, GuildVerificationLevel, GuildVerificationLevel> VerificationLevel
                = Root.createBind("verification_level")
                .extractAs(ValueType.INTEGER)
                .andRemap(GuildVerificationLevel::valueOf)
                .onceEach()
                .build();
        VarBind<Object, Integer, DefaultMessageNotificationLevel, DefaultMessageNotificationLevel> DefaultMessageNotifications
                = Root.createBind("default_message_notifications")
                .extractAs(ValueType.INTEGER)
                .andRemap(DefaultMessageNotificationLevel::valueOf)
                .onceEach()
                .build();
        VarBind<Object, Integer, ExplicitContentFilter, ExplicitContentFilter> ExplicitContentFilterLevel
                = Root.createBind("explicit_content_filter")
                .extractAs(ValueType.INTEGER)
                .andRemap(ExplicitContentFilter::valueOf)
                .onceEach()
                .build();
        VarBind<Object, UniObjectNode, Role, ArrayList<Role>> Roles
                = Root.createBind("roles")
                .extractAsObject()
                .andResolve((data, bot) -> bot.getSnowflake(Type.ROLE, DiscordEntity.Bind.ID.getFrom(data))
                        .wrap()
                        .orElseGet(() -> Type.ROLE.create(bot, data)))
                .intoCollection(ArrayList::new)
                .build();
        VarBind<Object, UniObjectNode, CustomEmoji, ArrayList<CustomEmoji>> Emojis
                = Root.createBind("emojis")
                .extractAsObject()
                .andResolve((data, bot) -> bot.getSnowflake(Type.CUSTOM_EMOJI, DiscordEntity.Bind.ID.getFrom(data))
                        .wrap()
                        .orElseGet(() -> Type.CUSTOM_EMOJI.create(bot, data)))
                .intoCollection(ArrayList::new)
                .build();
        VarBind<Object, String, GuildFeature, HashSet<GuildFeature>> Features
                = Root.createBind("features")
                .extractAs(ValueType.STRING)
                .andRemap(GuildFeature::valueOf)
                .intoCollection(HashSet::new)
                .build();
        VarBind<Object, Integer, MFALevel, MFALevel> MfaLevel
                = Root.createBind("mfa_level")
                .extractAs(ValueType.INTEGER)
                .andRemap(MFALevel::valueOf)
                .onceEach()
                .build();
        VarBind<Object, Long, User, User> OwnerApplication
                = Root.createBind("application_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.USER, id).get())
                .onceEach()
                .build();
        VarBind<Object, Boolean, Boolean, Boolean> WidgetEnabled
                = Root.createBind("widget_enabled")
                .extractAs(ValueType.BOOLEAN)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Long, GuildChannel, GuildChannel> WidgetChannel
                = Root.createBind("widget_channel_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.GUILD_CHANNEL, id).get())
                .onceEach()
                .build();
        VarBind<Object, Long, GuildChannel, GuildChannel> SystemChannel
                = Root.createBind("system_channel_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.GUILD_CHANNEL, id).get())
                .onceEach()
                .build();
        VarBind<Object, Integer, Set<GuildSystemChannelFlag>, Set<GuildSystemChannelFlag>> SystemChannelFlags
                = Root.createBind("system_channel_flags")
                .extractAs(ValueType.INTEGER)
                .andRemap(GuildSystemChannelFlag::valueOf)
                .onceEach()
                .build();
        VarBind<Object, Long, GuildTextChannel, GuildTextChannel> RulesChannel
                = Root.createBind("rules_channel_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.GUILD_TEXT_CHANNEL, id).get())
                .onceEach()
                .build();
        VarBind<Object, String, Instant, Instant> YouJoinedAt
                = Root.createBind("joined_at")
                .extractAs(ValueType.STRING)
                .andRemap(Instant::parse)
                .onceEach()
                .build();
        VarBind<Object, Boolean, Boolean, Boolean> Large
                = Root.createBind("large")
                .extractAs(ValueType.BOOLEAN)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Boolean, Boolean, Boolean> Unavailable
                = Root.createBind("unavailable")
                .extractAs(ValueType.BOOLEAN)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Integer, Integer, Integer> MemberCount
                = Root.createBind("member_count")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, UniObjectNode, VoiceState, HashSet<VoiceState>> VoiceStates
                = Root.createBind("voice_states")
                .extractAsObject()
                .andResolve((data, bot) -> bot.updateVoiceState(data))
                .intoCollection(HashSet::new)
                .build();
        VarBind<Object, UniObjectNode, GuildMember, HashSet<GuildMember>> Members
                = Root.createBind("members")
                .extractAsObject()
                .andResolve((data, bot) -> bot.updateGuildMember(data))
                .intoCollection(HashSet::new)
                .build();
        VarBind<Object, UniObjectNode, GuildChannel, HashSet<GuildChannel>> Channels
                = Root.createBind("channels")
                .extractAsObject()
                .andResolve((data, bot) -> bot.getSnowflake(Type.GUILD_CHANNEL, DiscordEntity.Bind.ID.getFrom(data))
                        .wrap()
                        .orElseGet(() -> Type.GUILD_CHANNEL.create(bot, data)))
                .intoCollection(HashSet::new)
                .build();
        VarBind<Object, UniObjectNode, UserPresence, HashSet<UserPresence>> Presences
                = Root.createBind("presences")
                .extractAsObject()
                .andResolve((data, bot) -> bot.updatePresence(data))
                .intoCollection(HashSet::new)
                .build();
        VarBind<Object, Integer, Integer, Integer> MaxPresences
                = Root.createBind("max_presences")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Integer, Integer, Integer> MaxMembers
                = Root.createBind("max_members")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, String, URL, URL> VanityUrl
                = Root.createBind("vanity_url")
                .extractAs(ValueType.STRING)
                .andRemap(code -> Polyfill.url("https://discord.gg/" + code))
                .onceEach()
                .build();
        VarBind<Object, String, String, String> Description
                = Root.createBind("description")
                .extractAs(ValueType.STRING)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, String, String, String> BannerHash
                = Root.createBind("banner")
                .extractAs(ValueType.STRING)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Integer, PremiumTierLevel, PremiumTierLevel> BoostLevel
                = Root.createBind("premium_tier")
                .extractAs(ValueType.INTEGER)
                .andRemap(PremiumTierLevel::valueOf)
                .onceEach()
                .build();
        VarBind<Object, Integer, Integer, Integer> BoostCount
                = Root.createBind("premium_subscription_count")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, String, java.util.Locale, java.util.Locale> Locale
                = Root.createBind("preferred_locale")
                .extractAs(ValueType.STRING)
                .andRemap(java.util.Locale::new)
                .onceEach()
                .build();
        VarBind<Object, Long, GuildChannel, GuildChannel> PublicUpdatesChannel
                = Root.createBind("public_updates_channel_id")
                .extractAs(ValueType.LONG)
                .andResolve((id, bot) -> bot.getSnowflake(Type.GUILD_CHANNEL, id).get())
                .onceEach()
                .build();
        VarBind<Object, Integer, Integer, Integer> MaxVideoChannelUsers
                = Root.createBind("max_video_channel_users")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Integer, Integer, Integer> ApproximateMemberCount
                = Root.createBind("approximate_member_count")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
        VarBind<Object, Integer, Integer, Integer> ApproximatePresenceCount
                = Root.createBind("approximate_presence_count")
                .extractAs(ValueType.INTEGER)
                .asIdentities()
                .onceEach()
                .build();
    }

    final class Base extends DiscordEntity.Base implements Guild {
        protected Base(DiscordBot bot, @Nullable UniObjectNode initialData) {
            super(bot, initialData);
        }
    }
}
