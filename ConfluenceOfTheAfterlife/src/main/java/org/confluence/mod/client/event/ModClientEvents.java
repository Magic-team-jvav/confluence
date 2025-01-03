package org.confluence.mod.client.event;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.connected.CustomBlockModels;
import org.confluence.mod.client.connected.ModConnectives;
import org.confluence.mod.client.connected.ModelSwapper;
import org.confluence.mod.client.gui.AchievementToast;
import org.confluence.mod.client.gui.container.*;
import org.confluence.mod.client.gui.hud.ArrowInBowHud;
import org.confluence.mod.client.gui.hud.HealthHudLayer;
import org.confluence.mod.client.gui.hud.ManaHudLayer;
import org.confluence.mod.client.model.block.*;
import org.confluence.mod.client.model.entity.FallingStarRenderer;
import org.confluence.mod.client.model.entity.bomb.*;
import org.confluence.mod.client.model.entity.fishing.BaseFishingHookModel;
import org.confluence.mod.client.model.entity.fishing.BloodyFishingHookModel;
import org.confluence.mod.client.model.entity.fishing.GlowingFishingHookModel;
import org.confluence.mod.client.model.entity.fishing.HotlineFishingHookModel;
import org.confluence.mod.client.model.entity.hook.BaseHookModel;
import org.confluence.mod.client.model.entity.hook.SkeletronHandModel;
import org.confluence.mod.client.model.entity.hook.WebSlingerModel;
import org.confluence.mod.client.model.entity.projectile.*;
import org.confluence.mod.client.particle.BiomeColorParticle;
import org.confluence.mod.client.particle.DamageIndicatorParticle;
import org.confluence.mod.client.particle.SimpleTextureSheetParticle;
import org.confluence.mod.client.renderer.block.*;
import org.confluence.mod.client.renderer.entity.EmptyEntityRenderer;
import org.confluence.mod.client.renderer.entity.fishing.BaseFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.BloodyFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.GlowingFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.HotlineFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.hook.*;
import org.confluence.mod.client.renderer.entity.projectile.*;
import org.confluence.mod.client.renderer.entity.projectile.bomb.*;
import org.confluence.mod.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.mod.client.textures.GrayBlockModelSwapper;
import org.confluence.mod.client.textures.GraySpriteShifterEntry;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.confluence.mod.common.init.item.BowItems;
import org.confluence.mod.common.init.item.FishingPoleItems;
import org.confluence.mod.common.init.item.MaterialItems;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.Map;

import static org.confluence.mod.common.init.ModEntities.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = Confluence.MODID)
public final class ModClientEvents {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientConfigs.onLoad();
            BowItems.registerProperties();
            FishingPoleItems.registerCast();
            ArrowInBowHud.initAdaptionMap();
            AchievementToast.registerAll();

            ModClientSetups.registerItemProperties();
            ModClientSetups.setRenderLayers();
            ModClientSetups.eventBus(ModConnectives::register);
        });
    }

    @SubscribeEvent
    public static void modConfig$Reloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(Confluence.MODID)) {
            ClientConfigs.onLoad();
        }
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SKY_MILL.get(), SkyMillScreen::new);
        event.register(ModMenuTypes.HEAVY_WORK_BENCH.get(), HeavyWorkBenchScreen::new);
        event.register(ModMenuTypes.HELLFORGE.get(), HellforgeScreen::new);
        event.register(ModMenuTypes.FLETCHING_TABLE.get(), FletchingTableScreen::new);
        event.register(ModMenuTypes.ALCHEMY_TABLE.get(), AlchemyTableScreen::new);
        event.register(ModMenuTypes.EXTRA_INVENTORY.get(), ExtraInventoryScreen::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.SELECTED_ITEM_NAME, Confluence.asResource("mana_hud"), new ManaHudLayer());
        event.registerAboveAll(Confluence.asResource("health_hud"), new HealthHudLayer());
    }

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BaseBombEntityModel.LAYER_LOCATION, BaseBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(BouncyBombEntityModel.LAYER_LOCATION, BouncyBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(ScarabBombEntityModel.LAYER_LOCATION, ScarabBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(StickyBombEntityModel.LAYER_LOCATION, StickyBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(BombFishEntityModel.LAYER_LOCATION, BombFishEntityModel::createBodyLayer);

        event.registerLayerDefinition(BaseFishingHookModel.WOOD, BaseFishingHookModel::createWoodLayer);
        event.registerLayerDefinition(BaseFishingHookModel.REINFORCED, BaseFishingHookModel::createReinforcedLayer);
        event.registerLayerDefinition(BaseFishingHookModel.FISHER_OF_SOULS, BaseFishingHookModel::createFisherOfSoulsLayer);
        event.registerLayerDefinition(BaseFishingHookModel.FLESHCATCHER, BaseFishingHookModel::createFleshcatcherLayer);
        event.registerLayerDefinition(BaseFishingHookModel.SCARAB, BaseFishingHookModel::createScarabLayer);
        event.registerLayerDefinition(BloodyFishingHookModel.LAYER_LOCATION, BloodyFishingHookModel::createBodyLayer);
        event.registerLayerDefinition(BaseFishingHookModel.FIBERGLASS, BaseFishingHookModel::createFiberglassLayer);
        event.registerLayerDefinition(BaseFishingHookModel.MECHANICS, BaseFishingHookModel::createMechanicsLayer);
        event.registerLayerDefinition(BaseFishingHookModel.SITTING_DUCKS, BaseFishingHookModel::createSittingDucksLayer);
        event.registerLayerDefinition(HotlineFishingHookModel.LAYER_LOCATION, HotlineFishingHookModel::createBodyLayer);
        event.registerLayerDefinition(BaseFishingHookModel.GOLDEN, BaseFishingHookModel::createGoldenLayer);
        event.registerLayerDefinition(GlowingFishingHookModel.MOSS, GlowingFishingHookModel::createMossLayer);
        event.registerLayerDefinition(GlowingFishingHookModel.COMMON, GlowingFishingHookModel::createCommonLayer);
        event.registerLayerDefinition(GlowingFishingHookModel.GLOWING, GlowingFishingHookModel::createGlowingLayer);

        event.registerLayerDefinition(IceBladeSwordProjectileModel.LAYER_LOCATION, IceBladeSwordProjectileModel::createBodyLayer);
        event.registerLayerDefinition(EnchantedSwordProjectileModel.LAYER_LOCATION, EnchantedSwordProjectileModel::createBodyLayer);
        event.registerLayerDefinition(BoulderModel.LAYER_LOCATION, BoulderModel::createBodyLayer);
        event.registerLayerDefinition(ShurikenProjectileModel.LAYER_LOCATION, ShurikenProjectileModel::createBodyLayer);
        event.registerLayerDefinition(ThrownKnivesProjectileModel.LAYER_LOCATION, ThrownKnivesProjectileModel::createBodyLayer);

        event.registerLayerDefinition(BaseHookModel.LAYER_LOCATION, BaseHookModel::createBodyLayer);
        event.registerLayerDefinition(WebSlingerModel.LAYER_LOCATION, WebSlingerModel::createBodyLayer);
        event.registerLayerDefinition(SkeletronHandModel.LAYER_LOCATION, SkeletronHandModel::createBodyLayer);
        /* todo 静止钩 */

        event.registerLayerDefinition(WeatherVaneBlockModel.LAYER_LOCATION, WeatherVaneBlockModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BOMB_ENTITY.get(), BaseBombEntityRenderer::new);
        event.registerEntityRenderer(BOUNCY_BOMB_ENTITY.get(), BouncyBombEntityRenderer::new);
        event.registerEntityRenderer(SCARAB_BOMB_ENTITY.get(), ScarabBombEntityRenderer::new);
        event.registerEntityRenderer(STICKY_BOMB_ENTITY.get(), StickyBombEntityRenderer::new);
        event.registerEntityRenderer(BOMB_FISH_ENTITY.get(), BombFishEntityRenderer::new);

        event.registerEntityRenderer(BASE_MANA_STAFF_PROJECTILE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(ARROW_PROJECTILE.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(EFFECT_THROWN_POTION.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ICE_BLADE_SWORD_PROJECTILE.get(), IceBladeSwordProjectileRenderer::new);
        event.registerEntityRenderer(STAR_FURY_PROJECTILE.get(), StarFuryProjectileRenderer::new);
        event.registerEntityRenderer(ENCHANTED_SWORD_PROJECTILE.get(), EnchantedSwordProjectileRenderer::new);
        event.registerEntityRenderer(BOOMERANG_PROJECTILE.get(), BoomerangProjRenderer::new);
        event.registerEntityRenderer(BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(THROWN_KNIVES_PROJECTILE.get(), ThrownKnivesProjectileRenderer::new);
        event.registerEntityRenderer(SHURIKEN_PROJECTILE.get(), ShurikenProjectileRenderer::new);

        event.registerEntityRenderer(FALLING_STAR_ITEM_ENTITY.get(), FallingStarRenderer::new);

        event.registerEntityRenderer(HOTLINE_FISHING_HOOK.get(), HotlineFishingHookRenderer::new);
        event.registerEntityRenderer(BASE_FISHING_HOOK.get(), BaseFishingHookRenderer::new);
        event.registerEntityRenderer(BLOODY_FISHING_HOOK.get(), BloodyFishingHookRenderer::new);
        event.registerEntityRenderer(CURIO_FISHING_HOOK.get(), GlowingFishingHookRenderer::new);

        event.registerEntityRenderer(BASE_HOOK.get(), BaseHookRenderer::new);
        event.registerEntityRenderer(WEB_SLINGER.get(), WebSlingerRenderer::new);
        event.registerEntityRenderer(SKELETRON_HAND.get(), SkeletronHandRenderer::new);
        event.registerEntityRenderer(SLIME_HOOK.get(), SlimeHookRenderer::new);
        event.registerEntityRenderer(FISH_HOOK.get(), FishHookRenderer::new);
        event.registerEntityRenderer(IVY_WHIP.get(), IvyWhipRenderer::new);
        event.registerEntityRenderer(BAT_HOOK.get(), BatHookRenderer::new);
        event.registerEntityRenderer(CANDY_CANE_HOOK.get(), CandyCaneHookRenderer::new);
        event.registerEntityRenderer(DUAL_HOOK.get(), DualHookRenderer::new);
        event.registerEntityRenderer(HOOK_OF_DISSONANCE.get(), HookOfDissonanceRenderer::new);
        event.registerEntityRenderer(THORN_HOOK.get(), ThornHookRenderer::new);
        event.registerEntityRenderer(MIMIC_HOOK.get(), MimicHookRenderer::new);
        event.registerEntityRenderer(ANTI_GRAVITY_HOOK.get(), AntiGravityHookRenderer::new);
        event.registerEntityRenderer(SPOOKY_HOOK.get(), SpookyHookRenderer::new);
        event.registerEntityRenderer(CHRISTMAS_HOOK.get(), ChristmasHookRenderer::new);
        event.registerEntityRenderer(LUNAR_HOOK.get(), LunarHookRenderer::new);
        /* todo 静止钩 */

        event.registerEntityRenderer(WOODEN_MINECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART)); // todo 模型
        event.registerEntityRenderer(GENERIC_MINECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(MECHANICAL_CART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(MINECARP.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(DEMONIC_HELLCART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(MEOWMERE_MINECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(DIGGING_MOLECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));

        event.registerBlockEntityRenderer(ModBlocks.SIGN_BLOCK_ENTITY.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new AltarBlockModel()));
        event.registerBlockEntityRenderer(FunctionalBlocks.SKY_MILL_ENTITY.get(), context -> new SkyMillBlockRenderer());
        event.registerBlockEntityRenderer(FunctionalBlocks.EXTRACTINATOR_ENTITY.get(), context -> new ExtractinatorBlockRenderer());
        event.registerBlockEntityRenderer(FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), MechanicalBlockRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.SILLY_BALLOON_MACHINE_ENTITY.get(), MechanicalBlockRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.BASE_CHEST_BLOCK_ENTITY.get(), BaseChestBlockRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.DEATH_CHEST_BLOCK_ENTITY.get(), DeathChestBlockRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.WEATHER_VANE_ENTITY.get(), WeatherVaneBlockRenderer::new);
        event.registerBlockEntityRenderer(NatureBlocks.LIFE_CRYSTAL_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new LifeCrystalBlockModel()));
        event.registerBlockEntityRenderer(StatueBlocks.BLOCK_ENTITY.get(), MechanicalBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(ModClientSetups.HALLOW_LEAVES_COLOR, NatureBlocks.PEARL_LOG_BLOCKS.getLeaves().get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(ModClientSetups.SIMPLE, MaterialItems.GEL.get());
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(ModClientSetups.HONEY_CLIENT_EXTENSIONS, ModFluids.HONEY.type());
        event.registerFluidType(ModClientSetups.SHIMMER_CLIENT_EXTENSIONS, ModFluids.SHIMMER.type());

        event.registerItem(ModClientSetups.ALTAR_CLIENT_EXTENSIONS, FunctionalBlocks.DEMON_ALTAR.asItem(), FunctionalBlocks.CRIMSON_ALTAR.asItem());
        event.registerItem(new SimpleGeoItemRenderer<>(LifeCrystalBlockModel.MODEL, LifeCrystalBlockModel.TEXTURE, null), NatureBlocks.LIFE_CRYSTAL_BLOCK.asItem());
        event.registerItem(new SimpleGeoItemRenderer<>(SkyMillBlockModel.MODEL, SkyMillBlockModel.TEXTURE, SkyMillBlockModel.ANIMATION), FunctionalBlocks.SKY_MILL.asItem());
        event.registerItem(new SimpleGeoItemRenderer<>(ExtractinatorBlockModel.MODEL, ExtractinatorBlockModel.TEXTURE, ExtractinatorBlockModel.ANIMATION), FunctionalBlocks.EXTRACTINATOR.asItem());
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticleTypes.DAMAGE_INDICATOR.get(), new DamageIndicatorParticle.Provider());
        event.registerSpriteSet(ModParticleTypes.LEAVES.get(), BiomeColorParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.RED_SAND.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SAND.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SNOW.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.YELLOW_WILLOW.get(), SimpleTextureSheetParticle.Provider::new);
    }

    @SubscribeEvent
    public static void textureAtlasStitched(TextureAtlasStitchedEvent event) {
        TextureAtlas atlas = event.getAtlas();
        if (atlas.location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            for (Map.Entry<ResourceLocation, TextureAtlasSprite> entry : atlas.getTextures().entrySet()) {
                ResourceLocation key = entry.getKey();
                if (!key.getPath().endsWith(".gray")) {
                    ResourceLocation gray = key.withSuffix(".gray");
                    GraySpriteShifterEntry.ALL.put(key, new GraySpriteShifterEntry(entry.getValue(), atlas.getSprite(gray)));
                }
            }
        }
    }

    @SubscribeEvent
    public static void model$ModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        CustomBlockModels customBlockModels = ModConnectives.MODEL_SWAPPER.getCustomBlockModels();
        BuiltInRegistries.BLOCK.stream().forEach(block -> {
            if (customBlockModels.containsBlock(block)) return;
            for (ModelResourceLocation modelLocation : ModelSwapper.getAllBlockStateModelLocations(block)) {
                BakedModel bakedModel = modelRegistry.get(modelLocation);
                if (bakedModel != null) {
                    modelRegistry.put(modelLocation, new GrayBlockModelSwapper(bakedModel));
                }
            }
        });
    }
}
