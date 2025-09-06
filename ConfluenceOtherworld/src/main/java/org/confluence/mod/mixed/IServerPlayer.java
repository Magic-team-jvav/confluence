package org.confluence.mod.mixed;

import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.mixed.SelfGetter;

public interface IServerPlayer extends SelfGetter<ServerPlayer> {
    void confluence$setCouldPickupItem(boolean enable);

    boolean confluence$isCouldPickupItem();

    void confluence$bulldozer();

    boolean confluence$chunkPosChanged();

    void confluence$setCouldHurtCritter(boolean could);

    boolean confluence$isCouldHurtCritter();

    static IServerPlayer of(ServerPlayer player) {
        return (IServerPlayer) player;
    }
}
