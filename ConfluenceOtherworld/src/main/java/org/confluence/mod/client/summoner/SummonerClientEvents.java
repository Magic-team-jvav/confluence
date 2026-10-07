package org.confluence.mod.client.summoner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.info.InfoRenderDispatcher;
import org.confluence.mod.client.summoner.particle.GenericParticleProvider;
import org.confluence.mod.client.summoner.particle.ZenithParticleProvider;
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

public final class SummonerClientEvents {

    /// `FMLClientSetupEvent` / `EntityRenderersEvent.AddLayers` 是**模组总线**（从 `@Mod` 构造器
    /// 拿到的 `IEventBus`），`ItemTooltipEvent` / `RenderLevelStageEvent` 是**游戏总线**
    /// （`NeoForge.EVENT_BUS`）。写错能编译但静默不触发。
    public static void init(IEventBus modBus) {
        modBus.addListener((RegisterParticleProvidersEvent event) -> {
            event.registerSpriteSet(SummonerParticleTypes.GENERIC.get(), GenericParticleProvider::new);
            event.registerSpriteSet(SummonerParticleTypes.ZENITH.get(), ZenithParticleProvider::new);
        });
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> {
            Player player = event.getEntity();
            ItemStack itemStack = event.getItemStack();
            if (player != null && itemStack.getItem() instanceof SummonerWeaponItem<?> summonerWeaponItem) {
                event.getToolTip().addAll(summonerWeaponItem.getTooltips(itemStack, player));
            }
        });
        NeoForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            ClientLevel level = minecraft.level;
            // 取游戏刻插值进度：`getPartialTick()` 本身不再是 float。
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            if (player != null && event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                WhipMarkTracker tracker = player.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                LivingEntity target = tracker.getMarkTarget();
                if (target != null) {
                    Vec3 targetPos = target.getPosition(partialTick).add(0, target.getBbHeight() + 0.25, 0);
                    MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                    RenderUtil.renderImageInWorld(Confluence.asResource("textures/summon_mark.png"), targetPos, event.getPoseStack(), 0.25F, 0.25F, bufferSource, true, FastColor.ARGB32.color(191, 255, 255, 255));
                }
            }
            if (level != null && event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                AttachmentEntityRenderDispatcher.render(level, event.getCamera(), event.getPoseStack(), bufferSource, partialTick);
                InfoRenderDispatcher.render(level, event.getCamera(), event.getPoseStack(), bufferSource, partialTick);
            }
        });
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.FINCH.get(), new FinchRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.HORNET.get(), new HornetRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.HORNET_STINGER.get(), new HornetStingerRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.IRON_GOLEM.get(), new IronGolemRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.SCULK_WISP.get(), new SculkWispRenderer());
            AttachmentEntityRenderDispatcher.register(SummonerAttachmentEntityTypes.IMP.get(), new ImpRenderer());
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
        modBus.addListener((EntityRenderersEvent.AddLayers event) -> {
            for (PlayerSkin.Model skin : PlayerSkin.Model.values()) {
                PlayerRenderer playerRenderer = event.getSkin(skin);
                if (playerRenderer != null) {
                    playerRenderer.addLayer(new BirdNestLayer(playerRenderer));
                }
            }
        });
    }
}
