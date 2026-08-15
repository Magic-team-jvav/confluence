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
import org.confluence.mod.client.model.entity.projectile.DemonScytheProjectileModel;
import org.confluence.mod.common.entity.projectile.HostileDemonScytheProjectile;

/**
 * 生物恶魔镰刀的客户端渲染器。
 *
 * <p>与玩家武器弹幕共享模型和贴图，但读取各自独立的旋转状态，
 * 从而不让客户端表现反向耦合玩家法力弹幕的运行时实现。</p>
 */
public final class HostileDemonScytheProjectileRenderer
        extends EntityRenderer<HostileDemonScytheProjectile> {
    private static final ResourceLocation TEXTURE =
            Confluence.asResource("textures/entity/demon_scythe_projectile.png");
    private final DemonScytheProjectileModel model;

    public HostileDemonScytheProjectileRenderer(
            EntityRendererProvider.Context context) {
        super(context);
        this.model = new DemonScytheProjectileModel(
                context.bakeLayer(DemonScytheProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(
            HostileDemonScytheProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(
            HostileDemonScytheProjectile entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.75F, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.ZP.rotation(
                -Mth.lerp(partialTick, entity.rotate.old, entity.rotate.neo)));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(model.renderType(TEXTURE)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1,
                1,
                1,
                1);
        poseStack.popPose();
    }
}
