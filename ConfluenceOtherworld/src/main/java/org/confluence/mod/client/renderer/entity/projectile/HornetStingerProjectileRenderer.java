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
import org.confluence.mod.client.model.entity.projectile.HornetStingerProjectileModel;
import org.confluence.mod.common.entity.projectile.HornetStingerProjectile;

/**
 * 按实际飞行方向绘制黄蜂毒刺。
 */
public final class HornetStingerProjectileRenderer
        extends EntityRenderer<HornetStingerProjectile> {
    private static final ResourceLocation TEXTURE =
            Confluence.asResource("textures/entity/model/stinger.png");
    private final HornetStingerProjectileModel model;

    public HornetStingerProjectileRenderer(
            EntityRendererProvider.Context context) {
        super(context);
        this.model = new HornetStingerProjectileModel(
                context.bakeLayer(HornetStingerProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(HornetStingerProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(
            HornetStingerProjectile entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight) {
        Vec3 velocity = entity.getDeltaMovement();
        poseStack.pushPose();
        if (velocity.lengthSqr() > 1.0E-7) {
            double horizontal = Math.sqrt(
                    velocity.x * velocity.x + velocity.z * velocity.z);
            poseStack.mulPose(Axis.YN.rotation(
                    (float) (Math.atan2(velocity.z, velocity.x)
                            - Math.PI / 2.0)));
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
        super.render(
                entity, entityYaw, partialTick, poseStack,
                bufferSource, packedLight);
    }
}
