package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientGameEventSystem;
import org.confluence.mod.common.gameevent.GameEvent;

import java.util.Arrays;
import java.util.List;

public record GameEventSyncPacketS2C(
        List<ResourceKey<? extends GameEvent>> keys,
        boolean start
) implements IPacketS2C {
    public static final Type<GameEventSyncPacketS2C> TYPE = Confluence.createType("game_event_sync");
    public static final StreamCodec<ByteBuf, GameEventSyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            GameEvent.KEY_STREAM_CODEC.apply(ByteBufCodecs.list()), GameEventSyncPacketS2C::keys,
            ByteBufCodecs.BOOL, GameEventSyncPacketS2C::start,
            GameEventSyncPacketS2C::new
    );

    @Override
    public void work(Player player) {
        ClientGameEventSystem.handlePacket(player, keys, start);
    }

    @Override
    public Type<GameEventSyncPacketS2C> type() {
        return TYPE;
    }

    @SafeVarargs
    public static void sendToAll(boolean start, ResourceKey<? extends GameEvent>... keys) {
        sendToAll(start, Arrays.stream(keys).toList());
    }

    public static void sendToAll(boolean start, List<ResourceKey<? extends GameEvent>> keys) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new GameEventSyncPacketS2C(keys, start));
        }
    }

    @SafeVarargs
    public static void sentToClient(ServerPlayer player, boolean start, ResourceKey<? extends GameEvent>... keys) {
        sentToClient(player, start, Arrays.stream(keys).toList());
    }

    public static void sentToClient(ServerPlayer player, boolean start, List<ResourceKey<? extends GameEvent>> keys) {
        PacketDistributor.sendToPlayer(player, new GameEventSyncPacketS2C(keys, start));
    }
}
