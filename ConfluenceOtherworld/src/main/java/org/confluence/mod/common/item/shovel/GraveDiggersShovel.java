package org.confluence.mod.common.item.shovel;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.BaseHammerItem;
import org.confluence.mod.common.item.common.BaseShovelItem;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import java.util.function.Supplier;

/// 掘墓者铲子
/// 继承 BaseShovelItem，使用 BaseHammerItem.hammerMineBlock 方法实现 3x3 挖掘
/// 挖掘等级：铁（NEEDS_2_LEVEL）
/// 特点：对柔软方块挖掘特别快
public class GraveDiggersShovel extends BaseShovelItem {
    // 需要 2 次敲击的方块
    private static final Supplier<Reference2IntMap<Block>> FAST_BLOCKS_HITS = Suppliers.memoize(() -> {
        Reference2IntMap<Block> map = new Reference2IntOpenHashMap<>();
        // 2 次敲击的方块（1秒）
        // 原版方块
        map.put(Blocks.DIRT, 2);
        map.put(Blocks.GRASS_BLOCK, 2);
        map.put(Blocks.SAND, 2);
        map.put(Blocks.SNOW_BLOCK, 2);
        map.put(Blocks.CLAY, 2);
        map.put(Blocks.MUD, 2);

        // Terraria 方块 - 灰烬相关
        map.put(NatureBlocks.ASH_BLOCK.get(), 2);
        map.put(NatureBlocks.ASH_GRASS_BLOCK.get(), 2);
        map.put(NatureBlocks.ASH_GRASS.get(), 2);

        // Terraria 方块 - 腐化相关
        map.put(NatureBlocks.CORRUPT_GRASS_BLOCK.get(), 2);
        map.put(NatureBlocks.CORRUPT_GRASS.get(), 2);
        map.put(NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get(), 2);

        // Terraria 方块 - 猩红相关
        map.put(NatureBlocks.CRIMSON_GRASS_BLOCK.get(), 2);
        map.put(NatureBlocks.CRIMSON_GRASS.get(), 2);
        map.put(NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get(), 2);

        // Terraria 方块 - 神圣相关
        map.put(NatureBlocks.HALLOW_GRASS_BLOCK.get(), 2);
        map.put(NatureBlocks.HALLOW_GRASS.get(), 2);

        // Terraria 方块 - 丛林草
        map.put(NatureBlocks.JUNGLE_GRASS_BLOCK.get(), 2);

        // Terraria 方块 - 蘑菇草
        map.put(NatureBlocks.MUSHROOM_GRASS_BLOCK.get(), 2);

        // Terraria 方块 - 泥沙
        map.put(NatureBlocks.SILT_BLOCK.get(), 2);
        map.put(NatureBlocks.SLUSH.get(), 2);

        // 4 次敲击的方块（2秒）
        map.put(NatureBlocks.EBONSAND.get(), 4);
        map.put(NatureBlocks.CRIMSAND.get(), 4);
        map.put(NatureBlocks.PEARLSAND.get(), 4);
        // Shell Pile 和 The Dirtiest Block 需要后续添加

        return map;
    });

    public GraveDiggersShovel(float rawDamage, float rawSpeed, PortItem.PortProperties properties, ModRarity rarity) {
        super(ModTiers.GRAVE_DIGGERS_TIER, rawDamage, rawSpeed, properties, rarity);
    }

    public GraveDiggersShovel(float rawDamage, float rawSpeed, ModRarity rarity) {
        super(ModTiers.GRAVE_DIGGERS_TIER, rawDamage, rawSpeed, rarity);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        // 实现 3x3 挖掘
        BaseHammerItem.hammerMineBlock(stack, level, state, pos, entity);
        return super.mineBlock(stack, level, state, pos, entity);
    }

    /// 获取自定义挖掘速度，对特定方块加快挖掘
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        int hits = FAST_BLOCKS_HITS.get().getInt(state.getBlock());
        float destroySpeed = super.getDestroySpeed(stack, state);
        if (hits == 2) {
            // 2 次敲击，原速度 * 3
            return destroySpeed * 3.0F;
        } else if (hits == 4) {
            // 4 次敲击，原速度 * 1.5
            return destroySpeed * 1.5F;
        }
        return destroySpeed;
    }
}
