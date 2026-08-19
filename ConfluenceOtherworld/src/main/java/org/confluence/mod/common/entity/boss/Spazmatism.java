package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.confluence.mod.common.entity.projectile.TwinEyeProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.HashSet;
import java.util.Set;

/// 双子魔眼中的魔焰眼。
///
/// <p>一阶段以短魔焰齐射衔接冲刺，半血变形后改为贴近目标的连续火流和更快冲刺。
/// 另一只魔眼死亡后，本体继续执行当前阶段的独立循环；当前 1.21 实现没有额外的单眼
/// 狂暴参数，因此这里也不虚构第三套战斗数值。</p>
public class Spazmatism extends AbstractTwinEye {
    private static final String STATE_TAG = "CombatState";
    private static final String STATE_TICKS_TAG = "StateTicks";
    private static final String SHOTS_TAG = "ShotsRemaining";
    private static final String DASHES_TAG = "DashesRemaining";
    private static final String DASH_X_TAG = "DashX";
    private static final String DASH_Y_TAG = "DashY";
    private static final String DASH_Z_TAG = "DashZ";

    private static final int APPROACH = 0;
    private static final int FLAME_VOLLEY = 1;
    private static final int DASH_WINDUP = 2;
    private static final int DASH = 3;

    private int combatState = APPROACH;
    private int stateTicks = 50;
    private int shotsRemaining;
    private int dashesRemaining;
    private Vec3 dashDirection = Vec3.ZERO;

    public Spazmatism(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 8970.0)
                .add(Attributes.ATTACK_DAMAGE, 22.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 96.0);
    }

    @Override
    protected boolean isRetinazer() {
        return false;
    }

    @Override
    protected boolean isDashCombatState() {
        return combatState == DASH;
    }

    @Override
    protected void tickTwinCombat(TheTwins master) {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        switch (combatState) {
            case APPROACH -> tickApproach(target);
            case FLAME_VOLLEY -> tickFlameVolley(target);
            case DASH_WINDUP -> tickDashWindup(target);
            case DASH -> tickDash();
            default -> beginApproach();
        }
    }

    private void tickApproach(LivingEntity target) {
        moveTowardTarget(target, movementSpeed(), followDistance());
        if (--stateTicks <= 0) {
            combatState = FLAME_VOLLEY;
            shotsRemaining = isTransformed() ? 33 : 5;
            stateTicks = flameInterval();
        }
    }

    private void tickFlameVolley(LivingEntity target) {
        moveTowardTarget(target, movementSpeed(), followDistance());
        if (--stateTicks > 0) {
            return;
        }
        fireFlame(target);
        if (--shotsRemaining <= 0) {
            dashesRemaining = 5;
            combatState = DASH_WINDUP;
            stateTicks = 10;
        } else {
            stateTicks = flameInterval();
        }
    }

    private void tickDashWindup(LivingEntity target) {
        setDeltaMovement(getDeltaMovement().scale(0.68));
        if (--stateTicks > 0) {
            return;
        }
        dashDirection = target.getEyePosition().subtract(getEyePosition()).normalize();
        combatState = DASH;
        stateTicks = 10;
        setDeltaMovement(dashDirection.scale(dashSpeed()));
    }

    private void tickDash() {
        setDeltaMovement(dashDirection.scale(dashSpeed()));
        if (--stateTicks > 0) {
            return;
        }
        if (--dashesRemaining > 0) {
            combatState = DASH_WINDUP;
            stateTicks = 10;
        } else {
            beginApproach();
        }
    }

    private void beginApproach() {
        combatState = APPROACH;
        stateTicks = 50;
        dashDirection = Vec3.ZERO;
    }

    private void moveTowardTarget(LivingEntity target, double speed, double distance) {
        if (distance <= 0.0) {
            Vec3 direction = target.position().subtract(position());
            setDeltaMovement(direction.lengthSqr() < 1.0E-7
                    ? Vec3.ZERO
                    : direction.normalize().scale(speed));
            return;
        }
        Vec3 away = position().subtract(target.position()).multiply(1.0, 0.0, 1.0);
        Vec3 desiredPosition = target.position()
                .add(away.normalize().scale(distance))
                .add(0.0, isTransformed() ? 0.0 : 1.0, 0.0);
        addDeltaMovement(desiredPosition.subtract(position()).scale(speed * 0.01));
        if (distanceToSqr(target) < 2.0) {
            setDeltaMovement(getDeltaMovement().scale(0.95));
        }
    }

    boolean fireFlame(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (isTransformed()) {
            return fireContinuousFlame(serverLevel);
        }
        TwinEyeProjectile flame = ModEntities.SPAZMATISM_FLAME.get().create(serverLevel);
        if (flame == null) {
            return false;
        }
        flame.configure(this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 1.5F, 10.0F);
        return serverLevel.addFreshEntity(flame);
    }

    /// 半血后的魔焰眼使用十格连续火流，而不是把火流替换成自动追踪弹丸。
    /// 每个刻度只对同一生物结算一次伤害，采样间距和碰撞半径与 1.21 保持一致。
    private boolean fireContinuousFlame(ServerLevel serverLevel) {
        Vec3 direction = getLookAngle();
        if (direction.lengthSqr() <= 1.0E-7) {
            return false;
        }
        direction = direction.normalize();
        Vec3 origin = getBoundingBox().getCenter().add(direction);
        Set<LivingEntity> hitEntities = new HashSet<>();
        for (int step = 0; step < 10; step++) {
            Vec3 point = origin.add(direction.scale(step));
            serverLevel.sendParticles(ParticleTypes.FLAME, point.x, point.y, point.z, 3, 0.3, 0.3, 0.3, 0.02);
            AABB hitBox = new AABB(point.add(-0.5, -0.5, -0.5), point.add(0.5, 0.5, 0.5));
            hitEntities.addAll(serverLevel.getEntitiesOfClass(LivingEntity.class, hitBox, entity -> entity != this));
        }
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity entity : hitEntities) {
            if (canAttack(entity)) {
                entity.hurt(damageSources().inFire(), damage);
                entity.setRemainingFireTicks(100);
            }
        }
        return true;
    }

    private int flameInterval() {
        return isTransformed() ? 3 : 20;
    }

    private double movementSpeed() {
        return isTransformed() ? 1.0 : 1.5;
    }

    private double dashSpeed() {
        return isTransformed() ? 2.0 : 1.0;
    }

    private double followDistance() {
        return isTransformed() ? 0.0 : 7.0;
    }

    @Override
    protected void onCombatProfileChanged() {
        beginApproach();
    }

    @Override
    protected void saveTwinCombat(CompoundTag tag) {
        tag.putInt(STATE_TAG, combatState);
        tag.putInt(STATE_TICKS_TAG, stateTicks);
        tag.putInt(SHOTS_TAG, shotsRemaining);
        tag.putInt(DASHES_TAG, dashesRemaining);
        tag.putDouble(DASH_X_TAG, dashDirection.x);
        tag.putDouble(DASH_Y_TAG, dashDirection.y);
        tag.putDouble(DASH_Z_TAG, dashDirection.z);
    }

    @Override
    protected void loadTwinCombat(CompoundTag tag) {
        combatState = tag.getInt(STATE_TAG);
        stateTicks = tag.getInt(STATE_TICKS_TAG);
        shotsRemaining = tag.getInt(SHOTS_TAG);
        dashesRemaining = tag.getInt(DASHES_TAG);
        dashDirection = new Vec3(tag.getDouble(DASH_X_TAG), tag.getDouble(DASH_Y_TAG), tag.getDouble(DASH_Z_TAG));
    }
}
