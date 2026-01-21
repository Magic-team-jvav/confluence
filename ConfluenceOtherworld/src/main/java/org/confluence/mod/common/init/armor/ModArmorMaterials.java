package org.confluence.mod.common.init.armor;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Confluence.MODID);

    public static final Holder<ArmorMaterial> VANITY_ARMOR_MATERIALS = ARMOR_MATERIALS.register("vanity_armor_materials", () -> new ArmorMaterial(
            new Object2IntOpenHashMap<>(),
            0, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(Confluence.asResource("vanity_armor"))), 0.0F, 0.0F)
    );
    public static final Holder<ArmorMaterial> CACTUS_ARMOR_MATERIALS = registerArmorMaterial("cactus_armor_materials",
            1, 2, 2, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, Items.CACTUS, "cactus", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> MINING_ARMOR_MATERIALS = registerArmorMaterial("mining_armor_materials",
            2, 3, 2, 1,
            9, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.SILK, "mining", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> ANGLER_ARMOR_MATERIALS = registerArmorMaterial("angler_armor_materials",
            1, 1, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.SILK, "angler", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> EBONY_ARMOR_MATERIALS = registerArmorMaterial("ebony_armor_materials",
            1, 2, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.EBONY_LOG_BLOCKS.PLANKS, "ebony", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> SHADOW_PLANK_ARMOR_MATERIALS = registerArmorMaterial("shadow_plank_armor_materials",
            1, 2, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS, "shadow_plank", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> PEARL_ARMOR_MATERIALS = registerArmorMaterial("pearl_armor_materials",
            1, 2, 2, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.PEARL_LOG_BLOCKS.PLANKS,
            "pearl", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> ASH_ARMOR_MATERIALS = registerArmorMaterial("ash_armor_materials",
            2, 2, 2, 2,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, NatureBlocks.ASH_LOG_BLOCKS.PLANKS, "pearl", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> PUMPKIN_ARMOR_MATERIALS = registerArmorMaterial("pumpkin_armor_materials",
            2, 2, 2, 2,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, Blocks.PUMPKIN, "pearl", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> RAINCOAT_ARMOR_MATERIALS = registerArmorMaterial("raincoat_armor_materials",
            1, 2, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_LEATHER, MaterialItems.GEL, "raincoat", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> SNOW_ARMOR_MATERIALS = registerArmorMaterial("snow_armor_materials",
            2, 2, 2, 2,
            9, SoundEvents.ARMOR_EQUIP_LEATHER, Items.BLUE_WOOL, "snow", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> PINK_SNOW_ARMOR_MATERIALS = registerArmorMaterial("pink_snow_armor_materials",
            2, 2, 2, 2,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, Items.PINK_WOOL, "pink_snow", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> PLANK_ARMOR_MATERIALS = registerArmorMaterial("plank_armor_materials",
            1, 1, 1, 1,
            9, SoundEvents.ARMOR_EQUIP_GENERIC, Items.OAK_PLANKS, "plank", 0.0F,
            0.0F
    );
    public static final Holder<ArmorMaterial> COPPER_ARMOR_MATERIALS = registerArmorMaterial("copper_armor_materials",
            2, 3, 3, 2,
            9, SoundEvents.ARMOR_EQUIP_IRON, Items.COPPER_INGOT,
            "copper", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> TIN_ARMOR_MATERIALS = registerArmorMaterial("tin_armor_materials",
            2, 3, 3, 2,
            9, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.TIN_INGOT,
            "tin", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> LEAD_ARMOR_MATERIALS = registerArmorMaterial("lead_armor_materials",
            2, 5, 5, 2,
            9, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.LEAD_INGOT,
            "lead", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> SILVER_ARMOR_MATERIALS = registerArmorMaterial("silver_armor_materials",
            2, 6, 5, 2,
            10, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.SILVER_INGOT,
            "silver", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> TUNGSTEN_ARMOR_MATERIALS = registerArmorMaterial("tungsten_armor_materials",
            2, 6, 5, 2,
            10, SoundEvents.ARMOR_EQUIP_IRON, MaterialItems.TUNGSTEN_INGOT,
            "tungsten", 0.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> GOLDEN_ARMOR_MATERIALS = registerArmorMaterial("golden_armor_materials",
            3, 6, 6, 3,
            10, SoundEvents.ARMOR_EQUIP_GOLD, Items.GOLD_INGOT,
            "golden", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> PLATINUM_ARMOR_MATERIALS = registerArmorMaterial("platinum_armor_materials",
            3, 6, 6, 3,
            10, SoundEvents.ARMOR_EQUIP_GOLD, MaterialItems.PLATINUM_INGOT,
            "platinum", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> FOSSIL_ARMOR_MATERIALS = registerArmorMaterial("fossil_armor_materials",
            2, 6, 6, 2,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.STURDY_FOSSIL,
            "fossil", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> BEE_ARMOR_MATERIALS = registerArmorMaterial("bee_armor_materials",
            3, 5, 5, 3,
            14, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.ROYAL_WAX,
            "bee", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> NINJA_ARMOR_MATERIALS = registerArmorMaterial("ninja_armor_materials",
            2, 5, 5, 2,
            11, SoundEvents.ARMOR_EQUIP_LEATHER, MaterialItems.BLACK_INK,
            "ninja", 1.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> OBSIDIAN_ARMOR_MATERIALS = registerArmorMaterial("obsidian_armor_materials",
            3, 6, 5, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, Blocks.OBSIDIAN,
            "obsidian", 1.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> GLADIATOR_ARMOR_MATERIALS = registerArmorMaterial("gladiator_armor_materials",
            3, 6, 6, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, Items.GOLD_INGOT,
            "gladiator", 1.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> METEOR_ARMOR_MATERIALS = registerArmorMaterial("meteor_armor_materials",
            3, 6, 6, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, MaterialItems.METEORITE_INGOT,
            "meteor", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> SPORE_ROOT_ARMOR_MATERIALS = registerArmorMaterial("spore_root_armor_materials",
            2, 4, 4, 2,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.SPORE_ROOT,
            "spore_root", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> COLD_CRYSTAL_ARMOR_MATERIALS = registerArmorMaterial("cold_crystal_armor_materials",
            2, 5, 5, 2,
            11, SoundEvents.ARMOR_EQUIP_DIAMOND, MaterialItems.COLD_CRYSTAL,
            "cold_crystal", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> HEIM_ARMOR_MATERIALS = registerArmorMaterial("heim_armor_materials",
            3, 6, 6, 3,
            11, SoundEvents.ARMOR_EQUIP_GENERIC, MaterialItems.HEIM,
            "heim", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> SHADOW_ARMOR_MATERIALS = registerArmorMaterial("shadow_armor_materials",
            3, 7, 6, 3,
            12, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.DEMONITE_INGOT,
            "shadow", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> CRIMSON_ARMOR_MATERIALS = registerArmorMaterial("crimson_armor_materials",
            3, 7, 6, 3,
            12, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.CRIMTANE_INGOT,
            "crimson", 2.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> MOLTEN_ARMOR_MATERIALS = registerArmorMaterial("molten_armor_materials",
            5, 7, 7, 6,
            15, SoundEvents.ARMOR_EQUIP_NETHERITE, MaterialItems.HELLSTONE_INGOT,
            "molten", 1.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> NECRO_ARMOR_MATERIALS = registerArmorMaterial("necro_armor_materials",
            3, 7, 6, 3,
            16, SoundEvents.ARMOR_EQUIP_LEATHER, ConsumableItems.DUNGEON_DEMON_BONE,
            "necro", 2.0F, 0.0F);
    public static final Holder<ArmorMaterial> SPIDER_ARMOR_MATERIALS = registerArmorMaterial("spider_armor_materials",
            3, 7, 7, 3,
            17, SoundEvents.ARMOR_EQUIP_LEATHER, Items.BONE,
            "spider", 2.0F, 0.0F);
    public static final Holder<ArmorMaterial> TIKI_ARMOR_MATERIALS = registerArmorMaterial("tiki_armor_materials",
            3, 6, 5, 3,
            13, SoundEvents.ARMOR_EQUIP_LEATHER, Blocks.OBSIDIAN,
            "obsidian", 1.0F, 0.0F
    );
    public static final Holder<ArmorMaterial> GOGGLES_MATERIAL = registerSingletonMaterial("goggles_material", ArmorItem.Type.HELMET, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "goggles", 0, 0);
    public static final Holder<ArmorMaterial> GREEN_CAP_MATERIAL = registerSingletonMaterial("green_cap_material", ArmorItem.Type.HELMET, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "green_cap", 0, 0);

    public static final Holder<ArmorMaterial> WIZARD_HAT_MATERIAL = registerSingletonMaterial("wizard_hat_material", ArmorItem.Type.HELMET, 4, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "wizard_hat", 0, 0);
    public static final Holder<ArmorMaterial> MAGIC_HAT_ARMOR_MATERIAL = registerSingletonMaterial("magic_hat_material", ArmorItem.Type.HELMET, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "magic_hat", 0, 0);
    public static final Holder<ArmorMaterial> VIKING_ARMOR_MATERIAL = registerSingletonMaterial("viking_helmet_material", ArmorItem.Type.HELMET, 4, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "viking_helmet", 0, 0);
    public static final Holder<ArmorMaterial> FLINX_FUR_COAT_MATERIAL = registerSingletonMaterial("flinx_fur_coat_material", ArmorItem.Type.CHESTPLATE, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "flinx_fur_coat", 0, 0);
    public static final Holder<ArmorMaterial> AMETHYST_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("amethyst_robe_material", ArmorItem.Type.CHESTPLATE, 0, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.AMETHYST), "amethyst_robe", 0, 0);
    public static final Holder<ArmorMaterial> TOPAZ_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("topaz_robe_material", ArmorItem.Type.CHESTPLATE, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.TOPAZ), "topaz_robe", 0, 0);
    public static final Holder<ArmorMaterial> SAPPHIRE_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("sapphire_robe_material", ArmorItem.Type.CHESTPLATE, 1, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.SAPPHIRE), "sapphire_robe", 0, 0);
    public static final Holder<ArmorMaterial> EMERALD_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("emerald_robe_material", ArmorItem.Type.CHESTPLATE, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.JADE), "emerald_robe", 0, 0);
    public static final Holder<ArmorMaterial> RUBY_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("ruby_robe_material", ArmorItem.Type.CHESTPLATE, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.RUBY), "ruby_robe", 0, 0);
    public static final Holder<ArmorMaterial> MYSTIC_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("mystic_robe_material", ArmorItem.Type.CHESTPLATE, 2, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, "mystic_robe", 0, 0);
    public static final Holder<ArmorMaterial> DIAMOND_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("diamond_robe_material", ArmorItem.Type.CHESTPLATE, 3, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.DIAMOND), "diamond_robe", 0, 0);
    public static final Holder<ArmorMaterial> AMBER_ROBE_ARMOR_MATERIAL = registerSingletonMaterial("amber_robe_material", ArmorItem.Type.CHESTPLATE, 3, 10, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.AMBER), "amber_robe", 0, 0);

    public static final Holder<ArmorMaterial> JUNGLE_ARMOR_MATERIALS = registerArmorMaterial("jungle_armor_materials", 3, 6, 6, 3, 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.JUNGLE_SPORE), "jungle", 2, 0);

    public static final Holder<ArmorMaterial> COBALT_MASK_MATERIAL = registerSingletonMaterial("cobalt_mask_material", ArmorItem.Type.HELMET, 3, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.COBALT_INGOT), "cobalt_mask", 2.0F, 0);
    public static final Holder<ArmorMaterial> COBALT_HAT_MATERIAL = registerSingletonMaterial("cobalt_hat_material", ArmorItem.Type.HELMET, 1, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.COBALT_INGOT), "cobalt_hat", 2.0F, 0);
    public static final Holder<ArmorMaterial> COBALT_ARMOR_MATERIALS = registerArmorMaterial("cobalt_armor_materials", 5, 7, 7, 6, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.COBALT_INGOT), "cobalt", 2.0F, 0);

    public static final Holder<ArmorMaterial> PALLADIUM_MASK_MATERIAL = registerSingletonMaterial("palladium_mask_material", ArmorItem.Type.HELMET, 5, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT), "palladium_mask", 2.0F, 0);
    public static final Holder<ArmorMaterial> PALLADIUM_HEADGEAR_MATERIAL = registerSingletonMaterial("palladium_headgear_material", ArmorItem.Type.HELMET, 1, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT), "palladium_headgear", 2.0F, 0);
    public static final Holder<ArmorMaterial> PALLADIUM_ARMOR_MATERIALS = registerArmorMaterial("palladium_armor_materials", 3, 7, 7, 6, 16, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.PALLADIUM_INGOT), "palladium", 2.0F, 0);

    // 2级新三矿，+1防

    public static final Holder<ArmorMaterial> MYTHRIL_HOOD_MATERIAL = registerSingletonMaterial("mythril_hood_material", ArmorItem.Type.HELMET, 2, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.MYTHRIL_INGOT), "mythril_hood", 2.0F, 0);
    public static final Holder<ArmorMaterial> MYTHRIL_HAT_MATERIAL = registerSingletonMaterial("mythril_hat_material", ArmorItem.Type.HELMET, 4, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.MYTHRIL_INGOT), "mythril_hat", 2.0F, 0);
    public static final Holder<ArmorMaterial> MYTHRIL_ARMOR_MATERIALS = registerArmorMaterial("mythril_armor_materials", 6, 7, 7, 6, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.MYTHRIL_INGOT), "mythril", 2.0F, 0);

    public static final Holder<ArmorMaterial> ORICHALCUM_HEADGEAR_MATERIAL = registerSingletonMaterial("orichalcum_headgear_material", ArmorItem.Type.HELMET, 2, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT), "orichalcum_headgear", 2.0F, 0);
    public static final Holder<ArmorMaterial> ORICHALCUM_MASK_MATERIAL = registerSingletonMaterial("orichalcum_mask_material", ArmorItem.Type.HELMET, 6, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT), "orichalcum_mask", 2.0F, 0);
    public static final Holder<ArmorMaterial> ORICHALCUM_ARMOR_MATERIALS = registerArmorMaterial("orichalcum_armor_materials", 4, 7, 7, 6, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ORICHALCUM_INGOT), "orichalcum", 2.0F, 0);

    // 3级新三矿，+1防

    public static final Holder<ArmorMaterial> ADAMANTITE_HEADGEAR_MATERIAL = registerSingletonMaterial("adamantite_headgear_material", ArmorItem.Type.HELMET, 3, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT), "adamantite_headgear", 2.0F, 0);
    public static final Holder<ArmorMaterial> ADAMANTITE_MASK_MATERIAL = registerSingletonMaterial("adamantite_mask_material", ArmorItem.Type.HELMET, 4, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT), "adamantite_mask", 2.0F, 0);
    public static final Holder<ArmorMaterial> ADAMANTITE_ARMOR_MATERIALS = registerArmorMaterial("adamantite_armor_materials", 7, 8, 7, 7, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.ADAMANTITE_INGOT), "adamantite", 2.0F, 0);

    public static final Holder<ArmorMaterial> TITANIUM_HEADGEAR_MATERIAL = registerSingletonMaterial("titanium_headgear_material", ArmorItem.Type.HELMET, 3, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT), "titanium_headgear", 2.0F, 0);
    public static final Holder<ArmorMaterial> TITANIUM_MASK_MATERIAL = registerSingletonMaterial("titanium_mask_material", ArmorItem.Type.HELMET, 7, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT), "titanium_mask", 2.0F, 0);
    public static final Holder<ArmorMaterial> TITANIUM_ARMOR_MATERIALS = registerArmorMaterial("titanium_armor_materials", 4, 7, 7, 6, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.TITANIUM_INGOT), "titanium", 2.0F, 0);

    public static final Holder<ArmorMaterial> CRYSTAL_ASSASSIN_ARMOR_MATERIALS = registerArmorMaterial("crystal_assassin_armor_materials",
            6, 7, 7, 6,
            16, SoundEvents.ARMOR_EQUIP_LEATHER, Items.BONE,
            "crystal_assassin", 0.0F, 0.0F);

    public static final Holder<ArmorMaterial> HALLOWED_MASK_MATERIAL = registerSingletonMaterial("hallowed_mask_material", ArmorItem.Type.HELMET, 7, 19, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed_mask", 2.0F, 0);
    public static final Holder<ArmorMaterial> HALLOWED_HEADGEAR_MATERIAL = registerSingletonMaterial("hallowed_headgear_material", ArmorItem.Type.HELMET, 3, 19, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed_headgear", 2.0F, 0);
    public static final Holder<ArmorMaterial> HALLOWED_HOOD_MATERIAL = registerSingletonMaterial("hallowed_hood_material", ArmorItem.Type.HELMET, 1, 19, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed_hood", 2.0F, 0);
    public static final Holder<ArmorMaterial> HALLOWED_ARMOR_MATERIALS = registerArmorMaterial("hallowed_armor_materials", 4, 8, 7, 7, 19,  SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.HALLOWED_INGOT), "hallowed", 2.0F, 0);

    public static Holder<ArmorMaterial> registerArmorMaterial(String name, int helmetArmor, int chestplateArmor, int leggingsArmor, int bootsArmor, int enchantmentValue, Holder<SoundEvent> equipSound, ItemLike fixItem, String layersName, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, helmetArmor);
                    map.put(ArmorItem.Type.CHESTPLATE, chestplateArmor);
                    map.put(ArmorItem.Type.LEGGINGS, leggingsArmor);
                    map.put(ArmorItem.Type.BOOTS, bootsArmor);
                }),
                enchantmentValue,
                equipSound,
                () -> Ingredient.of(fixItem),
                Collections.singletonList(new ArmorMaterial.Layer(Confluence.asResource(layersName))),
                toughness,
                knockbackResistance
        ));
    }

    public static Holder<ArmorMaterial> registerArmorMaterial(String name, int helmetArmor, int chestplateArmor, int leggingsArmor, int bootsArmor, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> ingredient, String layersName, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, helmetArmor);
                    map.put(ArmorItem.Type.CHESTPLATE, chestplateArmor);
                    map.put(ArmorItem.Type.LEGGINGS, leggingsArmor);
                    map.put(ArmorItem.Type.BOOTS, bootsArmor);
                }),
                enchantmentValue,
                equipSound,
                ingredient,
                Collections.singletonList(new ArmorMaterial.Layer(Confluence.asResource(layersName))),
                toughness,
                knockbackResistance
        ));
    }

    public static Holder<ArmorMaterial> registerSingletonMaterial(String name, ArmorItem.Type type, int armor, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> ingredient, String layersName, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(Collections.singletonMap(type, armor), enchantmentValue, equipSound, ingredient, Collections.singletonList(new ArmorMaterial.Layer(Confluence.asResource(layersName))), toughness, knockbackResistance));
    }
}
