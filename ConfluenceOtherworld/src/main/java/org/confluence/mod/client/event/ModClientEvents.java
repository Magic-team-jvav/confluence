package org.confluence.mod.client.event;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.LibStartupConfig;
import org.confluence.lib.client.render.item.SimpleClientItemExtensions;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.lib.common.item.GroupItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.api.event.bestiary.RegisterCustomBestiaryEntryRendererEvent;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.effect.ColoredGlintContext;
import org.confluence.mod.client.effect.biome.ClientBiomeEffectSystem;
import org.confluence.mod.client.effect.connected.CustomBlockModels;
import org.confluence.mod.client.effect.connected.ModConnectives;
import org.confluence.mod.client.effect.connected.ModelSwapper;
import org.confluence.mod.client.effect.connected.StitchedSprite;
import org.confluence.mod.client.effect.textures.GrayBlockModelSwapper;
import org.confluence.mod.client.effect.textures.GraySpriteShifterEntry;
import org.confluence.mod.client.gameevent.GoblinArmyProgressRenderer;
import org.confluence.mod.client.gui.container.*;
import org.confluence.mod.client.gui.hud.*;
import org.confluence.mod.client.handler.SoulSkillClientHolder;
import org.confluence.mod.client.handler.StarPhaseHandler;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.model.block.LifeCrystalBlockModel;
import org.confluence.mod.client.model.block.RelicBlockModel;
import org.confluence.mod.client.model.block.WeatherVaneBlockModel;
import org.confluence.mod.client.model.entity.RainbowSheepFurModel;
import org.confluence.mod.client.model.entity.RainbowSheepModel;
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
import org.confluence.mod.client.renderer.entity.*;
import org.confluence.mod.client.renderer.entity.bestiary.BestiaryEntryDisplayRenderer;
import org.confluence.mod.client.renderer.entity.bestiary.SlimeZombieRenderer;
import org.confluence.mod.client.renderer.entity.bullet.BulletRenderer;
import org.confluence.mod.client.renderer.entity.fishing.BaseFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.BloodyFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.GlowingFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.HotlineFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.flail.BaseFlailRenderer;
import org.confluence.mod.client.renderer.entity.flail.FlailModel;
import org.confluence.mod.client.renderer.entity.hook.*;
import org.confluence.mod.client.renderer.entity.projectile.*;
import org.confluence.mod.client.renderer.entity.projectile.bomb.*;
import org.confluence.mod.client.renderer.entity.projectile.sword.ForwardProjRenderer;
import org.confluence.mod.client.renderer.entity.projectile.sword.LightsBaneProjectileRenderer;
import org.confluence.mod.client.renderer.entity.projectile.sword.NightEdgeProjectileRenderer;
import org.confluence.mod.client.renderer.entity.projectile.sword.StarFuryProjectileRenderer;
import org.confluence.mod.client.renderer.item.ArrowInBowRenderer;
import org.confluence.mod.client.renderer.item.EnemyBannerItemRenderer;
import org.confluence.mod.client.renderer.item.LucyTheAxeDialogRenderer;
import org.confluence.mod.client.renderer.item.ShortSwordInHandRenderer;
import org.confluence.mod.client.renderer.item.gun.SimpleGeoItemRenderer;
import org.confluence.mod.client.renderer.tooltip.AltImageTooltip;
import org.confluence.mod.client.renderer.tooltip.ClientRepeaterContentsTooltip;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.functional.boulder.GeoBoulderBlock;
import org.confluence.mod.common.data.LucyTheAxeDialogCategory;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.confluence.mod.common.entity.projectile.spear.GhastlyProjectile;
import org.confluence.mod.common.entity.projectile.spear.MushroomProjectile;
import org.confluence.mod.common.entity.projectile.spear.NorthPoleProjectile;
import org.confluence.mod.common.entity.projectile.spear.StormSpearProjectile;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.common.item.tooltipcomponent.RepeaterComponent;
import org.confluence.mod.util.ClientUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.model.entity.BeeProjectileModel;
import org.confluence.terra_curio.client.renderer.entity.BeeProjectileRenderer;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.client.*;
import org.mesdag.portlib.event.client.extensions.common.PortRegisterClientExtensionsEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLClientSetupEventPort;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.confluence.mod.client.event.ModClientSetups.VOID_B;
import static org.confluence.mod.common.init.ModEntities.*;

public final class ModClientEvents {
    public static void init() {
        PortEventHandler.addListener(ModClientEvents::clientSetup);
        PortEventHandler.addListener(ModClientEvents::modConfig$Loading);
        PortEventHandler.addListener(ModClientEvents::modConfig$Reloading);
        PortEventHandler.addListener(ModClientEvents::registerMenuScreens);
        PortEventHandler.addListener(ModClientEvents::registerGuiLayers);
        PortEventHandler.addListener(ModClientEvents::registerEntityLayers);
        PortEventHandler.addListener(ModClientEvents::registerEntityRenderers);
        PortEventHandler.addListener(ModClientEvents::registerBlockColors);
        PortEventHandler.addListener(ModClientEvents::registerItemColors);
        PortEventHandler.addListener(ModClientEvents::registerClientExtensions);
        PortEventHandler.addListener(ModClientEvents::registerParticles);
        PortEventHandler.addListener(ModClientEvents::textureAtlasStitched);
        PortEventHandler.addListener(ModClientEvents::registerMaterialAtlasesEvent);
        PortEventHandler.addListener(ModClientEvents::model$ModifyBakingResult);
        PortEventHandler.addListener(ModClientEvents::registerRecipeBookCategories);
        PortEventHandler.addListener(ModClientEvents::registerRenderBuffers);
        PortEventHandler.addListener(ModClientEvents::registerClientTooltipComponentFactories);
        PortEventHandler.addListener(ModClientEvents::registerClientReloadListeners);
        PortEventHandler.addListener(ModClientEvents::registerItemDecorations);
    }

    public static void clientSetup(PortFMLClientSetupEventPort event) {
        event.enqueueWork(() -> {
            StarPhaseHandler.enabled = CommonConfigs.STAR_PHASE.get();
            ModClientSetups.registerBowProperties();
            ModClientSetups.registerFishingPoleProperties();
            ArrowInBowRenderer.initAdaptionMap();

            ModClientSetups.registerItemProperties();
            ModClientSetups.setRenderLayers();

            ClientBestiary.getInstance().registerCustomFilter();

            ClientBiomeEffectSystem.registerEffects();
        });
    }

    public static void modConfig$Loading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && Confluence.MODID.equals(event.getConfig().getModId())) {
            ClientConfigs.onLoad();
        }
    }

    public static void modConfig$Reloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && Confluence.MODID.equals(event.getConfig().getModId())) {
            ClientConfigs.onLoad();
            StarPhaseHandler.enabled = CommonConfigs.STAR_PHASE.get();
        }
    }

    public static void registerMenuScreens(PortRegisterMenuScreensEvent event) {
        // block
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
        event.register(ModMenuTypes.LOOM.get(), LoomScreen::new);
        event.register(ModMenuTypes.DYE_VAT.get(), DyeVatScreen::new);
        event.register(ModMenuTypes.DYE_MIX.get(), DyeMixScreen::new);
        event.register(ModMenuTypes.PIGGY_BANK.get(), PiggyBankScreen::new);
        // npc
        event.register(ModMenuTypes.NPC_TRADES_MENU.get(), WithForgeTradeScreen::new);
        event.register(ModMenuTypes.REFORGE_MENU.get(), NPCReforgeScreen::new);
    }

    public static void registerGuiLayers(PortRegisterGuiLayersEvent event) {
        ResourceLocation repeaterHud = Confluence.asResource("repeater_hud");
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, repeaterHud, new RepeaterHud());
        ResourceLocation healthHud = Confluence.asResource("health_hud");
        event.registerBelow(VanillaGuiLayers.ARMOR_LEVEL, healthHud, new TerraStyleHealthHud());
        ResourceLocation armorHud = Confluence.asResource("armor_hud");
        event.registerAbove(healthHud, armorHud, new TerraStyleArmorHud());
        ResourceLocation manaHud = Confluence.asResource("mana_hud");
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, manaHud, new TerraStyleManaHud());
//        ResourceLocation soulHud = Confluence.asResource("soul_hud");
//        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, soulHud, new TerraStyleSoulHud());
        ResourceLocation foodHud = Confluence.asResource("food_hud");
        event.registerBelow(manaHud, foodHud, new TerraStyleFoodHud());

        event.registerBelow(VanillaGuiLayers.CROSSHAIR, Confluence.asResource("house_select"), new HouseSelectHud());
        event.registerBelow(VanillaGuiLayers.BOSS_OVERLAY, Confluence.asResource("goblin_army"), new GoblinArmyProgressRenderer());
        event.registerAboveAll(Confluence.asResource("ask_for_softcore"), new AskForSoftcoreLayer());

        event.registerAbove(VanillaGuiLayers.SUBTITLE_OVERLAY, Confluence.asResource("card_horizontal_l_hud"), SoulSkillClientHolder.CARD_HORIZONTAL_L_HUD_INSTANCE);
        event.registerAbove(VanillaGuiLayers.SUBTITLE_OVERLAY, Confluence.asResource("card_horizontal_r_hud"), SoulSkillClientHolder.CARD_HORIZONTAL_R_HUD_INSTANCE);
        event.registerBelow(VanillaGuiLayers.HOTBAR, Confluence.asResource("roulette_wheel_small_hud"), SoulSkillClientHolder.ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
        event.registerBelow(VanillaGuiLayers.HOTBAR, Confluence.asResource("current_selected_skill_hud"), SoulSkillClientHolder.CURRENT_SELECTED_SKILL_HUD_INSTANCE);
        event.registerAbove(VanillaGuiLayers.SUBTITLE_OVERLAY, Confluence.asResource("roulette_wheel_big_hud"), SoulSkillClientHolder.ROULETTE_WHEEL_BIG_HUD_INSTANCE);
        SoulSkillClientHolder.INSTANCE.init();
    }

    public static void registerEntityLayers(PortRegisterLayerDefinitions event) {
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
        event.registerLayerDefinition(TitaniumShardsProjectileModel.LAYER_LOCATION, TitaniumShardsProjectileModel::createBodyLayer);

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
        event.registerLayerDefinition(ShurikenProjectileModel.LAYER_LOCATION, ShurikenProjectileModel::createBodyLayer);
        event.registerLayerDefinition(ThrownKniveProjectileModel.LAYER_LOCATION, ThrownKniveProjectileModel::createBodyLayer);
        event.registerLayerDefinition(BoneThrownKnivesProjectileModel.LAYER_LOCATION, BoneThrownKnivesProjectileModel::createBodyLayer);
        event.registerLayerDefinition(DungeonDemonBoneProjectileModel.LAYER_LOCATION, DungeonDemonBoneProjectileModel::createBodyLayer);
        event.registerLayerDefinition(FrostDaggerfishProjectileModel.LAYER_LOCATION, FrostDaggerfishProjectileModel::createBodyLayer);
        event.registerLayerDefinition(VilethronProjectileModel.LAYER_LOCATION, VilethronProjectileModel::createBodyLayer);
        event.registerLayerDefinition(DemonScytheProjectileModel.LAYER_LOCATION, DemonScytheProjectileModel::createBodyLayer);
        event.registerLayerDefinition(SpikyBallProjectileModel.LAYER_LOCATION, SpikyBallProjectileModel::createBodyLayer);
        event.registerLayerDefinition(HurtnadoProjectileModel.LAYER_LOCATION, HurtnadoProjectileModel::createBodyLayer);
        event.registerLayerDefinition(RollingCactusSpikeModel.LAYER_LOCATION, RollingCactusSpikeModel::createBodyLayer);
        event.registerLayerDefinition(RainProjectileModel.LAYER_LOCATION, RainProjectileModel::createBodyLayer);
        event.registerLayerDefinition(SkullProjectileModel.LAYER_LOCATION, SkullProjectileModel::createBodyLayer);
        event.registerLayerDefinition(StormSpearProjectile.LAYER_LOCATION, StormSpearProjectile::createBodyLayer);
        event.registerLayerDefinition(NorthPoleProjectile.LAYER_LOCATION, NorthPoleProjectile::createBodyLayer);
        event.registerLayerDefinition(MushroomProjectile.LAYER_LOCATION, MushroomProjectile::createBodyLayer);
        event.registerLayerDefinition(GhastlyProjectile.LAYER_LOCATION, GhastlyProjectile::createBodyLayer);

        event.registerLayerDefinition(BaseHookModel.LAYER_LOCATION, BaseHookModel::createBodyLayer);
        event.registerLayerDefinition(WebSlingerModel.LAYER_LOCATION, WebSlingerModel::createBodyLayer);
        event.registerLayerDefinition(SkeletronHandModel.LAYER_LOCATION, SkeletronHandModel::createBodyLayer);

        /* todo 静止钩 */

        event.registerLayerDefinition(FlailModel.LAYER_LOCATION, FlailModel::createBodyLayer);

        event.registerLayerDefinition(WeatherVaneBlockModel.LAYER_LOCATION, WeatherVaneBlockModel::createBodyLayer);

        event.registerLayerDefinition(RainbowSheepModel.LAYER_LOCATION, RainbowSheepModel::createBodyLayer);
        event.registerLayerDefinition(RainbowSheepFurModel.LAYER_LOCATION, RainbowSheepFurModel::createFurLayer);
    }

    public static void registerEntityRenderers(PortEntityRenderersEvent.PortRegisterRenderers event) {
        event.registerEntityRenderer(EMPTY_ENTITY.get(), EmptyEntityRenderer::new); // 牢枕专用
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
        event.registerEntityRenderer(CRYSTAL_VILE_SHARD_PROJECTILE.get(), CrystalVileShardProjectileRenderer::new);
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
        event.registerEntityRenderer(DRIVE_AWAY_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(FOLLOWER_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(EXPLODE_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(ROLLING_CACTUS_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(ROLLING_CACTUS_SPIKE.get(), RollingCactusSpikeRenderer::new);
        event.registerEntityRenderer(TOMBSTONE_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(BOUNCY_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(GHOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(LAVA_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(POO_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(SPIDER_BOULDER.get(), BoulderRenderer::new);
        event.registerEntityRenderer(RAINBOW_BOULDER.get(), RainbowBoulderRenderer::new);
        event.registerEntityRenderer(LIFECRYSTAL_BOULDER.get(), LifecrystalBoulderRenderer::new);
        event.registerEntityRenderer(BOULDER_3X.get(), BoulderRenderer::new);
        event.registerEntityRenderer(THROWN_KNIVE_PROJECTILE.get(), ThrownKniveProjectileRenderer::new);
        event.registerEntityRenderer(BONE_THROWN_KNIVE_PROJECTILE.get(), BoneThrownKniveProjectileRenderer::new);
        event.registerEntityRenderer(FROST_DAGGERFISH_PROJECTILE.get(), FrostDaggerfishProjectileRenderer::new);
        event.registerEntityRenderer(DUNGEON_DEMON_BONE_PROJECTILE.get(), DungeonDemonBoneProjectileRenderer::new);
        event.registerEntityRenderer(JAVELIN_PROJECTILE.get(), SpearRenderer::new);
        event.registerEntityRenderer(SHURIKEN_PROJECTILE.get(), ShurikenProjectileRenderer::new);
        event.registerEntityRenderer(SPIKY_BALL_PROJECTILE.get(), SpikyBallProjectileRenderer::new);
        event.registerEntityRenderer(THROWN_WATER_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(MAGIC_DAGGER_PROJECTILE.get(), MagicDaggerRenderer::new);
        event.registerEntityRenderer(CRYSTAL_STORM_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(CURSED_FLAMES_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(FLOWER_PETAL_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(TITANIUM_SHARDS_PROJECTILE.get(), TitaniumShardsProjectileRenderer::new);
        event.registerEntityRenderer(FALLING_STAR_ITEM_ENTITY.get(), FallingStarRenderer::new);
        event.registerEntityRenderer(TREASURE_BAG_ITEM_ENTITY.get(), TreasureBagRenderer::new);
        event.registerEntityRenderer(COIN_PORTAL.get(), NoopRenderer::new);
        event.registerEntityRenderer(THROWN_POWDER.get(), NoopRenderer::new);
        event.registerEntityRenderer(ROPE_COILS.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ICE_TOFU_BRICK_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BODY_PART.get(), BodyPartRenderer::new);
        event.registerEntityRenderer(FLAME_CLOUD.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(SUPER_SPIKY_BALL_PROJECTILE.get(), SuperSpikyBallProjectileRenderer::new);
        event.registerEntityRenderer(SPEAR.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(BALL_OF_FROST_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(DEMON_SCYTHE_PROJECTILE.get(), DemonScytheProjectileRenderer::new);
        event.registerEntityRenderer(SKULL_PROJECTILE.get(), SkullProjectileRenderer::new);
        event.registerEntityRenderer(BLOOD_CLOUD_PROJECTILE.get(), context -> new GeoNegativeVolumeRenderer<>(context, new BloodCloudProjectileModel(), false, 2, -0.2F));
        event.registerEntityRenderer(BLOOD_RAIN_PROJECTILE.get(), context -> new RainProjectileRenderer(context, RainProjectileRenderer.BLOOD_RAIN));
        event.registerEntityRenderer(RAIN_CLOUD_PROJECTILE.get(), context -> new GeoNegativeVolumeRenderer<>(context, new RainCloudProjectileModel(), false, 2, -0.2F));
        event.registerEntityRenderer(RAIN_PROJECTILE.get(), context -> new RainProjectileRenderer(context, RainProjectileRenderer.RAIN));
        event.registerEntityRenderer(STORM_SPEAR_SHOT_PROJECTILE.get(), context -> new SpearProjectileRenderer(context, StormSpearProjectile.LAYER_LOCATION));
        event.registerEntityRenderer(SPORE_CLOUD_PROJECTILE.get(), NoopRenderer::new);//todo 贴图模型粒子
        event.registerEntityRenderer(NORTH_POLE_PROJECTILE.get(), context -> new SpearProjectileRenderer(context, NorthPoleProjectile.LAYER_LOCATION));
        event.registerEntityRenderer(NORTH_POLE_SUB_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(GHASTLY_PROJECTILE.get(), context -> new SpearProjectileRenderer(context, GhastlyProjectile.LAYER_LOCATION));
        event.registerEntityRenderer(MUSHROOM_PROJECTILE.get(), context -> new SpearProjectileRenderer(context, MushroomProjectile.LAYER_LOCATION));
        event.registerEntityRenderer(GOLDEN_SHOWER_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(MAGIC_MISSILE_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(FLAMELASH_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(RAINBOW_PROJECTILE.get(), NoopRenderer::new); // todo 粒子
        event.registerEntityRenderer(SKY_FRACTURE_PROJECTILE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(CRYSTAL_CHARGE_1_PROJECTILE.get(), NoopRenderer::new); // todo 粒子
        event.registerEntityRenderer(CRYSTAL_CHARGE_2_PROJECTILE.get(), NoopRenderer::new); // todo 粒子

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

        event.registerEntityRenderer(FLAIL_ENTITY.get(), BaseFlailRenderer::new);

        EntityRendererProvider<BaseMinecartEntity> provider = context -> new MinecartRenderer<>(context, ModelLayers.MINECART);
        event.registerEntityRenderer(VANILLA_MINECART.get(), provider);
        event.registerEntityRenderer(WOODEN_MINECART.get(), provider); // todo 模型
        event.registerEntityRenderer(GENERIC_MINECART.get(), provider);
        event.registerEntityRenderer(MECHANICAL_CART.get(), provider);
        event.registerEntityRenderer(MINECARP.get(), provider);
        event.registerEntityRenderer(DEMONIC_HELLCART.get(), provider);
        event.registerEntityRenderer(MEOWMERE_MINECART.get(), provider);
        event.registerEntityRenderer(DIGGING_MOLECART.get(), provider);

        event.registerEntityRenderer(BESTIARY_ENTRY_DISPLAY.get(), BestiaryEntryDisplayRenderer::new);

        event.registerEntityRenderer(STAR_CANNON_BULLET.get(), StarCannonBulletRenderer::new);
        event.registerEntityRenderer(BEE_GUN_BULLET.get(), BeeProjectileRenderer::new);
        event.registerEntityRenderer(BASE_BULLET_ENTITY.get(), BulletRenderer::new);
        event.registerEntityRenderer(GRAVITY_BULLET_ENTITY.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(RAINBOW_SHEEP.get(), RainbowSheepRenderer::new);
        event.registerEntityRenderer(INVERSE_ENDERMAN.get(), EndermanRenderer::new);

        event.registerEntityRenderer(ACCUMULATING_ENERGY.get(), NoopRenderer::new);

        event.registerBlockEntityRenderer(FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(AltarBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.SKY_MILL_ENTITY.get(), ClientUtils.rendererProvider(SkyMillBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.EXTRACTINATOR_ENTITY.get(), ClientUtils.rendererProvider(ExtractinatorBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.SILLY_BALLOON_MACHINE_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.WEATHER_VANE_ENTITY.get(), WeatherVaneBlockRenderer::new);
        event.registerBlockEntityRenderer(ChestBlocks.BASE_CHEST_ENTITY.get(), BaseChestBlockRenderer::new);
        event.registerBlockEntityRenderer(ChestBlocks.DEATH_CHEST_ENTITY.get(), DeathChestBlockRenderer::new);
        event.registerBlockEntityRenderer(NatureBlocks.LIFE_CRYSTAL_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new LifeCrystalBlockModel()));
        event.registerBlockEntityRenderer(FunctionalBlocks.LIFECRYSTAL_BOULDER_ENTITY.get(), context -> new GeoBlockRenderer<GeoBoulderBlock.BEntity>(new LifeCrystalBlockModel()));
        event.registerBlockEntityRenderer(DecorativeBlocks.RELIC_ENTITY.get(), context -> new IgnoreEnvironmentLightGeoBlockRenderer<>(new RelicBlockModel()));
        event.registerBlockEntityRenderer(StatueBlocks.BLOCK_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.COOKING_POT_ENTITY.get(), context -> new GeoBlockRenderer<>(new DefaultedBlockGeoModel<>(Confluence.asResource("cooking_pot"))));
        event.registerBlockEntityRenderer(FunctionalBlocks.ANNOUNCEMENT_BOX_ENTITY.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.SAFE_ENTITY.get(), context -> new GeoBlockRenderer<>(new DefaultedBlockGeoModel<>(Confluence.asResource("safe"))));
        event.registerBlockEntityRenderer(DecorativeBlocks.MURAL_ENTITY_BLOCK.get(), ClientUtils.rendererProvider(MuralBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.BEWITCHING_TABLE_ENTITY.get(), ClientUtils.rendererProvider(BewitchingTableBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.LOOM_ENTITY.get(), ClientUtils.rendererProvider(LoomBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.SOUL_BOTTLE_ENTITY.get(), ClientUtils.rendererProvider(SoulBottleBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.TUFF_BOOTH_ENTITY.get(), ClientUtils.rendererProvider(TuffBoothBlockRenderer::new));
        event.registerBlockEntityRenderer(ModBlocks.VOID_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(VoidBlockRenderer::new));
        event.registerBlockEntityRenderer(NatureBlocks.VOID_TREE_ROOT_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(VoidTreeRootBlockRenderer::new));
        event.registerBlockEntityRenderer(ModBlocks.ENEMY_BANNER_ENTITY.get(), EnemyBannerBlockRenderer::new);
    }

    public static void registerBlockColors(PortRegisterColorHandlersEvent.PortBlock event) {
        event.register(ModClientSetups.HALLOW_LEAVES_COLOR, NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get());
        event.register(ModClientSetups.VOID_LEAVES_COLOR, NatureBlocks.VOID_LOG_BLOCKS.LEAVES.get());
        event.register(ModClientSetups.VOID_WEAVE_COLOR, NatureBlocks.VOID_WEAVE.get());
        event.register(ModClientSetups.DREAM_BUBBLE_COLOR, NatureBlocks.DREAM_BUBBLE.get());
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(), NatureBlocks.BAOBAB_LOG_BLOCKS.LEAVES.get());
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor(), NatureBlocks.JUNGLE_GRASS_BLOCK.get());
    }

    public static void registerItemColors(PortRegisterColorHandlersEvent.PortItem event) {
        event.register((pStack, pTintIndex) -> VOID_B.get(), NatureBlocks.VOID_LOG_BLOCKS.LEAVES.get());
        event.register((pStack, pTintIndex) -> ColoredItem.getRGBA(pStack), MaterialItems.GEL.get());
        event.register((pStack, pTintIndex) -> GrassColor.getDefaultColor(), NatureBlocks.JUNGLE_GRASS_BLOCK.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? PaintItem.getARGB(stack) : 0xFFFFFFFF, PaintItems.PAINT_ITEMS.toArray(new Item[0]));
        event.register((stack, tintIndex) -> tintIndex == 1 ? BaseDyeItem.getARGB(stack) : 0xFFFFFFFF, VanityArmorItems.COLORED_DYE_ITEMS.toArray(new Item[0]));
    }

    public static void registerClientExtensions(PortRegisterClientExtensionsEvent event) {
        event.registerFluidType(ModClientSetups.HONEY_CLIENT_EXTENSIONS, ModFluids.HONEY.type());
        event.registerFluidType(ModClientSetups.VOID_CLIENT_EXTENSIONS, ModFluids.VOID.type());
        event.registerFluidType(ModClientSetups.SHIMMER_CLIENT_EXTENSIONS, ModFluids.SHIMMER.type());
        event.registerBlock(ModClientSetups.NO_HIT_EFFECTS, ModBlocks.ROPE.get(), ModBlocks.VINE_ROPE.get(), ModBlocks.SILK_ROPE.get(), ModBlocks.WEB_ROPE.get(), ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET.get());
        event.registerItem(ModClientSetups.ENTITY_DISPLAY, ModItems.ENTITY_DISPLAY.get());
        event.registerItem(new SimpleClientItemExtensions().customRenderer((minecraft, stack, displayContext, poseStack, buffer, packedLight, packedOverlay) -> {
            SimpleClientItemExtensions.renderSimpleItem(minecraft, stack, poseStack, buffer, packedLight, packedOverlay);
            if (LucyTheAxeDialogRenderer.dialog != null && displayContext == ItemDisplayContext.GUI) {
                LucyTheAxeDialogRenderer.renderInGui(minecraft, poseStack);
            }
        }), AxeItems.LUCY_THE_AXE.get());
        event.registerItem(ModClientSetups.BREATHING_REED, SwordItems.BREATHING_REED);
        event.registerItem(ModClientSetups.SPEAR, SpearItems.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));
        event.registerItem(ModClientSetups.UMBRELLA, SwordItems.UMBRELLA, SwordItems.TRAGIC_UMBRELLA);
        event.registerItem(ModClientSetups.DRILL_O_CHAINSAW, Streams.stream(Iterables.concat(
                DrillItems.ITEMS.getEntries(),
                ChainsawItems.ITEMS.getEntries()
        )).filter(Objects::nonNull).map(DeferredHolder::get).toArray(Item[]::new));
        event.registerItem(ModClientSetups.LANCE, LanceItems.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));
        for (DeferredHolder<Item, ? extends Item> holder : SwordItems.ITEMS.getEntries()) {
            if (SwordItems.isShortSword(holder)) {
                event.registerItem(ShortSwordInHandRenderer.INSTANCE, holder.get());
            }
        }
        event.registerItem(ModClientSetups.NOOP_ITEM, SwordItems.ZOMBIE_ARM);
        event.registerItem(ModClientSetups.GUIDE_VOODOO_DOLL, AccessoryItems.GUIDE_VOODOO_DOLL);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_FRIGHT);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_MIGHT);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_SIGHT);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_LIGHT);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_NIGHT);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_FLIGHT);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_VOIGHT);
        event.registerItem(ModClientSetups.FULL_LIGHT, MaterialItems.SOUL_OF_BRIGHT);
        event.registerItem(ModClientSetups.GLINT_RAINBOW_EXTENSIONS, TreasureBagItems.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));
        event.registerItem(new EnemyBannerItemRenderer(), ModItems.ENEMY_BANNER);
        registerGunModel(event, Confluence.MODID, ManaWeaponItems.BEE_GUN);
        registerGunModel(event, Confluence.MODID, ManaWeaponItems.SPACE_GUN);
        GunItems.ITEMS.getEntries().forEach(holder -> registerGunModel(event, Confluence.MODID, holder));
        event.registerMobEffect(ModClientSetups.TRANSLUCENT_EFFECT_ICON, ModEffects.LUCK_EFFECT.get());
    }

    private static void registerGunModel(PortRegisterClientExtensionsEvent event, String modid, RegistryObject<? extends Item> gunSupplier) {
        if (gunSupplier.getId() != null) {
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(modid, "gun/" + gunSupplier.getId().getPath());
            event.registerItem(new SimpleGeoItemRenderer<BaseGun>(new DefaultedItemGeoModel<>(loc)), gunSupplier.get());
        }
    }

    public static void registerParticles(PortRegisterParticleProvidersEvent event) {
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
        event.registerSpriteSet(ModParticleTypes.ECTO_MIST.get(), EctoMistParticle.Provider::new);
    }

    public static void textureAtlasStitched(PortTextureAtlasStitchedEvent event) {
        TextureAtlas atlas = event.getAtlas();
        StitchedSprite.onTextureStitchPost(atlas);

        if (ModClientSetups.VANILLA_BLOCK_ATLAS.equals(atlas.location())) {
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

    public static void registerMaterialAtlasesEvent(PortRegisterMaterialAtlasesEvent event) {
        event.register(ModClientSetups.ENTITY_BLOOD_ATLAS, Confluence.asResource("entity_blood"));
    }

    public static void model$ModifyBakingResult(PortModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();

        ModClientSetups.asCustomModel(modelRegistry,
                AccessoryItems.GUIDE_VOODOO_DOLL,
                MaterialItems.SOUL_OF_FRIGHT,
                MaterialItems.SOUL_OF_MIGHT,
                MaterialItems.SOUL_OF_SIGHT,
                MaterialItems.SOUL_OF_LIGHT,
                MaterialItems.SOUL_OF_NIGHT,
                MaterialItems.SOUL_OF_FLIGHT,
                MaterialItems.SOUL_OF_BRIGHT,
                MaterialItems.SOUL_OF_VOIGHT,
                AxeItems.LUCY_THE_AXE
        );
        ModClientSetups.asCustomModel(modelRegistry, TreasureBagItems.ITEMS.getEntries().toArray(DeferredHolder[]::new));

        ModConnectives.MODEL_SWAPPER.onModelBake(modelRegistry);

        if (ModClientSetups.SHOULD_NOT_GENERATE_BLOCK_GRAY_TEXTURE || !StartupConfigs.paintsReplaceTexture())
            return;

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

    public static void registerRecipeBookCategories(PortRegisterRecipeBookCategoriesEvent event) {
        ModRecipes.TYPES.getEntries().forEach(holder -> event.registerRecipeCategoryFinder(holder.get(), recipeHolder -> RecipeBookCategories.UNKNOWN));
    }

    public static void registerRenderBuffers(PortRegisterRenderBuffersEvent event) {
        for (ColoredGlintContext context : ColoredGlintContext.COLORED_GLINT_CONTEXTS) {
            event.registerRenderBuffer(context.renderType());
        }
    }

    public static void registerClientTooltipComponentFactories(PortRegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AltImageComponent.class, AltImageTooltip::new);
        event.register(RepeaterComponent.class, ClientRepeaterContentsTooltip::new);
    }

    public static void registerClientReloadListeners(PortRegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ClientBestiary.getInstance());
        event.registerReloadListener(LucyTheAxeDialogCategory.Loader.getInstance());
    }

    public static void registerCustomBestiaryEntryModel(RegisterCustomBestiaryEntryRendererEvent event) {
        EntityRendererProvider.Context context = event.getContext();
        event.registeSurefaceWorm(TEMonsterEntities.DEVOURER);
        event.registerBaseWorm(TEMonsterEntities.TOMB_CRAWLER);
        event.registerBaseWorm(TEMonsterEntities.GIANT_WORM);
        event.registerBaseWorm(TEMonsterEntities.LEECH);
        event.registerBoneSerpent(TEMonsterEntities.BONE_SERPENT);
        event.registerBoneSerpent(TEMonsterEntities.WITHER_BONE_SERPENT);
        event.register("entity.minecraft.zombie.slime", new SlimeZombieRenderer(context));
    }

    public static void registerItemDecorations(PortRegisterItemDecorationsEvent event) {
        for (DeferredHolder<Item, ? extends Item> entry : FishingPoleItems.ITEMS.getEntries()) {
            event.register(entry.get(), ModClientSetups.FISHING_POLE_DECORATOR);
        }
        for (DeferredHolder<Item, ? extends Item> entry : CrossbowItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof BaseTerraRepeaterItem) {
                event.register(item, ModClientSetups.REPEATER_AMMO);
            }
        }
        if (LibStartupConfig.itemGroups()) {
            ResourceLocation plus = Confluence.asResource("plus");
            ResourceLocation minus = Confluence.asResource("minus");
            event.register(GroupItem.getInstance(), (guiGraphics, font, stack, xOffset, yOffset) -> {
                GroupItem.Stacks stacks = stack.get(ConfluenceMagicLib.GROUP_STACKS);
                if (stacks != null) {
                    PoseStack pose = guiGraphics.pose();
                    pose.pushPose();
                    pose.translate(xOffset + 9, yOffset + 9, 200);
                    guiGraphics.blitSprite(stacks.isVisible() ? minus : plus, 0, 0, 7, 7);
                    pose.popPose();
                }
                return false;
            });
        }
    }
}
