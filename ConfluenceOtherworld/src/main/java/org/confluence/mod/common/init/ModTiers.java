package org.confluence.mod.common.init;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.mesdag.portlib.diff.Diff;
import org.mesdag.portlib.wrapper.common.PortSimpleTier;

import java.util.function.Supplier;

/// [镐的Terraria Wiki页面](https://terraria.wiki.gg/zh/wiki/%E9%95%90)
public final class ModTiers {
    public static final Tier CACTUS = new PoweredTier(35, ModTags.Blocks.NEEDS_2_LEVEL, 220, 4, 1, 4, () -> Ingredient.of(Items.CACTUS));
    public static final Tier COPPER = new PoweredTier(35, ModTags.Blocks.NEEDS_2_LEVEL, 250, 4, 1, 5, () -> Ingredient.of(Items.COPPER_INGOT));
    public static final Tier TIN = new PoweredTier(35, ModTags.Blocks.NEEDS_2_LEVEL, 270, 4, 1, 6, () -> Ingredient.of(MaterialItems.TIN_INGOT));
    public static final Tier IRON = new PoweredTier(40, ModTags.Blocks.NEEDS_2_LEVEL, 286, 5, 2, 7, () -> Ingredient.of(Items.IRON_INGOT));
    public static final Tier LEAD = new PoweredTier(43, ModTags.Blocks.NEEDS_2_LEVEL, 304, 6, 2, 8, () -> Ingredient.of(MaterialItems.LEAD_INGOT));
    public static final Tier SILVER = new PoweredTier(45, ModTags.Blocks.NEEDS_2_LEVEL, 546, 6, 2, 9, () -> Ingredient.of(MaterialItems.SILVER_INGOT));
    public static final Tier TUNGSTEN = new PoweredTier(50, ModTags.Blocks.NEEDS_3_LEVEL, 890, 7, 2, 10, () -> Ingredient.of(MaterialItems.TUNGSTEN_INGOT));
    public static final Tier GOLD = new PoweredTier(55, ModTags.Blocks.NEEDS_4_LEVEL, 1600, 10, 3, 11, () -> Ingredient.of(Items.GOLD_INGOT));
    public static final Tier CANDY_CANE = new PoweredTier(55, ModTags.Blocks.NEEDS_4_LEVEL, 10000, 7, 2, 11, () -> Ingredient.of(Items.SUGAR));
    public static final Tier FOSSIL = new PoweredTier(55, ModTags.Blocks.NEEDS_4_LEVEL, 10000, 8, 3, 11, () -> Ingredient.of(MaterialItems.STURDY_FOSSIL));
    public static final Tier BONE = new PoweredTier(55, ModTags.Blocks.NEEDS_4_LEVEL, 10000, 13, 3, 11, () -> Ingredient.of(Items.BONE_BLOCK));
    public static final Tier PLATINUM = new PoweredTier(59, ModTags.Blocks.NEEDS_4_LEVEL, 1661, 10, 3, 11, () -> Ingredient.of(MaterialItems.PLATINUM_INGOT));
    public static final Tier REAVER_SHARK = new PoweredTier(59, ModTags.Blocks.NEEDS_4_LEVEL, 10000, 9, 3, 10, () -> Ingredient.of(MaterialItems.SHARK_FIN));

    // 无限耐久uses为10000

    public static final Tier DEMONITE = new PoweredTier(65, ModTags.Blocks.NEEDS_5_LEVEL, 10000, 24, 5, 12, () -> Ingredient.of(MaterialItems.DEMONITE_INGOT));
    public static final Tier CRIMTANE = new PoweredTier(70, ModTags.Blocks.NEEDS_5_LEVEL, 10000, 25, 6, 13, () -> Ingredient.of(MaterialItems.CRIMTANE_INGOT));
    public static final Tier METEOR = new PoweredTier(80, ModTags.Blocks.NEEDS_6_LEVEL, 10000, 27, 6.5f, 14, () -> Ingredient.of(MaterialItems.METEORITE_INGOT));
    public static final Tier HELLSTONE = new PoweredTier(100, ModTags.Blocks.NEEDS_6_LEVEL, 10000, 30, 7, 15, () -> Ingredient.of(MaterialItems.HELLSTONE_INGOT));
    public static final Tier COBALT = new PoweredTier(110, ModTags.Blocks.NEEDS_7_LEVEL, 10000, 31, 8, 15, () -> Ingredient.of(MaterialItems.COBALT_INGOT));
    public static final Tier PALLADIUM = new PoweredTier(130, ModTags.Blocks.NEEDS_7_LEVEL, 10000, 32, 12, 15, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT));
    public static final Tier MYTHRIL = new PoweredTier(150, ModTags.Blocks.NEEDS_8_LEVEL, 10000, 33, 14, 16, () -> Ingredient.of(MaterialItems.MYTHRIL_INGOT));
    public static final Tier ORICHALCUM = new PoweredTier(165, ModTags.Blocks.NEEDS_8_LEVEL, 10000, 34, 16, 16, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT));
    public static final Tier ADAMANTITE = new PoweredTier(180, ModTags.Blocks.NEEDS_8_LEVEL, 10000, 35, 22, 17, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT));
    public static final Tier TITANIUM = new PoweredTier(190, ModTags.Blocks.NEEDS_8_LEVEL, 10000, 36, 24, 18, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT));
    public static final Tier SPECTRE = new PoweredTier(200, ModTags.Blocks.NEEDS_9_LEVEL, 10000, 48, 26, 21, () -> Ingredient.of(MaterialItems.SPECTRE_INGOT));
    public static final Tier CHLOROPHYTE = new PoweredTier(200, ModTags.Blocks.NEEDS_9_LEVEL, 10000, 47, 26, 20, () -> Ingredient.of(MaterialItems.CHLOROPHYTE_INGOT));
    public static final Tier HALLOWED = new PoweredTier(200, ModTags.Blocks.NEEDS_9_LEVEL, 10000, 46, 25, 19, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT));
    public static final Tier SHROOMITE = new PoweredTier(200, ModTags.Blocks.NEEDS_9_LEVEL, 10000, 60, 28, 22, () -> Ingredient.of(MaterialItems.SHROOMITE_INGOT));
    public static final Tier LIHZAHRD = new PoweredTier(210, ModTags.Blocks.UNBREAKABLE, 10000, 80, 32, 24, () -> Ingredient.of(DecorativeBlocks.LIHZAHRD_BRICKS.FULL));
    public static final Tier LUMINITE = new PoweredTier(225, ModTags.Blocks.UNBREAKABLE, 10000, 95, 38, 25, () -> Ingredient.of(MaterialItems.LUMINITE_INGOT));

    // 给一些无限耐久的物品使用的
    public static final Tier UNBREAKABLE = new PoweredTier(190, ModTags.Blocks.NEEDS_2_LEVEL, 10000, 4, 1, 18, Ingredient::of);

    /// @return 原版Tiers的对应镐力
    public static int getPowerForVanillaTiers(Tiers tiers) {
        return switch (tiers) {
            case WOOD -> 35;
            case STONE -> 38;
            case GOLD -> 39;
            case IRON -> 40;
            case DIAMOND -> 59;
            case NETHERITE -> 90;
            default -> -1; // 有模组添加了新枚举
        };
    }

    /// 镐力 ======================= 等级
    /// 全都能挖，比如丛林蜥蜴砖
    /// 201 ======================= 9
    /// 200 神圣
    /// 191 ======================= 8
    /// 190 钛金
    /// 180 精金
    /// 165 山铜
    /// 150 秘银
    /// 131 ======================= 7
    /// 130 钯金
    /// 110 钴
    /// 101 ======================= 6
    /// 100 狱石
    /// 90 下界合金
    /// 71 ======================== 5
    /// 70 猩红
    /// 65 腐化
    /// 60 ======================== 4
    /// 59 铂金、骨镐、掠夺鲨、钻石
    /// 55 金（升级）、糖棒、化石
    /// 51 ======================== 3
    /// 50 钨
    /// 46 ======================== 2
    /// 45 银
    /// 43 铅
    /// 40 铁
    /// 39 金（原版）
    /// 38 石
    /// 35 木、铜、锡
    /// 34 ======================== 1
    /// 什么也挖不了
    public static boolean isCorrectToolForDrops(int power, ItemStack pickaxeItem, BlockState blockState) {
        if (!blockState.requiresCorrectToolForDrops()) return true;
        if (!pickaxeItem.isCorrectToolForDrops(blockState)) return false;
        if (power == -1 || power >= 201) return true;
        if (power >= 191) return !blockState.is(ModTags.Blocks.NEEDS_9_LEVEL);
        if (power >= 131) return !blockState.is(ModTags.Blocks.NEEDS_8_LEVEL);
        if (power >= 101) return !blockState.is(ModTags.Blocks.NEEDS_7_LEVEL);
        if (power >= 71) return !blockState.is(ModTags.Blocks.NEEDS_6_LEVEL);
        if (power >= 60) return !blockState.is(ModTags.Blocks.NEEDS_5_LEVEL);
        if (power >= 51) return !blockState.is(ModTags.Blocks.NEEDS_4_LEVEL);
        if (power >= 46) return !blockState.is(ModTags.Blocks.NEEDS_3_LEVEL);
        if (power >= 34) return !blockState.is(ModTags.Blocks.NEEDS_2_LEVEL);
        return !blockState.is(ModTags.Blocks.NEEDS_1_LEVEL);
    }

    public static class PoweredTier extends PortSimpleTier {
        private final int power;

        /// @param power                   镐力
        /// @param incorrectBlocksForDrops 不能挖的方块，请参照这个[#INCORRECT_FOR_WOODEN_TOOL]。
        /// @param uses                    耐久
        /// @param speed                   挖掘速度（挖不正确的方块速度为1）
        /// @param attackDamageBonus       攻击伤害加成
        /// @param enchantmentValue        附魔能力
        /// @param repairIngredient        修复材料
        public PoweredTier(int power, TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
            super(incorrectBlocksForDrops, uses, speed, attackDamageBonus, enchantmentValue, repairIngredient);
            this.power = power;
        }

        public int getPower() {
            return power;
        }

        @Diff
        @Override
        public int getLevel() {
            return power;
        }

        @Override
        public String toString() {
            return "PoweredTier[" +
                    "incorrectBlocksForDrops=" + getTag() + ", " +
                    "uses=" + getUses() + ", " +
                    "speed=" + getSpeed() + ", " +
                    "attackDamageBonus=" + getAttackDamageBonus() + ", " +
                    "enchantmentValue=" + getEnchantmentValue() + ", " +
                    "repairIngredient=" + getRepairIngredient() + ", " +
                    "power=" + power + ']';
        }
    }
}
