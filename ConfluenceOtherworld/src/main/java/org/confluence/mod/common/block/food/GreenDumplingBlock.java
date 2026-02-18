package org.confluence.mod.common.block.food;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.FoodItems;

public class GreenDumplingBlock extends Block {
    public static final IntegerProperty PIECE = IntegerProperty.create("piece", 0, 4);
    protected static final VoxelShape[] SHAPE_BY_PIECE;

    static {
        VoxelShape[] shapes = new VoxelShape[5];
        double[] ys = {3, 4, 5, 9, 11};
        for (int i = 0; i < 5; i++) {
            shapes[i] = box(3, 0, 3, 13, ys[i], 13);
        }
        SHAPE_BY_PIECE = shapes;
    }

    public GreenDumplingBlock() {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).strength(1.0f));
        this.registerDefaultState(this.stateDefinition.any().setValue(PIECE, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState clickedBlockState = context.getLevel().getBlockState(context.getClickedPos());
        if (clickedBlockState.is(this)) {
            int currentPiece = clickedBlockState.getValue(PIECE);
            if (currentPiece < 5) {
                return clickedBlockState.cycle(PIECE);
            }
        }
        return this.defaultBlockState();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(FoodItems.GREEN_DUMPLING.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        int currentPiece = state.getValue(PIECE);
        if (currentPiece >= 4) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!player.isCreative()) stack.shrink(1);
        level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlockAndUpdate(pos, state.setValue(PIECE, currentPiece + 1));
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.hasEffect(ModEffects.CHOKING)) {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.translatable("message.confluence.choking"));
            }
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            InteractionResult result = eat(level, pos, state, player);
            if (result.consumesAction()) return InteractionResult.SUCCESS;
            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected static InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!player.canEat(false)) return InteractionResult.PASS;
        player.getFoodData().eat(3, 0.25F);
        player.addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 6000, 1));
        player.addEffect(new MobEffectInstance(ModEffects.CHOKING, 2400));
        player.addEffect(new MobEffectInstance(ModEffects.HUNGER_DELAYED, 1000));
        player.playSound(SoundEvents.GENERIC_EAT);
        int pieceCount = state.getValue(PIECE);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (!itemStack.is(FoodItems.GREEN_DUMPLING.get())) {
            if (pieceCount > 1) {
                level.setBlock(pos, state.setValue(PIECE, pieceCount - 1), 3);
            } else {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_PIECE[state.getValue(PIECE)];
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIECE);
    }
}
