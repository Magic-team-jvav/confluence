package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.confluence.terraentity.entity.ai.brain.behavior.range.RangeAttackOnCooldownBrain;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class NPCRangeAttackOnCooldownBrain<T extends AbstractTerraNPC> extends RangeAttackOnCooldownBrain<T> {
    public NPCRangeAttackOnCooldownBrain(int cooldownTime, float attackRange, float speedModifier) {
        super(cooldownTime, attackRange, speedModifier);
    }

    @Override
    protected void start(ServerLevel level, T entity, long gameTimeIn) {

        super.start(level, entity, gameTimeIn);

        entity.getBrain().getMemory(MemoryModuleType.ATTACK_COOLING_DOWN).ifPresent(
                cd->entity.setCooledDown(cd)
        );
    }

    @Override
    protected void stop(ServerLevel level,T entity, long gameTimeIn) {
        super.stop(level, entity, gameTimeIn);
        entity.getBrain().getMemory(MemoryModuleType.ATTACK_COOLING_DOWN).ifPresent(
                cd->entity.setCooledDown(false)
        );
        entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(
                target->entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target.getEyePosition()))
        );
    }

}
