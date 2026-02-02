package org.confluence.terra_curio.client.event;

import net.minecraft.client.RecipeBookCategories;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.TooltipComponentsValue;
import org.confluence.terra_curio.client.CuriosClient;
import org.confluence.terra_curio.client.TCClientConfigs;
import org.confluence.terra_curio.client.gui.DivingHelmetOverlay;
import org.confluence.terra_curio.client.gui.InfoHudOverlay;
import org.confluence.terra_curio.client.gui.WorkshopScreen;
import org.confluence.terra_curio.client.model.entity.BeeProjectileModel;
import org.confluence.terra_curio.client.model.entity.StepStoolModel;
import org.confluence.terra_curio.client.model.entity.XBoneProjectileModel;
import org.confluence.terra_curio.client.renderer.entity.BeeProjectileRenderer;
import org.confluence.terra_curio.client.renderer.entity.StarCloakEntityRenderer;
import org.confluence.terra_curio.client.renderer.entity.StepStoolRenderer;
import org.confluence.terra_curio.client.renderer.entity.XBoneProjectileRenderer;
import org.confluence.terra_curio.client.renderer.tooltip.MultiFunctionTooltip;
import org.confluence.terra_curio.common.init.TCEntities;
import org.confluence.terra_curio.common.init.TCMenus;
import org.confluence.terra_curio.common.init.TCRecipes;

@EventBusSubscriber(modid = TerraCurio.MODID, value = Dist.CLIENT)
public final class ModClientEvent {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TCClientConfigs.onLoad();
            CuriosClient.registerRenderers();
        });
    }

    @SubscribeEvent
    public static void modConfig$Reloading(ModConfigEvent.Reloading event) {
        if (TerraCurio.MODID.equals(event.getConfig().getModId())) {
            TCClientConfigs.onLoad();
        }
    }

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        CuriosClient.registerLayers(event::registerLayerDefinition);
        event.registerLayerDefinition(BeeProjectileModel.LAYER_LOCATION, BeeProjectileModel::createBodyLayer);
        event.registerLayerDefinition(XBoneProjectileModel.LAYER_LOCATION, XBoneProjectileModel::createBodyLayer);
        event.registerLayerDefinition(StepStoolModel.LAYER_LOCATION, StepStoolModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TCEntities.BEE_PROJECTILE.get(), BeeProjectileRenderer::new);
        event.registerEntityRenderer(TCEntities.STEP_STOOL.get(), StepStoolRenderer::new);
        event.registerEntityRenderer(TCEntities.STAR_CLOAK.get(), StarCloakEntityRenderer::new);
        event.registerEntityRenderer(TCEntities.X_BONE.get(), XBoneProjectileRenderer::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(TerraCurio.asResource("info_hud"), new InfoHudOverlay());
        event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, TerraCurio.asResource("diving_helmet"), new DivingHelmetOverlay());
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TCMenus.WORKSHOP.get(), WorkshopScreen::new);
    }

    @SubscribeEvent
    public static void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TooltipComponentsValue.Multi.class, multi -> new MultiFunctionTooltip(multi.storages()));
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerRecipeCategoryFinder(TCRecipes.WORKSHOP_TYPE.get(), recipeHolder -> RecipeBookCategories.UNKNOWN);
    }
}
