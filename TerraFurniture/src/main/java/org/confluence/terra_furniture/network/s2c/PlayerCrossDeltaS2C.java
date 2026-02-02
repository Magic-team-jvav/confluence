package org.confluence.terra_furniture.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.block.func.be.BaseSwayingBE;
import org.jetbrains.annotations.NotNull;

/**
 * 同步玩家加速度信息，一般应用在摇晃方块中。
 * @apiNote 远程玩家在本地没有加速度！注意应通过{@link net.minecraft.world.entity.player.Player#getPosition(float)}在1f和0f时的向量差值计算。
 */
public record PlayerCrossDeltaS2C(Vec3 delta, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerCrossDeltaS2C> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TerraFurniture.MODID, "player_delta"));
    public static final StreamCodec<ByteBuf, PlayerCrossDeltaS2C> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.fromCodec(Vec3.CODEC),
        PlayerCrossDeltaS2C::delta,
        ByteBufCodecs.fromCodec(BlockPos.CODEC),
        PlayerCrossDeltaS2C::pos,
        PlayerCrossDeltaS2C::new
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(@NotNull PlayerCrossDeltaS2C data, @NotNull IPayloadContext context) {
        context.enqueueWork(() ->{
            if (context.player().level().getBlockEntity(data.pos) instanceof BaseSwayingBE cast) {
                cast.applyMovingAffectedDelta(data.delta());
            }
        }).exceptionally(e -> null);
    }
}
