package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.common.item.GroupItem;
import org.confluence.mod.mixed.IAbstractContainerScreen;
import org.confluence.mod.mixed.ICreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin implements SelfGetter<CreativeModeInventoryScreen> {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Unique
    private CreativeModeTab confluence$lastCreativeModeTab;

    @Shadow
    protected abstract void refreshCurrentTabContents(Collection<ItemStack> items);

    @Unique
    private void confluence$check() {
        if (confluence$lastCreativeModeTab != selectedTab) {
            this.confluence$lastCreativeModeTab = selectedTab;
            IAbstractContainerScreen.of(confluence$self()).confluence$setShouldRenderGroupBackground(
                    StartupConfigs.itemGroups() && !GroupItem.isInvalidCreativeModeTab(selectedTab)
            );
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        confluence$check();
    }

    @Inject(method = "containerTick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        confluence$check();
    }

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;getCarried()Lnet/minecraft/world/item/ItemStack;", ordinal = 7), cancellable = true)
    private void checkIsGroupItem(CallbackInfo ci, @Local(argsOnly = true) Slot slot) {
        if (IAbstractContainerScreen.of(confluence$self()).confluence$shouldRenderGroupBackground()) {
            ItemStack stack = slot.getItem();
            if (stack.is(GroupItem.getInstance())) {
                GroupItem.toggleVisibility(stack);
                ICreativeModeTab.of(selectedTab).confluence$buildGroup();
                refreshCurrentTabContents(selectedTab.getDisplayItems());
                ci.cancel();
            }
        }
    }
}
