package org.confluence.mod.common.summon.ground;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonStats;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

/// 沿用铁傀儡寻路与近战逻辑，使用独立模型和动画的召唤物。
public final class IronGolemSummon extends SummonInstance {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 8.0F;
    private static final double SEARCH_RANGE = 32.0;
    private static final double FOLLOW_START_DISTANCE_SQR = 8.0 * 8.0;
    private SummonedIronGolem entity;

    public IronGolemSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("i_32_iron_golem"), owner, slotCost, stats, initialPose);
    }

    @Override
    protected LivingEntity findTarget() {
        Vec3 origin = entity == null ? position() : entity.position();
        LivingEntity target = SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), origin, SEARCH_RANGE);
        return target != null && target.distanceToSqr(owner()) <= SEARCH_RANGE * SEARCH_RANGE ? target : null;
    }

    @Override
    protected void beforeGoalTick() {
        if (ensureEntity()) entity.setTarget(target());
    }

    @Override
    protected void afterGoalTick() {
        if (entity != null && !entity.isRemoved()) {
            advanceTo(new SummonPose(entity.position(), entity.getYRot(), entity.getXRot(), 0.0F));
        }
    }

    @Override
    protected boolean usesOwnerRecovery() {
        return false;
    }

    @Override
    protected void onRemoved() {
        super.onRemoved();
        if (entity != null) {
            entity.discard();
            entity = null;
        }
    }

    private boolean ensureEntity() {
        if (entity != null && entity.level() != owner().level()) {
            entity.discard();
            entity = null;
        }
        if (entity != null) {
            if (entity.isAlive() && !entity.isRemoved()) return true;
            remove();
            return false;
        }
        entity = new SummonedIronGolem(owner().serverLevel(), this);
        entity.moveTo(position().x, position().y, position().z, currentPose().yaw(), currentPose().pitch());
        if (!owner().serverLevel().addFreshEntity(entity)) {
            entity = null;
            remove();
            return false;
        }
        return true;
    }

    public static final class SummonedIronGolem extends IronGolem implements GeoEntity {
        private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walking");
        private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("standing");
        private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attacking");
        private static final ResourceLocation SPEED_MODIFIER_ID = Confluence.asResource("summon_iron_golem_speed");
        private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(PortAttributeModifier.rl2uuid(SPEED_MODIFIER_ID), SPEED_MODIFIER_ID.getPath(), 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
        private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
        private IronGolemSummon summon;
        private int attackAnimationTicks;

        private SummonedIronGolem(ServerLevel level, IronGolemSummon summon) {
            this(ModEntities.SUMMONED_IRON_GOLEM.get(), level);
            this.summon = summon;
        }

        public SummonedIronGolem(EntityType<? extends IronGolem> type, Level level) {
            super(type, level);
            if (!getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(SPEED_MODIFIER)) {
                getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SPEED_MODIFIER);
            }
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, "Body", 2, state -> state.setAndContinue(state.isMoving() ? WALK : IDLE)).triggerableAnim("attack", ATTACK));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return animationCache;
        }

        @Override
        public void tick() {
            if (!level().isClientSide && summon == null) {
                discard();
                return;
            }
            if (!level().isClientSide && getTarget() != null && !canAttack(getTarget())) {
                setTarget(null);
                getNavigation().stop();
            }
            super.tick();
            if (!level().isClientSide && attackAnimationTicks > 0) {
                if (getTarget() == null || !canAttack(getTarget()) || attackAnimationTicks == 1) {
                    stopAttackAnimation();
                } else {
                    attackAnimationTicks--;
                }
            }
        }

        @Override
        public void setTarget(LivingEntity target) {
            super.setTarget(target);
            if (!level().isClientSide && target == null) stopAttackAnimation();
        }

        private void stopAttackAnimation() {
            if (attackAnimationTicks == 0) return;
            attackAnimationTicks = 0;
            stopTriggeredAnimation("Body", "attack");
        }

        @Override
        protected void registerGoals() {
            goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
            goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
            goalSelector.addGoal(6, new FollowSummonOwnerGoal(this));
            goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
            goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        }

        @Override
        public boolean doHurtTarget(Entity target) {
            if (!(target instanceof LivingEntity living) || !canAttack(living)) return false;
            if (!summon.hurtTarget(living, 1.0F))
                return false;
            stopAttackAnimation();
            attackAnimationTicks = 20;
            triggerAnim("Body", "attack");
            playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
            if (living instanceof BaseBoss) return true;
            double resistance = living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            living.setDeltaMovement(living.getDeltaMovement().add(
                    0.0, 0.4 * Math.max(0.0, 1.0 - resistance) * summon.stats().knockbackMultiplier(), 0.0));
            living.hasImpulse = true;
            return true;
        }

        @Override
        public boolean canAttack(LivingEntity target) {
            return summon != null && target.isAlive() && !target.isRemoved() && target.level() == level()
                    && SummonTargetCache.isValidTarget(summon.owner(), target, SEARCH_RANGE, true)
                    && super.canAttack(target);
        }

        @Override
        public boolean hurt(DamageSource source, float amount) {
            return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
        }

        @Override
        public boolean isPickable() {
            return false;
        }

        @Override
        public boolean shouldBeSaved() {
            return false;
        }
    }

    private static final class FollowSummonOwnerGoal extends Goal {
        private final SummonedIronGolem golem;
        private int repathTicks;

        private FollowSummonOwnerGoal(SummonedIronGolem golem) {
            this.golem = golem;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return golem.getTarget() == null && golem.distanceToSqr(golem.summon.owner()) >= FOLLOW_START_DISTANCE_SQR;
        }

        @Override
        public boolean canContinueToUse() {
            return golem.getTarget() == null && !golem.getNavigation().isDone() && golem.distanceToSqr(golem.summon.owner()) > 2.0 * 2.0;
        }

        @Override
        public void start() {
            repathTicks = 0;
        }

        @Override
        public void stop() {
            golem.getNavigation().stop();
        }

        @Override
        public void tick() {
            ServerPlayer owner = golem.summon.owner();
            golem.getLookControl().setLookAt(owner, 10.0F, golem.getMaxHeadXRot());
            if (--repathTicks > 0) return;
            repathTicks = adjustedTickDelay(10);
            if (golem.distanceToSqr(owner) >= 40.0 * 40.0) teleportNearOwner(owner);
            else golem.getNavigation().moveTo(owner, 1.0);
        }

        private void teleportNearOwner(ServerPlayer owner) {
            BlockPos origin = owner.blockPosition();
            for (int attempt = 0; attempt < 10; attempt++) {
                int x = golem.getRandom().nextIntBetweenInclusive(-3, 3);
                int z = golem.getRandom().nextIntBetweenInclusive(-3, 3);
                if (Math.abs(x) < 2 && Math.abs(z) < 2) continue;
                BlockPos target = origin.offset(x, golem.getRandom().nextIntBetweenInclusive(-1, 1), z);
                if (WalkNodeEvaluator.getBlockPathTypeStatic(golem.level(), target.mutable()) != BlockPathTypes.WALKABLE || golem.level().getBlockState(target.below()).getBlock() instanceof LeavesBlock)
                    continue;
                Vec3 destination = Vec3.atBottomCenterOf(target);
                Vec3 offset = destination.subtract(golem.position());
                if (!golem.level().noCollision(golem, golem.getBoundingBox().move(offset)))
                    continue;
                golem.moveTo(destination.x, destination.y, destination.z,
                        golem.getYRot(), golem.getXRot());
                golem.getNavigation().stop();
                return;
            }
        }
    }
}
