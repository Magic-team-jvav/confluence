package org.confluence.terraentity.entity.npc.brain.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.confluence.terraentity.entity.ai.brain.behavior.range.AttackTriggerBrain;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

import java.util.Map;
import java.util.function.Function;

public class NPCAttackTriggerBrain<T extends AbstractTerraNPC> extends AttackTriggerBrain<T> {

    public NPCAttackTriggerBrain(Map<MemoryModuleType<?>, MemoryStatus> entryCondition) {
        super(entryCondition, 0);
    }

    public NPCAttackTriggerBrain(Function<ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus>, ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus>> modifier) {
        super(modifier, 0);
    }

    public NPCAttackTriggerBrain() {
        super(0); // npc使用自带的攻击距离
    }

    protected boolean shouldAttack(LivingEntity target, T living) {
        return target.canBeSeenAsEnemy() &&  target.distanceToSqr(living) < getDetectDistanceSqr(living) && target.isAlive();
    }

    protected boolean checkExtraStartConditions(ServerLevel level, T owner) {
        return owner.canPerformerAttack();
    }


    protected float getDetectDistanceSqr(T living) {
        float r = living.getAttackRange() + 5;// +5额外侦测距离
        return r * r;
    }


}
