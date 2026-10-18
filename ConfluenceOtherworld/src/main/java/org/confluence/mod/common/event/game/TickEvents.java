package org.confluence.mod.common.event.game;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.TaskScheduler;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.BossDelaySpawner;
import org.confluence.mod.common.data.saved.EnvironmentalMachines;
import org.confluence.mod.common.data.saved.HardmodeConvertor;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.data.spawner.NPCSpawner;
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

@EventBusSubscriber(modid = Confluence.MODID)
public final class TickEvents {
    /// 局地风向的同步间隔（tick）。载荷只有 3 个 float，4 包/秒/玩家。
    private static final int WIND_SYNC_INTERVAL = 5;

    @SubscribeEvent
    public static void levelTick$Post(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level instanceof ServerLevel machineLevel)
            EnvironmentalMachines.INSTANCE.tick(machineLevel);
        ResourceKey<Level> dimensionKey = level.dimension();
        if (dimensionKey == Level.END) {
            // 让客户端也自己计算
            VoidSeaHelper.tick(level);
        }
        if (!(level instanceof ServerLevel serverLevel)
                || dimensionKey != OverworldUtils.dimension()) {
            return;
        }
        GameEventSystem.INSTANCE.tick(); // 最高优先级，其会影响BossDelaySpawner、NPCSpawner等内容
        FallingStarItemEntity.summon(serverLevel);
        MeteoriteTracker.INSTANCE.tick(serverLevel);
        BossDelaySpawner.INSTANCE.tick(serverLevel);

        // 风：风速全局连续演化，方向随位置变化（WindField 按温度噪声梯度求值，只能服务端算）。
        // 不需要 setDirty：WindState 是 IGlobalData，写进 level.dat，而 level.dat 在每次自动保存时
        // 由 saveAllChunks 无条件重写（dirty 门控只属于 SavedData，与它无关）。
        WindState.INSTANCE.tick(serverLevel);
        if (serverLevel.getGameTime() % WIND_SYNC_INTERVAL == 0) {
            WindSpeedPacketS2C.sendToAll(serverLevel); // 局地风向逐玩家下发，约 4 包/秒
        }

        int dayTime = LibDateUtils.getDayTime(serverLevel);
        if (dayTime == LibDateUtils._19$30) {
            BossDelaySpawner.spawnEyeOfCthulhu(serverLevel);
            MeteoriteTracker.spawnMeteor(serverLevel);
        } else if (dayTime == LibDateUtils._00$00) {
            BossDelaySpawner.spawnDeerClops(serverLevel);
        }
        NPCSpawner.respawnNPC(serverLevel, dayTime);

        TaskScheduler scheduler = TooEasy.getScheduler(false);
        if (scheduler != null) {
            scheduler.tick(1);
        }
        HardmodeConvertor.INSTANCE.scheduleRefill(serverLevel);
    }

    @SubscribeEvent
    public static void playerTick$Post(PlayerTickEvent.Post event) {
        Player entity = event.getEntity();
        long gameTime = entity.level().getGameTime();
        if (entity instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            IServerPlayer.of(player).confluence$setCouldPickupItem(true);
            PlayerUtils.regenerateMana(player);
            PlayerUtils.learnSoulSkillsOnFirstObtain(player);
            PlayerUtils.tickSoulSkills(player);
            PlayerUtils.regenerateSoul(player);
            PlayerUtils.tickSoulArmorSets(player, gameTime);
            ExtraInventory.of(player).sync(player);
            MountManager.validate(player);
            YoyoSession.of(player).tick(player);
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
        PlayerVoidVaultContainer.tickContents(entity);
        if (entity instanceof ServerPlayer player)
            PlayerVoidVaultContainer.of(player).sync(player, false);

        if (gameTime % 60 == 3) {
            AbstractFishingPole.resetCurrentBait(entity);
            PlayerSpecialData.resetSomeData(entity);
        }
    }

    @SubscribeEvent
    public static void entityTick$Post(EntityTickEvent.Post event) {
        Immunity.tick(event.getEntity());
        // 平时（含大风天）第一行就返回：风不影响实体，只有沙尘暴/暴风雪激活时才会走到下面
        GameEventSystem.INSTANCE.pushEventWind(event.getEntity());
    }

    @SubscribeEvent
    public static void serverTick$Post(ServerTickEvent.Post event) {
        PathService.INSTANCE.pathFindingTick();
        WhipSession.handle();
    }
}
