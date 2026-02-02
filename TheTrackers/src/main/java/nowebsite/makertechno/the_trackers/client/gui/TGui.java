package nowebsite.makertechno.the_trackers.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;
import nowebsite.makertechno.the_trackers.core.config.TConfig;
import nowebsite.makertechno.the_trackers.core.track.EntityTracker;
import nowebsite.makertechno.the_trackers.core.track.WorldSingletonTracker;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TGui implements LayeredDraw.Layer {
    private final Minecraft minecraft;
    private static @Nullable WorldSingletonTracker tracker = null;

    public TGui(Minecraft minecraft) {
        this.minecraft = minecraft;
    }
    @Nullable
    public static Player getCameraPlayer(@NotNull Minecraft minecraft) {
        return minecraft.getCameraEntity() instanceof Player player ? player : null;
    }

    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        Player player = getCameraPlayer(this.minecraft);
        if (!minecraft.options.hideGui && player != null && TConfig.available){
            EntityTracker.getRENDERING().forEach((uuid, state) -> state.renderComponent(guiGraphics, deltaTracker.getGameTimeDeltaTicks(), player));
            if (tracker != null) tracker.render(guiGraphics, deltaTracker.getGameTimeDeltaTicks(), player);
        }
    }

    public static synchronized void setExtendTracker(@Nullable WorldSingletonTracker tracker) {
        TGui.tracker = tracker;
    }
}
