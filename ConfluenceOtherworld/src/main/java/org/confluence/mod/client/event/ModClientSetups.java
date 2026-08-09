package org.confluence.mod.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.client.render.item.SimpleClientItemExtensions;
import org.confluence.lib.color.IntegerRGB;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.ColoredGlintContext;
import org.confluence.mod.client.handler.MeteorLandingHandler;
import org.confluence.mod.client.model.WrappedBakedModel;
import org.confluence.mod.client.renderer.item.CustomLightItemExtension;
import org.confluence.mod.client.renderer.item.EntityDisplayItemRenderer;
import org.confluence.mod.client.renderer.item.MutableRenderTypeItemExtension;
import org.confluence.mod.common.init.ModArmPoses;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.accessory.GuideVooDooDollItem;
import org.confluence.mod.common.item.bow.ShortBowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.mod.util.RepeaterContentsComponentHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.client.renderer.RenderStateShard.*;

@SuppressWarnings("deprecation")
public final class ModClientSetups {
    public static final ResourceLocation VANILLA_BLOCK_ATLAS = InventoryMenu.BLOCK_ATLAS;
    public static final ResourceLocation ENTITY_BLOOD_ATLAS = Confluence.asResource("textures/atlas/entity_blood.png");

    public static final WidgetSprites EXTRA_INVENTORY_BUTTON = new WidgetSprites(Confluence.asResource("widget/extra_inventory_button"), Confluence.asResource("widget/extra_inventory_button_highlighted"));
    public static final ResourceLocation BLOOM_TEXTURE = Confluence.asResource("textures/misc/bloom.png");
    public static final ResourceLocation LEGACY_SPRITE = Confluence.asResource("hud/icon");
    public static final ResourceLocation OVERLAY_SPRITE = Confluence.asResource("hud/overlay");
    // region sun todo dynamic texture
    public static final ResourceLocation SUNGLASSES_TEXTURE = Confluence.asResource("textures/environment/sunglasses.png");
    public static final ResourceLocation BOULDER_SUN_TEXTURE = Confluence.asResource("textures/environment/boulder.png");
    public static final ResourceLocation SUNGLASSES_BOULDER_TEXTURE = Confluence.asResource("textures/environment/sunglasses_boulder.png");
    // endregion
    private static final NormalNoise normalNoise = NormalNoise.create(RandomSource.create(0), new NormalNoise.NoiseParameters(-5, 1.0, 1.0, 1.0, 1.0));
    static final IClientFluidTypeExtensions HONEY_CLIENT_EXTENSIONS = new IClientFluidTypeExtensions() {
        private static final ResourceLocation STILL = Confluence.asResource("block/fluid/honey_still");
        private static final ResourceLocation FLOWING = Confluence.asResource("block/fluid/honey_flowing");
        private static final Vector3f FOG_COLOR = new Vector3f(1.0F, 1.0F, 0.0F);

        @Override
        public ResourceLocation getStillTexture() {
            return STILL;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return FLOWING;
        }

        @Override
        public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
            return FOG_COLOR;
        }

        @Override
        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
            RenderSystem.setShaderFogStart(0.125F);
            RenderSystem.setShaderFogEnd(5.0F);
        }
    };
    static final IClientFluidTypeExtensions VOID_CLIENT_EXTENSIONS = new IClientFluidTypeExtensions() {
        private static final ResourceLocation STILL = Confluence.asResource("block/fluid/void_still");
        private static final ResourceLocation FLOWING = Confluence.asResource("block/fluid/void_flowing");
        private static final Vector3f FOG_COLOR = new Vector3f(1.0F, 1.0F, 0.0F);

        @Override
        public ResourceLocation getStillTexture() {
            return STILL;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return FLOWING;
        }

        @Override
        public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
            return FOG_COLOR;
        }

        @Override
        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
            RenderSystem.setShaderFogStart(0.125F);
            RenderSystem.setShaderFogEnd(5.0F);
        }
    };
    static final IClientFluidTypeExtensions SHIMMER_CLIENT_EXTENSIONS = new IClientFluidTypeExtensions() {
        private static final ResourceLocation STILL = Confluence.asResource("block/fluid/shimmer_still");
        private static final ResourceLocation FLOWING = Confluence.asResource("block/fluid/shimmer_flowing");
        private static final Vector3f FOG_COLOR = new Vector3f(1.0F, 0.5882F, 1.0F);

        @Override
        public ResourceLocation getStillTexture() {
            return STILL;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return FLOWING;
        }

        @Override
        public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
            return FOG_COLOR;
        }

        @Override
        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
            RenderSystem.setShaderFogStart(0.125F);
            RenderSystem.setShaderFogEnd(10.0F);
        }
    };
    static final IClientBlockExtensions NO_HIT_EFFECTS = new IClientBlockExtensions() {
        @Override
        public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
            return true;
        }
    };
    static final IClientItemExtensions ENTITY_DISPLAY = new IClientItemExtensions() {
        private EntityDisplayItemRenderer renderer;

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (renderer == null) {
                this.renderer = new EntityDisplayItemRenderer();
            }
            return renderer;
        }
    };
    static final IClientItemExtensions BREATHING_REED = simpleArmPose(ModArmPoses.BREATHING_REED::getValue);
    static final IClientItemExtensions SPEAR = simpleArmPose(ModArmPoses.SPEAR::getValue);
    static final IClientItemExtensions UMBRELLA = simpleArmPose(ModArmPoses.UMBRELLA::getValue);
    static final IClientItemExtensions DRILL_O_CHAINSAW = simpleArmPose(ModArmPoses.DRILL_O_CHAINSAW::getValue);
    static final IClientItemExtensions LANCE = simpleArmPose(ModArmPoses.LANCE::getValue);
    static final IItemDecorator FISHING_POLE_DECORATOR = (guiGraphics, font, itemStack, x, y) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getMainHandItem() == itemStack) {
            ItemStack stack = IPlayer.of(player).confluence$getCurrentBait();
            if (!stack.isEmpty()) {
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(x + 8, y + 8, 100);
                pose.scale(0.5F, 0.5F, 0.5F);
                guiGraphics.renderItem(stack, 0, 0);
                pose.popPose();
            }
        }
        return false;
    };
    static final IItemDecorator REPEATER_AMMO = (guiGraphics, font, itemStack, x, y) -> {
        if (itemStack.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterContentsComponentHandler handler) {
            Iterator<ItemStack> itemIterator = handler.getAllItemIterator();
            if (itemIterator.hasNext()) {
                ItemStack stack = itemIterator.next();
                if (!stack.isEmpty()) {
                    PoseStack pose = guiGraphics.pose();
                    pose.pushPose();
                    pose.translate(x + 8, y + 8, 100);
                    pose.mulPose(Axis.ZN.rotation(Mth.HALF_PI));
                    pose.translate(-7, -9, 0);
                    guiGraphics.renderItem(stack, 0, 0);
                    pose.popPose();
                }
            }
        }
        return false;
    };

    private static IClientItemExtensions simpleArmPose(Supplier<HumanoidModel.ArmPose> supplier) {
        return new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return supplier.get();
            }
        };
    }

    static final IClientItemExtensions NOOP_ITEM = new SimpleClientItemExtensions().handTransform(true).armPose(HumanoidModel.ArmPose.EMPTY).noRenderer();
    static final IClientItemExtensions GUIDE_VOODOO_DOLL = new MutableRenderTypeItemExtension(stack -> GuideVooDooDollItem.isWall(LibUtils.getItemStackNbtIfPresent(stack)) ? ModClientSetups.GLINT_FF0000.renderType() : RenderType.glint());
    static final IClientItemExtensions GLINT_RAINBOW_EXTENSIONS = new MutableRenderTypeItemExtension(stack -> ModClientSetups.GLINT_RAINBOW.renderType());
    static final IClientItemExtensions FULL_LIGHT = new CustomLightItemExtension(15);

    /// 对于使用原版json模型，且使用了Extensions来自定义渲染的物品，需使用该方法标记为自定义模型
    static void asCustomModel(Map<ModelResourceLocation, BakedModel> modelRegistry, DeferredHolder<?, ?>... deferredItems) {
        for (DeferredHolder<?, ?> holder : deferredItems) {
            modelRegistry.compute(ModelResourceLocation.inventory(holder.getId()), (k, model) -> new WrappedBakedModel(model));
        }
    }

    static final IClientMobEffectExtensions TRANSLUCENT_EFFECT_ICON = new IClientMobEffectExtensions() {
        @Override
        public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
            RenderSystem.enableBlend();
            Holder<MobEffect> holder = instance.getEffect();
            TextureAtlasSprite textureAtlasSprite = screen.getMinecraft().getMobEffectTextures().get(holder);
            guiGraphics.blit(x, y + 7, blitOffset, 18, 18, textureAtlasSprite);
            RenderSystem.disableBlend();
            return true;
        }
    };
    static final BlockColor HALLOW_LEAVES_COLOR = (state, level, pos, tintIndex) -> {
        if (pos == null) return -1;
        double scale = 1.5;
        int r = (int) Mth.clamp((
                normalNoise.getValue(
                        pos.getX() * scale,
                        pos.getY() * scale,
                        pos.getZ() * scale
                ) + 1) / 2 * 255, 0, 255);
        int g = (int) Mth.clamp((
                normalNoise.getValue(
                        (pos.getX() + 50) * scale,
                        (pos.getY() + 50) * scale,
                        (pos.getZ() + 50) * scale
                ) + 1) / 2 * 255, 0, 255);
        int b = (int) Mth.clamp((
                normalNoise.getValue(
                        (pos.getX() + 100) * scale,
                        (pos.getY() + 100) * scale,
                        (pos.getZ() + 100) * scale
                ) + 1) / 2 * 255 + 255 - r - g, 0, 255);
        float[] hsb = Color.RGBtoHSB(r, g, b, null);

        return Color.HSBtoRGB(hsb[0], 0.5F, 1.0F);
    };

    public static final IntegerRGB VOID_A = IntegerRGB.of(0x2c182a);
    public static final IntegerRGB VOID_B = IntegerRGB.of(0x3b2e6b);
    public static final IntegerRGB VOID_C = IntegerRGB.of(0x3c6f98);
    static final BlockColor VOID_LEAVES_COLOR = (state, level, pos, tintIndex) -> threeColor(pos, VOID_A, VOID_B, VOID_C, 3);

    public static final IntegerRGB VOID_WEAVE_A = IntegerRGB.of(0x8641f8);
    public static final IntegerRGB VOID_WEAVE_B = IntegerRGB.of(0x6516e9);
    public static final IntegerRGB VOID_WEAVE_C = IntegerRGB.of(0x4d57fb);
    static final BlockColor VOID_WEAVE_COLOR = (state, level, pos, tintIndex) -> threeColor(pos, VOID_WEAVE_A, VOID_WEAVE_B, VOID_WEAVE_C, 3);

    public static final IntegerRGB DREAM_BUBBLE_A = IntegerRGB.of(0xff3a6f);
    public static final IntegerRGB DREAM_BUBBLE_B = IntegerRGB.of(0xffd03a);
    public static final IntegerRGB DREAM_BUBBLE_C = IntegerRGB.of(0xb7ff3a);
    static final BlockColor DREAM_BUBBLE_COLOR = (state, level, pos, tintIndex) -> threeColor(pos, DREAM_BUBBLE_A, DREAM_BUBBLE_B, DREAM_BUBBLE_C, 2);

    private static int threeColor(@Nullable BlockPos pos, IntegerRGB colorA, IntegerRGB colorB, IntegerRGB colorC, double scale) {
        if (pos == null) return colorB.get();
        double noiseVal = normalNoise.getValue(
                pos.getX() * scale,
                pos.getY() * scale,
                pos.getZ() * scale
        );

        float t = (float) (noiseVal + 1) * 0.5F;
        if (t < 0.5F) {
            return colorA.mixture(colorB, t * 2).get();
        }
        return colorB.mixture(colorC, (t - 0.5F) * 2).get();
    }

    static boolean guideCheckedJEI = LibUtils.isModLoaded("jei") || LibUtils.isModLoaded("emi");

    static void setRenderLayers() {
        RenderType translucent = RenderType.translucent();
        ItemBlockRenderTypes.setRenderLayer(ModFluids.SHIMMER.fluid().get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.SHIMMER.flowing().get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.WHITE_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.GRAY_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.BLACK_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.BROWN_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.RED_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.ORANGE_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.YELLOW_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.LIME_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.GREEN_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.CYAN_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.BLUE_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.PURPLE_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.MAGENTA_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.PINK_PURE_GLASS.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.PACKED_ICE_BRICKS.FULL.get(), translucent);
        RenderType cutout = RenderType.cutout();
        ItemBlockRenderTypes.setRenderLayer(FunctionalBlocks.EVER_POWERED_RAIL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.PURE_GLASS.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FunctionalBlocks.ECHO_BLOCK.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CURSED_FLAME_BLOCK.get(), cutout);
    }

    static void registerItemProperties() {
        ResourceLocation enable = Confluence.asResource("enable");
        ItemProperties.register(AccessoryItems.SPECTRE_GOGGLES.get(), enable, LibClientUtils.COULD_ENABLE_PROPERTY_FUNCTION);
        ItemProperties.register(AccessoryItems.MECHANICAL_LENS.get(), enable, LibClientUtils.COULD_ENABLE_PROPERTY_FUNCTION);
        ItemProperties.register(ToolItems.ENCUMBERING_STONE.get(), enable, LibClientUtils.COULD_ENABLE_PROPERTY_FUNCTION);
        ItemProperties.register(ToolItems.METEOR_COMPASS.get(), ResourceLocation.withDefaultNamespace("angle"), new CompassItemPropertyFunction((level, stack, entity) -> MeteorLandingHandler.getGlobalPos()));
    }

    public static final boolean SHOULD_NOT_GENERATE_BLOCK_GRAY_TEXTURE = LibUtils.isModLoaded("ctm") || LibUtils.isModLoaded("fusion") || LibUtils.isModLoaded("continuity");

    public static final RenderType TERRA_SWORD_RENDER_TYPE = RenderType.create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setTextureState(new TextureStateShard(Confluence.asResource("textures/mask/sword.png"), true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));

    public static final ColoredGlintContext GLINT_FF0000 = ColoredGlintContext.create("FF0000", 0xFF0000);
    public static final ColoredGlintContext GLINT_RAINBOW = ColoredGlintContext.create("rainbow", 0, 0, 0);

    public static void registerBowProperties() {
        ResourceLocation pull = ResourceLocation.withDefaultNamespace("pull");
        ClampedItemPropertyFunction shortBowPull = (itemStack, clientLevel, living, speed) -> living != null && living.getUseItem() == itemStack ? (float) (itemStack.getUseDuration(living) - living.getUseItemRemainingTicks()) / ShortBowItem.MAX_DRAW_DURATION : 0.0F;
        ClampedItemPropertyFunction bowPull = (itemStack, clientLevel, living, speed) -> living != null && living.getUseItem() == itemStack ? (float) (itemStack.getUseDuration(living) - living.getUseItemRemainingTicks()) / BowItem.MAX_DRAW_DURATION : 0.0F;
        ResourceLocation pulling = ResourceLocation.withDefaultNamespace("pulling");
        ClampedItemPropertyFunction bowPulling = (itemStack, clientLevel, living, speed) -> living != null && living.isUsingItem() && living.getUseItem() == itemStack ? 1.0F : 0.0F;

        BowItems.ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof ShortBowItem) {
                ItemProperties.register(item.get(), pull, shortBowPull);
            } else {
                ItemProperties.register(item.get(), pull, bowPull);
            }
            ItemProperties.register(item.get(), pulling, bowPulling);
        });

        ClampedItemPropertyFunction crossbowPulling = (itemStack, clientLevel, living, speed) -> {
            if (living == null || (!(itemStack.getItem() instanceof BaseTerraRepeaterItem repeater))) {
                return 0.0F;
            }
            var projectiles = repeater.getHandler(itemStack);
            if (projectiles != null && !projectiles.isEmpty()) {
                return 1.0F;
            }
            return 0.0F;
        };
        CrossbowItems.ITEMS.getEntries().forEach(item -> ItemProperties.register(item.get(), pulling, crossbowPulling));
    }

    public static void registerFishingPoleProperties() {
        ResourceLocation cast = ResourceLocation.withDefaultNamespace("cast");
        ClampedItemPropertyFunction function = (itemStack, level, living, speed) -> {
            if (living == null) {
                return 0.0F;
            } else {
                boolean flag = living.getMainHandItem() == itemStack;
                boolean flag1 = living.getOffhandItem() == itemStack;
                if (living.getMainHandItem().getItem() instanceof FishingRodItem) flag1 = false;
                return (flag || flag1) && living instanceof Player && ((Player) living).fishing != null ? 1.0F : 0.0F;
            }
        };
        FishingPoleItems.ITEMS.getEntries().forEach(pole -> ItemProperties.register(pole.get(), cast, function));
    }
}
