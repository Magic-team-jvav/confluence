package org.confluence.mod.common.combat.gun;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/// 一次射击解析完成后的不可变服务端快照。
public record ShotContext(ServerPlayer shooter, ItemStack gun, ItemStack ammo, float damage,
                          float knockback, float velocity, int penetrate, float inaccuracy) {
    public ServerLevel level() {
        return shooter.serverLevel();
    }
}
