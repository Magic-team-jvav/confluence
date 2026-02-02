package org.confluence.terraentity.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Optional;
import java.util.Set;

public abstract class TENearestVisibleLivingEntitySensor<T extends LivingEntity> extends Sensor<T> {

    float range;
    public TENearestVisibleLivingEntitySensor(float range) {
        this.range = range;
    }

    protected abstract boolean isMatchingEntity(T owner, LivingEntity target);

    protected boolean isClose(T owner, LivingEntity target) {
        return target.distanceToSqr(owner) <= (double)(getRangeSqr(owner) );
    }

    protected float getRangeSqr(T owner) {
        return range * range;
    }

    protected abstract MemoryModuleType<LivingEntity> getMemory();

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(this.getMemory());
    }

    protected void doTick(ServerLevel level, T entity) {
        entity.getBrain().setMemory(this.getMemory(), this.getNearestEntity(entity));
    }

    protected Optional<LivingEntity> getNearestEntity(T entity) {
        return this.getVisibleEntities(entity).flatMap(
                (entities) -> entities.findClosest(
                        (entity1) -> this.isMatchingEntity(entity, entity1)
                )
        );
    }

    protected Optional<NearestVisibleLivingEntities> getVisibleEntities(T entity) {
        return entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}
