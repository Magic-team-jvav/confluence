package org.confluence.mod.util;

import com.google.common.collect.Streams;
import net.minecraft.advancements.AdvancementHolder;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_VANITY_ARMOR;

public final class AchievementUtils {
    public static final String PREFIX = "achievements/";

    public static ResourceLocation asAchievement(String path) {
        return Confluence.asResource(PREFIX + path);
    }

    public static boolean achievedAchievement(ServerPlayer player, String path) {
        if (LibUtils.getOrCreatePersistedData(player).getBoolean(Confluence.MODID + ':' + path)) return true;
        AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement(path));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static void awardAchievement(ServerPlayer player, String path) {
        CompoundTag data = LibUtils.getOrCreatePersistedData(player);
        String key = Confluence.MODID + ':' + path;
        if (!data.getBoolean(key)) {
            AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement(path));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            data.putBoolean(key, true);
        }
    }

    public static void youCanDoIt(ServerPlayer player, ServerLevel level) {
        if (level.getGameTime() % 1200 == 0L) { // 每分钟检查一次
            byte firstNight = LibUtils.getOrCreatePersistedData(player).getByte("confluence:you_can_do_it");
            if (firstNight == -1) return;
            int dayTime = LibDateUtils.getDayTime(level);
            if (LibDateUtils.isNight(dayTime)) {
                LibUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) 1);
            } else if (firstNight == 1 && LibDateUtils.isDay(dayTime)) {
                AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("you_can_do_it"));
                if (advancement != null) {
                    player.getAdvancements().award(advancement, "never");
                }
                LibUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) -1);
            }
        }
    }

    public static boolean marathonMedalist(ServerPlayer player, ServerStatsCounter stats, boolean marathon) {
        if (marathon) return true;
        int sprint = stats.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        int crouch = stats.getValue(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM));
        int walk = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
        if (sprint + crouch + walk > 46112_00) {
            AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("marathon_medalist"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            return true;
        }
        return false;
    }

    public static void luckyBreak_watchYourStep(LivingEntity damagingEntity, DamageSource damageSource, Entity sourceEntity) {
        if (damagingEntity instanceof ServerPlayer serverPlayer) {
            if (damagingEntity.isAlive()) {
                if (damagingEntity.getHealth() / damagingEntity.getMaxHealth() < 0.1F && damageSource.is(DamageTypeTags.IS_FALL)) {
                    awardAchievement(serverPlayer, "lucky_break");
                }
            } else if (sourceEntity != null && DartTrapBlock.NAME.equals(sourceEntity.getCustomName())) {
                awardAchievement(serverPlayer, "watch_your_step");
            }
        }
    }

    public static void matchingAttire_fashionStatement(EquipmentSlot slot, ServerPlayer player) {
        if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
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
    }

    public static void theFrequentFlyer(ServerPlayer player, long cost) {
        short before = LibUtils.getOrCreatePersistedData(player).getShort("confluence:the_frequent_flyer");
        if (before > 10000) return;
        long total = before + cost;
        if (total >= 10000) {
            AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("the_frequent_flyer"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
        }
        LibUtils.getOrCreatePersistedData(player).putShort("confluence:the_frequent_flyer", (short) total);
    }

    public static void noHobo(AbstractTerraNPC npc, NPCSpawner.Region region) {
        if (npc.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                if (region.isOnRegion(player.chunkPosition())) {
                    AchievementUtils.awardAchievement(player, "no_hobo");
                }
            }
        }
    }

    public static void quietNeighborhood(ServerPlayer player, ServerLevel level) {
        if (level.getGameTime() % 40 == 2) {
            ILevelChunkSection iSection = DynamicBiomeUtils.getISection(level, player.blockPosition());
            if (iSection != null && iSection.confluence$isGraveyard()) {
                awardAchievement(player, "quiet_neighborhood");
            }
        }
    }

    public static void aRareRealm(ServerPlayer player, ServerLevel level) {
        if (IMinecraftServer.of(player.server).confluence$matchesSecretFlag(IWorldOptions.SECRET_SEED)) {
            if (level.getGameTime() % 20 == 3) {
                awardAchievement(player, "a_rare_realm");
            }
        }
    }

    public static void unusualSurvivalStrategies(ServerPlayer player, boolean isWatterBottle) {
        if (isWatterBottle && player.isInWater() && player.getAirSupply() <= 0) {
            awardAchievement(player, "unusual_survival_strategies");
        }
    }
}
