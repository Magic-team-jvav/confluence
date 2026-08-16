package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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
import org.confluence.mod.common.entity.ai.bt.Blackboard;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.init.ModSecretSeeds;

import javax.annotation.Nullable;
import java.util.*;

/// 所有可独立结束战斗的 Boss 本体基类。
///
/// <p>Boss 本体是生命值、Boss 条、行为树和子实体生命周期的服务端权威。客户端只接收正常的
/// 实体同步与事件，不参与玩家选择或撤离计时。手、机械臂、触手等临时部件登记在
/// {@link #subEntities} 中，本体销毁时会统一级联清理；普通区块卸载则保留可存档从属的恢复机会。</p>
///
/// <p>战斗是否仍在继续只由合格玩家决定，而不是由铁傀儡等任意存活目标决定。当前玩家死亡、
/// 切换到创造/旁观、离开维度或离开战斗范围时，服务器立即在本维度追踪范围内选择仇恨值最高
/// 的其他存活玩家。多人战中只要仍有一名合格玩家，撤离计时就会清零；全部玩家离场后 Boss
/// 脱战 {@value #DISENGAGE_TICKS} tick，随后无死亡奖励、无击杀消息地消失。</p>
public abstract class BaseBoss extends BaseMonster implements Boss {
    protected final ServerBossEvent bossEvent;
    protected final Blackboard blackboard = new Blackboard();
    protected final List<Entity> subEntities = new ArrayList<>();
    protected float ironGolemResistance = 0.4f;
    protected float explosionResistance = 0.5f;
    protected int noTargetTicks = 0;
    /// 1.21 contract: discard after incrementing beyond tick 100.
    protected static final int DISENGAGE_TICKS = 100;
    private static final byte PHASE_PARTICLE_EVENT = 60;
    private static final byte DEATH_PARTICLE_EVENT = 61;
    private @Nullable UUID synchronizedCombatTarget;
    /// 本次遭遇中曾成为目标或直接攻击过 Boss 的玩家；仅存 UUID，避免跨卸载强引用。
    private final Set<UUID> combatParticipantIds = new LinkedHashSet<>();
    /// 已在机械三王同时存活期间共同参与三场遭遇的玩家。
    private final Set<UUID> mechanicalMayhemParticipantIds = new LinkedHashSet<>();
    /// 保证同一场遭遇的掉落、击败记录与成就只进入一次结算流程。
    private boolean deathRewardsSettled;
    private boolean removingSubEntities;

    public BaseBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(), getBossBarColor(), BossEvent.BossBarOverlay.PROGRESS)
                .setDarkenScreen(true).setPlayBossMusic(true);
    }

    // === Boss bar ===

    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        bossEvent.setName(getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
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
    /// <p>默认保持 1.21 侧按玩家数量线性放大的行为；需要复现原作特殊多人公式的 Boss
    /// 可以单独覆写。传入值已经限制在一至八人。</p>
    double getBossHealthPlayerMultiplier(int playerCount) {
        return playerCount;
    }

    // === 多部件生命周期 ===

    public void addSubEntity(Entity part) {
        if (!subEntities.contains(part)) subEntities.add(part);
    }

    public void removeSubEntity(Entity part) {
        subEntities.remove(part);
    }

    public List<Entity> getSubEntities() {
        return subEntities;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (level() instanceof ServerLevel serverLevel
                && (reason == RemovalReason.DISCARDED || reason.shouldDestroy())) {
            // Boss 永久离场后不会再消费从属死亡邮箱，必须同时清除其持久化记录。
            BossChildDeathLedger.clear(serverLevel, getUUID());
        }
        removingSubEntities = true;
        try {
            for (Entity part : List.copyOf(subEntities)) {
                /// 区块卸载时只清理可重建的临时部件；主动撤离和真正销毁必须
                /// 清理全部尚未移除的从属。不能用 isAlive() 过滤，因为已经
                /// 进入死亡动画但仍留在世界中的从属同样属于本场遭遇。
                if (!part.isRemoved() && (part instanceof BaseBossPart<?>
                        || reason == RemovalReason.DISCARDED || reason.shouldDestroy())) {
                    part.remove(reason);
                }
            }
            subEntities.clear();
            super.remove(reason);
        } finally {
            removingSubEntities = false;
        }
    }

    /// 当前是否正由本体级联移除子实体。
    ///
    /// <p>可破坏的从属生物会在正常死亡时回报本体；Boss 撤离时的级联移除不是“子实体被玩家
    /// 击败”，不得写入死亡账本或触发阶段推进。</p>
    final boolean isRemovingSubEntities() {
        return removingSubEntities;
    }

    // === 难度 ===

    protected boolean isExpert() {
        return LibUtils.isAtLeastExpert(level(), blockPosition());
    }

    protected boolean isMaster() {
        return LibUtils.isMaster(level(), blockPosition());
    }

    /// 当前世界是否启用了“受够了”特殊种子规则。
    ///
    /// <p>1.21 侧通过 TerraEntity 的工具方法再由本体 Mixin 接入世界种子；合并到 1.20 后
    /// 直接读取同一个本体种子标志，避免重新引入只为一次查询服务的桥接层。</p>
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
        return super.hurt(source, amount);
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

    @Override
    public void tick() {
        LivingEntity targetBeforeAi = level().isClientSide ? null : getTarget();
        super.tick();
        if (isRemoved()) return;
        if (level().isClientSide) {
            if (tickCount == 1) spawnParticleBurst(ParticleTypes.POOF, 24, 0.16);
        } else {
            MechanicalMayhemTracker.observe(this);
            updateCombatLifecycle(targetBeforeAi);
        }
    }

    /// 更新多人目标与脱战计时。该方法只在服务端调用。
    ///
    /// <p>已有合格目标时保持仇恨稳定，不会因为另一名玩家仇恨值更高就每 tick 来回换目标；只有当前
    /// 目标失效时才选择追踪范围内仇恨值最高的替代玩家。没有替代者时立即清空目标，使行为树停止
    /// 攻击，再进入宽限计时。</p>
    private void updateCombatLifecycle(@Nullable LivingEntity targetBeforeAi) {
        LivingEntity currentTarget = targetBeforeAi != null ? targetBeforeAi : getTarget();
        Player combatPlayer = currentTarget instanceof Player player
                && isValidCurrentCombatPlayer(player) ? player : null;
        if (combatPlayer == null) {
            combatPlayer = findCombatPlayer();
        }
        if (combatPlayer != null) registerCombatParticipant(combatPlayer);
        if (getTarget() != combatPlayer) setTarget(combatPlayer);

        synchronizeCombatTarget(combatPlayer);
        if (combatPlayer != null) {
            noTargetTicks = 0;
            return;
        }

        if (++noTargetTicks > DISENGAGE_TICKS
                && CommonConfigs.BOSS_CLEAR_WHEN_NO_TARGET.get()
                && shouldDiscardWhenNoTarget()) {
            onDisengageComplete();
        }
    }

    /// 从本维度追踪范围内的在线玩家中寻找替代目标。
    ///
    /// <p>只比较玩家的 AGGRO 属性；距离和奖励参与者登记都不影响排序。多个玩家并列最高值时，
    /// 在并列集合内随机选择。</p>
    protected @Nullable Player findCombatPlayer() {
        return selectCombatPlayer(level().players());
    }

    /// Package-visible pure candidate selection seam used by deterministic GameTests.
    final @Nullable Player selectCombatPlayer(List<? extends Player> candidates) {
        double range = getCombatPlayerRange();
        double rangeSqr = range * range;
        double maximumAggro = Double.NEGATIVE_INFINITY;
        List<Player> maximumPlayers = new ArrayList<>();
        for (Player player : candidates) {
            if (!isEligibleRetargetCandidate(player)
                    || distanceToSqr(player) >= rangeSqr) continue;
            var aggro = player.getAttribute(ConfluenceMagicLib.AGGRO.get());
            double value = aggro == null ? 0.0D : aggro.getValue();
            int comparison = Double.compare(value, maximumAggro);
            if (comparison > 0) {
                maximumAggro = value;
                maximumPlayers.clear();
                maximumPlayers.add(player);
            } else if (comparison == 0) {
                maximumPlayers.add(player);
            }
        }
        return maximumPlayers.isEmpty()
                ? null : maximumPlayers.get(random.nextInt(maximumPlayers.size()));
    }

    /// 把玩家登记为本场遭遇参与者。正常游戏中由锁定目标和玩家伤害自动登记；包级可见性还让
    /// GameTest 能登记不属于 {@link ServerLevel#players()} 的生存模拟玩家，而无需放宽生产规则。
    final void registerCombatParticipant(Player player) {
        combatParticipantIds.add(player.getUUID());
    }

    /// 由 Boss 召唤道具创建实体时，立即把使用者绑定到本次遭遇。
    ///
    /// <p>自然生成的 Boss 仍然可以依靠附近玩家搜索接管战斗；召唤道具路径则必须显式登记召唤者，
    /// 否则刚生成的 Boss 会短暂进入无目标分支，飞行 Boss 在客户端实测中会表现为沉底或贴地滑行。</p>
    public final void initializeSummonedCombat(Player player) {
        registerCombatParticipant(player);
        setTarget(player);
        noTargetTicks = 0;
    }

    final Set<UUID> combatParticipantIdsSnapshot() {
        return Set.copyOf(combatParticipantIds);
    }

    final void confirmMechanicalMayhemParticipants(Set<UUID> participantIds) {
        mechanicalMayhemParticipantIds.addAll(participantIds);
    }

    public final boolean isMechanicalMayhemParticipant(UUID playerId) {
        return mechanicalMayhemParticipantIds.contains(playerId);
    }

    /// 取得本场遭遇当前仍在线的参战玩家快照。
    ///
    /// <p>返回值按照首次登记顺序生成且不可修改。玩家死亡、切换游戏模式或离开战斗半径不会
    /// 抹掉已经发生的参战事实；已经离线、离开 Boss 所在维度或从未登记过的玩家不会进入本次
    /// 即时结算。调用方必须保存本次返回值完成整轮结算，不能在发放过程中反复读取。</p>
    public final List<ServerPlayer> getOnlineCombatParticipants() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return List.of();
        }
        return resolveOnlineCombatParticipants(serverLevel.players());
    }

    /// Package-visible pure online-resolution seam used without mutating the server player list.
    final List<ServerPlayer> resolveOnlineCombatParticipants(List<ServerPlayer> onlinePlayers) {
        List<ServerPlayer> participants = new ArrayList<>(combatParticipantIds.size());
        for (UUID participantId : combatParticipantIds) {
            for (ServerPlayer player : onlinePlayers) {
                if (player.getUUID().equals(participantId)) {
                    participants.add(player);
                    break;
                }
            }
        }
        return List.copyOf(participants);
    }

    /// 尝试取得本场遭遇的死亡结算权。
    ///
    /// <p>死亡事件、部件转发或外部联动可能在同一 tick 重复请求结算。只有第一次调用返回
    /// {@code true}，后续调用不得再次生成奖励或推进击败记录。</p>
    public final boolean tryBeginDeathRewardSettlement() {
        if (deathRewardsSettled) return false;
        deathRewardsSettled = true;
        return true;
    }

    /// 判定玩家能否维持当前 Boss 战。创造、旁观、死亡和跨维度玩家都不计入多人存活人数。
    protected boolean isValidCurrentCombatPlayer(Player player) {
        return player.level() == level() && player.isAlive()
                && !player.isCreative() && !player.isSpectator();
    }

    private boolean isEligibleRetargetCandidate(Player player) {
        return player.level() == level() && player.canBeSeenAsEnemy();
    }

    /// Boss 战保留半径。默认跟随 {@link Attributes#FOLLOW_RANGE}，但至少为 16 格；静态大型
    /// 遭遇或不可见管理实体可覆盖此值，使用实际竞技场尺寸。
    protected double getCombatPlayerRange() {
        return Math.max(16.0, getAttributeValue(Attributes.FOLLOW_RANGE));
    }

    /// 计算玩家到整场遭遇最近锚点的平方距离。活着的独立部件也算锚点，因此多人分别牵制
    /// Boss 本体和部件时不会因只远离本体而错误脱战。
    protected double combatAnchorDistanceSqr(Player player) {
        double nearest = distanceToSqr(player);
        for (Entity part : subEntities) {
            if (part.isAlive()) {
                nearest = Math.min(nearest, part.distanceToSqr(player));
            }
        }
        return nearest;
    }

    /// 在目标周围寻找适合飞行 Boss 的传送点。
    ///
    /// <p>这里不能使用世界高度图：高度图只返回整列最高表面，地下 Boss 会因此穿出洞穴或
    /// 竞技场。候选点以目标当前高度为中心，并同时检查区块加载、实体碰撞和液体占用；
    /// 找不到安全位置时返回 {@code null}，调用方应保持原位等待下一次尝试。</p>
    protected final @Nullable Vec3 findFlyingTeleportPosition(
            LivingEntity target, double minimumRadius, double maximumRadius,
            double verticalRadius, int attempts) {
        if (!(level() instanceof ServerLevel serverLevel)) return null;
        for (int attempt = 0; attempt < attempts; attempt++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            double radius = Mth.lerp(random.nextDouble(), minimumRadius, maximumRadius);
            double x = target.getX() + Math.cos(angle) * radius;
            double y = Mth.clamp(
                    target.getY() + Mth.lerp(random.nextDouble(), -verticalRadius, verticalRadius),
                    serverLevel.getMinBuildHeight() + 1.0,
                    serverLevel.getMaxBuildHeight() - getBbHeight() - 1.0);
            double z = target.getZ() + Math.sin(angle) * radius;
            BlockPos blockPos = BlockPos.containing(x, y, z);
            AABB destinationBounds = getBoundingBox().move(
                    x - getX(), y - getY(), z - getZ());
            if (serverLevel.hasChunkAt(blockPos)
                    && serverLevel.noCollision(this, destinationBounds)
                    && !serverLevel.containsAnyLiquid(destinationBounds)) {
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

    /// 遭遇的权威玩家目标发生变化时调用。默认只在彻底失去玩家时清除从属生物残留的玩家目标；
    /// 双子魔眼等由不可见管理实体组织的遭遇可覆盖此方法，把新目标同步给实际战斗实体。
    protected void onCombatTargetChanged(@Nullable Player target) {
        if (target != null) return;
        for (Entity part : List.copyOf(subEntities)) {
            if (part instanceof Mob mob && mob.getTarget() instanceof Player) {
                mob.setTarget(null);
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
            level().addParticle(particle, x, y, z,
                    random.nextGaussian() * speed * radius,
                    random.nextGaussian() * speed,
                    random.nextGaussian() * speed * radius);
        }
    }

    // === 存档 ===

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("CombatParticipants", saveUuidSet(combatParticipantIds));
        tag.put("MechanicalMayhemParticipants", saveUuidSet(mechanicalMayhemParticipantIds));
        tag.putBoolean("DeathRewardsSettled", deathRewardsSettled);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        loadUuidSet(tag, "CombatParticipants", combatParticipantIds);
        loadUuidSet(tag, "MechanicalMayhemParticipants", mechanicalMayhemParticipantIds);
        deathRewardsSettled = tag.getBoolean("DeathRewardsSettled");
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

    public static AttributeSupplier.Builder createBossAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    // === Death ===

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide) level().broadcastEntityEvent(this, DEATH_PARTICLE_EVENT);
        Boss.sendBossDeathMessage(this);
        super.die(source);
    }
}
