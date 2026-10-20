package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.projectile.HornetStingerProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/// 丛林黄蜂。
///
/// 可见目标时持续平滑追踪并射击；失去视线后按固定间隔寻路，避免受阻时逐刻重试。
public class Hornet extends BaseFlyingMonster {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack.cast");

    public Hornet(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(new HornetCombatNode(), new HornetWanderNode());
            }
        };
    }

    HornetStingerProjectile createStinger(LivingEntity target) {
        HornetStingerProjectile projectile = new HornetStingerProjectile(ModEntities.HORNET_STINGER.get(), level());
        Vec3 origin = getBoundingBox().getCenter();
        Vec3 aim = new Vec3(target.getX() - getX(), target.getY() + target.getEyeHeight() * 0.5F - getY(), target.getZ() - getZ());
        projectile.configure(this, origin, aim, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 5.0F, 0);
        return projectile;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle/Attack", 3, state -> state.setAndContinue(swinging ? ATTACK : IDLE)));
    }

    @Override
    protected boolean hasPushableBody() {
        return true;
    }

    /// 泰拉黄蜂入水后向上脱离水体，不在水底继续悬停和射击。
    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && !isNoAi() && isInWater()) {
            Vec3 movement = getDeltaMovement();
            setDeltaMovement(movement.x, Math.max(0.2, movement.y), movement.z);
            hasImpulse = true;
        }
    }

    /// 黄蜂可以穿过门洞，但不会把水面当作可漂浮路径。
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level) {
            @Override
            public boolean isStableDestination(BlockPos position) {
                return !this.level.getBlockState(position.below()).isAir();
            }
        };
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(false);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    /// 黄蜂毒刺在经典模式有三分之一概率中毒；专家及大师模式必定触发。
    /// 高难度下三分之一为固定长时效果，其余为随机短时效果。
    public void applyStingerPoison(LivingEntity target) {
        boolean master = LibUtils.isMaster(level());
        boolean expert = master || LibUtils.isAtLeastExpert(level());
        if (!expert && random.nextInt(3) != 0) return;
        int duration;
        if (!expert) {
            duration = 200;
        } else if (random.nextInt(3) == 0) {
            duration = master ? 500 : 400;
        } else {
            duration = master ? Mth.randomBetweenInclusive(random, 50, 250) : Mth.randomBetweenInclusive(random, 40, 200);
        }
        target.addEffect(new MobEffectInstance(MobEffects.POISON, duration), this);
    }

    /// 持续平滑追踪并沿飞行方向射击，不使用停下瞄准后突然加速的伪冲刺。
    private final class HornetCombatNode extends BTNode {
        private static final int SHOOT_INTERVAL = 25;
        private static final int REPATH_INTERVAL = 20;
        private static final double FIRE_ANGLE = 0.7;
        private static final double HOVER_HEIGHT = 1.5;
        private int shootCooldown;
        private int repathTicks;

        @Override
        public boolean canStart() {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && canAttack(target);
        }

        @Override
        public void start() {
            repathTicks = 0;
            getNavigation().stop();
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive() || !canAttack(target)) return BTStatus.FAILURE;
            boolean visible = getSensing().hasLineOfSight(target);
            if (visible) {
                getNavigation().stop();
                getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0.0);
                setSpeed(0.0F);
                setXxa(0.0F);
                setYya(0.0F);
                setZza(0.0F);
                Vec3 offset = target.getBoundingBox().getCenter().add(0.0, HOVER_HEIGHT, 0.0).subtract(getBoundingBox().getCenter());
                double speed = getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.6;
                Vec3 desired = offset.normalize().scale(speed);
                Vec3 current = getDeltaMovement();
                double acceleration = speed * 0.06;
                Vec3 next = current.add(Mth.clamp(desired.x - current.x, -acceleration, acceleration),
                        Mth.clamp(desired.y - current.y, -acceleration, acceleration),
                        Mth.clamp(desired.z - current.z, -acceleration, acceleration));
                setDeltaMovement(next);
                faceCombatMovement(10.0F, 30.0F);
                hasImpulse = true;
                repathTicks = 0;
            } else if (--repathTicks <= 0) {
                /// 失去视线才寻路，失败也等待下一轮，不在每 tick 重复分配路径节点。
                var path = getNavigation().createPath(target, 0);
                if (path != null) getNavigation().moveTo(path, 1.0);
                repathTicks = REPATH_INTERVAL;
            }
            if (shootCooldown > 0) shootCooldown--;
            if (shootCooldown == 0 && visible && !isInWater() && distanceToSqr(target) <= 32.0 * 32.0
                    && angleBetween(getLookAngle(), target.getEyePosition().subtract(getEyePosition())) < FIRE_ANGLE) {
                HornetStingerProjectile projectile = createStinger(target);
                if (level().addFreshEntity(projectile)) swing(InteractionHand.MAIN_HAND);
                else projectile.discard();
                shootCooldown = SHOOT_INTERVAL;
            }
            return BTStatus.RUNNING;
        }

        @Override
        public void stop() {
            getNavigation().stop();
            setSpeed(0.0F);
            setZza(0.0F);
        }
    }

    /// 无战斗目标时使用的原版黄蜂悬空游荡节点。
    /// 幼蜂沿用同一套路径选择，只替换战斗节点。
    protected class HornetWanderNode extends BTNode {
        private boolean moving;
        private int retryTicks;

        @Override
        public void start() {
            moving = false;
        }

        @Override
        public BTStatus execute() {
            if (getTarget() != null) {
                return BTStatus.FAILURE;
            }
            if (moving) {
                return getNavigation().isInProgress()
                        ? BTStatus.RUNNING
                        : BTStatus.SUCCESS;
            }
            if (--retryTicks > 0 || !getNavigation().isDone()) {
                return BTStatus.SUCCESS;
            }
            retryTicks = 20 + random.nextInt(20);
            Vec3 destination = findFlightPosition(3, 1);
            if (destination == null) {
                return BTStatus.SUCCESS;
            }
            moving = getNavigation().moveTo(destination.x, destination.y, destination.z, 1.0);
            return moving ? BTStatus.RUNNING : BTStatus.SUCCESS;
        }
    }

    protected final Vec3 findFlightPosition(int verticalRange, int minimumHeight) {
        Vec3 view = getViewVector(0.0F);
        Vec3 destination = HoverRandomPos.getPos(this, 8, 7, view.x, view.z, (float) (Math.PI / 2.0), verticalRange, minimumHeight);
        return destination != null
                ? destination
                : AirAndWaterRandomPos.getPos(this, 8, 4, -2, view.x, view.z, Math.PI / 2.0);
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        double denominator = first.length() * second.length();
        if (denominator < 1.0E-7) {
            return 0.0;
        }
        double cosine = first.dot(second) / denominator;
        return Math.acos(Mth.clamp(cosine, -1.0, 1.0));
    }

}
