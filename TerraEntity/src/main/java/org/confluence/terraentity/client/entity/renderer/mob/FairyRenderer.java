package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * 调整了负体积渲染的初始化，适配更复杂的模型
 */
public class FairyRenderer<T extends Entity & GeoEntity> extends GeoNegativeVolumeRenderer<T> {


    public FairyRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        this(renderManager, path, true);
    }

    public FairyRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX) {
        this(renderManager, path, ifRotX, 1.0F, 0.0F);
    }

    public FairyRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX, float scale, float offsetY) {
        this(renderManager, new GeoNormalModel<>(path), ifRotX, scale, offsetY);
    }
    public FairyRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model, ifRotX, scale, offsetY);
    }

//    @Override
//    protected void processInit(BakedGeoModel model){
//
//        Stream.of("Outline", "Outline2","Outline3","Outline4","Outline5")
//                .map(s->model.getBone(s).get())
//                .forEach(toHide::add);
//        Stream.of("Body", "Internal","Internal2","Internal3","Internal4")
//                .map(s->model.getBone(s).get())
//                .forEach(notToHide::add);
//
//    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, int colour) {
        if(isReRender){
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, 0xFFFFFFFF);
        }else{
             super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, 0X0F000F0, packedOverlay, 0xFFFFFFFF);
        }
    }
}
