package org.confluence.mod.api.item;

import net.minecraft.world.item.ItemStack;

/// 魂师弓的标记接口：击杀额外收魂、灵魂足够时增伤都由 {@link org.confluence.mod.common.event.game.entity.LivingEntityEvents} 统一结算。
///
/// 新增灵魂弓时只需让物品类实现本接口，无需再改事件。
public interface ISoulBow {
    static boolean isSoulBow(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ISoulBow;
    }
}
