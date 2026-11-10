package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Predicate;

/// 通用附魔实现：只带「类别 / 槽位 / 最高等级 / 互斥判定」四件事。
///
/// 具体效果不写在附魔类里，统一由 {@link EnchantmentBehaviors} 在各事件的结算点上处理——
/// 这样同一个附魔的数值只有一个来源，也不会把逻辑散到二十几个类里。
///
/// 这些附魔**暂不开放附魔台**：{@link #canApplyAtEnchantingTable} 与 {@link #isDiscoverable} 都返回 false，
/// 附魔书只从创造模式物品栏或战利品取得。
public class SimpleEnchantment extends Enchantment {
    private final int maxLevel;
    private final Predicate<Enchantment> incompatible;

    public SimpleEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLevel) {
        this(rarity, category, slots, maxLevel, other -> false);
    }

    public SimpleEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLevel, Predicate<Enchantment> incompatible) {
        super(rarity, category, slots);
        this.maxLevel = maxLevel;
        this.incompatible = incompatible;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
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
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !incompatible.test(other) && super.checkCompatibility(other);
    }
}
