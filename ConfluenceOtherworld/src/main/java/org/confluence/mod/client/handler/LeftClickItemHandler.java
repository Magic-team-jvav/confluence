package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.network.c2s.LeftClickItemActionPacketC2S;

/// 只在按键状态或主手物品变化时发送包，不按 tick 重复触发物品动作。
public final class LeftClickItemHandler {
    private static ItemStack activeStack = ItemStack.EMPTY;
    private static boolean mouseHeld;

    private LeftClickItemHandler() {}

    public static void tick(LocalPlayer player, boolean attackHeld) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemStack stack = player.getMainHandItem();
        boolean active = (attackHeld || mouseHeld) && !player.isSpectator() && minecraft.screen == null && !minecraft.isPaused()
                && stack.getItem() instanceof ILeftClickStateItem;
        if (!activeStack.isEmpty() && (!active || activeStack != stack)) {
            LeftClickItemActionPacketC2S.sendReleased();
            activeStack = ItemStack.EMPTY;
        }
        if (active && activeStack.isEmpty()) {
            LeftClickItemActionPacketC2S.sendPressed();
            activeStack = stack;
        }
    }

    /// 鼠标事件捕获短于一个客户端 tick 的点击，tick 仍负责换物品、开界面和改键后的状态。
    public static void mouseButton(LocalPlayer player, boolean pressed) {
        mouseHeld = pressed;
        if (pressed) {
            tick(player, true);
        } else if (!activeStack.isEmpty()) {
            LeftClickItemActionPacketC2S.sendReleased();
            activeStack = ItemStack.EMPTY;
        }
    }

    /// 持键时阻止需要先松开的物品通过滚轮切走；其他物品可接收滚轮回调。
    public static boolean scroll(LocalPlayer player, int amount) {
        ItemStack stack = player.getMainHandItem();
        if (activeStack != stack || !(stack.getItem() instanceof ILeftClickStateItem item))
            return false;
        LeftClickItemActionPacketC2S.sendScroll(amount);
        return !item.canSwitchWithoutRelease(player, stack);
    }

    public static void reset() {
        activeStack = ItemStack.EMPTY;
        mouseHeld = false;
    }
}
