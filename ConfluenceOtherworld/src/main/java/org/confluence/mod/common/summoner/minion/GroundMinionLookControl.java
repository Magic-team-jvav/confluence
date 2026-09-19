package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 地面召唤物的视角控制，参考原版 {@link net.minecraft.world.entity.ai.control.LookControl}：
 * 只转动头部，不带动整个模型，观察位置取召唤物脚下位置加上视角高度。
 */
public class GroundMinionLookControl {

    /** 头部相对身体的偏航上限 */
    private static final float MAX_HEAD_YAW = 75.0F;
    /** 头部俯仰上限 */
    private static final float MAX_HEAD_PITCH = 40.0F;
    /** 每 tick 的转动速度 */
    private static final float YAW_SPEED = 10.0F;
    private static final float PITCH_SPEED = 10.0F;

    protected final GroundMinion minion;
    protected float headYaw;
    protected float headYawO;
    protected float headPitch;
    protected float headPitchO;
    protected @Nullable Vec3 target;

    public GroundMinionLookControl(GroundMinion minion) {
        this.minion = minion;
    }

    /**
     * 设置观察位置，传 null 表示视线回到正前方。
     */
    public void lookAt(@Nullable Vec3 target) {
        this.target = target;
    }

    public void lookAt(LivingEntity target) {
        lookAt(target.getBoundingBox().getCenter());
    }

    /**
     * 视线回到身体正前方。
     */
    public void lookForward() {
        target = null;
    }

    /**
     * 在服务端平滑地把头部转向目标，超出头部活动范围时让身体缓慢跟上。
     */
    public void tick() {
        headYawO = headYaw;
        headPitchO = headPitch;
        float wantedYaw = 0.0F;
        float wantedPitch = 0.0F;
        if (target != null) {
            Vec3 offset = target.subtract(minion.getEyePosition());
            wantedYaw = Mth.wrapDegrees((float) Math.toDegrees(Math.atan2(-offset.x, offset.z)) - minion.getYaw());
            wantedPitch = Mth.clamp((float) -Math.toDegrees(Math.atan2(offset.y, offset.horizontalDistance())), -MAX_HEAD_PITCH, MAX_HEAD_PITCH);
        }
        headYaw = approach(headYaw, Mth.clamp(wantedYaw, -MAX_HEAD_YAW, MAX_HEAD_YAW), YAW_SPEED);
        headPitch = approach(headPitch, wantedPitch, PITCH_SPEED);
        if (Math.abs(wantedYaw) > MAX_HEAD_YAW) {
            minion.setDesiredRotation(minion.getYaw() + Mth.clamp(wantedYaw - headYaw, -YAW_SPEED, YAW_SPEED), 0.0F, minion.getRoll());
        }
    }

    /**
     * 朝目标角度靠拢，单次最多转过 speed 度。
     */
    private static float approach(float current, float wanted, float speed) {
        return current + Mth.clamp(Mth.wrapDegrees(wanted - current), -speed, speed);
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public float getHeadYawO() {
        return headYawO;
    }

    /**
     * 渲染插值后的头部偏航。
     */
    public float getHeadYaw(float partialTick) {
        return Mth.rotLerp(partialTick, headYawO, headYaw);
    }

    public void setHeadYaw(float headYaw) {
        this.headYaw = headYaw;
    }

    public void setHeadYawO(float headYawO) {
        this.headYawO = headYawO;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public float getHeadPitchO() {
        return headPitchO;
    }

    /**
     * 渲染插值后的头部俯仰。
     */
    public float getHeadPitch(float partialTick) {
        return Mth.lerp(partialTick, headPitchO, headPitch);
    }

    public void setHeadPitch(float headPitch) {
        this.headPitch = headPitch;
    }

    public void setHeadPitchO(float headPitchO) {
        this.headPitchO = headPitchO;
    }
}
