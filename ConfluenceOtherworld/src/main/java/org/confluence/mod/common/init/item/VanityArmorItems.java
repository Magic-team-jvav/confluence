package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.common.item.vanity_armor.BaseDyeItem;
import org.confluence.mod.common.item.vanity_armor.BaseVanityArmorItem;
import org.confluence.terra_curio.common.component.ModRarity;
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
