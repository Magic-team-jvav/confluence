package org.confluence.mod.common.data.gen;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.util.AchievementUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ModAchievementOffsetProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider pathProvider;
    private final Codec<Map<ResourceLocation, AchievementOffset>> codec;

    protected ModAchievementOffsetProvider(
            PackOutput output,
            PackOutput.Target target,
            Codec<Map<ResourceLocation, AchievementOffset>> codec
    ) {
        super(output);
        this.codec = codec;
        this.pathProvider = output.createPathProvider(target, "");
    }

    public static ModAchievementOffsetProvider client(PackOutput output) {
        return new ModAchievementOffsetProvider(
                output, PackOutput.Target.RESOURCE_PACK,
                Codec.unboundedMap(ResourceLocation.CODEC, AchievementOffset.CLIENT_CODEC)
        );
    }

    public static ModAchievementOffsetProvider server(PackOutput output) {
        return new ModAchievementOffsetProvider(
                output, PackOutput.Target.DATA_PACK,
                Codec.unboundedMap(ResourceLocation.CODEC, AchievementOffset.SERVER_CODEC)
        );
    }

    /// Collector Explorer
    /// Slayer    Challenger
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        recipe(codec, pathProvider().json(Confluence.asResource("achievement_offset"))).addRecipe(new Builder()
                // Collector
                .push(true, true)
                .add("new_world", 0)
                .add("timber", 10)
                .add("benched", 20)
                .add("hammer_time", 40)
                .add("heavy_metal", 70)
                .add("star_power", 90)
                .add("hold_on_tight", 100)
                .add("miner_for_fire", 200)
                .add("head_in_the_clouds", 250)
                .add("like_a_boss", 260)
                .add("drax_attax", 280)
                .add("temple_raider", 320)
                .add("fashion_statement", 580)
                .add("sword_of_the_hero", 630)
                .add("dye_hard", 660)
                .add("sick_throw", 670)
                .add("the_cavalry", 690)
                .add("completely_awesome", 700)
                .add("prismancer", 760)
                .add("glorious_golden_pole", 800)
                .add("matching_attire", 890)
                .add("infinity_1_sword", 940)
                .add("boots_of_the_hero", 950)
                .add("feast_of_midas", 1060)
                .add("black_mirror", 1080)
                .add("ankhumulation_complete", 1090)
                .add("organized_chaos", 1250)
                .add("on_fleek", 1260)
                .add("mini_me", 1290)
                .add("new_digs", 1310)
                .add("sea_you_later", 1340)
                .pop()
                // Explorer
                .push(true, false)
                .add("no_hobo", 30)
                .add("ooo_shinny", 50)
                .add("heart_breaker", 60)
                .add("i_am_loot", 80)
                .add("smashing_poppet", 120)
                .add("wheres_my_honey", 150)
                .add("dungeon_heist", 180)
                .add("its_getting_hot_in_here", 190)
                .add("its_hard", 220)
                .add("begone_evil", 230)
                .add("extra_shiny", 240)
                .add("photosynthesis", 290)
                .add("get_a_life", 300)
                .add("robbing_the_grave", 340)
                .add("big_booty", 350)
                .add("bloodbath", 400)
                .add("kill_the_sun", 440)
                .add("sticky_situation", 490)
                .add("jeepers_creepers", 520)
                .add("funkytown", 530)
                .add("into_orbit", 540)
                .add("rock_bottom", 550)
                .add("it_can_talk", 770)
                .add("watch_your_step", 780)
                .add("you_can_do_it", 880)
                .add("quiet_neighborhood", 970)
                .add("hey_listen", 1020)
                .add("a_rare_realm", 1110)
                .add("a_shimmer_in_the_dark", 1130)
                .add("its_shaling_outside", 1210)
                .add("fortune_favors_the_bould", 1270)
                .add("training_day", 1280)
                .pop()
                // Slayer
                .push(false, true)
                .add("eye_on_you", 110)
                .add("worm_fodder", 130)
                .add("mastermind", 140)
                .add("sting_operation", 160)
                .add("boned", 170)
                .add("still_hungry", 210)
                .add("buckets_of_bolts", 270)
                .add("the_great_southern_plantkill", 310)
                .add("lihzahrdian_idol", 330)
                .add("fish_out_of_water", 360)
                .add("obsessive_devotion", 370)
                .add("star_destroyer", 380)
                .add("champion_of_terraria", 390)
                .add("slippery_shinobi", 410)
                .add("goblin_punter", 420)
                .add("walk_the_plank", 430)
                .add("do_you_want_to_slay_a_snowman", 450)
                .add("tin_foil_hatter", 460)
                .add("baleful_harvest", 470)
                .add("ice_scream", 480)
                .add("vehicular_manslaughter", 590)
                .add("there_are_some_who_call_him", 610)
                .add("deceiver_of_fools", 620)
                .add("til_death", 710)
                .add("archaeologist", 720)
                .add("pretty_in_pink", 730)
                .add("fae_flayer", 900)
                .add("just_desserts", 910)
                .add("dont_dread_on_me", 920)
                .add("hero_of_etheria", 930)
                .add("an_eye_for_an_eye", 1050)
                .add("torch_god", 1100)
                .pop()
                // Challenger
                .push(false, false)
                .add("real_estate_agent", 500)
                .add("not_the_bees", 510)
                .add("mecha_mayhem", 560)
                .add("gelatin_world_tour", 570)
                .add("bulldozer", 600)
                .add("lucky_break", 640)
                .add("throwing_lines", 650)
                .add("the_frequent_flyer", 680)
                .add("rainbows_and_unicorns", 740)
                .add("you_and_what_army", 750)
                .add("marathon_medalist", 790)
                .add("servant_in_training", 810)
                .add("good_little_slave", 820)
                .add("trout_monkey", 830)
                .add("fast_and_fishious", 840)
                .add("supreme_helper_minion", 850)
                .add("topped_off", 860)
                .add("slayer_of_worlds", 870)
                .add("a_rather_blustery_day", 960)
                .add("hot_reels", 980)
                .add("heliophobia", 990)
                .add("leading_landlord", 1000)
                .add("feeling_petty", 1010)
                .add("jolly_jamboree", 1030)
                .add("dead_men_tell_no_tales", 1040)
                .add("unusual_survival_strategies", 1070)
                .add("the_great_slime_mitosis", 1120)
                .add("and_good_riddance", 1140)
                .add("to_infinity_and_beyond", 1150)
                .add("book_worm", 1160)
                .add("boulder_lord", 1170)
                .add("queen_machine", 1180)
                .add("rollin_in_your_grave", 1190)
                .add("fear_the_sun", 1200)
                .add("extra_life", 1220)
                .add("grave_mistake", 1230)
                .add("spicy_licks", 1240)
                .add("terrarist", 1300)
                .add("my_people_need_me", 1320)
                .add("going_oldschool", 1330)
                .add("trash_compactor", 1350)
                .add("conservationist", 1360)
                .add("interdimensional_recycling", 1370)
                .pop()
                .build());
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return pathProvider;
    }

    static class Builder {
        private final CategoryBuilder topLeft = new CategoryBuilder(AchievementOffset.Category.COLLECTOR);
        private final CategoryBuilder topRight = new CategoryBuilder(AchievementOffset.Category.EXPLORER);
        private final CategoryBuilder bottomLeft = new CategoryBuilder(AchievementOffset.Category.SLAYER);
        private final CategoryBuilder bottomRight = new CategoryBuilder(AchievementOffset.Category.CHALLENGER);

        public CategoryBuilder push(boolean top, boolean left) {
            return top ? (left ? topLeft : topRight) : (left ? bottomLeft : bottomRight);
        }

        public Map<ResourceLocation, AchievementOffset> build() {
            Map<ResourceLocation, AchievementOffset> offsetMap = new LinkedHashMap<>();

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

        private static void calculateOffset(CategoryBuilder categoryBuilder, int length, float startX, float startY, Map<ResourceLocation, AchievementOffset> offsetMap) {
            int size = categoryBuilder.list.size();
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
                ObjectIntPair<ResourceLocation> pair = categoryBuilder.list.get(i);
                offsetMap.put(pair.left(), new AchievementOffset(startX + x, startY + y, categoryBuilder.category, pair.rightInt()));
            }
        }

        private static int getLength(int size) {
            int sqrt = (int) Math.sqrt(size);
            int less = Mth.square(sqrt);
            int more = Mth.square(sqrt + 1);
            return size - less > more - size ? sqrt + 1 : sqrt;
        }

        class CategoryBuilder {
            private final List<ObjectIntPair<ResourceLocation>> list = new ArrayList<>();
            private final AchievementOffset.Category category;

            CategoryBuilder(AchievementOffset.Category category) {
                this.category = category;
            }

            public CategoryBuilder add(String path, int order) {
                list.add(new ObjectIntImmutablePair<>(AchievementUtils.asAchievement(path), order));
                return this;
            }

            public Builder pop() {
                return Builder.this;
            }
        }
    }
}
