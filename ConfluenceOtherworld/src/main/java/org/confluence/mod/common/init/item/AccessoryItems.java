package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.gen.ModItemTagsProvider;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.accessory.*;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.api.primitive.*;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.common.item.curio.health.BandOfRegeneration;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.init.TEAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static org.confluence.lib.common.component.ModRarity.*;
import static org.confluence.terra_curio.common.component.AccessoriesComponent.of;
import static org.confluence.terra_curio.common.component.AccessoriesComponent.units;

@SuppressWarnings("all")
public class AccessoryItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final List<DeferredItem<BaseCurioItem>> WINGS = new ArrayList<>();

    public static final ValueType<Unit, UnitValue> LUCKY$COIN = ValueType.ofUnit("lucky_coin");
    public static final ValueType<Unit, UnitValue> VINE$ROPE = ValueType.ofUnit("vine_rope");
    public static final ValueType<Unit, UnitValue> AUTO$GET$MANA = ValueType.ofUnit("auto_get_mama");
    public static final ValueType<Unit, UnitValue> HURT$GET$MANA = ValueType.ofUnit("hurt_get_mana");
    public static final ValueType<Unit, UnitValue> FAST$MANA$GENERATION = ValueType.ofUnit("faset_mana_regeneration");
    public static final ValueType<Unit, UnitValue> HIGH$TEST$FISHING$LINE = ValueType.ofUnit("high_test_fishing_line");
    public static final ValueType<Unit, UnitValue> TACKLE$BOX = ValueType.ofUnit("tackle_box");
    public static final ValueType<Unit, UnitValue> LAVAPROOF$FISHING$HOOK = ValueType.ofUnit("lavaproof_fishing_hook");
    public static final ValueType<Unit, UnitValue> SPECTRE$GOGGLES = ValueType.ofUnit("spectre_goggles");
    public static final ValueType<Unit, UnitValue> PAINT$SPRAYER = ValueType.ofUnit("paint_sprayer");

    public static final ValueType<Float, FloatValue> MANA$USE$REDUCE = ValueType.ofFloat("mana_use_reduce", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType<Float, FloatValue> REDUCE$HEALING$COOLDOWN = ValueType.ofFloat("reduce_healing_cooldown", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType<Float, FloatValue> FISHING$POWER = ValueType.ofFloat("fishing_power", FloatValue.ADDITION, 0.0F);
    public static final ValueType<Integer, IntegerValue> ADDITIONAL$MANA = ValueType.ofInteger("additional_mana", IntegerValue.ADDITION, 0);
    public static final ValueType<Integer, IntegerValue> SPECIAL$PRICE = ValueType.ofInteger("special_price", IntegerValue.GET_MAX, 0);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> MANA$PICKUP$RANGE = ValueType.create("mana_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(1.75F, 0), PickupRangeAbilityValue::new);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> COIN$PICKUP$RANGE = ValueType.create("coin_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(2.0F, 0), PickupRangeAbilityValue::new);

    public static final DeferredItem<BaseCurioItem> ADHESIVE_BANDAGE = registerCurio("adhesive_bandage", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BLEEDING)))),
            MEDICATED_BANDAGE = registerCurio("medicated_bandage", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.POISON, ModEffects.BLEEDING)))),
            POCKET_MIRROR = registerCurio("pocket_mirror", builder -> builder.rarity(ORANGE).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.STONED)))),
            REFLECTIVE_SHADES = registerCurio("reflective_shades", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.BLINDNESS, ModEffects.STONED)))),
            ARMOR_POLISH = registerCurio("armor_polish", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BROKEN_ARMOR)))),
            ARMOR_BRACING = registerCurio("armor_bracing", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.WEAKNESS, ModEffects.BROKEN_ARMOR)))),
            MEGAPHONE = registerCurio("megaphone", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED)))),
            NAZAR = registerCurio("nazar", builder -> builder.rarity(GREEN).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.CURSED)))),
            COUNTERCURSE_MANTRA = registerCurio("countercurse_mantra", builder -> builder.rarity(LIGHT_RED).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED, ModEffects.CURSED))));

    public static final DeferredItem<BaseCurioItem> NATURES_GIFT = registerCurio("natures_gift", builder -> builder.rarity(ORANGE).accessories(of(MANA$USE$REDUCE, 0.06F))),
            MANA_FLOWER = registerCurio("mana_flower", builder -> builder.tooltips(1).rarity(LIGHT_RED).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F))),
            CELESTIAL_MAGNET = registerCurio("celestial_magnet", builder -> builder.rarity(LIGHT_RED).accessories(of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            CELESTIAL_EMBLEM = registerCurio("celestial_emblem", builder -> builder.rarity(PINK).accessories(of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0))).attribute(TCAttributes.getMagicDamage(), 0.15, ADD_MULTIPLIED_TOTAL)),
            MAGNET_FLOWER = registerCurio("magnet_flower", builder -> builder.tooltips(2).rarity(PINK).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F), of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            ARCANE_FLOWER = registerCurio("arcane_flower", builder -> builder.tooltips(2).rarity(PINK).accessories(units(AUTO$GET$MANA), of(MANA$USE$REDUCE, 0.08F)).attribute(TCAttributes.AGGRO, -400, ADD_VALUE)),
            BAND_OF_STARPOWER = registerCurio("band_of_starpower", builder -> builder.accessories(of(ADDITIONAL$MANA, 20))),
            MANA_REGENERATION_BAND = registerCurio("mana_regeneration_band", builder -> builder.tooltips(1).accessories(units(FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 20))),
            MAGIC_CUFFS = registerCurio("magic_cuffs", builder -> builder.tooltips(1).rarity(GREEN).accessories(units(HURT$GET$MANA, FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 20))),
            CELESTIAL_CUFFS = registerCurio("celestial_cuffs", builder -> builder.tooltips(2).rarity(PINK).accessories(units(HURT$GET$MANA, FAST$MANA$GENERATION), of(ADDITIONAL$MANA, 20), of(MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))),
            MANA_CLOAK = registerCurio("mana_cloak", builder -> builder.tooltips(3).rarity(PINK).accessories(units(AUTO$GET$MANA), of(TCItems.STAR$CLOCK, true), of(MANA$USE$REDUCE, 0.08F))),
            PHILOSOPHERS_STONE = registerCurio("philosophers_stone", builder -> builder.rarity(LIGHT_RED).accessories(of(REDUCE$HEALING$COOLDOWN, 0.25F))),
            CHARM_OF_MYTHS = registerDirectly("charm_of_myths", name -> new BandOfRegeneration(BaseCurioItem.builder(name).rarity(LIGHT_PURPLE).accessories(of(REDUCE$HEALING$COOLDOWN, 0.25F))));

    public static final DeferredItem<BaseCurioItem> HIGH_TEST_FISHING_LINE = registerCurio("high_test_fishing_line", builder -> builder.accessories(units(HIGH$TEST$FISHING$LINE))), // 优质钓鱼线
            TACKLE_BOX = registerCurio("tackle_box", builder -> builder.accessories(units(TACKLE$BOX))), // 钓具箱
            ANGLER_TACKLE_BAG = registerCurio("angler_tackle_bag", builder -> builder.rarity(ORANGE).accessories(units(HIGH$TEST$FISHING$LINE, TACKLE$BOX), of(FISHING$POWER, 10.0F))), // 渔夫渔具袋
            LAVAPROOF_FISHING_HOOK = registerCurio("lavaproof_fishing_hook", builder -> builder.rarity(LIME).accessories(units(LAVAPROOF$FISHING$HOOK))), // 防熔岩钓钩
            LAVAPROOF_TACKLE_BAG = registerCurio("lavaproof_tackle_bag", builder -> builder.rarity(YELLOW).accessories(units(HIGH$TEST$FISHING$LINE, TACKLE$BOX, LAVAPROOF$FISHING$HOOK), of(FISHING$POWER, 10.0F))), // 防熔岩渔具袋
            FISHING_BOBBER = ITEMS.register("fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.COMMON)), // 钓鱼浮标
            GLOWING_FISHING_BOBBER = ITEMS.register("glowing_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.GLOWING)), // 发光钓鱼浮标
            LAVA_MOSS_FISHING_BOBBER = ITEMS.register("lava_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.LAVA)), // 熔岩苔藓钓鱼浮标
            HELIUM_MOSS_FISHING_BOBBER = ITEMS.register("helium_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.HELIUM)), // 氦苔藓钓鱼浮标
            NEON_MOSS_FISHING_BOBBER = ITEMS.register("neon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.NEON)), // 氖苔藓钓鱼浮标
            ARGON_MOSS_FISHING_BOBBER = ITEMS.register("argon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.ARGON)), // 氩苔藓钓鱼浮标
            KRYPTON_MOSS_FISHING_BOBBER = ITEMS.register("krypton_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.KRYPTON)), // 氪苔藓钓鱼浮标
            XENON_MOSS_FISHING_BOBBER = ITEMS.register("xenon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.XENON)); // 氙苔藓钓鱼浮标


    public static final DeferredItem<BaseCurioItem> MECHANICAL_LENS = registerDirectly("mechanical_lens", name -> new MechanicalLens(BaseCurioItem.builder("mechanical_lens").rarity(ORANGE).tooltips(1).accessories(of(TCItems.INFORMATION, List.of(TCItems.MECHANICAL$LENS))))); //机械晶状体
    /* 标尺 */
    /* 机械标尺 */

    /* 自动安放器 */
    public static final DeferredItem<BaseCurioItem> PAINT_SPRAYER = registerCurio("paint_sprayer", builder -> builder.rarity(ORANGE).accessories(units(PAINT$SPRAYER))); // 喷漆器

    /* 向导巫毒娃娃 */
    /* 服装商巫毒娃娃 */
    public static final DeferredItem<BaseCurioItem> LUCKY_COIN = registerCurio("lucky_coin", builder -> builder.rarity(PINK).accessories(units(LUCKY$COIN)).attribute(Attributes.LUCK, 0.05, ADD_VALUE)), // 幸运币
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
            CHROMATIC_CLOAK = registerCurio("chromatic_cloak", builder -> builder.rarity(PINK).accessories(of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SHIMMER)))); // 炫彩斗篷

    public static final DeferredItem<BaseCurioItem> SUMMONER_EMBLEM = registerCurio("summoner_emblem", builder -> builder.noTooltip().rarity(LIGHT_RED).attribute(TEAttributes.SUMMON_DAMAGE, 0.15, ADD_MULTIPLIED_TOTAL)), // 召唤师徽章
            APPRENTICES_SCARF = registerCurio("apprentices_scarf", builder -> builder.noTooltip().rarity(PINK).attribute(TEAttributes.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(TEAttributes.SUMMON_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 学徒围巾
            SQUIRES_SHIELD = registerCurio("squires_shield", builder -> builder.noTooltip().rarity(PINK).attribute(TEAttributes.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(TEAttributes.SUMMON_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 侍卫护盾
            HUNTRESSS_BUCKLER = registerCurio("huntresss_buckler", builder -> builder.noTooltip().rarity(PINK).attribute(TEAttributes.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(TEAttributes.SUMMON_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 女猎人圆盾
            MONKS_BELT = registerCurio("monks_belt", builder -> builder.rarity(PINK).noTooltip().attribute(TEAttributes.SENTRY_CAPACITY, 1.0, ADD_VALUE).attribute(TEAttributes.SUMMON_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 武僧腰带
            HERCULES_BEETLE = registerCurio("hercules_beetle", builder -> builder.noTooltip().rarity(LIME).attribute(TEAttributes.SUMMON_DAMAGE, 0.15, ADD_MULTIPLIED_TOTAL).attribute(TEAttributes.SUMMON_KNOCKBACK, 2.0, ADD_VALUE)), // 大力士甲虫
            NECROMANTIC_SCROLL = registerCurio("necromantic_scroll", builder -> builder.noTooltip().rarity(YELLOW).attribute(TEAttributes.MINION_CAPACITY, 1.0, ADD_VALUE).attribute(TEAttributes.SUMMON_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 死灵卷轴
            PAPYRUS_SCARAB = registerCurio("papyrus_scarab", builder -> builder.noTooltip().rarity(YELLOW).attribute(TEAttributes.MINION_CAPACITY, 1.0, ADD_VALUE).attribute(TEAttributes.SUMMON_DAMAGE, 0.15, ADD_MULTIPLIED_TOTAL).attribute(TEAttributes.SUMMON_KNOCKBACK, 2.0, ADD_VALUE)), // 甲虫莎草纸
            PYGMY_NECKLACE = registerCurio("pygmy_necklace", builder -> builder.noTooltip().rarity(LIME).attribute(TEAttributes.MINION_CAPACITY, 1.0, ADD_VALUE)); // 矮人项链

    public static final DeferredItem<BaseCurioItem> FLEDGLING_WINGS = registerWings("fledgling_wings", WHITE, 0.3F, 28, true, false);  // 飞行高度：12
    public static final DeferredItem<BaseCurioItem> ANGEL_WINGS = registerWings("angel_wings", PINK, 0.6F, 50, true, false);  // 飞行高度：34
    public static final DeferredItem<BaseCurioItem> DEMON_WINGS = registerWings("demon_wings", PINK, 0.6F, 50, true, false); // 飞行高度：34
    public static final DeferredItem<BaseCurioItem> FAIRY_WINGS = registerWings("fairy_wings", PINK, 0.65F, 56, true, false); // 飞行高度：44
    public static final DeferredItem<BaseCurioItem> FIN_WINGS = registerWings("fin_wings", LIGHT_RED, 0.65F, 56, true, false); // 飞行高度：44
    public static final DeferredItem<BaseCurioItem> FROZEN_WINGS = registerWings("frozen_wings", PINK, 0.65F, 56, true, false);  // 飞行高度：44
    public static final DeferredItem<BaseCurioItem> HARPY_WINGS = registerWings("harpy_wings", PINK, 0.65F, 56, true, false); // 飞行高度：44
    public static final DeferredItem<BaseCurioItem> JETPACK = registerWings("jetpack", PINK, 0.65F, 63, true, false);  // 飞行高度：51
    public static final DeferredItem<BaseCurioItem> LEAF_WINGS = registerWings("leaf_wings", PINK, 0.6F, 50, true, false);  // 飞行高度：34
    public static final DeferredItem<BaseCurioItem> BAT_WINGS = registerWings("bat_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final DeferredItem<BaseCurioItem> BEE_WINGS = registerWings("bee_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final DeferredItem<BaseCurioItem> BUTTERFLY_WINGS = registerWings("butterfly_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final DeferredItem<BaseCurioItem> FLAME_WINGS = registerWings("flame_wings", PINK, 0.68F, 62, true, false); // 飞行高度：54
    public static final DeferredItem<BaseCurioItem> HOVERBOARD = registerWings("hoverboard", PINK, 0.68F, 74, true, true); // 飞行高度：62
    public static final DeferredItem<BaseCurioItem> BONE_WINGS = registerWings("bone_wings", PINK, 0.68F, 74, true, false); // 飞行高度：62
    public static final DeferredItem<BaseCurioItem> MOTHRON_WINGS = registerWings("mothron_wings", YELLOW, 0.68F, 74, true, false); // 飞行高度：62
    public static final DeferredItem<BaseCurioItem> SPECTRE_WINGS = registerWings("spectre_wings", YELLOW, 0.68F, 74, true, false);// 飞行高度：62
    public static final DeferredItem<BaseCurioItem> BEETLE_WINGS = registerWings("beetle_wings", LIME, 0.68F, 74, true, false); // 飞行高度：62
    public static final DeferredItem<BaseCurioItem> FESTIVE_WINGS = registerWings("festive_wings", PINK, 0.7F, 84, true, false); // 飞行高度：71
    public static final DeferredItem<BaseCurioItem> SPOOKY_WINGS = registerWings("spooky_wings", LIME, 0.7F, 84, true, false); // 飞行高度：71
    public static final DeferredItem<BaseCurioItem> TATTERED_WINGS = registerWings("tattered_wings", LIME, 0.7F, 84, true, false); // 飞行高度：71
    public static final DeferredItem<BaseCurioItem> STEAMPUNK_WINGS = registerWings("steampunk_wings", YELLOW, 0.7F, 84, true, false); // 飞行高度：71
    public static final DeferredItem<BaseCurioItem> BETSYS_WINGS = registerWings("betsys_wings", YELLOW, 0.72F, 84, true, true); // 飞行高度：79
    public static final DeferredItem<BaseCurioItem> EMPRESS_WINGS = registerWings("empress_wings", CYAN, 0.85F, 86, true, false);  // 飞行高度：85
    public static final DeferredItem<BaseCurioItem> FISHRON_WINGS = registerWings("fishron_wings", YELLOW, 0.85F, 92, true, false);  // 飞行高度：95
    public static final DeferredItem<BaseCurioItem> NEBULA_WINGS = registerWings("nebula_wings", RED, 0.85F, 92, true, true); // 飞行高度：95
    public static final DeferredItem<BaseCurioItem> VORTEX_BOOSTER = registerWings("vortex_booster", RED, 0.85F, 92, true, true); // 飞行高度：95
    public static final DeferredItem<BaseCurioItem> SOLAR_WINGS = registerWings("solar_wings", RED, 0.85F, 92, true, false); // 飞行高度：95
    public static final DeferredItem<BaseCurioItem> STARDUST_WINGS = registerWings("stardust", RED, 0.85F, 92, true, false); // 飞行高度：95

    private static DeferredItem<BaseCurioItem> registerCurio(String name, Consumer<BaseCurioItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(name);
            consumer.accept(builder);
            return builder.build();
        });
    }

    private static <I extends BaseCurioItem> DeferredItem<I> registerDirectly(String name, Function<String, I> function) {
        return ITEMS.register(name, () -> function.apply(name));
    }

    private static DeferredItem<BaseCurioItem> registerWings(String name, ModRarity rarity, float flySpeed, int flyTicks, boolean couldGlide, boolean horizontalFlight) {
        DeferredItem<BaseCurioItem> item = registerCurio(name, builder -> builder.rarity(rarity)
                .accessories(of(TCItems.MAY$FLY, MayFlyAbilityValue.of(name, flySpeed, flyTicks, couldGlide, horizontalFlight)))
                .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100, ADD_VALUE));
        WINGS.add(item);
        return item;
    }

    public static void acceptTags(ModItemTagsProvider provider) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> accessory = provider.tag(TCTags.ACCESSORY);
        ITEMS.getEntries().forEach(item -> accessory.add(item.get()));
        accessory.add(
                ModItems.PARADOX_INTERACTIVE_MEDAL.get(),
                ModItems.BOREDOMS_PACT_FALLING_RESOLVE.get()
        );
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> wings = provider.tag(ModTags.Items.WINGS);
        WINGS.forEach(item -> wings.add(item.get()));
        wings.add(TCItems.CELESTIAL_STARBOARD.get());
    }

    public static void applyLuckyCoin(ServerPlayer player, Entity target) {
        if (!CommonConfigs.DROP_MONEY.get()) return;
        RandomSource randomSource = player.getRandom();
        if (TCUtils.hasAccessoriesType(player, LUCKY$COIN) && randomSource.nextFloat() < 0.2F) {
            Item item;
            float a = randomSource.nextFloat();
            if (a < 0.01F) {
                item = ModItems.GOLDEN_COIN.get();
            } else if (a < 0.099F) {
                item = ModItems.SILVER_COIN.get();
            } else {
                item = ModItems.COPPER_COIN.get();
            }
            ItemStack itemStack = item.getDefaultInstance();
            itemStack.setCount(randomSource.nextInt(1, 3));
            LibUtils.createItemEntity(itemStack, target.getX(), target.getY(), target.getZ(), player.level(), 0);
        }
    }

    public static void applyHurtGetMana(ServerPlayer serverPlayer, DamageSource damageSource, int amount) {
        if (TCUtils.hasAccessoriesType(serverPlayer, HURT$GET$MANA)) {
            if (!damageSource.is(DamageTypes.DROWN) && !damageSource.is(TCTags.HARMFUL_EFFECT)) {
                PlayerUtils.receiveMana(serverPlayer, () -> amount);
            }
        }
    }
}
