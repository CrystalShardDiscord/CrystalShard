package de.kaleidox.crystalshard.api.entity.guild;

import java.awt.Color;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;

import de.kaleidox.crystalshard.adapter.Adapter;
import de.kaleidox.crystalshard.api.entity.Snowflake;
import de.kaleidox.crystalshard.api.model.Mentionable;
import de.kaleidox.crystalshard.api.model.permission.PermissionOverridable;
import de.kaleidox.crystalshard.api.model.permission.PermissionOverride;
import de.kaleidox.crystalshard.core.api.cache.CacheManager;
import de.kaleidox.crystalshard.core.api.rest.DiscordEndpoint;
import de.kaleidox.crystalshard.core.api.rest.HTTPCodes;
import de.kaleidox.crystalshard.core.api.rest.RestMethod;
import de.kaleidox.crystalshard.util.annotation.IntroducedBy;

import static de.kaleidox.crystalshard.util.annotation.IntroducedBy.ImplementationSource.API;
import static de.kaleidox.crystalshard.util.annotation.IntroducedBy.ImplementationSource.GETTER;
import static de.kaleidox.crystalshard.util.annotation.IntroducedBy.ImplementationSource.PRODUCTION;

@IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/topics/permissions#role-object")
public interface Role extends Snowflake, PermissionOverridable, Mentionable {
    @IntroducedBy(PRODUCTION)
    Guild getGuild();

    @IntroducedBy(GETTER)
    String getName();

    @IntroducedBy(GETTER)
    Optional<Color> getColor();

    @IntroducedBy(GETTER)
    boolean isHoisted();

    @IntroducedBy(GETTER)
    int getPosition();

    @IntroducedBy(GETTER)
    PermissionOverride getPermissions();

    @IntroducedBy(GETTER)
    boolean isManaged();

    @IntroducedBy(GETTER)
    boolean isMentionable();

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#delete-guild-role")
    default CompletableFuture<Void> delete() {
        return Adapter.<Void>request(getAPI())
                .endpoint(DiscordEndpoint.ROLE, getGuild().getID(), getID())
                .method(RestMethod.DELETE)
                .expectCode(HTTPCodes.EMPTY_RESPONSE)
                .executeAs(data -> CacheManager.deleteMember(Guild.class, Role.class, getGuild().getID(), getID()));
    }

    interface Builder {
        Guild getGuild();

        Optional<String> getName();

        Builder setName(String name);

        Optional<PermissionOverride> getPermissionOverride();

        Builder setPermissionOverride(PermissionOverride permissionOverride);

        Optional<Color> getColor();

        Builder setColor(Color color);

        boolean isHoisted();

        Builder setHoisted(boolean hoisted);

        boolean isMentionable();

        Builder setMentionable(boolean mentionable);

        OptionalInt getPosition();

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#modify-guild-role-positions")
        Updater setPosition(int position);

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#create-guild-role")
        CompletableFuture<Role> build();
    }

    interface Updater {
        Guild getGuild();

        String getName();

        Updater setName(String name);

        PermissionOverride getPermissionOverride();

        Updater setPermissionOverride(PermissionOverride permissionOverride);

        Optional<Color> getColor();

        Updater setColor(Color color);

        boolean isHoisted();

        Updater setHoisted(boolean hoisted);

        boolean isMentionable();

        Updater setMentionable(boolean mentionable);

        int getPosition();

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#modify-guild-role-positions")
        Updater setPosition(int position);

        @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/guild#modify-guild-role")
        CompletableFuture<Role> update();
    }
}