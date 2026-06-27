package org.confluence.mod.util.entity.ai.brain.behavior.range;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

/**
 * 攻击行为清除
 */
public class AttackCalmDownBrain<T extends Mob> extends Behavior<T> {
    float distanceToRemove;
    public AttackCalmDownBrain(float distanceToRemove) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED
        ));
        this.distanceToRemove = distanceToRemove * distanceToRemove;
    }

    protected boolean checkExtraStartConditions(ServerLevel level, T owner) {
        if(owner.getBrain().getMemory(MemoryModuleType.ATTACK_COOLING_DOWN).isPresent()){
            return !owner.getBrain().getMemory(MemoryModuleType.ATTACK_COOLING_DOWN).get();
        }
        return true;
    }

    @Override
    protected void start(ServerLevel level, T living, long gameTimeIn) {
        Brain<?> brain = living.getBrain();
        var memory = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        boolean shouldRemove;
        if(memory.isEmpty()){
            // 目标丢失，取消攻击状态
            shouldRemove = true;
        }else{
            LivingEntity target = memory.get();
            // 距离过远，目标死亡，没有视线
            shouldRemove= target.distanceToSqr(living) > distanceToRemove || !target.isAlive();
        }
        if (shouldRemove) {
            calmDown(level, living, gameTimeIn);
        }
    }

    protected void calmDown(ServerLevel level, T living, long gameTimeIn) {
        Brain<?> brain = living.getBrain();
        brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
        brain.updateActivityFromSchedule(level.getDayTime(), gameTimeIn);
    }
}
