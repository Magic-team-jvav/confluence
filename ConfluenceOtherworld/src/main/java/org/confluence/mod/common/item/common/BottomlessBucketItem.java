package org.confluence.mod.common.item.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.capability.FluidBottomlessBucketWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BottomlessBucketItem extends BucketItem {
    public BottomlessBucketItem(Supplier<Fluid> flowingFluid, ModRarity rarity) {
        super(flowingFluid, new Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (getClass() == BottomlessBucketItem.class) {
            return new FluidBottomlessBucketWrapper(stack);
        }
        return super.initCapabilities(stack, nbt);
    }
}
