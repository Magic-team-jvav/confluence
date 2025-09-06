package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.mixed.IAbstractArrow;

public class ShortBowItem extends TerraBowItem {
    public static final int MAX_DRAW_DURATION = 8; // 满蓄力时间为8 tick


    public ShortBowItem(float baseDamage, int durability) {
        super(baseDamage, new TerraBowItem.Builder().setDuration(durability));
        this.baseDamage = baseDamage;
    }

    public float getShortPowerForTime(int pCharge) {
        float f = (float) pCharge / MAX_DRAW_DURATION;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    public float getVelocityMultiplier() {
        return 2.0F;
    }

    public static void applyToArrow(ItemStack itemStack, AbstractArrow arrow) {
        if (itemStack.getItem() instanceof ShortBowItem) {
            IAbstractArrow.of(arrow).confluence$setShootFromShortBow(true);
        }
    }
}
