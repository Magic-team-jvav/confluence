package org.confluence.mod.common.block.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;

/**
 * <a href="https://terraria.wiki.gg/zh/wiki/%E9%9B%95%E5%83%8F">雕像Wiki页面</a>
 */
public class StatueBlock extends HorizontalDirectionalWithVerticalTwoPartBlock {
    public static final MapCodec<StatueBlock> CODEC = simpleCodec(StatueBlock::new);
    private static final VoxelShape LOWER_SHAPE_Z = Shapes.or(
            box(0, 0, 0, 16, 3, 16),
            box(1, 3, 1, 15, 7, 15),
            box(0, 7, 0, 16, 10, 16),
            box(1, 10, 6, 15, 16, 10)
    );
    private static final VoxelShape LOWER_SHAPE_X = Shapes.or(
            box(0, 0, 0, 16, 3, 16),
            box(1, 3, 1, 15, 7, 15),
            box(0, 7, 0, 16, 10, 16),
            box(6, 10, 1, 10, 16, 15)
    );
    private static final VoxelShape UPPER_SHAPE_Z = box(1, 0, 6, 15, 14, 10);
    private static final VoxelShape UPPER_SHAPE_X = box(6, 0, 1, 10, 14, 15);

    public StatueBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<StatueBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean base = state.getValue(StateProperties.VERTICAL_TWO_PART).isBase();
        return switch (state.getValue(FACING)) {
            case NORTH, SOUTH -> base ? LOWER_SHAPE_Z : UPPER_SHAPE_Z;
            default -> base ? LOWER_SHAPE_X : UPPER_SHAPE_X;
        };
    }
}
