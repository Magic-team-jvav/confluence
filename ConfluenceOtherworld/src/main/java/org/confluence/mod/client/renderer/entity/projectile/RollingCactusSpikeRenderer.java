package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.RollingCactusSpikeModel;
import org.confluence.mod.common.entity.projectile.boulder.RollingCactusBoulderEntity;

public class RollingCactusSpikeRenderer extends EntityRenderer<RollingCactusBoulderEntity.SpikeProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/rolling_cactus_spike.png");

    private final RollingCactusSpikeModel model;

    public RollingCactusSpikeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new RollingCactusSpikeModel(context.bakeLayer(RollingCactusSpikeModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(RollingCactusBoulderEntity.SpikeProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(RollingCactusBoulderEntity.SpikeProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }
}
