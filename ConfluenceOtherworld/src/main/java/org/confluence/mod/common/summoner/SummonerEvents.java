package org.confluence.mod.common.summoner;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.entity.living.PortLivingDamageEvent;
import org.mesdag.portlib.event.tick.PortPlayerTickEvent;

public final class SummonerEvents {

    public static void init() {
        PortEventHandler.addListener((PortPlayerTickEvent.Post event) -> {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getData(SummonerAttachmentTypes.TARGET_CACHE).tick(serverPlayer);
            }
            SummonerHelper.get(player).getEntityData().tick(player);
        });
        PortEventHandler.addListener((PortLivingDamageEvent.Post event) -> {
            LivingEntity target = event.getEntity();
            if (event.getSource().getEntity() instanceof LivingEntity attacker && target != attacker) {
                if (target instanceof Player player) {
                    player.getData(SummonerAttachmentTypes.TARGET_CACHE).record(attacker, 200);
                } else if (attacker instanceof Player player) {
                    player.getData(SummonerAttachmentTypes.TARGET_CACHE).record(target, 200);
                }
            }
        });
    }
}
