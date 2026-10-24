package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.armor.ModArmorMaterials;
import org.confluence.mod.common.item.armor.BaseArmorItem;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;

import java.util.function.Consumer;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class ArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseArmorItem> MINING_HELMET = register("mining_helmet", "挖矿头盔", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(TCItems.LUMINANCE, 10)));
    public static final DeferredItem<BaseArmorItem> MINING_CHESTPLATE = register("mining_chestplate", "挖矿衣", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.BLOCK_BREAK_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final DeferredItem<BaseArmorItem> MINING_LEGGINGS = register("mining_leggings", "挖矿裤", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.BLOCK_BREAK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final DeferredItem<BaseArmorItem> MINING_BOOTS = register("mining_boots", "挖矿靴", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.BLOCK_BREAK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static final DeferredItem<BaseArmorItem> PLANK_HELMET = register("plank_helmet", "木制头盔", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/plank_armor")
            .durability(55));
    public static final DeferredItem<BaseArmorItem> PLANK_CHESTPLATE = register("plank_chestplate", "木制胸甲", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/plank_armor")
            .durability(80));
    public static final DeferredItem<BaseArmorItem> PLANK_LEGGINGS = register("plank_leggings", "木制护腿", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/plank_armor")
            .durability(75));
    public static final DeferredItem<BaseArmorItem> PLANK_BOOTS = register("plank_boots", "木制靴子", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/plank_armor")
            .durability(65));

    public static final DeferredItem<BaseArmorItem> EBONY_HELMET = register("ebony_helmet", "乌木头盔", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/ebony_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> EBONY_CHESTPLATE = register("ebony_chestplate", "乌木胸甲", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ebony_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> EBONY_LEGGINGS = register("ebony_leggings", "乌木护腿", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/ebony_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> EBONY_BOOTS = register("ebony_boots", "乌木靴子", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/ebony_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_HELMET = register("shadow_plank_helmet", "暗影木头盔", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_CHESTPLATE = register("shadow_plank_chestplate", "暗影木胸甲", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_LEGGINGS = register("shadow_plank_leggings", "暗影木护腿", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_BOOTS = register("shadow_plank_boots", "暗影木靴子", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> ASH_HELMET = register("ash_helmet", "灰烬木头盔", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/ash_armor")
            .durability(220));
    public static final DeferredItem<BaseArmorItem> ASH_CHESTPLATE = register("ash_chestplate", "灰烬木胸甲", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ash_armor")
            .durability(270));
    public static final DeferredItem<BaseArmorItem> ASH_LEGGINGS = register("ash_leggings", "灰烬木护腿", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/ash_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> ASH_BOOTS = register("ash_boots", "灰烬木靴子", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/ash_armor")
            .durability(230));

    public static final DeferredItem<BaseArmorItem> RAIN_CAP = register("rain_cap", "雨帽", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/raincoat_armor"));
    public static final DeferredItem<BaseArmorItem> RAINCOAT = registerWithEnglishName("raincoat", "雨衣", "Rain Coat", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/raincoat_armor"));

    public static final DeferredItem<BaseArmorItem> SNOW_CAPS = register("snow_caps", "防雪帽", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/snow_armor"));
    public static final DeferredItem<BaseArmorItem> SNOW_SUITS = register("snow_suits", "防雪衣", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/snow_armor"));
    public static final DeferredItem<BaseArmorItem> INSULATED_PANTS = register("insulated_pants", "保温裤", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/snow_armor"));
    public static final DeferredItem<BaseArmorItem> INSULATED_SHOES = register("insulated_shoes", "保温鞋", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/snow_armor"));

    public static final DeferredItem<BaseArmorItem> PINK_SNOW_CAPS = register("pink_snow_caps", "粉色防雪帽", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/snow_pink_armor"));
    public static final DeferredItem<BaseArmorItem> PINK_SNOW_SUITS = register("pink_snow_suits", "粉色防雪衣", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/snow_pink_armor"));
    public static final DeferredItem<BaseArmorItem> PINK_INSULATED_PANTS = register("pink_insulated_pants", "粉色保温裤", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/snow_pink_armor"));
    public static final DeferredItem<BaseArmorItem> PINK_INSULATED_SHOES = register("pink_insulated_shoes", "粉色保温鞋", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/snow_pink_armor"));

    public static final DeferredItem<BaseArmorItem> ANGLER_HAT = register("angler_hat", "渔夫帽", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/angler_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.FISHING$POWER, 5.0F)));
    public static final DeferredItem<BaseArmorItem> ANGLER_VEST = register("angler_vest", "渔夫背心", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/angler_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.FISHING$POWER, 5.0F)));
    public static final DeferredItem<BaseArmorItem> ANGLER_PANTS = register("angler_pants", "渔夫裤", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/angler_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.FISHING$POWER, 5.0F)));

    public static final DeferredItem<BaseArmorItem> CACTUS_HELMET = register("cactus_helmet", "仙人掌头盔", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cactus_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> CACTUS_CHESTPLATE = register("cactus_chestplate", "仙人掌胸甲", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/cactus_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> CACTUS_LEGGINGS = register("cactus_leggings", "仙人掌护腿", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/cactus_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> CACTUS_BOOTS = register("cactus_boots", "仙人掌靴子", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/cactus_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> COPPER_HELMET = register("copper_helmet", "铜头盔", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/copper_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> COPPER_CHESTPLATE = register("copper_chestplate", "铜胸甲", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/copper_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> COPPER_LEGGINGS = register("copper_leggings", "铜护腿", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/copper_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> COPPER_BOOTS = register("copper_boots", "铜靴子", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/copper_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> TIN_HELMET = register("tin_helmet", "锡头盔", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/tin_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> TIN_CHESTPLATE = register("tin_chestplate", "锡胸甲", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/tin_armor")
            .durability(190));
    public static final DeferredItem<BaseArmorItem> TIN_LEGGINGS = register("tin_leggings", "锡护腿", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/tin_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> TIN_BOOTS = register("tin_boots", "锡靴子", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/tin_armor")
            .durability(150));

    public static final DeferredItem<BaseArmorItem> PUMPKIN_HELMET = register("pumpkin_helmet", "南瓜头盔", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/pumpkin_armor"));
    public static final DeferredItem<BaseArmorItem> PUMPKIN_CHESTPLATE = register("pumpkin_chestplate", "南瓜胸甲", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/pumpkin_armor"));
    public static final DeferredItem<BaseArmorItem> PUMPKIN_LEGGINGS = register("pumpkin_leggings", "南瓜护腿", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/pumpkin_armor"));
    public static final DeferredItem<BaseArmorItem> PUMPKIN_BOOTS = register("pumpkin_boots", "南瓜靴子", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/pumpkin_armor"));

    public static final DeferredItem<BaseArmorItem> WHITE_PUMPKIN_HELMET = register("white_pumpkin_helmet", "白南瓜头盔", ModArmorMaterials.WHITE_PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/white_pumpkin_armor")
            .attribute(LibAttributes.getSoulDamage(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> WHITE_PUMPKIN_CHESTPLATE = register("white_pumpkin_chestplate", "白南瓜胸甲", ModArmorMaterials.WHITE_PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/white_pumpkin_armor")
            .criticalChance(0.05));
    public static final DeferredItem<BaseArmorItem> WHITE_PUMPKIN_LEGGINGS = register("white_pumpkin_leggings", "白南瓜护腿", ModArmorMaterials.WHITE_PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/white_pumpkin_armor")
            .meleeDamage(0.04));
    public static final DeferredItem<BaseArmorItem> WHITE_PUMPKIN_BOOTS = register("white_pumpkin_boots", "白南瓜靴子", ModArmorMaterials.WHITE_PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/white_pumpkin_armor")
            .meleeDamage(0.04));

    public static final DeferredItem<BaseArmorItem> NINJA_HELMET = register("ninja_helmet", "忍者头盔", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> NINJA_CHESTPLATE = register("ninja_chestplate", "忍者胸甲", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> NINJA_LEGGINGS = register("ninja_leggings", "忍者护腿", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.015));
    public static final DeferredItem<BaseArmorItem> NINJA_BOOTS = register("ninja_boots", "忍者靴", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.015));

    public static final DeferredItem<BaseArmorItem> WOLF_HELMET = register("wolf_helmet", "狼头盔", ModArmorMaterials.WOLF_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/wolf_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> WOLF_CHESTPLATE = register("wolf_chestplate", "狼胸甲", ModArmorMaterials.WOLF_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/wolf_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> WOLF_LEGGINGS = register("wolf_leggings", "狼护腿", ModArmorMaterials.WOLF_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/wolf_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> WOLF_BOOTS = register("wolf_boots", "狼靴子", ModArmorMaterials.WOLF_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/wolf_armor")
            .rarity(ModRarity.BLUE));

    public static final DeferredItem<BaseArmorItem> ROOT_ROT_HELMET = register("root_rot_helmet", "腐根头盔", ModArmorMaterials.ROOT_ROT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/root_rot_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> ROOT_ROT_CHESTPLATE = register("root_rot_chestplate", "腐根胸甲", ModArmorMaterials.ROOT_ROT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/root_rot_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> ROOT_ROT_LEGGINGS = register("root_rot_leggings", "腐根护腿", ModArmorMaterials.ROOT_ROT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/root_rot_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> ROOT_ROT_BOOTS = register("root_rot_boots", "腐根靴子", ModArmorMaterials.ROOT_ROT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/root_rot_armor")
            .rarity(ModRarity.BLUE));

    public static final DeferredItem<BaseArmorItem> BLACK_SPOT_HELMET = register("black_spot_helmet", "黑斑头盔", ModArmorMaterials.BLACK_SPOT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/black_spot_armor")
            .rarity(ModRarity.ORANGE)
            .fiveClassesDamage(0.05));
    public static final DeferredItem<BaseArmorItem> BLACK_SPOT_CHESTPLATE = register("black_spot_chestplate", "黑斑胸甲", ModArmorMaterials.BLACK_SPOT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/black_spot_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.05));
    public static final DeferredItem<BaseArmorItem> BLACK_SPOT_LEGGINGS = register("black_spot_leggings", "黑斑护腿", ModArmorMaterials.BLACK_SPOT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/black_spot_armor")
            .rarity(ModRarity.ORANGE));
    public static final DeferredItem<BaseArmorItem> BLACK_SPOT_BOOTS = register("black_spot_boots", "黑斑靴子", ModArmorMaterials.BLACK_SPOT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/black_spot_armor")
            .rarity(ModRarity.ORANGE)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> ENTERTAINERS_GARB_HAT = register("entertainers_garb_hat", "演艺家帽", ModArmorMaterials.ENTERTAINERS_GARB_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/entertainers_garb_armor")
            .fiveClassesDamage(0.08)
            .rarity(ModRarity.GREEN));
    public static final DeferredItem<BaseArmorItem> ENTERTAINERS_GARB_SHIRT = register("entertainers_garb_shirt", "演艺家上衣", ModArmorMaterials.ENTERTAINERS_GARB_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/entertainers_garb_armor")
            .criticalChance(0.05)
            .rarity(ModRarity.GREEN));
    public static final DeferredItem<BaseArmorItem> ENTERTAINERS_GARB_PANTS = register("entertainers_garb_pants", "演艺家长裤", ModArmorMaterials.ENTERTAINERS_GARB_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/entertainers_garb_armor")
            .criticalChance(0.05)
            .rarity(ModRarity.GREEN));
    public static final DeferredItem<BaseArmorItem> ENTERTAINERS_GARB_SHOES = register("entertainers_garb_shoes", "演艺家鞋", ModArmorMaterials.ENTERTAINERS_GARB_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/entertainers_garb_armor")
            .attribute(Attributes.MOVEMENT_SPEED, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .rarity(ModRarity.GREEN));

    public static final DeferredItem<BaseArmorItem> TROADOUR_HAT = register("troubadour_hat", "吟游诗人帽", ModArmorMaterials.TROADOUR_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/troubadour_armor")
            .fiveClassesDamage(0.14)
            .rarity(ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseArmorItem> TROADOUR_SHIRT = register("troubadour_shirt", "吟游诗人上衣", ModArmorMaterials.TROADOUR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/troubadour_armor")
            .criticalChance(0.08)
            .rarity(ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseArmorItem> TROADOUR_PANTS = register("troubadour_pants", "吟游诗人长裤", ModArmorMaterials.TROADOUR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/troubadour_armor")
            .criticalChance(0.08)
            .rarity(ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseArmorItem> TROADOUR_SHOES = register("troubadour_shoes", "吟游诗人鞋", ModArmorMaterials.TROADOUR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/troubadour_armor")
            .attribute(Attributes.MOVEMENT_SPEED, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .rarity(ModRarity.LIGHT_RED));

    public static final DeferredItem<BaseArmorItem> THIEF_HELMET = register("thief_helmet", "窃贼头盔", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/thief_armor")
            .rarity(ModRarity.GREEN)
            .attribute(Attributes.ATTACK_SPEED, 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .attribute(Attributes.MOVEMENT_SPEED, 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> THIEF_CHESTPLATE = register("thief_chestplate", "窃贼胸甲", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/thief_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.1));
    public static final DeferredItem<BaseArmorItem> THIEF_LEGGINGS = register("thief_leggings", "窃贼护腿", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/thief_armor")
            .rarity(ModRarity.GREEN)
            .attribute(Attributes.ATTACK_SPEED, 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .attribute(Attributes.MOVEMENT_SPEED, 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> THIEF_BOOTS = register("thief_boots", "窃贼靴子", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/thief_armor")
            .rarity(ModRarity.GREEN)
            .criticalChance(0.05));

    public static final DeferredItem<BaseArmorItem> HUNERS_HELMET = register("hunters_helmet", "猎人头盔", ModArmorMaterials.HUNERS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hunters_armor")
            .rarity(ModRarity.BLUE)
            .rangedDamage(0.05));
    public static final DeferredItem<BaseArmorItem> HUNERS_CHESTPLATE = register("hunters_chestplate", "猎人胸甲", ModArmorMaterials.HUNERS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/hunters_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SKIP$CONSUME$AMMO$CHANCE, 0.1F)));
    public static final DeferredItem<BaseArmorItem> HUNERS_LEGGINGS = register("hunters_leggings", "猎人护腿", ModArmorMaterials.HUNERS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/hunters_armor")
            .rarity(ModRarity.BLUE)
            .rangedDamage(0.05));
    public static final DeferredItem<BaseArmorItem> HUNERS_BOOTS = register("hunters_boots", "猎人靴子", ModArmorMaterials.HUNERS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/hunters_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SKIP$CONSUME$AMMO$CHANCE, 0.1F)));

    public static final DeferredItem<BaseArmorItem> SCALE_MAIL_HELMET = register("scale_mail_helmet", "鳞甲头盔", ModArmorMaterials.SCALE_MAIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/scale_mail_armor")
            .rarity(ModRarity.BLUE)
            .meleeDamage(0.05));
    public static final DeferredItem<BaseArmorItem> SCALE_MAIL_CHESTPLATE = register("scale_mail_chestplate", "鳞甲胸甲", ModArmorMaterials.SCALE_MAIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/scale_mail_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.05));
    public static final DeferredItem<BaseArmorItem> SCALE_MAIL_LEGGINGS = register("scale_mail_leggings", "鳞甲护腿", ModArmorMaterials.SCALE_MAIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/scale_mail_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.ATTACK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> SCALE_MAIL_BOOTS = register("scale_mail_boots", "鳞甲靴子", ModArmorMaterials.SCALE_MAIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/scale_mail_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.MOVEMENT_SPEED, 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> HIGHLAND_HELMET = register("highland_helmet", "高地头盔", ModArmorMaterials.HIGHLAND_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/highland_armor")
            .rarity(ModRarity.ORANGE)
            .meleeDamage(0.07));
    public static final DeferredItem<BaseArmorItem> HIGHLAND_CHESTPLATE = register("highland_chestplate", "高地胸甲", ModArmorMaterials.HIGHLAND_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/highland_armor")
            .rarity(ModRarity.BLUE)
            .fiveClassesDamage(0.07));
    public static final DeferredItem<BaseArmorItem> HIGHLAND_LEGGINGS = register("highland_leggings", "高地护腿", ModArmorMaterials.HIGHLAND_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/highland_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.ATTACK_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> HIGHLAND_BOOTS = register("highland_boots", "高地靴子", ModArmorMaterials.HIGHLAND_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/highland_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> REINFORCED_MAIL_HELMET = register("reinforced_mail_helmet", "强化链甲头盔", ModArmorMaterials.REINFORCED_MAIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/reinforced_mail_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> REINFORCED_MAIL_CHESTPLATE = register("reinforced_mail_chestplate", "强化链甲胸甲", ModArmorMaterials.REINFORCED_MAIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/reinforced_mail_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> REINFORCED_MAIL_LEGGINGS = register("reinforced_mail_leggings", "强化链甲护腿", ModArmorMaterials.REINFORCED_MAIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/reinforced_mail_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> REINFORCED_MAIL_BOOTS = register("reinforced_mail_boots", "强化链甲靴子", ModArmorMaterials.REINFORCED_MAIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/reinforced_mail_armor")
            .rarity(ModRarity.BLUE));

    public static final DeferredItem<BaseArmorItem> STALWART_HELMET = register("stalwart_helmet", "坚毅头盔", ModArmorMaterials.STALWART_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/stalwart_armor")
            .rarity(ModRarity.ORANGE));
    public static final DeferredItem<BaseArmorItem> STALWART_CHESTPLATE = register("stalwart_chestplate", "坚毅胸甲", ModArmorMaterials.STALWART_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/stalwart_armor")
            .rarity(ModRarity.ORANGE));
    public static final DeferredItem<BaseArmorItem> STALWART_LEGGINGS = register("stalwart_leggings", "坚毅护腿", ModArmorMaterials.STALWART_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/stalwart_armor")
            .rarity(ModRarity.ORANGE));
    public static final DeferredItem<BaseArmorItem> STALWART_BOOTS = register("stalwart_boots", "坚毅靴子", ModArmorMaterials.STALWART_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/stalwart_armor")
            .rarity(ModRarity.ORANGE));

    public static final DeferredItem<BaseArmorItem> MERCENARY_HELMET = register("mercenary_helmet", "雇佣兵头盔", ModArmorMaterials.MERCENARY_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mercenary_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> MERCENARY_CHESTPLATE = register("mercenary_chestplate", "雇佣兵胸甲", ModArmorMaterials.MERCENARY_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/mercenary_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> MERCENARY_LEGGINGS = register("mercenary_leggings", "雇佣兵护腿", ModArmorMaterials.MERCENARY_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/mercenary_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> MERCENARY_BOOTS = register("mercenary_boots", "雇佣兵靴子", ModArmorMaterials.MERCENARY_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/mercenary_armor")
            .rarity(ModRarity.BLUE));

    public static final DeferredItem<BaseArmorItem> RENEGADE_HELMET = register("renegade_helmet", "叛军头盔", ModArmorMaterials.RENEGADE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/renegade_armor")
            .rarity(ModRarity.ORANGE));
    public static final DeferredItem<BaseArmorItem> RENEGADE_CHESTPLATE = register("renegade_chestplate", "叛军胸甲", ModArmorMaterials.RENEGADE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/renegade_armor")
            .rarity(ModRarity.ORANGE));
    public static final DeferredItem<BaseArmorItem> RENEGADE_LEGGINGS = register("renegade_leggings", "叛军护腿", ModArmorMaterials.RENEGADE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/renegade_armor")
            .rarity(ModRarity.ORANGE));
    public static final DeferredItem<BaseArmorItem> RENEGADE_BOOTS = register("renegade_boots", "叛军靴子", ModArmorMaterials.RENEGADE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/renegade_armor")
            .rarity(ModRarity.ORANGE));

    public static final DeferredItem<BaseArmorItem> CLIMBING_HELMET = register("climbing_helmet", "登山头盔", ModArmorMaterials.CLIMBING_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/climbing_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> CLIMBING_CHESTPLATE = register("climbing_chestplate", "登山胸甲", ModArmorMaterials.CLIMBING_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/climbing_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> CLIMBING_LEGGINGS = register("climbing_leggings", "登山护腿", ModArmorMaterials.CLIMBING_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/climbing_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> CLIMBING_BOOTS = register("climbing_boots", "登山靴子", ModArmorMaterials.CLIMBING_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/climbing_armor")
            .rarity(ModRarity.BLUE));

    public static final DeferredItem<BaseArmorItem> EVOCATION_HAT = register("evocation_hat", "唤魔帽", ModArmorMaterials.EVOCATION_ROBE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/evocation_robe_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> EVOCATION_ROBE = register("evocation_robe", "唤魔长袍", ModArmorMaterials.EVOCATION_ROBE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/evocation_robe_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> EVOCATION_LEGGINGS = register("evocation_leggings", "唤魔护腿", ModArmorMaterials.EVOCATION_ROBE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/evocation_robe_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.MOVEMENT_SPEED, 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> EVOCATION_BOOTS = register("evocation_boots", "唤魔靴子", ModArmorMaterials.EVOCATION_ROBE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/evocation_robe_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.MOVEMENT_SPEED, 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> VERDANT_HAT = register("verdant_hat", "碧绿帽", ModArmorMaterials.VERDANT_ROBE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/verdant_robe_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> VERDANT_ROBE = register("verdant_robe", "碧绿长袍", ModArmorMaterials.VERDANT_ROBE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/verdant_robe_armor")
            .rarity(ModRarity.ORANGE)
            .magicDamage(0.05));
    public static final DeferredItem<BaseArmorItem> VERDANT_LEGGINGS = register("verdant_leggings", "碧绿护腿", ModArmorMaterials.VERDANT_ROBE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/verdant_robe_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.03)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> VERDANT_BOOTS = register("verdant_boots", "碧绿靴子", ModArmorMaterials.VERDANT_ROBE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/verdant_robe_armor")
            .rarity(ModRarity.ORANGE)
            .magicDamage(0.05)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> EMBER_HAT = register("ember_hat", "余烬帽", ModArmorMaterials.EMBER_ROBE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/ember_robe_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.06));
    public static final DeferredItem<BaseArmorItem> EMBER_ROBE = register("ember_robe", "余烬长袍", ModArmorMaterials.EMBER_ROBE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ember_robe_armor")
            .rarity(ModRarity.ORANGE)
            .magicDamage(0.08));
    public static final DeferredItem<BaseArmorItem> EMBER_LEGGINGS = register("ember_leggings", "余烬护腿", ModArmorMaterials.EMBER_ROBE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/ember_robe_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.06)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> EMBER_BOOTS = register("ember_boots", "余烬靴子", ModArmorMaterials.EMBER_ROBE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/ember_robe_armor")
            .rarity(ModRarity.ORANGE)
            .magicDamage(0.08)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> BATTLE_COLLAR = register("battle_collar", "战斗衣领", ModArmorMaterials.BATTLE_ROBE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/battle_robe_armor")
            .rarity(ModRarity.BLUE)
            .meleeDamage(0.07));
    public static final DeferredItem<BaseArmorItem> BATTLE_ROBE = register("battle_robe", "战袍", ModArmorMaterials.BATTLE_ROBE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/battle_robe_armor")
            .rarity(ModRarity.BLUE)
            .meleeDamage(0.07));
    public static final DeferredItem<BaseArmorItem> BATTLE_LEGGINGS = register("battle_leggings", "战斗护腿", ModArmorMaterials.BATTLE_ROBE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/battle_robe_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.ATTACK_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> BATTLE_BOOTS = register("battle_boots", "战斗靴子", ModArmorMaterials.BATTLE_ROBE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/battle_robe_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.ATTACK_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> SPLENDID_COLLAR = register("splendid_collar", "华丽衣领", ModArmorMaterials.SPLENDID_ROBE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/splendid_robe_armor")
            .rarity(ModRarity.ORANGE)
            .meleeDamage(0.07)
            .magicDamage(0.08));
    public static final DeferredItem<BaseArmorItem> SPLENDID_ROBE = register("splendid_robe", "华丽长袍", ModArmorMaterials.SPLENDID_ROBE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/splendid_robe_armor")
            .rarity(ModRarity.ORANGE)
            .meleeDamage(0.07)
            .magicDamage(0.08));
    public static final DeferredItem<BaseArmorItem> SPLENDID_LEGGINGS = register("splendid_leggings", "华丽护腿", ModArmorMaterials.SPLENDID_ROBE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/splendid_robe_armor")
            .rarity(ModRarity.ORANGE)
            .attribute(Attributes.ATTACK_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .criticalChance(0.04));
    public static final DeferredItem<BaseArmorItem> SPLENDID_BOOTS = register("splendid_boots", "华丽靴子", ModArmorMaterials.SPLENDID_ROBE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/splendid_robe_armor")
            .rarity(ModRarity.ORANGE)
            .attribute(Attributes.ATTACK_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .criticalChance(0.04));

    public static final DeferredItem<BaseArmorItem> ARCHERS_HELMET = register("archers_helmet", "弓箭手头盔", ModArmorMaterials.ARCHERS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/archers_armor")
            .rarity(ModRarity.GREEN));
    public static final DeferredItem<BaseArmorItem> ARCHERS_CHESTPLATE = register("archers_chestplate", "弓箭手胸甲", ModArmorMaterials.ARCHERS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/archers_armor")
            .rarity(ModRarity.GREEN)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SKIP$CONSUME$AMMO$CHANCE, 0.1F)));
    public static final DeferredItem<BaseArmorItem> ARCHERS_LEGGINGS = register("archers_leggings", "弓箭手护腿", ModArmorMaterials.ARCHERS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/archers_armor")
            .rarity(ModRarity.GREEN)
            .criticalChance(0.05)
            .attribute(Attributes.ATTACK_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> ARCHERS_BOOTS = register("archers_boots", "弓箭手靴子", ModArmorMaterials.ARCHERS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/archers_armor")
            .rarity(ModRarity.GREEN)
            .criticalChance(0.05)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SKIP$CONSUME$AMMO$CHANCE, 0.1F)));

    public static final DeferredItem<BaseArmorItem> PHANTOM_HELMET = register("phantom_helmet", "幻翼头盔", ModArmorMaterials.PHANTOM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/phantom_armor")
            .rarity(ModRarity.BLUE)
            .attribute(LibAttributes.getSoulDamage(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> PHANTOM_CHESTPLATE = register("phantom_chestplate", "幻翼胸甲", ModArmorMaterials.PHANTOM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/phantom_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> PHANTOM_LEGGINGS = register("phantom_leggings", "幻翼护腿", ModArmorMaterials.PHANTOM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/phantom_armor")
            .rarity(ModRarity.BLUE)
            .rangedDamage(0.03));
    public static final DeferredItem<BaseArmorItem> PHANTOM_BOOTS = register("phantom_boots", "幻翼靴子", ModArmorMaterials.PHANTOM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/phantom_armor")
            .rarity(ModRarity.BLUE)
            .rangedDamage(0.03));

    public static final DeferredItem<BaseArmorItem> HERMIT_HELMET = register("hermit_helmet", "隐匿者头盔", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hermit_armor")
            .rarity(ModRarity.BLUE)
            .attribute(LibAttributes.getSoulDamage(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> HERMIT_CHESTPLATE = register("hermit_chestplate", "隐匿者胸甲", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/hermit_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.08));
    public static final DeferredItem<BaseArmorItem> HERMIT_LEGGINGS = register("hermit_leggings", "隐匿者护腿", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/hermit_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SOUL$DROP, 1.0F)));
    public static final DeferredItem<BaseArmorItem> HERMIT_BOOTS = register("hermit_boots", "隐匿者靴子", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/hermit_armor")
            .rarity(ModRarity.BLUE)
            .attribute(LibAttributes.getSoulDamage(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> BLUE_HERMIT_HELMET = register("blue_hermit_helmet", "蓝隐匿者头盔", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/blue_hermit_armor")
            .rarity(ModRarity.GREEN)
            .fiveClassesDamage(0.04));
    public static final DeferredItem<BaseArmorItem> BLUE_HERMIT_CHESTPLATE = register("blue_hermit_chestplate", "蓝隐匿者胸甲", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/blue_hermit_armor")
            .rarity(ModRarity.GREEN)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> BLUE_HERMIT_LEGGINGS = register("blue_hermit_leggings", "蓝隐匿者护腿", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/blue_hermit_armor")
            .rarity(ModRarity.GREEN)
            .fiveClassesDamage(0.04));
    public static final DeferredItem<BaseArmorItem> BLUE_HERMIT_BOOTS = register("blue_hermit_boots", "蓝隐匿者靴子", ModArmorMaterials.HERMIT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/blue_hermit_armor")
            .rarity(ModRarity.GREEN)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SOUL$DROP, 1.0F)));

    public static final DeferredItem<BaseArmorItem> GUARDS_HELMET = register("guards_helmet", "守卫头盔", ModArmorMaterials.GUARDS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/guards_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.05));
    public static final DeferredItem<BaseArmorItem> GUARDS_CHESTPLATE = register("guards_chestplate", "守卫胸甲", ModArmorMaterials.GUARDS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/guards_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.05));
    public static final DeferredItem<BaseArmorItem> GUARDS_LEGGINGS = register("guards_leggings", "守卫护腿", ModArmorMaterials.GUARDS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/guards_armor")
            .rarity(ModRarity.GREEN)
            .criticalChance(0.04));
    public static final DeferredItem<BaseArmorItem> GUARDS_BOOTS = register("guards_boots", "守卫靴子", ModArmorMaterials.GUARDS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/guards_armor")
            .rarity(ModRarity.GREEN)
            .criticalChance(0.04));

    public static final DeferredItem<BaseArmorItem> SPELUNKER_HELMET = register("spelunker_helmet", "探穴者头盔", ModArmorMaterials.SPELUNKER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/spelunker_armor")
            .rarity(ModRarity.GREEN)
            .meleeDamage(0.07));
    public static final DeferredItem<BaseArmorItem> SPELUNKER_CHESTPLATE = register("spelunker_chestplate", "探穴者胸甲", ModArmorMaterials.SPELUNKER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/spelunker_armor")
            .rarity(ModRarity.GREEN)
            .summonDamage(0.05));
    public static final DeferredItem<BaseArmorItem> SPELUNKER_LEGGINGS = register("spelunker_leggings", "探穴者护腿", ModArmorMaterials.SPELUNKER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/spelunker_armor")
            .rarity(ModRarity.GREEN)
            .attribute(Attributes.ATTACK_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> SPELUNKER_BOOTS = register("spelunker_boots", "探穴者靴子", ModArmorMaterials.SPELUNKER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/spelunker_armor")
            .rarity(ModRarity.GREEN)
            .summonDamage(0.07));

    public static final DeferredItem<BaseArmorItem> LEAD_HELMET = register("lead_helmet", "铅头盔", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/lead_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> LEAD_CHESTPLATE = register("lead_chestplate", "铅胸甲", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/lead_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> LEAD_LEGGINGS = register("lead_leggings", "铅护腿", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/lead_armor")
            .durability(230));
    public static final DeferredItem<BaseArmorItem> LEAD_BOOTS = register("lead_boots", "铅靴子", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/lead_armor")
            .durability(160));

    public static final DeferredItem<BaseArmorItem> SILVER_HELMET = register("silver_helmet", "银头盔", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/silver_armor")
            .durability(190));
    public static final DeferredItem<BaseArmorItem> SILVER_CHESTPLATE = register("silver_chestplate", "银胸甲", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/silver_armor")
            .durability(270));
    public static final DeferredItem<BaseArmorItem> SILVER_LEGGINGS = register("silver_leggings", "银护腿", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/silver_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> SILVER_BOOTS = register("silver_boots", "银靴子", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/silver_armor")
            .durability(180));

    public static final DeferredItem<BaseArmorItem> TUNGSTEN_HELMET = register("tungsten_helmet", "钨头盔", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(210));
    public static final DeferredItem<BaseArmorItem> TUNGSTEN_CHESTPLATE = register("tungsten_chestplate", "钨胸甲", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(290));
    public static final DeferredItem<BaseArmorItem> TUNGSTEN_LEGGINGS = register("tungsten_leggings", "钨护腿", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(270));
    public static final DeferredItem<BaseArmorItem> TUNGSTEN_BOOTS = register("tungsten_boots", "钨靴子", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(200));

    public static final DeferredItem<BaseArmorItem> GOLDEN_HELMET = register("golden_helmet", "金头盔", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/golden_armor")
            .durability(250)
            .setGolden());
    public static final DeferredItem<BaseArmorItem> GOLDEN_CHESTPLATE = register("golden_chestplate", "金胸甲", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/golden_armor")
            .durability(340)
            .setGolden());
    public static final DeferredItem<BaseArmorItem> GOLDEN_LEGGINGS = register("golden_leggings", "金护腿", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/golden_armor")
            .durability(320)
            .setGolden());
    public static final DeferredItem<BaseArmorItem> GOLDEN_BOOTS = register("golden_boots", "金靴子", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/golden_armor")
            .durability(260)
            .setGolden());

    public static final DeferredItem<BaseArmorItem> PLATINUM_HELMET = register("platinum_helmet", "铂金头盔", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/platinum_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> PLATINUM_CHESTPLATE = register("platinum_chestplate", "铂金胸甲", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/platinum_armor")
            .durability(340));
    public static final DeferredItem<BaseArmorItem> PLATINUM_LEGGINGS = register("platinum_leggings", "铂金护腿", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/platinum_armor")
            .durability(320));
    public static final DeferredItem<BaseArmorItem> PLATINUM_BOOTS = register("platinum_boots", "铂金靴子", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/platinum_armor")
            .durability(260));

    public static final DeferredItem<BaseArmorItem> FOSSIL_HELMET = register("fossil_helmet", "化石头盔", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.04));
    public static final DeferredItem<BaseArmorItem> FOSSIL_CHESTPLATE = register("fossil_chestplate", "化石胸甲", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .rangedDamage(0.05));
    public static final DeferredItem<BaseArmorItem> FOSSIL_LEGGINGS = register("fossil_leggings", "化石护腿", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.02));
    public static final DeferredItem<BaseArmorItem> FOSSIL_BOOTS = register("fossil_boots", "化石靴", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.02));

    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_HELMET = register("spore_root_helmet", "孢根头盔", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.02)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_CHESTPLATE = register("spore_root_chestplate", "孢根胸甲", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.03));
    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_LEGGINGS = register("spore_root_leggings", "孢根护腿", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.03));
    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_BOOTS = register("spore_root_boots", "孢根靴", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.02));

    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_HELMET = register("cold_crystal_helmet", "寒晶头盔", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .additionalMana(20)
            .criticalChance(0.04));
    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_CHESTPLATE = register("cold_crystal_chestplate", "寒晶胸甲", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .additionalMana(20)
            .criticalChance(0.04));
    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_LEGGINGS = register("cold_crystal_leggings", "寒晶护腿", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.04));
    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_BOOTS = register("cold_crystal_boots", "寒晶靴", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.04));

    public static final DeferredItem<BaseArmorItem> HEIM_HELMET = register("heim_helmet", "渊鳞头盔", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/heim_armor")
            .rarity(ModRarity.BLUE)
            .meleeDamage(0.03));
    public static final DeferredItem<BaseArmorItem> HEIM_CHESTPLATE = register("heim_chestplate", "渊鳞胸甲", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/heim_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.04));
    public static final DeferredItem<BaseArmorItem> HEIM_LEGGINGS = register("heim_leggings", "渊鳞护腿", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/heim_armor")
            .rarity(ModRarity.BLUE)
            .meleeDamage(0.02)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> HEIM_BOOTS = register("heim_boots", "渊鳞靴", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/heim_armor")
            .rarity(ModRarity.BLUE)
            .attribute(NeoForgeMod.SWIM_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> BEE_HELMET = register("bee_helmet", "蜜蜂头盔", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.04)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseArmorItem> BEE_CHESTPLATE = register("bee_chestplate", "蜜蜂胸甲", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.04)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseArmorItem> BEE_LEGGINGS = register("bee_leggings", "蜜蜂护腿", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.025));
    public static final DeferredItem<BaseArmorItem> BEE_BOOTS = register("bee_boots", "蜜蜂靴", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.025));

    public static final DeferredItem<BaseArmorItem> OBSIDIAN_HELMET = register("obsidian_helmet", "黑曜石头盔", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.08));
    public static final DeferredItem<BaseArmorItem> OBSIDIAN_CHESTPLATE = register("obsidian_chestplate", "黑曜石胸甲", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseArmorItem> OBSIDIAN_LEGGINGS = register("obsidian_leggings", "黑曜石护腿", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.04));
    public static final DeferredItem<BaseArmorItem> OBSIDIAN_BOOTS = register("obsidian_boots", "黑曜石靴子", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.04));

    public static final DeferredItem<BaseArmorItem> GLADIATOR_HELMET = register("gladiator_helmet", "角斗士头盔", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/gladiator_armor"));
    public static final DeferredItem<BaseArmorItem> GLADIATOR_CHESTPLATE = register("gladiator_chestplate", "角斗士胸甲", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/gladiator_armor"));
    public static final DeferredItem<BaseArmorItem> GLADIATOR_LEGGINGS = register("gladiator_leggings", "角斗士护腿", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/gladiator_armor"));
    public static final DeferredItem<BaseArmorItem> GLADIATOR_BOOTS = register("gladiator_boots", "角斗士靴子", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/gladiator_armor"));

    public static final DeferredItem<BaseArmorItem> METEOR_HELMET = register("meteor_helmet", "流星头盔", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.09));
    public static final DeferredItem<BaseArmorItem> METEOR_CHESTPLATE = register("meteor_chestplate", "流星胸甲", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.09));
    public static final DeferredItem<BaseArmorItem> METEOR_LEGGINGS = register("meteor_leggings", "流星护腿", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.045));
    public static final DeferredItem<BaseArmorItem> METEOR_BOOTS = register("meteor_boots", "流星靴子", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.045));

    public static final DeferredItem<BaseArmorItem> JUNGLE_HELMET = register("jungle_helmet", "丛林头盔", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .additionalMana(40)
            .criticalChance(0.06));
    public static final DeferredItem<BaseArmorItem> JUNGLE_CHESTPLATE = register("jungle_chestplate", "丛林胸甲", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .additionalMana(20)
            .magicDamage(0.06));
    public static final DeferredItem<BaseArmorItem> JUNGLE_LEGGINGS = register("jungle_leggings", "丛林护腿", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .additionalMana(20));
    public static final DeferredItem<BaseArmorItem> JUNGLE_BOOTS = register("jungle_boots", "丛林靴子", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.06));

    public static final DeferredItem<BaseArmorItem> NECRO_HELMET = register("necro_helmet", "死灵头盔", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.05));
    public static final DeferredItem<BaseArmorItem> NECRO_CHESTPLATE = register("necro_chestplate", "死灵胸甲", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.05));
    public static final DeferredItem<BaseArmorItem> NECRO_LEGGINGS = register("necro_leggings", "死灵护腿", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.025));
    public static final DeferredItem<BaseArmorItem> NECRO_BOOTS = register("necro_boots", "死灵靴子", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.025));

    public static final DeferredItem<BaseArmorItem> SEEKER_HELLMET = register("seeker_helmet", "逐灵头盔", ModArmorMaterials.SEEKER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/seeker_armor")
            .rarity(ModRarity.GREEN)
            .attribute(LibAttributes.getSoulDamage(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> SEEKER_CHESTPLATE = register("seeker_chestplate", "逐灵胸甲", ModArmorMaterials.SEEKER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/seeker_armor")
            .rarity(ModRarity.GREEN)
            .criticalChance(0.07));
    public static final DeferredItem<BaseArmorItem> SEEKER_LEGGINGS = register("seeker_leggings", "逐灵护腿", ModArmorMaterials.SEEKER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/seeker_armor")
            .rarity(ModRarity.GREEN)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SOUL$DROP, 2.0F)));
    public static final DeferredItem<BaseArmorItem> SEEKER_BOOTS = register("seeker_boots", "逐灵靴子", ModArmorMaterials.SEEKER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/seeker_armor")
            .rarity(ModRarity.GREEN)
            .attribute(LibAttributes.getSoulDamage(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> SHADOW_HELMET = register("shadow_helmet", "暗影头盔", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.05));
    public static final DeferredItem<BaseArmorItem> SHADOW_CHESTPLATE = register("shadow_chestplate", "暗影胸甲", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.05));
    public static final DeferredItem<BaseArmorItem> SHADOW_LEGGINGS = register("shadow_leggings", "暗影护腿", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.025));
    public static final DeferredItem<BaseArmorItem> SHADOW_BOOTS = register("shadow_boots", "暗影靴", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.025));

    public static final DeferredItem<BaseArmorItem> CRIMSON_HELMET = register("crimson_helmet", "猩红头盔", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fiveClassesDamage(0.03));
    public static final DeferredItem<BaseArmorItem> CRIMSON_CHESTPLATE = register("crimson_chestplate", "猩红胸甲", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fiveClassesDamage(0.03));
    public static final DeferredItem<BaseArmorItem> CRIMSON_LEGGINGS = register("crimson_leggings", "猩红护腿", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fiveClassesDamage(0.015));
    public static final DeferredItem<BaseArmorItem> CRIMSON_BOOTS = register("crimson_boots", "猩红靴", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fiveClassesDamage(0.015));

    public static final DeferredItem<BaseArmorItem> MOLTEN_HELMET = register("molten_helmet", "熔岩头盔", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.07));
    public static final DeferredItem<BaseArmorItem> MOLTEN_CHESTPLATE = register("molten_chestplate", "熔岩胸甲", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .meleeDamage(0.07));
    public static final DeferredItem<BaseArmorItem> MOLTEN_LEGGINGS = register("molten_leggings", "熔岩护腿", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .attribute(Attributes.ATTACK_SPEED, 0.035, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> MOLTEN_BOOTS = register("molten_boots", "熔岩靴子", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .attribute(Attributes.ATTACK_SPEED, 0.035, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> PEARL_HELMET = register("pearl_helmet", "珍珠木头盔", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/pearl_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> PEARL_CHESTPLATE = register("pearl_chestplate", "珍珠木胸甲", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/pearl_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> PEARL_LEGGINGS = register("pearl_leggings", "珍珠木护腿", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/pearl_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> PEARL_BOOTS = register("pearl_boots", "珍珠木靴子", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/pearl_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> SPIDER_HELMET = register("spider_helmet", "蜘蛛头盔", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            .summonDamage(0.05));
    public static final DeferredItem<BaseArmorItem> SPIDER_CHESTPLATE = register("spider_chestplate", "蜘蛛胸甲", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            .summonDamage(0.05));
    public static final DeferredItem<BaseArmorItem> SPIDER_LEGGINGS = register("spider_leggings", "蜘蛛护腿", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredItem<BaseArmorItem> SPIDER_BOOTS = register("spider_boots", "蜘蛛靴子", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .summonDamage(0.06));

    public static final DeferredItem<BaseArmorItem> COBALT_MASK = register("cobalt_mask", "钴面具", ModArmorMaterials.COBALT_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.1)
            .criticalChance(0.1));
    public static final DeferredItem<BaseArmorItem> COBALT_HAT = register("cobalt_hat", "钴帽", ModArmorMaterials.COBALT_HAT_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .additionalMana(40)
            .magicDamage(0.1)
            .criticalChance(0.09));
    public static final DeferredItem<BaseArmorItem> COBALT_HELMET = register("cobalt_helmet", "钴头盔", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .meleeDamage(0.15));
    public static final DeferredItem<BaseArmorItem> COBALT_CHESTPLATE = register("cobalt_chestplate", "钴胸甲", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.05));
    public static final DeferredItem<BaseArmorItem> COBALT_LEGGINGS = register("cobalt_leggings", "钴护腿", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> COBALT_BOOTS = register("cobalt_boots", "钴靴子", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fiveClassesDamage(0.03));

    public static final DeferredItem<BaseArmorItem> PALLADIUM_MASK = register("palladium_mask", "钯金面具", ModArmorMaterials.PALLADIUM_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .meleeDamage(0.12)
            .attribute(Attributes.ATTACK_SPEED, 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> PALLADIUM_HEADGEAR = register("palladium_headgear", "钯金头饰", ModArmorMaterials.PALLADIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .magicDamage(0.09)
            .criticalChance(0.09)
            .additionalMana(60));
    public static final DeferredItem<BaseArmorItem> PALLADIUM_HELMET = register("palladium_helmet", "钯金头盔", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .rangedDamage(0.09)
            .criticalChance(0.09));
    public static final DeferredItem<BaseArmorItem> PALLADIUM_CHESTPLATE = register("palladium_chestplate", "钯金胸甲", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .fiveClassesDamage(0.03)
            .criticalChance(0.02));
    public static final DeferredItem<BaseArmorItem> PALLADIUM_LEGGINGS = register("palladium_leggings", "钯金护腿", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .fiveClassesDamage(0.02));
    public static final DeferredItem<BaseArmorItem> PALLADIUM_BOOTS = register("palladium_boots", "钯金靴子", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .criticalChance(0.01));

    public static final DeferredItem<BaseArmorItem> MYTHRIL_HOOD = register("mythril_hood", "秘银兜帽", ModArmorMaterials.MYTHRIL_HOOD_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .additionalMana(60)
            .magicDamage(0.15));
    public static final DeferredItem<BaseArmorItem> MYTHRIL_HAT = register("mythril_hat", "秘银帽", ModArmorMaterials.MYTHRIL_HAT_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.12)
            .criticalChance(0.07));
    public static final DeferredItem<BaseArmorItem> MYTHRIL_HELMET = register("mythril_helmet", "秘银头盔", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.08)
            .meleeDamage(0.1));
    public static final DeferredItem<BaseArmorItem> MYTHRIL_CHESTPLATE = register("mythril_chestplate", "秘银胸甲", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fiveClassesDamage(0.07));
    public static final DeferredItem<BaseArmorItem> MYTHRIL_LEGGINGS = register("mythril_leggings", "秘银护腿", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.05));
    public static final DeferredItem<BaseArmorItem> MYTHRIL_BOOTS = register("mythril_boots", "秘银靴子", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.05));

    public static final DeferredItem<BaseArmorItem> ORICHALCUM_HEADGEAR = register("orichalcum_headgear", "山铜头饰", ModArmorMaterials.ORICHALCUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.18)
            .additionalMana(80));
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_MASK = register("orichalcum_mask", "山铜面具", ModArmorMaterials.ORICHALCUM_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .meleeDamage(0.11)
            .attribute(Attributes.ATTACK_SPEED, 0.11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_HELMET = register("orichalcum_helmet", "山铜头盔", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.15)
            .attribute(Attributes.MOVEMENT_SPEED, 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_CHESTPLATE = register("orichalcum_chestplate", "山铜胸甲", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.06));
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_LEGGINGS = register("orichalcum_leggings", "山铜护腿", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fiveClassesDamage(0.08));
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_BOOTS = register("orichalcum_boots", "山铜靴子", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> ADAMANTITE_HEADGEAR = register("adamantite_headgear", "精金头饰", ModArmorMaterials.ADAMANTITE_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .additionalMana(80)
            .magicDamage(0.12)
            .criticalChance(0.12));
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_MASK = register("adamantite_mask", "精金面具", ModArmorMaterials.ADAMANTITE_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.14)
            .criticalChance(0.1));
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_HELMET = register("adamantite_helmet", "精金头盔", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.07)
            .meleeDamage(0.14));
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_CHESTPLATE = register("adamantite_chestplate", "精金胸甲", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fiveClassesDamage(0.08));
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_LEGGINGS = register("adamantite_leggings", "精金护腿", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.07));
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_BOOTS = register("adamantite_boots", "精金靴子", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> TITANIUM_HEADGEAR = register("titanium_headgear", "钛金头饰", ModArmorMaterials.TITANIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .magicDamage(0.16)
            .criticalChance(0.07)
            .additionalMana(100));
    public static final DeferredItem<BaseArmorItem> TITANIUM_MASK = register("titanium_mask", "钛金面具", ModArmorMaterials.TITANIUM_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .meleeDamage(0.09)
            .criticalChance(0.09)
            .attribute(Attributes.ATTACK_SPEED, 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> TITANIUM_HELMET = register("titanium_helmet", "钛金头盔", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.16)
            .criticalChance(0.07));
    public static final DeferredItem<BaseArmorItem> TITANIUM_CHESTPLATE = register("titanium_chestplate", "钛金胸甲", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fiveClassesDamage(0.04)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> TITANIUM_LEGGINGS = register("titanium_leggings", "钛金护腿", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fiveClassesDamage(0.03)
            .criticalChance(0.03));
    public static final DeferredItem<BaseArmorItem> TITANIUM_BOOTS = register("titanium_boots", "钛金靴子", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_HELMET = register("crystal_assassin_helmet", "水晶刺客头盔", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_CHESTPLATE = register("crystal_assassin_chestplate", "水晶刺客胸甲", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_LEGGINGS = register("crystal_assassin_leggings", "水晶刺客护腿", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_BOOTS = register("crystal_assassin_boots", "水晶刺客靴子", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));

    public static final DeferredItem<BaseArmorItem> HALLOWED_MASK = register("hallowed_mask", "神圣面具", ModArmorMaterials.HALLOWED_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_HEADGEAR = register("hallowed_headgear", "神圣头饰", ModArmorMaterials.HALLOWED_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_HOOD = register("hallowed_hood", "神圣兜帽", ModArmorMaterials.HALLOWED_HOOD_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_HELMET = register("hallowed_helmet", "神圣头盔", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_CHESTPLATE = register("hallowed_chestplate", "神圣胸甲", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_LEGGINGS = register("hallowed_leggings", "神圣护腿", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_BOOTS = register("hallowed_boots", "神圣靴子", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));

    public static final DeferredItem<BaseArmorItem> TIKI_MASK = register("tiki_mask", "提基面具", ModArmorMaterials.TIKI_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/tiki_armor")
            .rarity(ModRarity.LIME)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            .summonDamage(0.10));
    public static final DeferredItem<BaseArmorItem> TIKI_SHIRT = register("tiki_shirt", "提基衣", ModArmorMaterials.TIKI_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/tiki_armor")
            .rarity(ModRarity.LIME)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            .summonDamage(0.10));
    public static final DeferredItem<BaseArmorItem> TIKI_LEGGINGS = register("tiki_leggings", "提基裤", ModArmorMaterials.TIKI_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/tiki_armor")
            .rarity(ModRarity.LIME)
            .attribute(ConfluenceMagicLib.WHIP_RANGE, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredItem<BaseArmorItem> TIKI_BOOTS = register("tiki_boots", "提基靴子", ModArmorMaterials.TIKI_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/tiki_armor")
            .rarity(ModRarity.LIME)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            .summonDamage(0.10));

    public static final DeferredItem<BaseArmorItem> WIZARD_HAT = register("wizard_hat", "巫师帽", ModArmorMaterials.WIZARD_HAT_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/wizard_hat")
            .rarity(ModRarity.GREEN)
            .magicDamage(0.05));
    public static final DeferredItem<BaseArmorItem> MAGIC_HAT = register("magic_hat", "魔法帽", ModArmorMaterials.MAGIC_HAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/magic_hat")
            .rarity(ModRarity.GREEN)
            .magicDamage(0.06)
            .criticalChance(0.06));
    public static final DeferredItem<BaseArmorItem> AMETHYST_ROBE = register("amethyst_robe", "紫晶长袍", ModArmorMaterials.AMETHYST_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/amethyst_robe")
            .rarity(ModRarity.WHITE)
            .additionalMana(20)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.05F)));
    public static final DeferredItem<BaseArmorItem> TOPAZ_ROBE = register("topaz_robe", "黄玉长袍", ModArmorMaterials.TOPAZ_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/topaz_robe")
            .rarity(ModRarity.WHITE)
            .additionalMana(40)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.07F)));
    public static final DeferredItem<BaseArmorItem> SAPPHIRE_ROBE = register("sapphire_robe", "蓝玉长袍", ModArmorMaterials.SAPPHIRE_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/sapphire_robe")
            .rarity(ModRarity.BLUE)
            .additionalMana(40)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.09F)));
    public static final DeferredItem<BaseArmorItem> JADE_ROBE = register("jade_robe", "翡翠长袍", ModArmorMaterials.EMERALD_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/jade_robe")
            .rarity(ModRarity.BLUE)
            .additionalMana(60)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.11F)));
    public static final DeferredItem<BaseArmorItem> RUBY_ROBE = register("ruby_robe", "红玉长袍", ModArmorMaterials.RUBY_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ruby_robe")
            .rarity(ModRarity.BLUE)
            .additionalMana(60)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.13F)));
    public static final DeferredItem<BaseArmorItem> MYSTIC_ROBE = register("mystic_robe", "神秘长袍", ModArmorMaterials.MYSTIC_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/mystic_robe")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.06)
            .criticalChance(0.06)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.1F)));
    public static final DeferredItem<BaseArmorItem> DIAMOND_ROBE = register("diamond_robe", "钻石长袍", ModArmorMaterials.DIAMOND_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/diamond_robe")
            .rarity(ModRarity.GREEN)
            .additionalMana(80)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.15F)));
    public static final DeferredItem<BaseArmorItem> AMBER_ROBE = register("amber_robe", "琥珀长袍", ModArmorMaterials.AMBER_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/amber_robe")
            .rarity(ModRarity.GREEN)
            .additionalMana(60)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.13F)));

    public static final DeferredItem<BaseArmorItem> SOUL_HOOD = register("soul_hood", "灵魂头套", ModArmorMaterials.SOUL_HOOD_ARMOR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/soul_robe")
            .rarity(ModRarity.GREEN)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SOUL$DROP, 1.0F)));
    public static final DeferredItem<BaseArmorItem> SOUL_ROBE = register("soul_robe", "灵魂长袍", ModArmorMaterials.SOUL_ROPE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/soul_robe")
            .rarity(ModRarity.GREEN)
            .attribute(LibAttributes.getSoulDamage(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredItem<BaseArmorItem> SOULDANCER_HOOD = register("souldancer_hood", "魂舞头套", ModArmorMaterials.SOULDANCER_HOOD_ARMOR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/souldancer_robe")
            .rarity(ModRarity.BLUE)
            .attribute(LibAttributes.getSoulDamage(), 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SOUL$DROP, 1.0F)));
    public static final DeferredItem<BaseArmorItem> SOULDANCER_ROBE = register("souldancer_robe", "魂舞长袍", ModArmorMaterials.SOULDANCER_ROPE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/souldancer_robe")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.08)
            .armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SOUL$DROP, 1.0F)));

    public static final DeferredItem<BaseArmorItem> GOGGLES = register("goggles", "护目镜", ModArmorMaterials.GOGGLES_MATERIAL, ArmorItem.Type.HELMET, builder -> {});
    public static final DeferredItem<BaseArmorItem> GREEN_CAP = register("green_cap", "绿帽", ModArmorMaterials.GREEN_CAP_MATERIAL, ArmorItem.Type.HELMET, builder -> {});
    public static final DeferredItem<BaseArmorItem> VIKING_HELMET = register("viking_helmet", "维京海盗头盔", ModArmorMaterials.VIKING_ARMOR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/viking_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> FLINX_FUR_COAT = register("flinx_fur_coat", "小雪怪皮毛外套", ModArmorMaterials.FLINX_FUR_COAT_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/flinx_fur_coat_armor")
            .rarity(ModRarity.GREEN)
            .summonDamage(0.05)
            .attribute(ConfluenceMagicLib.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE));

    public static void register(IEventBus eventBus) {
        ModArmorMaterials.ARMOR_MATERIALS.register(eventBus);
        ITEMS.register(eventBus);
    }

    private static DeferredItem<BaseArmorItem> register(String registryId, String chineseName, Holder<ArmorMaterial> material, ArmorItem.Type type, Consumer<BaseArmorItem.Builder> consumer) {
        DeferredItem<BaseArmorItem> entry = ITEMS.register(registryId, () -> {
            BaseArmorItem.Builder builder = BaseArmorItem.builder(registryId, material, type);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BaseArmorItem> registerWithEnglishName(String registryId, String chineseName, String englishName, Holder<ArmorMaterial> material, ArmorItem.Type type, Consumer<BaseArmorItem.Builder> consumer) {
        DeferredItem<BaseArmorItem> entry = ITEMS.register(registryId, () -> {
            BaseArmorItem.Builder builder = BaseArmorItem.builder(registryId, material, type);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
