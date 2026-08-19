package org.confluence.mod.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.network.c2s.SwordProjectilePacketC2S;

public final class SwordProjectileInputHandler {
    private static long lastRequestTick = Long.MIN_VALUE;

    public static void handle(LocalPlayer player, boolean attackHeld) {
        if (!attackHeld) return;
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BaseSwordItem sword && sword.projectile(stack) != null && !player.getCooldowns().isOnCooldown(sword) && lastRequestTick != player.level().getGameTime()) {
            lastRequestTick = player.level().getGameTime();
            SwordProjectilePacketC2S.sendToServer();
        }
    }

    private SwordProjectileInputHandler() {}
}
