package org.confluence.mod.util.entity.ai.brain.behavior.range;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.confluence.mod.common.init.TEAi;

import java.util.Map;
import java.util.function.Function;

/**
 * 触发攻击行为
 */
public class AttackTriggerBrain<T extends LivingEntity> extends Behavior<T> {
    float detectDistance;


    /**
     * 用于其他的攻击目标触发条件，比如护士的攻击目标是朋友，而不是敌对生物
     * @param detectDistance 检测距离
     */
    public AttackTriggerBrain(Map<MemoryModuleType<?>, MemoryStatus> entryCondition, float detectDistance) {
        super(entryCondition);

        this.detectDistance = detectDistance;
    }

    /**
     * 用于添加额外条件
     * @param modifier 用于修改MemoryModuleType
     * @param detectDistance 检测距离
     */
    public AttackTriggerBrain(Function<ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus>, ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus>> modifier, float detectDistance) {
        super(modifier.apply(
                ImmutableMap.<MemoryModuleType<?>, MemoryStatus>builder()
                        .put(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
        ).build());

        this.detectDistance = detectDistance;
    }

    /**
     * 默认攻击敌对生物
     * @param detectDistance 检测距离
     */
    public AttackTriggerBrain(float detectDistance) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.NEAREST_HOSTILE, MemoryStatus.VALUE_PRESENT
        ));
        this.detectDistance = detectDistance;
    }

    @Override
    protected void start(ServerLevel level, T living, long gameTimeIn) {
        Brain<?> brain = living.getBrain();
        var memory = brain.getMemory(targetMemoryType());
        if(memory.isPresent()) {
            LivingEntity target = memory.get();
            boolean shouldAdd = shouldAttack(target, living);
            if (shouldAdd) {
                this.onTrigger(level, living, gameTimeIn, target);
            }
        }
    }

    protected boolean shouldAttack(LivingEntity target, T living) {
        return target.canBeSeenAsEnemy() &&  target.distanceToSqr(living) < getDetectDistanceSqr(living) && target.isAlive();
    }

    protected MemoryModuleType<LivingEntity> targetMemoryType() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    protected float getDetectDistanceSqr(T living) {
        return detectDistance * detectDistance;
    }

    protected void onTrigger(ServerLevel level, T living, long gameTime, LivingEntity target) {
        Brain<?> brain = living.getBrain();
        brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        brain.setActiveActivityIfPossible(TEAi.Activities.RANGE_ATTACK);
        brain.setMemory(MemoryModuleType.ATTACK_COOLING_DOWN, false); // 触发攻击时冷却直接完毕，防止冷却memory不存在导致卡在攻击检查状态
    }
}
