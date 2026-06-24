package org.confluence.mod.common.entity.ai.motion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class DragonMovement {
    protected final Mob mob;

    public Vec3 targetPos;
    public boolean shouldMove;
    public float turnSpeed;
    public float yRotA;
    /**
     * 由于某些生物体型较大，需要向下调整目标的偏移量
     */
    public float offsetY = 0;

    public DragonMovement(Mob mob, float turnSpeed, boolean shouldMove) {
        this.mob = mob;
        this.turnSpeed = turnSpeed;
        this.shouldMove = shouldMove;
    }

    public void tickAI() {
        if (this.mob.level().isClientSide) {
            return;
        }
        if (targetPos != null) {
            //todo debug
//                DebugBlocksHelper.Singleton().addDebugBlock(new BlockPos((int) targetPos.x,  (int) targetPos.y, (int) targetPos.z));
            if (shouldMove) {
                this.move(targetPos);
            }
        }
    }

    public void move(Vec3 targetPos) {
        double deltaX = targetPos.x - this.getX();
        double deltaY = targetPos.y - this.getY();
        double deltaZ = targetPos.z - this.getZ();
        double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        float flySpeed = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED); // fly speed
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (horizontalDistance > 0.0) {
            deltaY = Mth.clamp(deltaY / horizontalDistance, -flySpeed, flySpeed);
        }

        // 应用垂直速度
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, deltaY * 0.05, 0.0));
        this.setYRot(Mth.wrapDegrees(this.getYRot()));

        // 计算目标方向向量
        Vec3 directionToTarget = targetPos.subtract(this.getX(), this.getY(), this.getZ()).normalize();

        // 计算当前朝向向量
        Vec3 forwardDirection = new Vec3(
                Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                this.getDeltaMovement().y,
                Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
        ).normalize();

        // 计算朝向匹配度（0-1之间）
        float alignmentFactor = Math.max(((float) forwardDirection.dot(directionToTarget) + 0.5F) / 1.5F, 0.0F);

        // 水平方向转向控制
        if (Math.abs(deltaX) > 1.0E-5F || Math.abs(deltaZ) > 1.0E-5F) {
            float targetYaw = -(float) Mth.atan2(deltaX, deltaZ) * (180.0F / (float) Math.PI);

            float yawError = Mth.clamp(Mth.wrapDegrees(targetYaw - this.getYRot()), -50.0F, 50.0F);
            this.yRotA *= 0.8F;
            this.yRotA = this.yRotA + yawError * this.getTurnSpeed();
            this.setYRot(this.getYRot() + this.yRotA * 0.1F);
            this.applyYawMovement(this.yRotA);
        }

        // 动态移动速度系数（距离目标越近，移动越慢）
        float proximityFactor = (float) (2.0 / (distanceSquared + 1.0));
        float baseSpeed = 0.06F;
//        float movementSpeed = baseSpeed * (alignmentFactor * proximityFactor + (1.0F - proximityFactor));
        float movementSpeed = baseSpeed * (alignmentFactor + 1.5F);
        this.moveRelative(movementSpeed, new Vec3(0.0, 0.0, 1.0));

        if (this.isInWall()) {
            this.move(MoverType.SELF, this.getDeltaMovement().scale(0.8F));
        } else {
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        Vec3 velocityDirection = this.getDeltaMovement().normalize();
        double directionMatch = 0.8 + 0.15 * (velocityDirection.dot(forwardDirection) + 1.0) / 2.0;
        this.setDeltaMovement(this.getDeltaMovement().multiply(directionMatch * flySpeed, 0.91F, directionMatch * flySpeed));

    }

    protected void applyYawMovement(float yawRotA) {

    }

    public float getTurnSpeed() {
        float f = (float) this.getDeltaMovement().horizontalDistance() + 1.0F;
        float f1 = Math.min(f, 40.0F);
        return this.turnSpeed / f1 / f;
    }

    protected void move(MoverType moverType, Vec3 deltaMovement) {
        this.mob.move(moverType, deltaMovement);
    }

    protected void setDeltaMovement(Vec3 multiply) {
        this.mob.setDeltaMovement(multiply);
    }

    protected boolean isInWall() {
        return this.mob.isInWall();
    }

    protected void moveRelative(float movementSpeed, Vec3 vec3) {
        this.mob.moveRelative(movementSpeed, vec3);
    }

    protected Vec3 getDeltaMovement() {
        return this.mob.getDeltaMovement();
    }

    protected double getZ() {
        return this.mob.getZ();
    }

    protected double getY() {
        return this.mob.getY() + offsetY;
    }

    protected double getX() {
        return this.mob.getX();
    }

    protected float getYRot() {
        return this.mob.getYRot();
    }

    protected void setYRot(float yRot) {
        this.mob.setYRot(yRot);
    }

}
