package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.confluence.lib.util.ReturnException;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.mixin.accessor.LevelAccessor;

import java.util.function.Predicate;

public class SignalPressurePlateBlock extends PressurePlateBlock implements EntityBlock, INetworkBlock {
    public SignalPressurePlateBlock(BlockSetType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        onNodeRemove(pState, pLevel, pPos, pNewState);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
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
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AbstractMechanicalBlock.Entity(blockPos, blockState);
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {}

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        Predicate<Entity> test = EntitySelector.NO_SPECTATORS.and(entity -> !entity.isIgnoringBlockTriggers() && (type.pressurePlateSensitivity() != BlockSetType.PressurePlateSensitivity.MOBS || entity instanceof LivingEntity));
        try {
            ((LevelAccessor) level).callGetEntities().get(TOUCH_AABB.move(pos), entity -> {
                if (test.test(entity)) {
                    throw new ReturnException(0);
                }
            });
        } catch (ReturnException e) {
            return 15;
        }
        return 0;
    }
}
