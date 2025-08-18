package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.potion.*;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.common.init.TCEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PotionItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BottleItem> BOTTLE = ITEMS.register("bottle", BottleItem::new);
    public static final DeferredItem<AbstractPotionItem> BOTTLED_WATER = ITEMS.register("bottled_water", () -> new AbstractPotionItem(new Item.Properties().stacksTo(16)) {
        @Override
        protected void apply(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity living) {
            if (living instanceof ServerPlayer player) AchievementUtils.unusualSurvivalStrategies(player, true);
        }
    });

    public static final DeferredItem<TooltipItem> MUG = ITEMS.register("mug", () -> new TooltipItem(new Item.Properties(), ModRarity.GRAY, Component.translatable("tooltip.item.confluence.mug.0")));
    public static final DeferredItem<AbstractPotionItem> ALE = ITEMS.register("ale", () -> new EffectPotionItem(ModEffects.TIPSY, 2400) {
        @Override
        protected @NotNull ItemStack getReturnItem() {
            return MUG.toStack();
        }
    });
    public static final DeferredItem<AbstractPotionItem> ARCHERY_POTION = ITEMS.register("archery_potion", () -> new EffectPotionItem(ModEffects.ARCHERY, 9600));
    public static final DeferredItem<AbstractPotionItem> BUILDER_POTION = ITEMS.register("builder_potion", () -> new EffectPotionItem(ModEffects.BUILDER, 54000));
    public static final DeferredItem<AbstractPotionItem> DANGERSENSE_POTION = ITEMS.register("dangersense_potion", () -> new EffectPotionItem(ModEffects.DANGER_SENSE, 4800));
    public static final DeferredItem<AbstractPotionItem> ENDURANCE_POTION = ITEMS.register("endurance_potion", () -> new EffectPotionItem(ModEffects.ENDURANCE, 4800));
    public static final DeferredItem<AbstractPotionItem> FEATHERFALL_POTION = ITEMS.register("featherfall_potion", () -> new EffectPotionItem(MobEffects.SLOW_FALLING, 12000));
    public static final DeferredItem<AbstractPotionItem> FLIPPER_POTION = ITEMS.register("flipper_potion", () -> new EffectPotionItem(ModEffects.FLIPPER, 9600));
    public static final DeferredItem<AbstractPotionItem> FISHING_POTION = ITEMS.register("fishing_potion", () -> new EffectPotionItem(ModEffects.FISHING, 9600));
    public static final DeferredItem<AbstractPotionItem> GILLS_POTION = ITEMS.register("gills_potion", () -> new EffectPotionItem(MobEffects.WATER_BREATHING, 4800));
    public static final DeferredItem<AbstractPotionItem> GRAVITATION_POTION = ITEMS.register("gravitation_potion", () -> new EffectPotionItem(TCEffects.GRAVITATION, 3600));
    public static final DeferredItem<AbstractPotionItem> HEART_REACH_POTION = ITEMS.register("heart_reach_potion", () -> new EffectPotionItem(ModEffects.HEART_REACH, 9600));
    public static final DeferredItem<AbstractPotionItem> HUNTER_POTION = ITEMS.register("hunter_potion", () -> new EffectPotionItem(ModEffects.HUNTER, 9600));
    public static final DeferredItem<AbstractPotionItem> INFERNO_POTION = ITEMS.register("inferno_potion", () -> new EffectPotionItem(ModEffects.INFERNO, 4800));
    public static final DeferredItem<AbstractPotionItem> INVISIBILITY_POTION = ITEMS.register("invisibility_potion", () -> new EffectPotionItem(MobEffects.INVISIBILITY, 3600));
    public static final DeferredItem<AbstractPotionItem> IRON_SKIN_POTION = ITEMS.register("iron_skin_potion", () -> new EffectPotionItem(ModEffects.IRON_SKIN, 9600));
    public static final DeferredItem<AbstractPotionItem> LIFEFORCE_POTION = ITEMS.register("lifeforce_potion", () -> new EffectPotionItem(ModEffects.LIFE_FORCE, 9600));
    public static final DeferredItem<AbstractPotionItem> LOVE_POTION = ITEMS.register("love_potion", () -> new EffectThrowablePotionItem(ModEffects.LOVE, 600));
    public static final DeferredItem<AbstractPotionItem> LESSER_LUCK_POTION = ITEMS.register("lesser_luck_potion", () -> new EffectPotionItem(ModEffects.LUCK_EFFECT, 6000));
    public static final DeferredItem<AbstractPotionItem> LUCK_POTION = ITEMS.register("luck_potion", () -> new EffectPotionItem(ModEffects.LUCK_EFFECT, 6000, 1));
    public static final DeferredItem<AbstractPotionItem> GREATER_LUCK_POTION = ITEMS.register("greater_luck_potion", () -> new EffectPotionItem(ModEffects.LUCK_EFFECT, 6000, 2));
    public static final DeferredItem<AbstractPotionItem> MANA_REGENERATION_POTION = ITEMS.register("mana_regeneration_potion", () -> new EffectPotionItem(ModEffects.MANA_REGENERATION, 9600));
    public static final DeferredItem<AbstractPotionItem> MAGIC_POWER_POTION = ITEMS.register("magic_power_potion", () -> new EffectPotionItem(ModEffects.MAGIC_POWER, 4800));
    public static final DeferredItem<AbstractPotionItem> MINING_POTION = ITEMS.register("mining_potion", () -> new EffectPotionItem(MobEffects.DIG_SPEED, 12000, 1));
    public static final DeferredItem<AbstractPotionItem> NIGHT_OWL_POTION = ITEMS.register("night_owl_potion", () -> new EffectPotionItem(MobEffects.NIGHT_VISION, 6000));
    public static final DeferredItem<AbstractPotionItem> OBSIDIAN_SKIN_POTION = ITEMS.register("obsidian_skin_potion", () -> new EffectPotionItem(ModEffects.OBSIDIAN_SKIN, 7200));
    public static final DeferredItem<AbstractPotionItem> RAGE_POTION = ITEMS.register("rage_potion", () -> new EffectPotionItem(ModEffects.RAGE, 4800));
    public static final DeferredItem<AbstractPotionItem> RECALL_POTION = ITEMS.register("recall_potion", RecallPotionItem::new);
    public static final DeferredItem<AbstractPotionItem> REGENERATION_POTION = ITEMS.register("regeneration_potion", () -> new EffectPotionItem(MobEffects.REGENERATION, 9600));
    public static final DeferredItem<AbstractPotionItem> SHINE_POTION = ITEMS.register("shine_potion", () -> new EffectPotionItem(ModEffects.SHINE, 12000) {
        private final boolean noneLoaded = !ModList.get().isLoaded("sodiumdynamiclights");

        @ParametersAreNonnullByDefault
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (noneLoaded) tooltipComponents.add(Component.translatable("tooltip.terra_curio.requires_mod_loaded", "sodiumdynamiclights"));
        }
    });
    public static final DeferredItem<AbstractPotionItem> SPELUNKER_POTION = ITEMS.register("spelunker_potion", () -> new EffectPotionItem(ModEffects.SPELUNKER, 6000));
    public static final DeferredItem<AbstractPotionItem> SWIFTNESS_POTION = ITEMS.register("swiftness_potion", () -> new EffectPotionItem(MobEffects.MOVEMENT_SPEED, 9600));
    public static final DeferredItem<AbstractPotionItem> THORNS_POTION = ITEMS.register("thorns_potion", () -> new EffectPotionItem(ModEffects.THORNS, 9600));
    public static final DeferredItem<AbstractPotionItem> TITAN_POTION = ITEMS.register("titan_potion", () -> new EffectPotionItem(ModEffects.TITAN, 9600));
    public static final DeferredItem<AbstractPotionItem> WATER_WALKING_POTION = ITEMS.register("water_walking_potion", () -> new EffectPotionItem(ModEffects.WATER_WALKING, 12000));
    public static final DeferredItem<AbstractPotionItem> WRATH_POTION = ITEMS.register("wrath_potion", () -> new EffectPotionItem(ModEffects.WRATH, 4800));
    public static final DeferredItem<AbstractPotionItem> LESSER_HEALING_POTION = ITEMS.register("lesser_healing_potion", () -> new HealingPotionItem(10, ModRarity.WHITE));
    public static final DeferredItem<AbstractPotionItem> EGGNOG = ITEMS.register("eggnog", () -> new HealingPotionItem(16, ModRarity.WHITE));
    public static final DeferredItem<AbstractPotionItem> RESTORATION_POTION = ITEMS.register("restoration_potion", () -> new HealingPotionItem(18, ModRarity.BLUE));
    public static final DeferredItem<AbstractPotionItem> HEALING_POTION = ITEMS.register("healing_potion", () -> new HealingPotionItem(20, ModRarity.BLUE));
    public static final DeferredItem<AbstractPotionItem> GREATER_HEALING_POTION = ITEMS.register("greater_healing_potion", () -> new HealingPotionItem(40, ModRarity.ORANGE));
    public static final DeferredItem<AbstractPotionItem> SUPER_HEALING_POTION = ITEMS.register("super_healing_potion", () -> new HealingPotionItem(60, ModRarity.LIME));
    public static final DeferredItem<AbstractPotionItem> LESSER_MANA_POTION = ITEMS.register("lesser_mana_potion", () -> new ManaPotionItem(50, ModRarity.WHITE));
    public static final DeferredItem<AbstractPotionItem> MANA_POTION = ITEMS.register("mana_potion", () -> new ManaPotionItem(100, ModRarity.BLUE));
    public static final DeferredItem<AbstractPotionItem> GREATER_MANA_POTION = ITEMS.register("greater_mana_potion", () -> new ManaPotionItem(200, ModRarity.ORANGE));
    public static final DeferredItem<AbstractPotionItem> SUPER_MANA_POTION = ITEMS.register("super_mana_potion", () -> new ManaPotionItem(300, ModRarity.LIGHT_RED));
    public static final DeferredItem<AbstractPotionItem> RANDOM_TELEPORT_POTION = ITEMS.register("random_teleport_potion", RandomTeleportPotionItem::new);
    public static final DeferredItem<AbstractPotionItem> CRATE_POTION = ITEMS.register("crate_potion", () -> new EffectPotionItem(ModEffects.CRATE, 4800));
    public static final DeferredItem<AbstractPotionItem> STINK_POTION = ITEMS.register("stink_potion", () -> new EffectThrowablePotionItem(ModRarity.WHITE, ModEffects.STINKY, 600));
    public static final DeferredItem<CustomRarityItem> WORMHOLE_POTION = ITEMS.register("wormhole_potion", () -> new CustomRarityItem(new Item.Properties().stacksTo(16), ModRarity.BLUE));
    public static final DeferredItem<AbstractPotionItem> AMMO_RESERVATION_POTION = ITEMS.register("ammo_reservation_potion", () -> new EffectPotionItem(ModEffects.AMMO_BOX, 9600));
    public static final DeferredItem<AbstractPotionItem> SUMMONING_POTION = ITEMS.register("summoning_potion", () -> new EffectPotionItem(ModEffects.SUMMONING, 9600));
    public static final DeferredItem<AbstractPotionItem> SHIMMER_POTION = ITEMS.register("shimmer_potion", () -> new EffectPotionItem(ModEffects.SHIMMER, 1200));

    public static final DeferredItem<AbstractPotionItem> RED_POTION = ITEMS.register("red_potion", RedPotionItem::new);
    public static final DeferredItem<AbstractPotionItem> CHAOS_POTION = ITEMS.register("chaos_potion", ChaosPotionItem::new);
    public static final DeferredItem<AbstractPotionItem> STRANGE_BREW = ITEMS.register("strange_brew", StrangeBrewItem::new);

    public static final DeferredItem<AbstractPotionItem> FLASK_OF_FIRE = ITEMS.register("flask_of_fire", () -> new EffectPotionItem(ModRarity.LIGHT_RED, ModEffects.WEAPON_IMBUE_FIRE, 24000));
    public static final DeferredItem<AbstractPotionItem> FLASK_OF_GOLD = ITEMS.register("flask_of_gold", () -> new EffectPotionItem(ModRarity.LIGHT_RED, ModEffects.WEAPON_IMBUE_GOLD, 24000));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof AbstractPotionItem item1) {
                tag.add(item1);
            }
        });
    }
}
