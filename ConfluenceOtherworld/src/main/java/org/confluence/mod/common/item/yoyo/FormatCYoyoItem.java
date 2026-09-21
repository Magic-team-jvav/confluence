package org.confluence.mod.common.item.yoyo;

import org.confluence.lib.common.component.ModRarity;

public final class FormatCYoyoItem extends YoyoItem {
    public FormatCYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.PINK, 19, 20, 0xFFFFFFFF, 16 * 20, 3.25F);
    }

    @Override
    public float bonusCriticalChance() {return 0.2F;}

    @Override
    public float criticalDamageMultiplier() {return 2.5F;}

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.critical";}
}
