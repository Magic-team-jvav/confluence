package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 风眼的旋风。
///
/// 存在 30 秒，持续把范围内的敌人吸向中心，并对中心附近的敌人每半秒造成 1 点伤害。
/// 只能用粒子风暴的螺旋粒子表现外观；施法者打入中心的第一枚弹幕会被吸收，
/// 后续弹幕不再叠加伤害。
public class WindEyeTornadoEntity extends AbstractManaProjectile {
    /// 存在时间：30 秒。
    private static final int DURATION = 30 * 20;
    /// 吸附半径。
    private static final double PULL_RADIUS = 7.0;
    /// 伤害半径。
    private static final double DAMAGE_RADIUS = 3.5;
    /// 吸附强度。
    private static final double PULL_STRENGTH = 0.16;
    /// 伤害结算间隔（tick）。
    private static final int DAMAGE_INTERVAL = 10;
    /// 吸收其它弹幕的中心半径。
    private static final double ABSORB_RADIUS = 2.0;

    private float absorbedDamage;
    private boolean absorbedProjectile;
    private int lastDamageTick = -DAMAGE_INTERVAL;

    public WindEyeTornadoEntity(EntityType<WindEyeTornadoEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        noPhysics = true;
        withParticle(Confluence.asResource("wind_eye_tornado"));
    }

    public WindEyeTornadoEntity(LivingEntity living) {
        this(ModEntities.WIND_EYE_TORNADO.get(), living.level());
    }

    /// 旋风自身不做常规碰撞，只在原地做范围结算。
    @Override
    protected void doHitCheck() {}

    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide) return;
        if (tickCount > DURATION) {
            discard();
            return;
        }
        absorbProjectiles();
        AABB pullArea = new AABB(blockPosition()).inflate(PULL_RADIUS);
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, pullArea)) {
            if (!isValidVictim(victim)) continue;
            /// 目标是旋风中心与敌人身体中心，而不是脚底；同时不再持续叠加向上速度，
            /// 让敌人停在中心而不是被推到旋涡上方。
            Vec3 victimCenter = victim.position().add(0.0, victim.getBbHeight() * 0.5, 0.0);
            Vec3 direction = position().subtract(victimCenter);
            if (direction.lengthSqr() < 1.0E-4) continue;
            Vec3 pull = direction.normalize().scale(PULL_STRENGTH);
            Vec3 motion = victim.getDeltaMovement();
            victim.setDeltaMovement(motion.x + pull.x, motion.y + pull.y, motion.z + pull.z);
            victim.hurtMarked = true;
        }
        if (tickCount - lastDamageTick >= DAMAGE_INTERVAL) {
            this.lastDamageTick = tickCount;
            float damage = 1.0F + absorbedDamage;
            for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(DAMAGE_RADIUS))) {
                if (!isValidVictim(victim)) continue;
                victim.invulnerableTime = 0;
                victim.hurt(getDamageSource(), damage);
            }
        }
    }

    private boolean isValidVictim(LivingEntity victim) {
        return victim != getOwner() && victim.isAlive() && !victim.isSpectator() && LibEntityUtils.canHit(getOwner(), victim);
    }

    /// 吸收施法者打进中心的第一枚弹幕，只记录这一次的伤害；后续弹幕不再叠加。
    private void absorbProjectiles() {
        if (absorbedProjectile) return;
        for (Entity entity : level().getEntities(this, new AABB(blockPosition()).inflate(ABSORB_RADIUS))) {
            if (!(entity instanceof DamageSettableProjectile projectile) || projectile == this) continue;
            if (projectile.getOwner() != getOwner()) continue;
            this.absorbedDamage = Math.max(0.0F, projectile.getCalculatedDamage());
            this.absorbedProjectile = true;
            projectile.discard();
            return;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("AbsorbedDamage", absorbedDamage);
        compound.putBoolean("AbsorbedProjectile", absorbedProjectile);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.absorbedDamage = compound.getFloat("AbsorbedDamage");
        this.absorbedProjectile = compound.getBoolean("AbsorbedProjectile");
    }
}
