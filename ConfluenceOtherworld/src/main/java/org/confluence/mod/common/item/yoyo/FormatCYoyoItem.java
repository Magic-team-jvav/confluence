package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import org.confluence.lib.common.component.ModRarity;

public final class FormatCYoyoItem extends YoyoItem {
    private final float bonusCriticalChance;
    private final float criticalDamageMultiplier;

    public FormatCYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, float bonusCriticalChance, float criticalDamageMultiplier) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.bonusCriticalChance = bonusCriticalChance;
        this.criticalDamageMultiplier = criticalDamageMultiplier;
    }

    @Override
    public float bonusCriticalChance() {return bonusCriticalChance;}

    @Override
    public float criticalDamageMultiplier() {return criticalDamageMultiplier;}

    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.critical");}
}
