package org.confluence.mod.common.item.flail;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.FlailAttackStrategy;
import org.confluence.mod.common.entity.projectile.Flail.FlowerProjectile;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>花之力连枷物品</h1>
 * 绑定 {@link FlowerProjectile.AttackStrategy}，在挥舞/投掷/收回时持续向附近敌人发射花瓣。
 */
public class FlowerPowerItem extends BaseFlailItem {

    public FlowerPowerItem(@NotNull FlailComponent flailComponent, @NotNull ModRarity rarity) {
        super(flailComponent, rarity);
    }

    @Override
    public FlailAttackStrategy getAttackStrategy() {
        return FlowerProjectile.AttackStrategy.INSTANCE;
    }
}
