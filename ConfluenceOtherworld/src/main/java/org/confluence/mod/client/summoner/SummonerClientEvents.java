package org.confluence.mod.client.summoner;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.info.InfoRenderDispatcher;
import org.confluence.mod.client.particle.GenericParticle;
import org.confluence.mod.client.particle.ZenithParticle;
import org.confluence.mod.client.summoner.renderer.layer.BirdNestLayer;
import org.confluence.mod.client.summoner.renderer.minion.*;
import org.confluence.mod.client.summoner.renderer.projectile.EyeFireballRenderer;
import org.confluence.mod.client.summoner.renderer.projectile.HornetStingerRenderer;
import org.confluence.mod.client.summoner.renderer.projectile.ImpFireballRenderer;
import org.confluence.mod.client.summoner.renderer.projectile.ZenithRenderer;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.confluence.mod.common.summoner.attachment.WhipMarkTracker;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.register.SummonerParticleTypes;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.client.PortEntityRenderersEvent;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class SummonerClientEvents {

    public static void init() {
        PortEventHandler.addListener(SummonerClientEvents::shaders);
        PortEventHandler.addListener(PortEventPriority.LOWEST, SummonerClientEvents::renderAttachments);
        PortEventHandler.addListener((RegisterParticleProvidersEvent event) -> {
            event.registerSpriteSet(SummonerParticleTypes.GENERIC.get(), GenericParticle.GenericParticleProvider::new);
            event.registerSpriteSet(SummonerParticleTypes.ZENITH.get(), ZenithParticle.ZenithParticleProvider::new);
        });
        PortEventHandler.addListener(PortEventPriority.HIGH, (ItemTooltipEvent event) -> {
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
                    RenderUtil.renderImageInWorld(Confluence.asResource("textures/summon_mark.png"), targetPos, event.getPoseStack(), 0.25F, 0.25F, bufferSource, true, FastColor.ARGB32.color(191, 255, 255, 255));
                }
            }
            if (level != null && event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                InfoRenderDispatcher.render(level, event.getCamera(), event.getPoseStack(), bufferSource, event.getPartialTick());
            }
        });
        PortEventHandler.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.FINCH.get(), new FinchRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.HORNET.get(), new HornetRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.HORNET_STINGER.get(), new HornetStingerRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.IRON_GOLEM.get(), new IronGolemRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.SCULK_WISP.get(), new SculkWispRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.IMP.get(), new ImpRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.VEX_TOTEM.get(), new VexTotemRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.IMP_FIREBALL.get(), new ImpFireballRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.DEADLY_SPHERE.get(), new DeadlySphereRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.SANGUINE_BAT.get(), new SanguineBatRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.TERRAPRISMA.get(), new TerraprismaRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.SLIME.get(), new SlimeMinionRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.SNOW_FLINX.get(), new SnowFlinxRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.VAMPIRE_FROG.get(), new VampireFrogRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.DESERT_TIGER.get(), new DesertTigerRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.SPIDER.get(), new SpiderRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.RUIN_RELIC.get(), new RuinRelicRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.EYE_LASER_TURRET.get(), new EyeLaserTurretRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.EYE_FIREBALL.get(), new EyeFireballRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.ZENITH.get(), new ZenithRenderer());
        }));
        PortEventHandler.addListener((PortEntityRenderersEvent.AddLayers event) -> {
            for (PortEntityRenderersEvent.AddLayers.Model skin : PortEntityRenderersEvent.AddLayers.Model.values()) {
                PlayerRenderer playerRenderer = event.getSkin(skin);
                if (playerRenderer != null) {
                    playerRenderer.addLayer(new BirdNestLayer(playerRenderer));
                }
            }
        });
    }

    private static void shaders(RegisterShadersEvent event) {
        AfterimageManager.close();
        WholeModelTransparency.close();
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), Confluence.asResource("model_transparency"), DefaultVertexFormat.PARTICLE), WholeModelTransparency::setShader);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static void renderAttachments(RenderLevelStageEvent event) {
        if (!AttachmentEntityRenderDispatcher.isShadowPass()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES && minecraft.level != null) {
                MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
                AfterimageManager.render(minecraft.level, event.getCamera(), event.getPoseStack(), event.getPartialTick());
                AttachmentEntityRenderDispatcher.render(minecraft.level, event.getCamera(), event.getPoseStack(), buffers, event.getPartialTick());
                buffers.endBatch();
            } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                WholeModelTransparency.finishFrame();
            }
        }
    }
}
