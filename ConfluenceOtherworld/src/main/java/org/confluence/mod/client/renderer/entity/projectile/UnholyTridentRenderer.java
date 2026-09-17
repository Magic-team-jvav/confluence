package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.UnholyTridentProjectile;

public final class UnholyTridentRenderer extends EntityRenderer<UnholyTridentProjectile> {
    private final TridentModel model;

    public UnholyTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new TridentModel(context.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void render(UnholyTridentProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        Vec3 velocity = entity.getDeltaMovement();
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotation((float) Math.atan2(velocity.x, velocity.z) - (float) Math.PI / 2.0F));
        poseStack.mulPose(Axis.ZP.rotation((float) Math.atan2(velocity.y, velocity.horizontalDistance()) + (float) Math.PI / 2.0F));
        model.renderToBuffer(poseStack, buffers.getBuffer(model.renderType(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, 0.7F, 0.25F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(UnholyTridentProjectile entity) {
        return ThrownTridentRenderer.TRIDENT_LOCATION;
    }
}
