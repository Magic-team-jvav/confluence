package org.confluence.mod.common.event.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.TaskScheduler;
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
import org.confluence.mod.common.worldgen.secret_seed.TooEasy;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.tick.PortEntityTickEvent;
import org.mesdag.portlib.event.tick.PortLevelTickEvent;
import org.mesdag.portlib.event.tick.PortPlayerTickEvent;
import org.mesdag.portlib.event.tick.PortServerTickEvent;

public final class TickEvents {
    public static void init() {
        PortEventHandler.addListener(TickEvents::levelTick$Post);
        PortEventHandler.addListener(TickEvents::playerTick$Post);
        PortEventHandler.addListener(TickEvents::entityTick$Post);
        PortEventHandler.addListener(TickEvents::serverTick$Post);
    }

    public static void levelTick$Post(PortLevelTickEvent.PortPost event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != OverworldUtils.dimension()) {
            return;
        }
        GameEventSystem.INSTANCE.tick(); // 最高优先级，其会影响BossDelaySpawner、NPCSpawner等内容
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

        TaskScheduler scheduler = TooEasy.getScheduler(false);
        if (scheduler != null) {
            scheduler.tick(1);
        }
        HardmodeConvertor.INSTANCE.scheduleRefill(level);
    }

    public static void playerTick$Post(PortPlayerTickEvent.PortPost event) {
        Player entity = event.getEntity();
        long gameTime = entity.level().getGameTime();
        if (entity instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            IServerPlayer.of(player).confluence$setCouldPickupItem(true);
            PlayerUtils.regenerateMana(player);
            ExtraInventory.of(player).sync(player);
            PlayerSpecialData.of(player).sync(player);
            AchievementUtils.youCanDoIt(player, level, gameTime);
            AchievementUtils.quietNeighborhood(player, level, gameTime);
            AchievementUtils.aRareRealm(player, gameTime);
            TheConstant.applyDarkness(player, level, gameTime);
            TheConstant.instantlyDieWhenHasNoFoodLevel(player);
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

    public static void entityTick$Post(PortEntityTickEvent.PortPost event) {
        Immunity.tick(event.getEntity());
    }

    public static void serverTick$Post(PortServerTickEvent.PortPost event) {
        PathService.INSTANCE.pathFindingTick();
    }
}
