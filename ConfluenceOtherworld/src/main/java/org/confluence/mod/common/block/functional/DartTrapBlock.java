package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSecretSeeds;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.TRIGGERED;

public class DartTrapBlock extends AbstractMechanicalBlock {
    public static final ItemStack PICKUP_ITEM_STACK = Items.SPECTRAL_ARROW.getDefaultInstance();
    public static final Component NAME = Component.translatable("entity.confluence.dart");

    public DartTrapBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return rotate(pState, pMirror.getRotation(pState.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, TRIGGERED);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            execute(pState, (ServerLevel) pLevel, pPos, pLevel.hasNeighborSignal(pPos));
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.setBlockAndUpdate(pPos, pState.setValue(TRIGGERED, false));
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        if (pState.getValue(TRIGGERED)) return;
        pLevel.setBlockAndUpdate(pPos, pState.setValue(TRIGGERED, true));
        Direction direction = pState.getValue(FACING);
        double x = pPos.getX() + 0.5 + 0.7 * direction.getStepX();
        double y = pPos.getY() + 0.5 + 0.7 * direction.getStepY();
        double z = pPos.getZ() + 0.5 + 0.7 * direction.getStepZ();
        Arrow arrow = new Arrow(pLevel, x, y, z, PICKUP_ITEM_STACK, null);
        arrow.setCustomName(NAME);
        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        arrow.addEffect(new MobEffectInstance(MobEffects.POISON, LibUtils.switchByDifficulty(pLevel, pPos, 300, 600, 750), 1));
        if (ModSecretSeeds.NO_TRAPS.match(pLevel)) {
            arrow.addEffect(new MobEffectInstance(ModEffects.BLEEDING, 1200, 1));
        }
        arrow.setBaseDamage(1.0);
        arrow.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), 3.0F, 0.0F);
        pLevel.addFreshEntity(arrow);
        pLevel.setBlockAndUpdate(pPos, pState.setValue(TRIGGERED, true));
        pLevel.scheduleTick(pPos, this, 20);
    }
}
