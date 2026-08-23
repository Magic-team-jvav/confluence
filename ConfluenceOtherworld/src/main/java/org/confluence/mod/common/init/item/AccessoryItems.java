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

import static org.confluence.lib.common.component.ModRarity.*;

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
    public static int AFK_INDEX = -1;

    public static final ValueType.FloatType MANA$USE$REDUCE = ValueType.ofFloat("mana_use_reduce", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType.FloatType REDUCE$HEALING$COOLDOWN = ValueType.ofFloat("reduce_healing_cooldown", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType.FloatType FISHING$POWER = ValueType.ofFloat("fishing_power", FloatValue.ADDITION, 0.0F);
    public static final ValueType.IntegerType ADDITIONAL$MANA = ValueType.ofInteger("additional_mana", IntegerValue.ADDITION, 0);
    public static final ValueType.IntegerType SPECIAL$PRICE = ValueType.ofInteger("special_price", IntegerValue.GET_MAX, 0);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> MANA$PICKUP$RANGE = ValueType.create("mana_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(1.75F, 0), PickupRangeAbilityValue::new);
    public static final ValueType<Tuple<Float, Integer>, PickupRangeAbilityValue> COIN$PICKUP$RANGE = ValueType.create("coin_pickup_range", PickupRangeAbilityValue.COMBINE_RULE, PickupRangeAbilityValue.CODEC, new Tuple<>(2.0F, 0), PickupRangeAbilityValue::new);

    public static final PortDeferredItem<BaseCurioItem> ADHESIVE_BANDAGE = registerCurio("adhesive_bandage", builder -> builder.rarity(LIGHT_RED)),
            MEDICATED_BANDAGE = registerCurio("medicated_bandage", builder -> builder.rarity(PINK)),
            POCKET_MIRROR = registerCurio("pocket_mirror", builder -> builder.rarity(ORANGE)),
            REFLECTIVE_SHADES = registerCurio("reflective_shades", builder -> builder.rarity(PINK)),
            ARMOR_POLISH = registerCurio("armor_polish", builder -> builder.rarity(LIGHT_RED)),
            ARMOR_BRACING = registerCurio("armor_bracing", builder -> builder.rarity(PINK)),
            MEGAPHONE = registerCurio("megaphone", builder -> builder.rarity(LIGHT_RED)),
            NAZAR = registerCurio("nazar", builder -> builder.rarity(GREEN)),
            COUNTERCURSE_MANTRA = registerCurio("countercurse_mantra", builder -> builder.rarity(LIGHT_RED));

    public static final PortDeferredItem<BaseCurioItem> NATURES_GIFT = registerCurio("natures_gift", builder -> builder.rarity(ORANGE)),
            MANA_FLOWER = registerCurio("mana_flower", builder -> builder.tooltips(1).rarity(LIGHT_RED)),
            CELESTIAL_MAGNET = registerCurio("celestial_magnet", builder -> builder.rarity(LIGHT_RED)),
            CELESTIAL_EMBLEM = registerCurio("celestial_emblem", builder -> builder.rarity(PINK)),
            MAGNET_FLOWER = registerCurio("magnet_flower", builder -> builder.tooltips(2).rarity(PINK)),
            ARCANE_FLOWER = registerCurio("arcane_flower", builder -> builder.tooltips(2).rarity(PINK)),
            BAND_OF_STARPOWER = registerCurio("band_of_starpower", builder -> {}),
            MANA_REGENERATION_BAND = registerCurio("mana_regeneration_band", builder -> builder.tooltips(1)),
            MAGIC_CUFFS = registerCurio("magic_cuffs", builder -> builder.tooltips(1).rarity(GREEN)),
            CELESTIAL_CUFFS = registerCurio("celestial_cuffs", builder -> builder.tooltips(2).rarity(PINK)),
            MANA_CLOAK = registerCurio("mana_cloak", builder -> builder.tooltips(3).rarity(PINK)),
            PHILOSOPHERS_STONE = registerCurio("philosophers_stone", builder -> builder.rarity(LIGHT_RED)),
            CHARM_OF_MYTHS = registerDirectly("charm_of_myths", name -> new BandOfRegeneration(BaseCurioItem.builder(name).rarity(LIGHT_PURPLE)));

    public static final PortDeferredItem<BaseCurioItem> HIGH_TEST_FISHING_LINE = registerCurio("high_test_fishing_line", builder -> {}), // 优质钓鱼线
            TACKLE_BOX = registerCurio("tackle_box", builder -> {}), // 钓具箱
            ANGLER_TACKLE_BAG = registerCurio("angler_tackle_bag", builder -> builder.rarity(ORANGE)), // 渔夫渔具袋
            LAVAPROOF_FISHING_HOOK = registerCurio("lavaproof_fishing_hook", builder -> builder.rarity(LIME)), // 防熔岩钓钩
            LAVAPROOF_TACKLE_BAG = registerCurio("lavaproof_tackle_bag", builder -> builder.rarity(YELLOW).tooltips(1)), // 防熔岩渔具袋
            FISHING_BOBBER = ITEMS.register("fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.COMMON)), // 钓鱼浮标
            GLOWING_FISHING_BOBBER = ITEMS.register("glowing_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.GLOWING)), // 发光钓鱼浮标
            LAVA_MOSS_FISHING_BOBBER = ITEMS.register("lava_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.LAVA)), // 熔岩苔藓钓鱼浮标
            HELIUM_MOSS_FISHING_BOBBER = ITEMS.register("helium_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.HELIUM)), // 氦苔藓钓鱼浮标
            NEON_MOSS_FISHING_BOBBER = ITEMS.register("neon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.NEON)), // 氖苔藓钓鱼浮标
            ARGON_MOSS_FISHING_BOBBER = ITEMS.register("argon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.ARGON)), // 氩苔藓钓鱼浮标
            KRYPTON_MOSS_FISHING_BOBBER = ITEMS.register("krypton_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.KRYPTON)), // 氪苔藓钓鱼浮标
            XENON_MOSS_FISHING_BOBBER = ITEMS.register("xenon_moss_fishing_bobber", () -> new FishingBobber(CurioFishingHook.Variant.XENON)); // 氙苔藓钓鱼浮标


    public static final PortDeferredItem<BaseCurioItem> MECHANICAL_LENS = registerDirectly("mechanical_lens", name -> new MechanicalLens(BaseCurioItem.builder("mechanical_lens").rarity(ORANGE).tooltips(1))); //机械晶状体
    /* 标尺 */
    /* 机械标尺 */

    /* 自动安放器 */
    public static final PortDeferredItem<BaseCurioItem> PAINT_SPRAYER = registerCurio("paint_sprayer", builder -> builder.rarity(ORANGE)); // 喷漆器

    public static final PortDeferredItem<BaseCurioItem> LUCKY_COIN = registerCurio("lucky_coin", builder -> builder.rarity(PINK)), // 幸运币
            GOLD_RING = registerCurio("gold_ring", builder -> builder.rarity(PINK)), // 金戒指
            COIN_RING = registerCurio("coin_ring", builder -> builder.rarity(PINK)), // 钱币戒指
            DISCOUNT_CARD = registerCurio("discount_card", builder -> builder.rarity(PINK)), // 优惠卡
            GREEDY_RING = registerCurio("greedy_ring", builder -> builder.rarity(LIGHT_PURPLE)), // 贪婪戒指
            GUIDE_TO_PLANT_FIBER_CORDAGE = registerCurio("guide_to_plant_fiber_cordage", builder -> {}), // 植物纤维绳索宝典
            RADIO_THING = registerDirectly("radio_thing", name -> new RadioThing(BaseCurioItem.builder(name).rarity(BLUE).tooltips(1))), // 收音机
            SPECTRE_GOGGLES = registerDirectly("spectre_goggles", name -> new SpectreGoggles(BaseCurioItem.builder(name).rarity(PINK).tooltips(1))), // 幽灵护目镜
            CHROMATIC_CLOAK = registerCurio("chromatic_cloak", builder -> builder.rarity(PINK)), // 炫彩斗篷
            STRESS_BALL = registerCurio("stress_ball", builder -> builder.rarity(BLUE).tooltips(1)); // 压力球

    public static final PortDeferredItem<BaseCurioItem> SUMMONER_EMBLEM = registerCurio("summoner_emblem", builder -> builder.noTooltip().rarity(LIGHT_RED)), // 召唤师徽章
            APPRENTICES_SCARF = registerCurio("apprentices_scarf", builder -> builder.noTooltip().rarity(PINK)), // 学徒围巾
            SQUIRES_SHIELD = registerCurio("squires_shield", builder -> builder.noTooltip().rarity(PINK)), // 侍卫护盾
            HUNTRESSS_BUCKLER = registerCurio("huntresss_buckler", builder -> builder.noTooltip().rarity(PINK)), // 女猎人圆盾
            MONKS_BELT = registerCurio("monks_belt", builder -> builder.rarity(PINK).noTooltip()), // 武僧腰带
            HERCULES_BEETLE = registerCurio("hercules_beetle", builder -> builder.noTooltip().rarity(LIME)), // 大力士甲虫
            NECROMANTIC_SCROLL = registerCurio("necromantic_scroll", builder -> builder.noTooltip().rarity(YELLOW)), // 死灵卷轴
            PAPYRUS_SCARAB = registerCurio("papyrus_scarab", builder -> builder.noTooltip().rarity(YELLOW)), // 甲虫莎草纸
            PYGMY_NECKLACE = registerCurio("pygmy_necklace", builder -> builder.noTooltip().rarity(LIME)); // 矮人项链


    public static final PortDeferredItem<BaseCurioItem> CLOTHIER_VOODOO_DOLL = registerCurio("clothier_voodoo_doll", builder -> builder.rarity(BLUE));
    public static final PortDeferredItem<BaseCurioItem> GUIDE_VOODOO_DOLL = registerDirectly("guide_voodoo_doll", GuideVooDooDollItem::new);

    private static PortDeferredItem<BaseCurioItem> registerCurio(String name, Consumer<BaseCurioItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(name);
            consumer.accept(builder);
            return builder.build();
        });
    }

    private static <I extends BaseCurioItem> PortDeferredItem<I> registerDirectly(String name, Function<String, I> function) {
        return ITEMS.register(name, () -> function.apply(name));
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
