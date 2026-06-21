package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.item.FoodItems;

import static org.confluence.mod.common.init.block.DecorativeBlocks.CARVED_WHITE_PUMPKIN;

public class WhitePumpkinBlock extends PumpkinBlock {
    public WhitePumpkinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.canPerformAction(net.minecraftforge.common.ToolActions.SHEARS_CARVE)) {
            return super.use(state, level, pos, player, hand, hit);
        }
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        Direction face = hit.getDirection();
        Direction facing = face.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : face;
        level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlock(pos, CARVED_WHITE_PUMPKIN.get().defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facing), 11);
        ItemEntity seeds = new ItemEntity(level, pos.getX() + 0.5 + facing.getStepX() * 0.65, pos.getY() + 0.1, pos.getZ() + 0.5 + facing.getStepZ() * 0.65, new ItemStack(FoodItems.WHITE_PUMPKIN_SEED.get(), 4));
        seeds.setDeltaMovement(0.05 * facing.getStepX() + level.random.nextDouble() * 0.02, 0.05, 0.05 * facing.getStepZ() + level.random.nextDouble() * 0.02);
        level.addFreshEntity(seeds);
        stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        level.gameEvent(player, GameEvent.SHEAR, pos);
        player.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
        return InteractionResult.sidedSuccess(false);
    }
}
