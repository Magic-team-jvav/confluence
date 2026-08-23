package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/// 由可独立破坏体节和可分裂链条组成的世界吞噬怪战斗实体。
///
/// 体节被摧毁后，剩余链条会重新选举头尾并继续战斗；Boss 栏则由主头部统一汇总。
public class EaterOfWorlds extends BaseWormBoss {
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
    private static final String NEXT_WANDER_BELOW_TAG = "NextWanderBelow";
    private static final int WANDER_TICKS = 120;
    private static final int ALIGN_TICKS = 300;
    private static final int DASH_TICKS = 120;
    private static final double WANDER_SPEED = 0.4;
    private static final double PURSUIT_SPEED = 0.55;

    private static final EntityDataAccessor<Optional<UUID>> ENCOUNTER_UUID = SynchedEntityData.defineId(EaterOfWorlds.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> PRIMARY_HEAD = SynchedEntityData.defineId(EaterOfWorlds.class, EntityDataSerializers.BOOLEAN);

    private final List<Float> segmentHealths = new ArrayList<>();
    private int activeSegmentCount = INITIAL_SEGMENT_COUNT;
    private boolean restructuring;
    private MovementPhase movementPhase = MovementPhase.WANDER;
    private int movementTicks;
    private Vec3 wanderTarget = Vec3.ZERO;
    private boolean nextWanderBelow = true;

    private enum MovementPhase {
        WANDER,
        ALIGN,
        DASH
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
    }

    @Override
    protected int getSegmentCount() {
        return activeSegmentCount;
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

    /// 复现 1.21 出生时的盘曲链，而不是把六十个碰撞体叠在头部中心。
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
        return previousPosition.add(direction.normalize().scale(-getSegmentSpacing()).yRot(curve));
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
        if (index < 1 || index > segmentHealths.size()) return false;

        float appliedDamage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : CombatRules.getDamageAfterAbsorb(amount, NODE_ARMOR, 0.0F);
        if (appliedDamage <= 0.0F) return false;
        float remaining = Math.max(0.0F, segmentHealths.get(index - 1) - appliedDamage);
        segmentHealths.set(index - 1, remaining);
        segment.setPartHealth(remaining);
        if (source.getEntity() instanceof LivingEntity attacker && canAttack(attacker))
            setTarget(attacker);
        if (remaining <= 0.0F) {
            dropSegmentLoot(segment, source);
            splitAt(index);
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

    private void splitAt(int destroyedIndex) {
        if (!(level() instanceof ServerLevel) || destroyedIndex < 1 || destroyedIndex > activeSegmentCount || restructuring)
            return;
        restructuring = true;
        try {
            List<Float> oldHealths = List.copyOf(segmentHealths);
            List<Float> frontHealths = new ArrayList<>(oldHealths.subList(0, destroyedIndex - 1));
            EaterOfWorlds rearHead = null;

            if (destroyedIndex < activeSegmentCount) {
                BossWormPart rearHeadPart = segments.get(destroyedIndex);
                float rearHeadHealth = oldHealths.get(destroyedIndex);
                List<Float> rearHealths = new ArrayList<>(oldHealths.subList(destroyedIndex + 1, oldHealths.size()));
                rearHead = spawnSplitHead(rearHeadPart.position(), rearHeadPart.getYRot(), rearHeadPart.getXRot(), rearHeadHealth, rearHealths, false);
                if (rearHead == null) {
                    frontHealths.add(rearHeadHealth);
                    frontHealths.addAll(rearHealths);
                }
            }

            rebuildWithHealths(frontHealths);
            if (rearHead != null && getTarget() != null) rearHead.setTarget(getTarget());
        } finally {
            restructuring = false;
        }
    }

    private void rebuildWithHealths(List<Float> healths) {
        discardSegments();
        activeSegmentCount = Math.min(INITIAL_SEGMENT_COUNT, healths.size());
        segmentHealths.clear();
        for (int index = 0; index < activeSegmentCount; index++) {
            segmentHealths.add(Mth.clamp(healths.get(index), 0.0F, NODE_MAX_HEALTH));
        }
        initSegments();
    }

    private @Nullable EaterOfWorlds spawnSplitHead(Vec3 position, float yaw, float pitch, float headHealth, List<Float> bodyHealths, boolean primary) {
        if (!(level() instanceof ServerLevel serverLevel)) return null;
        EaterOfWorlds head = BossEntities.EATER_OF_WORLDS.get().create(level());
        if (head == null) return null;

        head.setPos(position);
        head.setRot(yaw, pitch);
        head.setEncounterUUID(getEncounterUUID() == null ? getUUID() : getEncounterUUID());
        head.setPrimaryHead(primary);
        head.activeSegmentCount = Math.min(INITIAL_SEGMENT_COUNT, bodyHealths.size());
        head.segmentHealths.clear();
        for (int index = 0; index < head.activeSegmentCount; index++) {
            head.segmentHealths.add(Mth.clamp(bodyHealths.get(index), 0.0F, NODE_MAX_HEALTH));
        }
        head.setHealth(Mth.clamp(headHealth, 0.1F, (float) head.getMaxHealth()));
        if (getTarget() != null) head.setTarget(getTarget());
        /// 分裂头是现有 Boss 链的一部分，必须继承 Boss 的持久生命周期。原版 Mob
        /// 会在和平难度的首次 AI 检查中丢弃未标记为持久的怪物；这会让刚完成分裂、
        /// 尚未来得及进入第一个实体 tick 的新头直接消失。遭遇退出仍由 BaseBoss 的
        /// 脱战计时统一控制，因此这里不会让已经无人参与的战斗永久滞留。
        head.setPersistenceRequired();
        if (!serverLevel.addFreshEntity(head)) {
            head.discard();
            return null;
        }
        return head;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && !restructuring && activeSegmentCount > 0 && !segments.isEmpty()) {
            BossWormPart promotedPart = segments.get(0);
            List<Float> remainingBody = new ArrayList<>(segmentHealths.subList(1, segmentHealths.size()));
            EaterOfWorlds promoted = spawnSplitHead(promotedPart.position(), promotedPart.getYRot(), promotedPart.getXRot(), segmentHealths.get(0), remainingBody, isMainBody());
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
                setPrimaryHead(false);
                successor.setPrimaryHead(true);
            }
        }
        super.die(source);
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
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (isMainBody()) {
            float remainingHealth = 0.0F;
            for (EaterOfWorlds head : encounterHeads()) remainingHealth += head.chainHealth();
            bossEvent.setProgress(Mth.clamp(remainingHealth / ENCOUNTER_MAX_HEALTH, 0.0F, 1.0F));
        }
    }

    private float chainHealth() {
        float total = getHealth();
        for (float health : segmentHealths) total += health;
        return total;
    }

    private List<EaterOfWorlds> encounterHeads() {
        UUID encounter = getEncounterUUID();
        if (encounter == null) return isAlive() ? List.of(this) : List.of();
        return level().getEntitiesOfClass(EaterOfWorlds.class, getBoundingBox().inflate(512.0), candidate -> encounter.equals(candidate.getEncounterUUID()));
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
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        if (!level().isClientSide) {
            if (getTarget() == null && tickCount % 30 == 0) {
                Player replacement = findCombatPlayer();
                if (replacement != null) setTarget(replacement);
            }
            tickWormMovement();
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
                // 1.21 的游走段只调整朝向，目标点每刻上移；本体在该段不会前进。
                wanderTarget = wanderTarget.add(0.0, 0.05, 0.0);
                steerInThreeDimensions(wanderTarget, WANDER_SPEED, 2.0F);
                setDeltaMovement(Vec3.ZERO);
                if (movementTicks >= WANDER_TICKS) {
                    beginPhase(MovementPhase.ALIGN);
                }
            }
            case ALIGN -> {
                Vec3 toTarget = target.position().subtract(position());
                double angle = angleBetween(getLookAngle(), toTarget);
                steerInThreeDimensions(target.position(), isFtw() ? PURSUIT_SPEED * (1.5 / 1.1) : PURSUIT_SPEED, 5.0F);
                if (movementTicks >= ALIGN_TICKS || angle < Math.PI / 8.0 && toTarget.lengthSqr() < 20.0) {
                    beginPhase(MovementPhase.DASH);
                }
            }
            case DASH -> {
                steerInThreeDimensions(target.position(), isFtw() ? PURSUIT_SPEED * (1.5 / 1.1) : PURSUIT_SPEED, 5.0F);
                if (movementTicks >= DASH_TICKS) {
                    beginPhase(MovementPhase.WANDER);
                    chooseWanderTarget(target);
                }
            }
        }
    }

    private void beginPhase(MovementPhase phase) {
        movementPhase = phase;
        movementTicks = 0;
        if (phase != MovementPhase.WANDER) {
            wanderTarget = Vec3.ZERO;
        }
    }

    private void chooseWanderTarget(LivingEntity target) {
        double angle = random.nextDouble() * Mth.TWO_PI;
        double radius = 10.0;
        double verticalOffset = nextWanderBelow
                ? -10.0 - random.nextDouble() * 4.0
                : 5.0 + random.nextDouble() * 2.0;
        nextWanderBelow = !nextWanderBelow;
        wanderTarget = target.position().add(Math.sin(angle) * radius, verticalOffset, Math.cos(angle) * radius);
    }

    private static int phaseDuration(MovementPhase phase) {
        return switch (phase) {
            case WANDER -> WANDER_TICKS;
            case ALIGN -> ALIGN_TICKS;
            case DASH -> DASH_TICKS;
        };
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof EaterOfWorlds) && super.canAttack(target);
    }
}
