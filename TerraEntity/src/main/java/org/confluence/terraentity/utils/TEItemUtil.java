package org.confluence.terraentity.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class TEItemUtil {
    public static ItemStack make(Item item, int count, Consumer<ItemStack> modifier){
        ItemStack stack = new ItemStack(item, count);
        modifier.accept(stack);
        return stack;
    }

    public static ItemStack make(Item item, int count){
        return new ItemStack(item, count);
    }

    /**
     * 合并堆叠物品
     * @param items 物品列表
     */
    public static void unionItemStacks(List<ItemStack> items){
        for (int i = 0; i < items.size(); i++) {
            ItemStack current = items.get(i);
            if (current.isEmpty()) {
                continue; // 跳过空槽
            }
            for (int j = i + 1; j < items.size(); j++) {
                ItemStack next = items.get(j);
                if (!next.isEmpty() && current.is(next.getItem()) && current.getCount() < current.getMaxStackSize()) {
                    // 合并堆叠
                    int transfer = Math.min(next.getCount(), current.getMaxStackSize() - current.getCount());
                    current.grow(transfer); // 增加当前物品的数量
                    next.shrink(transfer);  // 减少下一个物品的数量
                    if (next.isEmpty()) {
                        items.set(j, ItemStack.EMPTY); // 如果下一个物品被完全合并，设置为空
                    }
                }
            }
        }
    }
}
