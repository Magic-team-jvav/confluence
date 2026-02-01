package org.confluence.mod.common.data.gen;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.util.AchievementUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModAchievementOffsetProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider pathProvider;

    public ModAchievementOffsetProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "");
    }

    /// Collector Explorer
    /// Slayer    Challenger
    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        recipe(Codec.unboundedMap(ResourceLocation.CODEC, AchievementOffset.CODEC), pathProvider().json(Confluence.asResource("achievement_offset"))).addRecipe(new Builder()
                // Collector
                .push(true, true)
                .add("new_world")
                .add("timber")
                .add("benched")
                .add("hammer_time")
                .add("heavy_metal")
                .add("star_power")
                .add("hold_on_tight")
                .add("miner_for_fire")
                .add("head_in_the_clouds")
                .add("like_a_boss")
                .add("drax_attax")
                .add("temple_raider")
                .add("fashion_statement")
                .add("sword_of_the_hero")
                .add("dye_hard")
                .add("sick_throw")
                .add("the_cavalry")
                .add("completely_awesome")
                .add("prismancer")
                .add("glorious_golden_pole")
                .add("matching_attire")
                .add("infinity_1_sword")
                .add("boots_of_the_hero")
                .add("feast_of_midas")
                .add("black_mirror")
                .add("ankhumulation_complete")
                .add("organized_chaos")
                .add("on_fleek")
                .add("mini_me")
                .add("new_digs")
                .add("sea_you_later")
                .pop()
                // Explorer
                .push(true, false)
                .add("no_hobo")
                .add("ooo_shinny")
                .add("heart_breaker")
                .add("i_am_loot")
                .add("smashing_poppet")
                .add("wheres_my_honey")
                .add("dungeon_heist")
                .add("its_getting_hot_in_here")
                .add("its_hard")
                .add("begone_evil")
                .add("extra_shiny")
                .add("photosynthesis")
                .add("get_a_life")
                .add("robbing_the_grave")
                .add("big_booty")
                .add("bloodbath")
                .add("kill_the_sun")
                .add("sticky_situation")
                .add("jeepers_creepers")
                .add("funkytown")
                .add("into_orbit")
                .add("rock_bottom")
                .add("it_can_talk")
                .add("watch_your_step")
                .add("you_can_do_it")
                .add("quiet_neighborhood")
                .add("hey_listen")
                .add("a_rare_realm")
                .add("a_shimmer_in_the_dark")
                .add("its_shaling_outside")
                .add("fortune_favors_the_bould")
                .add("training_day")
                .pop()
                // Slayer
                .push(false, true)
                .add("eye_on_you")
                .add("worm_fodder")
                .add("mastermind")
                .add("sting_operation")
                .add("boned")
                .add("still_hungry")
                .add("buckets_of_bolts")
                .add("the_great_southern_plantkill")
                .add("lihzahrdian_idol")
                .add("fish_out_of_water")
                .add("obsessive_devotion")
                .add("star_destroyer")
                .add("champion_of_terraria")
                .add("slippery_shinobi")
                .add("goblin_punter")
                .add("walk_the_plank")
                .add("do_you_want_to_slay_a_snowman")
                .add("tin_foil_hatter")
                .add("baleful_harvest")
                .add("ice_scream")
                .add("vehicular_manslaughter")
                .add("there_are_some_who_call_him")
                .add("deceiver_of_fools")
                .add("til_death")
                .add("archaeologist")
                .add("pretty_in_pink")
                .add("fae_flayer")
                .add("just_desserts")
                .add("dont_dread_on_me")
                .add("hero_of_etheria")
                .add("an_eye_for_an_eye")
                .add("torch_god")
                .pop()
                // Challenger
                .push(false, false)
                .add("real_estate_agent")
                .add("not_the_bees")
                .add("mecha_mayhem")
                .add("gelatin_world_tour")
                .add("bulldozer")
                .add("lucky_break")
                .add("throwing_lines")
                .add("the_frequent_flyer")
                .add("rainbows_and_unicorns")
                .add("you_and_what_army")
                .add("marathon_medalist")
                .add("servant_in_training")
                .add("good_little_slave")
                .add("trout_monkey")
                .add("fast_and_fishious")
                .add("supreme_helper_minion")
                .add("topped_off")
                .add("slayer_of_worlds")
                .add("a_rather_blustery_day")
                .add("hot_reels")
                .add("heliophobia")
                .add("leading_landlord")
                .add("feeling_petty")
                .add("jolly_jamboree")
                .add("dead_men_tell_no_tales")
                .add("unusual_survival_strategies")
                .add("the_great_slime_mitosis")
                .add("and_good_riddance")
                .add("to_infinity_and_beyond")
                .add("book_worm")
                .add("boulder_lord")
                .add("queen_machine")
                .add("rollin_in_your_grave")
                .add("fear_the_sun")
                .add("extra_life")
                .add("grave_mistake")
                .add("spicy_licks")
                .add("terrarist")
                .add("my_people_need_me")
                .add("going_oldschool")
                .add("trash_compactor")
                .add("conservationist")
                .add("interdimensional_recycling")
                .pop()
                .build());
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return pathProvider;
    }

    static class Builder {
        private final Category topLeft = new Category();
        private final Category topRight = new Category();
        private final Category bottomLeft = new Category();
        private final Category bottomRight = new Category();

        public Category push(boolean top, boolean left) {
            return top ? (left ? topLeft : topRight) : (left ? bottomLeft : bottomRight);
        }

        public Map<ResourceLocation, AchievementOffset> build() {
            Map<ResourceLocation, AchievementOffset> offsetMap = new HashMap<>();

            int topLeftLength = getLength(topLeft.list.size());
            int topRightLength = getLength(topRight.list.size());
            int bottomLeftLength = getLength(bottomLeft.list.size());
            int bottomRightLength = getLength(bottomRight.list.size());

            float rightStartX = Math.max(topLeftLength, bottomLeftLength) + 1;
            float bottomStartY = Math.max(topLeftLength, topRightLength) + 1;

            calculateOffset(topLeft, topLeftLength, 0, 0, offsetMap);
            calculateOffset(topRight, topRightLength, rightStartX, 0, offsetMap);
            calculateOffset(bottomLeft, bottomLeftLength, 0, bottomStartY, offsetMap);
            calculateOffset(bottomRight, bottomRightLength, rightStartX, bottomStartY, offsetMap);

            return offsetMap;
        }

        private static void calculateOffset(Category category, int length, float startX, float startY, Map<ResourceLocation, AchievementOffset> offsetMap) {
            int size = category.list.size();
            int remain = size - Mth.square(length);
            float offsetX;
            if (remain < 0) {
                offsetX = remain * -0.5F;
                remain += length;
            } else {
                offsetX = (length - remain) * 0.5F;
            }
            int remainStart = size - remain;
            for (int i = 0; i < size; i++) {
                float x = (float) (i % length);
                float y = (float) (i / length);
                if (i >= remainStart) {
                    x += offsetX;
                }
                offsetMap.put(category.list.get(i), new AchievementOffset(startX + x, startY + y));
            }
        }

        private static int getLength(int size) {
            int sqrt = (int) Math.sqrt(size);
            int less = Mth.square(sqrt);
            int more = Mth.square(sqrt + 1);
            return size - less > more - size ? sqrt + 1 : sqrt;
        }

        class Category {
            private final List<ResourceLocation> list = new ArrayList<>();

            public Category add(String path) {
                list.add(AchievementUtils.asAchievement(path));
                return this;
            }

            public Builder pop() {
                return Builder.this;
            }
        }
    }
}
