package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.TEAi;

import java.util.Map;

public class NurseAttackTriggerBrain<T extends AbstractTerraNPC> extends NPCAttackTriggerBrain<T> {

    public NurseAttackTriggerBrain() {
        super(Map.of(TEAi.MemoryModules.NEAREST_VISIBLE_ALLIANCE_NURSE_TARGET.get(), MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T owner) {
//        if(owner.getHealth() / owner.getMaxHealth() < 0.8f){
//            return true;
//        }
//        var memory = owner.getBrain().getMemory(TEAi.MemoryModules.NEAREST_VISIBLE_ALLIANCE_NURSE_TARGET.get());
//        if(memory.isPresent()){
//            LivingEntity living = memory.get();
//            return living.getHealth() / living.getMaxHealth() < 0.33f;
//        }
//        return false;
        return super.checkExtraStartConditions(level, owner);
    }

    protected MemoryModuleType<LivingEntity> targetMemoryType() {
        return TEAi.MemoryModules.NEAREST_VISIBLE_ALLIANCE_NURSE_TARGET.get();
    }

}
