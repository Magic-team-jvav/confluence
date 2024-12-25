package org.confluence.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.network.c2s.ApplySelectionPacketC2S;
import org.confluence.mod.network.s2c.OpenSelectionsScreenPacketS2C;
import org.jetbrains.annotations.NotNull;

public class SelectionsScreen extends Screen {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );
    private static final int INTERVAL = 8;
    private final Component[] selections;
    private final boolean[] enables;
    private int buttonWidth;
    private int buttonHeight;
    private int top;
    private int left;

    public SelectionsScreen(Component[] selections, boolean[] enables) {
        super(Component.empty());
        this.selections = selections;
        this.enables = enables;
    }

    @Override
    protected void init() {
        this.buttonWidth = 200;
        this.buttonHeight = 20;
        this.top = (height - ((selections.length - 1) * (buttonHeight + INTERVAL) + buttonHeight)) / 2;
        this.left = (width - buttonWidth) / 2;

        int localTop = top;
        for (int i = 0; i < enables.length; i++) {
            int y = localTop + i * buttonHeight;
            int index = i;
            ImageButton widget = new ImageButton(left, y, buttonWidth, buttonHeight, SPRITES, button -> {
                ApplySelectionPacketC2S.sendToServer((byte) index);
                Minecraft.getInstance().setScreen(null);
            });
            widget.active = enables[i];
            addRenderableWidget(widget);
            localTop += INTERVAL;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int localTop = top;
        for (int i = 0; i < selections.length; i++) {
            int y = localTop + i * buttonHeight;
            AbstractWidget.renderScrollingString(guiGraphics, font, selections[i], left, y, left + buttonWidth, y + buttonHeight, 0xFFFFFF);
            localTop += INTERVAL;
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    public static void handlePacket(OpenSelectionsScreenPacketS2C packet) {
        Minecraft.getInstance().setScreen(new SelectionsScreen(packet.selections(), packet.enables()));
    }
}
