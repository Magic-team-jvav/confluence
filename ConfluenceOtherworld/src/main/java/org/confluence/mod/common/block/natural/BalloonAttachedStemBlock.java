package org.confluence.mod.common.block.natural;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.SupAttachedStemBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Map;
import java.util.function.Supplier;

public class BalloonAttachedStemBlock extends SupAttachedStemBlock {
    /// 1.20 的 StemGrownBlock 接口强制返回 AttachedStemBlock，因此这里必须
    /// 沿用原版附着茎的水平朝向属性。此前另建同名六向属性却没有加入状态定义，
    /// 导致每次查询形状都会抛出属性不存在异常。
    public static final DirectionProperty FACING = AttachedStemBlock.FACING;

    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.SOUTH, Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0))
            .put(Direction.WEST, Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0))
            .put(Direction.NORTH, Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0))
            .put(Direction.EAST, Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0))
            .put(Direction.UP, Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0))
            .build());

    public BalloonAttachedStemBlock(Supplier<? extends StemGrownBlock> fruitSup, Supplier<Item> seedSupplier, BlockBehaviour.Properties properties) {
        super(fruitSup, seedSupplier, properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABBS.get(state.getValue(FACING));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction attachedDirection = state.getValue(FACING);
        if (facing == attachedDirection) {
            if (!facingState.is(fruitSup.get())) {
                BlockState stemState = fruitSup.get().getStem().defaultBlockState();
                if (stemState.hasProperty(StemBlock.AGE)) {
                    return stemState.setValue(StemBlock.AGE, 7);
                }
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof FarmBlock ||
                state.is(NatureBlocks.CLOUD_BLOCK.get()) ||
                state.is(NatureBlocks.RAIN_CLOUD_BLOCK.get());
    }
}
