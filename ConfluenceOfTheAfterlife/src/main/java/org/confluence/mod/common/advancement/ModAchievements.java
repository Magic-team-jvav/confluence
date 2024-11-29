package org.confluence.mod.common.advancement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.confluence.mod.Confluence;

import java.util.Hashtable;

public final class ModAchievements {
    public static final Hashtable<ResourceLocation, Vec2> DISPLAY_OFFSET = new Hashtable<>();

    public static void initialize() {
        offset("new_world", 0, 0);
        // Collector
        offset("timber", 1, 0);
        offset("benched", 2, 0);
        offset("hammer_time", 3,0);
        offset("heavy_metal", 4, 0);
        offset("star_power", 5, 0);
        offset("hold_on_tight", 0, 1);
        /* Miner for Fire (1, 1) */
        /* Head in the Clouds (2, 1) */
        /* Like a Boss (3, 1) */
        /* Drax Attax (4, 1) */
        /* Temple Raider (0, 2) */
        /* Fashion Statement (1, 2) */
        /* Sword of the Hero (2, 2) */
        /* Dye Hard (3, 2) */
        /* Sick Throw (4, 2) */
        /* The Cavalry (0, 3) */
        /* Completely Awesome (1, 3) */
        /* Prismancer (2, 3) */
        /* Glorious Golden Pole (3, 3) */
        /* Matching Attire (4, 3) */
        /* Infinity +1 Sword (0, 4) */
        offset("boots_of_the_hero", 1, 4);
        /* Feast of Midas (2, 4) */
        offset("black_mirror", 3, 4);
        offset("ankhumulation_complete", 4, 4);

        // Explorer
        /* No Hobo (6, 0) */
        offset("ooo_shinny", 7, 0);
        offset("heart_breaker", 8, 0);
        offset("i_am_loot", 9, 0);
        /* Smashing Poppet (10, 0) */
        /* Wheres My Honey (6, 1) */
        /* Dungeon Heist (7, 1) */
        /* Its Getting Hot in Here (8, 1) */
        /* Its Hard (9, 1) */
        /* Begone Evil (10, 1) */
        /* Extra Shiny (6, 2) */
        /* Photosynthesis (7, 2) */
        /* Get a Life (8, 2) */
        /* Robbing the Grave (9, 2) */
        /* Big Booty (10, 2) */
        /* Bloodbath (6, 3) */
        /* Kill the Sun (7, 3) */
        /* Sticky Situation (8, 3) */
        /* Jeepers Creepers (9, 3) */
        /* Funkytown (10, 3) */
        /* Into Orbit (6, 4) */
        /* Rock Bottom (7, 4) */
        /* It Can Talk (8, 4) */
        /* Watch Your Step (9, 4) */
        /* You Can Do It (10, 4) */
        /* Quiet Neighborhood (6.5, 5) */
        /* Hey Listen (7.5, 5) */
        /* A Rare Realm (8.5, 5) */
        offset("a_shimmer_in_the_dark", 9.5F, 5);


        // Explorer
        offset("slippery_shinobi", 20F, 20);
        offset("eye_on_you", 21F, 20);
        offset("pretty_in_pink", 20F, 40);
    }

    private static void offset(String path, float x, float y) {
        DISPLAY_OFFSET.put(Confluence.asResource("achievements/" + path), new Vec2(x, y));
    }
}
