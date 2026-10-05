package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.confluence.mod.common.data.map.CreatureDefinition.ProjectileOverrides;
import org.confluence.mod.common.entity.boss.SkeletronPrime;

public final class PrimeCannonballProjectile extends Projectile {
    public static final int MAX_LIFETIME = 80;
    private static final double BLAST_RADIUS = 3.0;
    private float damage;
    private int lifetime = MAX_LIFETIME;

    public PrimeCannonballProjectile(EntityType<? extends PrimeCannonballProjectile> type, Level level) {
        super(type, level);
    }

    public void configure(SkeletronPrime owner, Vec3 origin, LivingEntity target) {
        setOwner(owner);
        setPos(origin);
        var parameters = ProjectileOverrides.get(owner, getType());
        damage = parameters.damageOr(0);
        lifetime = parameters.lifetimeOr(MAX_LIFETIME);
        Vec3 aim = target.getEyePosition().subtract(origin);
        double horizontal = Math.sqrt(aim.x * aim.x + aim.z * aim.z);
        shoot(aim.x, aim.y + horizontal * 0.08, aim.z, parameters.speedOr(0.72F), parameters.inaccuracyOr(0.03F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        if (tickCount > lifetime) {
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
                && !NeoForge.EVENT_BUS.post(new ProjectileImpactEvent(this, hitResult)).isCanceled()) {
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
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, blast, prime::canAttack)) {
                double distance = Math.sqrt(distanceToSqr(target));
                float scale = (float) Math.max(0.4, 1.0 - distance / (BLAST_RADIUS * 1.6));
                target.hurt(damageSources().mobProjectile(this, prime), damage * scale);
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
