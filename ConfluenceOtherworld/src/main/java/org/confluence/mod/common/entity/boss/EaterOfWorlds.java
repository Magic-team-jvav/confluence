package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 由可独立破坏体节和可分裂链条组成的世界吞噬怪战斗实体。
///
/// 体节被摧毁后，剩余链条会重新选举头尾并继续战斗；Boss 栏则由主头部统一汇总。
public class EaterOfWorlds extends BaseWormBoss {
    // 一条完整世界吞噬者的初始体节数，以及难度/多人倍率前的头部与单体节生命。
    public static final int INITIAL_SEGMENT_COUNT = 60;
    public static final float HEAD_MAX_HEALTH = 54.0F;
    public static final float NODE_MAX_HEALTH = 50.0F;
    private static final float NODE_ARMOR = 6.0F;
    private static final float ENCOUNTER_MAX_HEALTH = HEAD_MAX_HEALTH + NODE_MAX_HEALTH * INITIAL_SEGMENT_COUNT;
    private static final String ENCOUNTER_TAG = "Encounter";
    private static final String PRIMARY_TAG = "PrimaryHead";
    private static final String SEGMENT_COUNT_TAG = "ActiveSegmentCount";
    private static final String SEGMENT_HEALTHS_TAG = "SegmentHealths";
    private static final String MOVEMENT_PHASE_TAG = "MovementPhase";
    private static final String MOVEMENT_TICKS_TAG = "MovementTicks";
    private static final String WANDER_TARGET_TAG = "WanderTarget";
    private static final String WANDER_CURVE_START_TAG = "WanderCurveStart";
    private static final String WANDER_CURVE_CONTROL_TAG = "WanderCurveControl";
    private static final String NEXT_WANDER_BELOW_TAG = "NextWanderBelow";
    // 三段移动状态的持续时间，单位为 tick。1.21 的游走曲线以 80 tick 为基准，但其目标
    // 缺失时才真正运行；这里在战斗中也持续绘制曲线，因此压缩到 60 tick，避免有效追击
    // 时间被低速游走吞掉。分裂出来的新头先 ALIGN，再进入 DASH。
    private static final int WANDER_TICKS = 60;
    private static final int ALIGN_TICKS = 100;
    private static final int DASH_TICKS = 120;
    // 常态追击的速度上限（方块/tick）及跨开阔场地重新索敌的半径（方块）。
    private static final double PURSUIT_SPEED = 0.62;
    private static final double TARGET_SEARCH_RANGE = 300.0D;

    private static final EntityDataAccessor<Optional<UUID>> ENCOUNTER_UUID = SynchedEntityData.defineId(EaterOfWorlds.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> PRIMARY_HEAD = SynchedEntityData.defineId(EaterOfWorlds.class, EntityDataSerializers.BOOLEAN);
    // 仅保存当前服务端世界内同一场世界吞噬者的多个分裂头；弱键避免卸载世界被静态表持有。
    private static final Map<ServerLevel, Map<UUID, EncounterState>> ENCOUNTERS = new WeakHashMap<>();

    private final List<Float> segmentHealths = new ArrayList<>();
    private @Nullable List<Vec3> pendingSegmentPositions;
    private int activeSegmentCount = INITIAL_SEGMENT_COUNT;
    private boolean restructuring;
    private boolean headDeathRuleHandled;
    private MovementPhase movementPhase = MovementPhase.ALIGN;
    private int movementTicks;
    private Vec3 wanderTarget = Vec3.ZERO;
    private Vec3 wanderCurveStart = Vec3.ZERO;
    private Vec3 wanderCurveControl = Vec3.ZERO;
    private boolean nextWanderBelow = true;
    private int clientLerpSteps;
    private double clientLerpX;
    private double clientLerpY;
    private double clientLerpZ;
    private float clientLerpYaw;
    private float clientLerpPitch;

    private enum MovementPhase {
        WANDER,
        ALIGN,
        DASH
    }

    private static final class EncounterState {
        private final LinkedHashSet<UUID> headIds = new LinkedHashSet<>();
        private @Nullable UUID primaryHeadId;
        private @Nullable UUID targetId;
    }

    public EaterOfWorlds(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 800;
        resetSegmentHealths(INITIAL_SEGMENT_COUNT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ENCOUNTER_UUID, Optional.empty());
        entityData.define(PRIMARY_HEAD, true);
    }

    @Override
    public void onAddedToWorld() {
        if (!level().isClientSide && getEncounterUUID() == null) {
            setEncounterUUID(getUUID());
        }
        super.onAddedToWorld();
        registerEncounterHead();
    }

    @Override
    protected int getSegmentCount() {
        return activeSegmentCount;
    }

    @Override
    public void initSegments() {
        super.initSegments();
        if (pendingSegmentPositions != null && segments.size() == activeSegmentCount) {
            List<Vec3> positions = pendingSegmentPositions;
            pendingSegmentPositions = null;
            restoreSegmentPositions(positions);
        }
    }

    @Override
    protected float getInitialSegmentHealth(int index) {
        return index >= 1 && index <= segmentHealths.size()
                ? segmentHealths.get(index - 1)
                : NODE_MAX_HEALTH;
    }

    @Override
    protected float getSegmentSpacing() {
        return 2.8F;
    }

    /// 出生时使用盘曲链，而不是把六十个碰撞体叠在头部中心。
    ///
    /// 每一节都以头部反向为基准逐渐增加偏航角；这样完整身体在有限区块内展开，
    /// 同时首刻就具备正确间距，不会产生模型堆叠和批量接触伤害。
    @Override
    protected Vec3 getInitialSegmentPosition(int index, Vec3 previousPosition) {
        Vec3 direction = getLookAngle().multiply(1.0, 0.0, 1.0);
        if (direction.lengthSqr() <= 1.0E-7) {
            direction = new Vec3(0.0, 0.0, 1.0);
        }
        float curve = (0.2F - index * 0.08F / INITIAL_SEGMENT_COUNT) * index;
        // 1.21 的延迟生成会让第 i 节额外下降 i*0.3 格；这里把相同落差并入每一步，
        // 再归一化到权威中心距。出生链因此是三维螺旋，而不是一张水平圆盘接上垂直头部轨迹。
        Vec3 step = direction.normalize().scale(-1.0D).yRot(curve).add(0.0D, -0.3D, 0.0D);
        return previousPosition.add(step.normalize().scale(getEffectiveSegmentSpacing()));
    }

    @Override
    protected boolean hurtSegment(BossWormPart segment, DamageSource source, float amount) {
        if (restructuring || segment.getOwner() != this || segment.isInvulnerableTo(source) || amount <= 0.0F) {
            return false;
        }
        if (source.getEntity() instanceof Player player) {
            registerCombatParticipant(player);
        }
        int index = segment.getSegmentIndex();
        if (index < 1 || index > segmentHealths.size()
                || index > segments.size()
                || segments.get(index - 1) != segment
                || segment.isRemoved()) {
            return false;
        }

        float appliedDamage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : CombatRules.getDamageAfterAbsorb(amount, NODE_ARMOR, 0.0F);
        if (appliedDamage <= 0.0F) return false;
        float remaining = Math.max(0.0F, segmentHealths.get(index - 1) - appliedDamage);
        segmentHealths.set(index - 1, remaining);
        segment.setPartHealth(remaining);
        segment.indicateHurt();
        if (level() instanceof ServerLevel serverLevel) {
            segment.playSound(SoundEvents.GENERIC_HURT, 0.8F, 0.9F + random.nextFloat() * 0.2F);
            Vec3 center = segment.getBoundingBox().getCenter();
            double spreadX = Math.max(0.15D, segment.getBbWidth() * 0.22D);
            double spreadY = Math.max(0.15D, segment.getBbHeight() * 0.22D);
            serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                    center.x, center.y, center.z, 5,
                    spreadX, spreadY, spreadX, 0.05D);
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    center.x, center.y, center.z, 8,
                    spreadX, spreadY, spreadX, 0.12D);
        }
        if (source.getEntity() instanceof LivingEntity attacker && canAttack(attacker))
            setTarget(attacker);
        if (remaining <= 0.0F) {
            if (level() instanceof ServerLevel serverLevel) {
                Vec3 center = segment.getBoundingBox().getCenter();
                serverLevel.sendParticles(ParticleTypes.POOF,
                        center.x, center.y, center.z, 18,
                        segment.getBbWidth() * 0.35D,
                        segment.getBbHeight() * 0.35D,
                        segment.getBbWidth() * 0.35D, 0.08D);
            }
            dropSegmentLoot(segment, source);
            splitAt(index, source);
        }
        return true;
    }

    private void dropSegmentLoot(BossWormPart segment, DamageSource source) {
        if (!(level() instanceof ServerLevel serverLevel) || !serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
            return;
        LootParams.Builder params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, segment.position())
                .withParameter(LootContextParams.THIS_ENTITY, segment)
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity())
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity());
        if (source.getEntity() instanceof ServerPlayer player) {
            params.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
        }
        LootTable table = serverLevel.getServer().getLootData().getLootTable(BossEntities.EATER_OF_WORLDS.get().getDefaultLootTable());
        for (ItemStack stack : table.getRandomItems(params.create(LootContextParamSets.ENTITY))) {
            serverLevel.addFreshEntity(new ItemEntity(serverLevel, segment.getX(), segment.getY(), segment.getZ(), stack));
        }
    }

    /// 世界吞噬怪不能使用原版地面导航。
    ///
    /// 它始终在三维空间中穿过方块移动，因此行为树只保留生命周期时钟，具体移动由
    /// 本类的服务端状态机统一处理。这样不会在地面路径创建失败后静止。
    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    private void splitAt(int destroyedIndex, DamageSource source) {
        if (!(level() instanceof ServerLevel) || destroyedIndex < 1 || destroyedIndex > activeSegmentCount || restructuring)
            return;
        restructuring = true;
        try {
            List<Float> oldHealths = List.copyOf(segmentHealths);
            List<Float> frontHealths = new ArrayList<>(oldHealths.subList(0, destroyedIndex - 1));
            List<Vec3> frontPositions = segments.subList(0, destroyedIndex - 1).stream()
                    .map(BossWormPart::position).toList();
            EaterOfWorlds rearHead = null;

            boolean rearTakesPrimaryRole = isMainBody() && frontHealths.isEmpty();
            if (destroyedIndex < activeSegmentCount) {
                BossWormPart rearHeadPart = segments.get(destroyedIndex);
                float rearHeadHealth = oldHealths.get(destroyedIndex);
                List<Float> rearHealths = new ArrayList<>(oldHealths.subList(destroyedIndex + 1, oldHealths.size()));
                List<Vec3> rearPositions = segments.subList(destroyedIndex + 1, segments.size()).stream()
                        .map(BossWormPart::position).toList();
                rearHead = spawnSplitHead(rearHeadPart.position(), rearHeadPart.getYRot(), rearHeadPart.getXRot(),
                        rearHeadHealth, rearHealths, rearPositions, rearTakesPrimaryRole, true);
                if (rearHead == null) {
                    frontHealths.add(rearHeadHealth);
                    frontHealths.addAll(rearHealths);
                    frontPositions = new ArrayList<>(frontPositions);
                    frontPositions.add(rearHeadPart.position());
                    frontPositions.addAll(rearPositions);
                }
            }

            // 断口前只剩头部时，该孤立节点不再构成一条有效链。后半条已接管主头职责时，
            // 直接结束旧头而不触发整场 Boss 的死亡结算。
            if (frontHealths.isEmpty() && rearHead != null) {
                spawnHeadDeathEater(source);
                discardSegments();
                activeSegmentCount = 0;
                segmentHealths.clear();
                if (isMainBody()) setPrimaryHead(false);
                discard();
                return;
            }

            rebuildWithHealths(frontHealths, frontPositions);
            if (rearHead != null && getTarget() != null) rearHead.setTarget(getTarget());

            // 头后方只剩被摧毁的最后一节时，整条链已经没有身体，按头节点死亡结束。
            if (frontHealths.isEmpty()) {
                setHealth(0.0F);
                die(source);
            }
        } finally {
            restructuring = false;
        }
    }

    private void rebuildWithHealths(List<Float> healths, List<Vec3> positions) {
        discardSegments();
        activeSegmentCount = Math.min(INITIAL_SEGMENT_COUNT, healths.size());
        segmentHealths.clear();
        for (int index = 0; index < activeSegmentCount; index++) {
            segmentHealths.add(Mth.clamp(healths.get(index), 0.0F, NODE_MAX_HEALTH));
        }
        initSegments();
        restoreSegmentPositions(positions);
    }

    private void restoreSegmentPositions(List<Vec3> positions) {
        int count = Math.min(segments.size(), positions.size());
        for (int index = 0; index < count; index++) {
            Vec3 position = positions.get(index);
            segments.get(index).setPos(position.x, position.y, position.z);
        }
        for (BossWormPart segment : segments) segment.updateSegmentRotation();
    }

    private @Nullable EaterOfWorlds spawnSplitHead(Vec3 position, float yaw, float pitch, float headHealth,
                                                   List<Float> bodyHealths, List<Vec3> bodyPositions,
                                                   boolean primary, boolean divergeFromSource) {
        if (!(level() instanceof ServerLevel serverLevel)) return null;
        EaterOfWorlds head = BossEntities.EATER_OF_WORLDS.get().create(level());
        if (head == null) return null;

        head.setPos(position);
        head.setRot(yaw, pitch);
        head.setEncounterUUID(getEncounterUUID() == null ? getUUID() : getEncounterUUID());
        head.setPrimaryHead(false);
        head.activeSegmentCount = Math.min(INITIAL_SEGMENT_COUNT, bodyHealths.size());
        head.segmentHealths.clear();
        for (int index = 0; index < head.activeSegmentCount; index++) {
            head.segmentHealths.add(Mth.clamp(bodyHealths.get(index), 0.0F, NODE_MAX_HEALTH));
        }
        BossMultiplayerEnhancement.copyEncounterScaling(this, head);
        head.setHealth(Mth.clamp(headHealth, 0.1F, (float) head.getMaxHealth()));
        head.inheritEncounterState(this);
        head.pendingSegmentPositions = List.copyOf(bodyPositions);
        // 断节新链先从自己的接近通道对齐，再冲向玩家。直接进入 DASH 会让所有分裂头
        // 同时走目标中心线，长链在玩家附近交叉成看似分叉的巨大结团。
        head.movementPhase = divergeFromSource ? MovementPhase.ALIGN : MovementPhase.DASH;
        head.movementTicks = 0;
        head.wanderTarget = Vec3.ZERO;
        head.nextWanderBelow = !nextWanderBelow;
        Vec3 inheritedVelocity = getDeltaMovement();
        LivingEntity splitTarget = getTarget();
        if (splitTarget != null && splitTarget.isAlive()) head.setTarget(splitTarget);
        Vec3 initialVelocity = inheritedVelocity;
        if (divergeFromSource && splitTarget != null && splitTarget.isAlive()) {
            Vec3 towardTarget = head.getEncounterApproachPoint(splitTarget).subtract(position);
            if (towardTarget.lengthSqr() > 1.0E-7D) {
                initialVelocity = towardTarget.normalize().scale(PURSUIT_SPEED);
                head.faceCombatDirection(initialVelocity, 180.0F, 180.0F);
            }
        }
        head.setDeltaMovement(initialVelocity);
        head.setPersistenceRequired();
        List<ServerPlayer> viewers = primary ? List.copyOf(bossEvent.getPlayers()) : List.of();
        if (!serverLevel.addFreshEntity(head)) {
            head.discard();
            return null;
        }
        // 加入世界通常会同步触发体节初始化；显式补一次可覆盖不同加载时序，
        // pending 列表只会在链条完整后消费，因此不会空跑或重复覆盖。
        head.initSegments();
        head.setPrimaryHead(primary);
        for (ServerPlayer viewer : viewers) head.bossEvent.addPlayer(viewer);
        return head;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide) spawnHeadDeathEater(source);
        if (!level().isClientSide && !restructuring && activeSegmentCount > 0 && !segments.isEmpty()) {
            BossWormPart promotedPart = segments.get(0);
            List<Float> remainingBody = new ArrayList<>(segmentHealths.subList(1, segmentHealths.size()));
            List<Vec3> remainingPositions = segments.subList(1, segments.size()).stream()
                    .map(BossWormPart::position).toList();
            EaterOfWorlds promoted = spawnSplitHead(promotedPart.position(), promotedPart.getYRot(), promotedPart.getXRot(),
                    segmentHealths.get(0), remainingBody, remainingPositions, isMainBody(), false);
            if (promoted != null) {
                setPrimaryHead(false);
                discard();
                return;
            }
        }

        if (!level().isClientSide && isMainBody()) {
            EaterOfWorlds successor = encounterHeads().stream()
                    .filter(candidate -> candidate != this && candidate.isAlive())
                    .findFirst().orElse(null);
            if (successor != null) {
                transferPrimaryRoleTo(successor);
            }
        }
        super.die(source);
    }

    private void spawnHeadDeathEater(DamageSource source) {
        if (headDeathRuleHandled) return;
        headDeathRuleHandled = true;
        if (!isFtw() || !(level() instanceof ServerLevel serverLevel)) return;

        var eater = MonsterEntities.EATER_OF_SOULS.get().create(serverLevel);
        if (eater == null) return;
        eater.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());

        LivingEntity inheritedTarget = getTarget();
        if (inheritedTarget == null || !inheritedTarget.isAlive() || !eater.canAttack(inheritedTarget)) {
            inheritedTarget = source.getEntity() instanceof LivingEntity attacker
                    && attacker.isAlive() && eater.canAttack(attacker)
                    ? attacker
                    : null;
        }
        if (inheritedTarget != null) eater.setTarget(inheritedTarget);

        // 注册表目前只有标准噬魂怪实体；生成点和目标继承集中在这里，后续增加大型变体时
        // 只需替换实体工厂，不会污染普通体节断裂逻辑。
        serverLevel.addFreshEntity(eater);
    }

    private void transferPrimaryRoleTo(EaterOfWorlds successor) {
        if (successor == this) return;
        List<ServerPlayer> viewers = List.copyOf(bossEvent.getPlayers());
        successor.setPrimaryHead(true);
        for (ServerPlayer viewer : viewers) successor.bossEvent.addPlayer(viewer);
        setPrimaryHead(false);
    }

    public @Nullable UUID getEncounterUUID() {
        return entityData.get(ENCOUNTER_UUID).orElse(null);
    }

    private void setEncounterUUID(UUID uuid) {
        entityData.set(ENCOUNTER_UUID, Optional.of(uuid));
    }

    private void setPrimaryHead(boolean primary) {
        entityData.set(PRIMARY_HEAD, primary);
        if (level().isClientSide) return;
        if (level() instanceof ServerLevel serverLevel && getEncounterUUID() != null) {
            EncounterState state = encounterState(serverLevel, getEncounterUUID());
            if (primary) {
                state.primaryHeadId = getUUID();
            } else if (getUUID().equals(state.primaryHeadId)) {
                state.primaryHeadId = null;
            }
        }
        if (!primary) {
            bossEvent.removeAllPlayers();
        } else if (level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                if (distanceToSqr(player) <= 16384.0) bossEvent.addPlayer(player);
            }
        }
    }

    @Override
    public boolean isMainBody() {
        return entityData.get(PRIMARY_HEAD);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (!isMainBody()) bossEvent.removePlayer(player);
    }

    @Override
    protected float getBossBarProgress() {
        if (!isMainBody()) return super.getBossBarProgress();
        float remainingHealth = 0.0F;
        for (EaterOfWorlds head : encounterHeads()) remainingHealth += head.chainHealth();
        return remainingHealth / ENCOUNTER_MAX_HEALTH;
    }

    private float chainHealth() {
        float total = getHealth();
        for (float health : segmentHealths) total += health;
        return total;
    }

    private List<EaterOfWorlds> encounterHeads() {
        UUID encounter = getEncounterUUID();
        if (encounter == null) return isAlive() ? List.of(this) : List.of();
        if (!(level() instanceof ServerLevel serverLevel)) {
            return level().getEntitiesOfClass(EaterOfWorlds.class, getBoundingBox().inflate(512.0),
                    candidate -> candidate.isAlive() && encounter.equals(candidate.getEncounterUUID()));
        }

        EncounterState state = encounterState(serverLevel, encounter);
        state.headIds.add(getUUID());
        List<EaterOfWorlds> heads = new ArrayList<>(state.headIds.size());
        var iterator = state.headIds.iterator();
        while (iterator.hasNext()) {
            UUID headId = iterator.next();
            var entity = serverLevel.getEntity(headId);
            if (entity instanceof EaterOfWorlds candidate
                    && candidate.isAlive()
                    && encounter.equals(candidate.getEncounterUUID())) {
                heads.add(candidate);
            } else {
                iterator.remove();
                if (headId.equals(state.primaryHeadId)) state.primaryHeadId = null;
            }
        }
        heads.sort(Comparator.comparing(EaterOfWorlds::getUUID));
        return heads;
    }

    private void registerEncounterHead() {
        if (!(level() instanceof ServerLevel serverLevel) || getEncounterUUID() == null) return;
        EncounterState state = encounterState(serverLevel, getEncounterUUID());
        state.headIds.add(getUUID());
        EaterOfWorlds currentPrimary = resolveLoadedHead(serverLevel, state.primaryHeadId, getEncounterUUID());
        if (isMainBody()) {
            if (currentPrimary != null && currentPrimary != this)
                currentPrimary.setPrimaryHead(false);
            setPrimaryHead(true);
        } else if (currentPrimary == null) {
            // 分裂后的任意已加载链都必须能维持整场战斗；原主头稍后加载时会重新接管。
            setPrimaryHead(true);
        }
    }

    private void unregisterEncounterHead() {
        if (!(level() instanceof ServerLevel serverLevel) || getEncounterUUID() == null) return;
        Map<UUID, EncounterState> levelStates = ENCOUNTERS.get(serverLevel);
        if (levelStates == null) return;
        EncounterState state = levelStates.get(getEncounterUUID());
        if (state == null) return;
        state.headIds.remove(getUUID());
        if (getUUID().equals(state.primaryHeadId)) state.primaryHeadId = null;
        if (state.primaryHeadId == null) {
            for (UUID candidateId : List.copyOf(state.headIds)) {
                EaterOfWorlds candidate = resolveLoadedHead(serverLevel, candidateId, getEncounterUUID());
                if (candidate != null) {
                    candidate.setPrimaryHead(true);
                    break;
                }
                state.headIds.remove(candidateId);
            }
        }
        if (state.headIds.isEmpty()) levelStates.remove(getEncounterUUID());
        if (levelStates.isEmpty()) ENCOUNTERS.remove(serverLevel);
    }

    private static @Nullable EaterOfWorlds resolveLoadedHead(ServerLevel level, @Nullable UUID headId, UUID encounterId) {
        if (headId == null) return null;
        return level.getEntity(headId) instanceof EaterOfWorlds head
                && head.isAlive()
                && encounterId.equals(head.getEncounterUUID())
                ? head : null;
    }

    private static EncounterState encounterState(ServerLevel level, UUID encounter) {
        return ENCOUNTERS.computeIfAbsent(level, ignored -> new java.util.HashMap<>())
                .computeIfAbsent(encounter, ignored -> new EncounterState());
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!level().isClientSide) unregisterEncounterHead();
    }

    private void resetSegmentHealths(int count) {
        segmentHealths.clear();
        for (int index = 0; index < count; index++) segmentHealths.add(NODE_MAX_HEALTH);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID encounter = getEncounterUUID();
        if (encounter != null) tag.putUUID(ENCOUNTER_TAG, encounter);
        tag.putBoolean(PRIMARY_TAG, isMainBody());
        tag.putInt(SEGMENT_COUNT_TAG, activeSegmentCount);
        ListTag healths = new ListTag();
        for (float health : segmentHealths) healths.add(FloatTag.valueOf(health));
        tag.put(SEGMENT_HEALTHS_TAG, healths);
        tag.putInt(MOVEMENT_PHASE_TAG, movementPhase.ordinal());
        tag.putInt(MOVEMENT_TICKS_TAG, movementTicks);
        ListTag target = new ListTag();
        target.add(FloatTag.valueOf((float) wanderTarget.x));
        target.add(FloatTag.valueOf((float) wanderTarget.y));
        target.add(FloatTag.valueOf((float) wanderTarget.z));
        tag.put(WANDER_TARGET_TAG, target);
        tag.put(WANDER_CURVE_START_TAG, writeVector(wanderCurveStart));
        tag.put(WANDER_CURVE_CONTROL_TAG, writeVector(wanderCurveControl));
        tag.putBoolean(NEXT_WANDER_BELOW_TAG, nextWanderBelow);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(ENCOUNTER_TAG)) setEncounterUUID(tag.getUUID(ENCOUNTER_TAG));
        entityData.set(PRIMARY_HEAD, !tag.contains(PRIMARY_TAG) || tag.getBoolean(PRIMARY_TAG));
        activeSegmentCount = tag.contains(SEGMENT_COUNT_TAG)
                ? Mth.clamp(tag.getInt(SEGMENT_COUNT_TAG), 0, INITIAL_SEGMENT_COUNT)
                : INITIAL_SEGMENT_COUNT;
        ListTag healths = tag.getList(SEGMENT_HEALTHS_TAG, FloatTag.TAG_FLOAT);
        segmentHealths.clear();
        for (int index = 0; index < activeSegmentCount; index++) {
            float health = index < healths.size() ? healths.getFloat(index) : NODE_MAX_HEALTH;
            segmentHealths.add(Float.isFinite(health) ? Mth.clamp(health, 0.0F, NODE_MAX_HEALTH) : NODE_MAX_HEALTH);
        }
        int phaseIndex = Mth.clamp(tag.getInt(MOVEMENT_PHASE_TAG), 0, MovementPhase.values().length - 1);
        movementPhase = MovementPhase.values()[phaseIndex];
        movementTicks = Mth.clamp(tag.getInt(MOVEMENT_TICKS_TAG), 0, phaseDuration(movementPhase));
        if (tag.contains(WANDER_TARGET_TAG, Tag.TAG_LIST)) {
            ListTag target = tag.getList(WANDER_TARGET_TAG, FloatTag.TAG_FLOAT);
            wanderTarget = target.size() == 3
                    ? new Vec3(target.getFloat(0), target.getFloat(1), target.getFloat(2))
                    : Vec3.ZERO;
        } else {
            wanderTarget = Vec3.ZERO;
        }
        wanderCurveStart = readVector(tag, WANDER_CURVE_START_TAG);
        wanderCurveControl = readVector(tag, WANDER_CURVE_CONTROL_TAG);
        nextWanderBelow = tag.getBoolean(NEXT_WANDER_BELOW_TAG);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PURPLE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createWormBossAttributes()
                .add(Attributes.MAX_HEALTH, HEAD_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 11.5)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, TARGET_SEARCH_RANGE);
    }

    @Override
    protected double getCombatPlayerRange() {
        // 分裂后的多条链可能同时位于竞技场两端；实际距离由所有头部和体节
        // 的最近锚点共同判定，因此这里需要覆盖整场遭遇。
        return 512.0D;
    }

    @Override
    protected double combatAnchorDistanceSqr(Player player) {
        double nearest = super.combatAnchorDistanceSqr(player);
        UUID encounter = getEncounterUUID();
        if (encounter == null || !(level() instanceof ServerLevel)) return nearest;

        for (EaterOfWorlds head : encounterHeads()) {
            if (head == this) continue;
            nearest = Math.min(nearest, head.distanceToSqr(player));
            for (BossWormPart segment : head.segments) {
                if (segment.isAlive()) nearest = Math.min(nearest, segment.distanceToSqr(player));
            }
        }
        return nearest;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            acquireTargetFromHeadRange();
            restoreEncounterTargetBeforeLifecycle();
        }
        super.tick();
        if (isRemoved()) return;
        if (level().isClientSide) {
            tickClientInterpolation();
            return;
        }
        synchronizeEncounterTarget();
        tickWormMovement();
        refreshSegmentsAfterHeadMovement();
        tickEntityContactAttack();
    }

    /// 按 TerraEntity 的实际索敌语义，从每条已加载头部周围 300 格直接选择玩家。
    ///
    /// 公共 Boss 生命周期使用整场锚点距离维持既有目标，适合处理超长链条和分裂遭遇；
    /// 初次获取目标不能反过来依赖整条链的所有部件坐标，否则一个尚未初始化或异常的部件
    /// 就可能让距离结果失效。这里先以头部的有限坐标完成搜索，再交给遭遇层同步到其他头部。
    private void acquireTargetFromHeadRange() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        if (getTarget() instanceof Player current && isDirectlyTargetable(current)) return;

        double maximumAggro = Double.NEGATIVE_INFINITY;
        double nearestDistanceSqr = Double.POSITIVE_INFINITY;
        Player selected = null;
        for (ServerPlayer player : serverLevel.players()) {
            if (!isDirectlyTargetable(player)) continue;
            var aggro = player.getAttribute(ConfluenceMagicLib.AGGRO);
            double aggroValue = aggro == null ? 0.0D : aggro.getValue();
            if (!Double.isFinite(aggroValue)) aggroValue = 0.0D;
            double distanceSqr = distanceToSqr(player);
            int aggroComparison = Double.compare(aggroValue, maximumAggro);
            int distanceComparison = Double.compare(distanceSqr, nearestDistanceSqr);
            if (aggroComparison > 0
                    || (aggroComparison == 0 && distanceComparison < 0)
                    || (aggroComparison == 0 && distanceComparison == 0
                    && (selected == null || player.getUUID().compareTo(selected.getUUID()) < 0))) {
                maximumAggro = aggroValue;
                nearestDistanceSqr = distanceSqr;
                selected = player;
            }
        }
        if (selected == null) return;

        setTarget(selected);
        registerCombatParticipant(selected);
        UUID encounter = getEncounterUUID();
        if (encounter != null) {
            encounterState(serverLevel, encounter).targetId = selected.getUUID();
        }
    }

    private boolean isDirectlyTargetable(Player player) {
        if (player.level() != level() || !player.isAlive()
                || player.isCreative() || player.isSpectator()
                || !player.canBeSeenAsEnemy()) {
            return false;
        }
        double distanceSqr = distanceToSqr(player);
        return Double.isFinite(distanceSqr)
                && distanceSqr < TARGET_SEARCH_RANGE * TARGET_SEARCH_RANGE;
    }

    @Override
    protected boolean usesPostMovementContactAttack() {
        return true;
    }

    /// 分裂头可能先于主头进入本 tick。公共 Boss 生命周期运行前先从遭遇记录恢复目标，
    /// 避免各头因实体执行顺序不同而各自累计脱战计时。
    private void restoreEncounterTargetBeforeLifecycle() {
        if (!(level() instanceof ServerLevel serverLevel) || getEncounterUUID() == null) return;
        EncounterState state = encounterState(serverLevel, getEncounterUUID());
        Player sharedTarget = state.targetId == null ? null : serverLevel.getPlayerByUUID(state.targetId);
        if (sharedTarget != null && !isValidCurrentCombatPlayer(sharedTarget)) {
            sharedTarget = null;
        }
        if (sharedTarget == null && getTarget() instanceof Player current
                && isValidCurrentCombatPlayer(current)) {
            sharedTarget = current;
        }
        if (sharedTarget == null) {
            sharedTarget = findCombatPlayer();
        }
        if (sharedTarget != null) {
            state.targetId = sharedTarget.getUUID();
            registerCombatParticipant(sharedTarget);
            if (getTarget() != sharedTarget) setTarget(sharedTarget);
        }
    }

    private void synchronizeEncounterTarget() {
        if (!isMainBody() || !(level() instanceof ServerLevel serverLevel) || getEncounterUUID() == null)
            return;
        List<EaterOfWorlds> heads = encounterHeads();
        EncounterState state = encounterState(serverLevel, getEncounterUUID());
        Player sharedTarget = state.targetId == null ? null : serverLevel.getPlayerByUUID(state.targetId);
        if (sharedTarget != null && !isValidCurrentCombatPlayer(sharedTarget)) sharedTarget = null;
        if (sharedTarget == null) sharedTarget = getAuthoritativeCombatTarget();
        if (sharedTarget == null) sharedTarget = findCombatPlayer();
        if (sharedTarget == null) {
            for (EaterOfWorlds head : heads) {
                sharedTarget = head.getAuthoritativeCombatTarget();
                if (sharedTarget != null) break;
            }
        }
        state.targetId = sharedTarget == null ? null : sharedTarget.getUUID();
        if (sharedTarget == null) return;

        for (EaterOfWorlds head : heads) {
            if (!head.isValidCurrentCombatPlayer(sharedTarget)) continue;
            head.registerCombatParticipant(sharedTarget);
            if (head.getTarget() != sharedTarget) head.setTarget(sharedTarget);
        }
    }

    private void tickWormMovement() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            setDeltaMovement(getDeltaMovement().scale(0.8));
            return;
        }

        if (movementPhase == MovementPhase.WANDER && wanderTarget.equals(Vec3.ZERO)) {
            chooseWanderTarget(target);
        }
        movementTicks++;

        switch (movementPhase) {
            case WANDER -> {
                // TerraEntity 在这一段由独立 WormRandomWanderGoal 沿等速贝塞尔曲线推进；
                // 新架构把同一行为收进权威状态机，避免 FSM 显示“停止移动”而 Goal 暗中改速度。
                ensureWanderCurve(target);
                double progress = movementTicks / (double) WANDER_TICKS;
                Vec3 nextPosition = sampleUniformQuadratic(
                        wanderCurveStart, wanderCurveControl, wanderTarget, progress);
                moveDirectlyAlongCurve(nextPosition);
                if (movementTicks >= WANDER_TICKS) {
                    beginPhase(MovementPhase.ALIGN);
                }
            }
            case ALIGN -> {
                Vec3 attackPoint = getEncounterApproachPoint(target);
                Vec3 toTarget = attackPoint.subtract(position());
                double angle = angleBetween(getLookAngle(), toTarget);
                steerAndAdvance(attackPoint, isFtw() ? PURSUIT_SPEED * (1.5 / 1.1) : PURSUIT_SPEED, 6.5F);
                if (movementTicks >= ALIGN_TICKS || angle < Math.PI / 8.0 && toTarget.lengthSqr() < 20.0) {
                    beginPhase(MovementPhase.DASH);
                }
            }
            case DASH -> {
                Vec3 attackPoint = org.confluence.mod.common.entity.ai.BossMinionCoordinator
                        .predict(target, 4.0D, 4.0D);
                steerAndAdvance(attackPoint,
                        isFtw() ? PURSUIT_SPEED * (1.5 / 1.1) : PURSUIT_SPEED, 5.5F);
                if (movementTicks >= DASH_TICKS) {
                    beginPhase(MovementPhase.WANDER);
                    chooseWanderTarget(target);
                }
            }
        }
    }

    /// 为同一遭遇中的每条分裂虫链分配稳定接近通道。
    /// 单头时仍直接对准玩家；多头时以 UUID 稳定分散到玩家周围，避免链条同步穿过同一点。
    private Vec3 getEncounterApproachPoint(LivingEntity target) {
        Vec3 center = target.getBoundingBox().getCenter();
        if (encounterHeads().size() <= 1) return center;
        int hash = getUUID().hashCode();
        int slot = Math.floorMod(hash, 12);
        double angle = slot * Mth.TWO_PI / 12.0D;
        double vertical = (Math.floorMod(hash >>> 8, 5) - 2) * 1.5D;
        return center.add(Math.cos(angle) * 6.0D, vertical, Math.sin(angle) * 6.0D);
    }

    /// 世吞的移动由专用状态机在服务端直接提交，不能把速度留给下一刻的通用生物移动。
    /// 否则原版控制器会在真正位移前衰减或覆盖速度，表现为有目标却停在原地。
    private void steerAndAdvance(Vec3 destination, double speed, float maximumTurnDegrees) {
        steerInThreeDimensions(destination, speed, maximumTurnDegrees);
        Vec3 movement = getDeltaMovement();
        if (movement.lengthSqr() > 1.0E-7) {
            setPos(position().add(movement));
            hasImpulse = true;
        }
        setDeltaMovement(Vec3.ZERO);
    }

    /// 头部在本类 tick 的末段直接移动后，同一刻重新收紧体节链。
    private void refreshSegmentsAfterHeadMovement() {
        updateSegmentsAlongTrail();
    }

    private void beginPhase(MovementPhase phase) {
        movementPhase = phase;
        movementTicks = 0;
        if (phase != MovementPhase.WANDER) {
            wanderTarget = Vec3.ZERO;
            wanderCurveStart = Vec3.ZERO;
            wanderCurveControl = Vec3.ZERO;
        }
    }

    private void chooseWanderTarget(LivingEntity target) {
        double angle = random.nextDouble() * Mth.TWO_PI;
        double radius = 10.0;
        double verticalOffset = nextWanderBelow
                ? -10.0 - random.nextDouble() * 4.0
                : 5.0 + random.nextDouble() * 2.0;
        nextWanderBelow = !nextWanderBelow;
        wanderTarget = target.getBoundingBox().getCenter()
                .add(Math.sin(angle) * radius, verticalOffset, Math.cos(angle) * radius);
        wanderCurveStart = position();

        Vec3 tangent = getLookAngle();
        if (tangent.lengthSqr() <= 1.0E-7) {
            tangent = wanderTarget.subtract(wanderCurveStart);
        }
        if (tangent.lengthSqr() <= 1.0E-7) {
            tangent = new Vec3(0.0, 0.0, 1.0);
        }
        tangent = tangent.normalize();
        // 先沿当前头向延伸，再向目标高度的反方向轻微拱起，保留蠕虫钻出/钻入的弧线感。
        double counterArc = wanderTarget.y < wanderCurveStart.y ? 6.0D : -6.0D;
        wanderCurveControl = wanderCurveStart.add(tangent.scale(8.0D)).add(0.0D, counterArc, 0.0D);
    }

    private void ensureWanderCurve(LivingEntity target) {
        if (wanderTarget.equals(Vec3.ZERO)) {
            chooseWanderTarget(target);
            movementTicks = Math.max(1, movementTicks);
            return;
        }
        if (wanderCurveStart.equals(Vec3.ZERO) || wanderCurveControl.equals(Vec3.ZERO)) {
            // 兼容旧存档：保留已保存的终点，从当前实际位置重建剩余路径。
            wanderCurveStart = position();
            Vec3 direction = wanderTarget.subtract(wanderCurveStart);
            if (direction.lengthSqr() <= 1.0E-7) direction = getLookAngle();
            if (direction.lengthSqr() <= 1.0E-7) direction = new Vec3(0.0, 0.0, 1.0);
            double counterArc = wanderTarget.y < wanderCurveStart.y ? 6.0D : -6.0D;
            wanderCurveControl = wanderCurveStart.add(direction.normalize().scale(8.0D))
                    .add(0.0D, counterArc, 0.0D);
            movementTicks = 1;
        }
    }

    private void moveDirectlyAlongCurve(Vec3 nextPosition) {
        if (!Double.isFinite(nextPosition.x) || !Double.isFinite(nextPosition.y)
                || !Double.isFinite(nextPosition.z)) {
            beginPhase(MovementPhase.ALIGN);
            return;
        }
        Vec3 movement = nextPosition.subtract(position());
        if (movement.lengthSqr() > 1.0E-7) {
            setPos(nextPosition);
            hasImpulse = true;
            faceCombatDirection(movement, 12.0F, 12.0F);
        }
        setDeltaMovement(Vec3.ZERO);
    }

    /// 以固定弧长比例采样二次贝塞尔，避免参数曲线在控制点附近忽快忽慢。
    private static Vec3 sampleUniformQuadratic(Vec3 start, Vec3 control, Vec3 end, double progress) {
        final int subdivisions = 24;
        double clampedProgress = Mth.clamp(progress, 0.0D, 1.0D);
        Vec3 previous = start;
        double[] cumulativeLengths = new double[subdivisions + 1];
        Vec3[] samples = new Vec3[subdivisions + 1];
        samples[0] = start;
        for (int index = 1; index <= subdivisions; index++) {
            double t = index / (double) subdivisions;
            Vec3 sample = quadratic(start, control, end, t);
            samples[index] = sample;
            cumulativeLengths[index] = cumulativeLengths[index - 1] + previous.distanceTo(sample);
            previous = sample;
        }
        double totalLength = cumulativeLengths[subdivisions];
        if (totalLength <= 1.0E-7) return end;
        double targetLength = totalLength * clampedProgress;
        for (int index = 1; index <= subdivisions; index++) {
            if (cumulativeLengths[index] < targetLength) continue;
            double segmentLength = cumulativeLengths[index] - cumulativeLengths[index - 1];
            double localProgress = segmentLength <= 1.0E-7 ? 1.0D
                    : (targetLength - cumulativeLengths[index - 1]) / segmentLength;
            return samples[index - 1].lerp(samples[index], localProgress);
        }
        return end;
    }

    private static Vec3 quadratic(Vec3 start, Vec3 control, Vec3 end, double progress) {
        double inverse = 1.0D - progress;
        return start.scale(inverse * inverse)
                .add(control.scale(2.0D * inverse * progress))
                .add(end.scale(progress * progress));
    }

    private static ListTag writeVector(Vec3 vector) {
        ListTag result = new ListTag();
        result.add(DoubleTag.valueOf(vector.x));
        result.add(DoubleTag.valueOf(vector.y));
        result.add(DoubleTag.valueOf(vector.z));
        return result;
    }

    private static Vec3 readVector(CompoundTag tag, String key) {
        if (!tag.contains(key, Tag.TAG_LIST)) return Vec3.ZERO;
        ListTag values = tag.getList(key, Tag.TAG_DOUBLE);
        if (values.size() != 3) return Vec3.ZERO;
        Vec3 result = new Vec3(values.getDouble(0), values.getDouble(1), values.getDouble(2));
        return Double.isFinite(result.x) && Double.isFinite(result.y) && Double.isFinite(result.z)
                ? result : Vec3.ZERO;
    }

    private static int phaseDuration(MovementPhase phase) {
        return switch (phase) {
            case WANDER -> WANDER_TICKS;
            case ALIGN -> ALIGN_TICKS;
            case DASH -> DASH_TICKS;
        };
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch,
                       int steps, boolean teleport) {
        if (!level().isClientSide || teleport || distanceToSqr(x, y, z) > 4096.0D) {
            setPos(x, y, z);
            setRot(yaw, pitch);
            clientLerpSteps = 0;
            return;
        }
        clientLerpX = x;
        clientLerpY = y;
        clientLerpZ = z;
        clientLerpYaw = yaw;
        clientLerpPitch = pitch;
        clientLerpSteps = Math.max(1, steps);
    }

    private void tickClientInterpolation() {
        if (clientLerpSteps <= 0) return;
        double progress = 1.0D / clientLerpSteps;
        setPos(
                Mth.lerp(progress, getX(), clientLerpX),
                Mth.lerp(progress, getY(), clientLerpY),
                Mth.lerp(progress, getZ(), clientLerpZ));
        setRot(
                Mth.rotLerp((float) progress, getYRot(), clientLerpYaw),
                Mth.lerp((float) progress, getXRot(), clientLerpPitch));
        clientLerpSteps--;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof EaterOfWorlds) && super.canAttack(target);
    }
}
