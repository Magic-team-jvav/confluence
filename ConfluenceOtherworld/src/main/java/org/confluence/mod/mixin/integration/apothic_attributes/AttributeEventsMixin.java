package org.confluence.mod.mixin.integration.apothic_attributes;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.confluence.mod.mixed.IDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.apothic_attributes.impl.AttributeEvents", remap = false)
public abstract class AttributeEventsMixin {
    @Inject(method = "apothCriticalStrike", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/PacketDistributor;sendToPlayersTrackingChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;[Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V"))
    private void markCrit(LivingIncomingDamageEvent e, CallbackInfo ci) {
        ((IDamageSource) e.getSource()).confluence$setCritical(true);
    }
}
