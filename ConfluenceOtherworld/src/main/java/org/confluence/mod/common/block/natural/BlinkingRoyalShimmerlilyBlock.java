package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalFourPartBlock;
import org.confluence.mod.common.init.ModFluids;
import org.jetbrains.annotations.Nullable;

public class BlinkingRoyalShimmerlilyBlock extends HorizontalDirectionalWithHorizontalFourPartBlock {
    private static final VoxelShape A_SHAPE = box(3,-1,0,16,0,13); // 南
    private static final VoxelShape B_SHAPE = box(3,-1,3,16,0,16); // 西
    private static final VoxelShape C_SHAPE = box(0,-1,3,13,0,16); // 北
    private static final VoxelShape D_SHAPE = box(0,-1,0,13,0,13); // 东
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{A_SHAPE, B_SHAPE, C_SHAPE, D_SHAPE};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{D_SHAPE, A_SHAPE, B_SHAPE, C_SHAPE};
    private static final VoxelShape[] CORNER_SHAPES = new VoxelShape[]{C_SHAPE, D_SHAPE, A_SHAPE, B_SHAPE};
    private static final VoxelShape[] FRONT_SHAPES = new VoxelShape[]{B_SHAPE, C_SHAPE, D_SHAPE, A_SHAPE};

    public BlinkingRoyalShimmerlilyBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return switch (state.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case FRONT -> FRONT_SHAPES[index];
            case CORNER -> CORNER_SHAPES[index];
        };
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getFluidState().getType().getFluidType() == ModFluids.SHIMMER.type().get();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && direction == Direction.DOWN) {
            level.scheduleTick(pos, this, 2);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    public static class BItem extends BlockItem {
        public BItem(Block block) {
            super(block, new Properties());
        }

        @Override
        public InteractionResult onItemUseFirst(ItemStack itemstack, UseOnContext context) {
            Player player = context.getPlayer();
            if (player == null || context.getLevel().isClientSide) return super.onItemUseFirst(itemstack, context);
            Level level = context.getLevel();
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (blockhitresult.getType() == HitResult.Type.MISS) {
                return InteractionResult.PASS;
            } else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
                return InteractionResult.PASS;
            } else {
                BlockPos blockpos = blockhitresult.getBlockPos();
                Direction direction = blockhitresult.getDirection();
                BlockPos blockpos1 = blockpos.relative(direction);
                if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, itemstack)) {
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    if (blockstate1.getFluidState().is(ModFluids.SHIMMER.fluid().get())) {
                        player.swing(context.getHand(), true);
                        return useOn(new BlockPlaceContext(player, context.getHand(), itemstack, new BlockHitResult(blockhitresult.getLocation(), direction, blockpos1, blockhitresult.isInside())));
                    }
                }
                return InteractionResult.FAIL;
            }
        }
    }
}
