package org.confluence.mod.common.init.item;

import PortLib.extensions.net.minecraft.network.chat.MutableComponent.PortMutableComponentExtension;
import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import com.google.common.base.Supplier;
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
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.generation.variant.ForwardGeneration;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.item.sword.*;
import org.confluence.mod.common.item.sword.legacy.SwordPrefabs;
import org.confluence.mod.common.item.sword.legacy.SwordStrategy;
import org.confluence.mod.common.track.variant.SimpleTrack;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.IPortFoodProperties;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.Optional;

import static org.confluence.mod.common.item.sword.legacy.SwordPrefabs.*;

/// 允许空挥的剑都是特殊横扫剑
/// 有自动挥舞的剑都是特殊横扫剑
/// 有特殊横扫的剑不一定是自动挥舞的剑
/// 是否允许自动挥舞是根据[ModTags.Items#AUTO_ATTACK_WHITELIST]判断的
/// 是否允许特殊横扫是根据[BaseSwordItem.ModifierBuilder#specialSweep]判断的，即下文中的.setSpecialSweep()
/// 如果不是由BOARD_SWORD定义的，那么需要使用[SwordPrefabs#withSpecialSweep]方法
public class SwordItems {
    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    // 铂金以上剑参考数值为 泰拉wiki中的伤害÷2后 + 2为基础值
    // 普通短剑
    public static final PortDeferredItem<BaseSwordItem> COPPER_SHORT_SWORD = register("copper_short_sword", ModTiers.COPPER, 2, 3, ModRarity.WHITE, SHORT_SWORD.get()
            .addTooltip(p -> PortMutableComponentExtension.withColor(p, 0x984c11))
            .addTooltip(p -> PortMutableComponentExtension.withColor(p, 0x984c11)));
    public static final PortDeferredItem<BaseSwordItem> TIN_SHORT_SWORD = register("tin_short_sword", ModTiers.TIN, 2, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> IRON_SHORT_SWORD = register("iron_short_sword", ModTiers.IRON, 4, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> LEAD_SHORT_SWORD = register("lead_short_sword", ModTiers.LEAD, 4, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> SILVER_SHORT_SWORD = register("silver_short_sword", ModTiers.SILVER, 4, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> TUNGSTEN_SHORT_SWORD = register("tungsten_short_sword", ModTiers.TUNGSTEN, 5, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> GOLDEN_SHORT_SWORD = register("golden_short_sword", ModTiers.GOLD, 6, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> PLATINUM_SHORT_SWORD = register("platinum_short_sword", ModTiers.PLATINUM, 7, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> BREATHING_REED = register("breathing_reed", ModTiers.UNBREAKABLE, 2, 1.6F, ModRarity.BLUE, SHORT_SWORD.get()
            .addTooltip(p -> PortMutableComponentExtension.withColor(p, 11184810)));
    public static final PortDeferredItem<BaseSwordItem> GLADIUS = register("gladius", ModTiers.UNBREAKABLE, 6, 3, SHORT_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> UMBRELLA = register("umbrella", () -> new GeoSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, UMBRELLA_SWORD.get()
            .addTooltip(p -> PortMutableComponentExtension.withColor(p, 11184810))
            .unbreakable()));
    public static final PortDeferredItem<BaseSwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella", () -> new GeoSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, UMBRELLA_SWORD.get()
            .addTooltip(p -> PortMutableComponentExtension.withColor(p, 11184810))
            .unbreakable()));

    // 普通宽剑 默认横扫*1.5
    public static final PortDeferredItem<BaseSwordItem> CACTUS_SWORD = register("cactus_sword", ModTiers.CACTUS, 5, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> EBONWOOD_SWORD = register("ebonwood_sword", ModTiers.CACTUS, 6, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> SHADEWOOD_SWORD = register("shadewood_sword", ModTiers.CACTUS, 6, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> ASH_WOOD_SWORD = register("ash_wood_sword", ModTiers.CACTUS, 7, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> PEARLWOOD_SWORD = register("pearlwood_sword", ModTiers.CACTUS, 8, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> COPPER_BROADSWORD = register("copper_broadsword", ModTiers.COPPER, 5, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> TIN_BROADSWORD = register("tin_broadsword", ModTiers.TIN, 5, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> LEAD_BROADSWORD = register("lead_broadsword", ModTiers.LEAD, 6, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> SILVER_BROADSWORD = register("silver_broadsword", ModTiers.SILVER, 6, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> TUNGSTEN_BROADSWORD = register("tungsten_broadsword", ModTiers.TUNGSTEN, 6, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> GOLDEN_BROADSWORD = register("golden_broadsword", ModTiers.GOLD, 7, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> PLATINUM_BROADSWORD = register("platinum_broadsword", ModTiers.PLATINUM, 8, 1.6F, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> MURAMASA = register("muramasa", ModTiers.UNBREAKABLE, 15, 3, BOARD_SWORD.apply(0.8F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 1.5F, PortAttributeModifier.PortOperation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.2f, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage());
    public static final PortDeferredItem<BaseSwordItem> COBALT_SWORD = register("cobalt_sword", ModTiers.UNBREAKABLE, 25, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.apply(0.8F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage());
    public static final PortDeferredItem<BaseSwordItem> PALLADIUM_SWORD = register("palladium_sword", ModTiers.UNBREAKABLE, 29, 2.6F, ModRarity.LIGHT_RED, BOARD_SWORD.apply(0.8F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage());
    public static final PortDeferredItem<BaseSwordItem> MYTHRIL_SWORD = register("mythril_sword", ModTiers.UNBREAKABLE, 30, 2.6F, ModRarity.LIGHT_RED, BOARD_SWORD.apply(0.8F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage());
    public static final PortDeferredItem<BaseSwordItem> ORICHALCUM_SWORD = register("orichalcum_sword", ModTiers.UNBREAKABLE, 34, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.apply(0.8F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage());
    public static final PortDeferredItem<BaseSwordItem> ADAMANTITE_SWORD = register("adamantite_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.apply(0.8F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage());
    public static final PortDeferredItem<BaseSwordItem> TITANIUM_SWORD = register("titanium_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.apply(0.8F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage());

    // tip 注册剑的特殊功能只需修改最后一个参数即可，只需要把 NORMAL_SWORD替换成prefab的其他预制效果，还可以追加效果
    public static final PortDeferredItem<BaseSwordItem> FAKE_SWORD = register("fake_sword", ModTiers.CANDY_CANE, 3, 1.6F, ModRarity.GRAY, NORMAL_SWORD.get());
    public static final PortDeferredItem<BaseSwordItem> CANDY_CANE_SWORD = register("candy_cane_sword", ModTiers.CANDY_CANE, 5, 1.8F, BOARD_SWORD.apply(0.5F).hasImage());
    public static final PortDeferredItem<BaseSwordItem> FALCON_BLADE = register("falcon_blade", ModTiers.UNBREAKABLE, 6, 1.8F, ModRarity.BLUE, BOARD_SWORD.apply(0.5F)
            .setInventoryTick(SwordStrategy.FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_A)
            .setPreLivingDamage(SwordStrategy.FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_B)
            .setPostHurtEnemy(SwordStrategy.FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_C));
    public static final PortDeferredItem<BaseSwordItem> ZOMBIE_ARM = register("zombie_arm", ModTiers.UNBREAKABLE, 5, 2.4F, BOARD_SWORD.apply(0.5F));
    public static final PortDeferredItem<BaseSwordItem> MANDIBLE_BLADE = register("mandible_blade", ModTiers.UNBREAKABLE, 6, 2.4F, BOARD_SWORD.apply(0.8F));
    public static final PortDeferredItem<BaseSwordItem> BONE_SWORD = register("bone_sword", ModTiers.UNBREAKABLE, 7, 2.4F, ModRarity.ORANGE, BOARD_SWORD.apply(0.8F).hasImage());
    public static final PortDeferredItem<BaseSwordItem> STYLISH_SCISSORS = register("stylish_scissors", ModTiers.UNBREAKABLE, 5, 2.2F, ModRarity.GREEN, BOARD_SWORD.apply(0.8F));
    public static final PortDeferredItem<BaseSwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar", ModTiers.UNBREAKABLE, 7, 2.3F, ModRarity.GREEN, BOARD_SWORD.apply(0.8F)
            .setInventoryTick(SwordStrategy.FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_A)
            .setPreLivingDamage(SwordStrategy.FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_B)
            .setPostHurtEnemy(SwordStrategy.FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_C));
    public static final PortDeferredItem<BaseSwordItem> KATANA = register("katana", ModTiers.UNBREAKABLE, 6, 3.7F, ModRarity.BLUE, BOARD_SWORD.apply(0.8F));

    // 改横扫大小的宽剑(由 ENTITY_INTERACTION_RANGE 属性控制)
    public static final PortDeferredItem<BaseSwordItem> TERRAGRIM = register("terragrim", ModTiers.UNBREAKABLE, 7, 7, ModRarity.ORANGE, BOARD_SWORD.apply(0.0F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), -1.4F, PortAttributeModifier.PortOperation.ADD_VALUE));

    public static final PortDeferredItem<BaseSwordItem> BREAKER_BLADE = register("breaker_blade", ModTiers.UNBREAKABLE, 37, 1.0F, ModRarity.LIGHT_RED, BOARD_SWORD.apply(0.8F).hasImage()
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 9, PortAttributeModifier.PortOperation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.8F, PortAttributeModifier.PortOperation.ADD_VALUE));

    // 效果剑
    public static final PortDeferredItem<BaseSwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish", ModTiers.UNBREAKABLE, 15, 0.5F, withSpecialSweep(0.8F, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.PURPLE_CLUBBERFISH_EFFECT.get()).hasImage()
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 2, PortAttributeModifier.PortOperation.ADD_VALUE)));
    public static final PortDeferredItem<BaseSwordItem> LIGHTS_BANE = register("lights_bane", ModTiers.UNBREAKABLE, 11, 3, ModRarity.BLUE, withSpecialSweep(0.8F, PROJ_SWORD
            .apply(SwordProjectileComponent.LIGHTS_BANE_PROJ).hasImage()));
    public static final PortDeferredItem<BaseSwordItem> BLOOD_BUTCHERER = register("blood_butcherer", ModTiers.UNBREAKABLE, 14, 1.3F, ModRarity.BLUE, withSpecialSweep(0.8F, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BLOOD_BUTCHERED_EFFECT.get()).hasImage()));
    public static final PortDeferredItem<BaseSwordItem> VOLCANO = register("volcano", ModTiers.UNBREAKABLE, 25, 1.2f, ModRarity.ORANGE, withSpecialSweep(0.8F, EFFECT_SWORD
            .apply(TEEffectStrategies.Components.HELL_FIRE_EFFECT.get()).hasImage()
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4f, PortAttributeModifier.PortOperation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.5f, PortAttributeModifier.PortOperation.ADD_VALUE)));
    public static final PortDeferredItem<BaseSwordItem> BAT_BAT = register("bat_bat", ModTiers.UNBREAKABLE, 21, 0.6F, ModRarity.ORANGE, withSpecialSweep(0.8F, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BAT_FANG_EFFECT.get()).hasImage()
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 2, PortAttributeModifier.PortOperation.ADD_VALUE)));
    public static final PortDeferredItem<BaseSwordItem> TENTACLE_MACE = register("tentacle_mace", ModTiers.UNBREAKABLE, 13, 2.0F, ModRarity.GREEN, withSpecialSweep(0.8F, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.TENTACLE_SPIKES_EFFECT.get())));
    public static final PortDeferredItem<BaseSwordItem> BEE_KEEPER = register("bee_keeper", ModTiers.UNBREAKABLE, 18, 1.6F, ModRarity.GREEN, withSpecialSweep(0.8F, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BEE_KEEPER_EFFECT.get()).addTooltips(2).hasImage()));

    // 弹幕剑
    public static final PortDeferredItem<BaseSwordItem> ICE_BLADE = register("ice_blade", ModTiers.UNBREAKABLE, 10, 2.0F, ModRarity.BLUE, withSpecialSweep(0.8F, PROJ_SWORD
            .apply(SwordProjectileComponent.ICE_PROJ).hasImage()));
    public static final PortDeferredItem<BaseSwordItem> STARFURY = register("starfury", ModTiers.UNBREAKABLE, 14, 2.0F, ModRarity.GREEN, withSpecialSweep(0.8F, PROJ_SWORD
            .apply(SwordProjectileComponent.STAR_FURY_PROJ).addTooltip(p -> PortMutableComponentExtension.withColor(p, 0xe44189)).addTooltip(p -> PortMutableComponentExtension.withColor(p, 0xe44189))));
    public static final PortDeferredItem<BaseSwordItem> ENCHANTED_SWORD = register("enchanted_sword", ModTiers.UNBREAKABLE, 9, 2.0F, ModRarity.ORANGE, withSpecialSweep(0.8F, PROJ_SWORD
            .apply(SwordProjectileComponent.ENCHANTED_SWORD_PROJ).addTooltip(p -> PortMutableComponentExtension.withColor(p, 0x4156e4)).addTooltip(p -> PortMutableComponentExtension.withColor(p, 0x4156e4))));
    public static final PortDeferredItem<BaseSwordItem> BLADE_OF_GRASS = register("blade_of_grass", ModTiers.UNBREAKABLE, 10, 2.0F, ModRarity.GREEN, withSpecialSweep(0.8F, PROJ_SWORD
            .apply(SwordProjectileComponent.GRASS_PROJ).hasImage()
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 2, PortAttributeModifier.PortOperation.ADD_VALUE)));
    public static final PortDeferredItem<BaseSwordItem> NIGHTS_EDGE = register("nights_edge", ModTiers.UNBREAKABLE, 25, 2.5F, ModRarity.GREEN, withSpecialSweep(0.8F, PROJ_SWORD
            .apply(SwordProjectileComponent.NIGHT_PROJ).hasImage()
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 4, PortAttributeModifier.PortOperation.ADD_VALUE)));
    public static final PortDeferredItem<BaseSwordItem> WAFFLES_IRON = register("waffles_iron", ModTiers.UNBREAKABLE, 27, 2.5F, ModRarity.PINK, PROJ_SWORD
            .apply(SwordProjectileComponent.ICE_PROJ).hasImage());

    public static final PortDeferredItem<BaseSwordItem> RED_PHASEBLADE = register("red_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "red"));
    public static final PortDeferredItem<BaseSwordItem> ORANGE_PHASEBLADE = register("orange_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "orange"));
    public static final PortDeferredItem<BaseSwordItem> YELLOW_PHASEBLADE = register("yellow_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "yellow"));
    public static final PortDeferredItem<BaseSwordItem> GREEN_PHASEBLADE = register("green_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "green"));
    public static final PortDeferredItem<BaseSwordItem> BLUE_PHASEBLADE = register("blue_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "blue"));
    public static final PortDeferredItem<BaseSwordItem> PURPLE_PHASEBLADE = register("purple_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "purple"));
    public static final PortDeferredItem<BaseSwordItem> WHITE_PHASEBLADE = register("white_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "white"));

    // 特殊剑
    public static final PortDeferredItem<BaseSwordItem> CROWBAR = register("crowbar", ModTiers.UNBREAKABLE, 18, 3, ModRarity.MASTER, BOARD_SWORD.apply(1.0F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), -1, PortAttributeModifier.PortOperation.ADD_VALUE));
    public static final PortDeferredItem<BaseSwordItem> DEVELOPER_SWORD = register("developer_sword", ModTiers.UNBREAKABLE, 9999, 9999, ModRarity.MASTER, BOARD_SWORD.apply(1.0F)
            .addAttributeModifier(PortAttributesExtension.entityInteractionRange(), 7, PortAttributeModifier.PortOperation.ADD_VALUE).hasImage()
            .modifyProperties(p -> p.component(ModDataComponentTypes.SWORD_PROJECTILE, new SwordProjectileComponent(
                    1, 0.3f, 1, 50, 0f, 20, ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD_PROJECTILE.getId(),
                    Optional.of(new SimpleTrack(Mth.HALF_PI, 0.8f, 0.2f, Optional.empty(), 0.1)),
                    ForwardGeneration.of(0, 0), Optional.empty()
            ))));

    // 赞助者物品
    public static final PortDeferredItem<BaseSwordItem> BROKEN_SWEET_SWORD = register("broken_sweet_sword", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 2, 1, new BaseSwordItem.ModifierBuilder()));
    public static final PortDeferredItem<BaseSwordItem> SWEET_SWORD = register("sweet_sword", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 6, 2, new BaseSwordItem.ModifierBuilder()
            .addTooltip(p -> PortMutableComponentExtension.withColor(p, 0xe44189))
            .modifyProperties(p -> {
                FoodProperties properties = new FoodProperties.Builder().nutrition(1).saturationMod(1)
                        .effect(() -> new MobEffectInstance(ModEffects.DELICIOUS.get(), 200), 1.0f)
                        .build();
                IPortFoodProperties i = IPortFoodProperties.of(properties);
                i.portlib$setEatSeconds(2);
                i.portlib$setUsingConvertsTo(BROKEN_SWEET_SWORD.toStack());
                p.food(properties);
            })
    ));

    public static final PortDeferredItem<BaseSwordItem> STAR_STEEL_SWORD = register("star_steel_sword", StarSteelSword::new);

    public static PortDeferredItem<BaseSwordItem> register(String name, Supplier<BaseSwordItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static PortDeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, BaseSwordItem.ModifierBuilder modifierBuilder) {
        return register(name, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
    }

    public static PortDeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, ModRarity rarity, BaseSwordItem.ModifierBuilder modifierBuilder) {
        return register(name, () -> {
            if (tier == ModTiers.UNBREAKABLE) {
                modifierBuilder.unbreakable();
            }
            return new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, modifierBuilder);
        });
    }

    public static boolean isShortSword(PortDeferredItem<? extends Item> holder) {
        return holder.getId().getPath().endsWith("_short_sword");
    }

    public static float processEffect(DamageSource damageSource, @Nullable Entity attacker, LivingEntity victim, float amount) {
        ItemStack weapon = damageSource.getWeaponItem();
        if (weapon != null && weapon.getItem() instanceof BaseSwordItem sword) {
            sword.applyHitEffects(weapon, attacker, victim, damageSource);
            if (sword.modifier != null && sword.modifier.preLivingDamage != null) {
                amount = sword.modifier.preLivingDamage.apply(weapon, damageSource, attacker, victim, amount);
            }
        }
        return amount;
    }
}
