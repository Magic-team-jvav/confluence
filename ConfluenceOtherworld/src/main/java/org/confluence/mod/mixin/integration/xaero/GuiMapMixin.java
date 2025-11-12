package org.confluence.mod.mixin.integration.xaero;

import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.integration.xaero.IGuiMap;
import org.confluence.mod.integration.xaero.XaeroHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.gui.GuiMap;

@Pseudo
@Mixin(targets = "xaero.map.gui.GuiMap", remap = false)
public abstract class GuiMapMixin implements IGuiMap, SelfGetter<GuiMap> {
    @Shadow
    private HoveredMapElementHolder<?, ?> viewed;

    @Override
    public @Nullable Object confluence$getHovered() {
        return viewed == null ? null : viewed.getElement();
    }

    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lxaero/map/gui/MapMouseButtonPress;pressedAtY:I", ordinal = 0, shift = At.Shift.AFTER))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (viewed == null) return;
        if (CompatibilityHandler.isXaerosMapWormholePotion() && XaeroHelper.teleport(viewed.getElement())) {
            confluence$self().onClose();
        } else if (CompatibilityHandler.isXaerosMapPylonWaypoint() && WaystonesHelper.teleport(viewed.getElement())) {
            confluence$self().onClose();
        }
    }
}
