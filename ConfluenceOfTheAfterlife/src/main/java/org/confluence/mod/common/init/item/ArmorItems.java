package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModArmorMaterials;
import org.confluence.mod.common.item.armor.NormalArmorItem;

import java.util.function.Supplier;

public class ArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<ArmorItem> CACTUS_HELMET = registerNormalArmor("cactus_helmet", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> CACTUS_CHESTPLATE = registerNormalArmor("cactus_chestplate", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final Supplier<ArmorItem> CACTUS_LEGGINGS = registerNormalArmor("cactus_leggings", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final Supplier<ArmorItem> CACTUS_BOOTS = registerNormalArmor("cactus_boots", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final Supplier<ArmorItem> PLANK_HELMET = registerNormalArmor("plank_helmet", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 55);
    public static final Supplier<ArmorItem> PLANK_CHESTPLATE = registerNormalArmor("plank_chestplate", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 80);
    public static final Supplier<ArmorItem> PLANK_LEGGINGS = registerNormalArmor("plank_leggings", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 75);
    public static final Supplier<ArmorItem> PLANK_BOOTS = registerNormalArmor("plank_boots", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 65);

    public static final Supplier<ArmorItem> EBONY_HELMET = registerNormalArmor("ebony_helmet", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> EBONY_CHESTPLATE = registerNormalArmor("ebony_chestplate", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final Supplier<ArmorItem> EBONY_LEGGINGS = registerNormalArmor("ebony_leggings", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final Supplier<ArmorItem> EBONY_BOOTS = registerNormalArmor("ebony_boots", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final Supplier<ArmorItem> SHADOW_PLANK_HELMET = registerNormalArmor("shadow_plank_helmet", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> SHADOW_PLANK_CHESTPLATE = registerNormalArmor("shadow_plank_chestplate", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final Supplier<ArmorItem> SHADOW_PLANK_LEGGINGS = registerNormalArmor("shadow_plank_leggings", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final Supplier<ArmorItem> SHADOW_PLANK_BOOTS = registerNormalArmor("shadow_plank_boots", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final Supplier<ArmorItem> PEARL_HELMET = registerNormalArmor("pearl_helmet", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> PEARL_CHESTPLATE = registerNormalArmor("pearl_chestplate", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final Supplier<ArmorItem> PEARL_LEGGINGS = registerNormalArmor("pearl_leggings", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final Supplier<ArmorItem> PEARL_BOOTS = registerNormalArmor("pearl_boots", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final Supplier<ArmorItem> RAIN_CAP = registerNormalArmor("rain_cap", "raincoat_armor", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> RAINCOAT = registerNormalArmor("raincoat", "raincoat_armor", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);

    public static final Supplier<ArmorItem> SNOW_CAPS = registerNormalArmor("snow_caps", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> SNOW_SUITS = registerNormalArmor("snow_suits", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final Supplier<ArmorItem> INSULATED_PANTS = registerNormalArmor("insulated_pants", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final Supplier<ArmorItem> INSULATED_SHOES = registerNormalArmor("insulated_shoes", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 140);

    public static final Supplier<ArmorItem> PINK_SNOW_CAPS = registerNormalArmor("pink_snow_caps", "", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> PINK_SNOW_SUITS = registerNormalArmor("pink_snow_suits", "", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final Supplier<ArmorItem> PINK_INSULATED_PANTS = registerNormalArmor("pink_insulated_pants", "", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final Supplier<ArmorItem> PINK_INSULATED_SHOES = registerNormalArmor("pink_insulated_shoes", "", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 140);

    public static final Supplier<ArmorItem> COPPER_HELMET = registerNormalArmor("copper_helmet", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final Supplier<ArmorItem> COPPER_CHESTPLATE = registerNormalArmor("copper_chestplate", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final Supplier<ArmorItem> COPPER_LEGGINGS = registerNormalArmor("copper_leggings", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final Supplier<ArmorItem> COPPER_BOOTS = registerNormalArmor("copper_boots", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final Supplier<ArmorItem> TIN_HELMET = registerNormalArmor("tin_helmet", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 150);
    public static final Supplier<ArmorItem> TIN_CHESTPLATE = registerNormalArmor("tin_chestplate", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 190);
    public static final Supplier<ArmorItem> TIN_LEGGINGS = registerNormalArmor("tin_leggings", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 170);
    public static final Supplier<ArmorItem> TIN_BOOTS = registerNormalArmor("tin_boots", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 150);

    public static final Supplier<ArmorItem> LEAD_HELMET = registerNormalArmor("lead_helmet", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 170);
    public static final Supplier<ArmorItem> LEAD_CHESTPLATE = registerNormalArmor("lead_chestplate", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 250);
    public static final Supplier<ArmorItem> LEAD_LEGGINGS = registerNormalArmor("lead_leggings", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 230);
    public static final Supplier<ArmorItem> LEAD_BOOTS = registerNormalArmor("lead_boots", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 160);

    public static final Supplier<ArmorItem> SILVER_HELMET = registerNormalArmor("silver_helmet", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 190);
    public static final Supplier<ArmorItem> SILVER_CHESTPLATE = registerNormalArmor("silver_chestplate", "silver_", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final Supplier<ArmorItem> SILVER_LEGGINGS = registerNormalArmor("silver_leggings", "silver_", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final Supplier<ArmorItem> SILVER_BOOTS = registerNormalArmor("silver_boots", "silver_", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 180);

    public static final Supplier<ArmorItem> TUNGSTEN_HELMET = registerNormalArmor("tungsten_helmet", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 210);
    public static final Supplier<ArmorItem> TUNGSTEN_CHESTPLATE = registerNormalArmor("tungsten_chestplate", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 290);
    public static final Supplier<ArmorItem> TUNGSTEN_LEGGINGS = registerNormalArmor("tungsten_leggings", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 270);
    public static final Supplier<ArmorItem> TUNGSTEN_BOOTS = registerNormalArmor("tungsten_boots", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 200);

    public static final Supplier<ArmorItem> GOLDEN_HELMET = registerNormalArmor("golden_helmet", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 250);
    public static final Supplier<ArmorItem> GOLDEN_CHESTPLATE = registerNormalArmor("golden_chestplate", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 340);
    public static final Supplier<ArmorItem> GOLDEN_LEGGINGS = registerNormalArmor("golden_leggings", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 320);
    public static final Supplier<ArmorItem> GOLDEN_BOOTS = registerNormalArmor("golden_boots", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 260);

    public static final Supplier<ArmorItem> PLATINUM_HELMET = registerNormalArmor("platinum_helmet", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 250);
    public static final Supplier<ArmorItem> PLATINUM_CHESTPLATE = registerNormalArmor("platinum_chestplate", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 340);
    public static final Supplier<ArmorItem> PLATINUM_LEGGINGS = registerNormalArmor("platinum_leggings", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 320);
    public static final Supplier<ArmorItem> PLATINUM_BOOTS = registerNormalArmor("platinum_boots", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 260);

    public static final Supplier<ArmorItem> NINJA_HELMET = registerNormalArmor("ninja_helmet", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 170);
    public static final Supplier<ArmorItem> NINJA_CHESTPLATE = registerNormalArmor("ninja_chestplate", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 2500);
    public static final Supplier<ArmorItem> NINJA_LEGGINGS = registerNormalArmor("ninja_leggings", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 2300);
    public static final Supplier<ArmorItem> NINJA_BOOTS = registerNormalArmor("ninja_boots", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 160);

    public static final Supplier<ArmorItem> FOSSIL_HELMET = registerNormalArmor("fossil_helmet", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 200);
    public static final Supplier<ArmorItem> FOSSIL_CHESTPLATE = registerNormalArmor("fossil_chestplate", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final Supplier<ArmorItem> FOSSIL_LEGGINGS = registerNormalArmor("fossil_leggings", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final Supplier<ArmorItem> FOSSIL_BOOTS = registerNormalArmor("fossil_boots", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 200);

    public static final Supplier<ArmorItem> GEMSTONE_HELMET = registerNormalArmor("gemstone_helmet", "gemstone_armor", ModArmorMaterials.GEMSTONE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 200);
    public static final Supplier<ArmorItem> GEMSTONE_CHESTPLATE = registerNormalArmor("gemstone_chestplate", "gemstone_armor", ModArmorMaterials.GEMSTONE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final Supplier<ArmorItem> GEMSTONE_LEGGINGS = registerNormalArmor("gemstone_leggings", "gemstone_armor", ModArmorMaterials.GEMSTONE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final Supplier<ArmorItem> GEMSTONE_BOOTS = registerNormalArmor("gemstone__boots", "gemstone_armor", ModArmorMaterials.GEMSTONE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 200);

    public static final Supplier<ArmorItem> COLD_CRYSTAL_HELMET = registerNormalArmor("cold_crystal_helmet", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 200);
    public static final Supplier<ArmorItem> COLD_CRYSTAL_CHESTPLATE = registerNormalArmor("cold_crystal_chestplate", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final Supplier<ArmorItem> COLD_CRYSTAL_LEGGINGS = registerNormalArmor("cold_crystal_leggings", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final Supplier<ArmorItem> COLD_CRYSTAL_BOOTS = registerNormalArmor("cold_crystal_boots", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 200);

    public static final Supplier<ArmorItem> SEA_FOAM_STONE_HELMET = registerNormalArmor("sea_foam_stone_helmet", "sea_foam_stone_armor", ModArmorMaterials.SEA_FOAM_STONE_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 200);
    public static final Supplier<ArmorItem> SEA_FOAM_STONE_CHESTPLATE = registerNormalArmor("sea_foam_stone_chestplate", "sea_foam_stone_armor", ModArmorMaterials.SEA_FOAM_STONE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final Supplier<ArmorItem> SEA_FOAM_STONE_LEGGINGS = registerNormalArmor("sea_foam_stone_leggings", "sea_foam_stone_armor", ModArmorMaterials.SEA_FOAM_STONE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final Supplier<ArmorItem> SEA_FOAM_STONE_BOOTS = registerNormalArmor("sea_foam_stone_boots", "sea_foam_stone_armor", ModArmorMaterials.SEA_FOAM_STONE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 200);

    private static Supplier<ArmorItem> registerNormalArmor(String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type, int durability) {
        return ITEMS.register(name, () -> new NormalArmorItem("armor/" + geoName, material, type, new Item.Properties().stacksTo(1).durability(durability)));
    }

    public static void register(IEventBus eventBus) {
        ModArmorMaterials.ARMOR_MATERIALS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
