package org.confluence.terraentity.entity.ai.brain.behavior.panic;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.confluence.terraentity.init.TEAi;

/**
 * 恐慌行为清除
 */
public class PanicCalmDownBrain<T extends Mob> extends Behavior<T> {
    float chanceToCalmDown;
    public PanicCalmDownBrain() {
        this(0);
    }
    public PanicCalmDownBrain(float chanceToCalmDown) {
        super(ImmutableMap.of(
                MemoryModuleType.NEAREST_HOSTILE, MemoryStatus.REGISTERED,
                MemoryModuleType.HURT_BY, MemoryStatus.REGISTERED,
                MemoryModuleType.HURT_BY_ENTITY,MemoryStatus.REGISTERED
        ));
        this.chanceToCalmDown = chanceToCalmDown;
    }

    public static boolean hasHostile(Mob living) {
        return living.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean isHurt(Mob living) {
        return living.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
    }

    public static boolean isHurtByEntity(Mob living) {
        return living.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY);
    }

    @Override
    protected void start(ServerLevel level, T living, long gameTimeIn) {
        Brain<?> brain = living.getBrain();
        boolean hurtOrHostileOrAway = brain.hasMemoryValue(MemoryModuleType.HURT_BY)
                || brain.hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE)
                || brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).filter(entity -> entity.distanceTo(living) < 10 && entity!= living).isPresent();
        // 受到伤害后概率不再恐慌
        if (!hurtOrHostileOrAway || living.getRandom().nextFloat() < getCalmDownChance(living) ) {
            LivingEntity target = brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).orElse(null);
            brain.eraseMemory(MemoryModuleType.PATH);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
//            brain.updateActivityFromSchedule(level.getDayTime(), gameTimeIn);
            brain.setActiveActivityIfPossible(TEAi.Activities.RANGE_ATTACK);
            if(target!= null && target.isAlive() && target != living) {
                this.beforeCalmDown(level, living, brain, target);
                if(hurtByTargetPredicate(target)) {
                    onCalmDown(level, brain, target);
                }
            }
            brain.setMemory(MemoryModuleType.ATTACK_COOLING_DOWN, false);

        }
    }

    protected boolean hurtByTargetPredicate(LivingEntity target) {
        return target.canBeSeenAsEnemy();
    }

    protected void beforeCalmDown(ServerLevel level, T living, Brain<?> brain, LivingEntity target) {

    }

    protected void onCalmDown(ServerLevel level, Brain<?> brain, LivingEntity target) {
        brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
    }

    public float getCalmDownChance(T living){
        return chanceToCalmDown;
    }
}
