package org.confluence.mod.common.event.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.TaskScheduler;
import org.confluence.mod.common.attachment.ChunkDropletsData;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.attachment.YoyoSession;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.HardmodeConvertor;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.data.spawner.BossDelaySpawner;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.effect.harmful.DriveAwayController;
import org.confluence.mod.common.entity.FallingStarItemEntity;
import org.confluence.mod.common.entity.mount.MountManager;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.item.axe.LucyTheAxe;
import org.confluence.mod.common.item.fishing.AbstractFishingPole;
import org.confluence.mod.common.item.whip.WhipSession;
import org.confluence.mod.common.util.VoidSeaHelper;
import org.confluence.mod.common.wind.WindState;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.common.worldgen.secret_seed.TooEasy;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.network.s2c.WindSpeedPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.tick.PortEntityTickEvent;

public final class TickEvents {
    /// 局地风向的同步间隔（tick）。载荷只有 3 个 float，4 包/秒/玩家。
    private static final int WIND_SYNC_INTERVAL = 5;

    public static void init() {
        PortEventHandler.addListener(TickEvents::levelTick$Post);
        PortEventHandler.addListener(TickEvents::playerTick$Post);
        PortEventHandler.addListener(TickEvents::entityTick$Post);
        PortEventHandler.addListener(TickEvents::serverTick$Post);
    }

    private static void levelTick$Post(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level.dimension() == Level.END) {
            /// 客户端和服务端按各自世界时间更新潮位。
            VoidSeaHelper.tick(event.level);
        }
        if (!(event.level instanceof ServerLevel level) || level.dimension() != OverworldUtils.dimension()) {
            return;
        }
        GameEventSystem.INSTANCE.tick(); // 最高优先级，其会影响BossDelaySpawner、NPCSpawner等内容
        FallingStarItemEntity.summon(level);
        MeteoriteTracker.INSTANCE.tick(level);
        BossDelaySpawner.INSTANCE.tick(level);

        // 风：风速全局连续演化，方向随位置变化（WindField 按温度噪声梯度求值，只能服务端算）。
        // 不需要 setDirty：WindState 是 IGlobalData，写进 level.dat，而 level.dat 在每次自动保存时
        // 由 saveAllChunks 无条件重写（dirty 门控只属于 SavedData，与它无关）。
        WindState.INSTANCE.tick(level);
        if (level.getGameTime() % WIND_SYNC_INTERVAL == 0) {
            WindSpeedPacketS2C.sendToAll(level); // 局地风向逐玩家下发，约 4 包/秒
        }

        int dayTime = LibDateUtils.getDayTime(level);
        if (dayTime == LibDateUtils._19$30) {
            BossDelaySpawner.spawnEyeOfCthulhu(level);
            MeteoriteTracker.spawnMeteor(level);
        } else if (dayTime == LibDateUtils._00$00) {
            BossDelaySpawner.spawnDeerClops(level);
        }
        NPCSpawner.respawnNPC(level, dayTime);
        NPCSpawner.INSTANCE.tickDungeonResidents(level);

        TaskScheduler scheduler = TooEasy.getScheduler(false);
        if (scheduler != null) {
            scheduler.tick(1);
        }
        HardmodeConvertor.INSTANCE.scheduleRefill(level);
    }

    private static void playerTick$Post(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        long gameTime = event.player.level().getGameTime();
        if (event.player instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            IServerPlayer.of(player).confluence$setCouldPickupItem(true);
            PlayerUtils.regenerateMana(player);
            ExtraInventory.of(player).sync(player);
            MountManager.validate(player);
            YoyoSession.of(player).tick(player);
            PlayerSpecialData.of(player).sync(player);
            AchievementUtils.youCanDoIt(player, level, gameTime);
            AchievementUtils.quietNeighborhood(player, level, gameTime);
            AchievementUtils.aRareRealm(player, gameTime);
            if (gameTime % 20 == player.getId() % 10) {
                AchievementUtils.marathonMedalist(player);
            }
            TheConstant.applyDarkness(player, level, gameTime);
            TheConstant.instantlyDieWhenHasNoFoodLevel(player);
            DungeonStructure.checkSkeletronDefeated(player, level);
            ChunkDropletsData.syncDroplets(player);
            ModArmorBonus.afterTick(player, gameTime);
            PlayerUtils.applySunflowerEffect(player, level, gameTime);
            LucyTheAxe.onIdle(player, gameTime);
        }

        if (gameTime % 60 == 3) {
            AbstractFishingPole.resetCurrentBait(event.player);
            PlayerSpecialData.resetSomeData(event.player);
        }
    }

    private static void entityTick$Post(PortEntityTickEvent.Post event) {
        Immunity.tick(event.getEntity());
        // 平时（含大风天）第一行就返回：风不影响实体，只有沙尘暴/暴风雪激活时才会走到下面
        GameEventSystem.INSTANCE.pushEventWind(event.getEntity());
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof net.minecraft.world.entity.Mob mob) {
            DriveAwayController.tick(mob);
        }
    }

    private static void serverTick$Post(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        PathService.INSTANCE.pathFindingTick();
        WhipSession.handle();
    }
}
