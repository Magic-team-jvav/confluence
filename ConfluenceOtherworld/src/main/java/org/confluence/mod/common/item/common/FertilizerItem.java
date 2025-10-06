package org.confluence.mod.common.item.common;

import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

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
                blockstate = blockstate.trySetValue(BlockStateProperties.STAGE, 1);
                for (Property<?> property : blockstate.getProperties()) {
                    if ("age".equals(property.getName()) && property instanceof IntegerProperty integerProperty) {
                        Integer max = Iterables.getLast(integerProperty.getPossibleValues());
                        blockstate = setValue(blockstate, property, max);
                    }
                }
                bonemealableblock.performBonemeal(serverLevel, level.random, clickedPos, blockstate);
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>, V extends T> BlockState setValue(BlockState blockState, Property<?> property, V value) {
        return blockState.setValue((Property<T>) property, value);
    }
}
