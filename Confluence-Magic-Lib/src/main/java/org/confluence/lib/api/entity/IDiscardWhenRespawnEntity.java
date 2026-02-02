package org.confluence.lib.api.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/// 当玩家重生时，若附近没有存活的玩家，实现该接口的实体会消失，以实现出生保护
public interface IDiscardWhenRespawnEntity {
    default boolean shouldDiscard(boolean hasNearbyPlayer) {
        return true;
    }

    static void process(ServerPlayer player) {
        List<Entity> entities = player.level().getEntities(player, player.getBoundingBox().inflate(32));
        boolean hasPlayer = entities.stream().anyMatch(e -> e instanceof Player);
        entities.stream().filter(e -> e instanceof IDiscardWhenRespawnEntity entity && entity.shouldDiscard(hasPlayer)).forEach(Entity::discard);
    }
}
