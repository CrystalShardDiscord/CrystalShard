package de.kaleidox.crystalshard.internal.handling.event.message.generic;

import de.kaleidox.crystalshard.api.entity.channel.Channel;
import de.kaleidox.crystalshard.api.entity.channel.ServerChannel;
import de.kaleidox.crystalshard.api.entity.message.Message;
import de.kaleidox.crystalshard.api.entity.message.embed.SentEmbed;
import de.kaleidox.crystalshard.api.entity.server.Server;
import de.kaleidox.crystalshard.api.entity.user.Author;
import de.kaleidox.crystalshard.api.entity.user.AuthorUser;
import de.kaleidox.crystalshard.api.handling.editevent.EditTrait;
import de.kaleidox.crystalshard.api.handling.event.message.generic.MessageEditEvent;
import de.kaleidox.crystalshard.internal.DiscordInternal;
import de.kaleidox.crystalshard.internal.handling.event.EventBase;

import java.util.Optional;
import java.util.Set;

public class MessageEditEventInternal extends EventBase implements MessageEditEvent {
    private final Message message;
    private final String prevContent;
    private final SentEmbed prevEmbed;
    private final Set<EditTrait<Message>> traits;

    public MessageEditEventInternal(DiscordInternal discordInternal, Message message, Set<EditTrait<Message>> traits, String prevContent, SentEmbed prevEmbed) {
        super(discordInternal);
        this.message = message;
        this.traits = traits;
        this.prevContent = prevContent;
        this.prevEmbed = prevEmbed;
    }

    // Override Methods
    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public Channel getChannel() {
        return message.getChannel();
    }

    @Override
    public Author getMessageAuthor() {
        return message.getAuthor();
    }

    @Override
    public Optional<AuthorUser> getMessageAuthorUser() {
        return message.getAuthorAsUser();
    }

    @Override
    public String getMessageContent() {
        return message.getContent();
    }

    @Override
    public Optional<Server> getServer() {
        return message.getChannel()
                .toServerChannel()
                .map(ServerChannel::getServer);
    }

    @Override
    public Optional<String> getPreviousContent() {
        return Optional.ofNullable(prevContent);
    }

    @Override
    public Optional<SentEmbed> getPreviousEmbed() {
        return Optional.ofNullable(prevEmbed);
    }

    @Override
    public Set<EditTrait<Message>> getEditTraits() {
        return traits;
    }
}