package org.confluence.mod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.GroupItem;
import org.confluence.mod.mixed.ICreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin implements ICreativeModeTab {
    @Shadow
    public abstract CreativeModeTab.Type getType();

    @Shadow
    private Collection<ItemStack> displayItems;
    @Unique
    private Collection<ItemStack> confluence$displayItems;

    @Override
    public void confluence$buildGroup() {
        if (confluence$displayItems == null) {
            this.confluence$displayItems = new GroupItem.DisplayItems(displayItems);
        }
    }

    @ModifyReturnValue(method = "getDisplayItems", at = @At("RETURN"))
    private Collection<ItemStack> wrap(Collection<ItemStack> original) {
        return confluence$displayItems == null ? original : confluence$displayItems;
    }
}
