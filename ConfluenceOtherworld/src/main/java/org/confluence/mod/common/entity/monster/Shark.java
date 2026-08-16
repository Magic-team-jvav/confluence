package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Shark extends Piranha {
    public Shark(EntityType<? extends Shark> type, Level level) {
        super(type, level);
        this.moveControl = new SharkMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AquaticAttributeProfiles.SHARK.createBuilder();
    }

    @Override
    protected Goal createStrollGoal() {
        return new SharkRandomSwimmingGoal(this, 0.6, 10);
    }

    private static final class SharkMoveControl extends SmoothSwimmingMoveControl {
        private SharkMoveControl(Shark mob) {
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

    private static final class SharkRandomSwimmingGoal extends RandomSwimmingGoal {
        private SharkRandomSwimmingGoal(Shark mob, double speed, int interval) {
            super(mob, speed, interval);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            Vec3 rawPosition = BehaviorUtils.getRandomSwimmablePos(mob, 10, 3);
            if (rawPosition == null) {
                return null;
            }
            int y = (int) rawPosition.y;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(rawPosition.x, y + 1, rawPosition.z);
            while (mob.level().getBlockState(pos).isPathfindable(mob.level(), pos, PathComputationType.WATER) && y < rawPosition.y + 3) {
                y++;
                pos.set(rawPosition.x, y + 1, rawPosition.z);
            }
            return new Vec3(rawPosition.x, y, rawPosition.z);
        }
    }
}
