package org.confluence.mod.client.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.network.c2s.OpenNPCServicePacketC2S;

/// 哥布林工匠的专属服务入口，公共商店不负责重铸业务。
public final class GoblinTinkererDialogScreen extends NPCDialogScreen {
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_GAP = 10;

    public GoblinTinkererDialogScreen(int entityId) {
        super(entityId, true);
    }

    @Override
    protected void addTradeButtons(BaseNPC npc, int buttonY) {
        int left = (width - BUTTON_WIDTH * 3 - BUTTON_GAP * 2) / 2;
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), button -> selectDialog(npc)).bounds(left, buttonY, BUTTON_WIDTH, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.shop"), button -> OpenNPCServicePacketC2S.sendToServer(entityId, OpenNPCServicePacketC2S.TRADE)).bounds(left + BUTTON_WIDTH + BUTTON_GAP, buttonY, BUTTON_WIDTH, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("button.confluence.reforge"), button -> OpenNPCServicePacketC2S.sendToServer(entityId, OpenNPCServicePacketC2S.REFORGE)).bounds(left + (BUTTON_WIDTH + BUTTON_GAP) * 2, buttonY, BUTTON_WIDTH, 20).build());
    }
}
