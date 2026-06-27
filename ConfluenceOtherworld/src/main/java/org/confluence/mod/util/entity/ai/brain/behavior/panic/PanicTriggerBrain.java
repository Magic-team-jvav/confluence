package org.confluence.mod.util.entity.ai.brain.behavior.panic;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

/**
 * 恐慌触发器
 */
public class PanicTriggerBrain<T extends LivingEntity> extends Behavior<T> {
    public PanicTriggerBrain() {
        super(ImmutableMap.of());
    }

    protected boolean canStillUse(ServerLevel level, T entity, long gameTime) {
        return isHurt(entity) || hasHostile(entity);
    }

    protected void start(ServerLevel level, T entity, long gameTime) {
        if (isHurt(entity) || hasHostile(entity)) {
            Brain<?> brain = entity.getBrain();

            if (!brain.isActive(Activity.PANIC)) {
                brain.eraseMemory(MemoryModuleType.PATH);
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
                brain.eraseMemory(MemoryModuleType.BREED_TARGET);
                brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            }

            if(shouldPanic(entity)) {
                brain.setActiveActivityIfPossible(Activity.PANIC);
            }
        }

    }

    protected void tick(ServerLevel level, T owner, long gameTime) {
        super.tick(level, owner, gameTime);
    }

    public boolean hasHostile(T entity) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE);
    }

    public boolean isHurt(T owner) {
        return owner.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY) || owner.hurtTime > 0;
    }

    protected boolean shouldPanic(T entity) {
        return true;
    }
}
