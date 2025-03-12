package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.network.c2s.KeyRequestPacketC2S;

@OnlyIn(Dist.CLIENT)
public final class KeyRequestHandler {
    private static boolean keyHealingDown = false;
    private static boolean keyManaDown = false;

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
    }
}
