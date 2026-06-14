package org.confluence.mod.common.init.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.armor.ModArmorMaterials;
import org.confluence.mod.common.item.armor.BaseVanityArmorItem;
import org.confluence.mod.common.item.armor.FamiliarVanityArmorItem;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class VanityArmorItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final List<BaseDyeItem> COLORED_DYE_ITEMS = new ArrayList<>();

    public static final PortDeferredItem<BaseVanityArmorItem> GOLD_CROWN = registerVanityArmor("gold_crown", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> PLATINUM_CROWN = registerVanityArmor("platinum_crown", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> ROBE = registerVanityArmor("robe", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> TOP_HAT = registerVanityArmor("top_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> TUXEDO_SHIRT = registerVanityArmor("tuxedo_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> TUXEDO_PANTS = registerVanityArmor("tuxedo_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> TUXEDO_SHOES = registerVanityArmor("tuxedo_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> SUMMER_HAT = registerVanityArmor("summer_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> BUNNY_HOOD = registerVanityArmor("bunny_hood", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> PLUMBERS_HAT = registerVanityArmor("plumbers_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> PLUMBERS_SHIRT = registerVanityArmor("plumbers_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> PLUMBERS_PANTS = registerVanityArmor("plumbers_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> PLUMBERS_SHOES = registerVanityArmor("plumbers_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> HEROS_HAT = registerVanityArmor("heros_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> HEROS_SHIRT = registerVanityArmor("heros_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> HEROS_PANTS = registerVanityArmor("heros_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> HEROS_SHOES = registerVanityArmor("heros_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_HAT = registerVanityArmor("archaeologists_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_JACKET = registerVanityArmor("archaeologists_jacket", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_PANTS = registerVanityArmor("archaeologists_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_SHOES = registerVanityArmor("archaeologists_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> CLOTHIERS_HAT = registerVanityArmor("clothiers_hat", "vanity_armor/clothiers_set", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> CLOTHIERS_JACKET = registerVanityArmor("clothiers_jacket", "vanity_armor/clothiers_set", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> CLOTHIERS_PANTS = registerVanityArmor("clothiers_pants", "vanity_armor/clothiers_set", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> CLOTHIERS_SHOES = registerVanityArmor("clothiers_shoes", "vanity_armor/clothiers_set", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> ROBOT_HAT = registerVanityArmor("robot_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> FAMILIAR_WIG = ITEMS.register("familiar_wig", () -> new FamiliarVanityArmorItem(ArmorItem.Type.HELMET));
    public static final PortDeferredItem<BaseVanityArmorItem> FAMILIAR_SHIRT = ITEMS.register("familiar_shirt", () -> new FamiliarVanityArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final PortDeferredItem<BaseVanityArmorItem> FAMILIAR_PANTS = ITEMS.register("familiar_pants", () -> new FamiliarVanityArmorItem(ArmorItem.Type.LEGGINGS));
    public static final PortDeferredItem<BaseVanityArmorItem> FAMILIAR_SHOES = ITEMS.register("familiar_shoes", () -> new FamiliarVanityArmorItem(ArmorItem.Type.BOOTS));
    public static final PortDeferredItem<BaseVanityArmorItem> MIME_MASK = registerVanityArmor("mime_mask", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHIRT = registerVanityArmor("the_doctors_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> THE_DOCTORS_PANTS = registerVanityArmor("the_doctors_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHOES = registerVanityArmor("the_doctors_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> DEAD_MANS_SWEATER = registerVanityArmor(
            "dead_mans_seater",
            "vanity_armor/dead_mans_seater",
            ModArmorMaterials.VANITY_ARMOR_MATERIALS.get(),
            ArmorItem.Type.CHESTPLATE,
            ModRarity.GREEN,
            (id, builder) -> builder.add(Attributes.ARMOR, new PortAttributeModifier(id, 4.0, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.CHEST)
    );
    public static final PortDeferredItem<BaseVanityArmorItem> GUY_FAWKES_MASK = registerVanityArmor("guy_fawkes_mask", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> GUY_FAWKES_HAT = registerVanityArmor("guy_fawkes_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> GUY_FAWKES_MASK_SET = registerVanityArmor("guy_fawkes_mask_set", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> MUMMY_MASK = registerVanityArmor("mummy_mask", "vanity_armor/mummy_set", ArmorItem.Type.HELMET, ModRarity.BLUE);
    public static final PortDeferredItem<BaseVanityArmorItem> MUMMY_SHIRT = registerVanityArmor("mummy_shirt", "vanity_armor/mummy_set", ArmorItem.Type.CHESTPLATE, ModRarity.BLUE);
    public static final PortDeferredItem<BaseVanityArmorItem> MUMMY_PANTS = registerVanityArmor("mummy_pants", "vanity_armor/mummy_set", ArmorItem.Type.LEGGINGS, ModRarity.BLUE);
    public static final PortDeferredItem<BaseVanityArmorItem> MUMMY_SHOES = registerVanityArmor("mummy_shoes", "vanity_armor/mummy_set", ArmorItem.Type.BOOTS, ModRarity.BLUE);
    public static final PortDeferredItem<BaseVanityArmorItem> SUNGLASSES = registerVanityArmor("sunglasses", ArmorItem.Type.HELMET, ModRarity.GREEN);
    public static final PortDeferredItem<BaseVanityArmorItem> AVIATORS = registerVanityArmor("aviators", ArmorItem.Type.HELMET, ModRarity.CYAN);
    public static final PortDeferredItem<BaseVanityArmorItem> EYE_PATCH = registerVanityArmor("eye_patch", ArmorItem.Type.HELMET, ModRarity.BLUE);
    public static final PortDeferredItem<BaseVanityArmorItem> HALLOWED_CROWN = registerVanityArmor("hallowed_crown", ArmorItem.Type.HELMET, ModRarity.PINK);
    public static final PortDeferredItem<BaseVanityArmorItem> WIZARDS_HAT = registerVanityArmor("wizards_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseVanityArmorItem> PEDDLERS_HAT = registerVanityArmor("peddlers_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    // public static final PortDeferredItem<BaseVanityArmorItem> BUCKET_HAT = registerVanityArmor("bucket_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final PortDeferredItem<BaseDyeItem> DYE = registerColoredDye("dye", 0x39C5BB);
    public static final PortDeferredItem<BaseDyeItem> RED_DYE = registerColoredDye("red_dye", 0xDB0909);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_RED_DYE = registerColoredDye("bright_red_dye", 0xFF4F4F);
    public static final PortDeferredItem<BaseDyeItem> ORANGE_DYE = registerColoredDye("orange_dye", 0xEC5a07);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_ORANGE_DYE = registerColoredDye("bright_orange_dye", 0xFF7B3E);
    public static final PortDeferredItem<BaseDyeItem> YELLOW_DYE = registerColoredDye("yellow_dye", 0xF0b007);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_YELLOW_DYE = registerColoredDye("bright_yellow_dye", 0xFFE07B);
    public static final PortDeferredItem<BaseDyeItem> LIME_DYE = registerColoredDye("lime_dye", 0xb7e013);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_LIME_DYE = registerColoredDye("bright_lime_dye", 0x9CE14D);
    public static final PortDeferredItem<BaseDyeItem> GREEN_DYE = registerColoredDye("green_dye", 0x2cdf09);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_GREEN_DYE = registerColoredDye("bright_green_dye", 0x9FD57A);
    public static final PortDeferredItem<BaseDyeItem> TEAL_DYE = registerColoredDye("teal_dye", 0x03c75b);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_TEAL_DYE = registerColoredDye("bright_teal_dye", 0x4CC29D);
    public static final PortDeferredItem<BaseDyeItem> CYAN_DYE = registerColoredDye("cyan_dye", 0x03c7a2);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_CYAN_DYE = registerColoredDye("bright_cyan_dye", 0x00D1C1);
    public static final PortDeferredItem<BaseDyeItem> SKY_BLUE_DYE = registerColoredDye("sky_blue_dye", 0x0699ce);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_SKY_BLUE_DYE = registerColoredDye("bright_sky_blue_dye", 0x8DC9E3);
    public static final PortDeferredItem<BaseDyeItem> BLUE_DYE = registerColoredDye("blue_dye", 0x203ebe);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_BLUE_DYE = registerColoredDye("bright_blue_dye", 0x4C65B3);
    public static final PortDeferredItem<BaseDyeItem> PURPLE_DYE = registerColoredDye("purple_dye", 0x6214d6);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_PURPLE_DYE = registerColoredDye("bright_purple_dye", 0x9A71F5);
    public static final PortDeferredItem<BaseDyeItem> VIOLET_DYE = registerColoredDye("violet_dye", 0xba14d6);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_VIOLET_DYE = registerColoredDye("bright_violet_dye", 0x9A5FD8);
    public static final PortDeferredItem<BaseDyeItem> PINK_DYE = registerColoredDye("pink_dye", 0xed10cc);
    public static final PortDeferredItem<BaseDyeItem> BRIGHT_PINK_DYE = registerColoredDye("bright_pink_dye", 0xFF75A7);
    public static final PortDeferredItem<BaseDyeItem> BLACK_DYE = registerColoredDye("black_dye", 0x1e1d20);
    public static final PortDeferredItem<BaseDyeItem> GRAY_DYE = registerColoredDye("gray_dye", 0x676764);
    public static final PortDeferredItem<BaseDyeItem> SILVER_DYE = registerColoredDye("silver_dye", 0xfffef6);
    public static final PortDeferredItem<BaseDyeItem> BROWN_DYE = registerColoredDye("brown_dye", 0x8b653f);

    public static final PortDeferredItem<Item> TEAM_DYE = ITEMS.register("team_dye", () -> new CustomRarityItem(ModRarity.BLUE));

    private static PortDeferredItem<BaseDyeItem> registerColoredDye(String name, int color) {
        return ITEMS.register(name, () -> {
            BaseDyeItem item = new BaseDyeItem(ModRarity.BLUE, color);
            COLORED_DYE_ITEMS.add(item);
            return item;
        });
    }

    private static PortDeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, ArmorItem.Type type, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem(geoName, type, rarity));
    }

    private static PortDeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, ArmorItem.Type type, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem("vanity_armor/" + name, type, rarity));
    }

    private static PortDeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, ArmorMaterial material, ArmorItem.Type type, PortItem.PortProperties properties, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem(geoName, material, type, properties, rarity));
    }

    private static PortDeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, ArmorMaterial material, ArmorItem.Type type, ModRarity rarity, BiConsumer<ResourceLocation, PortItemAttributeModifiers.PortBuilder> consumer) {
        return ITEMS.register(name, id -> {
            PortItemAttributeModifiers.PortBuilder builder = PortItemAttributeModifiers.builder();
            consumer.accept(id, builder);
            return new BaseVanityArmorItem(geoName, material, type, new PortItem.PortProperties().attributes(builder.build()), rarity);
        });
    }
}
