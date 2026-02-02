package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public record BroadcastRenderPacketS2C(int playerId, short render) implements IPacketS2C {
    public static final short LUMINANCE_MASK = 0b001111;
    public static final short NEPTUNES_SHELL = 0b010000;
    public static final short MOON_CHARM = 0b100000;
    public static final Type<BroadcastRenderPacketS2C> TYPE = new Type<>(TerraCurio.asResource("broadcast_render"));
    public static final StreamCodec<ByteBuf, BroadcastRenderPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BroadcastRenderPacketS2C::playerId,
            ByteBufCodecs.SHORT, BroadcastRenderPacketS2C::render,
            BroadcastRenderPacketS2C::new
    );

    @Override
    public Type<BroadcastRenderPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        TCClientPacketHandler.handleRender(playerId, render, player);
    }

    public static void sendToPlayersTrackingTarget(ServerPlayer target) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            short luminance = (short) (TCUtils.getValue(target, TCItems.LUMINANCE) & LUMINANCE_MASK);
            short neptunesShell = TCUtils.hasType(target, TCItems.NEPTUNES$SHELL) ? NEPTUNES_SHELL : 0;
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new BroadcastRenderPacketS2C(target.getId(), (short) (luminance | neptunesShell)));
        }
    }
}
