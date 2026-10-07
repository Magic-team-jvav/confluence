package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
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

    /// 贴图和文本始终面向相机，不叠加实体朝向及模型缩放平移。
    /// 避免展示图标随实体姿态翻滚。
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
