package com.xiaohunao.enemybanner.banner;


import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class LootBannerBehavior extends BasicBannerBehavior{
    public static final Logger LOGGER = LogUtils.getLogger();


    @Override
    public void onPlayerKillLivingEntity(Player player, LivingEntity livingEntity) {
        super.onPlayerKillLivingEntity(player, livingEntity);
    }
}
