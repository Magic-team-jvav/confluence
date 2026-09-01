package org.confluence.mod.common.entity.monster;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.data.entity.CreatureDefinitionLoader;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public abstract class BaseMonster extends Monster implements GeoEntity {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean behaviorTreeRegistered;
    private int contactAttackTicks = 20;
    private int creatureDefinitionRevision = Integer.MIN_VALUE;
    private double defaultMaxHealth = Double.NaN;
    private double defaultAttackDamage;
    private double defaultArmor;
    private double defaultMovementSpeed;
    private double defaultFollowRange;
    private double defaultKnockbackResistance;
    private double defaultScale;

    public BaseMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, mustSeePlayerTarget(), this::canTargetPlayer));
    }

    protected boolean canTargetPlayer(LivingEntity target) {
        return true;
    }

    protected boolean mustSeePlayerTarget() {
        return false;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide && !behaviorTreeRegistered) {
            applyCreatureDefinition();
            BTRoot behaviorTree = Objects.requireNonNull(createBT(), () -> "Missing behavior tree for " + getType());
            this.goalSelector.addGoal(0, behaviorTree);
            behaviorTreeRegistered = true;
        }
    }

    protected abstract BTRoot createBT();

    /// 推进由实体持有的接触攻击计时器。
    ///
    /// 接触伤害不属于某个追击动作：只要实体当前处于战斗状态，等待、施法或切换
    /// 行为都不会清空冷却。这里将相同语义放在实体基类中，同时默认关闭，避免让原本通过普通
    /// 近战目标执行伤害的陆地生物额外获得一次碰撞攻击。
    @Override
    public void tick() {
        if (!level().isClientSide && behaviorTreeRegistered
                && creatureDefinitionRevision != CreatureDefinitionLoader.getRevision()) {
            applyCreatureDefinition();
        }
        super.tick();
        if (!usesPostMovementContactAttack()) {
            tickEntityContactAttack();
        }
    }

    /// 推进一次连续接触攻击。直接改写位置的状态机可把调用延后到本 tick 位移完成之后。
    protected final void tickEntityContactAttack() {
        if (level().isClientSide || !isAlive() || !hasEntityContactAttack() || getTarget() == null || --contactAttackTicks > 0) {
            return;
        }

        var entities = SweptContactAttack.findTargets(
                this,
                contactAttackInflation(),
                maximumContactSweepDistance(),
                this::canContactAttack
        );
        if (entities.isEmpty()) {
            contactAttackTicks = contactDetectionInterval();
            return;
        }
        for (Entity entity : entities) {
            doHurtTarget(entity);
            contactAttackTicks = contactAttackInterval();
        }
    }

    protected boolean usesPostMovementContactAttack() {
        return false;
    }

    /// 由需要持续接触攻击的生物覆盖。
    protected boolean hasEntityContactAttack() {
        return false;
    }

    protected int contactDetectionInterval() {
        return 10;
    }

    protected int contactAttackInterval() {
        return 20;
    }

    protected double contactAttackInflation() {
        return 0.0;
    }

    /// 超过该距离的单 tick 位移按传送处理，只检测落点，防止传送型生物沿路径误伤。
    protected double maximumContactSweepDistance() {
        return SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE;
    }

    /// 只攻击当前实体可以合法攻击且类型不同的目标。
    protected boolean canContactAttack(Entity entity) {
        return entity instanceof LivingEntity living
                && entity.getType() != getType()
                && canAttack(living);
    }

    /// 以实际战斗方向为唯一权威，同时更新实体、身体、头部和 LookControl。
    /// 飞行怪、冲刺怪与 Boss 共用这一入口，避免各自只写一半旋转状态。
    protected final void faceCombatDirection(Vec3 direction, float maximumYawChange, float maximumPitchChange) {
        if (!Double.isFinite(direction.x) || !Double.isFinite(direction.y) || !Double.isFinite(direction.z)
                || direction.lengthSqr() < 1.0E-7D) {
            return;
        }
        double horizontal = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float targetYaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float targetPitch = (float) (-Mth.atan2(direction.y, horizontal) * Mth.RAD_TO_DEG);
        // 第一个参数是目标角，第二个参数是当前参考角；传反会使实体从目标角反推，
        // 表现为拒绝转向、突然跳向或越过目标后反复摆动。
        float yaw = Mth.rotateIfNecessary(targetYaw, getYRot(), maximumYawChange);
        float pitch = Mth.rotateIfNecessary(targetPitch, getXRot(), maximumPitchChange);
        setYRot(yaw);
        setXRot(pitch);
        yBodyRot = yaw;
        yHeadRot = yaw;
        getLookControl().setLookAt(position().add(direction));
    }

    protected final void faceCombatPosition(Vec3 targetPosition, float maximumYawChange, float maximumPitchChange) {
        faceCombatDirection(targetPosition.subtract(getEyePosition()), maximumYawChange, maximumPitchChange);
    }

    protected final void faceCombatMovement(float maximumYawChange, float maximumPitchChange) {
        faceCombatDirection(getDeltaMovement(), maximumYawChange, maximumPitchChange);
    }

    /// 在保留当前运动惯性的前提下，将方向按每刻最大转角逐步转向目标。
    /// 冲刺类生物用它获得可预判但不僵硬的弧线，避免完全锁向或瞬间吸附目标。
    protected final Vec3 turnDirectionToward(Vec3 currentDirection, Vec3 desiredDirection, float maximumTurnDegrees) {
        if (desiredDirection.lengthSqr() < 1.0E-7D) {
            return currentDirection;
        }
        Vec3 desired = desiredDirection.normalize();
        if (currentDirection.lengthSqr() < 1.0E-7D) {
            return desired;
        }
        Vec3 current = currentDirection.normalize();
        double angle = Math.acos(Mth.clamp(current.dot(desired), -1.0D, 1.0D));
        double maximumTurn = maximumTurnDegrees * Mth.DEG_TO_RAD;
        if (angle <= maximumTurn || angle < 1.0E-7D) {
            return desired;
        }
        double sine = Math.sin(angle);
        if (Math.abs(sine) < 1.0E-6D) {
            Vec3 axis = Math.abs(current.y) < 0.9D
                    ? current.cross(new Vec3(0.0D, 1.0D, 0.0D)).normalize()
                    : current.cross(new Vec3(1.0D, 0.0D, 0.0D)).normalize();
            return current.scale(Math.cos(maximumTurn)).add(axis.scale(Math.sin(maximumTurn))).normalize();
        }
        double progress = maximumTurn / angle;
        double currentWeight = Math.sin((1.0D - progress) * angle) / sine;
        double desiredWeight = Math.sin(progress * angle) / sine;
        return current.scale(currentWeight).add(desired.scale(desiredWeight)).normalize();
    }

    protected final CreatureDefinition creatureDefinition() {
        return CreatureDefinitionLoader.get(getType());
    }

    /// 恢复 Java 注册默认值后重新应用当前数据包快照。
    ///
    /// 先恢复默认值保证删除覆盖文件后也能还原，而不是把上一次覆盖继续当成新的基础值。
    /// 生命百分比不强制保持：满血实体继续满血，受伤实体只在新上限更低时截断。
    private void applyCreatureDefinition() {
        if (Double.isNaN(defaultMaxHealth)) {
            defaultMaxHealth = baseValue(Attributes.MAX_HEALTH);
            defaultAttackDamage = baseValue(Attributes.ATTACK_DAMAGE);
            defaultArmor = baseValue(Attributes.ARMOR);
            defaultMovementSpeed = baseValue(Attributes.MOVEMENT_SPEED);
            defaultFollowRange = baseValue(Attributes.FOLLOW_RANGE);
            defaultKnockbackResistance = baseValue(Attributes.KNOCKBACK_RESISTANCE);
            defaultScale = baseValue(PortAttributesExtension.scale().value());
        } else {
            setBaseValue(Attributes.MAX_HEALTH, defaultMaxHealth);
            setBaseValue(Attributes.ATTACK_DAMAGE, defaultAttackDamage);
            setBaseValue(Attributes.ARMOR, defaultArmor);
            setBaseValue(Attributes.MOVEMENT_SPEED, defaultMovementSpeed);
            setBaseValue(Attributes.FOLLOW_RANGE, defaultFollowRange);
            setBaseValue(Attributes.KNOCKBACK_RESISTANCE, defaultKnockbackResistance);
            setBaseValue(PortAttributesExtension.scale().value(), defaultScale);
        }

        float oldHealth = getHealth();
        float oldMaxHealth = getMaxHealth();
        float oldScale = getScale();
        boolean wasFullHealth = Math.abs(oldHealth - oldMaxHealth) < 0.001F;
        CreatureDefinitionLoader.applyAttributes(this);
        setHealth(wasFullHealth ? getMaxHealth() : Math.min(oldHealth, getMaxHealth()));
        if (Math.abs(oldScale - getScale()) > 0.0001F) {
            refreshDimensions();
        }
        creatureDefinitionRevision = CreatureDefinitionLoader.getRevision();
        onCreatureDefinitionReload();
    }

    /// 数据包覆盖刷新后的扩展点。多部件实体可在这里同步碰撞箱或重建几何缓存。
    protected void onCreatureDefinitionReload() {}

    private double baseValue(net.minecraft.world.entity.ai.attributes.Attribute attribute) {
        AttributeInstance instance = getAttribute(attribute);
        return instance == null ? 0.0D : instance.getBaseValue();
    }

    private void setBaseValue(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        AttributeInstance instance = getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    public static AttributeSupplier.Builder createMonsterAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(PortAttributesExtension.scale().get(), 1.0D);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}
