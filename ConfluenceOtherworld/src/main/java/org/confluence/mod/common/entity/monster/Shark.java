package org.confluence.mod.common.entity.monster;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.ai.goal.AquaticRandomSwimmingGoal;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 鲨鱼以水中路径引导巡游和追击，推进与转向使用同一个游泳控制器。
public class Shark extends BaseAquaticMonster {
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");

    public Shark(EntityType<? extends Shark> type, Level level) {
        super(type, level);
        moveControl = new SharkSwimmingControl();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new MeleeAttackGoal(Shark.this, 1.2, true)),
                        new VanillaGoalAction(new PatrolGoal()),
                        new WaitAction(10));
            }
        };
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return false;
    }

    @Override
    protected boolean flopsOnLand() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Swim", 5, state -> state.setAndContinue(SWIM)));
    }

    private final class PatrolGoal extends AquaticRandomSwimmingGoal {
        private Path patrolPath;
        private int retryAt;

        private PatrolGoal() {
            super(Shark.this, 0.6, 1);
        }

        @Override
        public boolean canUse() {
            return tickCount >= retryAt && super.canUse();
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            patrolPath = null;
            Vec3 forward = position().add(getViewVector(1.0F).scale(8.0));
            for (int attempt = 0; attempt < 8; attempt++) {
                Vec3 candidate = attempt < 4
                        ? DefaultRandomPos.getPosTowards(Shark.this, 10, 3, forward, Math.PI / 2.0)
                        : DefaultRandomPos.getPos(Shark.this, 6, 3);
                if (candidate == null) continue;
                Path path = getNavigation().createPath(candidate.x, candidate.y, candidate.z, 0);
                if (path == null || !path.canReach() || path.getNodeCount() < 2) continue;
                Vec3 destination = path.getEntityPosAtNode(Shark.this, path.getNodeCount() - 1);
                if (destination.distanceToSqr(position()) < 4.0) continue;
                patrolPath = path;
                return destination;
            }
            retryAt = tickCount + 10;
            return null;
        }

        @Override
        public void start() {
            getNavigation().moveTo(patrolPath, speedModifier);
        }

        @Override
        public void stop() {
            super.stop();
            patrolPath = null;
        }
    }

    private final class SharkSwimmingControl extends MoveControl {
        private SharkSwimmingControl() {
            super(Shark.this);
        }

        @Override
        public void tick() {
            setSpeed(0.0F);
            setXxa(0.0F);
            setYya(0.0F);
            setZza(0.0F);
            if (!isInWaterOrBubble() || operation != Operation.MOVE_TO || getNavigation().isDone())
                return;

            Vec3 offset = new Vec3(wantedX - getX(), wantedY - getY(), wantedZ - getZ());
            double distance = offset.length();
            if (distance < 0.05) return;
            float desiredYaw = (float) (Mth.atan2(offset.z, offset.x) * Mth.RAD_TO_DEG) - 90.0F;
            float desiredPitch = (float) (-Mth.atan2(offset.y, offset.horizontalDistance()) * Mth.RAD_TO_DEG);
            float turn = Math.abs(Mth.wrapDegrees(desiredYaw - getYRot()));
            setYRot(rotlerp(getYRot(), desiredYaw, 10.0F));
            setXRot(rotlerp(getXRot(), desiredPitch, 5.0F));
            setYBodyRot(getYRot());
            setYHeadRot(getYRot());

            // 转弯和接近路径节点时自然收油，顺着身体朝向推进，不横着滑向目标。
            double throttle = Mth.lerp(Mth.clamp(turn / 90.0F, 0.0F, 1.0F), 1.0, 0.25);
            double speed = getAttributeValue(Attributes.MOVEMENT_SPEED) * speedModifier * 0.12
                    * throttle * Math.min(1.0, distance);
            Vec3 desired = Vec3.directionFromRotation(getXRot(), getYRot()).scale(speed);
            setDeltaMovement(getDeltaMovement().lerp(desired, 0.2));
        }
    }
}
