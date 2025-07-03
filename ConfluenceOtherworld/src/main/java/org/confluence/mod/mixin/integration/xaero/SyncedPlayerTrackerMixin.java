package org.confluence.mod.mixin.integration.xaero;

import org.confluence.mod.client.handler.CompatibilityHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "xaero.map.server.radar.tracker.SyncedPlayerTracker", remap = false)
public abstract class SyncedPlayerTrackerMixin {
    @ModifyVariable(method = "onTick", at = @At(value = "INVOKE", target = "Lxaero/map/server/radar/tracker/SyncedPlayerTrackerSystemManager;getSystems()Ljava/lang/Iterable;"), ordinal = 1)
    private boolean forceAllow(boolean shouldSyncToPlayer) {
        return CompatibilityHandler.isXaerosMapWormholePotion() || shouldSyncToPlayer;
    }
}
