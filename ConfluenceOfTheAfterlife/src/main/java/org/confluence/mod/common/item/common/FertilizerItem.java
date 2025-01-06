package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FertilizerItem extends Item {
    public FertilizerItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(clickedPos);
        if (blockstate.getBlock() instanceof BonemealableBlock bonemealableblock && bonemealableblock.isValidBonemealTarget(level, clickedPos, blockstate)) {
            if (level instanceof ServerLevel serverLevel) {
                if (blockstate.hasProperty(BlockStateProperties.STAGE)) {
                    blockstate = blockstate.setValue(BlockStateProperties.STAGE, 1);
                }
                bonemealableblock.performBonemeal(serverLevel, level.random, clickedPos, blockstate);
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
