package org.confluence.terra_curio.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.terra_curio.client.model.accessory.*;
import org.confluence.terra_curio.client.renderer.accessory.*;
import org.confluence.terra_curio.common.init.TCItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class CuriosClient {
    public static void registerRenderers() {
        EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
        CuriosRendererRegistry.register(TCItems.WORM_SCARF.get(), () -> new WormScarfRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.TERRASPARK_BOOTS.get(), () -> new TerrasparkBootsRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.DUNERIDER_BOOTS.get(), () -> new DuneriderBootsRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.MAGMA_SKULL.get(), () -> new MagmaSkullRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.FLURRY_BOOTS.get(), () -> new FlurryBootsRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.HERMES_BOOTS.get(), () -> new HermesBootsRenderer(entityModels));
        //CuriosRendererRegistry.register(TCItems.OBSIDIAN_SKULL.get(), () -> new ObsidianSkullRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.SHIELD_OF_CTHULHU.get(), () -> new ShieldOfCthulhuRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.NEPTUNES_SHELL.get(), () -> new MermanRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.MOON_CHARM.get(), () -> new WerewolfRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.MOON_SHELL.get(), () -> new MoonShellRenderer(entityModels));
        CuriosRendererRegistry.register(TCItems.CELESTIAL_SHELL.get(), () -> new MoonShellRenderer(entityModels));
    }

    public static void registerLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinition) {
        layerDefinition.accept(WormScarfModel.LAYER_LOCATION, WormScarfModel::createBodyLayer);
        layerDefinition.accept(TerrasparkBootsModel.LAYER_LOCATION, TerrasparkBootsModel::createBodyLayer);
        layerDefinition.accept(DuneriderBootsModel.LAYER_LOCATION, DuneriderBootsModel::createBodyLayer);
        layerDefinition.accept(MagmaSkullModel.LAYER_LOCATION, MagmaSkullModel::createBodyLayer);
        layerDefinition.accept(FlurryBootsModel.LAYER_LOCATION, FlurryBootsModel::createBodyLayer);
        layerDefinition.accept(HermesBootsModel.LAYER_LOCATION, HermesBootsModel::createBodyLayer);
        //layerDefinition.accept(ObsidianSkullModel.LAYER_LOCATION, ObsidianSkullModel::createBodyLayer);
        layerDefinition.accept(ShieldOfCthulhuModel.LAYER_LOCATION, ShieldOfCthulhuModel::createBodyLayer);
        layerDefinition.accept(MermanModel.LAYER_LOCATION, MermanModel::createBodyLayer);
        layerDefinition.accept(WerewolfModel.LAYER_LOCATION, WerewolfModel::createBodyLayer);
    }

    public static void onlyFollowMainBody(LivingEntity livingEntity, ModelPart... renderers) {
        EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);
        if (render instanceof LivingEntityRenderer<?, ?> livingRenderer && livingRenderer.getModel() instanceof HumanoidModel<?> humanoidModel) {
            for (ModelPart renderer : renderers) {
                renderer.copyFrom(humanoidModel.body);
            }
        }
    }
}
