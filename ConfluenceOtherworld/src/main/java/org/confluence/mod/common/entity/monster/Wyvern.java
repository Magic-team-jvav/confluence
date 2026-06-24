package org.confluence.mod.common.entity.monster;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.IDiscardWhenRespawnEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.entity.IHeightControlMob;
import org.confluence.mod.common.entity.ai.goal.ComeAndBackDashAttackGoal;
import org.confluence.mod.common.entity.ai.goal.MutableRangeNearestAttackableTargetGoal;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.EnumSet;
import java.util.UUID;

public class Wyvern<S extends BaseWormPart> extends BaseWorm<S> implements IDiscardWhenRespawnEntity {
    public Wyvern(EntityType<? extends Wyvern> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        this.segInternal = 1;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WyvernAttackGoal<>(this, 16));
        this.goalSelector.addGoal(3, new WyvernRandomStrollGoal<>(this, 20, 0.015f, 0.2f, 6, 150, 10, 0.9f));

//        this.targetSelector.addGoal(1,new AccelerateOnSeeingGoal(this,0.25f));
        this.targetSelector.addGoal(2, new MutableRangeNearestAttackableTargetGoal<>(this, Player.class, false));
    }

    /// 类似飞龙的游走ai：绕中心做圆周运动，随机隔段时间选定当前位置作为下一个中心
    public static class WyvernRandomStrollGoal<T extends Mob & IHeightControlMob> extends Goal {
        protected T worm;
        protected Vec3 center;
        protected float radius;
        protected float angle;
        protected float offsetY;
        protected boolean crease = false;
        protected int changeCenterCount = 0;
        protected float angleIncrease;
        protected float wanderOffsetYMax;
        protected float offsetYSpeed;
        protected int interval;
        protected float wanderRandomOffsetXZ;
        private final float defaultSpeed;

        /// @param radius           圆周运动半径
        /// @param angleIncrease    角度增量
        /// @param offsetYSpeed     y轴偏移增量
        /// @param wanderOffsetYMax y轴最大偏移
        /// @param interval         随机中心变换时间间隔
        public WyvernRandomStrollGoal(T worm, float radius, float angleIncrease, float offsetYSpeed, float wanderOffsetYMax, int interval, float wanderRandomOffsetXZ, float speedModifier) {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.worm = worm;
            this.radius = radius;
            this.angleIncrease = angleIncrease;
            this.offsetYSpeed = offsetYSpeed;
            this.wanderOffsetYMax = wanderOffsetYMax;
            this.interval = interval;
            this.wanderRandomOffsetXZ = wanderRandomOffsetXZ;
            this.defaultSpeed = (float) Math.sqrt(angleIncrease * radius * angleIncrease * radius + offsetYSpeed * offsetYSpeed) * speedModifier; // 默认线速度，必须小于这个值，否则会卡顿

        }


        @Override
        public boolean canUse() {
            return worm.getTarget() == null;
        }

        @Override
        public void start() {
            this.changeCenterCount = this.getNextCenterCount();

            this.angleIncrease = worm.getRandom().nextBoolean() ? this.angleIncrease : -this.angleIncrease;
            this.center = this.getNextCenter();

        }

        @Override
        public boolean canContinueToUse() {
            return --this.changeCenterCount > 0;
        }

        @Override
        public void tick() {
            this.angle += this.angleIncrease;
            if (this.offsetY > this.wanderOffsetYMax) {
                this.crease = false;
            } else if (offsetY < -this.wanderOffsetYMax) {
                this.crease = true;
            }
            this.offsetY = Mth.clamp(this.offsetY, -this.wanderOffsetYMax, this.wanderOffsetYMax);
            this.offsetY += this.crease ? this.offsetYSpeed : -this.offsetYSpeed;

//            Vec3 dynamicPos = calPosition(radius, angle, 0);
            Vec3 dynamicPos = this.calCirclePosition(this.radius, this.angle, this.offsetY);
            Vec3 dir = dynamicPos.subtract(this.worm.position());

            Vec3 lookPos = this.calCirclePosition(this.radius, this.angle + this.angleIncrease * 5, 0);
            this.worm.getLookControl().setLookAt(lookPos);

            this.worm.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos);

            this.worm.setDeltaMovement(this.getMovement(dir));


        }

        protected Vec3 getMovement(Vec3 delta) {
            return delta.normalize().scale(this.defaultSpeed);
        }

        protected int getNextCenterCount() {
            return this.worm.getRandom().nextInt(this.interval, (int) (this.interval * 1.5));
        }

        protected Vec3 getNextCenter() {
            return this.worm.position().add(
                    (this.worm.getRandom().nextFloat() - 0.5f) * this.wanderRandomOffsetXZ,
                    this.worm.wrapWanderHeight(this.worm.position()) - this.worm.position().y,
                    (this.worm.getRandom().nextFloat() - 0.5f) * this.wanderRandomOffsetXZ);
        }

        private Vec3 calCirclePosition(float radius, float angle, double offsetY) {
            return this.center.add(
                    radius * Math.cos(angle),
                    offsetY,
                    radius * Math.sin(angle));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class WyvernAttackGoal<T extends Mob & IHeightControlMob> extends ComeAndBackDashAttackGoal<T> {
        private final UUID followRangeModifier;
        private int triggerDash = 0;

        public WyvernAttackGoal(T warm, float distanceToTurn) {
            super(warm, distanceToTurn);
            this.followRangeModifier = PortAttributeModifier.rl2uuid(Confluence.asResource("aggressive"));
        }

        @Override
        public boolean canUse() {
            return worm.getTarget() != null;
        }

        @Override
        public void start() {
            // 飞龙有攻击目标后索敌范围增加
            var att = worm.getAttribute(Attributes.FOLLOW_RANGE);
            if (!att.hasModifier(followRangeModifier)) {
                att.addPermanentModifier(new AttributeModifier(followRangeModifier, 200, AttributeModifier.Operation.ADDITION));
            }
        }


        @Override
        public void tick() {
            boolean turn = this.turning;
            if (this.turning) {
                this.triggerDash--;
            }
            super.tick();
            if (!this.turning && turn) {
                this.triggerDash = 0;
            }
        }

        @Override
        protected Vec3 getBackTurnMovement(double distance) {
            return worm.getLookAngle().normalize().scale(0.4f).add(0, 0.4f, 0);
        }

        @Override
        protected Vec3 getAttackMovement(LivingEntity target, double distance) {
            if (this.worm.distanceTo(target) < 5f && target.level().getBlockState(target.blockPosition().below()).isAir()) {
                this.triggerDash = 15;
            }
            if (this.triggerDash > 0) {
                return super.getAttackMovement(target, distance).scale(2.5f).add(0, 0.1, 0);
            }
            return super.getAttackMovement(target, distance).scale(2f);
        }

        @Override
        protected void attackLookAt(LivingEntity target) {
            if (this.triggerDash > 0) {
                worm.getLookControl().setLookAt(worm.getEyePosition().add(worm.getForward().normalize().scale(5)));
                return;
            }
            worm.getLookControl().setLookAt(target);
            worm.lookAt(target, 2f, 30f);
        }
    }

    @Override
    public S createPart(int index) {
        return (S) createSimplePart(this, index);
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return true;
    }

    @Override
    public double wrapWanderHeight(Vec3 pos) {
        double y = Math.max(pos.y, 95);
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(pos.x, y, pos.z);
        if (!level().canSeeSky(blockPos)) {
            blockPos.move(0, 5, 0);
        }
        return y + 10; // 至少离地面10格高
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.WYVERN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.WYVERN_DEATH.get();
    }
}
