package org.confluence.mod.common.item.flail;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;

/// 命中时按固定概率点燃目标的链锤。
public class IgnitingFlailItem extends BaseFlailItem {
    private static final int FIRE_TICKS = 60;
    private final float igniteChance;

    public IgnitingFlailItem(FlailComponent component, ModRarity rarity, float igniteChance) {
        super(component, rarity);
        if (igniteChance < 0.0F || igniteChance > 1.0F) {
            throw new IllegalArgumentException("Flail ignite chance must be between zero and one");
        }
        this.igniteChance = igniteChance;
    }

    @Override
    public void onFlailHit(Player owner, LivingEntity target, BaseFlailEntity flail) {
        if (target.getRandom().nextFloat() < igniteChance) {
            target.setRemainingFireTicks(FIRE_TICKS);
        }
    }
}
