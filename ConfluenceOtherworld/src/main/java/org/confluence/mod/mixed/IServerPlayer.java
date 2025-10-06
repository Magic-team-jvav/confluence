package org.confluence.mod.mixed;

import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.mixed.SelfGetter;
import org.joml.Vector3f;

public interface IServerPlayer extends SelfGetter<ServerPlayer> {
    void confluence$setCouldPickupItem(boolean enable);

    boolean confluence$isCouldPickupItem();

    void confluence$bulldozer();

    boolean confluence$chunkPosChanged();

    Vector3f confluence$getMovementSpeed();

    static IServerPlayer of(ServerPlayer player) {
        return (IServerPlayer) player;
    }
}
