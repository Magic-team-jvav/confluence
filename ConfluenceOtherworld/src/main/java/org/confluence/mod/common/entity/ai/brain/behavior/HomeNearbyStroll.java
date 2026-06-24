package org.confluence.mod.common.entity.ai.brain.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

import static net.minecraft.world.entity.ai.util.LandRandomPos.movePosUpOutOfSolid;

/**
 * 白天在家附近随机游走
 */
public class HomeNearbyStroll {


    public HomeNearbyStroll() {
    }

    public static OneShot<PathfinderMob> create(float speedModifier) {
        return create(speedModifier, 20, 20);
    }

    /**
     * 白天在家附近随机游走
     * @param speedModifier 速度
     * @param maxHorizontalDist 最大水平距离
     * @param maxVerticalDist 最大垂直距离
     */
    public static OneShot<PathfinderMob> create(float speedModifier, int maxHorizontalDist, int maxVerticalDist) {
        return BehaviorBuilder.create((instance) -> instance.group(
                instance.absent(MemoryModuleType.WALK_TARGET),
                instance.present(MemoryModuleType.HOME)
        ).apply(instance, (walk_target, gHomePos) -> (serverLevel, mob, time) -> {

            Vec3 vec3;
            var home = mob.getBrain().getMemory(MemoryModuleType.HOME);
            if(home.isPresent()){

                BlockPos homePos = home.get().pos();
                vec3 = RandomPos.generateRandomPos(() -> {
                    BlockPos direction = RandomPos.generateRandomDirection(mob.getRandom(), maxHorizontalDist, maxVerticalDist);
                    BlockPos pos = homePos.offset(direction);
                    return movePosUpOutOfSolid(mob, pos);
                }, mob::getWalkTargetValue);

            }else{
                vec3 = LandRandomPos.getPos(mob, maxHorizontalDist, maxVerticalDist);
            }


            walk_target.setOrErase(Optional.ofNullable(vec3).map(pos ->
                    new WalkTarget(pos, speedModifier, 0)));
            return true;
        }));
    }

}
