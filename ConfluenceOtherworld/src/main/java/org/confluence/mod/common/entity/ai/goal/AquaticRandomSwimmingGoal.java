package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AquaticRandomSwimmingGoal extends RandomStrollGoal {
    public AquaticRandomSwimmingGoal(PathfinderMob mob, double speed, int interval) {
        // 空闲计数不应禁止鱼类巡游，启动间隔仍由随机游走目标控制。
        super(mob, speed, interval, false);
    }

    @Override
    public boolean canUse() {
        return mob.isInWaterOrBubble() && super.canUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        return BehaviorUtils.getRandomSwimmablePos(mob, 10, 7);
    }
}
