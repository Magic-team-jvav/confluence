package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.entity.ai.brain.behavior.panic.PanicTriggerBrain;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class NPCPanicTriggerBrain<T extends AbstractTerraNPC> extends PanicTriggerBrain<T> {

    @Override
    public boolean shouldPanic(T owner) {
        if(super.shouldPanic(owner)){
            DamageSource hurtBy = owner.getBrain().getMemory(MemoryModuleType.HURT_BY).orElse(null);
            if (hurtBy!= null) {
                Entity target = hurtBy.getEntity();
                if(target instanceof Player || target instanceof AbstractTerraNPC){
                    if(owner.getRandom().nextFloat() < 0.8f) {
                        // 概率看向敌人
                        owner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target.getEyePosition()));
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
