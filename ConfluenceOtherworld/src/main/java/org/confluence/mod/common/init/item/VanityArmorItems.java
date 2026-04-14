package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.armor.ModArmorMaterials;
import org.confluence.mod.common.item.armor.BaseVanityArmorItem;
import org.confluence.mod.common.item.armor.FamiliarVanityArmorItem;
import org.confluence.mod.common.item.common.BaseDyeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class VanityArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final List<BaseDyeItem> COLORED_DYE_ITEMS = new ArrayList<>();

    public static final DeferredItem<BaseVanityArmorItem> GOLD_CROWN = registerVanityArmor("gold_crown", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLATINUM_CROWN = registerVanityArmor("platinum_crown", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ROBE = registerVanityArmor("robe", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TOP_HAT = registerVanityArmor("top_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_SHIRT = registerVanityArmor("tuxedo_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_PANTS = registerVanityArmor("tuxedo_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_SHOES = registerVanityArmor("tuxedo_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> SUMMER_HAT = registerVanityArmor("summer_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> BUNNY_HOOD = registerVanityArmor("bunny_hood", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_HAT = registerVanityArmor("plumbers_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_SHIRT = registerVanityArmor("plumbers_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_PANTS = registerVanityArmor("plumbers_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_SHOES = registerVanityArmor("plumbers_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_HAT = registerVanityArmor("heros_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_SHIRT = registerVanityArmor("heros_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_PANTS = registerVanityArmor("heros_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_SHOES = registerVanityArmor("heros_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_HAT = registerVanityArmor("archaeologists_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_JACKET = registerVanityArmor("archaeologists_jacket", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_PANTS = registerVanityArmor("archaeologists_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_SHOES = registerVanityArmor("archaeologists_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_HAT = registerVanityArmor("clothiers_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_JACKET = registerVanityArmor("clothiers_jacket", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_PANTS = registerVanityArmor("clothiers_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_SHOES = registerVanityArmor("clothiers_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ROBOT_HAT = registerVanityArmor("robot_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_WIG = ITEMS.register("familiar_wig", () -> new FamiliarVanityArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_SHIRT = ITEMS.register("familiar_shirt", () -> new FamiliarVanityArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_PANTS = ITEMS.register("familiar_pants", () -> new FamiliarVanityArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_SHOES = ITEMS.register("familiar_shoes", () -> new FamiliarVanityArmorItem(ArmorItem.Type.BOOTS));
    public static final DeferredItem<BaseVanityArmorItem> MIME_MASK = registerVanityArmor("mime_mask", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHIRT = registerVanityArmor("the_doctors_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_PANTS = registerVanityArmor("the_doctors_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHOES = registerVanityArmor("the_doctors_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> DEAD_MANS_SWEATER = registerVanityArmor(
            "dead_mans_seater",
            "vanity_armor/dead_mans_seater",
            ModArmorMaterials.VANITY_ARMOR_MATERIALS,
            ArmorItem.Type.CHESTPLATE,
            ModRarity.GREEN,
            (id, builder) -> builder.add(Attributes.ARMOR, new AttributeModifier(id, 4.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
    );
    public static final DeferredItem<BaseVanityArmorItem> GUY_FAWKES_MASK = registerVanityArmor("guy_fawkes_mask", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> GUY_FAWKES_HAT = registerVanityArmor("guy_fawkes_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> GUY_FAWKES_MASK_SET = registerVanityArmor("guy_fawkes_mask_set", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> MUMMY_MASK = registerVanityArmor("mummy_mask", "vanity_armor/mummy_set", ArmorItem.Type.HELMET, ModRarity.BLUE);
    public static final DeferredItem<BaseVanityArmorItem> MUMMY_SHIRT = registerVanityArmor("mummy_shirt", "vanity_armor/mummy_set", ArmorItem.Type.CHESTPLATE, ModRarity.BLUE);
    public static final DeferredItem<BaseVanityArmorItem> MUMMY_PANTS = registerVanityArmor("mummy_pants", "vanity_armor/mummy_set", ArmorItem.Type.LEGGINGS, ModRarity.BLUE);
    public static final DeferredItem<BaseVanityArmorItem> MUMMY_SHOES = registerVanityArmor("mummy_shoes", "vanity_armor/mummy_set", ArmorItem.Type.BOOTS, ModRarity.BLUE);

    public static final DeferredItem<BaseDyeItem> DYE = registerColoredDye("dye", 0x39C5BB);
    public static final DeferredItem<BaseDyeItem> RED_DYE = registerColoredDye("red_dye", 0xDB0909);
    public static final DeferredItem<BaseDyeItem> BRIGHT_RED_DYE = registerColoredDye("bright_red_dye", 0xFF4F4F);
    public static final DeferredItem<BaseDyeItem> ORANGE_DYE = registerColoredDye("orange_dye", 0xEC5a07);
    public static final DeferredItem<BaseDyeItem> BRIGHT_ORANGE_DYE = registerColoredDye("bright_orange_dye", 0xFF7B3E);
    public static final DeferredItem<BaseDyeItem> YELLOW_DYE = registerColoredDye("yellow_dye", 0xF0b007);
    public static final DeferredItem<BaseDyeItem> BRIGHT_YELLOW_DYE = registerColoredDye("bright_yellow_dye", 0xFFE07B);
    public static final DeferredItem<BaseDyeItem> LIME_DYE = registerColoredDye("lime_dye", 0xb7e013);
    public static final DeferredItem<BaseDyeItem> BRIGHT_LIME_DYE = registerColoredDye("bright_lime_dye", 0x9CE14D);
    public static final DeferredItem<BaseDyeItem> GREEN_DYE = registerColoredDye("green_dye", 0x2cdf09);
    public static final DeferredItem<BaseDyeItem> BRIGHT_GREEN_DYE = registerColoredDye("bright_green_dye", 0x9FD57A);
    public static final DeferredItem<BaseDyeItem> TEAL_DYE = registerColoredDye("teal_dye", 0x03c75b);
    public static final DeferredItem<BaseDyeItem> BRIGHT_TEAL_DYE = registerColoredDye("bright_teal_dye", 0x4CC29D);
    public static final DeferredItem<BaseDyeItem> CYAN_DYE = registerColoredDye("cyan_dye", 0x03c7a2);
    public static final DeferredItem<BaseDyeItem> BRIGHT_CYAN_DYE = registerColoredDye("bright_cyan_dye", 0x00D1C1);
    public static final DeferredItem<BaseDyeItem> SKY_BLUE_DYE = registerColoredDye("sky_blue_dye", 0x0699ce);
    public static final DeferredItem<BaseDyeItem> BRIGHT_SKY_BLUE_DYE = registerColoredDye("bright_sky_blue_dye", 0x8DC9E3);
    public static final DeferredItem<BaseDyeItem> BLUE_DYE = registerColoredDye("blue_dye", 0x203ebe);
    public static final DeferredItem<BaseDyeItem> BRIGHT_BLUE_DYE = registerColoredDye("bright_blue_dye", 0x4C65B3);
    public static final DeferredItem<BaseDyeItem> PURPLE_DYE = registerColoredDye("purple_dye", 0x6214d6);
    public static final DeferredItem<BaseDyeItem> BRIGHT_PURPLE_DYE = registerColoredDye("bright_purple_dye", 0x9A71F5);
    public static final DeferredItem<BaseDyeItem> VIOLET_DYE = registerColoredDye("violet_dye", 0xba14d6);
    public static final DeferredItem<BaseDyeItem> BRIGHT_VIOLET_DYE = registerColoredDye("bright_violet_dye", 0x9A5FD8);
    public static final DeferredItem<BaseDyeItem> PINK_DYE = registerColoredDye("pink_dye", 0xed10cc);
    public static final DeferredItem<BaseDyeItem> BRIGHT_PINK_DYE = registerColoredDye("bright_pink_dye", 0xFF75A7);
    public static final DeferredItem<BaseDyeItem> BLACK_DYE = registerColoredDye("black_dye", 0x1e1d20);
    public static final DeferredItem<BaseDyeItem> GRAY_DYE = registerColoredDye("gray_dye", 0x676764);
    public static final DeferredItem<BaseDyeItem> SILVER_DYE = registerColoredDye("silver_dye", 0xfffef6);
    public static final DeferredItem<BaseDyeItem> BROWN_DYE = registerColoredDye("brown_dye", 0x8b653f);

    public static final DeferredItem<Item> TEAM_DYE = ITEMS.register("team_dye", () -> new CustomRarityItem(ModRarity.BLUE));

    private static DeferredItem<BaseDyeItem> registerColoredDye(String name, int color) {
        return ITEMS.register(name, () -> {
            BaseDyeItem item = new BaseDyeItem(ModRarity.BLUE, color);
            COLORED_DYE_ITEMS.add(item);
            return item;
        });
    }

    private static DeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, ArmorItem.Type type, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem(geoName, type, rarity));
    }

    private static DeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, ArmorItem.Type type, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem("vanity_armor/" + name, type, rarity));
    }

    private static DeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem(geoName, material, type, properties, rarity));
    }

    private static DeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type, ModRarity rarity, BiConsumer<ResourceLocation, ItemAttributeModifiers.Builder> consumer) {
        return ITEMS.register(name, id -> {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
            consumer.accept(id, builder);
            return new BaseVanityArmorItem(geoName, material, type, new Item.Properties().attributes(builder.build()), rarity);
        });
    }
}
