package org.confluence.mod.mixin.integration.terraentity;

import net.minecraft.network.chat.Component;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.terraentity.entity.npc.TravelingMerchantNPC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TravelingMerchantNPC.class, remap = false)
public abstract class TravelingMerchantNPCMixin implements SelfGetter<TravelingMerchantNPC> {
    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lorg/confluence/terraentity/entity/npc/TravelingMerchantNPC;discard()V"))
    private void broadcast(CallbackInfo ci) {
        TravelingMerchantNPC npc = confluence$self();
        NPCSpawner.INSTANCE.setNPCAlive(IAbstractTerraNPC.of(npc).confluence$getRegion(), npc.getType(), false);
        NPCSpawner.broadcastMessageToRegion(npc.level(), npc, Component.translatable("event.confluence.traveling_merchant.departed", npc.getName()).withColor(GlobalColors.NPC_ARRIVED.get()));
    }
}
