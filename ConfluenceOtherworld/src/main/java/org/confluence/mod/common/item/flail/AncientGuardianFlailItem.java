package org.confluence.mod.common.item.flail;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.FlailAttackStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>远古守卫链球物品</h1>
 * 连枷 STAY 阶段向周围敌人发射守卫者激光，同时锁定 3 个目标。
 * <p>
 * 复用 {@link GuardianFlailItem.GuardianAttackStrategy}（elder=true）。
 */
public class AncientGuardianFlailItem extends BaseFlailItem {

    public AncientGuardianFlailItem(@NotNull FlailComponent flailComponent, @NotNull ModRarity rarity) {
        super(flailComponent, rarity);
    }

    @Override
    @NotNull
    public FlailAttackStrategy getAttackStrategy() {
        return new GuardianFlailItem.GuardianAttackStrategy(true);
    }
}
