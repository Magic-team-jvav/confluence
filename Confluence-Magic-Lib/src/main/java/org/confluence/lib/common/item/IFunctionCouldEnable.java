package org.confluence.lib.common.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.jetbrains.annotations.Nullable;

public interface IFunctionCouldEnable {
    String DISABLE_KEY = "disable";

    default String getDisableKey() {
        return DISABLE_KEY;
    }

    default boolean isEnabled(ItemStack itemStack) {
        return !LibUtils.getItemStackNbtNoCopy(itemStack).getBoolean(getDisableKey());
    }

    default void cycleEnable(ItemStack itemStack) {
        LibUtils.updateItemStackNbt(itemStack, tag -> {
            String key = getDisableKey();
            tag.putBoolean(key, !tag.getBoolean(key));
        });
    }

    default @Nullable TooltipComponent getTooltipComponent(ItemStack itemStack) {
        return null;
    }
}
