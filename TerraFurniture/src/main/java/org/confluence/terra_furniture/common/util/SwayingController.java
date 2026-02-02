package org.confluence.terra_furniture.common.util;

import net.minecraft.world.phys.Vec3;

public class SwayingController {
    // 当前摇晃角度（绕X和Z轴）
    private float swingX = 0f;
    private float swingZ = 0f;

    // 当前摇晃速度
    private float velocityX = 0f;
    private float velocityZ = 0f;

    // 阻尼系数（越接近1越持久，越接近0越快衰减）
    private float DAMPING = 0.98f;

    // 回复力
    private float RESTORE_FORCE = 0.005f;

    // 最大摇晃角度（弧度）
    private float MAX_ANGLE = (float)Math.toRadians(30);

    // 映射加速度到角度的系数
    private float ACCEL_TO_ANGLE = 0.016f;

    public void updateSwing(Vec3 delta) {
        // 将加速度映射为角速度
        velocityX += (float) (-delta.z * ACCEL_TO_ANGLE);
        velocityZ += (float) (delta.x * ACCEL_TO_ANGLE);

        // 添加回正力（弹性恢复）
        velocityX -= swingX * RESTORE_FORCE;
        velocityZ -= swingZ * RESTORE_FORCE;

        // 应用阻尼
        velocityX *= DAMPING;
        velocityZ *= DAMPING;

        // 更新角度
        swingX += velocityX;
        swingZ += velocityZ;

        // 限制最大角度
        swingX = Math.max(-MAX_ANGLE, Math.min(MAX_ANGLE, swingX));
        swingZ = Math.max(-MAX_ANGLE, Math.min(MAX_ANGLE, swingZ));
    }

    public float getSwingX() {
        return swingX;
    }

    public float getSwingZ() {
        return swingZ;
    }

    public void setDAMPING(float DAMPING) {
        this.DAMPING = DAMPING;
    }

    public void setRESTORE_FORCE(float RESTORE_FORCE) {
        this.RESTORE_FORCE = RESTORE_FORCE;
    }

    public void setACCEL_TO_ANGLE(float ACCEL_TO_ANGLE) {
        this.ACCEL_TO_ANGLE = ACCEL_TO_ANGLE;
    }

    public void setMAX_ANGLE(float MAX_ANGLE) {
        this.MAX_ANGLE = MAX_ANGLE;
    }
}
