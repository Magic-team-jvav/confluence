package org.confluence.mod.common.init.item;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.sword.*;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.IPortFoodProperties;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

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
            () -> new GeoSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, SwordDefinition.builder()
                    .withoutSweep()
                    .behavior(SwordBehaviors.UMBRELLA)
                    .tooltip(p -> p.withColor(11184810))
                    .unbreakable()));
    public static final PortDeferredItem<BaseSwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella",
            () -> new GeoSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, SwordDefinition.builder()
                    .withoutSweep()
                    .behavior(SwordBehaviors.UMBRELLA)
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
                    .attribute(PortAttributesExtension.entityInteractionRange(), 1.5F, PortAttributeModifier.Operation.ADD_VALUE)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 0.2F, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> COBALT_SWORD = register("cobalt_sword", ModTiers.UNBREAKABLE, 25, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> PALLADIUM_SWORD = register("palladium_sword", ModTiers.UNBREAKABLE, 29, 2.6F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> MYTHRIL_SWORD = register("mythril_sword", ModTiers.UNBREAKABLE, 30, 2.6F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> ORICHALCUM_SWORD = register("orichalcum_sword", ModTiers.UNBREAKABLE, 34, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> ADAMANTITE_SWORD = register("adamantite_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> TITANIUM_SWORD = register("titanium_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage());

    public static final PortDeferredItem<BaseSwordItem> FAKE_SWORD = register("fake_sword", ModTiers.CANDY_CANE, 3, 1.6F, ModRarity.GRAY, SwordDefinition::builder);
    public static final PortDeferredItem<BaseSwordItem> CANDY_CANE_SWORD = register("candy_cane_sword", ModTiers.CANDY_CANE, 5, 1.8F,
            () -> SwordDefinition.builder()
                    .specialSweep(0.5F)
                    .tooltipImage());
    public static final PortDeferredItem<BaseSwordItem> FALCON_BLADE = register("falcon_blade", ModTiers.UNBREAKABLE, 6, 1.8F, ModRarity.BLUE,
            () -> SwordDefinition.builder()
                    .specialSweep(0.5F)
                    .behavior(SwordBehaviors.MOMENTUM));
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
    public static final PortDeferredItem<BaseSwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar", ModTiers.UNBREAKABLE, 7, 2.3F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .behavior(SwordBehaviors.MOMENTUM));
    public static final PortDeferredItem<BaseSwordItem> KATANA = register("katana", ModTiers.UNBREAKABLE, 6, 3.7F, ModRarity.BLUE,
            () -> SwordDefinition.builder().specialSweep(0.8F));

    // 改横扫大小的宽剑(由 ENTITY_INTERACTION_RANGE 属性控制)
    public static final PortDeferredItem<BaseSwordItem> TERRAGRIM = register("terragrim", ModTiers.UNBREAKABLE, 7, 7, ModRarity.ORANGE,
            () -> SwordDefinition.builder()
                    .specialSweep(0.0F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), -1.4F, PortAttributeModifier.Operation.ADD_VALUE));

    public static final PortDeferredItem<BaseSwordItem> BREAKER_BLADE = register("breaker_blade", ModTiers.UNBREAKABLE, 37, 1.0F, ModRarity.LIGHT_RED,
            () -> SwordDefinition.builder()
                    .specialSweep(0.8F)
                    .tooltipImage()
                    .attribute(PortAttributesExtension.entityInteractionRange(), 9, PortAttributeModifier.Operation.ADD_VALUE)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 0.8F, PortAttributeModifier.Operation.ADD_VALUE));

    // 效果剑
    public static final PortDeferredItem<BaseSwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish", ModTiers.UNBREAKABLE, 15, 0.5F,
            () -> SwordDefinition.builder()
                    .behavior(SwordBehaviors.PURPLE_CLUBBERFISH)
                    .tooltipImage()
                    .attribute(PortAttributesExtension.entityInteractionRange(), 2, PortAttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> LIGHTS_BANE = register("lights_bane", ModTiers.UNBREAKABLE, 11, 3, ModRarity.BLUE,
            () -> SwordDefinition.builder()
                    .projectile(SwordProjectileDefinitions.LIGHTS_BANE)
                    .tooltipImage()
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> BLOOD_BUTCHERER = register("blood_butcherer", ModTiers.UNBREAKABLE, 14, 1.3F, ModRarity.BLUE,
            () -> SwordDefinition.builder()
                    .behavior(SwordBehaviors.BLOOD_BUTCHERER)
                    .tooltipImage()
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> VOLCANO = register("volcano", ModTiers.UNBREAKABLE, 25, 1.2F, ModRarity.ORANGE,
            () -> SwordDefinition.builder()
                    .behavior(SwordBehaviors.VOLCANO)
                    .tooltipImage()
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4.0F, PortAttributeModifier.Operation.ADD_VALUE)
                    .attribute(Attributes.ATTACK_KNOCKBACK, 0.5F, PortAttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> BAT_BAT = register("bat_bat", ModTiers.UNBREAKABLE, 21, 0.6F, ModRarity.ORANGE,
            () -> SwordDefinition.builder()
                    .behavior(SwordBehaviors.BAT_BAT)
                    .tooltipImage()
                    .attribute(PortAttributesExtension.entityInteractionRange(), 2, PortAttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> TENTACLE_MACE = register("tentacle_mace", ModTiers.UNBREAKABLE, 13, 2.0F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .behavior(SwordBehaviors.TENTACLE_MACE)
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> BEE_KEEPER = register("bee_keeper", ModTiers.UNBREAKABLE, 18, 1.6F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .behavior(SwordBehaviors.BEE_KEEPER)
                    .tooltips(2)
                    .tooltipImage()
                    .specialSweep(0.8F));

    // 弹幕剑
    public static final PortDeferredItem<BaseSwordItem> ICE_BLADE = register("ice_blade", ModTiers.UNBREAKABLE, 10, 2.0F, ModRarity.BLUE,
            () -> SwordDefinition.builder()
                    .projectile(SwordProjectileDefinitions.ICE_BLADE)
                    .tooltipImage()
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> STARFURY = register("starfury", ModTiers.UNBREAKABLE, 14, 2.0F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .projectile(SwordProjectileDefinitions.STARFURY)
                    .tooltip(p -> p.withColor(0xe44189))
                    .tooltip(p -> p.withColor(0xe44189))
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> ENCHANTED_SWORD = register("enchanted_sword", ModTiers.UNBREAKABLE, 9, 2.0F, ModRarity.ORANGE,
            () -> SwordDefinition.builder()
                    .projectile(SwordProjectileDefinitions.ENCHANTED_SWORD)
                    .tooltip(p -> p.withColor(0x4156e4))
                    .tooltip(p -> p.withColor(0x4156e4))
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> BLADE_OF_GRASS = register("blade_of_grass", ModTiers.UNBREAKABLE, 10, 2.0F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .projectile(SwordProjectileDefinitions.BLADE_OF_GRASS)
                    .tooltipImage()
                    .attribute(PortAttributesExtension.entityInteractionRange(), 2, PortAttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> NIGHTS_EDGE = register("nights_edge", ModTiers.UNBREAKABLE, 25, 2.5F, ModRarity.GREEN,
            () -> SwordDefinition.builder()
                    .projectile(SwordProjectileDefinitions.NIGHTS_EDGE)
                    .tooltipImage()
                    .attribute(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .specialSweep(0.8F));
    public static final PortDeferredItem<BaseSwordItem> WAFFLES_IRON = register("waffles_iron", ModTiers.UNBREAKABLE, 27, 2.5F, ModRarity.PINK,
            () -> SwordDefinition.builder()
                    .projectile(SwordProjectileDefinitions.ICE_BLADE)
                    .tooltipImage());

    public static final PortDeferredItem<BaseSwordItem> RED_PHASEBLADE = register("red_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "red"));
    public static final PortDeferredItem<BaseSwordItem> ORANGE_PHASEBLADE = register("orange_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "orange"));
    public static final PortDeferredItem<BaseSwordItem> YELLOW_PHASEBLADE = register("yellow_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "yellow"));
    public static final PortDeferredItem<BaseSwordItem> GREEN_PHASEBLADE = register("green_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "green"));
    public static final PortDeferredItem<BaseSwordItem> BLUE_PHASEBLADE = register("blue_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "blue"));
    public static final PortDeferredItem<BaseSwordItem> PURPLE_PHASEBLADE = register("purple_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "purple"));
    public static final PortDeferredItem<BaseSwordItem> WHITE_PHASEBLADE = register("white_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "white"));

    // 特殊剑
    public static final PortDeferredItem<BaseSwordItem> CROWBAR = register("crowbar", ModTiers.UNBREAKABLE, 18, 3, ModRarity.MASTER,
            () -> SwordDefinition.builder()
                    .specialSweep(1.0F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), -1, PortAttributeModifier.Operation.ADD_VALUE));
    public static final PortDeferredItem<BaseSwordItem> DEVELOPER_SWORD = register("developer_sword", ModTiers.UNBREAKABLE, 9999, 9999, ModRarity.MASTER,
            () -> SwordDefinition.builder()
                    .specialSweep(1.0F)
                    .attribute(PortAttributesExtension.entityInteractionRange(), 7, PortAttributeModifier.Operation.ADD_VALUE)
                    .tooltipImage()
                    .projectile(SwordProjectileDefinitions.DEVELOPER));

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
}
