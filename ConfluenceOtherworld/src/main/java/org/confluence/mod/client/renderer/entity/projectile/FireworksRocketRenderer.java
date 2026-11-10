package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.FireworksRocketModel;
import org.confluence.mod.common.entity.projectile.FireworksRocketProjectile;

/// 按实际飞行方向绘制烟花火箭：模型机头朝 -Z，先俯仰再偏航。
public final class FireworksRocketRenderer extends EntityRenderer<FireworksRocketProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/model/fireworks_rocket.png");
    private final FireworksRocketModel model;

    public FireworksRocketRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new FireworksRocketModel(context.bakeLayer(FireworksRocketModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(FireworksRocketProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(FireworksRocketProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Vec3 velocity = entity.getDeltaMovement();
        poseStack.pushPose();
        if (velocity.lengthSqr() > 1.0E-7) {
            Vec3 direction = velocity.normalize();
            poseStack.mulPose(Axis.YP.rotation((float) Math.atan2(-direction.x, -direction.z)));
            poseStack.mulPose(Axis.XP.rotation((float) Math.asin(Mth.clamp(direction.y, -1.0F, 1.0F))));
        }
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
