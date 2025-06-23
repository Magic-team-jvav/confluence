package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class JungleGrassBlock extends SpreadingGrassBlock implements BonemealableBlock {
    public JungleGrassBlock(ISpreadable.Type type, Properties properties) {
        super(type, properties.mapColor(DyeColor.byId(0x59C93C)));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        if (!serverLevel.isAreaLoaded(blockPos, 3)) return;
        if (isFullBlock(serverLevel, blockPos.above())) {
            serverLevel.setBlockAndUpdate(blockPos, Blocks.MUD.defaultBlockState());
        } else {
            super.randomTick(blockState, serverLevel, blockPos, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.above();
        for (int i = 0; i < 128; i++) {
            BlockPos blockpos1 = blockpos;
            for (int j = 0; j < i / 16; j++) {
                blockpos1 = blockpos1.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (level.getBlockState(blockpos1).isCollisionShapeFullBlock(level, blockpos1)) {
                    break;
                }
            }
            BlockState blockstate1 = level.getBlockState(blockpos1);
            if (blockstate1.isAir()) {
                BlockState plantState = selectJunglePlant(random);
                if (plantState.canSurvive(level, blockpos1)) {
                    level.setBlock(blockpos1, plantState, 3);
                }
            }
        }
    }

    private BlockState selectJunglePlant(RandomSource random) {
        int chance = random.nextInt(100);
        if (chance < 40) {
            return Blocks.FERN.defaultBlockState();
        } else if (chance < 70) {
            return Blocks.SHORT_GRASS.defaultBlockState();
        } else if (chance < 85) {
            return Blocks.RED_MUSHROOM.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }
}
