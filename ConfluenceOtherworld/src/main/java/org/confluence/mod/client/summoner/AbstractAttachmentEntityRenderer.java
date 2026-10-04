package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.joml.Quaternionf;
import software.bernie.geckolib.core.object.Color;

/**
 * 附件实体渲染器基类。
 * <p>
 * 负责构建 {@link RenderContext}、写入颜色、绘制拖尾并施加模型变换，
 * 子类只需在 {@code createContext} 里链式配置上下文，并实现
 * {@link #renderModel}（模型本体）或 {@link #render}（附加渲染）。
 * </p>
 * <p>
 * 形态对齐 Lyra 1.21.1.13（{@code first.lyra.client.render.AbstractAttachmentEntityRenderer}）：
 * 使用 {@link #context} 字段 + 无参渲染钩子。
 * </p>
 *
 * @param <T> 附件实体类型
 * @see RenderContext
 * @see ModelContext
 */
public abstract class AbstractAttachmentEntityRenderer<T extends AttachmentEntity> implements IAttachmentEntityRenderer<T> {

    protected RenderContext<T> context;

    /** 构建当帧渲染上下文：子类调用 super 后链式追加配置 */
    protected RenderContext<T> createContext(T entity, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(entity, visualNode, partialTick, packedLight);
    }

    @Override
    public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, PathNode visualNode) {
        context = createContext(entity, visualNode, partialTick, packedLight);
        if (context == null) {
            return;
        }
        float alpha = getAlpha();
        Color color = context.color;
        context.color(Color.ofRGBA(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha));
        poseStack.pushPose();
        modelModify(poseStack, bufferSource);
        poseStack.popPose();
    }

    /** 施加视觉节点朝向与模型偏移，随后依次渲染模型本体与附加内容 */
    protected void modelModify(PoseStack poseStack, MultiBufferSource bufferSource) {
        PathNode visualNode = context.visualNode;
        ModelContext model = context.model;
        Quaternionf rotation = new Quaternionf()
                .mul(Axis.YN.rotationDegrees(visualNode.yaw()))
                .mul(Axis.XP.rotationDegrees(visualNode.pitch()))
                .mul(Axis.ZP.rotationDegrees(visualNode.roll()))
                .mul(Axis.YN.rotationDegrees(model.yawOffset))
                .mul(Axis.XP.rotationDegrees(model.pitchOffset))
                .mul(Axis.ZP.rotationDegrees(model.rollOffset));
        poseStack.pushPose();
        // 拖尾必须在这里渲染：调度器已把姿态原点平移到实体处，而拖尾顶点是
        // 「实体局部坐标」（RibbonTrailContext 用 node.pos() - visualNode.pos()），
        // 由 RenderUtil.writeVertices 取当前姿态变换。放在 modelModify 之前会被
        // 当成相机坐标，导致拖尾塌在镜头原点（表现为完全不显示）。
        // 注意：拖尾只吃位置，不该继承下面的模型朝向/缩放/平移，故紧随平移之后、
        // mulPose 之前提交。
        if (context.hasTrail()) {
            context.trail.render(poseStack, bufferSource, context);
        }
        poseStack.mulPose(rotation);
        poseStack.scale(model.scaleX, model.scaleY, model.scaleZ);
        poseStack.translate(model.translateX, model.translateY, model.translateZ);
        renderModel(poseStack, bufferSource);
        render(poseStack, bufferSource);
        poseStack.popPose();
    }

    /** 模型本体渲染（geo 渲染器在此提交模型），默认不渲染 */
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
    }

    /** 附加渲染，默认不渲染 */
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
    }

    /** 当前透明度（第一人称按距离淡化，AlphaModify 关闭时 1.0） */
    protected float getAlpha() {
        return SummonerRenderConfig.AlphaModify ? getAlphaModify() : 1.0F;
    }

    /** 第一人称下按距离淡出，避免贴脸时模型糊住视野 */
    protected float getAlphaModify() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || !minecraft.options.getCameraType().isFirstPerson()) {
            return 1.0F;
        }
        Vec3 entityPos = context.visualNode.pos();
        Vec3 eyePos = player.getEyePosition(context.partialTick);
        double distance = entityPos.distanceTo(eyePos);

        float minDistance = 0.5F * context.model.alphaDistanceFactor;
        float maxDistance = 4.0F * context.model.alphaDistanceFactor;

        if (distance <= minDistance) {
            return 0.0F;
        }
        if (distance >= maxDistance) {
            return 1.0F;
        }
        float alpha = (float) ((distance - minDistance) / (maxDistance - minDistance));
        return Math.max(0.102F, Math.min(1.0F, alpha));
    }
}
