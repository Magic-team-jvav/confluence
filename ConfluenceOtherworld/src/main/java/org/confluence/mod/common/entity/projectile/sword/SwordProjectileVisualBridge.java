package org.confluence.mod.common.entity.projectile.sword;

import java.util.Objects;

/// 隔离通用剑气实体与客户端视觉实现。
public final class SwordProjectileVisualBridge {
    private static Handler handler = Handler.NONE;

    public static void install(Handler handler) {
        SwordProjectileVisualBridge.handler = Objects.requireNonNull(handler, "handler");
    }

    public static void tick(SwordProjectile projectile) {
        handler.tick(projectile);
    }

    public static void entityHit(SwordProjectile projectile) {
        handler.entityHit(projectile);
    }

    public static void blockHit(SwordProjectile projectile) {
        handler.blockHit(projectile);
    }

    public interface Handler {
        Handler NONE = new Handler() {};

        default void tick(SwordProjectile projectile) {}

        default void entityHit(SwordProjectile projectile) {}

        default void blockHit(SwordProjectile projectile) {}
    }

    private SwordProjectileVisualBridge() {}
}
