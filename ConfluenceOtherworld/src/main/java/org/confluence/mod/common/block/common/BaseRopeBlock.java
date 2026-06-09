package org.confluence.mod.common.block.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class BaseRopeBlock extends PipeBlock implements SimpleWaterloggedBlock {
    public enum RopeConnection implements StringRepresentable {
        NONE("none"),
        ROPE("rope"),
        FENCE("fence"),
        WALL("wall");

        private final String name;

        RopeConnection(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private static final TagKey<Block> FENCES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "fences"));
    private static final TagKey<Block> WALLS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "walls"));

    public static final MapCodec<BaseRopeBlock> CODEC = simpleCodec(BaseRopeBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final EnumProperty<RopeConnection> NORTH = EnumProperty.create("north", RopeConnection.class);
    public static final EnumProperty<RopeConnection> EAST = EnumProperty.create("east", RopeConnection.class);
    public static final EnumProperty<RopeConnection> SOUTH = EnumProperty.create("south", RopeConnection.class);
    public static final EnumProperty<RopeConnection> WEST = EnumProperty.create("west", RopeConnection.class);

    public static final VoxelShape NORTH_SHAPE = Block.box(7.5, 3, 0, 8.5, 10, 8.5);
    public static final VoxelShape SOUTH_SHAPE = Block.box(7.5, 3, 7.5, 8.5, 10, 16);
    public static final VoxelShape WEST_SHAPE = Block.box(0, 3, 7.5, 8.5, 10, 8.5);
    public static final VoxelShape EAST_SHAPE = Block.box(7.5, 3, 7.5, 16, 10, 8.5);

    private VoxelShape SHAPE = null;
    private BlockState cachedState = null;

    @SuppressWarnings("unchecked")
    private static final EnumProperty<RopeConnection>[] DIRECTION_TO_PROPERTY = new EnumProperty[]{
            null,
            null,
            NORTH,
            SOUTH,
            WEST,
            EAST,
    };

    private static final VoxelShape[] DIRECTION_TO_SHAPE = new VoxelShape[]{
            null,
            null,
            NORTH_SHAPE,
            SOUTH_SHAPE,
            WEST_SHAPE,
            EAST_SHAPE,
    };

    public BaseRopeBlock(Properties properties) {
        super(0.25F, properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, RopeConnection.NONE)
                .setValue(EAST, RopeConnection.NONE)
                .setValue(SOUTH, RopeConnection.NONE)
                .setValue(WEST, RopeConnection.NONE)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    protected MapCodec<BaseRopeBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (facing.getAxis() == Direction.Axis.Y) return state.setValue(PROPERTY_BY_DIRECTION.get(facing), shouldConnect(level, facingState, facingPos, facing));
        return state.setValue(DIRECTION_TO_PROPERTY[facing.ordinal()], whatConnect(level, facingState, facingPos, facing));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState belowState = level.getBlockState(pos);
        if (belowState.getFluidState().is(Fluids.WATER)) {
            state = state.setValue(WATERLOGGED, true);
        }
        for (Direction direction : LibUtils.DIRECTIONS) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (shouldConnect(level, neighborState, neighborPos, direction)) {
                if (direction.getAxis() == Direction.Axis.Y) state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
                else state = state.setValue(DIRECTION_TO_PROPERTY[direction.ordinal()], whatConnect(level, neighborState, neighborPos, direction));
            }
        }
        return state;
    }

    private static boolean shouldConnect(LevelAccessor level, BlockState facingState, BlockPos facingPos, Direction facing) {
        return facingState.is(ModTags.Blocks.ROPE) || (!facingState.isAir() && facingState.isFaceSturdy(level, facingPos, facing.getOpposite())) || ((facing == Direction.DOWN) && level.getBlockState(facingPos).is(FunctionalBlocks.STAR_IN_A_BOTTLE));
    }

    private static RopeConnection whatConnect(LevelAccessor level, BlockState facingState, BlockPos facingPos, Direction facing) {
        RopeConnection r = RopeConnection.NONE;
        if (facingState.is(ModTags.Blocks.ROPE) || (!facingState.isAir() && facingState.isFaceSturdy(level, facingPos, facing.getOpposite()))) r = RopeConnection.ROPE;
        if (facingState.is(FENCES)) r = RopeConnection.FENCE;
        if (facingState.is(WALLS)) r = RopeConnection.WALL;
        return r;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ecc) {
            Entity entity = ecc.getEntity();
            if (entity instanceof Player) {
                BlockPos blockPos = entity.blockPosition().atY((int) entity.getEyeY());
                if (level.getBlockState(blockPos).is(ModTags.Blocks.ROPE) || level.getBlockState(blockPos.below()).is(ModTags.Blocks.ROPE)) {
                    return Shapes.empty(); // 玩家在绳子里不阻挡
                }
                if (this.cachedState != state) {
                    updateAABBShape(state);
                    this.cachedState = state;
                }
                else if (this.SHAPE == null) updateAABBShape(state);
                return this.SHAPE;
            }
        }
        return Shapes.empty();
    }

    private void updateAABBShape(BlockState blockState){
        int up = (blockState.getValue(UP)) ? 16 : 10;
        int down = (blockState.getValue(DOWN)) ? 0 : 6;
        VoxelShape r = Block.box(6, down, 6, 10, up, 10);
        for (int i = 2; i < 6; i++) {
            if (blockState.getValue(DIRECTION_TO_PROPERTY[i]) != RopeConnection.NONE) {
                r = Shapes.or(r, DIRECTION_TO_SHAPE[i]);
            }
        }
        this.SHAPE = r;
    }

    public static class BItem extends BlockItem {
        public BItem(Block block) {
            super(block, new Properties());
        }

        @Override
        public InteractionResult place(BlockPlaceContext context) {
            Player player = context.getPlayer();
            if (player == null || player.isCrouching()) return super.place(context);
            Level level = context.getLevel();
            BlockHitResult hitResult = context.getHitResult();
            BlockPos.MutableBlockPos relative = hitResult.getBlockPos().mutable();
            while (level.getBlockState(relative).is(ModTags.Blocks.ROPE)) {
                relative.move(0, -1, 0);
            }
            BlockHitResult hitResult1 = new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), relative, hitResult.isInside());
            InteractionResult place = super.place(new BlockPlaceContext(level, player, context.getHand(), context.getItemInHand(), hitResult1));
            if (place == InteractionResult.FAIL) {
                return super.place(context);
            }
            return place;
        }
    }
}
