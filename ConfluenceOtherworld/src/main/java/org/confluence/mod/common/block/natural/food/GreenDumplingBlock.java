package org.confluence.mod.common.block.natural.food;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.item.food.FoodType;

public class GreenDumplingBlock extends Block {
    public static final IntegerProperty PIECE = IntegerProperty.create("piece", 1, 5);

    public GreenDumplingBlock() {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).strength(1.0f));
        this.registerDefaultState(this.stateDefinition.any().setValue(PIECE, 1));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState clickedBlockState = context.getLevel().getBlockState(context.getClickedPos());
        if (clickedBlockState.is(this) && clickedBlockState.getValue(PIECE) < 5) {
            return clickedBlockState.cycle(PIECE);
        }
        return clickedBlockState;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return eat(level, pos, state, player);
    }

    protected static InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(true)) {
            return InteractionResult.PASS;
        } else {
            player.getFoodData().eat(FoodType.WellFedPropertiesDuration(6000));
            int i = state.getValue(PIECE);
            level.gameEvent(player, GameEvent.EAT, pos);
            if (i > 1) {
                level.setBlock(pos, state.setValue(PIECE, i - 1), 3);
            } else {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIECE);
    }
}


