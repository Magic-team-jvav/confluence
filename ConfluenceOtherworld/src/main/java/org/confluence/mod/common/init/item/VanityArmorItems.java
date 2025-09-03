package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModArmorMaterials;
import org.confluence.mod.common.item.armor.NormalArmorItem;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.item.common.BaseVanityArmorItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VanityArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final List<DeferredHolder<Item, ? extends Item>> DYE_ITEMS = new ArrayList<>();

    public static final DeferredItem<BaseVanityArmorItem> GOLD_CROWN = registerVanityArmor("gold_crown", "vanity_armor/gold_crown", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLATINUM_CROWN = registerVanityArmor("platinum_crown", "vanity_armor/platinum_crown", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ROBE = registerVanityArmor("robe", "vanity_armor/robe", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TOP_HAT = registerVanityArmor("top_hat", "vanity_armor/top_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_SHIRT = registerVanityArmor("tuxedo_shirt", "vanity_armor/tuxedo_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_PANTS = registerVanityArmor("tuxedo_pants", "vanity_armor/tuxedo_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_SHOES = registerVanityArmor("tuxedo_shoes", "vanity_armor/tuxedo_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> SUMMER_HAT = registerVanityArmor("summer_hat", "vanity_armor/summer_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> BUNNY_HOOD = registerVanityArmor("bunny_hood", "vanity_armor/bunny_hood", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_HAT = registerVanityArmor("plumbers_hat", "vanity_armor/plumbers_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_SHIRT = registerVanityArmor("plumbers_shirt", "vanity_armor/plumbers_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_PANTS = registerVanityArmor("plumbers_pants", "vanity_armor/plumbers_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_SHOES = registerVanityArmor("plumbers_shoes", "vanity_armor/plumbers_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_HAT = registerVanityArmor("heros_hat", "vanity_armor/heros_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_SHIRT = registerVanityArmor("heros_shirt", "vanity_armor/heros_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_PANTS = registerVanityArmor("heros_pants", "vanity_armor/heros_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> HEROS_SHOES = registerVanityArmor("heros_shoes", "vanity_armor/heros_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_HAT = registerVanityArmor("archaeologists_hat", "vanity_armor/archaeologists_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_JACKET = registerVanityArmor("archaeologists_jacket", "vanity_armor/archaeologists_jacket", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_PANTS = registerVanityArmor("archaeologists_pants", "vanity_armor/archaeologists_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_SHOES = registerVanityArmor("archaeologists_shoes", "vanity_armor/archaeologists_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_HAT = registerVanityArmor("clothiers_hat", "vanity_armor/clothiers_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_JACKET = registerVanityArmor("clothiers_jacket", "vanity_armor/clothiers_jacket", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_PANTS = registerVanityArmor("clothiers_pants", "vanity_armor/clothiers_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_SHOES = registerVanityArmor("clothiers_shoes", "vanity_armor/clothiers_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> ROBOT_HAT = registerVanityArmor("robot_hat", "vanity_armor/robot_hat", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_WIG = registerVanityArmor("familiar_wig", "vanity_armor/familiar_wig", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_SHIRT = registerVanityArmor("familiar_shirt", "vanity_armor/familiar_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_PANTS = registerVanityArmor("familiar_pants", "vanity_armor/familiar_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_SHOES = registerVanityArmor("familiar_shoes", "vanity_armor/familiar_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> MIME_MASK = registerVanityArmor("mime_mask", "vanity_armor/mime_mask", ArmorItem.Type.HELMET, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHIRT = registerVanityArmor("the_doctors_shirt", "vanity_armor/the_doctors_shirt", ArmorItem.Type.CHESTPLATE, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_PANTS = registerVanityArmor("the_doctors_pants", "vanity_armor/the_doctors_pants", ArmorItem.Type.LEGGINGS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHOES = registerVanityArmor("the_doctors_shoes", "vanity_armor/the_doctors_shoes", ArmorItem.Type.BOOTS, ModRarity.WHITE);
    public static final DeferredItem<BaseVanityArmorItem> DEAD_MANS_SWEATER = registerVanityArmor("dead_mans_seater", "vanity_armor/dead_mans_seater", ArmorItem.Type.CHESTPLATE, ModRarity.GREEN);

    public static final DeferredItem<BaseDyeItem> DYE = registerDye("dye", 0x39C5BB);
    public static final DeferredItem<BaseDyeItem> RED_DYE = registerDye("red_dye", 0xDB0909);
    public static final DeferredItem<BaseDyeItem> BRIGHT_RED_DYE = registerDye("bright_red_dye", 0xFF4F4F);
    public static final DeferredItem<BaseDyeItem> ORANGE_DYE = registerDye("orange_dye", 0xEC5a07);
    public static final DeferredItem<BaseDyeItem> BRIGHT_ORANGE_DYE = registerDye("bright_orange_dye", 0xFF7B3E);
    public static final DeferredItem<BaseDyeItem> YELLOW_DYE = registerDye("yellow_dye", 0xF0b007);
    public static final DeferredItem<BaseDyeItem> BRIGHT_YELLOW_DYE = registerDye("bright_yellow_dye", 0xFFE07B);
    public static final DeferredItem<BaseDyeItem> LIME_DYE = registerDye("lime_dye", 0xb7e013);
    public static final DeferredItem<BaseDyeItem> BRIGHT_LIME_DYE = registerDye("bright_lime_dye", 0x9CE14D);
    public static final DeferredItem<BaseDyeItem> GREEN_DYE = registerDye("green_dye", 0x2cdf09);
    public static final DeferredItem<BaseDyeItem> BRIGHT_GREEN_DYE = registerDye("bright_green_dye", 0x9FD57A);
    public static final DeferredItem<BaseDyeItem> TEAL_DYE = registerDye("teal_dye", 0x03c75b);
    public static final DeferredItem<BaseDyeItem> BRIGHT_TEAL_DYE = registerDye("bright_teal_dye", 0x4CC29D);
    public static final DeferredItem<BaseDyeItem> CYAN_DYE = registerDye("cyan_dye", 0x03c7a2);
    public static final DeferredItem<BaseDyeItem> BRIGHT_CYAN_DYE = registerDye("bright_cyan_dye", 0x00D1C1);
    public static final DeferredItem<BaseDyeItem> SKY_BLUE_DYE = registerDye("sky_blue_dye", 0x0699ce);
    public static final DeferredItem<BaseDyeItem> BRIGHT_SKY_BLUE_DYE = registerDye("bright_sky_blue_dye", 0x8DC9E3);
    public static final DeferredItem<BaseDyeItem> BLUE_DYE = registerDye("blue_dye", 0x203ebe);
    public static final DeferredItem<BaseDyeItem> BRIGHT_BLUE_DYE = registerDye("bright_blue_dye", 0x4C65B3);
    public static final DeferredItem<BaseDyeItem> PURPLE_DYE = registerDye("purple_dye", 0x6214d6);
    public static final DeferredItem<BaseDyeItem> BRIGHT_PURPLE_DYE = registerDye("bright_purple_dye", 0x9A71F5);
    public static final DeferredItem<BaseDyeItem> VIOLET_DYE = registerDye("violet_dye", 0xba14d6);
    public static final DeferredItem<BaseDyeItem> BRIGHT_VIOLET_DYE = registerDye("bright_violet_dye", 0x9A5FD8);
    public static final DeferredItem<BaseDyeItem> PINK_DYE = registerDye("pink_dye", 0xed10cc);
    public static final DeferredItem<BaseDyeItem> BRIGHT_PINK_DYE = registerDye("bright_pink_dye", 0xFF75A7);
    public static final DeferredItem<BaseDyeItem> BLACK_DYE = registerDye("black_dye", 0x1e1d20);
    public static final DeferredItem<BaseDyeItem> GRAY_DYE = registerDye("gray_dye", 0x676764);
    public static final DeferredItem<BaseDyeItem> SILVER_DYE = registerDye("silver_dye", 0xfffef6);
    public static final DeferredItem<BaseDyeItem> BROWN_DYE = registerDye("brown_dye", 0x8b653f);

    public static final DeferredItem<Item> TEAM_DYE = ITEMS.register("team_dye", () -> new CustomRarityItem(ModRarity.BLUE));

    private static DeferredItem<BaseDyeItem> registerDye(String name, int color) {
        DeferredItem<BaseDyeItem> item = ITEMS.register(name, () -> new BaseDyeItem(ModRarity.BLUE, color));
        DYE_ITEMS.add(item);
        return item;
    }
    private static DeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, ArmorItem.Type type, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem(geoName, type, rarity));
    }

    private static DeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, Holder<ArmorMaterial> material,
                                                                         ArmorItem.Type type, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem(geoName, material, type, rarity));
    }

    private static DeferredItem<BaseVanityArmorItem> registerVanityArmor(String name, String geoName, Holder<ArmorMaterial> material,
                                                                         ArmorItem.Type type, Item.Properties properties, ModRarity rarity) {
        return ITEMS.register(name, () -> new BaseVanityArmorItem(geoName, material, type, properties, rarity));
    }
}
