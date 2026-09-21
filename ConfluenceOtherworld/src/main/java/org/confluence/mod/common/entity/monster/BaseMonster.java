package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.EnemyTargeting;
import org.confluence.mod.common.entity.ai.EnemyWalkNodeEvaluator;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public abstract class BaseMonster extends Monster implements GeoEntity {
    private final java.util.Set<Enum<?>> activeStates = new java.util.LinkedHashSet<>();
    private CreatureDefinition appliedDefinition;
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean behaviorTreeRegistered;
    private BTRoot behaviorTree;
    private NearestAttackableTargetGoal<Player> playerTargetGoal;
    private int contactAttackTicks = 20;
    private boolean performingContactAttack;
    private int contactTeleportTick = Integer.MIN_VALUE;
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

    protected final void setSpecialState(Enum<?> state, boolean enabled) {
        if (level().isClientSide) return;
        if (enabled) activeStates.add(state);
        else activeStates.remove(state);
        applyStateAttributes(state, enabled);
    }

    public final CreatureDefinition.StateOverrides stateParameters(Enum<?> state) {
        var definition = ModEntities.creatureAttributes(getType());
        var defaults = definition == null ? null : definition.states().get(state);
        return creatureDefinition().state(state).withDefaults(defaults == null ? CreatureDefinition.StateOverrides.EMPTY : defaults.parameters().apply(this));
    }

    protected final double stateAttributeValue(Enum<?> state, Attribute attribute, double base) {
        var values = creatureDefinition().state(state).attributes();
        double override = attribute == Attributes.MOVEMENT_SPEED ? values.movementSpeed()
                : attribute == Attributes.ATTACK_DAMAGE ? values.attackDamage()
                : attribute == Attributes.ARMOR ? values.armor()
                : attribute == Attributes.MAX_HEALTH ? values.maxHealth()
                : attribute == Attributes.KNOCKBACK_RESISTANCE ? values.knockbackResistance()
                : attribute == Attributes.FOLLOW_RANGE ? values.followRange()
                : attribute == Attributes.SCALE.value() ? values.scale() : -1;
        if (override >= 0) return override;
        var definition = ModEntities.creatureAttributes(getType());
        var declared = definition == null ? null : definition.states().get(state);
        var value = declared == null ? null : declared.attributes().get(attribute);
        if (value == null) return base;
        double amount = value.amount().applyAsDouble(this);
        if (value.absolute()) return amount;
        return value.operation() == AttributeModifier.Operation.ADDITION ? base + amount : base * (1 + amount);
    }

    private void applyStateAttributes(Enum<?> state, boolean enabled) {
        double oldScale = getAttributeValue(Attributes.SCALE.value());
        var values = creatureDefinition().state(state).attributes();
        applyStateModifier(Attributes.ATTACK_DAMAGE, stateModifier(state, Attributes.ATTACK_DAMAGE), values.attackDamage(), enabled);
        applyStateModifier(Attributes.ARMOR, stateModifier(state, Attributes.ARMOR), values.armor(), enabled);
        applyStateModifier(Attributes.MOVEMENT_SPEED, stateModifier(state, Attributes.MOVEMENT_SPEED), values.movementSpeed(), enabled);
        applyStateModifier(Attributes.KNOCKBACK_RESISTANCE, stateModifier(state, Attributes.KNOCKBACK_RESISTANCE), values.knockbackResistance(), enabled);
        applyStateModifier(Attributes.FOLLOW_RANGE, stateModifier(state, Attributes.FOLLOW_RANGE), values.followRange(), enabled);
        applyStateModifier(Attributes.MAX_HEALTH, stateModifier(state, Attributes.MAX_HEALTH), values.maxHealth(), enabled);
        applyStateModifier(Attributes.SCALE.value(), stateModifier(state, Attributes.SCALE.value()), values.scale(), enabled);
        var definition = ModEntities.creatureAttributes(getType());
        var declared = definition == null ? null : definition.states().get(state);
        if (declared != null) {
            for (Attribute attribute : declared.attributes().keySet()) {
                if (attribute == Attributes.ATTACK_DAMAGE || attribute == Attributes.ARMOR || attribute == Attributes.MOVEMENT_SPEED
                        || attribute == Attributes.KNOCKBACK_RESISTANCE || attribute == Attributes.FOLLOW_RANGE
                        || attribute == Attributes.MAX_HEALTH || attribute == Attributes.SCALE.value())
                    continue;
                applyStateModifier(attribute, stateModifier(state, attribute), -1, enabled);
            }
        }
        if (getHealth() > getMaxHealth()) setHealth(getMaxHealth());
        if (oldScale != getAttributeValue(Attributes.SCALE.value())) refreshDimensions();
    }

    private AttributeModifier stateModifier(Enum<?> state, Attribute attribute) {
        String name = "confluence.state." + state.getDeclaringClass().getName() + "." + state.name() + "." + attribute.getDescriptionId();
        var definition = ModEntities.creatureAttributes(getType());
        var defaults = definition == null ? null : definition.states().get(state);
        var value = defaults == null ? null : defaults.attributes().get(attribute);
        if (value != null) {
            double amount = value.amount().applyAsDouble(this);
            if (value.absolute() && getAttribute(attribute) != null)
                amount -= getAttribute(attribute).getBaseValue();
            return new AttributeModifier(java.util.UUID.nameUUIDFromBytes(name.getBytes(java.nio.charset.StandardCharsets.UTF_8)), name, amount, value.operation());
        }
        return new AttributeModifier(java.util.UUID.nameUUIDFromBytes(name.getBytes(java.nio.charset.StandardCharsets.UTF_8)), name, 0, AttributeModifier.Operation.ADDITION);
    }

    private void applyStateModifier(Attribute attribute, AttributeModifier original, double override, boolean enabled) {
        AttributeInstance instance = getAttribute(attribute);
        if (instance == null) return;
        if (enabled) {
            AttributeModifier modifier = override >= 0
                    ? new AttributeModifier(original.getId(), original.getName(), override - instance.getBaseValue(), AttributeModifier.Operation.ADDITION)
                    : original;
            var existing = instance.getModifier(original.getId());
            if (existing != null && existing.getAmount() == modifier.getAmount() && existing.getOperation() == modifier.getOperation())
                return;
            instance.removeModifier(original.getId());
            if (modifier.getAmount() != 0) instance.addTransientModifier(modifier);
        } else {
            instance.removeModifier(original.getId());
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level) {
            @Override
            protected PathFinder createPathFinder(int maxVisitedNodes) {
                nodeEvaluator = new EnemyWalkNodeEvaluator();
                return new PathFinder(nodeEvaluator, maxVisitedNodes);
            }
        };
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!isNoGravity()) super.playStepSound(pos, state);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.playerTargetGoal = new NearestAttackableTargetGoal<>(this, Player.class, mustSeePlayerTarget(), this::canTargetPlayer);
        this.targetSelector.addGoal(1, playerTargetGoal);
    }

    /// 构造参数决定索敌视线规则的实体在自身字段完成初始化后调用此方法，避免超类构造期间读取未初始化状态。
    protected final void configurePlayerTargetLineOfSight(boolean mustSee) {
        if (playerTargetGoal != null) targetSelector.removeGoal(playerTargetGoal);
        playerTargetGoal = new NearestAttackableTargetGoal<>(this, Player.class, mustSee, this::canTargetPlayer);
        targetSelector.addGoal(1, playerTargetGoal);
    }

    protected boolean canTargetPlayer(LivingEntity target) {
        return true;
    }

    /// 共用目标仲裁保留物种的视线、环境限制；被玩家攻击时允许反击该玩家。
    public final boolean canSelectPlayerTarget(Player player) {
        return (canTargetPlayer(player) || getLastHurtByMob() == player) && (!mustSeePlayerTarget() || getSensing().hasLineOfSight(player));
    }

    /// 在自己的实体基类仲裁目标，不修改原版 Mob；Boss 保留专用遭遇规则。
    @Override
    public void setTarget(LivingEntity target) {
        super.setTarget(EnemyTargeting.applies(this) ? EnemyTargeting.select(this, target) : target);
    }

    protected boolean mustSeePlayerTarget() {
        return false;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide && !behaviorTreeRegistered) {
            applyCreatureDefinition();
            behaviorTree = createSafeBehaviorTree();
            this.goalSelector.addGoal(0, behaviorTree);
            behaviorTreeRegistered = true;
        }
    }

    private BTRoot createSafeBehaviorTree() {
        try {
            return Objects.requireNonNull(createBT(), () -> "Missing behavior tree for " + getType());
        } catch (RuntimeException exception) {
            Confluence.LOGGER.error("Failed to create behavior tree for {}; the entity will remain passive instead of crashing the level", getType(), exception);
            return disabledBehaviorTree();
        }
    }

    private static BTRoot disabledBehaviorTree() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    protected abstract BTRoot createBT();

    /// 推进由实体持有的接触攻击计时器。
    ///
    /// 接触伤害不属于某个追击动作：只要实体当前处于战斗状态，等待、施法或切换
    /// 行为都不会清空冷却。这里将相同语义放在实体基类中，同时默认关闭，避免让原本通过普通
    /// 近战目标执行伤害的陆地生物额外获得一次碰撞攻击。
    @Override
    public void tick() {
        if (!level().isClientSide && behaviorTreeRegistered && tickCount % 20 == 0 && creatureDefinition() != appliedDefinition)
            applyCreatureDefinition();
        if (!level().isClientSide) clearInvalidCombatTarget();
        if (getTarget() != null && EnemyTargeting.applies(this)) {
            LivingEntity selected = EnemyTargeting.select(this, getTarget());
            if (selected != getTarget()) setTarget(selected);
        }
        super.tick();
        if (!level().isClientSide) clearInvalidCombatTarget();
        if (!usesPostMovementContactAttack()) {
            tickEntityContactAttack();
        }
    }

    /// 在 AI 执行前和接触伤害前清理失效目标，蓄力中的动作也不能继续追击创造玩家。
    private void clearInvalidCombatTarget() {
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || target.isRemoved() || target.level() != level() || !canAttack(target)
                || target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
            setTarget(null);
        }
    }

    @Override
    public void teleportTo(double x, double y, double z) {
        super.teleportTo(x, y, z);
        contactTeleportTick = tickCount;
    }

    /// 推进一次连续接触攻击。直接改写位置的状态机可把调用延后到本 tick 位移完成之后。
    protected final void tickEntityContactAttack() {
        if (level().isClientSide || !isAlive()) {
            return;
        }
        // 冷却按世界时间持续流逝，不能在非攻击阶段暂停；否则下一次冲刺会继承旧冷却，
        // 高速越过目标后才恢复攻击能力。
        if (contactAttackTicks > 0) contactAttackTicks--;
        if (!hasEntityContactAttack() || getTarget() == null || contactAttackTicks > 0) return;

        var entities = SweptContactAttack.findTargets(
                this,
                contactTeleportTick == tickCount ? position() : new Vec3(xo, yo, zo),
                contactAttackInflation(),
                maximumContactSweepDistance(),
                this::canContactAttack
        );
        if (entities.isEmpty()) {
            return;
        }
        boolean attacked = false;
        for (Entity entity : entities) attacked |= doContactHurtTarget(entity);
        // 目标正处于受伤无敌帧等情况下不算命中，保持检测频率以免一次冲刺完全漏伤。
        contactAttackTicks = attacked ? contactAttackInterval() : 0;
    }

    /// 标记当前伤害调用的来源，不改变伤害类型、武器附魔或子类的攻击钩子。
    protected final boolean doContactHurtTarget(Entity target) {
        boolean previous = performingContactAttack;
        performingContactAttack = true;
        try {
            return doHurtTarget(target);
        } finally {
            performingContactAttack = previous;
        }
    }

    public final boolean isPerformingContactAttack() {
        return performingContactAttack;
    }

    protected boolean usesPostMovementContactAttack() {
        return false;
    }

    /// 由需要持续接触攻击的生物覆盖。
    protected boolean hasEntityContactAttack() {
        return false;
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
        if (!(entity instanceof LivingEntity living) || entity.getType() == getType() || !canAttack(living)) {
            return false;
        }
        return living == getTarget() || living instanceof Player;
    }

    /// 以实际战斗方向为唯一权威，同时更新实体、身体和头部朝向。
    /// 飞行怪、冲刺怪与 Boss 共用这一入口，避免各自只写一半旋转状态。
    public final void faceCombatDirection(Vec3 direction, float maximumYawChange, float maximumPitchChange) {
        if (direction.lengthSqr() < 1.0E-7D) {
            return;
        }
        double horizontal = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float targetYaw = horizontal < 1.0E-7D ? getYRot() : (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float targetPitch = (float) (-Mth.atan2(direction.y, horizontal) * Mth.RAD_TO_DEG);
        // 第一个参数是目标角，第二个参数是当前参考角；传反会使实体从目标角反推，
        // 表现为拒绝转向、突然跳向或越过目标后反复摆动。
        float yaw = Mth.rotateIfNecessary(targetYaw, getYRot(), maximumYawChange);
        float pitch = Mth.rotateIfNecessary(targetPitch, getXRot(), maximumPitchChange);
        setYRot(yaw);
        setXRot(pitch);
        yBodyRot = yaw;
        yHeadRot = yaw;
    }

    public final void faceCombatPosition(Vec3 targetPosition, float maximumYawChange, float maximumPitchChange) {
        faceCombatDirection(targetPosition.subtract(getEyePosition()), maximumYawChange, maximumPitchChange);
    }

    public final void faceCombatMovement(float maximumYawChange, float maximumPitchChange) {
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
        return CreatureDefinition.get(getType());
    }

    /// 恢复 Java 注册默认值后重新应用当前数据包快照。
    ///
    /// 先恢复默认值保证删除覆盖文件后也能还原，而不是把上一次覆盖继续当成新的基础值。
    /// 生命百分比不强制保持：满血实体继续满血，受伤实体只在新上限更低时截断。
    private void applyCreatureDefinition() {
        appliedDefinition = creatureDefinition();
        float oldHealth = getHealth();
        float oldMaxHealth = getMaxHealth();
        float oldScale = getScale();
        boolean wasFullHealth = Math.abs(oldHealth - oldMaxHealth) < 0.001F;
        if (Double.isNaN(defaultMaxHealth)) {
            defaultMaxHealth = baseValue(Attributes.MAX_HEALTH);
            defaultAttackDamage = baseValue(Attributes.ATTACK_DAMAGE);
            defaultArmor = baseValue(Attributes.ARMOR);
            defaultMovementSpeed = baseValue(Attributes.MOVEMENT_SPEED);
            defaultFollowRange = baseValue(Attributes.FOLLOW_RANGE);
            defaultKnockbackResistance = baseValue(Attributes.KNOCKBACK_RESISTANCE);
            defaultScale = baseValue(Attributes.SCALE.value());
        } else {
            setBaseValue(Attributes.MAX_HEALTH, defaultMaxHealth);
            setBaseValue(Attributes.ATTACK_DAMAGE, defaultAttackDamage);
            setBaseValue(Attributes.ARMOR, defaultArmor);
            setBaseValue(Attributes.MOVEMENT_SPEED, defaultMovementSpeed);
            setBaseValue(Attributes.FOLLOW_RANGE, defaultFollowRange);
            setBaseValue(Attributes.KNOCKBACK_RESISTANCE, defaultKnockbackResistance);
            setBaseValue(Attributes.SCALE.value(), defaultScale);
        }
        CreatureDefinition.applyAttributes(this);
        setHealth(wasFullHealth ? getMaxHealth() : Math.min(oldHealth, getMaxHealth()));
        if (Math.abs(oldScale - getScale()) > 0.0001F) {
            refreshDimensions();
        }
        if (behaviorTreeRegistered) {
            goalSelector.removeGoal(behaviorTree);
            behaviorTree = createSafeBehaviorTree();
            goalSelector.addGoal(0, behaviorTree);
        }
        activeStates.forEach(state -> applyStateAttributes(state, true));
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}
