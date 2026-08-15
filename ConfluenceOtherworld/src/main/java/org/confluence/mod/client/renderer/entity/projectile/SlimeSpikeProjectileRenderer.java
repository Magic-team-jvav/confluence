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
import org.confluence.mod.client.model.entity.projectile.SlimeSpikeProjectileModel;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

/**
 * 按弹丸当前运动方向绘制史莱姆尖刺。
 */
public class SlimeSpikeProjectileRenderer extends EntityRenderer<SlimeSpikeEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource(
            "textures/entity/proj/slime_spiked_projectile.png");
    private final SlimeSpikeProjectileModel model;

    public SlimeSpikeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SlimeSpikeProjectileModel(
                context.bakeLayer(SlimeSpikeProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeSpikeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SlimeSpikeEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight) {
        Vec3 velocity = entity.getDeltaMovement();
        poseStack.pushPose();
        if (velocity.lengthSqr() > 1.0E-7) {
            double horizontal = Math.sqrt(
                    velocity.x * velocity.x + velocity.z * velocity.z);
            poseStack.mulPose(Axis.YN.rotation(
                    (float) (Math.atan2(velocity.z, velocity.x) - Math.PI / 2.0)));
            poseStack.mulPose(Axis.ZN.rotation(
                    (float) Math.atan2(velocity.y, horizontal)));
        }
        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(model.renderType(TEXTURE)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack,
                bufferSource, packedLight);
    }
}
