package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/// 世纪之花三类基础弹幕的共享实现。
///
/// <p>种子和孢子保持发射瞬间的直线方向；刺球受轻微重力影响，并可在方块表面
/// 有限次数反弹。三种注册类型共享碰撞与伤害流程，但不会通过运行时字段混淆表现。</p>
public final class PlanteraProjectile extends StraightMonsterProjectile {
    private final Variant variant;
    private int remainingBounces = 4;

    public PlanteraProjectile(EntityType<? extends PlanteraProjectile> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    /// 按当前目标位置快照一条弹道。
    public void configure(Mob owner, LivingEntity target, float damage, float velocity, float inaccuracy) {
        if (variant == Variant.SPORE) {
            Vec3 origin = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
            Vec3 horizontalAim = target.position().subtract(origin).multiply(1.0, 0.0, 1.0);
            Vec3 initialVelocity = horizontalAim.lengthSqr() < 1.0E-8
                    ? Vec3.ZERO
                    : horizontalAim.normalize().scale(velocity);
            super.configure(owner, origin, initialVelocity, damage, 100);
            return;
        }
        super.configure(
                owner,
                target,
                damage,
                velocity,
                inaccuracy,
                variant == Variant.THORN_BALL ? 160 : 100);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return variant == Variant.THORN_BALL
                ? velocity.add(0.0, -0.03, 0.0)
                : velocity;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (variant != Variant.THORN_BALL || remainingBounces-- <= 0) {
            super.onHitBlock(result);
            return;
        }

        Direction direction = result.getDirection();
        Vec3 velocity = getDeltaMovement();
        double x = direction.getAxis() == Direction.Axis.X
                ? -velocity.x : velocity.x;
        double y = direction.getAxis() == Direction.Axis.Y
                ? -velocity.y : velocity.y;
        double z = direction.getAxis() == Direction.Axis.Z
                ? -velocity.z : velocity.z;
        setDeltaMovement(new Vec3(x, y, z).scale(0.78));
        setPos(result.getLocation().add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.08)));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            level().addParticle(variant.particle, getRandomX(0.3), getRandomY(), getRandomZ(0.3), 0.0, 0.0, 0.0);
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
