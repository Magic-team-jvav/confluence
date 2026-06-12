package org.confluence.mod.client.gameevent;

import net.minecraft.client.player.LocalPlayer;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

@FunctionalInterface
public interface AfterRenderSky {
    void render(LocalPlayer player, PortRenderLevelStageEvent event);
}
