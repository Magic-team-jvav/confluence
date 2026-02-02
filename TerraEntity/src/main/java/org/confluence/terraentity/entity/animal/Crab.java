package org.confluence.terraentity.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Crab extends SimpleAnimal {
    public Crab(EntityType<? extends SimpleAnimal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new CrabMoveControl(this);

    }

    // 螃蟹横着走，非常有意思
    static class CrabMoveControl extends MoveControl {

        public CrabMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {

            if (this.operation == MoveControl.Operation.MOVE_TO) {
                this.operation = MoveControl.Operation.WAIT;
                double deltaX = this.wantedX - this.mob.getX();
                double deltaY = this.wantedY - this.mob.getY();
                double deltaZ = this.wantedZ - this.mob.getZ();
                double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
                if (distanceSquared < 2.500000277905201E-7) {
                    this.mob.setXxa(0.0f);
                    return;
                }

                float targetYaw = (float)(Mth.atan2(deltaZ, deltaX) * 180.0 / 3.1415927410125732);
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, 90.0F));
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                BlockPos currentBlockPos = this.mob.blockPosition();
                BlockState currentBlockState = this.mob.level().getBlockState(currentBlockPos);
                VoxelShape collisionShape = currentBlockState.getCollisionShape(this.mob.level(), currentBlockPos);
                if (deltaY > (double)this.mob.maxUpStep() && deltaX * deltaX + deltaZ * deltaZ < (double)Math.max(1.0F, this.mob.getBbWidth()) ||
                        !collisionShape.isEmpty() && this.mob.getY() < collisionShape.max(Direction.Axis.Y) + (double)currentBlockPos.getY() &&
                                !currentBlockState.is(BlockTags.DOORS) && !currentBlockState.is(BlockTags.FENCES)) {
                    this.mob.getJumpControl().jump();
                    this.operation = MoveControl.Operation.JUMPING;
                }

            }else{
                super.tick();
                if(this.operation == MoveControl.Operation.WAIT){
                    this.mob.setSpeed(0);
                }
            }

        }
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        this.setZza(0);
        this.setXxa(speed);
    }

    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 deltaMovement, float friction) {
        return super.handleRelativeFrictionAndCalculateMovement(deltaMovement, friction * 1.2f);
    }
}
