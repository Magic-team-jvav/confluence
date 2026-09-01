package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.EnumMap;
import java.util.List;

/// 本源末影龙的服务端权威战斗实现。
///
/// 当前战斗包含平滑追踪与惯性俯冲、短时头部激光、概率着陆，
/// 以及两轮游走后发射龙息弹。攻击循环由可保存的显式状态机维护，
/// 避免重载后攻击循环、着陆状态或飞行目标丢失。
///
/// 主体负责渲染、生命、Boss 条、目标、移动和奖励；头、身体、三节尾巴和双翼
/// 使用七个无渲染临时部件提供真实受击区域。部件不保存且不独立结算奖励，主体重载后
/// 按槽位补齐，主动撤离或死亡时统一清理。
public final class PrimeEnderDragon extends BaseBoss {
    private static final EntityDataAccessor<Integer> DATA_COMBAT_STATE = SynchedEntityData.defineId(PrimeEnderDragon.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_LASER_ACTIVE = SynchedEntityData.defineId(PrimeEnderDragon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LANDING = SynchedEntityData.defineId(PrimeEnderDragon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_LASER_RANGE = SynchedEntityData.defineId(PrimeEnderDragon.class, EntityDataSerializers.INT);

    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation LAND = RawAnimation.begin().thenPlayAndHold("down");

    private static final String STATE_TAG = "CombatState";
    private static final String STATE_TICKS_TAG = "CombatStateTicks";
    private static final String FIRE_ROUNDS_TAG = "FireRounds";
    private static final String WANDER_DURATION_TAG = "WanderDuration";
    private static final String TARGET_X_TAG = "FlightTargetX";
    private static final String TARGET_Y_TAG = "FlightTargetY";
    private static final String TARGET_Z_TAG = "FlightTargetZ";
    private static final String HAS_TARGET_TAG = "HasFlightTarget";

    private static final double MAXIMUM_FLIGHT_SPEED = 0.9;
    private static final double FLIGHT_ACCELERATION = 0.08;
    private static final double LASER_MAXIMUM_RANGE = 20.0;
    private static final double LASER_RADIUS = 1.5;
    private static final int OPENING_WAIT_TICKS = 20;
    private static final int TARGET_REFRESH_TICKS = 20;
    private static final int INERTIA_TICKS = 20;
    private static final int POST_DASH_WANDER_TICKS = 20;
    private static final int LAND_APPROACH_TIMEOUT = 100;
    private static final int LAND_TICKS = 50;
    private static final int SKIPPED_LANDING_WAIT_TICKS = 20;
    private static final int CONTACT_INTERVAL = 20;
    private static final double APPROACH_ANGLE = Math.PI / 4.0;
    private static final double LASER_ANGLE = Math.PI / 6.0;

    private final EnumMap<PartSlot, PrimeEnderDragonPart> parts = new EnumMap<>(PartSlot.class);
    private CombatState combatState = CombatState.OPENING_WAIT;
    private int combatStateTicks;
    private int fireRounds;
    private int wanderFireDuration = 120;
    private int idleWanderTicks;
    private int idleWanderDuration;
    private int laserChargeTicks;
    private Vec3 flightTarget;
    private boolean hadCombatTarget;

    public PrimeEnderDragon(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
        noCulling = true;
        xpReward = 5000;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 4624.0)
                .add(Attributes.ATTACK_DAMAGE, 32.0)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 300.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_COMBAT_STATE, CombatState.OPENING_WAIT.ordinal());
        entityData.define(DATA_LASER_ACTIVE, false);
        entityData.define(DATA_LANDING, false);
        entityData.define(DATA_LASER_RANGE, 0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PURPLE;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                // 专用状态机负责全部移动，行为树不得覆盖飞行速度。
                return new WaitAction(Integer.MAX_VALUE);
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void setNoAi(boolean noAi) {
        super.setNoAi(noAi);
        /// 本源末影龙的头部、身体、尾巴和双翼碰撞框是本体战斗能力的一部分，
        /// 不能因为调试、暂停或加载流程临时关闭 AI 就延后创建。
        /// 部件改为可重建实体后，仍要保持“需要时始终可用”的语义。
        ensureParts();
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) {
            return;
        }
        if (level().isClientSide) {
            return;
        }

        ensureParts();
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            if (hadCombatTarget) {
                resetCombatCycle();
            }
            hadCombatTarget = false;
            setLaserActive(false);
            updateIdleWander();
            updateFlight();
            updateLaserAttack();
            return;
        }

        if (!hadCombatTarget) {
            resetCombatCycle();
        }
        hadCombatTarget = true;
        advanceCombatState(target);
        updateFlight();
        updateLaserAttack();
        for (PrimeEnderDragonPart part : List.copyOf(parts.values())) {
            if (!part.isRemoved()) updatePartPosition(part);
        }
        updatePartContactDamage();
    }

    private void advanceCombatState(LivingEntity target) {
        if (flightTarget == null) {
            flightTarget = target.position().add(0.0D, 6.0D, 0.0D);
        }
        combatStateTicks++;
        switch (combatState) {
            case OPENING_WAIT -> {
                if (combatStateTicks >= OPENING_WAIT_TICKS) {
                    enterState(CombatState.APPROACH);
                }
            }
            case APPROACH -> {
                if (combatStateTicks % TARGET_REFRESH_TICKS == 0) {
                    flightTarget = target.position();
                    if (isWithinAttackCone(target, LASER_ANGLE, 20.0) && random.nextInt(6) < 5) {
                        setLaserActive(true);
                    }
                }
                if (isWithinAttackCone(target, APPROACH_ANGLE, 12.0)) {
                    enterState(CombatState.INERTIA);
                }
            }
            case INERTIA -> {
                if (combatStateTicks >= INERTIA_TICKS) {
                    chooseWanderTarget();
                    enterState(CombatState.POST_DASH_WANDER);
                }
            }
            case POST_DASH_WANDER -> {
                if (combatStateTicks >= POST_DASH_WANDER_TICKS) {
                    setLaserActive(false);
                    if (random.nextInt(11) < 10) {
                        enterState(CombatState.LAND_APPROACH);
                    } else {
                        enterState(CombatState.SKIPPED_LANDING_WAIT);
                    }
                }
            }
            case LAND_APPROACH -> {
                if (isNearFlightTarget(10.0)) {
                    setLanding(true);
                    enterState(CombatState.LAND);
                } else if (combatStateTicks >= LAND_APPROACH_TIMEOUT) {
                    resetCombatCycle();
                } else if (combatStateTicks % TARGET_REFRESH_TICKS == 0) {
                    flightTarget = target.position();
                }
            }
            case LAND -> {
                if (onGround()) {
                    setDeltaMovement(Vec3.ZERO);
                }
                if (combatStateTicks >= LAND_TICKS) {
                    setLanding(false);
                    startWanderFire();
                }
            }
            case SKIPPED_LANDING_WAIT -> {
                if (combatStateTicks >= SKIPPED_LANDING_WAIT_TICKS) {
                    startWanderFire();
                }
            }
            case WANDER_FIRE -> {
                if (combatStateTicks >= wanderFireDuration || isNearFlightTarget(10.0)) {
                    shootDragonFireball(target);
                    fireRounds++;
                    if (fireRounds >= 2) {
                        fireRounds = 0;
                        enterState(CombatState.OPENING_WAIT);
                    } else {
                        startWanderFire();
                    }
                }
            }
        }
    }

    private void enterState(CombatState state) {
        combatState = state;
        combatStateTicks = 0;
        entityData.set(DATA_COMBAT_STATE, state.ordinal());
    }

    private void resetCombatCycle() {
        setLanding(false);
        setLaserActive(false);
        fireRounds = 0;
        enterState(CombatState.OPENING_WAIT);
    }

    private void startWanderFire() {
        setLanding(false);
        chooseWanderTarget();
        wanderFireDuration = 100 + random.nextInt(101);
        enterState(CombatState.WANDER_FIRE);
    }

    private void chooseWanderTarget() {
        flightTarget = AirRandomPos.getPosTowards(this, 100, 30, 5, blockPosition().getBottomCenter(), Mth.PI * 0.1F);
    }

    private void updateIdleWander() {
        idleWanderTicks++;
        if (flightTarget == null || isNearFlightTarget(10.0) || idleWanderTicks >= idleWanderDuration) {
            chooseWanderTarget();
            idleWanderTicks = 0;
            idleWanderDuration = 100 + random.nextInt(101);
        }
    }

    private boolean isNearFlightTarget(double distance) {
        return flightTarget != null
                && getBoundingBox().getCenter().distanceToSqr(flightTarget)
                < distance * distance;
    }

    private boolean isWithinAttackCone(LivingEntity target, double maximumAngle, double maximumDistance) {
        if (distanceToSqr(target) > maximumDistance * maximumDistance) {
            return false;
        }
        Vec3 velocity = getDeltaMovement();
        Vec3 targetDirection = target.position().subtract(position());
        if (velocity.lengthSqr() < 1.0E-8 || targetDirection.lengthSqr() < 1.0E-8) {
            return false;
        }
        double cosine = Mth.clamp(velocity.dot(targetDirection) / Math.sqrt(velocity.lengthSqr() * targetDirection.lengthSqr()), -1.0, 1.0);
        return Math.acos(cosine) < maximumAngle;
    }

    private void updateFlight() {
        if (flightTarget == null) {
            return;
        }

        Vec3 offset = flightTarget.subtract(getBoundingBox().getCenter());
        if (offset.lengthSqr() > 1.0E-6) {
            Vec3 velocity = getDeltaMovement().scale(0.94).add(offset.normalize().scale(FLIGHT_ACCELERATION));
            if (velocity.lengthSqr() > MAXIMUM_FLIGHT_SPEED * MAXIMUM_FLIGHT_SPEED) {
                velocity = velocity.normalize().scale(MAXIMUM_FLIGHT_SPEED);
            }
            setDeltaMovement(velocity);
            hasImpulse = true;
        }
        updateRotationFromVelocity(combatState == CombatState.INERTIA ? 2.0F : 7.0F);
    }

    private void updateRotationFromVelocity(float yawStep) {
        Vec3 velocity = getDeltaMovement();
        double horizontal = velocity.horizontalDistance();
        if (velocity.lengthSqr() < 1.0E-6) {
            return;
        }
        float targetYaw = (float) (Mth.atan2(-velocity.x, velocity.z) * Mth.RAD_TO_DEG);
        float targetPitch = (float) (-Mth.atan2(velocity.y, horizontal) * Mth.RAD_TO_DEG);
        setYRot(Mth.approachDegrees(getYRot(), targetYaw, yawStep));
        setXRot(Mth.approachDegrees(getXRot(), targetPitch, 4.0F));
        setYHeadRot(getYRot());
        yBodyRot = getYRot();
    }

    private void setLanding(boolean landing) {
        entityData.set(DATA_LANDING, landing);
        setNoGravity(!landing);
        noPhysics = !landing;
    }

    public boolean isLanding() {
        return entityData.get(DATA_LANDING);
    }

    private void setLaserActive(boolean active) {
        entityData.set(DATA_LASER_ACTIVE, active);
    }

    public boolean isLaserActive() {
        return entityData.get(DATA_LASER_ACTIVE);
    }

    private void updateLaserAttack() {
        double attackRange = easeLaserRange(laserChargeTicks);
        if (attackRange > 3.0) {
            performLaserAttack(attackRange);
        }
        laserChargeTicks = Mth.clamp(laserChargeTicks + (isLaserActive() ? 1 : -1), 0, (int) LASER_MAXIMUM_RANGE);
        entityData.set(DATA_LASER_RANGE, laserChargeTicks);
    }

    static float easeLaserRange(int chargeTicks) {
        float clamped = Mth.clamp(chargeTicks, 0, (int) LASER_MAXIMUM_RANGE);
        return clamped * clamped / (float) LASER_MAXIMUM_RANGE;
    }

    int performLaserAttack(double range) {
        Vec3 start = getHeadPosition();
        Vec3 direction = getViewVector(1.0F).normalize();
        Vec3 end = start.add(direction.scale(Math.min(range, LASER_MAXIMUM_RANGE)));
        AABB area = new AABB(start, end).inflate(LASER_RADIUS);
        int hits = 0;
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, area, entity -> entity != this && canAttack(entity))) {
            if (distanceToSegmentSqr(entity.getBoundingBox().getCenter(), start, end) > LASER_RADIUS * LASER_RADIUS) {
                continue;
            }
            if (entity.hurt(LibDamageTypes.of(level(), DamageTypes.MAGIC, this), 5.0F)) {
                entity.setRemainingFireTicks(100);
                hits++;
            }
        }
        return hits;
    }

    /// 返回客户端绘制激光所需的权威长度。
    ///
    /// 长度由服务端逐 tick 增减并通过实体数据同步，客户端不得自行计时，
    /// 否则网络抖动或暂停会让可见光束与真实伤害距离分离。
    public float getLaserRange() {
        return easeLaserRange(entityData.get(DATA_LASER_RANGE));
    }

    /// 计算当前头部激光的世界坐标起点。
    ///
    /// 临时碰撞部件可能比主体晚一包到达客户端，因此渲染起点只依赖主体
    /// 姿态；服务端伤害仍优先使用真实头部碰撞箱中心。
    public Vec3 getLaserOrigin(float partialTick) {
        double x = Mth.lerp(partialTick, xo, getX());
        double y = Mth.lerp(partialTick, yo, getY());
        double z = Mth.lerp(partialTick, zo, getZ());
        float pitch = Mth.rotLerp(partialTick, xRotO, getXRot());
        float yaw = Mth.rotLerp(partialTick, yRotO, getYRot());
        Vec3 forward = Vec3.directionFromRotation(pitch, yaw);
        return new Vec3(x, y, z).add(forward.scale(4.5)).add(0.0, 3.0, 0.0);
    }

    private static double distanceToSegmentSqr(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 segment = end.subtract(start);
        double lengthSqr = segment.lengthSqr();
        if (lengthSqr < 1.0E-8) {
            return point.distanceToSqr(start);
        }
        double factor = Mth.clamp(point.subtract(start).dot(segment) / lengthSqr, 0.0, 1.0);
        return point.distanceToSqr(start.add(segment.scale(factor)));
    }

    boolean shootDragonFireball(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        Vec3 start = getHeadPosition();
        Vec3 direction = target.getBoundingBox().getCenter().subtract(start).normalize();
        DragonFireball projectile = EntityType.DRAGON_FIREBALL.create(serverLevel);
        if (projectile == null) {
            return false;
        }
        projectile.setOwner(this);
        projectile.setPos(start);
        projectile.shoot(direction.x, direction.y, direction.z, 1.0F, 0.0F);
        return serverLevel.addFreshEntity(projectile);
    }

    private void updatePartContactDamage() {
        if (tickCount % CONTACT_INTERVAL != 0) {
            return;
        }
        for (PrimeEnderDragonPart part : List.copyOf(parts.values())) {
            for (net.minecraft.world.entity.Entity entity : SweptContactAttack.findTargets(part, 0.2D,
                    SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                    candidate -> candidate instanceof Player player && canAttack(player))) {
                doHurtTarget(entity);
            }
        }
    }

    @Override
    protected boolean hasEntityContactAttack() {
        // 接触区域由七个精确部件承担，主体碰撞箱不再重复结算伤害。
        return false;
    }

    private void ensureParts() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (PartSlot slot : PartSlot.values()) {
            PrimeEnderDragonPart existing = parts.get(slot);
            if (existing != null && existing.isAlive() && !existing.isRemoved()) {
                continue;
            }
            PrimeEnderDragonPart part = BossEntities.PRIME_ENDER_DRAGON_PART.get().create(serverLevel);
            if (part == null) {
                continue;
            }
            part.setPos(position());
            part.setMaster(this, slot);
            updatePartPosition(part);
            if (!serverLevel.addFreshEntity(part)) {
                parts.remove(slot);
                removeSubEntity(part);
                part.discard();
            }
        }
    }

    void bindPart(PrimeEnderDragonPart part) {
        parts.put(part.getSlot(), part);
    }

    void onPartRemoved(PrimeEnderDragonPart part) {
        parts.remove(part.getSlot(), part);
    }

    void updatePartPosition(PrimeEnderDragonPart part) {
        Vec3 forward = getViewVector(1.0F).normalize();
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0));
        if (right.lengthSqr() < 1.0E-6) {
            right = new Vec3(1.0, 0.0, 0.0);
        } else {
            right = right.normalize();
        }
        Vec3 offset = (switch (part.getSlot()) {
            case HEAD -> forward.scale(4.5).add(0.0, 2.0, 0.0);
            case BODY -> forward.scale(0.5).add(0.0, 2.0, 0.0);
            case TAIL_ONE -> forward.scale(-3.0).add(0.0, 2.0, 0.0);
            case TAIL_TWO -> forward.scale(-5.0).add(0.0, 2.0, 0.0);
            case TAIL_THREE -> forward.scale(-7.0).add(0.0, 2.0, 0.0);
            case LEFT_WING -> right.scale(4.5).add(0.0, 4.0, 0.0);
            case RIGHT_WING -> right.scale(-4.5).add(0.0, 4.0, 0.0);
        }).scale(getScale());
        part.setPos(position().add(offset));
        part.setYRot(getYRot());
        part.setXRot(getXRot());
        part.setDeltaMovement(getDeltaMovement());
    }

    private Vec3 getHeadPosition() {
        PrimeEnderDragonPart head = parts.get(PartSlot.HEAD);
        return head == null
                ? getEyePosition()
                : head.getBoundingBox().getCenter();
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(10.0F, 10.0F).scale(getScale());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(STATE_TAG, combatState.ordinal());
        tag.putInt(STATE_TICKS_TAG, combatStateTicks);
        tag.putInt(FIRE_ROUNDS_TAG, fireRounds);
        tag.putInt(WANDER_DURATION_TAG, wanderFireDuration);
        tag.putBoolean(HAS_TARGET_TAG, flightTarget != null);
        if (flightTarget != null) {
            tag.putDouble(TARGET_X_TAG, flightTarget.x);
            tag.putDouble(TARGET_Y_TAG, flightTarget.y);
            tag.putDouble(TARGET_Z_TAG, flightTarget.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        combatState = CombatState.fromOrdinal(tag.getInt(STATE_TAG));
        combatStateTicks = Math.max(0, tag.getInt(STATE_TICKS_TAG));
        fireRounds = Mth.clamp(tag.getInt(FIRE_ROUNDS_TAG), 0, 1);
        wanderFireDuration = Mth.clamp(tag.getInt(WANDER_DURATION_TAG), 100, 200);
        flightTarget = tag.getBoolean(HAS_TARGET_TAG)
                ? new Vec3(tag.getDouble(TARGET_X_TAG), tag.getDouble(TARGET_Y_TAG), tag.getDouble(TARGET_Z_TAG))
                : null;
        entityData.set(DATA_COMBAT_STATE, combatState.ordinal());
        setLaserActive(false);
        setLanding(combatState == CombatState.LAND);
    }

    CombatState getCombatState() {
        return combatState;
    }

    int getCombatStateTicks() {
        return combatStateTicks;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "action", 10, state -> state.setAndContinue(isLanding() ? LAND : FLY)));
    }

    enum CombatState {
        OPENING_WAIT,
        APPROACH,
        INERTIA,
        POST_DASH_WANDER,
        LAND_APPROACH,
        LAND,
        SKIPPED_LANDING_WAIT,
        WANDER_FIRE;

        static CombatState fromOrdinal(int ordinal) {
            CombatState[] values = values();
            return values[Mth.clamp(ordinal, 0, values.length - 1)];
        }
    }

    /// 七个固定碰撞槽位及其尺寸。
    public enum PartSlot {
        HEAD(2.0F, 2.0F),
        BODY(6.0F, 3.0F),
        TAIL_ONE(2.0F, 2.0F),
        TAIL_TWO(2.0F, 2.0F),
        TAIL_THREE(2.0F, 2.0F),
        LEFT_WING(7.0F, 2.0F),
        RIGHT_WING(7.0F, 2.0F);

        private final float width;
        private final float height;

        PartSlot(float width, float height) {
            this.width = width;
            this.height = height;
        }

        public float width() {
            return width;
        }

        public float height() {
            return height;
        }

        static PartSlot fromOrdinal(int ordinal) {
            PartSlot[] values = values();
            return values[Mth.clamp(ordinal, 0, values.length - 1)];
        }
    }
}
