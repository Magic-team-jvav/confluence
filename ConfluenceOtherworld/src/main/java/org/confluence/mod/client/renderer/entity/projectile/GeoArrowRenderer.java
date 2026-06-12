package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.projectile.range.arrow.HellBatArrowEntity;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class GeoArrowRenderer extends GeoNormalRenderer<HellBatArrowEntity> {


    public GeoArrowRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path, false);
    }

    public GeoArrowRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, float scale, float offsetY) {
        super(renderManager, path, false, scale, offsetY);
    }


    @Override
    protected void adjustPose(PoseStack poseStack, HellBatArrowEntity animatable, BakedGeoModel model, float partialTick) {
        poseStack.translate(0, 0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(60F));
        poseStack.translate(0F, -0.5, -0.3F);
    }
}
