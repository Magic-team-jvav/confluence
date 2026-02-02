package com.xiaohunao.enemybanner.handler;

import com.xiaohunao.enemybanner.AttachmentTypeRegister;
import com.xiaohunao.enemybanner.BannerConfig;
import com.xiaohunao.enemybanner.EnemyBanner;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = EnemyBanner.MODID)
public class ServerEventHandler {
    @SubscribeEvent
    public static void onPlayKillMob(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if (living.getType().is(EnemyBanner.DENIED_ENTITIES)) return; // excludedList不如tag好用
        // 玩家击杀生物获得的相应的旗帜点数
        if (event.getSource() != null && event.getSource().getEntity() instanceof Player player) {
            String key = EntityType.getKey(living.getType()).toString();
            if (BannerConfig.contains(key)) {
                Object2IntOpenHashMap<String> playerData = player.getData(AttachmentTypeRegister.PLAYER_BANNER_COUNT);
                int nextValue = playerData.addTo(key, 1) + 1;
                int basicKills = BannerConfig.getBasicKills(key);

                if (nextValue % basicKills == 0) {
                    String k;
                    if (nextValue == basicKills) {
                        //“<玩家名>击败了<X>个<生物名>，该旗帜已在旗帜盒中解锁”
                        k = EnemyBanner.asDescriptionId("message.player.killcount");
                    } else {
                        //“<玩家名>击败了<X>个<生物名>”
                        k = EnemyBanner.asDescriptionId("message.player.killcount.short");
                    }
                    player.sendSystemMessage(Component.translatable(k, player.getName(), nextValue, living.getName()).withColor(0xfff014));
                }
            }
        }
    }
}
