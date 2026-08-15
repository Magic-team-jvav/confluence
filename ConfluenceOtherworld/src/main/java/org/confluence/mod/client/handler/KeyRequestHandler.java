package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.network.c2s.KeyRequestPacketC2S;
import org.confluence.mod.network.c2s.MountTogglePacketC2S;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;

public final class KeyRequestHandler {
    private static boolean keyHealingDown = false;
    private static boolean keyManaDown = false;
    private static boolean keyExtraInventory = false;
    private static boolean keyMountDown = false;

    public static void handle() {
        if (Minecraft.getInstance().isPaused()) return;
        if (ModKeyBindings.HEALING.get().isDown()) {
            if (!keyHealingDown) {
                KeyRequestPacketC2S.requestHealing();
                keyHealingDown = true;
            }
        } else {
            keyHealingDown = false;
        }
        if (ModKeyBindings.MANA.get().isDown()) {
            if (!keyManaDown) {
                KeyRequestPacketC2S.requestMana();
                keyManaDown = true;
            }
        } else {
            keyManaDown = false;
        }
        if (ModKeyBindings.EXTRA_INVENTORY.get().isDown()) {
            if (!keyExtraInventory) {
                OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.EXTRA_INVENTORY);
                keyExtraInventory = true;
            }
        } else {
            keyExtraInventory = false;
        }
        if (ModKeyBindings.MOUNT.get().isDown()) {
            if (!keyMountDown) {
                MountTogglePacketC2S.sendToServer();
                keyMountDown = true;
            }
        } else {
            keyMountDown = false;
        }
    }
}
