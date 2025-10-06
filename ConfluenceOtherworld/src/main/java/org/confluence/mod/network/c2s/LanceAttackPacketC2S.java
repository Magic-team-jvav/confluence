package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;

public record LanceAttackPacketC2S() implements CustomPacketPayload {
    public static final Type<LanceAttackPacketC2S> TYPE = new Type<>(Confluence.asResource("lance_attack"));
    public static final LanceAttackPacketC2S INSTANCE = new LanceAttackPacketC2S();
    public static final StreamCodec<ByteBuf, LanceAttackPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<LanceAttackPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = serverPlayer.getMainHandItem();
                if (itemStack.is(ModTags.Items.LANCES)) {
                    itemStack.onEntitySwing(serverPlayer, InteractionHand.MAIN_HAND);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
