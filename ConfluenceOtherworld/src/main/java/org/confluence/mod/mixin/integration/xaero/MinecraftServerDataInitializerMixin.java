package org.confluence.mod.mixin.integration.xaero;

import net.minecraft.server.MinecraftServer;
import org.confluence.mod.integration.xaero.ConfluenceSyncedPlayerTrackerSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.server.IMinecraftServer;

@Pseudo
@Mixin(targets = "xaero.map.server.MineraftServerDataInitializer", remap = false)
public abstract class MinecraftServerDataInitializerMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private void registerSyncedPlayerTrackerSystem(MinecraftServer server, CallbackInfo ci) {
        ((IMinecraftServer) server).getXaeroWorldMapServerData().getSyncedPlayerTrackerSystemManager().register("confluence", new ConfluenceSyncedPlayerTrackerSystem());
    }
}
