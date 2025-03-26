package org.confluence.mod.common.data.saved;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
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
                    List<ServerPlayer> players = serverLevel.players();
                    if (!players.isEmpty()) {
                        ModUtils.summonBoss(serverLevel, players.stream().findAny().get().position(), mobDelayed.entity);
                        return true;
                    }
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
