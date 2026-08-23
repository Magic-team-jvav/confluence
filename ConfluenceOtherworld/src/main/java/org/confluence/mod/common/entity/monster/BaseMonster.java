package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.data.entity.CreatureDefinitionLoader;
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
            CreatureDefinitionLoader.applyAttributes(this);
            BTRoot behaviorTree = Objects.requireNonNull(createBT(), () -> "Missing behavior tree for " + getType());
            this.goalSelector.addGoal(0, behaviorTree);
            behaviorTreeRegistered = true;
        }
    }

    protected abstract BTRoot createBT();

    /// 推进由实体持有的接触攻击计时器。
    ///
    /// <p>1.21 的接触伤害并不属于某个追击动作：只要实体当前处于战斗状态，等待、施法或切换
    /// 行为都不会清空冷却。这里将相同语义放在实体基类中，同时默认关闭，避免让原本通过普通
    /// 近战目标执行伤害的陆地生物额外获得一次碰撞攻击。</p>
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive() || !hasEntityContactAttack() || getTarget() == null || --contactAttackTicks > 0) {
            return;
        }

        var entities = level().getEntities(this, getBoundingBox().inflate(contactAttackInflation()), this::canContactAttack);
        if (entities.isEmpty()) {
            contactAttackTicks = contactDetectionInterval();
            return;
        }
        for (Entity entity : entities) {
            doHurtTarget(entity);
            contactAttackTicks = contactAttackInterval();
        }
    }

    /// 仅由 1.21 中启用了实体接触攻击的生物覆盖。
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

    /// 保留 1.21 的筛选规则：只攻击当前实体可以合法攻击且类型不同的目标。
    protected boolean canContactAttack(Entity entity) {
        return entity instanceof LivingEntity living
                && entity.getType() != getType()
                && canAttack(living);
    }

    protected final CreatureDefinition creatureDefinition() {
        return CreatureDefinitionLoader.get(getType());
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
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}
