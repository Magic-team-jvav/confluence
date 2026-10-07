package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

/// 诅咒焰可重复点燃方块，放置时保留材料数量。
public class CursedFlameItem extends BlockItem {
    public CursedFlameItem(Block block) {
        super(block, new Item.Properties().fireResistant());
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        int count = stack.getCount();
        InteractionResult result = super.place(context);
        if (result.consumesAction()) {
            stack.setCount(count);
        }
        return result;
    }
}
