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
    private static final int COMBO_CHARGE_TICKS = 40;
    private static final int COMBO_WAIT_TICKS = 100;
    private static final int COMBO_COOLDOWN_TICKS = 320;
    private static final int APPROACH_TICKS = 16;
    private static final int FIRE_TICKS = 100;
    private static final int WINDUP_TICKS = 10;
    private static final int DASH_TICKS = 24;
    private static final int RECOVER_TICKS = 6;
    private static final int DASH_COUNT = 3;
    private static final int PHASE_TWO_DASH_COUNT = 6;
    private static final int STREAM_TICKS = 140;
    private static final int MAX_COORDINATION_WAIT_TICKS = 80;
    private static final int POSITION_REFRESH_TICKS = 12;
    private static final int STREAM_SOUND_INTERVAL_TICKS = 14;
    private static final double PHASE_ONE_DASH_SPEED = 1.9;
    private static final double PHASE_TWO_DASH_SPEED = 2.7;
    private static final double PHASE_ONE_MOVE_SPEED = 0.8;
    private static final double PHASE_TWO_MOVE_SPEED = 0.69;
    private static final double FIRE_DISTANCE = 11.0;
    private static final double STREAM_DISTANCE = 8.0;
    private static final float FLAME_BALL_SPEED = 1.1F;
    private static final float STREAM_SPEED = 0.9F;
    private static final int DASH_FLAME_INTERVAL_TICKS = 2;
    private static final float DASH_SPIN_DEGREES = 360.0F / DASH_TICKS;
    private static final float DASH_SPIN_PITCH_STEP = 6.0F;

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
            MolangParticleEngine.INSTANCE.addEmitter(comboChargeEmitter);
        }
        if (comboChargeEmitter != null) {
            Vec3 offset = getMuzzlePosition(1).subtract(position());
            Matrix4x3f transform = comboChargeEmitter.getLocalSpace();
            transform.translation((float) offset.x, (float) offset.y, (float) offset.z);
            comboChargeEmitter.setLocalSpace(transform);
        }
    }

    @Override
    protected double getMuzzleDistance() {return isTransformed() ? 39.0 / 16.0 : 23.0 / 16.0;}

    @Override
    protected boolean isRetinazer() {return false;}

    @Override
    protected boolean isDashCombatState() {return state == State.DASH;}

    @Override
    protected int transformationDuration() {return 49;}

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
                if (distanceToSqr(target) < 14 * 14 && stateTicks >= APPROACH_TICKS) {
                    shotTimer = 1;
                    enter(State.FIRE);
                }
            }
            case FIRE -> {
                approach(target);
                if (--shotTimer <= 0 && hasFiringLine(getMuzzlePosition(1.0F), target)) {
                    fireFlame(target);
                    shotTimer = isTransformed() ? 2 : 18;
                }
                int fireDuration = isTransformed() ? STREAM_TICKS : FIRE_TICKS;
                AbstractTwinEye partner = getPartner();
                boolean partnerBeam = sharesFirstPhaseTarget(partner) && partner instanceof Retinazer retinazer && retinazer.isBeamAttack();
                if (stateTicks >= fireDuration && (!partnerBeam || stateTicks >= fireDuration + MAX_COORDINATION_WAIT_TICKS)) {
                    // 魔焰眼二阶段才能发起，激光眼无需变形；无法配合时继续原本的冲刺循环。
                    if (isTransformed() && comboCooldown == 0 && partner instanceof Retinazer laser && laser.beginFlameCombo(this)) {
                        comboAim = predictTargetPosition(target, 12, 4).add(0, 6, 0);
                        comboCooldown = COMBO_COOLDOWN_TICKS;
                        enter(State.COMBO_CHARGE);
                        playSound(ModSoundEvents.GUN_FLAMETHROWER.get(), 0.8F, 0.6F);
                    } else beginDashChain(target);
                }
            }
            case COMBO_CHARGE -> {
                setDeltaMovement(getDeltaMovement().scale(0.7));
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
                setDeltaMovement(getDeltaMovement().scale(0.85));
                if (getComboFlame() == null || stateTicks >= COMBO_WAIT_TICKS
                        || !(getPartner() instanceof Retinazer laser) || !laser.isFlameComboActive())
                    finishFlameCombo(target);
            }
            case WINDUP -> {
                setDeltaMovement(getDeltaMovement().scale(0.7));
                if (stateTicks <= WINDUP_TICKS - 3) {
                    dashDirection = predictDashDirection(target);
                }
                if (stateTicks >= WINDUP_TICKS && (getLookAngle().dot(dashDirection) > 0.92 || stateTicks >= 26)) {
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
                setDeltaMovement(getDeltaMovement().scale(0.8));
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
                legendaryCombat() ? 8 : 6).subtract(getEyePosition());
        return direction.lengthSqr() < 1.0E-7 ? getLookAngle() : direction.normalize();
    }

    private void approach(LivingEntity target) {
        if (firingPosition == null || stateTicks % POSITION_REFRESH_TICKS == 0
                || firingPosition.distanceToSqr(target.position()) > 20 * 20) {
            chooseFiringPosition(target);
        }
        Vec3 destination = firingPosition;
        if (!isTransformed()) destination = new Vec3(destination.x, target.getY(), destination.z);
        Vec3 velocity = getDeltaMovement().scale(0.82).add(destination.subtract(position()).scale(0.035));
        double speed = isTransformed() ? PHASE_TWO_MOVE_SPEED : PHASE_ONE_MOVE_SPEED;
        setDeltaMovement(separateFromPartner(velocity, speed));
    }

    private void chooseFiringPosition(LivingEntity target) {
        Vec3 away = position().subtract(target.position());
        Vec3 center = predictTargetPosition(target, 4, 2).subtract(0, target.getEyeHeight(), 0);
        double baseAngle = Math.atan2(away.z, away.x);
        double radius = isTransformed() ? STREAM_DISTANCE : FIRE_DISTANCE;
        double bestScore = -Double.MAX_VALUE;
        Vec3 best = null;
        AbstractTwinEye partner = getPartner();
        for (int i = 0; i < 8; i++) {
            double angle = baseAngle + orbitDirection * (0.2 + i * Math.PI / 4);
            double height = isTransformed() ? Mth.clamp(away.y, -4, 4) + Math.sin(angle) * 2 : 0;
            Vec3 candidate = center.add(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
            if (!level().noCollision(this, getBoundingBox().move(candidate.subtract(position()))))
                continue;
            double score = hasFiringLine(candidate.add(0, getBbHeight() * 0.5, 0), target) ? 40 : 0;
            score -= position().distanceTo(candidate);
            if (partner != null && partner.isAlive())
                score -= Math.max(0, 7 - partner.position().distanceTo(candidate)) * 6;
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        firingPosition = best != null ? best : center.add(Math.cos(baseAngle) * radius, isTransformed() ? 4 : 0, Math.sin(baseAngle) * radius);
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
                isTransformed() ? STREAM_SPEED : FLAME_BALL_SPEED, 0, legendaryCombat() ? 3 : 2);
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
        shotTimer = Mth.clamp(tag.getInt("ShotTimer"), 0, 18);
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
