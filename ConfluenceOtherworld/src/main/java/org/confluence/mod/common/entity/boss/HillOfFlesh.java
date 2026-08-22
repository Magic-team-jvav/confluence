package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.SimpleWormMonster;
import org.confluence.mod.common.entity.monster.slime.FleshSlime;
import org.confluence.mod.common.entity.projectile.HillLavaPillarProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.world.IncrementalCylinderDestruction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.*;

/// 肉丘——静止的地狱 Boss，拥有环形伤害区域、5 只眼睛 + 5 张嘴巴。
/// Phase2 (HP<50%) 时外圈扩大、攻击加速。
public class HillOfFlesh extends BaseBoss {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    static final float INNER_RADIUS = 14.0F;
    static final float OUTER_RADIUS = 75.0F;
    private static final float ATTACHED_DAMAGE = 10.0F;
    private static final float INNER_DAMAGE = 40.0F;
    private static final float OUTER_DAMAGE = 40.0F;
    private static final int ARENA_HEIGHT = 100;
    private static final int INITIALIZATION_TICKS = 150;
    private static final int DESTRUCTION_START_TICK = 75;
    private static final int INNER_EXPANSION_TICKS = 600;
    private static final int DAMAGE_INTERVAL = 32;
    private static final int TRACK_INTERVAL = 64;
    private static final int PART_COUNT = 10;
    private static final int FLESH_SLIME_INTERVAL = 300;
    private static final int LEECH_INTERVAL = 300;
    private static final int LAVA_PILLAR_INTERVAL = 100;

    private static final String ENCOUNTER_TICKS_TAG = "EncounterTicks";
    private static final String PHASE_TWO_TAG = "PhaseTwo";
    private static final String EXPANDING_TICKS_TAG = "ExpandingTicks";
    private static final String OUTER_RADIUS_TAG = "OuterRadius";
    private static final String TERRAIN_DESTRUCTION_TAG = "TerrainDestruction";
    private static final String FLESH_SLIME_TIMER_TAG = "FleshSlimeTimer";
    private static final String LEECH_TIMER_TAG = "LeechTimer";
    private static final String LAVA_PILLAR_TIMER_TAG = "LavaPillarTimer";

    private static final EntityDataAccessor<Boolean> DATA_INITIALIZING = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_OUTER_RADIUS = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.FLOAT);

    private static final double[][] PART_OFFSETS = {
            {8, 13, 5}, {-8, 10, 8}, {7, 8, -7},
            {0.5, 6.5, 8}, {-3, 5.5, -6},
            {0, 11, 0}, {-6, 9, -3}, {8, 8, 5},
            {-6.5, 4, 8.5}, {7, 3, -7}
    };

    private final Entity[] parts = new Entity[PART_COUNT];
    private final Set<LivingEntity> encounterEntities = new HashSet<>();
    private List<LivingEntity> nearbyLivingEntities = List.of();
    private int encounterTicks;
    private int expandingTicks;
    private boolean phase2;
    private boolean terrainDestructionEnabled;
    private int damageTimer;
    private int fleshSlimeTimer = FLESH_SLIME_INTERVAL;
    private int leechTimer = LEECH_INTERVAL;
    private int lavaPillarTimer = LAVA_PILLAR_INTERVAL;
    private IncrementalCylinderDestruction destructionTask;

    public HillOfFlesh(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        xpReward = 5000;
    }

    /// 肉山丘陵的墙体布局由自身锚点控制，不接受原版重力。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 3824.0)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, OUTER_RADIUS);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_INITIALIZING, true);
        entityData.define(DATA_OUTER_RADIUS, INNER_RADIUS);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(100);
            }
        };
    }

    public boolean isInitializing() {
        return entityData.get(DATA_INITIALIZING);
    }

    public boolean isPhase2() {
        return phase2;
    }

    public float getOuterRadius() {
        return entityData.get(DATA_OUTER_RADIUS);
    }

    public float getInnerRadius() {
        if (!phase2 || !isExpert()) {
            return INNER_RADIUS;
        }
        return Mth.lerp(Mth.clamp(expandingTicks / (float) INNER_EXPANSION_TICKS, 0.0F, 1.0F), INNER_RADIUS, OUTER_RADIUS * 0.25F);
    }

    /// 仅供正式召唤流程开启地形清场。
    public void enableArenaDestruction() {
        terrainDestructionEnabled = true;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !isInitializing() && super.canBeSeenAsEnemy();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !target.getType().is(ModTags.EntityTypes.FLESH_ALLIANCE)
                && super.canAttack(target);
    }

    @Nullable
    public LivingEntity findTargetForPart(Vec3 partPosition) {
        if (isInitializing()) {
            return null;
        }
        float radius = getOuterRadius();
        /// 部件优先跟随主体当前锁定的目标，避免多人战斗或相邻测试场景中的其他玩家
        /// 仅因离某个部件更近就抢走仇恨。当前目标离开战斗区域后，才由部件独立寻找
        /// 区域内仍然有效的候选者。
        LivingEntity primaryTarget = getTarget();
        if (isValidPartTarget(primaryTarget, radius)) {
            return primaryTarget;
        }
        List<LivingEntity> candidates = level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(position(), radius * 2.0, ARENA_HEIGHT, radius * 2.0), entity -> isValidPartTarget(entity, radius));
        return candidates.stream()
                .min(Comparator.<LivingEntity>comparingInt(
                                entity -> entity instanceof Player ? 0 : 1)
                        .thenComparingDouble(
                                entity -> entity.distanceToSqr(partPosition))).orElse(null);
    }

    /// 判断生物是否仍处于血肉山的圆形战斗区域内，并且可以成为攻击目标。
    private boolean isValidPartTarget(@Nullable LivingEntity entity, float radius) {
        return entity != null
                && entity.isAlive()
                && canAttack(entity)
                && entity.position().subtract(position()).horizontalDistanceSqr() <= radius * radius;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) {
            spawnParts();
        }
    }

    private void spawnParts() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int index = 0; index < PART_COUNT; index++) {
            if (parts[index] != null && parts[index].isAlive()) {
                continue;
            }
            parts[index] = spawnPart(serverLevel, index);
        }
    }

    private Entity spawnPart(ServerLevel serverLevel, int index) {
        Entity part = index < 5
                ? BossEntities.HILL_OF_FLESH_EYE.get().create(level())
                : BossEntities.HILL_OF_FLESH_MOUTH.get().create(level());
        if (part == null) {
            return null;
        }
        double[] offset = PART_OFFSETS[index];
        part.setPos(position().add(offset[0], offset[1], offset[2]));
        if (part instanceof HillOfFleshEye eye) {
            eye.setMaster(this);
        } else if (part instanceof HillOfFleshMouth mouth) {
            mouth.setMaster(this);
        }
        if (!serverLevel.addFreshEntity(part)) {
            part.discard();
            return null;
        }
        return part;
    }

    public Entity getPart(int index) {
        return index >= 0 && index < PART_COUNT
                ? parts[index] : null;
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || level().isClientSide) {
            return;
        }

        encounterTicks++;
        spawnParts();
        updateInitialization();
        updatePhase();
        updateArenaDestruction();
        tickParts();

        if (isInitializing()) {
            return;
        }
        acquireFallbackTarget();
        trackNearbyEntities();
        applyStormPull();
        updateArenaDamage();
        updateSummons();
    }

    private void updateInitialization() {
        if (isInitializing() && encounterTicks >= INITIALIZATION_TICKS) {
            entityData.set(DATA_INITIALIZING, false);
        }
    }

    private void updatePhase() {
        if (!phase2 && getHealth() / getMaxHealth() < 0.5F) {
            phase2 = true;
            broadcastPhaseTransition();
        }
        if (phase2 && isExpert() && expandingTicks < INNER_EXPANSION_TICKS) {
            expandingTicks++;
        }
    }

    private void updateArenaDestruction() {
        if (!terrainDestructionEnabled || encounterTicks < DESTRUCTION_START_TICK || getOuterRadius() >= OUTER_RADIUS) {
            return;
        }
        if (destructionTask == null) {
            destructionTask = new IncrementalCylinderDestruction(
                    level(),
                    blockPosition().getX(),
                    blockPosition().getZ(),
                    blockPosition().getY() - 1,
                    blockPosition().getY() + ARENA_HEIGHT - 1,
                    Mth.floor(getOuterRadius()),
                    Mth.floor(OUTER_RADIUS));
        }
        boolean complete = destructionTask.tick();
        entityData.set(DATA_OUTER_RADIUS, (float) destructionTask.getCurrentRadius());
        if (complete) {
            entityData.set(DATA_OUTER_RADIUS, OUTER_RADIUS);
            destructionTask = null;
        }
    }

    private void tickParts() {
        for (int index = 0; index < PART_COUNT; index++) {
            Entity part = parts[index];
            if (part == null || !part.isAlive()) {
                continue;
            }
            double[] offset = PART_OFFSETS[index];
            Vec3 rotated = new Vec3(offset[0], offset[1], offset[2]).yRot(-getYRot() * Mth.DEG_TO_RAD);
            part.setPos(getX() + rotated.x, getY() + rotated.y, getZ() + rotated.z);
        }
    }

    private void acquireFallbackTarget() {
        if (getTarget() != null || tickCount % 30 != 0) {
            return;
        }
        Player nearest = level().getNearestPlayer(this, getOuterRadius());
        if (nearest != null && canAttack(nearest)) {
            registerCombatParticipant(nearest);
            setTarget(nearest);
        }
    }

    private void trackNearbyEntities() {
        if (tickCount % TRACK_INTERVAL != 0) {
            return;
        }
        float outerRadius = getOuterRadius();
        float outerSquared = outerRadius * outerRadius;
        float innerSquared = getInnerRadius() * getInnerRadius();
        nearbyLivingEntities = new ArrayList<>(
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(
                                outerRadius * 1.2F,
                                ARENA_HEIGHT / 2.0,
                                outerRadius * 1.2F),
                        entity -> {
                            if (!entity.isAlive()
                                    || !canAttack(entity)) {
                                return false;
                            }
                            double horizontalDistance = entity.position().subtract(position()).horizontalDistanceSqr();
                            if (horizontalDistance < innerSquared) {
                                markEncounterEntity(entity);
                            }
                            boolean insideArena = horizontalDistance <= outerSquared;
                            if (insideArena) {
                                markEncounterEntity(entity);
                            }
                            return insideArena;
                        }));
        nearbyLivingEntities.sort(Comparator.comparingDouble(this::distanceToSqr));
    }

    void markEncounterEntity(LivingEntity entity) {
        encounterEntities.add(entity);
        entity.addEffect(new MobEffectInstance(ModEffects.CRIMSON_STORM.get(), 200, 0), this);
        if (entity instanceof Player player) {
            registerCombatParticipant(player);
        }
    }

    private void updateArenaDamage() {
        damageTimer++;
        if (damageTimer < DAMAGE_INTERVAL) {
            return;
        }
        damageTimer = 0;
        applyArenaDamage();
    }

    void applyArenaDamage() {
        float outerRadius = getOuterRadius();
        float innerRadius = getInnerRadius();
        encounterEntities.removeIf(entity -> !entity.isAlive());
        for (LivingEntity entity : List.copyOf(encounterEntities)) {
            double distanceSquared = entity.position().subtract(position()).horizontalDistanceSqr();
            if (distanceSquared > outerRadius * outerRadius) {
                entity.hurt(damageSources().magic(), OUTER_DAMAGE);
            } else if (distanceSquared
                    < (innerRadius - 5.0F)
                    * (innerRadius - 5.0F)) {
                entity.hurt(damageSources().magic(), ATTACHED_DAMAGE);
            } else if (distanceSquared
                    < innerRadius * innerRadius) {
                entity.hurt(damageSources().magic(), INNER_DAMAGE);
            }
        }
    }

    /// 对本次遭遇的每个参与者分别施加风暴牵引。
    ///
    /// <p>牵引由血肉山实体执行，而不是把可变的 Boss 引用保存在全局效果单例中，因此
    /// 多个血肉山或多名玩家同时存在时不会互相覆盖目标。距离越远时牵引越弱，主要用于
    /// 抑制持续向外逃离，同时保留玩家在主战斗带内调整位置的能力。</p>
    void applyStormPull() {
        encounterEntities.removeIf(entity -> !entity.isAlive());
        for (LivingEntity entity : encounterEntities) {
            Vec3 direction = position().subtract(entity.position());
            double distance = direction.length();
            if (distance < 1.0E-6) {
                continue;
            }
            double strength = Math.min(0.03, 0.5 / distance);
            entity.addDeltaMovement(direction.scale(strength / distance));
            entity.hasImpulse = true;
        }
    }

    private void updateSummons() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        fleshSlimeTimer--;
        if (fleshSlimeTimer <= 0) {
            fleshSlimeTimer = getSummonInterval(FLESH_SLIME_INTERVAL);
            spawnFleshSlimes(target);
        }
        if (!phase2) {
            return;
        }

        leechTimer--;
        if (leechTimer <= 0) {
            leechTimer = getSummonInterval(LEECH_INTERVAL);
            spawnLeeches(target);
        }
        lavaPillarTimer--;
        if (lavaPillarTimer <= 0) {
            lavaPillarTimer = LAVA_PILLAR_INTERVAL;
            spawnLavaPillars();
        }
    }

    private int getSummonInterval(int baseInterval) {
        return phase2
                ? Math.max(1, Mth.floor(baseInterval * 0.3F))
                : baseInterval;
    }

    private int getSummonLimit() {
        return isMaster() ? 7 : isExpert() ? 6 : 5;
    }

    int spawnFleshSlimes(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return 0;
        }
        int livingCount = countLivingSubEntities(FleshSlime.class);
        int requested = Math.min(getSummonLimit() - livingCount, isMaster() ? 3 : isExpert() ? 2 : 1);
        int spawned = 0;
        for (int index = 0; index < requested; index++) {
            FleshSlime slime = MonsterEntities.FLESH_SLIME.get().create(level());
            if (slime == null) {
                continue;
            }
            Entity mouth = parts[5 + random.nextInt(5)];
            slime.setPos(mouth != null ? mouth.position() : position());
            slime.configureSummonedSize(phase2 && isExpert() ? 4 : 2);
            slime.setTarget(target);
            if (serverLevel.addFreshEntity(slime)) {
                addSubEntity(slime);
                spawned++;
            } else {
                slime.discard();
            }
        }
        return spawned;
    }

    int spawnLeeches(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return 0;
        }
        int livingCount = countLivingSubEntities(SimpleWormMonster.class);
        int requested = Math.min(getSummonLimit() - livingCount, isMaster() ? 3 : isExpert() ? 2 : 1);
        int spawned = 0;
        for (int index = 0; index < requested; index++) {
            SimpleWormMonster leech = MonsterEntities.LEECH.get().create(level());
            if (leech == null) {
                continue;
            }
            Vec3 offset = target.position().subtract(position()).normalize();
            leech.setPos(position().add(offset));
            leech.setTarget(target);
            if (serverLevel.addFreshEntity(leech)) {
                addSubEntity(leech);
                spawned++;
            } else {
                leech.discard();
            }
        }
        return spawned;
    }

    int spawnLavaPillars() {
        if (!(level() instanceof ServerLevel)) {
            return 0;
        }
        List<LivingEntity> candidates = new ArrayList<>(nearbyLivingEntities);
        candidates.removeIf(entity -> Math.abs(entity.getY() - getY()) >= 3.0);
        if (candidates.isEmpty()) {
            return 0;
        }
        for (int index = candidates.size() - 1; index > 0; index--) {
            int swapIndex = random.nextInt(index + 1);
            LivingEntity value = candidates.get(index);
            candidates.set(index, candidates.get(swapIndex));
            candidates.set(swapIndex, value);
        }
        int count = Math.min(candidates.size(), Math.min(getSummonLimit(), 1 + random.nextInt(4)));
        int spawned = 0;
        for (int index = 0; index < count; index++) {
            LivingEntity target = candidates.get(index);
            if (spawnLavaPillarAt(target)) {
                spawned++;
            }
        }
        return spawned;
    }

    boolean spawnLavaPillarAt(LivingEntity target) {
        return spawnLavaPillarEntityAt(target) != null;
    }

    /// 在目标当前位置生成熔岩柱，并返回已经加入世界的实体。
    ///
    /// <p>调用方需要继续配置表现或验证实例时，应直接使用返回值，避免在同一
    /// tick 内重新扫描世界实体列表时受到区块实体列表刷新顺序影响。</p>
    @Nullable
    HillLavaPillarProjectile spawnLavaPillarEntityAt(LivingEntity target) {
        HillLavaPillarProjectile pillar = ModEntities.HILL_LAVA_PILLAR.get().create(level());
        if (pillar == null) {
            return null;
        }
        pillar.setPos(target.getX(), Math.min(getY(), target.getY()), target.getZ());
        pillar.configure(this, isMaster() ? 20.0F : isExpert() ? 17.0F : 14.0F);
        if (level().addFreshEntity(pillar)) {
            return pillar;
        }
        pillar.discard();
        return null;
    }

    private int countLivingSubEntities(Class<? extends LivingEntity> type) {
        return (int) getSubEntities().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(LivingEntity::isAlive)
                .count();
    }

    /// 血肉山本体资源只有持续蠕动的待机动画。
    ///
    /// 移动由整个战斗区域和子实体共同表达，因此不按位移切换动画；死亡阶段停止控制器，避免模型消失过程中继续循环。
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 10,
                state -> deathTime <= 0 ? state.setAndContinue(IDLE) : PlayState.STOP));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && !encounterEntities.contains(source.getEntity())) {
            return false;
        }
        return super.hurt(source, amount * 0.5F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(ENCOUNTER_TICKS_TAG, encounterTicks);
        tag.putBoolean(PHASE_TWO_TAG, phase2);
        tag.putInt(EXPANDING_TICKS_TAG, expandingTicks);
        tag.putFloat(OUTER_RADIUS_TAG, getOuterRadius());
        tag.putBoolean(TERRAIN_DESTRUCTION_TAG, terrainDestructionEnabled);
        tag.putInt(FLESH_SLIME_TIMER_TAG, fleshSlimeTimer);
        tag.putInt(LEECH_TIMER_TAG, leechTimer);
        tag.putInt(LAVA_PILLAR_TIMER_TAG, lavaPillarTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        encounterTicks = Math.max(0, tag.getInt(ENCOUNTER_TICKS_TAG));
        phase2 = tag.getBoolean(PHASE_TWO_TAG);
        expandingTicks = Mth.clamp(tag.getInt(EXPANDING_TICKS_TAG), 0, INNER_EXPANSION_TICKS);
        entityData.set(DATA_OUTER_RADIUS, Mth.clamp(tag.getFloat(OUTER_RADIUS_TAG), INNER_RADIUS, OUTER_RADIUS));
        terrainDestructionEnabled = tag.getBoolean(TERRAIN_DESTRUCTION_TAG);
        fleshSlimeTimer = restoreTimer(tag, FLESH_SLIME_TIMER_TAG, FLESH_SLIME_INTERVAL);
        leechTimer = restoreTimer(tag, LEECH_TIMER_TAG, LEECH_INTERVAL);
        lavaPillarTimer = restoreTimer(tag, LAVA_PILLAR_TIMER_TAG, LAVA_PILLAR_INTERVAL);
        entityData.set(DATA_INITIALIZING, encounterTicks < INITIALIZATION_TICKS);
        destructionTask = null;
        nearbyLivingEntities = List.of();
        encounterEntities.clear();
        java.util.Arrays.fill(parts, null);
    }

    private static int restoreTimer(CompoundTag tag, String key, int fallback) {
        return tag.contains(key)
                ? Math.max(0, tag.getInt(key)) : fallback;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected double getCombatPlayerRange() {
        return OUTER_RADIUS;
    }
}
