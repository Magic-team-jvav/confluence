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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;

/// 机械骷髅王本体及四条可破坏机械臂的权威控制器。
///
/// <p>夜间战斗由悬浮追踪和旋转冲锋两个阶段组成。摧毁机械臂只会移除对应武器威胁，
/// 不会凭空改变头部护甲或产生额外阶段。白天会跳过普通周期，直接进入狂暴追击。</p>
///
/// <p>机械臂实体是可重建的临时部件。本体只保存各槽位的摧毁状态和剩余生命，
/// 因而区块重载不会复制仍存活的部件，也不会复活已经摧毁的部件。</p>
public class SkeletronPrime extends BaseBoss {
    private static final int ARM_COUNT = 4;
    private static final int ALL_ARMS_DESTROYED = (1 << ARM_COUNT) - 1;
    private static final int NORMAL_HOVER_TICKS = 200;
    private static final int NORMAL_SPIN_END_TICKS = 250;
    private static final int COMBAT_CYCLE_TICKS = 260;
    private static final float ARM_MAX_HEALTH = 2080.0F;

    private static final String DESTROYED_ARMS_TAG = "DestroyedArms";
    private static final String ARM_HEALTH_TAG = "ArmHealth";
    private static final String COMBAT_CYCLE_TAG = "CombatCycle";

    private static final EntityDataAccessor<Boolean> DATA_SPINNING = SynchedEntityData.defineId(SkeletronPrime.class, EntityDataSerializers.BOOLEAN);

    private final SkeletronPrimeArm[] arms = new SkeletronPrimeArm[ARM_COUNT];
    private final float[] armHealth =
            {-1.0F, -1.0F, -1.0F, -1.0F};
    private int destroyedArms;
    private int combatCycle;
    private int contactCooldown = 20;

    public SkeletronPrime(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 2500;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 10920.0)
                .add(Attributes.ATTACK_DAMAGE, 21.0)
                .add(Attributes.ARMOR, 6.0)
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
        return BossEvent.BossBarColor.RED;
    }

    /// 行为树只维持调度生命周期，实际阶段状态由服务端 tick 的单一计时源控制。
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
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) {
            return;
        }
        if (level().isClientSide) {
            applyAirResistance();
            return;
        }

        ensureArms();
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            setSpinning(false);
            return;
        }

        combatCycle = (combatCycle + 1) % COMBAT_CYCLE_TICKS;
        boolean enraged = isDayEnraged();
        boolean spinning = enraged
                || combatCycle >= NORMAL_HOVER_TICKS
                && combatCycle < NORMAL_SPIN_END_TICKS;
        setSpinning(spinning);
        if (spinning) {
            updateSpinningMovement(target, enraged);
        } else if (combatCycle < NORMAL_HOVER_TICKS) {
            updateHoverMovement(target);
        }
        // 夜间周期的最后十刻只负责等待。1.21 侧不会在这里重新运行悬浮追踪，
        // 因此保留旋转结束时已有的速度，直到下一个周期重新进入悬浮阶段。
        damageContactTargets(enraged);
        faceTarget(target);
        applyAirResistance();
    }

    /// 还原 1.21 通用 Boss 基类在行为树执行后的空气阻力。
    ///
    /// <p>悬浮阶段会把旧速度乘以 1.1 后再叠加追踪力；如果缺少这一步阻力，实体一旦越过玩家，
    /// 旧速度就会稳定卡在最大值，无法重新转向并最终飞出有效高度。旋转阶段同样需要经过该阻力，
    /// 因而必须统一放在本轮运动计算之后，而不能只修正悬浮分支。</p>
    private void applyAirResistance() {
        setDeltaMovement(getDeltaMovement().scale(0.95));
    }

    /// 复用 1.21 侧简单追踪器的速度合成参数。
    ///
    /// <p>当前速度先乘 1.1，再叠加朝向目标的 0.12 吸引力，最终限制在
    /// 0.3 至 2.5 的速度区间。这里不能改成追逐目标上方固定点，否则悬浮轨迹、
    /// 转向半径和机械臂相对位置都会与原实现不同。</p>
    private void updateHoverMovement(LivingEntity target) {
        Vec3 current = getDeltaMovement();
        Vec3 targetDirection = target.position().subtract(position());
        Vec3 result;
        if (current.lengthSqr() <= 1.0E-9) {
            result = targetDirection.lengthSqr() <= 1.0E-9
                    ? Vec3.ZERO
                    : targetDirection.normalize().scale(0.3);
        } else if (targetDirection.lengthSqr() <= 1.0E-9) {
            result = current;
        } else {
            result = current.scale(1.1).add(targetDirection.normalize().scale(0.12));
        }
        double speed = result.length();
        if (speed > 2.5) {
            result = result.scale(2.5 / speed);
        } else if (speed > 1.0E-9 && speed < 0.3) {
            result = result.scale(0.3 / speed);
        }
        setDeltaMovement(result);
    }

    /// 旋转阶段持续朝目标追击；白天狂暴使用更高速度和致命接触伤害。
    private void updateSpinningMovement(LivingEntity target, boolean enraged) {
        Vec3 direction = target.getEyePosition().subtract(position());
        if (direction.lengthSqr() <= 1.0E-7) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        double speed = enraged ? 2.0 : 0.8;
        setDeltaMovement(direction.normalize().scale(speed));
    }

    private void faceTarget(LivingEntity target) {
        lookAt(target, 90.0F, 85.0F);
        getLookControl().setLookAt(target);
        // 当前行为在 super.tick() 之后执行；立即同步头部可还原 1.21 行为树在
        // 原版头部更新阶段之前执行 LookAtTargetAction 的可见结果。
        setYHeadRot(getYRot());
    }

    private void damageContactTargets(boolean enraged) {
        if (--contactCooldown > 0) {
            return;
        }
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE)
                + (enraged ? 999.0F : 0.0F);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox(), living -> living.canBeSeenAsEnemy() && canAttack(living))) {
            target.hurt(damageSources().mobAttack(this), damage);
            contactCooldown = 20;
            return;
        }
        contactCooldown = 5;
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

    /// 机械骷髅王的悬停和旋转追击不叠加原版重力。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public boolean isDayEnraged() {
        return level().isDay();
    }

    private void ensureArms() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int index = 0; index < ARM_COUNT; index++) {
            if ((destroyedArms & 1 << index) != 0 || arms[index] != null && arms[index].isAlive()) {
                continue;
            }
            arms[index] = spawnArm(serverLevel, index);
        }
    }

    private SkeletronPrimeArm spawnArm(ServerLevel serverLevel, int index) {
        SkeletronPrimeArm arm = BossEntities.SKELETRON_PRIME_ARM.get().create(level());
        if (arm == null) {
            return null;
        }
        arm.setPos(position());
        arm.setMaster(this, index);
        if (armHealth[index] > 0.0F) {
            arm.setPartHealth(armHealth[index]);
        } else {
            armHealth[index] = arm.getPartHealth();
        }
        if (!serverLevel.addFreshEntity(arm)) {
            arm.discard();
            return null;
        }
        return arm;
    }

    void onArmHealthChanged(int index, float remainingHealth) {
        if (index >= 0 && index < armHealth.length) {
            armHealth[index] = remainingHealth;
        }
    }

    void onArmDestroyed(int index, SkeletronPrimeArm arm) {
        if (index < 0 || index >= ARM_COUNT) {
            return;
        }
        destroyedArms |= 1 << index;
        armHealth[index] = 0.0F;
        if (arms[index] == arm) {
            arms[index] = null;
        }
    }

    public SkeletronPrimeArm getArm(int index) {
        return index >= 0 && index < ARM_COUNT
                ? arms[index] : null;
    }

    public int getDestroyedArmsMask() {
        return destroyedArms;
    }

    /// 返回头部和四条机械臂共同组成的遭遇生命比例。
    float getEncounterProgress() {
        float current = getHealth();
        for (float health : armHealth) {
            current += Math.max(0.0F, health);
        }
        float maximum = getMaxHealth() + ARM_MAX_HEALTH * ARM_COUNT;
        return Mth.clamp(current / maximum, 0.0F, 1.0F);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getEncounterProgress());
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return !(entity instanceof SkeletronPrime)
                && super.canAttack(entity);
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
        tag.putInt(DESTROYED_ARMS_TAG, destroyedArms);
        tag.putInt(COMBAT_CYCLE_TAG, combatCycle);
        for (int index = 0; index < ARM_COUNT; index++) {
            tag.putFloat(ARM_HEALTH_TAG + index, armHealth[index]);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        destroyedArms = tag.getInt(DESTROYED_ARMS_TAG) & ALL_ARMS_DESTROYED;
        combatCycle = Mth.clamp(tag.getInt(COMBAT_CYCLE_TAG), 0, COMBAT_CYCLE_TICKS - 1);
        for (int index = 0; index < ARM_COUNT; index++) {
            String key = ARM_HEALTH_TAG + index;
            armHealth[index] = (destroyedArms & 1 << index) != 0
                    ? 0.0F
                    : tag.contains(key)
                    ? tag.getFloat(key) : -1.0F;
        }
        java.util.Arrays.fill(arms, null);
        setSpinning(false);
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
    protected boolean shouldDiscardWhenNoTarget() {
        return true;
    }
}
