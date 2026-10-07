package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/// 一次开火已计算完成的射弹参数，供创建事件与射弹工厂共用。
public record ShotContext(ServerPlayer shooter, ItemStack gun, ItemStack ammo, float damage,
                          float knockback, float velocity, int penetrate, float inaccuracy) {
    public ServerLevel level() {
        return shooter.serverLevel();
    }
}
