package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.Condition;
import org.confluence.mod.common.entity.ai.bt.leaf.MeleeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.UUID;

/// 会伪装成迷失女孩、受到玩家攻击后显露真身的宁芙。
///
/// <p>未显形时保持坐姿，不会移动或主动选择附近玩家；玩家造成实际伤害后，宁芙会锁定
/// 该攻击者，先播放显形动作，再高速追击。失去目标一段随机时间后会重新伪装。处于虚弱
/// 状态时可用金苹果净化，净化完成后不再敌对并允许拴系。显形、净化和驯服状态全部同步
/// 并保存，重载不会重新开始计时。</p>
public final class Nymph extends BaseMonster {
    private static final double DISGUISED_FOLLOW_RANGE = 5.0;
    private static final double REVEALED_FOLLOW_RANGE = 32.0;
    private static final String TRIGGERED_TAG = "Triggered";
    private static final String TAMED_TAG = "Tamed";
    private static final String CONVERSION_TIME_TAG = "ConversionTime";
    private static final String CONVERSION_STARTER_TAG = "ConversionStarter";
    private static final String RECOVERY_TIME_TAG = "RecoveryTime";
    private static final String RECOVERY_LIMIT_TAG = "RecoveryLimit";
    private static final String REVEAL_TIME_TAG = "RevealTime";
    private static final UUID PURSUIT_SPEED_UUID = UUID.fromString("4c62d2c5-5ea3-48a2-a73c-1bf9957e6d02");
    private static final AttributeModifier PURSUIT_SPEED = new AttributeModifier(PURSUIT_SPEED_UUID, "Nymph revealed pursuit speed", 0.25, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> TRIGGERED = SynchedEntityData.defineId(Nymph.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(Nymph.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(Nymph.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    private static final RawAnimation DASH = RawAnimation.begin().thenLoop("dash");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private int revealTime;
    private int recoveryTime;
    private int recoveryLimit;
    private int conversionTime;
    private UUID conversionStarter;

    public Nymph(EntityType<? extends Nymph> type, Level level) {
        super(type, level);
        recoveryLimit = 75 + random.nextInt(50);
        xpReward = 20;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 156.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 15.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TRIGGERED, false);
        entityData.define(TAMED, false);
        entityData.define(CONVERTING, false);
    }

    @Override
    protected boolean canTargetPlayer(net.minecraft.world.entity.LivingEntity target) {
        // 关闭基础怪物的接近索敌；玩家攻击后的目标由 hurt 精确指定。
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide && !isTamed() && source.getEntity() instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            setTarget(player);
            setTriggered(true);
        }
        return damaged;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        tickConversion();
        if (isTamed()) {
            setTarget(null);
            setTriggered(false);
            updatePursuitSpeed(false);
            return;
        }
        if (getTarget() != null && !(getTarget() instanceof Player)) {
            setTarget(null);
        }
        // 伪装时保留较小的观察属性，显形后扩大范围以维持已经开始的追击。
        var followRange = getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null) {
            followRange.setBaseValue(isTriggered()
                    ? REVEALED_FOLLOW_RANGE
                    : DISGUISED_FOLLOW_RANGE);
        }
        boolean hasLiveTarget = getTarget() != null
                && getTarget().isAlive();
        if (isTriggered() && hasLiveTarget) {
            revealTime++;
            recoveryTime = 0;
        } else if (isTriggered()) {
            recoveryTime++;
            if (recoveryTime >= recoveryLimit) {
                setTriggered(false);
            }
        } else {
            revealTime = 0;
            getNavigation().stop();
        }
        updatePursuitSpeed(isTriggered() && hasLiveTarget && revealTime > 20);
    }

    private void tickConversion() {
        if (!isConverting()) {
            return;
        }
        conversionTime--;
        if (conversionTime > 0) {
            return;
        }
        entityData.set(CONVERTING, false);
        setTamed(true);
        setTriggered(false);
        addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!isSilent()) {
            level().levelEvent(null, 1027, blockPosition(), 0);
        }
    }

    private void updatePursuitSpeed(boolean pursuing) {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        if (pursuing && speed.getModifier(PURSUIT_SPEED_UUID) == null) {
            speed.addTransientModifier(PURSUIT_SPEED);
        } else if (!pursuing) {
            speed.removeModifier(PURSUIT_SPEED_UUID);
        }
        setSprinting(pursuing);
    }

    public boolean isTriggered() {
        return entityData.get(TRIGGERED);
    }

    public void setTriggered(boolean triggered) {
        if (entityData.get(TRIGGERED) == triggered) {
            return;
        }
        entityData.set(TRIGGERED, triggered);
        if (!triggered) {
            revealTime = 0;
            recoveryTime = 0;
            recoveryLimit = 75 + random.nextInt(50);
        }
        refreshDimensions();
    }

    public boolean isTamed() {
        return entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        entityData.set(TAMED, tamed);
        if (tamed) {
            setTarget(null);
        }
        refreshDimensions();
    }

    public boolean isConverting() {
        return entityData.get(CONVERTING);
    }

    void startConverting(UUID starter, int ticks) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Nymph conversion time must be positive");
        }
        conversionStarter = starter;
        conversionTime = ticks;
        entityData.set(CONVERTING, true);
        removeEffect(MobEffects.WEAKNESS);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, Math.max(0, level().getDifficulty().getId() - 1)));
        level().broadcastEntityEvent(this, (byte) 16);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.GOLDEN_APPLE)) {
            return super.mobInteract(player, hand);
        }
        if (!hasEffect(MobEffects.WEAKNESS) || isTamed() || isConverting()) {
            return InteractionResult.CONSUME;
        }
        if (!level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            startConverting(player.getUUID(), 2000 + random.nextInt(500));
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return !isTriggered() && !isTamed()
                ? dimensions.scale(1.0F, 0.75F)
                : dimensions;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return isTamed() && super.canBeLeashed(player);
    }

    @Override
    public boolean isPreventingPlayerRest(Player player) {
        return !isTamed();
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !isTamed() && super.canBeSeenAsEnemy();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(
                                new AttackReadyCondition(Nymph.this),
                                new MoveToTargetAction(
                                        Nymph.this, 0.6, 2.0),
                                new MeleeAttackAction(
                                        Nymph.this, 2.0)),
                        SequenceNode.of(
                                new TamedCondition(Nymph.this),
                                new RandomStrollAction(
                                        Nymph.this, 1.0, 8)),
                        new WaitAction(10));
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(TRIGGERED_TAG, isTriggered());
        tag.putBoolean(TAMED_TAG, isTamed());
        tag.putInt(CONVERSION_TIME_TAG, conversionTime);
        tag.putInt(RECOVERY_TIME_TAG, recoveryTime);
        tag.putInt(RECOVERY_LIMIT_TAG, recoveryLimit);
        tag.putInt(REVEAL_TIME_TAG, revealTime);
        if (conversionStarter != null) {
            tag.putUUID(CONVERSION_STARTER_TAG, conversionStarter);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setTamed(tag.getBoolean(TAMED_TAG));
        setTriggered(tag.getBoolean(TRIGGERED_TAG) && !isTamed());
        conversionTime = Math.max(0, tag.getInt(CONVERSION_TIME_TAG));
        recoveryTime = Math.max(0, tag.getInt(RECOVERY_TIME_TAG));
        recoveryLimit = Math.max(1, tag.getInt(RECOVERY_LIMIT_TAG));
        revealTime = Math.max(0, tag.getInt(REVEAL_TIME_TAG));
        conversionStarter = tag.hasUUID(CONVERSION_STARTER_TAG)
                ? tag.getUUID(CONVERSION_STARTER_TAG) : null;
        entityData.set(CONVERTING, conversionTime > 0 && !isTamed());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "state",
                5,
                state -> {
                    if (!isTamed()) {
                        return state.setAndContinue(
                                isTriggered() ? DASH : SIT);
                    }
                    return state.setAndContinue(
                            state.isMoving() ? WALK : IDLE);
                }));
    }

    private static final class AttackReadyCondition
            extends Condition<Nymph> {
        private AttackReadyCondition(Nymph nymph) {
            super(nymph);
        }

        @Override
        protected boolean test() {
            return !mob.isTamed()
                    && mob.isTriggered()
                    && mob.revealTime > 20
                    && mob.getTarget() != null
                    && mob.getTarget().isAlive();
        }
    }

    private static final class TamedCondition extends Condition<Nymph> {
        private TamedCondition(Nymph nymph) {
            super(nymph);
        }

        @Override
        protected boolean test() {
            return mob.isTamed();
        }
    }
}
