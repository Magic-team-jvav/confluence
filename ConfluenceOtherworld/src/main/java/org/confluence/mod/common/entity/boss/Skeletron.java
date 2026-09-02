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
/// 服务端以固定战斗周期控制悬浮与旋转追击：夜间前 267 tick 悬浮在目标上方，
/// 后 134 tick 旋转追击；白天立即进入狂暴旋转。双手各自保有生命值，但和头部共同
/// 构成同一条首领总血量，双手全部摧毁后头部防御归零。
///
/// 手部实体是可重建的临时部件。这里只保存每个槽位是否已摧毁及剩余生命，
/// 避免区块重载复活已摧毁的手，或复制仍然存活的手。
public class Skeletron extends BaseBoss {
    // 非旋转阶段相对目标眼睛的悬浮偏移，单位均为方块。
    private static final double FLOAT_HEIGHT_ABOVE_TARGET = 7.0D;
    private static final double FLOAT_HORIZONTAL_DISTANCE = 6.0D;
    // 两个低位分别代表左、右手；两位均置一表示双手都已被摧毁。
    private static final int ALL_HANDS_DESTROYED = 0b11;
    // 一个 400 tick 战斗循环中，[0, 267) 为悬浮阶段，其余时间为旋转追击阶段。
    private static final int FLOAT_PHASE_END = 267;
    private static final int COMBAT_CYCLE_END = 400;
    // 骷髅弹的基础发射间隔（tick）和单发基础伤害；难度修正另行计算。
    private static final int BASE_SKULL_COOLDOWN = 20;
    private static final float SKULL_DAMAGE = 6.0F;
    // 每只手在难度及多人倍率生效前的基础生命和接触伤害。
    private static final float BASE_HAND_MAX_HEALTH = 405.0F;
    private static final float BASE_HAND_CONTACT_DAMAGE = 10.0F;

    private static final String DESTROYED_HANDS_TAG = "DestroyedHands";
    private static final String PHASE_TWO_TAG = "PhaseTwo";
    private static final String HAND_HEALTH_TAG = "HandHealth";
    private static final String HAND_MAX_HEALTH_TAG = "HandMaxHealth";
    private static final String COMBAT_CYCLE_TAG = "CombatCycle";
    private static final String INITIAL_ROAR_PLAYED_TAG = "InitialRoarPlayed";
    private static final EntityDataAccessor<Boolean> DATA_SPINNING = SynchedEntityData.defineId(Skeletron.class, EntityDataSerializers.BOOLEAN);

    private SkeletronHand leftHand;
    private SkeletronHand rightHand;
    private boolean phase2;
    private int destroyedHands;
    private int combatCycle;
    private boolean floatingActive;
    private boolean floatingCrazy;
    private float handMaxHealth = BASE_HAND_MAX_HEALTH;
    private final float[] handHealth = {BASE_HAND_MAX_HEALTH, BASE_HAND_MAX_HEALTH};
    private boolean initialRoarPlayed;
    private boolean restoringSavedState;
    private int lastRoarTick = Integer.MIN_VALUE;

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
                .add(Attributes.FOLLOW_RANGE, 300.0);
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
    public void tick() {
        if (!level().isClientSide) {
            synchronizeHandStats();
        }
        super.tick();
        if (isRemoved() || level().isClientSide) {
            return;
        }

        if (!initialRoarPlayed) {
            initialRoarPlayed = true;
            playRoarOnce();
        }

        LivingEntity target = getTarget();

        ensureHands();
        updatePhaseTwo();

        if (target == null || !target.isAlive()) {
            floatingActive = false;
            setSpinning(false);
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            LivingEntity observer = findVisualObserver();
            if (observer != null) faceTarget(observer);
            advanceCombatCycle();
            restoringSavedState = false;
            return;
        }

        boolean enraged = level().isDay();
        boolean spinning = enraged || combatCycle >= FLOAT_PHASE_END;
        setSpinning(spinning);
        if (spinning) {
            floatingActive = false;
            updateSpinningMovement(target, enraged);
            faceMovementDirection(30.0F, 30.0F);
        } else {
            if (!floatingActive) {
                floatingActive = true;
                floatingCrazy = random.nextFloat() < 0.3F;
            }
            if (getY() < target.getY()) {
                setDeltaMovement(getDeltaMovement().add(0.0, 0.02, 0.0));
            }
            updateFloatingMovement(target);
            faceTarget(target);
            updateSkullAttack(target);
        }
        advanceCombatCycle();
        restoringSavedState = false;
    }

    /// 将实体朝向对准目标，并在同一处更新身体和头部角度。
    ///
    /// 骷髅王只有这一份权威水平朝向；渲染插值、双手站位与战斗运动都从实体角度读取，
    /// 不再维护可能与实体旋转脱节的第二份同步状态。
    private void faceTarget(LivingEntity target) {
        Vec3 direction = target.getEyePosition().subtract(getEyePosition());
        applyExclusiveFacing(direction, 90.0F, 85.0F);
    }

    private void faceMovementDirection(float maximumYawChange, float maximumPitchChange) {
        applyExclusiveFacing(getDeltaMovement(), maximumYawChange, maximumPitchChange);
    }

    /// 骷髅王专用状态机是唯一朝向权威，不向 LookControl 留下下一刻会被重放的命令。
    private void applyExclusiveFacing(Vec3 direction, float maximumYawChange, float maximumPitchChange) {
        if (direction.lengthSqr() <= 1.0E-7D) return;
        double horizontal = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float targetYaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float targetPitch = (float) (-Mth.atan2(direction.y, horizontal) * Mth.RAD_TO_DEG);
        float yaw = Mth.rotateIfNecessary(targetYaw, getYRot(), maximumYawChange);
        float pitch = Mth.rotateIfNecessary(targetPitch, getXRot(), maximumPitchChange);
        setYRot(yaw);
        setXRot(pitch);
        setYBodyRot(yaw);
        setYHeadRot(yaw);
    }

    /// 返回实体网络角度的帧间插值，供头部和手臂使用同一朝向来源。
    public float getFacingYaw(float partialTick) {
        return Mth.rotLerp(partialTick, yRotO, getYRot());
    }

    /// 返回头部俯仰的帧间插值。实体朝向仍由服务端统一计算，客户端只在旧值与同步值之间过渡。
    public float getFacingPitch(float partialTick) {
        return Mth.lerp(partialTick, xRotO, getXRot());
    }

    /// 创造玩家可以作为视觉观察者；旁观玩家和视觉观察均不会写入战斗目标。
    private Player findVisualObserver() {
        double range = getAttributeValue(Attributes.FOLLOW_RANGE);
        double rangeSqr = range * range;
        Player nearest = null;
        double nearestDistanceSqr = rangeSqr;
        for (Player player : level().players()) {
            if (player.level() != level() || !player.isAlive() || player.isSpectator()) {
                continue;
            }
            double distanceSqr = distanceToSqr(player);
            if (distanceSqr < nearestDistanceSqr) {
                nearest = player;
                nearestDistanceSqr = distanceSqr;
            }
        }
        return nearest;
    }

    private void advanceCombatCycle() {
        combatCycle = combatCycle >= COMBAT_CYCLE_END ? 0 : combatCycle + 1;
    }

    /// 悬浮阶段采用带阻尼的加速度，而不是每 tick 瞬间改向。
    ///
    /// 目标点位于玩家上方五格。速度上限按当前世界难度选择，避免近距离抖动，
    /// 同时让专家及大师难度具有更强的追随压力。
    private void updateFloatingMovement(LivingEntity target) {
        double acceleration = isFtw() ? 0.16 : isExpert() ? 0.1 : 0.07;
        double maximumSpeed = isFtw() ? 2.0 : isExpert() ? 1.0 : 0.7;
        Vec3 horizontalAway = position().subtract(target.position()).multiply(1.0D, 0.0D, 1.0D);
        if (horizontalAway.lengthSqr() <= 1.0E-7D) {
            float yaw = getYRot() * Mth.DEG_TO_RAD;
            horizontalAway = new Vec3(Mth.sin(yaw), 0.0D, -Mth.cos(yaw));
        }
        Vec3 targetPosition = target.position()
                .add(horizontalAway.normalize().scale(FLOAT_HORIZONTAL_DISTANCE))
                .add(0.0D, FLOAT_HEIGHT_ABOVE_TARGET, 0.0D);
        Vec3 dampedVelocity = getDeltaMovement().scale(10.0);
        double targetDistance = target.position().subtract(position()).length();
        Vec3 correction = targetPosition.subtract(position()).subtract(dampedVelocity);
        if (correction.lengthSqr() <= 1.0E-7) {
            return;
        }

        double strength = Math.max(acceleration * (0.07 * targetDistance - 0.29), 0.01);
        Vec3 result = getDeltaMovement().add(correction.normalize().scale(strength));
        if (result.length() > maximumSpeed) {
            result = result.normalize().scale(maximumSpeed);
        }
        if (floatingCrazy) {
            // 疯狂悬浮只增加水平压迫，不能用玩家脚底的 Y 坐标持续把头部往地面拉。
            Vec3 horizontalPressure = target.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            result = result.add(horizontalPressure.scale(0.01));
            if (result.length() > maximumSpeed) {
                result = result.normalize().scale(maximumSpeed);
            }
        }
        setDeltaMovement(result);
    }

    /// 旋转阶段直接朝目标追击。
    ///
    /// 白天狂暴固定为最高速度；夜间普通难度保持较慢追击，专家及以上则根据
    /// 距离和剩余手数提高速度。
    private void updateSpinningMovement(LivingEntity target, boolean enraged) {
        // 旋转阶段只做水平追击，保持从悬浮阶段带入的当前高度。
        // 既不能追玩家脚底导致持续下降，也不能人为追加新的高度目标。
        Vec3 direction = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
        if (direction.lengthSqr() <= 1.0E-7) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        double speed;
        double maximumSpeed;
        if (enraged) {
            speed = 1.0;
            maximumSpeed = 1.0;
        } else if (isExpert()) {
            speed = Mth.clamp(0.01 * direction.length() + 0.16, 0.22, 0.48);
            if (isFtw()) {
                speed *= 1.3;
            }
            int remainingHands = getRemainingHandCount();
            if (remainingHands == 1) {
                speed *= 1.05;
            } else if (remainingHands == 0) {
                speed *= 1.1;
            }
            maximumSpeed = isFtw() ? 0.48 * 1.3 : 0.48;
        } else {
            speed = 0.2;
            maximumSpeed = 0.2;
        }
        speed = Math.min(speed, maximumSpeed);
        setDeltaMovement(direction.normalize().scale(speed));
    }

    private void setSpinning(boolean spinning) {
        boolean previous = entityData.get(DATA_SPINNING);
        if (previous == spinning) {
            return;
        }
        entityData.set(DATA_SPINNING, spinning);
        if (spinning && !restoringSavedState) {
            playRoarOnce();
        }
    }

    private void playRoarOnce() {
        if (lastRoarTick == tickCount) {
            return;
        }
        lastRoarTick = tickCount;
        playSound(ModSoundEvents.ROAR.get());
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
                && (getHealth() / getMaxHealth() < 0.75F || getRemainingHandCount() < 2);
    }

    /// 生成一枚持续追踪本次目标的敌对骷髅弹。
    ///
    /// @return 实体成功创建并加入世界时为 {@code true}
    boolean shootSkull(LivingEntity target) {
        SkeletronSkullProjectile projectile = ModEntities.SKELETRON_SKULL.get().create(level());
        if (projectile == null) {
            return false;
        }
        projectile.configure(this, target, SKULL_DAMAGE);
        return level().addFreshEntity(projectile);
    }

    /// 让双手采用与头部相同的生命倍率，同时保留各自独立的当前生命比例。
    private void synchronizeHandStats() {
        float scaledMaximum = calculateScaledHandMaxHealth();
        if (Math.abs(scaledMaximum - handMaxHealth) <= 1.0E-3F) {
            return;
        }

        float previousMaximum = handMaxHealth > 0.0F ? handMaxHealth : BASE_HAND_MAX_HEALTH;
        float[] healthRatios = new float[handHealth.length];
        for (int index = 0; index < handHealth.length; index++) {
            healthRatios[index] = Mth.clamp(handHealth[index] / previousMaximum, 0.0F, 1.0F);
        }
        handMaxHealth = scaledMaximum;
        for (int index = 0; index < handHealth.length; index++) {
            if ((destroyedHands & 1 << index) != 0) {
                handHealth[index] = 0.0F;
                continue;
            }
            handHealth[index] = scaledMaximum * healthRatios[index];
            SkeletronHand hand = getHand(index);
            if (hand != null && hand.isAlive()) {
                hand.setPartHealth(handHealth[index]);
            }
        }
    }

    private float calculateScaledHandMaxHealth() {
        var maxHealthAttribute = getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute == null || maxHealthAttribute.getBaseValue() <= 0.0D) {
            return BASE_HAND_MAX_HEALTH;
        }
        double multiplier = maxHealthAttribute.getValue() / maxHealthAttribute.getBaseValue();
        double result = BASE_HAND_MAX_HEALTH * multiplier;
        return Double.isFinite(result) && result > 0.0D
                ? (float) result : BASE_HAND_MAX_HEALTH;
    }

    float getHandMaxHealth() {
        return handMaxHealth;
    }

    float getHandContactDamage() {
        var attackAttribute = getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute == null || attackAttribute.getBaseValue() <= 0.0D) {
            return BASE_HAND_CONTACT_DAMAGE;
        }
        double multiplier = attackAttribute.getValue() / attackAttribute.getBaseValue();
        double result = BASE_HAND_CONTACT_DAMAGE * multiplier;
        return Double.isFinite(result) && result >= 0.0D
                ? (float) result : BASE_HAND_CONTACT_DAMAGE;
    }

    private void ensureHands() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if ((destroyedHands & 1) == 0 && (leftHand == null || !leftHand.isAlive())) {
            leftHand = spawnHand(serverLevel, 0);
        }
        if ((destroyedHands & 2) == 0 && (rightHand == null || !rightHand.isAlive())) {
            rightHand = spawnHand(serverLevel, 1);
        }
    }

    private SkeletronHand spawnHand(ServerLevel serverLevel, int index) {
        SkeletronHand hand = BossEntities.SKELETRON_HAND.get().create(level());
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
        return 2 - Integer.bitCount(destroyedHands & ALL_HANDS_DESTROYED);
    }

    public SkeletronHand getHand(int index) {
        return index == 0 ? leftHand : index == 1 ? rightHand : null;
    }

    public boolean isPhase2() {
        return phase2;
    }

    /// 返回头部与两只手共同组成的遭遇血量比例。
    ///
    /// 分母始终包含两只手的最大生命值；已摧毁手的当前生命为零，因此 Boss 条
    /// 不会在部件死亡时突然扩张或缩短。
    protected float getEncounterProgress() {
        float current = getHealth();
        for (float health : handHealth) {
            current += Math.max(0.0F, health);
        }
        float maximum = getMaxHealth() + handMaxHealth * 2.0F;
        return Mth.clamp(current / maximum, 0.0F, 1.0F);
    }

    @Override
    protected float getBossBarProgress() {
        return getEncounterProgress();
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
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            int remainingHands = getRemainingHandCount();
            if (remainingHands != 0) {
                amount *= 1.0F - remainingHands * 0.45F;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(DESTROYED_HANDS_TAG, destroyedHands);
        tag.putBoolean(PHASE_TWO_TAG, phase2);
        tag.putInt(COMBAT_CYCLE_TAG, combatCycle);
        tag.putFloat(HAND_MAX_HEALTH_TAG, handMaxHealth);
        tag.putBoolean(INITIAL_ROAR_PLAYED_TAG, initialRoarPlayed);
        for (int index = 0; index < handHealth.length; index++) {
            tag.putFloat(HAND_HEALTH_TAG + index, handHealth[index]);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        destroyedHands = tag.getInt(DESTROYED_HANDS_TAG) & ALL_HANDS_DESTROYED;
        phase2 = tag.getBoolean(PHASE_TWO_TAG)
                || destroyedHands == ALL_HANDS_DESTROYED;
        combatCycle = Mth.clamp(tag.getInt(COMBAT_CYCLE_TAG), 0, COMBAT_CYCLE_END);
        handMaxHealth = tag.contains(HAND_MAX_HEALTH_TAG)
                ? tag.getFloat(HAND_MAX_HEALTH_TAG) : BASE_HAND_MAX_HEALTH;
        if (!Float.isFinite(handMaxHealth) || handMaxHealth <= 0.0F) {
            handMaxHealth = BASE_HAND_MAX_HEALTH;
        }
        initialRoarPlayed = tag.getBoolean(INITIAL_ROAR_PLAYED_TAG);
        restoringSavedState = true;
        if (phase2) {
            destroyedHands = ALL_HANDS_DESTROYED;
            if (getAttribute(Attributes.ARMOR) != null) {
                getAttribute(Attributes.ARMOR).setBaseValue(0.0);
            }
        }
        for (int index = 0; index < handHealth.length; index++) {
            String key = HAND_HEALTH_TAG + index;
            float savedHealth = (destroyedHands & 1 << index) != 0
                    ? 0.0F
                    : tag.contains(key) ? tag.getFloat(key) : handMaxHealth;
            handHealth[index] = Float.isFinite(savedHealth)
                    ? Mth.clamp(savedHealth, 0.0F, handMaxHealth) : handMaxHealth;
        }
        leftHand = null;
        rightHand = null;
        setSpinning(false);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {return false;}

}
