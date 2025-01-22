package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.mod.common.block.HorizontalDirectionalWithHorizontalFourPartBlock;
import org.confluence.mod.common.init.ModFluids;
import org.jetbrains.annotations.Nullable;

public class BlinkingRoyalWaterlilyBlock extends HorizontalDirectionalWithHorizontalFourPartBlock {
    public static final MapCodec<BlinkingRoyalWaterlilyBlock> CODEC = simpleCodec(BlinkingRoyalWaterlilyBlock::new);

    public BlinkingRoyalWaterlilyBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<BlinkingRoyalWaterlilyBlock> codec() {
        return CODEC;
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

    public static class Item extends BlockItem {
        public Item(Block block) {
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
                        player.swing(context.getHand());
                        return useOn(new BlockPlaceContext(player, context.getHand(), itemstack, new BlockHitResult(blockhitresult.getLocation(), direction, blockpos1, blockhitresult.isInside())));
                    }
                }
                return InteractionResult.FAIL;
            }
        }
    }
}
