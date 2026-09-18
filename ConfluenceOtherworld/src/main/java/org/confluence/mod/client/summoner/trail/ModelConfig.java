package org.confluence.mod.client.summoner.trail;

import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;

/**
 * 模型渲染配置。
 * <p>
 * 封装实体本体渲染所需的所有参数，包括缩放、平移、旋转偏移等。
 * </p>
 *
 * @param <T> 实体类型
 */
public class ModelConfig<T extends AttachmentEntity> {

    // ===================== 缩放参数 =====================

    /**
     * 模型缩放比例
     */
    public float scale = 1.0f;

    // ===================== 平移参数 =====================

    /**
     * X 轴平移偏移
     */
    public float translateX = 0f;

    /**
     * Y 轴平移偏移
     */
    public float translateY = 0f;

    /**
     * Z 轴平移偏移
     */
    public float translateZ = 0f;

    // ===================== 旋转参数 =====================

    /**
     * Yaw 轴旋转偏移（度）
     */
    public float yawOffset = 0f;

    /**
     * Pitch 轴旋转偏移（度）
     */
    public float pitchOffset = 0f;

    /**
     * Roll 轴旋转偏移（度）
     */
    public float rollOffset = 0f;

    // ===================== 透明度参数 =====================

    /**
     * 透明度距离修正系数
     */
    public float alphaDistanceFactor = 1.0f;

    // ===================== 链式配置方法 =====================

    protected ModelConfig<T> self() {
        return this;
    }

    public ModelConfig<T> scale(float scale) {
        this.scale = scale;
        return this;
    }

    public ModelConfig<T> translateOffset(float x, float y, float z) {
        this.translateX = x;
        this.translateY = y;
        this.translateZ = z;
        return this;
    }

    public ModelConfig<T> rotationOffset(float yaw, float pitch, float roll) {
        this.yawOffset = yaw;
        this.pitchOffset = pitch;
        this.rollOffset = roll;
        return this;
    }

    public ModelConfig<T> alphaDistanceFactor(float factor) {
        this.alphaDistanceFactor = factor;
        return this;
    }
}
