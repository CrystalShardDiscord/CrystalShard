package de.comroid.crystalshard.api.entity.channel;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import de.comroid.crystalshard.api.entity.EntityType;
import de.comroid.crystalshard.api.model.channel.ChannelType;
import de.comroid.crystalshard.util.annotation.IntroducedBy;
import de.comroid.crystalshard.util.model.FileType;
import de.comroid.crystalshard.util.model.ImageHelper;
import de.comroid.crystalshard.util.model.serialization.JSONBinding;

import com.alibaba.fastjson.JSONObject;

import static de.comroid.crystalshard.util.annotation.IntroducedBy.ImplementationSource.API;
import static de.comroid.crystalshard.util.annotation.IntroducedBy.ImplementationSource.GETTER;
import static de.comroid.crystalshard.util.model.serialization.JSONBinding.simple;

public interface  GroupTextChannel extends PrivateChannel, TextChannel {
    @Override
    default ChannelType getChannelType() {
        return ChannelType.GROUP_DM;
    }

    @Override
    default EntityType getEntityType() {
        return EntityType.GROUP_TEXT_CHANNEL;
    }
    
    @IntroducedBy(GETTER)
    default Optional<URL> getIconUrl() {
        return wrapBindingValue(JSON.ICON);
    }
    
    interface JSON extends PrivateChannel.Trait, TextChannel.Trait {
        JSONBinding.TwoStage<String, URL> ICON = simple("icon", JSONObject::getString, hash -> ImageHelper.GUILD_ICON.url(FileType.PNG, hash));
    }

    interface Builder extends
            PrivateChannel.Builder<GroupTextChannel, GroupTextChannel.Builder>,
            TextChannel.Builder<GroupTextChannel, GroupTextChannel.Builder> {
        @Override
        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/user#create-group-dm")
        CompletableFuture<GroupTextChannel> build();
    }

    interface Updater extends
            PrivateChannel.Updater<GroupTextChannel, GroupTextChannel.Updater>,
            TextChannel.Updater<GroupTextChannel, GroupTextChannel.Updater> {
    }
}