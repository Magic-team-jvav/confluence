package org.confluence.mod.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.AchievementToast;
import org.confluence.mod.client.gui.hud.ArrowInBowHud;
import org.confluence.mod.client.gui.hud.HealthHudLayer;
import org.confluence.mod.client.gui.hud.ManaHudLayer;
import org.confluence.mod.client.gui.screens.GroupWikiScreen;
import org.confluence.mod.client.gui.screens.ObjectWikiScreen;
import org.confluence.mod.client.gui.screens.SkyMillScreen;
import org.confluence.mod.client.handler.WeatherHandler;
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
import org.confluence.mod.client.model.entity.projectile.BoulderModel;
import org.confluence.mod.client.model.entity.projectile.EnchantedSwordProjectileModel;
import org.confluence.mod.client.model.entity.projectile.IceBladeSwordProjectileModel;
import org.confluence.mod.client.renderer.block.*;
import org.confluence.mod.client.renderer.entity.bomb.*;
import org.confluence.mod.client.renderer.entity.fishing.BaseFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.BloodyFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.GlowingFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.fishing.HotlineFishingHookRenderer;
import org.confluence.mod.client.renderer.entity.hook.*;
import org.confluence.mod.client.renderer.entity.projectile.*;
import org.confluence.mod.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModMenus;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.ColoredItem;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.color.IntegerRGB;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;

import static org.confluence.mod.common.init.ModEntities.*;

@SuppressWarnings("deprecation")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = Confluence.MODID)
public final class ModClientEvents {
    private static final BlockColor HALLOW_LEAVES_COLOR = (blockState, getter, pos, tint) -> {
        if (pos == null) return -1;
        IntegerRGB x = hallowMixture(Math.abs(pos.getX()) % 12);
        IntegerRGB y = hallowMixture(Math.abs(pos.getY()) % 12);
        IntegerRGB z = hallowMixture(Math.abs(pos.getZ()) % 12);
        return x.mixture(y, 0.5F).mixture(z, 0.5F).get();
    };
    private static final ItemColor SIMPLE = (pStack, pTintIndex) -> ColoredItem.getColor(pStack);

    private static IntegerRGB hallowMixture(int m) {
        if (m <= 4) return IntegerRGB.HALLOW_A.mixture(IntegerRGB.HALLOW_B, m * 0.25F);
        if (m <= 8) return IntegerRGB.HALLOW_B.mixture(IntegerRGB.HALLOW_C, (m - 4) * 0.25F);
        return IntegerRGB.HALLOW_C.mixture(IntegerRGB.HALLOW_A, (m - 8) * 0.25F);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientConfigs.onLoad();
            BowItems.registerProperties();
            FishingPoleItems.registerCast();
            ArrowInBowHud.initAdaptionMap();
            AchievementToast.registerAll();
            WeatherHandler.initialize();

            ResourceLocation enable = Confluence.asResource("enable");
            ItemPropertyFunction enableFunction = (itemStack, level, living, speed) -> {
                CompoundTag tag = ModUtils.getItemStackNbt(itemStack);
                if (tag == null) return 0;
                return tag.getBoolean(IFunctionCouldEnable.DISABLE) ? 0 : 1;
            };
            ItemProperties.register(AccessoryItems.SPECTRE_GOGGLES.get(), enable, enableFunction);
            ItemProperties.register(AccessoryItems.MECHANICAL_LENS.get(), enable, enableFunction);
            ResourceLocation variant = Confluence.asResource("variant");
            ItemPropertyFunction variantFunction = (itemStack, level, living, speed) -> {
                CompoundTag tag = ModUtils.getItemStackNbt(itemStack);
                if (tag == null) return 0;
                return tag.getInt("VariantId");
            };
            ItemProperties.register(FunctionalBlocks.BASE_CHEST_BLOCK.get().asItem(), variant, variantFunction);
            ItemProperties.register(FunctionalBlocks.DEATH_CHEST_BLOCK.get().asItem(), variant, variantFunction);

            ItemBlockRenderTypes.setRenderLayer(NatureBlocks.RED_ICE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(NatureBlocks.PURPLE_ICE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(NatureBlocks.PINK_ICE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(NatureBlocks.THIN_ICE_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(NatureBlocks.EBONY_LOG_BLOCKS.getDoor().get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(NatureBlocks.EBONY_LOG_BLOCKS.getTrapdoor().get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(NatureBlocks.PALM_LOG_BLOCKS.getDoor().get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(FunctionalBlocks.EVER_POWERED_RAIL.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SHIMMER.fluid().get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SHIMMER.flowing().get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY.fluid().get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY.flowing().get(), RenderType.translucent());

            GroupWikiScreen.putWikiType("item",
                    List.of(AccessoryItems.ITEMS, ArrowItems.ITEMS, AxeItems.ITEMS, BaitItems.ITEMS, BowItems.ITEMS, FishingPoleItems.ITEMS,
                            FoodItems.ITEMS, MaterialItems.ITEMS, ModItems.ITEMS, QuestedFishes.ITEMS, SwordItems.ITEMS, PotionItems.ITEMS),
                    List.of("accessories", "arrow", "axe", "bait", "bow", "fishing_pole",
                            "food", "material", "misc", "quested_fish", "sword", "terra_potion"));
            ObjectWikiScreen.putDescription("confluence:copper_short_sword", Component.translatable("wiki.confluence.copper_short_sword"));
        });
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.SKY_MILL.get(), SkyMillScreen::new);
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

        event.registerEntityRenderer(ARROW_PROJECTILE.get(), TerraArrowRenderer::new);
        event.registerEntityRenderer(EFFECT_THROWN_POTION.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ICE_BLADE_SWORD_PROJECTILE.get(), IceBladeSwordProjectileRenderer::new);
        event.registerEntityRenderer(STAR_FURY_PROJECTILE.get(), StarFuryProjectileRenderer::new);
        event.registerEntityRenderer(ENCHANTED_SWORD_PROJECTILE.get(), EnchantedSwordProjectileRenderer::new);
        event.registerEntityRenderer(BOOMERANG_PROJECTILE.get(), BoomerangProjRenderer::new);
        event.registerEntityRenderer(BOULDER.get(), BoulderRenderer::new);

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
        event.registerEntityRenderer(MECHANICAL_CART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(DIGGING_MOLECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));

        event.registerBlockEntityRenderer(ModBlocks.SIGN_BLOCK_ENTITY.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new AltarBlockModel()));
        event.registerBlockEntityRenderer(FunctionalBlocks.SKY_MILL_ENTITY.get(), context -> new SkyMillBlockRenderer());
        event.registerBlockEntityRenderer(FunctionalBlocks.EXTRACTINATOR_ENTITY.get(), context -> new ExtractinatorBlockRenderer());
        event.registerBlockEntityRenderer(FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), MechanicalBlockRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.BASE_CHEST_BLOCK_ENTITY.get(), BaseChestBlockRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.DEATH_CHEST_BLOCK_ENTITY.get(), DeathChestBlockRenderer::new);
        event.registerBlockEntityRenderer(FunctionalBlocks.WEATHER_VANE_ENTITY.get(), WeatherVaneBlockRenderer::new);
        event.registerBlockEntityRenderer(NatureBlocks.LIFE_CRYSTAL_BLOCK_ENTITY.get(), context -> new GeoBlockRenderer<>(new LifeCrystalBlockModel()));
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(HALLOW_LEAVES_COLOR, NatureBlocks.PEARL_LOG_BLOCKS.getLeaves().get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(SIMPLE, MaterialItems.GEL.get());
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = Confluence.asResource("block/fluid/honey_still");
            private static final ResourceLocation FLOWING = Confluence.asResource("block/fluid/honey_flowing");
            private static final Vector3f FOG_COLOR = new Vector3f(1.0F, 1.0F, 0.0F);

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
                return FOG_COLOR;
            }

            @Override
            public void modifyFogRender(@NotNull Camera camera, FogRenderer.@NotNull FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, @NotNull FogShape shape) {
                RenderSystem.setShaderFogStart(0.125F);
                RenderSystem.setShaderFogEnd(5.0F);
            }
        }, ModFluids.HONEY.type());
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = Confluence.asResource("block/fluid/shimmer_still");
            private static final ResourceLocation FLOWING = Confluence.asResource("block/fluid/shimmer_flowing");
            private static final Vector3f FOG_COLOR = new Vector3f(1.0F, 0.5882F, 1.0F);

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
                return FOG_COLOR;
            }

            @Override
            public void modifyFogRender(@NotNull Camera camera, FogRenderer.@NotNull FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, @NotNull FogShape shape) {
                RenderSystem.setShaderFogStart(0.125F);
                RenderSystem.setShaderFogEnd(10.0F);
            }
        }, ModFluids.SHIMMER.type());
        event.registerItem(new SimpleGeoItemRenderer<>(LifeCrystalBlockModel.MODEL, LifeCrystalBlockModel.TEXTURE, null), NatureBlocks.LIFE_CRYSTAL_BLOCK.asItem());
        event.registerItem(new IClientItemExtensions() {
            private GeoItemRenderer<AltarBlock.Item> renderer;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new GeoModel<>() {
                        @Override
                        public ResourceLocation getModelResource(AltarBlock.Item animatable) {
                            return AltarBlockModel.MODELS[animatable.getVariant().getId()];
                        }

                        @Override
                        public ResourceLocation getTextureResource(AltarBlock.Item animatable) {
                            return AltarBlockModel.TEXTURES[animatable.getVariant().getId()];
                        }

                        @Override
                        public ResourceLocation getAnimationResource(AltarBlock.Item animatable) {
                            return AltarBlockModel.ANIMATIONS[animatable.getVariant().getId()];
                        }
                    });
                }
                return renderer;
            }
        }, FunctionalBlocks.DEMON_ALTAR.asItem(), FunctionalBlocks.CRIMSON_ALTAR.asItem());
        event.registerItem(new SimpleGeoItemRenderer<>(SkyMillBlockModel.MODEL, SkyMillBlockModel.TEXTURE, SkyMillBlockModel.ANIMATION), FunctionalBlocks.SKY_MILL.asItem());
        event.registerItem(new SimpleGeoItemRenderer<>(ExtractinatorBlockModel.MODEL, ExtractinatorBlockModel.TEXTURE, ExtractinatorBlockModel.ANIMATION), FunctionalBlocks.EXTRACTINATOR.asItem());
    }
}
