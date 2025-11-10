package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DecomposeTheSourceExtractBlock extends Block implements EntityBlock {
    public static final Set<BlockPos> ALL_BLOCKS = ConcurrentHashMap.newKeySet();
    public static final BooleanProperty VISIBLE = StateProperties.VISIBLE;
    protected static final VoxelShape SHAPE = Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);

    public DecomposeTheSourceExtractBlock() {
        super(BlockBehaviour.Properties.of().randomTicks());
        registerDefaultState(stateDefinition.any().setValue(VISIBLE, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }

    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, state.setValue(VISIBLE, false));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, false);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) level.scheduleTick(pos, this, 1);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState aboveBelow = level.getBlockState(pos.above());
        return aboveBelow.is(ModTags.Blocks.DECOMPOSE_THE_SOURCE_EXTRACT_BASE_BLOCK);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return !state.getValue(VISIBLE);
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(VISIBLE) ? SHAPE : Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(VISIBLE) ? SHAPE : Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VISIBLE);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock())) {
            ALL_BLOCKS.remove(pos);
        }
    }

    public void checkVisibilityAndSummonEntity(Level level, LivingEntity living) {
        BlockPos blockPos = living.blockPosition();
        Iterator<BlockPos> iterator = ALL_BLOCKS.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (pos.distSqr(blockPos) > 400) continue;
            BlockState state = level.getBlockState(pos);
            if (state.is(this)) {
                level.setBlockAndUpdate(pos.immutable(), state.setValue(VISIBLE, true));
                AbstractMonster entity = TEMonsterEntities.EATER_OF_SOULS.get().create(level);
                if (entity != null) {
                    entity.setPos(pos.getX() - 0.5, pos.getY() - 1.5, pos.getZ() - 0.5);
                    level.addFreshEntity(entity);
                }
            } else {
                iterator.remove();
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    public static class BEntity extends BlockEntity {
        public BEntity(BlockPos pos, BlockState blockState) {
            super(NatureBlocks.DECOMPOSE_THE_SOURCE_EXTRACT_ENTITY.get(), pos, blockState);
        }

        @Override
        public void onLoad() {
            super.onLoad();
            ALL_BLOCKS.add(getBlockPos());
        }

        @Override
        public void onChunkUnloaded() {
            super.onChunkUnloaded();
            ALL_BLOCKS.remove(getBlockPos());
        }
    }
}
