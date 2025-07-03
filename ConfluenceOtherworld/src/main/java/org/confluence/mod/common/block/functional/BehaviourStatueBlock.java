package org.confluence.mod.common.block.functional;

import com.mojang.datafixers.util.Function3;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.common.StatueBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.Network;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BehaviourStatueBlock extends StatueBlock implements INetworkBlock, EntityBlock {
    private final Behaviour behaviour;

    public BehaviourStatueBlock(Behaviour behaviour, Properties properties) {
        super(properties);
        this.behaviour = behaviour;
        registerDefaultState(stateDefinition.any().setValue(StateProperties.DRIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(StateProperties.DRIVE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return behaviour.getStateForPlacement(pContext, super.getStateForPlacement(pContext));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = behaviour.getShape(pState, pLevel, pPos, pContext);
        return shape == null ? super.getShape(pState, pLevel, pPos, pContext) : shape;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            onNodeRemove(pState, pLevel, pPos, pNewState);
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        behaviour.onExecute(state, level, pos, color, networkEntity);
        execution(state, level, pos, color, true);
    }

    @Override
    public void onUnExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        behaviour.onUnExecute(state, level, pos, color, networkEntity);
        execution(state, level, pos, color, false);
    }

    private void execution(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, boolean hasSignal) {
        BlockPos relative = pPos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(pState));
        if (pLevel.getBlockEntity(relative) instanceof INetworkEntity entity) {
            Network network = entity.getOrCreateNetworkNode().getNetwork(pColor);
            if (network != null && hasSignal != network.hasSignal()) {
                network.setSignal(hasSignal);
                network.getNodes().stream()
                        .map(NetworkNode::getEntity)
                        .collect(Collectors.toSet())
                        .forEach(entity1 -> INetworkBlock.internalExecute(pLevel, relative, pColor, hasSignal, entity1));
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return behaviour.newBlockEntity(pos, state);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        behaviour.tick(state, level, pos, random);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return state.getValue(StateProperties.VERTICAL_TWO_PART).isBase() ? LibUtils.getTicker(blockEntityType, StatueBlocks.BLOCK_ENTITY.get(), behaviour::entityTick) : null;
    }

    public static class Entity extends AbstractMechanicalBlock.Entity {
        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(StatueBlocks.BLOCK_ENTITY.get(), pPos, pBlockState);
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            ((BehaviourStatueBlock) getBlockState().getBlock()).behaviour.saveAdditional(tag, registries);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            ((BehaviourStatueBlock) getBlockState().getBlock()).behaviour.loadAdditional(tag, registries);

        }

        @Override
        public void connectTo(int color, BlockPos relatedPos, INetworkEntity related) {
            if (!relatedPos.equals(getBlockPos().relative(StateProperties.VerticalTwoPart.getConnectedDirection(getBlockState())))) {
                super.connectTo(color, relatedPos, related); // 确保上下之间不连接
            }
        }
    }

    public static class Behaviour {
        public @Nullable Entity newBlockEntity(BlockPos pos, BlockState state) {
            return new Entity(pos, state);
        }

        public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {}

        public void onUnExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {}

        public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            if (state.getValue(StateProperties.DRIVE)) {
                BlockState state1 = state.setValue(StateProperties.DRIVE, false);
                level.setBlockAndUpdate(pos, state1);
                level.setBlockAndUpdate(pos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(state)), state1);
            }
        }

        public void entityTick(Level level, BlockPos pos, BlockState blockState, Entity entity) {}

        public BlockState getStateForPlacement(BlockPlaceContext pContext, BlockState original) {
            return original;
        }

        public @Nullable VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
            return null;
        }

        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {}

        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {}
    }

    public static class SummonBehaviour<E extends net.minecraft.world.entity.Entity> extends Behaviour {
        private final List<UUID> entities = new ArrayList<>();

        private final boolean noDrops;
        private final int cooldown;
        private final Function3<BlockState, Level, Vec3, E> factory;
        private final Consumer<E> afterSummon;

        public SummonBehaviour(boolean noDrops, Function3<BlockState, Level, Vec3, E> factory) {
            this(noDrops, 10, factory, entity -> {});
        }

        public SummonBehaviour(boolean noDrops, Function3<BlockState, Level, Vec3, E> factory, Consumer<E> afterSummon) {
            this(noDrops, 10, factory, afterSummon);
        }

        public SummonBehaviour(boolean noDrops, int cooldown, Function3<BlockState, Level, Vec3, E> factory) {
            this(noDrops, cooldown, factory, entity -> {});
        }

        public SummonBehaviour(boolean noDrops, int cooldown, Function3<BlockState, Level, Vec3, E> factory, Consumer<E> afterSummon) {
            this.noDrops = noDrops;
            this.cooldown = cooldown;
            this.factory = factory;
            this.afterSummon = afterSummon;
        }

        @Override
        public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
            if (!state.getValue(StateProperties.DRIVE) && state.getValue(StateProperties.VERTICAL_TWO_PART).isBase()) {
                entities.removeIf(entity -> {
                    net.minecraft.world.entity.Entity entity1 = level.getEntity(entity);
                    return entity1 == null || entity1.isRemoved();
                });
                if (entities.size() >= 3) return;
                BlockPos relative = pos.relative(Util.getRandom(LibUtils.HORIZONTAL, level.random));
                E entity = factory.apply(state, level, relative.getCenter());
                level.addFreshEntity(entity);
                afterSummon.accept(entity);
                if (noDrops) {
                    entity.addTag(LibUtils.NO_DROPS_TAG);
                }
                entities.add(entity.getUUID());
                level.setBlockAndUpdate(pos, state.setValue(StateProperties.DRIVE, true));
                level.scheduleTick(pos, state.getBlock(), cooldown);
                networkEntity.getSelf().setChanged();
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            ListTag listTag = new ListTag();
            for (UUID entity : entities) {
                listTag.add(NbtUtils.createUUID(entity));
            }
            tag.put("entities", listTag);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            entities.clear();
            for (Tag entity : tag.getList("entities", Tag.TAG_INT_ARRAY)) {
                entities.add(NbtUtils.loadUUID(entity));
            }
        }
    }
}
