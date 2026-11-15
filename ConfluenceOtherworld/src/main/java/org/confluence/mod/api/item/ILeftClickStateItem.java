package org.confluence.mod.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/// 持于主手的物品接收左键按下与松开；持续状态由玩家的临时附件保存。
public interface ILeftClickStateItem {
    void onLeftClick(Player player, ItemStack stack);

    void onLeftRelease(Player player, ItemStack stack);

    default void onWheelScroll(Player player, ItemStack stack, int amount) {}

    default boolean canSwitchWithoutRelease(Player player, ItemStack stack) {
        return true;
    }
}
