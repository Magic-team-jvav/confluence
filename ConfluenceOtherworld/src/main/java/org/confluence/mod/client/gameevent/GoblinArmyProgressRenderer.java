package org.confluence.mod.client.gameevent;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.client.PortDeltaTicker;
import org.mesdag.portlib.client.PortGuiLayer;
import org.mesdag.portlib.client.gui.components.PortSprite;

public final class GoblinArmyProgressRenderer implements PortGuiLayer {
    static final PortSprite SPRITE = new PortSprite(Confluence.asResource("hud/goblin_army"), 256, 96);
    static final int IMAGE_WIDTH = 256;
    static final int IMAGE_HEIGHT = 96;
    static final int HALF_U_WIDTH = IMAGE_WIDTH / 2;
    static final int V_HEIGHT = IMAGE_HEIGHT / 2;
    public static Rect2i occupied;
    static boolean started;
    static float progressed;

    static void handleSync(Player player, boolean start) {
        started = start;
    }

    static void reset() {
        occupied = null;
        started = false;
        progressed = 0;
    }

    public static void handleProgress(float progress) {
        progressed = progress; // 0 -> 1
    }

    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        if (!started) return;
        int centerX = guiGraphics.guiWidth() / 2;
        float yOffset;
        int x = centerX - HALF_U_WIDTH;

        if (occupied != null &&
                Math.max(x, occupied.getX()) < Math.min(x + IMAGE_WIDTH, occupied.getX() + occupied.getWidth()) &&
                Math.max(0, occupied.getY()) < Math.min(V_HEIGHT, occupied.getY() + occupied.getHeight())
        ) {
            yOffset = occupied.getY() + occupied.getHeight();
        } else {
            yOffset = 0;
        }

        int y = Math.max(Mth.ceil(yOffset) - 16, 0);
        guiGraphics.blitSprite(SPRITE, IMAGE_WIDTH, IMAGE_HEIGHT, 0, 0, x, y, IMAGE_WIDTH, V_HEIGHT);
        int u = (int) (IMAGE_WIDTH * progressed);
        guiGraphics.blitSprite(SPRITE, IMAGE_WIDTH, IMAGE_HEIGHT, 0, V_HEIGHT, x, y, IMAGE_WIDTH - u, V_HEIGHT);
        occupied = null;
    }
}
