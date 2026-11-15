package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.common.attachment.LeftClickCooldowns;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.network.c2s.LeftClickItemActionPacketC2S;

/// 只在按键状态或主手物品变化时发送包，不按 tick 重复触发物品动作。
public final class LeftClickItemHandler {
    private static ItemStack activeStack = ItemStack.EMPTY;
    private static boolean mouseHeld;
    private static int activeSlot = -1;

    private LeftClickItemHandler() {}

    public static void tick(Minecraft minecraft, LocalPlayer player, boolean attackHeld) {
        ItemStack stack = player.getMainHandItem();
        boolean active = (attackHeld || mouseHeld) && !player.isSpectator() && minecraft.screen == null && !minecraft.isPaused()
                && stack.getItem() instanceof ILeftClickStateItem;
        boolean sameRepeater = activeStack.getItem() instanceof BaseTerraRepeaterItem
                && activeStack.getItem() == stack.getItem() && activeSlot == player.getInventory().selected;
        if (!activeStack.isEmpty() && (!active || activeStack != stack && !sameRepeater)) {
            LeftClickItemActionPacketC2S.sendReleased();
            activeStack = ItemStack.EMPTY;
            activeSlot = -1;
        } else if (sameRepeater) {
            activeStack = stack;
        }
        if (active && activeStack.isEmpty()) {
            if (!(stack.getItem() instanceof BaseTerraRepeaterItem)
                    && LeftClickCooldowns.of(player).isOnCooldown(stack.getItem())) {
                return;
            }

            LeftClickItemActionPacketC2S.sendPressed();
            activeStack = stack;
            activeSlot = player.getInventory().selected;
        }
    }

    /// 鼠标事件捕获短于一个客户端 tick 的点击，tick 仍负责换物品、开界面和改键后的状态。
    public static void mouseButton(LocalPlayer player, boolean pressed) {
        mouseHeld = pressed;
        if (pressed) {
            tick(Minecraft.getInstance(), player, true);
        } else if (!activeStack.isEmpty()) {
            LeftClickItemActionPacketC2S.sendReleased();
            activeStack = ItemStack.EMPTY;
            activeSlot = -1;
        }
    }

    /// 持键时阻止需要先松开的物品通过滚轮切走；其他物品可接收滚轮回调。
    public static boolean scroll(LocalPlayer player, int amount) {
        ItemStack stack = player.getMainHandItem();
        boolean sameRepeater = activeStack.getItem() instanceof BaseTerraRepeaterItem
                && activeStack.getItem() == stack.getItem() && activeSlot == player.getInventory().selected;
        if (activeStack != stack && !sameRepeater || !(stack.getItem() instanceof ILeftClickStateItem item)) {
            return false;
        }
        activeStack = stack;
        LeftClickItemActionPacketC2S.sendScroll(amount);
        return !item.canSwitchWithoutRelease(player, stack);
    }

    public static void reset() {
        activeStack = ItemStack.EMPTY;
        mouseHeld = false;
        activeSlot = -1;
    }
}
