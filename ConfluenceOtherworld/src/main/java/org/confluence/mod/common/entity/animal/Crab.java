package org.confluence.mod.common.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;

public class Crab extends BaseCritter {

    public Crab(EntityType<? extends Crab> type, Level level) {
        super(type, level);
        this.moveControl = new SidewaysMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createInsectAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createGroundCritterRoutine(1.0), 1.5);
            }
        };
    }

    /// 将导航计算出的行进速度转换为螃蟹的横向步态。
    ///
    /// <p>移动控制器通过 {@link #setSpeed(float)} 写入速度，因此恐慌和闲逛行为都会自然复用
    /// 同一套横向移动规则，不需要让每个行为节点分别识别螃蟹。</p>
    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        setZza(0.0F);
        setXxa(speed);
    }

    /// 略微提高横向步态克服地面摩擦的能力，使螃蟹不会在方块边缘反复丢失导航速度。
    @Override
    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 movement, float friction) {
        return super.handleRelativeFrictionAndCalculateMovement(movement, friction * 1.2F);
    }

    /// 把普通“面朝目标向前走”转换为“身体侧面朝向目标横着走”。
    ///
    /// <p>除了朝向和输入轴以外，控制器仍保留原版的速度属性、台阶检测和跳跃控制，
    /// 因而可以继续使用通用导航，也能跨越一格高差和较低的碰撞体。</p>
    static final class SidewaysMoveControl extends MoveControl {
        private static final double MINIMUM_DISTANCE_SQUARED = 2.500000277905201E-7;

        SidewaysMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (operation != Operation.MOVE_TO) {
                super.tick();
                if (operation == Operation.WAIT) {
                    mob.setSpeed(0.0F);
                }
                return;
            }

            operation = Operation.WAIT;
            double deltaX = wantedX - mob.getX();
            double deltaY = wantedY - mob.getY();
            double deltaZ = wantedZ - mob.getZ();
            double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distanceSquared < MINIMUM_DISTANCE_SQUARED) {
                mob.setXxa(0.0F);
                mob.setZza(0.0F);
                return;
            }

            float targetYaw = (float) (Mth.atan2(deltaZ, deltaX) * Mth.RAD_TO_DEG);
            mob.setYRot(rotlerp(mob.getYRot(), targetYaw, 90.0F));
            mob.setSpeed((float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

            BlockPos currentPos = mob.blockPosition();
            BlockState currentState = mob.level().getBlockState(currentPos);
            VoxelShape collision = currentState.getCollisionShape(mob.level(), currentPos);
            boolean targetAboveStep = deltaY > mob.maxUpStep()
                    && deltaX * deltaX + deltaZ * deltaZ
                    < Math.max(1.0F, mob.getBbWidth());
            boolean obstructed = !collision.isEmpty()
                    && mob.getY()
                    < collision.max(Direction.Axis.Y) + currentPos.getY()
                    && !currentState.is(BlockTags.DOORS)
                    && !currentState.is(BlockTags.FENCES);
            if (targetAboveStep || obstructed) {
                mob.getJumpControl().jump();
                operation = Operation.JUMPING;
            }
        }
    }
}
