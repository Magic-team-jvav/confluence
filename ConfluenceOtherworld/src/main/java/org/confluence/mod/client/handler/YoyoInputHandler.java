package org.confluence.mod.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.confluence.mod.network.c2s.YoyoControlPacketC2S;

/// 将客户端按键状态转换为悠悠球的离散控制动作。
public final class YoyoInputHandler implements ClientWeaponInputHandler {
    public static final YoyoInputHandler INSTANCE = new YoyoInputHandler();
    private boolean held;

    private YoyoInputHandler() {}

    @Override
    public void tick(LocalPlayer player, ItemStack stack, boolean attackHeld) {
        boolean active = usesLeftButton(stack) && attackHeld;
        if (active && !held) YoyoControlPacketC2S.sendPress();
        else if (held && !active) YoyoControlPacketC2S.sendRelease();
        held = active;
    }

    @Override
    public boolean scroll(LocalPlayer player, ItemStack stack, double amount) {
        if (!held || !(stack.getItem() instanceof YoyoItem) || amount == 0.0) return false;
        YoyoControlPacketC2S.sendRangeAdjustment(amount > 0.0 ? 1 : -1);
        return true;
    }

    @Override
    public boolean blocksAttack(ItemStack stack) {
        return usesLeftButton(stack);
    }

    @Override
    public boolean blocksUse(ItemStack stack) {
        return usesLeftButton(stack);
    }

    private static boolean usesLeftButton(ItemStack stack) {
        return stack.getItem() instanceof YoyoItem && ClientConfigs.usesLeftWeaponButton(stack);
    }

    @Override
    public void reset() {
        held = false;
    }
}
