package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.block.StateProperties;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;
import static org.confluence.lib.common.block.StateProperties.HorizontalTwoPart.getConnectedDirection;

public class TitaniumForgeBlock extends HardmodeForgeBlock {
    public TitaniumForgeBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).lightLevel(state -> state.getValue(LIT) ? 15 : 7).noOcclusion());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        Direction facing = state.getValue(FACING);
        StateProperties.HorizontalTwoPart part = state.getValue(PART);
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.8875;
        double z = pos.getZ() + 0.5;
        boolean axisX = facing.getAxis() == Direction.Axis.X;
        boolean positive = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        double off = positive ? 0.3125 : -0.3125;
        double v1 = random.nextDouble() * off;
        double v2 = random.nextDouble() * off;
        double rx;
        double rz;
        if (axisX) {
            rx = x - (facing.getStepX() * 0.25 + v1);
            if (part.isBase()) {
                rz = z + v2 + 0.125;
            } else {
                rz = z + facing.getCounterClockWise().getStepZ() * 0.625 - v2;
            }
        } else {
            rz = z - (facing.getStepZ() * 0.25 + v1);
            if (part.isBase()) {
                rx = x - v2 - 0.125;
            } else {
                rx = x + facing.getCounterClockWise().getStepX() * 0.625 + v2;
            }
        }
        boolean soul;
        if (part.isBase()) {
            soul = state.getValue(LIT);
        } else {
            BlockState blockState = level.getBlockState(pos.relative(getConnectedDirection(state)));
            soul = blockState.is(this) && blockState.getValue(LIT);
        }
        level.addParticle(soul ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, rx, y, rz, 0, 0.02, 0);
    }
}
