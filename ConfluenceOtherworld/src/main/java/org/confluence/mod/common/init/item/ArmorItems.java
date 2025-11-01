package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.armor.ModArmorMaterials;
import org.confluence.mod.common.item.armor.BaseArmorItem;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.init.TEAttributes;

import java.util.function.Consumer;

public class ArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseArmorItem> MINING_HELMET = register("mining_helmet", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(TCItems.LUMINANCE, 10))
            .requiresModLoaded("sodiumdynamiclights")
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> MINING_CHESTPLATE = register("mining_chestplate", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.BLOCK_BREAK_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final DeferredItem<BaseArmorItem> MINING_LEGGINGS = register("mining_leggings", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.BLOCK_BREAK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final DeferredItem<BaseArmorItem> MINING_BOOTS = register("mining_boots", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/mining_armor")
            .rarity(ModRarity.BLUE)
            .attribute(Attributes.BLOCK_BREAK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );

    public static final DeferredItem<BaseArmorItem> PLANK_HELMET = register("plank_helmet", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/plank_armor")
            .durability(55));
    public static final DeferredItem<BaseArmorItem> PLANK_CHESTPLATE = register("plank_chestplate", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/plank_armor")
            .durability(80));
    public static final DeferredItem<BaseArmorItem> PLANK_LEGGINGS = register("plank_leggings", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/plank_armor")
            .durability(75));
    public static final DeferredItem<BaseArmorItem> PLANK_BOOTS = register("plank_boots", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/plank_armor")
            .durability(65));

    public static final DeferredItem<BaseArmorItem> EBONY_HELMET = register("ebony_helmet", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/ebony_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> EBONY_CHESTPLATE = register("ebony_chestplate", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ebony_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> EBONY_LEGGINGS = register("ebony_leggings", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/ebony_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> EBONY_BOOTS = register("ebony_boots", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/ebony_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_HELMET = register("shadow_plank_helmet", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_CHESTPLATE = register("shadow_plank_chestplate", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_LEGGINGS = register("shadow_plank_leggings", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> SHADOW_PLANK_BOOTS = register("shadow_plank_boots", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/shadow_plank_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> ASH_HELMET = register("ash_helmet", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/ash_armor")
            .durability(220));
    public static final DeferredItem<BaseArmorItem> ASH_CHESTPLATE = register("ash_chestplate", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ash_armor")
            .durability(270));
    public static final DeferredItem<BaseArmorItem> ASH_LEGGINGS = register("ash_leggings", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/ash_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> ASH_BOOTS = register("ash_boots", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/ash_armor")
            .durability(230));

    public static final DeferredItem<BaseArmorItem> RAIN_CAP = register("rain_cap", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/raincoat_armor"));
    public static final DeferredItem<BaseArmorItem> RAINCOAT = register("raincoat", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/raincoat_armor"));

    public static final DeferredItem<BaseArmorItem> SNOW_CAPS = register("snow_caps", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/snow_armor"));
    public static final DeferredItem<BaseArmorItem> SNOW_SUITS = register("snow_suits", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/snow_armor"));
    public static final DeferredItem<BaseArmorItem> INSULATED_PANTS = register("insulated_pants", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/snow_armor"));
    public static final DeferredItem<BaseArmorItem> INSULATED_SHOES = register("insulated_shoes", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/snow_armor"));

    public static final DeferredItem<BaseArmorItem> PINK_SNOW_CAPS = register("pink_snow_caps", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/snow_pink_armor"));
    public static final DeferredItem<BaseArmorItem> PINK_SNOW_SUITS = register("pink_snow_suits", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/snow_pink_armor"));
    public static final DeferredItem<BaseArmorItem> PINK_INSULATED_PANTS = register("pink_insulated_pants", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/snow_pink_armor"));
    public static final DeferredItem<BaseArmorItem> PINK_INSULATED_SHOES = register("pink_insulated_shoes", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/snow_pink_armor"));

    public static final DeferredItem<BaseArmorItem> ANGLER_HAT = register("angler_hat", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/angler_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.FISHING$POWER, 5.0F))
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> ANGLER_VEST = register("angler_vest", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/angler_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.FISHING$POWER, 5.0F))
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> ANGLER_PANTS = register("angler_pants", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/angler_armor")
            .rarity(ModRarity.BLUE)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.FISHING$POWER, 5.0F))
            .tooltips(1));

    public static final DeferredItem<BaseArmorItem> CACTUS_HELMET = register("cactus_helmet", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cactus_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> CACTUS_CHESTPLATE = register("cactus_chestplate", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/cactus_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> CACTUS_LEGGINGS = register("cactus_leggings", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/cactus_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> CACTUS_BOOTS = register("cactus_boots", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/cactus_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> COPPER_HELMET = register("copper_helmet", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/copper_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> COPPER_CHESTPLATE = register("copper_chestplate", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/copper_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> COPPER_LEGGINGS = register("copper_leggings", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/copper_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> COPPER_BOOTS = register("copper_boots", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/copper_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> TIN_HELMET = register("tin_helmet", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/tin_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> TIN_CHESTPLATE = register("tin_chestplate", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/tin_armor")
            .durability(190));
    public static final DeferredItem<BaseArmorItem> TIN_LEGGINGS = register("tin_leggings", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/tin_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> TIN_BOOTS = register("tin_boots", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/tin_armor")
            .durability(150));

    public static final DeferredItem<BaseArmorItem> PUMPKIN_HELMET = register("pumpkin_helmet", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/pumpkin_armor"));
    public static final DeferredItem<BaseArmorItem> PUMPKIN_CHESTPLATE = register("pumpkin_chestplate", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/pumpkin_armor"));
    public static final DeferredItem<BaseArmorItem> PUMPKIN_LEGGINGS = register("pumpkin_leggings", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/pumpkin_armor"));
    public static final DeferredItem<BaseArmorItem> PUMPKIN_BOOTS = register("pumpkin_boots", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/pumpkin_armor"));

    public static final DeferredItem<BaseArmorItem> NINJA_HELMET = register("ninja_helmet", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.03)
    );
    public static final DeferredItem<BaseArmorItem> NINJA_CHESTPLATE = register("ninja_chestplate", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.03)
    );
    public static final DeferredItem<BaseArmorItem> NINJA_LEGGINGS = register("ninja_leggings", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.015)
    );
    public static final DeferredItem<BaseArmorItem> NINJA_BOOTS = register("ninja_boots", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/ninja_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.015)
    );

    public static final DeferredItem<BaseArmorItem> LEAD_HELMET = register("lead_helmet", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/lead_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> LEAD_CHESTPLATE = register("lead_chestplate", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/lead_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> LEAD_LEGGINGS = register("lead_leggings", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/lead_armor")
            .durability(230));
    public static final DeferredItem<BaseArmorItem> LEAD_BOOTS = register("lead_boots", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/lead_armor")
            .durability(160));

    public static final DeferredItem<BaseArmorItem> SILVER_HELMET = register("silver_helmet", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/silver_armor")
            .durability(190));
    public static final DeferredItem<BaseArmorItem> SILVER_CHESTPLATE = register("silver_chestplate", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/silver_armor")
            .durability(270));
    public static final DeferredItem<BaseArmorItem> SILVER_LEGGINGS = register("silver_leggings", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/silver_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> SILVER_BOOTS = register("silver_boots", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/silver_armor")
            .durability(180));

    public static final DeferredItem<BaseArmorItem> TUNGSTEN_HELMET = register("tungsten_helmet", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(210));
    public static final DeferredItem<BaseArmorItem> TUNGSTEN_CHESTPLATE = register("tungsten_chestplate", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(290));
    public static final DeferredItem<BaseArmorItem> TUNGSTEN_LEGGINGS = register("tungsten_leggings", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(270));
    public static final DeferredItem<BaseArmorItem> TUNGSTEN_BOOTS = register("tungsten_boots", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/tungsten_armor")
            .durability(200));

    public static final DeferredItem<BaseArmorItem> GOLDEN_HELMET = register("golden_helmet", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/golden_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> GOLDEN_CHESTPLATE = register("golden_chestplate", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/golden_armor")
            .durability(340));
    public static final DeferredItem<BaseArmorItem> GOLDEN_LEGGINGS = register("golden_leggings", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/golden_armor")
            .durability(320));
    public static final DeferredItem<BaseArmorItem> GOLDEN_BOOTS = register("golden_boots", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/golden_armor")
            .durability(260));

    public static final DeferredItem<BaseArmorItem> PLATINUM_HELMET = register("platinum_helmet", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/platinum_armor")
            .durability(250));
    public static final DeferredItem<BaseArmorItem> PLATINUM_CHESTPLATE = register("platinum_chestplate", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/platinum_armor")
            .durability(340));
    public static final DeferredItem<BaseArmorItem> PLATINUM_LEGGINGS = register("platinum_leggings", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/platinum_armor")
            .durability(320));
    public static final DeferredItem<BaseArmorItem> PLATINUM_BOOTS = register("platinum_boots", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/platinum_armor")
            .durability(260));

    public static final DeferredItem<BaseArmorItem> FOSSIL_HELMET = register("fossil_helmet", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.04)
    );
    public static final DeferredItem<BaseArmorItem> FOSSIL_CHESTPLATE = register("fossil_chestplate", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .rangedDamage(0.05)
    );
    public static final DeferredItem<BaseArmorItem> FOSSIL_LEGGINGS = register("fossil_leggings", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.02)
    );
    public static final DeferredItem<BaseArmorItem> FOSSIL_BOOTS = register("fossil_boots", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/fossil_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.02)
    );

    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_HELMET = register("spore_root_helmet", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.02)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_CHESTPLATE = register("spore_root_chestplate", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.03)
    );
    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_LEGGINGS = register("spore_root_leggings", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.03)
    );
    public static final DeferredItem<BaseArmorItem> SPORE_ROOT_BOOTS = register("spore_root_boots", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/spore_root_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.02)
    );

    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_HELMET = register("cold_crystal_helmet", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .additionalMana(20)
            .criticalChance(0.04)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_CHESTPLATE = register("cold_crystal_chestplate", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .additionalMana(20)
            .criticalChance(0.04)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_LEGGINGS = register("cold_crystal_leggings", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.04)
    );
    public static final DeferredItem<BaseArmorItem> COLD_CRYSTAL_BOOTS = register("cold_crystal_boots", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/cold_crystal_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.04)
    );

    public static final DeferredItem<BaseArmorItem> HEIM_HELMET = register("heim_helmet", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/heim_armor")
            .rarity(ModRarity.BLUE)
            // todo延长水下呼吸时间5%
            .meleeDamage(0.03)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> HEIM_CHESTPLATE = register("heim_chestplate", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/heim_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.04)
    );
    public static final DeferredItem<BaseArmorItem> HEIM_LEGGINGS = register("heim_leggings", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/heim_armor")
            .rarity(ModRarity.BLUE)
            .meleeDamage(0.02)
            .criticalChance(0.03)
    );
    public static final DeferredItem<BaseArmorItem> HEIM_BOOTS = register("heim_boots", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/heim_armor")
            // todo提高水下移动速度5%
            .rarity(ModRarity.BLUE)
            .tooltips(1));

    public static final DeferredItem<BaseArmorItem> BEE_HELMET = register("bee_helmet", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.04)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            );
    public static final DeferredItem<BaseArmorItem> BEE_CHESTPLATE = register("bee_chestplate", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.04)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            );
    public static final DeferredItem<BaseArmorItem> BEE_LEGGINGS = register("bee_leggings", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.025)
    );
    public static final DeferredItem<BaseArmorItem> BEE_BOOTS = register("bee_boots", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/bee_armor")
            .rarity(ModRarity.ORANGE)
            .summonDamage(0.025)
    );

    public static final DeferredItem<BaseArmorItem> OBSIDIAN_HELMET = register("obsidian_helmet", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.08)
    );
    public static final DeferredItem<BaseArmorItem> OBSIDIAN_CHESTPLATE = register("obsidian_chestplate", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final DeferredItem<BaseArmorItem> OBSIDIAN_LEGGINGS = register("obsidian_leggings", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.04)
    );
    public static final DeferredItem<BaseArmorItem> OBSIDIAN_BOOTS = register("obsidian_boots", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/obsidian_armor")
            .rarity(ModRarity.BLUE)
            .summonDamage(0.04)
    );

    public static final DeferredItem<BaseArmorItem> GLADIATOR_HELMET = register("gladiator_helmet", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/gladiator_armor"));
    public static final DeferredItem<BaseArmorItem> GLADIATOR_CHESTPLATE = register("gladiator_chestplate", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/gladiator_armor"));
    public static final DeferredItem<BaseArmorItem> GLADIATOR_LEGGINGS = register("gladiator_leggings", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/gladiator_armor"));
    public static final DeferredItem<BaseArmorItem> GLADIATOR_BOOTS = register("gladiator_boots", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/gladiator_armor"));

    public static final DeferredItem<BaseArmorItem> METEOR_HELMET = register("meteor_helmet", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.09)
    );
    public static final DeferredItem<BaseArmorItem> METEOR_CHESTPLATE = register("meteor_chestplate", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.09)
    );
    public static final DeferredItem<BaseArmorItem> METEOR_LEGGINGS = register("meteor_leggings", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.045)
    );
    public static final DeferredItem<BaseArmorItem> METEOR_BOOTS = register("meteor_boots", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/meteor_armor")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.045)
    );

    public static final DeferredItem<BaseArmorItem> JUNGLE_HELMET = register("jungle_helmet", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .additionalMana(40)
            .criticalChance(0.06)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> JUNGLE_CHESTPLATE = register("jungle_chestplate", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .additionalMana(20)
            .magicDamage(0.06)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> JUNGLE_LEGGINGS = register("jungle_leggings", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .additionalMana(20)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> JUNGLE_BOOTS = register("jungle_boots", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/jungle_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.06)
    );

    public static final DeferredItem<BaseArmorItem> NECRO_HELMET = register("necro_helmet", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.05)
    );
    public static final DeferredItem<BaseArmorItem> NECRO_CHESTPLATE = register("necro_chestplate", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.05)
    );
    public static final DeferredItem<BaseArmorItem> NECRO_LEGGINGS = register("necro_leggings", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.025)
    );
    public static final DeferredItem<BaseArmorItem> NECRO_BOOTS = register("necro_boots", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/necro_armor")
            .rarity(ModRarity.GREEN)
            .rangedDamage(0.025)
    );

    public static final DeferredItem<BaseArmorItem> SHADOW_HELMET = register("shadow_helmet", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.05)
    );
    public static final DeferredItem<BaseArmorItem> SHADOW_CHESTPLATE = register("shadow_chestplate", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.05)
    );
    public static final DeferredItem<BaseArmorItem> SHADOW_LEGGINGS = register("shadow_leggings", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.025)
    );
    public static final DeferredItem<BaseArmorItem> SHADOW_BOOTS = register("shadow_boots", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/shadow_armor")
            .rarity(ModRarity.BLUE)
            .criticalChance(0.025)
    );

    public static final DeferredItem<BaseArmorItem> CRIMSON_HELMET = register("crimson_helmet", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fourClassesDamage(0.03)
    );
    public static final DeferredItem<BaseArmorItem> CRIMSON_CHESTPLATE = register("crimson_chestplate", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fourClassesDamage(0.03)
    );
    public static final DeferredItem<BaseArmorItem> CRIMSON_LEGGINGS = register("crimson_leggings", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fourClassesDamage(0.015)
    );
    public static final DeferredItem<BaseArmorItem> CRIMSON_BOOTS = register("crimson_boots", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/crimson_armor")
            .rarity(ModRarity.BLUE)
            .fourClassesDamage(0.015)
    );

    public static final DeferredItem<BaseArmorItem> MOLTEN_HELMET = register("molten_helmet", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .criticalChance(0.07)
    );
    public static final DeferredItem<BaseArmorItem> MOLTEN_CHESTPLATE = register("molten_chestplate", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .meleeDamage(0.07)
    );
    public static final DeferredItem<BaseArmorItem> MOLTEN_LEGGINGS = register("molten_leggings", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .attribute(Attributes.ATTACK_SPEED, 0.035, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredItem<BaseArmorItem> MOLTEN_BOOTS = register("molten_boots", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/molten_armor")
            .rarity(ModRarity.ORANGE)
            .attribute(Attributes.ATTACK_SPEED, 0.035, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static final DeferredItem<BaseArmorItem> PEARL_HELMET = register("pearl_helmet", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/pearl_armor")
            .durability(120));
    public static final DeferredItem<BaseArmorItem> PEARL_CHESTPLATE = register("pearl_chestplate", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/pearl_armor")
            .durability(170));
    public static final DeferredItem<BaseArmorItem> PEARL_LEGGINGS = register("pearl_leggings", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/pearl_armor")
            .durability(150));
    public static final DeferredItem<BaseArmorItem> PEARL_BOOTS = register("pearl_boots", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/pearl_armor")
            .durability(130));

    public static final DeferredItem<BaseArmorItem> SPIDER_HELMET = register("spider_helmet", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            .summonDamage(0.05)
    );
    public static final DeferredItem<BaseArmorItem> SPIDER_CHESTPLATE = register("spider_chestplate", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
            .summonDamage(0.05)
    );
    public static final DeferredItem<BaseArmorItem> SPIDER_LEGGINGS = register("spider_leggings", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final DeferredItem<BaseArmorItem> SPIDER_BOOTS = register("spider_boots", ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/spider_armor")
            .rarity(ModRarity.LIGHT_RED)
            .summonDamage(0.06)
    );

    public static final DeferredItem<BaseArmorItem> COBALT_MASK = register("cobalt_mask", ModArmorMaterials.COBALT_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.1)
            .criticalChance(0.1)
    );
    public static final DeferredItem<BaseArmorItem> COBALT_HAT = register("cobalt_hat", ModArmorMaterials.COBALT_HAT_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .additionalMana(40)
            .magicDamage(0.1)
            .criticalChance(0.09)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> COBALT_HELMET = register("cobalt_helmet", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .meleeDamage(0.15)
    );
    public static final DeferredItem<BaseArmorItem> COBALT_CHESTPLATE = register("cobalt_chestplate", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.05)
    );
    public static final DeferredItem<BaseArmorItem> COBALT_LEGGINGS = register("cobalt_leggings", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredItem<BaseArmorItem> COBALT_BOOTS = register("cobalt_boots", ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/cobalt_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fourClassesDamage(0.03)
    );

    public static final DeferredItem<BaseArmorItem> PALLADIUM_MASK = register("palladium_mask", ModArmorMaterials.PALLADIUM_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .meleeDamage(0.12)
            .attribute(Attributes.ATTACK_SPEED, 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredItem<BaseArmorItem> PALLADIUM_HEADGEAR = register("palladium_headgear", ModArmorMaterials.PALLADIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .magicDamage(0.09)
            .criticalChance(0.09)
            .additionalMana(60)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> PALLADIUM_HELMET = register("palladium_helmet", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .rangedDamage(0.09)
            .criticalChance(0.09)
    );
    public static final DeferredItem<BaseArmorItem> PALLADIUM_CHESTPLATE = register("palladium_chestplate", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .fourClassesDamage(0.03)
            .criticalChance(0.02)
    );
    public static final DeferredItem<BaseArmorItem> PALLADIUM_LEGGINGS = register("palladium_leggings", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .fourClassesDamage(0.02)
    );
    public static final DeferredItem<BaseArmorItem> PALLADIUM_BOOTS = register("palladium_boots", ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/palladium_armor", true)
            .rarity(ModRarity.PINK)
            .criticalChance(0.01)
    );

    public static final DeferredItem<BaseArmorItem> MYTHRIL_HOOD = register("mythril_hood", ModArmorMaterials.MYTHRIL_HOOD_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .additionalMana(60)
            .magicDamage(0.15)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> MYTHRIL_HAT = register("mythril_hat", ModArmorMaterials.MYTHRIL_HAT_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.12)
            .criticalChance(0.07)
    );
    public static final DeferredItem<BaseArmorItem> MYTHRIL_HELMET = register("mythril_helmet", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.08)
            .meleeDamage(0.1)
    );
    public static final DeferredItem<BaseArmorItem> MYTHRIL_CHESTPLATE = register("mythril_chestplate", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fourClassesDamage(0.07)
    );
    public static final DeferredItem<BaseArmorItem> MYTHRIL_LEGGINGS = register("mythril_leggings", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.05)
    );
    public static final DeferredItem<BaseArmorItem> MYTHRIL_BOOTS = register("mythril_boots", ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/mythril_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.05)
    );

    public static final DeferredItem<BaseArmorItem> ORICHALCUM_HEADGEAR = register("orichalcum_headgear", ModArmorMaterials.ORICHALCUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.18)
            .additionalMana(80)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_MASK = register("orichalcum_mask", ModArmorMaterials.ORICHALCUM_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .meleeDamage(0.11)
            .attribute(Attributes.ATTACK_SPEED, 0.11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .attribute(Attributes.MOVEMENT_SPEED, 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_HELMET = register("orichalcum_helmet", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.15)
            .attribute(Attributes.MOVEMENT_SPEED, 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_CHESTPLATE = register("orichalcum_chestplate", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.06)
    );
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_LEGGINGS = register("orichalcum_leggings", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fourClassesDamage(0.08)
    );
    public static final DeferredItem<BaseArmorItem> ORICHALCUM_BOOTS = register("orichalcum_boots", ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/orichalcum_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static final DeferredItem<BaseArmorItem> ADAMANTITE_HEADGEAR = register("adamantite_headgear", ModArmorMaterials.ADAMANTITE_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .additionalMana(80)
            .magicDamage(0.12)
            .criticalChance(0.12)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_MASK = register("adamantite_mask", ModArmorMaterials.ADAMANTITE_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.14)
            .criticalChance(0.1)
    );
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_HELMET = register("adamantite_helmet", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.07)
            .meleeDamage(0.14)
    );
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_CHESTPLATE = register("adamantite_chestplate", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fourClassesDamage(0.08)
    );
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_LEGGINGS = register("adamantite_leggings", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .criticalChance(0.07)
    );
    public static final DeferredItem<BaseArmorItem> ADAMANTITE_BOOTS = register("adamantite_boots", ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/adamantite_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static final DeferredItem<BaseArmorItem> TITANIUM_HEADGEAR = register("titanium_headgear", ModArmorMaterials.TITANIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .magicDamage(0.16)
            .criticalChance(0.07)
            .additionalMana(100)
            .tooltips(1));
    public static final DeferredItem<BaseArmorItem> TITANIUM_MASK = register("titanium_mask", ModArmorMaterials.TITANIUM_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .meleeDamage(0.09)
            .criticalChance(0.09)
            .attribute(Attributes.ATTACK_SPEED, 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredItem<BaseArmorItem> TITANIUM_HELMET = register("titanium_helmet", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .rangedDamage(0.16)
            .criticalChance(0.07)
    );
    public static final DeferredItem<BaseArmorItem> TITANIUM_CHESTPLATE = register("titanium_chestplate", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fourClassesDamage(0.04)
            .criticalChance(0.03)
    );
    public static final DeferredItem<BaseArmorItem> TITANIUM_LEGGINGS = register("titanium_leggings", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .fourClassesDamage(0.03)
            .criticalChance(0.03)
    );
    public static final DeferredItem<BaseArmorItem> TITANIUM_BOOTS = register("titanium_boots", ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/titanium_armor", true)
            .rarity(ModRarity.LIGHT_RED)
            .attribute(Attributes.MOVEMENT_SPEED, 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_HELMET = register("crystal_assassin_helmet", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_CHESTPLATE = register("crystal_assassin_chestplate", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_LEGGINGS = register("crystal_assassin_leggings", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> CRYSTAL_ASSASSIN_BOOTS = register("crystal_assassin_boots", ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/crystal_assassin_armor")
            .rarity(ModRarity.PINK));

    public static final DeferredItem<BaseArmorItem> HALLOWED_MASK = register("hallowed_mask", ModArmorMaterials.HALLOWED_MASK_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_HEADGEAR = register("hallowed_headgear", ModArmorMaterials.HALLOWED_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_HOOD = register("hallowed_hood", ModArmorMaterials.HALLOWED_HOOD_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_HELMET = register("hallowed_helmet", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_CHESTPLATE = register("hallowed_chestplate", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_LEGGINGS = register("hallowed_leggings", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));
    public static final DeferredItem<BaseArmorItem> HALLOWED_BOOTS = register("hallowed_boots", ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, builder -> builder
            .geo("armor/hallowed_armor", true)
            .rarity(ModRarity.PINK));

    public static final DeferredItem<BaseArmorItem> WIZARD_HAT = register("wizard_hat", ModArmorMaterials.WIZARD_HAT_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/wizard_hat")
            .rarity(ModRarity.GREEN)
            .magicDamage(0.05)
    );
    public static final DeferredItem<BaseArmorItem> MAGIC_HAT = register("magic_hat", ModArmorMaterials.MAGIC_HAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/magic_hat")
            .rarity(ModRarity.GREEN)
            .magicDamage(0.06)
            .criticalChance(0.06)
    );
    public static final DeferredItem<BaseArmorItem> AMETHYST_ROBE = register("amethyst_robe", ModArmorMaterials.AMETHYST_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/amethyst_robe")
            .rarity(ModRarity.WHITE)
            .additionalMana(20)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.05F))
            .tooltips(2));
    public static final DeferredItem<BaseArmorItem> TOPAZ_ROBE = register("topaz_robe", ModArmorMaterials.TOPAZ_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/topaz_robe")
            .rarity(ModRarity.WHITE)
            .additionalMana(40)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.07F))
            .tooltips(2));
    public static final DeferredItem<BaseArmorItem> SAPPHIRE_ROBE = register("sapphire_robe", ModArmorMaterials.SAPPHIRE_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/sapphire_robe")
            .rarity(ModRarity.BLUE)
            .additionalMana(40)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.09F))
            .tooltips(2));
    public static final DeferredItem<BaseArmorItem> JADE_ROBE = register("jade_robe", ModArmorMaterials.EMERALD_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/jade_robe")
            .rarity(ModRarity.BLUE)
            .additionalMana(60)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.11F))
            .tooltips(2));
    public static final DeferredItem<BaseArmorItem> RUBY_ROBE = register("ruby_robe", ModArmorMaterials.RUBY_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/ruby_robe")
            .rarity(ModRarity.BLUE)
            .additionalMana(60)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.13F))
            .tooltips(2));
    public static final DeferredItem<BaseArmorItem> MYSTIC_ROBE = register("mystic_robe", ModArmorMaterials.MYSTIC_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/mystic_robe")
            .rarity(ModRarity.BLUE)
            .magicDamage(0.06)
            .criticalChance(0.06)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.1F))
    );
    public static final DeferredItem<BaseArmorItem> DIAMOND_ROBE = register("diamond_robe", ModArmorMaterials.DIAMOND_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/diamond_robe")
            .rarity(ModRarity.GREEN)
            .additionalMana(80)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.15F))
            .tooltips(2));
    public static final DeferredItem<BaseArmorItem> AMBER_ROBE = register("amber_robe", ModArmorMaterials.AMBER_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/amber_robe")
            .rarity(ModRarity.GREEN)
            .additionalMana(60)
            .armorBonus(PrimitiveValueComponent.of(AccessoryItems.MANA$USE$REDUCE, 0.13F))
            .tooltips(2));

    public static final DeferredItem<BaseArmorItem> GOGGLES = register("goggles", ModArmorMaterials.GOGGLES_MATERIAL, ArmorItem.Type.HELMET, builder -> {
    });
    public static final DeferredItem<BaseArmorItem> GREEN_CAP = register("green_cap", ModArmorMaterials.GREEN_CAP_MATERIAL, ArmorItem.Type.HELMET, builder -> {
    });
    public static final DeferredItem<BaseArmorItem> VIKING_HELMET = register("viking_helmet", ModArmorMaterials.VIKING_ARMOR_MATERIAL, ArmorItem.Type.HELMET, builder -> builder
            .geo("armor/viking_armor")
            .rarity(ModRarity.BLUE));
    public static final DeferredItem<BaseArmorItem> FLINX_FUR_COAT = register("flinx_fur_coat", ModArmorMaterials.FLINX_FUR_COAT_MATERIAL, ArmorItem.Type.CHESTPLATE, builder -> builder
            .geo("armor/flinx_fur_coat_armor")
            .rarity(ModRarity.GREEN)
            .summonDamage(0.05)
            .attribute(TEAttributes.MINION_CAPACITY, 1, AttributeModifier.Operation.ADD_VALUE)
    );

    private static DeferredItem<BaseArmorItem> register(String name, Holder<ArmorMaterial> material, ArmorItem.Type type, Consumer<BaseArmorItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseArmorItem.Builder builder = BaseArmorItem.builder(name, material, type);
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static void register(IEventBus eventBus) {
        ModArmorMaterials.ARMOR_MATERIALS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
