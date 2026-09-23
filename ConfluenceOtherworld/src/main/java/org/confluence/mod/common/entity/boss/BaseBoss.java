package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.EnemyTargeting;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.network.s2c.BossBarSyncPacketS2C;
import org.mesdag.portlib.network.PortPacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

/// 所有可独立结束战斗的 Boss 本体基类。
///
/// Boss 本体是生命值、Boss 条、行为树和子实体生命周期的服务端权威。客户端只接收正常的
/// 实体同步与事件，不参与玩家选择或撤离计时。手、机械臂、触手等临时部件登记在
/// {@link #subEntities} 中，本体销毁时会统一级联清理；普通区块卸载则保留可存档从属的恢复机会。
///
/// 战斗是否仍在继续只由合格玩家决定，而不是由铁傀儡等任意存活目标决定。当前玩家死亡、
/// 切换到创造/旁观、离开维度或离开战斗范围时，服务器立即在本维度追踪范围内选择仇恨值最高
/// 的其他存活玩家。多人战中只要仍有一名合格玩家，撤离计时就会清零；若没有战斗目标但仍有
/// 正在追踪本体的创造玩家，Boss 保持无目标待机。只有现场确实无人时才开始
/// {@value #DISENGAGE_TICKS} tick 的脱战，随后无死亡奖励、无击杀消息地消失。
public abstract class BaseBoss extends BaseMonster implements Boss {
    protected final ServerBossEvent bossEvent;
    private final List<Entity> subEntities = new ArrayList<>();
    /// 只在成员变化时重建，供渲染和其他 Boss 逻辑安全遍历，避免回调中解绑实体修改迭代器。
    private List<Entity> subEntitiesSnapshot = List.of();
    protected float ironGolemResistance = 0.4f;
    protected float explosionResistance = 0.5f;
    protected int noTargetTicks = 0;
    /// 连续失去合格玩家超过该时长后结束遭遇。
    protected static final int DISENGAGE_TICKS = 200;
    /// 定期补发自定义样式元数据，覆盖换档、重连及部件主身份交接造成的客户端时序差。
    private static final int BOSS_BAR_RESYNC_INTERVAL = 40;
    private static final int RETREAT_TICKS = 40;
    private static final byte PHASE_PARTICLE_EVENT = 60;
    private static final byte DEATH_PARTICLE_EVENT = 61;
    private @Nullable UUID synchronizedCombatTarget;
    private @Nullable LivingEntity retaliationTarget;
    /// 本次遭遇中曾成为目标或直接攻击过 Boss 的玩家；仅存 UUID，避免跨卸载强引用。
    private final Set<UUID> combatParticipantIds = new LinkedHashSet<>();
    /// 已在机械三王同时存活期间共同参与三场遭遇的玩家。
    private final Set<UUID> mechanicalMayhemParticipantIds = new LinkedHashSet<>();
    /// 保证同一场遭遇的掉落、击败记录与成就只进入一次结算流程。
    private boolean deathRewardsSettled;
    private boolean deathAnnouncementSent;
    // 死亡玩家丢失本场遭遇的自动仇恨；主动攻击 Boss 会重新加入目标候选。
    private final Set<UUID> deathRetargetBlocked = new HashSet<>();
    private boolean disengageRetreating;
    private boolean noPhysicsBeforeRetreat;
    private boolean applyingDisengageMovement;
    private boolean removingSubEntities;
    /// 本 tick 是否有创造模式玩家在交战范围内维持现场。它不参与索敌，只阻止无人脱战清理。
    private boolean encounterObserverNearby;
    private float lastSynchronizedBossBarHealth = Float.NaN;
    private float lastSynchronizedBossBarMaximumHealth = Float.NaN;
    private final BossChunkTicket encounterChunkTicket = new BossChunkTicket(getUUID());

    public BaseBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // Boss 模型和整场部件布局常超出本体碰撞箱，普通视锥不能代表实际可见范围。
        this.noCulling = true;
        // Boss 的离场只能由遭遇生命周期决定，不能再被原版 Mob
        // 根据距离和 noActionTime 当作普通怪物自然清除。
        setPersistenceRequired();
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(), getBossBarColor(), BossEvent.BossBarOverlay.PROGRESS)
                .setDarkenScreen(true).setPlayBossMusic(true);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    // === Boss bar ===

    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        addBossBarPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        removeBossBarPlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        bossEvent.setName(getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        float progress = Mth.clamp(getBossBarProgress(), 0.0F, 1.0F);
        bossEvent.setProgress(progress);
        float maximumHealth = getBossBarMaximumHealth();
        float health = progress * maximumHealth;
        if (Float.compare(health, lastSynchronizedBossBarHealth) != 0 || Float.compare(maximumHealth, lastSynchronizedBossBarMaximumHealth) != 0 || tickCount % BOSS_BAR_RESYNC_INTERVAL == 0) {
            lastSynchronizedBossBarHealth = health;
            lastSynchronizedBossBarMaximumHealth = maximumHealth;
            for (ServerPlayer player : bossEvent.getPlayers())
                sendBossBar(player, health, maximumHealth, true);
        }
    }

    /// 返回本场遭遇在当前 tick 的唯一 Boss 条进度快照。
    protected float getBossBarProgress() {
        return getMaxHealth() <= 0.0F ? 0.0F : getHealth() / getMaxHealth();
    }

    protected float getBossBarMaximumHealth() {
        return getMaxHealth();
    }

    protected final void addBossBarPlayer(ServerPlayer player) {
        bossEvent.addPlayer(player);
        synchronizeBossBar(player, true);
    }

    protected final void removeBossBarPlayer(ServerPlayer player) {
        bossEvent.removePlayer(player);
        synchronizeBossBar(player, false);
    }

    protected final void removeAllBossBarPlayers() {
        for (ServerPlayer player : List.copyOf(bossEvent.getPlayers())) removeBossBarPlayer(player);
    }

    private void synchronizeBossBar(ServerPlayer player, boolean visible) {
        if (!visible) {
            sendBossBar(player, 0.0F, 0.0F, false);
            return;
        }
        float maximumHealth = getBossBarMaximumHealth();
        sendBossBar(player, Mth.clamp(getBossBarProgress(), 0.0F, 1.0F) * maximumHealth, maximumHealth, true);
    }

    private void sendBossBar(ServerPlayer player, float health, float maximumHealth, boolean visible) {
        PortPacketDistributor.sendToPlayer(player, new BossBarSyncPacketS2C(bossEvent.getId(), BuiltInRegistries.ENTITY_TYPE.getKey(getType()), health, maximumHealth, visible));
    }

    // === Boss interface ===

    @Override
    public boolean shouldShowMessage() { return isMainBody(); }

    @Override
    public boolean isMainBody() { return true; }

    @Override
    public boolean shouldEnhanceMultiplayer() {return true;}

    // === Multi-part ===
    double getBossHealthDifficultyMultiplier(double defaultMultiplier) {
        return defaultMultiplier;
    }

    /// 返回生成时玩家数量对应的最大生命倍率。
    ///
    /// 默认按玩家数量线性放大；具有特殊多人公式的 Boss
    /// 可以单独覆写。传入值已经限制在一至八人。
    double getBossHealthPlayerMultiplier(int playerCount) {
        return playerCount;
    }

    // === 多部件生命周期 ===

    public void addSubEntity(Entity part) {
        if (!subEntities.contains(part)) {
            subEntities.add(part);
            refreshSubEntitiesSnapshot();
        }
    }

    public void removeSubEntity(Entity part) {
        if (!removingSubEntities && subEntities.remove(part)) refreshSubEntitiesSnapshot();
    }

    public List<Entity> getSubEntities() {
        return subEntitiesSnapshot;
    }

    private void refreshSubEntitiesSnapshot() {
        subEntitiesSnapshot = List.copyOf(subEntities);
    }

    @Override
    protected void onCreatureDefinitionReload() {
        // 本体 scale 属性由 LivingEntity 同步；非生物部件没有属性表，需要主动刷新碰撞箱。
        for (Entity part : subEntitiesSnapshot) {
            if (!part.isRemoved()) part.refreshDimensions();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        encounterChunkTicket.release();
        if (level() instanceof ServerLevel serverLevel && (reason == RemovalReason.DISCARDED || reason.shouldDestroy())) {
            // Boss 永久离场后不会再消费从属死亡邮箱，必须同时清除其持久化记录。
            BossChildDeathLedger.clear(serverLevel, getUUID());
        }
        removingSubEntities = true;
        try {
            for (Entity part : subEntitiesSnapshot) {
                /// 区块卸载时只清理可重建的临时部件；主动撤离和真正销毁必须
                /// 清理全部尚未移除的从属。不能用 isAlive() 过滤，因为已经
                /// 进入死亡动画但仍留在世界中的从属同样属于本场遭遇。
                if (!part.isRemoved() && (part instanceof BaseBossPart<?> || reason == RemovalReason.DISCARDED || reason.shouldDestroy())) {
                    part.remove(reason);
                }
            }
            subEntities.clear();
            refreshSubEntitiesSnapshot();
            super.remove(reason);
        } finally {
            removingSubEntities = false;
        }
    }

    /// 当前是否正由本体级联移除子实体。
    ///
    /// 可破坏的从属生物会在正常死亡时回报本体；Boss 撤离时的级联移除不是“子实体被玩家
    /// 击败”，不得写入死亡账本或触发阶段推进。
    final boolean isRemovingSubEntities() {
        return removingSubEntities;
    }

    // === Difficulty ===

    protected boolean isExpert() {
        return LibUtils.isAtLeastExpert(level(), blockPosition());
    }

    protected boolean isMaster() {
        return LibUtils.isMaster(level(), blockPosition());
    }

    /// 当前世界是否启用了“受够了”特殊种子规则。
    ///
    /// 直接读取世界的特殊种子标志，避免为单次查询引入额外桥接层。
    protected boolean isFtw() {
        return level() instanceof ServerLevel serverLevel
                && ModSecretSeeds.FOR_THE_WORTHY.match(serverLevel);
    }

    // === Damage ===

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) registerCombatParticipant(player);
        if (source.getEntity() instanceof IronGolem) {
            amount *= ironGolemResistance;
        }
        if (source.is(DamageTypes.EXPLOSION)) {
            amount *= explosionResistance;
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt) onEncounterHurt(source);
        return hurt;
    }

    /// 本体和独立扣血的部件共用受击响应，不向本体重复结算部件伤害。
    public final void onEncounterHurt(DamageSource source) {
        if (level().isClientSide || !isAlive()) return;
        LivingEntity attacker = EnemyTargeting.attacker(source);
        if (attacker instanceof Player player) registerCombatParticipant(player);
        if (attacker == null || attacker == this || EnemyDamageRules.isEnemy(attacker)) return;
        if (!(attacker instanceof Player)) retaliationTarget = attacker;
        Player player = validCombatPlayer(getTarget());
        if (player == null) player = findCombatPlayer();
        if (player != null) setTarget(player);
        else if (canAttack(attacker)) setTarget(attacker);
    }

    /// Boss 优先攻击玩家，也能反击非敌怪攻击者；不主动攻击无关生物。
    @Override
    public boolean canAttack(LivingEntity target) {
        return isAlive() && shouldMaintainCombatTarget()
                && target.level() == level() && target.isAlive() && !target.isRemoved()
                && (!(target instanceof Player player) || !player.isCreative() && !player.isSpectator())
                && !EnemyDamageRules.isEnemy(target)
                && (target instanceof Player || target == retaliationTarget || target == getLastHurtByMob())
                && super.canAttack(target);
    }

    // === Immunity ===

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    /// Boss 的位移只由自身技能控制，忽略武器与弹幕施加的受击击退。
    @Override
    public void knockback(double strength, double x, double z) {}

    /// Boss 通过自身伤害箱处理接触攻击，不使用 Minecraft 的实体互推。
    /// 否则大型或悬浮 Boss 会把玩家持续压向地面和方块缝隙，
    /// 错误触发原版的伏地/游泳姿态。
    @Override
    public void push(Entity entity) {}

    /// 原版 Mob 每刻还会主动调用该入口推开相交实体。
    /// Boss 的接触伤害由独立扫描结算，不能再借此改变玩家位置或姿态。
    @Override
    protected void doPush(Entity entity) {}

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypes.LAVA)) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public void lavaHurt() {
        if (!fireImmune()) {
            float multiplier = isExpert() ? 0.05f : 0.25f;
            igniteForSeconds(15.0F * multiplier);
            if (hurt(damageSources().lava(), 4.0F * multiplier)) {
                playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + random.nextFloat() * 0.4F);
            }
        }
    }

    // === Tick ===

    /// 状态机决定朝向，不能由原版倒退行走修正再翻转半圈。
    @Override
    protected float tickHeadTurn(float bodyYaw, float animationSpeed) {
        yBodyRot = getYRot();
        yHeadRot = getYRot();
        return animationSpeed;
    }

    @Override
    public void tick() {
        if (!isAlive()) {
            if (!level().isClientSide) {
                setTarget(null);
                synchronizeCombatTarget(null);
                encounterChunkTicket.release();
            }
            super.tick();
            return;
        }
        LivingEntity targetBeforeAi = null;
        if (!level().isClientSide) {
            LivingEntity currentTarget = getTarget();
            UUID diedTargetId = currentTarget instanceof Player player && !player.isAlive() ? player.getUUID() : null;
            if (diedTargetId == null && currentTarget == null && synchronizedCombatTarget != null) {
                for (Player player : level().players()) {
                    if (player.getUUID().equals(synchronizedCombatTarget) && !player.isAlive()) {
                        diedTargetId = player.getUUID();
                        break;
                    }
                }
            }
            if (diedTargetId != null) {
                deathRetargetBlocked.add(diedTargetId);
            }
            for (Player player : level().players()) {
                if (!player.isAlive() && combatParticipantIds.contains(player.getUUID())) {
                    deathRetargetBlocked.add(player.getUUID());
                }
            }
            if (shouldMaintainCombatTarget()) {
                targetBeforeAi = validCombatPlayer(getTarget());
                if (targetBeforeAi == null) {
                    targetBeforeAi = findCombatPlayer();
                }
                if (targetBeforeAi == null)
                    targetBeforeAi = validRetaliationTarget(retaliationTarget);
                if (getTarget() != targetBeforeAi) {
                    setTarget(targetBeforeAi);
                }
            } else if (getTarget() != null) {
                setTarget(null);
            }
        }
        super.tick();
        if (isRemoved()) return;
        if (level().isClientSide) {
            if (tickCount == 1) spawnParticleBurst(ParticleTypes.POOF, 24, 0.16);
        } else {
            if (subEntities.removeIf(Entity::isRemoved)) refreshSubEntitiesSnapshot();
            if (maintainsEncounterChunkTicket() && noTargetTicks <= DISENGAGE_TICKS) {
                encounterChunkTicket.refresh(this, BossChunkTicket.REGION_DISTANCE);
            } else {
                // 配置允许无目标 Boss 长期存在时，也不能让空战斗永久强加载区块。
                encounterChunkTicket.release();
            }
            MechanicalMayhemTracker.observe(this);
            updateCombatLifecycle(targetBeforeAi);
        }
    }

    /// 是否由公共遭遇生命周期维持本体区块。具有专用区域票据的巨型 Boss 可覆盖。
    protected boolean maintainsEncounterChunkTicket() {
        return true;
    }

    /// 更新多人目标与脱战计时。该方法只在服务端调用。
    ///
    /// 已有合格目标时保持仇恨稳定，不会因为另一名玩家仇恨值更高就每 tick 来回换目标；只有当前
    /// 目标失效时才选择追踪范围内仇恨值最高的替代玩家。没有替代者时立即清空目标，使行为树停止
    /// 攻击，再进入宽限计时。
    private void updateCombatLifecycle(@Nullable LivingEntity targetBeforeAi) {
        if (!shouldMaintainCombatTarget()) {
            if (getTarget() != null) setTarget(null);
            synchronizeCombatTarget(null);
            noTargetTicks = 0;
            encounterObserverNearby = false;
            stopDisengageRetreat();
            return;
        }

        // AI 运行期间可以刷新导航和攻击状态，但不能把仍然合法的权威玩家换成铁傀儡、
        // 召唤物或另一名玩家。只有原目标已经失效，才接受 AI 找到的合法玩家或重新选取。
        Player combatPlayer = validCombatPlayer(targetBeforeAi);
        if (combatPlayer == null) combatPlayer = validCombatPlayer(getTarget());
        if (combatPlayer == null) combatPlayer = findCombatPlayer();
        if (combatPlayer != null) registerCombatParticipant(combatPlayer);
        if (getTarget() != combatPlayer) setTarget(combatPlayer);

        synchronizeCombatTarget(combatPlayer);
        if (combatPlayer != null) {
            noTargetTicks = 0;
            encounterObserverNearby = false;
            stopDisengageRetreat();
            return;
        }

        LivingEntity retaliation = validRetaliationTarget(retaliationTarget);
        if (retaliation != null) {
            setTarget(retaliation);
            noTargetTicks = 0;
            encounterObserverNearby = false;
            stopDisengageRetreat();
            return;
        }
        retaliationTarget = null;

        // 创造和旁观玩家只维持现场，不参与战斗，也绝不会进入 Mob#getTarget。
        // 该检查必须放在脱战计时之前：创造模式玩家同样“在场”，Boss 却永远找不到
        // 合格战斗目标。若只按合格目标计时，创造模式实测时 Boss 会在 200 tick 后
        // 判空脱战并 discard，玩家看到的就是一个既打不动、又已经不存在于服务端的实体。
        if (refreshEncounterObserver()) {
            noTargetTicks = 0;
            stopDisengageRetreat();
            return;
        }

        tickDisengageTimer();
    }

    /// 刷新“现场玩家”判定：正在由服务端追踪该 Boss 的创造模式玩家会成为
    /// {@link #encounterObserverNearby}。他们不能被索敌，但会让 Boss 保持无目标待机，
    /// 而不是进入撤离或移除流程。这里不能复用较小的索敌半径：客户端仍能看到 Boss 时
    /// 就开始服务端撤离，会造成可见实体与服务端生命周期不一致。旁观者不计入。
    private boolean refreshEncounterObserver() {
        boolean observerNearby = false;
        for (ServerPlayer player : bossEvent.getPlayers()) {
            if (!isEncounterObserver(player)) continue;
            registerCombatParticipant(player);
            observerNearby = true;
        }
        encounterObserverNearby = observerNearby;
        return observerNearby;
    }

    private void tickDisengageTimer() {
        noTargetTicks++;
        boolean clearsWhenEmpty = CommonConfigs.BOSS_CLEAR_WHEN_NO_TARGET.get() && shouldDiscardWhenNoTarget();
        if (clearsWhenEmpty && noTargetTicks >= DISENGAGE_TICKS - RETREAT_TICKS) {
            beginDisengageRetreat();
            applyDisengageRetreat();
        }
        if (noTargetTicks <= DISENGAGE_TICKS) return;
        if (clearsWhenEmpty) {
            onDisengageComplete();
        }
    }

    private void beginDisengageRetreat() {
        if (disengageRetreating) return;
        disengageRetreating = true;
        noPhysicsBeforeRetreat = noPhysics;
        noPhysics = true;
        navigation.stop();
    }

    protected void applyDisengageRetreat() {
        applyingDisengageMovement = true;
        try {
            super.setDeltaMovement(getDisengageMovement());
        } finally {
            applyingDisengageMovement = false;
        }
    }

    protected Vec3 getDisengageMovement() {
        Vec3 movement = getDeltaMovement();
        double verticalSpeed = isNoGravity() ? 0.45D : -0.22D;
        return new Vec3(movement.x * 0.35D, verticalSpeed, movement.z * 0.35D);
    }

    private void stopDisengageRetreat() {
        if (!disengageRetreating) return;
        disengageRetreating = false;
        noPhysics = noPhysicsBeforeRetreat;
    }

    final boolean isDisengageRetreating() {
        return disengageRetreating;
    }

    /// 本 tick 是否有创造模式玩家在交战范围内维持现场。
    ///
    /// 这类玩家不能被索敌，但仍然在场；子实体可以据此决定是否播放撤离表现，
    /// 而不是沿用“已经没有玩家”的撤离语义。
    protected final boolean hasEncounterObserverNearby() {
        return encounterObserverNearby;
    }

    @Override
    public void setDeltaMovement(Vec3 movement) {
        if (!disengageRetreating || applyingDisengageMovement) {
            super.setDeltaMovement(movement);
        }
    }

    /// 从本维度追踪范围内的在线玩家中寻找替代目标。
    ///
    /// 首先比较玩家的 AGGRO 属性；并列时选择距离遭遇最近者，距离仍相同时按 UUID 固定排序，
    /// 保证相同世界状态始终得到同一个目标。
    protected @Nullable Player findCombatPlayer() {
        return selectCombatPlayer(level().players());
    }

    private @Nullable Player selectCombatPlayer(List<? extends Player> candidates) {
        double maximumAggro = Double.NEGATIVE_INFINITY;
        double nearestDistanceSqr = Double.POSITIVE_INFINITY;
        Player selected = null;
        for (Player player : candidates) {
            if (!isEligibleRetargetCandidate(player)
                    || !isValidCurrentCombatPlayer(player)) continue;
            var aggro = player.getAttribute(ConfluenceMagicLib.AGGRO);
            double value = aggro == null ? 0.0D : aggro.getValue();
            double distanceSqr = combatAnchorDistanceSqr(player);
            int aggroComparison = Double.compare(value, maximumAggro);
            int distanceComparison = Double.compare(distanceSqr, nearestDistanceSqr);
            if (aggroComparison > 0
                    || (aggroComparison == 0 && distanceComparison < 0)
                    || (aggroComparison == 0 && distanceComparison == 0
                    && (selected == null || player.getUUID().compareTo(selected.getUUID()) < 0))) {
                maximumAggro = value;
                nearestDistanceSqr = distanceSqr;
                selected = player;
            }
        }
        return selected;
    }

    /// 把玩家登记为本场遭遇参与者。正常游戏中由锁定目标和玩家伤害自动登记。
    public final void registerCombatParticipant(Player player) {
        if (player.level() == level() && player.isAlive()
                && !player.isSpectator()) {
            combatParticipantIds.add(player.getUUID());
            if (deathRetargetBlocked.remove(player.getUUID()) && isValidCurrentCombatPlayer(player)) {
                setTarget(player);
                noTargetTicks = 0;
                stopDisengageRetreat();
            }
        }
    }

    /// 由 Boss 召唤道具创建实体时，立即把使用者绑定到本次遭遇。
    ///
    /// 自然生成的 Boss 仍然可以依靠附近玩家搜索接管战斗；召唤道具路径则必须显式登记召唤者，
    /// 否则刚生成的 Boss 会短暂进入无目标分支，飞行 Boss 在客户端实测中会表现为沉底或贴地滑行。
    public final void initializeSummonedCombat(Player player) {
        registerCombatParticipant(player);
        Player combatPlayer = isValidCurrentCombatPlayer(player) ? player : null;
        setTarget(combatPlayer);
        noTargetTicks = 0;
        stopDisengageRetreat();
    }

    final Set<UUID> combatParticipantIdsSnapshot() {
        return Set.copyOf(combatParticipantIds);
    }

    /// 将同一场战斗的目标与参与者转交给替代实体。
    /// 分裂、换头或阶段替换不能重新开始遭遇，否则会丢失目标、血条观察者和奖励归属。
    protected final void inheritEncounterState(BaseBoss source) {
        combatParticipantIds.addAll(source.combatParticipantIds);
        mechanicalMayhemParticipantIds.addAll(source.mechanicalMayhemParticipantIds);
        LivingEntity sourceTarget = source.getAuthoritativeLivingTarget();
        if (sourceTarget != null) {
            setTarget(sourceTarget);
        }
        noTargetTicks = source.noTargetTicks;
        deathRetargetBlocked.addAll(source.deathRetargetBlocked);
    }

    final void confirmMechanicalMayhemParticipants(Set<UUID> participantIds) {
        mechanicalMayhemParticipantIds.addAll(participantIds);
    }

    public final boolean isMechanicalMayhemParticipant(UUID playerId) {
        return mechanicalMayhemParticipantIds.contains(playerId);
    }

    /// 取得本场遭遇当前仍在线的参战玩家快照。
    ///
    /// 返回值按照首次登记顺序生成且不可修改。玩家死亡、切换游戏模式或离开战斗半径不会
    /// 抹掉已经发生的参战事实；已经离线、离开 Boss 所在维度或从未登记过的玩家不会进入本次
    /// 即时结算。调用方必须保存本次返回值完成整轮结算，不能在发放过程中反复读取。
    public final List<ServerPlayer> getOnlineCombatParticipants() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return List.of();
        }
        return resolveOnlineCombatParticipants(serverLevel.players());
    }

    /// 默认在本体处掉落；大型场地 Boss 可选择参战玩家能够拾取的位置。
    public Vec3 getRewardPosition(ServerPlayer player) {
        return position();
    }

    private List<ServerPlayer> resolveOnlineCombatParticipants(List<ServerPlayer> onlinePlayers) {
        List<ServerPlayer> participants = new ArrayList<>(combatParticipantIds.size());
        for (UUID participantId : combatParticipantIds) {
            for (ServerPlayer player : onlinePlayers) {
                if (player.getUUID().equals(participantId)) {
                    participants.add(player);
                    break;
                }
            }
        }
        return participants;
    }

    /// 尝试取得本场遭遇的死亡结算权。
    ///
    /// 死亡事件、部件转发或外部联动可能在同一 tick 重复请求结算。只有第一次调用返回
    /// {@code true}，后续调用不得再次生成奖励或推进击败记录。
    public final boolean tryBeginDeathRewardSettlement() {
        if (deathRewardsSettled) return false;
        deathRewardsSettled = true;
        return true;
    }

    /// 判定玩家能否维持当前 Boss 战。创造、旁观、死亡和跨维度玩家都不计入多人存活人数。
    protected boolean isValidCurrentCombatPlayer(Player player) {
        double range = getCombatPlayerRange();
        return player.level() == level() && player.isAlive()
                && !player.isCreative() && !player.isSpectator()
                && player.canBeSeenAsEnemy()
                && !hasLostAggroAfterDeath(player)
                && combatAnchorDistanceSqr(player) < range * range;
    }

    private boolean hasLostAggroAfterDeath(Player player) {
        return deathRetargetBlocked.contains(player.getUUID());
    }

    private @Nullable Player validCombatPlayer(@Nullable LivingEntity target) {
        return target instanceof Player player && isValidCurrentCombatPlayer(player) ? player : null;
    }

    private @Nullable LivingEntity validRetaliationTarget(@Nullable LivingEntity target) {
        return target != null && !(target instanceof Player)
                && target.level() == level() && target.isAlive()
                && combatAnchorDistanceSqr(target) < getCombatPlayerRange() * getCombatPlayerRange()
                && canAttack(target) ? target : null;
    }

    private boolean isEncounterObserver(Player player) {
        return player.level() == level() && player.isAlive()
                && player.isCreative() && !player.isSpectator();
    }

    /// 是否由公共生命周期持续维护玩家目标。
    ///
    /// 主动离场、演出或不可攻击阶段可以临时返回 {@code false}。此时公共层会清空攻击目标、
    /// 停止从属追击，并暂停无目标清理计时，把该阶段的结束权完整交还给具体 Boss。
    protected boolean shouldMaintainCombatTarget() {
        return true;
    }

    /// 返回已经通过本 Boss 特殊规则校验的权威玩家目标，供从属生物继承。
    final @Nullable Player getAuthoritativeCombatTarget() {
        return shouldMaintainCombatTarget() ? validCombatPlayer(getTarget()) : null;
    }

    final @Nullable LivingEntity getAuthoritativeLivingTarget() {
        if (!shouldMaintainCombatTarget()) return null;
        Player player = validCombatPlayer(getTarget());
        return player == null ? validRetaliationTarget(getTarget()) : player;
    }

    private boolean isEligibleRetargetCandidate(Player player) {
        return player.level() == level() && player.canBeSeenAsEnemy();
    }

    /// Boss 战保留半径。默认跟随 {@link Attributes#FOLLOW_RANGE}，但至少为 16 格；静态大型
    /// 遭遇或不可见管理实体可覆盖此值，使用实际竞技场尺寸。
    protected double getCombatPlayerRange() {
        return Math.max(16.0, getAttributeValue(Attributes.FOLLOW_RANGE));
    }

    /// 计算战斗目标到整场遭遇最近锚点的平方距离。活着的结构部件也算锚点，因此多人分别牵制
    /// Boss 本体和部件时不会因只远离本体而错误脱战；普通仆从不会扩大整场战斗的保留范围。
    protected double combatAnchorDistanceSqr(LivingEntity target) {
        double nearest = distanceToSqr(target);
        for (Entity part : subEntitiesSnapshot) {
            if (part.isAlive() && isCombatAnchor(part)) {
                nearest = Math.min(nearest, part.distanceToSqr(target));
            }
        }
        return nearest;
    }

    /// 只有构成本体空间范围或共享胜负条件的实体才参与遭遇距离计算。
    protected boolean isCombatAnchor(Entity entity) {
        return entity instanceof BaseBossPart<?> || entity instanceof BaseLivingBossPart<?> || entity instanceof AbstractTwinEye;
    }

    /// 在目标周围寻找适合飞行 Boss 的传送点。
    ///
    /// 这里不能使用世界高度图：高度图只返回整列最高表面，地下 Boss 会因此穿出洞穴或
    /// 竞技场。候选点以目标当前高度为中心，并同时检查区块加载、实体碰撞和液体占用；
    /// 找不到安全位置时返回 {@code null}，调用方应保持原位等待下一次尝试。
    protected final @Nullable Vec3 findFlyingTeleportPosition(LivingEntity target, double minimumRadius, double maximumRadius, double verticalRadius, int attempts) {
        if (!(level() instanceof ServerLevel serverLevel)) return null;
        for (int attempt = 0; attempt < attempts; attempt++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            double radius = Mth.lerp(random.nextDouble(), minimumRadius, maximumRadius);
            double x = target.getX() + Math.cos(angle) * radius;
            double y = Mth.clamp(target.getY() + Mth.lerp(random.nextDouble(), -verticalRadius, verticalRadius), serverLevel.getMinBuildHeight() + 1.0, serverLevel.getMaxBuildHeight() - getBbHeight() - 1.0);
            double z = target.getZ() + Math.sin(angle) * radius;
            BlockPos blockPos = BlockPos.containing(x, y, z);
            AABB destinationBounds = getBoundingBox().move(x - getX(), y - getY(), z - getZ());
            if (serverLevel.hasChunkAt(blockPos) && serverLevel.noCollision(this, destinationBounds) && !serverLevel.containsAnyLiquid(destinationBounds)) {
                return new Vec3(x, y, z);
            }
        }
        return null;
    }

    private void synchronizeCombatTarget(@Nullable Player target) {
        UUID targetUUID = target == null ? null : target.getUUID();
        if (Objects.equals(synchronizedCombatTarget, targetUUID)) return;
        synchronizedCombatTarget = targetUUID;
        onCombatTargetChanged(target);
    }

    /// 遭遇的权威玩家目标发生变化时调用。所有已加载的生物型从属立即继承新目标；
    /// 目标消失时也同时停战，不能让仆从继续追逐已经失效的玩家。
    protected void onCombatTargetChanged(@Nullable Player target) {
        for (Entity part : subEntitiesSnapshot) {
            if (part instanceof Mob mob && mob.isAlive()) {
                mob.setTarget(target);
            }
        }
    }

    /// 脱战宽限结束后的默认行为。撤离不是死亡，不能发送击杀消息、掉落战利品或推进击败进度。
    protected void onDisengageComplete() {
        discard();
    }

    protected boolean shouldDiscardWhenNoTarget() {
        return true;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected int contactAttackInterval() {
        return 1;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.25D;
    }

    protected final void broadcastPhaseTransition() {
        if (!level().isClientSide) level().broadcastEntityEvent(this, PHASE_PARTICLE_EVENT);
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == PHASE_PARTICLE_EVENT) {
            spawnParticleBurst(ParticleTypes.ENCHANTED_HIT, 40, 0.24);
            return;
        }
        if (eventId == DEATH_PARTICLE_EVENT) {
            spawnParticleBurst(ParticleTypes.EXPLOSION, 48, 0.32);
            return;
        }
        super.handleEntityEvent(eventId);
    }

    private void spawnParticleBurst(ParticleOptions particle, int count, double speed) {
        if (!level().isClientSide) return;
        double radius = Math.max(0.5, Math.max(getBbWidth(), getBbHeight()) * 0.5);
        for (int index = 0; index < count; index++) {
            double x = getX() + (random.nextDouble() - 0.5) * getBbWidth();
            double y = getY() + random.nextDouble() * getBbHeight();
            double z = getZ() + (random.nextDouble() - 0.5) * getBbWidth();
            level().addParticle(particle, x, y, z, random.nextGaussian() * speed * radius, random.nextGaussian() * speed, random.nextGaussian() * speed * radius);
        }
    }

    // === Save ===

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("CombatParticipants", saveUuidSet(combatParticipantIds));
        tag.put("MechanicalMayhemParticipants", saveUuidSet(mechanicalMayhemParticipantIds));
        tag.put("DeathRetargetBlocked", saveUuidSet(deathRetargetBlocked));
        tag.putBoolean("DeathRewardsSettled", deathRewardsSettled);
        tag.putBoolean("DeathAnnouncementSent", deathAnnouncementSent);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        loadUuidSet(tag, "CombatParticipants", combatParticipantIds);
        loadUuidSet(tag, "MechanicalMayhemParticipants", mechanicalMayhemParticipantIds);
        loadUuidSet(tag, "DeathRetargetBlocked", deathRetargetBlocked);
        deathRewardsSettled = tag.getBoolean("DeathRewardsSettled");
        deathAnnouncementSent = tag.getBoolean("DeathAnnouncementSent");
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }

    private static ListTag saveUuidSet(Set<UUID> values) {
        ListTag list = new ListTag();
        for (UUID value : values) {
            list.add(NbtUtils.createUUID(value));
        }
        return list;
    }

    private static void loadUuidSet(CompoundTag tag, String key, Set<UUID> destination) {
        destination.clear();
        ListTag list = tag.getList(key, Tag.TAG_INT_ARRAY);
        for (Tag value : list) {
            try {
                destination.add(NbtUtils.loadUUID(value));
            } catch (IllegalArgumentException ignored) {
                // 损坏的单个 UUID 不应阻止 Boss 其余有效状态加载。
            }
        }
    }

    // === Attributes ===

// === Death ===

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && !deathAnnouncementSent) {
            deathAnnouncementSent = true;
            level().broadcastEntityEvent(this, DEATH_PARTICLE_EVENT);
            Boss.sendBossDeathMessage(this);
        }
        super.die(source);
    }
}
