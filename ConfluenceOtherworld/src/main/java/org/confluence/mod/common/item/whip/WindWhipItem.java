package org.confluence.mod.common.item.whip;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;

import java.util.function.Supplier;

/// 风鞭：被击中的敌人和玩家之间高度差越大，伤害越高。
///
/// 每差 1 格提高 6.25%，最多提高到 1.75 倍（即高度差 12 格封顶）。
/// 攻速按原 0.75 的挥动周期放慢 30%（`0.75 → 0.225`，约 11.4 tick → 16.3 tick）。
public final class WindWhipItem extends BaseWhipItem {
    /// 每 1 格高度差提供的伤害加成（原 0.25，降低 75%）。
    private static final float BONUS_PER_BLOCK = 0.0625F;
    /// 高度差带来的最大额外倍率（不含基础 1 倍，原 3.0，降低 75%）。
    private static final float MAX_BONUS = 0.75F;

    public WindWhipItem(Supplier<? extends SummonMarkType> summonMarkType) {
        super("wind_whip", 18.0F, 0.225F, 2.0F, 15, summonMarkType);
    }

    @Override
    public float damageMultiplierForTarget(Player owner, LivingEntity target) {
        float heightDifference = (float) Math.abs(target.getY() - owner.getY());
        return 1.0F + Math.min(MAX_BONUS, heightDifference * BONUS_PER_BLOCK);
    }
}
