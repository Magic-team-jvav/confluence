package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

public class BestiaryScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/patchouli/otherworld_note.png");
    private final @Nullable Screen parent;
    private int imageWidth;
    private int imageHeight;
    private int topPos;
    private int leftPos;

    public BestiaryScreen(@Nullable Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.imageWidth = 272;
        this.imageHeight = 180;
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 512, 256);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (parent != null) {
            minecraft.setScreen(parent);
        }
    }
}
