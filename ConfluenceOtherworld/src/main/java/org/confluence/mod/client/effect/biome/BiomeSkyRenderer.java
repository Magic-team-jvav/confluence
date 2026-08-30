package org.confluence.mod.client.effect.biome;

import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@FunctionalInterface
public interface BiomeSkyRenderer {
    void render(LocalPlayer player, RenderLevelStageEvent event, float alphaMul);
}
