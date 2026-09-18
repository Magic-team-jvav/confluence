package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.confluence.mod.client.summoner.SummonerRenderConfig;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.client.summoner.trail.TrailConfig;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * 附件实体渲染器抽象基类。
 * <p>
 * 提供完整的拖尾渲染和本体渲染框架，支持强类型配置分离。
 * </p>
 *
 * @param <T> 附件实体类型
 * @see RenderContext
 * @see TrailConfig
 * @see ModelConfig
 */
public abstract class AbstractAttachmentEntityRenderer<T extends AttachmentEntity> implements IAttachmentEntityRenderer<T> {

    /**
     * 为指定附件实体创建渲染上下文
     *
     * @param entity      附件实体
     * @param partialTick 部分 tick 插值进度
     */
    protected abstract RenderContext<T> createContext(T entity, float partialTick);

    /** 渲染附件实体本体（alpha：第一人称距离淡化透明度，模型渲染调用点直接使用） */
    protected abstract void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<T> context, float partialTick, int packedLight, float alpha);

    @Override
    public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, PathNode visualNode) {
        RenderContext<T> context = createContext(entity, partialTick);
        if (context != null) {
            poseStack.pushPose();
            float alpha = getAlpha(context, visualNode, partialTick);
            if (context.hasTrail()) {
                context.trail.render(entity, poseStack, bufferSource, partialTick, visualNode, LyraRenderTypes.getTrail());
            }
            modelModify(entity, poseStack, bufferSource, visualNode, context, partialTick, packedLight, alpha);
            poseStack.popPose();
        }
    }

    /** 当前透明度（第一人称按距离淡化，AlphaModify 关闭时 1.0）。渲染器调用点直接使用。 */
    protected float getAlpha(RenderContext<T> context, PathNode visualNode, float partialTick) {
        return SummonerRenderConfig.AlphaModify ? getAlphaModify(context, visualNode, partialTick) : 1.0f;
    }

    protected void modelModify(T entity, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<T> context, float partialTick, int packedLight, float alpha) {
        ModelConfig<T> model = context.model;

        // 绕过 PoseStack 的 6 次 mulPose + scale + translate，
        // 直接用 JOML 构造完整变换矩阵，一次性设入 PoseStack。
        // 使用 Axis.rotationDegrees() 构造四元数，与原版 mulPose 完全一致。
        float yawDeg = visualNode.yaw();
        float pitchDeg = visualNode.pitch();
        float rollDeg = visualNode.roll();

        // 逐个构造四元数，再合成为一个旋转，与 mulPose 顺序完全一致
        Quaternionf qYaw = Axis.YN.rotationDegrees(yawDeg);
        Quaternionf qPitch = Axis.XP.rotationDegrees(pitchDeg);
        Quaternionf qRoll = Axis.ZP.rotationDegrees(rollDeg);
        Quaternionf qYawOff = Axis.YN.rotationDegrees(model.yawOffset);
        Quaternionf qPitchOff = Axis.XP.rotationDegrees(model.pitchOffset);
        Quaternionf qRollOff = Axis.ZP.rotationDegrees(model.rollOffset);

        // mulPose 是左乘：result = q * current，所以合成的顺序是 qRollOff * qPitchOff * qYawOff * qRoll * qPitch * qYaw
        Quaternionf rotation = new Quaternionf(qYaw)
                .mul(qPitch)
                .mul(qRoll)
                .mul(qYawOff)
                .mul(qPitchOff)
                .mul(qRollOff);

        float s = model.scale;
        // 构造完整变换：旋转 → 缩放 → 平移
        Matrix4f transform = new Matrix4f()
                .rotate(rotation)
                .scale(s, s, s)
                .translate(model.translateX, model.translateY, model.translateZ);

        poseStack.pushPose();
        poseStack.last().pose().mul(transform);
        poseStack.last().normal().mul(new Matrix3f().rotation(rotation));

        render(entity, poseStack, bufferSource, visualNode, context, partialTick, packedLight, alpha);
        poseStack.popPose();
    }

    protected float getAlphaModify(RenderContext<T> config, PathNode visualNode, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || !minecraft.options.getCameraType().isFirstPerson()) {
            return 1.0f;
        }

        Vec3 entityPos = visualNode.pos();
        Vec3 eyePos = player.getEyePosition(partialTick);
        double distance = entityPos.distanceTo(eyePos);

        float minDistance = 0.5f * config.model.alphaDistanceFactor;
        float maxDistance = 4.0f * config.model.alphaDistanceFactor;

        if (distance <= minDistance) {
            return 0.0f;
        }
        if (distance >= maxDistance) {
            return 1.0f;
        }

        float alpha = (float) ((distance - minDistance) / (maxDistance - minDistance));
        return Math.max(0.102f, Math.min(1.0f, alpha));
    }
}
