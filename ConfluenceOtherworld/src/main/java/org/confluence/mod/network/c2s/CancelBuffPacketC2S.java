package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;

/// 泰拉式“右键取消增益”：只能取消非减益效果，服务端再次校验。
public record CancelBuffPacketC2S(ResourceLocation effect) implements IPacketC2S {
    public static final Type<CancelBuffPacketC2S> TYPE = Confluence.createType("cancel_buff");
    public static final StreamCodec<ByteBuf, CancelBuffPacketC2S> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(CancelBuffPacketC2S::new, CancelBuffPacketC2S::effect);

    @Override
    public Type<CancelBuffPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        BuiltInRegistries.MOB_EFFECT.getHolder(effect).ifPresent(holder -> {
            if (holder.value().getCategory() != MobEffectCategory.HARMFUL) {
                player.removeEffect(holder);
            }
        });
    }

    public static void sendToServer(ResourceLocation effect) {
        PacketDistributor.sendToServer(new CancelBuffPacketC2S(effect));
    }
}
