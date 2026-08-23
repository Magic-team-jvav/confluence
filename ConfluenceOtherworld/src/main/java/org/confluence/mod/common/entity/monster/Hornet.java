package org.confluence.mod.common.entity.monster;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.projectile.HornetStingerProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 丛林黄蜂。
///
/// <p>黄蜂的射击与战斗走位是 1.21 中独立于普通飞行预制体的特例：射击前停止导航并
/// 转正，发射后按固定周期重新寻找悬空路径。相关计时保留在实体专用节点中，避免为单个
/// 特例增加公共飞行参数。</p>
public class Hornet extends BaseFlyingMonster {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack.cast");

    public Hornet(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0);
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
        Vec3 origin = position();
        Vec3 aim = new Vec3(target.getX() - getX(), target.getY() + target.getEyeHeight() * 0.5F - getY(), target.getZ() - getZ());
        projectile.configure(this, origin, aim, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 5.0F, 0);
        swing(InteractionHand.MAIN_HAND);
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

    @Override
    protected boolean hasEntityContactAttack() {
        return false;
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

    @Override
    public boolean doHurtTarget(Entity target) {
        swing(InteractionHand.MAIN_HAND);
        var damageSource = damageSources().sting(this);
        boolean hit = target.hurt(damageSource, (float) (int) getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (!hit) return false;
        if (level() instanceof ServerLevel serverLevel) {
            PortEnchantmentHelperExtension.doPostAttackEffects(serverLevel, target, damageSource);
        }
        if (!(target instanceof LivingEntity living)) return true;
        living.setStingerCount(living.getStingerCount() + 1);
        int duration = level().getDifficulty() == Difficulty.NORMAL ? 200 : level().getDifficulty() == Difficulty.HARD ? 360 : 0;
        if (duration > 0)
            living.addEffect(new MobEffectInstance(MobEffects.POISON, duration), this);
        playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
        return true;
    }

    private final class HornetCombatNode extends BTNode {
        private static final int SHOOT_INTERVAL = 25;
        private static final int REPATH_RESET = 200;
        private static final int REPATH_THRESHOLD = 175;
        private static final double FIRE_ANGLE = 0.1;

        private int shootCooldown;
        private int repathTicks;
        private boolean aiming;

        @Override
        public void start() {
            aiming = false;
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive()) {
                aiming = false;
                return BTStatus.FAILURE;
            }

            if (aiming) {
                lookAtTarget(target, 10.0F, 89.0F);
                if (angleBetween(getLookAngle(), target.getEyePosition().subtract(getEyePosition())) < FIRE_ANGLE) {
                    HornetStingerProjectile projectile = createStinger(target);
                    level().addFreshEntity(projectile);
                    shootCooldown = SHOOT_INTERVAL;
                    aiming = false;
                }
                return BTStatus.RUNNING;
            }

            if (--shootCooldown <= 0) {
                getNavigation().stop();
                aiming = true;
                return BTStatus.RUNNING;
            }

            if (--repathTicks <= 0 || repathTicks < REPATH_THRESHOLD) {
                Vec3 destination = findFlightPosition(6, 3);
                if (destination != null) {
                    swing(InteractionHand.MAIN_HAND);
                    getNavigation().moveTo(destination.x, destination.y, destination.z, 1.5);
                }
                repathTicks = REPATH_RESET;
            }
            lookAtTarget(target, 360.0F, 360.0F);
            return BTStatus.RUNNING;
        }

        @Override
        public void stop() {
            aiming = false;
        }
    }

    /// 无战斗目标时使用的原版黄蜂悬空游荡节点。
    /// 幼蜂沿用同一套路径选择，只替换战斗节点。
    protected class HornetWanderNode extends BTNode {
        private boolean moving;

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
            if (!getNavigation().isDone() || getRandom().nextInt(10) != 0) {
                return BTStatus.SUCCESS;
            }
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

    private void lookAtTarget(LivingEntity target, float yawLimit, float pitchLimit) {
        lookAt(target, yawLimit, pitchLimit);
        getLookControl().setLookAt(target);
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        double denominator = first.length() * second.length();
        if (denominator < 1.0E-7) {
            return 0.0;
        }
        double cosine = first.dot(second) / denominator;
        return Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
    }
}
