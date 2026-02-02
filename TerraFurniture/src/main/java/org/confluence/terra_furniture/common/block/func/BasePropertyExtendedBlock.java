package org.confluence.terra_furniture.common.block.func;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.confluence.terra_furniture.common.datagen.empowered.AutoGenBlockData;

import java.util.function.Consumer;

/**
 * 基础的多种材料属性的变体方块，预留了很多有关生成的方法。
 */
public abstract class BasePropertyExtendedBlock<T extends BasePropertyExtendedBlock<T>> extends Block implements SimpleWaterloggedBlock, BlockSetGetter<T>, AutoGenBlockData<T> {
    public final MapCodec<BasePropertyExtendedBlock<T>> codec = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BlockState.CODEC.fieldOf("base_state").forGetter(block -> block.baseState),
                    propertiesCodec()
            ).apply(instance, this::createNewInstance)
    );
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public final Block base;
    private final BlockState baseState;
    private final TFBlockSetType type;

    @SuppressWarnings("deprecation")
    public static Properties calcProperties(Block block, Consumer<Properties> extraPropApplier) {
        Properties newProp = Properties.ofLegacyCopy(block);
        extraPropApplier.accept(newProp);
        return newProp;
    }

    public BasePropertyExtendedBlock(TFBlockSetType type, BlockState state, Consumer<Properties> extraProperties) {
        super(calcProperties(state.getBlock(), extraProperties));
        this.type = type;
        this.base = state.getBlock();
        this.baseState = state;
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    /**
     * 仅供给CODEC使用
     */
    public BasePropertyExtendedBlock(TFBlockSetType type, BlockState state, Properties properties) {
        super(properties);
        this.type = type;
        this.base = state.getBlock();
        this.baseState = state;
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacement(context);
        return
            blockState == null ?
                defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER)
                :
                blockState.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getExplosionResistance() {
        return this.base.getExplosionResistance();
    }


    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return codec;
    }

    protected abstract BasePropertyExtendedBlock<T> createNewInstance(BlockState baseState, Properties properties);

    public TFBlockSetType getType() {
        return type;
    }
}
