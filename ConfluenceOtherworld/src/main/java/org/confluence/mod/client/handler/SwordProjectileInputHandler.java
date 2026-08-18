package org.confluence.mod.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.network.c2s.SwordProjectilePacketC2S;

public final class SwordProjectileInputHandler {
    public static void handle(LocalPlayer player, boolean attackHeld) {
        if (!attackHeld) return;
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BaseSwordItem sword && stack.has(ModDataComponentTypes.SWORD_PROJECTILE)
                && !player.getCooldowns().isOnCooldown(sword)) {
            SwordProjectilePacketC2S.sendToServer();
        }
    }

    private SwordProjectileInputHandler() {}
}
