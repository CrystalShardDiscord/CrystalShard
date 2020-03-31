package org.comroid.crystalshard.api.entity.emoji;

// https://discordapp.com/developers/docs/resources/emoji#emoji-object-emoji-structure

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.comroid.crystalshard.adapter.Adapter;
import org.comroid.crystalshard.adapter.MainAPI;
import org.comroid.crystalshard.api.entity.Snowflake;
import org.comroid.crystalshard.api.entity.emoji.CustomEmoji.Bind;
import org.comroid.crystalshard.api.entity.guild.Guild;
import org.comroid.crystalshard.api.entity.guild.Role;
import org.comroid.crystalshard.api.entity.user.User;
import org.comroid.crystalshard.api.model.Mentionable;
import org.comroid.crystalshard.core.cache.Cacheable;
import org.comroid.crystalshard.core.rest.DiscordEndpoint;
import org.comroid.crystalshard.core.rest.HTTPStatusCodes;
import org.comroid.crystalshard.util.annotation.IntroducedBy;
import org.comroid.crystalshard.util.model.serialization.JSONBinding;
import org.comroid.crystalshard.util.model.serialization.JSONBindingLocation;

import com.alibaba.fastjson.JSONObject;

import static org.comroid.crystalshard.util.annotation.IntroducedBy.ImplementationSource.API;
import static org.comroid.crystalshard.util.annotation.IntroducedBy.ImplementationSource.PRODUCTION;
import static org.comroid.crystalshard.util.model.serialization.JSONBinding.identity;
import static org.comroid.crystalshard.util.model.serialization.JSONBinding.mappingCollection;
import static org.comroid.crystalshard.util.model.serialization.JSONBinding.require;

@MainAPI
@JSONBindingLocation(Bind.class)
public interface CustomEmoji extends Emoji, Mentionable, Snowflake, Cacheable {
    @IntroducedBy(PRODUCTION)
    Guild getGuild();

    default String getName() {
        return getBindingValue(Bind.NAME);
    }

    default Collection<Role> getRoles() {
        return getBindingValue(Bind.WHITELISTED_ROLES);
    }

    default Optional<User> getCreator() {
        return wrapBindingValue(Bind.CREATOR);
    }

    default Optional<Boolean> requiresColons() {
        return wrapBindingValue(Bind.REQUIRE_COLONS);
    }

    default Optional<Boolean> isManaged() {
        return wrapBindingValue(Bind.MANAGED);
    }

    default Optional<Boolean> isAnimated() {
        return wrapBindingValue(Bind.ANIMATED);
    }

    interface Bind extends Emoji.JSON, Snowflake.Bind {
        JSONBinding.TriStage<Long, Role> WHITELISTED_ROLES = mappingCollection("roles", JSONObject::getLong, (api, id) -> api.getCacheManager()
                .getByID(Role.class, id)
                .orElseThrow());
        JSONBinding.TwoStage<JSONObject, User> CREATOR = require("user", User.class);
        JSONBinding.OneStage<Boolean> REQUIRE_COLONS = identity("require_colons", JSONObject::getBoolean);
        JSONBinding.OneStage<Boolean> MANAGED = identity("managed", JSONObject::getBoolean);
        JSONBinding.OneStage<Boolean> ANIMATED = identity("animated", JSONObject::getBoolean);
    }
    
    CompletableFuture<CustomEmoji> requestMetadata();

    @Override
    default String getDiscordPrintableString() {
        return getMentionTag();
    }

    Updater createUpdater();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/emoji#delete-guild-emoji")
    default CompletableFuture<Void> delete() {
        return Adapter.<Void>request(getAPI())
                .endpoint(DiscordEndpoint.CUSTOM_EMOJI_SPECIFIC, getGuild().getID(), getID())
                .method(RestMethod.DELETE)
                .expectCode(HTTPStatusCodes.NO_CONTENT)
                .executeAsObject(data -> getAPI().getCacheManager()
                        .delete(CustomEmoji.class, getID()));
    }

    static Builder builder(Guild guild) {
        return Adapter.require(Builder.class);
    }

    @IntroducedBy(PRODUCTION)
    interface Builder {
        Guild getGuild();

        Optional<String> getName();

        Builder setName(String name);

        Optional<InputStream> getEmojiInputStream();

        Builder setImage(InputStream inputStream);

        Builder setImage(URL url);

        Collection<Role> getWhitelistedRoles();

        Builder addWhitelistedRole(Role role);

        Builder removeWhitelistedRoleIf(Predicate<Role> tester);

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/emoji#create-guild-emoji")
        CompletableFuture<CustomEmoji> build();
    }

    @IntroducedBy(PRODUCTION)
    interface Updater {
        Guild getGuild();

        String getName();

        Updater setName(String name);

        Collection<Role> getWhitelistedRoles();

        Updater addWhitelistedRole(Role role);

        Updater removeWhitelistedRoleIf(Predicate<Role> tester);

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/emoji#modify-guild-emoji")
        CompletableFuture<CustomEmoji> update();
    }
}
