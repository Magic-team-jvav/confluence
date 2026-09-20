package org.confluence.mod.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.confluence.mod.network.c2s.FlailControlPacketC2S;
import org.confluence.mod.network.c2s.LeftClickItemActionPacketC2S;

public final class FlailHandler {
    private static boolean wasFlailKeyHeld = false;
    private static boolean wasRepeaterKeyHeld = false;

    public static void handle(LocalPlayer player, boolean attackHeld) {
        ItemStack mainHandItem = player.getMainHandItem();
        boolean isRepeater = mainHandItem.getItem() instanceof BaseTerraRepeaterItem;
        if (isRepeater && attackHeld && !wasRepeaterKeyHeld) {
            LeftClickItemActionPacketC2S.sendPressed();
        } else if (wasRepeaterKeyHeld && (!isRepeater || !attackHeld)) {
            LeftClickItemActionPacketC2S.sendReleased();
        }
        wasRepeaterKeyHeld = attackHeld && isRepeater;
        ClientWeaponInputManager.tick(player);
        boolean isFlail = mainHandItem.has(ModDataComponentTypes.FLAIL);
        if (isFlail) {
            BaseFlailItem flailItem = mainHandItem.getItem() instanceof BaseFlailItem item ? item : null;
            if (flailItem != null && flailItem.isAutoSwing()) {
                // 自动挥舞：客户端每 tick 请求，服务端用冷却和活跃射弹上限校验。
                if (attackHeld && flailItem.canAutoSwing(player)) {
                    FlailControlPacketC2S.sendHold();
                }
            } else if (attackHeld && !wasFlailKeyHeld) {
                FlailControlPacketC2S.sendHold();
            } else if (!attackHeld && wasFlailKeyHeld) {
                FlailControlPacketC2S.sendRelease();
            }
        }
        wasFlailKeyHeld = attackHeld && isFlail;
    }

    public static void reset() {
        wasFlailKeyHeld = false;
        wasRepeaterKeyHeld = false;
    }
}
