package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class SimpleRenderer<T extends AttachmentEntity> extends AbstractAttachmentEntityRenderer<T> {

    private final Render<T> renderer;

    @FunctionalInterface
    public interface Render<T extends AttachmentEntity> {
        void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<T> context, float partialTick, float alpha);
    }

    public SimpleRenderer(@Nullable Render<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    protected RenderContext<T> createContext(T entity, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(entity, visualNode, partialTick, packedLight);
    }

    /**
     * 本渲染器绘制的是<b>面向相机的贴图</b>（{@code RenderUtil.renderImage} 内部再乘 cameraOrientation）
     * 与<b>面向相机的文本</b>，方向完全由相机决定。
     * <p>
     * 因此这里刻意不施加基类的 visualNode 朝向 / model 缩放平移变换：叠加后贴图会随实体朝向翻滚，
     * 与旧实现（{@code modelModify} 覆写里不带任何姿态变换）保持一致。
     * </p>
     */
    @Override
    protected void modelModify(PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        renderModel(poseStack, bufferSource);
        render(poseStack, bufferSource);
        poseStack.popPose();
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        if (renderer != null) {
            renderer.render(context.entity, poseStack, bufferSource, context.visualNode, context, context.partialTick, context.color.getAlphaFloat());
        } else {
            ResourceLocation location = context.entity.getType().location();
            String key = "summon." + location.getNamespace() + "." + location.getPath();
            Component component = Component.translatable(key).withStyle(ChatFormatting.DARK_AQUA);
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            poseStack.pushPose();
            Matrix4f pose = poseStack.last().pose();
            float x = pose.m30(), y = pose.m31(), z = pose.m32();
            poseStack.setIdentity();
            poseStack.translate(x, y, z);
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            poseStack.scale(0.02F, -0.02F, 0.02F);
            PoseStack.Pose last = poseStack.last();
            font.drawInBatch(component, (float) -font.width(component) / 2, -font.lineHeight, 0xFFFFFF, true, last.pose(), bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        }
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
    }
}
