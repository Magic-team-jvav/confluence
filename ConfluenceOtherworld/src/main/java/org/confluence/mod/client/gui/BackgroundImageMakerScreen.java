package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;

public class BackgroundImageMakerScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/background_image_maker.png");
    private static final ResourceLocation BORDER_SPRITE = Confluence.asResource("background_image_maker_border");
    private static final int U = 0;
    private static final int V = 1;
    private static final int W = 2;
    private static final int H = 3;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int[] closeUVWH = {253, 0, 3, 3};

    private int imageWidth = 176;
    private int imageHeight = 166;
    private int topPos;
    private int leftPos;

    private final int[] closeXY = new int[2];
    private final int[] baseRegionXYWH = new int[4];
    private final int[] layerRegionXYWH = new int[4];

    public BackgroundImageMakerScreen() {
        super(Component.literal("Background Image Maker"));
    }

    @Override
    protected void init() {
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;

        closeXY[X] = leftPos + imageWidth - closeUVWH[W];
        closeXY[Y] = topPos;
        baseRegionXYWH[W] = 32;
        baseRegionXYWH[H] = imageHeight / 2;
        baseRegionXYWH[X] = leftPos - baseRegionXYWH[W] / 2;
        baseRegionXYWH[Y] = topPos;
        layerRegionXYWH[W] = 32;
        layerRegionXYWH[H] = imageHeight / 2;
        layerRegionXYWH[X] = leftPos - layerRegionXYWH[W] / 2;
        layerRegionXYWH[Y] = topPos + baseRegionXYWH[H];
    }

    @Override
    protected void renderMenuBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFEEEEEE);

        guiGraphics.fill(closeXY[X], closeXY[Y], closeXY[X] + closeUVWH[W], closeXY[Y] + closeUVWH[H], 0xFF444444);

        guiGraphics.fill(baseRegionXYWH[X], baseRegionXYWH[Y], baseRegionXYWH[X] + baseRegionXYWH[W], baseRegionXYWH[Y] + baseRegionXYWH[H], 0xFF999999);
        guiGraphics.fill(layerRegionXYWH[X], layerRegionXYWH[Y], layerRegionXYWH[X] + layerRegionXYWH[W], layerRegionXYWH[Y] + layerRegionXYWH[H], 0xFF666666);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, closeXY[X], closeXY[Y], closeUVWH[W], closeUVWH[H])) {
            onClose();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static boolean isHovered(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
    }
}
