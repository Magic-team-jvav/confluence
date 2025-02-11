package org.confluence.mod.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import javax.annotation.Nullable;

public class EnchantmentUtil {
    public static int getEnchantmentLevel(ResourceKey<Enchantment> enchantments, ItemStack stack) {
        if(stack == null || stack.isEmpty()) return 0;
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
