package org.confluence.mod.common.event.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.common.attachment.ChunkDropletsData;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.*;
import org.confluence.mod.common.entity.FallingStarItemEntity;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.item.axe.LucyTheAxe;
import org.confluence.mod.common.item.fishing.AbstractFishingPole;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.util.PlayerUtils;

@EventBusSubscriber(modid = Confluence.MODID)
public final class TickEvents {
    @SubscribeEvent
    public static void levelTick$Post(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide) {
            WeatherHandler.tick();
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != OverworldUtils.dimension()) {
            return;
        }
        FallingStarItemEntity.summon(level);
        MeteoriteTracker.INSTANCE.tick(level);
        BossDelaySpawner.INSTANCE.tick(level);

        int dayTime = LibDateUtils.getDayTime(level);
        if (dayTime == LibDateUtils._06$00) {
            ConfluenceData.updateWind(level);
        } else if (dayTime == LibDateUtils._19$30) {
            BossDelaySpawner.spawnEyeOfCthulhu(level);
            MeteoriteTracker.spawnMeteor(level);
        } else if (dayTime == LibDateUtils._00$00) {
            BossDelaySpawner.spawnDeerClops(level);
        }
        NPCSpawner.respawnNPC(level, dayTime);

        HardmodeConvertor.INSTANCE.scheduleRefill(level);
        GameEventSystem.INSTANCE.tick();
    }

    @SubscribeEvent
    public static void playerTick$Post(PlayerTickEvent.Post event) {
        Player entity = event.getEntity();
        long gameTime = entity.level().getGameTime();
        if (entity instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            IServerPlayer.of(player).confluence$setCouldPickupItem(true);
            PlayerUtils.regenerateMana(player);
            ExtraInventory.of(player).sync(player);
            AchievementUtils.youCanDoIt(player, level, gameTime);
            AchievementUtils.quietNeighborhood(player, level, gameTime);
            AchievementUtils.aRareRealm(player, level, gameTime);
            TheConstant.applyDarkness(player, level, gameTime);
            DungeonStructure.checkSkeletronDefeated(player, level);
            ChunkDropletsData.syncDroplets(player);
            ModArmorBonus.afterTick(player, gameTime);
            PlayerUtils.applySunflowerEffect(player, level, gameTime);
            LucyTheAxe.onIdle(player, gameTime);
        }

        if (gameTime % 60 == 3) {
            AbstractFishingPole.resetCurrentBait(entity);
            PlayerSpecialData.resetSomeData(entity);
        }
    }

    @SubscribeEvent
    public static void entityTick$Post(EntityTickEvent.Post event) {
        Immunity.tick(event.getEntity());
    }

    @SubscribeEvent
    public static void serverTick$Post(ServerTickEvent.Post event) {
        PathService.INSTANCE.pathFindingTick();
    }
}
