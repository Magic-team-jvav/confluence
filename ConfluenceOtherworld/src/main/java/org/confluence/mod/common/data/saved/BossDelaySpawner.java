package org.confluence.mod.common.data.saved;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.ModUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BossDelaySpawner {
    public static final BossDelaySpawner INSTANCE = new BossDelaySpawner();
    private final List<Delayed<Mob>> bossQueue = new ArrayList<>();

    public void tick(ServerLevel serverLevel) {
        if (!bossQueue.isEmpty()) {
            bossQueue.removeIf(mobDelayed -> {
                if (mobDelayed.delay-- <= 0 && mobDelayed.predicate.test(serverLevel)) {
                    Vec3 vec3 = serverLevel.players().stream().findAny().map(Entity::position)
                            .orElseGet(serverLevel.getLevelData().getSpawnPos()::getCenter);
                    ModUtils.summonBoss(serverLevel, vec3, mobDelayed.entity);
                    return true;
                }
                return false;
            });
        }
    }

    public void pushBoss(int delay, Mob entity, Predicate<ServerLevel> predicate) {
        if (bossQueue.size() == 8) bossQueue.removeFirst();
        bossQueue.add(new Delayed<>(delay, entity, predicate));
    }

    public void clear() {
        bossQueue.clear();
    }

    static class Delayed<E extends Entity> {
        int delay;
        final E entity;
        final Predicate<ServerLevel> predicate;

        Delayed(int delay, E entity, Predicate<ServerLevel> predicate) {
            this.delay = delay;
            this.entity = entity;
            this.predicate = predicate;
        }
    }
}
