package org.confluence.mod.common.init;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.EnumMap;
import java.util.List;

public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Confluence.MODID);

    public static final Holder<ArmorMaterial> VANITY_ARMOR_MATERIALS = ARMOR_MATERIALS.register("vanity_armor_materials", () -> new ArmorMaterial(
            new Object2IntOpenHashMap<>(),
            0, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(Confluence.asResource("vanity_armor"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> CACTUS_ARMOR_MATERIALS = ARMOR_MATERIALS.register("cactus_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 2);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 1);
            }),
            9, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(Items.CACTUS),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("cactus"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> EBONY_ARMOR_MATERIALS = ARMOR_MATERIALS.register("ebony_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 2);
                map.put(ArmorItem.Type.LEGGINGS, 1);
                map.put(ArmorItem.Type.BOOTS, 1);
            }),
            9, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks().get()),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("ebony"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> SHADOW_PLANK_ARMOR_MATERIALS = ARMOR_MATERIALS.register("shadow_plank_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 2);
                map.put(ArmorItem.Type.LEGGINGS, 1);
                map.put(ArmorItem.Type.BOOTS, 1);
            }),
            9, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks().get()),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("shadow_plank"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> PEARL_ARMOR_MATERIALS = ARMOR_MATERIALS.register("pearl_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 2);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 1);
            }),
            9, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks().get()),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("pearl"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> RAINCOAT_ARMOR_MATERIALS = ARMOR_MATERIALS.register("raincoat_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 1);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 1);
            }),
            9, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.GEL.get()),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("raincoat"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> SNOW_ARMOR_MATERIALS = ARMOR_MATERIALS.register("snow_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 2);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            9, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.BLUE_WOOL),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("snow"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> PINK_SNOW_ARMOR_MATERIALS = ARMOR_MATERIALS.register("pink_snow_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 2);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            9, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(Items.PINK_WOOL),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("pink_snow"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> PLANK_ARMOR_MATERIALS = ARMOR_MATERIALS.register("plank_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 1);
                map.put(ArmorItem.Type.LEGGINGS, 1);
                map.put(ArmorItem.Type.BOOTS, 1);
            }),
            9, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(Items.OAK_PLANKS),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("plank"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> COPPER_ARMOR_MATERIALS = ARMOR_MATERIALS.register("copper_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 4);
                map.put(ArmorItem.Type.LEGGINGS, 4);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Items.COPPER_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("copper"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> TIN_ARMOR_MATERIALS = ARMOR_MATERIALS.register("tin_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 4);
                map.put(ArmorItem.Type.LEGGINGS, 4);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(MaterialItems.TIN_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("tin"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> LEAD_ARMOR_MATERIALS = ARMOR_MATERIALS.register("lead_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(MaterialItems.LEAD_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("lead"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> SILVER_ARMOR_MATERIALS = ARMOR_MATERIALS.register("silver_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            10, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(MaterialItems.SILVER_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("silver"))), 0.0F, 0.0F
    ));

    public static final Holder<ArmorMaterial> TUNGSTEN_ARMOR_MATERIALS = ARMOR_MATERIALS.register("tungsten_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            10, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(MaterialItems.TUNGSTEN_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("tungsten"))), 0.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> GOLDEN_ARMOR_MATERIALS = ARMOR_MATERIALS.register("golden_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
            }),
            10, SoundEvents.ARMOR_EQUIP_GOLD, () -> Ingredient.of(Items.GOLD_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("golden"))), 2.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> PLATINUM_ARMOR_MATERIALS = ARMOR_MATERIALS.register("platinum_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 3);
            }),
            10, SoundEvents.ARMOR_EQUIP_GOLD, () -> Ingredient.of(MaterialItems.PLATINUM_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("platinum"))), 2.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> FOSSIL_ARMOR_MATERIALS = ARMOR_MATERIALS.register("fossil_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            11, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(MaterialItems.STURDY_FOSSIL),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("fossil"))), 1.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> NINJA_ARMOR_MATERIALS = ARMOR_MATERIALS.register("ninja_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            11, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(MaterialItems.BLACK_INK),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("ninja"))), 1.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> SPORE_ROOT_ARMOR_MATERIALS = ARMOR_MATERIALS.register("gemstone_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 4);
                map.put(ArmorItem.Type.LEGGINGS, 4);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            11, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(MaterialItems.SPORE_ROOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("spore_root"))), 2.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> COLD_CRYSTAL_ARMOR_MATERIALS = ARMOR_MATERIALS.register("cold_crystal_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            11, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(MaterialItems.COLD_CRYSTAL),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("cold_crystal"))), 2.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> HEIM_ARMOR_MATERIALS = ARMOR_MATERIALS.register("heim_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
            }),
            11, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(MaterialItems.HEIM),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("heim"))), 2.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> SHADOW_ARMOR_MATERIALS = ARMOR_MATERIALS.register("shadow_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 3);
            }),
            12, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.DEMONITE_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("shadow"))), 2.0F, 0.0F
    ));
    public static final Holder<ArmorMaterial> CRIMSON_ARMOR_MATERIALS = ARMOR_MATERIALS.register("crimson_armor_materials", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 7);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
            }),
            12, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MaterialItems.TR_CRIMSON_INGOT),
            List.of(new ArmorMaterial.Layer(Confluence.asResource("crimson"))), 2.0F, 0.0F
    ));
}
