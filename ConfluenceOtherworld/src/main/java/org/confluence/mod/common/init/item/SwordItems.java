package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.item.sword.LightSaber;
import org.confluence.mod.common.item.sword.SweetSword;
import org.confluence.mod.common.item.sword.legacy.InventoryTickStrategy;
import org.confluence.mod.common.item.sword.legacy.SwordPrefabs;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.terraentity.init.TEEffectStrategies;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.registries.generation.variant.ForwardGeneration;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.confluence.mod.common.item.sword.legacy.SwordPrefabs.*;


public class SwordItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 普通短剑
    public static final DeferredItem<SwordItem> COPPER_SHORT_SWORD = register("copper_short_sword", ModTiers.COPPER, 2, 3, ModRarity.WHITE, SHORT_SWORD.get()
            .addTooltip(p -> p.withColor(0x984c11)).addTooltip(p -> p.withColor(0x984c11)));
    public static final DeferredItem<SwordItem> TIN_SHORT_SWORD = register("tin_short_sword", ModTiers.TIN, 2, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> IRON_SHORT_SWORD = register("iron_short_sword", ModTiers.IRON, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> LEAD_SHORT_SWORD = register("lead_short_sword", ModTiers.LEAD, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> SILVER_SHORT_SWORD = register("silver_short_sword", ModTiers.SILVER, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> TUNGSTEN_SHORT_SWORD = register("tungsten_short_sword", ModTiers.TUNGSTEN, 5, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> GOLDEN_SHORT_SWORD = register("golden_short_sword", ModTiers.GOLD, 6, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> PLATINUM_SHORT_SWORD = register("platinum_short_sword", ModTiers.PLATINUM, 7, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> BREATHING_REED = register("breathing_reed", ModTiers.UNBREAKABLE, 2, 1.6F, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> GLADIUS = register("gladius", ModTiers.UNBREAKABLE, 6, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> UMBRELLA = register("umbrella", ModTiers.UNBREAKABLE, 2, 1.6F, ModRarity.BLUE, UMBRELLA_SWORD.get());
    public static final DeferredItem<SwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella", ModTiers.UNBREAKABLE, 2, 1.6F, ModRarity.BLUE, UMBRELLA_SWORD.get());


    //普通宽剑 默认横扫*1.5
    public static final DeferredItem<SwordItem> CACTUS_SWORD = register("cactus_sword", ModTiers.CACTUS, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> COPPER_BOARD_SWORD = register("copper_board_sword", ModTiers.COPPER, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> TIN_BOARD_SWORD = register("tin_board_sword", ModTiers.TIN, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> LEAD_BOARD_SWORD = register("lead_board_sword", ModTiers.LEAD, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> SILVER_BOARD_SWORD = register("silver_board_sword", ModTiers.SILVER, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> TUNGSTEN_BOARD_SWORD = register("tungsten_board_sword", ModTiers.TUNGSTEN, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> GOLDEN_BOARD_SWORD = register("golden_board_sword", ModTiers.GOLD, 7, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> PLATINUM_BOARD_SWORD = register("platinum_board_sword", ModTiers.PLATINUM, 8, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> MURAMASA = register("muramasa", ModTiers.UNBREAKABLE, 12, 3, NORMAL_SWORD.get()
            .setSweepRange(2.5F).addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.2f, AttributeModifier.Operation.ADD_VALUE));


    //tip 注册剑的特殊功能只需修改最后一个参数即可，只需要把 NORMAL_SWORD替换成prefab的其他预制效果，还可以追加效果
    public static final DeferredItem<SwordItem> FAKE_SWORD = register("fake_sword", ModTiers.CANDY_CANE, 3, 1.6F, ModRarity.GRAY, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> CANDY_CANE_SWORD = register("candy_cane_sword", ModTiers.CANDY_CANE, 5, 2.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> FALCON_BLADE = register("falcon_blade", ModTiers.UNBREAKABLE, 5, 2.55F, ModRarity.BLUE, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> ZOMBIE_ARM = register("zombie_arm", ModTiers.UNBREAKABLE, 5, 2.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> MANDIBLE_BLADE = register("mandible_blade", ModTiers.UNBREAKABLE, 6, 2.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> BONE_SWORD = register("bone_sword", ModTiers.UNBREAKABLE, 7, 2.9F, ModRarity.ORANGE, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish", ModTiers.UNBREAKABLE, 12, 0.5F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> STYLISH_SCISSORS = register("stylish_scissors", ModTiers.UNBREAKABLE, 5, 2.8F, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar", ModTiers.UNBREAKABLE, 7, 2.9F, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> KATANA = register("katana", ModTiers.UNBREAKABLE, 6, 3.7F, ModRarity.BLUE, NORMAL_SWORD.get());


    //改横扫大小的宽剑
    public static final DeferredItem<SwordItem> TERRAGRIM = register("terragrim", ModTiers.UNBREAKABLE, 1, 9, ModRarity.ORANGE, BOARD_SWORD.apply(1.2f));

    //效果剑
    public static final DeferredItem<SwordItem> LIGHTS_BANE = register("lights_bane", ModTiers.UNBREAKABLE, 7, 3, ModRarity.BLUE, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.LIGHTS_BANE_EFFECT.get()));
    public static final DeferredItem<SwordItem> BLOOD_BUTCHERER = register("blood_butcherer", ModTiers.UNBREAKABLE, 9, 1.3F, ModRarity.BLUE, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BLOOD_BUTCHERED_EFFECT.get()));
    public static final DeferredItem<SwordItem> VOLCANO = register("volcano", ModTiers.UNBREAKABLE, 25, 1.2f, ModRarity.ORANGE, EFFECT_SWORD
            .apply(TEEffectStrategies.Components.HELL_FIRE_EFFECT.get())
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 2f, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.5f, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<SwordItem> BAT_BAT = register("bat_bat", ModTiers.UNBREAKABLE, 14, 0.3F, ModRarity.ORANGE, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BAT_FANG_EFFECT.get()));
    public static final DeferredItem<SwordItem> TENTACLE_MACE = register("tentacle_mace", ModTiers.UNBREAKABLE, 7, 2.6F, ModRarity.GREEN, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.TENTACLE_SPIKES_EFFECT.get()));
    public static final DeferredItem<SwordItem> BEE_KEEPER = register("bee_keeper", ModTiers.UNBREAKABLE, 13, 1.6F, ModRarity.GREEN, EFFECT_SWORD
            .apply(ModEffectStrategies.Components.BEE_KEEPER_EFFECT.get()).addTooltip(2));


    //弹幕剑
    public static final DeferredItem<SwordItem> ICE_BLADE = register("ice_blade", ModTiers.UNBREAKABLE, 7, 3, ModRarity.BLUE, PROJ_SWORD
            .apply(SwordProjectileComponent.ICE_PROJ));
    public static final DeferredItem<SwordItem> STARFURY = register("starfury", ModTiers.UNBREAKABLE, 8, 2.9F, ModRarity.GREEN, PROJ_SWORD
            .apply(SwordProjectileComponent.STAR_FURY_PROJ).addTooltip(p -> p.withColor(0xe44189)).addTooltip(p -> p.withColor(0xe44189)));
    public static final DeferredItem<SwordItem> ENCHANTED_SWORD = register("enchanted_sword", ModTiers.UNBREAKABLE, 9, 2.9F, ModRarity.ORANGE, PROJ_SWORD
            .apply(SwordProjectileComponent.ENCHANTED_SWORD_PROJ).addTooltip(p -> p.withColor(0x4156e4)).addTooltip(p -> p.withColor(0x4156e4)));
    public static final DeferredItem<SwordItem> BLADE_OF_GRASS = register("blade_of_grass", ModTiers.UNBREAKABLE, 7, 2.9F, ModRarity.GREEN, PROJ_SWORD
            .apply(SwordProjectileComponent.GRASS_PROJ));
    public static final DeferredItem<SwordItem> NIGHT_EDGE = register("night_edge", ModTiers.UNBREAKABLE, 25, 2.5F, ModRarity.GREEN, PROJ_SWORD
            .apply(SwordProjectileComponent.NIGHT_PROJ));

    // 光剑 todo 重命名为phaseblade
    public static final DeferredItem<SwordItem> RED_LIGHT_SABER = register("red_light_saber", () -> new LightSaber(ModTiers.UNBREAKABLE, ModRarity.BLUE, 10, 2, "red"));
    public static final DeferredItem<SwordItem> ORANGE_LIGHT_SABER = register("orange_light_saber", () -> new LightSaber(ModTiers.UNBREAKABLE, ModRarity.BLUE, 10, 2, "orange"));
    public static final DeferredItem<SwordItem> YELLOW_LIGHT_SABER = register("yellow_light_saber", () -> new LightSaber(ModTiers.UNBREAKABLE, ModRarity.BLUE, 10, 2, "yellow"));
    public static final DeferredItem<SwordItem> GREEN_LIGHT_SABER = register("green_light_saber", () -> new LightSaber(ModTiers.UNBREAKABLE, ModRarity.BLUE, 10, 2, "green"));
    public static final DeferredItem<SwordItem> BLUE_LIGHT_SABER = register("blue_light_saber", () -> new LightSaber(ModTiers.UNBREAKABLE, ModRarity.BLUE, 10, 2, "blue"));
    public static final DeferredItem<SwordItem> PURPLE_LIGHT_SABER = register("purple_light_saber", () -> new LightSaber(ModTiers.UNBREAKABLE, ModRarity.BLUE, 10, 2, "purple"));
    public static final DeferredItem<SwordItem> WHITE_LIGHT_SABER = register("white_light_saber", () -> new LightSaber(ModTiers.UNBREAKABLE, ModRarity.BLUE, 10, 2, "white"));


    // 特殊剑
    public static final DeferredItem<SwordItem> CROWBAR = register("crowbar", ModTiers.UNBREAKABLE, 18, 3, ModRarity.MASTER, BOARD_SWORD.apply(2.0f));
    public static final DeferredItem<SwordItem> DEVELOPER_SWORD = register("developer_sword", ModTiers.UNBREAKABLE, 9999, 9999, ModRarity.MASTER, SwordPrefabs.BOARD_SWORD
            .apply(10.0f)                                //宽剑
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, 1.5f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)        //手持属性加成
            .setInventoryTick(InventoryTickStrategy.INVINCIBLE)             //背包每刻效果
            .modifyProperties(p -> p.component(ModDataComponentTypes.SWORD_PROJECTILE, new SwordProjectileComponent(
                    1, 1, 1, 50, 0.05f, 20, TESounds.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD_PROJECTILE.getId(),
                    Optional.of(new SimpleTrack(Mth.HALF_PI, 0.5f, 0.1f, Optional.empty(), 0.1)),
                    ForwardGeneration.of(0, 0), Optional.empty()
            ))));

    // 赞助者物品
    public static final DeferredItem<SwordItem> BROKEN_SWEET_SWORD = register("broken_sweet_sword", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 2, 1, new BaseSwordItem.ModifierBuilder()));
    public static final DeferredItem<SwordItem> SWEET_SWORD = register("sweet_sword", () -> new SweetSword(ModTiers.UNBREAKABLE, ModRarity.EXPERT, 6, 2, new BaseSwordItem.ModifierBuilder()
            .addTooltip(p -> p.withColor(0xe44189))
            .modifyProperties(p -> p.food(new FoodProperties(1, 1, false, 2, Optional.of(BROKEN_SWEET_SWORD.get().getDefaultInstance()), List.of(
                    new FoodProperties.PossibleEffect(() -> new MobEffectInstance(ModEffects.DELICIOUS, 200), 1.0f)
            ))))
    ));

    public static DeferredItem<SwordItem> register(String name, Supplier<SwordItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static DeferredItem<SwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, BaseSwordItem.ModifierBuilder modifierBuilder) {
        return register(name, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
    }

    public static DeferredItem<SwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, ModRarity rarity, BaseSwordItem.ModifierBuilder modifierBuilder) {
        if (tier == ModTiers.UNBREAKABLE) {
            modifierBuilder.modifyProperties(p -> p.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
        }
        return register(name, () -> new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, modifierBuilder));
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
