package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.IMomentumAttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.PlannedPath;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Random;

/**
 * 基于动量物理的仆从抽象基类。
 */
public abstract class MomentumMinion extends Minion implements IMomentumAttachmentEntity {

    private float desiredYaw = 0;
    private float desiredPitch = 0;
    private float desiredRoll = 0;
    private Vec3 velocity = Vec3.ZERO;
    private boolean physics = true;
    private float drag = 0.92F * new Random().nextFloat(0.99f, 1.01f);
    private float gravity = -0.08F;

    public MomentumMinion(RegistryObject<? extends AttachmentEntityType<?>> type) {
        super(type);
    }

    @Override
    public void tick() {
        super.tick();
        tickDirection();
        tickPhysics();
    }

    private void tickDirection() {
        PathNode pathNode = getCurrentPathNode();
        float yaw = pathNode.yaw();
        float pitch = pathNode.pitch();
        float roll = pathNode.roll();
        float rotationSpeed = 36;
        yaw = yaw + Mth.clamp(Mth.wrapDegrees(desiredYaw - yaw), -rotationSpeed, rotationSpeed);
        pitch = pitch + Mth.clamp(Mth.wrapDegrees(desiredPitch - pitch), -rotationSpeed, rotationSpeed);
        roll = roll + Mth.clamp(Mth.wrapDegrees(desiredRoll - roll), -rotationSpeed, rotationSpeed);
        setCurrentPathNode(pathNode.modifyEuler(yaw, pitch, roll));
    }

    public void tickPhysics() {
        if (physics) {
            velocity = velocity.add(0, gravity, 0).scale(drag);
            Vec3 newPos = getPos().add(velocity);
            setPath(new PlannedPath("physics", Collections.singletonList(new PathNode(newPos, getYaw(), getPitch(), getRoll()))));
        }
    }

    public boolean isPhysics() {
        return physics;
    }

    public void setPhysics(boolean physics) {
        this.physics = physics;
    }

    @Override
    public @NotNull Vec3 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(@NotNull Vec3 velocity) {
        this.velocity = velocity;
    }

    @Override
    public void addVelocity(@NotNull Vec3 velocity) {
        this.velocity = this.velocity.add(velocity);
    }

    @Override
    public float getDrag() {
        return drag;
    }

    @Override
    public void setDrag(float drag) {
        this.drag = Mth.clamp(drag, 0, 1);
    }

    @Override
    public float getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    /**
     * 看向指定位置
     */
    public void lookAtPos(Vec3 targetPos) {
        lookAtDirection(targetPos.subtract(getPos()).normalize());
    }

    /** 看向指定方向 */
    public void lookAtDirection(Vec3 direction) {
        float targetYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float targetPitch = (float) Math.toDegrees(Math.asin(-direction.y));
        setDesiredRotation(targetYaw, targetPitch, getRoll());
    }

    /** 设置期望朝向 */
    public void setDesiredRotation(float yaw, float pitch, float roll) {
        this.desiredYaw = yaw;
        this.desiredPitch = pitch;
        this.desiredRoll = roll;
    }

    @Deprecated
    public Vec3 getWanderPos(Vec3 lastWanderPos, Vec3 targetPos, float distance, float height) {
        if (lastWanderPos.equals(Vec3.ZERO) || getRandom().nextDouble() < 0.025 || lastWanderPos.distanceToSqr(targetPos) > distance * distance) {
            Vec3 newPos = targetPos.add(targetPos.offsetRandom(getRandom(), 1).subtract(targetPos).normalize().scale(distance));
            while (newPos.y() < targetPos.y() + height) {
                newPos = newPos.add(0, 1, 0);
            }
            return newPos;
        }
        return lastWanderPos;
    }
}
