package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.network.c2s.ZenithPacketC2S;
import org.mesdag.portlib.network.PortPacketDistributor;

public final class ZenithInputHandler {

    public static void handle(LocalPlayer player) {
        if (Minecraft.getInstance().options.keyUse.isDown() && player.getMainHandItem().is(SwordItems.ZENITH.get())) {
            PortPacketDistributor.sendToServer(new ZenithPacketC2S());
        }
    }
}
