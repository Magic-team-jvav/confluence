package org.confluence.mod.common.event.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.entity.FallingStarItemEntity;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.util.PlayerUtils;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class TickEvents {
    @SubscribeEvent
    public static void levelTick$Post(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        PathService.INSTANCE.pathFindingTick();
        if (serverLevel.dimension() != Level.OVERWORLD) return;
        FallingStarItemEntity.summon(serverLevel);
    }

    @SubscribeEvent
    public static void playerTick$Post(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerUtils.regenerateMana(serverPlayer);
            ((IServerPlayer) serverPlayer).confluence$setCouldPickupItem(true);
        }
    }
}
