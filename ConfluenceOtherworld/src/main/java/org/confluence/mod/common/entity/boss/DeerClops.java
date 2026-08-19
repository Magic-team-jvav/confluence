package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.DeerclopsIcePillarProjectile;
import org.confluence.mod.common.entity.projectile.DeerclopsShadowHandProjectile;
import org.confluence.mod.common.entity.projectile.DeerclopsThrownIceProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;

/// 独眼巨鹿 Boss。
///
/// <p>战斗流程由服务端状态机统一驱动：首次锁定玩家时依次播放咆哮准备和持续咆哮，
/// 随后在追击与冰击之间循环。冰击根据目标位置选择抛冰、暗影之手或地面冰柱；
/// 玩家离得过远时 Boss 会停止攻击并进入无敌状态，避免远距离无风险消耗。</p>
///
/// <p>无目标期间会短暂寻找附近箱子并将其破坏，这一环境行为不会绕过
/// {@link BaseBoss} 的统一脱战计时。所有计时和弹幕生成都只在服务端执行，
/// 客户端仅根据同步状态选择动画和无敌纹理。</p>
public class DeerClops extends BaseBoss {
    private static final EntityDataAccessor<Integer> DATA_COMBAT_STATE = SynchedEntityData.defineId(DeerClops.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FAR_INVULNERABLE = SynchedEntityData.defineId(DeerClops.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation ICE = RawAnimation.begin().thenPlay("Ice");
    private static final RawAnimation ROAR = RawAnimation.begin().thenPlay("Roar");
    private static final RawAnimation ROARING = RawAnimation.begin().thenLoop("Roaring");

    private static final int INTRO_STAGE_TICKS = 10;
    private static final int ATTACK_WINDUP_TICKS = 12;
    private static final int ATTACK_TOTAL_TICKS = 15;
    private static final int ATTACK_COOLDOWN_TICKS = 30;
    private static final int THROWN_ICE_COUNT = 20;
    private static final int ICE_WAVE_STEPS = 10;
    private static final int CHEST_SEARCH_RADIUS = 12;
    private static final int STUCK_TICKS_BEFORE_JUMP = 8;
    private static final int TRAVERSAL_JUMP_COOLDOWN = 12;
    private static final double PREFERRED_RANGE = 7.0;
    private static final double MAXIMUM_ATTACK_RANGE = 20.0;
    private static final double THROWN_ICE_RANGE = 10.0;
    private static final double SHADOW_HAND_HEIGHT = 5.0;
    private static final double TRAVERSAL_JUMP_SPEED = 0.62;
    private static final double TRAVERSAL_FORWARD_SPEED = 0.22;
    private static final float ATTACK_DAMAGE = 10.0F;
    private static final float RANGE_DAMAGE = 10.0F;
    private static final float SHADOW_HAND_DAMAGE = 10.0F;

    private int stateTicks;
    private int attackCooldown;
    private boolean introComplete;
    private int iceWaveStep = -1;
    private Vec3 iceWaveOrigin = Vec3.ZERO;
    private Vec3 iceWaveDirection = Vec3.ZERO;
    private @Nullable BlockPos chestTarget;
    private int chestAttackTicks;
    private int traversalJumpCooldown;
    private int stuckTicks;
    private double lastChaseX;
    private double lastChaseZ;
    private boolean hasLastChasePosition;

    public DeerClops(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 1500;
        setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 3094.0)
                .add(Attributes.ATTACK_DAMAGE, 10.4)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_COMBAT_STATE, CombatState.IDLE.ordinal());
        entityData.define(DATA_FAR_INVULNERABLE, false);
    }

    @Override
    protected BTRoot createBT() {
        /// 具体战斗由 tick 中的显式状态机负责。保留一个永远等待的行为树，
        /// 是为了继续遵守 BaseMonster 的统一注册约定，同时避免原版 Goal
        /// 与状态机争抢导航、转向和攻击时序。
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
        if (traversalJumpCooldown > 0) {
            traversalJumpCooldown--;
        }

        tickIceWave();
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            tickWithoutCombatTarget();
            return;
        }

        chestTarget = null;
        chestAttackTicks = 0;
        if (!introComplete) {
            tickIntro();
            return;
        }
        tickCombat(target);
    }

    private void tickIntro() {
        navigation.stop();
        resetTraversalTracking();
        setFarInvulnerable(false);
        CombatState state = getCombatState();
        if (state != CombatState.ROAR && state != CombatState.ROARING) {
            setCombatState(CombatState.ROAR);
            return;
        }
        if (++stateTicks < INTRO_STAGE_TICKS) {
            return;
        }
        if (state == CombatState.ROAR) {
            setCombatState(CombatState.ROARING);
        } else {
            introComplete = true;
            attackCooldown = ATTACK_COOLDOWN_TICKS;
            setCombatState(CombatState.CHASE);
        }
    }

    private void tickCombat(LivingEntity target) {
        double distanceSqr = distanceToSqr(target);
        boolean outsideAttackRange = distanceSqr > MAXIMUM_ATTACK_RANGE * MAXIMUM_ATTACK_RANGE;
        setFarInvulnerable(outsideAttackRange);
        getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (getCombatState() == CombatState.ATTACK) {
            navigation.stop();
            resetTraversalTracking();
            if (++stateTicks == ATTACK_WINDUP_TICKS) {
                performIceAttack(target);
            }
            if (stateTicks >= ATTACK_TOTAL_TICKS) {
                attackCooldown = ATTACK_COOLDOWN_TICKS;
                setCombatState(CombatState.CHASE);
            }
            return;
        }

        setCombatState(CombatState.CHASE);
        if (distanceSqr > PREFERRED_RANGE * PREFERRED_RANGE) {
            boolean pathStarted = navigation.moveTo(target, 1.0);
            tryTraversalJump(target, pathStarted);
        } else {
            navigation.stop();
            resetTraversalTracking();
        }
        if (outsideAttackRange) {
            return;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        } else {
            setCombatState(CombatState.ATTACK);
        }
    }

    private AttackResult performIceAttack(LivingEntity target) {
        Vec3 horizontalOffset = target.position().subtract(position()).multiply(1.0, 0.0, 1.0);
        if (horizontalOffset.length() >= THROWN_ICE_RANGE) {
            return new AttackResult(AttackPattern.THROWN_ICE, spawnThrownIce());
        } else if (target.getY() - getY() > SHADOW_HAND_HEIGHT) {
            return new AttackResult(AttackPattern.SHADOW_HAND, spawnShadowHands(target));
        } else {
            beginIcePillarWave(horizontalOffset);
            return new AttackResult(AttackPattern.ICE_PILLAR, 0);
        }
    }

    private int spawnThrownIce() {
        int spawned = 0;
        for (int index = 0; index < THROWN_ICE_COUNT; index++) {
            DeerclopsThrownIceProjectile projectile = ModEntities.THROWN_ICE_PROJECTILE.get().create(level());
            if (projectile == null) {
                continue;
            }
            Vec3 origin = position().add(random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 2.0 + 1.0, random.nextDouble() * 2.0 - 1.0);
            projectile.configure(this, origin, RANGE_DAMAGE);
            if (level().addFreshEntity(projectile)) {
                spawned++;
            }
        }
        return spawned;
    }

    private int spawnShadowHands(LivingEntity target) {
        Vec3 center = target.position();
        double formationRotation = random.nextDouble() * Math.PI * 2.0;
        int spawned = 0;
        for (int index = 0; index < 4; index++) {
            DeerclopsShadowHandProjectile projectile = ModEntities.SHADOW_HAND.get().create(level());
            if (projectile == null) {
                continue;
            }
            Vec3 origin = findShadowHandOrigin(center, index, formationRotation, projectile);
            Vec3 direction = center.subtract(origin);
            projectile.configure(this, origin, direction, SHADOW_HAND_DAMAGE);
            if (level().addFreshEntity(projectile)) {
                spawned++;
            }
        }
        return spawned;
    }

    /// 为暗影之手寻找不会一生成就卡进方块的位置。
    ///
    /// <p>1.21 侧直接在球面上随机取点，靠近洞壁或地面时会有部分黑手立刻撞墙消失。
    /// 这里先尝试随机球面，再退回四个均匀方向并逐格上移，保证一次攻击稳定形成
    /// 四向包夹，同时仍保持方向在生成时锁定、之后不自动追踪。</p>
    private Vec3 findShadowHandOrigin(Vec3 center, int handIndex, double formationRotation, DeerclopsShadowHandProjectile projectile) {
        double formationAngle = formationRotation + handIndex * Math.PI * 0.5;
        Vec3 formationOrigin = center.add(
                Math.cos(formationAngle) * 4.5,
                handIndex % 2 == 0 ? 2.0 : -2.0,
                Math.sin(formationAngle) * 4.5);
        projectile.setPos(formationOrigin);
        if (isUsableShadowHandOrigin(projectile, formationOrigin)) {
            return formationOrigin;
        }

        for (int attempt = 0; attempt < 12; attempt++) {
            Vec3 origin = center.add(randomSphereOffset(5.0));
            projectile.setPos(origin);
            if (isUsableShadowHandOrigin(projectile, origin)) {
                return origin;
            }
        }

        double angle = handIndex * Math.PI * 0.5;
        Vec3 fallback = center.add(Math.cos(angle) * 5.0, 2.0, Math.sin(angle) * 5.0);
        for (int upward = 0; upward < 6; upward++) {
            Vec3 candidate = fallback.add(0.0, upward, 0.0);
            projectile.setPos(candidate);
            if (isUsableShadowHandOrigin(projectile, candidate)) {
                return candidate;
            }
        }

        /// 测试结构边缘或极窄洞穴中，目标所在区块可能是唯一已加载区块。
        /// 最终回退必须留在目标正上方，而不能重新返回已经判定为未加载的方位点。
        Vec3 verticalFallback = center.add((handIndex - 1.5) * 0.35, 3.0 + handIndex * 0.4, 0.0);
        projectile.setPos(verticalFallback);
        return verticalFallback;
    }

    private boolean isUsableShadowHandOrigin(DeerclopsShadowHandProjectile projectile, Vec3 origin) {
        return level().hasChunkAt(BlockPos.containing(origin))
                && level().noCollision(projectile, projectile.getBoundingBox());
    }

    private Vec3 randomSphereOffset(double radius) {
        double azimuth = random.nextDouble() * Math.PI * 2.0;
        double cosine = random.nextDouble() * 2.0 - 1.0;
        double horizontal = Math.sqrt(1.0 - cosine * cosine);
        return new Vec3(Math.cos(azimuth) * horizontal * radius, cosine * radius, Math.sin(azimuth) * horizontal * radius);
    }

    private void beginIcePillarWave(Vec3 horizontalOffset) {
        Vec3 direction = horizontalOffset.normalize();
        if (direction.lengthSqr() < 1.0E-4) {
            direction = getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
        }
        if (direction.lengthSqr() < 1.0E-4) {
            direction = new Vec3(0.0, 0.0, 1.0);
        }
        iceWaveOrigin = position();
        iceWaveDirection = direction;
        iceWaveStep = 0;
    }

    private void tickIceWave() {
        if (iceWaveStep < 0) {
            return;
        }
        int rowWidth = iceWaveStep * 2 + 1;
        for (int column = 0; column < rowWidth; column++) {
            createIcePillar(column - 3, Math.max(column, 5), iceWaveOrigin, iceWaveDirection);
        }
        if (++iceWaveStep >= ICE_WAVE_STEPS) {
            iceWaveStep = -1;
        }
    }

    private void createIcePillar(int forwardOffset, int horizontalRange, Vec3 center, Vec3 direction) {
        DeerclopsIcePillarProjectile projectile = ModEntities.ICE_PILLAR.get().create(level());
        if (projectile == null) {
            return;
        }
        Vec3 side = direction.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        Vec3 origin = center.add(direction.scale(forwardOffset))
                .add(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5)
                .add(side.scale((random.nextDouble() - 0.5) * horizontalRange));
        projectile.configure(this, origin, ATTACK_DAMAGE);
        level().addFreshEntity(projectile);
    }

    private void tickWithoutCombatTarget() {
        setFarInvulnerable(false);
        resetTraversalTracking();
        introComplete = false;
        attackCooldown = 0;
        iceWaveStep = -1;

        if (getCombatState() == CombatState.ATTACK_CHEST) {
            tickChestAttack();
            return;
        }
        setCombatState(CombatState.IDLE);
        if (chestTarget == null || !(level().getBlockEntity(chestTarget) instanceof ChestBlockEntity)) {
            chestTarget = findNearbyChest();
        }
        if (chestTarget == null) {
            return;
        }

        double distanceSqr = distanceToSqr(Vec3.atCenterOf(chestTarget));
        if (distanceSqr <= 20.0) {
            navigation.stop();
            chestAttackTicks = 0;
            setCombatState(CombatState.ATTACK_CHEST);
        } else {
            navigation.moveTo(chestTarget.getX() + 0.5, chestTarget.getY(), chestTarget.getZ() + 0.5, 1.0);
        }
    }

    private void tickChestAttack() {
        navigation.stop();
        if (getTarget() != null) {
            chestTarget = null;
            chestAttackTicks = 0;
            setCombatState(CombatState.ROAR);
            return;
        }
        if (++chestAttackTicks == 7 && chestTarget != null && level().getBlockEntity(chestTarget) instanceof ChestBlockEntity) {
            level().destroyBlock(chestTarget, true, this);
        }
        if (chestAttackTicks > ATTACK_TOTAL_TICKS) {
            chestTarget = null;
            chestAttackTicks = 0;
            setCombatState(CombatState.IDLE);
        }
    }

    private @Nullable BlockPos findNearbyChest() {
        BlockPos origin = blockPosition();
        BlockPos nearest = null;
        double nearestDistanceSqr = Double.MAX_VALUE;
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-CHEST_SEARCH_RADIUS, -4, -CHEST_SEARCH_RADIUS), origin.offset(CHEST_SEARCH_RADIUS, 4, CHEST_SEARCH_RADIUS))) {
            if (!(level().getBlockEntity(candidate) instanceof ChestBlockEntity)) {
                continue;
            }
            double distanceSqr = candidate.distSqr(origin);
            if (distanceSqr < nearestDistanceSqr) {
                nearestDistanceSqr = distanceSqr;
                nearest = candidate.immutable();
            }
        }
        return nearest;
    }

    /// 在正常寻路无法跨越地形时执行一次受控跳跃。
    ///
    /// <p>1.21 侧只不断重发地面路径，大体型碰撞箱遇到台阶、短墙或无法生成完整路径时
    /// 会长时间贴住障碍。这里仍让原版导航决定路线，仅在水平碰撞、连续停滞，或目标
    /// 明显更高且本次寻路失败时介入。跳跃带有冷却并检查上方空间，因此不会退化成
    /// 持续兔子跳，也不会在低矮洞穴里把 Boss 反复顶向天花板。</p>
    ///
    /// @return 本 tick 是否真正提交了跳跃速度
    boolean tryTraversalJump(LivingEntity target, boolean pathStarted) {
        updateTraversalStuckState();
        boolean targetAbove = target.getY() - getY() > 0.75;
        boolean needsRecovery = horizontalCollision
                || stuckTicks >= STUCK_TICKS_BEFORE_JUMP
                || (!pathStarted && targetAbove);
        if (!needsRecovery || traversalJumpCooldown > 0 || !onGround()) {
            return false;
        }

        Vec3 horizontal = target.position().subtract(position()).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-6) {
            return false;
        }
        Vec3 direction = horizontal.normalize();

        /// 以跳过一格障碍后的包围盒检查净空。实体当前仍贴地，若只上移不足一格，
        /// 检查会把本来能够越过的台阶误判为阻挡。
        if (!level().noCollision(this, getBoundingBox().move(direction.x * 0.8, 1.1, direction.z * 0.8))) {
            return false;
        }

        Vec3 movement = getDeltaMovement();
        double currentForward = movement.x * direction.x
                + movement.z * direction.z;
        double addedForward = Math.max(0.0, TRAVERSAL_FORWARD_SPEED - currentForward);
        setDeltaMovement(movement.x + direction.x * addedForward, Math.max(movement.y, TRAVERSAL_JUMP_SPEED), movement.z + direction.z * addedForward);
        hasImpulse = true;
        traversalJumpCooldown = TRAVERSAL_JUMP_COOLDOWN;
        stuckTicks = 0;
        navigation.stop();
        return true;
    }

    /// 只按水平位移判断追击是否停滞，避免正常起跳和落地的垂直运动把计数清零。
    private void updateTraversalStuckState() {
        if (!hasLastChasePosition) {
            lastChaseX = getX();
            lastChaseZ = getZ();
            hasLastChasePosition = true;
            stuckTicks = 0;
            return;
        }
        double deltaX = getX() - lastChaseX;
        double deltaZ = getZ() - lastChaseZ;
        if (deltaX * deltaX + deltaZ * deltaZ < 0.0025) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }
        lastChaseX = getX();
        lastChaseZ = getZ();
    }

    private void resetTraversalTracking() {
        stuckTicks = 0;
        hasLastChasePosition = false;
    }

    public boolean isFarForInvulnerable() {
        return entityData.get(DATA_FAR_INVULNERABLE);
    }

    private void setFarInvulnerable(boolean value) {
        entityData.set(DATA_FAR_INVULNERABLE, value);
    }

    public CombatState getCombatState() {
        int state = entityData.get(DATA_COMBAT_STATE);
        CombatState[] states = CombatState.values();
        return state >= 0 && state < states.length ? states[state] : CombatState.IDLE;
    }

    private void setCombatState(CombatState state) {
        if (getCombatState() == state) {
            return;
        }
        entityData.set(DATA_COMBAT_STATE, state.ordinal());
        stateTicks = 0;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return isFarForInvulnerable()
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || super.isInvulnerableTo(source);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "Controller",
                5,
                state -> state.setAndContinue(switch (getCombatState()) {
                    case ROAR -> ROAR;
                    case ROARING -> ROARING;
                    case ATTACK, ATTACK_CHEST -> ICE;
                    case IDLE, CHASE -> state.isMoving() ? WALK : STAND;
                })));
    }

    /// 客户端动画和测试共同使用的少量稳定战斗状态。
    public enum CombatState {
        IDLE,
        ROAR,
        ROARING,
        CHASE,
        ATTACK,
        ATTACK_CHEST
    }

    /// 冰击的位置分支。该类型只描述本次已经选中的攻击方式，不承担状态机推进。
    enum AttackPattern {
        THROWN_ICE,
        SHADOW_HAND,
        ICE_PILLAR
    }

    /// 一次冰击的服务端提交结果。
    ///
    /// <p>弹幕实体会先进入世界的待加入队列，因此测试或诊断代码不应依赖同一 tick
    /// 的实体快照来判断生成是否成功。这里保留实际被 {@code addFreshEntity}
    /// 接受的数量，使调用方可以观察确定的提交结果；正常战斗逻辑无需使用返回值。</p>
    record AttackResult(AttackPattern pattern, int spawnedEntities) {}
}
