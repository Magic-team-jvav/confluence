package org.confluence.mod.mixed;

import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface IServerPlayer extends SelfGetter<ServerPlayer> {
    void confluence$setCouldPickupItem(boolean enable);

    boolean confluence$isCouldPickupItem();

    void confluence$bulldozer();

    boolean confluence$chunkPosChanged();

    Vector3f confluence$getMovementSpeed();

    void confluence$setTitaniumShards(@Nullable TitaniumShardsProjectile projectile);

    boolean confluence$hasTitaniumShards();

    static IServerPlayer of(ServerPlayer player) {
        return (IServerPlayer) player;
    }
}
