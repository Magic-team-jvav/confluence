package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import org.confluence.mod.client.gui.AskForSoftcoreScreen;

public class AskForSoftcoreLayer implements LayeredDraw.Layer {
    private static boolean askForSoftcoreLayer = false;

    private static final Screen screen = new AskForSoftcoreScreen();

    public static void setAskForSoftcoreLayer(boolean b) {
        askForSoftcoreLayer = b;
    }

    public static boolean isAskForSoftcoreLayer() {
        return askForSoftcoreLayer;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (isAskForSoftcoreLayer()) {
            Minecraft minecraft = Minecraft.getInstance();
            Window window = minecraft.getWindow();
            minecraft.pauseGame(true);
            minecraft.setScreen(screen);
            if (InputConstants.isKeyDown(window.getWindow(), InputConstants.KEY_ESCAPE)) {
                setAskForSoftcoreLayer(false);
            }
        }
    }
}
