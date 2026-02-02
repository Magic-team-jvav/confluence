package org.confluence.terraentity.data.enchantment;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import javax.annotation.Nullable;

/**
 * 辅助类，用于处理与附魔相关的逻辑。
 */
public class TEEnchantmentHelper {

    /**
     * 检查给定的物品堆栈的附魔等级。
     *
     * @param enchantments 附魔的类型
     * @param stack 要检查的物品堆栈。
     * @return 如果物品具有Adam附魔，则返回true，否则返回false。
     */
    public static int getEnchantmentLevel(ResourceKey<Enchantment> enchantments, ItemStack stack) {
        // 从物品堆栈中获取附魔信息，如果没有则使用空的附魔集合。
        ItemEnchantments itemenchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {// 获得物品的所有附魔
            @Nullable ResourceKey<Enchantment> enchantmentKey = entry.getKey().getKey();
            if (enchantmentKey != null && enchantmentKey.equals(enchantments)) { // key是附魔，value是附魔的等级
                return entry.getIntValue();
            }
        }

        //没有附魔则返回0。
        return 0;
    }


}