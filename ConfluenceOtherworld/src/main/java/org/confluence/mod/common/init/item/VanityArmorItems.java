package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.vanity_armor.BaseDyeItem;
import org.confluence.mod.common.item.vanity_armor.BaseVanityArmorItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class VanityArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final List<BaseDyeItem> DYE_ITEMS = new ArrayList<>();

    public static final Supplier<BaseVanityArmorItem> GOLD_CROWN = ITEMS.register("gold_crown", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE) {
        @Override
        public @NotNull Holder<ArmorMaterial> getMaterial() {
            return ArmorMaterials.GOLD;
        }
    });
    public static final Supplier<BaseVanityArmorItem> PLATINUM_CROWN = ITEMS.register("platinum_crown", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> ROBE = ITEMS.register("robe", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> TOP_HAT = ITEMS.register("top_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> TUXEDO_SHIRT = ITEMS.register("tuxedo_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> TUXEDO_PANTS = ITEMS.register("tuxedo_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> TUXEDO_SHOES = ITEMS.register("tuxedo_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> SUMMER_HAT = ITEMS.register("summer_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> BUNNY_HOOD = ITEMS.register("bunny_hood", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> PLUMBERS_HAT = ITEMS.register("plumbers_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> PLUMBERS_SHIRT = ITEMS.register("plumbers_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> PLUMBERS_PANTS = ITEMS.register("plumbers_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> PLUMBERS_SHOES = ITEMS.register("plumbers_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> HEROS_HAT = ITEMS.register("heros_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> HEROS_SHIRT = ITEMS.register("heros_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> HEROS_PANTS = ITEMS.register("heros_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> HEROS_SHOES = ITEMS.register("heros_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> ARCHAEOLOGISTS_HAT = ITEMS.register("archaeologists_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> ARCHAEOLOGISTS_JACKET = ITEMS.register("archaeologists_jacket", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> ARCHAEOLOGISTS_PANTS = ITEMS.register("archaeologists_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> ARCHAEOLOGISTS_SHOES = ITEMS.register("archaeologists_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> CLOTHIERS_HAT = ITEMS.register("clothiers_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> CLOTHIERS_JACKET = ITEMS.register("clothiers_jacket", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> CLOTHIERS_PANTS = ITEMS.register("clothiers_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> CLOTHIERS_SHOES = ITEMS.register("clothiers_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> ROBOT_HAT = ITEMS.register("robot_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> FAMILIAR_WIG = ITEMS.register("familiar_wig", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> FAMILIAR_SHIRT = ITEMS.register("familiar_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> FAMILIAR_PANTS = ITEMS.register("familiar_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> FAMILIAR_SHOES = ITEMS.register("familiar_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> MIME_MASK = ITEMS.register("mime_mask", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> THE_DOCTORS_SHIRT = ITEMS.register("the_doctors_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> THE_DOCTORS_PANTS = ITEMS.register("the_doctors_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> THE_DOCTORS_SHOES = ITEMS.register("the_doctors_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final Supplier<BaseVanityArmorItem> DEAD_MANS_SWEATER = ITEMS.register("dead_mans_seater", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.GREEN));

    public static final Supplier<BaseDyeItem> RED_DYE = registerDye("red_dye", ModRarity.BLUE, 0xFFDB0909);
    public static final Supplier<BaseDyeItem> BRIGHT_RED_DYE = registerDye("bright_red_dye", ModRarity.BLUE, 0xFFFF4F4F); // Adjusted to a brighter red
    public static final Supplier<BaseDyeItem> ORANGE_DYE = registerDye("orange_dye", ModRarity.BLUE, 0xFFEC5a07);
    public static final Supplier<BaseDyeItem> BRIGHT_ORANGE_DYE = registerDye("bright_orange_dye", ModRarity.BLUE, 0xFFFF7B3E); // Adjusted to a brighter orange
    public static final Supplier<BaseDyeItem> YELLOW_DYE = registerDye("yellow_dye", ModRarity.BLUE, 0xFFF0b007);
    public static final Supplier<BaseDyeItem> BRIGHT_YELLOW_DYE = registerDye("bright_yellow_dye", ModRarity.BLUE, 0xFFFFE07B); // Adjusted to a brighter yellow
    public static final Supplier<BaseDyeItem> LIME_DYE = registerDye("lime_dye", ModRarity.BLUE, 0xFFb7e013);
    public static final Supplier<BaseDyeItem> BRIGHT_LIME_DYE = registerDye("bright_lime_dye", ModRarity.BLUE, 0xFF9CE14D); // Adjusted to a brighter lime
    public static final Supplier<BaseDyeItem> GREEN_DYE = registerDye("green_dye", ModRarity.BLUE, 0xFF2cdf09);
    public static final Supplier<BaseDyeItem> BRIGHT_GREEN_DYE = registerDye("bright_green_dye", ModRarity.BLUE, 0xFF9FD57A); // Adjusted to a brighter green
    public static final Supplier<BaseDyeItem> TEAL_DYE = registerDye("teal_dye", ModRarity.BLUE, 0xFF03c75b);
    public static final Supplier<BaseDyeItem> BRIGHT_TEAL_DYE = registerDye("bright_teal_dye", ModRarity.BLUE, 0xFF4CC29D); // Adjusted to a brighter teal
    public static final Supplier<BaseDyeItem> CYAN_DYE = registerDye("cyan_dye", ModRarity.BLUE, 0xFF03c7a2);
    public static final Supplier<BaseDyeItem> BRIGHT_CYAN_DYE = registerDye("bright_cyan_dye", ModRarity.BLUE, 0xFF00D1C1); // Adjusted to a brighter cyan
    public static final Supplier<BaseDyeItem> SKY_BLUE_DYE = registerDye("sky_blue_dye", ModRarity.BLUE, 0xFF0699ce);
    public static final Supplier<BaseDyeItem> BRIGHT_SKY_BLUE_DYE = registerDye("bright_sky_blue_dye", ModRarity.BLUE, 0xFF8DC9E3); // Adjusted to a brighter sky blue
    public static final Supplier<BaseDyeItem> BLUE_DYE = registerDye("blue_dye", ModRarity.BLUE, 0xFF203ebe);
    public static final Supplier<BaseDyeItem> BRIGHT_BLUE_DYE = registerDye("bright_blue_dye", ModRarity.BLUE, 0xFF4C65B3); // Adjusted to a brighter blue
    public static final Supplier<BaseDyeItem> PURPLE_DYE = registerDye("purple_dye", ModRarity.BLUE, 0xFF6214d6);
    public static final Supplier<BaseDyeItem> BRIGHT_PURPLE_DYE = registerDye("bright_purple_dye", ModRarity.BLUE, 0xFF9A71F5); // Adjusted to a brighter purple
    public static final Supplier<BaseDyeItem> VIOLET_DYE = registerDye("violet_dye", ModRarity.BLUE, 0xFFba14d6);
    public static final Supplier<BaseDyeItem> BRIGHT_VIOLET_DYE = registerDye("bright_violet_dye", ModRarity.BLUE, 0xFF9A5FD8); // Adjusted to a brighter violet
    public static final Supplier<BaseDyeItem> PINK_DYE = registerDye("pink_dye", ModRarity.BLUE, 0xFFed10cc);
    public static final Supplier<BaseDyeItem> BRIGHT_PINK_DYE = registerDye("bright_pink_dye", ModRarity.BLUE, 0xFFFF75A7); // Adjusted to a brighter pink
    public static final Supplier<BaseDyeItem> BLACK_DYE = registerDye("black_dye", ModRarity.BLUE, 0xFF1e1d20);
    public static final Supplier<BaseDyeItem> GRAY_DYE = registerDye("gray_dye", ModRarity.BLUE, 0xFF676764);
    public static final Supplier<BaseDyeItem> SILVER_DYE = registerDye("silver_dye", ModRarity.BLUE, 0xFFfffef6);
    public static final Supplier<BaseDyeItem> BROWN_DYE = registerDye("brown_dye", ModRarity.BLUE, 0xFF8b653f);

    public static final Supplier<Item> TEAM_DYE = ITEMS.register("team_dye", () -> new CustomRarityItem(ModRarity.BLUE));

    private static Supplier<BaseDyeItem> registerDye(String name, ModRarity rarity, int color) {
        return ITEMS.register(name, () -> {
            BaseDyeItem dyeItem = new BaseDyeItem(rarity, color);
            DYE_ITEMS.add(dyeItem);
            return dyeItem;
        });
    }
}
