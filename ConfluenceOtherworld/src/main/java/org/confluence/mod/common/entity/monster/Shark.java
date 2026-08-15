package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomSwimAction;

/**
 * 鲨鱼使用带惯性的持续水下追击，而不是离散的蓄力技能。
 *
 * <p>泰拉瑞亚鲨鱼属于 Swimming AI：玩家浸水时持续高速追逐，玩家离水后停止追击。
 * 1.21 实现只在食人鱼基础上修改随机游泳高度，寻路会让大体型鲨鱼贴近目标后频繁抖动。
 * 这里保留原本语义，用速度插值表达惯性和有限转向，既不会瞬间掉头，也不额外发明蓄力冲刺。</p>
 */
public class Shark extends Piranha {
    private static final double PURSUIT_SPEED = 0.46;
    private static final double PURSUIT_ACCELERATION = 0.12;

    public Shark(EntityType<? extends Shark> type, Level level) {
        super(type, level);
        this.moveControl = new SharkMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AquaticAttributeProfiles.SHARK.createBuilder();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new SharkPursuitAction(Shark.this),
                        new RandomSwimAction(Shark.this, 0.35, 10, 4));
            }
        };
    }

    BTNode createPursuitAction() {
        return new SharkPursuitAction(this);
    }

    private static final class SharkPursuitAction extends BTNode {
        private final Shark shark;
        private long nextAttackTick;

        private SharkPursuitAction(Shark shark) {
            this.shark = shark;
        }

        @Override
        public void start() {
            shark.getNavigation().stop();
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = shark.getTarget();
            if (target == null || !target.isInWater() || !shark.canAttack(target)) {
                return BTStatus.FAILURE;
            }

            Vec3 offset = target.getEyePosition().subtract(shark.getEyePosition());
            if (offset.lengthSqr() > 1.0E-6) {
                Vec3 desiredVelocity = offset.normalize().scale(PURSUIT_SPEED);
                Vec3 velocity = shark.getDeltaMovement().lerp(desiredVelocity, PURSUIT_ACCELERATION);
                if (shark.horizontalCollision) {
                    velocity = velocity.add(0.0, 0.12, 0.0);
                }
                shark.setDeltaMovement(velocity);

                float desiredYaw = (float) (Mth.atan2(velocity.z, velocity.x) * Mth.RAD_TO_DEG) - 90.0F;
                float yaw = Mth.rotLerp(0.18F, shark.getYRot(), desiredYaw);
                shark.setYRot(yaw);
                shark.yBodyRot = yaw;
            }

            long gameTime = shark.level().getGameTime();
            if (gameTime >= nextAttackTick
                    && shark.getBoundingBox().inflate(0.35).intersects(target.getBoundingBox())) {
                shark.swing(InteractionHand.MAIN_HAND);
                if (shark.doHurtTarget(target)) {
                    nextAttackTick = gameTime + 10;
                }
            }
            return BTStatus.RUNNING;
        }
    }

    private static final class SharkMoveControl extends SmoothSwimmingMoveControl {
        private SharkMoveControl(Mob mob) {
            super(mob, 85, 10, 0.02F, 0.1F, true);
        }

        @Override
        public void tick() {
            if (mob.isInWater()) {
                mob.setDeltaMovement(mob.getDeltaMovement().add(0.0, 0.001, 0.0));
            }
            super.tick();
        }
    }
}
