package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.DemonEye;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 克苏鲁之眼 Boss。
///
/// 服务端显式状态机复现两阶段战斗：第一阶段在目标上方悬停并召唤仆从，
/// 每轮完成三次定向冲刺；所有难度低于 50% 生命时
/// 播放不可跳过的变身阶段并移除护甲，
/// 再进入按当前生命和难度计算冲刺次数的第二阶段。白天离场优先级最高，会立即
/// 清除目标并终止正在执行的悬停、变身或冲刺。
///
/// 战斗状态和阶段通过实体数据同步，客户端动画不读取本地生命值猜测阶段。
/// 保存时保留当前循环位置和锁定的冲刺方向，世界重载不会把第二阶段错误恢复成
/// 第一阶段，也不会在冲刺中途突然改为追踪玩家。
public class EyeOfCthulhu extends BaseBoss {
    private static final EntityDataAccessor<Integer> DATA_COMBAT_STATE = SynchedEntityData.defineId(EyeOfCthulhu.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COMBAT_STAGE = SynchedEntityData.defineId(EyeOfCthulhu.class, EntityDataSerializers.INT);

    private static final RawAnimation PHASE_ONE_IDLE = RawAnimation.begin().thenLoop("type_1");
    private static final RawAnimation PHASE_ONE_DASH = RawAnimation.begin().thenLoop("type_1_run");
    private static final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("switching");
    private static final RawAnimation PHASE_TWO_IDLE = RawAnimation.begin().thenLoop("type_2");
    private static final RawAnimation PHASE_TWO_DASH = RawAnimation.begin().thenLoop("type_2_run");

    private static final int PHASE_ONE_STARE_TICKS = 100;
    private static final int PHASE_TWO_STARE_TICKS = 60;
    // switching 动画资源长度为 1.5 秒，即 30 tick；状态时长必须覆盖完整单次播放。
    private static final int TRANSFORM_TICKS = 30;
    private static final int PHASE_ONE_WINDUP_TICKS = 20;
    private static final int PHASE_ONE_DASH_TICKS = 10;
    private static final int PHASE_TWO_WINDUP_TICKS = 10;
    private static final int PHASE_TWO_DASH_TICKS = 20;
    private static final int PHASE_ONE_DASH_COUNT = 3;
    private static final int LEAVE_DISCARD_TICKS = 100;
    private static final int PHASE_ONE_SERVANT_COOLDOWN = 20;
    private static final int TRANSFORM_SERVANT_COOLDOWN = 7;

    // 非冲刺阶段以目标眼睛上方 5 方块为悬停中心；其余速度单位均为方块/tick。
    private static final double HOVER_HEIGHT_ABOVE_TARGET = 5.0;
    private static final double PHASE_ONE_HOVER_SPEED = 0.50;
    private static final double PHASE_TWO_HOVER_SPEED = 0.75;
    private static final double PHASE_ONE_DASH_SPEED = 1.00;
    private static final double PHASE_TWO_DASH_SPEED = 1.50;
    private static final double ENHANCED_DASH_SPEED = 2.25;
    private static final float PHASE_ONE_DAMAGE = 4.0F;
    private static final float PHASE_TWO_DAMAGE = 6.0F;
    private static final float DASH_DAMAGE_FACTOR = 1.5F;
    private static final double EXPERT_BASE_HEALTH = 728.0D;

    private int stateTicks;
    private int remainingDashCount = PHASE_ONE_DASH_COUNT;
    private int servantTimer = PHASE_ONE_SERVANT_COOLDOWN;
    private int leavingTicks;
    private Vec3 lockedDashDirection = Vec3.ZERO;

    public EyeOfCthulhu(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 1000;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, EXPERT_BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, PHASE_ONE_DAMAGE)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_COMBAT_STATE, CombatState.IDLE.ordinal());
        entityData.define(DATA_COMBAT_STAGE, 1);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected BTRoot createBT() {
        /// 具体战斗由 tick 状态机推进。保留永远等待的根节点以继续遵守
        /// BaseMonster 的行为树注册契约，同时避免通用 Goal 改写锁定冲刺方向。
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
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

    /// 白天升空和主动离场期间不再维持攻击目标。
    ///
    /// 该阶段由自身状态机控制运动和销毁时机，公共遭遇层只负责把本体及仆从的目标清空。
    @Override
    protected boolean shouldMaintainCombatTarget() {
        return getCombatState() != CombatState.LEAVING && !level().isDay();
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || level().isClientSide) {
            return;
        }

        if (getCombatState() == CombatState.LEAVING || level().isDay()) {
            tickLeaving();
            return;
        }

        LivingEntity target = getTarget();
        if (target == null) {
            setCombatState(CombatState.IDLE);
            setDeltaMovement(getDeltaMovement().scale(0.85));
            faceAlongMovement(20.0F, 20.0F);
            resetAttackDamage();
            return;
        }

        if (getCombatStage() == 1 && getHealth() / getMaxHealth() < getTransformationHealthThreshold(isExpert())) {
            beginTransformation();
        }

        switch (getCombatState()) {
            case IDLE -> beginStaring();
            case STARING -> tickStaring(target);
            case DASH_WINDUP -> tickDashWindup(target);
            case DASHING -> tickDashing(target);
            case TRANSFORMING -> tickTransformation(target);
            case LEAVING -> tickLeaving();
        }
    }

    private void beginStaring() {
        stateTicks = 0;
        remainingDashCount = getCombatStage() == 1
                ? PHASE_ONE_DASH_COUNT
                : calculatePhaseTwoDashCount();
        resetAttackDamage();
        setCombatState(CombatState.STARING);
    }

    private void tickStaring(LivingEntity target) {
        /// 经典难度且目标距离较远时，有一半概率暂停二阶段凝视计时。
        /// 这会给远处玩家留下追赶窗口，但专家难度不会因此降低进攻频率。
        if (getCombatStage() != 2 || isExpert() || distanceTo(target) <= 8.0F || random.nextFloat() >= 0.5F) {
            stateTicks++;
        }
        if (getCombatStage() == 2 && isFtw()) {
            if (getHealth() / getMaxHealth() < 0.15F) {
                stateTicks++;
            }
            /// 连续调用两次带冷却的生成入口，使冷却每 tick 推进两次，
            /// 不是无冷却地直接生成两只实体。
            tickServantSummoning(PHASE_ONE_SERVANT_COOLDOWN);
            tickServantSummoning(PHASE_ONE_SERVANT_COOLDOWN);
        }
        hoverAboveTarget(target);
        if (getCombatStage() == 1) {
            tickServantSummoning(PHASE_ONE_SERVANT_COOLDOWN);
        }

        int duration = getCombatStage() == 1
                ? PHASE_ONE_STARE_TICKS : PHASE_TWO_STARE_TICKS;
        if (stateTicks >= duration) {
            beginDashWindup();
        }
    }

    private void beginDashWindup() {
        stateTicks = 0;
        lockedDashDirection = Vec3.ZERO;
        resetAttackDamage();
        setCombatState(CombatState.DASH_WINDUP);
    }

    private void tickDashWindup(LivingEntity target) {
        stateTicks++;
        if (getCombatStage() == 2 && isEnhancedDash() && random.nextFloat() < 0.3F) {
            stateTicks++;
        }
        faceTowards(target.getEyePosition(), 360.0F, 360.0F);
        setDeltaMovement(getDeltaMovement().scale(0.72).add(0.0, 0.02, 0.0));
        if (getCombatStage() == 2 && distanceToSqr(target) < 20.0) {
            Vec3 retreatDirection = position().subtract(target.position());
            if (retreatDirection.lengthSqr() > 1.0E-6) {
                setDeltaMovement(retreatDirection.normalize());
            }
        }

        int duration = getCombatStage() == 1
                ? PHASE_ONE_WINDUP_TICKS : PHASE_TWO_WINDUP_TICKS;
        if (stateTicks < duration) {
            return;
        }

        lockedDashDirection = createDashDirection(target);
        stateTicks = 0;
        setDashAttackDamage();
        setCombatState(CombatState.DASHING);
        playSound(isEnhancedDash() ? ModSoundEvents.HURRIED_ROARING.get() : ModSoundEvents.ROAR.get(), 1.0F, 1.0F);
    }

    private void tickDashing(LivingEntity target) {
        stateTicks++;
        double speed = getCombatStage() == 1
                ? PHASE_ONE_DASH_SPEED
                : isEnhancedDash()
                ? ENHANCED_DASH_SPEED
                : PHASE_TWO_DASH_SPEED;
        // 蓄力结束时已经锁定穿过玩家的方向。冲刺期间保持该方向直到时长结束，
        // 命中玩家只结算接触伤害，不能立即掉头或把这一轮冲刺提前截断。
        setDeltaMovement(lockedDashDirection.scale(speed));
        // 直接使用冲刺方向。若构造 position+direction 再交给 faceCombatPosition，
        // 该方法会从眼睛位置相减，额外减去眼高并让模型无故向下俯冲。
        faceCombatDirection(lockedDashDirection, 360.0F, 360.0F);
        int duration = getCombatStage() == 1
                ? PHASE_ONE_DASH_TICKS : PHASE_TWO_DASH_TICKS;
        boolean erraticEarlyEnd = getCombatStage() == 2
                && isEnhancedDash()
                && stateTicks > 13
                && random.nextFloat() < 0.2F;
        if (stateTicks < duration && !erraticEarlyEnd) {
            return;
        }

        resetAttackDamage();
        remainingDashCount--;
        if (remainingDashCount > 0) {
            beginDashWindup();
        } else {
            beginStaring();
        }
    }

    private void beginTransformation() {
        entityData.set(DATA_COMBAT_STAGE, 2);
        stateTicks = 0;
        servantTimer = 0;
        remainingDashCount = calculatePhaseTwoDashCount();
        lockedDashDirection = Vec3.ZERO;
        setBaseAttribute(Attributes.ARMOR, 0.0);
        /// 变身动画期间仍保留一阶段接触伤害，动画结束后再切换疯狂阶段伤害。
        setBaseAttribute(Attributes.ATTACK_DAMAGE, PHASE_ONE_DAMAGE);
        setCombatState(CombatState.TRANSFORMING);
        playSound(ModSoundEvents.HURRIED_ROARING.get(), 1.0F, 1.0F);
    }

    private void tickTransformation(LivingEntity target) {
        stateTicks++;
        faceTowards(target.getEyePosition(), 360.0F, 360.0F);
        setDeltaMovement(getDeltaMovement().scale(0.65));
        tickServantSummoning(TRANSFORM_SERVANT_COOLDOWN);
        if (stateTicks >= TRANSFORM_TICKS) {
            setBaseAttribute(Attributes.ATTACK_DAMAGE, PHASE_TWO_DAMAGE);
            beginStaring();
        }
    }

    private void tickLeaving() {
        if (getCombatState() != CombatState.LEAVING) {
            stateTicks = 0;
            leavingTicks = 0;
            resetAttackDamage();
            setCombatState(CombatState.LEAVING);
        }
        setTarget(null);
        navigation.stop();
        leavingTicks++;
        /// 白天撤离沿竖直方向快速升空。
        setDeltaMovement(0.0, 0.8, 0.0);
        faceAlongMovement(60.0F, 60.0F);
        if (leavingTicks >= LEAVE_DISCARD_TICKS) {
            discard();
        }
    }

    /// 克苏鲁之眼在所有阶段都属于真正的飞行实体。
    ///
    /// 不能只在构造器调用一次 {@link #setNoGravity(boolean)}：实体加入世界后的通用
    /// 状态恢复、微光处理以及网络标志同步都可能重新写入该标志，因此覆盖
    /// 此查询保持永久无重力，避免在水体中或没有合格目标时逐渐沉底。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    /// 把悬停目标设置在玩家上方，并使用惯性收敛避免每 tick 瞬间改向。
    private void hoverAboveTarget(LivingEntity target) {
        Vec3 destination = target.position().add(0.0, HOVER_HEIGHT_ABOVE_TARGET, 0.0);
        Vec3 offset = destination.subtract(position());
        double speed = getCombatStage() == 1
                ? PHASE_ONE_HOVER_SPEED : PHASE_TWO_HOVER_SPEED;
        if (offset.lengthSqr() > 1.0) {
            Vec3 acceleration = offset.normalize().scale(speed * 0.10);
            setDeltaMovement(getDeltaMovement().add(acceleration).scale(0.90));
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.80));
        }
        faceTowards(target.getEyePosition(), 30.0F, 30.0F);
    }

    /// 冲刺方向只在蓄力结束时计算一次。第二阶段会根据玩家当前速度加入有限预判，
    /// 低血量专家冲刺再增加随机偏差，保持泰拉瑞亚狂暴阶段“更快但不精确”的特征。
    /// 将实体真实旋转同步到目标位置。
    ///
    /// 克苏鲁之眼的 Geo 模型会读取实体的 {@code YRot/XRot} 来决定朝向和俯仰。
    /// 如果只设置 LookControl，服务端本 tick 计算出的悬停、蓄力和冲刺方向不会稳定写入同步旋转，
    /// 客户端就可能看到 Boss 贴地滑行、侧脸滑动或不正面朝向玩家。这里不改变速度、阶段时长、
    /// 召唤物和伤害，只补齐渲染所依赖的朝向数据。
    private void faceTowards(Vec3 targetPosition, float maxYawChange, float maxPitchChange) {
        faceCombatPosition(targetPosition, maxYawChange, maxPitchChange);
    }

    /// 没有明确目标时沿当前速度方向修正朝向，用于脱战离场和短暂失去目标的惯性阶段。
    private void faceAlongMovement(float maxYawChange, float maxPitchChange) {
        faceCombatMovement(maxYawChange, maxPitchChange);
    }

    private Vec3 createDashDirection(LivingEntity target) {
        Vec3 destination = target.position().add(0.0, 1.0, 0.0);
        float inaccuracy = getCombatStage() == 1
                ? 1.0F
                : (float) target.getDeltaMovement().length() * 10.0F;
        if (getCombatStage() == 2 && isEnhancedDash()) {
            inaccuracy *= 6.0F;
        }
        destination = destination.offsetRandom(random, inaccuracy);

        Vec3 direction = destination.subtract(position());
        if (direction.lengthSqr() < 1.0E-6) {
            direction = getLookAngle();
        }
        return direction.normalize();
    }

    private int calculatePhaseTwoDashCount() {
        if (!isExpert()) {
            return PHASE_ONE_DASH_COUNT;
        }
        float healthRatio = Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 0.5F);
        return Math.max(PHASE_ONE_DASH_COUNT, Mth.floor((-2.0F + 10.0F - healthRatio * 10.0F) * 1.5F));
    }

    private boolean isEnhancedDash() {
        return isExpert()
                && getHealth() / getMaxHealth() < 0.30F;
    }

    private void tickServantSummoning(int cooldown) {
        if (--servantTimer > 0) {
            return;
        }
        servantTimer = cooldown;
        spawnServant();
    }

    void spawnServant() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ServantOfCthulhu servant = BossEntities.SERVANT_OF_CTHULHU.get().create(level());
        if (servant == null) {
            return;
        }
        Vec3 backward = getLookAngle();
        if (backward.lengthSqr() < 1.0E-6) {
            backward = new Vec3(0.0, 0.0, 1.0);
        }
        servant.setPos(position().subtract(backward.normalize()));
        servant.setMaster(this);
        servant.setTarget(getTarget());
        if (!serverLevel.addFreshEntity(servant)) {
            servant.discard();
        }
    }

    private void setDashAttackDamage() {
        float baseDamage = getCombatStage() == 1
                ? PHASE_ONE_DAMAGE : PHASE_TWO_DAMAGE;
        setBaseAttribute(Attributes.ATTACK_DAMAGE, baseDamage * DASH_DAMAGE_FACTOR);
    }

    private void resetAttackDamage() {
        setBaseAttribute(Attributes.ATTACK_DAMAGE, getCombatStage() == 1 ? PHASE_ONE_DAMAGE : PHASE_TWO_DAMAGE);
    }

    /// 专家难度下，克苏鲁之眼会在极低生命值时进入负防御区间。原版护甲属性
    /// 不能表达负数，因此把泰拉瑞亚的负防御换算成每次命中的等效额外伤害。
    /// 低于 40% 时增加 15 点，低于 12% 时再增加 7 点。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        amount += getLowHealthDamageBonus(isExpert(), getHealth() / getMaxHealth());
        return super.hurt(source, amount);
    }

    /// 返回进入第二阶段的生命值比例。当前与泰拉瑞亚一致，所有难度均为 50%。
    static float getTransformationHealthThreshold(boolean expert) {
        return 0.5F;
    }

    /// 计算负防御对应的额外受伤数值。该换算只在专家及大师难度生效。
    static float getLowHealthDamageBonus(boolean expert, float healthRatio) {
        if (!expert) {
            return 0.0F;
        }
        float bonus = 0.0F;
        if (healthRatio < 0.4F) {
            bonus += 15.0F;
        }
        if (healthRatio < 0.12F) {
            bonus += 7.0F;
        }
        return bonus;
    }

    private void setBaseAttribute(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        AttributeInstance instance = getAttribute(attribute);
        if (instance == null) {
            throw new IllegalStateException("Eye of Cthulhu is missing required attribute " + attribute.getDescriptionId());
        }
        instance.setBaseValue(value);
    }

    public CombatState getCombatState() {
        int ordinal = entityData.get(DATA_COMBAT_STATE);
        CombatState[] values = CombatState.values();
        return ordinal >= 0 && ordinal < values.length
                ? values[ordinal] : CombatState.IDLE;
    }

    private void setCombatState(CombatState state) {
        entityData.set(DATA_COMBAT_STATE, state.ordinal());
    }

    public int getCombatStage() {
        return entityData.get(DATA_COMBAT_STAGE);
    }

    int getRemainingDashCount() {
        return remainingDashCount;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        /// 仆从属于恶魔眼行为族，必须显式排除同阵营实体以免互相伤害。
        /// 所有权持久化使用独立实体类型，必须同时排除该等价类型。
        return super.canAttack(target)
                && !(target instanceof DemonEye)
                && !(target instanceof ServantOfCthulhu);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 变身是一段严格的一次性拓扑切换动画，不能用 5 tick 淡入吞掉开头关键帧。
        controllers.add(new AnimationController<>(this, "Controller", 0, state -> state.setAndContinue(animationForCurrentState())));
    }

    private RawAnimation animationForCurrentState() {
        return switch (getCombatState()) {
            case TRANSFORMING -> TRANSFORM;
            case DASH_WINDUP, DASHING -> getCombatStage() == 1
                    ? PHASE_ONE_DASH : PHASE_TWO_DASH;
            case IDLE, STARING, LEAVING -> getCombatStage() == 1
                    ? PHASE_ONE_IDLE : PHASE_TWO_IDLE;
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("EyeCombatStage", getCombatStage());
        tag.putInt("EyeCombatState", getCombatState().ordinal());
        tag.putInt("EyeStateTicks", stateTicks);
        tag.putInt("EyeRemainingDashes", remainingDashCount);
        tag.putInt("EyeServantTimer", servantTimer);
        tag.putInt("EyeLeavingTicks", leavingTicks);
        tag.putDouble("EyeDashX", lockedDashDirection.x);
        tag.putDouble("EyeDashY", lockedDashDirection.y);
        tag.putDouble("EyeDashZ", lockedDashDirection.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int stage = Mth.clamp(tag.getInt("EyeCombatStage"), 1, 2);
        entityData.set(DATA_COMBAT_STAGE, stage);

        int stateOrdinal = tag.getInt("EyeCombatState");
        CombatState[] states = CombatState.values();
        setCombatState(stateOrdinal >= 0 && stateOrdinal < states.length
                ? states[stateOrdinal] : CombatState.IDLE);
        stateTicks = Math.max(0, tag.getInt("EyeStateTicks"));
        remainingDashCount = Math.max(0, tag.getInt("EyeRemainingDashes"));
        servantTimer = Math.max(0, tag.getInt("EyeServantTimer"));
        leavingTicks = Math.max(0, tag.getInt("EyeLeavingTicks"));
        lockedDashDirection = new Vec3(tag.getDouble("EyeDashX"), tag.getDouble("EyeDashY"), tag.getDouble("EyeDashZ"));

        if (stage == 2) {
            setBaseAttribute(Attributes.ARMOR, 0.0);
        }
        resetAttackDamage();
    }

    /// 客户端动画、存档和测试共同使用的稳定战斗状态。
    public enum CombatState {
        IDLE,
        STARING,
        DASH_WINDUP,
        DASHING,
        TRANSFORMING,
        LEAVING
    }
}
