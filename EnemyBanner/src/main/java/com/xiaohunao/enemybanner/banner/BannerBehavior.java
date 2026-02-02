package com.xiaohunao.enemybanner.banner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

public interface BannerBehavior {

    int getRange();

    void onTick(Level level, BlockPos blockPos, int tickCount);

    void onEntityDamage(DamageContainer damageContainer, Entity entity, Player player);

    void onPlayerDamage(DamageContainer damageContainer, Entity entity, Player player);

    void onPlayerKillLivingEntity(Player player, LivingEntity livingEntity);
}
