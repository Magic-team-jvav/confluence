package org.confluence.mod.mixin.integration.better_combat;

import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.network.Packets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.confluence.mod.util.PlayerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.bettercombat.network.ServerNetwork", remap = false)
public abstract class ServerNetworkMixin {
    @Inject(method = "lambda$handleAttackRequest$3", at= @At(value = "INVOKE", target = "Lnet/bettercombat/utils/SoundHelper;playSound(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/bettercombat/api/WeaponAttributes$Sound;)V"))
    private static void doSwordProjectile(ServerPlayer player, WeaponAttributes attributes, WeaponAttributes.Attack attack, AttackHand hand, ServerLevel world, Packets.C2S_AttackRequest request, boolean useVanillaPacket, ServerGamePacketListenerImpl handler, CallbackInfo ci) {
        PlayerUtils.swordProjectile(player);
    }
}
