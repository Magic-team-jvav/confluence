package org.confluence.mod.common.block.functional;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.StateProperties;
import org.confluence.mod.common.block.common.StatueBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.Network;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BehaviourStatueBlock extends StatueBlock implements INetworkBlock, EntityBlock {
    private final Behaviour behaviour;

    public BehaviourStatueBlock(Behaviour behaviour, Properties properties) {
        super(properties);
        this.behaviour = behaviour;
        registerDefaultState(stateDefinition.any().setValue(StateProperties.DRIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(StateProperties.DRIVE);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            onNodeRemove(pState, pLevel, pPos, pNewState);
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        behaviour.onExecute(pState, pLevel, pPos, pColor, pEntity);
        execution(pState, pLevel, pPos, pColor, true);
    }

    @Override
    public void onUnExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        behaviour.onUnExecute(pState, pLevel, pPos, pColor, pEntity);
        execution(pState, pLevel, pPos, pColor, false);
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
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    protected void tick(BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(StateProperties.DRIVE)) {
            BlockState state1 = state.setValue(StateProperties.DRIVE, false);
            level.setBlockAndUpdate(pos, state1);
            level.setBlockAndUpdate(pos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(state)), state1);
        }
    }

    public static class Entity extends AbstractMechanicalBlock.Entity {
        private final List<UUID> entities = new ArrayList<>();

        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(StatueBlocks.BLOCK_ENTITY.get(), pPos, pBlockState);
        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
            super.saveAdditional(tag, registries);
            ListTag listTag = new ListTag();
            for (UUID entity : entities) {
                listTag.add(NbtUtils.createUUID(entity));
            }
            tag.put("entities", listTag);
        }

        @Override
        protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
            super.loadAdditional(tag, registries);
            entities.clear();
            for (Tag entity : tag.getList("entities", Tag.TAG_INT_ARRAY)) {
                entities.add(NbtUtils.loadUUID(entity));
            }
        }

        @Override
        public void connectTo(int color, BlockPos relatedPos, INetworkEntity related) {
            if (!relatedPos.equals(getBlockPos().relative(StateProperties.VerticalTwoPart.getConnectedDirection(getBlockState())))) {
                super.connectTo(color, relatedPos, related); // 确保上下之间不连接
            }
        }
    }

    public static class Behaviour {
        public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {}

        public void onUnExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {}
    }

    public static class SummonBehaviour extends Behaviour {
        private final boolean noDrops;
        private final BiFunction<Level, Vec3, net.minecraft.world.entity.Entity> factory;

        public SummonBehaviour(boolean noDrops, BiFunction<Level, Vec3, net.minecraft.world.entity.Entity> factory) {
            this.noDrops = noDrops;
            this.factory = factory;
        }

        @Override
        public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
            if (!pState.getValue(StateProperties.DRIVE) && pState.getValue(StateProperties.VERTICAL_TWO_PART).isBase()) {
                List<UUID> entities = ((Entity) pEntity.getSelf()).entities;
                entities.removeIf(entity -> {
                    net.minecraft.world.entity.Entity entity1 = pLevel.getEntity(entity);
                    return entity1 == null || entity1.isRemoved();
                });
                if (entities.size() >= 3) return;
                BlockPos relative = pPos.relative(Util.getRandom(ModUtils.HORIZONTAL, pLevel.random));
                net.minecraft.world.entity.Entity entity = factory.apply(pLevel, relative.getCenter());
                pLevel.addFreshEntity(entity);
                if (noDrops) {
                    entity.addTag(ModUtils.NO_DROPS_TAG);
                }
                entities.add(entity.getUUID());
                pLevel.setBlockAndUpdate(pPos, pState.setValue(StateProperties.DRIVE, true));
                pLevel.scheduleTick(pPos, pState.getBlock(), 20);
            }
        }
    }
}
