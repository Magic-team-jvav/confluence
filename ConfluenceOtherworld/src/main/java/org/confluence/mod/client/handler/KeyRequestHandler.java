package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.network.c2s.ActivateSetBonusPacketC2S;
import org.confluence.mod.network.c2s.KeyRequestPacketC2S;
import org.confluence.mod.network.c2s.MountTogglePacketC2S;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;

public final class KeyRequestHandler {
    private static boolean keyHealingDown = false;
    private static boolean keyManaDown = false;
    private static boolean keyExtraInventory = false;
    private static boolean keyMountDown = false;
    /// 上次按下“激活套装奖励”的刻；两次按下间隔不超过 DOUBLE_TAP 就视为双击。
    private static int setBonusClickTick = -1000;
    private static final int SET_BONUS_DOUBLE_TAP_TICKS = 7;

    public static void handle() {
        while (ModKeyBindings.QUICK_BUFF.get().consumeClick()) {
            KeyRequestPacketC2S.requestQuickBuff();
        }
        // “激活套装奖励”按需求做成双击触发：两次按下间隔不超过 SET_BONUS_DOUBLE_TAP_TICKS 刻才算。
        while (ModKeyBindings.ACTIVATE_SET_BONUS.get().consumeClick()) {
            int now = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.tickCount;
            if (now - setBonusClickTick <= SET_BONUS_DOUBLE_TAP_TICKS) {
                ActivateSetBonusPacketC2S.send2Server();
                setBonusClickTick = -1000;
            } else {
                setBonusClickTick = now;
            }
        }
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
