package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientGameEventSystem;
import org.confluence.mod.common.gameevent.GameEvent;

public record GameEventSyncPacketS2C(
        ResourceKey<? extends GameEvent> key,
        boolean start
) implements IPacketS2C {
    public static final Type<GameEventSyncPacketS2C> TYPE = Confluence.createType("game_event_sync");
    public static final StreamCodec<ByteBuf, GameEventSyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.map(
                    GameEvent::createKey,
                    ResourceKey::location
            ), GameEventSyncPacketS2C::key,
            ByteBufCodecs.BOOL, GameEventSyncPacketS2C::start,
            GameEventSyncPacketS2C::new
    );

    @Override
    public void work(Player player) {
        ClientGameEventSystem.handle(player, key, start);
    }

    @Override
    public Type<GameEventSyncPacketS2C> type() {
        return TYPE;
    }

    public static void sendToAll(ResourceKey<? extends GameEvent> key, boolean start) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new GameEventSyncPacketS2C(key, start));
        }
    }
}
