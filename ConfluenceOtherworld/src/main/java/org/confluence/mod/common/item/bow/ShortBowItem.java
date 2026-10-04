package org.confluence.mod.common.item.bow;


public class ShortBowItem extends BaseTerraBowItem {
    public static final int MAX_DRAW_DURATION = 8; // 满蓄力时间为8 tick

    public ShortBowItem(float baseDamage, int durability) {
        super(baseDamage, new Properties().durability(durability));
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
}
