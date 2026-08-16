package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.confluence.mod.common.entity.boss.SkeletronPrime;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileExtension;

/// 机械炮管发射的小范围爆炸炮弹。
///
/// <p>爆炸只影响实体，不破坏地形，避免 Boss 攻击永久损坏玩家建筑与战斗场地。</p>
public final class PrimeCannonballProjectile extends Projectile implements IPortProjectileExtension {
    public static final int MAX_LIFETIME = 80;
    private static final float BLAST_DAMAGE = 22.0F;
    private static final double BLAST_RADIUS = 3.0;

    public PrimeCannonballProjectile(EntityType<? extends PrimeCannonballProjectile> type, Level level) {
        super(type, level);
    }

    public void configure(SkeletronPrime owner, Vec3 origin, LivingEntity target) {
        setOwner(owner);
        setPos(origin);
        Vec3 aim = target.getEyePosition().subtract(origin);
        double horizontal = Math.sqrt(aim.x * aim.x + aim.z * aim.z);
        shoot(aim.x, aim.y + horizontal * 0.08, aim.z, 0.72F, 0.03F);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        if (tickCount > MAX_LIFETIME) {
            explodeWithoutTerrainDamage();
            return;
        }

        if (level().isClientSide) {
            level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            if ((tickCount & 1) == 0) {
                level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            }
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS
                && !PortProjectileImpactEvent.onProjectileImpact(this, hitResult)) {
            hitTargetOrDeflectSelf(hitResult);
        }
        if (isRemoved()) return;

        checkInsideBlocks();
        Vec3 velocity = getDeltaMovement().add(0.0, -0.018, 0.0);
        setDeltaMovement(velocity);
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
        updateRotation();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        explodeWithoutTerrainDamage();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        explodeWithoutTerrainDamage();
    }

    private void explodeWithoutTerrainDamage() {
        if (!level().isClientSide && getOwner() instanceof SkeletronPrime prime) {
            AABB blast = getBoundingBox().inflate(BLAST_RADIUS);
            for (LivingEntity target : level().getEntitiesOfClass(
                    LivingEntity.class, blast, prime::canAttack)) {
                double distance = Math.sqrt(distanceToSqr(target));
                float scale = (float) Math.max(0.4, 1.0 - distance / (BLAST_RADIUS * 1.6));
                target.hurt(damageSources().mobProjectile(this, prime), BLAST_DAMAGE * scale);
            }
        }
        discard();
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target instanceof LivingEntity living
                && getOwner() instanceof SkeletronPrime prime
                && prime.canAttack(living)
                && super.canHitEntity(target);
    }
}
