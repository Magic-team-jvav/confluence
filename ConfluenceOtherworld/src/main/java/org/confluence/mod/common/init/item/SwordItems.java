package org.confluence.mod.common.init.item;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.sword.*;
import org.confluence.mod.common.item.sword.SwordDefinition.Projectile;
import org.confluence.mod.common.summoner.projectile.Zenith;
import org.confluence.mod.util.generation.variant.AboveFallenGeneration;
import org.confluence.mod.util.generation.variant.ForwardGeneration;
import org.confluence.mod.util.track.variant.SimpleTrack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

/// 允许空挥的剑都属于特殊横扫剑。
/// 允许自动挥舞的剑都属于特殊横扫剑，但特殊横扫不一定代表自动挥舞。
/// 自动挥舞由 {@link ModTags.Items#AUTO_ATTACK_WHITELIST} 判断。
/// 特殊横扫由 {@link SwordDefinition.Builder#specialSweep(float)} 配置。
public class SwordItems {
    public static void init() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 铂金以上剑参考数值为 泰拉wiki中的伤害÷2后 + 2为基础值
    // 普通短剑
    public static final DeferredItem<BaseSwordItem> COPPER_SHORT_SWORD = registerWithEnglishName("copper_short_sword", "铜短剑", "Copper Shortsword", ModTiers.COPPER, 2, 3, ModRarity.WHITE, () -> SwordDefinition.builder()
                    .withoutSweep());
    public static final DeferredItem<BaseSwordItem> TIN_SHORT_SWORD = registerWithEnglishName("tin_short_sword", "锡短剑", "Tin Shortsword", ModTiers.TIN, 2, 3, () -> SwordDefinition.builder().withoutSweep());
    public static final DeferredItem<BaseSwordItem> IRON_SHORT_SWORD = registerWithEnglishName("iron_short_sword", "铁短剑", "Iron Shortsword", ModTiers.IRON, 4, 3, () -> SwordDefinition.builder().withoutSweep());
    public static final DeferredItem<BaseSwordItem> LEAD_SHORT_SWORD = registerWithEnglishName("lead_short_sword", "铅短剑", "Lead Shortsword", ModTiers.LEAD, 4, 3, () -> SwordDefinition.builder().withoutSweep());
    public static final DeferredItem<BaseSwordItem> SILVER_SHORT_SWORD = registerWithEnglishName("silver_short_sword", "银短剑", "Silver Shortsword", ModTiers.SILVER, 4, 3, () -> SwordDefinition.builder().withoutSweep());
    public static final DeferredItem<BaseSwordItem> TUNGSTEN_SHORT_SWORD = registerWithEnglishName("tungsten_short_sword", "钨短剑", "Tungsten Shortsword", ModTiers.TUNGSTEN, 5, 3, () -> SwordDefinition.builder().withoutSweep());
    public static final DeferredItem<BaseSwordItem> GOLDEN_SHORT_SWORD = register("golden_short_sword", "金短剑", ModTiers.GOLD, 6, 3, () -> SwordDefinition.builder().withoutSweep());
    public static final DeferredItem<BaseSwordItem> PLATINUM_SHORT_SWORD = registerWithEnglishName("platinum_short_sword", "铂金短剑", "Platinum Shortsword", ModTiers.PLATINUM, 7, 3, () -> SwordDefinition.builder().withoutSweep());
    public static final DeferredItem<BaseSwordItem> BREATHING_REED = register("breathing_reed", "芦苇呼吸管", ModTiers.UNBREAKABLE, 2, 1.6F, ModRarity.BLUE, () -> SwordDefinition.builder()
                    .withoutSweep());
    public static final DeferredItem<BaseSwordItem> GLADIUS = register("gladius", "罗马短剑", ModTiers.UNBREAKABLE, 6, 3, () -> SwordDefinition.builder().withoutSweep());
    /// 大马林鱼刺剑：短剑定位的蓄力刺剑，1 段基础伤害 + 最多 8 段蓄力，满段后拆成多段命中。
    public static final DeferredItem<BaseSwordItem> MARLIN_SPIKE_SWORD = register("marlin_spike_sword", "大马林鱼刺剑", () -> new MarlinSpikeSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 14, 2.2F,
            SwordDefinition.builder().withoutSweep().tooltip().tooltip()));
    public static final DeferredItem<BaseSwordItem> UMBRELLA = register("umbrella", "伞", () -> new UmbrellaSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, SwordDefinition.builder()
                    .withoutSweep()
                    .unbreakable()));
    public static final DeferredItem<BaseSwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella", "悲剧雨伞", () -> new UmbrellaSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, SwordDefinition.builder()
                    .withoutSweep()
                    .unbreakable()));

    // 普通宽剑 默认横扫*1.5
    public static final DeferredItem<BaseSwordItem> CACTUS_SWORD = register("cactus_sword", "仙人掌剑", ModTiers.CACTUS, 5, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> EBONWOOD_SWORD = register("ebonwood_sword", "乌木剑", ModTiers.CACTUS, 6, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> SHADEWOOD_SWORD = register("shadewood_sword", "暗影木剑", ModTiers.CACTUS, 6, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> ASH_WOOD_SWORD = register("ash_wood_sword", "灰烬木剑", ModTiers.CACTUS, 7, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> PEARLWOOD_SWORD = register("pearlwood_sword", "珍珠木剑", ModTiers.CACTUS, 8, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> COPPER_BROADSWORD = register("copper_broadsword", "铜阔剑", ModTiers.COPPER, 5, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> TIN_BROADSWORD = register("tin_broadsword", "锡宽剑", ModTiers.TIN, 5, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> LEAD_BROADSWORD = register("lead_broadsword", "铅宽剑", ModTiers.LEAD, 6, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> SILVER_BROADSWORD = register("silver_broadsword", "银阔剑", ModTiers.SILVER, 6, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> TUNGSTEN_BROADSWORD = register("tungsten_broadsword", "钨宽剑", ModTiers.TUNGSTEN, 6, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> GOLDEN_BROADSWORD = register("golden_broadsword", "金阔剑", ModTiers.GOLD, 7, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> PLATINUM_BROADSWORD = register("platinum_broadsword", "铂金宽剑", ModTiers.PLATINUM, 8, 1.6F, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> MURAMASA = register("muramasa", "村正", ModTiers.UNBREAKABLE, 15, 3, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 1.5F, AttributeModifier.Operation.ADD_VALUE)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 0.2F, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> COBALT_SWORD = register("cobalt_sword", "钴剑", ModTiers.UNBREAKABLE, 25, 2.4F, ModRarity.LIGHT_RED, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> PALLADIUM_SWORD = register("palladium_sword", "钯金剑", ModTiers.UNBREAKABLE, 29, 2.6F, ModRarity.LIGHT_RED, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> MYTHRIL_SWORD = register("mythril_sword", "秘银剑", ModTiers.UNBREAKABLE, 30, 2.6F, ModRarity.LIGHT_RED, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> ORICHALCUM_SWORD = register("orichalcum_sword", "山铜剑", ModTiers.UNBREAKABLE, 34, 2.4F, ModRarity.LIGHT_RED, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> ADAMANTITE_SWORD = register("adamantite_sword", "精金剑", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> TITANIUM_SWORD = register("titanium_sword", "钛金剑", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());

    public static final DeferredItem<BaseSwordItem> FAKE_SWORD = register("fake_sword", "假剑", ModTiers.CANDY_CANE, 3, 1.6F, ModRarity.GRAY, SwordDefinition::builder);
    public static final DeferredItem<BaseSwordItem> CANDY_CANE_SWORD = register("candy_cane_sword", "糖棒剑", ModTiers.CANDY_CANE, 5, 1.8F, () -> SwordDefinition.builder()
                    .specialSweep(0.5F)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> FALCON_BLADE = register("falcon_blade", "猎鹰刃", () -> new MomentumSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 6, 1.8F,
                    SwordDefinition.builder().specialSweep(0.5F)));
    public static final DeferredItem<BaseSwordItem> ZOMBIE_ARM = register("zombie_arm", "僵尸臂", ModTiers.UNBREAKABLE, 5, 2.4F, () -> SwordDefinition.builder().specialSweep(0.5F));
    public static final DeferredItem<BaseSwordItem> MANDIBLE_BLADE = register("mandible_blade", "颌骨剑", ModTiers.UNBREAKABLE, 6, 2.4F, () -> SwordDefinition.builder().specialSweep(0.8F));
    public static final DeferredItem<BaseSwordItem> BONE_SWORD = register("bone_sword", "骨剑", ModTiers.UNBREAKABLE, 7, 2.4F, ModRarity.ORANGE, () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .tooltipImage());
    public static final DeferredItem<BaseSwordItem> STYLISH_SCISSORS = register("stylish_scissors", "时尚剪刀", ModTiers.UNBREAKABLE, 5, 2.2F, ModRarity.GREEN, () -> SwordDefinition.builder().specialSweep(0.8F));
    public static final DeferredItem<BaseSwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar", "异域弯刀", () -> new MomentumSwordItem(ModTiers.UNBREAKABLE, ModRarity.GREEN, 7, 2.3F,
                    SwordDefinition.builder().specialSweep(0.8F)));
    public static final DeferredItem<BaseSwordItem> KATANA = register("katana", "武士刀", ModTiers.UNBREAKABLE, 6, 3.7F, ModRarity.BLUE, () -> SwordDefinition.builder().specialSweep(0.8F));

    // 改横扫大小的宽剑(由 ENTITY_INTERACTION_RANGE 属性控制)
    public static final DeferredItem<BaseSwordItem> TERRAGRIM = register("terragrim", "泰拉魔刃", ModTiers.UNBREAKABLE, 7, 7, ModRarity.ORANGE, () -> SwordDefinition.builder()
                    .specialSweep(0.0F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, -1.4F, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredItem<BaseSwordItem> BREAKER_BLADE = register("breaker_blade", "毁灭刃", BreakerBladeItem::new);

    // 效果剑
    public static final DeferredItem<BaseSwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish", "紫挥棒鱼", () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.WHITE, 15, 0.5F, SwordDefinition.builder()
                    .tooltipImage()
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 2, AttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F), LibEffects.CONFUSED, 40, 1, 0.5F));
    public static final DeferredItem<BaseSwordItem> LIGHTS_BANE = registerWithEnglishName("lights_bane", "魔光剑", "Light's Bane", LightsBaneItem::new);
    public static final DeferredItem<BaseSwordItem> BLOOD_BUTCHERER = register("blood_butcherer", "血腥屠刀", () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 14, 1.3F, SwordDefinition.builder()
                    .tooltipImage()
                    .specialSweep(0.8F), ModEffects.BLOOD_BUTCHERED, 180, 4, 1.0F));
    public static final DeferredItem<BaseSwordItem> VOLCANO = register("volcano", "火山", VolcanoItem::new);
    public static final DeferredItem<BaseSwordItem> BAT_BAT = register("bat_bat", "蝙蝠棍", BatBatItem::new);
    public static final DeferredItem<BaseSwordItem> TENTACLE_MACE = register("tentacle_mace", "触手钉锤", () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.GREEN, 13, 2.0F,
                    SwordDefinition.builder().specialSweep(0.8F), ModEffects.TENTACLE_SPIKES, 180, 4, 1.0F));
    public static final DeferredItem<BaseSwordItem> BEE_KEEPER = register("bee_keeper", "养蜂人", BeeKeeperItem::new);

    // 弹幕剑
    public static final DeferredItem<BaseSwordItem> ICE_BLADE = register("ice_blade", "冰雪刃", ModTiers.UNBREAKABLE, 10, 2.0F, ModRarity.BLUE, () -> SwordDefinition.builder()
                    .projectile(new Projectile(1.0F, 0.6F, 0.9F, 40, 0.0F, 15,
                            ModSoundEvents.FROZEN_ARROW.getId(), ModEntities.ICE_BLADE_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Model(Confluence.asResource("ice_blade_sword_projectile"),
                                    Confluence.asResource("textures/entity/ice_blade_sword_projectile.png"), 1.0F, 0.0F, 0.0F, 0.0F,
                                    SwordProjectileAppearance.Lifecycle.GROW, SwordProjectileAppearance.Material.CUTOUT),
                            List.of(SwordProjectileParticleEffect.emitter(SwordProjectileParticleEffect.Event.TRAIL, Confluence.asResource("ball_of_frost_trail")))))
                    .tooltipImage()
                    .specialSweep(0.8F));
    public static final DeferredItem<BaseSwordItem> STARFURY = register("starfury", "星怒", ModTiers.UNBREAKABLE, 14, 2.0F, ModRarity.GREEN, () -> SwordDefinition.builder()
                    .projectile(new Projectile(1.5F, 2.0F, 0.9F, 100, 0.0F, 15,
                            ModSoundEvents.STAR.getId(), ModEntities.STAR_FURY.getId(), Optional.empty(),
                            new AboveFallenGeneration(30.0F, 30.0F, 10.0F, 1.0F, 20.0F, 5.0F),
                            new SwordProjectileAppearance.Cross(Confluence.asResource("textures/entity/star_fury_projectile.png"),
                                    0xFFFF9696, 2.0F, 18.0F, 10)))
                    .specialSweep(0.8F));
    public static final DeferredItem<BaseSwordItem> ENCHANTED_SWORD = register("enchanted_sword", "附魔剑", ModTiers.UNBREAKABLE, 9, 2.0F, ModRarity.ORANGE, () -> SwordDefinition.builder()
                    .projectile(new Projectile(1.0F, 0.8F, 0.9F, 40, 0.0F, 10,
                            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Model(Confluence.asResource("enchanted_sword_projectile"),
                                    Confluence.asResource("textures/entity/enchanted_sword_projectile.png"), 1.0F, 0.2F, 0.0F, 0.89F,
                                    SwordProjectileAppearance.Lifecycle.GROW, SwordProjectileAppearance.Material.CUTOUT),
                            List.of(SwordProjectileParticleEffect.emitter(SwordProjectileParticleEffect.Event.TRAIL, Confluence.asResource("falling_star")))))
                    .specialSweep(0.8F));
    public static final DeferredItem<BaseSwordItem> BLADE_OF_GRASS = registerWithEnglishName("blade_of_grass", "草剑", "Blade of Grass", BladeOfGrassItem::new);
    public static final DeferredItem<BaseSwordItem> NIGHTS_EDGE = registerWithEnglishName("nights_edge", "永夜刃", "Night's Edge", ModTiers.UNBREAKABLE, 25, 2.5F, ModRarity.GREEN, () -> SwordDefinition.builder()
                    .projectile(new Projectile(1.0F, 0.8F, 0.9F, 20, 0.0F, 10,
                            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.NIGHTS_EDGE.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 20.0F),
                            new SwordProjectileAppearance.Item(0.8F, SwordProjectileAppearance.Transform.OWNER_SWING,
                                    Optional.of(Confluence.asResource("nights_edge")))))
                    .tooltipImage()
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F));
    public static final DeferredItem<BaseSwordItem> WAFFLES_IRON = registerWithEnglishName("waffles_iron", "华夫饼烘烤模", "Waffle's Iron", () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.PINK, 27, 2.5F,
                    SwordDefinition.builder().projectile(new Projectile(1.0F, 0.8F, 1.0F, 60, 0.06F, 10,
                            ModSoundEvents.ITEM_WAFFLE_IRON.getId(), ModEntities.ICE_BLADE_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Cross(Confluence.asResource("textures/entity/waffle.png"),
                                    0xFFFFFFFF, 1.0F, 0.0F, 0))).tooltipImage(),
                    ModEffects.HELLFIRE, 100, 0, 1.0F / 3.0F));

    // 陨石光剑
    public static final DeferredItem<BaseSwordItem> RED_PHASEBLADE = register("red_phaseblade", "红陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.RED));
    public static final DeferredItem<BaseSwordItem> ORANGE_PHASEBLADE = register("orange_phaseblade", "橙陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.ORANGE));
    public static final DeferredItem<BaseSwordItem> YELLOW_PHASEBLADE = register("yellow_phaseblade", "黄陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.YELLOW));
    public static final DeferredItem<BaseSwordItem> GREEN_PHASEBLADE = register("green_phaseblade", "绿陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.GREEN));
    public static final DeferredItem<BaseSwordItem> BLUE_PHASEBLADE = register("blue_phaseblade", "蓝陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.BLUE));
    public static final DeferredItem<BaseSwordItem> PURPLE_PHASEBLADE = register("purple_phaseblade", "紫陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.PURPLE));
    public static final DeferredItem<BaseSwordItem> WHITE_PHASEBLADE = register("white_phaseblade", "白陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.WHITE));
    public static final DeferredItem<BaseSwordItem> PINK_PHASEBLADE = register("pink_phaseblade", "粉陨石光剑", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.PINK));

    // 晶光刃
    public static final DeferredItem<BaseSwordItem> RED_PHASESABER = register("red_phasesaber", "红晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.RED));
    public static final DeferredItem<BaseSwordItem> ORANGE_PHASESABER = register("orange_phasesaber", "橙晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.ORANGE));
    public static final DeferredItem<BaseSwordItem> YELLOW_PHASESABER = register("yellow_phasesaber", "黄晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.YELLOW));
    public static final DeferredItem<BaseSwordItem> GREEN_PHASESABER = register("green_phasesaber", "绿晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.GREEN));
    public static final DeferredItem<BaseSwordItem> BLUE_PHASESABER = register("blue_phasesaber", "蓝晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.BLUE));
    public static final DeferredItem<BaseSwordItem> PURPLE_PHASESABER = register("purple_phasesaber", "紫晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.PURPLE));
    public static final DeferredItem<BaseSwordItem> WHITE_PHASESABER = register("white_phasesaber", "白晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.WHITE));
    public static final DeferredItem<BaseSwordItem> PINK_PHASESABER = register("pink_phasesaber", "粉晶光刃", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.PINK));

    public static final DeferredItem<ZenithItem> ZENITH = registerItem("zenith", "天顶剑", () -> new ZenithItem(ModTiers.UNBREAKABLE, ModRarity.RED, 97, 1.6F));
    public static final DeferredItem<ZenithItem> TRUE_COPPER_SHORTSWORD = registerItem("true_copper_shortsword", "真铜短剑", () -> new ZenithItem(ModTiers.UNBREAKABLE, ModRarity.RED, 97, 1.6F) {
        @Override
        public Zenith createZenith(Player owner) {
            Zenith zenith = super.createZenith(owner);
            zenith.renderType = Zenith.RenderType.COPPER_SHORT_SWORD;
            return zenith;
        }
    });

    // 特殊剑
    public static final DeferredItem<BaseSwordItem> CROWBAR = register("crowbar", "撬棍", ModTiers.UNBREAKABLE, 18, 3, ModRarity.MASTER, () -> SwordDefinition.builder()
                    .specialSweep(1.0F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, -1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseSwordItem> DEVELOPER_SWORD = register("developer_sword", "开发者剑", ModTiers.UNBREAKABLE, 9999, 9999, ModRarity.MASTER, () -> SwordDefinition.builder()
                    .specialSweep(1.0F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 7, AttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage()
                    .projectile(new Projectile(1.0F, 0.3F, 1.0F, 50, 0.0F, 20,
                            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD.getId(),
                            Optional.of(new SimpleTrack(Mth.HALF_PI, 0.8F, 0.2F, Optional.empty(), 0.1)), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Model(Confluence.asResource("enchanted_sword_projectile"),
                                    Confluence.asResource("textures/entity/enchanted_sword_projectile.png"), 1.0F, 0.2F, 0.0F, 0.89F,
                                    SwordProjectileAppearance.Lifecycle.GROW, SwordProjectileAppearance.Material.CUTOUT))));

    // 赞助者物品
    public static final DeferredItem<BaseSwordItem> BROKEN_SWEET_SWORD = register("broken_sweet_sword", "断裂的糖果剑", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 2, 1, SwordDefinition.builder()));
    public static final DeferredItem<BaseSwordItem> SWEET_SWORD = register("sweet_sword", "糖果剑", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 6, 2, SwordDefinition.builder()
                    .properties(p -> {
                        FoodProperties properties = new FoodProperties(1, 1.0F, false, 2.0F,
                                Optional.of(BROKEN_SWEET_SWORD.toStack()),
                                List.of(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(ModEffects.DELICIOUS, 200), 1.0F)));
                        p.food(properties);
                    })));

    public static final DeferredItem<BaseSwordItem> STAR_STEEL_SWORD = register("star_steel_sword", "星钢剑", StarSteelSword::new);

    public static DeferredItem<BaseSwordItem> register(String name, Supplier<BaseSwordItem> supplier) {
        return ITEMS.register(name, supplier::get);
    }

    public static DeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, Supplier<SwordDefinition.Builder> modifierBuilder) {
        return register(name, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
    }

    public static DeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, ModRarity rarity, Supplier<SwordDefinition.Builder> modifierBuilder) {
        return register(name, () -> {
            SwordDefinition.Builder builder = modifierBuilder.get();
            if (tier == ModTiers.UNBREAKABLE) {
                builder.unbreakable();
            }
            return new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, builder);
        });
    }

    public static boolean isShortSword(DeferredHolder<Item, ? extends Item> holder) {
        return holder.getId().getPath().endsWith("_short_sword");
    }

    public static float processEffect(DamageSource damageSource, @Nullable Entity attacker, LivingEntity victim, float amount) {
        ItemStack weapon = damageSource.getWeaponItem();
        if (weapon != null && weapon.getItem() instanceof BaseSwordItem sword) {
            sword.applyHitEffects(weapon, attacker, victim, damageSource);
            amount = sword.modifyDamage(weapon, damageSource, attacker, victim, amount);
        }
        return amount;
    }

    public static void afterSuccessfulDamage(DamageSource damageSource, @Nullable Entity attacker, LivingEntity victim) {
        ItemStack weapon = damageSource.getWeaponItem();
        if (weapon != null && attacker instanceof LivingEntity living
                && damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            if (weapon.getItem() instanceof VolcanoItem volcano)
                volcano.afterSuccessfulDamage(living, victim);
        }
    }

    private static DeferredItem<BaseSwordItem> register(String registryId, String chineseName, Supplier<BaseSwordItem> supplier) {
        DeferredItem<BaseSwordItem> entry = ITEMS.register(registryId, supplier::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BaseSwordItem> register(String registryId, String chineseName, Tier tier, int rawDamage, float rawSpeed, Supplier<SwordDefinition.Builder> modifierBuilder) {
        DeferredItem<BaseSwordItem> entry = register(registryId, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BaseSwordItem> register(String registryId, String chineseName, Tier tier, int rawDamage, float rawSpeed, ModRarity rarity, Supplier<SwordDefinition.Builder> modifierBuilder) {
        DeferredItem<BaseSwordItem> entry = register(registryId, () -> {
            SwordDefinition.Builder builder = modifierBuilder.get();
            if (tier == ModTiers.UNBREAKABLE) {
                builder.unbreakable();
            }
            return new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, builder);
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BaseSwordItem> registerWithEnglishName(String registryId, String chineseName, String englishName, Supplier<BaseSwordItem> supplier) {
        DeferredItem<BaseSwordItem> entry = ITEMS.register(registryId, supplier::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static DeferredItem<BaseSwordItem> registerWithEnglishName(String registryId, String chineseName, String englishName, Tier tier, int rawDamage, float rawSpeed, Supplier<SwordDefinition.Builder> modifierBuilder) {
        DeferredItem<BaseSwordItem> entry = register(registryId, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static DeferredItem<BaseSwordItem> registerWithEnglishName(String registryId, String chineseName, String englishName, Tier tier, int rawDamage, float rawSpeed, ModRarity rarity, Supplier<SwordDefinition.Builder> modifierBuilder) {
        DeferredItem<BaseSwordItem> entry = register(registryId, () -> {
            SwordDefinition.Builder builder = modifierBuilder.get();
            if (tier == ModTiers.UNBREAKABLE) {
                builder.unbreakable();
            }
            return new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, builder);
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
