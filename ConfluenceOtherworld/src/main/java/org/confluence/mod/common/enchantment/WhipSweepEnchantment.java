package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.mod.common.init.ModEnchantments;

/// 允许鞭子偶尔改用横扫轨迹的专属附魔。
///
/// <p>等级、附魔台消耗和适用槽位与 1.21 侧保持一致。横扫是否触发以及触发后的
/// 轨迹和伤害仍由一次挥鞭对应的攻击实体决定，附魔本身不复制战斗逻辑。</p>
public final class WhipSweepEnchantment extends Enchantment {
    public WhipSweepEnchantment() {
        super(Rarity.VERY_RARE, ModEnchantments.Categories.WHIP, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + 8 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 18 + 8 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return other != this && super.checkCompatibility(other);
    }
}
