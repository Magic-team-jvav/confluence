package org.confluence.mod.client.effect.biome;

import net.minecraft.client.player.LocalPlayer;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

@FunctionalInterface
public interface BiomeSkyRenderer {
    void render(LocalPlayer player, PortRenderLevelStageEvent event, float alphaMul);
}
