package org.confluence.mod.common.event.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.confluence.lib.color.GlobalColors;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.*;
import org.confluence.mod.common.entity.FallingStarItemEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.entity.boss.EyeOfCthulhu;
import org.confluence.terraentity.init.entity.TEBossEntities;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class TickEvents {
    @SubscribeEvent
    public static void levelTick$Post(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        PathService.INSTANCE.pathFindingTick();
        if (serverLevel.dimension() != OverworldUtils.dimension()) return;
        FallingStarItemEntity.summon(serverLevel);
        MeteoriteTracker.INSTANCE.tick(serverLevel);
        BossDelaySpawner.INSTANCE.tick(serverLevel);

        long dayTime = serverLevel.getDayTime() % 24000L;
        if (dayTime == DateUtils._06$00) {
            float factorX = Mth.nextFloat(serverLevel.random, -1.0F, 1.0F);
            float factorZ = Mth.nextFloat(serverLevel.random, -1.0F, 1.0F);
            ConfluenceData.get(serverLevel).setWindSpeed(factorX, factorZ);
        } else if (dayTime == DateUtils._19$30) {
            if (!KillBoard.INSTANCE.isDefeated(TEBossEntities.EYE_OF_CTHULHU.get())) {
                for (ServerPlayer player : serverLevel.players()) {
                    boolean attributeFactor = player.getMaxHealth() >= 40 && player.getArmorValue() >= 10;
                    boolean npcFactor = NPCSpawner.INSTANCE.getAliveNpcCount(new NPCSpawner.Region(NPCSpawner.getNpcSpawnPos(player)), entityType -> true/* todo 骷髅商人不计入 */) >= 4;
                    if (attributeFactor && npcFactor) {
                        if (serverLevel.random.nextFloat() < 0.3333F) {
                            BossDelaySpawner.INSTANCE.pushBoss(1350, new EyeOfCthulhu(serverLevel), level -> level.getDayTime() % 24000 > 12000);
                            serverLevel.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("event.confluence.eye_of_cthulhu").withColor(GlobalColors.MESSAGE.get()), false);
                        }
                        break;
                    }
                }
            }
            if (KillBoard.INSTANCE.isAnyDefeated(TEBossEntities.EATER_OF_WORLDS.get(), TEBossEntities.BRAIN_OF_CTHULHU.get()) && serverLevel.random.nextFloat() < 0.02F) {
                MeteoriteTracker.INSTANCE.spawnAtNextNight = true;
            }
        }
        if (CommonConfigs.DO_NPC_SPAWNING.get() &&
                (dayTime < 12000 || dayTime > 22500) &&
                serverLevel.getGameTime() % CommonConfigs.NPC_SPAWN_INTERVAL.get() == 0 &&
                serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
        ) {
            NPCSpawner.INSTANCE.checkNpcRespawn(serverLevel);
        }

        HardmodeConvertor.INSTANCE.scheduleRefill(serverLevel);
    }

    @SubscribeEvent
    public static void playerTick$Post(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerUtils.regenerateMana(serverPlayer);
            ((IServerPlayer) serverPlayer).confluence$setCouldPickupItem(true);
            serverPlayer.getData(ModAttachmentTypes.EXTRA_INVENTORY).sync(serverPlayer);
            ServerLevel serverLevel = serverPlayer.serverLevel();
            AchievementUtils.youCanDoIt(serverPlayer, serverLevel);
            TheConstant.applyDarkness(serverPlayer, serverLevel);
            DungeonStructure.checkSkeletronDefeated(serverPlayer, serverLevel);
        }
    }

    @SubscribeEvent
    public static void entityTick$Post(EntityTickEvent.Post event) {
        // 实体身上的无敌帧每刻-1
        if (event.getEntity() instanceof ILivingEntity living) {
            int extraInvulnerableTicks = living.confluence$getExtraInvulnerableTicks();
            if (extraInvulnerableTicks > 0) {
                living.confluence$setExtraInvulnerableTicks(extraInvulnerableTicks - 1);
            }

            Object2IntMap<Immunity> invTicks = living.confluence$getImmunityTicks();
            if (invTicks.isEmpty()) return;
            ObjectIterator<Object2IntMap.Entry<Immunity>> iterator = invTicks.object2IntEntrySet().iterator();
            while (iterator.hasNext()) {
                Object2IntMap.Entry<Immunity> entry = iterator.next();
                int remain = entry.getIntValue() - 1;
                if (remain < 0) {
                    iterator.remove();
                } else {
                    entry.setValue(remain);
                }
            }
        }
    }
}
