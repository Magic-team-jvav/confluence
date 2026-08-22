package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.VisualNeuron;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.*;

/// 克苏鲁之脑及其两阶段战斗控制器。
///
/// <p>第一阶段会分批召唤二十只视神经元。Boss 维护每只神经元的精确 UUID、编队位置和出击节奏，
/// 本体在所有神经元被击败前不可受伤。神经元不会自主索敌，只有处于待命状态的个体才会接受 Boss
/// 发出的攻击命令。第二阶段才由本体执行瞬移、冲刺与追击行为。</p>
///
/// <p>神经元是可独立存档的实体，因此阶段推进不能只依赖附近实体数量。权威 UUID 集合会持久化；
/// 子实体在 Boss 区块卸载期间死亡时通过世界账本回报，Boss 重新加载后再统一结算。</p>
public class BrainOfCthulhu extends BaseBoss {
    private static final RawAnimation CLOSED = RawAnimation.begin().thenLoop("close");
    private static final RawAnimation OPEN = RawAnimation.begin().thenPlay("to_open").thenLoop("open");
    private static final EntityDataAccessor<Boolean> DATA_PHASE_TWO = SynchedEntityData.defineId(BrainOfCthulhu.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PHASE_ONE_STATE = SynchedEntityData.defineId(BrainOfCthulhu.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE_ONE_STATE_TICKS = SynchedEntityData.defineId(BrainOfCthulhu.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE_TWO_STATE = SynchedEntityData.defineId(BrainOfCthulhu.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE_TWO_STATE_TICKS = SynchedEntityData.defineId(BrainOfCthulhu.class, EntityDataSerializers.INT);
    private static final int NEURON_COUNT = 20;
    private static final int SUMMON_START_TICK = 21;
    private static final int SUMMON_INTERVAL_TICKS = 2;
    private static final int PHASE1_APPROACH_TICKS = 200;
    private static final int PHASE1_FADE_OUT_TICKS = 40;
    private static final int PHASE1_FADE_IN_TICKS = 40;
    private static final int PHASE2_TRANSFORM_TICKS = 15;
    private static final int PHASE2_STALK_TICKS = 40;
    private static final int PHASE2_DASH_TICKS = 30;
    private static final int PHASE2_DASH_WINDUP_TICKS = 10;
    private static final int PHASE2_FADE_OUT_TICKS = 30;
    private static final int PHASE2_FADE_IN_TICKS = 100;
    private static final String PHASE_TWO_TAG = "PhaseTwo";
    private static final String PHASE_TWO_STATE_TAG = "PhaseTwoState";
    private static final String PHASE_TWO_STATE_TICKS_TAG = "PhaseTwoStateTicks";
    private static final String PHASE_TWO_DASH_REMAINING_TAG = "PhaseTwoDashRemaining";
    private static final String PHASE_ONE_STATE_TAG = "PhaseOneState";
    private static final String PHASE_ONE_STATE_TICKS_TAG = "PhaseOneStateTicks";
    private static final String PHASE_ONE_INERTIA_X_TAG = "PhaseOneInertiaX";
    private static final String PHASE_ONE_INERTIA_Y_TAG = "PhaseOneInertiaY";
    private static final String PHASE_ONE_INERTIA_Z_TAG = "PhaseOneInertiaZ";
    private static final String SUMMON_TICKS_TAG = "NeuronSummonTicks";
    private static final String SPAWNED_NEURON_COUNT_TAG = "SpawnedNeuronCount";
    private static final String TRACKED_NEURON_COUNT_TAG = "TrackedNeuronCount";
    private static final String TRACKED_NEURON_TAG = "TrackedNeuron";

    private int summonTicks;
    private int spawnedNeuronCount;
    private int aliveNeurons;
    private boolean phase2;
    private PhaseOneState phaseOneState = PhaseOneState.SUMMONING;
    private int phaseOneStateTicks;
    private Vec3 phaseOneInertia = Vec3.ZERO;
    private PhaseTwoState phaseTwoState = PhaseTwoState.TRANSFORMING;
    private int phaseTwoStateTicks;
    private int phaseTwoDashRemaining;
    private Vec3 phaseTwoCurveStart;
    private Vec3 phaseTwoCurveControl;
    private Vec3 phaseTwoCurveEnd;
    private final Set<UUID> activeNeuronUUIDs = new HashSet<>();
    private final BrainFake[] illusions = new BrainFake[3];

    public BrainOfCthulhu(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 2000;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 552.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.5)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.YELLOW;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                // 两个阶段都由本体的权威技能时序驱动，行为树不能同时修改速度或位置。
                return new WaitAction(Integer.MAX_VALUE);
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || level().isClientSide) return;

        if (getTarget() == null && tickCount % 20 == 0) {
            Player replacement = findCombatPlayer();
            if (replacement != null) setTarget(replacement);
        }

        if (!phase2) {
            applyRecordedNeuronDeaths();

            List<VisualNeuron> loadedNeurons = findOwnedLoadedNeurons();
            aliveNeurons = loadedNeurons.size();
            updateNeuronHomes(loadedNeurons);
            tickPhaseOneCycle(loadedNeurons);

            if (summoningComplete() && activeNeuronUUIDs.isEmpty()) {
                enterPhaseTwo();
            }
        } else {
            ensureIllusions();
            tickPhaseTwoCycle();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PHASE_TWO, false);
        entityData.define(DATA_PHASE_ONE_STATE, PhaseOneState.SUMMONING.id);
        entityData.define(DATA_PHASE_ONE_STATE_TICKS, 0);
        entityData.define(DATA_PHASE_TWO_STATE, PhaseTwoState.TRANSFORMING.id);
        entityData.define(DATA_PHASE_TWO_STATE_TICKS, 0);
    }

    /// 第一阶段使用与 1.21 相同的技能段循环：靠近、淡出惯性、重新定位、淡入靠近。
    /// 飞眼怪不会随本体瞬移，而是继续追逐更新后的编队位置，形成跨越玩家的大范围扫掠轨迹。
    private void tickPhaseOneCycle(List<VisualNeuron> loadedNeurons) {
        switch (phaseOneState) {
            case SUMMONING -> {
                tickNeuronSummoning();
                setDeltaMovement(0.0, 0.05, 0.0);
                if (summoningComplete()) {
                    enterPhaseOneState(PhaseOneState.APPROACHING);
                }
            }
            case APPROACHING -> {
                tickPhaseOneMovement(5.0);
                dispatchReadyNeuron(loadedNeurons);
                if (++phaseOneStateTicks >= PHASE1_APPROACH_TICKS) {
                    phaseOneInertia = getDeltaMovement();
                    enterPhaseOneState(PhaseOneState.FADING_OUT);
                }
            }
            case FADING_OUT -> {
                setDeltaMovement(phaseOneInertia);
                if (++phaseOneStateTicks >= PHASE1_FADE_OUT_TICKS) {
                    doTeleport();
                    enterPhaseOneState(PhaseOneState.FADING_IN);
                }
            }
            case FADING_IN -> {
                tickPhaseOneMovement(2.0);
                if (++phaseOneStateTicks >= PHASE1_FADE_IN_TICKS) {
                    enterPhaseOneState(PhaseOneState.APPROACHING);
                }
            }
        }
        entityData.set(DATA_PHASE_ONE_STATE_TICKS, phaseOneStateTicks);
    }

    private void enterPhaseOneState(PhaseOneState state) {
        phaseOneState = state;
        phaseOneStateTicks = 0;
        entityData.set(DATA_PHASE_ONE_STATE, state.id);
        entityData.set(DATA_PHASE_ONE_STATE_TICKS, 0);
    }

    /// 复刻 1.21 的首次登场节奏：第 21 刻开始，每两刻召唤一只神经元。
    private void tickNeuronSummoning() {
        if (summoningComplete()) return;
        summonTicks++;
        if (summonTicks < SUMMON_START_TICK || (summonTicks - SUMMON_START_TICK) % SUMMON_INTERVAL_TICKS != 0) {
            return;
        }
        spawnNeuron(spawnedNeuronCount);
    }

    private void spawnNeuron(int index) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        VisualNeuron neuron = MonsterEntities.VISUAL_NEURON.get().create(level());
        if (neuron == null) return;

        Vec3 homeOffset = createNeuronHomeOffset();
        Vec3 home = position().add(homeOffset);
        neuron.setPos(home);
        neuron.setOwner(this);
        neuron.setHomeOffset(homeOffset);
        neuron.setHomePosition(home);
        if (serverLevel.addFreshEntity(neuron)) {
            activeNeuronUUIDs.add(neuron.getUUID());
            spawnedNeuronCount++;
        } else {
            neuron.cancelPendingSpawn();
        }
    }

    /// 按照 1.21 的球坐标范围生成神经元初始编队偏移。
    private Vec3 createNeuronHomeOffset() {
        double radius = random.nextFloat() + 5.0;
        double theta = random.nextFloat() * Math.PI;
        double beta = random.nextFloat() * Math.PI;
        double sinBeta = Math.sin(beta);
        return new Vec3(radius * sinBeta * Math.cos(theta), radius * Math.cos(beta), radius * sinBeta * Math.sin(theta));
    }

    private void updateNeuronHomes(List<VisualNeuron> loadedNeurons) {
        for (VisualNeuron neuron : loadedNeurons) {
            Vec3 baseOffset = neuron.getHomeOffset();
            if (baseOffset == null) {
                baseOffset = createNeuronHomeOffset();
                neuron.setHomeOffset(baseOffset);
            }
            Vec3 currentOffset = baseOffset;
            if (random.nextFloat() < 0.5F) {
                double angle = random.nextDouble() * 20.0;
                double cosine = Math.cos(angle);
                double sine = Math.sin(angle);
                currentOffset = new Vec3(baseOffset.x * cosine + baseOffset.z * sine, baseOffset.y, -baseOffset.x * sine + baseOffset.z * cosine);
            }
            neuron.setHomePosition(position().add(currentOffset));
        }
    }

    private void tickPhaseOneMovement(double verticalOffset) {
        Player target = getTarget() instanceof Player player ? player : null;
        if (target == null || !target.isAlive()) return;

        Vec3 away = position().subtract(target.position()).multiply(1.0, 0.0, 1.0);
        if (away.lengthSqr() < 1.0E-6) {
            away = new Vec3(1.0, 0.0, 0.0);
        }
        Vec3 destination = target.position().add(away.normalize().scale(10.0)).add(0.0, verticalOffset, 0.0);
        Vec3 offset = destination.subtract(position());
        if (offset.lengthSqr() > 2.0) {
            // 1.21 侧每刻直接写入固定速度，不保留上一刻惯性。
            setDeltaMovement(offset.normalize().scale(0.15));
        }
        getLookControl().setLookAt(target, 10.0F, 30.0F);
    }

    private void dispatchReadyNeuron(List<VisualNeuron> loadedNeurons) {
        if (!summoningComplete() || getTarget() == null) return;
        int interval = isMaster() ? 5 : isExpert() ? 6 : 10;
        if (tickCount % interval != 0) return;

        for (VisualNeuron neuron : loadedNeurons) {
            if (neuron.attack(getTarget())) return;
        }
    }

    /// 第二阶段沿用 1.21 的技能顺序：短暂变形、曲线绕行、冲刺、淡出、重新定位并淡入。
    /// 额外冲刺前会重新经过绕行段，不能直接在上一段冲刺结束的位置再次启动冲刺。
    private void tickPhaseTwoCycle() {
        Player target = getTarget() instanceof Player player && player.isAlive()
                ? player
                : null;
        if (target == null) return;

        switch (phaseTwoState) {
            case TRANSFORMING -> {
                setDeltaMovement(Vec3.ZERO);
                lookAtTarget(target);
                if (++phaseTwoStateTicks >= PHASE2_TRANSFORM_TICKS) {
                    enterPhaseTwoState(PhaseTwoState.STALKING);
                }
            }
            case STALKING -> {
                if (phaseTwoCurveStart == null) initializeStalkingCurve(target);
                moveAlongPhaseTwoCurve((phaseTwoStateTicks + 1.0) / PHASE2_STALK_TICKS);
                lookAtTarget(target);
                if (++phaseTwoStateTicks >= PHASE2_STALK_TICKS) {
                    enterPhaseTwoState(PhaseTwoState.DASHING);
                }
            }
            case DASHING -> {
                if (phaseTwoStateTicks < PHASE2_DASH_WINDUP_TICKS) {
                    Vec3 away = position().subtract(target.position());
                    setDeltaMovement(away.lengthSqr() > 1.0E-6
                            ? away.normalize().scale(0.3)
                            : Vec3.ZERO);
                } else {
                    if (phaseTwoCurveStart == null) initializeDashCurve(target);
                    double dashTicks = phaseTwoStateTicks - PHASE2_DASH_WINDUP_TICKS + 1.0;
                    moveAlongPhaseTwoCurve(dashTicks / (PHASE2_DASH_TICKS - PHASE2_DASH_WINDUP_TICKS));
                }
                lookAtTarget(target);
                if (++phaseTwoStateTicks >= PHASE2_DASH_TICKS) {
                    int configuredDashCount = getHealth() / getMaxHealth() < 0.3F ? 3 : 2;
                    if (phaseTwoDashRemaining > 0) {
                        phaseTwoDashRemaining--;
                        enterPhaseTwoState(PhaseTwoState.STALKING);
                    } else {
                        phaseTwoDashRemaining = configuredDashCount;
                        enterPhaseTwoState(PhaseTwoState.FADING_OUT);
                    }
                }
            }
            case FADING_OUT -> {
                Vec3 away = position().subtract(target.position());
                setDeltaMovement(away.lengthSqr() > 1.0E-6
                        ? away.normalize().scale(0.3)
                        : Vec3.ZERO);
                if (++phaseTwoStateTicks >= PHASE2_FADE_OUT_TICKS) {
                    enterPhaseTwoState(PhaseTwoState.FADING_IN);
                }
            }
            case FADING_IN -> {
                Vec3 destination = target.position().add(0.0, 1.0, 0.0);
                Vec3 offset = destination.subtract(position());
                if (offset.lengthSqr() > 2.0 && hurtTime == 0) {
                    setDeltaMovement(offset.normalize().scale(0.25));
                }
                lookAtTarget(target);
                if (++phaseTwoStateTicks >= PHASE2_FADE_IN_TICKS) {
                    enterPhaseTwoState(PhaseTwoState.STALKING);
                }
            }
        }
        entityData.set(DATA_PHASE_TWO_STATE_TICKS, phaseTwoStateTicks);
    }

    private void enterPhaseTwoState(PhaseTwoState state) {
        phaseTwoState = state;
        phaseTwoStateTicks = 0;
        phaseTwoCurveStart = null;
        phaseTwoCurveControl = null;
        phaseTwoCurveEnd = null;
        if (state == PhaseTwoState.FADING_IN) {
            doPhaseTwoTeleport();
        }
        entityData.set(DATA_PHASE_TWO_STATE, state.id);
        entityData.set(DATA_PHASE_TWO_STATE_TICKS, 0);
    }

    /// 构造第二阶段绕行曲线。控制点位于玩家周围，终点略高于控制点，形成 1.21 的掠过轨迹。
    private void initializeStalkingCurve(Player target) {
        double radius = 16.0 + random.nextDouble();
        double angle = random.nextDouble() * Mth.TWO_PI;
        phaseTwoCurveStart = position();
        phaseTwoCurveControl = target.position().add(Math.sin(angle) * radius, 2.0, Math.cos(angle) * radius);
        phaseTwoCurveEnd = phaseTwoCurveControl.add(0.0, 3.0, 0.0);
        setDeltaMovement(Vec3.ZERO);
    }

    /// 冲刺曲线先穿过玩家附近，再延伸到玩家另一侧，避免自动索敌式折线追踪。
    private void initializeDashCurve(Player target) {
        Vec3 fromTarget = target.position().subtract(position());
        Vec3 horizontal = fromTarget.multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-6) horizontal = new Vec3(1.0, 0.0, 0.0);
        phaseTwoCurveStart = position();
        phaseTwoCurveControl = target.position().add(random.nextDouble() - 0.5, -2.0, random.nextDouble() - 0.5);
        phaseTwoCurveEnd = target.position().add(horizontal.normalize().scale(10.0)).add(0.0, 2.0, 0.0);
        setDeltaMovement(Vec3.ZERO);
    }

    private void moveAlongPhaseTwoCurve(double progress) {
        if (phaseTwoCurveStart == null || phaseTwoCurveControl == null || phaseTwoCurveEnd == null) {
            return;
        }
        double t = Mth.clamp(progress, 0.0, 1.0);
        double inverse = 1.0 - t;
        Vec3 position = phaseTwoCurveStart.scale(inverse * inverse)
                .add(phaseTwoCurveControl.scale(2.0 * inverse * t))
                .add(phaseTwoCurveEnd.scale(t * t));
        setDeltaMovement(Vec3.ZERO);
        setPos(position);
    }

    private void lookAtTarget(Player target) {
        getLookControl().setLookAt(target, 10.0F, 30.0F);
    }

    private void enterPhaseTwo() {
        phase2 = true;
        entityData.set(DATA_PHASE_TWO, true);
        aliveNeurons = 0;
        broadcastPhaseTransition();
        phaseTwoDashRemaining = 0;
        enterPhaseTwoState(PhaseTwoState.TRANSFORMING);
        setHealth(getMaxHealth());
        ensureIllusions();
    }

    /// 保证第二阶段始终存在三个不同槽位的镜像幻象。
    ///
    /// <p>幻象不写入区块存档，因此本体从存档恢复、幻象被外部命令移除或实体生成暂时失败时，
    /// 都由这里按槽位补齐。已有幻象会先从本体的部件列表中恢复引用，避免重复生成。</p>
    private void ensureIllusions() {
        if (!phase2 || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        for (Entity entity : List.copyOf(getSubEntities())) {
            if (entity instanceof BrainFake fake && !fake.isRemoved() && fake.getOwner() == this) {
                bindIllusion(fake);
            }
        }

        for (int slot = 0; slot < illusions.length; slot++) {
            BrainFake existing = illusions[slot];
            if (existing != null && !existing.isRemoved()) {
                continue;
            }

            BrainFake illusion = BossEntities.BRAIN_FAKE.get().create(level());
            if (illusion == null) {
                continue;
            }
            illusion.setPos(position());
            illusion.setIllusionIndex(slot + 1);
            if (serverLevel.addFreshEntity(illusion)) {
                illusion.setMaster(this, slot + 1);
            } else {
                illusion.discard();
            }
        }
    }

    void bindIllusion(BrainFake illusion) {
        int slot = illusion.getIllusionIndex() - 1;
        if (slot >= 0 && slot < illusions.length) {
            BrainFake previous = illusions[slot];
            if (previous == null || previous.isRemoved() || previous.getId() > illusion.getId()) {
                illusions[slot] = illusion;
            } else if (previous != illusion && !illusion.isRemoved()) {
                illusion.discard();
            }
        }
    }

    void onIllusionRemoved(BrainFake illusion) {
        int slot = illusion.getIllusionIndex() - 1;
        if (slot >= 0 && slot < illusions.length && illusions[slot] == illusion) {
            illusions[slot] = null;
        }
    }

    private void doTeleport() {
        double minimumRadius = isExpert() ? 7.0 : 9.0;
        teleportAroundTarget(minimumRadius, minimumRadius + 1.0, 0.0, Math.PI, 0.0, 16);
    }

    /// 第二阶段在玩家较远处重新显现，并保留安全碰撞检查，避免传送进方块或贴到玩家脸上。
    private void doPhaseTwoTeleport() {
        teleportAroundTarget(10.0, 11.0, Math.PI * 0.35, Math.PI * 0.65, 2.0, 24);
    }

    /// 保留 1.21 的球坐标取样范围，同时跳过会把实体放进方块或液体的位置。
    private void teleportAroundTarget(double minimumRadius, double maximumRadius, double minimumBeta, double maximumBeta, double verticalOffset, int attempts) {
        Player target = getTarget() instanceof Player player ? player : null;
        if (target == null || !(level() instanceof ServerLevel serverLevel)) return;

        for (int attempt = 0; attempt < attempts; attempt++) {
            double radius = Mth.lerp(random.nextDouble(), minimumRadius, maximumRadius);
            double theta = random.nextDouble() * Mth.TWO_PI;
            double beta = Mth.lerp(random.nextDouble(), minimumBeta, maximumBeta);
            double sinBeta = Math.sin(beta);
            Vec3 destination = target.position().add(radius * sinBeta * Math.cos(theta), radius * Math.cos(beta) + verticalOffset, radius * sinBeta * Math.sin(theta));
            destination = new Vec3(destination.x, Mth.clamp(destination.y, serverLevel.getMinBuildHeight() + 1.0, serverLevel.getMaxBuildHeight() - getBbHeight() - 1.0), destination.z);
            BlockPos blockPosition = BlockPos.containing(destination);
            AABB destinationBounds = getBoundingBox().move(destination.x - getX(), destination.y - getY(), destination.z - getZ());
            if (serverLevel.hasChunkAt(blockPosition) && serverLevel.noCollision(this, destinationBounds) && !serverLevel.containsAnyLiquid(destinationBounds)) {
                teleportTo(destination.x, destination.y, destination.z);
                return;
            }
        }
    }

    private List<VisualNeuron> findOwnedLoadedNeurons() {
        Set<VisualNeuron> found = new LinkedHashSet<>();
        for (Entity entity : getSubEntities()) {
            if (entity instanceof VisualNeuron neuron && neuron.isAlive() && neuron.isOwnedBy(this)) {
                found.add(neuron);
            }
        }
        found.addAll(level().getEntitiesOfClass(VisualNeuron.class, getBoundingBox().inflate(96.0), neuron -> neuron.isAlive() && neuron.isOwnedBy(this)));
        return List.copyOf(found);
    }

    private void applyRecordedNeuronDeaths() {
        if (level() instanceof ServerLevel serverLevel) {
            activeNeuronUUIDs.removeAll(BossChildDeathLedger.consume(serverLevel, getUUID()));
        }
    }

    public void onNeuronDefeated(VisualNeuron neuron) {
        activeNeuronUUIDs.remove(neuron.getUUID());
    }

    public static void recordDetachedNeuronDeath(ServerLevel level, UUID ownerUUID, UUID neuronUUID) {
        BossChildDeathLedger.record(level, ownerUUID, neuronUUID);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getEncounterProgress());
    }

    /// 第一阶段 Boss 条使用固定的遭遇总生命上限。
    ///
    /// <p>神经元死亡后只能减少当前生命，不能同时从最大生命中移除；否则每击杀一只神经元，
    /// 分母也随之缩小，Boss 条就会错误回升。仍被追踪但暂未加载的神经元按满生命计入当前值，
    /// 避免区块短暂卸载被误判为已经击败；开场尚未生成的神经元也预占完整生命，防止召唤阶段
    /// Boss 条从半血反向增长。</p>
    public float getEncounterProgress() {
        if (phase2) return getHealth() / getMaxHealth();
        List<VisualNeuron> loaded = findOwnedLoadedNeurons();
        double loadedHealth = loaded.stream().mapToDouble(VisualNeuron::getHealth).sum();
        int unloadedCount = Math.max(0, activeNeuronUUIDs.size() - loaded.size());
        int pendingSpawnCount = Math.max(0, NEURON_COUNT - spawnedNeuronCount);
        double health = getHealth()
                + loadedHealth
                + (unloadedCount + pendingSpawnCount) * VisualNeuron.BASE_MAX_HEALTH;
        double maximum = getMaxHealth()
                + NEURON_COUNT * VisualNeuron.BASE_MAX_HEALTH;
        return maximum > 0.0 ? Mth.clamp((float) (health / maximum), 0.0F, 1.0F) : 0.0F;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !phase2 || super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return phase2 && super.hurt(source, amount);
    }

    @Override
    public boolean isPickable() {
        return phase2;
    }

    /// 脑的两阶段位移都由自身曲线控制，不叠加原版重力。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {return false;}

    @Override
    protected boolean shouldDiscardWhenNoTarget() {
        return true;
    }

    public boolean isPhase2() {
        return level().isClientSide ? entityData.get(DATA_PHASE_TWO) : phase2;
    }

    /// 返回客户端插值后的显隐进度。第一阶段生成时由半透明逐渐显现，重新定位前后分别淡出和淡入。
    public float getFadeProgress(float partialTick) {
        if (isPhase2()) {
            PhaseTwoState state = level().isClientSide
                    ? PhaseTwoState.byId(entityData.get(DATA_PHASE_TWO_STATE))
                    : phaseTwoState;
            float ticks = (level().isClientSide
                    ? entityData.get(DATA_PHASE_TWO_STATE_TICKS)
                    : phaseTwoStateTicks) + partialTick;
            return switch (state) {
                case FADING_OUT -> 1.0F - Mth.clamp(ticks / PHASE2_FADE_OUT_TICKS, 0.0F, 1.0F);
                case FADING_IN -> Mth.clamp(ticks / PHASE2_FADE_OUT_TICKS, 0.0F, 1.0F);
                default -> 1.0F;
            };
        }
        PhaseOneState state = level().isClientSide
                ? PhaseOneState.byId(entityData.get(DATA_PHASE_ONE_STATE))
                : phaseOneState;
        float ticks = (level().isClientSide
                ? entityData.get(DATA_PHASE_ONE_STATE_TICKS)
                : phaseOneStateTicks) + partialTick;
        return switch (state) {
            case SUMMONING -> Mth.clamp(tickCount / 51.0F, 0.0F, 1.0F);
            case APPROACHING -> 1.0F;
            case FADING_OUT -> 1.0F - Mth.clamp(ticks / PHASE1_FADE_OUT_TICKS, 0.0F, 1.0F);
            case FADING_IN -> Mth.clamp(ticks / PHASE1_FADE_IN_TICKS, 0.0F, 1.0F);
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Brain", 0, state -> state.setAndContinue(isPhase2() ? OPEN : CLOSED)));
    }

    int getPhaseTwoDashRemaining() {
        return phaseTwoDashRemaining;
    }

    private boolean summoningComplete() {
        return spawnedNeuronCount >= NEURON_COUNT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(PHASE_TWO_TAG, phase2);
        tag.putInt(PHASE_TWO_STATE_TAG, phaseTwoState.id);
        tag.putInt(PHASE_TWO_STATE_TICKS_TAG, phaseTwoStateTicks);
        tag.putInt(PHASE_TWO_DASH_REMAINING_TAG, phaseTwoDashRemaining);
        tag.putInt(PHASE_ONE_STATE_TAG, phaseOneState.id);
        tag.putInt(PHASE_ONE_STATE_TICKS_TAG, phaseOneStateTicks);
        tag.putDouble(PHASE_ONE_INERTIA_X_TAG, phaseOneInertia.x);
        tag.putDouble(PHASE_ONE_INERTIA_Y_TAG, phaseOneInertia.y);
        tag.putDouble(PHASE_ONE_INERTIA_Z_TAG, phaseOneInertia.z);
        tag.putInt(SUMMON_TICKS_TAG, summonTicks);
        tag.putInt(SPAWNED_NEURON_COUNT_TAG, spawnedNeuronCount);

        List<UUID> trackedNeurons = new ArrayList<>(activeNeuronUUIDs);
        trackedNeurons.sort(Comparator.comparing(UUID::toString));
        tag.putInt(TRACKED_NEURON_COUNT_TAG, trackedNeurons.size());
        for (int index = 0; index < trackedNeurons.size(); index++) {
            tag.putUUID(TRACKED_NEURON_TAG + index, trackedNeurons.get(index));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        phase2 = tag.getBoolean(PHASE_TWO_TAG);
        phaseTwoState = PhaseTwoState.byId(tag.getInt(PHASE_TWO_STATE_TAG));
        phaseTwoStateTicks = Math.max(0, tag.getInt(PHASE_TWO_STATE_TICKS_TAG));
        phaseTwoDashRemaining = Math.max(0, tag.getInt(PHASE_TWO_DASH_REMAINING_TAG));
        // 曲线控制点不写入存档；恢复后从当前位置重新开始当前曲线段，避免跨版本坐标残留。
        if (phaseTwoState == PhaseTwoState.STALKING || phaseTwoState == PhaseTwoState.DASHING) {
            phaseTwoStateTicks = 0;
        }
        phaseTwoCurveStart = null;
        phaseTwoCurveControl = null;
        phaseTwoCurveEnd = null;
        phaseOneState = PhaseOneState.byId(tag.getInt(PHASE_ONE_STATE_TAG));
        phaseOneStateTicks = Math.max(0, tag.getInt(PHASE_ONE_STATE_TICKS_TAG));
        phaseOneInertia = new Vec3(tag.getDouble(PHASE_ONE_INERTIA_X_TAG), tag.getDouble(PHASE_ONE_INERTIA_Y_TAG), tag.getDouble(PHASE_ONE_INERTIA_Z_TAG));
        entityData.set(DATA_PHASE_TWO, phase2);
        entityData.set(DATA_PHASE_TWO_STATE, phaseTwoState.id);
        entityData.set(DATA_PHASE_TWO_STATE_TICKS, phaseTwoStateTicks);
        entityData.set(DATA_PHASE_ONE_STATE, phaseOneState.id);
        entityData.set(DATA_PHASE_ONE_STATE_TICKS, phaseOneStateTicks);
        summonTicks = Math.max(0, tag.getInt(SUMMON_TICKS_TAG));
        spawnedNeuronCount = Mth.clamp(tag.getInt(SPAWNED_NEURON_COUNT_TAG), 0, NEURON_COUNT);

        activeNeuronUUIDs.clear();
        int savedTrackedCount = tag.getInt(TRACKED_NEURON_COUNT_TAG);
        int trackedCount = Mth.clamp(savedTrackedCount, 0, spawnedNeuronCount);
        for (int index = 0; index < trackedCount; index++) {
            String key = TRACKED_NEURON_TAG + index;
            if (tag.hasUUID(key)) activeNeuronUUIDs.add(tag.getUUID(key));
        }

        for (int index = 0; index < illusions.length; index++) {
            illusions[index] = null;
        }
    }

    @Override
    public void die(DamageSource source) {
        activeNeuronUUIDs.clear();
        for (VisualNeuron neuron : findOwnedLoadedNeurons()) {
            neuron.discard();
        }
        for (int index = 0; index < illusions.length; index++) {
            illusions[index] = null;
        }
        super.die(source);
    }

    enum PhaseOneState {
        SUMMONING(0),
        APPROACHING(1),
        FADING_OUT(2),
        FADING_IN(3);

        private final int id;

        PhaseOneState(int id) {
            this.id = id;
        }

        private static PhaseOneState byId(int id) {
            return switch (id) {
                case 1 -> APPROACHING;
                case 2 -> FADING_OUT;
                case 3 -> FADING_IN;
                default -> SUMMONING;
            };
        }
    }

    enum PhaseTwoState {
        TRANSFORMING(0),
        STALKING(1),
        DASHING(2),
        FADING_OUT(3),
        FADING_IN(4);

        private final int id;

        PhaseTwoState(int id) {
            this.id = id;
        }

        private static PhaseTwoState byId(int id) {
            return switch (id) {
                case 1 -> STALKING;
                case 2 -> DASHING;
                case 3 -> FADING_OUT;
                case 4 -> FADING_IN;
                default -> TRANSFORMING;
            };
        }
    }
}
