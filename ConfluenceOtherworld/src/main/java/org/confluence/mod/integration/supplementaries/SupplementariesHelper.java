package org.confluence.mod.integration.supplementaries;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.attachment.ExtraInventory;

public class SupplementariesHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("supplementaries");

    public static boolean shouldSkip(ItemStack itemStack, LivingEntity shooter) {
        if (IS_LOADED && shooter instanceof Player player) {
            ExtraInventory data = ExtraInventory.of(player);
            for (int i = 0; i < ExtraInventory.SIZE_AMMO; i++) {
                if (data.getAmmo(i) == itemStack) {
                    return true;
                }
            }
        }
        return false;
    }
}
