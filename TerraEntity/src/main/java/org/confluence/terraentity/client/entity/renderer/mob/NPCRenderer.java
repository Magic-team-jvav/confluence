package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terraentity.api.npc.chat.IBubbleRenderer;
import org.confluence.terraentity.client.buffer.NPCChatBubbleBuffer;
import org.confluence.terraentity.client.event.RenderEvent;
import org.confluence.terraentity.config.ClientConfig;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatArranger;
import org.joml.Matrix4f;

public class NPCRenderer<T extends AbstractTerraNPC> extends HumanoidRenderer<T>{

    MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    IBubbleRenderer bubbleRenderer;

    public NPCRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path.withPrefix("npc/"));
        setMotionAnimThreshold(0.01F);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if(Minecraft.getInstance().player != null && entity.getChat()!= null && RenderEvent.isAfterSky) { // iris的阴影会在renderSky之前调用
            ChatArranger chat = entity.getChat();
            float scale = chat.getBubbleScale();
            float width = 50 * scale;
            float height = 50 * scale;
            float progress = (entity.chatCount - partialTick )/ entity._chatCount;
            progress = progress * (1 - progress) * 10;
            progress = 1 - Mth.clamp(progress, 0, 1);

            poseStack.pushPose();
            // 存储之前的mp
            Matrix4f cache_m = new Matrix4f(RenderSystem.getModelViewMatrix());
            Matrix4f cache_p = new Matrix4f(RenderSystem.getProjectionMatrix());
            RenderSystem.getModelViewMatrix().set(new Matrix4f());
            RenderSystem.getProjectionMatrix().set(createOrthographicMatrix(0, 100 * scale, 100 * scale, 0, -100, 100));

            // 离屏渲染聊天气泡
            TextureTarget target = entity.textureTarget;
            if(target == null){
                target = new TextureTarget(200,200,false, false);
                entity.textureTarget = target;
            }
            target.setClearColor(0,1,0,1f);
//            target.clear(true);
            target.bindWrite(true);
            GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(), this.bufferSource);


            // 渲染内容
            bubbleRenderer = ClientConfig.NPC_CHAT_BUBBLE_STYLE.get().getRenderer();
            bubbleRenderer.renderBubble(guiGraphics, chat, poseStack, entity.level(), this.bufferSource, scale, width, height, packedLight, OverlayTexture.NO_OVERLAY);

            guiGraphics.flush();

            // 恢复之前的mp
            RenderSystem.getModelViewMatrix().set(cache_m);
            RenderSystem.getProjectionMatrix().set(cache_p);

            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);

//            target.blitToScreen(200,200); // debug



            poseStack.translate(0, 2.5, 0);

            // yaw面向摄像机
            float yaw1 = -Mth.lerp(partialTick, Minecraft.getInstance().player.yHeadRotO, Minecraft.getInstance().player.yHeadRot);
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw1));

            bubbleRenderer.adjustPose(poseStack, scale, width, height);


            // 聊天气泡广告牌渲染到头顶
            poseStack.scale(scale, scale, 1);
            poseStack.translate(0, height / 50 * 0.05f, 0);

            renderChatBubble(poseStack.last().pose(), target, progress, packedLight);

            poseStack.popPose();
        }

    }

    /**
     * 渲染聊天气泡Billboard
     */
    private void renderChatBubble(Matrix4f viewMatrix, TextureTarget target, float progress, int light) {

        BufferBuilder consumer = NPCChatBubbleBuffer.getInstance().getBufferBuilder();

        consumer.addVertex(viewMatrix, -0.5f, -0.5f, 0).setUv(1, 0);
        consumer.addVertex(viewMatrix, 0.5f, -0.5f, 0).setUv(0, 0);
        consumer.addVertex(viewMatrix, 0.5f, 0.5f, 0).setUv(0, 1);
        consumer.addVertex(viewMatrix, -0.5f, 0.5f, 0).setUv(1, 1);

        NPCChatBubbleBuffer.getInstance().addRenderData(consumer, target, progress, light);

    }

    /**
     * 创建正交投影矩阵
     */
    public static Matrix4f createOrthographicMatrix(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.setOrtho(left, right, bottom, top, near, far);
        return matrix;
    }


}
