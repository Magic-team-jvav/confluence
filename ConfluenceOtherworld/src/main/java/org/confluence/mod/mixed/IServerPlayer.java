package org.confluence.mod.mixed;

import net.minecraft.server.level.ServerPlayer;

public interface IServerPlayer {
    void confluence$setCouldPickupItem(boolean enable);

    boolean confluence$isCouldPickupItem();

    void confluence$bulldozer();

    boolean confluence$chunkPosChanged();

    static IServerPlayer of(ServerPlayer player) {
        return (IServerPlayer) player;
    }
}
