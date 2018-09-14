package de.kaleidox.crystalshard.internal.items.channel;

import com.fasterxml.jackson.databind.JsonNode;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.main.items.channel.VoiceChannel;

public abstract class VoiceChannelInternal extends ChannelInternal implements VoiceChannel {
    final int bitrate;
    final int limit;

    VoiceChannelInternal(Discord discord, JsonNode data) {
        super(discord, data);
        this.bitrate = data.path("bitrate").asInt(0);
        this.limit = data.path("user_limit").asInt(-1);
    }

    @Override
    public int getBitrate() {
        return bitrate;
    }

    @Override
    public int getUserLimit() {
        return limit;
    }
}