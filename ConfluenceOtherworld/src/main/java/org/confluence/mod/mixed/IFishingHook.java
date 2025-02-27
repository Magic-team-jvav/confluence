package org.confluence.mod.mixed;

import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.fishing.IBait;
import org.jetbrains.annotations.Nullable;

public interface IFishingHook {
    void confluence$setIsLavaHook();

    boolean confluence$isLavaHook();

    @Nullable ItemStack confluence$getBait();

    default float confluence$getBonus() {
        ItemStack itemStack = confluence$getBait();
        if (itemStack != null && itemStack.getItem() instanceof IBait iBait) {
            return iBait.getBaitBonus();
        }
        return 0.0F;
    }
}
