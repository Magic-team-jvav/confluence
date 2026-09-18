package org.confluence.mod.client.event;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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
import org.confluence.mod.client.effect.DivaSlimeVertexConsumer;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.effect.biome.ClientBiomeEffectSystem;
import org.confluence.mod.client.effect.connected.CustomBlockModels;
import org.confluence.mod.client.effect.connected.ModConnectives;
import org.confluence.mod.client.effect.connected.ModelSwapper;
import org.confluence.mod.client.effect.connected.StitchedSprite;
import org.confluence.mod.client.effect.textures.GrayBlockModelSwapper;
import org.confluence.mod.client.effect.textures.GraySpriteShifterEntry;
import org.confluence.mod.client.entity.model.*;
import org.confluence.mod.client.entity.renderer.*;
import org.confluence.mod.client.gameevent.GoblinArmyProgressRenderer;
import org.confluence.mod.client.gui.container.*;
import org.confluence.mod.client.gui.hud.*;
import org.confluence.mod.client.handler.SoulSkillClientHandler;
import org.confluence.mod.client.handler.StarPhaseHandler;
import org.confluence.mod.client.handler.SwordProjectileVisualHandler;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.init.model.WhipModelRegister;
import org.confluence.mod.client.model.block.GardenGnomeBlockModel;
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
import org.confluence.mod.client.model.entity.summon.TerraprismaModel;
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
import org.confluence.mod.client.renderer.entity.projectile.sword.SwordProjectileRenderer;
import org.confluence.mod.client.renderer.entity.yoyo.YoyoRenderer;
import org.confluence.mod.client.renderer.item.*;
import org.confluence.mod.client.renderer.tooltip.AltImageTooltip;
import org.confluence.mod.client.renderer.tooltip.ClientRepeaterContentsTooltip;
import org.confluence.mod.client.summon.ClientSummonManager;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.LucyTheAxeDialogCategory;
import org.confluence.mod.common.entity.animal.Fairy;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.entity.mount.RideableBeeMountEntity;
import org.confluence.mod.common.entity.mount.RideableSlimeMountEntity;
import org.confluence.mod.common.entity.npc.dialog.NPCDialogLoader;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.common.item.tooltipcomponent.RepeaterComponent;
import org.confluence.mod.util.ClientUtils;
import org.mesdag.portlib.client.gui.components.PortSprite;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.client.*;
import org.mesdag.portlib.event.client.extensions.common.PortRegisterClientExtensionsEvent;
import org.mesdag.portlib.registries.PortRegistryEntry;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.io.IOException;
import java.util.*;

import static org.confluence.mod.client.event.ModClientSetups.VOID_B;
import static org.confluence.mod.common.init.entity.ModEntities.*;

public final class ModClientEvents {
    public static void init() {
        PortEventHandler.addListener(ModClientEvents::fmlClientSetup);
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
        PortEventHandler.addListener(ModClientEvents::registerAdditionalModels);
        PortEventHandler.addListener(ModClientEvents::registerRecipeBookCategories);
        PortEventHandler.addListener(ModClientEvents::registerRenderBuffers);
        PortEventHandler.addListener(ModClientEvents::registerClientTooltipComponentFactories);
        PortEventHandler.addListener(ModClientEvents::registerClientReloadListeners);
        PortEventHandler.addListener(ModClientEvents::registerShaders);
        PortEventHandler.addListener(ModClientEvents::registerCustomBestiaryEntryModel);
        PortEventHandler.addListener(ModClientEvents::registerItemDecorations);
    }

    private static void fmlClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            StarPhaseHandler.enabled = CommonConfigs.STAR_PHASE.get();
            ModClientSetups.registerBowProperties();
            ModClientSetups.registerFishingPoleProperties();
            ArrowInBowRenderer.initAdaptionMap();
            SwordProjectileVisualHandler.install();

            ModClientSetups.registerItemProperties();
            ModClientSetups.setRenderLayers();

            ClientBestiary.getInstance().registerCustomFilter();

            ClientBiomeEffectSystem.registerEffects();
        });
    }

    private static void modConfig$Loading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && Confluence.MODID.equals(event.getConfig().getModId())) {
            ClientConfigs.onLoad();
        }
    }

    private static void modConfig$Reloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && Confluence.MODID.equals(event.getConfig().getModId())) {
            ClientConfigs.onLoad();
            StarPhaseHandler.enabled = CommonConfigs.SPEC.isLoaded() ? CommonConfigs.STAR_PHASE.get() : false;
        }
    }

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(ClientSummonManager.FINCH_STAFF_EMPTY_MODEL);
        event.register(TongueRenderer.SEGMENT_MODEL);
        WhipModelRegister.register(event);
    }

    private static void registerMenuScreens(PortRegisterMenuScreensEvent event) {
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
        event.register(ModMenuTypes.REFORGE_MENU.get(), NPCReforgeScreen::new);
        event.register(ModMenuTypes.NPC_TRADE.get(), NPCTradeScreen::new);
    }

    private static void registerGuiLayers(PortRegisterGuiLayersEvent event) {
        ResourceLocation repeaterHud = Confluence.asResource("repeater_hud");
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), repeaterHud, new RepeaterHud());
        ResourceLocation healthHud = Confluence.asResource("health_hud");
        event.registerBelow(VanillaGuiOverlay.ARMOR_LEVEL.id(), healthHud, new TerraStyleHealthHud());
        ResourceLocation armorHud = Confluence.asResource("armor_hud");
        event.registerAbove(healthHud, armorHud, new TerraStyleArmorHud());
        ResourceLocation manaHud = Confluence.asResource("mana_hud");
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), manaHud, new TerraStyleManaHud());
        ResourceLocation foodHud = Confluence.asResource("food_hud");
        event.registerBelow(manaHud, foodHud, new TerraStyleFoodHud());

        event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), Confluence.asResource("house_select"), new HouseSelectHud());
        event.registerBelow(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), Confluence.asResource("goblin_army"), new GoblinArmyProgressRenderer());

        if (Confluence.SOUL_SKILLS) {
            event.registerAbove(VanillaGuiOverlay.SUBTITLES.id(), Confluence.asResource("card_horizontal_l_hud"), SoulSkillClientHandler.CARD_HORIZONTAL_L_HUD_INSTANCE);
            event.registerAbove(VanillaGuiOverlay.SUBTITLES.id(), Confluence.asResource("card_horizontal_r_hud"), SoulSkillClientHandler.CARD_HORIZONTAL_R_HUD_INSTANCE);
            event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), Confluence.asResource("roulette_wheel_small_hud"), SoulSkillClientHandler.ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
            event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), Confluence.asResource("current_selected_skill_hud"), SoulSkillClientHandler.CURRENT_SELECTED_SKILL_HUD_INSTANCE);
            event.registerAbove(VanillaGuiOverlay.SUBTITLES.id(), Confluence.asResource("roulette_wheel_big_hud"), SoulSkillClientHandler.ROULETTE_WHEEL_BIG_HUD_INSTANCE);
            SoulSkillClientHandler.INSTANCE.init();
        }
    }

    private static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BaseSlimeModel.INNER_LAYER, BaseSlimeModel::createInnerBodyLayer);
        event.registerLayerDefinition(BaseSlimeModel.OUTER_LAYER, BaseSlimeModel::createOuterBodyLayer);
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
        event.registerLayerDefinition(SlimeSpikeProjectileModel.LAYER_LOCATION, SlimeSpikeProjectileModel::createBodyLayer);
        event.registerLayerDefinition(HarpyFeatherProjectileModel.LAYER_LOCATION, HarpyFeatherProjectileModel::createBodyLayer);
        event.registerLayerDefinition(HornetStingerProjectileModel.LAYER_LOCATION, HornetStingerProjectileModel::createBodyLayer);
        event.registerLayerDefinition(BeeProjectileModel.LAYER_LOCATION, BeeProjectileModel::createBodyLayer);

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
        event.registerLayerDefinition(SpearProjectileModels.STORM, SpearProjectileModels::createSpearLayer);
        event.registerLayerDefinition(SpearProjectileModels.NORTH_POLE, SpearProjectileModels::createSpearLayer);
        event.registerLayerDefinition(SpearProjectileModels.MUSHROOM, SpearProjectileModels::createMushroomLayer);
        event.registerLayerDefinition(SpearProjectileModels.GHASTLY, SpearProjectileModels::createGhastlyLayer);

        event.registerLayerDefinition(BaseHookModel.LAYER_LOCATION, BaseHookModel::createBodyLayer);
        event.registerLayerDefinition(WebSlingerModel.LAYER_LOCATION, WebSlingerModel::createBodyLayer);
        event.registerLayerDefinition(SkeletronHandModel.LAYER_LOCATION, SkeletronHandModel::createBodyLayer);
        event.registerLayerDefinition(CrownOfKingSlimeModel.LAYER_LOCATION, CrownOfKingSlimeModel::createBodyLayer);

        event.registerLayerDefinition(FlailModel.LAYER_LOCATION, FlailModel::createBodyLayer);
        event.registerLayerDefinition(TerraprismaModel.LAYER_LOCATION, TerraprismaModel::createBodyLayer);

        event.registerLayerDefinition(WeatherVaneBlockModel.LAYER_LOCATION, WeatherVaneBlockModel::createBodyLayer);

        event.registerLayerDefinition(RainbowSheepModel.LAYER_LOCATION, RainbowSheepModel::createBodyLayer);
        event.registerLayerDefinition(RainbowSheepFurModel.LAYER_LOCATION, RainbowSheepFurModel::createFurLayer);
    }

    @SuppressWarnings("all")
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EMPTY_ENTITY.get(), EmptyEntityRenderer::new); // 牢枕专用
        event.registerEntityRenderer(SUMMONED_IRON_GOLEM.get(), context -> new GeoNormalRenderer<>(context, new GeoNormalModel<>(Confluence.asResource("summon/iron_golem_32"), false)));
        event.registerEntityRenderer(CHESTER.get(), context -> new GeoNormalRenderer<>(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/summon/chester.geo.json"), Confluence.asResource("textures/entity/summon/chester.png"), Confluence.asResource("animations/entity/summon/chester.animation.json"))));
        event.registerEntityRenderer(FLYING_PIGGY_BANK.get(), context -> new GeoNormalRenderer<>(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/summon/piggy_bank.geo.json"), Confluence.asResource("textures/entity/summon/piggy_bank.png"), Confluence.asResource("animations/entity/summon/piggy_bank.animation.json"))));
        event.registerEntityRenderer(RIDEABLE_SLIME.get(), context -> new MountGeoRenderer<>(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/rideable/rideable_slime.geo.json"), Confluence.asResource("textures/entity/rideable/rideable_slime.png"), Confluence.asResource("animations/entity/rideable/rideable_slime.animation.json"))).withScale(RideableSlimeMountEntity.RENDER_SCALE).setShadowRadius(0.35F));
        event.registerEntityRenderer(RIDEABLE_BEE.get(), context -> new MountGeoRenderer<>(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/rideable/rideable_bee.geo.json"), Confluence.asResource("textures/entity/rideable/rideable_bee.png"), Confluence.asResource("animations/entity/rideable/rideable_bee.animation.json"))).withScale(RideableBeeMountEntity.RENDER_SCALE).setShadowRadius(0.35F));
        event.registerEntityRenderer(RIDEABLE_UNICORN.get(), context -> new MountGeoRenderer<>(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/rideable/unicorn.geo.json"), Confluence.asResource("textures/entity/rideable/unicorn.png"), Confluence.asResource("animations/entity/unicorn.animation.json"))));
        event.registerEntityRenderer(RIDEABLE_LAVA_SHARK.get(), context -> new MountGeoRenderer<>(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/rideable/lava_shark.geo.json"), Confluence.asResource("textures/entity/rideable/lava_shark.png"), Confluence.asResource("animations/entity/rideable/lava_shark.animation.json"))));
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

        event.registerEntityRenderer(WHIP_ATTACK.get(), WhipAttackRenderer::new);
        event.registerEntityRenderer(BASE_MANA_STAFF.get(), NoopRenderer::new);
        event.registerEntityRenderer(VILETHRON.get(), VilethronProjectileRenderer::new);
        event.registerEntityRenderer(CRYSTAL_VILE_SHARD.get(), CrystalVileShardProjectileRenderer::new);
        event.registerEntityRenderer(HURTNADO.get(), HurtnadoProjectileRenderer::new);
        event.registerEntityRenderer(WATER_STREAM.get(), NoopRenderer::new);
        event.registerEntityRenderer(WATER_BOLT.get(), NoopRenderer::new);
        event.registerEntityRenderer(BALL_OF_FIRE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EFFECT_THROWN_POTION.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(GEO_SWORD_PROJECTILE.get(), SwordProjectileRenderer::new);
        event.registerEntityRenderer(ICE_BLADE_SWORD.get(), SwordProjectileRenderer::new);
        event.registerEntityRenderer(STAR_FURY.get(), SwordProjectileRenderer::new);
        event.registerEntityRenderer(ENCHANTED_SWORD.get(), SwordProjectileRenderer::new);
        event.registerEntityRenderer(LIGHTS_BANE.get(), SwordProjectileRenderer::new);
        event.registerEntityRenderer(GRASS.get(), SwordProjectileRenderer::new);
        event.registerEntityRenderer(BEE.get(), context -> new ForwardProjectileRenderer<>(context, new BeeProjectileModel<>(context.bakeLayer(BeeProjectileModel.LAYER_LOCATION)), Confluence.asResource("textures/entity/bee_projectile.png")));
        event.registerEntityRenderer(NIGHTS_EDGE.get(), SwordProjectileRenderer::new);
        event.registerEntityRenderer(BASE_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(BEE_ARROW.get(), context -> new ForwardProjectileRenderer<>(context, new BeeProjectileModel<>(context.bakeLayer(BeeProjectileModel.LAYER_LOCATION)), Confluence.asResource("textures/entity/bee_projectile.png")));
        event.registerEntityRenderer(HELL_BAT_ARROW.get(), context -> new GeoArrowRenderer(context, MonsterEntities.HELL_BAT.getId()));
        event.registerEntityRenderer(DRIVE_AWAY_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(FLAMING_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(UNHOLY_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(STAR_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(HELLFIRE_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(FROSTBURN_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(BONE_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(SHIMMER_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(FOSSIL_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(FLY_FISH_ARROW.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(DEVELOPER_ARROW.get(), TerraArrowRenderer::new);
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
        event.registerEntityRenderer(THROWN_KNIVE.get(), ThrownKniveProjectileRenderer::new);
        event.registerEntityRenderer(NPC_WEAPON_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(PIRATE_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(PIRATE_FLAMING_ARROW.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(PIRATE_CANNONBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CYBORG_EXPLOSIVE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BONE_THROWN_KNIVE.get(), BoneThrownKniveProjectileRenderer::new);
        event.registerEntityRenderer(FROST_DAGGERFISH.get(), FrostDaggerfishProjectileRenderer::new);
        event.registerEntityRenderer(DUNGEON_DEMON_BONE.get(), DungeonDemonBoneProjectileRenderer::new);
        event.registerEntityRenderer(JAVELIN.get(), c -> new SpearRenderer<>(c));
        event.registerEntityRenderer(HOPLITE_JAVELIN.get(), c -> new SpearRenderer<>(c));
        event.registerEntityRenderer(BOOMERANG_PROJECTILE.get(), BoomerangProjectileRenderer::new);
        event.registerEntityRenderer(SHURIKEN.get(), ShurikenProjectileRenderer::new);
        event.registerEntityRenderer(SPIKY_BALL.get(), SpikyBallProjectileRenderer::new);
        event.registerEntityRenderer(THROWN_WATER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SPARKLE_SLIME_BALLOON.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(MAGIC_DAGGER.get(), MagicDaggerRenderer::new);
        event.registerEntityRenderer(CRYSTAL_STORM.get(), NoopRenderer::new);
        event.registerEntityRenderer(CURSED_FLAMES.get(), NoopRenderer::new);
        event.registerEntityRenderer(FLOWER_PETAL.get(), NoopRenderer::new);
        event.registerEntityRenderer(HARPY_FEATHER.get(), HarpyFeatherProjectileRenderer::new);
        event.registerEntityRenderer(HOSTILE_DEMON_SCYTHE.get(), HostileDemonScytheProjectileRenderer::new);
        event.registerEntityRenderer(HORNET_STINGER.get(), HornetStingerProjectileRenderer::new);
        event.registerEntityRenderer(SKELETRON_SKULL.get(), SkullProjectileRenderer::new);
        event.registerEntityRenderer(NPC_SHADOWFLAME_SKULL.get(), SkullProjectileRenderer::new);
        event.registerEntityRenderer(HILL_LAVA_PILLAR.get(), NoopRenderer::new);
        event.registerEntityRenderer(WALL_OF_FLESH_LASER.get(), c -> new LaserProjectileRenderer<>(c, 0xDD52FF, 0xA238DD, 0x6E09A6));
        event.registerEntityRenderer(HILL_FIRE_BOUND.get(), NoopRenderer::new);
        event.registerEntityRenderer(DESTROYER_LASER.get(), c -> new LaserProjectileRenderer<>(c, 0xFFFFFF, 0xB5FFF0, 0x33E2DF));
        event.registerEntityRenderer(PRIME_LASER.get(), c -> new LaserProjectileRenderer<>(c, 0xFF0000, 0x8E0000, 0x4D0000));
        event.registerEntityRenderer(PLANTERA_SEED.get(), NoopRenderer::new);
        event.registerEntityRenderer(PLANTERA_THORN_BALL.get(), NoopRenderer::new);
        event.registerEntityRenderer(PLANTERA_SPORE.get(), NoopRenderer::new);
        event.registerEntityRenderer(RETINAZER_LASER.get(), c -> new LaserProjectileRenderer<>(c, 0xFF0000, 0x8E0000, 0x4D0000));
        event.registerEntityRenderer(SPAZMATISM_FLAME.get(), NoopRenderer::new);
        event.registerEntityRenderer(DARK_CASTER_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(RUNE_BLAST.get(), NoopRenderer::new);
        event.registerEntityRenderer(DESERT_SPIRIT_CURSE.get(), NoopRenderer::new);
        event.registerEntityRenderer(UNHOLY_TRIDENT.get(), UnholyTridentRenderer::new);
        event.registerEntityRenderer(CLINGER_FLAME.get(), NoopRenderer::new);
        event.registerEntityRenderer(FUNGI_SPORE.get(), NoopRenderer::new);
        event.registerEntityRenderer(CHAOS_BALL_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(SHADOW_BEAM_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(INFERNO_BOLT_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(LOST_SOUL_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(VILE_SPIT_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(FIRE_IMP_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(GASTROPOD_PROJECTILE.get(), c -> new LaserProjectileRenderer<>(c, 0xFFFFFF, 0xF091CF, 0xF44BB9));
        event.registerEntityRenderer(FROST_BEAM.get(), c -> new LaserProjectileRenderer<>(c, 0xFFFFFF, 0xA7E9FF, 0x408EFF));
        event.registerEntityRenderer(FROST_BLAST.get(), NoopRenderer::new);
        event.registerEntityRenderer(THROWN_ROCK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ANTLION_SAND_BALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SPIDER_WEB_SPIT.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(DANDELION_SEED.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/proj/angry_dandelion.geo.json"), Confluence.asResource("textures/entity/proj/angry_dandelion.png"), Confluence.asResource("animations/entity/proj/angry_dandelion.animation.json")), false, 1.0F, 0.0F));
        event.registerEntityRenderer(ICEWATER_SPIT.get(), NoopRenderer::new);
        event.registerEntityRenderer(PALADIN_HAMMER_PROJECTILE.get(), PaladinHammerProjectileRenderer::new);
        event.registerEntityRenderer(ANCIENT_LIGHT.get(), NoopRenderer::new);
        event.registerEntityRenderer(CULTIST_FIREBALL.get(), NoopRenderer::new);
        event.registerEntityRenderer(CULTIST_ICE_MIST.get(), NoopRenderer::new);
        event.registerEntityRenderer(CULTIST_LIGHTNING_ORB.get(), NoopRenderer::new);
        event.registerEntityRenderer(PRIME_CANNONBALL.get(), NoopRenderer::new);
        event.registerEntityRenderer(TITANIUM_SHARDS.get(), TitaniumShardsProjectileRenderer::new);
        event.registerEntityRenderer(SLIME_SPIKE.get(), SlimeSpikeProjectileRenderer::new);
        event.registerEntityRenderer(FALLING_STAR.get(), FallingStarRenderer::new);
        event.registerEntityRenderer(TREASURE_BAG.get(), TreasureBagRenderer::new);
        event.registerEntityRenderer(COIN_PORTAL.get(), NoopRenderer::new);
        event.registerEntityRenderer(THROWN_POWDER.get(), NoopRenderer::new);
        event.registerEntityRenderer(ROPE_COILS.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ICE_TOFU_BRICK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BODY_PART.get(), BodyPartRenderer::new);
        event.registerEntityRenderer(FLAME_CLOUD.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(SUPER_SPIKY_BALL.get(), SuperSpikyBallProjectileRenderer::new);
        event.registerEntityRenderer(SPEAR.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(BALL_OF_FROST.get(), NoopRenderer::new);
        event.registerEntityRenderer(DEMON_SCYTHE.get(), DemonScytheProjectileRenderer::new);
        event.registerEntityRenderer(SKULL.get(), SkullProjectileRenderer::new);
        event.registerEntityRenderer(BLOOD_CLOUD.get(), context -> new GeoNegativeVolumeRenderer<>(context, new BloodCloudProjectileModel(), false, 2.0F, -0.2F));
        event.registerEntityRenderer(BLOOD_RAIN.get(), context -> new RainProjectileRenderer<>(context, RainProjectileRenderer.BLOOD_RAIN));
        event.registerEntityRenderer(RAIN_CLOUD.get(), context -> new GeoNegativeVolumeRenderer<>(context, new RainCloudProjectileModel(), false, 2.0F, -0.2F));
        event.registerEntityRenderer(RAIN.get(), context -> new RainProjectileRenderer<>(context, RainProjectileRenderer.RAIN));
        event.registerEntityRenderer(NIMBUS_RAIN.get(), context -> new RainProjectileRenderer<>(context, RainProjectileRenderer.RAIN, -0.5F));
        event.registerEntityRenderer(STORM_SPEAR_SHOT.get(), context -> new SpearProjectileRenderer(context, SpearProjectileModels.STORM));
        event.registerEntityRenderer(SPORE_CLOUD.get(), NoopRenderer::new);//todo 贴图模型粒子
        event.registerEntityRenderer(NORTH_POLE.get(), context -> new SpearProjectileRenderer(context, SpearProjectileModels.NORTH_POLE));
        event.registerEntityRenderer(NORTH_POLE_SUB.get(), NoopRenderer::new);
        event.registerEntityRenderer(GHASTLY.get(), context -> new SpearProjectileRenderer(context, SpearProjectileModels.GHASTLY));
        event.registerEntityRenderer(MUSHROOM.get(), context -> new SpearProjectileRenderer(context, SpearProjectileModels.MUSHROOM));
        event.registerEntityRenderer(GOLDEN_SHOWER.get(), NoopRenderer::new);
        event.registerEntityRenderer(MAGIC_MISSILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(FLAMELASH.get(), NoopRenderer::new);
        event.registerEntityRenderer(RAINBOW.get(), NoopRenderer::new); // todo 粒子
        event.registerEntityRenderer(SKY_FRACTURE.get(), NoopRenderer::new); // todo 模型
        event.registerEntityRenderer(CRYSTAL_CHARGE_1.get(), NoopRenderer::new); // todo 粒子
        event.registerEntityRenderer(CRYSTAL_CHARGE_2.get(), NoopRenderer::new); // todo 粒子

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
        event.registerEntityRenderer(FLAIL_ENTITY.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(GUARDIAN_FLAIL_ENTITY.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(ANCIENT_GUARDIAN_FLAIL_ENTITY.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(FLOWER_POWER_FLAIL.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(DRIPPLER_CRIPPLER_FLAIL.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(FLAIRON_FLAIL.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(CHAIN_KNIFE_FLAIL.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(ANCHOR_FLAIL.get(), BaseFlailRenderer::new);
        event.registerEntityRenderer(FLOWER_POWER_PETAL.get(), FlailAuxiliaryProjectileRenderer::new);
        event.registerEntityRenderer(DRIPPLER_CRIPPLER_PROJECTILE.get(), FlailAuxiliaryProjectileRenderer::new);
        event.registerEntityRenderer(FLAIRON_BUBBLE.get(), FlailAuxiliaryProjectileRenderer::new);
        event.registerEntityRenderer(YOYO.get(), YoyoRenderer::new);

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
//        event.registerEntityRenderer(INVERSE_ENDERMAN.get(), EndermanRenderer::new);

        // Critter renderers — Bunny 保留自定义模型，其余用 CritterRenderer
        event.registerEntityRenderer(CritterEntities.BUNNY.get(), BunnyRenderer::new);
        event.registerEntityRenderer(CritterEntities.PENGUIN.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.MYSTIC_FROG.get(), net.minecraft.client.renderer.entity.FrogRenderer::new);
        event.registerEntityRenderer(CritterEntities.GOLDFISH.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/animal/goldfish.geo.json"), Confluence.asResource("textures/entity/animal/goldfish.png"), Confluence.asResource("animations/entity/animal/goldfish.animation.json")), false, 1.0F, 0.1875F));
        // penguin.bbmodel 的三个变种共用模型和动画，差异仅在贴图。
        event.registerEntityRenderer(MonsterEntities.CORRUPT_PENGUIN.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/animal/penguin.geo.json"), Confluence.asResource("textures/entity/corrupt_penguin.png"), Confluence.asResource("animations/entity/animal/penguin.animation.json"))));
        event.registerEntityRenderer(MonsterEntities.VICIOUS_PENGUIN.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/animal/penguin.geo.json"), Confluence.asResource("textures/entity/vicious_penguin.png"), Confluence.asResource("animations/entity/animal/penguin.animation.json"))));
        event.registerEntityRenderer(CritterEntities.JEWEL_BUNNY.get(), BunnyRenderer::new);
        event.registerEntityRenderer(CritterEntities.EXPLOSIVE_BUNNY.get(), BunnyRenderer::new);
        event.registerEntityRenderer(CritterEntities.HOSTILE_BUNNY.get(), BunnyRenderer::new);
        event.registerEntityRenderer(CritterEntities.BIRD.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.BLUE_JAY.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.CARDINAL.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.SQUIRREL.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.RED_SQUIRREL.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.JEWEL_SQUIRREL.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.DUCK.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.CRAB.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.WORM.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.BUTTERFLY.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.FAIRY.get(), c -> FullbrightGeoRenderer.<Fairy>forCritter(c).setBoneToGlow(List.of("Outline", "Outline2", "Outline3", "Outline4", "Outline5"), List.of("Body", "Internal", "Internal2", "Internal3", "Internal4")));
        event.registerEntityRenderer(CritterEntities.FEALING.get(), FealingRenderer::new);
        event.registerEntityRenderer(CritterEntities.GLOWING_SNAIL.get(), SnailRenderer::new);
        event.registerEntityRenderer(CritterEntities.GRUBBY.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.BUGGY.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.GLOWING_MOOSHROOM.get(), c -> new MushroomAnimalRenderer<>(c, Confluence.asResource("animal/glowing_mooshroom")));
        event.registerEntityRenderer(CritterEntities.CLOUD_SHEEP.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.CLUCKSHROOM.get(), c -> new MushroomAnimalRenderer<>(c, Confluence.asResource("animal/cluckshroom")));
        event.registerEntityRenderer(CritterEntities.GLOWING_CLUCKSHROOM.get(), c -> new MushroomAnimalRenderer<>(c, Confluence.asResource("animal/cluckshroom")));
        event.registerEntityRenderer(CritterEntities.FIREFLY.get(), FullbrightGeoRenderer::forCritter);
        event.registerEntityRenderer(CritterEntities.LIGHTNING_BUG.get(), FullbrightGeoRenderer::forCritter);
        event.registerEntityRenderer(CritterEntities.TRUFFLE_WORM.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.MAGGOT.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.SCORPION.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.HELL_BUTTERFLY.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.PRISMATIC_LACEWING.get(), c -> FullbrightGeoRenderer.<org.confluence.mod.common.entity.animal.PrismaticLacewing>forCritter(c).withNoCull());
        event.registerEntityRenderer(CritterEntities.DRAGONFLY.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.GRASSHOPPER.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.STINKBUG.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.LADYBUG.get(), CritterRenderer::new);
        event.registerEntityRenderer(CritterEntities.MAGMA_SNAIL.get(), SnailRenderer::new);
        event.registerEntityRenderer(CritterEntities.SLUGGY.get(), c -> new GeoNormalRenderer<>(c, Confluence.asResource("animal/sluggy")));
        event.registerEntityRenderer(CritterEntities.SNAIL.get(), SnailRenderer::new);
        // 所有史莱姆共用泰拉瑞亚风格的内外层几何结构，仅按具体种类切换纹理。
        event.registerEntityRenderer(MonsterEntities.GREEN_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "green"));
        event.registerEntityRenderer(MonsterEntities.BLUE_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "blue"));
        event.registerEntityRenderer(MonsterEntities.JUNGLE_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "jungle"));
        event.registerEntityRenderer(MonsterEntities.PURPLE_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "purple"));
        event.registerEntityRenderer(MonsterEntities.GREEN_DUMPLING_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "green_dumpling"));
        event.registerEntityRenderer(MonsterEntities.SWAMP_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "swamp"));
        event.registerEntityRenderer(MonsterEntities.DESERT_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "desert"));
        event.registerEntityRenderer(MonsterEntities.RED_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "red"));
        event.registerEntityRenderer(MonsterEntities.YELLOW_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "yellow"));
        event.registerEntityRenderer(MonsterEntities.DUNGEON_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "dungeon"));
        event.registerEntityRenderer(MonsterEntities.PINK_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "pink"));
        event.registerEntityRenderer(MonsterEntities.ICE_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "ice"));
        event.registerEntityRenderer(MonsterEntities.LAVA_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "lava"));
        event.registerEntityRenderer(MonsterEntities.TROPIC_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "tropic"));
        event.registerEntityRenderer(MonsterEntities.CORRUPT_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "corrupted"));
        event.registerEntityRenderer(MonsterEntities.SLIMELING.get(), c -> new BaseSlimeRenderer<>(c, "corrupted"));
        event.registerEntityRenderer(MonsterEntities.WINGLESS_SLIMER.get(), c -> new BaseSlimeRenderer<>(c, "corrupted"));
        event.registerEntityRenderer(MonsterEntities.CRIMSLIME.get(), c -> new BaseSlimeRenderer<>(c, "crimson"));
        event.registerEntityRenderer(MonsterEntities.LUMINOUS_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "luminous"));
        event.registerEntityRenderer(MonsterEntities.BLACK_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "black"));
        event.registerEntityRenderer(MonsterEntities.MOTHER_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "black"));
        event.registerEntityRenderer(MonsterEntities.BABY_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "black"));
        event.registerEntityRenderer(MonsterEntities.SWEET_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "honey"));
        event.registerEntityRenderer(MonsterEntities.GOLDEN_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "gold"));
        event.registerEntityRenderer(MonsterEntities.FLESH_SLIME.get(), c -> new BaseSlimeRenderer<>(c, "flesh"));
        event.registerEntityRenderer(MonsterEntities.SPIKED_SLIME.get(), c -> new GeoSpecialSlimeRenderer<>(c, Confluence.asResource("slime/spiked_slime")));
        event.registerEntityRenderer(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), c -> new GeoSpecialSlimeRenderer<>(c, Confluence.asResource("slime/spiked_jungle_slime")));
        event.registerEntityRenderer(MonsterEntities.SPIKED_ICE_SLIME.get(), c -> new GeoSpecialSlimeRenderer<>(c, Confluence.asResource("slime/spiked_ice_slime")));
        event.registerEntityRenderer(MonsterEntities.DEMON_EYE.get(), DemonEyeRenderer::new);
        event.registerEntityRenderer(MonsterEntities.HARPY.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.HARPY.getId()));
        event.registerEntityRenderer(MonsterEntities.PIXIE.get(), c -> new FullbrightGeoRenderer<>(c, MonsterEntities.PIXIE.getId()).setBoneToGlow(List.of("Outline", "Outline2", "Outline3"), List.of("bone", "bone2", "bone3")));
        event.registerEntityRenderer(MonsterEntities.EATER_OF_SOULS.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.EATER_OF_SOULS.getId(), true, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.CRIMERA.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.CRIMERA.getId(), true, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.CURSED_SKULL.get(), c -> new GeoNegativeVolumeRenderer<>(c, new GeoNormalModel<>(MonsterEntities.CURSED_SKULL.getId()), true, 1.0F, 0.0F).addBoneToGlow("outline"));
        event.registerEntityRenderer(MonsterEntities.CORRUPTOR.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.CORRUPTOR.getId(), true, 1.15F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.SLIMER.get(), c -> new GeoSpecialSlimeRenderer<>(c, MonsterEntities.SLIMER.getId(), true));
        event.registerEntityRenderer(MonsterEntities.ENCHANTED_SWORD.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(MonsterEntities.SNATCHER.get(), c -> new SnatcherRenderer(c, MonsterEntities.SNATCHER.getId()));
        event.registerEntityRenderer(MonsterEntities.MAN_EATER.get(), c -> new SnatcherRenderer(c, MonsterEntities.MAN_EATER.getId()));
        event.registerEntityRenderer(MonsterEntities.SPORE_SKELETON.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.SPORE_SKELETON.getId()));
        event.registerEntityRenderer(MonsterEntities.BASE_BONES.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.BASE_BONES.getId())).withScale(0.9F));
        event.registerEntityRenderer(MonsterEntities.ANGER_BONES.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.ANGER_BONES.getId())).withScale(0.9F));
        event.registerEntityRenderer(MonsterEntities.SHORT_BONES.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.SHORT_BONES.getId())).withScale(0.8F));
        event.registerEntityRenderer(MonsterEntities.BIG_BONES.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.ANGER_BONES.getId())).withScale(1.1F));
        event.registerEntityRenderer(MonsterEntities.BIG_ANGER_BONES.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.BIG_ANGER_BONES.getId())).withScale(1.15F));
        event.registerEntityRenderer(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.BIG_MUSCLE_ANGER_BONES.getId())).withScale(1.2F));
        event.registerEntityRenderer(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.BIG_HELMET_ANGER_BONES.getId())).withScale(1.25F));
        event.registerEntityRenderer(MonsterEntities.UNDEAD_VIKING.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.UNDEAD_VIKING.getId()));
        event.registerEntityRenderer(MonsterEntities.ARMORED_VIKING.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/armored_viking.geo.json"), Confluence.asResource("textures/entity/armored_viking.png"), true)));
        event.registerEntityRenderer(MonsterEntities.ARMORED_SKELETON.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/armored_skeleton.geo.json"), Confluence.asResource("textures/entity/armored_skeleton.png"), true)));
        event.registerEntityRenderer(MonsterEntities.ICE_ELEMENTAL.get(), c -> new FullbrightGeoRenderer<IceElemental>(c, MonsterEntities.ICE_ELEMENTAL.getId()));
        event.registerEntityRenderer(MonsterEntities.ICY_MERMAN.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/icy_merman.geo.json"), Confluence.asResource("textures/entity/icy_merman.png"), true), false, 1.0F, 0.015625F));
        event.registerEntityRenderer(MonsterEntities.ICE_GOLEM.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ICE_GOLEM.getId(), false, 1.0F, -0.021875F));
        // giant_tortoise.bbmodel 内的两种陆龟共用骨架与动画，使用各自贴图。
        event.registerEntityRenderer(MonsterEntities.GIANT_TORTOISE.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GIANT_TORTOISE.getId()));
        event.registerEntityRenderer(MonsterEntities.ICE_TORTOISE.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/giant_tortoise.geo.json"), Confluence.asResource("textures/entity/ice_tortoise.png"), Confluence.asResource("animations/entity/giant_tortoise.animation.json"))));
        event.registerEntityRenderer(MonsterEntities.UNICORN.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.UNICORN.getId()));
        event.registerEntityRenderer(MonsterEntities.GASTROPOD.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GASTROPOD.getId(), false, 1.0F, -0.1875F));
        event.registerEntityRenderer(MonsterEntities.WORM_SEGMENT.get(), WormPartRenderer::new);
        event.registerEntityRenderer(MonsterEntities.WYVERN.get(), c -> new WyvernRenderer<>(c, 1.0F));
        event.registerEntityRenderer(MonsterEntities.ARCH_WYVERN.get(), c -> new WyvernRenderer<>(c, 1.25F));
        event.registerEntityRenderer(MonsterEntities.DEVOURER.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.DEVOURER.getId(), 2.0F));
        event.registerEntityRenderer(MonsterEntities.WORLD_FEEDER.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.WORLD_FEEDER.getId(), 1.0F));
        event.registerEntityRenderer(MonsterEntities.TOMB_CRAWLER.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.TOMB_CRAWLER.getId(), 2.0F));
        event.registerEntityRenderer(MonsterEntities.GIANT_WORM.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.GIANT_WORM.getId(), 2.0F));
        event.registerEntityRenderer(MonsterEntities.DIGGER.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.DIGGER.getId(), 1.0F));
        event.registerEntityRenderer(MonsterEntities.LEECH.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.LEECH.getId(), 2.0F));
        event.registerEntityRenderer(MonsterEntities.BONE_SERPENT.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.BONE_SERPENT.getId(), 2.0F));
        event.registerEntityRenderer(MonsterEntities.WITHER_BONE_SERPENT.get(), c -> new WormHeadRenderer<>(c, MonsterEntities.WITHER_BONE_SERPENT.getId(), 2.0F));
        event.registerEntityRenderer(MonsterEntities.DARK_CASTER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.DARK_CASTER.getId()));
        event.registerEntityRenderer(MonsterEntities.TIM.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.TIM.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.DOCTOR_BONES.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.DOCTOR_BONES.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.THE_GROOM.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/the_groom.geo.json"), Confluence.asResource("textures/entity/the_groom.png"), true), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.THE_BRIDE.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/the_bride.geo.json"), Confluence.asResource("textures/entity/the_bride.png"), true), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ZOMBIE_MERMAN.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/zombie_merman.geo.json"), Confluence.asResource("textures/entity/zombie_merman.png"), true), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.GOBLIN_SORCERER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GOBLIN_SORCERER.getId().withPrefix("goblin/")));
        event.registerEntityRenderer(MonsterEntities.GOBLIN_WARLOCK.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GOBLIN_WARLOCK.getId()));
        event.registerEntityRenderer(MonsterEntities.PIRATE_CROSSBOWER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.PIRATE_CROSSBOWER.getId()));
        event.registerEntityRenderer(MonsterEntities.PIRATE_CORSAIR.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.PIRATE_CORSAIR.getId()));
        event.registerEntityRenderer(MonsterEntities.PIRATE_CAPTAIN.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.PIRATE_CAPTAIN.getId()));
        event.registerEntityRenderer(MonsterEntities.PIRATE_PARROT.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.PIRATE_PARROT.getId()));
        event.registerEntityRenderer(MonsterEntities.PIRATES_CURSE.get(), c -> new FullbrightGeoRenderer<PirateFlyingMonster>(c, MonsterEntities.PIRATES_CURSE.getId(), -0.75F));
        event.registerEntityRenderer(MonsterEntities.PIRATE_DEADEYE.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/pirate_deadeye.geo.json"), Confluence.asResource("textures/entity/pirate_deadeye.png"), true)));
        event.registerEntityRenderer(MonsterEntities.PIRATE_DECKHAND.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/pirate_corsair.geo.json"), Confluence.asResource("textures/entity/pirate_deckhand.png"), Confluence.asResource("animations/entity/pirate_corsair.animation.json"))));
        event.registerEntityRenderer(MonsterEntities.SHADOWFLAME_APPARITION.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.SHADOWFLAME_APPARITION.getId()));
        event.registerEntityRenderer(MonsterEntities.CHAOS_ELEMENTAL.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.CHAOS_ELEMENTAL.getId()));
        event.registerEntityRenderer(MonsterEntities.NECROMANCER.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(MonsterEntities.DIABOLIST.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(MonsterEntities.RAGGED_CASTER.get(), MissingModelRenderer::new); // todo 专用模型
        // Boss
        event.registerEntityRenderer(BossEntities.KING_SLIME.get(), KingSlimeRenderer::new);
        event.registerEntityRenderer(BossEntities.CROWN_OF_KING_SLIME_MODEL.get(), CrownOfKingSlimeModelRenderer::new);
        event.registerEntityRenderer(BossEntities.EYE_OF_CTHULHU.get(), EyeOfCthulhuRenderer::new);
        event.registerEntityRenderer(BossEntities.SERVANT_OF_CTHULHU.get(), c -> new BossGeoRenderer<>(c, Confluence.asResource("servant_of_cthulhu")));
        event.registerEntityRenderer(BossEntities.EATER_OF_WORLDS_SEGMENT.get(), BossWormPartRenderer::new);
        event.registerEntityRenderer(BossEntities.THE_DESTROYER_PART.get(), MissingModelRenderer::new); // todo 专用身体、尾部和受损资源已导出，待适配体节连接与侧翼展开状态
        event.registerEntityRenderer(BossEntities.EATER_OF_WORLDS.get(), c -> new BossGeoRenderer<>(c, Confluence.asResource("boss/eater_of_worlds"), true, 2.2F, 0.0F));
        event.registerEntityRenderer(BossEntities.QUEEN_BEE.get(), QueenBeeRenderer::new);
        event.registerEntityRenderer(BossEntities.BRAIN_OF_CTHULHU.get(), BrainOfCthulhuRenderer::new);
        event.registerEntityRenderer(BossEntities.BRAIN_FAKE.get(), BrainFakeRenderer::new);
        event.registerEntityRenderer(BossEntities.SKELETRON.get(), SkeletronBossRenderer::new);
        event.registerEntityRenderer(BossEntities.SKELETRON_HAND.get(), SkeletronBossHandRenderer::new);
        event.registerEntityRenderer(BossEntities.DUNGEON_GUARDIAN.get(), DungeonGuardianRenderer::new);
        event.registerEntityRenderer(BossEntities.THE_DESTROYER.get(), MissingModelRenderer::new); // todo 专用头部资源已导出，待与体节一起适配
        event.registerEntityRenderer(BossEntities.THE_DESTROYER_PROBE.get(), c -> new BossGeoRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/boss/the_destroyer_probe.geo.json"), Confluence.asResource("textures/entity/boss/the_destroyer_probe.png"), null)));
        event.registerEntityRenderer(BossEntities.RETINAZER.get(), TwinEyeDissolveRenderer::retinazer);
        event.registerEntityRenderer(BossEntities.SPAZMATISM.get(), TwinEyeDissolveRenderer::spazmatism);
        // 双子魔眼控制实体只负责生命周期，两只眼睛实体仍各自独立渲染。
        event.registerEntityRenderer(BossEntities.THE_TWINS.get(), TheTwinsRenderer::new);
        event.registerEntityRenderer(BossEntities.SKELETRON_PRIME.get(), SkeletronPrimeBossRenderer::new);
        event.registerEntityRenderer(BossEntities.SKELETRON_PRIME_PART.get(), SkeletronPrimeArmRenderer::new);
        event.registerEntityRenderer(BossEntities.WALL_OF_FLESH.get(), WallOfFleshRenderer::new);
        event.registerEntityRenderer(BossEntities.WALL_OF_FLESH_EYE.get(), NoopRenderer::new);
        event.registerEntityRenderer(BossEntities.WALL_OF_FLESH_MOUTH.get(), NoopRenderer::new);
        event.registerEntityRenderer(BossEntities.PLANTERA.get(), MissingModelRenderer::new); // todo plantera_1 资源已导出，普通与狂暴花瓣的阶段位置仍待适配
        event.registerEntityRenderer(BossEntities.PLANTERA_HOOK.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(BossEntities.PLANTERA_TENTACLE.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(BossEntities.LUNATIC_CULTIST.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(BossEntities.LUNATIC_CULTIST_CLONE.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(BossEntities.PHANTASM_DRAGON.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(BossEntities.DEERCLOPS.get(), DeerclopsRenderer::new);
        event.registerEntityRenderer(THROWN_ICE_PROJECTILE.get(), DeerclopsThrownIceRenderer::new);
        event.registerEntityRenderer(ICE_PILLAR.get(), DeerclopsIcePillarRenderer::new);
        event.registerEntityRenderer(PHASEBLADE_PROJECTILE.get(), PhasebladeProjectileRenderer::new);
        event.registerEntityRenderer(PHASESABER_PROJECTILE.get(), PhasebladeProjectileRenderer::new);
        event.registerEntityRenderer(SHADOW_HAND.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/proj/shadow_hand.geo.json"), Confluence.asResource("textures/entity/proj/shadow_hand.png"), null)));
        event.registerEntityRenderer(BossEntities.HILL_OF_FLESH.get(), HillOfFleshRenderer::new);
        // 父实体模型已经包含全部眼睛和嘴部网格，这些子实体只承担命中判定，不单独渲染。
        event.registerEntityRenderer(BossEntities.HILL_OF_FLESH_EYE.get(), NoopRenderer::new);
        event.registerEntityRenderer(BossEntities.HILL_OF_FLESH_MOUTH.get(), NoopRenderer::new);
        event.registerEntityRenderer(BossEntities.PRIME_ENDER_DRAGON.get(), PrimeEnderDragonRenderer::new);
        event.registerEntityRenderer(BossEntities.PRIME_ENDER_DRAGON_PART.get(), NoopRenderer::new);
        // NPC
        event.registerEntityRenderer(NpcEntities.GUIDE.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/guide")));
        event.registerEntityRenderer(NpcEntities.MERCHANT.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/merchant")));
        event.registerEntityRenderer(NpcEntities.NURSE.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/nurse")));
        event.registerEntityRenderer(NpcEntities.DEMOLITIONIST.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/demolitionist")));
        event.registerEntityRenderer(NpcEntities.DYE_TRADER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/dye_trader")));
        event.registerEntityRenderer(NpcEntities.PAINTER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/painter")));
        event.registerEntityRenderer(NpcEntities.DRYAD.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/dryad")));
        event.registerEntityRenderer(NpcEntities.ARMS_DEALER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/arms_dealer")));
        event.registerEntityRenderer(NpcEntities.GOBLIN_TINKERER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/goblin_tinkerer")));
        event.registerEntityRenderer(NpcEntities.WITCH_DOCTOR.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/witch_doctor")));
        event.registerEntityRenderer(NpcEntities.CLOTHIER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/clothier")));
        event.registerEntityRenderer(NpcEntities.MECHANIC.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/mechanic")));
        event.registerEntityRenderer(NpcEntities.PARTY_GIRL.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/party_girl")));
        event.registerEntityRenderer(NpcEntities.STYLIST.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/stylist")));
        event.registerEntityRenderer(NpcEntities.TAX_COLLECTOR.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/tax_collector")));
        event.registerEntityRenderer(NpcEntities.TRUFFLE.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/truffle")));
        event.registerEntityRenderer(NpcEntities.WIZARD.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/wizard")));
        event.registerEntityRenderer(NpcEntities.ZOOLOGIST.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/zoologist")));
        event.registerEntityRenderer(NpcEntities.ANGLER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/angler")));
        event.registerEntityRenderer(NpcEntities.FEMALE_ANGLER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/female_angler")).withScale(0.78F));
        event.registerEntityRenderer(NpcEntities.TRAVELING_MERCHANT.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/traveling_merchant")));
        event.registerEntityRenderer(NpcEntities.OLD_MAN.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/old_man")));
        event.registerEntityRenderer(NpcEntities.STEAMPUNKER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/steampunker")));
        event.registerEntityRenderer(NpcEntities.CYBORG.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/cyborg")));
        event.registerEntityRenderer(NpcEntities.GOLFER.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/golfer")));
        event.registerEntityRenderer(NpcEntities.NERDY_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/nerdy_slime")));
        event.registerEntityRenderer(NpcEntities.COOL_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/cool_slime")));
        event.registerEntityRenderer(NpcEntities.ELDER_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/elder_slime")));
        event.registerEntityRenderer(NpcEntities.CLUMSY_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/clumsy_slime")));
        event.registerEntityRenderer(NpcEntities.DIVA_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/diva_slime")));
        event.registerEntityRenderer(NpcEntities.SURLY_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/surly_slime")));
        event.registerEntityRenderer(NpcEntities.MYSTIC_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/mystic_slime")));
        event.registerEntityRenderer(NpcEntities.SQUIRE_SLIME.get(), c -> new TownSlimeRenderer(c, Confluence.asResource("npc/squire_slime")));
        event.registerEntityRenderer(NpcEntities.SKELETON_MERCHANT.get(), c -> new NPCEntityRenderer<>(c, Confluence.asResource("npc/skeleton_merchant")));
        // 蝙蝠
        event.registerEntityRenderer(MonsterEntities.CAVE_BAT.get(), c -> new GeoNormalRenderer<CaveBat>(c, MonsterEntities.CAVE_BAT.getId()).withNoCull());
        event.registerEntityRenderer(MonsterEntities.JUNGLE_BAT.get(), c -> new GeoNormalRenderer<CaveBat>(c, MonsterEntities.JUNGLE_BAT.getId()).withNoCull());
        event.registerEntityRenderer(MonsterEntities.ICE_BAT.get(), c -> new GeoNormalRenderer<CaveBat>(c, MonsterEntities.ICE_BAT.getId()).withNoCull());
        event.registerEntityRenderer(MonsterEntities.GIANT_BAT.get(), c -> new GeoNormalRenderer<CaveBat>(c, MonsterEntities.GIANT_BAT.getId()).withNoCull());
        event.registerEntityRenderer(MonsterEntities.GIANT_FLYING_FOX.get(), c -> new GeoNormalRenderer<CaveBat>(c, new GeoNormalModel<>(MonsterEntities.GIANT_FLYING_FOX.getId(), false)).withNoCull());
        event.registerEntityRenderer(MonsterEntities.HELL_BAT.get(), c -> new GeoNormalRenderer<CaveBat>(c, MonsterEntities.HELL_BAT.getId()).withNoCull());
        event.registerEntityRenderer(MonsterEntities.LAVA_BAT.get(), c -> new BatRenderer(c, MonsterEntities.LAVA_BAT.getId(), false));
        event.registerEntityRenderer(MonsterEntities.ILLUMINANT_BAT.get(), c -> new BatRenderer(c, MonsterEntities.ILLUMINANT_BAT.getId(), true));
        event.registerEntityRenderer(MonsterEntities.SPORE_BAT.get(), c -> new GeoNormalRenderer<CaveBat>(c, MonsterEntities.SPORE_BAT.getId()).withNoCull());
        event.registerEntityRenderer(MonsterEntities.DRIPPLER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.DRIPPLER.getId(), false, 2.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.FLYING_FISH.get(), c -> new GeoNormalRenderer<FlyingFishMonster>(c, MonsterEntities.FLYING_FISH.getId(), true, 0.75F, 0.0F).withNoCull());
        // 眼球鱼的 Head 与 body 是两个独立根骨骼，不是可单独转动的人形头部。
        // 若使用 GeoNormalModel 默认的转头映射，Head 会绕自身枢轴脱离身体。
        event.registerEntityRenderer(MonsterEntities.WANDERING_EYE_FISH.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(MonsterEntities.WANDERING_EYE_FISH.getId(), false), true, 1.2F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.VISUAL_NEURON.get(), c -> new GeoNormalRenderer<>(c, Confluence.asResource("visual_neuron"), true, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.DEMON.get(), c -> new DemonRenderer(c, MonsterEntities.DEMON.getId(), 1.0F));
        event.registerEntityRenderer(MonsterEntities.VOODOO_DEMON.get(), c -> new DemonRenderer(c, MonsterEntities.VOODOO_DEMON.getId(), 1.1F));
        event.registerEntityRenderer(MonsterEntities.HORNET.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(MonsterEntities.HORNET.getId(), false), true, 1.0F, 0.2F));
        event.registerEntityRenderer(MonsterEntities.MOSS_HORNET.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(MonsterEntities.MOSS_HORNET.getId(), false), true, 1.0F, -0.085108F));
        event.registerEntityRenderer(MonsterEntities.LITTLE_HORNET.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(MonsterEntities.LITTLE_HORNET.getId(), false), true, 1.0F, 0.1F));
        event.registerEntityRenderer(MonsterEntities.FIRE_IMP.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.FIRE_IMP.getId()));
        event.registerEntityRenderer(MonsterEntities.DECAYEDER.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaSkeletonGeoModel<>(c, MonsterEntities.DECAYEDER.getId())));
        event.registerEntityRenderer(MonsterEntities.GHOST.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GHOST.getId()));
        event.registerEntityRenderer(MonsterEntities.DUNGEON_SPIRIT.get(), c -> new GeoNormalRenderer<DungeonSpirit>(c, MonsterEntities.DUNGEON_SPIRIT.getId(), false, 1.0F, 0.015625F) {
            @Override
            protected int getBlockLightLevel(DungeonSpirit entity, BlockPos pos) {
                return 15;
            }
        });
        event.registerEntityRenderer(MonsterEntities.DERPLING.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.DERPLING.getId()));
        event.registerEntityRenderer(MonsterEntities.HERPLING.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.HERPLING.getId()));
        event.registerEntityRenderer(MonsterEntities.METEOR_HEAD.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.METEOR_HEAD.getId(), true, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.GRANITE_ELEMENTAL.get(), c -> new GeoNegativeVolumeRenderer<>(c, new GeoNormalModel<>(MonsterEntities.GRANITE_ELEMENTAL.getId()), true, 1.0F, 0.0F).addBoneToGlow("Core"));
        event.registerEntityRenderer(MonsterEntities.GRANITE_GOLEM.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GRANITE_GOLEM.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.HOPLITE.get(), c -> new GeoNormalRenderer<>(c, new HopliteModel(c)));
        event.registerEntityRenderer(MonsterEntities.DESERT_SPIRIT.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.DESERT_SPIRIT.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.WALL_CREEPER.get(), c -> new GeoNormalRenderer<>(c, new SpiderSetModel("wall_creeper")).withNoCull());
        event.registerEntityRenderer(MonsterEntities.BLACK_RECLUSE.get(), c -> new GeoNormalRenderer<>(c, new SpiderSetModel("spider/black_recluse")).withNoCull());
        event.registerEntityRenderer(MonsterEntities.JUNGLE_CREEPER.get(), c -> new GeoNormalRenderer<>(c, new SpiderSetModel("spider/jungle")).withNoCull());
        event.registerEntityRenderer(MonsterEntities.WATER_BOLT_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.WATER_BOLT_MIMIC.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.RUNE_WIZARD.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.RUNE_WIZARD.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ANGRY_DANDELION.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ANGRY_DANDELION.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ANTLION_SWARMER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ANTLION_SWARMER.getId(), true, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ANTLION_CHARGER.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/antlion.geo.json"), Confluence.asResource("textures/entity/antlion.png"), Confluence.asResource("animations/entity/antlion_charger.animation.json")), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ANTLION.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ANTLION.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ANTLION_LARVA.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ANTLION_LARVA.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.GNOME.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GNOME.getId()));
        event.registerEntityRenderer(MonsterEntities.ANGRY_NIMBUS.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ANGRY_NIMBUS.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.RED_DEVIL.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.RED_DEVIL.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.WINDY_BALLOON.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.WINDY_BALLOON.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.OLD_SHAKING_CHEST.get(), OldShakingChestRenderer::new);
        event.registerEntityRenderer(MonsterEntities.CLUMSY_BALLOON_SLIME.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(MonsterEntities.ANGRY_TUMBLER.get(), c -> new GeoNormalRenderer<>(c, new AngryTumblerModel(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.SAND_SHARK.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.SAND_SHARK.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.BONE_BITER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.BONE_BITER.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.FLESH_REAVER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.FLESH_REAVER.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.CRYSTAL_THRESHER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.CRYSTAL_THRESHER.getId(), false, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.FUNGI_BULB.get(), c -> new TetheredPlantRenderer(c, MonsterEntities.FUNGI_BULB.getId()));
        event.registerEntityRenderer(MonsterEntities.GIANT_FUNGI_BULB.get(), c -> new TetheredPlantRenderer(c, MonsterEntities.GIANT_FUNGI_BULB.getId()));
        event.registerEntityRenderer(MonsterEntities.CLINGER.get(), c -> new TetheredPlantRenderer(c, MonsterEntities.CLINGER.getId()));
        event.registerEntityRenderer(MonsterEntities.GIANT_ANTLION_SWARMER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ANTLION_SWARMER.getId(), true, 1.25F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.THE_HUNGRY.get(), HungryRenderer::new);
        event.registerEntityRenderer(MonsterEntities.HILL_HUNGRY.get(), HungryRenderer::new);
        event.registerEntityRenderer(MonsterEntities.BLOOD_ZOMBIE.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaZombieGeoModel<>(c, MonsterEntities.BLOOD_ZOMBIE.getId())));
        event.registerEntityRenderer(MonsterEntities.SNOW_FLINX.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.SNOW_FLINX.getId()) {
            @Override
            protected void adjustPose(PoseStack poseStack, BaseWarriorMonster entity, BakedGeoModel model, float partialTick) {
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F + Mth.lerp(partialTick, entity.yBodyRotO - entity.yHeadRotO, entity.yBodyRot - entity.yHeadRot)));
            }
        });
        event.registerEntityRenderer(MonsterEntities.FACE_MONSTER.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.FACE_MONSTER.getId(), "RightArm", "LeftArm")));
        event.registerEntityRenderer(MonsterEntities.BLOOD_TUMORS.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.BLOOD_TUMORS.getId()));
        event.registerEntityRenderer(MonsterEntities.WEREWOLF.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/entity/werewolf.geo.json"), Confluence.asResource("textures/entity/werewolf.png"), true), false, 1.0F, -0.015625F));
        event.registerEntityRenderer(MonsterEntities.POSSESS_ARMOR.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/item/armor/possessed_armor.geo.json"), Confluence.asResource("textures/item/armor/possessed_armor.png"), true)));
        event.registerEntityRenderer(MonsterEntities.MUMMY.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.MUMMY.getId(), "RightArm", "LeftArm")).withCutout());
        event.registerEntityRenderer(MonsterEntities.DARK_MUMMY.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.DARK_MUMMY.getId(), "RightArm", "LeftArm")).withCutout());
        event.registerEntityRenderer(MonsterEntities.BLOOD_MUMMY.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.BLOOD_MUMMY.getId(), "RightArm", "LeftArm")).withCutout());
        event.registerEntityRenderer(MonsterEntities.LIGHT_MUMMY.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.LIGHT_MUMMY.getId(), "RightArm", "LeftArm")).withCutout());
        event.registerEntityRenderer(MonsterEntities.DARK_LAMIA.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.DARK_LAMIA.getId(), "right_arm", "left_arm")));
        event.registerEntityRenderer(MonsterEntities.LIGHT_LAMIA.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.LIGHT_LAMIA.getId(), "right_arm", "left_arm")));
        event.registerEntityRenderer(MonsterEntities.GHOUL.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<>(MonsterEntities.GHOUL.getId(), "hand_right", "hand_left")));
        event.registerEntityRenderer(MonsterEntities.BASILISK.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(MonsterEntities.BASILISK.getId(), false)));
        event.registerEntityRenderer(MonsterEntities.ROCK_GOLEM.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(MonsterEntities.ROCK_GOLEM.getId(), false), false, 1.0F, -0.05F));
        event.registerEntityRenderer(MonsterEntities.TAINTED_GHOUL.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<JumpingWarriorMonster>(MonsterEntities.TAINTED_GHOUL.getId(), "hand_right", "hand_left").withAltAnimations(Confluence.asResource("ghoul"))));
        event.registerEntityRenderer(MonsterEntities.VILE_GHOUL.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<JumpingWarriorMonster>(MonsterEntities.VILE_GHOUL.getId(), "hand_right", "hand_left").withAltAnimations(Confluence.asResource("ghoul"))));
        event.registerEntityRenderer(MonsterEntities.DREAMER_GHOUL.get(), c -> new GeoNormalRenderer<>(c, new ContactHumanoidGeoModel<JumpingWarriorMonster>(MonsterEntities.DREAMER_GHOUL.getId(), "hand_right", "hand_left").withAltAnimations(Confluence.asResource("ghoul"))));
        event.registerEntityRenderer(MonsterEntities.PALADIN.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(MonsterEntities.BONE_LEE.get(), MissingModelRenderer::new); // todo 专用模型
        event.registerEntityRenderer(MonsterEntities.GOBLIN_ARCHER.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaGoblinGeoModel<>(c, MonsterEntities.GOBLIN_ARCHER.getId().withPrefix("goblin/"))));
        event.registerEntityRenderer(MonsterEntities.GOBLIN_PEON.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaGoblinGeoModel<>(c, MonsterEntities.GOBLIN_PEON.getId().withPrefix("goblin/"))));
        event.registerEntityRenderer(MonsterEntities.GOBLIN_WARRIOR.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaGoblinGeoModel<>(c, MonsterEntities.GOBLIN_WARRIOR.getId().withPrefix("goblin/"))));
        event.registerEntityRenderer(MonsterEntities.GOBLIN_THIEF.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaGoblinGeoModel<>(c, MonsterEntities.GOBLIN_THIEF.getId().withPrefix("goblin/"))));
        event.registerEntityRenderer(MonsterEntities.GOBLIN_SCOUT.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaGoblinGeoModel<>(c, MonsterEntities.GOBLIN_SCOUT.getId().withPrefix("goblin/"))));
        event.registerEntityRenderer(MonsterEntities.ANGER_GOBLIN.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaGoblinGeoModel<>(c, Confluence.asResource("goblin/anger_goblin"))));
        // 陆行怪
        event.registerEntityRenderer(MonsterEntities.ZOMBIE.get(), c -> new VanillaHumanoidRenderer<>(c, new ZombieGeoModel(c)));
        event.registerEntityRenderer(MonsterEntities.BLOODY_SPORE.get(), c -> new BloodySporeRenderer(c, MonsterEntities.BLOODY_SPORE.getId()));
        event.registerEntityRenderer(MonsterEntities.BLOOD_CRAWLER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.BLOOD_CRAWLER.getId()));
        event.registerEntityRenderer(MonsterEntities.SPORE_ZOMBIE.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaZombieGeoModel<>(c, MonsterEntities.SPORE_ZOMBIE.getId())));
        event.registerEntityRenderer(MonsterEntities.HAT_SPORE_ZOMBIE.get(), c -> new VanillaHumanoidRenderer<>(c, new VanillaZombieGeoModel<>(c, MonsterEntities.HAT_SPORE_ZOMBIE.getId())));
        event.registerEntityRenderer(MonsterEntities.NYMPH.get(), c -> new GeoNormalRenderer<>(c, new NymphModel(MonsterEntities.NYMPH.getId())));
        event.registerEntityRenderer(MonsterEntities.SAND_POACHER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.SAND_POACHER.getId()));
        // 水怪
        event.registerEntityRenderer(MonsterEntities.PIRANHA.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.PIRANHA.getId(), true, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ANGLER_FISH.get(), c -> new GeoNegativeVolumeRenderer<>(c, new GeoNormalModel<>(MonsterEntities.ANGLER_FISH.getId(), false), true, 1.0F, -0.1875F).addBoneToGlow("light"));
        // goldfish.bbmodel 自带的腐化、猩红贴图变种。
        event.registerEntityRenderer(MonsterEntities.CORRUPT_GOLDFISH.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/animal/goldfish.geo.json"), Confluence.asResource("textures/entity/corrupt_goldfish.png"), Confluence.asResource("animations/entity/animal/goldfish.animation.json"))));
        event.registerEntityRenderer(MonsterEntities.VICIOUS_GOLDFISH.get(), c -> new GeoNormalRenderer<>(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/animal/goldfish.geo.json"), Confluence.asResource("textures/entity/vicious_goldfish.png"), Confluence.asResource("animations/entity/animal/goldfish.animation.json"))));
        event.registerEntityRenderer(MonsterEntities.BLOOD_FEEDER.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.BLOOD_FEEDER.getId(), true, 1.2F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.ARAPAIMA.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ARAPAIMA.getId(), true, 1.0F, 0.0F));
        event.registerEntityRenderer(MonsterEntities.BLUE_JELLYFISH.get(), c -> new JellyFishRenderer(c, jellyfishModel("blue")));
        event.registerEntityRenderer(MonsterEntities.PINK_JELLYFISH.get(), c -> new JellyFishRenderer(c, jellyfishModel("pink")));
        event.registerEntityRenderer(MonsterEntities.GREEN_JELLYFISH.get(), c -> new JellyFishRenderer(c, jellyfishModel("green")));
        event.registerEntityRenderer(MonsterEntities.BLOOD_JELLY.get(), c -> new JellyFishRenderer(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/blood_jelly.geo.json"), Confluence.asResource("textures/entity/blood_jelly.png"), Confluence.asResource("animations/entity/blood_jelly.animation.json")), 0.0625F, true));
        event.registerEntityRenderer(MonsterEntities.FUNGO_FISH.get(), c -> new JellyFishRenderer(c, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/fungo_fish.geo.json"), Confluence.asResource("textures/entity/fungo_fish.png"), Confluence.asResource("animations/entity/fungo_fish.animation.json")), 0.0625F, true));
        event.registerEntityRenderer(MonsterEntities.SHARK.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.SHARK.getId(), true, 1.0F, -0.10625F));
        // 卷壳怪
        event.registerEntityRenderer(MonsterEntities.GIANT_SHELLY.get(), c -> new GeoNormalRenderer<>(c, new VariantTextureGeoModel<>(Confluence.asResource("geo/entity/giant_shelly.geo.json"), Confluence.asResource("animations/entity/giant_shelly.animation.json"), shelly -> Confluence.asResource(shelly.getVariant() == 0 ? "textures/entity/giant_shelly/purple.png" : "textures/entity/giant_shelly/yellow.png"))));
        event.registerEntityRenderer(MonsterEntities.CRAWDAD.get(), c -> new GeoNormalRenderer<>(c, new VariantTextureGeoModel<>(Confluence.asResource("geo/entity/crawdad.geo.json"), Confluence.asResource("animations/entity/crawdad.animation.json"), crawdad -> Confluence.asResource(crawdad.getVariant() == 0 ? "textures/entity/crawdad/blue.png" : "textures/entity/crawdad/red.png"))));
        // Wraith + Mimics
        event.registerEntityRenderer(MonsterEntities.WRAITH.get(), c -> new GeoNormalRenderer<>(c, VanillaHumanoidGeoModel.armor(c, Confluence.asResource("geo/item/armor/wraith_armor.geo.json"), Confluence.asResource("textures/item/armor/wraith_armor.png"), false)));
        event.registerEntityRenderer(MonsterEntities.WOODEN_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.WOODEN_MIMIC.getId()));
        event.registerEntityRenderer(MonsterEntities.GOLDEN_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.GOLDEN_MIMIC.getId()));
        event.registerEntityRenderer(MonsterEntities.ICE_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.ICE_MIMIC.getId()));
        event.registerEntityRenderer(MonsterEntities.SHADOW_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.SHADOW_MIMIC.getId()));
        event.registerEntityRenderer(MonsterEntities.CRIMSON_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.CRIMSON_MIMIC.getId()).withScale(2.0F));
        event.registerEntityRenderer(MonsterEntities.CORRUPT_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.CORRUPT_MIMIC.getId()).withScale(2.0F));
        event.registerEntityRenderer(MonsterEntities.HALLOWED_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.HALLOWED_MIMIC.getId()).withScale(2.0F));
        event.registerEntityRenderer(MonsterEntities.JUNGLE_MIMIC.get(), c -> new GeoNormalRenderer<>(c, MonsterEntities.JUNGLE_MIMIC.getId()).withScale(2.0F));

        event.registerEntityRenderer(ACCUMULATING_ENERGY.get(), NoopRenderer::new);

        event.registerBlockEntityRenderer(FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(AltarBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.SKY_MILL_ENTITY.get(), ClientUtils.rendererProvider(SkyMillBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.EXTRACTINATOR_ENTITY.get(), ClientUtils.rendererProvider(ExtractinatorBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.SILLY_BALLOON_MACHINE_ENTITY.get(), ClientUtils.rendererProvider(MechanicalBlockRenderer::new));
        event.registerBlockEntityRenderer(FunctionalBlocks.WEATHER_VANE_ENTITY.get(), WeatherVaneBlockRenderer::new);
        event.registerBlockEntityRenderer(ChestBlocks.BASE_CHEST_ENTITY.get(), BaseChestBlockRenderer::new);
        event.registerBlockEntityRenderer(ChestBlocks.DEATH_CHEST_ENTITY.get(), DeathChestBlockRenderer::new);
        event.registerBlockEntityRenderer(NatureBlocks.LIFE_CRYSTAL_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new LifeCrystalBlockModel<>()));
        event.registerBlockEntityRenderer(DecorativeBlocks.GARDEN_GNOME_ENTITY.get(), context -> new GeoBlockRenderer<>(new GardenGnomeBlockModel()));
        event.registerBlockEntityRenderer(FunctionalBlocks.LIFECRYSTAL_BOULDER_ENTITY.get(), context -> new GeoBlockRenderer<>(new LifeCrystalBlockModel<>()));
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
        event.registerBlockEntityRenderer(ModBlocks.VOID_ENTITY.get(), ClientUtils.rendererProvider(VoidBlockRenderer::new));
        event.registerBlockEntityRenderer(NatureBlocks.VOID_TREE_ROOT_BLOCK_ENTITY.get(), ClientUtils.rendererProvider(VoidTreeRootBlockRenderer::new));
        event.registerBlockEntityRenderer(ModBlocks.ENEMY_BANNER_ENTITY.get(), EnemyBannerBlockRenderer::new);
    }

    private static ExplicitGeoModel<JellyFish> jellyfishModel(String color) {
        return new ExplicitGeoModel<>(Confluence.asResource("geo/entity/jellyfish.geo.json"), Confluence.asResource("textures/entity/" + color + "_jellyfish.png"), Confluence.asResource("animations/entity/jellyfish.animation.json"));
    }

    private static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(ModClientSetups.HALLOW_LEAVES_COLOR, NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get());
        event.register(ModClientSetups.VOID_LEAVES_COLOR, NatureBlocks.VOID_LOG_BLOCKS.LEAVES.get());
        event.register(ModClientSetups.VOID_WEAVE_COLOR, NatureBlocks.VOID_WEAVE.get());
        event.register(ModClientSetups.DREAM_BUBBLE_COLOR, NatureBlocks.DREAM_BUBBLE.get());
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(), NatureBlocks.BAOBAB_LOG_BLOCKS.LEAVES.get());
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor(), NatureBlocks.JUNGLE_GRASS_BLOCK.get());
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((pStack, pTintIndex) -> VOID_B.get(), NatureBlocks.VOID_LOG_BLOCKS.LEAVES.get());
        event.register((pStack, pTintIndex) -> ColoredItem.getRGBA(pStack), MaterialItems.GEL.get());
        event.register((pStack, pTintIndex) -> GrassColor.getDefaultColor(), NatureBlocks.JUNGLE_GRASS_BLOCK.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? PaintItem.getARGB(stack) : 0xFFFFFFFF, PaintItems.PAINT_ITEMS.toArray(new Item[0]));
        event.register((stack, tintIndex) -> tintIndex == 1 ? BaseDyeItem.getARGB(stack) : 0xFFFFFFFF, VanityArmorItems.COLORED_DYE_ITEMS.toArray(new Item[0]));
    }

    private static void registerClientExtensions(PortRegisterClientExtensionsEvent event) {
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
        // 长矛的 Geo 渲染器与手臂姿态由 AbstractSpearItem 的同一个客户端扩展提供。
        event.registerItem(ModClientSetups.DRILL_O_CHAINSAW, Streams.stream(Iterables.concat(
                DrillItems.ITEMS.getEntries(),
                ChainsawItems.ITEMS.getEntries()
        )).filter(Objects::nonNull).map(PortRegistryEntry::get).toArray(Item[]::new));
        event.registerItem(ModClientSetups.LANCE, LanceItems.ITEMS.getEntries().stream().map(PortRegistryEntry::get).toArray(Item[]::new));
        for (PortRegistryEntry<Item, ? extends Item> holder : SwordItems.ITEMS.getEntries()) {
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
        event.registerItem(ModClientSetups.GLINT_RAINBOW_EXTENSIONS, TreasureBagItems.ITEMS.getEntries().stream().map(PortRegistryEntry::get).toArray(Item[]::new));
        event.registerItem(new EnemyBannerItemRenderer(), ModItems.ENEMY_BANNER);
        registerGunModel(event, ManaWeaponItems.BEE_GUN);
        registerGunModel(event, ManaWeaponItems.SPACE_GUN);
        GunItems.GUN_ITEMS.forEach(holder -> registerGunModel(event, holder));
        event.registerMobEffect(ModClientSetups.TRANSLUCENT_EFFECT_ICON, ModEffects.LUCK_EFFECT.get());
    }

    private static void registerGunModel(PortRegisterClientExtensionsEvent event, PortRegistryEntry<Item, ? extends Item> gunSupplier) {
        ResourceLocation loc = Confluence.asResource("gun/" + gunSupplier.getId().getPath());
        event.registerItem(new SimpleGeoItemRenderer<>(new DefaultedItemGeoModel<>(loc)), gunSupplier.get());
    }

    private static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.SPIT_GLOW.get(), SpitParticle.EmissiveProvider::new);
        event.registerSpecial(ModParticleTypes.DAMAGE_INDICATOR.get(), new DamageIndicatorParticle.Provider());
        event.registerSpecial(ModParticleTypes.WHOLE_ITEM.get(), new WholeItemParticle.Provider());
        // 原版伤害弹丸不接受 null 尾迹；返回 null 粒子实例可明确表达“不渲染尾迹”。
        event.registerSpecial(ModParticleTypes.NO_TRAIL.get(), (type, level, x, y, z, xSpeed, ySpeed, zSpeed) -> null);
        event.registerSpriteSet(ModParticleTypes.LUMINITE_IMPACT.get(), LuminiteImpactParticle.Provider::new);
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

    private static void textureAtlasStitched(PortTextureAtlasStitchedEvent event) {
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

    private static void registerMaterialAtlasesEvent(PortRegisterMaterialAtlasesEvent event) {
        event.register(ModClientSetups.ENTITY_BLOOD_ATLAS, Confluence.asResource("entity_blood"));
    }

    private static void model$ModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();

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
                AxeItems.LUCY_THE_AXE);
        ModClientSetups.asCustomModel(modelRegistry, TreasureBagItems.ITEMS.getEntries().toArray(PortRegistryEntry[]::new));

        ModConnectives.MODEL_SWAPPER.onModelBake(modelRegistry);

        if (ModClientSetups.SHOULD_NOT_GENERATE_BLOCK_GRAY_TEXTURE || !StartupConfigs.paintsReplaceTexture())
            return;

        CustomBlockModels customBlockModels = ModConnectives.MODEL_SWAPPER.getCustomBlockModels();
        Set<String> bannedModForPaints = new HashSet<>(StartupConfigs.bannedModForPaints());
        for (Map.Entry<ResourceKey<Block>, Block> entry : ForgeRegistries.BLOCKS.getEntries()) {
            Block block = entry.getValue();
            ResourceLocation id = entry.getKey().location();
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

    private static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        ModRecipes.TYPES.getEntries().forEach(holder -> event.registerRecipeCategoryFinder(holder.get(), recipeHolder -> RecipeBookCategories.UNKNOWN));
    }

    private static void registerRenderBuffers(PortRegisterRenderBuffersEvent event) {
        for (ColoredGlintContext context : RenderStateShardAccessor.getColoredGlintContexts()) {
            event.registerRenderBuffer(context.renderType());
        }
    }

    private static void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AltImageComponent.class, AltImageTooltip::new);
        event.register(RepeaterComponent.class, ClientRepeaterContentsTooltip::new);
    }

    private static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> DivaSlimeVertexConsumer.clearTextures());
        event.registerReloadListener(ClientBestiary.getInstance());
        event.registerReloadListener(LucyTheAxeDialogCategory.Loader.getInstance());
        event.registerReloadListener(NPCDialogLoader.getInstance());
    }

    private static void registerCustomBestiaryEntryModel(RegisterCustomBestiaryEntryRendererEvent event) {
        EntityRendererProvider.Context context = event.getContext();
        event.registeSurefaceWorm(MonsterEntities.DEVOURER);
        event.registeSurefaceWorm(MonsterEntities.WORLD_FEEDER);
        event.registerBaseWorm(MonsterEntities.TOMB_CRAWLER);
        event.registerBaseWorm(MonsterEntities.GIANT_WORM);
        event.registerBaseWorm(MonsterEntities.DIGGER);
        event.registerBaseWorm(MonsterEntities.LEECH);
        event.registerBoneSerpent(MonsterEntities.BONE_SERPENT);
        event.registerBoneSerpent(MonsterEntities.WITHER_BONE_SERPENT);
        event.register("entity.minecraft.zombie.slime", new SlimeZombieRenderer(context));
    }

    private static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        for (PortRegistryEntry<Item, ? extends Item> entry : FishingPoleItems.ITEMS.getEntries()) {
            event.register(entry.get(), ModClientSetups.FISHING_POLE_DECORATOR);
        }
        for (PortRegistryEntry<Item, ? extends Item> entry : CrossbowItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof BaseTerraRepeaterItem) {
                event.register(item, ModClientSetups.REPEATER_AMMO);
            }
        }
        if (LibStartupConfig.itemGroups()) {
            // PortSprite 记录的是 PNG 的真实源尺寸；这两张图本身为 7×7，不能填入控件或预期显示尺寸。
            PortSprite plus = new PortSprite(Confluence.asResource("plus"), 7, 7);
            PortSprite minus = new PortSprite(Confluence.asResource("minus"), 7, 7);
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

    private static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), Confluence.asResource("boss_bar_flow"), DefaultVertexFormat.POSITION_TEX), CustomBossBarRenderer::setShader);
        } catch (IOException e) {
            Confluence.LOGGER.error("Failed to register shader", e);
        }
    }
}
