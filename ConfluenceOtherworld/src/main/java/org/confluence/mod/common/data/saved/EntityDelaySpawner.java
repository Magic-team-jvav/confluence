package org.confluence.mod.common.data.saved;

import com.google.common.collect.EvictingQueue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.util.ModUtils;

import java.util.List;
import java.util.Queue;

public class EntityDelaySpawner {
    public static final EntityDelaySpawner INSTANCE = new EntityDelaySpawner();
    private final Queue<Entry<Mob>> bossQueue = EvictingQueue.create(1);

    public void tick(ServerLevel serverLevel) {
        if (!bossQueue.isEmpty()) {
            Entry<Mob> entry = bossQueue.peek();
            if (entry.delay-- <= 0) {
                List<ServerPlayer> players = serverLevel.players();
                if (!players.isEmpty()) {
                    ModUtils.summonBoss(serverLevel, players.stream().findAny().get().position(), entry.entity);
                    bossQueue.remove();
                }
            }
        }
    }

    public void pushBoss(int delay, Mob entity) {
        if (bossQueue.isEmpty()) {
            bossQueue.add(new Entry<>(delay, entity));
        }
    }

    static class Entry<E extends Entity> {
        int delay;
        final E entity;

        Entry(int delay, E entity) {
            this.delay = delay;
            this.entity = entity;
        }
    }
}
