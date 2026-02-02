package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.StepStoolHandler;
import top.theillusivec4.curios.api.SlotContext;

public record StepStoolSteppingPacketS2C(int slot, int maxStep) implements IPacketS2C {
    public static final int NO_CURIO = -1;
    public static final int RESET_STEP = -2;
    public static final Type<StepStoolSteppingPacketS2C> TYPE = new Type<>(TerraCurio.asResource("step_stool_stepping_s2c"));
    public static final StreamCodec<ByteBuf, StepStoolSteppingPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StepStoolSteppingPacketS2C::slot,
            ByteBufCodecs.VAR_INT, StepStoolSteppingPacketS2C::maxStep,
            StepStoolSteppingPacketS2C::new
    );

    @Override
    public Type<StepStoolSteppingPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        StepStoolHandler.handlePacket(slot, maxStep);
    }

    public static void sendToClient(SlotContext slotContext, int maxStep) {
        if (slotContext.entity() instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new StepStoolSteppingPacketS2C(slotContext.index(), maxStep));
        }
    }

    public static void resetStep(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new StepStoolSteppingPacketS2C(RESET_STEP, 0));
        }
    }
}
