package org.confluence.mod.mixin.integration.waystones;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.requirement.NoRequirement;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.blay09.mods.waystones.network.message.SelectWaystoneMessage", remap = false)
public abstract class SelectWaystoneMessageMixin {
    @Inject(method = "lambda$handle$1", at = @At("TAIL"))
    private static void removeRequirements(WaystoneSelectionMenu selectionMenu, WaystoneTeleportContext it, CallbackInfo ci) {
        Waystone waystone;
        if (CommonConfigs.WAYSTONES_PYLON_NON_COST.get() && (waystone = selectionMenu.getWaystoneFrom()) != null && Confluence.MODID.equals(waystone.getWaystoneType().getNamespace())) {
            it.setRequirements(NoRequirement.INSTANCE);
        }
    }
}
