package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.TwinEyeProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.joml.Vector3f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

/// 激光眼：定点射击、五连发、慢速追踪光束和近身反制。
public class Retinazer extends AbstractTwinEye {
    private static final EntityDataAccessor<Vector3f> BEAM_DIRECTION = SynchedEntityData.defineId(Retinazer.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> BEAM_LENGTH = SynchedEntityData.defineId(Retinazer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> BEAM_CHARGING = SynchedEntityData.defineId(Retinazer.class, EntityDataSerializers.BOOLEAN);
    private static final int RELOCATE_TICKS = 40;
    private static final int HOVER_TICKS = 64;
    private static final int VOLLEY_SIZE = 5;
    private static final int VOLLEYS_PER_CYCLE = 3;
    private static final int PHASE_ONE_SHOT_INTERVAL = 18;
    private static final int PHASE_TWO_SHOT_INTERVAL = 4;
    private static final int LEGENDARY_PHASE_ONE_SHOT_INTERVAL = 10;
    private static final int LEGENDARY_PHASE_TWO_SHOT_INTERVAL = 2;
    private static final int VOLLEY_RECOVERY_TICKS = 24;
    private static final int TRACK_CHARGE_TICKS = 24;
    private static final int TRACK_TICKS = 100;
    private static final int SWEEP_CHARGE_TICKS = 30;
    private static final int SWEEP_TICKS = 40;
    private static final int BLAST_CHARGE_TICKS = 36;
    private static final int BLAST_TICKS = 6;
    private static final double CLOSE_RANGE_SQUARED = 64.0;
    private static final double CLOSE_EXIT_RANGE_SQUARED = 121.0;
    private static final int CLOSE_ATTACK_COOLDOWN = 80;
    private static final int BLOCKED_RELOCATE_TICKS = 16;
    private static final int HOVER_CANDIDATES = 10;
    private static final int AIM_SETTLE_TICKS = 8;
    private static final int COMBO_AIM_TIMEOUT_TICKS = 100;
    private static final int COMBO_FIRE_TICKS = 10;
    private static final int COMBO_MIN_FLIGHT_TICKS = 16;
    private static final int FOLLOWUP_WINDOW_TICKS = 30;
    private static final double FOLLOWUP_RANGE_SQUARED = 24 * 24;
    private static final double CROSS_FIRE_SCORE = 24.0;
    private static final byte MUZZLE_WARNING_EVENT = 66;
    private static final byte MUZZLE_FLASH_EVENT = 67;
    private static final double BEAM_RANGE = 96.0;
    private static final double BEAM_RADIUS = 0.4;
    private static final double BLAST_RADIUS = 3.5;
    private static final double HOVER_SPEED = 1.25;
    private static final float LASER_SPEED = 3.2F;
    private static final float TRACK_TURN_DEGREES = 1.2F;
    private static final DustParticleOptions CHARGE_PARTICLE = new DustParticleOptions(new Vector3f(0.65F, 0.2F, 1.0F), 1.3F);

    private State state = State.RELOCATE;
    private int stateTicks;
    private int shotTimer;
    private int shots;
    private int volleys;
    private int sweeps;
    private Vec3 hoverPosition;
    private Vec3 lockedDirection = new Vec3(0, 0, 1);
    private Vec3 beamDirection = new Vec3(0, 0, 1);
    private float sweepAngle;
    private float blastLength;
    private int closeAttackCooldown;
    private int blockedTicks;
    private boolean closeTarget;
    private boolean partnerDashCycle;
    private int followupTicks;

    public Retinazer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BEAM_DIRECTION, new Vector3f(0, 0, 1));
        entityData.define(BEAM_LENGTH, 0.0F);
        entityData.define(BEAM_CHARGING, false);
    }

    @Override
    protected boolean isRetinazer() {return true;}

    @Override
    protected boolean isDashCombatState() {return false;}

    @Override
    protected int transformationDuration() {return 31;}

    public Vec3 getBeamDirection() {return new Vec3(entityData.get(BEAM_DIRECTION));}

    public float getBeamLength() {return entityData.get(BEAM_LENGTH);}

    public boolean isBeamCharging() {return entityData.get(BEAM_CHARGING);}

    public Vec3 getBeamOrigin() {return getMuzzlePosition(1.0F);}

    @Override
    protected double getMuzzleDistance() {return isTransformed() ? 28.0 / 16.0 : 23.0 / 16.0;}


    @Override
    protected Vec3 getCombatLookDirection(LivingEntity target) {
        if (state == State.COMBO_AIM && getBeamLength() <= 0 && getPartner() instanceof Spazmatism flame)
            return flame.getMuzzlePosition(1.0F).subtract(getEyePosition());
        return getBeamLength() > 0 ? getBeamDirection()
                : predictInterceptPosition(target, getBeamOrigin(), LASER_SPEED, 0, legendaryCombat() ? 4 : 3).subtract(getEyePosition());
    }

    @Override
    protected void tickTwinCombat() {
        LivingEntity target = getTarget();
        if (target == null) return;
        updatePartnerCoordination(target);
        clearBeam();
        if (closeAttackCooldown > 0) closeAttackCooldown--;
        double targetDistance = distanceToSqr(target);
        if (targetDistance < CLOSE_RANGE_SQUARED) closeTarget = true;
        else if (targetDistance > CLOSE_EXIT_RANGE_SQUARED) closeTarget = false;
        stateTicks++;
        // 冲刺链结束后的短窗口接光束；保留蓄力、冷却与遮挡检查，不在冲刺中叠加横扫。
        if (followupTicks > 0 && !isBeamAttack() && closeAttackCooldown == 0
                && targetDistance < FOLLOWUP_RANGE_SQUARED && hasFiringLine(getBeamOrigin(), target)) {
            followupTicks = 0;
            beginCloseAttack(target);
        }
        switch (state) {
            case RELOCATE -> {
                if (hoverPosition == null) chooseHoverPosition(target);
                if (hoverPosition.distanceToSqr(target.position()) > 32 * 32
                        || stateTicks % 10 == 0 && !level().noCollision(this, getBoundingBox().move(hoverPosition.subtract(position())))) {
                    chooseHoverPosition(target);
                }
                steerTo(hoverPosition, HOVER_SPEED);
                if (position().distanceToSqr(hoverPosition) < 2.25
                        || stateTicks >= RELOCATE_TICKS && hasFiringLine(getBeamOrigin(), target)) {
                    volleys = 0;
                    shots = 0;
                    shotTimer = AIM_SETTLE_TICKS;
                    enter(State.SHOOT);
                } else if (stateTicks >= RELOCATE_TICKS * 2) {
                    relocate();
                }
            }
            case SHOOT -> {
                if (distanceToSqr(target) > 32 * 32) {
                    relocate();
                    break;
                }
                steerTo(hoverPosition, HOVER_SPEED);
                if (shotTimer == 3) muzzleFlash(true);
                blockedTicks = hasFiringLine(getBeamOrigin(), target) ? 0 : blockedTicks + 1;
                if (isClose(target)) {
                    beginCloseAttack(target);
                } else if (blockedTicks >= BLOCKED_RELOCATE_TICKS) {
                    relocate();
                } else if (--shotTimer <= 0) {
                    if (blockedTicks > 0) {
                        shotTimer = 2;
                        break;
                    }
                    fireLaser(target);
                    shots++;
                    shotTimer = isTransformed()
                            ? legendaryCombat() ? LEGENDARY_PHASE_TWO_SHOT_INTERVAL : PHASE_TWO_SHOT_INTERVAL
                            : legendaryCombat() ? LEGENDARY_PHASE_ONE_SHOT_INTERVAL : PHASE_ONE_SHOT_INTERVAL;
                    if (isTransformed() && shots >= VOLLEY_SIZE) {
                        volleys++;
                        enter(State.RECOVER);
                    } else if (!isTransformed() && !partnerDashCycle && stateTicks >= HOVER_TICKS) {
                        relocate();
                    }
                }
            }
            case RECOVER -> {
                steerTo(hoverPosition, HOVER_SPEED);
                if (isClose(target)) {
                    beginCloseAttack(target);
                } else if (stateTicks >= VOLLEY_RECOVERY_TICKS) {
                    if (volleys >= VOLLEYS_PER_CYCLE) {
                        lockedDirection = aimAt(target);
                        beamDirection = lockedDirection;
                        enter(State.TRACK_CHARGE);
                    } else {
                        shots = 0;
                        shotTimer = 1;
                        enter(State.SHOOT);
                    }
                }
            }
            case TRACK_CHARGE -> {
                brake();
                beamDirection = aimAt(target);
                updateBeam(beamDirection, true);
                if (isClose(target)) beginCloseAttack(target);
                else if (stateTicks >= TRACK_CHARGE_TICKS) enter(State.TRACK);
            }
            case TRACK -> {
                brake();
                if (isClose(target)) {
                    beginCloseAttack(target);
                } else {
                    // 限制瞄准方向的变化速度，光束不会瞬间追上横移的目标。
                    beamDirection = turnDirectionToward(beamDirection, aimAt(target), TRACK_TURN_DEGREES);
                    damageBeam(beamDirection, 1.0F);
                    if (stateTicks >= TRACK_TICKS) relocate();
                }
            }
            case SWEEP_CHARGE -> {
                brake();
                updateBeam(sweepDirection(0), true);
                if (stateTicks >= SWEEP_CHARGE_TICKS) enter(State.SWEEP);
            }
            case SWEEP -> {
                brake();
                float progress = Math.min(1.0F, (stateTicks - 1.0F) / (SWEEP_TICKS - 1));
                damageBeam(sweepDirection(isTransformed() && sweeps == 1 ? 1.0F - progress : progress), 1.0F);
                if (stateTicks >= SWEEP_TICKS) {
                    if (--sweeps > 0) enter(State.SWEEP);
                    else relocate();
                }
            }
            case COMBO_AIM -> {
                brake();
                if (!(getPartner() instanceof Spazmatism flame) || !flame.isAlive() || stateTicks >= COMBO_AIM_TIMEOUT_TICKS) {
                    relocate();
                    break;
                }
                TwinEyeProjectile giant = flame.getComboFlame();
                if (giant == null) {
                    if (!flame.isChargingComboFlame()) relocate();
                    else if (stateTicks % 4 == 0) muzzleFlash(true);
                } else {
                    Vec3 direction = giant.position().subtract(getBeamOrigin()).normalize();
                    updateBeam(direction, true);
                    if (giant.tickCount >= COMBO_MIN_FLIGHT_TICKS && getLookAngle().dot(direction) > 0.96)
                        enter(State.COMBO_FIRE);
                }
            }
            case COMBO_FIRE -> {
                brake();
                if (!(getPartner() instanceof Spazmatism flame) || flame.getComboFlame() == null || stateTicks >= COMBO_FIRE_TICKS) {
                    relocate();
                    break;
                }
                TwinEyeProjectile giant = flame.getComboFlame();
                Vec3 direction = giant.position().subtract(getBeamOrigin()).normalize();
                Vec3 end = updateBeam(direction, false);
                // 必须真的被光束命中；墙壁截断射线时不会隔墙引爆。
                var box = giant.getBoundingBox().inflate(0.9);
                if ((box.contains(getBeamOrigin()) || box.clip(getBeamOrigin(), end).isPresent()) && giant.detonateFromLaser(this)) {
                    entityData.set(BEAM_LENGTH, (float) getBeamOrigin().distanceTo(giant.position()));
                    relocate();
                }
            }
            case BLAST_CHARGE -> {
                brake();
                updateBeam(lockedDirection, true);
                if (stateTicks >= BLAST_CHARGE_TICKS) enter(State.BLAST);
            }
            case BLAST -> {
                brake();
                if (stateTicks == 1) fireBlast();
                else {
                    entityData.set(BEAM_DIRECTION, lockedDirection.toVector3f());
                    entityData.set(BEAM_LENGTH, blastLength);
                    entityData.set(BEAM_CHARGING, false);
                }
                if (stateTicks >= BLAST_TICKS) relocate();
            }
        }
    }

    // 同目标的一阶段才协调技能：蓄冲时提前换侧，普通激光继续压制，冲刺链后再接蓄力光束。
    private void updatePartnerCoordination(LivingEntity target) {
        AbstractTwinEye partner = getPartner();
        if (!sharesFirstPhaseTarget(partner) || !(partner instanceof Spazmatism flame)) {
            partnerDashCycle = false;
            followupTicks = 0;
            return;
        }
        boolean preparingOrDashing = flame.isPreparingOrDashing();
        if (preparingOrDashing && !partnerDashCycle) chooseHoverPosition(target);
        if (!preparingOrDashing && partnerDashCycle) followupTicks = FOLLOWUP_WINDOW_TICKS;
        else if (followupTicks > 0) followupTicks--;
        partnerDashCycle = preparingOrDashing;
    }

    private boolean isClose(LivingEntity target) {
        AbstractTwinEye partner = getPartner();
        return closeTarget && closeAttackCooldown == 0 && hasFiringLine(getBeamOrigin(), target)
                && !(sharesFirstPhaseTarget(partner) && partner instanceof Spazmatism spazmatism && spazmatism.isPreparingOrDashing());
    }

    // 联动可以跨形态配合，但只能由同组二阶段魔焰眼发起，且不打断正在释放的大招。
    boolean beginFlameCombo(Spazmatism flame) {
        if (!flame.isTransformed() || !flame.isAlive() || getPartner() != flame || !isAlive()
                || getTarget() == null || isBeamAttack() || distanceToSqr(flame) > 48 * 48)
            return false;
        followupTicks = 0;
        enter(State.COMBO_AIM);
        return true;
    }

    boolean isFlameComboActive() {return state == State.COMBO_AIM || state == State.COMBO_FIRE;}

    boolean isBeamAttack() {
        return isFlameComboActive() || state == State.TRACK_CHARGE || state == State.TRACK || state == State.SWEEP_CHARGE
                || state == State.SWEEP || state == State.BLAST_CHARGE || state == State.BLAST;
    }

    private void beginCloseAttack(LivingEntity target) {
        closeAttackCooldown = CLOSE_ATTACK_COOLDOWN;
        lockedDirection = aimAt(target);
        if (isTransformed() && random.nextBoolean()) {
            enter(State.BLAST_CHARGE);
        } else {
            sweepAngle = isTransformed() ? 120.0F : 60.0F;
            sweeps = isTransformed() ? 2 : 1;
            enter(State.SWEEP_CHARGE);
        }
        clearBeam();
    }

    private Vec3 aimAt(LivingEntity target) {
        Vec3 direction = target.getEyePosition().subtract(getBeamOrigin());
        return direction.lengthSqr() > 1.0E-7 ? direction.normalize() : getLookAngle();
    }

    private Vec3 sweepDirection(float progress) {
        Vec3 side = lockedDirection.cross(new Vec3(0, 1, 0));
        if (side.lengthSqr() < 1.0E-7) side = new Vec3(1, 0, 0);
        double angle = (progress - 0.5) * sweepAngle * Mth.DEG_TO_RAD;
        return lockedDirection.scale(Math.cos(angle)).add(side.normalize().scale(Math.sin(angle))).normalize();
    }

    private void chooseHoverPosition(LivingEntity target) {
        Vec3 away = position().subtract(target.position());
        Vec3 center = predictTargetPosition(target, 6, 3);
        Vec3 previous = hoverPosition;
        Vec3 best = null;
        double bestScore = -Double.MAX_VALUE;
        AbstractTwinEye partner = getPartner();
        double startAngle = Math.atan2(away.z, away.x) + (random.nextBoolean() ? 0.8 : -0.8);
        // 比较射线、实体空间和移动路径；遮挡时从掩体侧面寻找新的射击位置。
        for (int i = 0; i < HOVER_CANDIDATES; i++) {
            double angle = startAngle + i * Math.PI * 2 / HOVER_CANDIDATES;
            double radius = 11 + random.nextDouble() * 5;
            Vec3 candidate = center.add(Math.cos(angle) * radius, 6 + random.nextDouble() * 4, Math.sin(angle) * radius);
            if (!level().noCollision(this, getBoundingBox().move(candidate.subtract(position()))))
                continue;
            Vec3 eye = candidate.add(0, getBbHeight() * 0.5, 0);
            double score = hasFiringLine(eye, target) ? 80 : 0;
            if (level().clip(new ClipContext(getBoundingBox().getCenter(), eye,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS)
                score += 20;
            score -= position().distanceTo(candidate) * 0.3;
            if (previous != null) score += Math.min(6, previous.distanceTo(candidate));
            if (partner != null && partner.isAlive()) {
                score -= Math.max(0, 8 - partner.position().distanceTo(candidate)) * 8;
                if (sharesFirstPhaseTarget(partner)) {
                    Vec3 partnerSide = partner.position().subtract(target.position()).multiply(1, 0, 1).normalize();
                    Vec3 candidateSide = candidate.subtract(target.position()).multiply(1, 0, 1).normalize();
                    score -= partnerSide.dot(candidateSide) * CROSS_FIRE_SCORE;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        hoverPosition = best != null ? best : center.add(0, 12, 0);
    }

    private void steerTo(Vec3 destination, double speed) {
        if (destination == null) return;
        Vec3 correction = destination.subtract(position());
        Vec3 velocity = getDeltaMovement().scale(0.8).add(correction.scale(0.035));
        setDeltaMovement(separateFromPartner(velocity, speed));
    }

    private void brake() {setDeltaMovement(getDeltaMovement().scale(0.7));}

    private void enter(State next) {
        state = next;
        stateTicks = 0;
        if (next == State.SWEEP_CHARGE || next == State.TRACK_CHARGE || next == State.BLAST_CHARGE || next == State.COMBO_AIM)
            playSound(ModSoundEvents.ITEM_LASER_SHOOT.get(), 0.7F, 0.5F);
    }

    private void relocate() {
        hoverPosition = null;
        blockedTicks = 0;
        enter(State.RELOCATE);
    }

    private void clearBeam() {entityData.set(BEAM_LENGTH, 0.0F);}

    @Override
    protected void suspendCombatVisuals() {clearBeam();}

    /// 伤害和客户端模型使用同一条被方块截断的射线。
    private Vec3 updateBeam(Vec3 direction, boolean charging) {
        beamDirection = direction;
        Vec3 origin = getBeamOrigin();
        Vec3 end = level().clip(new ClipContext(origin, origin.add(direction.scale(BEAM_RANGE)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation();
        entityData.set(BEAM_DIRECTION, direction.toVector3f());
        entityData.set(BEAM_LENGTH, (float) origin.distanceTo(end));
        entityData.set(BEAM_CHARGING, charging);
        if (charging && stateTicks % 4 == 0 && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(CHARGE_PARTICLE, origin.x, origin.y, origin.z, 5, 0.25, 0.25, 0.25, 0);
        }
        return end;
    }

    private void damageBeam(Vec3 direction, float multiplier) {
        Vec3 end = updateBeam(direction, false);
        Vec3 origin = getBeamOrigin();
        AABB bounds = new AABB(origin, end).inflate(BEAM_RADIUS);
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, bounds, this::canAttack)) {
            AABB box = victim.getBoundingBox().inflate(BEAM_RADIUS);
            if (box.contains(origin) || box.clip(origin, end).isPresent()) {
                victim.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier);
            }
        }
    }

    private void fireBlast() {
        Vec3 end = updateBeam(lockedDirection, false);
        Vec3 origin = getBeamOrigin();
        // 爆炸止于第一个生物或障碍，不破坏方块。
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, new AABB(origin, end).inflate(BEAM_RADIUS), this::canAttack)) {
            Vec3 hit = victim.getBoundingBox().inflate(BEAM_RADIUS).clip(origin, end).orElse(null);
            if (hit != null && origin.distanceToSqr(hit) < origin.distanceToSqr(end)) end = hit;
        }
        blastLength = (float) origin.distanceTo(end);
        entityData.set(BEAM_LENGTH, blastLength);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, end.x, end.y, end.z, 1, 0, 0, 0, 0);
        }
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, new AABB(end, end).inflate(BLAST_RADIUS), this::canAttack)) {
            if (victim.getBoundingBox().getCenter().distanceToSqr(end) <= BLAST_RADIUS * BLAST_RADIUS
                    && level().clip(new ClipContext(end, victim.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS) {
                victim.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F);
            }
        }
    }

    private void fireLaser(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        TwinEyeProjectile laser = ModEntities.RETINAZER_LASER.get().create(serverLevel);
        if (laser != null) {
            Vec3 origin = getMuzzlePosition(1.0F);
            Vec3 direction = predictInterceptPosition(target, origin, LASER_SPEED, 0,
                    legendaryCombat() ? 4 : 3).subtract(origin).normalize();
            laser.configure(this, origin, direction.scale(LASER_SPEED), (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 60);
            serverLevel.addFreshEntity(laser);
            muzzleFlash(false);
            playSound(ModSoundEvents.ITEM_DEATH_LASER.get(), 0.6F, 1.2F);
        }
    }

    // 枪口短闪配合弹体拖尾提示射击来源，粒子不产生额外伤害。
    private void muzzleFlash(boolean warning) {
        if (level() instanceof ServerLevel serverLevel)
            serverLevel.broadcastEntityEvent(this, warning ? MUZZLE_WARNING_EVENT : MUZZLE_FLASH_EVENT);
    }

    // 短闪是一次性发射器，使用世界发射口位置；不附着持续发射，也不逐帧重复创建。
    @Override
    public void handleEntityEvent(byte event) {
        if ((event == MUZZLE_WARNING_EVENT || event == MUZZLE_FLASH_EVENT) && level().isClientSide) {
            ParticleEmitter flash = new ParticleEmitter(level(), getBeamOrigin(), Confluence.asResource("twins_laser_flash"));
            flash.hideOutline = true;
            flash.getVars().setValue("variable.flash_scale", instance -> event == MUZZLE_WARNING_EVENT ? 0.6F : 1.0F);
            MolangParticleEngine.INSTANCE.addEmitter(flash);
        } else super.handleEntityEvent(event);
    }

    @Override
    protected void resetCombatCycle() {
        shots = 0;
        volleys = 0;
        sweeps = 0;
        partnerDashCycle = false;
        followupTicks = 0;
        clearBeam();
        relocate();
    }

    @Override
    protected void saveTwinCombat(CompoundTag tag) {
        tag.putInt("AttackVersion", 2);
        tag.putString("Attack", state.name());
        tag.putInt("AttackTicks", stateTicks);
        tag.putInt("ShotTimer", shotTimer);
        tag.putInt("Shots", shots);
        tag.putInt("Volleys", volleys);
        tag.putInt("Sweeps", sweeps);
        tag.putFloat("SweepAngle", sweepAngle);
        tag.putFloat("BlastLength", blastLength);
        tag.putInt("CloseCooldown", closeAttackCooldown);
        tag.putBoolean("CloseTarget", closeTarget);
        if (hoverPosition != null) saveVector(tag, "Hover", hoverPosition);
        saveVector(tag, "LockedAim", lockedDirection);
        saveVector(tag, "BeamAim", beamDirection);
    }

    @Override
    protected void loadTwinCombat(CompoundTag tag) {
        if (tag.getInt("AttackVersion") != 2) return;
        try {state = State.valueOf(tag.getString("Attack"));} catch (
                IllegalArgumentException ignored) {return;}
        stateTicks = Mth.clamp(tag.getInt("AttackTicks"), 0, 120);
        shotTimer = Mth.clamp(tag.getInt("ShotTimer"), 0, 30);
        shots = Mth.clamp(tag.getInt("Shots"), 0, VOLLEY_SIZE);
        volleys = Mth.clamp(tag.getInt("Volleys"), 0, 3);
        sweeps = Mth.clamp(tag.getInt("Sweeps"), 0, 2);
        blastLength = Mth.clamp(tag.getFloat("BlastLength"), 0, (float) BEAM_RANGE);
        closeAttackCooldown = Mth.clamp(tag.getInt("CloseCooldown"), 0, CLOSE_ATTACK_COOLDOWN);
        closeTarget = tag.getBoolean("CloseTarget");
        sweepAngle = Mth.clamp(tag.getFloat("SweepAngle"), 60, 120);
        hoverPosition = tag.contains("HoverX") ? loadVector(tag, "Hover") : null;
        lockedDirection = loadVector(tag, "LockedAim").normalize();
        beamDirection = loadVector(tag, "BeamAim").normalize();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {return ModSoundEvents.METAL_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {return ModSoundEvents.METAL_DEATH.get();}

    private enum State {RELOCATE, SHOOT, RECOVER, TRACK_CHARGE, TRACK, SWEEP_CHARGE, SWEEP, BLAST_CHARGE, BLAST, COMBO_AIM, COMBO_FIRE}
}
