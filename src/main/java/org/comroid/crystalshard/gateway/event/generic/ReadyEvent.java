package org.comroid.crystalshard.gateway.event.generic;

import org.comroid.api.ContextualProvider;
import org.comroid.crystalshard.entity.channel.Channel;
import org.comroid.crystalshard.entity.guild.Guild;
import org.comroid.crystalshard.entity.user.SimpleUser;
import org.comroid.crystalshard.entity.user.User;
import org.comroid.crystalshard.gateway.event.GatewayEvent;
import org.comroid.mutatio.ref.Reference;
import org.comroid.uniform.node.UniNode;
import org.comroid.uniform.node.UniObjectNode;
import org.comroid.util.StandardValueType;
import org.comroid.varbind.annotation.RootBind;
import org.comroid.varbind.bind.GroupBind;
import org.comroid.varbind.bind.VarBind;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ReadyEvent extends GatewayEvent {
    @RootBind
    public static final GroupBind<ReadyEvent> TYPE
            = BASETYPE.subGroup("ready", ReadyEvent::new);
    public static final VarBind<ReadyEvent, Integer, Integer, Integer> VERSION
            = TYPE.createBind("v")
            .extractAs(StandardValueType.INTEGER)
            .asIdentities()
            .onceEach()
            .setRequired()
            .build();
    public static final VarBind<ReadyEvent, UniObjectNode, User, User> YOURSELF
            = TYPE.createBind("user")
            .extractAsObject()
            .andResolve(SimpleUser::resolve)
            .onceEach()
            .setRequired()
            .build();
    public static final VarBind<ReadyEvent, UniObjectNode, Channel, ArrayList<Channel>> PRIVATE_CHANNELS
            = TYPE.createBind("private_channels")
            .extractAsArray()
            .andResolve(Channel::resolve)
            .intoCollection((Supplier<ArrayList<Channel>>) ArrayList::new)
            .setRequired()
            .build();
    public static final VarBind<ReadyEvent, UniObjectNode, Guild, ArrayList<Guild>> GUILDS
            = TYPE.createBind("guilds")
            .extractAsArray()
            .andResolve(Guild::resolve)
            .intoCollection((Supplier<ArrayList<Guild>>) ArrayList::new)
            .setRequired()
            .build();
    public static final VarBind<ReadyEvent, String, String, String> SESSION_ID
            = TYPE.createBind("session_id")
            .extractAs(StandardValueType.STRING)
            .asIdentities()
            .onceEach()
            .setRequired()
            .build();
    public static final VarBind<ReadyEvent, Integer, Integer, List<Integer>> SHARD
            = TYPE.createBind("shard")
            .extractAsArray(StandardValueType.INTEGER)
            .asIdentities()
            .intoCollection((Supplier<List<Integer>>) ArrayList::new)
            .setRequired()
            .build();
    public final Reference<Integer> version = getComputedReference(VERSION);
    public final Reference<User> yourself = getComputedReference(YOURSELF);
    public final Reference<ArrayList<Channel>> privateChannels = getComputedReference(PRIVATE_CHANNELS);
    public final Reference<ArrayList<Guild>> guilds = getComputedReference(GUILDS);
    public final Reference<String> sessionID = getComputedReference(SESSION_ID);
    public final Reference<List<Integer>> shard = getComputedReference(SHARD);

    public ReadyEvent(ContextualProvider context, @Nullable UniNode initialData) {
        super(context, initialData);
    }
}
