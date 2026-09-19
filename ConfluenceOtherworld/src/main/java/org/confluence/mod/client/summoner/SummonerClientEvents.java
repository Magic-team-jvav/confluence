package org.confluence.mod.client.summoner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.renderer.minion.FinchRenderer;
import org.confluence.mod.client.summoner.renderer.minion.HornetRenderer;
import org.confluence.mod.client.summoner.renderer.minion.IronGolemRenderer;
import org.confluence.mod.client.summoner.renderer.minion.SculkWispRenderer;
import org.confluence.mod.client.summoner.renderer.layer.BirdNestLayer;
import org.confluence.mod.client.summoner.renderer.projectile.HornetStingerRenderer;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.attachment.WhipMarkTracker;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.client.PortEntityRenderersEvent;
import org.mesdag.portlib.event.entity.player.PortItemTooltipEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLClientSetupEventPort;

public final class SummonerClientEvents {

    public static void init() {
        PortEventHandler.addListener((PortItemTooltipEvent event) -> {
            Player player = event.getEntity();
            ItemStack itemStack = event.getItemStack();
            if (player != null && itemStack.getItem() instanceof SummonerWeaponItem<?> summonerWeaponItem) {
                event.getToolTip().addAll(summonerWeaponItem.getTooltips(itemStack, player));
            }
        });
        PortEventHandler.addListener((RenderLevelStageEvent event) -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            ClientLevel level = minecraft.level;
            if (player != null && event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                WhipMarkTracker tracker = player.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
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
        });
        PortEventHandler.addListener((PortFMLClientSetupEventPort event) -> event.enqueueWork(() -> {
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.FINCH.get(), new FinchRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.HORNET.get(), new HornetRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.HORNET_STINGER.get(), new HornetStingerRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.IRON_GOLEM.get(), new IronGolemRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.SCULK_WISP.get(), new SculkWispRenderer());
        }));
        PortEventHandler.addListener((PortEntityRenderersEvent.AddLayers event) -> {
            for (PortEntityRenderersEvent.AddLayers.PortModel skin : PortEntityRenderersEvent.AddLayers.PortModel.values()) {
                PlayerRenderer playerRenderer = event.getSkin(skin);
                if (playerRenderer != null) {
                    playerRenderer.addLayer(new BirdNestLayer(playerRenderer));
                }
            }
        });
    }
}
