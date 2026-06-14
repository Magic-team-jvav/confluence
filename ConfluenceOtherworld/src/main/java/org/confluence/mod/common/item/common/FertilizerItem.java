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
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BonemealableBlock block && block.isValidBonemealTarget(level, pos, state, level.isClientSide)) {
            if (level instanceof ServerLevel serverLevel) {
                state = state.trySetValue(BlockStateProperties.STAGE, 1);
                for (Property<?> property : state.getProperties()) {
                    if ("age".equals(property.getName()) && property instanceof IntegerProperty integerProperty) {
                        Integer max = Iterables.getLast(integerProperty.getPossibleValues());
                        state = setValue(state, property, max);
                    }
                }
                block.performBonemeal(serverLevel, level.random, pos, state);
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
