package org.confluence.mod.client.gameevent;

import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@FunctionalInterface
public interface AfterRenderSky {
    void render(LocalPlayer player, RenderLevelStageEvent event);
}
