package org.confluence.mod.common.entity.boss;

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
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.SkeletronSkullProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;

/// 骷髅王本体。
///
/// <p>服务端以固定战斗周期控制悬浮与旋转追击：夜间前 267 tick 悬浮在目标上方，
/// 后 134 tick 旋转追击；白天立即进入狂暴旋转。双手各自保有生命值，但和头部共同
/// 构成同一条首领总血量，双手全部摧毁后头部防御归零。</p>
///
/// <p>手部实体是可重建的临时部件。这里只保存每个槽位是否已摧毁及剩余生命，
/// 避免区块重载复活已摧毁的手，或复制仍然存活的手。</p>
public class Skeletron extends BaseBoss {
    private static final int ALL_HANDS_DESTROYED = 0b11;
    private static final int FLOAT_PHASE_END = 267;
    private static final int COMBAT_CYCLE_END = 400;
    private static final int BASE_SKULL_COOLDOWN = 20;
    private static final float SKULL_DAMAGE = 6.0F;
    private static final float HAND_MAX_HEALTH = 405.0F;

    private static final String DESTROYED_HANDS_TAG = "DestroyedHands";
    private static final String PHASE_TWO_TAG = "PhaseTwo";
    private static final String HAND_HEALTH_TAG = "HandHealth";
    private static final String COMBAT_CYCLE_TAG = "CombatCycle";
    private static final EntityDataAccessor<Boolean> DATA_SPINNING =
            SynchedEntityData.defineId(
                    Skeletron.class, EntityDataSerializers.BOOLEAN);

    private SkeletronHand leftHand;
    private SkeletronHand rightHand;
    private boolean phase2;
    private int destroyedHands;
    private int combatCycle;
    private boolean floatingActive;
    private boolean floatingCrazy;
    private final float[] handHealth = {-1.0F, -1.0F};

    public Skeletron(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 2000;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 2288.0)
                .add(Attributes.ATTACK_DAMAGE, 18.2)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SPINNING, false);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    // === BT ===
    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(
                2, new NearestAttackableTargetGoal<>(
                        this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || level().isClientSide) {
            return;
        }

        ensureHands();
        updatePhaseTwo();

        combatCycle++;
        if (combatCycle > COMBAT_CYCLE_END) {
            combatCycle = 0;
        }

        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            floatingActive = false;
            setSpinning(false);
            return;
        }

        boolean enraged = level().isDay();
        boolean spinning = enraged || combatCycle >= FLOAT_PHASE_END;
        setSpinning(spinning);
        if (spinning) {
            floatingActive = false;
            updateSpinningMovement(target, enraged);
        } else {
            if (!floatingActive) {
                floatingActive = true;
                floatingCrazy = random.nextFloat() < 0.3F;
            }
            if (getY() < target.getY()) {
                setDeltaMovement(getDeltaMovement().add(0.0, 0.02, 0.0));
            }
            updateFloatingMovement(target);
            updateSkullAttack(target);
        }
        faceMovement();
    }

    /// 悬浮阶段采用带阻尼的加速度，而不是每 tick 瞬间改向。
    ///
    /// <p>目标点位于玩家上方五格。速度上限按当前世界难度选择，避免近距离抖动，
    /// 同时让专家及大师难度具有更强的追随压力。</p>
    private void updateFloatingMovement(LivingEntity target) {
        double acceleration = isExpert() ? 0.1 : 0.07;
        double maximumSpeed = isExpert() ? 1.0 : 0.7;
        Vec3 targetPosition = target.position().add(0.0, 5.0, 0.0);
        Vec3 dampedVelocity = getDeltaMovement().scale(10.0);
        double horizontalDistance = target.position()
                .subtract(position()).length();
        Vec3 correction = targetPosition
                .subtract(position())
                .subtract(dampedVelocity);
        if (correction.lengthSqr() <= 1.0E-7) {
            return;
        }

        double strength = Math.max(
                acceleration * (0.07 * horizontalDistance - 0.29), 0.01);
        Vec3 result = getDeltaMovement()
                .add(correction.normalize().scale(strength));
        if (floatingCrazy) {
            result = result.add(
                    target.position().subtract(position()).scale(0.01));
        }
        if (result.length() > maximumSpeed) {
            result = result.normalize().scale(maximumSpeed);
        }
        setDeltaMovement(result);
    }

    /// 旋转阶段直接朝目标追击。
    ///
    /// <p>白天狂暴固定为最高速度；夜间普通难度保持较慢追击，专家及以上则根据
    /// 距离和剩余手数提高速度，与 1.21 实现保持同一组核心公式。</p>
    private void updateSpinningMovement(
            LivingEntity target, boolean enraged) {
        Vec3 direction = target.position().subtract(position());
        if (direction.lengthSqr() <= 1.0E-7) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        double speed;
        if (enraged) {
            speed = 1.0;
        } else if (isExpert()) {
            speed = Mth.clamp(
                    0.01 * direction.length() + 0.16, 0.22, 0.48);
            if (isFtw()) {
                speed *= 1.3;
            }
            int remainingHands = getRemainingHandCount();
            if (remainingHands == 1) {
                speed *= 1.05;
            } else if (remainingHands == 0) {
                speed *= 1.1;
            }
        } else {
            speed = 0.2;
        }
        setDeltaMovement(direction.normalize().scale(speed));
    }

    private void faceMovement() {
        Vec3 velocity = getDeltaMovement();
        if (velocity.horizontalDistanceSqr() <= 1.0E-7) {
            return;
        }
        float yaw = (float) (
                Mth.atan2(velocity.z, velocity.x) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(yaw);
        yBodyRot = yaw;
        yHeadRot = yaw;
    }

    private void setSpinning(boolean spinning) {
        boolean previous = entityData.get(DATA_SPINNING);
        if (previous == spinning) {
            return;
        }
        entityData.set(DATA_SPINNING, spinning);
        if (spinning) {
            playSound(ModSoundEvents.ROAR.get());
        }
    }

    public boolean isSpinning() {
        return entityData.get(DATA_SPINNING);
    }

    /// 骷髅王在悬浮和旋转阶段始终由自身速度公式控制高度。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    private void updateSkullAttack(LivingEntity target) {
        if (!shouldShootSkull()) {
            return;
        }
        int interval = getRemainingHandCount() == 0
                ? BASE_SKULL_COOLDOWN / 2
                : BASE_SKULL_COOLDOWN;
        if (isFtw()) {
            interval = Math.max(1, (int) (interval * 0.8F));
        }
        if (tickCount % interval == 0) {
            shootSkull(target);
        }
    }

    private boolean shouldShootSkull() {
        return isExpert()
                && (getHealth() / getMaxHealth() < 0.75F
                || getRemainingHandCount() < 2);
    }

    /// 生成一枚持续追踪本次目标的敌对骷髅弹。
    ///
    /// @return 实体成功创建并加入世界时为 {@code true}
    boolean shootSkull(LivingEntity target) {
        SkeletronSkullProjectile projectile =
                ModEntities.SKELETRON_SKULL.get().create(level());
        if (projectile == null) {
            return false;
        }
        projectile.configure(this, target, SKULL_DAMAGE);
        return level().addFreshEntity(projectile);
    }

    private void ensureHands() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if ((destroyedHands & 1) == 0
                && (leftHand == null || !leftHand.isAlive())) {
            leftHand = spawnHand(serverLevel, 0);
        }
        if ((destroyedHands & 2) == 0
                && (rightHand == null || !rightHand.isAlive())) {
            rightHand = spawnHand(serverLevel, 1);
        }
    }

    private SkeletronHand spawnHand(ServerLevel serverLevel, int index) {
        SkeletronHand hand =
                BossEntities.SKELETRON_HAND.get().create(level());
        if (hand == null) {
            return null;
        }
        hand.setPos(position());
        hand.setMaster(this, index);
        if (handHealth[index] > 0.0F) {
            hand.setPartHealth(handHealth[index]);
        } else {
            handHealth[index] = hand.getPartHealth();
        }
        if (!serverLevel.addFreshEntity(hand)) {
            hand.discard();
            return null;
        }
        return hand;
    }

    void onHandHealthChanged(int index, float remainingHealth) {
        if (index >= 0 && index < handHealth.length) {
            handHealth[index] = remainingHealth;
        }
    }

    void onHandDestroyed(int index, SkeletronHand hand) {
        if (index < 0 || index > 1) {
            return;
        }
        destroyedHands |= 1 << index;
        handHealth[index] = 0.0F;
        if (index == 0 && leftHand == hand) {
            leftHand = null;
        }
        if (index == 1 && rightHand == hand) {
            rightHand = null;
        }
        updatePhaseTwo();
    }

    private void updatePhaseTwo() {
        if (!phase2 && destroyedHands == ALL_HANDS_DESTROYED) {
            phase2 = true;
            if (getAttribute(Attributes.ARMOR) != null) {
                getAttribute(Attributes.ARMOR).setBaseValue(0.0);
            }
            broadcastPhaseTransition();
        }
    }

    private int getRemainingHandCount() {
        return 2 - Integer.bitCount(
                destroyedHands & ALL_HANDS_DESTROYED);
    }

    public SkeletronHand getHand(int index) {
        return index == 0 ? leftHand : index == 1 ? rightHand : null;
    }

    public int getDestroyedHandsMask() {
        return destroyedHands;
    }

    public boolean isPhase2() {
        return phase2;
    }

    /// 返回头部与两只手共同组成的遭遇血量比例。
    ///
    /// <p>分母始终包含两只手的最大生命值；已摧毁手的当前生命为零，因此 Boss 条
    /// 不会在部件死亡时突然扩张或缩短。</p>
    float getEncounterProgress() {
        float current = getHealth();
        for (float health : handHealth) {
            current += Math.max(0.0F, health);
        }
        float maximum = getMaxHealth() + HAND_MAX_HEALTH * 2.0F;
        return Mth.clamp(current / maximum, 0.0F, 1.0F);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getEncounterProgress());
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return !(entity instanceof Skeletron) && super.canAttack(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(DESTROYED_HANDS_TAG, destroyedHands);
        tag.putBoolean(PHASE_TWO_TAG, phase2);
        tag.putInt(COMBAT_CYCLE_TAG, combatCycle);
        for (int index = 0; index < handHealth.length; index++) {
            tag.putFloat(HAND_HEALTH_TAG + index, handHealth[index]);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        destroyedHands =
                tag.getInt(DESTROYED_HANDS_TAG) & ALL_HANDS_DESTROYED;
        phase2 = tag.getBoolean(PHASE_TWO_TAG)
                || destroyedHands == ALL_HANDS_DESTROYED;
        combatCycle = Mth.clamp(
                tag.getInt(COMBAT_CYCLE_TAG), 0, COMBAT_CYCLE_END);
        if (phase2) {
            destroyedHands = ALL_HANDS_DESTROYED;
            if (getAttribute(Attributes.ARMOR) != null) {
                getAttribute(Attributes.ARMOR).setBaseValue(0.0);
            }
        }
        for (int index = 0; index < handHealth.length; index++) {
            String key = HAND_HEALTH_TAG + index;
            handHealth[index] = (destroyedHands & 1 << index) != 0
                    ? 0.0F
                    : tag.contains(key) ? tag.getFloat(key) : -1.0F;
        }
        leftHand = null;
        rightHand = null;
        setSpinning(false);
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance,
            float multiplier,
            DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {return false;}

    @Override
    protected boolean shouldDiscardWhenNoTarget() {return true;}
}
