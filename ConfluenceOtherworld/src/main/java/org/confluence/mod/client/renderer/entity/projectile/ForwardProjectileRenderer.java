package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/// 按实体飞行方向渲染模型的通用弹幕渲染器。
///
/// <p>这类弹幕的外观应该沿当前速度向量朝前，而不是依赖实体同步到客户端的
/// {@code yRot/xRot}。蜜蜂、蜜蜂箭这类会持续调整速度的小型弹幕，如果使用普通实体朝向，
/// 客户端很容易看到模型集体朝向玩家或朝向上一帧方向。</p>
public class ForwardProjectileRenderer<T extends Entity, M extends EntityModel<T>> extends EntityRenderer<T> {
    private final M model;
    private final ResourceLocation texture;
    private final float scale;
    private final float yOffset;
    private final float zSpinSpeed;

    public ForwardProjectileRenderer(
            EntityRendererProvider.Context context,
            M model,
            ResourceLocation texture
    ) {
        this(context, model, texture, 1.0F, 0.0F, 0.0F);
    }

    public ForwardProjectileRenderer(
            EntityRendererProvider.Context context,
            M model,
            ResourceLocation texture,
            float scale,
            float yOffset,
            float zSpinSpeed
    ) {
        super(context);
        this.model = model;
        this.texture = texture;
        this.scale = scale;
        this.yOffset = yOffset;
        this.zSpinSpeed = zSpinSpeed;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }

    @Override
    public void render(
            T entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();
        poseStack.translate(0.0F, yOffset, 0.0F);
        poseStack.scale(scale, scale, scale);

        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.lengthSqr() > 1.0E-7) {
            float yaw = (float) Math.atan2(velocity.z, velocity.x);
            float pitch = -(float) Math.atan2(velocity.y, velocity.horizontalDistance());
            poseStack.mulPose(Axis.YN.rotation(yaw + Mth.HALF_PI));
            poseStack.mulPose(Axis.XN.rotation(pitch));
        }
        if (zSpinSpeed > 0.0F) {
            poseStack.mulPose(Axis.ZN.rotation((entity.tickCount + partialTick) * zSpinSpeed));
        }

        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(model.renderType(texture)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
