package org.confluence.terra_curio.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.terra_curio.TerraCurio;

public record PlayerJumpPacketC2S(byte jumpState, float motionY) implements IPacketC2S {
    public static final byte JUMP_BY_SELF = 1;
    public static final byte RESET_FALL_DISTANCE = 2;

    public static final Type<PlayerJumpPacketC2S> TYPE = new Type<>(TerraCurio.asResource("player_jump_c2s"));
    public static final StreamCodec<ByteBuf, PlayerJumpPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, PlayerJumpPacketC2S::jumpState,
            ByteBufCodecs.FLOAT, PlayerJumpPacketC2S::motionY,
            PlayerJumpPacketC2S::new
    );

    @Override
    public Type<PlayerJumpPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        player.hasImpulse = true;
        if ((jumpState & JUMP_BY_SELF) != 0) {
            player.awardStat(Stats.JUMP);
            player.causeFoodExhaustion(player.isSprinting() ? 0.2F : 0.05F);
        }
        if ((jumpState & RESET_FALL_DISTANCE) != 0) {
            player.resetFallDistance();
        }
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, motionY, motion.z);
    }
}
