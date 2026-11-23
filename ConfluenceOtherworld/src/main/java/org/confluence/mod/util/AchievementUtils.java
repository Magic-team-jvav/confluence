package org.confluence.mod.util;

import com.google.common.collect.Streams;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BrainOfCthulhu;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.projectile.boulder.TombstoneBoulderEntity;
import org.confluence.mod.common.init.ModBlockCounters;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.Diff;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_VANITY_ARMOR;

public final class AchievementUtils {
    public static final String PREFIX = "achievements/";
    /// 植树造林所需种下的树苗数量。
    private static final int CONSERVATIONIST_REQUIRED = 100;
    private static final String CONSERVATIONIST_KEY = "confluence:conservationist_planted";
    @Diff
    public static final ResourceLocation ROOT = asAchievement("new_world");

    public static ResourceLocation asAchievement(String path) {
        return Confluence.asResource(PREFIX + path);
    }

    public static String asPath(ResourceLocation achievement) {
        return achievement.getPath().substring(PREFIX.length());
    }

    public static boolean achievedAchievement(ServerPlayer player, String path) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement(path));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static void awardAchievement(ServerPlayer player, String path) {
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        String key = Confluence.asPlainId(path);
        if (!data.getBoolean(key)) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement(path));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            data.putBoolean(key, true);
        }
    }

    public static void youCanDoIt(ServerPlayer player, ServerLevel level, long gameTime) {
        if (gameTime % 1200 == 0L) { // 每分钟检查一次
            byte firstNight = LibEntityUtils.getOrCreatePersistedData(player).getByte("confluence:you_can_do_it");
            if (firstNight == -1) return;
            int dayTime = LibDateUtils.getDayTime(level);
            if (LibDateUtils.isNight(dayTime)) {
                LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) 1);
            } else if (firstNight == 1 && LibDateUtils.isDay(dayTime)) {
                Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement("you_can_do_it"));
                if (advancement != null) {
                    player.getAdvancements().award(advancement, "never");
                }
                LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) -1);
            }
        }
    }

    public static void marathonMedalist(ServerPlayer player) {
        if (achievedAchievement(player, "marathon_medalist")) return;
        ServerStatsCounter stats = player.getStats();
        int sprint = stats.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        int crouch = stats.getValue(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM));
        int walk = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
        if (sprint + crouch + walk > 46112_00) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement("marathon_medalist"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
        }
    }

    public static void luckyBreak_watchYourStep(ServerPlayer player, DamageSource damageSource, @Nullable Entity sourceEntity) {
        if (player.isAlive()) {
            if (player.getHealth() / player.getMaxHealth() < 0.1F && damageSource.is(DamageTypeTags.IS_FALL)) {
                awardAchievement(player, "lucky_break");
            }
        } else if (sourceEntity != null && DartTrapBlock.NAME.equals(sourceEntity.getCustomName())) {
            awardAchievement(player, "watch_your_step");
        }
    }

    public static void matchingAttire_fashionStatement(EquipmentSlot.Type type, ServerPlayer player) {
        if (type != EquipmentSlot.Type.ARMOR) return;
        if (Streams.stream(player.getArmorSlots()).noneMatch(ItemStack::isEmpty)) {
            awardAchievement(player, "matching_attire");
            ExtraInventory extraInventory = ExtraInventory.of(player);
            boolean fashionStatement = true;
            for (int i = 0; i < SIZE_VANITY_ARMOR; i++) {
                if (extraInventory.getVanityArmor(i, false).isEmpty()) {
                    fashionStatement = false;
                    break;
                }
            }
            if (fashionStatement) awardAchievement(player, "fashion_statement");
        }
    }

    public static void theFrequentFlyer(ServerPlayer player, long cost) {
        CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
        short before = tag.getShort("confluence:the_frequent_flyer");
        if (before > 10000) return;
        long total = before + cost;
        if (total >= 10000) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement("the_frequent_flyer"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
        }
        tag.putShort("confluence:the_frequent_flyer", (short) total);
    }

    public static void noHobo_itCanTalk(BaseNPC npc, NPCSpawner.Region region) {
        if (npc.level() instanceof ServerLevel level) {
            for (ServerPlayer player : level.players()) {
                if (region.isOnRegion(player.chunkPosition())) {
                    AchievementUtils.awardAchievement(player, "no_hobo");
                    if (npc.getType() == NpcEntities.TRUFFLE.get()) {
                        AchievementUtils.awardAchievement(player, "it_can_talk");
                    }
                }
            }
        }
    }

    public static void quietNeighborhood(ServerPlayer player, ServerLevel level) {
        if (ModBlockCounters.isGraveyard(level, player.blockPosition())) {
            awardAchievement(player, "quiet_neighborhood");
        }
    }

    public static void aRareRealm(ServerPlayer player) {
        if (IMinecraftServer.of(player.server).matchesSecretFlag(IWorldOptions.SECRET_SEED)) {
            awardAchievement(player, "a_rare_realm");
        }
    }

    public static void unusualSurvivalStrategies(ServerPlayer player, boolean isWatterBottle) {
        if (isWatterBottle && player.isInWater() && player.getAirSupply() <= 0) {
            awardAchievement(player, "unusual_survival_strategies");
        }
    }

    /// 机械 Boss 死亡后的共同结算：三王全灭与三王同时击杀。
    public static void mechanicalBossDefeated(ServerPlayer player, BaseBoss boss) {
        boolean allMechanicalDefeated = KillBoard.INSTANCE.isTwinsDefeated()
                && KillBoard.INSTANCE.isDefeated(BossEntities.THE_DESTROYER.get())
                && KillBoard.INSTANCE.isDefeated(BossEntities.SKELETRON_PRIME.get());
        if (!allMechanicalDefeated) return;
        awardAchievement(player, "buckets_of_bolts");
        if (boss.isMechanicalMayhemParticipant(player.getUUID())) {
            awardAchievement(player, "mecha_mayhem");
        }
    }

    /// 种下树苗，累计一百棵后达成“植树造林”。
    public static void conservationist(ServerPlayer player) {
        if (achievedAchievement(player, "conservationist")) return;
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        int planted = data.getInt(CONSERVATIONIST_KEY);
        if (planted >= CONSERVATIONIST_REQUIRED) return;
        if (++planted >= CONSERVATIONIST_REQUIRED) {
            awardAchievement(player, "conservationist");
        }
        data.putInt(CONSERVATIONIST_KEY, planted);
    }

    /// 使用先进战斗技术后，两卷都启用时达成“训练日”。
    public static void trainingDay(ServerLevel level) {
        if (NPCSpawner.INSTANCE.isAdvancedCombatTechniquesUsed() && NPCSpawner.INSTANCE.isAdvancedCombatTechniquesVolumeTwoUsed()) {
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                awardAchievement(player, "training_day");
            }
        }
    }

    /// 只有“为所欲为”种子下墓石才会造成伤害，因此命中即代表用墓石砸死了别人。
    public static void graveMistake(DamageSource damageSource, LivingEntity victim) {
        if (damageSource.getDirectEntity() instanceof TombstoneBoulderEntity tombstone
                && tombstone.getOwner() instanceof ServerPlayer owner && owner != victim
        ) awardAchievement(owner, "grave_mistake");
    }

    /// 冲出世界顶部：Remix／天顶种子，或“为所欲为”种子下位于二阶段克苏鲁之脑 250 格内。
    /// 满足条件越过顶部边界后先结算成就；非无敌模式的玩家会因此死亡（对应原版“单程旅行”）。
    public static void myPeopleNeedMe(ServerPlayer player) {
        if (player.getY() < OverworldUtils.getUltraY()) return;
        boolean canLeaveTop = ModSecretSeeds.DONT_DIG_UP.match(player.server)
                || ModSecretSeeds.GET_FIXED_BOI.match(player.server)
                || ModSecretSeeds.FOR_THE_WORTHY.match(player.server) && isNearPhaseTwoBrain(player);
        if (!canLeaveTop) return;
        awardAchievement(player, "my_people_need_me");
        if (!player.isCreative() && !player.isSpectator() && !player.isInvulnerable()) {
            player.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
        }
    }

    /// 250 格内是否存在第二阶段的克苏鲁之脑。
    private static boolean isNearPhaseTwoBrain(ServerPlayer player) {
        return !player.serverLevel().getEntitiesOfClass(
                BrainOfCthulhu.class,
                player.getBoundingBox().inflate(250.0D * 2 / 3),
                BrainOfCthulhu::isPhase2
        ).isEmpty();
    }
}
