package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;

import java.util.Objects;

/// 使用实体模型渲染直线剑气，并统一处理沿速度方向旋转、出生缩放和可选滚转。
///
/// <p>本类只存在于客户端包。公共剑气实体不保存模型、纹理或任何渲染器状态，避免专用服务端
/// 链接客户端类型，也避免不同实体共享渲染器时互相污染动画进度。</p>
public class ForwardSwordProjectileRenderer<T extends SwordProjectile> extends EntityRenderer<T> {
    private final EntityModel<SwordProjectile> model;
    private final ResourceLocation texture;
    private final float scale;
    private final float offsetY;
    private final float rollSpeed;

    public ForwardSwordProjectileRenderer(
            EntityRendererProvider.Context context,
            EntityModel<SwordProjectile> model,
            ResourceLocation texture,
            float scale,
            float offsetY,
            float rollSpeed
    ) {
        super(context);
        this.model = Objects.requireNonNull(model, "Sword projectile model must not be null");
        this.texture = Objects.requireNonNull(texture, "Sword projectile texture must not be null");
        if (!Float.isFinite(scale) || scale <= 0.0F) {
            throw new IllegalArgumentException("Sword projectile render scale must be finite and positive");
        }
        if (!Float.isFinite(offsetY) || !Float.isFinite(rollSpeed)) {
            throw new IllegalArgumentException("Sword projectile render offsets must be finite");
        }
        this.scale = scale;
        this.offsetY = offsetY;
        this.rollSpeed = rollSpeed;
    }

    public ForwardSwordProjectileRenderer(
            EntityRendererProvider.Context context,
            EntityModel<SwordProjectile> model,
            ResourceLocation texture,
            float scale,
            float offsetY
    ) {
        this(context, model, texture, scale, offsetY, 0.0F);
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
        float ageScale = getAgeScale(entity, partialTick);
        poseStack.scale(scale * ageScale, scale * ageScale, scale * ageScale);
        poseStack.translate(0.0F, offsetY, 0.0F);
        applyOrientation(entity, partialTick, poseStack);

        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, entityYaw, entity.getXRot());
        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(getRenderType(entity, partialTick)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    /// 出生前十 tick 平滑放大，消除模型在网络生成帧突然跳出的视觉闪烁。
    protected float getAgeScale(T entity, float partialTick) {
        return Math.min((entity.tickCount + partialTick) * 0.1F, 1.0F);
    }

    /// 按当前实际运动向量定向；渲染不依赖客户端自行猜测的武器朝向。
    protected void applyOrientation(T entity, float partialTick, PoseStack poseStack) {
        Vec3 motion = entity.getDeltaMovement();
        Vec3 orientation = motion.lengthSqr() > 1.0E-10 ? motion : entity.getProjectileDirection();
        if (orientation.lengthSqr() > 1.0E-10) {
            motion = orientation;
            float yaw = (float) Math.atan2(motion.z, motion.x);
            float pitch = -(float) Math.atan2(motion.y, motion.horizontalDistance());
            poseStack.mulPose(Axis.YN.rotation(yaw + Mth.HALF_PI));
            poseStack.mulPose(Axis.XN.rotation(pitch));
        }
        if (rollSpeed != 0.0F) {
            poseStack.mulPose(Axis.ZN.rotation((entity.tickCount + partialTick) * rollSpeed));
        }
    }

    protected RenderType getRenderType(T entity, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
