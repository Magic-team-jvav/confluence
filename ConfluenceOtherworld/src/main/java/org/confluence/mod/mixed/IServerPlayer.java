package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.Objects;

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

    Hash.Strategy<WeakReference<ServerPlayer>> STRATEGY = new Hash.Strategy<>() {
        @Override
        public int hashCode(WeakReference<ServerPlayer> o) {
            return Objects.hashCode(o.get());
        }

        @Override
        public boolean equals(WeakReference<ServerPlayer> o, WeakReference<ServerPlayer> k1) {
            return k1 != null && k1.get() == o.get();
        }
    };
    Object2ObjectLinkedOpenCustomHashMap<WeakReference<ServerPlayer>, Exception> ALL = new Object2ObjectLinkedOpenCustomHashMap<>(STRATEGY){
        @Override
        public boolean trim() {
            keySet().removeIf(reference -> reference.get() == null);
            return super.trim();
        }
    };

    static void track(ServerPlayer player) throws Exception {
        synchronized (ALL) {
            ALL.put(new WeakReference<>(player), new Exception("Tracked at"));
            if (ALL.size() > 50) {
                for (int i = 0; i < 9; i++) {
                    System.out.println("Exception #" + i + ':');
                    ALL.removeLast().printStackTrace();
                }
                System.out.println("Exception #9:");
                throw ALL.removeLast();
            }
        }
    }

    static void remove(ServerPlayer player) {
        synchronized (ALL) {
            ALL.remove(new WeakReference<>(player));
        }
    }
}
