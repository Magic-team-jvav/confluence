package org.confluence.mod.mixin.client.gui;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModTabs;
import org.confluence.mod.common.item.GroupItem;
import org.confluence.mod.mixed.ICreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Shadow
    protected abstract void refreshCurrentTabContents(Collection<ItemStack> items);

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;getCarried()Lnet/minecraft/world/item/ItemStack;", ordinal = 7), cancellable = true)
    private void checkIsGroupItem(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (!GroupItem.enable || selectedTab == ModTabs.DEVELOPER.get()) return;
        ItemStack stack = slot.getItem();
        if (stack.is(GroupItem.getInstance())) {
            GroupItem.toggleVisibility(stack);
            ICreativeModeTab.of(selectedTab).confluence$buildGroup();
            refreshCurrentTabContents(selectedTab.getDisplayItems());
            ci.cancel();
        }
    }
}
