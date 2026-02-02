package org.confluence.mod.client.gameevent;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;

public class GoblinArmyProgressRenderer implements LayeredDraw.Layer {
    static final ResourceLocation SPRITE = Confluence.asResource("hud/goblin_army");
    static final int IMAGE_WIDTH = 256;
    static final int IMAGE_HEIGHT = 96;
    static final int HALF_U_WIDTH = IMAGE_WIDTH / 2;
    static final int V_HEIGHT = IMAGE_HEIGHT / 2;
    public static float yOffset;
    static boolean started;
    static float progressed;

    static void handleSync(Player player, boolean start) {
        started = start;
    }

    static void reset() {
        yOffset = 0;
        started = false;
        progressed = 0;
    }

    public static void handleProgress(float progress) {
        progressed = progress; // 0 -> 1
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!started) return;
        int centerX = guiGraphics.guiWidth() / 2;
        int x = centerX - HALF_U_WIDTH;
        int y = Math.max(Mth.ceil(yOffset) - 16, 0);
        guiGraphics.blitSprite(SPRITE, IMAGE_WIDTH, IMAGE_HEIGHT, 0, 0, x, y, IMAGE_WIDTH, V_HEIGHT);
        int u = (int) (IMAGE_WIDTH * progressed);
        guiGraphics.blitSprite(SPRITE, IMAGE_WIDTH, IMAGE_HEIGHT, 0, V_HEIGHT, x, y, IMAGE_WIDTH - u, V_HEIGHT);
        yOffset = 0;
    }
}
