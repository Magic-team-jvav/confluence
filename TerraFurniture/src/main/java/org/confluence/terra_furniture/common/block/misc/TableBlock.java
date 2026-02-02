package org.confluence.terra_furniture.common.block.misc;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.confluence.terra_furniture.client.generators.TableBDG;
import org.confluence.terra_furniture.common.block.func.BlockSetGetter;
import org.confluence.terra_furniture.common.block.func.TFBlockSetType;
import org.confluence.terra_furniture.common.datagen.empowered.AutoGenBlockData;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.init.TFTags;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;

public class TableBlock extends CrossCollisionBlock implements AutoGenBlockData<TableBlock>, BlockSetGetter<TableBlock> {
    private static final VoxelShape TOP_SHAPE = Shapes.box(0, 0.8125, 0, 1, 1, 1);
    private static final VoxelShape LEG_NE_SHAPE = Shapes.box(0.75, 0, 0.125, 0.875, 0.8125, 0.25);
    private static final VoxelShape LEG_ES_SHAPE = Shapes.box(0.75, 0, 0.75, 0.875, 0.8125, 0.875);
    private static final VoxelShape LEG_SW_SHAPE = Shapes.box(0.125, 0, 0.75, 0.25, 0.8125, 0.875);
    private static final VoxelShape LEG_WN_SHAPE = Shapes.box(0.125, 0, 0.125, 0.25, 0.8125, 0.25);

    private final TFBlockSetType type;
    public final MapCodec<TableBlock> codec = simpleCodec(properties1 -> new TableBlock(getType(), properties1));

    public MapCodec<TableBlock> codec() {
        return codec;
    }

    public TableBlock(TFBlockSetType type, Properties properties) {
        super(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, properties);
        this.type = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    /**
     * Since BlockStateBase.Cache exist, we needn't prepare more static values to return.
     */
    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape buildShape = TOP_SHAPE;
        if (!state.getValue(NORTH) && !state.getValue(EAST)) buildShape = Shapes.or(buildShape, LEG_NE_SHAPE);
        if (!state.getValue(EAST) && !state.getValue(SOUTH)) buildShape = Shapes.or(buildShape, LEG_ES_SHAPE);
        if (!state.getValue(SOUTH) && !state.getValue(WEST)) buildShape = Shapes.or(buildShape, LEG_SW_SHAPE);
        if (!state.getValue(WEST) && !state.getValue(NORTH)) buildShape = Shapes.or(buildShape, LEG_WN_SHAPE);
        return buildShape;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.getOcclusionShape(pState, pLevel, pPos);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getOcclusionShape(state, level, pos);
    }

    public boolean connectsTo(BlockState state, boolean isSideSolid) {
        return state.getBlock() instanceof TableBlock;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockgetter = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockState blockstate = blockgetter.getBlockState(blockpos1);
        BlockState blockstate1 = blockgetter.getBlockState(blockpos2);
        BlockState blockstate2 = blockgetter.getBlockState(blockpos3);
        BlockState blockstate3 = blockgetter.getBlockState(blockpos4);
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(NORTH, this.connectsTo(blockstate, blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH))).setValue(EAST, this.connectsTo(blockstate1, blockstate1.isFaceSturdy(blockgetter, blockpos2, Direction.WEST))).setValue(SOUTH, this.connectsTo(blockstate2, blockstate2.isFaceSturdy(blockgetter, blockpos3, Direction.NORTH))).setValue(WEST, this.connectsTo(blockstate3, blockstate3.isFaceSturdy(blockgetter, blockpos4, Direction.EAST))).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(PROPERTY_BY_DIRECTION.get(facing), this.connectsTo(facingState, facingState.isFaceSturdy(level, facingPos, facing.getOpposite()))) : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

    @Override
    public @Nullable BlockDataGenerator<? super TableBlock> getGenerator() {
        return new TableBDG() {
            @Override
            public String getTemplateType(TableBlock block) {
                return "table";
            }

            @Override
            public void addBlockTags(TableBlock block, BlockTagsProvider provider, HashSet<TagKey<Block>> keys) {
                super.addBlockTags(block, provider, keys);
                keys.add(TFTags.HOUSE_TABLE);
            }
        };
    }

    @Override
    public boolean hasParticle(TableBlock block) {
        return false;
    }

    @Override
    public TFBlockSetType getType() {
        return type;
    }
}
