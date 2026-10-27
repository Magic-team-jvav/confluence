package org.confluence.mod.common.item.bow;

import net.minecraft.world.item.ItemStack;

public class ShortBowItem extends BaseTerraBowItem {
    public static final int MAX_DRAW_DURATION = 8; // 满蓄力时间为8 tick

    public ShortBowItem(float baseDamage, int durability) {
        super(baseDamage, new Properties().durability(durability));
    }

    /// 直接给定物品属性（例如不带耐久 = 不可破坏）的短弓。
    public ShortBowItem(float baseDamage, Properties properties) {
        super(baseDamage, properties);
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
        return 2.3F;
    }

    @Override
    public float getFullDrawDamage() {
        return getBaseDamage();
    }

    @Override
    public double getTooltipAttackSpeed(ItemStack stack, double attributeValue) {
        return 20.0 / MAX_DRAW_DURATION;
    }
}
