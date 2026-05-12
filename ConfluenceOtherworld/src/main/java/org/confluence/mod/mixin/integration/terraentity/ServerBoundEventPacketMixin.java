package org.confluence.mod.mixin.integration.terraentity;

import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.network.c2s.EventPacketC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EventPacketC2S.class, remap = false)
public abstract class ServerBoundEventPacketMixin {
    @Inject(method = "confluenceHook", at = @At(value = "INVOKE", target = "Lorg/confluence/terraentity/entity/npc/AbstractTerraNPC;discard()V"))
    private static void removeRegion(AbstractTerraNPC npc, CallbackInfo ci) {
        NPCSpawner.INSTANCE.onNPCRemoved(npc);
    }
}
