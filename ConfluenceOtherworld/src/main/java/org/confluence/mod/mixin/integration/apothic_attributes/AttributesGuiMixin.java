package org.confluence.mod.mixin.integration.apothic_attributes;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.confluence.mod.mixed.IInventoryScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.apothic_attributes.client.AttributesGui", remap = false)
public abstract class AttributesGuiMixin {
    @Shadow
    @Final
    protected InventoryScreen parent;

    @Shadow
    protected boolean open;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(InventoryScreen parent, CallbackInfo ci) {
        ((IInventoryScreen) parent).confluence$setExtraButtonVisibility(!open, parent.getGuiLeft());
    }

    @Inject(method = "toggleVisibility", at = @At("TAIL"))
    private void toggle(CallbackInfo ci) {
        ((IInventoryScreen) parent).confluence$setExtraButtonVisibility(!open, parent.getGuiLeft());
    }
}
