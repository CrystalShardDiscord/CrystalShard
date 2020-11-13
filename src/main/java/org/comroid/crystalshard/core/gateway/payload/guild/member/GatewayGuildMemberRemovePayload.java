package org.comroid.crystalshard.core.gateway.payload.guild.member;

import org.comroid.common.info.MessageSupplier;
import org.comroid.crystalshard.DiscordBot;
import org.comroid.crystalshard.core.gateway.event.GatewayPayloadWrapper;
import org.comroid.crystalshard.core.gateway.payload.AbstractGatewayPayload;
import org.comroid.crystalshard.entity.DiscordEntity;
import org.comroid.crystalshard.entity.guild.Guild;
import org.comroid.crystalshard.entity.user.User;
import org.comroid.uniform.ValueType;
import org.comroid.uniform.node.UniObjectNode;
import org.comroid.varbind.annotation.RootBind;
import org.comroid.varbind.bind.GroupBind;
import org.comroid.varbind.bind.VarBind;

public final class GatewayGuildMemberRemovePayload extends AbstractGatewayPayload {
    @RootBind
    public static final GroupBind<AbstractGatewayPayload, DiscordBot> Root
            = BaseGroup.rootGroup("gateway-guild-member-remove");
    public static final VarBind<Object, Long, Guild, Guild> guild
            = Root.createBind("guild_id")
            .extractAs(ValueType.LONG)
            .andResolve((id, bot) -> bot.getSnowflake(DiscordEntity.Type.GUILD, id)
                    .requireNonNull(MessageSupplier.format("Guild with ID %d not found", id)))
            .onceEach()
            .setRequired()
            .build();
    public static final VarBind<Object, UniObjectNode, User, User> user
            = Root.createBind("user")
            .extractAsObject()
            .andProvide(
                    DiscordEntity.Bind.ID,
                    (id, bot) -> bot.getSnowflake(DiscordEntity.Type.USER, id).get(),
                    User.Bind.Root)
            .onceEach()
            .setRequired()
            .build();

    public Guild getGuild() {
        return requireNonNull(guild);
    }

    public User getUser() {
        return requireNonNull(user);
    }

    public GatewayGuildMemberRemovePayload(GatewayPayloadWrapper gpw) {
        super(gpw);

        getGuild().removeUser(getUser());
    }
}
