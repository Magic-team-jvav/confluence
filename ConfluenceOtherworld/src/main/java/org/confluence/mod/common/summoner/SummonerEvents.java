package org.confluence.mod.common.summoner;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import org.confluence.mod.common.summoner.attachment.InvincibleData;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.tick.PortEntityTickEvent;

public final class SummonerEvents {
    private SummonerEvents() {
    }

    public static void init() {
        PortEventHandler.addListener(SummonerEvents::playerTick);
        PortEventHandler.addListener(SummonerEvents::entityTick);
        PortEventHandler.addListener(InvincibleData::handler);
    }

    private static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player instanceof ServerPlayer serverPlayer) {
            TargetCache targetCache = serverPlayer.getData(SummonerAttachmentTypes.TARGET_CACHE);
            targetCache.tick(serverPlayer);
        }
        SummonerHelper.get(player).getEntityData().tick(player);
    }

    private static void entityTick(PortEntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity living) {
            InvincibleData.get(living).tick();
        }
    }
}
