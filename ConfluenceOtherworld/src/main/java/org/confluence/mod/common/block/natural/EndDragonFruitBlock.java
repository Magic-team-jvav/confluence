package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.EnumMap;
import java.util.Map;
/**
 * 现在龙果的生长速度等属性暂时与可可果相同，若需修改，overrider andomTick方法
 */
public class EndDragonFruitBlock extends CocoaBlock {
    @SuppressWarnings("unchecked")
    public static final MapCodec<CocoaBlock> CODEC = (MapCodec<CocoaBlock>) (MapCodec<?>) simpleCodec(EndDragonFruitBlock::new);

    private static final Map<Direction, VoxelShape[]> SHAPES = new EnumMap<>(Direction.class);
    /**
    * 定义长方体的对角点，使用map对应关系，从上到下不断变大。
    */
    static {
        SHAPES.put(Direction.NORTH, new VoxelShape[]{
                Block.box(6.0, 6.0, 5.0, 10.0, 12.0, 10.0),
                Block.box(5.0, 4.0, 4.0, 11.0, 13.0, 10.0),
                Block.box(4.0, 2.0, 3.0, 12.0, 14.0, 10.0)
        });
        SHAPES.put(Direction.SOUTH, new VoxelShape[]{
                Block.box(6.0, 6.0, 6.0, 10.0, 12.0, 11.0),
                Block.box(5.0, 4.0, 6.0, 11.0, 13.0, 12.0),
                Block.box(4.0, 2.0, 6.0, 12.0, 14.0, 13.0)
        });
        SHAPES.put(Direction.WEST, new VoxelShape[]{
                Block.box(5.0, 6.0, 6.0, 10.0, 12.0, 10.0),
                Block.box(4.0, 4.0, 5.0, 10.0, 13.0, 11.0),
                Block.box(3.0, 2.0, 4.0, 10.0, 14.0, 12.0)
        });
        SHAPES.put(Direction.EAST, new VoxelShape[]{
                Block.box(6.0, 6.0, 6.0, 11.0, 12.0, 10.0),
                Block.box(6.0, 4.0, 5.0, 12.0, 13.0, 11.0),
                Block.box(6.0, 2.0, 4.0, 13.0, 14.0, 12.0)
        });
    }

    public EndDragonFruitBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<CocoaBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Block block = level.getBlockState(pos.relative(state.getValue(FACING))).getBlock();
        return block == NatureBlocks.VOID_LOG_BLOCKS.LOG.get()
                || block == NatureBlocks.VOID_LOG_BLOCKS.WOOD.get();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing == state.getValue(FACING) && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(AGE)];
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(AGE)];
    }
}
