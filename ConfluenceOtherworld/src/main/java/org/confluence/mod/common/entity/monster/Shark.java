package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Shark extends Piranha {

    public Shark(EntityType<? extends Shark> entityType, Level level) {
        super(entityType, level);
    }

    protected SmoothSwimmingMoveControl createMoveControl() {
        return new SharkMoveControl(this, 85, 10, 0.02F, 0.1F, true);
    }

    @Override
    protected Goal createStrollGoal() {
        return new SharkRandomSwimmingGoal(this, 0.6, 10);
    }

    public static class SharkRandomSwimmingGoal extends RandomSwimmingGoal {

        public SharkRandomSwimmingGoal(PathfinderMob mob, double speed, int interval) {
            super(mob, speed, interval);
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 rawPos = BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 3);
            if(rawPos == null) {
                return null;
            }
            int y = (int) rawPos.y;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(rawPos.x, y+1, rawPos.z);
            while(this.mob.level().getBlockState(pos).isPathfindable(this.mob.level(), pos, PathComputationType.WATER) && y < rawPos.y + 3) {
                y++;
                pos.set(rawPos.x, y+1, rawPos.z);
            }
            return new Vec3(rawPos.x, y, rawPos.z);
        }

    }

    protected static class SharkMoveControl extends SmoothSwimmingMoveControl {
        boolean applyGravity;
        public SharkMoveControl(Mob mob, int maxTurnX, int maxTurnY, float inWaterSpeedModifier, float outsideWaterSpeedModifier, boolean applyGravity) {
            super(mob, maxTurnX, maxTurnY, inWaterSpeedModifier, outsideWaterSpeedModifier, applyGravity);
            this.applyGravity = applyGravity;
        }

        @Override
        public void tick() {
            if (this.mob.isInWater()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0F, 0.001, 0.0F));
            }
            super.tick();
        }
    }


}
