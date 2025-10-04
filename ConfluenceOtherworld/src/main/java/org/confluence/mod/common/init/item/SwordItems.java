package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.item.sword.GeoSwordItem;
import org.confluence.mod.common.item.sword.Phaseblade;
import org.confluence.mod.common.item.sword.SweetSword;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.terraentity.init.TEEffectStrategies;
import org.confluence.terraentity.registries.generation.variant.ForwardGeneration;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.confluence.mod.common.item.sword.legacy.SwordPrefabs.*;

/**
 * 允许空挥的剑都是特殊横扫剑<p>
 * 有自动挥舞的剑都是特殊横扫剑<p>
 * 有特殊横扫的剑不一定是自动挥舞的剑<p>
 * 是否允许自动挥舞是根据{@link ModTags.Items#COULD_AUTO_ATTACK}判断的<p>
 * 是否允许特殊横扫是根据{@link BaseSwordItem.ModifierBuilder#specialSweep}判断的，即下文中的.setSpecialSweep()<p>
 * 如果不是由BOARD_SWORD定义的，那么需要手动加上.addAttributeModifier(Attributes.SWEEPING_DAMAGE_RATIO, 0.8F, AttributeModifier.Operation.ADD_VALUE)
 */
public class SwordItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 铂金以上剑参考数值为 泰拉wiki中的伤害÷2后 + 2为基础值
    // 普通短剑
    public static final DeferredItem<BaseSwordItem> COPPER_SHORT_SWORD = register("copper_short_sword", ModTiers.COPPER, 2, 3, ModRarity.WHITE, SHORT_SWORD.get()
            .addTooltip(p -> p.withColor(0x984c11)).addTooltip(p -> p.withColor(0x984c11)));
    public static final DeferredItem<BaseSwordItem> TIN_SHORT_SWORD = register("tin_short_sword", ModTiers.TIN, 2, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> IRON_SHORT_SWORD = register("iron_short_sword", ModTiers.IRON, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> LEAD_SHORT_SWORD = register("lead_short_sword", ModTiers.LEAD, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> SILVER_SHORT_SWORD = register("silver_short_sword", ModTiers.SILVER, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> TUNGSTEN_SHORT_SWORD = register("tungsten_short_sword", ModTiers.TUNGSTEN, 5, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> GOLDEN_SHORT_SWORD = register("golden_short_sword", ModTiers.GOLD, 6, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> PLATINUM_SHORT_SWORD = register("platinum_short_sword", ModTiers.PLATINUM, 7, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> BREATHING_REED = register("breathing_reed", ModTiers.UNBREAKABLE, 2, 1.6F, ModRarity.BLUE, SHORT_SWORD.get()
            .addTooltip(p -> p.withColor(11184810)));
    public static final DeferredItem<BaseSwordItem> GLADIUS = register("gladius", ModTiers.UNBREAKABLE, 6, 3, SHORT_SWORD.get());
    public static final DeferredItem<BaseSwordItem> UMBRELLA = register("umbrella", () -> new GeoSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, UMBRELLA_SWORD.get()
            .addTooltip(p -> p.withColor(11184810))
            .modifyProperties(p -> p.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE))));
    public static final DeferredItem<BaseSwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella", () -> new GeoSwordItem(ModTiers.UNBREAKABLE, ModRarity.BLUE, 2, 1.6F, UMBRELLA_SWORD.get()
            .addTooltip(p -> p.withColor(11184810))
            .modifyProperties(p -> p.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE))));

    // 普通宽剑 默认横扫*1.5
    public static final DeferredItem<BaseSwordItem> CACTUS_SWORD = register("cactus_sword", ModTiers.CACTUS, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> EBONWOOD_SWORD = register("ebonwood_sword", ModTiers.CACTUS, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> SHADEWOOD_SWORD = register("shadewood_sword", ModTiers.CACTUS, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> ASH_WOOD_SWORD = register("ash_wood_sword", ModTiers.CACTUS, 7, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> PEARLWOOD_SWORD = register("pearlwood_sword", ModTiers.CACTUS, 8, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> COPPER_BROADSWORD = register("copper_broadsword", ModTiers.COPPER, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> TIN_BROADSWORD = register("tin_broadsword", ModTiers.TIN, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> LEAD_BROADSWORD = register("lead_broadsword", ModTiers.LEAD, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> SILVER_BROADSWORD = register("silver_broadsword", ModTiers.SILVER, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> TUNGSTEN_BROADSWORD = register("tungsten_broadsword", ModTiers.TUNGSTEN, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> GOLDEN_BROADSWORD = register("golden_broadsword", ModTiers.GOLD, 7, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> PLATINUM_BROADSWORD = register("platinum_broadsword", ModTiers.PLATINUM, 8, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> MURAMASA = register("muramasa", ModTiers.UNBREAKABLE, 15, 3, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 1.5F, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.2f, AttributeModifier.Operation.ADD_VALUE).hasImage());
    public static final DeferredItem<BaseSwordItem> COBALT_SWORD = register("cobalt_sword", ModTiers.UNBREAKABLE, 25, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE).hasImage());
    public static final DeferredItem<BaseSwordItem> PALLADIUM_SWORD = register("palladium_sword", ModTiers.UNBREAKABLE, 29, 2.6F, ModRarity.LIGHT_RED, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE).hasImage());
    public static final DeferredItem<BaseSwordItem> MYTHRIL_SWORD = register("mythril_sword", ModTiers.UNBREAKABLE, 30, 2.6F, ModRarity.LIGHT_RED, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE).hasImage());
    public static final DeferredItem<BaseSwordItem> ORICHALCUM_SWORD = register("orichalcum_sword", ModTiers.UNBREAKABLE, 34, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE).hasImage());
    public static final DeferredItem<BaseSwordItem> ADAMANTITE_SWORD = register("adamantite_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE).hasImage());
    public static final DeferredItem<BaseSwordItem> TITANIUM_SWORD = register("titanium_sword", ModTiers.UNBREAKABLE, 36, 2.4F, ModRarity.LIGHT_RED, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 4, AttributeModifier.Operation.ADD_VALUE).hasImage());

    // tip 注册剑的特殊功能只需修改最后一个参数即可，只需要把 NORMAL_SWORD替换成prefab的其他预制效果，还可以追加效果
    public static final DeferredItem<BaseSwordItem> FAKE_SWORD = register("fake_sword", ModTiers.CANDY_CANE, 3, 1.6F, ModRarity.GRAY, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> CANDY_CANE_SWORD = register("candy_cane_sword", ModTiers.CANDY_CANE, 5, 2.6F, NORMAL_SWORD.get().hasImage());
    public static final DeferredItem<BaseSwordItem> FALCON_BLADE = register("falcon_blade", ModTiers.UNBREAKABLE, 9, 2.55F, ModRarity.BLUE, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> ZOMBIE_ARM = register("zombie_arm", ModTiers.UNBREAKABLE, 5, 2.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> MANDIBLE_BLADE = register("mandible_blade", ModTiers.UNBREAKABLE, 6, 2.6F, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> BONE_SWORD = register("bone_sword", ModTiers.UNBREAKABLE, 7, 2.9F, ModRarity.ORANGE, NORMAL_SWORD.get().hasImage());
    public static final DeferredItem<BaseSwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish", ModTiers.UNBREAKABLE, 15, 0.5F, NORMAL_SWORD.get().hasImage());
    public static final DeferredItem<BaseSwordItem> STYLISH_SCISSORS = register("stylish_scissors", ModTiers.UNBREAKABLE, 5, 2.8F, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar", ModTiers.UNBREAKABLE, 7, 2.9F, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<BaseSwordItem> KATANA = register("katana", ModTiers.UNBREAKABLE, 6, 3.7F, ModRarity.BLUE, NORMAL_SWORD.get().hasImage());

    // 改横扫大小的宽剑(由 ENTITY_INTERACTION_RANGE 属性控制)
    public static final DeferredItem<BaseSwordItem> TERRAGRIM = register("terragrim", ModTiers.UNBREAKABLE, 1, 9, ModRarity.ORANGE, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, -1.8F, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredItem<BaseSwordItem> BREAKER_BLADE = register("breaker_blade", ModTiers.UNBREAKABLE, 37, 1.0F, ModRarity.LIGHT_RED, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 9, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.8F, AttributeModifier.Operation.ADD_VALUE).hasImage());

    // 效果剑
    public static final DeferredItem<BaseSwordItem> LIGHTS_BANE = register("lights_bane", ModTiers.UNBREAKABLE, 11, 3, ModRarity.BLUE, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.LIGHTS_BANE_EFFECT.get()).hasImage());
    public static final DeferredItem<BaseSwordItem> BLOOD_BUTCHERER = register("blood_butcherer", ModTiers.UNBREAKABLE, 14, 1.3F, ModRarity.BLUE, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BLOOD_BUTCHERED_EFFECT.get()).hasImage());
    public static final DeferredItem<BaseSwordItem> VOLCANO = register("volcano", ModTiers.UNBREAKABLE, 25, 1.2f, ModRarity.ORANGE, EFFECT_SWORD
            .apply(TEEffectStrategies.Components.HELL_FIRE_EFFECT.get())
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 2f, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.5f, AttributeModifier.Operation.ADD_VALUE).hasImage());
    public static final DeferredItem<BaseSwordItem> BAT_BAT = register("bat_bat", ModTiers.UNBREAKABLE, 21, 0.6F, ModRarity.ORANGE, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BAT_FANG_EFFECT.get()).hasImage());
    public static final DeferredItem<BaseSwordItem> TENTACLE_MACE = register("tentacle_mace", ModTiers.UNBREAKABLE, 13, 2.6F, ModRarity.GREEN, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.TENTACLE_SPIKES_EFFECT.get()));
    public static final DeferredItem<BaseSwordItem> BEE_KEEPER = register("bee_keeper", ModTiers.UNBREAKABLE, 18, 1.6F, ModRarity.GREEN, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BEE_KEEPER_EFFECT.get()).addTooltip(2).hasImage());

    // 弹幕剑
    public static final DeferredItem<BaseSwordItem> ICE_BLADE = register("ice_blade", ModTiers.UNBREAKABLE, 10, 3, ModRarity.BLUE, PROJ_SWORD
            .apply(SwordProjectileComponent.ICE_PROJ).setSpecialSweep().hasImage()
            .addAttributeModifier(Attributes.SWEEPING_DAMAGE_RATIO, 0.8F, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseSwordItem> STARFURY = register("starfury", ModTiers.UNBREAKABLE, 14, 2.9F, ModRarity.GREEN, PROJ_SWORD
            .apply(SwordProjectileComponent.STAR_FURY_PROJ).addTooltip(p -> p.withColor(0xe44189)).addTooltip(p -> p.withColor(0xe44189)));
    public static final DeferredItem<BaseSwordItem> ENCHANTED_SWORD = register("enchanted_sword", ModTiers.UNBREAKABLE, 12, 2.9F, ModRarity.ORANGE, PROJ_SWORD
            .apply(SwordProjectileComponent.ENCHANTED_SWORD_PROJ).addTooltip(p -> p.withColor(0x4156e4)).addTooltip(p -> p.withColor(0x4156e4)));
    public static final DeferredItem<BaseSwordItem> BLADE_OF_GRASS = register("blade_of_grass", ModTiers.UNBREAKABLE, 11, 2.9F, ModRarity.GREEN, PROJ_SWORD
            .apply(SwordProjectileComponent.GRASS_PROJ).hasImage());
    public static final DeferredItem<BaseSwordItem> NIGHTS_EDGE = register("nights_edge", ModTiers.UNBREAKABLE, 25, 2.5F, ModRarity.GREEN, PROJ_SWORD
            .apply(SwordProjectileComponent.NIGHT_PROJ).hasImage());

    public static final DeferredItem<BaseSwordItem> RED_PHASEBLADE = register("red_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "red"));
    public static final DeferredItem<BaseSwordItem> ORANGE_PHASEBLADE = register("orange_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "orange"));
    public static final DeferredItem<BaseSwordItem> YELLOW_PHASEBLADE = register("yellow_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "yellow"));
    public static final DeferredItem<BaseSwordItem> GREEN_PHASEBLADE = register("green_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "green"));
    public static final DeferredItem<BaseSwordItem> BLUE_PHASEBLADE = register("blue_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "blue"));
    public static final DeferredItem<BaseSwordItem> PURPLE_PHASEBLADE = register("purple_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "purple"));
    public static final DeferredItem<BaseSwordItem> WHITE_PHASEBLADE = register("white_phaseblade", () -> new Phaseblade(ModTiers.METEOR, ModRarity.BLUE, 10, 2, "white"));

    // 特殊剑
    public static final DeferredItem<BaseSwordItem> CROWBAR = register("crowbar", ModTiers.UNBREAKABLE, 18, 3, ModRarity.MASTER, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, -1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseSwordItem> DEVELOPER_SWORD = register("developer_sword", ModTiers.UNBREAKABLE, 9999, 9999, ModRarity.MASTER, BOARD_SWORD.get()
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 7, AttributeModifier.Operation.ADD_VALUE)
            .modifyProperties(p -> p.component(ModDataComponentTypes.SWORD_PROJECTILE, new SwordProjectileComponent(
                    1, 1, 1, 50, 0.05f, 20, ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD_PROJECTILE.getId(),
                    Optional.of(new SimpleTrack(Mth.HALF_PI, 0.5f, 0.1f, Optional.empty(), 0.1)),
                    ForwardGeneration.of(0, 0), Optional.empty()
            ))));

    // 赞助者物品
    public static final DeferredItem<BaseSwordItem> BROKEN_SWEET_SWORD = register("broken_sweet_sword", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 2, 1, new BaseSwordItem.ModifierBuilder()));
    public static final DeferredItem<BaseSwordItem> SWEET_SWORD = register("sweet_sword", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 6, 2, new BaseSwordItem.ModifierBuilder()
            .addTooltip(p -> p.withColor(0xe44189))
            .modifyProperties(p -> p.food(new FoodProperties(1, 1, false, 2, Optional.of(BROKEN_SWEET_SWORD.get().getDefaultInstance()), List.of(
                    new FoodProperties.PossibleEffect(() -> new MobEffectInstance(ModEffects.DELICIOUS, 200), 1.0f)
            ))))
    ));

    public static DeferredItem<BaseSwordItem> register(String name, Supplier<BaseSwordItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static DeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, BaseSwordItem.ModifierBuilder modifierBuilder) {
        return register(name, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
    }

    public static DeferredItem<BaseSwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, ModRarity rarity, BaseSwordItem.ModifierBuilder modifierBuilder) {
        return register(name, () -> {
            if (tier == ModTiers.UNBREAKABLE) {
                modifierBuilder.modifyProperties(p -> p.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE));
            }
            return new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, modifierBuilder);
        });
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
