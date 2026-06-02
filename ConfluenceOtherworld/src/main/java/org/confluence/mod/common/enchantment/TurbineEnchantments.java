package org.confluence.mod.common.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.confluence.mod.common.init.ModEnchantments;

/**
 * <h1>涡轮 — 连枷涡轮附魔效果</h1>
 * 连枷 SPIN 阶段根据持续时长提供伤害和转速加成。
 * <p>
 * 每 {@value #TICKS_PER_STACK} tick（1.5 秒）叠加一层，每层 +2% 伤害和速度，
 * 最多 {@value #MAX_STACKS} 层（+8%）。离开 SPIN 阶段后归零。
 * <p>
 * 与 {@link WindBurstEnchantments}（flail_wind_burst）互斥。
 */
public final class TurbineEnchantments {
    /** 每层所需 tick 数（30 tick = 1.5 秒） */
    public static final int TICKS_PER_STACK = 30;
    /** 最大叠层数 */
    public static final int MAX_STACKS = 4;
    /** 每层伤害/速度加成比例 */
    public static final float BONUS_PER_STACK = 0.1F;

    /**
     * 获取玩家手持物品上的涡轮附魔等级。
     *
     * @param player 连枷持有者
     * @return 附魔等级（0 表示无此附魔）
     */
    public static int getLevel(Player player) {
        Holder<Enchantment> turbineHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.FLAIL_TURBINE);
        int level = EnchantmentHelper.getTagEnchantmentLevel(turbineHolder, player.getMainHandItem());
        if (level <= 0) {
            level = EnchantmentHelper.getTagEnchantmentLevel(turbineHolder, player.getOffhandItem());
        }
        return level;
    }

    /**
     * 根据当前 SPIN tick 数计算涡轮加成倍率。
     *
     * @param player    连枷持有者
     * @param spinTicks 当前 SPIN 持续 tick 数
     * @return 倍率加成（0.0 ~ 0.08 × enchantLevel）
     */
    public static float getBonus(Player player, int spinTicks) {
        if (spinTicks <= 0) return 0.0F;
        int level = getLevel(player);
        if (level <= 0) return 0.0F;
        int stacks = Math.min(spinTicks / TICKS_PER_STACK, MAX_STACKS);
        return stacks * BONUS_PER_STACK * level;
    }
}
