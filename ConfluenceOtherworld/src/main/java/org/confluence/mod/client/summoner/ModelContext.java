package org.confluence.mod.client.summoner;

/**
 * 模型渲染上下文：缩放、平移、朝向偏移与透明度距离修正系数，链式配置。
 * <p>
 * 对齐 Lyra 1.21.1.13（{@code first.lyra.client.render.ModelContext}），
 * 取代原先位于 {@code trail} 包下的 {@code ModelConfig}。
 * </p>
 */
public class ModelContext {

    public float scaleX = 1.0F;
    public float scaleY = 1.0F;
    public float scaleZ = 1.0F;

    public float translateX = 0.0F;
    public float translateY = 0.0F;
    public float translateZ = 0.0F;

    public float yawOffset = 0.0F;
    public float pitchOffset = 0.0F;
    public float rollOffset = 0.0F;

    /** 透明度距离修正系数 */
    public float alphaDistanceFactor = 1.0F;

    public ModelContext scale(float scale) {
        return scale(scale, scale, scale);
    }

    public ModelContext scale(float scaleX, float scaleY, float scaleZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        return this;
    }

    public ModelContext translateOffset(float x, float y, float z) {
        this.translateX = x;
        this.translateY = y;
        this.translateZ = z;
        return this;
    }

    public ModelContext rotationOffset(float yaw, float pitch, float roll) {
        this.yawOffset = yaw;
        this.pitchOffset = pitch;
        this.rollOffset = roll;
        return this;
    }

    public ModelContext alphaDistanceFactor(float factor) {
        this.alphaDistanceFactor = factor;
        return this;
    }
}
