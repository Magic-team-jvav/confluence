package org.confluence.mod.common.init.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.accessory.*;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.api.primitive.FloatValue;
import org.confluence.terra_curio.api.primitive.IntegerValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.common.item.curio.health.BandOfRegeneration;
import org.confluence.terra_curio.util.TCUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static org.confluence.lib.common.component.ModRarity.*;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.terra_curio.common.component.PrimitiveValueComponent.of;
import static org.confluence.terra_curio.common.component.PrimitiveValueComponent.units;

@SuppressWarnings("all")
public class AccessoryItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final ValueType.UnitType LUCKY$COIN = ValueType.ofUnit("lucky_coin");
    public static final ValueType.UnitType VINE$ROPE = ValueType.ofUnit("vine_rope");
    public static final ValueType.UnitType AUTO$GET$MANA = ValueType.ofUnit("auto_get_mama");
    public static final ValueType.UnitType HURT$GET$MANA = ValueType.ofUnit("hurt_get_mana");
    public static final ValueType.UnitType FAST$MANA$GENERATION = ValueType.ofUnit("faset_mana_regeneration");
    public static final ValueType.UnitType HIGH$TEST$FISHING$LINE = ValueType.ofUnit("high_test_fishing_line");
    public static final ValueType.UnitType TACKLE$BOX = ValueType.ofUnit("tackle_box");
    public static final ValueType.UnitType LAVAPROOF$FISHING$HOOK = ValueType.ofUnit("lavaproof_fishing_hook");
    public static final ValueType.UnitType SPECTRE$GOGGLES = ValueType.ofUnit("spectre_goggles");
    public static final ValueType.UnitType PAINT$SPRAYER = ValueType.ofUnit("paint_sprayer");
    /// 魂师饰品：灵魂缓慢回复的标记（手环三件共用）。
    public static final ValueType.UnitType BAND$OF$SOUL = ValueType.ofUnit("band_of_soul");
    /// 刻骨的记忆：无需手持主武器即可施放已装备技能。
    public static final ValueType.UnitType MEMORY$OF$BONEBRAND = ValueType.ofUnit("memory_of_bonebrand");
    public static final ValueType.UnitType CLOTHIER$KILLER = ValueType.ofUnit("clothier_killer");
    public static final ValueType.UnitType $AFK = ValueType.ofUnit("afk"); // todo
    public static int AFK_INDEX = -1;
    public static final ValueType.UnitType AUTO$GET$EXPERIENCE = ValueType.ofUnit("auto_get_experience"); // 经验吸管：仆从击杀的经验直接归玩家
    public static final ValueType.UnitType AUTO$GET$COIN = ValueType.ofUnit("auto_get_coin"); // 金箔：仆从击杀的钱币直接进存钱罐

    public static final ValueType.FloatType MANA$USE$REDUCE = ValueType.ofFloat("mana_use_reduce", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType.FloatType REDUCE$HEALING$COOLDOWN = ValueType.ofFloat("reduce_healing_cooldown", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType.FloatType FISHING$POWER = ValueType.ofFloat("fishing_power", FloatValue.ADDITION, 0.0F);
    public static final ValueType.IntegerType ADDITIONAL$MANA = ValueType.ofInteger("additional_mana", IntegerValue.ADDITION, 0);
    public static final ValueType.IntegerType SPECIAL$PRICE = ValueType.ofInteger("special_price", IntegerValue.GET_MAX, 0);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> MANA$PICKUP$RANGE = ValueType.create("mana_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(1.75F, 0), PickupRangeAbilityValue::new);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> COIN$PICKUP$RANGE = ValueType.create("coin_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(2.0F, 0), PickupRangeAbilityValue::new);

    public static final ValueType.UnitType YOYO$STRING = ValueType.ofUnit("yoyo_string");
    public static final ValueType.IntegerType YOYO$STRING_COLOR = ValueType.ofInteger("yoyo_string_color", IntegerValue.GET_MAX, -1);
    public static final ValueType.UnitType YOYO$GLOVE = ValueType.ofUnit("yoyo_glove");
    public static final ValueType.UnitType YOYO$OFFSTRING = ValueType.ofUnit("yoyo_offstring");
    public static final ValueType.IntegerType YOYO$COUNTERWEIGHT = ValueType.ofInteger("yoyo_counterweight", IntegerValue.GET_MAX, 0);

    public static final DeferredItem<BaseCurioItem> ADHESIVE_BANDAGE = registerCurio("adhesive_bandage", "粘性绷带", builder -> builder.noTooltip().rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BLEEDING)))),
            MEDICATED_BANDAGE = registerCurio("medicated_bandage", "药用绷带", builder -> builder.noTooltip().rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.POISON, ModEffects.BLEEDING)))),
            POCKET_MIRROR = registerCurio("pocket_mirror", "袖珍镜", builder -> builder.noTooltip().rarity(ORANGE).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.STONED)))),
            REFLECTIVE_SHADES = registerCurio("reflective_shades", "反光墨镜", builder -> builder.noTooltip().rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.BLINDNESS, ModEffects.STONED)))),
            ARMOR_POLISH = registerCurio("armor_polish", "盔甲抛光剂", builder -> builder.noTooltip().rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BROKEN_ARMOR)))),
            ARMOR_BRACING = registerCurio("armor_bracing", "盔甲背带", builder -> builder.noTooltip().rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.WEAKNESS, ModEffects.BROKEN_ARMOR)))),
            MEGAPHONE = registerCurio("megaphone", "扩音器", builder -> builder.noTooltip().rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED)))),
            NAZAR = registerCurio("nazar", "邪眼", builder -> builder.noTooltip().rarity(GREEN).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.CURSED)))),
            COUNTERCURSE_MANTRA = registerCurio("countercurse_mantra", "反诅咒咒语", builder -> builder.noTooltip().rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED, ModEffects.CURSED))));

    public static final DeferredItem<BaseCurioItem> NATURES_GIFT = registerCurioWithEnglishName("natures_gift", "大自然的恩赐", "Nature's Gift", builder -> builder.noTooltip().rarity(ORANGE).accessories(of(MANA$USE$REDUCE, 0.06F))),
            MANA_FLOWER = registerCurio("mana_flower", "魔力花", builder -> builder.noTooltip().rarity(LIGHT_RED).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F))),
            CELESTIAL_MAGNET = registerCurio("celestial_magnet", "天界磁石", builder -> builder.noTooltip().rarity(LIGHT_RED).accessories(of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            CELESTIAL_EMBLEM = registerCurio("celestial_emblem", "天界徽章", builder -> builder.noTooltip().rarity(PINK).accessories(of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0))).attribute(LibAttributes.getMagicDamage(), 0.15, ADD_MULTIPLIED_TOTAL)),
            MAGNET_FLOWER = registerCurio("magnet_flower", "磁花", builder -> builder.noTooltip().rarity(PINK).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F), of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            ARCANE_FLOWER = registerCurio("arcane_flower", "奥术花", builder -> builder.noTooltip().rarity(PINK).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F)).attribute(ConfluenceMagicLib.AGGRO, -400, ADD_VALUE)),
            BAND_OF_STARPOWER = registerCurioWithEnglishName("band_of_starpower", "星力手环", "Band of Starpower", builder -> builder.noTooltip().accessories(of(ADDITIONAL$MANA, 40))),
            MANA_REGENERATION_BAND = registerCurio("mana_regeneration_band", "魔力再生手环", builder -> builder.noTooltip().accessories(units(FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 40))),
            MAGIC_CUFFS = registerCurio("magic_cuffs", "魔法手铐", builder -> builder.noTooltip().rarity(GREEN).accessories(units(HURT$GET$MANA, FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 40))),
            CELESTIAL_CUFFS = registerCurio("celestial_cuffs", "天界手铐", builder -> builder.noTooltip().rarity(PINK).accessories(units(HURT$GET$MANA, FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 40), of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            MANA_CLOAK = registerCurio("mana_cloak", "魔力斗篷", builder -> builder.noTooltip().rarity(PINK).accessories(units(AUTO$GET$MANA), of(TCItems.STAR$CLOCK, true), of(MANA$USE$REDUCE, 0.08F))),
            PHILOSOPHERS_STONE = registerCurioWithEnglishName("philosophers_stone", "点金石", "Philosopher's Stone", builder -> builder.noTooltip().rarity(LIGHT_RED).accessories(of(REDUCE$HEALING$COOLDOWN, 0.25F))),
            CHARM_OF_MYTHS = registerDirectlyWithEnglishName("charm_of_myths", "神话护身符", "Charm of Myths", name -> new BandOfRegeneration(BaseCurioItem.builder(name).noTooltip().rarity(LIGHT_PURPLE).accessories(of(REDUCE$HEALING$COOLDOWN, 0.25F))));

    public static final DeferredItem<BaseCurioItem> HIGH_TEST_FISHING_LINE = registerCurio("high_test_fishing_line", "优质钓鱼线", builder -> builder.noTooltip().accessories(units(HIGH$TEST$FISHING$LINE))), // 优质钓鱼线
            TACKLE_BOX = registerCurio("tackle_box", "钓具箱", builder -> builder.noTooltip().accessories(units(TACKLE$BOX))), // 钓具箱
            ANGLER_TACKLE_BAG = registerCurio("angler_tackle_bag", "渔夫渔具袋", builder -> builder.noTooltip().rarity(ORANGE).accessories(units(HIGH$TEST$FISHING$LINE, TACKLE$BOX), of(FISHING$POWER, 10.0F))), // 渔夫渔具袋
            LAVAPROOF_FISHING_HOOK = registerCurio("lavaproof_fishing_hook", "防熔岩钓钩", builder -> builder.noTooltip().rarity(LIME).accessories(units(LAVAPROOF$FISHING$HOOK))), // 防熔岩钓钩
            LAVAPROOF_TACKLE_BAG = registerCurio("lavaproof_tackle_bag", "防熔岩渔具袋", builder -> builder.noTooltip().rarity(YELLOW).accessories(units(HIGH$TEST$FISHING$LINE, TACKLE$BOX, LAVAPROOF$FISHING$HOOK), of(FISHING$POWER, 10.0F))), // 防熔岩渔具袋
            FISHING_BOBBER = registerItem("fishing_bobber", "钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.COMMON)), // 钓鱼浮标
            GLOWING_FISHING_BOBBER = registerItem("glowing_fishing_bobber", "发光钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.GLOWING)), // 发光钓鱼浮标
            LAVA_MOSS_FISHING_BOBBER = registerItem("lava_moss_fishing_bobber", "熔岩苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.LAVA)), // 熔岩苔藓钓鱼浮标
            HELIUM_MOSS_FISHING_BOBBER = registerItem("helium_moss_fishing_bobber", "氦苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.HELIUM)), // 氦苔藓钓鱼浮标
            NEON_MOSS_FISHING_BOBBER = registerItem("neon_moss_fishing_bobber", "氖苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.NEON)), // 氖苔藓钓鱼浮标
            ARGON_MOSS_FISHING_BOBBER = registerItem("argon_moss_fishing_bobber", "氩苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.ARGON)), // 氩苔藓钓鱼浮标
            KRYPTON_MOSS_FISHING_BOBBER = registerItem("krypton_moss_fishing_bobber", "氪苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.KRYPTON)), // 氪苔藓钓鱼浮标
            XENON_MOSS_FISHING_BOBBER = registerItem("xenon_moss_fishing_bobber", "氙苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.XENON)); // 氙苔藓钓鱼浮标

    public static final DeferredItem<BaseCurioItem> MECHANICAL_LENS = registerDirectly("mechanical_lens", "机械透镜", name -> new MechanicalLens(BaseCurioItem.builder("mechanical_lens").rarity(ORANGE).noTooltip().accessories(of(TCItems.INFORMATION, List.of(TCItems.MECHANICAL$LENS))))); //机械晶状体
    /* 标尺 */
    /* 机械标尺 */

    /* 自动安放器 */
    public static final DeferredItem<BaseCurioItem> PAINT_SPRAYER = registerCurio("paint_sprayer", "喷漆器", builder -> builder.noTooltip().rarity(ORANGE).accessories(units(PAINT$SPRAYER))); // 喷漆器

    public static final DeferredItem<BaseCurioItem> LUCKY_COIN = registerCurio("lucky_coin", "幸运币", builder -> builder.noTooltip().rarity(PINK).accessories(units(LUCKY$COIN)).attribute(Attributes.LUCK, 0.05, ADD_VALUE)), // 幸运币
            GOLD_RING = registerCurio("gold_ring", "金戒指", builder -> builder.noTooltip().rarity(PINK).accessories(of(COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0)))), // 金戒指
            COIN_RING = registerCurio("coin_ring", "钱币戒指", builder -> builder.noTooltip().rarity(PINK)
                    .accessories(units(LUCKY$COIN), of(COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0)))
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)), // 钱币戒指
            DISCOUNT_CARD = registerCurio("discount_card", "优惠卡", builder -> builder.noTooltip().rarity(PINK).accessories(of(SPECIAL$PRICE, 1))), // 优惠卡
            GREEDY_RING = registerCurio("greedy_ring", "贪婪戒指", builder -> builder.noTooltip().rarity(LIGHT_PURPLE)
                    .accessories(units(LUCKY$COIN), of(COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0)), of(SPECIAL$PRICE, 1))
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)), // 贪婪戒指
            GUIDE_TO_PLANT_FIBER_CORDAGE = registerCurioWithEnglishName("guide_to_plant_fiber_cordage", "植物纤维绳索宝典", "Guide to Plant Fiber Cordage", builder -> builder.noTooltip().accessories(units(VINE$ROPE))), // 植物纤维绳索宝典
            RADIO_THING = registerDirectly("radio_thing", "收音机", name -> new RadioThing(BaseCurioItem.builder(name).rarity(BLUE).noTooltip())), // 收音机
            SPECTRE_GOGGLES = registerDirectly("spectre_goggles", "幽灵护目镜", name -> new SpectreGoggles(BaseCurioItem.builder(name).rarity(PINK).noTooltip().accessories(units(SPECTRE$GOGGLES)))), // 幽灵护目镜
            CHROMATIC_CLOAK = registerCurio("chromatic_cloak", "炫彩斗篷", builder -> builder.noTooltip().rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SHIMMER)))), // 炫彩斗篷
            STRESS_BALL = registerCurio("stress_ball", "解压球", builder -> builder.rarity(BLUE).accessories(units($AFK)).tooltips(1));

    public static final DeferredItem<BaseCurioItem> SUMMONER_EMBLEM = registerCurio("summoner_emblem", "召唤师徽章", builder -> builder.noTooltip().rarity(LIGHT_RED).attribute(LibAttributes.getSummonDamage(), 0.15, ADD_MULTIPLIED_TOTAL)), // 召唤师徽章
            BAND_OF_SOUL = registerCurio("band_of_soul", "灵魂手环", builder -> builder.rarity(BLUE).accessories(units(BAND$OF$SOUL))),
            BAND_OF_SOUL_REGENERATION = registerDirectly("band_of_soul_regeneration", "灵魂再生手环", name -> new BandOfSoulRegeneration(BaseCurioItem.builder(name).rarity(GREEN).accessories(units(BAND$OF$SOUL)))),
            BAND_OF_MEDIUMSHIP = registerCurio("band_of_mediumship", "通灵手环", builder -> builder.rarity(GREEN).attribute(ConfluenceMagicLib.ADDITIONAL_SOUL, 20.0, ADD_VALUE).accessories(units(BAND$OF$SOUL))),
            MEMORY_OF_BONEBRAND = registerCurio("memory_of_bonebrand", "刻骨的记忆", builder -> builder.rarity(PURPLE).accessories(units(MEMORY$OF$BONEBRAND))),
            SOULCERER_EMBLEM = registerCurio("soulcerer_emblem", "灵魂徽章", builder -> builder.noTooltip().rarity(LIGHT_RED).attribute(LibAttributes.getSoulDamage(), 0.12, ADD_MULTIPLIED_TOTAL)),
            APPRENTICES_SCARF = registerCurioWithEnglishName("apprentices_scarf", "学徒围巾", "Apprentice's Scarf", builder -> builder.noTooltip().rarity(PINK).attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 学徒围巾
            SQUIRES_SHIELD = registerCurioWithEnglishName("squires_shield", "侍卫护盾", "Squire's Shield", builder -> builder.noTooltip().rarity(PINK).attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 侍卫护盾
            HUNTRESSS_BUCKLER = registerCurioWithEnglishName("huntresss_buckler", "女猎人圆盾", "Huntress's Buckler", builder -> builder.noTooltip().rarity(PINK).attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 女猎人圆盾
            MONKS_BELT = registerCurioWithEnglishName("monks_belt", "武僧腰带", "Monk's Belt", builder -> builder.rarity(PINK).noTooltip().attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 武僧腰带
            HERCULES_BEETLE = registerCurio("hercules_beetle", "大力士甲虫", builder -> builder.noTooltip().rarity(LIME).attribute(LibAttributes.getSummonDamage(), 0.15, ADD_MULTIPLIED_TOTAL).attribute(ConfluenceMagicLib.SUMMON_KNOCKBACK, 2.0, ADD_VALUE)), // 大力士甲虫
            NECROMANTIC_SCROLL = registerCurio("necromantic_scroll", "死灵卷轴", builder -> builder.noTooltip().rarity(YELLOW).attribute(ConfluenceMagicLib.MINION_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 死灵卷轴
            PAPYRUS_SCARAB = registerCurio("papyrus_scarab", "甲虫莎草纸", builder -> builder.noTooltip().rarity(YELLOW).attribute(ConfluenceMagicLib.MINION_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.15, ADD_MULTIPLIED_TOTAL).attribute(ConfluenceMagicLib.SUMMON_KNOCKBACK, 2.0, ADD_VALUE)), // 甲虫莎草纸
            PYGMY_NECKLACE = registerCurio("pygmy_necklace", "矮人项链", builder -> builder.noTooltip().rarity(LIME).attribute(ConfluenceMagicLib.MINION_CAPACITY, 1.0, ADD_VALUE)); // 矮人项链

    public static final DeferredItem<BaseCurioItem> CLOTHIER_VOODOO_DOLL = registerCurio("clothier_voodoo_doll", "服装商巫毒娃娃", builder -> builder.noTooltip().rarity(BLUE).accessories(units(CLOTHIER$KILLER)));
    public static final DeferredItem<BaseCurioItem> GUIDE_VOODOO_DOLL = registerDirectly("guide_voodoo_doll", "向导巫毒娃娃", GuideVooDooDollItem::new);
    public static final DeferredItem<BaseCurioItem> WHITE_YOYO_STRING = registerCurio("white_string", "白绳", builder -> builder.noTooltip().rarity(WHITE)),
            RED_YOYO_STRING = registerCurio("red_string", "红绳", builder -> builder.noTooltip().rarity(WHITE)),
            ORANGE_YOYO_STRING = registerCurio("orange_string", "橙绳", builder -> builder.noTooltip().rarity(WHITE)),
            YELLOW_YOYO_STRING = registerCurio("yellow_string", "黄绳", builder -> builder.noTooltip().rarity(WHITE)),
            LIME_YOYO_STRING = registerCurio("lime_string", "橙绿绳", builder -> builder.noTooltip().rarity(WHITE)),
            GREEN_YOYO_STRING = registerCurio("green_string", "绿绳", builder -> builder.noTooltip().rarity(WHITE)),
            TEAL_YOYO_STRING = registerCurio("teal_string", "青绿绳", builder -> builder.noTooltip().rarity(WHITE)),
            CYAN_YOYO_STRING = registerCurio("cyan_string", "青绳", builder -> builder.noTooltip().rarity(WHITE)),
            SKY_BLUE_YOYO_STRING = registerCurio("sky_blue_string", "天蓝绳", builder -> builder.noTooltip().rarity(WHITE)),
            BLUE_YOYO_STRING = registerCurio("blue_string", "蓝绳", builder -> builder.noTooltip().rarity(WHITE)),
            PURPLE_YOYO_STRING = registerCurio("purple_string", "紫绳", builder -> builder.noTooltip().rarity(WHITE)),
            VIOLET_YOYO_STRING = registerCurio("violet_string", "蓝紫绳", builder -> builder.noTooltip().rarity(WHITE)),
            PINK_YOYO_STRING = registerCurio("pink_string", "粉绳", builder -> builder.noTooltip().rarity(WHITE)),
            BROWN_YOYO_STRING = registerCurio("brown_string", "棕绳", builder -> builder.noTooltip().rarity(WHITE)),
            RAINBOW_YOYO_STRING = registerCurio("rainbow_string", "彩虹绳", builder -> builder.noTooltip().rarity(WHITE)),
            BLACK_YOYO_STRING = registerCurio("black_string", "黑绳", builder -> builder.noTooltip().rarity(WHITE)),
            YOYO_GLOVE = registerCurio("yoyo_glove", "悠悠球手套", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            BLACK_COUNTERWEIGHT = registerCurio("black_counterweight", "黑平衡锤", builder -> builder.noTooltip().rarity(WHITE)),
            BLUE_COUNTERWEIGHT = registerCurio("blue_counterweight", "蓝平衡锤", builder -> builder.noTooltip().rarity(WHITE)),
            GREEN_COUNTERWEIGHT = registerCurio("green_counterweight", "绿平衡锤", builder -> builder.noTooltip().rarity(WHITE)),
            PURPLE_COUNTERWEIGHT = registerCurio("purple_counterweight", "紫平衡锤", builder -> builder.noTooltip().rarity(WHITE)),
            RED_COUNTERWEIGHT = registerCurio("red_counterweight", "红平衡锤", builder -> builder.noTooltip().rarity(WHITE)),
            YELLOW_COUNTERWEIGHT = registerCurio("yellow_counterweight", "黄平衡锤", builder -> builder.noTooltip().rarity(WHITE)),
            STRUNG_COUNTERWEIGHT = registerCurio("strung_counterweight", "串接平衡锤", builder -> builder.noTooltip().rarity(BLUE)),
            YOYO_BAG = registerCurio("yoyo_bag", "悠悠球袋", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            MAGIC_STRING = registerCurio("magic_string", "魔法绳", builder -> builder.rarity(PINK)),
            MAGIC_YOYO_BAG = registerCurio("magic_yoyo_bag", "魔法悠悠球袋", builder -> builder.noTooltip().rarity(PINK));

    public static final DeferredItem<BaseCurioItem> EXPERIENCE_PIPETTE = registerCurio("experience_pipette", "经验吸管", builder -> builder.noTooltip().rarity(LIME)),//经验吸管
            TINSEL = registerCurio("tinsel", "金箔", builder -> builder.noTooltip().rarity(LIME)),//金箔
            SHINY_PIPETTE = registerCurio("shiny_pipette", "闪亮吸管", builder -> builder.noTooltip().rarity(YELLOW));//闪亮吸管

    public static void applyLuckyCoin(ServerPlayer player, Entity target) {
        if (!CommonConfigs.ENEMY_DROPS_MONEY.get()) return;
        RandomSource randomSource = player.getRandom();
        if (TCUtils.hasType(player, LUCKY$COIN) && randomSource.nextFloat() < 0.2F) {
            Item item;
            float a = randomSource.nextFloat();
            if (a < 0.01F) {
                item = ModItems.GOLD_COIN.get();
            } else if (a < 0.099F) {
                item = ModItems.SILVER_COIN.get();
            } else {
                item = ModItems.COPPER_COIN.get();
            }
            ItemStack itemStack = item.getDefaultInstance();
            itemStack.setCount(randomSource.nextInt(1, 3));
            LibEntityUtils.createItemEntity(itemStack, target.getX(), target.getY(), target.getZ(), player.level(), 0);
        }
    }

    public static void applyHurtGetMana(ServerPlayer player, DamageSource damageSource, float amount) {
        if (TCUtils.hasType(player, HURT$GET$MANA) &&
                !damageSource.is(DamageTypes.DROWN) &&
                !damageSource.is(TCTags.HARMFUL_EFFECT)
        ) {
            CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
            long last = tag.getLong("confluence:last_hurt_get_mana_time");
            long cur = player.level().getGameTime();
            if (cur - last >= 10) {
                PlayerUtils.receiveMana(player, () -> amount);
                tag.putLong("confluence:last_hurt_get_mana_time", cur);
            }
        }
    }

    private static DeferredItem<BaseCurioItem> registerCurio(String registryId, String chineseName, Consumer<BaseCurioItem.Builder> consumer) {
        DeferredItem<BaseCurioItem> entry = ITEMS.register(registryId, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(registryId);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <I extends BaseCurioItem> DeferredItem<I> registerDirectly(String registryId, String chineseName, Function<String, I> function) {
        DeferredItem<I> entry = ITEMS.register(registryId, () -> function.apply(registryId));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BaseCurioItem> registerCurioWithEnglishName(String registryId, String chineseName, String englishName, Consumer<BaseCurioItem.Builder> consumer) {
        DeferredItem<BaseCurioItem> entry = ITEMS.register(registryId, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(registryId);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static <I extends BaseCurioItem> DeferredItem<I> registerDirectlyWithEnglishName(String registryId, String chineseName, String englishName, Function<String, I> function) {
        DeferredItem<I> entry = ITEMS.register(registryId, () -> function.apply(registryId));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
