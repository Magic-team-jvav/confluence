package org.confluence.mod.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.function.Supplier;

/// 用于管理缓冲区的抽象类
public abstract class AbstractBufferManager {
    protected VertexBuffer vertexBuffer;
    protected long lastRefreshTime = 0;
    protected final int refreshInterval;

    /// @param refreshTime 刷新间隔，单位毫秒
    public AbstractBufferManager(int refreshTime) {
//        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.refreshInterval = refreshTime;
    }

    protected boolean shouldRefresh() {
        return System.currentTimeMillis() - lastRefreshTime > refreshInterval;
    }

    protected abstract void beforeRender();

    protected abstract void afterRender(PoseStack poseStack);

    protected abstract void buildBuffer(BufferBuilder buffer);

    public void refresh() {
        lastRefreshTime = System.currentTimeMillis();
        if (vertexBuffer != null) {
            vertexBuffer.close();
        }
        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
        BufferBuilder buffer = getBufferBuilder();

        buildBuffer(buffer);
        this.bind(buffer);
    }

    public void bind(BufferBuilder buffer) {
        vertexBuffer.bind();
        vertexBuffer.upload(buffer.end());
        VertexBuffer.unbind();
    }

    public BufferBuilder getBufferBuilder() {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        return builder;
    }

    public void render(RenderLevelStageEvent event) {
        render(event.getPoseStack(), Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), event.getProjectionMatrix());
    }

    public void render(PoseStack poseStack, Vec3 playerPos, Matrix4f projectMatrix) {
        if (shouldRefresh()) {
            refresh();
        }

        if (vertexBuffer != null) {
            beforeRender();
            RenderSystem.setShader(setShader());

            poseStack.pushPose();
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

    protected Supplier<ShaderInstance> setShader() {
        return GameRenderer::getPositionColorShader;
    }

    public VertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }
}
