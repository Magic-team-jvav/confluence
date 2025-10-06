package org.confluence.mod.mixin.integration.waystones;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.requirement.NoRequirement;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreenBase", remap = false)
public class WaystoneSelectionScreenBaseMixin {
    @WrapOperation(method = "createWaystoneButton", at = @At(value = "INVOKE", target = "Lnet/blay09/mods/waystones/api/WaystonesAPI;resolveRequirements(Lnet/blay09/mods/waystones/api/WaystoneTeleportContext;)Lnet/blay09/mods/waystones/api/requirement/WarpRequirement;"))
    private WarpRequirement removeRequirements(WaystoneTeleportContext context, Operation<WarpRequirement> original) {
        if (CompatibilityHandler.isWaystonesPylonNonCost() && context.getFromWaystone().map(waystone -> Confluence.MODID.equals(waystone.getWaystoneType().getNamespace())).orElse(false)) {
            return NoRequirement.INSTANCE;
        }
        return original.call(context);
    }
}
