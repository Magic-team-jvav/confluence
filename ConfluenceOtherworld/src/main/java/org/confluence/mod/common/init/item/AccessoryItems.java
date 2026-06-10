package org.confluence.mod.common.init.item;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.core.registries.Registries;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.accessory.*;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.api.primitive.FloatValue;
import org.confluence.terra_curio.api.primitive.IntegerValue;
import org.confluence.terra_curio.api.primitive.MayFlyAbilityValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.common.item.curio.health.BandOfRegeneration;
import org.confluence.terra_curio.util.TCUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.confluence.lib.common.component.ModRarity.*;
import static org.confluence.terra_curio.common.component.PrimitiveValueComponent.of;
import static org.confluence.terra_curio.common.component.PrimitiveValueComponent.units;
import static org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL;
import static org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier.PortOperation.ADD_VALUE;

@SuppressWarnings("all")
public class AccessoryItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);
    public static final List<RegistryObject<BaseCurioItem>> WINGS = new ArrayList<>();

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
    public static int AFK_INDEX = -1;

    public static final ValueType.FloatType MANA$USE$REDUCE = ValueType.ofFloat("mana_use_reduce", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType.FloatType REDUCE$HEALING$COOLDOWN = ValueType.ofFloat("reduce_healing_cooldown", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType.FloatType FISHING$POWER = ValueType.ofFloat("fishing_power", FloatValue.ADDITION, 0.0F);
    public static final ValueType.IntegerType ADDITIONAL$MANA = ValueType.ofInteger("additional_mana", IntegerValue.ADDITION, 0);
    public static final ValueType.IntegerType SPECIAL$PRICE = ValueType.ofInteger("special_price", IntegerValue.GET_MAX, 0);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> MANA$PICKUP$RANGE = ValueType.create("mana_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(1.75F, 0), PickupRangeAbilityValue::new);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> COIN$PICKUP$RANGE = ValueType.create("coin_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(2.0F, 0), PickupRangeAbilityValue::new);

    public static final RegistryObject<BaseCurioItem> ADHESIVE_BANDAGE = registerCurio("adhesive_bandage", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BLEEDING.get())))),
            MEDICATED_BANDAGE = registerCurio("medicated_bandage", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.POISON, ModEffects.BLEEDING.get())))),
            POCKET_MIRROR = registerCurio("pocket_mirror", builder -> builder.rarity(ORANGE).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.STONED.get())))),
            REFLECTIVE_SHADES = registerCurio("reflective_shades", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.BLINDNESS, ModEffects.STONED.get())))),
            ARMOR_POLISH = registerCurio("armor_polish", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BROKEN_ARMOR.get())))),
            ARMOR_BRACING = registerCurio("armor_bracing", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.WEAKNESS, ModEffects.BROKEN_ARMOR.get())))),
            MEGAPHONE = registerCurio("megaphone", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED.get())))),
            NAZAR = registerCurio("nazar", builder -> builder.rarity(GREEN).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.CURSED.get())))),
            COUNTERCURSE_MANTRA = registerCurio("countercurse_mantra", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED.get(), ModEffects.CURSED.get()))));

    public static final RegistryObject<BaseCurioItem> NATURES_GIFT = registerCurio("natures_gift", builder -> builder.rarity(ORANGE).accessories(of(MANA$USE$REDUCE, 0.06F))),
            MANA_FLOWER = registerCurio("mana_flower", builder -> builder.tooltips(1).rarity(LIGHT_RED).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F))),
            CELESTIAL_MAGNET = registerCurio("celestial_magnet", builder -> builder.rarity(LIGHT_RED).accessories(of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            CELESTIAL_EMBLEM = registerCurio("celestial_emblem", builder -> builder.rarity(PINK).accessories(of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0))).attribute(LibAttributes.getMagicDamage(), 0.15, ADD_MULTIPLIED_TOTAL)),
            MAGNET_FLOWER = registerCurio("magnet_flower", builder -> builder.tooltips(2).rarity(PINK).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F), of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            ARCANE_FLOWER = registerCurio("arcane_flower", builder -> builder.tooltips(2).rarity(PINK).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F)).attribute(ConfluenceMagicLib.AGGRO, -400, ADD_VALUE)),
            BAND_OF_STARPOWER = registerCurio("band_of_starpower", builder -> builder.accessories(of(ADDITIONAL$MANA, 20))),
            MANA_REGENERATION_BAND = registerCurio("mana_regeneration_band", builder -> builder.tooltips(1).accessories(units(FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 20))),
            MAGIC_CUFFS = registerCurio("magic_cuffs", builder -> builder.tooltips(1).rarity(GREEN).accessories(units(HURT$GET$MANA, FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 20))),
            CELESTIAL_CUFFS = registerCurio("celestial_cuffs", builder -> builder.tooltips(2).rarity(PINK).accessories(units(HURT$GET$MANA, FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 20), of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            MANA_CLOAK = registerCurio("mana_cloak", builder -> builder.tooltips(3).rarity(PINK).accessories(units(AUTO$GET$MANA), of(TCItems.STAR$CLOCK, true), of(MANA$USE$REDUCE, 0.08F))),
            PHILOSOPHERS_STONE = registerCurio("philosophers_stone", builder -> builder.rarity(LIGHT_RED).accessories(of(REDUCE$HEALING$COOLDOWN, 0.25F))),
            CHARM_OF_MYTHS = registerDirectly("charm_of_myths", name -> new BandOfRegeneration(BaseCurioItem.builder(name).rarity(LIGHT_PURPLE).accessories(of(REDUCE$HEALING$COOLDOWN, 0.25F))));

    public static final RegistryObject<BaseCurioItem> HIGH_TEST_FISHING_LINE = registerCurio("high_test_fishing_line", builder -> builder.accessories(units(HIGH$TEST$FISHING$LINE))), // 优质钓鱼线
            TACKLE_BOX = registerCurio("tackle_box", builder -> builder.accessories(units(TACKLE$BOX))), // 钓具箱
            ANGLER_TACKLE_BAG = registerCurio("angler_tackle_bag", builder -> builder.rarity(ORANGE).accessories(units(HIGH$TEST$FISHING$LINE, TACKLE$BOX), of(FISHING$POWER, 10.0F))), // 渔夫渔具袋
            LAVAPROOF_FISHING_HOOK = registerCurio("lavaproof_fishing_hook", builder -> builder.rarity(LIME).accessories(units(LAVAPROOF$FISHING$HOOK))), // 防熔岩钓钩
            LAVAPROOF_TACKLE_BAG = registerCurio("lavaproof_tackle_bag", builder -> builder.rarity(YELLOW).tooltips(1).accessories(units(HIGH$TEST$FISHING$LINE, TACKLE$BOX, LAVAPROOF$FISHING$HOOK), of(FISHING$POWER, 10.0F))), // 防熔岩渔具袋
            FISHING_BOBBER = ITEMS.register("fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.COMMON)), // 钓鱼浮标
            GLOWING_FISHING_BOBBER = ITEMS.register("glowing_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.GLOWING)), // 发光钓鱼浮标
            LAVA_MOSS_FISHING_BOBBER = ITEMS.register("lava_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.LAVA)), // 熔岩苔藓钓鱼浮标
            HELIUM_MOSS_FISHING_BOBBER = ITEMS.register("helium_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.HELIUM)), // 氦苔藓钓鱼浮标
            NEON_MOSS_FISHING_BOBBER = ITEMS.register("neon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.NEON)), // 氖苔藓钓鱼浮标
            ARGON_MOSS_FISHING_BOBBER = ITEMS.register("argon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.ARGON)), // 氩苔藓钓鱼浮标
            KRYPTON_MOSS_FISHING_BOBBER = ITEMS.register("krypton_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.KRYPTON)), // 氪苔藓钓鱼浮标
            XENON_MOSS_FISHING_BOBBER = ITEMS.register("xenon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.XENON)); // 氙苔藓钓鱼浮标


    public static final RegistryObject<BaseCurioItem> MECHANICAL_LENS = registerDirectly("mechanical_lens", name -> new MechanicalLens(BaseCurioItem.builder("mechanical_lens").rarity(ORANGE).tooltips(1).accessories(of(TCItems.INFORMATION, List.of(TCItems.MECHANICAL$LENS))))); //机械晶状体
    /* 标尺 */
    /* 机械标尺 */

    /* 自动安放器 */
    public static final RegistryObject<BaseCurioItem> PAINT_SPRAYER = registerCurio("paint_sprayer", builder -> builder.rarity(ORANGE).accessories(units(PAINT$SPRAYER))); // 喷漆器

    public static final RegistryObject<BaseCurioItem> LUCKY_COIN = registerCurio("lucky_coin", builder -> builder.rarity(PINK).accessories(units(LUCKY$COIN)).attribute(Attributes.LUCK, 0.05, ADD_VALUE)), // 幸运币
            GOLD_RING = registerCurio("gold_ring", builder -> builder.rarity(PINK).accessories(of(COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0)))), // 金戒指
            COIN_RING = registerCurio("coin_ring", builder -> builder.rarity(PINK)
                    .accessories(units(LUCKY$COIN), of(COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0)))
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)), // 钱币戒指
            DISCOUNT_CARD = registerCurio("discount_card", builder -> builder.rarity(PINK).accessories(of(SPECIAL$PRICE, 1))), // 优惠卡
            GREEDY_RING = registerCurio("greedy_ring", builder -> builder.rarity(LIGHT_PURPLE)
                    .accessories(units(LUCKY$COIN), of(COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0)), of(SPECIAL$PRICE, 1))
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)), // 贪婪戒指
            GUIDE_TO_PLANT_FIBER_CORDAGE = registerCurio("guide_to_plant_fiber_cordage", builder -> builder.accessories(units(VINE$ROPE))), // 植物纤维绳索宝典
            RADIO_THING = registerDirectly("radio_thing", name -> new RadioThing(BaseCurioItem.builder(name).rarity(BLUE).tooltips(1))), // 收音机
            SPECTRE_GOGGLES = registerDirectly("spectre_goggles", name -> new SpectreGoggles(BaseCurioItem.builder(name).rarity(PINK).tooltips(1).accessories(units(SPECTRE$GOGGLES)))), // 幽灵护目镜
            CHROMATIC_CLOAK = registerCurio("chromatic_cloak", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SHIMMER.get())))), // 炫彩斗篷
            STRESS_BALL = registerCurio("stress_ball", builder -> builder.rarity(BLUE).accessories(units($AFK)).tooltips(1));

    public static final RegistryObject<BaseCurioItem> SUMMONER_EMBLEM = registerCurio("summoner_emblem", builder -> builder.noTooltip().rarity(LIGHT_RED).attribute(LibAttributes.getSummonDamage(), 0.15, ADD_MULTIPLIED_TOTAL)), // 召唤师徽章
            APPRENTICES_SCARF = registerCurio("apprentices_scarf", builder -> builder.noTooltip().rarity(PINK).attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 学徒围巾
            SQUIRES_SHIELD = registerCurio("squires_shield", builder -> builder.noTooltip().rarity(PINK).attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 侍卫护盾
            HUNTRESSS_BUCKLER = registerCurio("huntresss_buckler", builder -> builder.noTooltip().rarity(PINK).attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 女猎人圆盾
            MONKS_BELT = registerCurio("monks_belt", builder -> builder.rarity(PINK).noTooltip().attribute(ConfluenceMagicLib.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 武僧腰带
            HERCULES_BEETLE = registerCurio("hercules_beetle", builder -> builder.noTooltip().rarity(LIME).attribute(LibAttributes.getSummonDamage(), 0.15, ADD_MULTIPLIED_TOTAL).attribute(ConfluenceMagicLib.SUMMON_KNOCKBACK, 2.0, ADD_VALUE)), // 大力士甲虫
            NECROMANTIC_SCROLL = registerCurio("necromantic_scroll", builder -> builder.noTooltip().rarity(YELLOW).attribute(ConfluenceMagicLib.MINION_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 死灵卷轴
            PAPYRUS_SCARAB = registerCurio("papyrus_scarab", builder -> builder.noTooltip().rarity(YELLOW).attribute(ConfluenceMagicLib.MINION_CAPACITY, 1.0, ADD_VALUE).attribute(LibAttributes.getSummonDamage(), 0.15, ADD_MULTIPLIED_TOTAL).attribute(ConfluenceMagicLib.SUMMON_KNOCKBACK, 2.0, ADD_VALUE)), // 甲虫莎草纸
            PYGMY_NECKLACE = registerCurio("pygmy_necklace", builder -> builder.noTooltip().rarity(LIME).attribute(ConfluenceMagicLib.MINION_CAPACITY, 1.0, ADD_VALUE)); // 矮人项链

    public static final RegistryObject<BaseCurioItem> FLEDGLING_WINGS = registerWings("fledgling_wings", WHITE, 0.3F, 28, true, false);  // 飞行高度：12
    public static final RegistryObject<BaseCurioItem> ANGEL_WINGS = registerWings("angel_wings", PINK, 0.6F, 50, true, false);  // 飞行高度：34
    public static final RegistryObject<BaseCurioItem> DEMON_WINGS = registerWings("demon_wings", PINK, 0.6F, 50, true, false); // 飞行高度：34
    public static final RegistryObject<BaseCurioItem> FAIRY_WINGS = registerWings("fairy_wings", PINK, 0.65F, 56, true, false); // 飞行高度：44
    public static final RegistryObject<BaseCurioItem> FIN_WINGS = registerWings("fin_wings", LIGHT_RED, 0.65F, 56, true, false); // 飞行高度：44
    public static final RegistryObject<BaseCurioItem> FROZEN_WINGS = registerWings("frozen_wings", PINK, 0.65F, 56, true, false);  // 飞行高度：44
    public static final RegistryObject<BaseCurioItem> HARPY_WINGS = registerWings("harpy_wings", PINK, 0.65F, 56, true, false); // 飞行高度：44
    public static final RegistryObject<BaseCurioItem> JETPACK = registerWings("jetpack", PINK, 0.65F, 63, true, false);  // 飞行高度：51
    public static final RegistryObject<BaseCurioItem> LEAF_WINGS = registerWings("leaf_wings", PINK, 0.6F, 50, true, false);  // 飞行高度：34
    public static final RegistryObject<BaseCurioItem> BAT_WINGS = registerWings("bat_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final RegistryObject<BaseCurioItem> BEE_WINGS = registerWings("bee_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final RegistryObject<BaseCurioItem> BUTTERFLY_WINGS = registerWings("butterfly_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final RegistryObject<BaseCurioItem> FLAME_WINGS = registerWings("flame_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final RegistryObject<BaseCurioItem> HOVERBOARD = registerWings("hoverboard", PINK, 0.68F, 74, true, true); // 飞行高度：62
    public static final RegistryObject<BaseCurioItem> BONE_WINGS = registerWings("bone_wings", PINK, 0.68F, 74, true, false); // 飞行高度：62
    public static final RegistryObject<BaseCurioItem> MOTHRON_WINGS = registerWings("mothron_wings", YELLOW, 0.68F, 74, true, false); // 飞行高度：62
    public static final RegistryObject<BaseCurioItem> SPECTRE_WINGS = registerWings("spectre_wings", YELLOW, 0.68F, 74, true, false);// 飞行高度：62
    public static final RegistryObject<BaseCurioItem> BEETLE_WINGS = registerWings("beetle_wings", LIME, 0.68F, 74, true, false); // 飞行高度：62
    public static final RegistryObject<BaseCurioItem> FESTIVE_WINGS = registerWings("festive_wings", PINK, 0.7F, 84, true, false); // 飞行高度：71
    public static final RegistryObject<BaseCurioItem> SPOOKY_WINGS = registerWings("spooky_wings", LIME, 0.7F, 84, true, false); // 飞行高度：71
    public static final RegistryObject<BaseCurioItem> TATTERED_WINGS = registerWings("tattered_wings", LIME, 0.7F, 84, true, false); // 飞行高度：71
    public static final RegistryObject<BaseCurioItem> STEAMPUNK_WINGS = registerWings("steampunk_wings", YELLOW, 0.7F, 84, true, false); // 飞行高度：71
    public static final RegistryObject<BaseCurioItem> BETSYS_WINGS = registerWings("betsys_wings", YELLOW, 0.72F, 84, true, true); // 飞行高度：79
    public static final RegistryObject<BaseCurioItem> EMPRESS_WINGS = registerWings("empress_wings", CYAN, 0.85F, 86, true, false);  // 飞行高度：85
    public static final RegistryObject<BaseCurioItem> FISHRON_WINGS = registerWings("fishron_wings", YELLOW, 0.85F, 92, true, false);  // 飞行高度：95
    public static final RegistryObject<BaseCurioItem> NEBULA_WINGS = registerWings("nebula_wings", RED, 0.85F, 92, true, true); // 飞行高度：95
    public static final RegistryObject<BaseCurioItem> VORTEX_BOOSTER = registerWings("vortex_booster", RED, 0.85F, 92, true, true); // 飞行高度：95
    public static final RegistryObject<BaseCurioItem> SOLAR_WINGS = registerWings("solar_wings", RED, 0.85F, 92, true, false); // 飞行高度：95
    public static final RegistryObject<BaseCurioItem> STARDUST_WINGS = registerWings("stardust", RED, 0.85F, 92, true, false); // 飞行高度：95

    public static final RegistryObject<BaseCurioItem> CLOTHIER_VOODOO_DOLL = registerCurio("clothier_voodoo_doll", builder -> builder.rarity(BLUE).accessories(units(CLOTHIER$KILLER)));
    public static final RegistryObject<BaseCurioItem> GUIDE_VOODOO_DOLL = registerDirectly("guide_voodoo_doll", GuideVooDooDollItem::new);

    private static RegistryObject<BaseCurioItem> registerCurio(String name, Consumer<BaseCurioItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(name);
            consumer.accept(builder);
            return builder.build();
        });
    }

    private static <I extends BaseCurioItem> RegistryObject<I> registerDirectly(String name, Function<String, I> function) {
        return ITEMS.register(name, () -> function.apply(name));
    }

    private static RegistryObject<BaseCurioItem> registerWings(String name, ModRarity rarity, float flySpeed, int flyTicks, boolean couldGlide, boolean horizontalFlight) {
        RegistryObject<BaseCurioItem> item = registerCurio(name, builder -> builder.rarity(rarity)
                .accessories(of(TCItems.MAY$FLY, MayFlyAbilityValue.of(name, 1100, flySpeed, flyTicks, couldGlide, horizontalFlight)))
                .attribute(PortAttributesExtension.fallDamageMultiplier(), -100, ADD_VALUE));
        WINGS.add(item);
        return item;
    }

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
}
