package org.confluence.mod.common.init.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.potion.*;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class PotionItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BottleItem> BOTTLE = registerItem("bottle", "玻璃瓶", BottleItem::new);
    public static final PortDeferredItem<AbstractPotionItem> BOTTLED_WATER = registerItem("bottled_water", "瓶装水", () -> new AbstractPotionItem(new Item.Properties().stacksTo(16)) {
        @Override
        protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
            if (living instanceof ServerPlayer player) {
                AchievementUtils.unusualSurvivalStrategies(player, true);
            }
        }
    });

    public static final PortDeferredItem<CustomRarityItem> MUG = registerItem("mug", "玻璃杯", () -> new CustomRarityItem(new Item.Properties(), ModRarity.GRAY));
    public static final PortDeferredItem<AbstractPotionItem> ALE = registerItem("ale", "麦芽酒", () -> new EffectPotionItem(ModEffects.TIPSY, 2400) {
        @Override
        protected ItemStack getReturnItem() {
            return MUG.toStack();
        }
    });
    public static final PortDeferredItem<AbstractPotionItem> ARCHERY_POTION = registerItem("archery_potion", "箭术药水", () -> new EffectPotionItem(ModEffects.ARCHERY, 9600));
    public static final PortDeferredItem<AbstractPotionItem> BUILDER_POTION = registerItem("builder_potion", "建筑工药水", () -> new EffectPotionItem(ModEffects.BUILDER, 54000));
    public static final PortDeferredItem<AbstractPotionItem> DANGERSENSE_POTION = registerItem("dangersense_potion", "危险感药水", () -> new EffectPotionItem(ModEffects.DANGER_SENSE, 4800));
    public static final PortDeferredItem<AbstractPotionItem> ENDURANCE_POTION = registerItem("endurance_potion", "耐力药水", () -> new EffectPotionItem(ModEffects.ENDURANCE, 4800));
    public static final PortDeferredItem<AbstractPotionItem> FEATHERFALL_POTION = registerItem("featherfall_potion", "羽落药水", () -> new EffectPotionItem(supplier(MobEffects.SLOW_FALLING), 12000));
    public static final PortDeferredItem<AbstractPotionItem> FLIPPER_POTION = registerItem("flipper_potion", "脚蹼药水", () -> new EffectPotionItem(ModEffects.FLIPPER, 9600));
    public static final PortDeferredItem<AbstractPotionItem> FISHING_POTION = registerItem("fishing_potion", "钓鱼药水", () -> new EffectPotionItem(ModEffects.FISHING, 9600));
    public static final PortDeferredItem<AbstractPotionItem> GILLS_POTION = registerItem("gills_potion", "鱼鳃药水", () -> new EffectPotionItem(supplier(MobEffects.WATER_BREATHING), 4800));
    public static final PortDeferredItem<AbstractPotionItem> GRAVITATION_POTION = registerItem("gravitation_potion", "重力药水", () -> new EffectPotionItem(LibEffects.GRAVITATION, 3600));
    public static final PortDeferredItem<AbstractPotionItem> HEART_REACH_POTION = registerItemWithEnglishName("heart_reach_potion", "拾心药水", "Heartreach Potion", () -> new EffectPotionItem(ModEffects.HEART_REACH, 9600));
    public static final PortDeferredItem<AbstractPotionItem> HUNTER_POTION = registerItem("hunter_potion", "狩猎药水", () -> new EffectPotionItem(ModEffects.HUNTER, 9600));
    public static final PortDeferredItem<AbstractPotionItem> INFERNO_POTION = registerItem("inferno_potion", "狱火药水", () -> new EffectPotionItem(ModEffects.INFERNO, 4800));
    public static final PortDeferredItem<AbstractPotionItem> INVISIBILITY_POTION = registerItem("invisibility_potion", "隐身药水", () -> new EffectPotionItem(supplier(MobEffects.INVISIBILITY), 3600));
    public static final PortDeferredItem<AbstractPotionItem> IRON_SKIN_POTION = registerItemWithEnglishName("iron_skin_potion", "铁皮药水", "Ironskin Potion", () -> new EffectPotionItem(ModEffects.IRON_SKIN, 9600));
    public static final PortDeferredItem<AbstractPotionItem> LIFEFORCE_POTION = registerItem("lifeforce_potion", "生命力药水", () -> new EffectPotionItem(ModEffects.LIFE_FORCE, 9600));
    public static final PortDeferredItem<AbstractPotionItem> LOVE_POTION = registerItem("love_potion", "爱情药水", () -> new EffectThrowablePotionItem(ModEffects.LOVE, 600));
    public static final PortDeferredItem<AbstractPotionItem> LESSER_LUCK_POTION = registerItem("lesser_luck_potion", "弱效幸运药水", () -> new EffectPotionItem(ModEffects.LUCK_EFFECT, 6000));
    public static final PortDeferredItem<AbstractPotionItem> LUCK_POTION = registerItem("luck_potion", "幸运药水", () -> new EffectPotionItem(ModEffects.LUCK_EFFECT, 6000, 1));
    public static final PortDeferredItem<AbstractPotionItem> GREATER_LUCK_POTION = registerItem("greater_luck_potion", "强效幸运药水", () -> new EffectPotionItem(ModEffects.LUCK_EFFECT, 6000, 2));
    public static final PortDeferredItem<AbstractPotionItem> MANA_REGENERATION_POTION = registerItem("mana_regeneration_potion", "魔力再生药水", () -> new EffectPotionItem(ModEffects.MANA_REGENERATION, 9600));
    public static final PortDeferredItem<AbstractPotionItem> MAGIC_POWER_POTION = registerItem("magic_power_potion", "魔能药水", () -> new EffectPotionItem(ModEffects.MAGIC_POWER, 4800));
    public static final PortDeferredItem<AbstractPotionItem> MINING_POTION = registerItem("mining_potion", "挖矿药水", () -> new EffectPotionItem(supplier(MobEffects.DIG_SPEED), 12000, 1));
    public static final PortDeferredItem<AbstractPotionItem> NIGHT_OWL_POTION = registerItem("night_owl_potion", "夜猫子药水", () -> new EffectPotionItem(supplier(MobEffects.NIGHT_VISION), 6000));
    public static final PortDeferredItem<AbstractPotionItem> OBSIDIAN_SKIN_POTION = registerItem("obsidian_skin_potion", "黑曜石皮药水", () -> new EffectPotionItem(ModEffects.OBSIDIAN_SKIN, 7200));
    public static final PortDeferredItem<AbstractPotionItem> RAGE_POTION = registerItem("rage_potion", "暴怒药水", () -> new EffectPotionItem(ModEffects.RAGE, 4800));
    public static final PortDeferredItem<AbstractPotionItem> RECALL_POTION = registerItem("recall_potion", "回忆药水", RecallPotionItem::new);
    public static final PortDeferredItem<AbstractPotionItem> REGENERATION_POTION = registerItem("regeneration_potion", "再生药水", () -> new EffectPotionItem(supplier(MobEffects.REGENERATION), 9600));
    public static final PortDeferredItem<AbstractPotionItem> SHINE_POTION = registerItem("shine_potion", "光芒药水", () -> new EffectPotionItem(ModEffects.SHINE, 12000));
    public static final PortDeferredItem<AbstractPotionItem> SPELUNKER_POTION = registerItem("spelunker_potion", "洞穴探险药水", () -> new EffectPotionItem(ModEffects.SPELUNKER, 6000));
    public static final PortDeferredItem<AbstractPotionItem> SWIFTNESS_POTION = registerItem("swiftness_potion", "敏捷药水", () -> new EffectPotionItem(supplier(MobEffects.MOVEMENT_SPEED), 9600));
    public static final PortDeferredItem<AbstractPotionItem> THORNS_POTION = registerItem("thorns_potion", "荆棘药水", () -> new EffectPotionItem(ModEffects.THORNS, 9600));
    public static final PortDeferredItem<AbstractPotionItem> TITAN_POTION = registerItem("titan_potion", "泰坦药水", () -> new EffectPotionItem(ModEffects.TITAN, 9600));
    public static final PortDeferredItem<AbstractPotionItem> WATER_WALKING_POTION = registerItem("water_walking_potion", "水上漂药水", () -> new EffectPotionItem(ModEffects.WATER_WALKING, 12000));
    public static final PortDeferredItem<AbstractPotionItem> WRATH_POTION = registerItem("wrath_potion", "怒气药水", () -> new EffectPotionItem(ModEffects.WRATH, 4800));
    public static final PortDeferredItem<AbstractPotionItem> LESSER_HEALING_POTION = registerItem("lesser_healing_potion", "弱效治疗药水", () -> new HealingPotionItem(10, ModRarity.WHITE, 1200));
    public static final PortDeferredItem<AbstractPotionItem> EGGNOG = registerItem("eggnog", "蛋酒", () -> new HealingPotionItem(16, ModRarity.WHITE, 900));
    public static final PortDeferredItem<AbstractPotionItem> RESTORATION_POTION = registerItem("restoration_potion", "恢复药水", () -> new HealingPotionItem(18, ModRarity.BLUE, 1200));
    public static final PortDeferredItem<AbstractPotionItem> HEALING_POTION = registerItem("healing_potion", "治疗药水", () -> new HealingPotionItem(20, ModRarity.BLUE, 1200));
    public static final PortDeferredItem<AbstractPotionItem> GREATER_HEALING_POTION = registerItem("greater_healing_potion", "强效治疗药水", () -> new HealingPotionItem(40, ModRarity.ORANGE, 1200));
    public static final PortDeferredItem<AbstractPotionItem> SUPER_HEALING_POTION = registerItem("super_healing_potion", "超级治疗药水", () -> new HealingPotionItem(60, ModRarity.LIME, 1200));
    public static final PortDeferredItem<AbstractPotionItem> LESSER_MANA_POTION = registerItem("lesser_mana_potion", "弱效魔力药水", () -> new ManaPotionItem(50, ModRarity.WHITE));
    public static final PortDeferredItem<AbstractPotionItem> MANA_POTION = registerItem("mana_potion", "魔力药水", () -> new ManaPotionItem(100, ModRarity.BLUE));
    public static final PortDeferredItem<AbstractPotionItem> GREATER_MANA_POTION = registerItem("greater_mana_potion", "强效魔力药水", () -> new ManaPotionItem(200, ModRarity.ORANGE));
    public static final PortDeferredItem<AbstractPotionItem> SUPER_MANA_POTION = registerItem("super_mana_potion", "超级魔力药水", () -> new ManaPotionItem(400, ModRarity.LIGHT_RED));
    public static final PortDeferredItem<AbstractPotionItem> RANDOM_TELEPORT_POTION = registerItem("random_teleport_potion", "传送药水", RandomTeleportPotionItem::new);
    public static final PortDeferredItem<AbstractPotionItem> CRATE_POTION = registerItem("crate_potion", "宝匣药水", () -> new EffectPotionItem(ModEffects.CRATE, 4800));
    public static final PortDeferredItem<AbstractPotionItem> STINK_POTION = registerItem("stink_potion", "臭味药水", () -> new EffectThrowablePotionItem(ModRarity.WHITE, ModEffects.STINKY, 600));
    public static final PortDeferredItem<WormholePotionItem> WORMHOLE_POTION = registerItem("wormhole_potion", "虫洞药水", () -> new WormholePotionItem(new Item.Properties().stacksTo(16).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final PortDeferredItem<AbstractPotionItem> AMMO_RESERVATION_POTION = registerItem("ammo_reservation_potion", "弹药储备药水", () -> new EffectPotionItem(ModEffects.AMMO_BOX, 9600));
    public static final PortDeferredItem<AbstractPotionItem> SUMMONING_POTION = registerItem("summoning_potion", "召唤药水", () -> new EffectPotionItem(ModEffects.SUMMONING, 9600));
    public static final PortDeferredItem<AbstractPotionItem> SHIMMER_POTION = registerItem("shimmer_potion", "微光药水", () -> new EffectPotionItem(ModEffects.SHIMMER, 1200, 1));

    public static final PortDeferredItem<AbstractPotionItem> RED_POTION = registerItem("red_potion", "红药水", RedPotionItem::new);
    public static final PortDeferredItem<AbstractPotionItem> CHAOS_POTION = registerItem("chaos_potion", "混沌药水", ChaosPotionItem::new);
    public static final PortDeferredItem<AbstractPotionItem> STRANGE_BREW = registerItem("strange_brew", "诡药", StrangeBrewItem::new);

    public static final PortDeferredItem<AbstractPotionItem> FLASK_OF_FIRE = registerItemWithEnglishName("flask_of_fire", "烈火药剂", "Flask of Fire", () -> new EffectPotionItem(ModRarity.LIGHT_RED, ModEffects.WEAPON_IMBUE_FIRE, 24000));
    public static final PortDeferredItem<AbstractPotionItem> FLASK_OF_GOLD = registerItemWithEnglishName("flask_of_gold", "金药剂", "Flask of Gold", () -> new EffectPotionItem(ModRarity.LIGHT_RED, ModEffects.WEAPON_IMBUE_GOLD, 24000));

    public static final PortDeferredItem<AbstractPotionItem> BATTLE_POTION = registerItem("battle_potion", "战斗药水", () -> new EffectPotionItem(ModRarity.BLUE, ModEffects.BATTLE, 8400));
    public static final PortDeferredItem<AbstractPotionItem> CALMING_POTION = registerItem("calming_potion", "镇静药水", () -> new EffectPotionItem(ModRarity.BLUE, ModEffects.CALM, 14400));

    public static final PortDeferredItem<AbstractPotionItem> SATIETY_POTION = registerItem("satiety_potion", "耐饿药水", () -> new EffectPotionItem(ModRarity.ORANGE, ModEffects.HUNGER_DELAYED, 24000));

    private static Supplier<MobEffect> supplier(MobEffect effect) {
        return () -> effect;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
