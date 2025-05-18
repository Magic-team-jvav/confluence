package org.confluence.mod.mixin.integration.terra_entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.network.c2s.ServerBoundEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerBoundEventPacket.class, remap = false)
public abstract class ServerBoundEventPacketMixin {
    @Inject(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Lorg/confluence/terraentity/entity/npc/AbstractTerraNPC;discard()V"))
    private static void removeRegion(IPayloadContext context, ServerBoundEventPacket packet, CallbackInfo ci, @Local AbstractTerraNPC npc) {
        NPCSpawner.INSTANCE.onNPCRemoved(npc);
    }
}
