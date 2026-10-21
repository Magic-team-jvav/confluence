package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.particle.TwinEyeLinkEmitter;
import org.confluence.mod.common.entity.MoneyDropSource;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.network.s2c.BossBarSyncPacketS2C.Health;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/// 双眼各自维护技能与脱战，通过遭遇记录共享一阶段目标、血条和胜负。
public abstract class AbstractTwinEye extends BaseBoss implements MoneyDropSource {
    private static final EntityDataAccessor<Optional<UUID>> ENCOUNTER_UUID = SynchedEntityData.defineId(AbstractTwinEye.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_TRANSFORMED = SynchedEntityData.defineId(AbstractTwinEye.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DASHING = SynchedEntityData.defineId(AbstractTwinEye.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TRANSITION_TICKS = SynchedEntityData.defineId(AbstractTwinEye.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_BANK = SynchedEntityData.defineId(AbstractTwinEye.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_PRIMARY = SynchedEntityData.defineId(AbstractTwinEye.class, EntityDataSerializers.BOOLEAN);

    /// 双眼水平转向速度上限，单位度/tick。
    private static final float MAX_YAW_SPEED = 18.8F;
    /// 双眼俯仰转向速度上限，单位度/tick。
    private static final float MAX_PITCH_SPEED = 16.0F;
    /// 水平角速度每 tick 的最大变化量，单位度/tick²。
    private static final float YAW_ACCELERATION = 3.0F;
    /// 俯仰角速度每 tick 的最大变化量，单位度/tick²。
    private static final float PITCH_ACCELERATION = 2.0F;
    /// 转向角误差转换为目标角速度的系数。
    private static final float TURN_RESPONSE = 0.28F;
    /// 水平转向角转换为模型侧倾的系数。
    private static final float BANK_RESPONSE = 2.0F;
    /// 模型最大侧倾角，单位度。
    private static final float MAX_BANK_DEGREES = 16;
    /// 客户端侧倾角插值系数，取值 0 到 1。
    private static final float BANK_SMOOTHING = 0.25F;

    /// 目标移动采样的平滑系数，取值 0 到 1。
    private static final double MOTION_SMOOTHING = 0.35;
    /// 拦截预测允许的最大提前时间，单位 tick。
    private static final double MAX_PREDICTION_TICKS = 20;
    /// 目标位移超过此距离平方时按瞬移处理，不纳入预判，单位格²。
    private static final double TELEPORT_SAMPLE_DISTANCE_SQUARED = 16;
    /// 纳入预判的目标速度上限，单位格/tick。
    private static final double MAX_SAMPLED_TARGET_SPEED = 1.2;
    /// 拦截时间与落点的迭代计算次数。
    private static final int INTERCEPT_ITERATIONS = 3;

    /// 寻找同组搭档的最大范围，单位格。
    private static final double PARTNER_SEARCH_RANGE = 300;
    /// 双眼互相避让的距离，单位格。
    private static final double PARTNER_SEPARATION_DISTANCE = 6;
    /// 双眼间距不足时转为分离速度的系数。
    private static final double PARTNER_SEPARATION_STRENGTH = 0.06;
    /// 白天离场的上升速度，单位格/tick。
    private static final double LEAVING_SPEED = 0.8;
    /// 白天离场后移除实体的延迟，单位 tick。
    private static final int LEAVING_TICKS = 40;
    /// 无攻击目标时的上升速度，单位格/tick。
    private static final double IDLE_RISE_SPEED = 0.45D;
    /// 暂停战斗期间每 tick 保留的速度比例。
    private static final double SUSPENDED_DRAG = 0.85D;

    /// 普通难度进入二阶段的生命比例。
    private static final float PHASE_TWO_HEALTH_RATIO = 0.4F;
    /// 传奇难度进入二阶段的生命比例。
    private static final float LEGENDARY_PHASE_TWO_HEALTH_RATIO = 0.6F;
    /// 没有专属时长的模型使用默认变形时长。
    private static final int TRANSITION_DURATION_TICKS = 30;
    private static final String TRANSFORMED_TAG = "Transformed";
    private static final String TRANSITION_TICKS_TAG = "TransitionTicks";

    private static final RawAnimation PHASE_ONE = RawAnimation.begin().thenLoop("type_1");
    private static final RawAnimation PHASE_ONE_DASH = RawAnimation.begin().thenLoop("type_1_run");
    private static final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("switching").thenLoop("type_2");
    private static final RawAnimation PHASE_TWO = RawAnimation.begin().thenLoop("type_2");
    private static final RawAnimation PHASE_TWO_DASH = RawAnimation.begin().thenLoop("type_2_run");
    private boolean settlingDeath;
    private ParticleEmitter linkEmitter;
    private int transitionTicks;
    private int leavingTicks;
    private float yawVelocity;
    private float pitchVelocity;
    private float previousBank;
    private float visualBank;

    private UUID sampledTarget;
    private Vec3 previousTargetPosition;
    private Vec3 targetMotion = Vec3.ZERO;
    private long motionSampleTick = Long.MIN_VALUE;

    protected AbstractTwinEye(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
        moveControl = new FlyingMoveControl(this, 10, false);
        lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {return false;}
        };
        bossEvent.setName(Component.translatable("entity.confluence.the_twins"));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ENCOUNTER_UUID, Optional.empty());
        builder.define(DATA_PRIMARY, true);
        builder.define(DATA_TRANSFORMED, false);
        builder.define(DATA_DASHING, false);
        builder.define(DATA_TRANSITION_TICKS, 0);
        builder.define(DATA_BANK, 0.0F);
    }

    public final void setEncounterUUID(UUID id) {
        entityData.set(ENCOUNTER_UUID, Optional.of(id));
        entityData.set(DATA_PRIMARY, isRetinazer());
        bossEvent.setVisible(isRetinazer());
    }

    public final @Nullable UUID getEncounterUUID() {
        return entityData.get(ENCOUNTER_UUID).orElse(null);
    }

    public final @Nullable AbstractTwinEye getPartner() {
        if (getEncounterUUID() == null) return null;
        if (level() instanceof ServerLevel) return TwinEyeEncounter.state(this).partner(this);
        return level().getEntitiesOfClass(AbstractTwinEye.class, getBoundingBox().inflate(PARTNER_SEARCH_RANGE),
                eye -> eye != this && eye.isRetinazer() != isRetinazer() && eye.isAlive()
                        && getEncounterUUID().equals(eye.getEncounterUUID())).stream().findFirst().orElse(null);
    }

    public final boolean ownsSharedBossBar() {return entityData.get(DATA_PRIMARY);}

    @Override
    public boolean shouldShowMessage() {return settlingDeath || isAlive() && ownsSharedBossBar();}

    @Override
    public Component getDisplayName() {
        return settlingDeath || getEncounterUUID() != null && tickCount == 0 ? Component.translatable("entity.confluence.the_twins") : super.getDisplayName();
    }

    @Override
    public boolean allowsMoneyDrops() {return settlingDeath;}

    @Override
    protected boolean shouldDropLoot() {return settlingDeath;}

    @Override
    public boolean isNoGravity() {return true;}

    private void refreshBossBar(TwinEyeEncounter.State state) {
        boolean primary = state.isPrimary(isRetinazer());
        if (ownsSharedBossBar() == primary) return;
        entityData.set(DATA_PRIMARY, primary);
        bossEvent.setVisible(primary);
    }

    /// 双眼均处于一阶段时每刻只选一次目标，不因实体更新顺序或各自距离而分头追击。
    @Override
    protected @Nullable LivingEntity selectCombatTarget(@Nullable LivingEntity previous) {
        AbstractTwinEye partner = getPartner();
        if (isTransformed() || partner == null || !partner.isAlive() || partner.isTransformed()) {
            return super.selectCombatTarget(previous);
        }
        TwinEyeEncounter.State encounter = TwinEyeEncounter.state(this);
        if (encounter.targetSelectionTick != level().getGameTime()) {
            AbstractTwinEye selector = isRetinazer() ? this : partner;
            LivingEntity selected = selector.selectIndependentTarget(selector.getTarget());
            encounter.sharedTarget = selected == null ? null : selected.getUUID();
            encounter.targetSelectionTick = level().getGameTime();
        }
        return encounter.sharedTarget != null
                && ((ServerLevel) level()).getEntity(encounter.sharedTarget) instanceof LivingEntity target
                && target.isAlive() && !target.isRemoved() ? target : null;
    }

    private @Nullable LivingEntity selectIndependentTarget(@Nullable LivingEntity previous) {
        return super.selectCombatTarget(previous);
    }

    @Override
    protected boolean shouldMaintainCombatTarget() {
        return leavingTicks == 0 && !level().isDay();
    }

    @Override
    protected double combatAnchorDistanceSqr(LivingEntity target) {
        AbstractTwinEye partner = getPartner();
        return partner != null && partner.isAlive()
                ? Math.min(distanceToSqr(target), partner.distanceToSqr(target)) : distanceToSqr(target);
    }

    @Override
    protected boolean shouldDiscardWhenNoTarget() {
        AbstractTwinEye partner = getPartner();
        return partner == null || !partner.isAlive() || !partner.hasEncounterObserverNearby();
    }

    @Override
    protected float getBossBarProgress() {
        if (getEncounterUUID() == null || level().isClientSide) return super.getBossBarProgress();
        TwinEyeEncounter.State state = TwinEyeEncounter.state(this);
        return state.maximumHealth() > 0 ? state.health() / state.maximumHealth() : 0;
    }

    // 两眼共享样式，血量段顺序固定为激光眼、魔焰眼。
    @Override
    protected ResourceLocation getBossBarStyle() {
        return Confluence.asResource("the_twins");
    }

    // 死亡眼保持零血量，不挤占存活眼的显示长度。
    @Override
    protected List<Health> getBossBarHealth(float health, float maximumHealth) {
        if (getEncounterUUID() == null) {
            Health current = new Health(health, maximumHealth);
            Health absent = new Health(0, 0);
            return isRetinazer() ? List.of(current, absent) : List.of(absent, current);
        }
        TwinEyeEncounter.State state = TwinEyeEncounter.state(this);
        return List.of(new Health(state.laserHealth, state.laserMaximum), new Health(state.flameHealth, state.flameMaximum));
    }

    @Override
    protected float getBossBarMaximumHealth() {
        return getEncounterUUID() != null && !level().isClientSide
                ? TwinEyeEncounter.state(this).maximumHealth() : super.getBossBarMaximumHealth();
    }

    public final boolean isTransformed() {
        return entityData.get(DATA_TRANSFORMED);
    }

    @Override
    protected final BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                /// 两只眼睛各自维护明确战斗状态，通用行为树不得再并行覆盖冲刺或
                /// 弹幕速度。
                return new WaitAction(100);
            }
        };
    }

    @Override
    public void tick() {
        if (!level().isClientSide && isAlive() && getEncounterUUID() != null) {
            TwinEyeEncounter.State encounter = TwinEyeEncounter.state(this);
            if (encounter.abandoned) {
                discard();
                return;
            }
            refreshBossBar(encounter);
        }
        super.tick();
        if (level().isClientSide) {
            if (isRetinazer() && getEncounterUUID() != null && (linkEmitter == null || linkEmitter.isRemoved())) {
                linkEmitter = new TwinEyeLinkEmitter(this);
                MolangParticleEngine.INSTANCE.addEmitter(linkEmitter);
            }
            previousBank = visualBank;
            visualBank = Mth.lerp(BANK_SMOOTHING, visualBank, entityData.get(DATA_BANK));
        }
        if (level().isClientSide || !isAlive()) {
            return;
        }
        if (level().isDay() || leavingTicks > 0) {
            leavingTicks++;
            resetCombatCycle();
            setDeltaMovement(0, LEAVING_SPEED, 0);
            turnWithInertia(new Vec3(0, 1, 0));
            if (leavingTicks >= LEAVING_TICKS) discard();
            return;
        }
        // 变形属性由同步形态恢复，存档重载不重复叠加，也不覆盖注册基础值。
        setSpecialState(Form.TRANSFORMED, isTransformed());
        if (isDisengageRetreating()) {
            resetCombatCycle();
            setDeltaMovement(0.0D, IDLE_RISE_SPEED, 0.0D);
            entityData.set(DATA_DASHING, false);
            entityData.set(DATA_BANK, 0.0F);
            return;
        }

        if (!isTransformed() && getHealth() < getMaxHealth() * (legendaryCombat() ? LEGENDARY_PHASE_TWO_HEALTH_RATIO : PHASE_TWO_HEALTH_RATIO)) {
            entityData.set(DATA_TRANSFORMED, true);
            transitionTicks = transformationDuration();
            entityData.set(DATA_TRANSITION_TICKS, transitionTicks);
            resetCombatCycle();
        }
        if (transitionTicks > 0) {
            transitionTicks--;
            entityData.set(DATA_TRANSITION_TICKS, transitionTicks);
            setDeltaMovement(Vec3.ZERO);
            entityData.set(DATA_DASHING, false);
            entityData.set(DATA_BANK, 0.0F);
            return;
        }
        if (getTarget() == null || !getTarget().isAlive()) {
            resetCombatCycle();
            suspendCombatVisuals();
            setDeltaMovement(getDeltaMovement().scale(SUSPENDED_DRAG));
            entityData.set(DATA_DASHING, false);
            entityData.set(DATA_BANK, 0.0F);
            return;
        }
        sampleTargetMotion(getTarget());
        tickTwinCombat();
        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            turnCombatDirection(target);
        }
        entityData.set(DATA_DASHING, isDashCombatState());
    }

    /// 发射口随模型朝向和缩放移动，不能用瞄准方向代替眼球当前朝向。
    public final Vec3 getMuzzlePosition(float partialTick) {
        float pitch = Mth.rotLerp(partialTick, xRotO, getXRot());
        float yaw = Mth.rotLerp(partialTick, yBodyRotO, yBodyRot);
        return getPosition(partialTick).add(0, getBbHeight() * 0.5, 0)
                .add(Vec3.directionFromRotation(pitch, yaw).scale(getMuzzleDistance() * getScale()));
    }

    protected abstract double getMuzzleDistance();

    /// 只根据近期位移预判，切换目标、传送和重新进入战斗时不沿用旧速度。
    private void sampleTargetMotion(LivingEntity target) {
        long now = level().getGameTime();
        if (!target.getUUID().equals(sampledTarget) || motionSampleTick != now - 1) {
            targetMotion = Vec3.ZERO;
        } else {
            Vec3 movement = target.position().subtract(previousTargetPosition);
            targetMotion = movement.lengthSqr() > TELEPORT_SAMPLE_DISTANCE_SQUARED ? Vec3.ZERO
                    : targetMotion.lerp(limitLength(movement, MAX_SAMPLED_TARGET_SPEED), MOTION_SMOOTHING);
        }
        sampledTarget = target.getUUID();
        previousTargetPosition = target.position();
        motionSampleTick = now;
    }

    protected final Vec3 predictTargetPosition(LivingEntity target, double ticks, double maximumLead) {
        return target.getEyePosition().add(limitLength(targetMotion.scale(ticks), maximumLead));
    }

    // 用近期平滑速度迭代估算拦截点，计入出手延迟；限制时间和提前量，传送后不追溯旧速度。
    protected final Vec3 predictInterceptPosition(LivingEntity target, Vec3 origin, double speed, double delay, double maximumLead) {
        double flightTicks = origin.distanceTo(target.getEyePosition()) / speed;
        Vec3 intercept = target.getEyePosition();
        for (int i = 0; i < INTERCEPT_ITERATIONS; i++) {
            intercept = predictTargetPosition(target, Math.min(MAX_PREDICTION_TICKS, flightTicks + delay), maximumLead);
            flightTicks = origin.distanceTo(intercept) / speed;
        }
        return intercept;
    }

    protected final boolean hasFiringLine(Vec3 origin, LivingEntity target) {
        return level().clip(new ClipContext(origin, target.getEyePosition(),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    /// 常规走位保持间距，冲刺仍沿锁定方向飞行，不被避让力扭弯。
    protected final Vec3 separateFromPartner(Vec3 velocity, double maximumSpeed) {
        AbstractTwinEye partner = getPartner();
        if (partner != null && partner.isAlive() && !partner.isDashCombatState()) {
            Vec3 away = position().subtract(partner.position());
            double distance = away.length();
            if (distance < PARTNER_SEPARATION_DISTANCE) {
                if (distance < 1.0E-4) away = new Vec3(isRetinazer() ? 1 : -1, 0, 0);
                velocity = velocity.add(away.normalize().scale((PARTNER_SEPARATION_DISTANCE - distance) * PARTNER_SEPARATION_STRENGTH));
            }
        }
        return limitLength(velocity, maximumSpeed);
    }

    protected static Vec3 limitLength(Vec3 vector, double maximum) {
        return vector.lengthSqr() > maximum * maximum ? vector.normalize().scale(maximum) : vector;
    }

    protected final boolean sharesFirstPhaseTarget(AbstractTwinEye partner) {
        return partner != null && partner.isAlive() && !isTransformed() && !partner.isTransformed()
                && getTarget() != null && partner.getTarget() == getTarget();
    }

    protected abstract void tickTwinCombat();

    protected void suspendCombatVisuals() {}

    protected int transformationDuration() {return TRANSITION_DURATION_TICKS;}

    public final float getVisualBank(float partialTick) {return Mth.lerp(partialTick, previousBank, visualBank);}

    /// 用角速度收束大转角，并让转弯产生轻微侧倾；位置和方向都不会瞬间吸附目标。
    private void turnWithInertia(Vec3 direction) {
        if (direction.lengthSqr() < 1.0E-7) return;
        float yaw = (float) (Mth.atan2(-direction.x, direction.z) * Mth.RAD_TO_DEG);
        float pitch = (float) (Mth.atan2(-direction.y, direction.horizontalDistance()) * Mth.RAD_TO_DEG);
        float yawError = Mth.wrapDegrees(yaw - getYRot());
        float pitchError = pitch - getXRot();
        yawVelocity = Mth.approach(yawVelocity, Math.clamp(yawError * TURN_RESPONSE, -MAX_YAW_SPEED, MAX_YAW_SPEED), YAW_ACCELERATION);
        pitchVelocity = Mth.approach(pitchVelocity, Math.clamp(pitchError * TURN_RESPONSE, -MAX_PITCH_SPEED, MAX_PITCH_SPEED), PITCH_ACCELERATION);
        float yawStep = Math.abs(yawError) < Math.abs(yawVelocity) ? yawError : yawVelocity;
        float pitchStep = Math.abs(pitchError) < Math.abs(pitchVelocity) ? pitchError : pitchVelocity;
        faceCombatDirection(Vec3.directionFromRotation(getXRot() + pitchStep, getYRot() + yawStep), 360, 360);
        entityData.set(DATA_BANK, Mth.clamp(-yawStep * BANK_RESPONSE, -MAX_BANK_DEGREES, MAX_BANK_DEGREES));
    }

    // 常规转向保留惯性；特殊攻击可让身体与发射口共同执行旋转。
    protected void turnCombatDirection(LivingEntity target) {
        turnWithInertia(getCombatLookDirection(target));
    }

    protected Vec3 getCombatLookDirection(LivingEntity target) {
        return target.getEyePosition().subtract(getEyePosition());
    }

    protected final boolean legendaryCombat() {
        return LibUtils.switchByDifficulty(level(), false, false, false, true);
    }

    protected static void saveVector(CompoundTag tag, String key, Vec3 vector) {
        tag.putDouble(key + "X", vector.x);
        tag.putDouble(key + "Y", vector.y);
        tag.putDouble(key + "Z", vector.z);
    }

    protected static Vec3 loadVector(CompoundTag tag, String key) {
        return new Vec3(tag.getDouble(key + "X"), tag.getDouble(key + "Y"), tag.getDouble(key + "Z"));
    }

    protected abstract boolean isRetinazer();

    /// 返回服务端战斗状态是否正处于冲刺阶段。
    ///
    /// 战斗状态机仍由具体眼睛维护，这里只同步渲染所需的最小布尔状态，避免把整个
    /// 攻击计时器暴露给客户端。
    protected abstract boolean isDashCombatState();

    protected abstract void resetCombatCycle();

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide && getEncounterUUID() != null)
            TwinEyeEncounter.state(this);
        return hurt;
    }

    /// 双子魔眼共用阶段动画，但各自从自己的资源文件读取同名动画键。
    ///
    /// 达到本体变形阈值后完整播放一次变形，再进入二阶段循环；冲刺状态由服务端同步，确保
    /// 多人客户端看到的动画与真实伤害窗口一致，而不是根据可能有插值误差的速度猜测。
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "combat", 5, state -> {
            if (!isTransformed()) {
                return state.setAndContinue(entityData.get(DATA_DASHING) ? PHASE_ONE_DASH : PHASE_ONE);
            }
            if (entityData.get(DATA_TRANSITION_TICKS) > 0) {
                return state.setAndContinue(TRANSFORM);
            }
            return state.setAndContinue(entityData.get(DATA_DASHING) ? PHASE_TWO_DASH : PHASE_TWO);
        }));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof AbstractTwinEye)
                && super.canAttack(target);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && getEncounterUUID() != null) {
            TwinEyeEncounter.State encounter = TwinEyeEncounter.state(this);
            settlingDeath = encounter.recordDeath(isRetinazer());
            AbstractTwinEye partner = encounter.partner(this);
            if (partner != null && partner.isAlive()) {
                partner.mergeEncounterParticipants(encounter.participants, encounter.mayhemParticipants);
                partner.refreshBossBar(encounter);
            }
        }
        xpReward = settlingDeath ? 2000 : 0;
        removeAllBossBarPlayers();
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getEncounterUUID() != null) tag.putUUID("TwinEncounter", getEncounterUUID());
        tag.putBoolean(TRANSFORMED_TAG, isTransformed());
        tag.putInt(TRANSITION_TICKS_TAG, transitionTicks);
        tag.putInt("LeavingTicks", leavingTicks);
        tag.putFloat("YawVelocity", yawVelocity);
        tag.putFloat("PitchVelocity", pitchVelocity);
        saveTwinCombat(tag);
    }

    protected void saveTwinCombat(CompoundTag tag) {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TwinEncounter")) setEncounterUUID(tag.getUUID("TwinEncounter"));
        entityData.set(DATA_TRANSFORMED, tag.getBoolean(TRANSFORMED_TAG));
        transitionTicks = Mth.clamp(tag.getInt(TRANSITION_TICKS_TAG), 0, transformationDuration());
        yawVelocity = Math.clamp(tag.getFloat("YawVelocity"), -MAX_YAW_SPEED, MAX_YAW_SPEED);
        pitchVelocity = Math.clamp(tag.getFloat("PitchVelocity"), -MAX_PITCH_SPEED, MAX_PITCH_SPEED);
        entityData.set(DATA_TRANSITION_TICKS, transitionTicks);
        settlingDeath = false;
        leavingTicks = Math.max(0, tag.getInt("LeavingTicks"));
        resetCombatCycle();
        loadTwinCombat(tag);
    }

    protected void loadTwinCombat(CompoundTag tag) {}

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && reason.shouldDestroy() && getEncounterUUID() != null)
            TwinEyeEncounter.removed(this);
        super.remove(reason);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    public enum Form {TRANSFORMED}
}
