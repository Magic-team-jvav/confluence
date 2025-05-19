package org.confluence.mod.common.init;

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
import net.minecraft.world.phys.Vec2;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

import java.util.Hashtable;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_VANITY_ARMOR;

public final class ModAchievements {
    public static final String PREFIX = "achievements/";
    public static final Hashtable<ResourceLocation, Vec2> DISPLAY_OFFSET = new Hashtable<>();

    /*
     * 0 1 2 3 4 5 6 7 8 9 10
     * 1 Colle |   | Explo |
     * 2 -ctor |   | -rer  |
     * 3       |   |       |
     * 4 ----- *   |       |
     * 5           * ----- *
     * 6 ----- *
     * 7 Slaye |   * ----- *
     * 8 -r    |   | Chall |
     * 9       |   | -enge |
     * 10      |   | -r    |
     * 11      |   |       |
     * 12 ---- *   * ----- *
     */
    public static void initialize() {
        // Collector [0, 0] -> [4, 4]
        offset("new_world", 0, 0);
        offset("timber", 1, 0);
        offset("benched", 2, 0);
        offset("hammer_time", 3, 0);
        offset("heavy_metal", 4, 0);
        offset("star_power", 5, 0);
        offset("hold_on_tight", 0, 1);
        offset("miner_for_fire", 1, 1);
        offset("head_in_the_clouds", 2, 1);
        offset("like_a_boss", 3, 1);
        offset("drax_attax", 4, 1);
        offset("temple_raider", 0, 2);
        offset("fashion_statement", 1, 2);
        offset("sword_of_the_hero", 2, 2);
        offset("dye_hard", 3, 2);
        offset("sick_throw", 4, 2);
        offset("the_cavalry", 0, 3);
        offset("completely_awesome", 1, 3);
        offset("prismancer", 2, 3);
        offset("glorious_golden_pole", 3, 3);
        offset("matching_attire", 4, 3);
        offset("infinity_1_sword", 0, 4);
        offset("boots_of_the_hero", 1, 4);
        offset("feast_of_midas", 2, 4);
        offset("black_mirror", 3, 4);
        offset("ankhumulation_complete", 4, 4);


        // Explorer [6, 0] -> [10, 5]
        offset("no_hobo", 6, 0);
        offset("ooo_shinny", 7, 0);
        offset("heart_breaker", 8, 0);
        offset("i_am_loot", 9, 0);
        offset("smashing_poppet", 10, 0);
        offset("wheres_my_honey", 6, 1);
        offset("dungeon_heist", 7, 1);
        offset("its_getting_hot_in_here", 8, 1);
        offset("its_hard", 9, 1);
        offset("begone_evil", 10, 1);
        offset("extra_shiny", 6, 2);
        offset("photosynthesis", 7, 2);
        offset("get_a_life", 8, 2);
        offset("robbing_the_grave", 9, 2);
        offset("big_booty", 10, 2);
        offset("bloodbath", 6, 3);
        offset("kill_the_sun", 7, 3);
        offset("sticky_situation", 8, 3);
        offset("jeepers_creepers", 9, 3);
        offset("funkytown", 10, 3);
        offset("into_orbit", 6, 4);
        offset("rock_bottom", 7, 4);
        offset("it_can_talk", 8, 4);
        offset("watch_your_step", 9, 4);
        offset("you_can_do_it", 10, 4);
        offset("quiet_neighborhood", 6.5F, 5);
        offset("hey_listen", 7.5F, 5);
        offset("a_rare_realm", 8.5F, 5);
        offset("a_shimmer_in_the_dark", 9.5F, 5);


        // Slayer [0, 6] -> [4, 12]
        offset("eye_on_you", 0, 6);
        offset("worm_fodder", 1, 6);
        offset("mastermind", 2, 6);
        offset("sting_operation", 3, 6);
        offset("boned", 4, 6);
        offset("still_hungry", 0, 7);
        offset("buckets_of_bolts", 1, 7);
        offset("the_great_southern_plantkill", 2, 7);
        offset("lihzahrdian_idol", 3, 7);
        offset("fish_out_of_water", 4, 7);
        offset("obsessive_devotion", 0, 8);
        offset("star_destroyer", 1, 8);
        offset("champion_of_terraria", 2, 8);
        offset("slippery_shinobi", 3, 8);
        offset("goblin_punter", 4, 8);
        offset("walk_the_plank", 0, 9);
        offset("do_you_want_to_slay_a_snowman", 1, 9);
        offset("tin_foil_hatter", 2, 9);
        offset("baleful_harvest", 3, 9);
        offset("ice_scream", 4, 9);
        offset("vehicular_manslaughter", 0, 10);
        offset("there_are_some_who_call_him", 1, 10);
        offset("deceiver_of_fools", 2, 10);
        offset("til_death", 3, 10);
        offset("archaeologist", 4, 10);
        offset("pretty_in_pink", 0, 11);
        offset("fae_flayer", 1, 11);
        offset("just_desserts", 2, 11);
        offset("dont_dread_on_me", 3, 11);
        offset("hero_of_etheria", 4, 11);
        offset("an_eye_for_an_eye", 1.5F, 12);
        offset("torch_god", 2.5F, 12);


        // Challenger [6, 7] -> [10, 12]
        offset("real_estate_agent", 6, 7);
        offset("not_the_bees", 7, 7);
        offset("mecha_mayhem", 8, 7);
        offset("gelatin_world_tour", 9, 7);
        offset("bulldozer", 10, 7);
        offset("lucky_break", 6, 8);
        offset("throwing_lines", 7, 8);
        offset("the_frequent_flyer", 8, 8);
        offset("rainbows_and_unicorns", 9, 8);
        offset("you_and_what_army", 10, 8);
        offset("marathon_medalist", 6, 9);
        offset("servant_in_training", 7, 9);
        offset("good_little_slave", 8, 9);
        offset("trout_monkey", 9, 9);
        offset("fast_and_fishious", 10, 9);
        offset("supreme_helper_minion", 6, 10);
        offset("topped_off", 7, 10);
        offset("slayer_of_worlds", 8, 10);
        offset("a_rather_blustery_day", 9, 10);
        offset("hot_reels", 10, 10);
        offset("heliophobia", 6, 11);
        offset("leading_landlord", 7, 11);
        offset("feeling_petty", 8, 11);
        offset("jolly_jamboree", 9, 11);
        offset("dead_men_tell_no_tales", 10, 11);
        offset("unusual_survival_strategies", 6.5F, 12);
        offset("the_great_slime_mitosis", 7.5F, 12);
        offset("and_good_riddance", 8.5F, 12);
        offset("to_infinity_and_beyond", 9.5F, 12);
    }

    private static void offset(String path, float x, float y) {
        DISPLAY_OFFSET.put(Confluence.asResource(PREFIX + path), new Vec2(x, y));
    }

    public static void awardAchievement(ServerPlayer player, String path) {
        CompoundTag data = LibUtils.getOrCreatePersistedData(player);
        String key = Confluence.MODID + ':' + path;
        if (!data.getBoolean(key)) {
            AdvancementHolder advancement = player.server.getAdvancements().get(Confluence.asResource(PREFIX + path));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            data.putBoolean(key, true);
        }
    }

    public static void youCanDoIt(ServerPlayer player, ServerLevel level) {
        if (level.getDayTime() % 1200L == 0L) { // 每分钟检查一次
            long firstNight = LibUtils.getOrCreatePersistedData(player).getLong("confluence:you_can_do_it");
            if (firstNight != -1L) {
                if (firstNight == 0L && level.isNight()) {
                    LibUtils.getOrCreatePersistedData(player).putLong("confluence:you_can_do_it", level.getDayTime());
                } else if (firstNight != 0L && level.getDayTime() - firstNight > 12000L) {
                    AdvancementHolder advancement = player.server.getAdvancements().get(Confluence.asResource(PREFIX + "you_can_do_it"));
                    if (advancement != null) {
                        player.getAdvancements().award(advancement, "never");
                    }
                    LibUtils.getOrCreatePersistedData(player).putLong("confluence:you_can_do_it", -1L);
                }
            }
        }
    }

    public static boolean marathonMedalist(ServerPlayer player, ServerStatsCounter stats, boolean marathon) {
        if (marathon) return true;
        int sprint = stats.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        int crouch = stats.getValue(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM));
        int walk = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
        if (sprint + crouch + walk > 46112_00) {
            AdvancementHolder advancement = player.server.getAdvancements().get(Confluence.asResource(ModAchievements.PREFIX + "marathon_medalist"));
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

    public static void matchingAttire_fashionStatement(EquipmentSlot slot, ServerPlayer serverPlayer) {
        if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            if (Streams.stream(serverPlayer.getArmorSlots()).noneMatch(ItemStack::isEmpty)) {
                awardAchievement(serverPlayer, "matching_attire");
                ExtraInventory extraInventory = serverPlayer.getData(ModAttachmentTypes.EXTRA_INVENTORY);
                boolean fashionStatement = true;
                for (int i = 0; i < SIZE_VANITY_ARMOR; i++) {
                    if (extraInventory.getVanityArmor(i).isEmpty()) {
                        fashionStatement = false;
                        break;
                    }
                }
                if (fashionStatement) awardAchievement(serverPlayer, "fashion_statement");
            }
        }
    }

    public static void theFrequentFlyer(ServerPlayer player, long cost) {
        short before = LibUtils.getOrCreatePersistedData(player).getShort("confluence:the_frequent_flyer");
        if (before > 10000) return;
        long total = before + cost;
        if (total >= 10000) {
            AdvancementHolder advancement = player.server.getAdvancements().get(Confluence.asResource(ModAchievements.PREFIX + "the_frequent_flyer"));
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
                    ModAchievements.awardAchievement(player, "no_hobo");
                }
            }
        }
    }
}
