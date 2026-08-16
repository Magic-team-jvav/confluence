package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
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
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.PlanteraProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;

public class Plantera extends BaseBoss {
    static final int HOOK_COUNT = 3;
    static final int BODY_TENTACLE_COUNT = 8;
    static final int TENTACLES_PER_HOOK = 3;
    static final int TENTACLE_COUNT =
            BODY_TENTACLE_COUNT + HOOK_COUNT * TENTACLES_PER_HOOK;
    private static final double HOOK_SEARCH_RANGE = 48.0;
    private static final double HOOK_TETHER_LENGTH = 48.0;
    private static final double PHASE_ONE_MOVE_SPEED = 0.1;
    private static final double PHASE_TWO_MOVE_SPEED = 0.2;
    private static final double ENRAGED_MOVE_SPEED = 0.2;
    private static final double MOVE_ACCELERATION = 0.1;
    private static final float SEED_SPEED = 2.5F;
    private static final float THORN_SPEED = 0.85F;
    private static final float SPORE_SPEED = 2.5F;
    private static final int ATTACK_WARMUP_TICKS = 100;
    private static final int SEED_INTERVAL_FULL_HEALTH = 27;
    private static final int SEED_INTERVAL_LIMIT = 13;
    private static final int THORN_INTERVAL_FULL_HEALTH = 22;
    private static final int THORN_INTERVAL_LIMIT = 14;
    private static final int SPORE_INTERVAL_HALF_HEALTH = 27;
    private static final int SPORE_INTERVAL_LIMIT = 13;
    private static final int TENTACLE_REBUILD_BASE_COOLDOWN = 100;
    private static final String PHASE_TAG = "Phase";
    private static final String ATTACK_TICKS_TAG = "AttackTicks";
    private static final String TENTACLE_TIMER_TAG = "TentacleTimer";
    private static final String ENRAGED_TICKS_TAG = "EnragedTicks";
    private static final EntityDataAccessor<Integer> DATA_PHASE =
            SynchedEntityData.defineId(
                    Plantera.class, EntityDataSerializers.INT);
    private final PlanteraHook[] hooks = new PlanteraHook[HOOK_COUNT];
    private final PlanteraTentacle[] tentacles =
            new PlanteraTentacle[TENTACLE_COUNT];
    private int attackTicks;
    private int tentacleTimer;
    private int enragedTicks;

    public Plantera(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        setNoGravity(true);
        this.xpReward = 2000;
    }

    /// 世纪之花的位置由钩爪约束和阶段移动共同决定。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 10920.0)
                .add(Attributes.ATTACK_DAMAGE, 26.0)
                .add(Attributes.ARMOR, 36.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.GREEN;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PHASE, 0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                /// 世纪之花的位移由服务端 tick 统一计算。行为树只保持存活，不能再
                /// 并行写入速度，否则钩爪牵引会与冲刺节点互相覆盖并产生瞬时加速。
                return new WaitAction(20);
            }
        };
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
            if (tickCount == 1 || tickCount % 20 == 0) ensureHooks();
            if (enragedTicks > 0) {
                enragedTicks--;
            }
            updatePhase();

            if (getTarget() == null && tickCount % 20 == 0) {
                Player replacement = findCombatPlayer();
                if (replacement != null) setTarget(replacement);
            }

            // 1.21 侧从出生起推进全局 AI 刻，取得目标后直接读取同一条时间轴。
            attackTicks++;
            if (getTarget() != null) {
                if (distanceToSqr(getTarget())
                        > HOOK_SEARCH_RANGE * HOOK_SEARCH_RANGE) {
                    enrage();
                }
                tickProjectileAttacks();
            }

            // 第二阶段持续维护固定槽位的附着触手。
            if (getPhase() >= 1) {
                if (tentacleTimer > 0) {
                    tentacleTimer--;
                }
                if (tentacleTimer <= 0 && tickCount % 5 == 0) {
                    ensureTentacles();
                }
            }

            updateHookCycle();
            updateMovement();
        }
    }

    private void updatePhase() {
        if (getPhase() == 0
                && getHealth() / getMaxHealth() < 0.5F) {
            entityData.set(DATA_PHASE, 1);
            tentacleTimer = 0;
            broadcastPhaseTransition();
        }
    }

    public int getPhase() {
        return entityData.get(DATA_PHASE);
    }

    /// 按生命阶段分别推进种子、刺球和孢子的独立发射节拍。
    ///
    /// <p>出生后的前 100 tick 只允许世纪之花接近并展开钩爪，避免玩家在实体刚加载时
    /// 遭遇无法预判的零帧弹幕。第一阶段的种子与刺球互不占用冷却，因此同一 tick
    /// 可以同时发射；第二阶段才会完全切换为孢子。</p>
    private void tickProjectileAttacks() {
        if (attackTicks <= ATTACK_WARMUP_TICKS) {
            return;
        }
        if (isEnraged()) {
            if (attackTicks % SEED_INTERVAL_LIMIT == 0) {
                spawnProjectile(ModEntities.PLANTERA_SEED.get(), getSeedDamage(), SEED_SPEED, 0.02F);
            }
            if (attackTicks % THORN_INTERVAL_LIMIT == 0) {
                spawnProjectile(ModEntities.PLANTERA_THORN_BALL.get(), getThornDamage(), THORN_SPEED, 0.02F);
            }
            if (attackTicks % SPORE_INTERVAL_LIMIT == 0) {
                spawnProjectile(ModEntities.PLANTERA_SPORE.get(), getSporeDamage(), SPORE_SPEED, 0.04F);
            }
            return;
        }
        float healthRatio = getHealth() / getMaxHealth();
        if (getPhase() == 0) {
            float firstPhaseProgress =
                    Mth.clamp(healthRatio * 2.0F - 1.0F, 0.0F, 1.0F);
            int seedInterval = Math.round(Mth.lerp(
                    firstPhaseProgress,
                    SEED_INTERVAL_LIMIT,
                    SEED_INTERVAL_FULL_HEALTH));
            int thornInterval = Math.round(Mth.lerp(
                    firstPhaseProgress,
                    THORN_INTERVAL_LIMIT,
                    THORN_INTERVAL_FULL_HEALTH));
            if (attackTicks % seedInterval == 0) {
                spawnProjectile(ModEntities.PLANTERA_SEED.get(), getSeedDamage(), SEED_SPEED, 0.02F);
            }
            if (attackTicks % thornInterval == 0) {
                spawnProjectile(ModEntities.PLANTERA_THORN_BALL.get(), getThornDamage(), THORN_SPEED, 0.02F);
            }
            return;
        }

        float secondPhaseProgress =
                Mth.clamp(healthRatio * 2.0F, 0.0F, 1.0F);
        int sporeInterval = Math.round(Mth.lerp(
                secondPhaseProgress,
                SPORE_INTERVAL_LIMIT,
                SPORE_INTERVAL_HALF_HEALTH));
        if (attackTicks % sporeInterval == 0) {
            spawnProjectile(ModEntities.PLANTERA_SPORE.get(), getSporeDamage(), SPORE_SPEED, 0.04F);
        }
    }

    /// 立即执行一次当前阶段的基础射击。
    ///
    /// <p>自然战斗不通过此入口推进冷却；它保留为遭遇脚本和行为测试可调用的单次动作。
    /// 第一阶段发射种子，第二阶段发射孢子，刺球仍只由独立自然节拍负责。</p>
    boolean shootAtTarget() {
        return getPhase() == 0
                ? spawnProjectile(
                ModEntities.PLANTERA_SEED.get(),
                getSeedDamage(), SEED_SPEED, 0.02F)
                : spawnProjectile(
                ModEntities.PLANTERA_SPORE.get(),
                getSporeDamage(), SPORE_SPEED, 0.04F);
    }

    private float getSeedDamage() {
        return LibUtils.switchByDifficulty(
                level(), blockPosition(), 12.0F, 19.0F, 28.0F, 28.0F);
    }

    private float getThornDamage() {
        return LibUtils.switchByDifficulty(
                level(), blockPosition(), 18.0F, 28.0F, 42.0F, 42.0F);
    }

    private float getSporeDamage() {
        return LibUtils.switchByDifficulty(
                level(), blockPosition(), 12.0F, 19.0F, 28.0F, 28.0F);
    }

    private boolean spawnProjectile(
            EntityType<? extends PlanteraProjectile> type,
            float damage,
            float velocity,
            float inaccuracy) {
        LivingEntity target = getTarget();
        if (target == null) {
            return false;
        }

        PlanteraProjectile projectile = type.create(level());
        if (projectile == null) {
            return false;
        }
        projectile.configure(
                this,
                target,
                damage,
                velocity,
                inaccuracy);
        if (level().addFreshEntity(projectile)) {
            return true;
        }
        projectile.discard();
        return false;
    }

    void bindHook(PlanteraHook hook) {
        int index = hook.getHookIndex();
        if (index >= 0 && index < hooks.length) hooks[index] = hook;
    }

    void bindTentacle(PlanteraTentacle tentacle) {
        int slot = tentacle.getSlot();
        if (slot >= 0 && slot < tentacles.length) {
            tentacles[slot] = tentacle;
        }
    }

    PlanteraTentacle[] getTentacles() {
        return tentacles;
    }

    public PlanteraHook getHook(int index) {
        return index < 0 || index >= hooks.length ? null : hooks[index];
    }

    private void ensureHooks() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (int index = 0; index < hooks.length; index++) {
            PlanteraHook current = hooks[index];
            if (current != null && current.isAlive() && !current.isRemoved()) continue;

            PlanteraHook hook = BossEntities.PLANTERA_HOOK.get().create(level());
            if (hook == null) continue;
            hook.setPos(position().add(hookRestOffset(index)));
            hook.setMaster(this, index, null);
            if (!serverLevel.addFreshEntity(hook)) {
                removeSubEntity(hook);
                hooks[index] = null;
            }
        }
    }

    Vec3 hookRestOffset(int index) {
        double angle = index * Mth.TWO_PI / HOOK_COUNT;
        return new Vec3(Math.cos(angle) * 2.5, (index - 1) * 0.8, Math.sin(angle) * 2.5);
    }

    boolean isValidHookAnchor(BlockPos anchor) {
        return level().hasChunkAt(anchor) && level().getBlockState(anchor).blocksMotion();
    }

    BlockPos findHookAnchor(int hookIndex) {
        double baseAngle = hookIndex * Mth.TWO_PI / HOOK_COUNT + tickCount * 0.0125;
        for (int attempt = 0; attempt < 18; attempt++) {
            double angle = baseAngle + attempt * 2.399963229728653;
            double vertical = ((attempt % 7) - 3) * 0.14;
            Vec3 direction = new Vec3(Math.cos(angle), vertical, Math.sin(angle)).normalize();
            for (double distance = 4.0; distance <= HOOK_SEARCH_RANGE; distance += 2.0) {
                BlockPos candidate = BlockPos.containing(position().add(direction.scale(distance)));
                if (isValidHookAnchor(candidate)) return candidate;
            }
        }
        return null;
    }

    /// 以 1.21 侧相同的加速度和阶段速度上限追踪玩家，同时叠加已抓牢钩爪的牵引。
    /// 所有加速度先合并、再统一限速，保证任意难度和生命阶段都不会产生额外冲刺。
    private void updateMovement() {
        LivingEntity target = getTarget();
        if (target == null) {
            return;
        }

        Vec3 offsetToTarget = target.position().subtract(position());
        Vec3 acceleration = offsetToTarget.lengthSqr() < 1.0E-8
                ? Vec3.ZERO
                : offsetToTarget.normalize().scale(MOVE_ACCELERATION);
        for (PlanteraHook hook : hooks) {
            if (hook == null || hook.isRemoved() || !hook.hasReachedAnchor()) continue;
            Vec3 offset = hook.position().subtract(position());
            double distance = offset.length();
            if (distance <= HOOK_TETHER_LENGTH) continue;
            acceleration = acceleration.add(offset.scale(
                    (distance - HOOK_TETHER_LENGTH)
                            * MOVE_ACCELERATION / (distance * 25.0)));
        }

        double maximumSpeed = isEnraged()
                ? ENRAGED_MOVE_SPEED
                : getPhase() == 0
                ? PHASE_ONE_MOVE_SPEED
                : PHASE_TWO_MOVE_SPEED;
        Vec3 velocity = getDeltaMovement().add(acceleration);
        if (velocity.lengthSqr() > maximumSpeed * maximumSpeed) {
            velocity = velocity.normalize().scale(maximumSpeed);
        }
        setDeltaMovement(velocity);
    }

    /// 进入或刷新 200 tick 的狂暴窗口。只有首次进入时播放咆哮；
    /// 持续缺少锚点或玩家仍在范围外只刷新计时，不会每 tick 重复播放声音。
    private void enrage() {
        if (enragedTicks <= 0) {
            playSound(ModSoundEvents.ROAR.get());
        }
        enragedTicks = 200;
    }

    public boolean isEnraged() {
        return enragedTicks > 0;
    }

    /// 补齐第二阶段缺失的触手槽位。
    ///
    /// <p>首次转阶段会一次生成全部 17 根；之后若有触手被击毁，则按当前存活数量
    /// 延长重建间隔。触手是临时实体，区块重新加载后同样从这些权威槽位恢复。</p>
    private void ensureTentacles() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int slot = 0; slot < tentacles.length; slot++) {
            PlanteraTentacle current = tentacles[slot];
            if (current != null && current.isAlive() && !current.isRemoved()) {
                continue;
            }

            PlanteraTentacle created =
                    BossEntities.PLANTERA_TENTACLE.get().create(level());
            if (created == null) {
                continue;
            }
            created.setPos(getTentacleAnchor(slot));
            created.setMaster(this, slot);
            if (!serverLevel.addFreshEntity(created)) {
                removeSubEntity(created);
                tentacles[slot] = null;
                created.discard();
            }
        }
    }

    /// 每 50 tick 展开空闲钩爪，并在周期中点收回离目标最远的一根。
    ///
    /// <p>钩爪不再定时瞬间改写锚点，而是完整经过空闲、伸展、抓牢和收回四态。
    /// 主体牵引只读取真正抓牢的钩爪，客户端因此能获得连续的伸缩运动。</p>
    private void updateHookCycle() {
        LivingEntity target = getTarget();
        if (target == null) {
            return;
        }
        int cycleTick = tickCount % 50;
        if (cycleTick == 0) {
            for (int index = 0; index < hooks.length; index++) {
                PlanteraHook hook = hooks[index];
                if (hook == null || hook.isRemoved()
                        || hook.getState() != PlanteraHook.STATE_IDLE) {
                    continue;
                }
                BlockPos anchor = findHookAnchor(index);
                if (anchor != null) {
                    hook.setAnchor(anchor);
                    hook.setState(PlanteraHook.STATE_EXTENDING);
                } else {
                    enrage();
                }
            }
            return;
        }
        if (cycleTick != 25) {
            return;
        }

        int grabbed = 0;
        PlanteraHook farthest = null;
        double farthestDistance = -1.0;
        for (PlanteraHook hook : hooks) {
            if (hook == null || hook.isRemoved()
                    || hook.getState() != PlanteraHook.STATE_GRABBED) {
                continue;
            }
            grabbed++;
            double distance = hook.distanceToSqr(target);
            if (distance > farthestDistance) {
                farthestDistance = distance;
                farthest = hook;
            }
        }
        if (grabbed == HOOK_COUNT && farthest != null) {
            farthest.setAnchor(null);
            farthest.setState(PlanteraHook.STATE_RETRACTING);
        }
    }

    /// 返回触手槽位当前附着的锚点。钩爪尚未完成重建时退回本体的对应方向，
    /// 避免触手短暂出现在世界原点或错误玩家身边。
    Vec3 getTentacleAnchor(int slot) {
        if (slot < BODY_TENTACLE_COUNT) {
            return getEyePosition();
        }
        int hookIndex = (slot - BODY_TENTACLE_COUNT) / TENTACLES_PER_HOOK;
        PlanteraHook hook = getHook(hookIndex);
        return hook == null || hook.isRemoved()
                ? position().add(hookRestOffset(hookIndex))
                : hook.getEyePosition();
    }

    /// 返回触手所属本体或钩爪的当前速度。
    Vec3 getTentacleAnchorVelocity(int slot) {
        if (slot < BODY_TENTACLE_COUNT) {
            return getDeltaMovement();
        }
        int hookIndex = (slot - BODY_TENTACLE_COUNT)
                / TENTACLES_PER_HOOK;
        PlanteraHook hook = getHook(hookIndex);
        return hook == null || hook.isRemoved()
                ? getDeltaMovement()
                : hook.getDeltaMovement();
    }

    /// 用黄金角为刚生成且尚无朝向的触手提供稳定初始方向；进入运动后仍由
    /// 1.21 的吸引和排斥公式连续调整，不会锁死在固定槽位上。
    Vec3 getTentacleBaseDirection(int slot) {
        int localSlot;
        int count;
        double phaseOffset;
        if (slot < BODY_TENTACLE_COUNT) {
            localSlot = slot;
            count = BODY_TENTACLE_COUNT;
            phaseOffset = 0.0;
        } else {
            int hookIndex = (slot - BODY_TENTACLE_COUNT) / TENTACLES_PER_HOOK;
            localSlot = (slot - BODY_TENTACLE_COUNT) % TENTACLES_PER_HOOK;
            count = TENTACLES_PER_HOOK;
            phaseOffset = hookIndex * Mth.TWO_PI / HOOK_COUNT;
        }
        double y = 1.0 - 2.0 * (localSlot + 0.5) / count;
        double radius = Math.sqrt(Math.max(0.0, 1.0 - y * y));
        double angle = localSlot * 2.399963229728653 + phaseOffset;
        return new Vec3(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
    }

    void onTentacleDestroyed(int slot, PlanteraTentacle destroyed) {
        if (slot >= 0 && slot < tentacles.length && tentacles[slot] == destroyed) {
            tentacles[slot] = null;
        }
        int alive = 0;
        for (PlanteraTentacle tentacle : tentacles) {
            if (tentacle != null && tentacle.isAlive() && !tentacle.isRemoved()) {
                alive++;
            }
        }
        tentacleTimer = Math.max(
                tentacleTimer,
                TENTACLE_REBUILD_BASE_COOLDOWN * Math.max(1, alive + 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(PHASE_TAG, getPhase());
        tag.putInt(ATTACK_TICKS_TAG, attackTicks);
        tag.putInt(TENTACLE_TIMER_TAG, tentacleTimer);
        tag.putInt(ENRAGED_TICKS_TAG, enragedTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(
                DATA_PHASE,
                Mth.clamp(tag.getInt(PHASE_TAG), 0, 1));
        attackTicks = Math.max(0, tag.getInt(ATTACK_TICKS_TAG));
        tentacleTimer = Math.max(
                0, tag.getInt(TENTACLE_TIMER_TAG));
        enragedTicks = Mth.clamp(
                tag.getInt(ENRAGED_TICKS_TAG), 0, 200);
        java.util.Arrays.fill(hooks, null);
        java.util.Arrays.fill(tentacles, null);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall()
                || source.is(DamageTypeTags.IS_DROWNING)
                || super.isInvulnerableTo(source);
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return !(entity instanceof Plantera)
                && super.canAttack(entity);
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected boolean shouldDiscardWhenNoTarget() { return true; }
}
