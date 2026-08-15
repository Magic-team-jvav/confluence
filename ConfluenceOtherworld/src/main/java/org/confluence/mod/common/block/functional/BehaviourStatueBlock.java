package org.confluence.mod.common.block.functional;

import com.mojang.datafixers.util.Function3;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
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
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState original = super.getStateForPlacement(context);
        return original == null ? null : behaviour.getStateForPlacement(context, original);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = behaviour.getShape(state, level, pos, context);
        return shape == null ? super.getShape(state, level, pos, context) : shape;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            onNodeRemove(state, level, pos, newState);
            super.onRemove(state, level, pos, newState, movedByPiston);
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

    private void execution(BlockState state, ServerLevel level, BlockPos pos, int color, boolean hasSignal) {
        BlockPos relative = pos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(state));
        if (level.getBlockEntity(relative) instanceof INetworkEntity entity) {
            Network network = entity.getOrCreateNetworkNode().getNetwork(color);
            if (network != null && hasSignal != network.hasSignal()) {
                network.setSignal(hasSignal);
                network.getNodes().stream()
                        .map(NetworkNode::getEntity)
                        .collect(Collectors.toSet())
                        .forEach(entity1 -> INetworkBlock.internalExecute(level, relative, color, hasSignal, entity1));
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return behaviour.newBlockEntity(pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        behaviour.tick(state, level, pos, random);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return state.getValue(StateProperties.VERTICAL_TWO_PART).isBase() ? LibUtils.getTicker(blockEntityType, StatueBlocks.BLOCK_ENTITY.get(), behaviour::entityTick) : null;
    }

    public static class BEntity extends AbstractMechanicalBlock.BEntity {
        private static final int MAX_TRACKED_ENTITIES = 3;
        /**
         * 该列表必须属于具体方块实体，而不能放在 Behaviour 中。Behaviour 随方块注册只创建一次，
         * 若由它持有 UUID，同种雕像会跨坐标、跨维度甚至跨存档共享同一份召唤记录。
         */
        private final List<UUID> summonedEntities = new ArrayList<>();

        public BEntity(BlockPos pos, BlockState state) {
            super(StatueBlocks.BLOCK_ENTITY.get(), pos, state);
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            ListTag entitiesTag = new ListTag();
            for (int i = 0; i < Math.min(summonedEntities.size(), MAX_TRACKED_ENTITIES); i++) {
                entitiesTag.add(NbtUtils.createUUID(summonedEntities.get(i)));
            }
            tag.put("entities", entitiesTag);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            summonedEntities.clear();
            ListTag entitiesTag = tag.getList("entities", Tag.TAG_INT_ARRAY);
            for (int i = 0; i < entitiesTag.size() && summonedEntities.size() < MAX_TRACKED_ENTITIES; i++) {
                Tag entry = entitiesTag.get(i);
                if (!(entry instanceof IntArrayTag uuidTag) || uuidTag.getAsIntArray().length != 4)
                    continue;
                // 长度验证后 loadUUID 才不会因损坏的旧存档令整个区块加载失败。
                UUID uuid = NbtUtils.loadUUID(uuidTag);
                if (!summonedEntities.contains(uuid)) summonedEntities.add(uuid);
            }
        }

        List<UUID> getSummonedEntities() {
            return summonedEntities;
        }

        @Override
        public void connectTo(int color, BlockPos relatedPos, INetworkEntity related) {
            if (!relatedPos.equals(getBlockPos().relative(StateProperties.VerticalTwoPart.getConnectedDirection(getBlockState())))) {
                super.connectTo(color, relatedPos, related); // 确保上下之间不连接
            }
        }
    }

    public static class Behaviour {
        public @Nullable BehaviourStatueBlock.BEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new BEntity(pos, state);
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

        public void entityTick(Level level, BlockPos pos, BlockState blockState, BEntity entity) {}

        public BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
            return state;
        }

        public @Nullable VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return null;
        }

    }

    public static class SummonBehaviour<E extends net.minecraft.world.entity.Entity> extends Behaviour {
        private final boolean randomPos;
        private final boolean noDrops;
        private final int cooldown;
        private final Function3<BlockState, Level, Vec3, E> factory;
        private final Consumer<E> afterSummon;

        public SummonBehaviour(boolean randomPos, boolean noDrops, Function3<BlockState, Level, Vec3, E> factory) {
            this(randomPos, noDrops, 10, factory, entity -> {});
        }

        public SummonBehaviour(boolean randomPos, boolean noDrops, Function3<BlockState, Level, Vec3, E> factory, Consumer<E> afterSummon) {
            this(randomPos, noDrops, 10, factory, afterSummon);
        }

        public SummonBehaviour(boolean randomPos, boolean noDrops, int cooldown, Function3<BlockState, Level, Vec3, E> factory) {
            this(randomPos, noDrops, cooldown, factory, entity -> {});
        }

        public SummonBehaviour(boolean randomPos, boolean noDrops, int cooldown, Function3<BlockState, Level, Vec3, E> factory, Consumer<E> afterSummon) {
            this.randomPos = randomPos;
            this.noDrops = noDrops;
            this.cooldown = cooldown;
            this.factory = factory;
            this.afterSummon = afterSummon;
        }

        @Override
        public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
            if (!state.getValue(StateProperties.DRIVE) && state.getValue(StateProperties.VERTICAL_TWO_PART).isBase() &&
                    networkEntity.getSelf() instanceof BEntity blockEntity) {
                List<UUID> entities = blockEntity.getSummonedEntities();
                entities.removeIf(entity -> {
                    net.minecraft.world.entity.Entity entity1 = level.getEntity(entity);
                    return entity1 == null || entity1.isRemoved();
                });
                if (entities.size() >= 3) return;
                BlockPos relative = randomPos ? pos.relative(Util.getRandom(LibUtils.HORIZONTAL, level.random)) : pos;
                E entity = factory.apply(state, level, relative.getCenter());
                if (!level.addFreshEntity(entity)) return;
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
    }
}
