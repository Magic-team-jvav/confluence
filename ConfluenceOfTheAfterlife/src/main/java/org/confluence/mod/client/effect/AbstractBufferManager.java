package org.confluence.mod.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/**
 * 用于管理缓冲区的抽象类
 */
public abstract class AbstractBufferManager {
    private VertexBuffer vertexBuffer;
    long lastRefreshTime = 0;
    final int refreshInterval;

    /**
     * @param refreshTime 刷新间隔，单位毫秒
     */
    public AbstractBufferManager(int refreshTime) {
        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.refreshInterval = refreshTime;
    }

    boolean shouldRefresh() {
        return System.currentTimeMillis() - lastRefreshTime > refreshInterval;
    }

    protected abstract void beforeRender();

    protected abstract void afterRender(PoseStack poseStack);

    protected abstract void buildBuffer(BufferBuilder buffer);

    public void refresh() {
        lastRefreshTime = System.currentTimeMillis();

        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        buildBuffer(buffer);

        var build = buffer.build();
        if (build == null) {
            vertexBuffer = null;
        } else {
            vertexBuffer.bind();
            vertexBuffer.upload(build);
            VertexBuffer.unbind();
        }
    }


    public void render(RenderLevelStageEvent event){
        render(event.getPoseStack(), event.getModelViewMatrix(), Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), event.getProjectionMatrix());

    }
    public void render(PoseStack poseStack, Matrix4f modelMatrix, Vec3 playerPos, Matrix4f projectMatrix){
        if(shouldRefresh())
            refresh();

        if (vertexBuffer != null) {

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            beforeRender();

            poseStack.pushPose();
            poseStack.mulPose(modelMatrix);
            poseStack.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());
//            poseStack.mulPose(event.getCamera().rotation());

//            minecraft.getBlockRenderer().renderSingleBlock(minecraft.level.getBlockState(BlockPos.containing(playerPos.subtract(0,-1,0))),poseStack,minecraft.renderBuffers().bufferSource(),15, OverlayTexture.NO_OVERLAY);
            vertexBuffer.bind();
            vertexBuffer.drawWithShader(poseStack.last().pose(), projectMatrix, RenderSystem.getShader());
            VertexBuffer.unbind();
            poseStack.popPose();

            afterRender(poseStack);

        }
    }
}
