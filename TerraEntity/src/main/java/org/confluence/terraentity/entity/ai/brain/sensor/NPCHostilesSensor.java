package org.confluence.terraentity.entity.ai.brain.sensor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class NPCHostilesSensor<T extends AbstractTerraNPC> extends TENearestVisibleLivingEntitySensor<T> {

    public NPCHostilesSensor(float range) {
        super(range);
    }

    protected boolean isMatchingEntity(T owner, LivingEntity target) {
        return this.isHostile(target) && this.isClose(owner, target);
    }


    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    private boolean isHostile(LivingEntity entity) {
        return entity instanceof Enemy ;
    }

}
