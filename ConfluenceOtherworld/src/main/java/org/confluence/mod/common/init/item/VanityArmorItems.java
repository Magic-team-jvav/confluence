package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
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
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.item.common.BaseVanityArmorItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VanityArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final List<DeferredHolder<Item, ? extends Item>> DYE_ITEMS = new ArrayList<>();

    public static final DeferredItem<BaseVanityArmorItem> GOLD_CROWN = ITEMS.register("gold_crown", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE) {
        @Override
        public @NotNull Holder<ArmorMaterial> getMaterial() {
            return ArmorMaterials.GOLD;
        }
    });
    public static final DeferredItem<BaseVanityArmorItem> PLATINUM_CROWN = ITEMS.register("platinum_crown", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> ROBE = ITEMS.register("robe", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> TOP_HAT = ITEMS.register("top_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_SHIRT = ITEMS.register("tuxedo_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_PANTS = ITEMS.register("tuxedo_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> TUXEDO_SHOES = ITEMS.register("tuxedo_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> SUMMER_HAT = ITEMS.register("summer_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> BUNNY_HOOD = ITEMS.register("bunny_hood", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_HAT = ITEMS.register("plumbers_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_SHIRT = ITEMS.register("plumbers_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_PANTS = ITEMS.register("plumbers_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> PLUMBERS_SHOES = ITEMS.register("plumbers_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> HEROS_HAT = ITEMS.register("heros_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> HEROS_SHIRT = ITEMS.register("heros_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> HEROS_PANTS = ITEMS.register("heros_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> HEROS_SHOES = ITEMS.register("heros_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_HAT = ITEMS.register("archaeologists_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_JACKET = ITEMS.register("archaeologists_jacket", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_PANTS = ITEMS.register("archaeologists_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> ARCHAEOLOGISTS_SHOES = ITEMS.register("archaeologists_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_HAT = ITEMS.register("clothiers_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_JACKET = ITEMS.register("clothiers_jacket", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_PANTS = ITEMS.register("clothiers_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> CLOTHIERS_SHOES = ITEMS.register("clothiers_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> ROBOT_HAT = ITEMS.register("robot_hat", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_WIG = ITEMS.register("familiar_wig", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_SHIRT = ITEMS.register("familiar_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_PANTS = ITEMS.register("familiar_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> FAMILIAR_SHOES = ITEMS.register("familiar_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> MIME_MASK = ITEMS.register("mime_mask", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHIRT = ITEMS.register("the_doctors_shirt", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_PANTS = ITEMS.register("the_doctors_pants", () -> new BaseVanityArmorItem(ArmorItem.Type.LEGGINGS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> THE_DOCTORS_SHOES = ITEMS.register("the_doctors_shoes", () -> new BaseVanityArmorItem(ArmorItem.Type.BOOTS, ModRarity.WHITE));
    public static final DeferredItem<BaseVanityArmorItem> DEAD_MANS_SWEATER = ITEMS.register("dead_mans_seater", () -> new BaseVanityArmorItem(ArmorItem.Type.CHESTPLATE, ModRarity.GREEN));

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
}
