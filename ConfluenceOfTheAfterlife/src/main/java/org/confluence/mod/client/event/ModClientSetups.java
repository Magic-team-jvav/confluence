package org.confluence.mod.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.EntityDisplayItemRenderer;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.color.IntegerRGB;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ModClientSetups {
    static final WidgetSprites EXTRA_INVENTORY_BUTTON = new WidgetSprites(Confluence.asResource("widget/extra_inventory_button"), Confluence.asResource("widget/extra_inventory_button_highlighted"));
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
    static final BlockColor HALLOW_LEAVES_COLOR = new BlockColor() {
        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
            if (pos == null) return -1;
            IntegerRGB x = hallowMixture(Math.abs(pos.getX()) % 12);
            IntegerRGB y = hallowMixture(Math.abs(pos.getY()) % 12);
            IntegerRGB z = hallowMixture(Math.abs(pos.getZ()) % 12);
            return x.mixture(y, 0.5F).mixture(z, 0.5F).get();
        }

        private static IntegerRGB hallowMixture(int m) {
            if (m <= 4) return IntegerRGB.HALLOW_A.mixture(IntegerRGB.HALLOW_B, m * 0.25F);
            if (m <= 8) return IntegerRGB.HALLOW_B.mixture(IntegerRGB.HALLOW_C, (m - 4) * 0.25F);
            return IntegerRGB.HALLOW_C.mixture(IntegerRGB.HALLOW_A, (m - 8) * 0.25F);
        }
    };


    static void setRenderLayers() {
        RenderType translucent = RenderType.translucent();
        ItemBlockRenderTypes.setRenderLayer(ModFluids.SHIMMER.fluid().get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.SHIMMER.flowing().get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY.fluid().get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY.flowing().get(), translucent);
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
        RenderType cutout = RenderType.cutout();
        ItemBlockRenderTypes.setRenderLayer(NatureBlocks.EBONY_LOG_BLOCKS.getDoor().get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(NatureBlocks.EBONY_LOG_BLOCKS.getTrapdoor().get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(NatureBlocks.PALM_LOG_BLOCKS.getDoor().get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(FunctionalBlocks.EVER_POWERED_RAIL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(DecorativeBlocks.PURE_GLASS.get(), cutout);
    }

    static void registerItemProperties() {
        ResourceLocation enable = Confluence.asResource("enable");
        ItemPropertyFunction enableFunction = (itemStack, level, living, speed) -> {
            CompoundTag tag = ModUtils.getItemStackNbt(itemStack);
            if (tag == null) return 1;
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
    }

    static void eventBus(Consumer<IEventBus> consumer) {
        ModList.get().getModContainerById(Confluence.MODID).ifPresent(container -> {
            IEventBus eventBus = container.getEventBus();
            if (eventBus != null) consumer.accept(eventBus);
        });
    }
}
