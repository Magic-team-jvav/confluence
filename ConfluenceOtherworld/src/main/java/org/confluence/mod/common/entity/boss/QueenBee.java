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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.LittleHornet;
import org.confluence.mod.common.entity.projectile.HornetStingerProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 蜂王 Boss。
///
/// 战斗流程由服务端状态机统一推进：初次出现、短暂悬停、召唤黄蜂、连续发射毒刺，
/// 随后完成四轮“调整位置—蓄力—水平冲刺”。冲刺方向只在蓄力结束时计算一次，
/// 因而不会在冲刺途中自动追踪或突然转向。客户端只读取同步状态播放动画，不参与攻击判定。
///
/// 蜂王最多保有十只直属小黄蜂。上限依据明确的所有权关系统计，不会误计附近其他蜂王的
/// 随从；重复召唤和难度提高只能更快补足空位，不能突破上限。蜂王离开丛林后进入愤怒状态，
/// 专家及以上难度的冲刺速度和毒素强度随之提高。
public class QueenBee extends BaseBoss {
    private static final EntityDataAccessor<Integer> DATA_COMBAT_STATE = SynchedEntityData.defineId(QueenBee.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ANGRY = SynchedEntityData.defineId(QueenBee.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation INITIALIZATION = RawAnimation.begin().thenPlay("initialization");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SUMMON = RawAnimation.begin().thenLoop("summon");
    private static final RawAnimation PRE_DASH = RawAnimation.begin().thenPlayAndHold("pre_dash");
    private static final RawAnimation DASH = RawAnimation.begin().thenLoop("dash");
    private static final RawAnimation WING = RawAnimation.begin().thenLoop("wing");

    // 各战斗状态时长及召唤间隔，单位均为 tick。
    private static final int INITIALIZATION_TICKS = 1;
    private static final int IDLE_TICKS = 50;
    private static final int SUMMON_TICKS = 60;
    private static final int SUMMON_INTERVAL = 10;
    private static final int PRE_DASH_IDLE_TICKS = 20;
    private static final int PRE_DASH_TICKS = 15;
    private static final int DASH_MAX_TICKS = 50;
    private static final int DASH_MIN_TICKS = 20;
    // 普通模式固定三轮冲刺；专家模式可随生命降低增加，但最多六轮。
    private static final int CLASSIC_DASH_CYCLES = 3;
    private static final int EXPERT_MAX_DASH_CYCLES = 6;
    // 只统计并限制当前蜂王拥有的随从，不影响自然生成的黄蜂。
    private static final int MAX_OWNED_HORNETS = 10;

    // 状态移动速度单位为方块/tick；愤怒专家模式只对最终冲刺速度应用倍率。
    private static final double IDLE_SPEED = 1.0;
    private static final double HANG_SPEED = 1.0;
    private static final double PRE_DASH_HANG_SPEED = 1.2;
    private static final double DASH_SPEED = 2.0;
    private static final double EXPERT_ANGRY_DASH_MULTIPLIER = 1.50;
    // 冲刺越过目标且距离重新超过 15 方块后允许结束；保存平方值避免反复开方。
    private static final double DASH_END_RANGE_SQR = 15.0 * 15.0;

    private int stateTicks;
    private int completedDashCycles;
    private Vec3 idleDirection = Vec3.ZERO;
    private Vec3 lockedDashDirection = Vec3.ZERO;

    public QueenBee(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 1500;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 1237.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_COMBAT_STATE, CombatState.INITIALIZING.ordinal());
        entityData.define(DATA_ANGRY, false);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.YELLOW;
    }

    // === BT ===
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected BTRoot createBT() {
        /// 具体战斗行为完全由本类状态机管理。永久等待节点只用于满足 BaseMonster
        /// 的行为树契约，避免通用移动动作与锁定方向的冲刺相互覆盖。
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

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || level().isClientSide) {
            return;
        }

        setAngry(!level().getBiome(blockPosition()).is(Biomes.JUNGLE));
        LivingEntity target = getTarget();
        if (target == null) {
            resetCombatCycle();
            return;
        }

        switch (getCombatState()) {
            case INITIALIZING -> tickInitialization();
            case IDLE -> tickIdle(target);
            case SUMMONING_BEES -> tickSummoningBees(target);
            case SUMMONING_STINGERS -> tickSummoningStingers(target);
            case PRE_DASH_IDLE -> tickPreDashIdle(target);
            case PRE_DASH -> tickPreDash(target);
            case DASHING -> tickDashing(target);
        }
    }

    private void tickInitialization() {
        setDeltaMovement(getDeltaMovement().scale(0.75));
        if (++stateTicks >= INITIALIZATION_TICKS) {
            completedDashCycles = 0;
            enterState(CombatState.PRE_DASH_IDLE);
        }
    }

    private void tickIdle(LivingEntity target) {
        if (stateTicks == 0) {
            idleDirection = target.position().subtract(position());
            if (idleDirection.lengthSqr() > 1.0E-6) {
                idleDirection = idleDirection.normalize();
            }
        }
        stateTicks++;
        setDeltaMovement(idleDirection.scale(IDLE_SPEED));
        faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
        if (stateTicks >= IDLE_TICKS || stateTicks > 10 && distanceToSqr(target) > DASH_END_RANGE_SQR) {
            enterState(CombatState.SUMMONING_BEES);
        }
    }

    private void tickSummoningBees(LivingEntity target) {
        hangOnTarget(target, 5.0, 4.0, HANG_SPEED);
        stateTicks++;
        if (stateTicks % SUMMON_INTERVAL == 0) {
            spawnOneBee();
        }
        if (stateTicks >= SUMMON_TICKS || countOwnedHornets() >= MAX_OWNED_HORNETS) {
            enterState(CombatState.SUMMONING_STINGERS);
        }
    }

    private void tickSummoningStingers(LivingEntity target) {
        faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
        if (getY() < target.getY() + 2.0) {
            setDeltaMovement(getDeltaMovement().add(0.0, 0.02, 0.0));
        }
        stateTicks++;
        if (stateTicks % SUMMON_INTERVAL == 0) {
            spawnStinger(target);
        }
        int duration = getHealth() / getMaxHealth() < 0.30F ? 50 : SUMMON_TICKS;
        if (stateTicks >= duration) {
            completedDashCycles = 0;
            enterState(CombatState.PRE_DASH_IDLE);
        }
    }

    private void tickPreDashIdle(LivingEntity target) {
        hangOnTarget(target, 5.0, 0.0, PRE_DASH_HANG_SPEED);
        boolean needsAlignment = distanceToSqr(target) > 100.0
                || Math.abs(target.getY() - getY()) > 2.0
                || Math.abs(getXRot()) > 10.0F;
        if (!needsAlignment || !isExpert() || !random.nextBoolean()) {
            stateTicks++;
        }
        if (stateTicks >= PRE_DASH_IDLE_TICKS) {
            enterState(CombatState.PRE_DASH);
        }
    }

    private void tickPreDash(LivingEntity target) {
        setDeltaMovement(Vec3.ZERO);
        faceCombatPosition(target.getEyePosition(), 180.0F, 180.0F);
        if (++stateTicks < PRE_DASH_TICKS) {
            return;
        }

        lockedDashDirection = createHorizontalDashDirection(target);
        enterState(CombatState.DASHING);
    }

    private void tickDashing(LivingEntity target) {
        stateTicks++;
        double speed = DASH_SPEED;
        if (isAngry() && isExpert()) {
            speed *= EXPERT_ANGRY_DASH_MULTIPLIER;
        }
        Vec3 desiredDirection = target.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
        lockedDashDirection = turnDirectionToward(lockedDashDirection, desiredDirection, 3.0F)
                .multiply(1.0D, 0.0D, 1.0D).normalize();
        setDeltaMovement(lockedDashDirection.scale(speed));
        Vec3 lookPosition = position().add(lockedDashDirection);
        faceCombatDirection(lockedDashDirection, 180.0F, 180.0F);
        boolean reachedTimeLimit = stateTicks >= DASH_MAX_TICKS;
        boolean passedTargetRange = stateTicks >= DASH_MIN_TICKS
                && distanceToSqr(target) > DASH_END_RANGE_SQR;
        if (!reachedTimeLimit && !passedTargetRange) {
            return;
        }

        completedDashCycles++;
        if (completedDashCycles >= requiredDashCycles()) {
            completedDashCycles = 0;
            enterState(CombatState.IDLE);
        } else {
            enterState(CombatState.PRE_DASH_IDLE);
        }
    }

    /// 悬挂移动的加速度与实际偏移成正比，而不是先归一化成固定推力。
    /// 这样远处会快速回位，贴近目标后会自然减速，召蜂与冲刺准备阶段的速度也能分别保留。
    private void hangOnTarget(LivingEntity target, double horizontalDistance, double height, double speed) {
        Vec3 horizontal = position().subtract(target.position()).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-6) {
            horizontal = new Vec3(1.0, 0.0, 0.0);
        }
        Vec3 destination = target.position().add(horizontal.normalize().scale(horizontalDistance)).add(0.0, height, 0.0);
        Vec3 offset = destination.subtract(position());
        setDeltaMovement(getDeltaMovement().add(offset.scale(speed * 0.01)));
        if (distanceToSqr(target) < 2.0) {
            setDeltaMovement(getDeltaMovement().scale(0.95));
        }
        faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
    }

    /// 根据目标当前速度预判十刻后的位置，并丢弃垂直分量，保证整段冲刺保持水平。
    private Vec3 createHorizontalDashDirection(LivingEntity target) {
        Vec3 predictedPosition = target.position().add(target.getDeltaMovement().scale(10.0));
        Vec3 direction = predictedPosition.subtract(position()).multiply(1.0, 0.0, 1.0);
        if (direction.lengthSqr() < 1.0E-6) {
            direction = getLookAngle().multiply(1.0, 0.0, 1.0);
        }
        if (direction.lengthSqr() < 1.0E-6) {
            direction = new Vec3(1.0, 0.0, 0.0);
        }
        return direction.normalize();
    }

    private int requiredDashCycles() {
        if (!isExpert()) return CLASSIC_DASH_CYCLES;
        float lostHealth = 1.0F - Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F);
        return Mth.clamp(CLASSIC_DASH_CYCLES + Mth.floor(lostHealth * 4.0F),
                CLASSIC_DASH_CYCLES, EXPERT_MAX_DASH_CYCLES);
    }

    /// 兼容既有调用：一次补充当前难度对应数量的小黄蜂，但永远不突破十只上限。
    void spawnBees() {
        int requested = isMaster() ? 6 : isExpert() ? 4 : 2;
        int available = MAX_OWNED_HORNETS - countOwnedHornets();
        for (int index = 0; index < Math.min(requested, available); index++) {
            if (!spawnOneBee()) {
                break;
            }
        }
    }

    private boolean spawnOneBee() {
        if (!(level() instanceof ServerLevel serverLevel) || countOwnedHornets() >= MAX_OWNED_HORNETS) {
            return false;
        }
        LittleHornet hornet = MonsterEntities.LITTLE_HORNET.get().create(level());
        if (hornet == null) {
            return false;
        }
        hornet.setPos(position());
        hornet.setYRot(getYRot());
        hornet.setMaster(this);
        if (serverLevel.addFreshEntity(hornet)) {
            return true;
        }
        hornet.discard();
        return false;
    }

    private int countOwnedHornets() {
        int count = 0;
        for (var entity : subEntities) {
            if (entity instanceof LittleHornet hornet && hornet.isAlive() && hornet.getMasterUUID() != null && hornet.getMasterUUID().equals(getUUID())) {
                count++;
            }
        }
        return count;
    }

    boolean spawnStinger(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        HornetStingerProjectile projectile = ModEntities.HORNET_STINGER.get().create(level());
        if (projectile == null) {
            return false;
        }
        projectile.configure(
                this,
                position(),
                target.getEyePosition().subtract(position()),
                (float) getAttributeValue(Attributes.ATTACK_DAMAGE),
                5.0F,
                isAngry() ? 1 : 0);
        return serverLevel.addFreshEntity(projectile);
    }

    private void resetCombatCycle() {
        stateTicks = 0;
        completedDashCycles = 0;
        idleDirection = Vec3.ZERO;
        lockedDashDirection = Vec3.ZERO;
        if (getCombatState() != CombatState.INITIALIZING) {
            setCombatState(CombatState.IDLE);
        }
        setDeltaMovement(getDeltaMovement().scale(0.75));
    }

    private void enterState(CombatState state) {
        stateTicks = 0;
        setCombatState(state);
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

    public boolean isAngry() {
        return entityData.get(DATA_ANGRY);
    }

    private void setAngry(boolean angry) {
        entityData.set(DATA_ANGRY, angry);
    }

    int getCompletedDashCycles() {
        return completedDashCycles;
    }

    Vec3 getLockedDashDirection() {
        return lockedDashDirection;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof LittleHornet);
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
        controllers.add(new AnimationController<>(this, "action", 5, state -> state.setAndContinue(animationForCurrentState())), new AnimationController<>(this, "wing", 0, state -> state.setAndContinue(WING)));
    }

    private RawAnimation animationForCurrentState() {
        return switch (getCombatState()) {
            case INITIALIZING -> INITIALIZATION;
            case SUMMONING_BEES, SUMMONING_STINGERS -> SUMMON;
            case PRE_DASH -> PRE_DASH;
            case DASHING -> DASH;
            case IDLE, PRE_DASH_IDLE -> IDLE;
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("QueenCombatState", getCombatState().ordinal());
        tag.putInt("QueenStateTicks", stateTicks);
        tag.putInt("QueenCompletedDashes", completedDashCycles);
        tag.putBoolean("QueenAngry", isAngry());
        tag.putDouble("QueenIdleX", idleDirection.x);
        tag.putDouble("QueenIdleY", idleDirection.y);
        tag.putDouble("QueenIdleZ", idleDirection.z);
        tag.putDouble("QueenDashX", lockedDashDirection.x);
        tag.putDouble("QueenDashY", lockedDashDirection.y);
        tag.putDouble("QueenDashZ", lockedDashDirection.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int ordinal = tag.getInt("QueenCombatState");
        CombatState[] values = CombatState.values();
        setCombatState(ordinal >= 0 && ordinal < values.length
                ? values[ordinal] : CombatState.IDLE);
        stateTicks = Math.max(0, tag.getInt("QueenStateTicks"));
        completedDashCycles = Mth.clamp(tag.getInt("QueenCompletedDashes"), 0, EXPERT_MAX_DASH_CYCLES - 1);
        setAngry(tag.getBoolean("QueenAngry"));
        idleDirection = new Vec3(tag.getDouble("QueenIdleX"), tag.getDouble("QueenIdleY"), tag.getDouble("QueenIdleZ"));
        lockedDashDirection = new Vec3(tag.getDouble("QueenDashX"), tag.getDouble("QueenDashY"), tag.getDouble("QueenDashZ"));
    }

    public enum CombatState {
        INITIALIZING,
        IDLE,
        SUMMONING_BEES,
        SUMMONING_STINGERS,
        PRE_DASH_IDLE,
        PRE_DASH,
        DASHING
    }
}
