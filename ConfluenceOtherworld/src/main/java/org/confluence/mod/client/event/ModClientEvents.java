package org.confluence.mod.client.event;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.connected.CustomBlockModels;
import org.confluence.mod.client.connected.ModConnectives;
import org.confluence.mod.client.connected.ModelSwapper;
import org.confluence.mod.client.gui.container.*;
import org.confluence.mod.client.gui.hud.*;
import org.confluence.mod.client.model.block.AltarBlockModel;
import org.confluence.mod.client.model.block.LifeCrystalBlockModel;
import org.confluence.mod.client.model.block.WeatherVaneBlockModel;
import org.confluence.mod.client.model.entity.TargetDummyModel;
import org.confluence.mod.client.model.entity.bomb.*;
import org.confluence.mod.client.model.entity.fishing.BaseFishingHookModel;
import org.confluence.mod.client.model.entity.fishing.BloodyFishingHookModel;
import org.confluence.mod.client.model.entity.fishing.GlowingFishingHookModel;
import org.confluence.mod.client.model.entity.fishing.HotlineFishingHookModel;
import org.confluence.mod.client.model.entity.hook.BaseHookModel;
import org.confluence.mod.client.model.entity.hook.SkeletronHandModel;
import org.confluence.mod.client.model.entity.hook.WebSlingerModel;
import org.confluence.mod.client.model.entity.projectile.*;
import org.confluence.mod.client.particle.*;
import org.confluence.mod.client.renderer.block.*;
import org.confluence.mod.client.renderer.entity.BodyPartRenderer;
import org.confluence.mod.client.renderer.entity.FallingStarRenderer;
import org.confluence.mod.client.renderer.entity.TargetDummyRenderer;
import org.confluence.mod.client.renderer.entity.TreasureBagRenderer;
import org.confluence.mod.client.renderer.entity.fishing.BaseFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.BloodyFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.GlowingFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.HotlineFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.hook.*;
import org.confluence.mod.client.renderer.entity.projectile.*;
import org.confluence.mod.client.renderer.entity.projectile.bomb.*;
import org.confluence.mod.client.renderer.entity.projectile.sword.ForwardProjRenderer;
import org.confluence.mod.client.renderer.entity.projectile.sword.LightsBaneProjectileRenderer;
import org.confluence.mod.client.renderer.entity.projectile.sword.NightEdgeProjectileRenderer;
import org.confluence.mod.client.renderer.entity.projectile.sword.StarFuryProjectileRenderer;
import org.confluence.mod.client.textures.GrayBlockModelSwapper;
import org.confluence.mod.client.textures.GraySpriteShifterEntry;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.common.item.vanity_armor.BaseDyeItem;
import org.confluence.mod.integration.appleskin.AppleskinHelper;
import org.confluence.mod.integration.create.ponder.PonderHelper;
import org.confluence.mod.integration.sodium.SodiumDynamicLightHelper;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.util.ClientUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.model.entity.BeeProjectileModel;
import org.confluence.terra_guns.util.TGUtil;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

            ModClientSetups.registerItemProperties();
            ModClientSetups.setRenderLayers();
            ModClientSetups.eventBus(ModConnectives::register);

            PonderHelper.registerPlugin();
            AppleskinHelper.addListeners();
            SodiumDynamicLightHelper.registerDynamicLight();
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
        event.register(ModMenuTypes.COOKING_POT.get(), CookingPotScreen::new);
        event.register(ModMenuTypes.SAWMILL.get(), SawmillScreen::new);
        event.register(ModMenuTypes.SOLIDIFIER.get(), SolidifierScreen::new);
        event.register(ModMenuTypes.CRYSTAL_BALL.get(), CrystalBallScreen::new);
        event.register(ModMenuTypes.HARDMODE_ANVIL.get(), HardmodeAnvilScreen::new);
        event.register(ModMenuTypes.HARDMODE_FORGE.get(), HardmodeForgeScreen::new);

        event.register(ModMenuTypes.NPC_TRADES_MENU.get(), WithForgeTradeScreen::new);
        event.register(ModMenuTypes.REFORGE_MENU.get(), NPCReforgeScreen::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        ResourceLocation healthHud = Confluence.asResource("health_hud");
        event.registerBelow(VanillaGuiLayers.ARMOR_LEVEL, healthHud, new TerraStyleHealthHud());
        ResourceLocation armorHud = Confluence.asResource("armor_hud");
        event.registerAbove(healthHud, armorHud, new TerraStyleArmorHud());
        ResourceLocation manaHud = Confluence.asResource("mana_hud");
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, manaHud, new TerraStyleManaHud());
        ResourceLocation foodHud = Confluence.asResource("food_hud");
        event.registerBelow(manaHud, foodHud, new TerraStyleFoodHud());
    }

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BaseBombEntityModel.LAYER_LOCATION, BaseBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(BouncyBombEntityModel.LAYER_LOCATION, BouncyBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(ScarabBombEntityModel.LAYER_LOCATION, ScarabBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(StickyBombEntityModel.LAYER_LOCATION, StickyBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(BombFishEntityModel.LAYER_LOCATION, BombFishEntityModel::createBodyLayer);
        event.registerLayerDefinition(DirtBombEntityModel.LAYER_LOCATION, DirtBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(StickyDirtBombEntityModel.LAYER_LOCATION, StickyDirtBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(BaseDynamiteEntityModel.LAYER_LOCATION, BaseDynamiteEntityModel::createBodyLayer);
        event.registerLayerDefinition(StickyDynamiteEntityModel.LAYER_LOCATION, StickyDynamiteEntityModel::createBodyLayer);
        event.registerLayerDefinition(BouncyDynamiteEntityModel.LAYER_LOCATION, BouncyDynamiteEntityModel::createBodyLayer);
        event.registerLayerDefinition(BaseGrenadeEntityModel.LAYER_LOCATION, BaseGrenadeEntityModel::createBodyLayer);
        event.registerLayerDefinition(DryBombEntityModel.LAYER_LOCATION, DryBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(WetBombEntityModel.LAYER_LOCATION, WetBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(LavaBombEntityModel.LAYER_LOCATION, LavaBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(HoneyBombEntityModel.LAYER_LOCATION, HoneyBombEntityModel::createBodyLayer);
        event.registerLayerDefinition(StickyGrenadeEntityModel.LAYER_LOCATION, StickyGrenadeEntityModel::createBodyLayer);
        event.registerLayerDefinition(BouncyGrenadeEntityModel.LAYER_LOCATION, BouncyGrenadeEntityModel::createBodyLayer);
        event.registerLayerDefinition(BeenadeEntityModel.LAYER_LOCATION, BeenadeEntityModel::createBodyLayer);

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

        event.registerLayerDefinition(TargetDummyModel.LAYER_LOCATION, TargetDummyModel::createMesh);

        event.registerLayerDefinition(IceBladeSwordProjectileModel.LAYER_LOCATION, IceBladeSwordProjectileModel::createBodyLayer);
        event.registerLayerDefinition(EnchantedSwordProjectileModel.LAYER_LOCATION, EnchantedSwordProjectileModel::createBodyLayer);
        event.registerLayerDefinition(ShurikenProjectileModel.LAYER_LOCATION, ShurikenProjectileModel::createBodyLayer);
        event.registerLayerDefinition(ThrownKniveProjectileModel.LAYER_LOCATION, ThrownKniveProjectileModel::createBodyLayer);
        event.registerLayerDefinition(BoneThrownKnivesProjectileModel.LAYER_LOCATION, BoneThrownKnivesProjectileModel::createBodyLayer);
        event.registerLayerDefinition(FrostDaggerfishProjectileModel.LAYER_LOCATION, FrostDaggerfishProjectileModel::createBodyLayer);
        event.registerLayerDefinition(VilethronProjectileModel.LAYER_LOCATION, VilethronProjectileModel::createBodyLayer);
        event.registerLayerDefinition(DemonScytheProjectileModel.LAYER_LOCATION, DemonScytheProjectileModel::createBodyLayer);
        event.registerLayerDefinition(SpikyBallProjectileModel.LAYER_LOCATION, SpikyBallProjectileModel::createBodyLayer);
        event.registerLayerDefinition(HurtnadoProjectileModel.LAYER_LOCATION, HurtnadoProjectileModel::createBodyLayer);
        event.registerLayerDefinition(RollingCactusSpikeModel.LAYER_LOCATION, RollingCactusSpikeModel::createBodyLayer);

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
        event.registerEntityRenderer(SMOKE_BOMB_ENTITY.get(), NoopRenderer::new);
        event.registerEntityRenderer(BOMB_FISH_ENTITY.get(), BombFishEntityRenderer::new);
        event.registerEntityRenderer(DIRT_BOMB.get(), DirtBombEntityRenderer::new);
        event.registerEntityRenderer(STICKY_DIRT_BOMB.get(), StickyDirtBombEntityRenderer::new);
        event.registerEntityRenderer(GRENADE.get(), BaseGrenadeEntityRenderer::new);
        event.registerEntityRenderer(BOUNCY_GRENADE.get(), BouncyGrenadeEntityRenderer::new);
        event.registerEntityRenderer(STICKY_GRENADE.get(), StickyGrenadeEntityRenderer::new);
        event.registerEntityRenderer(BEENADE.get(), BeenadeEntityRenderer::new);
        event.registerEntityRenderer(DYNAMITE.get(), BaseDynamiteEntityRenderer::new);
        event.registerEntityRenderer(BOUNCY_DYNAMITE.get(), BouncyDynamiteEntityRenderer::new);
        event.registerEntityRenderer(STICKY_DYNAMITE.get(), StickyDynamiteEntityRenderer::new);
        event.registerEntityRenderer(DRY_BOMB.get(), DryBombEntityRenderer::new);
        event.registerEntityRenderer(WET_BOMB.get(), WetBombEntityRenderer::new);
        event.registerEntityRenderer(LAVA_BOMB.get(), LavaBombEntityRenderer::new);
        event.registerEntityRenderer(HONEY_BOMB.get(), HoneyBombEntityRenderer::new);

        event.registerEntityRenderer(BASE_MANA_STAFF_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(VILETHRON_PROJECTILE.get(), VilethronProjectileRenderer::new);
        event.registerEntityRenderer(HURTNADO_PROJECTILE.get(), HurtnadoProjectileRenderer::new);
        event.registerEntityRenderer(WATER_STREAM_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(WATER_BOLT_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(BALL_OF_FIRE_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EFFECT_THROWN_POTION.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ICE_BLADE_SWORD_PROJECTILE.get(), context -> new ForwardProjRenderer<>(context, new IceBladeSwordProjectileModel(context.bakeLayer(IceBladeSwordProjectileModel.LAYER_LOCATION)), Confluence.asResource("textures/entity/ice_blade_sword_projectile.png"), 1, 0F));
        event.registerEntityRenderer(STAR_FURY_PROJECTILE.get(), StarFuryProjectileRenderer::new);
        event.registerEntityRenderer(ENCHANTED_SWORD_PROJECTILE.get(), context -> new ForwardProjRenderer<>(context, new EnchantedSwordProjectileModel(context.bakeLayer(EnchantedSwordProjectileModel.LAYER_LOCATION)), Confluence.asResource("textures/entity/enchanted_sword_projectile.png"), 1, 0.2F, 0.89f));
        event.registerEntityRenderer(LIGHTS_BANE_PROJECTILE.get(), LightsBaneProjectileRenderer::new);
        event.registerEntityRenderer(GRASS_PROJECTILE.get(), context -> new ForwardProjRenderer<>(context, null, null));
        event.registerEntityRenderer(BEE_PROJECTILE.get(), context -> new ForwardProjRenderer<>(context, new BeeProjectileModel(context.bakeLayer(BeeProjectileModel.LAYER_LOCATION)), TerraCurio.asResource("textures/entity/bee_projectile.png")));
        event.registerEntityRenderer(NIGHTS_EDGE_PROJECTILE.get(), NightEdgeProjectileRenderer::new);

        event.registerEntityRenderer(ARROW_PROJECTILE.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(BEE_ARROW.get(), context -> new ForwardProjRenderer<>(context, new BeeProjectileModel(context.bakeLayer(BeeProjectileModel.LAYER_LOCATION)), TerraCurio.asResource("textures/entity/bee_projectile.png")));
        event.registerEntityRenderer(HELL_BAT_ARROW.get(), context -> new GeoArrowRenderer(context, TEMonsterEntities.HELL_BAT.getId(), 0.5f, 0));
        event.registerEntityRenderer(BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(FOLLOWER_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(EXPLODE_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(ROLLING_CACTUS_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(ROLLING_CACTUS_SPIKE.get(), RollingCactusSpikeRenderer::new);
        event.registerEntityRenderer(TOMBSTONE_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(BOULDER_3X.get(), BoulderRenderer::new);
        event.registerEntityRenderer(THROWN_KNIVE_PROJECTILE.get(), ThrownKniveProjectileRenderer::new);
        event.registerEntityRenderer(BONE_THROWN_KNIVE_PROJECTILE.get(), BoneThrownKniveProjectileRenderer::new);
        event.registerEntityRenderer(FROST_DAGGERFISH_PROJECTILE.get(), FrostDaggerfishProjectileRenderer::new);
        event.registerEntityRenderer(JAVELIN_PROJECTILE.get(), SpearRenderer::new);
        event.registerEntityRenderer(SHURIKEN_PROJECTILE.get(), ShurikenProjectileRenderer::new);
        event.registerEntityRenderer(SPIKY_BALL_PROJECTILE.get(), SpikyBallProjectileRenderer::new);
        event.registerEntityRenderer(THROWN_WATER_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(MAGIC_DAGGER_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(CRYSTAL_STORM_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(CURSED_FLAMES_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(FLOWER_PETAL_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(TITANIUM_SHARDS_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(FALLING_STAR_ITEM_ENTITY.get(), FallingStarRenderer::new);
        event.registerEntityRenderer(TREASURE_BAG_ITEM_ENTITY.get(), TreasureBagRenderer::new);
        event.registerEntityRenderer(COIN_PORTAL.get(), NoopRenderer::new);
        event.registerEntityRenderer(THROWN_POWDER.get(), NoopRenderer::new);
        event.registerEntityRenderer(ROPE_COILS.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ICE_TOFU_BRICK_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BODY_PART.get(), BodyPartRenderer::new);
        event.registerEntityRenderer(TARGET_DUMMY.get(), TargetDummyRenderer::new);
        event.registerEntityRenderer(FLAME_CLOUD.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(SUPER_SPIKY_BALL_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(SPEAR.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(BALL_OF_FROST_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(DEMON_SCYTHE_PROJECTILE.get(), DemonScytheProjectileRenderer::new);

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

        EntityRendererProvider<BaseMinecartEntity> provider = context -> new MinecartRenderer<>(context, ModelLayers.MINECART);
        event.registerEntityRenderer(WOODEN_MINECART.get(), provider); // todo 模型
        event.registerEntityRenderer(GENERIC_MINECART.get(), provider);
        event.registerEntityRenderer(MECHANICAL_CART.get(), provider);
        event.registerEntityRenderer(MINECARP.get(), provider);
        event.registerEntityRenderer(DEMONIC_HELLCART.get(), provider);
        event.registerEntityRenderer(MEOWMERE_MINECART.get(), provider);
        event.registerEntityRenderer(DIGGING_MOLECART.get(), provider);


        event.registerBlockEntityRenderer(FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new AltarBlockModel()));
        event.registerBlockEntityRenderer(FunctionalBlocks.SKY_MILL_ENTITY.get(), ClientUtils.rendererProvider(SkyMillBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.EXTRACTINATOR_ENTITY.get(), ClientUtils.rendererProvider(ExtractinatorBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.SILLY_BALLOON_MACHINE_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.WEATHER_VANE_ENTITY.get(), WeatherVaneBlockRenderer::new);
        event.registerBlockEntityRenderer(ChestBlocks.BASE_CHEST_ENTITY.get(), BaseChestBlockRenderer::new);
        event.registerBlockEntityRenderer(ChestBlocks.DEATH_CHEST_ENTITY.get(), DeathChestBlockRenderer::new);
        event.registerBlockEntityRenderer(NatureBlocks.LIFE_CRYSTAL_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new LifeCrystalBlockModel()));
        event.registerBlockEntityRenderer(StatueBlocks.BLOCK_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.COOKING_POT_ENTITY.get(), context -> new GeoBlockRenderer<>(new DefaultedBlockGeoModel<>(Confluence.asResource("cooking_pot"))));
        event.registerBlockEntityRenderer(FunctionalBlocks.ANNOUNCEMENT_BOX_ENTITY.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.SAFE_ENTITY.get(), context -> new GeoBlockRenderer<>(new DefaultedBlockGeoModel<>(Confluence.asResource("safe"))));
        event.registerBlockEntityRenderer(DecorativeBlocks.MURAL_ENTITY_BLOCK.get(), ClientUtils.rendererProvider(MuralBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.BEWITCHING_TABLE_ENTITY.get(), ClientUtils.rendererProvider(BewitchingTableBlockRenderer::new));

        WaystonesHelper.registerEntityRenderers(event);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(ModClientSetups.HALLOW_LEAVES_COLOR, NatureBlocks.PEARL_LOG_BLOCKS.getLeaves().get());
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(), NatureBlocks.BAOBAB_LOG_BLOCKS.getLeaves().get());
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor(), NatureBlocks.JUNGLE_GRASS_BLOCK.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((pStack, pTintIndex) -> ColoredItem.getColor(pStack), MaterialItems.GEL.get());
        event.register((pStack, pTintIndex) -> GrassColor.getDefaultColor(), NatureBlocks.JUNGLE_GRASS_BLOCK.get());
        event.register((stack, tintIndex) -> tintIndex == 1 && stack.getItem() instanceof PaintItem paintItem ? FastColor.ARGB32.opaque(paintItem.getColor(stack)) : 0xFFFFFFFF, PaintItems.PAINT_ITEMS.toArray(PaintItem[]::new));
        event.register((stack, tintIndex) -> tintIndex == 1 && stack.getItem() instanceof BaseDyeItem dyeItem ? dyeItem.color : 0xFFFFFFFF, VanityArmorItems.DYE_ITEMS.stream().map(DeferredHolder::get).toArray(Item[]::new));
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(ModClientSetups.HONEY_CLIENT_EXTENSIONS, ModFluids.HONEY.type());
        event.registerFluidType(ModClientSetups.SHIMMER_CLIENT_EXTENSIONS, ModFluids.SHIMMER.type());
        event.registerBlock(ModClientSetups.NO_HIT_EFFECTS, ModBlocks.ROPE.get(), ModBlocks.VINE_ROPE.get(), ModBlocks.SILK_ROPE.get(), ModBlocks.WEB_ROPE.get());
        event.registerItem(ModClientSetups.ENTITY_DISPLAY, ModItems.ENTITY_DISPLAY.get());
        event.registerItem(ModClientSetups.BREATHING_REED, SwordItems.BREATHING_REED);
        event.registerItem(ModClientSetups.LANCE, LanceItems.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));
        event.registerItem(ModClientSetups.NOOP_ITEM, SwordItems.ZOMBIE_ARM);
        TGUtil.registerOtherGunModel(event, Confluence.MODID, ManaWeaponItems.BEE_GUN);
        TGUtil.registerOtherGunModel(event, Confluence.MODID, ManaWeaponItems.SPACE_GUN);
        GunItems.ITEMS.getEntries().forEach(holder -> TGUtil.registerOtherGunModel(event, Confluence.MODID, holder));
        event.registerMobEffect(ModClientSetups.TRANSLUCENT_EFFECT_ICON, ModEffects.LUCK_EFFECT.get());
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticleTypes.DAMAGE_INDICATOR.get(), new DamageIndicatorParticle.Provider());
        event.registerSpecial(ModParticleTypes.WHOLE_ITEM.get(), new WholeItemParticle.Provider());
        event.registerSpriteSet(ModParticleTypes.LEAVES.get(), BiomeColorParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.RED_SAND.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SAND.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SNOW.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.YELLOW_WILLOW.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LIGHT_BANE.get(), LightBaneParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LIGHT_BANE_DUST.get(), SimpleTextureSheetParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LIGHT_BANE_FADE.get(), SimpleTextureSheetParticle.Provider::new);
    }

    @SubscribeEvent
    public static void textureAtlasStitched(TextureAtlasStitchedEvent event) {
        TextureAtlas atlas = event.getAtlas();
        if (InventoryMenu.BLOCK_ATLAS.equals(atlas.location())) {
            Map<ResourceLocation, TextureAtlasSprite> textures = atlas.getTextures();
            for (ResourceLocation key : ClientUtils.ORIGINAL) {
                TextureAtlasSprite sprite = textures.get(key);
                TextureAtlasSprite gray = textures.get(key.withSuffix(ClientUtils.GRAY_SUFFIX));
                TextureAtlasSprite negative = textures.get(key.withSuffix(ClientUtils.NEGATIVE_SUFFIX));
                if (sprite != null) {
                    GraySpriteShifterEntry.ALL.put(key, new GraySpriteShifterEntry(sprite, gray, negative));
                }
            }
            ClientUtils.ORIGINAL.clear();
        }
    }

    @SubscribeEvent
    public static void model$ModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (ModClientSetups.SHOULD_NOT_GENERATE_BLOCK_GRAY_TEXTURE || !StartupConfigs.paintsReplaceTexture()) return;

        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        CustomBlockModels customBlockModels = ModConnectives.MODEL_SWAPPER.getCustomBlockModels();
        Set<String> bannedModForPaints = new HashSet<>(StartupConfigs.bannedModForPaints());
        for (Map.Entry<Block, Holder.Reference<Block>> entry : ((DefaultedMappedRegistry<Block>) BuiltInRegistries.BLOCK).byValue.entrySet()) {
            Block block = entry.getKey();
            ResourceLocation id = entry.getValue().key().location();
            if (customBlockModels.containsBlock(block) || bannedModForPaints.contains(id.getNamespace())) {
                continue;
            }
            for (ModelResourceLocation modelLocation : ModelSwapper.getAllBlockStateModelLocations(id, block)) {
                BakedModel bakedModel = modelRegistry.get(modelLocation);
                if (bakedModel != null) {
                    modelRegistry.put(modelLocation, new GrayBlockModelSwapper(bakedModel));
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        ModRecipes.TYPES.getEntries().forEach(holder -> event.registerRecipeCategoryFinder(holder.get(), recipeHolder -> RecipeBookCategories.UNKNOWN));
    }
}
