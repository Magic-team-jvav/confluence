package org.confluence.mod.common.block.functional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public class BehaviourPressurePlateBlock extends BasePressurePlateBlock implements EntityBlock, INetworkBlock {
    public static final MapCodec<BehaviourPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Behaviour.CODEC.fieldOf("behaviour").forGetter(block -> block.behaviour),
            propertiesCodec(),
            BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.type)
    ).apply(instance, BehaviourPressurePlateBlock::new));
    public static final Behaviour PLAYER = Behaviour.register(Confluence.asResource("player"), new Behaviour() {
        @Override
        protected int getSignalStrength(Level level, BlockPos blockPos) {
            net.minecraft.world.phys.AABB aabb = TOUCH_AABB.move(blockPos);
            for (Player player : level.players()) {
                if (aabb.contains(player.position())) {
                    return 15;
                }
            }
            return 0;
        }
    });

    private final Behaviour behaviour;

    public BehaviourPressurePlateBlock(Behaviour behaviour, Properties pProperties, BlockSetType pType) {
        super(pProperties, pType);
        this.behaviour = behaviour;
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        onNodeRemove(pState, pLevel, pPos, pNewState);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AbstractMechanicalBlock.Entity(blockPos, blockState);
    }

    @Override
    protected MapCodec<BehaviourPressurePlateBlock> codec() {
        return CODEC;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        int i = getSignalForState(pState);
        int j = getSignalStrength(pLevel, pPos);
        if (i > 0 && i != j) {
            execute(pState, pLevel, pPos, j > 0);
        }
        super.tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        if (pLevel instanceof ServerLevel serverLevel) {
            int i = getSignalForState(pState);
            if (i == 0) {
                execute(pState, serverLevel, pPos, true);
            }
        }
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        behaviour.onExecute(state, level, pos, color, networkEntity);
    }

    @Override
    public void onUnExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        behaviour.onUnExecute(state, level, pos, color, networkEntity);
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos blockPos) {
        return behaviour.getSignalStrength(level, blockPos);
    }

    @Override
    protected int getSignalForState(BlockState blockState) {
        return behaviour.getSignalForState(blockState);
    }

    @Override
    protected BlockState setSignalForState(BlockState blockState, int i) {
        return behaviour.setSignalForState(blockState, i);
    }

    @SuppressWarnings("unused")
    public abstract static class Behaviour {
        private static final BiMap<ResourceLocation, Behaviour> MAP = HashBiMap.create();
        public static final Codec<Behaviour> CODEC = ResourceLocation.CODEC.xmap(MAP::get, MAP.inverse()::get);

        public static Behaviour register(ResourceLocation id, Behaviour behaviour) {
            MAP.put(id, behaviour);
            return behaviour;
        }

        public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {}

        public void onUnExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {}

        protected int getSignalForState(BlockState blockState) {
            return blockState.hasProperty(POWERED) && blockState.getValue(POWERED) ? 15 : 0;
        }

        protected BlockState setSignalForState(BlockState blockState, int strength) {
            return blockState.trySetValue(POWERED, strength > 0);
        }

        protected abstract int getSignalStrength(Level level, BlockPos blockPos);
    }
}
