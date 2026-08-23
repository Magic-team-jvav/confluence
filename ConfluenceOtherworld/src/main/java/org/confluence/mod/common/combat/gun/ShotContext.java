package org.confluence.mod.common.combat.gun;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/// Immutable result of resolving a gun and its ammunition into projectile data.
///
/// Keeping the resolved values together prevents gun subclasses from
/// recomputing damage, velocity or penetration independently.
public record ShotContext(ServerPlayer shooter, ItemStack gun, ItemStack ammo, float damage,
                          float knockback, float velocity, int penetrate, float inaccuracy) {
    public ServerLevel level() {
        return shooter.serverLevel();
    }
}
