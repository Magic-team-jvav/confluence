package org.confluence.mod.common.data.saved;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class BossDelaySpawner {
    public static final BossDelaySpawner INSTANCE = new BossDelaySpawner();
    private final List<Delayed<AbstractTerraBossBase<?>>> bossQueue = new ArrayList<>();

    public void tick(ServerLevel serverLevel) {
        if (!bossQueue.isEmpty()) {
            bossQueue.removeIf(mobDelayed -> {
                if (mobDelayed.delay-- <= 0 && mobDelayed.predicate.test(serverLevel)) {
                    serverLevel.players().stream().filter(player -> player.getY() > OverworldUtils.getSurfaceY()).findAny().ifPresentOrElse(player -> {
                        ModUtils.summonBoss(serverLevel, player.blockPosition(), mobDelayed.entity);
                    }, () -> mobDelayed.delay = 20);
                    return mobDelayed.delay <= 0;
                }
                return false;
            });
        }
    }

    public void pushBoss(int delay, AbstractTerraBossBase<?> boss, Predicate<ServerLevel> predicate) {
        if (bossQueue.size() == 8) bossQueue.removeFirst();
        bossQueue.add(new Delayed<>(delay, boss, predicate));
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
