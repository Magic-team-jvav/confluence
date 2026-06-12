package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.common.gameevent.GameEvent;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Arrays;
import java.util.List;

public record GameEventSyncPacketS2C(
        List<ResourceKey<? extends GameEvent>> keys,
        boolean start
) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("game_event_sync");
    public static final PortStreamCodec<ByteBuf, GameEventSyncPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            GameEvent.KEY_STREAM_CODEC.apply(ByteBufCodecs.list()), GameEventSyncPacketS2C::keys,
            PortByteBufCodecs.BOOL, GameEventSyncPacketS2C::start,
            GameEventSyncPacketS2C::new
    );

    @Override
    public void work(Player player) {
        ClientGameEventSystem.handlePacket(player, keys, start);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @SafeVarargs
    public static void sendToAll(boolean start, ResourceKey<? extends GameEvent>... keys) {
        sendToAll(start, Arrays.stream(keys).toList());
    }

    public static void sendToAll(boolean start, List<ResourceKey<? extends GameEvent>> keys) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new GameEventSyncPacketS2C(keys, start));
        }
    }

    @SafeVarargs
    public static void sentToClient(ServerPlayer player, boolean start, ResourceKey<? extends GameEvent>... keys) {
        sentToClient(player, start, Arrays.stream(keys).toList());
    }

    public static void sentToClient(ServerPlayer player, boolean start, List<ResourceKey<? extends GameEvent>> keys) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new GameEventSyncPacketS2C(keys, start));
    }

    public static void sentToClient(ServerPlayer player, List<ResourceKey<? extends GameEvent>> started, List<ResourceKey<? extends GameEvent>> ended) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new GameEventSyncPacketS2C(started, true), new GameEventSyncPacketS2C(ended, false));
    }
}
