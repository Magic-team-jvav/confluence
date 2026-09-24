package org.confluence.mod.common.init.item;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileAppearance;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.component.SwordProjectileParticleEffect;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.sword.*;
import org.confluence.mod.util.generation.variant.AboveFallenGeneration;
import org.confluence.mod.util.generation.variant.ForwardGeneration;
import org.confluence.mod.util.track.variant.SimpleTrack;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.IPortFoodProperties;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/// 允许空挥的剑都属于特殊横扫剑。
/// 允许自动挥舞的剑都属于特殊横扫剑，但特殊横扫不一定代表自动挥舞。
/// 自动挥舞由 {@link ModTags.Items#AUTO_ATTACK_WHITELIST} 判断。
/// 特殊横扫由 {@link SwordDefinition.Builder#specialSweep(float)} 配置。
public class SwordItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    // 铂金以上剑参考数值为 泰拉wiki中的伤害÷2后 + 2为基础值
    // 普通短剑
    public static final PortDeferredItem<BaseSwordItem> COPPER_SHORT_SWORD = register("copper_short_sword", ModTiers.COPPER, 2, 3, ModRarity.WHITE,
            () -> SwordDefinition.builder()
                    .withoutSweep()
                    .tooltip(p -> p.withColor(0x984c11))
                    .tooltip(p -> p.withColor(0x984c11)));
    public static final PortDeferredItem<BaseSwordItem> TIN_SHORT_SWORD = register("tin_short_sword", ModTiers.TIN, 2, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> IRON_SHORT_SWORD = register("iron_short_sword", ModTiers.IRON, 4, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> LEAD_SHORT_SWORD = register("lead_short_sword", ModTiers.LEAD, 4, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> SILVER_SHORT_SWORD = register("silver_short_sword", ModTiers.SILVER, 4, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> TUNGSTEN_SHORT_SWORD = register("tungsten_short_sword", ModTiers.TUNGSTEN, 5, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> GOLDEN_SHORT_SWORD = register("golden_short_sword", ModTiers.GOLD, 6, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> PLATINUM_SHORT_SWORD = register("platinum_short_sword", ModTiers.PLATINUM, 7, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> BREATHING_REED = register("breathing_reed", ModTiers.UNBREAKABLE, 2, 1.6F, ModRarity.BLUE,
            () -> SwordDefinition.builder()
                    .withoutSweep()
                    .tooltip(p -> p.withColor(11184810)));
    public static final PortDeferredItem<BaseSwordItem> GLADIUS = register("gladius", ModTiers.UNBREAKABLE, 6, 3,
            () -> SwordDefinition.builder().withoutSweep());
    public static final PortDeferredItem<BaseSwordItem> UMBRELLA = register("umbrella",
            () -> new UmbrellaSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, SwordDefinition.builder()
                    .withoutSweep()
                    .tooltip(p -> p.withColor(11184810))
                    .unbreakable()));
    public static final PortDeferredItem<BaseSwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella",
            () -> new UmbrellaSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, SwordDefinition.builder()
                    .withoutSweep()
                    .tooltip(p -> p.withColor(11184810))
                    .unbreakable()));

    // 普通宽剑 默认横扫*1.5
    public static final PortDeferredItem<BaseSwordItem> CACTUS_SWORD = register("cactus_sword", ModTiers.CACTUS, 5, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> EBONWOOD_SWORD = register("ebonwood_sword", ModTiers.CACTUS, 6, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> SHADEWOOD_SWORD = register("shadewood_sword", ModTiers.CACTUS, 6, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> ASH_WOOD_SWORD = register("ash_wood_sword", ModTiers.CACTUS, 7, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> PEARLWOOD_SWORD = register("pearlwood_sword", ModTiers.CACTUS, 8, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> COPPER_BROADSWORD = register("copper_broadsword", ModTiers.COPPER, 5, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> TIN_BROADSWORD = register("tin_broadsword", ModTiers.TIN, 5, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> LEAD_BROADSWORD = register("lead_broadsword", ModTiers.LEAD, 6, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> SILVER_BROADSWORD = register("silver_broadsword", ModTiers.SILVER, 6, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> TUNGSTEN_BROADSWORD = register("tungsten_broadsword", ModTiers.TUNGSTEN, 6, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> GOLDEN_BROADSWORD = register("golden_broadsword", ModTiers.GOLD, 7, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> PLATINUM_BROADSWORD = register("platinum_broadsword", ModTiers.PLATINUM, 8, 1.6F, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> MURAMASA = register("muramasa", ModTiers.UNBREAKABLE, 15, 3,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 1.5F, PortAttributeModifier.Operation.ADD_VALUE)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 0.2F, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> COBALT_SWORD = register("cobalt_sword", ModTiers.UNBREAKABLE, 25, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> PALLADIUM_SWORD = register("palladium_sword", ModTiers.UNBREAKABLE, 29, 2.6F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> MYTHRIL_SWORD = register("mythril_sword", ModTiers.UNBREAKABLE, 30, 2.6F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> ORICHALCUM_SWORD = register("orichalcum_sword", ModTiers.UNBREAKABLE, 34, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> ADAMANTITE_SWORD = register("adamantite_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> TITANIUM_SWORD = register("titanium_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());

    public static final PortDeferredItem<BaseSwordItem> FAKE_SWORD = register("fake_sword", ModTiers.CANDY_CANE, 3, 1.6F, ModRarity.GRAY, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> CANDY_CANE_SWORD = register("candy_cane_sword", ModTiers.CANDY_CANE, 5, 1.8F,
            () -> SwordDefinition.builder()
                    .specialSweep(0.5F)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> FALCON_BLADE = register("falcon_blade",
            () -> new MomentumSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 6, 1.8F,
                    SwordDefinition.builder().specialSweep(0.5F)));
    public static final PortDeferredItem<BaseSwordItem> ZOMBIE_ARM = register("zombie_arm", ModTiers.UNBREAKABLE, 5, 2.4F,
            () -> SwordDefinition.builder().specialSweep(0.5F));
    public static final PortDeferredItem<BaseSwordItem> MANDIBLE_BLADE = register("mandible_blade", ModTiers.UNBREAKABLE, 6, 2.4F,
            () -> SwordDefinition.builder().specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> BONE_SWORD = register("bone_sword", ModTiers.UNBREAKABLE, 7, 2.4F, ModRarity.ORANGE,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> STYLISH_SCISSORS = register("stylish_scissors", ModTiers.UNBREAKABLE, 5, 2.2F, ModRarity.GREEN,
            () -> SwordDefinition.builder().specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar",
            () -> new MomentumSwordItem(ModTiers.UNBREAKABLE, ModRarity.GREEN, 7, 2.3F,
                    SwordDefinition.builder().specialSweep(0.8F)));
    public static final PortDeferredItem<BaseSwordItem> KATANA = register("katana", ModTiers.UNBREAKABLE, 6, 3.7F, ModRarity.BLUE,
            () -> SwordDefinition.builder().specialSweep(0.8F));

    // 改横扫大小的宽剑(由 ENTITY_INTERACTION_RANGE 属性控制)
    public static final PortDeferredItem<BaseSwordItem> TERRAGRIM = register("terragrim", ModTiers.UNBREAKABLE, 7, 7, ModRarity.ORANGE,
            () -> SwordDefinition.builder()
                    .specialSweep(0.0F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, -1.4F, PortAttributeModifier.Operation.ADD_VALUE));

    public static final PortDeferredItem<BaseSwordItem> BREAKER_BLADE = register("breaker_blade", BreakerBladeItem::new);

    // 效果剑
    public static final PortDeferredItem<BaseSwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish",
            () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.WHITE, 15, 0.5F, SwordDefinition.builder()
                    .tooltipImage()
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 2, PortAttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F), LibEffects.CONFUSED, 40, 1, 0.5F));
    public static final PortDeferredItem<BaseSwordItem> LIGHTS_BANE = register("lights_bane", LightsBaneItem::new);
    public static final PortDeferredItem<BaseSwordItem> BLOOD_BUTCHERER = register("blood_butcherer",
            () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 14, 1.3F, SwordDefinition.builder()
                    .tooltipImage()
                    .specialSweep(0.8F), ModEffects.BLOOD_BUTCHERED, 180, 4, 1.0F));
    public static final PortDeferredItem<BaseSwordItem> VOLCANO = register("volcano", VolcanoItem::new);
    public static final PortDeferredItem<BaseSwordItem> BAT_BAT = register("bat_bat", BatBatItem::new);
    public static final PortDeferredItem<BaseSwordItem> TENTACLE_MACE = register("tentacle_mace",
            () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.GREEN, 13, 2.0F,
                    SwordDefinition.builder().specialSweep(0.8F), ModEffects.TENTACLE_SPIKES, 180, 4, 1.0F));
    public static final PortDeferredItem<BaseSwordItem> BEE_KEEPER = register("bee_keeper", BeeKeeperItem::new);

    // 弹幕剑
    public static final PortDeferredItem<BaseSwordItem> ICE_BLADE = register("ice_blade", ModTiers.UNBREAKABLE, 10, 2.0F, ModRarity.BLUE,
            () -> SwordDefinition.builder()
                    .projectile(new SwordProjectileComponent(1.0F, 0.6F, 0.9F, 40, 0.0F, 15,
                            ModSoundEvents.FROZEN_ARROW.getId(), ModEntities.ICE_BLADE_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Model(Confluence.asResource("ice_blade_sword_projectile"),
                                    Confluence.asResource("textures/entity/ice_blade_sword_projectile.png"), 1.0F, 0.0F, 0.0F, 0.0F,
                                    SwordProjectileAppearance.Lifecycle.GROW, SwordProjectileAppearance.Material.CUTOUT),
                            List.of(SwordProjectileParticleEffect.emitter(Confluence.asResource("ball_of_frost_trail")))))
                    .tooltipImage()
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> STARFURY = register("starfury", ModTiers.UNBREAKABLE, 14, 2.0F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .projectile(new SwordProjectileComponent(1.5F, 2.0F, 0.9F, 100, 0.0F, 15,
                            ModSoundEvents.STAR.getId(), ModEntities.STAR_FURY.getId(), Optional.empty(),
                            new AboveFallenGeneration(30.0F, 30.0F, 10.0F, 1.0F, 20.0F, 5.0F),
                            new SwordProjectileAppearance.Cross(Confluence.asResource("textures/entity/star_fury_projectile.png"),
                                    0xFFFF9696, 2.0F, 18.0F, 10)))
                    .tooltip(p -> p.withColor(0xe44189))
                    .tooltip(p -> p.withColor(0xe44189))
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> ENCHANTED_SWORD = register("enchanted_sword", ModTiers.UNBREAKABLE, 9, 2.0F, ModRarity.ORANGE,
            () -> SwordDefinition.builder()
                    .projectile(new SwordProjectileComponent(1.0F, 0.8F, 0.9F, 40, 0.0F, 10,
                            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Model(Confluence.asResource("enchanted_sword_projectile"),
                                    Confluence.asResource("textures/entity/enchanted_sword_projectile.png"), 1.0F, 0.2F, 0.0F, 0.89F,
                                    SwordProjectileAppearance.Lifecycle.GROW, SwordProjectileAppearance.Material.CUTOUT),
                            List.of(SwordProjectileParticleEffect.emitter(Confluence.asResource("falling_star")))))
                    .tooltip(p -> p.withColor(0x4156e4))
                    .tooltip(p -> p.withColor(0x4156e4))
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> BLADE_OF_GRASS = register("blade_of_grass", BladeOfGrassItem::new);
    public static final PortDeferredItem<BaseSwordItem> NIGHTS_EDGE = register("nights_edge", ModTiers.UNBREAKABLE, 25, 2.5F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .projectile(new SwordProjectileComponent(1.0F, 0.8F, 0.9F, 20, 0.0F, 10,
                            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.NIGHTS_EDGE.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 20.0F),
                            new SwordProjectileAppearance.Item(0.8F, SwordProjectileAppearance.Transform.OWNER_SWING,
                                    Optional.of(Confluence.asResource("nights_edge")))))
                    .tooltipImage()
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> WAFFLES_IRON = register("waffles_iron",
            () -> new EffectSwordItem(ModTiers.UNBREAKABLE, ModRarity.PINK, 27, 2.5F,
                    SwordDefinition.builder().projectile(new SwordProjectileComponent(1.0F, 0.8F, 1.0F, 60, 0.06F, 10,
                            ModSoundEvents.ITEM_WAFFLE_IRON.getId(), ModEntities.ICE_BLADE_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Cross(Confluence.asResource("textures/entity/waffle.png"),
                                    0xFFFFFFFF, 1.0F, 0.0F, 0))).tooltipImage(),
                    ModEffects.HELLFIRE, 100, 0, 1.0F / 3.0F));

    // 陨石光剑
    public static final PortDeferredItem<BaseSwordItem> RED_PHASEBLADE = register("red_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.RED));
    public static final PortDeferredItem<BaseSwordItem> ORANGE_PHASEBLADE = register("orange_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.ORANGE));
    public static final PortDeferredItem<BaseSwordItem> YELLOW_PHASEBLADE = register("yellow_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.YELLOW));
    public static final PortDeferredItem<BaseSwordItem> GREEN_PHASEBLADE = register("green_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.GREEN));
    public static final PortDeferredItem<BaseSwordItem> BLUE_PHASEBLADE = register("blue_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.BLUE));
    public static final PortDeferredItem<BaseSwordItem> PURPLE_PHASEBLADE = register("purple_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.PURPLE));
    public static final PortDeferredItem<BaseSwordItem> WHITE_PHASEBLADE = register("white_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.WHITE));
    public static final PortDeferredItem<BaseSwordItem> PINK_PHASEBLADE = register("pink_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, BasePhasebladeItem.PhaseColor.PINK));

    // 晶光刃
    public static final PortDeferredItem<BaseSwordItem> RED_PHASESABER = register("red_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.RED));
    public static final PortDeferredItem<BaseSwordItem> ORANGE_PHASESABER = register("orange_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.ORANGE));
    public static final PortDeferredItem<BaseSwordItem> YELLOW_PHASESABER = register("yellow_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.YELLOW));
    public static final PortDeferredItem<BaseSwordItem> GREEN_PHASESABER = register("green_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.GREEN));
    public static final PortDeferredItem<BaseSwordItem> BLUE_PHASESABER = register("blue_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.BLUE));
    public static final PortDeferredItem<BaseSwordItem> PURPLE_PHASESABER = register("purple_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.PURPLE));
    public static final PortDeferredItem<BaseSwordItem> WHITE_PHASESABER = register("white_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.WHITE));
    public static final PortDeferredItem<BaseSwordItem> PINK_PHASESABER = register("pink_phasesaber", () -> new Phasesaber(ModTiers.METEOR, ModRarity.LIGHT_RED, 27, 3, BasePhasebladeItem.PhaseColor.PINK));

    // 特殊剑
    public static final PortDeferredItem<BaseSwordItem> CROWBAR = register("crowbar", ModTiers.UNBREAKABLE, 18, 3, ModRarity.MASTER,
            () -> SwordDefinition.builder()
                    .specialSweep(1.0F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, -1, PortAttributeModifier.Operation.ADD_VALUE));
    public static final PortDeferredItem<BaseSwordItem> DEVELOPER_SWORD = register("developer_sword", ModTiers.UNBREAKABLE, 9999, 9999, ModRarity.MASTER,
            () -> SwordDefinition.builder()
                    .specialSweep(1.0F)
                    .attribute(Attributes.ENTITY_INTERACTION_RANGE, 7, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage()
                    .projectile(new SwordProjectileComponent(1.0F, 0.3F, 1.0F, 50, 0.0F, 20,
                            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD.getId(),
                            Optional.of(new SimpleTrack(Mth.HALF_PI, 0.8F, 0.2F, Optional.empty(), 0.1)), ForwardGeneration.of(0.0F, 0.0F),
                            new SwordProjectileAppearance.Model(Confluence.asResource("enchanted_sword_projectile"),
                                    Confluence.asResource("textures/entity/enchanted_sword_projectile.png"), 1.0F, 0.2F, 0.0F, 0.89F,
                                    SwordProjectileAppearance.Lifecycle.GROW, SwordProjectileAppearance.Material.CUTOUT))));

    // 赞助者物品
    public static final PortDeferredItem<BaseSwordItem> BROKEN_SWEET_SWORD = register("broken_sweet_sword",
            () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 2, 1, SwordDefinition.builder()));
    public static final PortDeferredItem<BaseSwordItem> SWEET_SWORD = register("sweet_sword",
            () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 6, 2, SwordDefinition.builder()
                    .tooltip(p -> p.withColor(0xe44189))
                    .properties(p -> {
                        FoodProperties properties = new FoodProperties.Builder().nutrition(1).saturationMod(1)
                                .effect(() -> new MobEffectInstance(ModEffects.DELICIOUS.get(), 200), 1.0F)
                                .build();
                        IPortFoodProperties i = IPortFoodProperties.of(properties);
                        i.portlib$setEatSeconds(2);
                        i.portlib$setUsingConvertsTo(BROKEN_SWEET_SWORD::toStack);
                        p.food(properties);
                    })));

    public static final PortDeferredItem<BaseSwordItem> STAR_STEEL_SWORD = register("star_steel_sword", StarSteelSword::new);

    public static PortDeferredItem<BaseSwordItem> register(String name, Supplier<BaseSwordItem> supplier) {
        return ITEMS.register(name, supplier::get);
    }

    public static PortDeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, Supplier<SwordDefinition.Builder> modifierBuilder) {
        return register(name, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
    }

    public static PortDeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, ModRarity rarity, Supplier<SwordDefinition.Builder> modifierBuilder) {
        return register(name, () -> {
            SwordDefinition.Builder builder = modifierBuilder.get();
            if (tier == ModTiers.UNBREAKABLE) {
                builder.unbreakable();
            }
            return new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, builder);
        });
    }

    public static boolean isShortSword(PortRegistryEntry<Item, ? extends Item> holder) {
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
                && damageSource.is(org.mesdag.portlib.wrapper.common.PortTags.DamageTypes.IS_PLAYER_ATTACK)) {
            if (weapon.getItem() instanceof VolcanoItem volcano)
                volcano.afterSuccessfulDamage(living, victim);
        }
    }
}
