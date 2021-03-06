package org.comroid.crystalshard.entity;

import org.comroid.api.ContextualProvider;
import org.comroid.crystalshard.entity.channel.Channel;
import org.comroid.crystalshard.entity.channel.GuildChannelCategory;
import org.comroid.crystalshard.entity.command.Command;
import org.comroid.crystalshard.entity.guild.CustomEmoji;
import org.comroid.crystalshard.entity.guild.Guild;
import org.comroid.crystalshard.entity.guild.Role;
import org.comroid.crystalshard.entity.message.Message;
import org.comroid.crystalshard.entity.message.MessageApplication;
import org.comroid.crystalshard.entity.message.MessageAttachment;
import org.comroid.crystalshard.entity.message.MessageSticker;
import org.comroid.crystalshard.entity.user.User;
import org.comroid.crystalshard.entity.webhook.Webhook;
import org.comroid.crystalshard.model.guild.GuildIntegration;
import org.comroid.mutatio.model.Ref;
import org.comroid.mutatio.ref.KeyedReference;
import org.comroid.mutatio.ref.ReferenceMap;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Objects;
import java.util.stream.Stream;

public final class SnowflakeCache implements ContextualProvider.Underlying {
    private final ReferenceMap<String, Snowflake> cache = new ReferenceMap<>();
    private final ContextualProvider context;

    @Override
    public ContextualProvider getUnderlyingContextualProvider() {
        return context.plus(this);
    }

    @Internal
    public SnowflakeCache(ContextualProvider context) {
        this.context = context;
    }

    public Ref<Guild> getGuild(long id) {
        return getSnowflake(EntityType.GUILD, id);
    }

    public Ref<GuildIntegration> getGuildIntegration(long id) {
        return getSnowflake(EntityType.GUILD_INTEGRATION, id);
    }

    public Ref<Role> getRole(long id) {
        return getSnowflake(EntityType.ROLE, id);
    }

    public Ref<Channel> getChannel(long id) {
        return getSnowflake(EntityType.CHANNEL, id);
    }

    public Ref<GuildChannelCategory> getChannelCategory(long id) {
        return getSnowflake(EntityType.GUILD_CHANNEL_CATEGORY, id);
    }

    public Ref<Message> getMessage(long id) {
        return getSnowflake(EntityType.MESSAGE, id);
    }

    public Ref<MessageApplication> getMessageApplication(long id) {
        return getSnowflake(EntityType.MESSAGE_APPLICATION, id);
    }

    public Ref<MessageAttachment> getMessageAttachment(long id) {
        return getSnowflake(EntityType.MESSAGE_ATTACHMENT, id);
    }

    public Ref<MessageSticker> getMessageSticker(long id) {
        return getSnowflake(EntityType.MESSAGE_STICKER, id);
    }

    public Ref<User> getUser(long id) {
        return getSnowflake(EntityType.USER, id);
    }

    public Ref<Webhook> getWebhook(long id) {
        return getSnowflake(EntityType.WEBHOOK, id);
    }

    public Ref<CustomEmoji> getCustomEmoji(long id) {
        return getSnowflake(EntityType.CUSTOM_EMOJI, id);
    }

    public Ref<Command> getApplicationCommand(long id) {
        return getSnowflake(EntityType.APPLICATION_COMMAND, id);
    }

    public <T extends Snowflake> Ref<T> getSnowflake(EntityType<T> type, long id) {
        return getReference(type, id).flatMap(type.getRelatedClass());
    }

    public <T extends Snowflake> KeyedReference<String, Snowflake> getReference(EntityType<T> type, long id) {
        return cache.getReference(getKey(type, id), true);
    }

    public <T extends Snowflake> Stream<T> streamSnowflakes(EntityType<T> type) {
        return cache.filterKey(key -> key.startsWith(type.getRelatedCacheName().getName()))
                .flatMap(type.getRelatedClass())
                .streamValues();
    }

    private <T extends Snowflake> String getKey(EntityType<T> type, long id) {
        Objects.requireNonNull(type, "Type is null");

        return String.format("%s#%d", type.getRelatedCacheName(), id);
    }

    public int size() {
        return cache.size();
    }
}
