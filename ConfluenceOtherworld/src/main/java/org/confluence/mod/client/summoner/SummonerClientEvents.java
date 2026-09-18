package org.confluence.mod.client.summoner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.client.summoner.model.bbmodel.BBModelManager;
import org.confluence.mod.client.summoner.model.geo.GeoAnimationManager;
import org.confluence.mod.client.summoner.model.geo.GeoModelManager;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.client.PortRegisterClientReloadListenersEvent;

public final class SummonerClientEvents {
    private SummonerClientEvents() {
    }

    public static void init() {
        PortEventHandler.addListener(SummonerClientEvents::renderLevel);
        PortEventHandler.addListener(SummonerClientEvents::registerClientReloadListeners);
    }

    private static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null) {
                return;
            }
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            AttachmentEntityRenderDispatcher.render(level, event.getCamera(), event.getPoseStack(), bufferSource, event.getPartialTick());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            DynamicLightDispatcher.update(event.getLevelRenderer());
        }
    }

    private static void registerClientReloadListeners(PortRegisterClientReloadListenersEvent event) {
        event.registerReloadListener(GeoModelManager.INSTANCE);
        event.registerReloadListener(GeoAnimationManager.INSTANCE);
        event.registerReloadListener(BBModelManager.INSTANCE);
    }
}
