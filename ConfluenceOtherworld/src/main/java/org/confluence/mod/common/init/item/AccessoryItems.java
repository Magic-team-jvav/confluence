package org.confluence.mod.common.init.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.item.accessory.*;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.api.primitive.FloatValue;
import org.confluence.terra_curio.api.primitive.IntegerValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.common.item.curio.health.BandOfRegeneration;
import org.confluence.terra_curio.util.TCUtils;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.lib.common.component.ModRarity.*;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

@SuppressWarnings("unused")
public class AccessoryItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

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
    public static final ValueType.UnitType CLOTHIER$KILLER = ValueType.ofUnit("clothier_killer");
    public static final ValueType.UnitType $AFK = ValueType.ofUnit("afk"); // todo
    public static final ValueType.UnitType YOYO$STRING = ValueType.ofUnit("yoyo_string");
    public static final ValueType.IntegerType YOYO$STRING_COLOR = ValueType.ofInteger("yoyo_string_color", IntegerValue.GET_MAX, -1);
    public static final ValueType.UnitType YOYO$GLOVE = ValueType.ofUnit("yoyo_glove");
    public static final ValueType.UnitType YOYO$OFFSTRING = ValueType.ofUnit("yoyo_offstring");
    public static final ValueType.IntegerType YOYO$COUNTERWEIGHT = ValueType.ofInteger("yoyo_counterweight", IntegerValue.GET_MAX, 0);
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

    public static final PortDeferredItem<BaseCurioItem> ADHESIVE_BANDAGE = registerCurio("adhesive_bandage", "粘性绷带", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            MEDICATED_BANDAGE = registerCurio("medicated_bandage", "药用绷带", builder -> builder.noTooltip().rarity(PINK)),
            POCKET_MIRROR = registerCurio("pocket_mirror", "袖珍镜", builder -> builder.noTooltip().rarity(ORANGE)),
            REFLECTIVE_SHADES = registerCurio("reflective_shades", "反光墨镜", builder -> builder.noTooltip().rarity(PINK)),
            ARMOR_POLISH = registerCurio("armor_polish", "盔甲抛光剂", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            ARMOR_BRACING = registerCurio("armor_bracing", "盔甲背带", builder -> builder.noTooltip().rarity(PINK)),
            MEGAPHONE = registerCurio("megaphone", "扩音器", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            NAZAR = registerCurio("nazar", "邪眼", builder -> builder.noTooltip().rarity(GREEN)),
            COUNTERCURSE_MANTRA = registerCurio("countercurse_mantra", "反诅咒咒语", builder -> builder.noTooltip().rarity(LIGHT_RED));

    public static final PortDeferredItem<BaseCurioItem> NATURES_GIFT = registerCurioWithEnglishName("natures_gift", "大自然的恩赐", "Nature's Gift", builder -> builder.noTooltip().rarity(ORANGE)),
            MANA_FLOWER = registerCurio("mana_flower", "魔力花", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            CELESTIAL_MAGNET = registerCurio("celestial_magnet", "天界磁石", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            CELESTIAL_EMBLEM = registerCurio("celestial_emblem", "天界徽章", builder -> builder.noTooltip().rarity(PINK)),
            MAGNET_FLOWER = registerCurio("magnet_flower", "磁花", builder -> builder.noTooltip().rarity(PINK)),
            ARCANE_FLOWER = registerCurio("arcane_flower", "奥术花", builder -> builder.noTooltip().rarity(PINK)),
            BAND_OF_STARPOWER = registerCurioWithEnglishName("band_of_starpower", "星力手环", "Band of Starpower", builder -> builder.noTooltip()),
            MANA_REGENERATION_BAND = registerCurio("mana_regeneration_band", "魔力再生手环", builder -> builder.noTooltip()),
            MAGIC_CUFFS = registerCurio("magic_cuffs", "魔法手铐", builder -> builder.noTooltip().rarity(GREEN)),
            CELESTIAL_CUFFS = registerCurio("celestial_cuffs", "天界手铐", builder -> builder.noTooltip().rarity(PINK)),
            MANA_CLOAK = registerCurio("mana_cloak", "魔力斗篷", builder -> builder.noTooltip().rarity(PINK)),
            PHILOSOPHERS_STONE = registerCurioWithEnglishName("philosophers_stone", "点金石", "Philosopher's Stone", builder -> builder.noTooltip().rarity(LIGHT_RED)),
            CHARM_OF_MYTHS = registerDirectlyWithEnglishName("charm_of_myths", "神话护身符", "Charm of Myths", name -> new BandOfRegeneration(BaseCurioItem.builder(name).noTooltip().rarity(LIGHT_PURPLE)));

    public static final PortDeferredItem<BaseCurioItem> HIGH_TEST_FISHING_LINE = registerCurio("high_test_fishing_line", "优质钓鱼线", builder -> builder.noTooltip()), // 优质钓鱼线
            TACKLE_BOX = registerCurio("tackle_box", "钓具箱", builder -> builder.noTooltip()), // 钓具箱
            ANGLER_TACKLE_BAG = registerCurio("angler_tackle_bag", "渔夫渔具袋", builder -> builder.noTooltip().rarity(ORANGE)), // 渔夫渔具袋
            LAVAPROOF_FISHING_HOOK = registerCurio("lavaproof_fishing_hook", "防熔岩钓钩", builder -> builder.noTooltip().rarity(LIME)), // 防熔岩钓钩
            LAVAPROOF_TACKLE_BAG = registerCurio("lavaproof_tackle_bag", "防熔岩渔具袋", builder -> builder.noTooltip().rarity(YELLOW)), // 防熔岩渔具袋
            FISHING_BOBBER = registerItem("fishing_bobber", "钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.COMMON)), // 钓鱼浮标
            GLOWING_FISHING_BOBBER = registerItem("glowing_fishing_bobber", "发光钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.GLOWING)), // 发光钓鱼浮标
            LAVA_MOSS_FISHING_BOBBER = registerItem("lava_moss_fishing_bobber", "熔岩苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.LAVA)), // 熔岩苔藓钓鱼浮标
            HELIUM_MOSS_FISHING_BOBBER = registerItem("helium_moss_fishing_bobber", "氦苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.HELIUM)), // 氦苔藓钓鱼浮标
            NEON_MOSS_FISHING_BOBBER = registerItem("neon_moss_fishing_bobber", "氖苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.NEON)), // 氖苔藓钓鱼浮标
            ARGON_MOSS_FISHING_BOBBER = registerItem("argon_moss_fishing_bobber", "氩苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.ARGON)), // 氩苔藓钓鱼浮标
            KRYPTON_MOSS_FISHING_BOBBER = registerItem("krypton_moss_fishing_bobber", "氪苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.KRYPTON)), // 氪苔藓钓鱼浮标
            XENON_MOSS_FISHING_BOBBER = registerItem("xenon_moss_fishing_bobber", "氙苔藓钓鱼浮标", () -> new FishingBobber(CurioFishingHook.Variant.XENON)); // 氙苔藓钓鱼浮标

    public static final PortDeferredItem<BaseCurioItem> MECHANICAL_LENS = registerDirectly("mechanical_lens", "机械透镜", name -> new MechanicalLens(BaseCurioItem.builder("mechanical_lens").rarity(ORANGE).noTooltip())); //机械晶状体
    /* 标尺 */
    /* 机械标尺 */

    /* 自动安放器 */
    public static final PortDeferredItem<BaseCurioItem> PAINT_SPRAYER = registerCurio("paint_sprayer", "喷漆器", builder -> builder.noTooltip().rarity(ORANGE)); // 喷漆器

    public static final PortDeferredItem<BaseCurioItem> LUCKY_COIN = registerCurio("lucky_coin", "幸运币", builder -> builder.noTooltip().rarity(PINK)), // 幸运币
            GOLD_RING = registerCurio("gold_ring", "金戒指", builder -> builder.noTooltip().rarity(PINK)), // 金戒指
            COIN_RING = registerCurio("coin_ring", "钱币戒指", builder -> builder.noTooltip().rarity(PINK)), // 钱币戒指
            DISCOUNT_CARD = registerCurio("discount_card", "优惠卡", builder -> builder.noTooltip().rarity(PINK)), // 优惠卡
            GREEDY_RING = registerCurio("greedy_ring", "贪婪戒指", builder -> builder.noTooltip().rarity(LIGHT_PURPLE)), // 贪婪戒指
            GUIDE_TO_PLANT_FIBER_CORDAGE = registerCurioWithEnglishName("guide_to_plant_fiber_cordage", "植物纤维绳索宝典", "Guide to Plant Fiber Cordage", builder -> builder.noTooltip()), // 植物纤维绳索宝典
            RADIO_THING = registerDirectly("radio_thing", "收音机", name -> new RadioThing(BaseCurioItem.builder(name).rarity(BLUE).noTooltip())), // 收音机
            SPECTRE_GOGGLES = registerDirectly("spectre_goggles", "幽灵护目镜", name -> new SpectreGoggles(BaseCurioItem.builder(name).rarity(PINK).noTooltip())), // 幽灵护目镜
            CHROMATIC_CLOAK = registerCurio("chromatic_cloak", "炫彩斗篷", builder -> builder.noTooltip().rarity(PINK)), // 炫彩斗篷
            STRESS_BALL = registerCurio("stress_ball", "解压球", builder -> builder.rarity(BLUE).tooltips(1)); // 压力球

    public static final PortDeferredItem<BaseCurioItem> SUMMONER_EMBLEM = registerCurio("summoner_emblem", "召唤师徽章", builder -> builder.noTooltip().rarity(LIGHT_RED)), // 召唤师徽章
            APPRENTICES_SCARF = registerCurioWithEnglishName("apprentices_scarf", "学徒围巾", "Apprentice's Scarf", builder -> builder.noTooltip().rarity(PINK)), // 学徒围巾
            SQUIRES_SHIELD = registerCurioWithEnglishName("squires_shield", "侍卫护盾", "Squire's Shield", builder -> builder.noTooltip().rarity(PINK)), // 侍卫护盾
            HUNTRESSS_BUCKLER = registerCurioWithEnglishName("huntresss_buckler", "女猎人圆盾", "Huntress's Buckler", builder -> builder.noTooltip().rarity(PINK)), // 女猎人圆盾
            MONKS_BELT = registerCurioWithEnglishName("monks_belt", "武僧腰带", "Monk's Belt", builder -> builder.rarity(PINK).noTooltip()), // 武僧腰带
            HERCULES_BEETLE = registerCurio("hercules_beetle", "大力士甲虫", builder -> builder.noTooltip().rarity(LIME)), // 大力士甲虫
            NECROMANTIC_SCROLL = registerCurio("necromantic_scroll", "死灵卷轴", builder -> builder.noTooltip().rarity(YELLOW)), // 死灵卷轴
            PAPYRUS_SCARAB = registerCurio("papyrus_scarab", "甲虫莎草纸", builder -> builder.noTooltip().rarity(YELLOW)), // 甲虫莎草纸
            PYGMY_NECKLACE = registerCurio("pygmy_necklace", "矮人项链", builder -> builder.noTooltip().rarity(LIME)); // 矮人项链

    public static final PortDeferredItem<BaseCurioItem> CLOTHIER_VOODOO_DOLL = registerCurio("clothier_voodoo_doll", "服装商巫毒娃娃", builder -> builder.noTooltip().rarity(BLUE));
    public static final PortDeferredItem<BaseCurioItem> GUIDE_VOODOO_DOLL = registerDirectly("guide_voodoo_doll", "向导巫毒娃娃", GuideVooDooDollItem::new);
    public static final PortDeferredItem<BaseCurioItem> WHITE_YOYO_STRING = registerCurio("white_string", "白绳", builder -> builder.noTooltip().rarity(WHITE)),
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

    public static final PortDeferredItem<BaseCurioItem> EXPERIENCE_PIPETTE = registerCurio("experience_pipette", "经验吸管", builder -> builder.noTooltip().rarity(LIME)),//经验吸管
            TINSEL = registerCurio("tinsel", "金箔", builder -> builder.noTooltip().rarity(LIME)),//金箔
            SHINY_PIPETTE = registerCurio("shiny_pipette", "闪亮吸管", builder -> builder.noTooltip().rarity(YELLOW));//闪亮吸管

    public static void applyLuckyCoin(ServerPlayer player, Entity target) {
        if (!CommonConfigs.ENEMY_DROPS_MONEY.get()) return;
        RandomSource randomSource = player.getRandom1211();
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

    private static PortDeferredItem<BaseCurioItem> registerCurio(String registryId, String chineseName, Consumer<BaseCurioItem.Builder> consumer) {
        PortDeferredItem<BaseCurioItem> entry = ITEMS.register(registryId, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(registryId);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <I extends BaseCurioItem> PortDeferredItem<I> registerDirectly(String registryId, String chineseName, Function<String, I> function) {
        PortDeferredItem<I> entry = ITEMS.register(registryId, () -> function.apply(registryId));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseCurioItem> registerCurioWithEnglishName(String registryId, String chineseName, String englishName, Consumer<BaseCurioItem.Builder> consumer) {
        PortDeferredItem<BaseCurioItem> entry = ITEMS.register(registryId, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(registryId);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static <I extends BaseCurioItem> PortDeferredItem<I> registerDirectlyWithEnglishName(String registryId, String chineseName, String englishName, Function<String, I> function) {
        PortDeferredItem<I> entry = ITEMS.register(registryId, () -> function.apply(registryId));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
