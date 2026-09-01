package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.confluence.mod.common.entity.projectile.TwinEyeProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 双子魔眼中的激光眼。
///
/// 第一阶段依次执行悬停、五连射和五次冲刺。半血变形完成后不再冲刺，而是依次从目标
/// 上方、平行方向和快速射击位置完成三组激光攻击。状态、剩余时间、弹幕数和冲刺方向均写入
/// 当前存档格式，重新加载不会重置正在执行的战斗片段。
public class Retinazer extends AbstractTwinEye {
    private static final String STATE_TAG = "CombatState";
    private static final String STATE_TICKS_TAG = "StateTicks";
    private static final String SHOTS_TAG = "ShotsRemaining";
    private static final String DASHES_TAG = "DashesRemaining";
    private static final String DASH_X_TAG = "DashX";
    private static final String DASH_Y_TAG = "DashY";
    private static final String DASH_Z_TAG = "DashZ";

    private static final int APPROACH = 0;
    private static final int PHASE_ONE_VOLLEY = 1;
    private static final int DASH_WINDUP = 2;
    private static final int DASH = 3;
    private static final int PHASE_TWO_TOP_VOLLEY = 4;
    private static final int PHASE_TWO_PARALLEL_VOLLEY = 5;
    private static final int PHASE_TWO_RAPID_VOLLEY = 6;

    private int combatState = APPROACH;
    private int stateTicks = 50;
    private int shotsRemaining;
    private int dashesRemaining;
    private Vec3 dashDirection = Vec3.ZERO;

    public Retinazer(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 7800.0)
                .add(Attributes.ATTACK_DAMAGE, 19.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 96.0);
    }

    @Override
    protected boolean isRetinazer() {
        return true;
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
            case PHASE_ONE_VOLLEY -> tickPhaseOneVolley(target);
            case DASH_WINDUP -> tickDashWindup(target);
            case DASH -> tickDash();
            case PHASE_TWO_TOP_VOLLEY -> tickTopVolley(target);
            case PHASE_TWO_PARALLEL_VOLLEY -> tickParallelVolley(target);
            case PHASE_TWO_RAPID_VOLLEY -> tickRapidVolley(target);
            default -> beginApproach();
        }
    }

    private void tickApproach(LivingEntity target) {
        moveToward(target, 7.0, isTransformed() ? 0.0 : 5.0, 1.0);
        if (--stateTicks > 0) {
            return;
        }
        if (isTransformed()) {
            combatState = PHASE_TWO_TOP_VOLLEY;
            shotsRemaining = 6;
            stateTicks = 20;
        } else {
            combatState = PHASE_ONE_VOLLEY;
            shotsRemaining = 5;
            stateTicks = 20;
        }
    }

    private void tickPhaseOneVolley(LivingEntity target) {
        moveToward(target, 7.0, 5.0, 1.0);
        if (--stateTicks > 0) {
            return;
        }
        // 第一阶段只允许在二十格内发射，过远时仍会消耗本次射击节拍。
        if (distanceToSqr(target) < 400.0) {
            fireLaser(target);
        }
        if (--shotsRemaining > 0) {
            stateTicks = 20;
            return;
        }
        dashesRemaining = 5;
        combatState = DASH_WINDUP;
        stateTicks = 10;
    }

    private void tickTopVolley(LivingEntity target) {
        moveToward(target, 0.0, 5.0, 1.0);
        if (--stateTicks > 0) {
            return;
        }
        fireLaser(target);
        if (--shotsRemaining > 0) {
            stateTicks = 20;
            return;
        }
        combatState = PHASE_TWO_PARALLEL_VOLLEY;
        shotsRemaining = 3;
        stateTicks = 10;
    }

    private void tickParallelVolley(LivingEntity target) {
        moveToward(target, 7.0, 0.0, 1.0);
        if (--stateTicks > 0) {
            return;
        }
        fireLaser(target);
        if (--shotsRemaining > 0) {
            stateTicks = 10;
            return;
        }
        combatState = PHASE_TWO_RAPID_VOLLEY;
        shotsRemaining = 12;
        stateTicks = 5;
    }

    private void tickRapidVolley(LivingEntity target) {
        moveToward(target, 7.0, 0.0, 1.0);
        if (--stateTicks > 0) {
            return;
        }
        fireLaser(target);
        if (--shotsRemaining > 0) {
            stateTicks = 5;
        } else {
            beginApproach();
        }
    }

    private void tickDashWindup(LivingEntity target) {
        setDeltaMovement(getDeltaMovement().scale(0.72));
        if (--stateTicks > 0) {
            return;
        }
        Vec3 direction = target.getEyePosition().subtract(getEyePosition());
        dashDirection = direction.lengthSqr() < 1.0E-7
                ? Vec3.ZERO : direction.normalize();
        combatState = DASH;
        stateTicks = 5;
        setDeltaMovement(dashDirection.scale(1.5));
    }

    private void tickDash() {
        setDeltaMovement(dashDirection.scale(1.5));
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

    private void moveToward(LivingEntity target, double horizontalDistance, double height, double speed) {
        Vec3 desiredPosition;
        if (horizontalDistance <= 0.0) {
            desiredPosition = target.position().add(0.0, height, 0.0);
        } else {
            Vec3 away = position().subtract(target.position()).multiply(1.0, 0.0, 1.0);
            desiredPosition = target.position().add(away.normalize().scale(horizontalDistance)).add(0.0, height, 0.0);
        }
        Vec3 correction = desiredPosition.subtract(position());
        Vec3 velocity = getDeltaMovement().scale(0.86D).add(correction.scale(speed * 0.014D));
        if (velocity.lengthSqr() > speed * speed) velocity = velocity.normalize().scale(speed);
        setDeltaMovement(velocity);
        if (distanceToSqr(target) < 2.0) {
            setDeltaMovement(getDeltaMovement().scale(0.95));
        }
    }

    boolean fireLaser(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        TwinEyeProjectile laser = ModEntities.RETINAZER_LASER.get().create(serverLevel);
        if (laser == null) {
            return false;
        }
        laser.configure(this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 1.5F, isTransformed() ? 10.0F : 30.0F);
        return serverLevel.addFreshEntity(laser);
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
