package org.confluence.terraentity.client.event;

import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.attachment.ItemInHandTrailAttachment;
import org.confluence.terraentity.client.block.renderer.FigureBlockRenderer;
import org.confluence.terraentity.client.entity.model.*;
import org.confluence.terraentity.client.entity.renderer.mob.ReplacedSpiderRenderer;
import org.confluence.terraentity.client.gui.container.SimpleTradeScreen;
import org.confluence.terraentity.client.init.model.AdditionalItemRegister;
import org.confluence.terraentity.client.init.model.EntityBlockModelRegister;
import org.confluence.terraentity.client.init.model.WhipModelRegister;
import org.confluence.terraentity.client.particle.BiomeColorParticle;
import org.confluence.terraentity.client.particle.SpitParticle;
import org.confluence.terraentity.config.ClientConfig;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TEMenus;
import org.confluence.terraentity.init.TEParticles;
import org.confluence.terraentity.init.block.TEFigureBlocks;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.integration.ModChecker;
import org.confluence.terraentity.integration.sodium_dynamic_light.SDHelper;

import static org.confluence.terraentity.client.util.RegisterUtils.registerModel;


@EventBusSubscriber(modid = TerraEntity.MODID, value = Dist.CLIENT)
public final class ModClientEvent {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientConfig.load();
            ItemInHandTrailAttachment.registerDefault();
            if(ModChecker.sodiumdynamiclights.isLoaded()) {
                SDHelper.registerDynamicLight();
            }
        });
    }

    @SubscribeEvent
    public static void configChanged(ModConfigEvent event) {
        if(event.getConfig().getSpec() == ClientConfig.SPEC){
            ClientConfig.load();
        }
    }



    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        event.registerLayerDefinition(CrownOfKingSlimeModel.LAYER_LOCATION, CrownOfKingSlimeModel::createBodyLayer);
        registerModel(event, CrownOfKingSlimeModel.class);
        registerModel(event, CabbageProjModel.class);
        registerModel(event, Stinger.class);
        registerModel(event, HarpyFeatherProjectileModel.class);
        registerModel(event, DemonScytheModel.class);
//        registerModel(event, TerraprismaModel.class);
        event.registerLayerDefinition(TerraprismaModel.LAYER_LOCATION, TerraprismaModel::createBodyLayer);
        registerModel(event, BeeProjModel.class);
        registerModel(event, SlimeSpikedProjectlieModel.class);
        registerModel(event, JungleSpikedProjectlieModel.class);
        registerModel(event, IceSpikeProjectileModel.class);


    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        TEEntities.registerEntityRenderers(event);

        // replaced
        if (ClientConfig.ENABLE_NON_SPIDER_MODEL.get()) {
            event.registerEntityRenderer(EntityType.SPIDER, c -> new ReplacedSpiderRenderer<>(c, "spider", EntityType.SPIDER));
            event.registerEntityRenderer(EntityType.CAVE_SPIDER, c -> new ReplacedSpiderRenderer<>(c, "cave_spider", EntityType.CAVE_SPIDER));
            event.registerEntityRenderer(TEMonsterEntities.BLOOD_CRAWLER.get(), c -> new ReplacedSpiderRenderer<>(c, "blood_crawler", TEMonsterEntities.BLOOD_CRAWLER.get()));
        }

        event.registerBlockEntityRenderer(TEFigureBlocks.FIGURE_BLOCK_ENTITY.get(), FigureBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TEParticles.LEAVES.get(), BiomeColorParticle.Provider::new);
        event.registerSpriteSet(TEParticles.SPIT.get(), SpitParticle.Provider::new);
        event.registerSpriteSet(TEParticles.SPIT_GLOW.get(), SpitParticle.EmissiveProvider::new);
        event.registerSpriteSet(TEParticles.FIRE_BOUND.get(), SpitParticle.EmissiveProvider::new);
    }

    @SubscribeEvent
    public static void registerAdditionalModel(ModelEvent.RegisterAdditional event) {
        WhipModelRegister.getInstance().register(event);
        EntityBlockModelRegister.getInstance().register(event);
        AdditionalItemRegister.getInstance().register(event);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TEMenus.SIMPLE_NPC_TRADES_MENU.get(), SimpleTradeScreen::new);

    }



//    @SubscribeEvent
//    public static void registerTextureAtlasSpriteLoaders(RegisterTextureAtlasSpriteLoadersEvent event) {
//        event.registerEgg("still_fluid", new ITextureAtlasSpriteLoader() {
//            @Override
//            public SpriteContents loadContents(ResourceLocation name, Resource resource, FrameSize frameSize, NativeImage image, AnimationMetadataSection animationMeta, ForgeTextureMetadata forgeMeta) {
//                return new SpriteContents(name, frameSize, image, animationMeta, forgeMeta);
//            }
//
//            @Override
//            public @NotNull TextureAtlasSprite makeSprite(ResourceLocation atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel) {
//                return new TextureAtlasSprite(atlasName, contents, 16, 512, 0, 0) {
//                };
//            }
//        });
//    }


}
