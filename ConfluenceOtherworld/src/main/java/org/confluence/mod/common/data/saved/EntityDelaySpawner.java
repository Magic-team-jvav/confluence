package org.confluence.mod.common.data.saved;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.ModUtils;

import java.util.ArrayList;
import java.util.List;

public class EntityDelaySpawner {
    public static final EntityDelaySpawner INSTANCE = new EntityDelaySpawner();
    private final List<Delayed<Mob>> bossQueue = new ArrayList<>();

    public void tick(ServerLevel serverLevel) {
        if (!bossQueue.isEmpty()) {
            bossQueue.removeIf(mobDelayed -> {
                if (mobDelayed.delay-- <= 0) {
                    Vec3 vec3 = serverLevel.players().stream().findAny().map(Entity::position)
                            .orElseGet(serverLevel.getLevelData().getSpawnPos()::getCenter);
                    ModUtils.summonBoss(serverLevel, vec3, mobDelayed.entity);
                    return true;
                }
                return false;
            });
        }
    }

    public void pushBoss(int delay, Mob entity) {
        if (bossQueue.size() == 8) bossQueue.removeFirst();
        bossQueue.add(new Delayed<>(delay, entity));
    }

    public void clear() {
        bossQueue.clear();
    }

    static class Delayed<E extends Entity> {
        int delay;
        final E entity;

        Delayed(int delay, E entity) {
            this.delay = delay;
            this.entity = entity;
        }
    }
}
