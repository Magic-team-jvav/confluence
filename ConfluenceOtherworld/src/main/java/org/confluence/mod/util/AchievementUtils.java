package org.confluence.mod.util;

import com.google.common.collect.Streams;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.advancement.AchievementAwardService;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerAchievementProgress;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_VANITY_ARMOR;

public final class AchievementUtils {
    public static final String PREFIX = "achievements/";

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
        AchievementAwardService.award(player, path);
    }

    /// 记录玩家击败的史莱姆种类，并在覆盖当前标签中的全部类型后授予成就。
    /// 标签是需要击败哪些实体的唯一配置入口，外部模组无需修改本体代码。
    public static boolean gelatinWorldTour(ServerPlayer player, EntityType<?> defeatedType) {
        if (player.isSpectator() || !defeatedType.is(LibTags.EntityTypes.SLIME)) {
            return false;
        }
        Set<ResourceLocation> requiredTypes = new LinkedHashSet<>();
        player.serverLevel().registryAccess()
                .registryOrThrow(Registries.ENTITY_TYPE)
                .getTagOrEmpty(LibTags.EntityTypes.SLIME)
                .forEach(holder -> {
                    ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(holder.value());
                    if (id != null) requiredTypes.add(id);
                });
        if (requiredTypes.isEmpty()
                || !PlayerAchievementProgress.of(player)
                .recordSlimeVariant(defeatedType, requiredTypes)) {
            return false;
        }
        return AchievementAwardService.award(player, "gelatin_world_tour").completed();
    }

    public static void youCanDoIt(ServerPlayer player, ServerLevel level, long gameTime) {
        if (gameTime % 1200 == 0L) { // 每分钟检查一次
            byte firstNight = LibEntityUtils.getOrCreatePersistedData(player).getByte("confluence:you_can_do_it");
            if (firstNight == -1) return;
            int dayTime = LibDateUtils.getDayTime(level);
            if (LibDateUtils.isNight(dayTime)) {
                LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) 1);
            } else if (firstNight == 1 && LibDateUtils.isDay(dayTime)) {
                if (AchievementAwardService.award(player, "you_can_do_it").completed()) {
                    LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) -1);
                }
            }
        }
    }

    // todo
    public static boolean marathonMedalist(ServerPlayer player, ServerStatsCounter stats) {
        if (achievedAchievement(player, "marathon_medalist")) return true;
        int sprint = stats.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        int crouch = stats.getValue(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM));
        int walk = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
        if (sprint + crouch + walk > 46112_00) {
            return AchievementAwardService.award(player, "marathon_medalist").completed();
        }
        return false;
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
            AchievementAwardService.award(player, "the_frequent_flyer");
        }
        tag.putShort("confluence:the_frequent_flyer", (short) total);
    }

    public static void noHobo(BaseNPC npc, NPCSpawner.Region region) {
        if (npc.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                if (region.isOnRegion(player.chunkPosition())) {
                    AchievementUtils.awardAchievement(player, "no_hobo");
                }
            }
        }
    }

    public static void quietNeighborhood(ServerPlayer player, ServerLevel level, long gameTime) {
        if (gameTime % 40 == 2) {
            ILevelChunkSection iSection = DynamicBiomeUtils.getISection(level, player.blockPosition());
            if (iSection != null && iSection.confluence$isGraveyard()) {
                awardAchievement(player, "quiet_neighborhood");
            }
        }
    }

    public static void aRareRealm(ServerPlayer player, long gameTime) {
        if (IMinecraftServer.of(player.server).confluence$matchesSecretFlag(IWorldOptions.SECRET_SEED) && gameTime % 40 == 3) {
            awardAchievement(player, "a_rare_realm");
        }
    }

    public static void unusualSurvivalStrategies(ServerPlayer player, boolean isWatterBottle) {
        if (isWatterBottle && player.isInWater() && player.getAirSupply() <= 0) {
            awardAchievement(player, "unusual_survival_strategies");
        }
    }
}
