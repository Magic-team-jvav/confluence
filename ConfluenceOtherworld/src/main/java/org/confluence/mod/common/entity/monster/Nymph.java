package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.UUID;

/// 伪装成迷失女孩、接近目标后显露真身并可用金苹果净化的宁芙。
public final class Nymph extends BaseMonster {
    private static final double DISGUISED_FOLLOW_RANGE = 5.0;
    private static final double REVEALED_FOLLOW_RANGE = 32.0;
    private static final String TAMED_TAG = "isTamed";
    private static final String CONVERTING_TAG = "Converting";
    private static final String CONVERSION_TIME_TAG = "ConversionTime";
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

    public Nymph(EntityType<? extends Nymph> type, Level level) {
        super(type, level);
        recoveryLimit = 75 + random.nextInt(50);
        xpReward = 20;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return isTriggered() && !isTamed();
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
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == TRIGGERED || key == TAMED) refreshDimensions();
    }

    @Override
    protected boolean canTargetPlayer(LivingEntity target) {
        return tickCount > 50 && !isTamed();
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, this::canTargetPlayer));
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
        if (getTarget() != null && getTarget().isAlive() && distanceToSqr(getTarget()) < 25.0)
            setTriggered(true);
        /// 伪装时保留较小的观察属性，显形后扩大范围以维持已经开始的追击。
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
        updatePursuitSpeed(isTriggered() && hasLiveTarget);
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

    void startConverting(int ticks) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Nymph conversion time must be positive");
        }
        conversionTime = ticks;
        entityData.set(CONVERTING, true);
        removeEffect(MobEffects.WEAKNESS);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, Math.max(level().getDifficulty().getId() - 1, 0)));
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
            startConverting(2000 + random.nextInt(500));
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
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return !isTriggered() && !isTamed()
                ? 1.05F
                : super.getStandingEyeHeight(pose, dimensions);
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
    protected Vec3 getLeashOffset() {
        return new Vec3(-0.3, getEyeHeight() * 0.5, getBbWidth() * 0.1);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new MeleeAttackGoal(Nymph.this, 0.6, true) {
                            @Override
                            public boolean canUse() {
                                return !isTamed() && isTriggered() && super.canUse();
                            }
                        }),
                        new VanillaGoalAction(new RandomStrollGoal(Nymph.this, 1.0, 10, true) {
                            @Override
                            public boolean canUse() {
                                return isTamed() && super.canUse();
                            }
                        }),
                        new VanillaGoalAction(new LookAtPlayerGoal(Nymph.this, Player.class, 10.0F, 1.0F)));
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(TAMED_TAG, isTamed());
        tag.putBoolean(CONVERTING_TAG, isConverting());
        if (isConverting()) tag.putInt(CONVERSION_TIME_TAG, conversionTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setTamed(tag.getBoolean(TAMED_TAG));
        setTriggered(false);
        conversionTime = Math.max(0, tag.getInt(CONVERSION_TIME_TAG));
        entityData.set(CONVERTING, tag.getBoolean(CONVERTING_TAG) && conversionTime > 0 && !isTamed());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "state", 5, state -> {
            if (!isTamed()) return state.setAndContinue(isTriggered() ? DASH : SIT);
            return state.setAndContinue(state.isMoving() ? WALK : IDLE);
        }));
    }
}
