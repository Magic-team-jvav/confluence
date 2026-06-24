package org.confluence.mod.common.entity.ai.brain.sensor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.confluence.mod.common.entity.npc.AbstractTerraNPC;
import org.confluence.mod.common.init.TEAi;

public class NPCNearestVisibleAllianceSensor<T extends AbstractTerraNPC> extends TENearestVisibleLivingEntitySensor<T> {

    public NPCNearestVisibleAllianceSensor(float range) {
        super(range);
    }

    protected boolean isMatchingEntity(T attacker, LivingEntity target) {
        return attacker.isAllianceTo(target) && this.isClose(attacker, target);
    }

    protected MemoryModuleType<LivingEntity> getMemory() {
        return TEAi.MemoryModules.NEAREST_VISIBLE_ALLIANCE.get();
    }

}
