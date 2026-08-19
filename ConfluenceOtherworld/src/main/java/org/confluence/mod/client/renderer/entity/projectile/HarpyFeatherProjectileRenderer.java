package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.HarpyFeatherProjectileModel;
import org.confluence.mod.common.entity.projectile.HarpyFeatherProjectile;

/// 按当前飞行方向绘制鸟妖羽毛弹幕。
public final class HarpyFeatherProjectileRenderer extends EntityRenderer<HarpyFeatherProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/model/harpy_feather_projectile.png");
    private final HarpyFeatherProjectileModel model;

    public HarpyFeatherProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new HarpyFeatherProjectileModel(context.bakeLayer(HarpyFeatherProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(HarpyFeatherProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(HarpyFeatherProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Vec3 velocity = entity.getDeltaMovement();
        poseStack.pushPose();
        if (velocity.lengthSqr() > 1.0E-7) {
            float yaw = (float) Math.atan2(velocity.z, velocity.x);
            float pitch = (float) Math.atan2(velocity.y, Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z));
            poseStack.mulPose(Axis.YN.rotation(yaw - (float) Math.PI / 2.0F));
            poseStack.mulPose(Axis.ZN.rotation(pitch));
        }
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
