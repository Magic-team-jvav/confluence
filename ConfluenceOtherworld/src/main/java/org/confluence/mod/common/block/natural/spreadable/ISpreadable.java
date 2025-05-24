package org.confluence.mod.common.block.natural.spreadable;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.*;

public interface ISpreadable {
    // That was a joke haha!
    BooleanProperty STILL_ALIVE = BooleanProperty.create("still_alive");

    Type getSpreadType();

    default void spread(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.getValue(STILL_ALIVE)) return;
        int chance = serverLevel.getGameRules().getInt(Confluence.SPREADABLE_CHANCE);
        if (chance == 0 || randomSource.nextInt(100) >= chance) return;
        int phase = KillBoard.INSTANCE.getGamePhase().ordinal();
        for (int i = 0; i < 4; ++i) {
            BlockPos targetPos = blockPos.offset(randomSource.nextInt(3) - 1, randomSource.nextInt(5) - 3, randomSource.nextInt(3) - 1);
            if (!serverLevel.isLoaded(targetPos)) continue;
            BlockState beforeTransformState = serverLevel.getBlockState(targetPos);
            Block targetBlock = getSpreadType().blockMap.get(beforeTransformState.getBlock());
            if (targetBlock == null || beforeTransformState.is(SHORT_GRASS) || beforeTransformState.is(FERN) || beforeTransformState.is(TALL_GRASS)) {
                continue; // 不要直接传播草
            }
            if (beforeTransformState.is(DIRT) || beforeTransformState.is(NatureBlocks.ASH_BLOCK.get())) {
                if (!isFullBlock(serverLevel, targetPos.above())) {
                    spreadOrDie(phase, blockState, serverLevel, blockPos, randomSource, targetBlock.defaultBlockState(), targetPos);
                }
            } else {
                spreadOrDie(phase, blockState, serverLevel, blockPos, randomSource, targetBlock.defaultBlockState(), targetPos);
            }
            BlockState above = serverLevel.getBlockState(targetPos.above());
            if (above.is(SHORT_GRASS) || above.is(FERN) || above.is(TALL_GRASS)) {  // 被动传播草
                targetBlock = getSpreadType().blockMap.get(above.getBlock());
                serverLevel.setBlockAndUpdate(targetPos.above(), targetBlock == null ? above : targetBlock.defaultBlockState());
            }
        }
    }

    default boolean isFullBlock(ServerLevel serverLevel, BlockPos pos) {
        return Block.isShapeFullBlock(serverLevel.getBlockState(pos).getCollisionShape(serverLevel, pos));
    }

    default void spreadOrDie(int phase, BlockState selfState, ServerLevel serverLevel, BlockPos selfPos, RandomSource randomSource, BlockState targetState, BlockPos targetPos) {
        spreadTree(serverLevel, targetPos, getSpreadType().blockMap);
        serverLevel.setBlockAndUpdate(targetPos, targetState);
        if (randomSource.nextInt(7) > phase) {
            serverLevel.setBlockAndUpdate(selfPos, selfState.setValue(STILL_ALIVE, false));
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Comparable<T>, V extends T> void spreadTree(ServerLevel serverLevel, BlockPos targetPos, Map<Block, Block> type) {
        BlockState blockState = serverLevel.getBlockState(targetPos.above());
        if (blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES)) {
            Map<BlockPos, BlockState> map = searchFace(serverLevel, targetPos, new Hashtable<>(), 0);
            for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
                BlockState value = entry.getValue();
                if (value == AIR) continue;
                Block block = type.get(value.getBlock());
                if (block == null) continue;
                BlockState targetState = block.defaultBlockState();
                for (Map.Entry<Property<?>, Comparable<?>> entry1 : value.getValues().entrySet()) {
                    if (targetState.hasProperty(entry1.getKey())) {
                        targetState = targetState.setValue((Property<T>) entry1.getKey(), (V) entry1.getValue());
                    }
                }
                serverLevel.setBlockAndUpdate(entry.getKey(), targetState);
            }
        }
    }

    Supplier<Set<Block>> PALMS = Suppliers.memoize(() -> {
        LogBlockSet set = NatureBlocks.PALM_LOG_BLOCKS;
        return Sets.newHashSet(
                set.getLog().get(),
                set.getWood().get(),
                set.getStrippedLog().get(),
                set.getStrippedWood().get(),
                set.getLeaves().get()
        );
    });
    BlockState AIR = Blocks.AIR.defaultBlockState();

    private static Map<BlockPos, BlockState> searchFace(ServerLevel serverLevel, BlockPos targetPos, Map<BlockPos, BlockState> map, int depth) {
        if (depth == 128) return map;
        for (Direction direction : LibUtils.DIRECTIONS) {
            BlockPos relative = targetPos.relative(direction);
            if (map.containsKey(relative)) continue;
            BlockState blockState = serverLevel.getBlockState(relative);
            if (blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES)) {
                map.put(relative, blockState);
                if (PALMS.get().contains(blockState.getBlock())) {
                    searchBox(serverLevel, relative, map, depth + 1);
                } else {
                    searchFace(serverLevel, relative, map, depth + 1);
                }
            } else {
                map.put(relative, AIR);
            }
        }
        return map;
    }

    private static void searchBox(ServerLevel serverLevel, BlockPos targetPos, Map<BlockPos, BlockState> map, int depth) {
        if (depth == 128) return;
        for (BlockPos relative : BlockPos.betweenClosed(targetPos.offset(-1, -1, -1), targetPos.offset(1, 1, 1))) {
            if (map.containsKey(relative)) continue;
            BlockState blockState = serverLevel.getBlockState(relative);
            if (blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES)) {
                map.put(relative.immutable(), blockState);
                if (PALMS.get().contains(blockState.getBlock())) {
                    searchBox(serverLevel, relative, map, depth + 1);
                } else {
                    searchFace(serverLevel, relative, map, depth + 1);
                }
            } else {
                map.put(relative.immutable(), AIR);
            }
        }
    }

    // 到时候溶液也用这个
    enum Type implements StringRepresentable {
        HALLOW(
                getSupplier(DIRT), NatureBlocks.HALLOW_GRASS_BLOCK,

                // 原木
                getSupplier(OAK_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                getSupplier(ACACIA_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                getSupplier(BIRCH_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                getSupplier(CHERRY_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                getSupplier(JUNGLE_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                getSupplier(DARK_OAK_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                getSupplier(MANGROVE_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                getSupplier(SPRUCE_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                NatureBlocks.PALM_LOG_BLOCKS.getLog(), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                NatureBlocks.EBONY_LOG_BLOCKS.getLog(), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getLog(), NatureBlocks.PEARL_LOG_BLOCKS.getLog(),
                // 树皮
                getSupplier(OAK_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                getSupplier(ACACIA_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                getSupplier(BIRCH_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                getSupplier(CHERRY_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                getSupplier(JUNGLE_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                getSupplier(DARK_OAK_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                getSupplier(MANGROVE_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                getSupplier(SPRUCE_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                NatureBlocks.PALM_LOG_BLOCKS.getWood(), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                NatureBlocks.EBONY_LOG_BLOCKS.getWood(), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getWood(), NatureBlocks.PEARL_LOG_BLOCKS.getWood(),
                // 去皮原木
                getSupplier(STRIPPED_ACACIA_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_CHERRY_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_BIRCH_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_DARK_OAK_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_OAK_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_MANGROVE_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_SPRUCE_LOG), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedLog(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),
                // 去皮树皮
                getSupplier(STRIPPED_ACACIA_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_CHERRY_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_BIRCH_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_DARK_OAK_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_OAK_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_MANGROVE_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_SPRUCE_WOOD), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedWood(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(),
                // 树叶
                getSupplier(OAK_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                getSupplier(ACACIA_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                getSupplier(BIRCH_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                getSupplier(CHERRY_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                getSupplier(JUNGLE_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                getSupplier(DARK_OAK_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                getSupplier(MANGROVE_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                getSupplier(SPRUCE_LEAVES), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                NatureBlocks.PALM_LOG_BLOCKS.getLeaves(), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(), NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(),

                // 原版环境方块
                getSupplier(GRASS_BLOCK), NatureBlocks.HALLOW_GRASS_BLOCK,
                getSupplier(STONE), NatureBlocks.PEARL_STONE,
                getSupplier(COBBLESTONE), NatureBlocks.PEARL_COBBLESTONE,
                getSupplier(SANDSTONE), NatureBlocks.PEARL_SANDSTONE,
                getSupplier(SAND), NatureBlocks.PEARL_SAND,
                getSupplier(SHORT_GRASS), NatureBlocks.HALLOW_GRASS,
                //getSupplier(TALL_GRASS), NatureBlocks.HALLOW_GRASS,
                getSupplier(ICE), NatureBlocks.PINK_ICE,
                getSupplier(PACKED_ICE), NatureBlocks.PINK_PACKED_ICE,
                // 邪恶环境方块
                NatureBlocks.TR_CRIMSON_GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK,
                NatureBlocks.CORRUPT_GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK,

                NatureBlocks.EBONY_STONE, NatureBlocks.PEARL_STONE,
                NatureBlocks.TR_CRIMSON_STONE, NatureBlocks.PEARL_STONE,

                NatureBlocks.EBONY_COBBLESTONE, NatureBlocks.PEARL_COBBLESTONE,
                NatureBlocks.TR_CRIMSON_COBBLESTONE, NatureBlocks.PEARL_COBBLESTONE,

                NatureBlocks.HARDENED_SAND_BLOCK, NatureBlocks.PEARL_HARDENED_SAND_BLOCK,
                NatureBlocks.RED_HARDENED_SAND_BLOCK, NatureBlocks.PEARL_HARDENED_SAND_BLOCK,
                NatureBlocks.MOIST_SAND_BLOCK, NatureBlocks.PEARL_MOIST_SAND_BLOCK,
                NatureBlocks.RED_MOIST_SAND_BLOCK, NatureBlocks.PEARL_MOIST_SAND_BLOCK,
                NatureBlocks.EBONY_HARDENED_SAND_BLOCK, NatureBlocks.PEARL_HARDENED_SAND_BLOCK,
                NatureBlocks.EBONY_MOIST_SAND_BLOCK, NatureBlocks.PEARL_MOIST_SAND_BLOCK,
                NatureBlocks.TR_CRIMSON_HARDENED_SAND_BLOCK, NatureBlocks.PEARL_HARDENED_SAND_BLOCK,
                NatureBlocks.TR_CRIMSON_MOIST_SAND_BLOCK, NatureBlocks.PEARL_MOIST_SAND_BLOCK,

                NatureBlocks.EBONY_SANDSTONE, NatureBlocks.PEARL_SANDSTONE,
                NatureBlocks.TR_CRIMSON_SANDSTONE, NatureBlocks.PEARL_SANDSTONE,

                NatureBlocks.PURPLE_ICE, NatureBlocks.PINK_ICE,
                NatureBlocks.PURPLE_PACKED_ICE, NatureBlocks.PINK_PACKED_ICE,
                NatureBlocks.RED_ICE, NatureBlocks.PINK_ICE,
                NatureBlocks.RED_PACKED_ICE, NatureBlocks.PINK_PACKED_ICE,
                // 蘑菇
                NatureBlocks.VICIOUS_MUSHROOM, NatureBlocks.LIFE_MUSHROOM,
                NatureBlocks.VILE_MUSHROOM, NatureBlocks.LIFE_MUSHROOM,
                //矿物
                getSupplier(REDSTONE_ORE), OreBlocks.SANCTIFICATION_REDSTONE_ORE,
                getSupplier(COAL_ORE), OreBlocks.SANCTIFICATION_COAL_ORE,
                getSupplier(LAPIS_ORE), OreBlocks.SANCTIFICATION_LAPIS_ORE,
                getSupplier(COPPER_ORE), OreBlocks.SANCTIFICATION_COPPER_ORE,
                getSupplier(IRON_ORE), OreBlocks.SANCTIFICATION_IRON_ORE,
                getSupplier(EMERALD_ORE), OreBlocks.SANCTIFICATION_EMERALD_ORE,
                getSupplier(DIAMOND_ORE), OreBlocks.SANCTIFICATION_DIAMOND_ORE,
                getSupplier(GOLD_ORE), OreBlocks.SANCTIFICATION_GOLD_ORE,
                OreBlocks.TIN_ORE, OreBlocks.SANCTIFICATION_TIN_ORE,
                OreBlocks.LEAD_ORE, OreBlocks.SANCTIFICATION_LEAD_ORE,
                OreBlocks.SILVER_ORE, OreBlocks.SANCTIFICATION_SILVER_ORE,
                OreBlocks.TUNGSTEN_ORE, OreBlocks.SANCTIFICATION_TUNGSTEN_ORE,
                OreBlocks.PLATINUM_ORE, OreBlocks.SANCTIFICATION_PLATINUM_ORE,

                OreBlocks.CORRUPTION_TIN_ORE, OreBlocks.SANCTIFICATION_TIN_ORE,
                OreBlocks.CORRUPTION_LEAD_ORE, OreBlocks.SANCTIFICATION_LEAD_ORE,
                OreBlocks.CORRUPTION_SILVER_ORE, OreBlocks.SANCTIFICATION_SILVER_ORE,
                OreBlocks.CORRUPTION_TUNGSTEN_ORE, OreBlocks.SANCTIFICATION_TUNGSTEN_ORE,
                OreBlocks.CORRUPTION_PLATINUM_ORE, OreBlocks.SANCTIFICATION_PLATINUM_ORE,
                OreBlocks.CORRUPTION_COAL_ORE, OreBlocks.SANCTIFICATION_COAL_ORE,
                OreBlocks.CORRUPTION_COPPER_ORE, OreBlocks.SANCTIFICATION_COPPER_ORE,
                OreBlocks.CORRUPTION_IRON_ORE, OreBlocks.SANCTIFICATION_IRON_ORE,
                OreBlocks.CORRUPTION_GOLD_ORE, OreBlocks.SANCTIFICATION_GOLD_ORE,
                OreBlocks.CORRUPTION_DIAMOND_ORE, OreBlocks.SANCTIFICATION_DIAMOND_ORE,
                OreBlocks.CORRUPTION_REDSTONE_ORE, OreBlocks.SANCTIFICATION_REDSTONE_ORE,
                OreBlocks.FLESHIFICATION_TIN_ORE, OreBlocks.SANCTIFICATION_TIN_ORE,
                OreBlocks.FLESHIFICATION_LEAD_ORE, OreBlocks.SANCTIFICATION_LEAD_ORE,
                OreBlocks.FLESHIFICATION_SILVER_ORE, OreBlocks.SANCTIFICATION_SILVER_ORE,
                OreBlocks.FLESHIFICATION_TUNGSTEN_ORE, OreBlocks.SANCTIFICATION_TUNGSTEN_ORE,
                OreBlocks.FLESHIFICATION_PLATINUM_ORE, OreBlocks.SANCTIFICATION_PLATINUM_ORE,
                OreBlocks.FLESHIFICATION_COAL_ORE, OreBlocks.SANCTIFICATION_COAL_ORE,
                OreBlocks.FLESHIFICATION_COPPER_ORE, OreBlocks.SANCTIFICATION_COPPER_ORE,
                OreBlocks.FLESHIFICATION_IRON_ORE, OreBlocks.SANCTIFICATION_IRON_ORE,
                OreBlocks.FLESHIFICATION_GOLD_ORE, OreBlocks.SANCTIFICATION_GOLD_ORE,
                OreBlocks.FLESHIFICATION_DIAMOND_ORE, OreBlocks.SANCTIFICATION_DIAMOND_ORE,
                OreBlocks.FLESHIFICATION_REDSTONE_ORE, OreBlocks.SANCTIFICATION_REDSTONE_ORE,
                OreBlocks.DEMONITE_ORE, OreBlocks.SANCTIFICATION_DEMONITE_ORE,
                OreBlocks.TR_CRIMSON_ORE, OreBlocks.SANCTIFICATION_TR_CRIMSON_ORE,
                OreBlocks.FLESHIFICATION_DEMONITE_ORE, OreBlocks.FLESHIFICATION_DEMONITE_ORE,
                OreBlocks.FLESHIFICATION_TR_CRIMSON_ORE, OreBlocks.SANCTIFICATION_TR_CRIMSON_ORE,
                OreBlocks.CORRUPTION_DEMONITE_ORE, OreBlocks.FLESHIFICATION_DEMONITE_ORE,
                OreBlocks.CORRUPTION_TR_CRIMSON_ORE, OreBlocks.SANCTIFICATION_TR_CRIMSON_ORE,
                // 植物
                NatureBlocks.CORRUPT_GRASS, NatureBlocks.HALLOW_GRASS,
                NatureBlocks.TR_CRIMSON_GRASS, NatureBlocks.HALLOW_GRASS,
                NatureBlocks.CRIMSON_THORN, getSupplier(Blocks.AIR),
                NatureBlocks.CORRUPTION_THORN, getSupplier(Blocks.AIR),
                NatureBlocks.JUNGLE_THORN, getSupplier(Blocks.AIR),
                NatureBlocks.PLANTERA_THORN, getSupplier(Blocks.AIR)
        ),
        CRIMSON(
                getSupplier(DIRT), NatureBlocks.TR_CRIMSON_GRASS_BLOCK,
                NatureBlocks.JUNGLE_GRASS_BLOCK, NatureBlocks.TR_CRIMSON_JUNGLE_GRASS_BLOCK,
                // 原木
                getSupplier(OAK_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                getSupplier(ACACIA_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                getSupplier(BIRCH_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                getSupplier(CHERRY_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                getSupplier(JUNGLE_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                getSupplier(DARK_OAK_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                getSupplier(MANGROVE_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                getSupplier(SPRUCE_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),
                NatureBlocks.PALM_LOG_BLOCKS.getLog(), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),

                // 树皮
                getSupplier(OAK_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                getSupplier(ACACIA_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                getSupplier(BIRCH_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                getSupplier(CHERRY_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                getSupplier(JUNGLE_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                getSupplier(DARK_OAK_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                getSupplier(MANGROVE_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                getSupplier(SPRUCE_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),
                NatureBlocks.PALM_LOG_BLOCKS.getWood(), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),

                // 去皮原木
                getSupplier(STRIPPED_ACACIA_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_CHERRY_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_BIRCH_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_DARK_OAK_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_OAK_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_MANGROVE_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_SPRUCE_LOG), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedLog(), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),

                // 去皮树皮
                getSupplier(STRIPPED_ACACIA_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_CHERRY_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_BIRCH_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_DARK_OAK_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_OAK_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_MANGROVE_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_SPRUCE_WOOD), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedWood(), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(),

                // 树叶
                getSupplier(OAK_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                getSupplier(ACACIA_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                getSupplier(BIRCH_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                getSupplier(CHERRY_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                getSupplier(JUNGLE_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                getSupplier(DARK_OAK_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                getSupplier(MANGROVE_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                getSupplier(SPRUCE_LEAVES), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),
                NatureBlocks.PALM_LOG_BLOCKS.getLeaves(), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves(),

                // 原版环境方块
                getSupplier(GRASS_BLOCK), NatureBlocks.TR_CRIMSON_GRASS_BLOCK,
                getSupplier(STONE), NatureBlocks.TR_CRIMSON_STONE,
                getSupplier(COBBLESTONE), NatureBlocks.TR_CRIMSON_COBBLESTONE,
                getSupplier(SANDSTONE), NatureBlocks.TR_CRIMSON_SANDSTONE,
                getSupplier(SAND), NatureBlocks.TR_CRIMSON_SAND,
                getSupplier(SHORT_GRASS), NatureBlocks.TR_CRIMSON_GRASS,
                //getSupplier(TALL_GRASS), NatureBlocks.TR_CRIMSON_GRASS,
                getSupplier(ICE), NatureBlocks.RED_ICE,
                getSupplier(PACKED_ICE), NatureBlocks.RED_PACKED_ICE,
                //矿物
                getSupplier(REDSTONE_ORE), OreBlocks.FLESHIFICATION_REDSTONE_ORE,
                getSupplier(COAL_ORE), OreBlocks.FLESHIFICATION_COAL_ORE,
                getSupplier(LAPIS_ORE), OreBlocks.FLESHIFICATION_LAPIS_ORE,
                getSupplier(COPPER_ORE), OreBlocks.FLESHIFICATION_COPPER_ORE,
                getSupplier(IRON_ORE), OreBlocks.FLESHIFICATION_IRON_ORE,
                getSupplier(EMERALD_ORE), OreBlocks.FLESHIFICATION_EMERALD_ORE,
                getSupplier(DIAMOND_ORE), OreBlocks.FLESHIFICATION_DIAMOND_ORE,
                getSupplier(GOLD_ORE), OreBlocks.FLESHIFICATION_GOLD_ORE,
                OreBlocks.TIN_ORE, OreBlocks.FLESHIFICATION_TIN_ORE,
                OreBlocks.LEAD_ORE, OreBlocks.FLESHIFICATION_LEAD_ORE,
                OreBlocks.SILVER_ORE, OreBlocks.FLESHIFICATION_SILVER_ORE,
                OreBlocks.TUNGSTEN_ORE, OreBlocks.FLESHIFICATION_TUNGSTEN_ORE,
                OreBlocks.PLATINUM_ORE, OreBlocks.FLESHIFICATION_PLATINUM_ORE,
                OreBlocks.DEMONITE_ORE, OreBlocks.FLESHIFICATION_DEMONITE_ORE,
                OreBlocks.TR_CRIMSON_ORE, OreBlocks.FLESHIFICATION_TR_CRIMSON_ORE,

                // 蘑菇
                NatureBlocks.LIFE_MUSHROOM, NatureBlocks.VICIOUS_MUSHROOM,

                NatureBlocks.HARDENED_SAND_BLOCK, NatureBlocks.TR_CRIMSON_HARDENED_SAND_BLOCK,
                NatureBlocks.RED_HARDENED_SAND_BLOCK, NatureBlocks.TR_CRIMSON_HARDENED_SAND_BLOCK,
                NatureBlocks.MOIST_SAND_BLOCK, NatureBlocks.TR_CRIMSON_MOIST_SAND_BLOCK,
                NatureBlocks.RED_MOIST_SAND_BLOCK, NatureBlocks.TR_CRIMSON_MOIST_SAND_BLOCK
        ),
        CORRUPT(
                getSupplier(DIRT), NatureBlocks.CORRUPT_GRASS_BLOCK,
                NatureBlocks.JUNGLE_GRASS_BLOCK, NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK,
                // 原木
                getSupplier(OAK_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                getSupplier(ACACIA_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                getSupplier(BIRCH_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                getSupplier(CHERRY_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                getSupplier(JUNGLE_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                getSupplier(DARK_OAK_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                getSupplier(MANGROVE_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                getSupplier(SPRUCE_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                NatureBlocks.PALM_LOG_BLOCKS.getLog(), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                NatureBlocks.PEARL_LOG_BLOCKS.getLog(), NatureBlocks.EBONY_LOG_BLOCKS.getLog(),
                // 树皮
                getSupplier(OAK_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                getSupplier(ACACIA_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                getSupplier(BIRCH_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                getSupplier(CHERRY_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                getSupplier(JUNGLE_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                getSupplier(DARK_OAK_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                getSupplier(MANGROVE_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                getSupplier(SPRUCE_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                NatureBlocks.PALM_LOG_BLOCKS.getWood(), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),
                NatureBlocks.PEARL_LOG_BLOCKS.getWood(), NatureBlocks.EBONY_LOG_BLOCKS.getWood(),

                // 去皮原木
                getSupplier(STRIPPED_ACACIA_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_CHERRY_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_BIRCH_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_DARK_OAK_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_OAK_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_MANGROVE_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                getSupplier(STRIPPED_SPRUCE_LOG), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedLog(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),
                NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),

                // 去皮树皮
                getSupplier(STRIPPED_ACACIA_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_CHERRY_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_BIRCH_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_DARK_OAK_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_OAK_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_MANGROVE_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                getSupplier(STRIPPED_SPRUCE_WOOD), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedWood(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(),
                // 树叶
                getSupplier(OAK_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                getSupplier(ACACIA_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                getSupplier(BIRCH_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                getSupplier(CHERRY_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                getSupplier(JUNGLE_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                getSupplier(DARK_OAK_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                getSupplier(MANGROVE_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                getSupplier(SPRUCE_LEAVES), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                NatureBlocks.PALM_LOG_BLOCKS.getLeaves(), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),
                NatureBlocks.PEARL_LOG_BLOCKS.getLeaves(), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves(),

                // 原版环境方块
                getSupplier(GRASS_BLOCK), NatureBlocks.CORRUPT_GRASS_BLOCK,
                getSupplier(STONE), NatureBlocks.EBONY_STONE,
                getSupplier(COBBLESTONE), NatureBlocks.EBONY_COBBLESTONE,
                getSupplier(SANDSTONE), NatureBlocks.EBONY_SANDSTONE,
                getSupplier(SAND), NatureBlocks.EBONY_SAND,
                getSupplier(SHORT_GRASS), NatureBlocks.CORRUPT_GRASS,
                //getSupplier(TALL_GRASS), NatureBlocks.CORRUPT_GRASS,
                getSupplier(ICE), NatureBlocks.PURPLE_ICE,
                getSupplier(PACKED_ICE), NatureBlocks.PURPLE_PACKED_ICE,
                //矿物
                getSupplier(REDSTONE_ORE), OreBlocks.CORRUPTION_REDSTONE_ORE,
                getSupplier(COAL_ORE), OreBlocks.CORRUPTION_COAL_ORE,
                getSupplier(LAPIS_ORE), OreBlocks.CORRUPTION_LAPIS_ORE,
                getSupplier(COPPER_ORE), OreBlocks.CORRUPTION_COPPER_ORE,
                getSupplier(IRON_ORE), OreBlocks.CORRUPTION_IRON_ORE,
                getSupplier(EMERALD_ORE), OreBlocks.CORRUPTION_EMERALD_ORE,
                getSupplier(DIAMOND_ORE), OreBlocks.CORRUPTION_DIAMOND_ORE,
                getSupplier(GOLD_ORE), OreBlocks.CORRUPTION_GOLD_ORE,
                OreBlocks.TIN_ORE, OreBlocks.CORRUPTION_TIN_ORE,
                OreBlocks.LEAD_ORE, OreBlocks.CORRUPTION_LEAD_ORE,
                OreBlocks.SILVER_ORE, OreBlocks.CORRUPTION_SILVER_ORE,
                OreBlocks.TUNGSTEN_ORE, OreBlocks.CORRUPTION_TUNGSTEN_ORE,
                OreBlocks.PLATINUM_ORE, OreBlocks.CORRUPTION_PLATINUM_ORE,
                OreBlocks.DEMONITE_ORE, OreBlocks.CORRUPTION_DEMONITE_ORE,
                OreBlocks.TR_CRIMSON_ORE, OreBlocks.CORRUPTION_TR_CRIMSON_ORE,

                OreBlocks.SANCTIFICATION_TIN_ORE, OreBlocks.CORRUPTION_TIN_ORE,
                OreBlocks.SANCTIFICATION_LEAD_ORE, OreBlocks.CORRUPTION_LEAD_ORE,
                OreBlocks.SANCTIFICATION_SILVER_ORE, OreBlocks.CORRUPTION_SILVER_ORE,
                OreBlocks.SANCTIFICATION_TUNGSTEN_ORE, OreBlocks.CORRUPTION_TUNGSTEN_ORE,
                OreBlocks.SANCTIFICATION_PLATINUM_ORE, OreBlocks.CORRUPTION_PLATINUM_ORE,
                OreBlocks.SANCTIFICATION_COAL_ORE, OreBlocks.CORRUPTION_COAL_ORE,
                OreBlocks.SANCTIFICATION_COPPER_ORE, OreBlocks.CORRUPTION_COPPER_ORE,
                OreBlocks.SANCTIFICATION_IRON_ORE, OreBlocks.CORRUPTION_IRON_ORE,
                OreBlocks.SANCTIFICATION_GOLD_ORE, OreBlocks.CORRUPTION_GOLD_ORE,
                OreBlocks.SANCTIFICATION_DIAMOND_ORE, OreBlocks.CORRUPTION_DIAMOND_ORE,
                OreBlocks.SANCTIFICATION_REDSTONE_ORE, OreBlocks.CORRUPTION_REDSTONE_ORE,

                // 邪恶环境方块
                NatureBlocks.HALLOW_GRASS_BLOCK, NatureBlocks.CORRUPT_GRASS_BLOCK,
                NatureBlocks.PEARL_STONE, NatureBlocks.EBONY_STONE,
                NatureBlocks.PEARL_COBBLESTONE, NatureBlocks.EBONY_COBBLESTONE,
                NatureBlocks.HARDENED_SAND_BLOCK, NatureBlocks.EBONY_HARDENED_SAND_BLOCK,
                NatureBlocks.RED_HARDENED_SAND_BLOCK, NatureBlocks.EBONY_HARDENED_SAND_BLOCK,
                NatureBlocks.MOIST_SAND_BLOCK, NatureBlocks.EBONY_MOIST_SAND_BLOCK,
                NatureBlocks.RED_MOIST_SAND_BLOCK, NatureBlocks.EBONY_MOIST_SAND_BLOCK,
                NatureBlocks.PEARL_HARDENED_SAND_BLOCK, NatureBlocks.EBONY_HARDENED_SAND_BLOCK,
                NatureBlocks.PEARL_MOIST_SAND_BLOCK, NatureBlocks.EBONY_MOIST_SAND_BLOCK,
                NatureBlocks.PEARL_SANDSTONE, NatureBlocks.EBONY_SANDSTONE,
                NatureBlocks.PINK_ICE, NatureBlocks.PURPLE_ICE,
                NatureBlocks.PINK_PACKED_ICE, NatureBlocks.PURPLE_PACKED_ICE,

                // 蘑菇
                NatureBlocks.LIFE_MUSHROOM, NatureBlocks.VILE_MUSHROOM,

                // 植物
                NatureBlocks.HALLOW_GRASS, NatureBlocks.CORRUPT_GRASS,
                NatureBlocks.CRIMSON_THORN, NatureBlocks.CORRUPTION_THORN,
                NatureBlocks.JUNGLE_THORN, NatureBlocks.CORRUPTION_THORN,
                NatureBlocks.PLANTERA_THORN, NatureBlocks.CORRUPTION_THORN
        ),
        GLOWING(
                getSupplier(MUD),NatureBlocks.MUSHROOM_GRASS_BLOCK
        ),
        JUNGLE(
                getSupplier(MUD),NatureBlocks.JUNGLE_GRASS_BLOCK
        ),
        PURE(
                NatureBlocks.ASH_BLOCK, NatureBlocks.ASH_GRASS_BLOCK,
                NatureBlocks.VICIOUS_MUSHROOM, NatureBlocks.LIFE_MUSHROOM,
                NatureBlocks.VILE_MUSHROOM, NatureBlocks.LIFE_MUSHROOM,
                NatureBlocks.CORRUPT_GRASS, getSupplier(SHORT_GRASS),
                NatureBlocks.TR_CRIMSON_GRASS, getSupplier(SHORT_GRASS),
                NatureBlocks.HALLOW_GRASS, getSupplier(SHORT_GRASS),
                NatureBlocks.TR_CRIMSON_STONE, getSupplier(STONE),
                NatureBlocks.TR_CRIMSON_COBBLESTONE, getSupplier(COBBLESTONE),
                NatureBlocks.TR_CRIMSON_SAND, getSupplier(SAND),
                NatureBlocks.TR_CRIMSON_SANDSTONE, getSupplier(SANDSTONE),
                NatureBlocks.TR_CRIMSON_HARDENED_SAND_BLOCK, NatureBlocks.HARDENED_SAND_BLOCK,
                NatureBlocks.TR_CRIMSON_MOIST_SAND_BLOCK, NatureBlocks.MOIST_SAND_BLOCK,
                NatureBlocks.TR_CRIMSON_GRASS_BLOCK, getSupplier(GRASS_BLOCK),
                NatureBlocks.EBONY_STONE, getSupplier(STONE),
                NatureBlocks.EBONY_COBBLESTONE, getSupplier(COBBLESTONE),
                NatureBlocks.EBONY_HARDENED_SAND_BLOCK, NatureBlocks.HARDENED_SAND_BLOCK,
                NatureBlocks.EBONY_SANDSTONE, getSupplier(SANDSTONE),
                NatureBlocks.EBONY_MOIST_SAND_BLOCK, NatureBlocks.MOIST_SAND_BLOCK,
                NatureBlocks.EBONY_SAND, getSupplier(SAND),
                NatureBlocks.CORRUPT_GRASS_BLOCK, getSupplier(GRASS_BLOCK),
                NatureBlocks.HALLOW_GRASS_BLOCK, getSupplier(GRASS_BLOCK),
                NatureBlocks.PEARL_STONE, getSupplier(STONE),
                NatureBlocks.PEARL_COBBLESTONE, getSupplier(COBBLESTONE),
                NatureBlocks.PEARL_HARDENED_SAND_BLOCK, NatureBlocks.HARDENED_SAND_BLOCK,
                NatureBlocks.PEARL_SANDSTONE, getSupplier(SANDSTONE),
                NatureBlocks.PEARL_MOIST_SAND_BLOCK, NatureBlocks.MOIST_SAND_BLOCK,
                NatureBlocks.PEARL_SAND, getSupplier(SAND),
                NatureBlocks.CRIMSON_THORN, getSupplier(Blocks.AIR),
                NatureBlocks.CORRUPTION_THORN, getSupplier(Blocks.AIR)
        );
/*
        SOLUTIONS_GREEN (
                NatureBlocks.GLOWING_MUSHROOM,  getSupplier(SHORT_GRASS),
                NatureBlocks.MUSHROOM_GRASS_BLOCK, NatureBlocks.JUNGLE_GRASS_BLOCK,
                NatureBlocks.GLOWING_MUSHROOM_VINE, NatureBlocks.JUNGLE_DROOPING_VINE
        ),
        SOLUTIONS_DARK_BULE (
                NatureBlocks.JUNGLE_SPORE, getSupplier(Blocks.AIR),
                NatureBlocks.JUNGLE_DROOPING_VINE, NatureBlocks.GLOWING_MUSHROOM_VINE,
                NatureBlocks.JUNGLE_THORN, getSupplier(Blocks.AIR),
                getSupplier(SHORT_GRASS), NatureBlocks.GLOWING_MUSHROOM,
                NatureBlocks.JUNGLE_GRASS_BLOCK, NatureBlocks.GLOWING_MUSHROOM
        );
*/
        private static final IntFunction<Type> BY_ID = ByIdMap.continuous(Type::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        private transient @Nullable Map<Supplier<? extends Block>, Supplier<? extends Block>> supplierMap;
        private Map<Block, Block> blockMap;

        @SafeVarargs
        Type(Supplier<? extends Block>... suppliers) {
            if (suppliers.length % 2 != 0) throw new RuntimeException("Not enough suppliers!");
            Hashtable<Supplier<? extends Block>, Supplier<? extends Block>> map = new Hashtable<>();
            int h = suppliers.length / 2;
            for (int i = 0; i < h; i++) {
                int j = i + i;
                map.put(suppliers[j], suppliers[j + 1]);
            }
            this.supplierMap = map;
        }

        public Map<Block, Block> getBlockMap() {
            return blockMap;
        }

        @SuppressWarnings("unchecked")
        public <T extends Comparable<T>, V extends T> boolean spread(Level level, BlockPos pos) {
            BlockState sourceState = level.getBlockState(pos);
            Block target = blockMap.get(sourceState.getBlock());
            if (target == null) return false;
            BlockState targetState = target.defaultBlockState();
            for (Map.Entry<Property<?>, Comparable<?>> entry1 : sourceState.getValues().entrySet()) {
                if (targetState.hasProperty(entry1.getKey())) {
                    targetState = targetState.setValue((Property<T>) entry1.getKey(), (V) entry1.getValue());
                }
            }
            return level.setBlockAndUpdate(pos, targetState);
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static Type byId(int pId) {
            return BY_ID.apply(pId);
        }

        private static Supplier<Block> getSupplier(Block block) {
            return () -> block;
        }

        public static void buildMap() {
            for (Type type : values()) {
                ImmutableMap.Builder<Block, Block> map = ImmutableMap.builder();
                type.supplierMap.forEach((s1, s2) -> map.put(s1.get(), s2.get()));
                type.blockMap = map.build();
                type.supplierMap = null;
            }
        }
    }
}
