package org.confluence.mod.common.item.shovel;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.item.common.BaseHammerItem;
import org.confluence.mod.common.item.common.BaseShovelItem;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

/**
 * 掘墓者铲子
 * 继承 BaseShovelItem，使用 BaseHammerItem.hammerMineBlock 方法实现 3x3 挖掘
 * 挖掘等级：铁（NEEDS_2_LEVEL）
 * 特点：对柔软方块挖掘特别快
 */
public class GraveDiggersShovel extends BaseShovelItem {

    /**
     * 需要 2 次敲击的方块（1秒）
     */
    private static final Supplier<Set<Block>> FAST_BLOCKS_2_HITS = Suppliers.memoize(() -> {
        Set<Block> set = new java.util.HashSet<>();
        // 原版方块
        set.add(Blocks.DIRT);
        set.add(Blocks.GRASS_BLOCK);
        set.add(Blocks.SAND);
        set.add(Blocks.SNOW_BLOCK);
        set.add(Blocks.CLAY);
        set.add(Blocks.MUD);

        // Terraria 方块 - 灰烬相关
        set.add(NatureBlocks.ASH_BLOCK.get());
        set.add(NatureBlocks.ASH_GRASS_BLOCK.get());
        set.add(NatureBlocks.ASH_GRASS.get());

        // Terraria 方块 - 腐化相关
        set.add(NatureBlocks.CORRUPT_GRASS_BLOCK.get());
        set.add(NatureBlocks.CORRUPT_GRASS.get());
        set.add(NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get());

        // Terraria 方块 - 猩红相关
        set.add(NatureBlocks.CRIMSON_GRASS_BLOCK.get());
        set.add(NatureBlocks.CRIMSON_GRASS.get());
        set.add(NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get());

        // Terraria 方块 - 神圣相关
        set.add(NatureBlocks.HALLOW_GRASS_BLOCK.get());
        set.add(NatureBlocks.HALLOW_GRASS.get());

        // Terraria 方块 - 丛林草
        set.add(NatureBlocks.JUNGLE_GRASS_BLOCK.get());

        // Terraria 方块 - 蘑菇草
        set.add(NatureBlocks.MUSHROOM_GRASS_BLOCK.get());

        // Terraria 方块 - 泥沙
        set.add(NatureBlocks.SILT_BLOCK.get());
        set.add(NatureBlocks.SLUSH.get());

        return set;
    });

    /**
     * 需要 4 次敲击的方块（2秒）
     * 使用 Supplier 延迟初始化，避免类加载时触发 NatureBlocks 的循环依赖
     */
    private static final Supplier<Set<Block>> FAST_BLOCKS_4_HITS = Suppliers.memoize(() -> {
        Set<Block> set = new java.util.HashSet<>();
        set.add(NatureBlocks.EBONSAND.get());
        set.add(NatureBlocks.CRIMSAND.get());
        set.add(NatureBlocks.PEARLSAND.get());
        // Shell Pile 和 The Dirtiest Block 需要后续添加
        return set;
    });

    private static final Tier GRAVE_DIGGERS_TIER = new ModTiers.PoweredTier(
            40, // 镐力 12
            ModTags.Blocks.NEEDS_2_LEVEL, // 挖掘等级：铁
            -1, // 耐久
            5.0F, // 挖掘速度
            2.0F, // 攻击伤害加成
            14, // 附魔能力
            (Supplier<Ingredient>) () -> Ingredient.of() // 无修复材料
    );

    public GraveDiggersShovel(float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(GRAVE_DIGGERS_TIER, rawDamage, rawSpeed, properties, rarity);
    }

    public GraveDiggersShovel(float rawDamage, float rawSpeed, ModRarity rarity) {
        super(GRAVE_DIGGERS_TIER, rawDamage, rawSpeed, rarity);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        // 实现 3x3 挖掘
        if (!level.isClientSide && entity instanceof net.minecraft.world.entity.player.Player) {
            BaseHammerItem.hammerMineBlock(stack, level, state, pos, entity);
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    /**
     * 获取自定义挖掘速度，对特定方块加快挖掘
     */
    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        Block block = state.getBlock();

        if (FAST_BLOCKS_2_HITS.get().contains(block)) {
            // 2 次敲击，原速度 * 3
            return super.getDestroySpeed(stack, state) * 3.0F;
        } else if (FAST_BLOCKS_4_HITS.get().contains(block)) {
            // 4 次敲击，原速度 * 1.5
            return super.getDestroySpeed(stack, state) * 1.5F;
        }

        return super.getDestroySpeed(stack, state);
    }
}
