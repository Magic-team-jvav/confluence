package org.confluence.mod.client.gui;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Hashtable;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class AchievementToast implements Toast {
    public static final Component DISPLAY = Component.translatable("achievements.toast.complete");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/achievement/toast.png");
    private static final Hashtable<ResourceLocation, AchievementToast> ACHIEVEMENTS = new Hashtable<>();
    private final ResourceLocation icon;
    private final Display display;
    public boolean playedSound;

    public AchievementToast(ResourceLocation icon, Display display) {
        this.icon = icon;
        this.display = display;
        this.playedSound = false;
    }

    @Override
    public int height() {
        return 64;
    }

    @Override
    public Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        Font font = pToastComponent.getMinecraft().font;
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0F, pGuiGraphics.guiHeight() - height(), 0.0F);
        pGuiGraphics.blit(TEXTURE, 0, 0, 0, 0, width(), height(), 160, 64);
        renderTitle(pGuiGraphics, pTimeSinceLastVisible, font);
        renderDescription(pGuiGraphics, font);
        renderIcon(pGuiGraphics);
        pGuiGraphics.pose().popPose();
        playSound(pToastComponent, pTimeSinceLastVisible);
        return (double) pTimeSinceLastVisible >= 5000.0 * pToastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    private void playSound(ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        if (!playedSound && pTimeSinceLastVisible > 0L) {
            this.playedSound = true;
            if (display.type() == AdvancementType.CHALLENGE) {
                pToastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.ACHIEVEMENTS.get(), 1.0F, 1.0F));
            }
        }
    }

    private void renderDescription(GuiGraphics pGuiGraphics, Font font) {
        List<FormattedCharSequence> list = font.split(display.description(), 141);
        if (list.size() == 1) {
            pGuiGraphics.drawString(font, list.getFirst(), 8, 44, 0, false);
        } else {
            int l = 48 - list.size() * 9 / 2;
            for (FormattedCharSequence formattedcharsequence : list) {
                pGuiGraphics.drawString(font, formattedcharsequence, 8, l, 0, false);
                l += 9;
            }
        }
    }

    private void renderIcon(@NotNull GuiGraphics pGuiGraphics) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(4, 4, 0);
        pGuiGraphics.pose().scale(0.375F, 0.375F, 1.0F);
        pGuiGraphics.blit(icon, 0, 0, 0, 0, 64, 64, 64, 64);
        pGuiGraphics.pose().popPose();
    }

    private void renderTitle(GuiGraphics pGuiGraphics, long pTimeSinceLastVisible, Font font) {
        List<FormattedCharSequence> list = font.split(display.title(), 125);
        int i = display.type() == AdvancementType.CHALLENGE ? 16746751 : 16776960;
        if (list.size() == 1) {
            pGuiGraphics.drawString(font, DISPLAY, 30, 7, i | -16777216, false);
            pGuiGraphics.drawString(font, list.getFirst(), 30, 18, -1, false);
        } else {
            if (pTimeSinceLastVisible < 1500L) {
                int k = Mth.floor(Mth.clamp((float) (1500L - pTimeSinceLastVisible) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                pGuiGraphics.drawString(font, DISPLAY, 30, 11, i | k, false);
            } else {
                int i1 = Mth.floor(Mth.clamp((float) (pTimeSinceLastVisible - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                int l = 16 - list.size() * 9 / 2;

                for (FormattedCharSequence formattedcharsequence : list) {
                    pGuiGraphics.drawString(font, formattedcharsequence, 30, l, 16777215 | i1, false);
                    l += 9;
                }
            }
        }
    }

    public static boolean renderWidgetIcon(ResourceLocation id, GuiGraphics guiGraphics, int x, int y) {
        AchievementToast achievementToast = getToast(id);
        if (achievementToast == null) return true;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(0.25F, 0.25F, 1.0F);
        guiGraphics.blit(achievementToast.icon, 0, 0, 0, 0, 64, 64, 64, 64);
        guiGraphics.pose().popPose();
        return false;
    }

    public static void registerToast(ResourceLocation advancement, AchievementToast toast) {
        ACHIEVEMENTS.put(advancement, toast);
    }

    public static void registerToast(String namespace, String path) {
        registerToast(ResourceLocation.fromNamespaceAndPath(namespace, ModAchievements.PREFIX + path), new AchievementToast(
                ResourceLocation.fromNamespaceAndPath(namespace, "textures/achievement/" + path + ".png"),
                new Display(AdvancementType.CHALLENGE,
                        Component.translatable("achievements." + namespace + "." + path + ".title"),
                        Component.translatable("achievements." + namespace + "." + path + ".description")
                )));
    }

    public static void registerToast(String path) {
        registerToast(Confluence.MODID, path);
    }

    public static @Nullable AchievementToast getToast(ResourceLocation advancement) {
        return ACHIEVEMENTS.get(advancement);
    }

    public static void removeToast(ResourceLocation advancement) {
        ACHIEVEMENTS.remove(advancement);
    }

    public static void clearToast() {
        ACHIEVEMENTS.clear();
    }

    public static void registerAll() {
        // Collector
        AchievementToast.registerToast("timber");
        AchievementToast.registerToast("benched");
        AchievementToast.registerToast("hammer_time");
        AchievementToast.registerToast("heavy_metal");
        AchievementToast.registerToast("matching_attire");
        AchievementToast.registerToast("star_power");
        AchievementToast.registerToast("hold_on_tight");
        AchievementToast.registerToast("miner_for_fire");
        AchievementToast.registerToast("head_in_the_clouds");
        AchievementToast.registerToast("like_a_boss");
        AchievementToast.registerToast("feast_of_midas");
        AchievementToast.registerToast("drax_attax");
        AchievementToast.registerToast("fashion_statement");
        AchievementToast.registerToast("sword_of_the_hero");
        AchievementToast.registerToast("dye_hard");
        AchievementToast.registerToast("sick_throw");
        AchievementToast.registerToast("the_cavalry");
        AchievementToast.registerToast("completely_awesome");
        AchievementToast.registerToast("prismancer");
        AchievementToast.registerToast("glorious_golden_pole");
        AchievementToast.registerToast("boots_of_the_hero");
        AchievementToast.registerToast("infinity_1_sword");
        AchievementToast.registerToast("black_mirror");
        AchievementToast.registerToast("ankhumulation_complete");

        // Explorer
        AchievementToast.registerToast("new_world");
        AchievementToast.registerToast("you_can_do_it");
        AchievementToast.registerToast("no_hobo");
        AchievementToast.registerToast("ooo_shinny");
        AchievementToast.registerToast("heart_breaker");
        AchievementToast.registerToast("i_am_loot");
        AchievementToast.registerToast("quiet_neighborhood");
        AchievementToast.registerToast("hey_listen");
        AchievementToast.registerToast("smashing_poppet");
        AchievementToast.registerToast("wheres_my_honey");
        AchievementToast.registerToast("dungeon_heist");
        AchievementToast.registerToast("its_getting_hot_in_here");
        AchievementToast.registerToast("its_hard");
        AchievementToast.registerToast("begone_evil");
        AchievementToast.registerToast("extra_shiny");
        AchievementToast.registerToast("photosynthesis");
        AchievementToast.registerToast("get_a_life");
        AchievementToast.registerToast("temple_raider");
        AchievementToast.registerToast("robbing_the_grave");
        AchievementToast.registerToast("big_booty");
        AchievementToast.registerToast("jeepers_creepers");
        AchievementToast.registerToast("funkytown");
        AchievementToast.registerToast("into_orbit");
        AchievementToast.registerToast("rock_bottom");
        AchievementToast.registerToast("it_can_talk");
        AchievementToast.registerToast("watch_your_step");
        AchievementToast.registerToast("rock_bottom");
        AchievementToast.registerToast("a_rare_realm");
        AchievementToast.registerToast("a_shimmer_in_the_dark");

        // Slayer boss
        AchievementToast.registerToast("slippery_shinobi");
        AchievementToast.registerToast("eye_on_you");
        AchievementToast.registerToast("worm_fodder");
        AchievementToast.registerToast("mastermind");
        AchievementToast.registerToast("sting_operation");
        AchievementToast.registerToast("boned");
        AchievementToast.registerToast("an_eye_for_an_eye");
        AchievementToast.registerToast("still_hungry");
        AchievementToast.registerToast("just_desserts");
        AchievementToast.registerToast("buckets_of_bolts");
        AchievementToast.registerToast("the_great_southern_plantkill");
        AchievementToast.registerToast("lihzahrdian_idol");
        AchievementToast.registerToast("fish_out_of_water");
        AchievementToast.registerToast("fae_flayer");
        AchievementToast.registerToast("obsessive_devotion");
        AchievementToast.registerToast("star_destroyer");
        AchievementToast.registerToast("champion_of_terraria");
        // Slayer event
        AchievementToast.registerToast("bloodbath");
        AchievementToast.registerToast("sticky_situation");
        AchievementToast.registerToast("goblin_punter");
        AchievementToast.registerToast("hero_of_etheria");
        AchievementToast.registerToast("walk_the_plank");
        AchievementToast.registerToast("dont_dread_on_me");
        AchievementToast.registerToast("kill_the_sun");
        AchievementToast.registerToast("do_you_want_to_slay_a_snowman");
        AchievementToast.registerToast("tin_foil_hatter");
        AchievementToast.registerToast("baleful_harvest");
        AchievementToast.registerToast("ice_scream");

        AchievementToast.registerToast("vehicular_manslaughter");
        AchievementToast.registerToast("there_are_some_who_call_him");
        AchievementToast.registerToast("deceiver_of_fools");
        AchievementToast.registerToast("til_death");
        AchievementToast.registerToast("archaeologist");
        AchievementToast.registerToast("pretty_in_pink");
        AchievementToast.registerToast("archaeologist");
        AchievementToast.registerToast("torch_god");

        // Challenger
        AchievementToast.registerToast("real_estate_agent");
        AchievementToast.registerToast("not_the_bees");
        AchievementToast.registerToast("mecha_mayhem");
        AchievementToast.registerToast("gelatin_world_tour");
        AchievementToast.registerToast("bulldozer");
        AchievementToast.registerToast("lucky_break");
        AchievementToast.registerToast("throwing_lines");
        AchievementToast.registerToast("the_frequent_flyer");
        AchievementToast.registerToast("rainbows_and_unicorns");
        AchievementToast.registerToast("you_and_what_army");
        AchievementToast.registerToast("marathon_medalist");
        AchievementToast.registerToast("servant_in_training");
        AchievementToast.registerToast("good_little_slave");
        AchievementToast.registerToast("trout_monkey");
        AchievementToast.registerToast("fast_and_fishious");
        AchievementToast.registerToast("supreme_helper_minion");
        AchievementToast.registerToast("topped_off");
        AchievementToast.registerToast("slayer_of_worlds");
        AchievementToast.registerToast("a_rather_blustery_day");
        AchievementToast.registerToast("hot_reels");
        AchievementToast.registerToast("heliophobia");
        AchievementToast.registerToast("leading_landlord");
        AchievementToast.registerToast("feeling_petty");
        AchievementToast.registerToast("jolly_jamboree");
        AchievementToast.registerToast("dead_men_tell_no_tales");
        AchievementToast.registerToast("unusual_survival_strategies");
        AchievementToast.registerToast("the_great_slime_mitosis");
        AchievementToast.registerToast("and_good_riddance");
        AchievementToast.registerToast("to_infinity_and_beyond");
    }

    public record Display(AdvancementType type, Component title, Component description) {}
}
