package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.confluence.mod.common.init.ModEnchantments;

/// 涡轮：连枷 SPIN 阶段根据持续时长提供伤害、挥舞速度、投出速度和命中间隔加成。
///
/// 每 {@value #TICKS_PER_STACK} tick（1.5 秒）叠加一层，每层 +2.5% 加成，
/// 最多 {@value #MAX_STACKS} 层（+10%），再乘以附魔等级。离开 SPIN 阶段后归零。
///
/// 与 [WindBurstEnchantment]（flail_wind_burst）互斥。
public final class TurbineEnchantment extends Enchantment {
    /// 每层所需 tick 数（30 tick = 1.5 秒）
    public static final int TICKS_PER_STACK = 30;
    /// 最大叠层数
    public static final int MAX_STACKS = 4;
    /// 每层伤害/速度加成比例
    public static final float BONUS_PER_STACK = 0.025F;

    public TurbineEnchantment() {
        super(Rarity.RARE, ModEnchantments.Categories.FLAIL, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 25 + 25 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 75 + 25 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof WindBurstEnchantment) && super.checkCompatibility(other);
    }

    /// 读取玩家手持或副手物品上的涡轮附魔等级。
    ///
    /// @param player 连枷持有者
    /// @return 附魔等级（0 表示无此附魔）
    public static int getLevel(Player player) {
        int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.FLAIL_TURBINE.get(), player.getMainHandItem());
        if (level <= 0) {
            level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.FLAIL_TURBINE.get(), player.getOffhandItem());
        }
        return level;
    }

    /// 根据当前 SPIN tick 数计算涡轮加成倍率。
    ///
    /// @param player    连枷持有者
    /// @param spinTicks 当前 SPIN 持续 tick 数
    /// @return 倍率加成（0.0 ~ 0.10 × 附魔等级）
    public static float getBonus(Player player, int spinTicks) {
        if (spinTicks <= 0) return 0.0F;
        int level = getLevel(player);
        if (level <= 0) return 0.0F;
        int stacks = Math.min(spinTicks / TICKS_PER_STACK, MAX_STACKS);
        return stacks * BONUS_PER_STACK * level;
    }
}
