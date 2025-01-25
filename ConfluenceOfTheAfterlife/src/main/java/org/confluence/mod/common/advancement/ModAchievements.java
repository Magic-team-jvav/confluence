package org.confluence.mod.common.advancement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.confluence.mod.Confluence;

import java.util.Hashtable;

public final class ModAchievements {
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
        offset("hammer_time", 3,0);
        offset("heavy_metal", 4, 0);
        offset("star_power", 5, 0);
        offset("hold_on_tight", 0, 1);
        offset("miner_for_fire", 1, 1);
        offset("head_in_the_clouds", 2, 1);
        offset("like_a_boss", 3, 1);
        offset("drax_attax", 4, 1);
        /* Temple Raider (0, 2) */
        /* Fashion Statement (1, 2) */
        /* Sword of the Hero (2, 2) */
        /* Dye Hard (3, 2) */
        /* Sick Throw (4, 2) */
        /* The Cavalry (0, 3) */
        offset("completely_awesome", 1, 3);
        /* Prismancer (2, 3) */
        offset("glorious_golden_pole", 3, 3);
        offset("matching_attire", 4, 3);
        /* Infinity +1 Sword (0, 4) */
        offset("boots_of_the_hero", 1, 4);
        /* Feast of Midas (2, 4) */
        offset("black_mirror", 3, 4);
        offset("ankhumulation_complete", 4, 4);


        // Explorer [6, 0] -> [10, 5]
        /* No Hobo (6, 0) */
        offset("ooo_shinny", 7, 0);
        offset("heart_breaker", 8, 0);
        offset("i_am_loot", 9, 0);
        offset("smashing_poppet", 10, 0);
        /* Where's My Honey (6, 1) */
        offset("dungeon_heist", 7, 1);
        offset("its_getting_hot_in_here", 8, 1);
        /* Its Hard (9, 1) */
        offset("begone_evil", 10, 1);
        offset("extra_shiny", 6, 2);
        offset("photosynthesis", 7, 2);
        offset("get_a_life", 8, 2);
        /* Robbing the Grave (9, 2) */
        /* Big Booty (10, 2) */
        /* Bloodbath (6, 3) */
        /* Kill the Sun (7, 3) */
        /* Sticky Situation (8, 3) */
        /* Jeepers Creepers (9, 3) */
        /* Funkytown (10, 3) */
        offset("into_orbit", 6, 4);
        offset("rock_bottom", 7, 4);
        /* It Can Talk (8, 4) */
        offset("watch_your_step", 9, 4);
        offset("you_can_do_it", 10, 4);
        /* Quiet Neighborhood (6.5, 5) */
        /* Hey Listen (7.5, 5) */
        /* A Rare Realm (8.5, 5) */
        offset("a_shimmer_in_the_dark", 9.5F, 5);


        // Slayer [0, 6] -> [4, 12]
        offset("eye_on_you", 0, 6);
        /* Worm Fodder 1, 6 */
        /* Mastermind 2, 6 */
        /* Sting Operation 3, 6 */
        /* Boned 4, 6 */
        /* Still Hungry 0, 7 */
        /* Buckets of Bolts 1, 7 */
        /* The Great Southern Plantkill 2, 7 */
        /* Lihzahrdian Idol 3, 7 */
        /* Fish Out of Water 4, 7 */
        /* Obsessive Devotion 0, 8 */
        /* Star Destroyer 1, 8 */
        /* Champion of Terraria 2, 8 */
        offset("slippery_shinobi", 3, 8);
        /* Goblin Punter 4, 8 */
        /* Walk the Plank 0, 9 */
        /* Do You Want to Slay a Snowman? 1, 9 */
        /* Tin-Foil Hatter 2, 9 */
        /* Baleful Harvest 3, 9 */
        /* Ice Scream 4, 9 */
        offset("vehicular_manslaughter", 0, 10);
        /* There are Some Who Call Him... 1, 10 */
        /* Deceiver of Fools 2, 10 */
        /* Til Death... 3, 10 */
        /* Archaeologist 4, 10 */
        offset("pretty_in_pink", 0, 11);
        /* Fae Flayer 1, 11 */
        /* Just Desserts 2, 11 */
        /* Don't Dread on Me 3, 11 */
        /* Hero of Etheria 4, 11 */
        /* An Eye For An Eye 1.5, 12 */
        /* Torch God 2.5, 12 */


        // Challenger [6, 7] -> [10, 12]
        /* Real Estate Agent 6, 7 */
        /* Not the Bees! 7, 7 */
        /* Mecha Mayhem 8, 7 */
        /* Gelatin World Tour 9, 7 */
        offset("bulldozer", 10, 7);
        offset("lucky_break", 6, 8);
        /* Throwing Lines 7, 8 */
        /* The Frequent Flyer 8, 8 */
        /* Rainbows and Unicorns 9, 8 */
        /* You and What Army? 10, 8 */
        offset("marathon_medalist", 6, 9);
        /* Servant-in-Training 7, 9 */
        /* Good Little Slave 8, 9 */
        /* Trout Monkey 9, 9 */
        /* Fast and Fishious 10, 9 */
        /* Supreme Helper Minion! 6, 10 */
        offset("topped_off", 7, 10);
        /* Slayer of Worlds 8, 10 */
        /* A Rather Blustery Day 9, 10 */
        offset("hot_reels", 10, 10);
        /* Heliophobia 6, 11 */
        /* Leading Landlord 7, 11 */
        /* Feeling Petty 8, 11 */
        /* Jolly Jamboree 9, 11 */
        offset("dead_men_tell_no_tales", 10, 11);
        offset("unusual_survival_strategies", 6.5F, 12);
        /* The Great Slime Mitosis 7.5, 12 */
        /* And Good Riddance! 8.5, 12 */
        /* To Infinity... and Beyond! 9.5, 12 */
    }

    private static void offset(String path, float x, float y) {
        DISPLAY_OFFSET.put(Confluence.asResource("achievements/" + path), new Vec2(x, y));
    }
}
