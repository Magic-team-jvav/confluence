package org.confluence.terra_curio.common.init;

import com.google.common.collect.ImmutableListMultimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.*;
import org.confluence.terra_curio.common.item.*;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.common.item.curio.NightBonusCurioItem;
import org.confluence.terra_curio.common.item.curio.RequiresModLoadedCurioItem;
import org.confluence.terra_curio.common.item.curio.combat.*;
import org.confluence.terra_curio.common.item.curio.expert.GravityGlobe;
import org.confluence.terra_curio.common.item.curio.expert.ShieldOfCthulhu;
import org.confluence.terra_curio.common.item.curio.expert.ShinnyStone;
import org.confluence.terra_curio.common.item.curio.health.BandOfRegeneration;
import org.confluence.terra_curio.common.item.curio.information.MultiInfoCurioItem;
import org.confluence.terra_curio.common.item.curio.master.BasePoint;
import org.confluence.terra_curio.common.item.curio.movement.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static org.confluence.lib.common.component.ModRarity.*;
import static org.confluence.terra_curio.api.primitive.ValueType.create;
import static org.confluence.terra_curio.api.primitive.ValueType.ofUnit;
import static org.confluence.terra_curio.common.component.PrimitiveValueComponent.of;
import static org.confluence.terra_curio.common.component.PrimitiveValueComponent.units;

@SuppressWarnings("all")
public final class TCItems {
    public static final DeferredRegister.Items OTHERS = DeferredRegister.createItems(TerraCurio.MODID);
    public static final DeferredRegister.Items CURIOS = DeferredRegister.createItems(TerraCurio.MODID);

    // client side info_check
    public static final ValueType<List<TooltipComponentsValue.Storage>, TooltipComponentsValue> INFORMATION = create("information", TooltipComponentsValue.EXPANSION, TooltipComponentsValue.CODEC, List.of(), TooltipComponentsValue::new);
    public static final TooltipComponentsValue.Storage HOUR$WATCH = TooltipComponentsValue.create("hour_watch");
    public static final TooltipComponentsValue.Storage HALF$HOUR$WATCH = TooltipComponentsValue.create("half_hour_watch");
    public static final TooltipComponentsValue.Storage MINUTE$WATCH = TooltipComponentsValue.create("minute_watch");
    public static final TooltipComponentsValue.Storage WEATHER$RADIO = TooltipComponentsValue.create("weather_radio");
    public static final TooltipComponentsValue.Storage $SEXTANT = TooltipComponentsValue.create("sextant");
    public static final TooltipComponentsValue.Storage FISHERMANS$POCKET$GUIDE = TooltipComponentsValue.create("fishermans_pocket_guide");
    public static final TooltipComponentsValue.Storage METAL$DETECTOR = TooltipComponentsValue.create("metal_detector");
    public static final TooltipComponentsValue.Storage LIFE$FORM$ANALYZER = TooltipComponentsValue.create("life_form_analyzer");
    public static final TooltipComponentsValue.Storage $RADAR = TooltipComponentsValue.create("radar");
    public static final TooltipComponentsValue.Storage TALLY$COUNTER = TooltipComponentsValue.create("tally_counter");
    public static final TooltipComponentsValue.Storage DPS$METER = TooltipComponentsValue.create("dps_meter");
    public static final TooltipComponentsValue.Storage $STOPWATCH = TooltipComponentsValue.create("stopwatch");
    public static final TooltipComponentsValue.Storage $COMPASS = TooltipComponentsValue.create("compass");
    public static final TooltipComponentsValue.Storage DEPTH$METER = TooltipComponentsValue.create("depth_meter");
    public static final TooltipComponentsValue.Storage MECHANICAL$LENS = TooltipComponentsValue.create("mechanical_lens");

    public static final List<TooltipComponentsValue.Storage> FULL_INFO = List.of(
            MINUTE$WATCH,
            WEATHER$RADIO,
            $SEXTANT,
            FISHERMANS$POCKET$GUIDE,
            METAL$DETECTOR,
            LIFE$FORM$ANALYZER,
            $RADAR,
            TALLY$COUNTER,
            DPS$METER,
            $STOPWATCH,
            $COMPASS,
            DEPTH$METER
    );


    // client side
    public static final ValueType<Set<TagKey<Fluid>>, FluidTagsValue> FLUID$WALK = ValueType.create("fluid_walk", FluidTagsValue.EXPANSION, FluidTagsValue.CODEC, Set.of(), FluidTagsValue::new);
    public static final ValueType<Unit, UnitValue> AUTO$ATTACK = ofUnit("auto_attack");
    public static final ValueType<Unit, UnitValue> SPRINTING = ofUnit("sprinting");
    public static final ValueType<Unit, UnitValue> SCOPE = ofUnit("scope");
    public static final ValueType<Unit, UnitValue> GRAVITY$GLOBE = ofUnit("gravity_globe");
    public static final ValueType<Unit, UnitValue> $MAGILUMINESCENCE = ofUnit("magiluminescence");
    public static final ValueType<Unit, UnitValue> BONE$GLOVE = ofUnit("bone_glove");

    // both side
    public static final ValueType<Unit, UnitValue> FLOAT$ON$LIQUID$SURFACE = ofUnit("float_on_liquid_surface");
    public static final ValueType<Unit, UnitValue> ICE$SAFE = ofUnit("ice_safe");
    public static final ValueType<Unit, UnitValue> SHIELD$OF$CTHULHU = ofUnit("shield_of_cthulhu");

    // require updates
    public static final ValueType<Unit, UnitValue> FIRE$ATTACK = ofUnit("fire_attack");
    public static final ValueType<Unit, UnitValue> HONEY$COMB = ofUnit("honey_comb");
    public static final ValueType<Unit, UnitValue> FROZEN$TURTLE$SHELL = ofUnit("frozen_turtle_shell");
    public static final ValueType<Unit, UnitValue> MAGIC$QUIVER = ofUnit("magic_quiver");
    public static final ValueType<Unit, UnitValue> IGNITE$ARROW = ofUnit("ignite_arrow");
    public static final ValueType<Unit, UnitValue> FIRE$IMMUNE = ofUnit("fire_immune");
    public static final ValueType<Unit, UnitValue> FROZEN$IMMUNE = ofUnit("frozen_immune");
    public static final ValueType<Unit, UnitValue> FLOWER$BOOTS = ofUnit("flower_boots");
    public static final ValueType<Unit, UnitValue> ICE$SPEED = ofUnit("ice_speed");
    public static final ValueType<Unit, UnitValue> DIVING = ofUnit("diving");
    public static final ValueType<Unit, UnitValue> BRAIN$OF$CONFUSION = ofUnit("brain_of_confusion");
    public static final ValueType<Unit, UnitValue> HIVE$PACK = ofUnit("hive_pack");
    public static final ValueType<Unit, UnitValue> INFINITE$FLIGHT = ofUnit("infinite_flight");

    public static final ValueType<Set<Holder<MobEffect>>, MobEffectsValue> EFFECT$IMMUNITIES = ValueType.create("effect_immunities", MobEffectsValue.MERGE, MobEffectsValue.CODEC, Set.of(), MobEffectsValue::new);
    public static final ValueType<Boolean, BooleanValue> STAR$CLOCK = ValueType.create("star_clock", BooleanValue.OR, BooleanValue.CODEC, false, BooleanValue::new);
    public static final ValueType<Float, FloatValue> INVULNERABLE$TICKS$MULTIPLIER = ValueType.ofFloat("invulnerable_ticks_multiplier", FloatValue.GET_MAX_WITHIN_0_TO_100, 1.0F);
    public static final ValueType<Float, FloatValue> LAVA$HURT$REDUCE = ValueType.ofFloat("lava_hurt_reduce", FloatValue.GET_MAX_WITHIN_0_TO_1, 0.0F);
    public static final ValueType<Integer, IntegerValue> LAVA$IMMUNE$TICKS = ValueType.ofInteger("lava_immune_ticks", IntegerValue.GET_MAX, 0);
    public static final ValueType<Byte, ByteValue> RIGHT$CLICK$DELAY$SUBSTRACTOR = ValueType.create("right_click_delay_substractor", ByteValue.GET_MAX, ByteValue.CODEC, (byte) 0, ByteValue::new);
    public static final ValueType<Map<ResourceKey<Item>, MayFlyAbilityValue.FlyStack>, MayFlyAbilityValue> MAY$FLY = ValueType.create("may_fly", MayFlyAbilityValue.COMBINE_RULE, MayFlyAbilityValue.CODEC, Map.of(), MayFlyAbilityValue::new);
    public static final ValueType<Integer, IntegerValue> LUMINANCE = ValueType.ofInteger("luminance", IntegerValue.GET_ABS_MAX, 0);
    public static final ValueType<Unit, UnitValue> NEPTUNES$SHELL = ofUnit("neptunes_shell");
    public static final ValueType<Byte, ByteValue> WALL$CLIMB = ValueType.create("wall_climb", ByteValue.ADDITION_WITHIN_0_TO_2, ByteValue.CODEC, (byte) 0, ByteValue::new);
    public static final ValueType<Float, FloatValue> CLOUD = ValueType.ofFloat("cloud", FloatValue.GET_SELF, 0.0F);
    public static final ValueType<Tuple<Float, Integer>, OneTimeJumpAbilityValue> BLIZZARD = ValueType.create("blizzard", OneTimeJumpAbilityValue.COMBINE_RULE, OneTimeJumpAbilityValue.CODEC, new Tuple<>(0.0F, 0), OneTimeJumpAbilityValue::new);
    public static final ValueType<Tuple<Float, Integer>, OneTimeJumpAbilityValue> SAND$STORM = ValueType.create("sand_storm", OneTimeJumpAbilityValue.COMBINE_RULE, OneTimeJumpAbilityValue.CODEC, new Tuple<>(0.0F, 0), OneTimeJumpAbilityValue::new);
    public static final ValueType<Float, FloatValue> FART = ValueType.ofFloat("fart", FloatValue.GET_SELF, 0.0F);
    public static final ValueType<Float, FloatValue> TSUNAMI = ValueType.ofFloat("tsunami", FloatValue.GET_SELF, 0.0F);
    public static final ValueType<TagKey<EntityType<?>>, EntityTypeTagValue> MOB$IGNORE = ValueType.create("mob_ignore", EntityTypeTagValue.GET_SELF, EntityTypeTagValue.CODEC, TCTags.NOTHING, EntityTypeTagValue::new);
    public static final ValueType<Float, FloatValue> INJURY$FREE = ValueType.ofFloat("injury_free", FloatValue.ADDITION_WITHIN_0_TO_1, 0.0F);
    public static final ValueType<Integer, IntegerValue> TOTEM$WITH$COOLDOWN = ValueType.ofInteger("totem_with_cooldown", IntegerValue.GET_MIN_GREAT_EQUAL_THAN_0, -1);

    // no updates
    public static final ValueType<ImmutableListMultimap<Holder<Attribute>, AttributeModifier>, AttributeModifiersValue> ATTRIBUTES = ValueType.create("attributes", AttributeModifiersValue.MERGE, AttributeModifiersValue.CODEC, ImmutableListMultimap.of(), AttributeModifiersValue::new);
    public static final ValueType<List<Component>, ComponentsValue> COMPONENTS = ValueType.create("components", ComponentsValue.COMBINE_RULE, ComponentsValue.CODEC, List.of(), ComponentsValue::new);


    public static final DeferredItem<MasterItem> STAR = OTHERS.register("star", MasterItem::new);
    public static final DeferredItem<MasterItem> ICON = OTHERS.register("icon", MasterItem::new);
    public static final DeferredItem<BasePoint> BASE_POINT = OTHERS.register("base_point", BasePoint::new);
    public static final DeferredItem<BaseCurioItem> EVERLASTING = OTHERS.register("everlasting", () -> BaseCurioItem.builder("everlasting").rarity(ModRarity.MASTER).build());

    public static final DeferredItem<BlockItem> WORKSHOP = OTHERS.register("workshop", () -> new BlockItem(TCBlocks.WORKSHOP.get(), new Item.Properties()));
    public static final DeferredItem<DemonHeart> DEMON_HEART = OTHERS.register("demon_heart", DemonHeart::new);
    public static final DeferredItem<MagicMirror> MAGIC_MIRROR = OTHERS.register("magic_mirror", () -> new MagicMirror(BLUE));
    public static final DeferredItem<CellPhone> CELL_PHONE = OTHERS.register("cell_phone", CellPhone::new);
    public static final DeferredItem<DivingHelmet> DIVING_HELMET = OTHERS.register("diving_helmet", DivingHelmet::new);

    public static final DeferredItem<BaseCurioItem> BEZOAR = registerCurio("bezoar", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.POISON)))), // 牛黄 中毒
            HOLY_WATER = registerCurio("holy_water", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.WITHER)))), // 圣水 凋零
            DETOXIFICATION_CAPSULE = registerCurio("detoxification_capsule", builder -> builder.rarity(PINK).jeiInfos(0).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.POISON, MobEffects.WITHER)))), // 解毒囊
            VITAMINS = registerCurio("vitamins", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.WEAKNESS)))), // 维生素 虚弱
            ENERGY_BAR = registerCurio("energy_bar", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.HUNGER)))), // 能量棒 饥饿
            NUTRIENT_SOLUTION = registerCurio("nutrient_solution", builder -> builder.rarity(PINK).jeiInfos(0).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.WEAKNESS, MobEffects.HUNGER)))), // 营养液
            BLINDFOLD = registerCurio("blindfold", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.BLINDNESS)))), // 蒙眼布 失明
            FLASHLIGHT = registerCurio("flashlight", builder -> builder.rarity(ORANGE).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.DARKNESS)))), // 手电筒 黑暗
            SEARCHLIGHT = registerCurio("searchlight", builder -> builder.rarity(PINK).jeiInfos(0).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.BLINDNESS, MobEffects.DARKNESS)))), // 探照灯
            FAST_CLOCK = registerCurio("fast_clock", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.MOVEMENT_SLOWDOWN)))), // 快走时钟 缓慢
            TRIFOLD_MAP = registerCurio("trifold_map", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.CONFUSION)))), // 三折地图 反胃
            THE_PLAN = registerCurio("the_plan", builder -> builder.rarity(PINK).jeiInfos(0).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.CONFUSION)))), // 计划书
            HAND_DRILL = registerCurio("hand_drill", builder -> builder.rarity(LIGHT_RED).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.DIG_SLOWDOWN)))), // 手钻 挖掘疲劳
            SHOT_PUT = registerCurio("shot_put", builder -> builder.rarity(GREEN).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.LEVITATION)))), // 铅球 漂浮
            EXPLORERS_EQUIPMENT = registerCurio("explorers_equipment", builder -> builder.jeiInfos(0).rarity(PINK).accessories(of(EFFECT$IMMUNITIES, Set.of(MobEffects.DIG_SLOWDOWN, MobEffects.LEVITATION)))), // 探险家宝具
            ANKH_CHARM = registerCurio("ankh_charm", builder -> builder.jeiInfos(0).rarity(LIGHT_PURPLE).accessories(of(EFFECT$IMMUNITIES, Set.of(
                    MobEffects.POISON, MobEffects.WITHER,
                    MobEffects.WEAKNESS, MobEffects.HUNGER,
                    MobEffects.BLINDNESS, MobEffects.DARKNESS,
                    MobEffects.MOVEMENT_SLOWDOWN, MobEffects.CONFUSION,
                    MobEffects.DIG_SLOWDOWN, MobEffects.LEVITATION)))), // 十字章护身符
            ANKH_SHIELD = registerCurio("ankh_shield", builder -> builder.jeiInfos(0).tooltips(1).rarity(LIME)
                    .accessories(units(FROZEN$IMMUNE), of(EFFECT$IMMUNITIES, Set.of(MobEffects.POISON, MobEffects.WITHER, MobEffects.WEAKNESS, MobEffects.HUNGER, MobEffects.BLINDNESS, MobEffects.DARKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.CONFUSION, MobEffects.DIG_SLOWDOWN, MobEffects.LEVITATION)))
                    .attribute(Attributes.KNOCKBACK_RESISTANCE, 1.0, ADD_VALUE)
                    .attribute(Attributes.ARMOR, 4.0, ADD_VALUE)), // 十字章护盾
            STAR_CLOAK = registerCurio("star_cloak", builder -> builder.jeiInfos(1).rarity(LIGHT_RED).accessories(of(STAR$CLOCK, false))), // 星星斗篷
            STAR_VEIL = registerCurio("star_veil", builder -> builder.rarity(LIGHT_PURPLE).jeiInfos(0).accessories(of(STAR$CLOCK, false), of(INVULNERABLE$TICKS$MULTIPLIER, 2.0F)).tooltips(1)), // 星星面纱
            BEE_CLOAK = registerCurio("bee_cloak", builder -> builder.jeiInfos(0).rarity(LIGHT_RED).accessories(units(HONEY$COMB), of(STAR$CLOCK, false), of(INVULNERABLE$TICKS$MULTIPLIER, 2.0F)).tooltips(1)), // 蜜蜂斗篷
            BLACK_BELT = registerCurio("black_belt", builder -> builder.rarity(LIME).attribute(TCAttributes.getDodgeChance(), 0.1, ADD_VALUE)), // 黑腰带
            SUN_STONE = registerCurio("sun_stone", SunStone::new), // 太阳石
            MOON_STONE = registerDirectly("moon_stone", (name, builder) -> new NightBonusCurioItem(0.2F, builder.rarity(PINK)
                    .attribute(Attributes.ATTACK_SPEED, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ARMOR, 4.0, ADD_VALUE)
                    .attribute(Attributes.BLOCK_BREAK_SPEED, 0.15, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getCriticalChance(), 0.02, ADD_VALUE)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getMagicDamage(), 0.1, ADD_MULTIPLIED_TOTAL))), // 月亮石
            CELESTIAL_STONE = registerDirectly("celestial_stone", (name, builder) -> new CelestialStone(builder.rarity(LIME).jeiInfos(0)
                    .attribute(Attributes.ATTACK_SPEED, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ARMOR, 4.0, ADD_VALUE)
                    .attribute(Attributes.BLOCK_BREAK_SPEED, 0.15, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getCriticalChance(), 0.02, ADD_VALUE)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getMagicDamage(), 0.1, ADD_MULTIPLIED_TOTAL))), // 天界石
            MOON_CHARM = registerDirectly("moon_charm", (name, builder) -> new MoonCharm(builder.rarity(LIGHT_RED)
                    .attribute(TCAttributes.getCriticalChance(), 0.02, ADD_VALUE)
                    .attribute(Attributes.ATTACK_DAMAGE, 0.051, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_SPEED, 0.051, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.MOVEMENT_SPEED, 0.05, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ARMOR, 3.0, ADD_VALUE)
                    .attribute(Attributes.JUMP_STRENGTH, 0.1, ADD_MULTIPLIED_TOTAL))), // 月光护身符
            NEPTUNES_SHELL = registerDirectly("neptunes_shell", (name, builder) -> new NeptunesShell(builder.rarity(PINK)
                    .accessories(units(NEPTUNES$SHELL))
                    .attribute(Attributes.SUBMERGED_MINING_SPEED, 0.8, ADD_VALUE))), // 海神贝壳
            MOON_SHELL = registerDirectly("moon_shell", (name, builder) -> new MoonShell(builder.rarity(LIGHT_PURPLE).jeiInfos(0)
                    .accessories(units(NEPTUNES$SHELL))
                    .attribute(TCAttributes.getCriticalChance(), 0.02, ADD_VALUE)
                    .attribute(Attributes.ATTACK_DAMAGE, 0.051, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_SPEED, 0.051, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.MOVEMENT_SPEED, 0.05, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ARMOR, 3.0, ADD_VALUE)
                    .attribute(Attributes.JUMP_STRENGTH, 0.1, ADD_MULTIPLIED_TOTAL))), // 月亮贝壳
            CELESTIAL_SHELL = registerDirectly("celestial_shell", (name, builder) -> new CelestialShell(builder.rarity(YELLOW).jeiInfos(0)
                    .accessories(units(NEPTUNES$SHELL))
                    .attribute(Attributes.SUBMERGED_MINING_SPEED, 0.8, ADD_VALUE)
                    .attribute(Attributes.ATTACK_SPEED, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ARMOR, 4.0, ADD_VALUE)
                    .attribute(Attributes.BLOCK_BREAK_SPEED, 0.15, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getCriticalChance(), 0.02, ADD_VALUE)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getMagicDamage(), 0.1, ADD_MULTIPLIED_TOTAL))), // 天界贝壳
            COBALT_SHIELD = registerCurio("cobalt_shield", builder -> builder.rarity(GREEN).attribute(Attributes.KNOCKBACK_RESISTANCE, 1.0, ADD_VALUE)), // 钴护盾
            CROSS_NECKLACE = registerCurio("cross_necklace", builder -> builder.rarity(LIGHT_RED).accessories(of(INVULNERABLE$TICKS$MULTIPLIER, 2.0F))), // 十字项链
            RANGER_EMBLEM = registerCurio("ranger_emblem", builder -> builder.rarity(LIGHT_RED).noTooltip().attribute(TCAttributes.getRangedDamage(), 0.15, ADD_MULTIPLIED_TOTAL)), // 游侠徽章
            WARRIOR_EMBLEM = registerCurio("warrior_emblem", builder -> builder.rarity(LIGHT_RED).noTooltip().attribute(Attributes.ATTACK_DAMAGE, 0.15, ADD_MULTIPLIED_TOTAL)), // 战士徽章
            SORCERER_EMBLEM = registerCurio("sorcerer_emblem", builder -> builder.rarity(LIGHT_RED).noTooltip().attribute(TCAttributes.getMagicDamage(), 0.15, ADD_MULTIPLIED_TOTAL)), // 巫士徽章
            AVENGER_EMBLEM = registerCurio("avenger_emblem", builder -> builder.jeiInfos(0).rarity(PINK).noTooltip()
                    .attribute(Attributes.ATTACK_DAMAGE, 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getRangedDamage(), 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getMagicDamage(), 0.12, ADD_MULTIPLIED_TOTAL)), // 复仇者勋章
            EYE_OF_THE_GOLEM = registerCurio("eye_of_the_golem", builder -> builder.rarity(LIME).noTooltip().attribute(TCAttributes.getCriticalChance(), 0.1, ADD_VALUE)), // 石巨人之眼
            DESTROYER_EMBLEM = registerCurio("destroyer_emblem", builder -> builder.jeiInfos(0).rarity(LIME).noTooltip()
                    .attribute(Attributes.ATTACK_DAMAGE, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getMagicDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getCriticalChance(), 0.08, ADD_VALUE)), // 毁灭者勋章
            FERAL_CLAWS = registerCurio("feral_claws", builder -> builder.rarity(ORANGE)
                    .accessories(units(AUTO$ATTACK))
                    .attribute(Attributes.ATTACK_SPEED, 0.12, ADD_MULTIPLIED_TOTAL)), // 狂爪手套
            TITAN_GLOVE = registerCurio("titan_glove", builder -> builder.rarity(LIGHT_RED).noTooltip()
                    .attribute(Attributes.ATTACK_KNOCKBACK, 1.0, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 泰坦手套
            POWER_GLOVE = registerCurio("power_glove", builder -> builder.jeiInfos(0).rarity(PINK)
                    .accessories(units(AUTO$ATTACK))
                    .attribute(Attributes.ATTACK_SPEED, 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 1.0, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 强力手套
            MECHANICAL_GLOVE = registerCurio("mechanical_glove", builder -> builder.jeiInfos(0).rarity(LIGHT_PURPLE)
                    .accessories(units(AUTO$ATTACK))
                    .attribute(Attributes.ATTACK_DAMAGE, 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_SPEED, 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 1.0, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 机械手套
            FIRE_GAUNTLET = registerCurio("fire_gauntlet", builder -> builder.jeiInfos(0).rarity(LIME).tooltips(1)
                    .accessories(units(AUTO$ATTACK, FIRE$ATTACK))
                    .attribute(Attributes.ATTACK_DAMAGE, 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_SPEED, 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 1.0, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 0.1, ADD_MULTIPLIED_TOTAL)), // 烈火手套
            FLESH_KNUCKLES = registerCurio("flesh_knuckles", builder -> builder.rarity(PINK)
                    .attribute(Attributes.ARMOR, 8.0, ADD_VALUE)
                    .attribute(TCAttributes.AGGRO, 400, ADD_VALUE)), // 血肉指虎
            BERSERKERS_GLOVE = registerCurio("berserkers_glove", builder -> builder.jeiInfos(0).noTooltip().rarity(PINK)
                    .accessories(units(AUTO$ATTACK))
                    .attribute(Attributes.ARMOR, 8.0, ADD_VALUE)
                    .attribute(Attributes.ATTACK_SPEED, 0.12, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 1.0, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.AGGRO, 400, ADD_VALUE)), // 狂战士手套
            PALADINS_SHIELD = registerDirectly("paladins_shield", name -> new PaladinsShield(BaseCurioItem.builder(name).rarity(YELLOW)
                    .attribute(Attributes.ARMOR, 6.0, ADD_VALUE)
                    .attribute(Attributes.KNOCKBACK_RESISTANCE, 1.0, ADD_VALUE))), // 圣骑士护盾
            HERO_SHIELD = registerDirectly("hero_shield", name -> new PaladinsShield(BaseCurioItem.builder(name).jeiInfos(0).rarity(PINK)
                    .attribute(Attributes.ARMOR, 10.0, ADD_VALUE)
                    .attribute(Attributes.KNOCKBACK_RESISTANCE, 1.0, ADD_VALUE)
                    .attribute(TCAttributes.AGGRO, 400, ADD_VALUE))), // 英雄护盾
            FROZEN_TURTLE_SHELL = registerDirectly("frozen_turtle_shell", name -> new FrozenTurtleShell(BaseCurioItem.builder(name).rarity(PINK).particle(TerraCurio.asResource("frozen_turtle_shell")).accessories(units(FROZEN$TURTLE$SHELL)))), // 冰冻海龟壳
            FROZEN_SHIELD = registerDirectly("frozen_shield", name -> new PaladinsShield(BaseCurioItem.builder(name).jeiInfos(0).rarity(PINK).tooltips(1)
                    .accessories(units(FROZEN$TURTLE$SHELL)).attribute(Attributes.ARMOR, 6.0, ADD_VALUE)
                    .attribute(Attributes.KNOCKBACK_RESISTANCE, 1.0, ADD_VALUE))), // 冰冻护盾
            HONEY_COMB = registerCurio("honey_comb", builder -> builder.rarity(GREEN).accessories(units(HONEY$COMB))), // 蜂窝
            SHARK_TOOTH_NECKLACE = registerCurio("shark_tooth_necklace", builder -> builder.noTooltip().attribute(TCAttributes.getArmorPenetration(), 5.0, ADD_VALUE)), // 鲨牙项链
            STINGER_NECKLACE = registerCurio("stinger_necklace", builder -> builder.rarity(PINK).jeiInfos(0).accessories(units(HONEY$COMB)).attribute(TCAttributes.getArmorPenetration(), 5.0, ADD_VALUE)), // 毒刺项链
            PANIC_NECKLACE = registerDirectly("panic_necklace", name -> new PanicNecklace(BaseCurioItem.builder(name))), // 恐慌项链
            SWEETHEART_NECKLACE = registerDirectly("sweetheart_necklace", name -> new PaladinsShield(BaseCurioItem.builder(name).jeiInfos(0).rarity(ORANGE).accessories(units(HONEY$COMB)))), // 甜心项链
            MAGIC_QUIVER = registerCurio("magic_quiver", builder -> builder.rarity(LIGHT_RED).accessories(units(MAGIC$QUIVER))
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getRangedVelocity(), 0.2, ADD_MULTIPLIED_TOTAL)), // 魔法箭袋
            MOLTEN_QUIVER = registerCurio("molten_quiver", builder -> builder.rarity(PINK).jeiInfos(0).accessories(units(MAGIC$QUIVER, IGNITE$ARROW)).tooltips(2)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getRangedVelocity(), 0.2, ADD_MULTIPLIED_TOTAL)), // 熔火箭袋
            STALKERS_QUIVER = registerCurio("stalkers_quiver", builder -> builder.rarity(PINK).jeiInfos(0).accessories(units(MAGIC$QUIVER))
                    .tooltips(1)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getRangedVelocity(), 0.2, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.AGGRO, -400, ADD_VALUE)), // 潜行者箭袋
            RIFLE_SCOPE = registerCurio("rifle_scope", builder -> builder.rarity(LIGHT_RED).accessories(units(SCOPE)).tooltips(1)), // 步枪瞄准镜
            SNIPER_SCOPE = registerCurio("sniper_scope", builder -> builder.rarity(LIME).jeiInfos(0).accessories(units(SCOPE)).tooltips(1)
                    .attribute(TCAttributes.getCriticalChance(), 0.1, ADD_VALUE)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)), // 狙击镜
            RECON_SCOPE = registerCurio("recon_scope", builder -> builder.rarity(PINK).jeiInfos(0).accessories(units(SCOPE)).tooltips(3)
                    .attribute(TCAttributes.getCriticalChance(), 0.1, ADD_VALUE)
                    .attribute(TCAttributes.getRangedDamage(), 0.1, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.AGGRO, -400, ADD_VALUE)), // 侦察镜
            MAGMA_STONE = registerCurio("magma_stone", builder -> builder.rarity(ORANGE).jeiInfos(1)), // 岩浆石
            OBSIDIAN_ROSE = registerCurio("obsidian_rose", builder -> builder.rarity(ORANGE).accessories(of(LAVA$HURT$REDUCE, 0.5F))), // 黑曜石玫瑰
            OBSIDIAN_SHIELD = registerCurio("obsidian_shield", builder -> builder.jeiInfos(0).rarity(LIGHT_RED).accessories(units(FIRE$IMMUNE))
                    .attribute(Attributes.KNOCKBACK_RESISTANCE, 1.0, ADD_VALUE)
                    .attribute(Attributes.ARMOR, 2.0, ADD_VALUE)), // 黑曜石护盾
            OBSIDIAN_SKULL = registerCurio("obsidian_skull", builder -> builder.jeiInfos(0).rarity(GREEN).accessories(units(FIRE$IMMUNE)).attribute(Attributes.ARMOR, 1, ADD_VALUE)), // 黑曜石骷髅头
            MOLTEN_SKULL_ROSE = registerCurio("molten_skull_rose", builder -> builder.rarity(LIGHT_PURPLE).jeiInfos(0).tooltips(2)
                    .accessories(units(FIRE$IMMUNE), of(LAVA$IMMUNE$TICKS, 140), of(LAVA$HURT$REDUCE, 0.5F))), // 熔火骷髅头玫瑰
            OBSIDIAN_SKULL_ROSE = registerCurio("obsidian_skull_rose", builder -> builder.jeiInfos(0).rarity(PINK).accessories(units(FIRE$IMMUNE), of(LAVA$HURT$REDUCE, 0.5F)).tooltips(1)), // 黑曜石骷髅头玫瑰
            HAND_WARMER = registerCurio("hand_warmer", builder -> builder.jeiInfos(0).rarity(GREEN).accessories(units(FROZEN$IMMUNE))), // 暖手宝
            PUTRID_SCENT = registerCurio("putrid_scent", builder -> builder.rarity(LIGHT_PURPLE)
                    .attribute(Attributes.ATTACK_DAMAGE, 0.05, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.getCriticalChance(), 0.05, ADD_VALUE)
                    .attribute(TCAttributes.AGGRO, -400, ADD_VALUE)), // 腐香囊
            SHACKLE = registerCurio("shackle", builder -> builder.rarity(BLUE).noTooltip()); // 镣铐

    public static final DeferredItem<BaseCurioItem> TOOLBELT = registerCurio("toolbelt", builder -> builder.noTooltip().rarity(ORANGE).attribute(Attributes.BLOCK_INTERACTION_RANGE, 1.0, ADD_VALUE)), // 工具腰带
            TOOLBOX = registerCurio("toolbox", builder -> builder.noTooltip().rarity(GREEN).attribute(Attributes.BLOCK_INTERACTION_RANGE, 1.0, ADD_VALUE)), // 工具箱
            EXTENDO_GRIP = registerCurio("extendo_grip", builder -> builder.noTooltip().rarity(ORANGE).attribute(Attributes.BLOCK_INTERACTION_RANGE, 3.0, ADD_VALUE)), // 加长握爪
            PORTABLE_CEMENT_MIXER = registerCurio("portable_cement_mixer", builder -> builder.rarity(ORANGE).accessories(of(RIGHT$CLICK$DELAY$SUBSTRACTOR, (byte) 1))), // 便携式水泥搅拌机
            BRICK_LAYER = registerCurio("brick_layer", builder -> builder.rarity(ORANGE).accessories(of(RIGHT$CLICK$DELAY$SUBSTRACTOR, (byte) 1))), // 砌砖刀
            ARCHITECT_GIZMO_PACK = registerCurio("architect_gizmo_pack", builder -> builder.jeiInfos(0).rarity(PINK)
                    .accessories(of(RIGHT$CLICK$DELAY$SUBSTRACTOR, (byte) 2))
                    .attribute(Attributes.BLOCK_INTERACTION_RANGE, 3.0, ADD_VALUE)), // 建筑师发明背包
            ANCIENT_CHISEL = registerCurio("ancient_chisel", builder -> builder.attribute(Attributes.BLOCK_BREAK_SPEED, 0.25, ADD_MULTIPLIED_TOTAL)), // 远古凿子
            HAND_OF_CREATION = registerDirectly("hand_of_creation", name -> new StepStool(BaseCurioItem.builder(name).tooltips(1).jeiInfos(0).rarity(LIGHT_PURPLE)
                    .accessories(of(RIGHT$CLICK$DELAY$SUBSTRACTOR, (byte) 3))
                    .attribute(Attributes.BLOCK_INTERACTION_RANGE, 3.0, ADD_VALUE)
                    .attribute(Attributes.BLOCK_BREAK_SPEED, 0.25, ADD_MULTIPLIED_TOTAL)
                    .attribute(TCAttributes.PICKUP_RANGE, 6.25, ADD_VALUE))); // 创造之手

    public static final DeferredItem<BaseCurioItem> BAND_OF_REGENERATION = registerDirectly("band_of_regeneration", name -> new BandOfRegeneration(BaseCurioItem.builder(name)));

    public static final DeferredItem<BaseCurioItem> COPPER_WATCH = registerCurio("copper_watch", builder -> builder.rarity(WHITE).jeiInfos(0).accessories(of(INFORMATION, List.of(HOUR$WATCH)))), // 铜表
            TIN_WATCH = registerCurio("tin_watch", builder -> builder.jeiInfos(0).rarity(WHITE).accessories(of(INFORMATION, List.of(HOUR$WATCH)))), // 锡表
            SILVER_WATCH = registerCurio("silver_watch", builder -> builder.jeiInfos(0).rarity(WHITE).accessories(of(INFORMATION, List.of(HALF$HOUR$WATCH)))), // 银表
            TUNGSTEN_WATCH = registerCurio("tungsten_watch", builder -> builder.jeiInfos(0).rarity(WHITE).accessories(of(INFORMATION, List.of(HALF$HOUR$WATCH)))), // 钨表
            GOLD_WATCH = registerDirectly("gold_watch", (name, builder) -> new MultiInfoCurioItem(builder.makesPiglinsNeutral().jeiInfos(0).accessories(of(INFORMATION, List.of(MINUTE$WATCH))))), // 金表
            PLATINUM_WATCH = registerDirectly("platinum_watch", (name, builder) -> new MultiInfoCurioItem(builder.jeiInfos(0).accessories(of(INFORMATION, List.of(MINUTE$WATCH))))), // 铂金表
            DEPTH_METER = registerDirectly("depth_meter", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of(DEPTH$METER))))), // 深度计
            COMPASS = registerDirectly("compass", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of($COMPASS))))), // 罗盘
            RADAR = registerDirectly("radar", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of($RADAR))))), // 雷达
            LIFE_FORM_ANALYZER = registerDirectly("life_form_analyzer", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of(LIFE$FORM$ANALYZER))))), // 生命体分析机
            TALLY_COUNTER = registerDirectly("tally_counter", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of(TALLY$COUNTER))))), // 杀怪计数器
            METAL_DETECTOR = registerDirectly("metal_detector", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of(METAL$DETECTOR))))), // 金属探测器
            STOPWATCH = registerDirectly("stopwatch", (name, builder) -> new MultiInfoCurioItem(builder.jeiInfos(0).accessories(of(INFORMATION, List.of($STOPWATCH))))), // 秒表
            DPS_METER = registerDirectly("dps_meter", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of(DPS$METER))))), // 每秒伤害计数器
            FISHERMANS_POCKET_GUIDE = registerDirectly("fishermans_pocket_guide", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of(FISHERMANS$POCKET$GUIDE))))), // 渔民袖珍宝典
            WEATHER_RADIO = registerDirectly("weather_radio", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of(WEATHER$RADIO))))), // 天气收音机
            SEXTANT = registerDirectly("sextant", (name, builder) -> new MultiInfoCurioItem(builder.accessories(of(INFORMATION, List.of($SEXTANT))))), // 六分仪
            GPS = registerDirectly("gps", (name, builder) -> new MultiInfoCurioItem(builder.rarity(ORANGE).jeiInfos(0).tooltips(2).accessories(of(INFORMATION, List.of(MINUTE$WATCH, DEPTH$METER, $COMPASS))))), // 全球定位系统
            REK_3000 = registerDirectly("rek_3000", (name, builder) -> new MultiInfoCurioItem(builder.rarity(ORANGE).jeiInfos(0).tooltips(2).accessories(of(INFORMATION, List.of($RADAR, LIFE$FORM$ANALYZER, TALLY$COUNTER))))), // R.E.K.3000
            GOBLIN_TECH = registerDirectly("goblin_tech", (name, builder) -> new MultiInfoCurioItem(builder.rarity(ORANGE).jeiInfos(0).tooltips(2).accessories(of(INFORMATION, List.of(METAL$DETECTOR, $STOPWATCH, DPS$METER))))), // 哥布林数据仪
            FISH_FINDER = registerDirectly("fish_finder", (name, builder) -> new MultiInfoCurioItem(builder.rarity(ORANGE).jeiInfos(0).tooltips(2).accessories(of(INFORMATION, List.of(FISHERMANS$POCKET$GUIDE, WEATHER$RADIO, $SEXTANT))))), // 探鱼器
            PDA = registerDirectly("pda", (name, builder) -> new MultiInfoCurioItem(builder.rarity(PINK).jeiInfos(0).tooltips(11).accessories(of(INFORMATION, FULL_INFO)))); // 个人数字助手

    public static final DeferredItem<BaseCurioItem> STEP_STOOL = registerDirectly("step_stool", name -> new StepStool(BaseCurioItem.builder(name))), // 梯凳
            FLYING_CARPET = registerCurio("flying_carpet", builder -> builder.rarity(GREEN).accessories(of(MAY$FLY, MayFlyAbilityValue.of("flying_carpet", 0.5625F, 100, false, true)))), // 飞毯
            AGLET = registerCurio("aglet", builder -> builder.noTooltip().attribute(Attributes.MOVEMENT_SPEED, 0.05, ADD_MULTIPLIED_TOTAL)), // 金属带扣
            ANKLET_OF_THE_WIND = registerCurio("anklet_of_the_wind", builder -> builder.jeiInfos(0).noTooltip().attribute(Attributes.MOVEMENT_SPEED, 0.1, ADD_MULTIPLIED_TOTAL)), // 疾风脚镯
            MAGILUMINESCENCE = registerDirectly("magiluminescence", name -> new RequiresModLoadedCurioItem(BaseCurioItem.builder(name).tooltips(1)
                    .accessories(units($MAGILUMINESCENCE), of(LUMINANCE, 14))
                    .attribute(Attributes.MOVEMENT_SPEED, 0.15, ADD_MULTIPLIED_TOTAL), "sodiumdynamiclights")), // 魔光护符
            LAVA_CHARM = registerCurio("lava_charm", builder -> builder.rarity(ORANGE).accessories(of(LAVA$IMMUNE$TICKS, 140))), // 熔岩护身符
            MAGMA_SKULL = registerCurio("magma_skull", builder -> builder.jeiInfos(0).tooltips(1).rarity(PINK).accessories(
                    units(FIRE$IMMUNE),
                    of(LAVA$IMMUNE$TICKS, 140))), // 岩浆骷髅头
            MOLTEN_CHARM = registerCurio("molten_charm", builder -> builder.tooltips(1).jeiInfos(0).rarity(PINK).accessories(
                    units(FIRE$IMMUNE),
                    of(LAVA$IMMUNE$TICKS, 140))), // 熔火护身符
            CLIMBING_CLAWS = registerCurio("climbing_claws", builder -> builder.tooltips(1).accessories(of(WALL$CLIMB, (byte) 1))), // 攀爬爪
            SHOE_SPIKES = registerCurio("shoe_spikes", builder -> builder.tooltips(1).accessories(of(WALL$CLIMB, (byte) 1))), // 鞋钉
            TIGER_CLIMBING_GEAR = registerCurio("tiger_climbing_gear", builder -> builder.jeiInfos(0).rarity(GREEN).accessories(of(WALL$CLIMB, (byte) 2))), // 猛虎攀爬装备
            TABI = registerCurio("tabi", builder -> builder.rarity(LIME).accessories(units(SPRINTING))), // 分趾厚底袜
            MASTER_NINJA_GEAR = registerCurio("master_ninja_gear", builder -> builder.tooltips(2).jeiInfos(0).rarity(YELLOW)
                    .accessories(units(SPRINTING), of(WALL$CLIMB, (byte) 2))
                    .attribute(TCAttributes.getDodgeChance(), 0.1, ADD_VALUE)), // 忍者大师装备
            ICE_SKATES = registerCurio("ice_skates", BLUE), // 溜冰鞋
            HERMES_BOOTS = registerDirectly("hermes_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).stepHeight())), // 赫尔墨斯靴
            FLURRY_BOOTS = registerDirectly("flurry_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).jeiInfos(1).stepHeight())), // 疾风雪靴
            SAILFISH_BOOTS = registerDirectly("sailfish_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).stepHeight())), // 旗鱼靴
            DUNERIDER_BOOTS = registerDirectly("dunerider_boots", DuneriderBoots::new), // 沙丘行者靴
            ROCKET_BOOTS = registerCurio("rocket_boots", builder -> builder.jeiInfos(0).accessories(of(MAY$FLY, MayFlyAbilityValue.of("rocket_boots", 0.3F, 36, false, false)))), // 火箭靴
            SPECTRE_BOOTS = registerDirectly("spectre_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).rarity(LIGHT_RED).jeiInfos(0)
                    .accessories(of(MAY$FLY, MayFlyAbilityValue.of("spectre_boots", 0.3F, 36, false, false)))
                    .stepHeight())), // 幽灵靴
            FAIRY_BOOTS = registerDirectly("fairy_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).jeiInfos(0).tooltips(1).rarity(PINK)
                    .accessories(units(FLOWER$BOOTS), of(MAY$FLY, MayFlyAbilityValue.of("fairy_boots", 0.3F, 36, false, false)))
                    .stepHeight())), // 仙灵靴
            LIGHTNING_BOOTS = registerDirectly("lightning_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).jeiInfos(0).rarity(PINK)
                    .accessories(of(MAY$FLY, MayFlyAbilityValue.of("lightning_boots", 0.3F, 36, false, false)))
                    .attribute(Attributes.MOVEMENT_SPEED, 0.08, ADD_MULTIPLIED_TOTAL)
                    .stepHeight())), // 闪电靴
            FROSTSPARK_BOOTS = registerDirectly("frostspark_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).jeiInfos(0).rarity(LIME)
                    .accessories(units(ICE$SPEED, ICE$SAFE), of(MAY$FLY, MayFlyAbilityValue.of("frostspark_boots", 0.3F, 40, false, false)))
                    .attribute(Attributes.MOVEMENT_SPEED, 0.08, ADD_MULTIPLIED_TOTAL)
                    .stepHeight())), // 霜花靴
            WATER_WALKING_BOOTS = registerCurio("water_walking_boots", builder -> builder.rarity(LIGHT_RED).accessories(of(FLUID$WALK, Set.of(TCTags.WATER_LIKE_WALK)))), // 水上漂靴
            OBSIDIAN_WATER_WALKING_BOOTS = registerCurio("obsidian_water_walking_boots", builder -> builder.jeiInfos(0).rarity(LIGHT_RED).tooltips(1).accessories(
                    units(FIRE$IMMUNE),
                    of(FLUID$WALK, Set.of(TCTags.WATER_LIKE_WALK, TCTags.LAVA_LIKE_WALK)))), // 黑曜石水上漂靴
            LAVA_WADERS = registerCurio("lava_waders", builder -> builder.rarity(LIME).tooltips(1).jeiInfos(0).accessories(
                    units(FIRE$IMMUNE),
                    of(FLUID$WALK, Set.of(TCTags.WATER_LIKE_WALK, TCTags.LAVA_LIKE_WALK)),
                    of(LAVA$IMMUNE$TICKS, 140),
                    of(LAVA$HURT$REDUCE, 0.5F))), // 熔岩靴
            TERRASPARK_BOOTS = registerDirectly("terraspark_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name).rarity(LIME).tooltips(3).jeiInfos(0).particle(TerraCurio.asResource("terraspark"))
                    .accessories(
                            units(ICE$SPEED, ICE$SAFE, FIRE$IMMUNE),
                            of(MAY$FLY, MayFlyAbilityValue.of("terraspark_boots", 0.3F, 40, false, false)),
                            of(FLUID$WALK, Set.of(TCTags.WATER_LIKE_WALK, TCTags.LAVA_LIKE_WALK)),
                            of(LAVA$IMMUNE$TICKS, 140),
                            of(LAVA$HURT$REDUCE, 0.5F)
                    )
                    .attribute(Attributes.MOVEMENT_SPEED, 0.08, ADD_MULTIPLIED_TOTAL)
                    .stepHeight())), // 泰拉闪耀靴
            CLOUD_IN_A_BOTTLE = registerDirectly("cloud_in_a_bottle", (name, builder) -> new CloudInABottle(builder.particle(TerraCurio.asResource("cloud"))
                    .accessories(of(CLOUD, 1.3F))
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 3, ADD_VALUE))), // 云朵瓶
            BLIZZARD_IN_A_BOTTLE = registerDirectly("blizzard_in_a_bottle", (name, builder) -> new BlizzardInABottle(builder.jeiInfos(0).particle(TerraCurio.asResource("blizzard"))
                    .accessories(of(BLIZZARD, new Tuple<>(0.4F, 14)))
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 3, ADD_VALUE))), // 暴雪瓶
            SANDSTORM_IN_A_BOTTLE = registerDirectly("sandstorm_in_a_bottle", (name, builder) -> new SandstormInABottle(builder.rarity(GREEN).particle(TerraCurio.asResource("sandstorm"))
                    .accessories(of(SAND$STORM, new Tuple<>(0.45F, 17)))
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 3, ADD_VALUE))), // 沙暴瓶
            FART_IN_A_JAR = registerCurio("fart_in_a_jar", builder -> builder.rarity(GREEN)
                    .accessories(of(FART, 1.7F))
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 3, ADD_VALUE)), // 罐中臭屁
            TSUNAMI_IN_A_BOTTLE = registerDirectly("tsunami_in_a_bottle", (name, builder) -> new TsunamiInABottle(builder.particle(TerraCurio.asResource("tsunami"))
                    .accessories(of(TSUNAMI, 1.5F))
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 3, ADD_VALUE))), // 海啸瓶
            SHINY_RED_BALLOON = registerCurio("shiny_red_balloon", builder -> builder
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 2, ADD_VALUE)), // 闪亮红气球
            BALLOON_PUFFERFISH = registerCurio("balloon_pufferfish", builder -> builder
                    .jeiInfos(0)
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 2, ADD_VALUE)), // 气球河豚鱼
            CLOUD_IN_A_BALLOON = registerCurio("cloud_in_a_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(1).jeiInfos(0)
                    .accessories(of(CLOUD, 1.3F))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 4, ADD_VALUE)), // 云朵气球
            BLIZZARD_IN_A_BALLOON = registerCurio("blizzard_in_a_balloon", builder -> builder
                    .jeiInfos(0).rarity(LIGHT_RED).tooltips(1).jeiInfos(0)
                    .accessories(of(BLIZZARD, new Tuple<>(0.4F, 14)))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 4, ADD_VALUE)), // 暴雪气球
            SANDSTORM_IN_A_BALLOON = registerCurio("sandstorm_in_a_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(1).jeiInfos(0)
                    .accessories(of(SAND$STORM, new Tuple<>(0.45F, 17)))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 4, ADD_VALUE)), // 沙暴气球
            FART_IN_A_BALLOON = registerCurio("fart_in_a_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(1).jeiInfos(0)
                    .accessories(of(FART, 1.1F))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 4, ADD_VALUE)), // 臭屁气球
            SHARKRON_BALLOON = registerCurio("sharkron_balloon", builder -> builder
                    .jeiInfos(0)
                    .accessories(of(TSUNAMI, 1.3F))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 4, ADD_VALUE)), // 鲨鱼龙气球
            HONEY_BALLOON = registerCurio("honey_balloon", builder -> builder
                    .rarity(GREEN).tooltips(1).jeiInfos(0)
                    .accessories(units(HONEY$COMB))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 2, ADD_VALUE)), // 蜂蜜气球
            BUNDLE_OF_BALLOONS = registerCurio("bundle_of_balloons", builder -> builder
                    .rarity(YELLOW).tooltips(1).jeiInfos(0)
                    .accessories(
                            of(SAND$STORM, new Tuple<>(0.45F, 17)),
                            of(BLIZZARD, new Tuple<>(0.4F, 14)),
                            of(CLOUD, 1.3F)
                    )
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 7, ADD_VALUE)), // 气球束
            LUCKY_HORSESHOE = registerCurio("lucky_horseshoe", builder -> builder
                    .tooltips(1)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 幸运马掌
            OBSIDIAN_HORSESHOE = registerCurio("obsidian_horseshoe", builder -> builder
                    .jeiInfos(0).rarity(LIGHT_RED).tooltips(1)
                    .accessories(units(FIRE$IMMUNE))
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 黑曜石马掌
            BLUE_HORSESHOE_BALLOON = registerCurio("blue_horseshoe_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(2).jeiInfos(0)
                    .accessories(of(CLOUD, 1.3F))
                    .attribute(Attributes.JUMP_STRENGTH, 0.75, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 蓝马掌气球
            WHITE_HORSESHOE_BALLOON = registerCurio("white_horseshoe_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(2).jeiInfos(0)
                    .accessories(of(BLIZZARD, new Tuple<>(0.4F, 14)))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 白马掌气球
            YELLOW_HORSESHOE_BALLOON = registerCurio("yellow_horseshoe_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(2).jeiInfos(0)
                    .accessories(of(SAND$STORM, new Tuple<>(0.45F, 17)))
                    .attribute(Attributes.JUMP_STRENGTH, 0.75, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 黄马掌气球
            GREEN_HORSESHOE_BALLOON = registerCurio("green_horseshoe_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(2).jeiInfos(0)
                    .accessories(of(FART, 1.1F))
                    .attribute(Attributes.JUMP_STRENGTH, 0.75, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 绿马掌气球
            PINK_HORSESHOE_BALLOON = registerCurio("pink_horseshoe_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(2).jeiInfos(0)
                    .accessories(of(TSUNAMI, 1.3F))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 粉马掌气球
            AMBER_HORSESHOE_BALLOON = registerCurio("amber_horseshoe_balloon", builder -> builder
                    .rarity(LIGHT_RED).tooltips(2).jeiInfos(0)
                    .accessories(units(HONEY$COMB))
                    .attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 琥珀马掌气球
            BUNDLE_OF_HORSESHOE_BALLOONS = registerCurio("bundle_of_horseshoe_balloons", builder -> builder
                    .rarity(YELLOW).tooltips(2).jeiInfos(0)
                    .accessories(
                            of(SAND$STORM, new Tuple<>(0.45F, 17)),
                            of(BLIZZARD, new Tuple<>(0.4F, 14)),
                            of(CLOUD, 1.3F)
                    ).attribute(Attributes.JUMP_STRENGTH, 0.43, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.LUCK, 0.05, ADD_VALUE)
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)), // 马掌气球束
            INNER_TUBE = registerCurio("inner_tube", builder -> builder.rarity(WHITE).accessories(units(FLOAT$ON$LIQUID$SURFACE))),
            FLIPPER = registerCurio("flipper", builder -> builder.noTooltip().attribute(NeoForgeMod.SWIM_SPEED, 0.5, ADD_VALUE)), // 脚蹼
            DIVING_GEAR = registerCurio("diving_gear", builder -> builder.jeiInfos(0).rarity(LIGHT_RED).equipable(EquipmentSlot.HEAD).accessories(units(DIVING)).attribute(NeoForgeMod.SWIM_SPEED, 0.5, ADD_VALUE)), // 潜水装备
            JELLYFISH_NECKLACE = registerDirectly("jellyfish_necklace", name -> new RequiresModLoadedCurioItem(BaseCurioItem.builder(name).rarity(GREEN).accessories(of(LUMINANCE, -12)), "sodiumdynamiclights")), // 水母项链
            JELLYFISH_DIVING_GEAR = registerDirectly("jellyfish_diving_gear", name -> new RequiresModLoadedCurioItem(BaseCurioItem.builder(name).rarity(PINK).tooltips(1).jeiInfos(0)
                    .accessories(units(DIVING), of(LUMINANCE, -12), of(EFFECT$IMMUNITIES, Set.of()))
                    .attribute(NeoForgeMod.SWIM_SPEED, 0.5, ADD_VALUE), "sodiumdynamiclights")), // 水母潜水装备
            ARCTIC_DIVING_GEAR = registerDirectly("arctic_diving_gear", name -> new RequiresModLoadedCurioItem(BaseCurioItem.builder(name).rarity(LIGHT_PURPLE).tooltips(2)
                    .accessories(units(DIVING, ICE$SPEED, FROZEN$IMMUNE), of(LUMINANCE, -12))
                    .attribute(NeoForgeMod.SWIM_SPEED, 0.5, ADD_VALUE), "sodiumdynamiclights")), // 北极潜水装备
            FROG_LEG = registerCurio("frog_leg", builder -> builder
                    .tooltips(1)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 7.0, ADD_VALUE)
                    .attribute(Attributes.JUMP_STRENGTH, 0.6, ADD_MULTIPLIED_TOTAL)), // 蛙腿
            FROG_FLIPPER = registerCurio("frog_flipper", builder -> builder
                    .tooltips(1)
                    .jeiInfos(0)
                    .attribute(NeoForgeMod.SWIM_SPEED, 0.5, ADD_VALUE)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 7.0, ADD_VALUE)
                    .attribute(Attributes.JUMP_STRENGTH, 0.6, ADD_MULTIPLIED_TOTAL)), // 青蛙脚蹼
            FROG_WEBBING = registerCurio("frog_webbing", builder -> builder.rarity(PINK)
                    .tooltips(2)
                    .jeiInfos(0)
                    .accessories(of(WALL$CLIMB, (byte) 2))
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 7.0, ADD_VALUE)
                    .attribute(Attributes.JUMP_STRENGTH, 0.6, ADD_MULTIPLIED_TOTAL)), // 青蛙蹼
            FROG_GEAR = registerCurio("frog_gear", builder -> builder.rarity(PINK)
                    .tooltips(3)
                    .jeiInfos(0)
                    .accessories(of(WALL$CLIMB, (byte) 2))
                    .attribute(NeoForgeMod.SWIM_SPEED, 0.5, ADD_VALUE)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 7.0, ADD_VALUE)
                    .attribute(Attributes.JUMP_STRENGTH, 0.6, ADD_MULTIPLIED_TOTAL)), // 青蛙装备
            AMBHIPIAN_BOOTS = registerDirectly("ambhipian_boots", name -> new BaseSpeedBoots(1, 40, BaseCurioItem.builder(name)
                    .jeiInfos(0)
                    .attribute(Attributes.SAFE_FALL_DISTANCE, 7.0, ADD_VALUE)
                    .attribute(Attributes.JUMP_STRENGTH, 0.6, ADD_MULTIPLIED_TOTAL)
                    .stepHeight())); // 水陆两用靴

    public static final DeferredItem<BaseCurioItem> TREASURE_MAGNET = registerCurio("treasure_magnet", builder -> builder.attribute(TCAttributes.PICKUP_RANGE, 6.25, ADD_VALUE)), // 宝藏磁石
            FLOWER_BOOTS = registerCurio("flower_boots", builder -> builder.rarity(LIME).accessories(units(FLOWER$BOOTS))); // 花靴

    public static final DeferredItem<BaseCurioItem> ANGLER_EARRING = registerCurio("angler_earring", builder -> builder.noTooltip()); // 渔夫耳环

    public static final DeferredItem<BaseCurioItem> ROYAL_GEL = registerCurio("royal_gel", builder -> builder.rarity(EXPERT).accessories(of(MOB$IGNORE, TCTags.SLIME))), // 皇家凝胶
            SHIELD_OF_CTHULHU = registerDirectly("shield_of_cthulhu", (name, builder) -> new ShieldOfCthulhu(builder.rarity(EXPERT).noTooltip()
                    .accessories(units(SHIELD$OF$CTHULHU))
                    .attribute(Attributes.ARMOR, 2, ADD_VALUE))), // 克苏鲁护盾
            WORM_SCARF = registerCurio("worm_scarf", builder -> builder.rarity(EXPERT).accessories(of(INJURY$FREE, 0.17F))), // 蠕虫围巾
            BRAIN_OF_CONFUSION = registerCurio("brain_of_confusion", builder -> builder.rarity(EXPERT).accessories(units(BRAIN$OF$CONFUSION)).tooltips(2)), // 混乱之脑
            HIVE_PACK = registerCurio("hive_pack", builder -> builder.rarity(EXPERT).accessories(units(HIVE$PACK))), // 蜂巢背包
            BONE_GLOVE = registerCurio("bone_glove", builder -> builder.rarity(EXPERT).accessories(units(BONE$GLOVE))), // 骨头手套
    /* 骸骨头盔 */
    /* 挥发明胶 */
    /* 孢子囊 */
    SHINY_STONE = registerDirectly("shiny_stone", name -> new ShinnyStone(BaseCurioItem.builder(name).rarity(EXPERT))), // 闪亮石
            SOARING_INSIGNIA = registerCurio("soaring_insignia", builder -> builder.rarity(EXPERT)
                    .accessories(units(INFINITE$FLIGHT))
                    .attribute(Attributes.MOVEMENT_SPEED, 0.075, ADD_MULTIPLIED_TOTAL)
                    .attribute(Attributes.JUMP_STRENGTH, 0.8, ADD_MULTIPLIED_TOTAL)), // 翱翔徽章
            GRAVITY_GLOBE = registerDirectly("gravity_globe", (name, builder) -> new GravityGlobe(builder.rarity(EXPERT).accessories(units(GRAVITY$GLOBE)))), // 重力球
            CELESTIAL_STARBOARD = registerCurio("celestial_starboard", builder -> builder.rarity(EXPERT).tooltips(2)
                    .accessories(of(MAY$FLY, MayFlyAbilityValue.of("celestial_starboard", 1100, 1.0F, 60, true, true)))
                    .attribute(Attributes.FALL_DAMAGE_MULTIPLIER, -100.0, ADD_VALUE)); // 天界星盘

    public static DeferredItem<BaseCurioItem> registerCurio(String name, Consumer<BaseCurioItem.Builder> consumer) {
        return CURIOS.register(name, () -> {
            BaseCurioItem.Builder builder = BaseCurioItem.builder(name);
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static DeferredItem<BaseCurioItem> registerCurio(String name, ModRarity rarity) {
        return CURIOS.register(name, () -> {
            Item.Properties properties = new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity);
            if (rarity != WHITE && rarity != GRAY) properties.fireResistant();
            return new BaseCurioItem(properties);
        });
    }

    public static DeferredItem<BaseCurioItem> registerCurio(String name, Supplier<BaseCurioItem> supplier) {
        return CURIOS.register(name, supplier);
    }

    public static DeferredItem<BaseCurioItem> registerDirectly(String name, Function<String, BaseCurioItem> function) {
        return CURIOS.register(name, () -> function.apply(name));
    }

    public static DeferredItem<BaseCurioItem> registerDirectly(String name, BiFunction<String, BaseCurioItem.Builder, BaseCurioItem> function) {
        return CURIOS.register(name, () -> function.apply(name, BaseCurioItem.builder(name)));
    }

    public static void register(IEventBus eventBus) {
        CURIOS.register(eventBus);
        OTHERS.register(eventBus);
    }
}
