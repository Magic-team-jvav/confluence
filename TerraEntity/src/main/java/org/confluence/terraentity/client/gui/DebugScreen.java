package org.confluence.terraentity.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.confluence.terraentity.item.DebugItem;
import org.confluence.terraentity.network.c2s.SetDebugModePacket;

public class DebugScreen extends Screen {
    protected Button behaviorTreeBt;

    public DebugScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        behaviorTreeBt = Button.builder(Component.literal("Behavior Tree"), p -> {
            if (Minecraft.getInstance().player != null) {
                SetDebugModePacket.send(DebugItem.DebugMode.BT_WEB_VIEWER, Minecraft.getInstance().player);
            }
        }).pos(10, 10).size(60, 20).build();

        this.addRenderableWidget(behaviorTreeBt);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void setScreen() {
        Minecraft.getInstance().setScreen(new DebugScreen(Component.literal("Debug Screen")));
    }
}
