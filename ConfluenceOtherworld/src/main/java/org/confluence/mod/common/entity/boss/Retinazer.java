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

    /// 更换悬停站位的基础超时，单位 tick。
    private static final int RELOCATE_TICKS = 40;
    /// 每次选择悬停位置时评估的候选点数。
    private static final int HOVER_CANDIDATES = 10;
    /// 抵达悬停位置的距离平方，单位格²。
    private static final double ARRIVAL_RANGE_SQUARED = 2.25;
    /// 激光眼移动到悬停点的速度上限，单位格/tick。
    private static final double HOVER_SPEED = 1.25;
    /// 移动到悬停点时每 tick 保留的旧速度比例。
    private static final double HOVER_DRAG = 0.8;
    /// 悬停位置误差转换为速度的系数。
    private static final double HOVER_ACCELERATION = 0.035;
    /// 悬停站位预测目标移动的提前时间，单位 tick。
    private static final int HOVER_PREDICTION_TICKS = 6;
    /// 悬停站位预测目标移动的最大偏移，单位格。
    private static final double HOVER_MAX_LEAD = 3;
    /// 悬停候选点相对当前方位的起始偏角，单位弧度。
    private static final double HOVER_ANGLE_OFFSET = 0.8;
    /// 悬停候选点距目标的最小水平距离，单位格。
    private static final double HOVER_MIN_DISTANCE = 11;
    /// 悬停水平距离的随机增加范围，单位格。
    private static final double HOVER_DISTANCE_VARIATION = 5;
    /// 悬停候选点高于预测目标的最小距离，单位格。
    private static final double HOVER_MIN_HEIGHT = 6;
    /// 悬停高度的随机增加范围，单位格。
    private static final double HOVER_HEIGHT_VARIATION = 4;
    /// 没有可用悬停候选点时的备用高度，单位格。
    private static final double HOVER_FALLBACK_HEIGHT = 12;
    /// 能够直接射击目标的悬停位置评分。
    private static final double HOVER_LINE_SCORE = 80;
    /// 移动路径没有遮挡时的站位评分。
    private static final double HOVER_PATH_SCORE = 20;
    /// 站位移动距离的评分惩罚系数。
    private static final double HOVER_TRAVEL_WEIGHT = 0.3;
    /// 相对上一次站位的距离评分上限，单位格。
    private static final double HOVER_CHANGE_SCORE_LIMIT = 6;
    /// 选取悬停站位时避让搭档的距离，单位格。
    private static final double HOVER_PARTNER_DISTANCE = 8;
    /// 悬停位置过近搭档时的评分惩罚系数。
    private static final double HOVER_PARTNER_WEIGHT = 8;
    /// 施放长光束时每 tick 保留的移动速度比例。
    private static final double BRAKE_DRAG = 0.7;

    /// 一阶段普通悬停射击持续时间，单位 tick。
    private static final int HOVER_TICKS = 64;
    /// 悬停射击允许的目标距离平方，单位格²。
    private static final double SHOOT_RANGE_SQUARED = 32 * 32;
    /// 进入射击站位后的首次瞄准等待时间，单位 tick。
    private static final int AIM_SETTLE_TICKS = 8;
    /// 普通难度一阶段小激光发射间隔，单位 tick。
    private static final int PHASE_ONE_SHOT_INTERVAL = 18;
    /// 传奇难度一阶段小激光发射间隔，单位 tick。
    private static final int LEGENDARY_PHASE_ONE_SHOT_INTERVAL = 10;
    /// 普通难度二阶段小激光连发间隔，单位 tick。
    private static final int PHASE_TWO_SHOT_INTERVAL = 4;
    /// 传奇难度二阶段小激光连发间隔，单位 tick。
    private static final int LEGENDARY_PHASE_TWO_SHOT_INTERVAL = 2;
    /// 二阶段每组小激光的连发数量，连冲结束后同样使用此数量。
    private static final int VOLLEY_SIZE = 10;
    /// 进入持续追踪光束前的小激光连发组数。
    private static final int VOLLEYS_PER_CYCLE = 3;
    /// 每组小激光连发后的恢复时间，单位 tick。
    private static final int VOLLEY_RECOVERY_TICKS = 24;
    /// 小激光飞行初速，单位格/tick。
    private static final float LASER_SPEED = 3.2F;
    /// 单枚小激光的寿命，单位 tick。
    private static final int LASER_LIFETIME = 60;
    /// 普通难度小激光拦截预测的最大偏移，单位格。
    private static final double LASER_MAX_LEAD = 3;
    /// 传奇难度小激光拦截预测的最大偏移，单位格。
    private static final double LEGENDARY_LASER_MAX_LEAD = 4;
    /// 小激光发射前的枪口预警提前时间，单位 tick。
    private static final int SHOT_WARNING_TICKS = 3;
    /// 射线受阻后再次尝试发射的间隔，单位 tick。
    private static final int BLOCKED_RETRY_TICKS = 2;
    /// 射线连续受阻多久后换位，单位 tick。
    private static final int BLOCKED_RELOCATE_TICKS = 16;

    /// 长光束的最大长度，单位格；实际长度受方块遮挡限制。
    private static final double BEAM_RANGE = 96.0;
    /// 长光束伤害检测的扩张半径，单位格。
    private static final double BEAM_RADIUS = 0.4;
    /// 持续追踪光束的蓄力时间，单位 tick。
    private static final int TRACK_CHARGE_TICKS = 24;
    /// 持续追踪光束的攻击时间，单位 tick。
    private static final int TRACK_TICKS = 100;
    /// 追踪光束每 tick 的最大转向角度，单位度。
    private static final float TRACK_TURN_DEGREES = 1.2F;

    /// 触发近身反制的距离平方，单位格²。
    private static final double CLOSE_RANGE_SQUARED = 64.0;
    /// 退出近身状态的距离平方，单位格²；形成距离滞回。
    private static final double CLOSE_EXIT_RANGE_SQUARED = 121.0;
    /// 近身反制技能冷却，单位 tick。
    private static final int CLOSE_ATTACK_COOLDOWN = 80;
    /// 扇形横扫前的蓄力时间，单位 tick。
    private static final int SWEEP_CHARGE_TICKS = 30;
    /// 一阶段横扫扇形的总角度，单位度。
    private static final float PHASE_ONE_SWEEP_ANGLE = 60.0F;
    /// 一阶段完成一次 60 度横扫的时间，单位 tick；10 tick 为半秒。
    private static final int FIRST_PHASE_SWEEP_TICKS = 10;
    /// 二阶段横扫扇形的总角度，单位度。
    private static final float PHASE_TWO_SWEEP_ANGLE = 120.0F;
    /// 二阶段每次横扫的时间，单位 tick。
    private static final int SWEEP_TICKS = 40;
    /// 二阶段连续往返横扫次数。
    private static final int PHASE_TWO_SWEEPS = 2;
    /// 横扫伤害射线之间允许的最大角度，单位度；避免相邻 tick 漏判。
    private static final float SWEEP_SAMPLE_DEGREES = 0.5F;

    /// 爆破激光的蓄力时间，单位 tick。
    private static final int BLAST_CHARGE_TICKS = 36;
    /// 爆破激光发射状态的持续时间，单位 tick。
    private static final int BLAST_TICKS = 6;
    /// 爆破激光的伤害半径，单位格。
    private static final double BLAST_RADIUS = 3.5;
    /// 爆破激光相对基础攻击伤害的倍率。
    private static final float BLAST_DAMAGE_MULTIPLIER = 1.5F;

    /// 等待并瞄准搭档巨焰的最长时间，单位 tick。
    private static final int COMBO_AIM_TIMEOUT_TICKS = 100;
    /// 巨焰发射后至少飞行多久才允许引爆，单位 tick。
    private static final int COMBO_MIN_FLIGHT_TICKS = 16;
    /// 允许引爆巨焰的朝向点积阈值。
    private static final double COMBO_AIM_DOT = 0.96;
    /// 用于引爆巨焰的光束状态时长，单位 tick。
    private static final int COMBO_FIRE_TICKS = 10;
    /// 光束检测巨焰命中时的碰撞箱扩张，单位格。
    private static final double COMBO_HIT_PADDING = 0.9;
    /// 请求巨焰联动时允许的搭档距离平方，单位格²。
    private static final double COMBO_PARTNER_RANGE_SQUARED = 48 * 48;
    /// 响应搭档冲刺后的协同攻击窗口，单位 tick。
    private static final int FOLLOWUP_WINDOW_TICKS = 30;
    /// 协同追击允许的目标距离平方，单位格²。
    private static final double FOLLOWUP_RANGE_SQUARED = 24 * 24;
    /// 优先选择与搭档形成交叉火力站位的评分权重。
    private static final double CROSS_FIRE_SCORE = 24.0;

    /// 一阶段激光喷口距模型中心的距离，单位格。
    private static final double PHASE_ONE_MUZZLE_DISTANCE = 23.0 / 16.0;
    /// 二阶段激光喷口距模型中心的距离，单位格。
    private static final double PHASE_TWO_MUZZLE_DISTANCE = 28.0 / 16.0;
    /// 激光眼变形动画时长，单位 tick。
    private static final int TRANSFORM_TICKS = 31;

    /// 选择悬停射击站位时允许与目标相距的最大距离平方，单位格²。
    private static final double HOVER_TARGET_MAX_DISTANCE_SQUARED = 32 * 32;
    private static final byte MUZZLE_WARNING_EVENT = 66;
    private static final byte MUZZLE_FLASH_EVENT = 67;
    private static final DustParticleOptions CHARGE_PARTICLE = new DustParticleOptions(new Vector3f(0.65F, 0.2F, 1.0F), 1.3F);

    /// 一阶段两轮冲刺之间的间隔，单位 tick。
    private static final int PHASE_ONE_DASH_INTERVAL = 160;
    /// 二阶段两轮连冲之间的间隔，单位 tick。
    private static final int PHASE_TWO_DASH_INTERVAL = 120;
    /// 冲刺前锁定目标并蓄力的时间，单位 tick。
    private static final int DASH_WINDUP_TICKS = 12;
    /// 每次直线冲刺的持续时间，单位 tick。
    private static final int DASH_TICKS = 12;
    /// 两次连续冲刺之间的恢复时间，单位 tick。
    private static final int DASH_RECOVER_TICKS = 6;
    /// 一阶段冲刺速度，单位格/tick。
    private static final double PHASE_ONE_DASH_SPEED = 1.8;
    /// 二阶段冲刺速度，单位格/tick。
    private static final double PHASE_TWO_DASH_SPEED = 2.4;
    /// 二阶段每轮连续冲刺的最少次数。
    private static final int PHASE_TWO_DASH_MIN = 3;
    /// 二阶段每轮连续冲刺的最多次数。
    private static final int PHASE_TWO_DASH_MAX = 4;

    private State state = State.RELOCATE;
    private int stateTicks;
    private int dashCooldown = PHASE_ONE_DASH_INTERVAL;
    private int dashes;
    /// 连冲后的首组十连发优先完成，随后恢复近距离攻击与组合技判断。
    private boolean postDashVolley;
    private Vec3 dashDirection = new Vec3(0, 0, 1);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BEAM_DIRECTION, new Vector3f(0, 0, 1));
        builder.define(BEAM_LENGTH, 0.0F);
        builder.define(BEAM_CHARGING, false);
    }

    @Override
    protected boolean isRetinazer() {return true;}

    @Override
    protected boolean isDashCombatState() {return state == State.DASH;}

    @Override
    protected int transformationDuration() {return TRANSFORM_TICKS;}

    public Vec3 getBeamDirection() {return new Vec3(entityData.get(BEAM_DIRECTION));}

    public float getBeamLength() {return entityData.get(BEAM_LENGTH);}

    public boolean isBeamCharging() {return entityData.get(BEAM_CHARGING);}

    public Vec3 getBeamOrigin() {return getMuzzlePosition(1.0F);}

    @Override
    protected double getMuzzleDistance() {return isTransformed() ? PHASE_TWO_MUZZLE_DISTANCE : PHASE_ONE_MUZZLE_DISTANCE;}


    @Override
    protected Vec3 getCombatLookDirection(LivingEntity target) {
        if (isDashCycle()) return dashDirection;
        if (state == State.COMBO_AIM && getBeamLength() <= 0 && getPartner() instanceof Spazmatism flame)
            return flame.getMuzzlePosition(1.0F).subtract(getEyePosition());
        return getBeamLength() > 0 ? getBeamDirection()
                : predictInterceptPosition(target, getBeamOrigin(), LASER_SPEED, 0, legendaryCombat() ? LEGENDARY_LASER_MAX_LEAD : LASER_MAX_LEAD).subtract(getEyePosition());
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
        if (dashCooldown > 0) dashCooldown--;
        if (dashCooldown == 0 && !isBeamAttack() && !isDashCycle()) {
            dashes = isTransformed() ? PHASE_TWO_DASH_MIN + random.nextInt(PHASE_TWO_DASH_MAX - PHASE_TWO_DASH_MIN + 1) : 1;
            dashDirection = aimAt(target);
            followupTicks = 0;
            enter(State.DASH_WINDUP);
        }
        // 冲刺链结束后的短窗口接光束；保留蓄力、冷却与遮挡检查，不在冲刺中叠加横扫。
        if (followupTicks > 0 && !postDashVolley && !isDashCycle() && !isBeamAttack() && closeAttackCooldown == 0
                && targetDistance < FOLLOWUP_RANGE_SQUARED && hasFiringLine(getBeamOrigin(), target)) {
            followupTicks = 0;
            beginCloseAttack(target);
        }
        switch (state) {
            case RELOCATE -> {
                if (hoverPosition == null) chooseHoverPosition(target);
                if (hoverPosition.distanceToSqr(target.position()) > HOVER_TARGET_MAX_DISTANCE_SQUARED
                        || stateTicks % 10 == 0 && !level().noCollision(this, getBoundingBox().move(hoverPosition.subtract(position())))) {
                    chooseHoverPosition(target);
                }
                steerTo(hoverPosition, HOVER_SPEED);
                if (position().distanceToSqr(hoverPosition) < ARRIVAL_RANGE_SQUARED
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
                if (distanceToSqr(target) > SHOOT_RANGE_SQUARED) {
                    relocate();
                    break;
                }
                steerTo(hoverPosition, HOVER_SPEED);
                if (shotTimer == SHOT_WARNING_TICKS) muzzleFlash(true);
                blockedTicks = hasFiringLine(getBeamOrigin(), target) ? 0 : blockedTicks + 1;
                if (!postDashVolley && isClose(target)) {
                    beginCloseAttack(target);
                } else if (blockedTicks >= BLOCKED_RELOCATE_TICKS) {
                    relocate();
                } else if (--shotTimer <= 0) {
                    if (blockedTicks > 0) {
                        shotTimer = BLOCKED_RETRY_TICKS;
                        break;
                    }
                    fireLaser(target);
                    shots++;
                    shotTimer = isTransformed()
                            ? legendaryCombat() ? LEGENDARY_PHASE_TWO_SHOT_INTERVAL : PHASE_TWO_SHOT_INTERVAL
                            : legendaryCombat() ? LEGENDARY_PHASE_ONE_SHOT_INTERVAL : PHASE_ONE_SHOT_INTERVAL;
                    if (isTransformed() && shots >= VOLLEY_SIZE) {
                        postDashVolley = false;
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
            case DASH_WINDUP -> {
                brake();
                if (stateTicks >= DASH_WINDUP_TICKS) {
                    enter(State.DASH);
                    playSound(ModSoundEvents.ROAR.get(), 0.8F, 1.2F);
                }
            }
            case DASH -> {
                setDeltaMovement(dashDirection.scale(isTransformed() ? PHASE_TWO_DASH_SPEED : PHASE_ONE_DASH_SPEED));
                if (stateTicks >= DASH_TICKS) {
                    dashes--;
                    enter(State.DASH_RECOVER);
                }
            }
            case DASH_RECOVER -> {
                brake();
                if (stateTicks >= DASH_RECOVER_TICKS) {
                    if (dashes > 0) {
                        dashDirection = aimAt(target);
                        enter(State.DASH_WINDUP);
                    } else {
                        dashCooldown = isTransformed() ? PHASE_TWO_DASH_INTERVAL : PHASE_ONE_DASH_INTERVAL;
                        if (isTransformed()) {
                            postDashVolley = true;
                            hoverPosition = position();
                            volleys = 0;
                            shots = 0;
                            shotTimer = AIM_SETTLE_TICKS;
                            enter(State.SHOOT);
                        } else relocate();
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
                int duration = isTransformed() ? SWEEP_TICKS : FIRST_PHASE_SWEEP_TICKS;
                float progress = Math.min(1.0F, (stateTicks - 1.0F) / (duration - 1));
                damageSweep(progress, Math.max(0.0F, (stateTicks - 2.0F) / (duration - 1)));
                if (stateTicks >= duration) {
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
                    if (giant.tickCount >= COMBO_MIN_FLIGHT_TICKS && getLookAngle().dot(direction) > COMBO_AIM_DOT)
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
                var box = giant.getBoundingBox().inflate(COMBO_HIT_PADDING);
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
                || getTarget() == null || isBeamAttack() || isDashCycle() || postDashVolley || distanceToSqr(flame) > COMBO_PARTNER_RANGE_SQUARED)
            return false;
        followupTicks = 0;
        enter(State.COMBO_AIM);
        return true;
    }

    boolean isFlameComboActive() {return state == State.COMBO_AIM || state == State.COMBO_FIRE;}

    /// 冲刺蓄力、位移和恢复共同组成一轮，期间不被协同攻击打断。
    private boolean isDashCycle() {
        return state == State.DASH_WINDUP || state == State.DASH || state == State.DASH_RECOVER;
    }

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
            sweepAngle = isTransformed() ? PHASE_TWO_SWEEP_ANGLE : PHASE_ONE_SWEEP_ANGLE;
            sweeps = isTransformed() ? PHASE_TWO_SWEEPS : 1;
            enter(State.SWEEP_CHARGE);
        }
        clearBeam();
    }

    private Vec3 aimAt(LivingEntity target) {
        Vec3 direction = target.getEyePosition().subtract(getBeamOrigin());
        return direction.lengthSqr() > 1.0E-7 ? direction.normalize() : getLookAngle();
    }

    /// 补齐相邻 tick 之间扫过的扇形，防止快速横扫跳过玩家；最终同步方向仍是本 tick 的末端。
    private void damageSweep(float progress, float previousProgress) {
        int samples = Math.max(1, Mth.ceil((progress - previousProgress) * sweepAngle / SWEEP_SAMPLE_DEGREES));
        boolean reverse = isTransformed() && sweeps == 1;
        for (int i = 0; i <= samples; i++) {
            float sample = Mth.lerp((float) i / samples, previousProgress, progress);
            damageBeam(sweepDirection(reverse ? 1.0F - sample : sample), 1.0F);
        }
    }

    private Vec3 sweepDirection(float progress) {
        Vec3 side = lockedDirection.cross(new Vec3(0, 1, 0));
        if (side.lengthSqr() < 1.0E-7) side = new Vec3(1, 0, 0);
        double angle = (progress - 0.5) * sweepAngle * Mth.DEG_TO_RAD;
        return lockedDirection.scale(Math.cos(angle)).add(side.normalize().scale(Math.sin(angle))).normalize();
    }

    private void chooseHoverPosition(LivingEntity target) {
        Vec3 away = position().subtract(target.position());
        Vec3 center = predictTargetPosition(target, HOVER_PREDICTION_TICKS, HOVER_MAX_LEAD);
        Vec3 previous = hoverPosition;
        Vec3 best = null;
        double bestScore = -Double.MAX_VALUE;
        AbstractTwinEye partner = getPartner();
        double startAngle = Math.atan2(away.z, away.x) + (random.nextBoolean() ? HOVER_ANGLE_OFFSET : -HOVER_ANGLE_OFFSET);
        // 比较射线、实体空间和移动路径；遮挡时从掩体侧面寻找新的射击位置。
        for (int i = 0; i < HOVER_CANDIDATES; i++) {
            double angle = startAngle + i * Math.PI * 2 / HOVER_CANDIDATES;
            double radius = HOVER_MIN_DISTANCE + random.nextDouble() * HOVER_DISTANCE_VARIATION;
            Vec3 candidate = center.add(Math.cos(angle) * radius, HOVER_MIN_HEIGHT + random.nextDouble() * HOVER_HEIGHT_VARIATION, Math.sin(angle) * radius);
            if (!level().noCollision(this, getBoundingBox().move(candidate.subtract(position()))))
                continue;
            Vec3 eye = candidate.add(0, getBbHeight() * 0.5, 0);
            double score = hasFiringLine(eye, target) ? HOVER_LINE_SCORE : 0;
            if (level().clip(new ClipContext(getBoundingBox().getCenter(), eye,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS)
                score += HOVER_PATH_SCORE;
            score -= position().distanceTo(candidate) * HOVER_TRAVEL_WEIGHT;
            if (previous != null)
                score += Math.min(HOVER_CHANGE_SCORE_LIMIT, previous.distanceTo(candidate));
            if (partner != null && partner.isAlive()) {
                score -= Math.max(0, HOVER_PARTNER_DISTANCE - partner.position().distanceTo(candidate)) * HOVER_PARTNER_WEIGHT;
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
        hoverPosition = best != null ? best : center.add(0, HOVER_FALLBACK_HEIGHT, 0);
    }

    private void steerTo(Vec3 destination, double speed) {
        if (destination == null) return;
        Vec3 correction = destination.subtract(position());
        Vec3 velocity = getDeltaMovement().scale(HOVER_DRAG).add(correction.scale(HOVER_ACCELERATION));
        setDeltaMovement(separateFromPartner(velocity, speed));
    }

    private void brake() {setDeltaMovement(getDeltaMovement().scale(BRAKE_DRAG));}

    private void enter(State next) {
        state = next;
        stateTicks = 0;
        if (next == State.SWEEP_CHARGE || next == State.TRACK_CHARGE || next == State.BLAST_CHARGE || next == State.COMBO_AIM)
            playSound(ModSoundEvents.ITEM_LASER_SHOOT.get(), 0.7F, 0.5F);
    }

    private void relocate() {
        postDashVolley = false;
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
                victim.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * BLAST_DAMAGE_MULTIPLIER);
            }
        }
    }

    private void fireLaser(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        TwinEyeProjectile laser = ModEntities.RETINAZER_LASER.get().create(serverLevel);
        if (laser != null) {
            Vec3 origin = getMuzzlePosition(1.0F);
            Vec3 direction = predictInterceptPosition(target, origin, LASER_SPEED, 0,
                    legendaryCombat() ? LEGENDARY_LASER_MAX_LEAD : LASER_MAX_LEAD).subtract(origin).normalize();
            laser.configure(this, origin, direction.scale(LASER_SPEED), (float) getAttributeValue(Attributes.ATTACK_DAMAGE), LASER_LIFETIME);
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
            MolangParticleEngine.INSTANCE._addEmitter(flash);
        } else super.handleEntityEvent(event);
    }

    @Override
    protected void resetCombatCycle() {
        shots = 0;
        volleys = 0;
        sweeps = 0;
        dashes = 0;
        dashCooldown = isTransformed() ? PHASE_TWO_DASH_INTERVAL : PHASE_ONE_DASH_INTERVAL;
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
        tag.putInt("Dashes", dashes);
        tag.putBoolean("PostDashVolley", postDashVolley);
        tag.putInt("DashCooldown", dashCooldown);
        saveVector(tag, "DashDirection", dashDirection);
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
        dashes = Mth.clamp(tag.getInt("Dashes"), 0, PHASE_TWO_DASH_MAX);
        postDashVolley = state == State.SHOOT && tag.getBoolean("PostDashVolley");
        dashCooldown = tag.contains("DashCooldown") ? Mth.clamp(tag.getInt("DashCooldown"), 0, PHASE_ONE_DASH_INTERVAL) : PHASE_ONE_DASH_INTERVAL;
        dashDirection = loadVector(tag, "DashDirection").normalize();
        if (dashDirection.lengthSqr() < 1.0E-8) dashDirection = getLookAngle();
        volleys = Mth.clamp(tag.getInt("Volleys"), 0, VOLLEYS_PER_CYCLE);
        sweeps = Mth.clamp(tag.getInt("Sweeps"), 0, PHASE_TWO_SWEEPS);
        blastLength = Mth.clamp(tag.getFloat("BlastLength"), 0, (float) BEAM_RANGE);
        closeAttackCooldown = Mth.clamp(tag.getInt("CloseCooldown"), 0, CLOSE_ATTACK_COOLDOWN);
        closeTarget = tag.getBoolean("CloseTarget");
        sweepAngle = Mth.clamp(tag.getFloat("SweepAngle"), PHASE_ONE_SWEEP_ANGLE, PHASE_TWO_SWEEP_ANGLE);
        hoverPosition = tag.contains("HoverX") ? loadVector(tag, "Hover") : null;
        lockedDirection = loadVector(tag, "LockedAim").normalize();
        beamDirection = loadVector(tag, "BeamAim").normalize();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {return ModSoundEvents.METAL_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {return ModSoundEvents.METAL_DEATH.get();}

    private enum State {
        /// 冲刺前蓄力并锁定本次方向。
        DASH_WINDUP,
        /// 沿锁定方向直线冲刺。
        DASH,
        /// 冲刺后的减速恢复，结束后继续连冲或进入射击。
        DASH_RECOVER,
        /// 选择并移动到新的悬停射击位置。
        RELOCATE,
        /// 朝目标发射普通激光；二阶段按十连发计数。
        SHOOT,
        /// 激光连发后的后摇，结束后继续射击或蓄力追踪光束。
        RECOVER,
        /// 持续追踪光束的蓄力预警。
        TRACK_CHARGE,
        /// 持续发射并缓慢转向追踪目标的长光束。
        TRACK,
        /// 扇形横扫前的蓄力预警。
        SWEEP_CHARGE,
        /// 按锁定扇形执行长光束横扫。
        SWEEP,
        /// 锁定方向并蓄力爆破激光。
        BLAST_CHARGE,
        /// 释放爆破激光并保留短暂光束画面。
        BLAST,
        /// 等待搭档发射巨焰，并将光束对准巨焰。
        COMBO_AIM,
        /// 发射光束，实际命中同组巨焰后将其引爆。
        COMBO_FIRE
    }
}
