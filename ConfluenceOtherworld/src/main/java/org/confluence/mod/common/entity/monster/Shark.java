package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.goal.AquaticRandomSwimmingGoal;
import org.jetbrains.annotations.Nullable;

public class Shark extends Piranha {
    public Shark(EntityType<? extends Shark> type, Level level) {
        super(type, level);
    }

    @Override
    protected Goal createStrollGoal() {
        return new SharkRandomSwimmingGoal(this, 0.6, 10);
    }

    @Override
    protected boolean flopsOnLand() {
        return false;
    }

    private static final class SharkRandomSwimmingGoal extends AquaticRandomSwimmingGoal {
        private Path patrolPath;

        private SharkRandomSwimmingGoal(Shark mob, double speed, int interval) {
            super(mob, speed, interval);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            patrolPath = null;
            for (int attempt = 0; attempt < 16; attempt++) {
                double angle = mob.getRandom().nextDouble() * Math.PI * 2.0;
                double distance = 2.0 + mob.getRandom().nextDouble() * (attempt < 8 ? 3.0 : 8.0);
                double height = attempt < 8 ? 0.0 : mob.getRandom().nextInt(7) - 3;
                Vec3 candidate = mob.position().add(Math.cos(angle) * distance, height, Math.sin(angle) * distance);
                if (!mob.isWithinRestriction(BlockPos.containing(candidate)) || !isSubmerged(candidate))
                    continue;
                Path path = mob.getNavigation().createPath(candidate.x, candidate.y, candidate.z, 0);
                if (path == null || !path.canReach() || path.getNodeCount() < 2) continue;
                Vec3 destination = path.getEntityPosAtNode(mob, path.getNodeCount() - 1);
                if (destination.distanceToSqr(mob.position()) < 1.0 || !isSubmerged(destination))
                    continue;
                patrolPath = path;
                return destination;
            }
            return null;
        }

        private boolean isSubmerged(Vec3 candidate) {
            AABB box = mob.getBoundingBox().move(candidate.subtract(mob.position())).deflate(1.0E-4);
            if (!mob.level().noCollision(mob, box)) return false;
            for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ), Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ))) {
                if (!mob.level().getFluidState(pos).is(FluidTags.WATER)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void start() {
            mob.getNavigation().moveTo(patrolPath, speedModifier);
        }

        @Override
        public void stop() {
            super.stop();
            patrolPath = null;
        }
    }
}
