package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.confluence.terraentity.entity.monster.BaseWormPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class GeoWormSegmentRenderer<S extends BaseWormPart, R extends GeoWormRenderer> extends GeoEntityRenderer<S> {

    float scale;
    float offsetY;

    GeoNormalModel<S> tailModel;
    R parent;

    public GeoWormSegmentRenderer(EntityRendererProvider.Context renderManager, R parent,  ResourceLocation body, ResourceLocation tail) {
        this(renderManager, parent, body, tail,1,0);
    }
    public GeoWormSegmentRenderer(EntityRendererProvider.Context renderManager, R parent,  ResourceLocation body, ResourceLocation tail, float scale, float offsetY) {
        super(renderManager, new GeoNormalModel<>(body, false));
        this.scale=scale;
        this.offsetY=offsetY;
        tailModel = new GeoNormalModel<>(tail,false);
        this.parent = parent;
    }


    @Override
    public void render(S part, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

        double lerpx = Mth.lerp(partialTick, part.xo, part.getX());
        double lerpy = Mth.lerp(partialTick, part.yo, part.getY());
        double lerpz = Mth.lerp(partialTick, part.zo, part.getZ());
        poseStack.translate(lerpx - parent.lerpx, lerpy - parent.lerpy, lerpz - parent.lerpz);

        poseStack.mulPose(Axis.YN.rotationDegrees(entityYaw));
//        poseStack.mulPose(parent.resetX);

        float lerpXRot = Mth.lerp(partialTick, part.xRotO, part.getXRot());
//        poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(rad1), 0, (float) Math.sin(rad1))).rotationDegrees(-lerpXRot));
        poseStack.mulPose(Axis.XN.rotationDegrees(lerpXRot));
        poseStack.scale(scale, scale, scale);

        super.render(part, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(S animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);

    }

    @Override
    public GeoModel<S> getGeoModel() {
        return this.animatable!=null && this.animatable.isTail? tailModel : this.model;
    }

    @Override
    public int getPackedOverlay(S animatable, float u, float partialTick) {

        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0 || animatable.deathTime > 0));
    }

}
