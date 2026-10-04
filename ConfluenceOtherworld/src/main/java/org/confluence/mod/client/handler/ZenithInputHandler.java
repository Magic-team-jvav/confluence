package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.SummonItems;
import org.confluence.mod.network.c2s.ZenithPacketC2S;

/**
 * 天顶剑输入：按住使用键（右键）时每 tick 向服务端上报一次挥砍。
 * <p>
 * 冷却按玩家攻速换算（{@code 20 / 攻速} tick），与服务端 {@code ZenithData.addPower()} 的
 * 蓄力速率一致；服务端仍会自行校验主手物品，客户端只是节流。
 * </p>
 */
public final class ZenithInputHandler {

    public static void handle(LocalPlayer player) {
        if (Minecraft.getInstance().options.keyUse.isDown()) {
            if (player.getMainHandItem().is(SummonItems.ZENITH.get())) {
                ZenithPacketC2S.sendToServer();
            }
        }
    }

    private ZenithInputHandler() {}
}
