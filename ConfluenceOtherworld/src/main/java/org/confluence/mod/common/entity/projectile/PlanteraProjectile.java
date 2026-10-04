package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/// 世纪之花三类基础弹幕的共享实现。
///
/// 种子持续修正航向；孢子先上浮到目标上方，再保持水平追踪并缓慢下落；刺球受重力影响，
/// 可在方块表面持续弹跳。三种注册类型共享安全的实体射线碰撞与阵营过滤。
public final class PlanteraProjectile extends StraightMonsterProjectile {
    // 每 tick 混入目标方向的追踪权重；孢子比种子转向更快。
    private static final double SEED_TRACKING_POWER = 0.05D;
    private static final double SPORE_TRACKING_POWER = 0.15D;
    // 单次修正最多允许转 90°，防止高速越过目标后瞬间反折。
    private static final double MAXIMUM_TRACKING_ANGLE = Math.PI * 0.5D;
    // 孢子上浮速度为方块/tick，到达目标上方 5 方块后切换为下落追踪。
    private static final double SPORE_VERTICAL_SPEED = 0.1D;
    private static final double SPORE_APEX_HEIGHT = 5.0D;

    private final Variant variant;
    private boolean sporeMovingUpward = true;

    public PlanteraProjectile(EntityType<? extends PlanteraProjectile> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    /// 在弹幕加入世界前保存初速度、伤害与寿命。
    public void configure(Mob owner, LivingEntity target, float damage, float velocity, float inaccuracy) {
        if (variant == Variant.SPORE) {
            Vec3 origin = new Vec3(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
            Vec3 horizontalAim = target.position().subtract(origin).multiply(1.0D, 0.0D, 1.0D);
            Vec3 initialVelocity = horizontalAim.lengthSqr() < 1.0E-8D
                    ? Vec3.ZERO
                    : horizontalAim.normalize().scale(velocity);
            super.configure(owner, origin, initialVelocity, damage, 300);
            return;
        }
        super.configure(owner, target, damage, velocity, inaccuracy,
                variant == Variant.THORN_BALL ? 200 : 100);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (level().isClientSide) return velocity;
        LivingEntity target = getOwner() instanceof Mob owner ? owner.getTarget() : null;
        return switch (variant) {
            case SEED -> target == null || !target.isAlive()
                    ? velocity
                    : trackToward(velocity, target.position().add(0.0D, target.getEyeHeight() * 0.5D, 0.0D)
                    .subtract(position()), SEED_TRACKING_POWER);
            case SPORE -> updateSporeVelocity(velocity, target);
            case THORN_BALL -> velocity.add(0.0D, -0.05D, 0.0D);
        };
    }

    private Vec3 updateSporeVelocity(Vec3 velocity, LivingEntity target) {
        if (target == null || !target.isAlive()) sporeMovingUpward = false;
        if (target != null && getY() >= target.getY() + SPORE_APEX_HEIGHT)
            sporeMovingUpward = false;

        Vec3 movement = new Vec3(velocity.x, sporeMovingUpward ? SPORE_VERTICAL_SPEED : -SPORE_VERTICAL_SPEED, velocity.z);
        if (target == null || !target.isAlive()) return movement;

        Vec3 desired = target.position().add(0.0D, target.getEyeHeight() * 0.5D, 0.0D).subtract(position());
        desired = new Vec3(desired.x, movement.y, desired.z);
        return trackToward(movement, desired, SPORE_TRACKING_POWER);
    }

    private static Vec3 trackToward(Vec3 current, Vec3 desired, double power) {
        double speed = current.length();
        if (speed < 1.0E-7D || desired.lengthSqr() < 1.0E-7D) return current;
        Vec3 currentDirection = current.scale(1.0D / speed);
        Vec3 desiredDirection = desired.normalize();
        double angle = Math.acos(Mth.clamp(currentDirection.dot(desiredDirection), -1.0D, 1.0D));
        if (angle >= MAXIMUM_TRACKING_ANGLE) return current;
        Vec3 blended = currentDirection.scale(1.0D - power).add(desiredDirection.scale(power));
        return blended.lengthSqr() < 1.0E-7D ? current : blended.normalize().scale(speed);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (variant != Variant.THORN_BALL) return;

        Direction direction = result.getDirection();
        Vec3 velocity = getDeltaMovement();
        double x = direction.getAxis() == Direction.Axis.X ? -velocity.x : velocity.x;
        double y = direction.getAxis() == Direction.Axis.Y ? -velocity.y : velocity.y;
        double z = direction.getAxis() == Direction.Axis.Z ? -velocity.z : velocity.z;
        setDeltaMovement(new Vec3(x, y, z).scale(0.99D));
        setPos(result.getLocation().add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.08D)));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) return;
        for (int index = 0; index < 3; index++) {
            level().addParticle(variant.particle, getRandomX(0.3D), getRandomY(), getRandomZ(0.3D), 0.0D, 0.0D, 0.0D);
        }
    }

    public enum Variant {
        SEED(new DustParticleOptions(new Vector3f(0.25F, 0.85F, 0.12F), 1.0F)),
        THORN_BALL(new DustParticleOptions(new Vector3f(0.1F, 0.5F, 0.08F), 1.35F)),
        SPORE(ParticleTypes.SPORE_BLOSSOM_AIR);

        private final ParticleOptions particle;

        Variant(ParticleOptions particle) {
            this.particle = particle;
        }
    }
}
