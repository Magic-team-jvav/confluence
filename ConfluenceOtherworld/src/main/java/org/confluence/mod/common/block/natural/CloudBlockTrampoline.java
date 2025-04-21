package org.confluence.mod.common.block.natural;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class CloudBlockTrampoline extends CloudBlock {
    public static final IntegerProperty HIGH = IntegerProperty.create("high", 0, 10);
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.EAST, EAST);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.WEST, WEST);
    }));

    public CloudBlockTrampoline(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState()
                .setValue(HIGH, 0)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockgetter = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            BlockPos adjacentPos = blockpos.relative(entry.getKey());
            BlockState adjacentState = blockgetter.getBlockState(adjacentPos);
            state = state.setValue(entry.getValue(), connectsTo(adjacentState, entry.getKey().getOpposite()));
        }
        return state;
    }

    public boolean connectsTo(BlockState state, Direction direction) {
        return state.is(this);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL) {
            for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
                BlockPos adjacentPos = currentPos.relative(entry.getKey());
                BlockState adjacentState = level.getBlockState(adjacentPos);
                boolean connected = this.connectsTo(adjacentState, entry.getKey().getOpposite());
                state = state.setValue(entry.getValue(), connected);
            }
            return state;
        } else {
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            int currentHigh = state.getValue(HIGH);
            boolean isSteppingCarefully = livingEntity.isSteppingCarefully();
            if (isSteppingCarefully) {
                level.setBlockAndUpdate(pos, state.setValue(HIGH, 0));
                livingEntity.setDeltaMovement(0, 0, 0);
            } else {
                int newHigh = Math.min(currentHigh + 1, 10);
                level.setBlockAndUpdate(pos, state.setValue(HIGH, newHigh));
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, currentHigh, 0));
            }
            level.scheduleTick(pos, this, 20);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        AABB detectionArea = new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 513, pos.getZ() + 1);
        boolean hasPlayer = level.getEntitiesOfClass(LivingEntity.class, detectionArea)
                .stream()
                .anyMatch(LivingEntity::isAlive);
        if (!hasPlayer) {
            level.setBlockAndUpdate(pos, state.setValue(HIGH, 0));
        }
        level.scheduleTick(pos, this, 20);
    }

//    @Override
//    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
//        return SHAPE;
//    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HIGH, NORTH, EAST, SOUTH, WEST);
    }
}
