package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
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
import org.confluence.mod.common.entity.projectile.TwinEyeProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;

/// 魔焰眼：同高射击与锁向长冲刺交替，变形后改为近距离持续喷焰。
public class Spazmatism extends AbstractTwinEye {
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
    private static final double FIRE_DISTANCE = 11.0;
    private static final double STREAM_DISTANCE = 6.0;
    private static final float FLAME_BALL_SPEED = 1.1F;
    private static final float STREAM_SPEED = 0.8F;

    private State state = State.APPROACH;
    private int stateTicks;
    private int shotTimer;
    private int dashes;
    private Vec3 dashDirection = Vec3.ZERO;
    private int orbitDirection = 1;
    private Vec3 firingPosition;
    private long nextStreamSoundTick;

    public Spazmatism(EntityType<? extends Monster> type, Level level) {
        super(type, level);
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
        return state == State.DASH || state == State.WINDUP ? dashDirection : super.getCombatLookDirection(target);
    }

    @Override
    protected void tickTwinCombat() {
        LivingEntity target = getTarget();
        if (target == null) return;
        stateTicks++;
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
                    dashes = isTransformed() ? PHASE_TWO_DASH_COUNT : DASH_COUNT;
                    dashDirection = predictDashDirection(target);
                    enter(State.WINDUP);
                }
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

    private double dashSpeed() {return isTransformed() ? PHASE_TWO_DASH_SPEED : PHASE_ONE_DASH_SPEED;}

    private Vec3 predictDashDirection(LivingEntity target) {
        double flightTicks = Math.min(10, distanceTo(target) / dashSpeed());
        Vec3 direction = predictTargetPosition(target, flightTicks, legendaryCombat() ? 5 : 3.5).subtract(getEyePosition());
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
        double speed = isTransformed() ? 0.6 : 0.7;
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

    private void fireFlame(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        TwinEyeProjectile flame = ModEntities.SPAZMATISM_FLAME.get().create(serverLevel);
        if (flame == null) return;
        Vec3 aim = isTransformed() ? target.getEyePosition()
                : predictTargetPosition(target, Math.min(10, distanceTo(target) / FLAME_BALL_SPEED), legendaryCombat() ? 3 : 2);
        flame.configureFlame(this, aim, (float) getAttributeValue(Attributes.ATTACK_DAMAGE),
                isTransformed() ? STREAM_SPEED : FLAME_BALL_SPEED, isTransformed(), legendaryCombat());
        serverLevel.addFreshEntity(flame);
        if (!isTransformed()) {
            playSound(ModSoundEvents.ITEM_FLAME_CAST.get(), 0.6F, 1.0F);
        } else if (level().getGameTime() >= nextStreamSoundTick) {
            playSound(ModSoundEvents.GUN_FLAMETHROWER.get(), 0.6F, 1.0F);
            nextStreamSoundTick = level().getGameTime() + STREAM_SOUND_INTERVAL_TICKS;
        }
    }

    private void enter(State next) {
        state = next;
        stateTicks = 0;
    }

    @Override
    protected void resetCombatCycle() {
        enter(State.APPROACH);
        shotTimer = 0;
        dashes = 0;
        dashDirection = Vec3.ZERO;
        firingPosition = null;
        nextStreamSoundTick = 0;
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
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {return ModSoundEvents.METAL_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {return ModSoundEvents.METAL_DEATH.get();}

    private enum State {APPROACH, FIRE, WINDUP, DASH, RECOVER}
}
