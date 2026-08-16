package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.boss.LunaticCultist;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileExtension;

/// 拜月教邪教徒“远古之光”齐射使用的敌对弹幕。
///
/// <p>实体沿用跨版本弹幕桥接约定，使 1.20.1 与后续同步侧共享一致的伤害和命中语义。</p>
public final class AncientLightProjectile extends Projectile implements IPortProjectileExtension {
    public static final int MAX_LIFETIME = 100;
    public static final float DAMAGE = 16.0F;
    private static final int HOMING_TICKS = 30;
    private static final double MAX_SPEED = 0.95;
    private static final EntityDataAccessor<Integer> TARGET_ID =
            SynchedEntityData.defineId(AncientLightProjectile.class, EntityDataSerializers.INT);

    public AncientLightProjectile(EntityType<? extends AncientLightProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public void configure(LunaticCultist owner, LivingEntity target, double spreadAngle) {
        setOwner(owner);
        entityData.set(TARGET_ID, target.getId());
        setPos(owner.getX(), owner.getEyeY() - 0.2, owner.getZ());

        Vec3 aim = target.getEyePosition().subtract(position()).normalize();
        double cosine = Math.cos(spreadAngle);
        double sine = Math.sin(spreadAngle);
        Vec3 spread = new Vec3(
                aim.x * cosine - aim.z * sine,
                aim.y,
                aim.x * sine + aim.z * cosine).normalize();
        setDeltaMovement(spread.scale(0.62));
    }

    public int getTargetId() {
        return entityData.get(TARGET_ID);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(TARGET_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > MAX_LIFETIME) {
            discard();
            return;
        }

        if (level().isClientSide) {
            level().addParticle(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            if ((tickCount & 1) == 0) {
                level().addParticle(ParticleTypes.WITCH, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            }
        }

        homeTowardTarget();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS
                && !PortProjectileImpactEvent.onProjectileImpact(this, hitResult)) {
            hitTargetOrDeflectSelf(hitResult);
        }
        if (isRemoved()) return;

        checkInsideBlocks();
        Vec3 velocity = getDeltaMovement();
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
        updateRotation();
    }

    private void homeTowardTarget() {
        if (tickCount > HOMING_TICKS) return;
        Entity target = level().getEntity(getTargetId());
        if (!(target instanceof LivingEntity living) || !living.isAlive()) return;

        Vec3 velocity = getDeltaMovement();
        Vec3 desired = living.getEyePosition().subtract(position()).normalize();
        double speed = Math.min(MAX_SPEED, velocity.length() + 0.012);
        Vec3 steered = velocity.normalize().scale(0.88).add(desired.scale(0.12)).normalize();
        setDeltaMovement(steered.scale(speed));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof LivingEntity target
                && getOwner() instanceof LunaticCultist cultist
                && cultist.canAttack(target)) {
            target.hurt(damageSources().mobProjectile(this, cultist), DAMAGE);
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        discard();
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return target instanceof LivingEntity living
                && getOwner() instanceof LunaticCultist cultist
                && cultist.canAttack(living)
                && super.canHitEntity(target);
    }
}
