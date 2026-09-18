package org.confluence.mod.client.summoner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.model.bbmodel.BBModelManager;
import org.confluence.mod.client.summoner.model.geo.GeoAnimationManager;
import org.confluence.mod.client.summoner.model.geo.GeoModelManager;
import org.confluence.mod.common.summoner.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.attachment.WhipTracker;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.client.PortRegisterClientReloadListenersEvent;

public final class SummonerClientEvents {

    public static void init() {
        PortEventHandler.addListener((RenderLevelStageEvent event) -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            ClientLevel level = minecraft.level;
            if (player != null && event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                WhipTracker tracker = player.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                LivingEntity target = tracker.getMarkTarget();
                if (target != null) {
                    float partialTick = event.getPartialTick();
                    Vec3 targetPos = target.getPosition(partialTick).add(0, target.getBbHeight() + 0.25, 0);
                    MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                    RenderUtil.renderImage(Confluence.asResource("textures/summon_mark.png"), targetPos, 0.25F, 0.25F, bufferSource, true, FastColor.ARGB32.color(191, 255, 255, 255));
                }
            }
            if (level != null && event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                AttachmentEntityRenderDispatcher.render(level, event.getCamera(), event.getPoseStack(), bufferSource, event.getPartialTick());
            }
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                DynamicLightDispatcher.update(event.getLevelRenderer());
            }
        });
        PortEventHandler.addListener((PortRegisterClientReloadListenersEvent event) -> {
            event.registerReloadListener(GeoModelManager.INSTANCE);
            event.registerReloadListener(GeoAnimationManager.INSTANCE);
            event.registerReloadListener(BBModelManager.INSTANCE);
        });
    }
}
