package org.confluence.mod.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

/// 一类客户端武器输入的独立处理器。
public interface ClientWeaponInputHandler {
    default void tick(LocalPlayer player, ItemStack stack, boolean attackHeld) {}

    default boolean scroll(LocalPlayer player, ItemStack stack, double amount) {
        return false;
    }

    default boolean blocksAttack(ItemStack stack) {
        return false;
    }

    default boolean blocksUse(ItemStack stack) {
        return false;
    }

    default void reset() {}
}
