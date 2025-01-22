package org.confluence.mod.common.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.SimpleTier;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * <a href="https://terraria.wiki.gg/zh/wiki/%E9%95%90">镐的Terraria Wiki页面</a> <p>
 */
public final class ModTiers {
    public static final Tier CACTUS = new PoweredTier(35, ModTags.Blocks.NEEDS_2_LEVEL, 144, 3, 1, 4, () -> Ingredient.of(Items.CACTUS));
    public static final Tier COPPER = new PoweredTier(35, ModTags.Blocks.NEEDS_2_LEVEL, 250, 4, 1, 5, () -> Ingredient.of(Items.COPPER_INGOT));
    public static final Tier TIN = new PoweredTier(35, ModTags.Blocks.NEEDS_2_LEVEL, 270, 4, 1, 5, () -> Ingredient.of(MaterialItems.TIN_INGOT.get()));
    public static final Tier IRON = new PoweredTier(40, ModTags.Blocks.NEEDS_2_LEVEL, 286, 5, 2, 14, () -> Ingredient.of(Items.IRON_INGOT));
    public static final Tier LEAD = new PoweredTier(43, ModTags.Blocks.NEEDS_2_LEVEL, 286, 6, 2, 14, () -> Ingredient.of(MaterialItems.LEAD_INGOT.get()));
    public static final Tier SILVER = new PoweredTier(45, ModTags.Blocks.NEEDS_2_LEVEL, 304, 6, 2, 14, () -> Ingredient.of(MaterialItems.SILVER_INGOT.get()));
    public static final Tier TUNGSTEN = new PoweredTier(50, ModTags.Blocks.NEEDS_3_LEVEL, 648, 8, 2, 18, () -> Ingredient.of(MaterialItems.TUNGSTEN_INGOT.get()));
    public static final Tier GOLD = new PoweredTier(55, ModTags.Blocks.NEEDS_4_LEVEL, 1600, 8, 3, 22, () -> Ingredient.of(Items.GOLD_INGOT));
    public static final Tier CANDY = new PoweredTier(55, ModTags.Blocks.NEEDS_4_LEVEL, 4000, 6, 2, 14, () -> Ingredient.of(Items.SUGAR)); // 糖(圣诞限定）
    public static final Tier FOSSIL = new PoweredTier(55, ModTags.Blocks.NEEDS_4_LEVEL, 1200, 8, 3, 22, () -> Ingredient.of(MaterialItems.STURDY_FOSSIL.get()));
    // 骨镐 55 4
    public static final Tier PLATINUM = new PoweredTier(59, ModTags.Blocks.NEEDS_4_LEVEL, 1661, 8, 3, 22, () -> Ingredient.of(MaterialItems.PLATINUM_INGOT.get()));
    // 掠夺鲨 59 4

    public static final Tier DEMONITE = new PoweredTier(65, ModTags.Blocks.NEEDS_5_LEVEL, 10000, 10, 4, 24, () -> Ingredient.of(MaterialItems.DEMONITE_INGOT.get()));
    public static final Tier TR_CRIMSON = new PoweredTier(70, ModTags.Blocks.NEEDS_5_LEVEL, 10000, 11, 4, 25, () -> Ingredient.of(MaterialItems.TR_CRIMSON_INGOT.get()));
    public static final Tier HELLSTONE = new PoweredTier(100, ModTags.Blocks.NEEDS_6_LEVEL, 10000, 14, 6, 27, () -> Ingredient.of(MaterialItems.HELLSTONE_INGOT.get()));
    public static final Tier COBALT = new PoweredTier(110, ModTags.Blocks.NEEDS_7_LEVEL, 10000, 4, 1, 5, () -> Ingredient.of(MaterialItems.COBALT_INGOT.get()));
    public static final Tier PALLADIUM = new PoweredTier(130, ModTags.Blocks.NEEDS_7_LEVEL, 10000, 4, 1, 5, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT.get()));
    public static final Tier MITHRIL = new PoweredTier(150, ModTags.Blocks.NEEDS_7_LEVEL, 10000, 4, 1, 5, () -> Ingredient.of(MaterialItems.MITHRIL_INGOT.get()));
    public static final Tier ORICHALCUM = new PoweredTier(165, ModTags.Blocks.NEEDS_7_LEVEL, 10000, 4, 1, 5, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT.get()));
    public static final Tier ADAMANTITE = new PoweredTier(180, ModTags.Blocks.NEEDS_8_LEVEL, 10000, 4, 1, 5, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT.get()));
    public static final Tier TITANIUM = new PoweredTier(190, ModTags.Blocks.NEEDS_8_LEVEL, 10000, 4, 1, 5, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT.get()));
    // 幽灵镐 200 9
    // 叶绿镐 200 9
    public static final Tier HALLOWED = new PoweredTier(200, ModTags.Blocks.NEEDS_9_LEVEL, 10000, 4, 1, 5, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT.get()));
    // 蘑菇矿挖爪 200 9
    public static final Tier LIHZAHRD = new PoweredTier(210, ModTags.Blocks.UNBREAKABLE, 10000, 4, 1, 5, () -> Ingredient.of(DecorativeBlocks.LIHZAHRD_BRICKS.get().asItem()));
    // 四柱镐 225

    /**
     * @return 原版Tiers的对应镐力
     */
    public static int getPowerForVanillaTiers(Tiers tiers) {
        return switch (tiers) {
            case WOOD -> 35;
            case STONE -> 38;
            case GOLD -> 39;
            case IRON -> 40;
            case DIAMOND -> 85;
            case NETHERITE -> 100;
        };
    }

    /**
     * 镐力 ======================= 等级 <p>
     * 全都能挖，比如丛林蜥蜴砖 <p>
     * 201 ======================= 9 <p>
     * 200 神圣 <p>
     * 191 ======================= 8 <p>
     * 190 钛金 <p>
     * 180 精金 <p>
     * 166 ======================= 7 <p>
     * 165 山铜 <p>
     * 150 秘银 <p>
     * 130 钯金 <p>
     * 110 钴 <p>
     * 101 ======================= 6 <p>
     * 100 狱石、下界合金 <p>
     * 85 钻石 <p>
     * 71 ======================= 5 <p>
     * 70 猩红 <p>
     * 65 腐化 <p>
     * 60 ======================= 4 <p>
     * 59 铂金、骨镐、掠夺鲨 <p>
     * 55 金（升级）、糖棒、化石 <p>
     * 51 ======================= 3 <p>
     * 50 钨 <p>
     * 46 ======================= 2 <p>
     * 45 银 <p>
     * 43 铅 <p>
     * 40 铁 <p>
     * 39 金（原版） <p>
     * 38 石 <p>
     * 35 木、铜、锡 <p>
     * 34 ======================= 1 <p>
     * 什么也挖不了
     */
    public static boolean isCorrectToolForDrops(int power, ItemStack pickaxeItem, BlockState blockState) {
        if (!blockState.requiresCorrectToolForDrops()) return true;
        if (!pickaxeItem.isCorrectToolForDrops(blockState)) return false;
        if (power >= 201) return true;
        if (power >= 191) return !blockState.is(ModTags.Blocks.NEEDS_9_LEVEL);
        if (power >= 166) return !blockState.is(ModTags.Blocks.NEEDS_8_LEVEL);
        if (power >= 101) return !blockState.is(ModTags.Blocks.NEEDS_7_LEVEL);
        if (power >= 71) return !blockState.is(ModTags.Blocks.NEEDS_6_LEVEL);
        if (power >= 60) return !blockState.is(ModTags.Blocks.NEEDS_5_LEVEL);
        if (power >= 51) return !blockState.is(ModTags.Blocks.NEEDS_4_LEVEL);
        if (power >= 46) return !blockState.is(ModTags.Blocks.NEEDS_3_LEVEL);
        if (power >= 34) return !blockState.is(ModTags.Blocks.NEEDS_2_LEVEL);
        return false;
    }

    public static class PoweredTier extends SimpleTier {
        private final int power;

        /**
         * @param power                   镐力
         * @param incorrectBlocksForDrops 不能挖的方块，请参照这个{@link BlockTags#INCORRECT_FOR_WOODEN_TOOL}。
         * @param uses                    耐久
         * @param speed                   挖掘速度（挖不正确的方块速度为1）
         * @param attackDamageBonus       攻击伤害加成
         * @param enchantmentValue        附魔能力
         * @param repairIngredient        修复材料
         */
        public PoweredTier(int power, TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
            super(incorrectBlocksForDrops, uses, speed, attackDamageBonus, enchantmentValue, repairIngredient);
            this.power = power;
        }

        public int getPower() {
            return power;
        }

        @Override
        public @NotNull Tool createToolProperties(@NotNull TagKey<Block> block) {
            return new Tool(List.of(
                    Tool.Rule.deniesDrops(getIncorrectBlocksForDrops()),
                    Tool.Rule.minesAndDrops(block, getSpeed())
            ), 1.0F, 1);
        }

        @Override
        public @NotNull String toString() {
            return "PoweredTier[" +
                    "incorrectBlocksForDrops=" + getIncorrectBlocksForDrops() + ", " +
                    "uses=" + getUses() + ", " +
                    "speed=" + getSpeed() + ", " +
                    "attackDamageBonus=" + getAttackDamageBonus() + ", " +
                    "enchantmentValue=" + getEnchantmentValue() + ", " +
                    "repairIngredient=" + getRepairIngredient() + ", " +
                    "power=" + power + ']';
        }
    }
}
