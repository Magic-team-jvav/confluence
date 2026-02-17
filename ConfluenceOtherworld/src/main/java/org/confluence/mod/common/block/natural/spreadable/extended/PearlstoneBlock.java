package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.block.natural.spreadable.SpreadingBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.OverworldUtils;

public class PearlstoneBlock extends SpreadingBlock {
    private static final Direction[] AVAILABLE = new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};

    public PearlstoneBlock(Properties properties) {
        super(Type.HALLOW, properties);
    }

    @Override
    public void spread(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        super.spread(blockState, level, blockPos, randomSource);
        generateCrystal(level, blockPos, randomSource);
    }

    public static void generateCrystal(ServerLevel level, BlockPos pos, RandomSource random) {
        if (pos.getY() <= OverworldUtils.getUndergroundY() && random.nextInt(100) == 0) {
            Direction direction = Util.getRandom(AVAILABLE, random);
            BlockPos relative = pos.relative(direction);
            AmethystClusterBlock crystalShards = NatureBlocks.CRYSTAL_SHARDS.get();
            if (level.getBlockState(relative).isAir() && level.getBlockStates(new AABB(relative).inflate(3.5)).filter(state -> {
                if (state.liquid() || state.isAir()) return false;
                return state.is(crystalShards);
            }).count() < 6) {
                AmethystClusterBlock block = random.nextInt(50) == 0 ? NatureBlocks.GELATIN_CRYSTAL.get() : crystalShards;
                level.setBlockAndUpdate(relative, block.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction));
            }
        }
    }
}
