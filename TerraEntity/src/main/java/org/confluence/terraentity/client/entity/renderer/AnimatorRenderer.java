package org.confluence.terraentity.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import org.confluence.terraentity.client.entity.model.AnimatorModel;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import javax.annotation.Nullable;

/**
 * 带有硬编码动画的生物骨骼动画渲染器
 */
public class AnimatorRenderer<T extends Mob & GeoEntity> extends GeoNormalRenderer<T>{

    protected final AnimatorModel<T> model;
    private boolean init = false;
    public AnimatorRenderer(EntityRendererProvider.Context renderManager, AnimatorModel<T> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model, ifRotX, scale, offsetY);
        this.model = model;
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @org.jetbrains.annotations.Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if(!isReRender && !init){
            init = true;
            this.model.initBoneAnimators(animatable, model);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
