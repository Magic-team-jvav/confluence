package org.confluence.mod.mixin.client.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.confluence.mod.client.gui.hud.TerraPickupTextHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 泰拉拾取文字：本地玩家捡起物品实体时记下物品与数量（在主线程、物品被扣减之前）
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerPickupMixin {
    @Inject(method = "handleTakeItemEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V", shift = At.Shift.AFTER))
    private void confluence$terraPickupText(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || packet.getPlayerId() != player.getId()) return;
        Entity entity = minecraft.level.getEntity(packet.getItemId());
        if (entity instanceof ItemEntity itemEntity) {
            TerraPickupTextHud.onPickup(itemEntity.getItem(), packet.getAmount());
        }
    }
}
