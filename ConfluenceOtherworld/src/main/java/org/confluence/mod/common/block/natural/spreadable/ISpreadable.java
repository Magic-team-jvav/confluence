package org.confluence.mod.common.block.natural.spreadable;

import com.google.common.base.Suppliers;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.spreadable.conversion_table.*;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/// [生物群系蔓延](https://terraria.wiki.gg/zh/wiki/%E7%94%9F%E7%89%A9%E7%BE%A4%E7%B3%BB%E8%94%93%E5%BB%B6)
public interface ISpreadable {
    // That was a joke haha!
    BooleanProperty STILL_ALIVE = BooleanProperty.create("still_alive");

    Type getSpreadType();

    default void spread(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.getValue(STILL_ALIVE)) return;
        int chance = level.getGameRules().getInt(Confluence.SPREADABLE_CHANCE);
        int phase = KillBoard.INSTANCE.getGamePhase().getOrder();
        if (phase >= GamePhase.PLANTERA.getOrder()) {
            chance /= 2;
        }
        if (chance != 100 && (chance == 0 || randomSource.nextInt(100) >= chance)) return;

        boolean hardmode = KillBoard.INSTANCE.getGamePhase().isHardmode();
        for (int i = 0; i < 4; ++i) {
            BlockPos targetPos = blockPos.offset(randomSource.nextInt(3) - 1, randomSource.nextInt(5) - 3, randomSource.nextInt(3) - 1);
            if (!level.isLoaded(targetPos)) continue;

            BlockState target = getSpreadType().getNullable(level.getBlockState(targetPos), hardmode);
            if (target == null) continue;

            if (target.is(ModTags.Blocks.SPREADABLE_GRASS_BLOCK)) {
                if (!isFullBlock(level, targetPos.above())) {
                    spreadOrDie(phase, blockState, level, blockPos, randomSource, target, targetPos);
                }
            } else {
                spreadOrDie(phase, blockState, level, blockPos, randomSource, target, targetPos);
            }
        }
    }

    default boolean isFullBlock(ServerLevel level, BlockPos pos) {
        return isFullBlock(level.getBlockState(pos), level, pos);
    }

    default boolean isFullBlock(BlockState state, ServerLevel level, BlockPos pos) {
        return state.isSolidRender(level, pos);
    }

    default void spreadOrDie(int phase, BlockState selfState, ServerLevel serverLevel, BlockPos selfPos, RandomSource randomSource, BlockState targetState, BlockPos targetPos) {
        spreadTree(serverLevel, targetPos);
        serverLevel.setBlockAndUpdate(targetPos, targetState);
        if (randomSource.nextInt(7) > phase) {
            serverLevel.setBlockAndUpdate(selfPos, selfState.setValue(STILL_ALIVE, false));
        }
    }

    default void spreadTree(ServerLevel level, BlockPos targetPos) {
        boolean hardmode = KillBoard.INSTANCE.getGamePhase().isHardmode();
        BlockState blockState = level.getBlockState(targetPos.above());
        if (blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES)) {
            Map<BlockPos, BlockState> map = searchFace(level, targetPos, new Hashtable<>(), 0);
            for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
                BlockState source = entry.getValue();
                if (source == AIR) continue;
                BlockState target = getSpreadType().getNullable(source, hardmode);
                if (target != null) {
                    level.setBlockAndUpdate(entry.getKey(), target);
                }
            }
        }
    }

    Supplier<Set<Block>> PALMS = Suppliers.memoize(() -> {
        LogBlockSet set = NatureBlocks.PALM_LOG_BLOCKS;
        return Sets.newHashSet(
                set.LOG.get(),
                set.WOOD.get(),
                set.STRIPPED_LOG.get(),
                set.STRIPPED_WOOD.get(),
                set.LEAVES.get()
        );
    });
    BlockState AIR = Blocks.AIR.defaultBlockState();

    private static Map<BlockPos, BlockState> searchFace(ServerLevel level, BlockPos targetPos, Map<BlockPos, BlockState> map, int depth) {
        if (depth == 128) return map;
        for (Direction direction : LibUtils.DIRECTIONS) {
            BlockPos relative = targetPos.relative(direction);
            if (map.containsKey(relative)) continue;
            BlockState blockState = level.getBlockState(relative);
            if (blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES)) {
                map.put(relative, blockState);
                if (PALMS.get().contains(blockState.getBlock())) {
                    searchBox(level, relative, map, depth + 1);
                } else {
                    searchFace(level, relative, map, depth + 1);
                }
            } else {
                map.put(relative, AIR);
            }
        }
        return map;
    }

    private static void searchBox(ServerLevel level, BlockPos targetPos, Map<BlockPos, BlockState> map, int depth) {
        if (depth == 128) return;
        for (BlockPos relative : BlockPos.betweenClosed(targetPos.offset(-1, -1, -1), targetPos.offset(1, 1, 1))) {
            relative = relative.immutable();
            if (map.containsKey(relative)) continue;
            BlockState blockState = level.getBlockState(relative);
            if (blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES)) {
                map.put(relative, blockState);
                if (PALMS.get().contains(blockState.getBlock())) {
                    searchBox(level, relative, map, depth + 1);
                } else {
                    searchFace(level, relative, map, depth + 1);
                }
            } else {
                map.put(relative, AIR);
            }
        }
    }

    enum Type implements StringRepresentable {
        HALLOW(new TheHallowConversionTable()),
        CRIMSON(new TheCrimsonConversionTable()),
        CORRUPT(new TheCorruptionConversionTable()),
        GLOWING(new GlowingMushroomConversionTable()),
        JUNGLE(new JungleConversionTable()),
        PURE(new PureConversionTable()),
        ASH(new AshConversionTable());

        private static final IntFunction<Type> BY_ID = ByIdMap.continuous(Type::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        private final ConversionTable conversionTable;

        Type(ConversionTable conversionTable) {
            this.conversionTable = conversionTable;
        }

        public BlockState getNotNull(BlockState source, boolean hardmode) {
            BlockState target = conversionTable.get(source, hardmode);
            return target == null ? source : target;
        }

        public @Nullable BlockState getNullable(BlockState source, boolean hardmode) {
            return conversionTable.get(source, hardmode);
        }

        public boolean spread(Level level, BlockPos pos, boolean hardmode) {
            BlockState target = getNullable(level.getBlockState(pos), hardmode);
            if (target != null) {
                return level.setBlockAndUpdate(pos, target);
            }
            return false;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static Type byId(int pId) {
            return BY_ID.apply(pId);
        }
    }
}
