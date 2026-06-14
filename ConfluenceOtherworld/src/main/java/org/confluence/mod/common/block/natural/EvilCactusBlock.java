package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModTags;

public class EvilCactusBlock extends CactusBlock {
    public EvilCactusBlock(Properties properties) {
        super(properties);
    }

    // 牢镜的码，出事找牢镜
    // 目测仅将原版仙人掌方块实例修改为标签了
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockstate = level.getBlockState(pos.relative(direction));
            if (blockstate.isSolid() || level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
                return false;
            }
        }

        BlockState blockstate1 = level.getBlockState(pos.below());
        PortTriState soilDecision = blockstate1.canSustainPlant(level, pos.below(), Direction.UP, state);
        if (!soilDecision.isDefault()) {
            return soilDecision.isTrue();
        } else {
            return (blockstate1.is(ModTags.Blocks.CACTUS) || blockstate1.is(BlockTags.SAND)) && !level.getBlockState(pos.above()).liquid();
        }
    }
}
