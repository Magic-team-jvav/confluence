package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.sword.BaseSwordItem;

public record SwordShootingPacketC2S() implements CustomPacketPayload {
    public static final SwordShootingPacketC2S INSTANCE = new SwordShootingPacketC2S();
    public static final Type<SwordShootingPacketC2S> TYPE = new Type<>(Confluence.asResource("sword_shooting"));
    public static final StreamCodec<ByteBuf, SwordShootingPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<SwordShootingPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack mainHandItem = player.getMainHandItem();
                if (mainHandItem.getItem() instanceof BaseSwordItem sword) {
                    sword.genProjectile(player, mainHandItem);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }
}
