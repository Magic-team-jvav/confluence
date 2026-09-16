package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.ThrownRockProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class RockGolem extends BaseMonster {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation THROW = RawAnimation.begin().thenPlay("attack.throw");
    private static final RawAnimation STRIKE = RawAnimation.begin().thenPlay("attack.strike");
    private int nextThrowTick;

    public RockGolem(EntityType<? extends RockGolem> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(new ThrowAction(),
                        new VanillaGoalAction(new MeleeAttackGoal(RockGolem.this, 1.0, true)),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(RockGolem.this, 0.7)));
            }
        };
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            stopTriggeredAnimation("Action", "strike");
            triggerAnim("Action", "strike");
        }
        return hit;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Action", 3, state -> state.setAndContinue(state.isMoving() ? WALK : IDLE))
                .triggerableAnim("throw", THROW).triggerableAnim("strike", STRIKE));
    }

    private final class ThrowAction extends BTNode {
        private int elapsed;

        @Override
        public boolean canStart() {
            LivingEntity target = getTarget();
            return tickCount >= nextThrowTick && target != null && target.isAlive()
                    && distanceToSqr(target) > 9.0 && distanceToSqr(target) < 576.0 && getSensing().hasLineOfSight(target);
        }

        @Override
        public void start() {
            elapsed = 0;
            navigation.stop();
            moveControl.setWantedPosition(getX(), getY(), getZ(), 0.0);
            setSpeed(0.0F);
            setXxa(0.0F);
            setZza(0.0F);
            triggerAnim("Action", "throw");
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive()) return BTStatus.FAILURE;
            faceCombatPosition(target.getEyePosition(), 15.0F, 60.0F);
            // 手臂在约 0.42 秒越过投掷点；完整动作约 0.79 秒。
            if (++elapsed == 9 && getSensing().hasLineOfSight(target)) {
                Vec3 origin = getEyePosition();
                Vec3 delta = target.getEyePosition().subtract(origin);
                double ticks = Mth.clamp(Math.ceil(delta.horizontalDistance() / 0.75), 8.0, 35.0);
                Vec3 velocity = new Vec3(delta.x / ticks, delta.y / ticks + ThrownRockProjectile.GRAVITY * (ticks + 1.0) * 0.5, delta.z / ticks);
                ThrownRockProjectile rock = new ThrownRockProjectile(ModEntities.THROWN_ROCK.get(), level());
                rock.configure(RockGolem.this, origin, velocity, (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 40.0F / 85.0F, 100);
                if (level().addFreshEntity(rock))
                    playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.7F);
            }
            return elapsed >= 16 ? BTStatus.SUCCESS : BTStatus.RUNNING;
        }

        @Override
        public void stop() {
            nextThrowTick = tickCount + 80;
            stopTriggeredAnimation("Action", "throw");
        }
    }
}
