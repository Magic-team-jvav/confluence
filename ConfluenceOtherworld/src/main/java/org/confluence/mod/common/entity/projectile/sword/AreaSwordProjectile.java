package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 短时区域剑气基类，用于魔光剑这类“在玩家前方出现一段判定”的攻击。
 *
 * <p>它不走原版弹幕的直线碰撞，而是在每 tick 根据所有者朝向刷新一个有厚度的局部判定盒。
 * 这样可以保留泰拉瑞亚的瞬时挥砍/暗影判定效果，同时避免把它伪装成一把会乱飞的剑。</p>
 */
public abstract class AreaSwordProjectile extends SwordProjectile {
    private final double reach;
    private final double halfWidth;
    private final double halfHeight;
    private final double halfDepth;
    private final double verticalOffset;

    protected AreaSwordProjectile(
            EntityType<? extends SwordProjectile> entityType,
            Level level,
            double reach,
            double halfWidth,
            double halfHeight,
            double halfDepth,
            double verticalOffset
    ) {
        super(entityType, level);
        this.reach = requirePositive(reach, "Area sword reach");
        this.halfWidth = requirePositive(halfWidth, "Area sword half width");
        this.halfHeight = requirePositive(halfHeight, "Area sword half height");
        this.halfDepth = requirePositive(halfDepth, "Area sword half depth");
        this.verticalOffset = requireFinite(verticalOffset, "Area sword vertical offset");
        canPenalize = true;
    }

    @Override
    public void tick() {
        if (!level().isClientSide && combatState().discardIfInvalid(this)) {
            return;
        }
        if (waitForLoadedOwner()) {
            return;
        }
        if (!level().isClientSide && (hitCount == 0 || tickCount >= lifetime)) {
            discard();
            return;
        }

        baseTick();
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity livingOwner) || !owner.isAlive()) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }

        AreaPose pose = updatePose(livingOwner);
        setDeltaMovement(Vec3.ZERO);
        if (level().isClientSide) {
            clientTickVisuals(pose.center(), pose.forward(), pose.right(), pose.up());
        } else {
            hitEntities(pose);
            if (tickCount >= lifetime) {
                discard();
            }
        }
    }

    /**
     * 区域剑气的客户端表现钩子。
     *
     * <p>基础类只定义判定，不强制具体显示方式。子类可以在这里生成粒子、同步模型姿态，
     * 或者什么都不做，避免把“伤害盒”和“渲染资源”绑死在同一层。</p>
     */
    protected void clientTickVisuals(Vec3 center, Vec3 forward, Vec3 right, Vec3 up) {}

    /**
     * 给统一发射流程使用的出生点。
     *
     * <p>区域剑气实际每 tick 都会贴回所有者视线前方；提前把入世位置放到同一个中心点，
     * 可以避免实体刚同步到客户端时先出现在手边、下一帧再跳到判定盒中心。</p>
     */
    public Vec3 initialCenter(LivingEntity owner, Vec3 launchDirection) {
        Vec3 forward = launchDirection.lengthSqr() <= 1.0E-8
                ? owner.getLookAngle()
                : launchDirection;
        if (forward.lengthSqr() <= 1.0E-8) {
            forward = Vec3.directionFromRotation(owner.getXRot(), owner.getYRot());
        }
        return owner.getEyePosition()
                .add(0.0, verticalOffset, 0.0)
                .add(forward.normalize().scale(reach));
    }

    private AreaPose updatePose(LivingEntity owner) {
        Vec3 forward = owner.getLookAngle();
        if (forward.lengthSqr() <= 1.0E-8) {
            forward = Vec3.directionFromRotation(owner.getXRot(), owner.getYRot());
        }
        forward = forward.normalize();
        Vec3 right = rightVector(forward);
        Vec3 up = upVector(forward, right);
        Vec3 center = owner.getEyePosition()
                .add(0.0, verticalOffset, 0.0)
                .add(forward.scale(reach));
        setPos(center.x, center.y, center.z);
        direction = forward;
        entityData.set(DATA_DIRECTION, forward.toVector3f());
        setYRot(owner.getYRot());
        setXRot(owner.getXRot());
        yRotO = getYRot();
        xRotO = getXRot();
        return new AreaPose(center, forward, right, up);
    }

    private void hitEntities(AreaPose pose) {
        double radius = reach + Math.max(Math.max(halfWidth, halfHeight), halfDepth) + 1.0;
        AABB searchBox = new AABB(pose.center(), pose.center()).inflate(radius);
        for (Entity target : level().getEntities(this, searchBox, this::canHitEntity)) {
            if (isInsideArea(pose, target.getBoundingBox())) {
                doHurt(target);
            }
        }
    }

    private boolean isInsideArea(AreaPose pose, AABB box) {
        Vec3 targetCenter = box.getCenter().subtract(pose.center());
        double extentX = box.getXsize() * 0.5;
        double extentY = box.getYsize() * 0.5;
        double extentZ = box.getZsize() * 0.5;
        return overlapsAxis(targetCenter, pose.forward(), halfDepth, extentX, extentY, extentZ)
                && overlapsAxis(targetCenter, pose.right(), halfWidth, extentX, extentY, extentZ)
                && overlapsAxis(targetCenter, pose.up(), halfHeight, extentX, extentY, extentZ);
    }

    /**
     * 用目标 AABB 在局部轴上的投影半径做相交判断。
     *
     * <p>只检查角点时，目标从判定盒边缘掠过可能没有任何角点落入盒内，表现为距离合适却偶尔打不中。
     * 投影判断仍然很轻量，但对斜向和贴边判定都更稳定。</p>
     */
    private static boolean overlapsAxis(
            Vec3 centerOffset,
            Vec3 axis,
            double areaHalfExtent,
            double targetExtentX,
            double targetExtentY,
            double targetExtentZ
    ) {
        double targetProjection =
                targetExtentX * Math.abs(axis.x)
                        + targetExtentY * Math.abs(axis.y)
                        + targetExtentZ * Math.abs(axis.z);
        return Math.abs(centerOffset.dot(axis)) <= areaHalfExtent + targetProjection;
    }

    private static Vec3 rightVector(Vec3 forward) {
        Vec3 right = new Vec3(0.0, 1.0, 0.0).cross(forward);
        if (right.lengthSqr() <= 1.0E-8) {
            return new Vec3(1.0, 0.0, 0.0);
        }
        return right.normalize();
    }

    private static Vec3 upVector(Vec3 forward, Vec3 right) {
        Vec3 up = forward.cross(right);
        if (up.lengthSqr() <= 1.0E-8) {
            return new Vec3(0.0, 1.0, 0.0);
        }
        return up.normalize();
    }

    private static double requirePositive(double value, String fieldName) {
        if (requireFinite(value, fieldName) <= 0.0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
        return value;
    }

    private static double requireFinite(double value, String fieldName) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(fieldName + " must be finite");
        }
        return value;
    }

    private record AreaPose(Vec3 center, Vec3 forward, Vec3 right, Vec3 up) {}
}
