package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.ThornBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

public class SpreadingGrassBlock extends SpreadingBlock {
    public SpreadingGrassBlock(Type type, Properties properties) {
        super(type, properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;
        BlockPos above = pos.above();
        if (isFullBlock(level, above)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
        } else {
            Type spreadType = getSpreadType();
            ThornBlock thorn = null;
            /// 这里刻意不使用枚举 switch。编译器会为 switch 生成额外的 $1
            /// 映射类，开发环境热替换主类时该合成类可能没有同步进入活动模块，
            /// 随机刻随后便会因缺类崩溃。直接比较也更准确地表达只有两种草地
            /// 会尝试生成荆棘。
            if (spreadType == Type.CRIMSON) {
                thorn = NatureBlocks.CRIMSON_THORN.get();
            } else if (spreadType == Type.CORRUPT) {
                thorn = NatureBlocks.CORRUPTION_THORN.get();
            }
            if (thorn != null && random.nextInt(50) == 0
                    && level.getBlockState(above).isAir()
                    && level.getBlockState(above.east()).isAir()
                    && level.getBlockState(above.west()).isAir()
                    && level.getBlockState(above.south()).isAir()
                    && level.getBlockState(above.north()).isAir()
            ) {
                level.setBlockAndUpdate(above, thorn.getStateForPlacement(level, above));
            }
            super.randomTick(state, level, pos, random);
        }
    }
}
