package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.FoodItems;


public class ShimmerDroopingVinesPlantBlock extends CaveVinesPlantBlock {
    public ShimmerDroopingVinesPlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BERRIES, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) NatureBlocks.SHIMMER_DROOPING_VINE.get();
    }

    @Override
    protected BlockState updateHeadAfterConvertedFromBody(BlockState oldBodyState, BlockState newHeadState) {
        if (oldBodyState.hasProperty(BERRIES) && newHeadState.hasProperty(BERRIES)) {
            return newHeadState.setValue(BERRIES, oldBodyState.getValue(BERRIES));
        }
        return newHeadState;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // 调用ShimmerDroopingVines的交互方法
        return ShimmerDroopingVines.useShimmerBerries(player, state, level, pos);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(FoodItems.SHIMMER_BERRIES.get());
    }
}