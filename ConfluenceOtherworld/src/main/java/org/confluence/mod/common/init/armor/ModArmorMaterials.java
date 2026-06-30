package org.confluence.mod.common.init.armor;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.mesdag.portlib.diff.Diff;
import org.mesdag.portlib.registries.PortArmorMaterialRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;
import org.mesdag.portlib.wrapper.sounds.SoundEventHolder;
import org.mesdag.portlib.wrapper.world.item.PortArmorMaterial;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ModArmorMaterials {
    public static void init() {}

    public static final PortArmorMaterialRegistration ARMOR_MATERIALS = PortRegisterHandler.armorMaterial(Confluence.MODID);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> VANITY_ARMOR_MATERIALS = ARMOR_MATERIALS.register("vanity_armor_materials", () -> new PortArmorMaterial(
            Map.of(), 0, SoundEventHolder.wrap(SoundEvents.ARMOR_EQUIP_LEATHER), () -> Ingredient.EMPTY,
            List.of(new PortArmorMaterial.Layer(Confluence.asResource("vanity_armor"))), 0.0F, 0.0F)
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> CACTUS_ARMOR_MATERIALS = registerArmorMaterial("cactus_armor_materials",
            1, 2, 2, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, Items.CACTUS, "cactus", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> MINING_ARMOR_MATERIALS = registerArmorMaterial("mining_armor_materials",
            2, 3, 2, 1,
            9, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.SILK, "mining", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ANGLER_ARMOR_MATERIALS = registerArmorMaterial("angler_armor_materials",
            1, 1, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.SILK, "angler", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> EBONY_ARMOR_MATERIALS = registerArmorMaterial("ebony_armor_materials",
            1, 2, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.EBONY_LOG_BLOCKS.PLANKS, "ebony", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SHADOW_PLANK_ARMOR_MATERIALS = registerArmorMaterial("shadow_plank_armor_materials",
            1, 2, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS, "shadow_plank", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PEARL_ARMOR_MATERIALS = registerArmorMaterial("pearl_armor_materials",
            1, 2, 2, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.PEARL_LOG_BLOCKS.PLANKS,
            "pearl", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ASH_ARMOR_MATERIALS = registerArmorMaterial("ash_armor_materials",
            2, 2, 2, 2,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.ASH_LOG_BLOCKS.PLANKS, "pearl", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PUMPKIN_ARMOR_MATERIALS = registerArmorMaterial("pumpkin_armor_materials",
            2, 2, 2, 2,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, Blocks.PUMPKIN, "pearl", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> RAINCOAT_ARMOR_MATERIALS = registerArmorMaterial("raincoat_armor_materials",
            1, 2, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_LEATHER, MaterialItems.GEL, "raincoat", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SNOW_ARMOR_MATERIALS = registerArmorMaterial("snow_armor_materials",
            2, 2, 2, 2,
            9, SoundEvents.ARMOR_EQUIP_LEATHER, Items.BLUE_WOOL, "snow", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PINK_SNOW_ARMOR_MATERIALS = registerArmorMaterial("pink_snow_armor_materials",
            2, 2, 2, 2,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, Items.PINK_WOOL, "pink_snow", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PLANK_ARMOR_MATERIALS = registerArmorMaterial("plank_armor_materials",
            1, 1, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, Items.OAK_PLANKS, "plank", 0.0F,
            0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> COPPER_ARMOR_MATERIALS = registerArmorMaterial("copper_armor_materials",
            2, 3, 3, 2,
            9, SoundEvents.ARMOR_EQUIP_IRON, Items.COPPER_INGOT,
            "copper", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> TIN_ARMOR_MATERIALS = registerArmorMaterial("tin_armor_materials",
            2, 3, 3, 2,
            9, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.TIN_INGOT,
            "tin", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> LEAD_ARMOR_MATERIALS = registerArmorMaterial("lead_armor_materials",
            2, 5, 5, 2,
            9, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.LEAD_INGOT,
            "lead", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SILVER_ARMOR_MATERIALS = registerArmorMaterial("silver_armor_materials",
            2, 6, 5, 2,
            10, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.SILVER_INGOT,
            "silver", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> TUNGSTEN_ARMOR_MATERIALS = registerArmorMaterial("tungsten_armor_materials",
            2, 6, 5, 2,
            10, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.TUNGSTEN_INGOT,
            "tungsten", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> GOLDEN_ARMOR_MATERIALS = registerArmorMaterial("golden_armor_materials",
            3, 6, 6, 3,
            10, SoundEvents.ARMOR_EQUIP_GOLD, Items.GOLD_INGOT,
            "golden", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PLATINUM_ARMOR_MATERIALS = registerArmorMaterial("platinum_armor_materials",
            3, 6, 6, 3,
            10, SoundEvents.ARMOR_EQUIP_GOLD, MaterialItems.PLATINUM_INGOT,
            "platinum", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> FOSSIL_ARMOR_MATERIALS = registerArmorMaterial("fossil_armor_materials",
            2, 6, 6, 2,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.STURDY_FOSSIL,
            "fossil", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> BEE_ARMOR_MATERIALS = registerArmorMaterial("bee_armor_materials",
            3, 5, 5, 3,
            14, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.ROYAL_WAX,
            "bee", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> NINJA_ARMOR_MATERIALS = registerArmorMaterial("ninja_armor_materials",
            2, 5, 5, 2,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, MaterialItems.BLACK_INK,
            "ninja", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> THIEF_CHESTPLATE = registerArmorMaterial("thief_chestplate",
            3, 5, 5, 3,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, MaterialItems.BLACK_INK,
            "ninja", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> HUNERS_ARMOR_MATERIALS = registerArmorMaterial("hunters_armor_materials",
            3, 5, 5, 2,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.LEATHER,
            "hunters", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SCALE_MAIL_ARMOR_MATERIALS = registerArmorMaterial("scale_mail_armor_materials",
            3, 5, 5, 2,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.LEATHER,
            "scale_mail", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PHANTOM_ARMOR_MATERIALS = registerArmorMaterial("phantom_armor_materials",
            3, 4, 4, 3,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.LEATHER,
            "hermit", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> HERMIT_ARMOR_MATERIALS = registerArmorMaterial("hermit_armormaterials",
            2, 4, 4, 2,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.LEATHER,
            "hermit", 0.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> REINFORCED_MAIL_ARMOR_MATERIALS = registerArmorMaterial("reinforced_mail_armor_materials",
            4, 5, 5, 4,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.IRON_INGOT,
            "reinforced_mail", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> CLIMBING_ARMOR_MATERIALS = registerArmorMaterial("climbing_armor_materials",
            3, 5, 5, 3,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.IRON_INGOT,
            "climbing", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> BATTLE_ROBE_ARMOR_MATERIALS = registerArmorMaterial("battle_robe_armor_materials",
            3, 4, 4, 3,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.IRON_INGOT,
            "battle_robe", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ARCHERS_ARMOR_MATERIALS = registerArmorMaterial("archers_armor_materials",
            3, 5, 5, 3,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.IRON_INGOT,
            "archers", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SPLENDID_ROBE_ARMOR_MATERIALS = registerArmorMaterial("splendid_robe_armor_materials",
            4, 5, 5, 4,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.IRON_INGOT,
            "splendid_robe", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> GUARDS_ARMOR_MATERIALS = registerArmorMaterial("guards_armor_materials",
            3, 5, 5, 3,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.LEATHER,
            "guards", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SPELUNKER_ARMOR_MATERIALS = registerArmorMaterial("spelunker_armor_materials",
            3, 5, 5, 3,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, Items.LEATHER,
            "spelunker", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> OBSIDIAN_ARMOR_MATERIALS = registerArmorMaterial("obsidian_armor_materials",
            3, 6, 5, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, Blocks.OBSIDIAN,
            "obsidian", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> GLADIATOR_ARMOR_MATERIALS = registerArmorMaterial("gladiator_armor_materials",
            3, 6, 6, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, Items.GOLD_INGOT,
            "gladiator", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> WHITE_PUMPKIN_ARMOR_MATERIALS = registerArmorMaterial("white_pumpkin_armor_materials",
            3, 5, 5, 3,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.WHITE_PUMPKIN,
            "white_pumpkin", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> METEOR_ARMOR_MATERIALS = registerArmorMaterial("meteor_armor_materials",
            3, 6, 6, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, MaterialItems.METEORITE_INGOT,
            "meteor", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SPORE_ROOT_ARMOR_MATERIALS = registerArmorMaterial("spore_root_armor_materials",
            2, 4, 4, 2,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.SPORE_ROOT,
            "spore_root", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> COLD_CRYSTAL_ARMOR_MATERIALS = registerArmorMaterial("cold_crystal_armor_materials",
            2, 5, 5, 2,
            11, SoundEvents.ARMOR_EQUIP_DIAMOND, MaterialItems.COLD_CRYSTAL,
            "cold_crystal", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> HEIM_ARMOR_MATERIALS = registerArmorMaterial("heim_armor_materials",
            3, 6, 6, 3,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.HEIM,
            "heim", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SHADOW_ARMOR_MATERIALS = registerArmorMaterial("shadow_armor_materials",
            3, 7, 6, 3,
            12, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.DEMONITE_INGOT,
            "shadow", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> CRIMSON_ARMOR_MATERIALS = registerArmorMaterial("crimson_armor_materials",
            3, 7, 6, 3,
            12, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.CRIMTANE_INGOT,
            "crimson", 2.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> MOLTEN_ARMOR_MATERIALS = registerArmorMaterial("molten_armor_materials",
            5, 7, 7, 6,
            15, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.HELLSTONE_INGOT,
            "molten", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> NECRO_ARMOR_MATERIALS = registerArmorMaterial("necro_armor_materials",
            3, 7, 6, 3,
            16, SoundEvents.ARMOR_EQUIP_LEATHER, ConsumableItems.DUNGEON_DEMON_BONE,
            "necro", 2.0F, 0.0F);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SEEKER_ARMOR_MATERIALS = registerArmorMaterial("seeker_armor_materials",
            3, 6, 6, 3,
            16, SoundEvents.ARMOR_EQUIP_LEATHER, ConsumableItems.DUNGEON_DEMON_BONE,
            "seeker", 2.0F, 0.0F);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SPIDER_ARMOR_MATERIALS = registerArmorMaterial("spider_armor_materials",
            3, 7, 7, 3,
            17, SoundEvents.ARMOR_EQUIP_LEATHER, Items.BONE,
            "spider", 2.0F, 0.0F);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> TIKI_ARMOR_MATERIALS = registerArmorMaterial("tiki_armor_materials",
            3, 6, 5, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, Blocks.OBSIDIAN,
            "obsidian", 1.0F, 0.0F
    );
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> GOGGLES_MATERIAL = registerSingletonMaterial("goggles_material", ArmorItem.Type.HELMET, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "goggles", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> GREEN_CAP_MATERIAL = registerSingletonMaterial("green_cap_material", ArmorItem.Type.HELMET, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "green_cap", 0, 0);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> WIZARD_HAT_MATERIAL = registerSingletonMaterial("wizard_hat_material", ArmorItem.Type.HELMET, 4, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "wizard_hat", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> MAGIC_HAT_ARMOR_MATERIAL = registerSingletonMaterial("magic_hat_material", ArmorItem.Type.HELMET, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "magic_hat", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> VIKING_ARMOR_MATERIAL = registerSingletonMaterial("viking_helmet_material", ArmorItem.Type.HELMET, 4, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "viking_helmet", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> FLINX_FUR_COAT_MATERIAL = registerSingletonMaterial("flinx_fur_coat_material", ArmorItem.Type.CHESTPLATE, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "flinx_fur_coat", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> AMETHYST_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("amethyst_robe_material", ArmorItem.Type.CHESTPLATE, 0, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.AMETHYST), "amethyst_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> TOPAZ_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("topaz_robe_material", ArmorItem.Type.CHESTPLATE, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.TOPAZ), "topaz_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SAPPHIRE_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("sapphire_robe_material", ArmorItem.Type.CHESTPLATE, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.SAPPHIRE), "sapphire_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> EMERALD_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("emerald_robe_material", ArmorItem.Type.CHESTPLATE, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.JADE), "emerald_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> RUBY_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("ruby_robe_material", ArmorItem.Type.CHESTPLATE, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.RUBY), "ruby_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> MYSTIC_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("mystic_robe_material", ArmorItem.Type.CHESTPLATE, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "mystic_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> DIAMOND_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("diamond_robe_material", ArmorItem.Type.CHESTPLATE, 3, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.DIAMOND), "diamond_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> AMBER_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("amber_robe_material", ArmorItem.Type.CHESTPLATE, 3, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.AMBER), "amber_robe", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SOUL_HOOD_ARMOR_MATERIAL = registerSingletonMaterial("soul_hood_material", ArmorItem.Type.HELMET, 3, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.LEATHER), "soul_hood", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SOUL_ROPE_ARMOR_MATERIAL = registerSingletonMaterial("soul_rope_material", ArmorItem.Type.CHESTPLATE, 5, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.LEATHER), "soul_rope", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SOULDANCER_HOOD_ARMOR_MATERIAL = registerSingletonMaterial("souldancer_hood_material", ArmorItem.Type.HELMET, 3, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.LEATHER), "souldancer_hood", 0, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> SOULDANCER_ROPE_ARMOR_MATERIAL = registerSingletonMaterial("souldancer_rope_material", ArmorItem.Type.CHESTPLATE, 5, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.LEATHER), "souldancer_rope", 0, 0);


    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> JUNGLE_ARMOR_MATERIALS = registerArmorMaterial("jungle_armor_materials", 3, 6, 6, 3, 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.JUNGLE_SPORE), "jungle", 2, 0);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> COBALT_MASK_MATERIAL = registerSingletonMaterial("cobalt_mask_material", ArmorItem.Type.HELMET, 3, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.COBALT_INGOT), "cobalt_mask", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> COBALT_HAT_MATERIAL = registerSingletonMaterial("cobalt_hat_material", ArmorItem.Type.HELMET, 1, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.COBALT_INGOT), "cobalt_hat", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> COBALT_ARMOR_MATERIALS = registerArmorMaterial("cobalt_armor_materials", 5, 7, 7, 6, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.COBALT_INGOT), "cobalt", 2.0F, 0);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PALLADIUM_MASK_MATERIAL = registerSingletonMaterial("palladium_mask_material", ArmorItem.Type.HELMET, 5, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT), "palladium_mask", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PALLADIUM_HEADGEAR_MATERIAL = registerSingletonMaterial("palladium_headgear_material", ArmorItem.Type.HELMET, 1, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT), "palladium_headgear", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> PALLADIUM_ARMOR_MATERIALS = registerArmorMaterial("palladium_armor_materials", 3, 7, 7, 6, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT), "palladium", 2.0F, 0);

    // 2级新三矿，+1防

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> MYTHRIL_HOOD_MATERIAL = registerSingletonMaterial("mythril_hood_material", ArmorItem.Type.HELMET, 2, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.MYTHRIL_INGOT), "mythril_hood", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> MYTHRIL_HAT_MATERIAL = registerSingletonMaterial("mythril_hat_material", ArmorItem.Type.HELMET, 4, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.MYTHRIL_INGOT), "mythril_hat", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> MYTHRIL_ARMOR_MATERIALS = registerArmorMaterial("mythril_armor_materials", 6, 7, 7, 6, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.MYTHRIL_INGOT), "mythril", 2.0F, 0);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ORICHALCUM_HEADGEAR_MATERIAL = registerSingletonMaterial("orichalcum_headgear_material", ArmorItem.Type.HELMET, 2, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT), "orichalcum_headgear", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ORICHALCUM_MASK_MATERIAL = registerSingletonMaterial("orichalcum_mask_material", ArmorItem.Type.HELMET, 6, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT), "orichalcum_mask", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ORICHALCUM_ARMOR_MATERIALS = registerArmorMaterial("orichalcum_armor_materials", 4, 7, 7, 6, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT), "orichalcum", 2.0F, 0);

    // 3级新三矿，+1防

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ADAMANTITE_HEADGEAR_MATERIAL = registerSingletonMaterial("adamantite_headgear_material", ArmorItem.Type.HELMET, 3, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT), "adamantite_headgear", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ADAMANTITE_MASK_MATERIAL = registerSingletonMaterial("adamantite_mask_material", ArmorItem.Type.HELMET, 4, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT), "adamantite_mask", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> ADAMANTITE_ARMOR_MATERIALS = registerArmorMaterial("adamantite_armor_materials", 7, 8, 7, 7, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT), "adamantite", 2.0F, 0);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> TITANIUM_HEADGEAR_MATERIAL = registerSingletonMaterial("titanium_headgear_material", ArmorItem.Type.HELMET, 3, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT), "titanium_headgear", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> TITANIUM_MASK_MATERIAL = registerSingletonMaterial("titanium_mask_material", ArmorItem.Type.HELMET, 7, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT), "titanium_mask", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> TITANIUM_ARMOR_MATERIALS = registerArmorMaterial("titanium_armor_materials", 4, 7, 7, 6, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT), "titanium", 2.0F, 0);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> CRYSTAL_ASSASSIN_ARMOR_MATERIALS = registerArmorMaterial("crystal_assassin_armor_materials",
            6, 7, 7, 6,
            16, SoundEvents.ARMOR_EQUIP_LEATHER, Items.BONE,
            "crystal_assassin", 0.0F, 0.0F);

    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> HALLOWED_MASK_MATERIAL = registerSingletonMaterial("hallowed_mask_material", ArmorItem.Type.HELMET, 7, 19, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed_mask", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> HALLOWED_HEADGEAR_MATERIAL = registerSingletonMaterial("hallowed_headgear_material", ArmorItem.Type.HELMET, 3, 19, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed_headgear", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> HALLOWED_HOOD_MATERIAL = registerSingletonMaterial("hallowed_hood_material", ArmorItem.Type.HELMET, 1, 19, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed_hood", 2.0F, 0);
    public static final PortRegistryEntry<ArmorMaterial, PortArmorMaterial> HALLOWED_ARMOR_MATERIALS = registerArmorMaterial("hallowed_armor_materials", 4, 8, 7, 7, 19, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed", 2.0F, 0);

    public static PortRegistryEntry<ArmorMaterial, PortArmorMaterial> registerArmorMaterial(String name, int helmetArmor, int chestplateArmor, int leggingsArmor, int bootsArmor, int enchantmentValue, Holder<SoundEvent> equipSound, ItemLike fixItem, String layersName, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> new PortArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, helmetArmor);
                    map.put(ArmorItem.Type.CHESTPLATE, chestplateArmor);
                    map.put(ArmorItem.Type.LEGGINGS, leggingsArmor);
                    map.put(ArmorItem.Type.BOOTS, bootsArmor);
                }),
                enchantmentValue,
                equipSound,
                () -> Ingredient.of(fixItem),
                Collections.singletonList(new PortArmorMaterial.Layer(Confluence.asResource(layersName))),
                toughness,
                knockbackResistance
        ).setName(name));
    }

    @Diff
    public static PortRegistryEntry<ArmorMaterial, PortArmorMaterial> registerArmorMaterial(String name, int helmetArmor, int chestplateArmor, int leggingsArmor, int bootsArmor, int enchantmentValue, SoundEvent equipSound, ItemLike fixItem, String layersName, float toughness, float knockbackResistance) {
        return registerArmorMaterial(name, helmetArmor, chestplateArmor, leggingsArmor, bootsArmor, enchantmentValue, SoundEventHolder.wrap(equipSound), fixItem, layersName, toughness, knockbackResistance);
    }

    public static PortRegistryEntry<ArmorMaterial, PortArmorMaterial> registerArmorMaterial(String name, int helmetArmor, int chestplateArmor, int leggingsArmor, int bootsArmor, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> ingredient, String layersName, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> new PortArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, helmetArmor);
                    map.put(ArmorItem.Type.CHESTPLATE, chestplateArmor);
                    map.put(ArmorItem.Type.LEGGINGS, leggingsArmor);
                    map.put(ArmorItem.Type.BOOTS, bootsArmor);
                }),
                enchantmentValue,
                equipSound,
                ingredient,
                Collections.singletonList(new PortArmorMaterial.Layer(Confluence.asResource(layersName))),
                toughness,
                knockbackResistance
        ).setName(name));
    }

    @Diff
    public static PortRegistryEntry<ArmorMaterial, PortArmorMaterial> registerArmorMaterial(String name, int helmetArmor, int chestplateArmor, int leggingsArmor, int bootsArmor, int enchantmentValue, SoundEvent equipSound, Supplier<Ingredient> ingredient, String layersName, float toughness, float knockbackResistance) {
        return registerArmorMaterial(name, helmetArmor, chestplateArmor, leggingsArmor, bootsArmor, enchantmentValue, SoundEventHolder.wrap(equipSound), ingredient, layersName, toughness, knockbackResistance);
    }

    public static PortRegistryEntry<ArmorMaterial, PortArmorMaterial> registerSingletonMaterial(String name, ArmorItem.Type type, int armor, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> ingredient, String layersName, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> new PortArmorMaterial(
                Collections.singletonMap(type, armor),
                enchantmentValue,
                equipSound,
                ingredient,
                Collections.singletonList(new PortArmorMaterial.Layer(Confluence.asResource(layersName))),
                toughness,
                knockbackResistance
        ).setName(name));
    }

    public static PortRegistryEntry<ArmorMaterial, PortArmorMaterial> registerSingletonMaterial(String name, ArmorItem.Type type, int armor, int enchantmentValue, SoundEvent equipSound, Supplier<Ingredient> ingredient, String layersName, float toughness, float knockbackResistance) {
        return registerSingletonMaterial(name, type, armor, enchantmentValue, SoundEventHolder.wrap(equipSound), ingredient, layersName, toughness, knockbackResistance);
    }
}
