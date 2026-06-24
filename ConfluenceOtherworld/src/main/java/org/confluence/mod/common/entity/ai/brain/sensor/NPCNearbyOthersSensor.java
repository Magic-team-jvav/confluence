package org.confluence.mod.common.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.npc.AbstractTerraNPC;
import org.confluence.mod.common.init.TEAi;

import java.util.List;
import java.util.Set;

/**
 * 感知周围的npc，同时设置和同步心情值
 */
public class NPCNearbyOthersSensor extends Sensor<AbstractTerraNPC> {

    int range;
    public NPCNearbyOthersSensor(int range) {
        this.range = range;
    }

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.HOME, TEAi.MemoryModules.NEARBY_NPC.get());
    }

    protected void doTick(ServerLevel level, AbstractTerraNPC entity) {
        List<AbstractTerraNPC> nearbyEntities = this.getNearestEntity(entity, level);
        entity.getBrain().setMemory(TEAi.MemoryModules.NEARBY_NPC.get(), nearbyEntities);
        entity.getMood().evaluate(nearbyEntities);
        entity.syncMood();
    }

    protected List<AbstractTerraNPC> getNearestEntity(AbstractTerraNPC entity, ServerLevel level) {
        var home = entity.getBrain().getMemory(MemoryModuleType.HOME);

        if (home.isPresent()) {
            BlockPos pos = home.get().pos();
            return level.getEntitiesOfClass(AbstractTerraNPC.class, new AABB(pos.offset(-range, -range, -range).getCenter(), pos.offset(range, range, range).getCenter()));
        }else{
            var opt = entity.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
            if (opt.isPresent()) {
                return opt.get().stream().filter(target -> target instanceof AbstractTerraNPC).map(target -> (AbstractTerraNPC) target).toList();
            }else{
                return List.of();
            }
        }


    }

}
