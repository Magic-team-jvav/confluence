package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.TwinEyeProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.joml.Matrix4x3f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.UUID;

// 魔焰眼：一阶段同高射击与旋转喷火长冲刺交替，二阶段持续喷焰与快速冲刺交替。
public class Spazmatism extends AbstractTwinEye {
    private static final EntityDataAccessor<Boolean> COMBO_CHARGING = SynchedEntityData.defineId(Spazmatism.class, EntityDataSerializers.BOOLEAN);

    /// 靠近阶段至少持续的时间，单位 tick。
    private static final int APPROACH_TICKS = 16;
    /// 一阶段靠近站位时的速度上限，单位格/tick。
    private static final double PHASE_ONE_MOVE_SPEED = 0.8;
    /// 二阶段靠近站位时的速度上限，单位格/tick。
    private static final double PHASE_TWO_MOVE_SPEED = 0.69;
    /// 靠近站位时每 tick 保留的旧速度比例。
    private static final double APPROACH_DRAG = 0.82;
    /// 站位位置误差转换为移动速度的系数。
    private static final double POSITION_ACCELERATION = 0.035;
    /// 重新选择喷火站位的间隔，单位 tick。
    private static final int POSITION_REFRESH_TICKS = 12;
    /// 目标偏离站位后重新选点的距离平方，单位格²。
    private static final double REPOSITION_RANGE_SQUARED = 20 * 20;
    /// 允许结束靠近并开始喷火的距离平方，单位格²。
    private static final double FIRE_START_RANGE_SQUARED = 14 * 14;
    /// 一阶段喷射站位与目标的水平距离，单位格；不是焰球射程。
    private static final double FIRE_DISTANCE = 11.0;
    /// 二阶段喷火站位与目标的水平距离，单位格；不是火焰射程。
    private static final double STREAM_DISTANCE = 8.0;
    /// 每次评估的喷火站位数量。
    private static final int FIRING_CANDIDATES = 8;
    /// 站位搜索相对当前方位的起始偏角，单位弧度。
    private static final double ORBIT_ANGLE_OFFSET = 0.2;
    /// 选取喷火站位的目标预测时间，单位 tick。
    private static final int POSITION_PREDICTION_TICKS = 4;
    /// 选取喷火站位的目标预测最大偏移，单位格。
    private static final double POSITION_MAX_LEAD = 2;
    /// 二阶段站位相对目标的基础高度上下限，单位格。
    private static final double POSITION_HEIGHT_LIMIT = 4;
    /// 二阶段站位高度的波动幅度，单位格。
    private static final double POSITION_HEIGHT_WAVE = 2;
    /// 能直视目标的喷火站位获得的评分。
    private static final double FIRING_LINE_SCORE = 40;
    /// 选择站位时避让搭档的距离，单位格。
    private static final double PARTNER_AVOID_DISTANCE = 7;
    /// 站位过于靠近搭档时的评分惩罚权重。
    private static final double PARTNER_AVOID_WEIGHT = 6;

    /// 一阶段持续发射诅咒焰球的时间，单位 tick。
    private static final int FIRE_TICKS = 100;
    /// 一阶段诅咒焰球发射间隔，单位 tick。
    private static final int FLAME_BALL_INTERVAL_TICKS = 18;
    /// 一阶段诅咒焰球初速，单位格/tick。
    private static final float FLAME_BALL_SPEED = 1.1F;
    /// 普通难度喷火拦截预测的最大偏移，单位格。
    private static final double FLAME_MAX_LEAD = 2;
    /// 传奇难度喷火拦截预测的最大偏移，单位格。
    private static final double LEGENDARY_FLAME_MAX_LEAD = 3;
    /// 二阶段一轮持续喷火的时间，单位 tick；不控制单枚火焰射程。
    private static final int STREAM_TICKS = 140;
    /// 二阶段持续喷火的发射间隔，单位 tick。
    private static final int STREAM_INTERVAL_TICKS = 2;
    /// 二阶段持续火焰初速，单位格/tick；与弹体寿命、阻力共同决定射程。
    private static final float STREAM_SPEED = 1.2F;
    /// 连续喷火音效的最短播放间隔，单位 tick。
    private static final int STREAM_SOUND_INTERVAL_TICKS = 14;

    /// 冲刺前的基础蓄力时间，单位 tick。
    private static final int WINDUP_TICKS = 10;
    /// 冲刺开始前停止更新预测方向的时间，单位 tick。
    private static final int DASH_LOCK_TICKS = 3;
    /// 朝向尚未对准时冲刺蓄力的最长时间，单位 tick。
    private static final int DASH_MAX_WINDUP_TICKS = 26;
    /// 允许发动冲刺的朝向点积阈值，越大要求越精确。
    private static final double DASH_AIM_DOT = 0.92;
    /// 每次冲刺的持续时间，单位 tick。
    private static final int DASH_TICKS = 24;
    /// 两次冲刺之间的恢复时间，单位 tick。
    private static final int RECOVER_TICKS = 6;
    /// 冲刺恢复期间每 tick 保留的移动速度比例。
    private static final double RECOVERY_DRAG = 0.8;
    /// 一阶段冲刺速度，单位格/tick。
    private static final double PHASE_ONE_DASH_SPEED = 1.9;
    /// 二阶段冲刺速度，单位格/tick。
    private static final double PHASE_TWO_DASH_SPEED = 2.7;
    /// 一阶段每轮连续冲刺次数。
    private static final int DASH_COUNT = 3;
    /// 二阶段每轮连续冲刺次数。
    private static final int PHASE_TWO_DASH_COUNT = 6;
    /// 普通难度冲刺预测的最大偏移，单位格。
    private static final double DASH_MAX_LEAD = 6;
    /// 传奇难度冲刺预测的最大偏移，单位格。
    private static final double LEGENDARY_DASH_MAX_LEAD = 8;
    /// 旋转冲刺时生成魔焰的间隔，单位 tick。
    private static final int DASH_FLAME_INTERVAL_TICKS = 2;
    /// 冲刺期间每 tick 的水平自转角度，完整冲刺旋转一周。
    private static final float DASH_SPIN_DEGREES = 360.0F / DASH_TICKS;
    /// 旋转冲刺时每 tick 回正俯仰的最大角度，单位度。
    private static final float DASH_SPIN_PITCH_STEP = 6.0F;

    /// 巨焰联动的蓄力时长，单位 tick（20 tick 为 1 秒）。
    private static final int COMBO_CHARGE_TICKS = 40;
    /// 巨焰发射后等待搭档引爆的最长时间，单位 tick。
    private static final int COMBO_WAIT_TICKS = 100;
    /// 两次巨焰联动之间的冷却，单位 tick。
    private static final int COMBO_COOLDOWN_TICKS = 320;
    /// 等待搭档完成当前攻击的最长时间，单位 tick。
    private static final int MAX_COORDINATION_WAIT_TICKS = 80;
    /// 巨焰落点预测提前时间，单位 tick。
    private static final int COMBO_PREDICTION_TICKS = 12;
    /// 巨焰落点预测最大偏移距离，单位格。
    private static final double COMBO_MAX_LEAD = 4;
    /// 巨焰预定引爆点高于目标的距离，单位格。
    private static final double COMBO_HEIGHT = 6;
    /// 蓄力期间每 tick 保留的移动速度比例。
    private static final double CHARGE_DRAG = 0.7;
    /// 等待巨焰引爆期间每 tick 保留的移动速度比例。
    private static final double COMBO_WAIT_DRAG = 0.85;

    /// 一阶段喷口距模型中心的距离，单位格。
    private static final double PHASE_ONE_MUZZLE_DISTANCE = 23.0 / 16.0;
    /// 二阶段喷口距模型中心的距离，单位格。
    private static final double PHASE_TWO_MUZZLE_DISTANCE = 39.0 / 16.0;
    /// 魔焰眼变形动画时长，单位 tick。
    private static final int TRANSFORM_TICKS = 49;

    private ParticleEmitter comboChargeEmitter;
    private UUID comboFlame;
    private Vec3 comboAim = Vec3.ZERO;
    private int comboCooldown;
    private State state = State.APPROACH;
    private int stateTicks;
    private int shotTimer;
    private int dashes;
    private Vec3 dashDirection = Vec3.ZERO;
    private Vec3 previousDashPosition;
    private int orbitDirection = 1;
    private Vec3 firingPosition;
    private long nextStreamSoundTick;

    public Spazmatism(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(COMBO_CHARGING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) return;
        if (!entityData.get(COMBO_CHARGING) || isRemoved()) {
            if (comboChargeEmitter != null) {
                comboChargeEmitter.remove();
                comboChargeEmitter = null;
            }
        } else if (comboChargeEmitter == null || comboChargeEmitter.isRemoved()) {
            // 直接复用现有火焰，附着实体后以局部变换将发射器移到发射口。
            comboChargeEmitter = new ParticleEmitter(level(), position(), Confluence.asResource("twins_flame_charge"));
            comboChargeEmitter.attachEntity(this);
            comboChargeEmitter.hideOutline = true;
            comboChargeEmitter.setLocalSpace(new Matrix4x3f());
            MolangParticleEngine.INSTANCE._addEmitter(comboChargeEmitter);
        }
        if (comboChargeEmitter != null) {
            Vec3 offset = getMuzzlePosition(1).subtract(position());
            Matrix4x3f transform = comboChargeEmitter.getLocalSpace();
            transform.translation((float) offset.x, (float) offset.y, (float) offset.z);
            comboChargeEmitter.setLocalSpace(transform);
        }
    }

    @Override
    protected double getMuzzleDistance() {return isTransformed() ? PHASE_TWO_MUZZLE_DISTANCE : PHASE_ONE_MUZZLE_DISTANCE;}

    @Override
    protected boolean isRetinazer() {return false;}

    @Override
    protected boolean isDashCombatState() {return state == State.DASH;}

    @Override
    protected int transformationDuration() {return TRANSFORM_TICKS;}

    boolean isPreparingOrDashing() {
        return state == State.WINDUP || state == State.DASH || state == State.RECOVER && dashes > 0;
    }

    @Override
    protected Vec3 getCombatLookDirection(LivingEntity target) {
        if (state == State.COMBO_CHARGE || state == State.COMBO_WAIT)
            return comboAim.subtract(getEyePosition());
        return state == State.DASH || state == State.WINDUP ? dashDirection : super.getCombatLookDirection(target);
    }

    // 旋转与实际冲刺位移绑定，尚未起步或没有前进时不旋转、不喷火。
    @Override
    protected void turnCombatDirection(LivingEntity target) {
        if (state != State.DASH || isTransformed()) {
            super.turnCombatDirection(target);
            return;
        }
        Vec3 previous = previousDashPosition;
        previousDashPosition = position();
        if (previous == null) return;
        double distance = position().subtract(previous).dot(dashDirection);
        // 异常位移不算冲刺进度，正常位移按速度折算旋转幅度。
        if (distance <= 1.0E-4 || distance > dashSpeed() * 2) return;
        float step = (float) Math.min(distance / dashSpeed(), 1.0);
        float pitch = Mth.approachDegrees(getXRot(), 0.0F, DASH_SPIN_PITCH_STEP * step);
        faceCombatDirection(Vec3.directionFromRotation(pitch, getYRot() + orbitDirection * DASH_SPIN_DEGREES * step), 360, 360);
        // 完成本 tick 转向后才取发射口，避免火焰从旧朝向或眼球中心冒出。
        if (stateTicks % DASH_FLAME_INTERVAL_TICKS == 0) fireDashFlame();
    }

    @Override
    protected void tickTwinCombat() {
        LivingEntity target = getTarget();
        if (target == null) return;
        stateTicks++;
        if (comboCooldown > 0) comboCooldown--;
        switch (state) {
            case APPROACH -> {
                approach(target);
                if (distanceToSqr(target) < FIRE_START_RANGE_SQUARED && stateTicks >= APPROACH_TICKS) {
                    shotTimer = 1;
                    enter(State.FIRE);
                }
            }
            case FIRE -> {
                approach(target);
                if (--shotTimer <= 0 && hasFiringLine(getMuzzlePosition(1.0F), target)) {
                    fireFlame(target);
                    shotTimer = isTransformed() ? STREAM_INTERVAL_TICKS : FLAME_BALL_INTERVAL_TICKS;
                }
                int fireDuration = isTransformed() ? STREAM_TICKS : FIRE_TICKS;
                AbstractTwinEye partner = getPartner();
                boolean partnerBeam = sharesFirstPhaseTarget(partner) && partner instanceof Retinazer retinazer && retinazer.isBeamAttack();
                if (stateTicks >= fireDuration && (!partnerBeam || stateTicks >= fireDuration + MAX_COORDINATION_WAIT_TICKS)) {
                    // 魔焰眼二阶段才能发起，激光眼无需变形；无法配合时继续原本的冲刺循环。
                    if (isTransformed() && comboCooldown == 0 && partner instanceof Retinazer laser && laser.beginFlameCombo(this)) {
                        comboAim = predictTargetPosition(target, COMBO_PREDICTION_TICKS, COMBO_MAX_LEAD).add(0, COMBO_HEIGHT, 0);
                        comboCooldown = COMBO_COOLDOWN_TICKS;
                        enter(State.COMBO_CHARGE);
                        playSound(ModSoundEvents.GUN_FLAMETHROWER.get(), 0.8F, 0.6F);
                    } else beginDashChain(target);
                }
            }
            case COMBO_CHARGE -> {
                setDeltaMovement(getDeltaMovement().scale(CHARGE_DRAG));
                if (!(getPartner() instanceof Retinazer laser) || !laser.isFlameComboActive()) {
                    finishFlameCombo(target);
                } else if (stateTicks >= COMBO_CHARGE_TICKS) {
                    if (level() instanceof ServerLevel server) {
                        TwinEyeProjectile giant = ModEntities.SPAZMATISM_FLAME.get().create(server);
                        if (giant != null) {
                            giant.configureGiantFlame(this, comboAim, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), legendaryCombat());
                            server.addFreshEntity(giant);
                            comboFlame = giant.getUUID();
                        }
                    }
                    enter(State.COMBO_WAIT);
                }
            }
            case COMBO_WAIT -> {
                setDeltaMovement(getDeltaMovement().scale(COMBO_WAIT_DRAG));
                if (getComboFlame() == null || stateTicks >= COMBO_WAIT_TICKS
                        || !(getPartner() instanceof Retinazer laser) || !laser.isFlameComboActive())
                    finishFlameCombo(target);
            }
            case WINDUP -> {
                setDeltaMovement(getDeltaMovement().scale(CHARGE_DRAG));
                if (stateTicks <= WINDUP_TICKS - DASH_LOCK_TICKS) {
                    dashDirection = predictDashDirection(target);
                }
                if (stateTicks >= WINDUP_TICKS && (getLookAngle().dot(dashDirection) > DASH_AIM_DOT || stateTicks >= DASH_MAX_WINDUP_TICKS)) {
                    enter(State.DASH);
                    setDeltaMovement(dashDirection.scale(dashSpeed()));
                    playSound(ModSoundEvents.ROAR.get(), 1.0F, isTransformed() ? 1.2F : 1.0F);
                }
            }
            case DASH -> {
                // 整段冲刺锁定方向，不在穿过玩家后掉头。
                setDeltaMovement(dashDirection.scale(dashSpeed()));
                if (stateTicks >= DASH_TICKS) {
                    dashes--;
                    enter(State.RECOVER);
                }
            }
            case RECOVER -> {
                setDeltaMovement(getDeltaMovement().scale(RECOVERY_DRAG));
                if (stateTicks >= RECOVER_TICKS) {
                    dashDirection = predictDashDirection(target);
                    if (dashes <= 0) orbitDirection = -orbitDirection;
                    enter(dashes > 0 ? State.WINDUP : State.APPROACH);
                }
            }
        }
    }

    // 联动只暂时占用攻击状态，结束或中断后回到正常冲刺链。
    private void beginDashChain(LivingEntity target) {
        dashes = isTransformed() ? PHASE_TWO_DASH_COUNT : DASH_COUNT;
        dashDirection = predictDashDirection(target);
        enter(State.WINDUP);
    }

    boolean isChargingComboFlame() {return state == State.COMBO_CHARGE;}

    TwinEyeProjectile getComboFlame() {
        if (comboFlame == null || !(level() instanceof ServerLevel server)) return null;
        return server.getEntity(comboFlame) instanceof TwinEyeProjectile flame && flame.isAlive() && flame.isGiantFlame() ? flame : null;
    }

    private void clearComboFlame() {
        TwinEyeProjectile flame = getComboFlame();
        if (flame != null) flame.discard();
        comboFlame = null;
    }

    private void finishFlameCombo(LivingEntity target) {
        clearComboFlame();
        beginDashChain(target);
    }

    private double dashSpeed() {return isTransformed() ? PHASE_TWO_DASH_SPEED : PHASE_ONE_DASH_SPEED;}

    private Vec3 predictDashDirection(LivingEntity target) {
        double delay = state == State.WINDUP ? Math.max(0, WINDUP_TICKS - stateTicks) : WINDUP_TICKS;
        Vec3 direction = predictInterceptPosition(target, getEyePosition(), dashSpeed(), delay,
                legendaryCombat() ? LEGENDARY_DASH_MAX_LEAD : DASH_MAX_LEAD).subtract(getEyePosition());
        return direction.lengthSqr() < 1.0E-7 ? getLookAngle() : direction.normalize();
    }

    private void approach(LivingEntity target) {
        if (firingPosition == null || stateTicks % POSITION_REFRESH_TICKS == 0
                || firingPosition.distanceToSqr(target.position()) > REPOSITION_RANGE_SQUARED) {
            chooseFiringPosition(target);
        }
        Vec3 destination = firingPosition;
        if (!isTransformed()) destination = new Vec3(destination.x, target.getY(), destination.z);
        Vec3 velocity = getDeltaMovement().scale(APPROACH_DRAG).add(destination.subtract(position()).scale(POSITION_ACCELERATION));
        double speed = isTransformed() ? PHASE_TWO_MOVE_SPEED : PHASE_ONE_MOVE_SPEED;
        setDeltaMovement(separateFromPartner(velocity, speed));
    }

    private void chooseFiringPosition(LivingEntity target) {
        Vec3 away = position().subtract(target.position());
        Vec3 center = predictTargetPosition(target, POSITION_PREDICTION_TICKS, POSITION_MAX_LEAD).subtract(0, target.getEyeHeight(), 0);
        double baseAngle = Math.atan2(away.z, away.x);
        double radius = isTransformed() ? STREAM_DISTANCE : FIRE_DISTANCE;
        double bestScore = -Double.MAX_VALUE;
        Vec3 best = null;
        AbstractTwinEye partner = getPartner();
        for (int i = 0; i < FIRING_CANDIDATES; i++) {
            double angle = baseAngle + orbitDirection * (ORBIT_ANGLE_OFFSET + i * Math.PI * 2 / FIRING_CANDIDATES);
            double height = isTransformed() ? Mth.clamp(away.y, -POSITION_HEIGHT_LIMIT, POSITION_HEIGHT_LIMIT) + Math.sin(angle) * POSITION_HEIGHT_WAVE : 0;
            Vec3 candidate = center.add(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
            if (!level().noCollision(this, getBoundingBox().move(candidate.subtract(position()))))
                continue;
            double score = hasFiringLine(candidate.add(0, getBbHeight() * 0.5, 0), target) ? FIRING_LINE_SCORE : 0;
            score -= position().distanceTo(candidate);
            if (partner != null && partner.isAlive())
                score -= Math.max(0, PARTNER_AVOID_DISTANCE - partner.position().distanceTo(candidate)) * PARTNER_AVOID_WEIGHT;
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        firingPosition = best != null ? best : center.add(Math.cos(baseAngle) * radius, isTransformed() ? POSITION_HEIGHT_LIMIT : 0, Math.sin(baseAngle) * radius);
    }

    // 冲刺魔焰独立使用抛物线，普通诅咒焰球和二阶段直喷不受影响。
    private void fireDashFlame() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        TwinEyeProjectile flame = ModEntities.SPAZMATISM_FLAME.get().create(serverLevel);
        if (flame == null) return;
        flame.configureArcingFlame(this, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), legendaryCombat());
        serverLevel.addFreshEntity(flame);
        playStreamSound();
    }

    // 连续喷火共用音效节流，不随发射频率重复叠加声音。
    private void playStreamSound() {
        if (level().getGameTime() >= nextStreamSoundTick) {
            playSound(ModSoundEvents.GUN_FLAMETHROWER.get(), 0.6F, 1.0F);
            nextStreamSoundTick = level().getGameTime() + STREAM_SOUND_INTERVAL_TICKS;
        }
    }

    private void fireFlame(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        TwinEyeProjectile flame = ModEntities.SPAZMATISM_FLAME.get().create(serverLevel);
        if (flame == null) return;
        Vec3 aim = predictInterceptPosition(target, getMuzzlePosition(1.0F),
                isTransformed() ? STREAM_SPEED : FLAME_BALL_SPEED, 0, legendaryCombat() ? LEGENDARY_FLAME_MAX_LEAD : FLAME_MAX_LEAD);
        flame.configureFlame(this, aim, (float) getAttributeValue(Attributes.ATTACK_DAMAGE),
                isTransformed() ? STREAM_SPEED : FLAME_BALL_SPEED, isTransformed(), legendaryCombat());
        serverLevel.addFreshEntity(flame);
        if (!isTransformed()) {
            playSound(ModSoundEvents.ITEM_FLAME_CAST.get(), 0.6F, 1.0F);
        } else {
            playStreamSound();
        }
    }

    private void enter(State next) {
        previousDashPosition = next == State.DASH ? position() : null;
        state = next;
        stateTicks = 0;
        entityData.set(COMBO_CHARGING, next == State.COMBO_CHARGE);
    }

    @Override
    protected void resetCombatCycle() {
        enter(State.APPROACH);
        shotTimer = 0;
        dashes = 0;
        dashDirection = Vec3.ZERO;
        firingPosition = null;
        nextStreamSoundTick = 0;
        clearComboFlame();
        comboAim = Vec3.ZERO;
        comboCooldown = 0;
    }

    @Override
    protected void saveTwinCombat(CompoundTag tag) {
        tag.putInt("AttackVersion", 2);
        tag.putString("Attack", state.name());
        tag.putInt("AttackTicks", stateTicks);
        tag.putInt("ShotTimer", shotTimer);
        tag.putInt("Dashes", dashes);
        saveVector(tag, "Dash", dashDirection);
        tag.putInt("OrbitDirection", orbitDirection);
        tag.putInt("ComboCooldown", comboCooldown);
        if (comboFlame != null) tag.putUUID("ComboFlame", comboFlame);
        saveVector(tag, "ComboAim", comboAim);
    }

    @Override
    protected void loadTwinCombat(CompoundTag tag) {
        if (tag.getInt("AttackVersion") != 2) return;
        try {state = State.valueOf(tag.getString("Attack"));} catch (
                IllegalArgumentException ignored) {return;}
        stateTicks = Mth.clamp(tag.getInt("AttackTicks"), 0, STREAM_TICKS + MAX_COORDINATION_WAIT_TICKS);
        shotTimer = Mth.clamp(tag.getInt("ShotTimer"), 0, FLAME_BALL_INTERVAL_TICKS);
        dashes = Mth.clamp(tag.getInt("Dashes"), 0, PHASE_TWO_DASH_COUNT);
        dashDirection = loadVector(tag, "Dash").normalize();
        orbitDirection = tag.getInt("OrbitDirection") < 0 ? -1 : 1;
        comboCooldown = Mth.clamp(tag.getInt("ComboCooldown"), 0, COMBO_COOLDOWN_TICKS);
        comboFlame = tag.hasUUID("ComboFlame") ? tag.getUUID("ComboFlame") : null;
        comboAim = loadVector(tag, "ComboAim");
        entityData.set(COMBO_CHARGING, state == State.COMBO_CHARGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {return ModSoundEvents.METAL_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {return ModSoundEvents.METAL_DEATH.get();}

    private enum State {APPROACH, FIRE, WINDUP, DASH, RECOVER, COMBO_CHARGE, COMBO_WAIT}
}
