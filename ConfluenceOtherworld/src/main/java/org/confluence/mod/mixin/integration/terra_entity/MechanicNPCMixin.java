package org.confluence.mod.mixin.integration.terra_entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.terraentity.entity.npc.MechanicNPC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MechanicNPC.class, remap = false)
public abstract class MechanicNPCMixin implements SelfGetter<MechanicNPC> {
    @ModifyReturnValue(method = "removeWhenFarAway", at = @At("RETURN"))
    private boolean removeIfNotInRegion(boolean original) {
        return !IAbstractTerraNPC.of(confluence$self()).confluence$getRegion().isOnRegion(confluence$self().chunkPosition());
    }

    @Inject(method = "checkDespawn", at = @At("TAIL"))
    private void onRemove(CallbackInfo ci) { // 由于不在区域内的机械师会被自然刷新走，因此不会触发setNPCAlive，需手动触发
        MechanicNPC npc = confluence$self();
        if (npc.isRemoved()) {
            NPCSpawner.INSTANCE.setNPCAlive(IAbstractTerraNPC.of(npc).confluence$getRegion(), npc.getType(), false);
        }
    }
}
