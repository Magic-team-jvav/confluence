package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.function.TriFunction;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.item.paint.*;

import java.util.ArrayList;
import java.util.List;

public class PaintItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);
    public static final List<PaintItem> PAINT_ITEMS = new ArrayList<>();

    public static final RegistryObject<PaintbrushItem> PAINTBRUSH = registerTool("paintbrush", PaintbrushItem::new, false);
    public static final RegistryObject<PaintRollerItem> PAINT_ROLLER = registerTool("paint_roller", PaintRollerItem::new, false);
    public static final RegistryObject<PaintScraperItem> PAINT_SCRAPER = registerTool("paint_scraper", PaintScraperItem::new, false);
    public static final RegistryObject<PaintbrushItem> SPECTRE_PAINTBRUSH = registerTool("paintbrush", PaintbrushItem::new, true);
    public static final RegistryObject<PaintRollerItem> SPECTRE_PAINT_ROLLER = registerTool("paint_roller", PaintRollerItem::new, true);
    public static final RegistryObject<PaintScraperItem> SPECTRE_PAINT_SCRAPER = registerTool("paint_scraper", PaintScraperItem::new, true);
    public static final RegistryObject<EyedropperItem> EYEDROPPER = ITEMS.register("eyedropper", EyedropperItem::new);
    public static final RegistryObject<PaintItem> PAINT = registerPaint("paint", 0x39C5BB);

    public static final RegistryObject<PaintItem> RED_PAINT = registerPaint("red_paint", 0xdb0909);
    public static final RegistryObject<PaintItem> DEEP_RED_PAINT = registerPaint("deep_red_paint", 0x55051d);
    public static final RegistryObject<PaintItem> ORANGE_PAINT = registerPaint("orange_paint", 0xec5a07);
    public static final RegistryObject<PaintItem> DEEP_ORANGE_PAINT = registerPaint("deep_orange_paint", 0x6f1c03);
    public static final RegistryObject<PaintItem> YELLOW_PAINT = registerPaint("yellow_paint", 0xf0b007);
    public static final RegistryObject<PaintItem> DEEP_YELLOW_PAINT = registerPaint("deep_yellow_paint", 0x764403);
    public static final RegistryObject<PaintItem> LIME_PAINT = registerPaint("lime_paint", 0xb7e013);
    public static final RegistryObject<PaintItem> DEEP_LIME_PAINT = registerPaint("deep_lime_paint", 0x446e06);
    public static final RegistryObject<PaintItem> GREEN_PAINT = registerPaint("green_paint", 0x2cdf09);
    public static final RegistryObject<PaintItem> DEEP_GREEN_PAINT = registerPaint("deep_green_paint", 0x04600a);
    public static final RegistryObject<PaintItem> TEAL_PAINT = registerPaint("teal_paint", 0x03c75b);
    public static final RegistryObject<PaintItem> DEEP_TEAL_PAINT = registerPaint("deep_teal_paint", 0x015335);
    public static final RegistryObject<PaintItem> CYAN_PAINT = registerPaint("cyan_paint", 0x03c7a2);
    public static final RegistryObject<PaintItem> DEEP_CYAN_PAINT = registerPaint("deep_cyan_paint", 0x045f5e);
    public static final RegistryObject<PaintItem> SKY_BLUE_PAINT = registerPaint("sky_blue_paint", 0x0699ce);
    public static final RegistryObject<PaintItem> DEEP_SKY_BLUE_PAINT = registerPaint("deep_sky_blue_paint", 0x053866);
    public static final RegistryObject<PaintItem> BLUE_PAINT = registerPaint("blue_paint", 0x203ebe);
    public static final RegistryObject<PaintItem> DEEP_BLUE_PAINT = registerPaint("deep_blue_paint", 0x0b084b);
    public static final RegistryObject<PaintItem> PURPLE_PAINT = registerPaint("purple_paint", 0x6214d6);
    public static final RegistryObject<PaintItem> DEEP_PURPLE_PAINT = registerPaint("deep_purple_paint", 0x26126b);
    public static final RegistryObject<PaintItem> VIOLET_PAINT = registerPaint("violet_paint", 0xba14d6);
    public static final RegistryObject<PaintItem> DEEP_VIOLET_PAINT = registerPaint("deep_violet_paint", 0x420757);
    public static final RegistryObject<PaintItem> PINK_PAINT = registerPaint("pink_paint", 0xed10cc);
    public static final RegistryObject<PaintItem> DEEP_PINK_PAINT = registerPaint("deep_pink_paint", 0x6a095e);
    public static final RegistryObject<PaintItem> BLACK_PAINT = registerPaint("black_paint", 0x1e1d20);
    public static final RegistryObject<PaintItem> GRAY_PAINT = registerPaint("gray_paint", 0x676764);
    public static final RegistryObject<PaintItem> WHITE_PAINT = registerPaint("white_paint", 0xfffef6);
    public static final RegistryObject<PaintItem> BROWN_PAINT = registerPaint("brown_paint", 0x8b653f);
    public static final RegistryObject<PaintItem> SHADOW_PAINT = registerPaint("shadow_paint", 0x000000);
    public static final RegistryObject<PaintItem> NEGATIVE_PAINT = registerPaint("negative_paint", BrushData.NEGATIVE_COLOR);
    public static final RegistryObject<PaintItem> ILLUMINANT_COATING = registerPaint("illuminant_coating", BrushData.ILLUMINANT_COLOR);
    public static final RegistryObject<PaintItem> ECHO_COATING = registerPaint("echo_coating", BrushData.ECHO_COLOR);

    private static RegistryObject<PaintItem> registerPaint(String name, int color) {
        return ITEMS.register(name, () -> {
            PaintItem paintItem = new PaintItem(color);
            PAINT_ITEMS.add(paintItem);
            return paintItem;
        });
    }

    private static <T extends Item> RegistryObject<T> registerTool(String suffix, TriFunction<Item.Properties, ModRarity, List<Component>, T> factory, boolean spectre) {
        return ITEMS.register(spectre ? "spectre_" + suffix : suffix, () -> {
            Item.Properties properties = new Item.Properties();
            if (spectre) properties.attributes(ItemAttributeModifiers.builder().add(
                    Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(ModItems.BASE_BLOCK_INTERACTION_RANGE_ID, 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            ).build());
            return factory.apply(properties, ModRarity.WHITE, TooltipItem.getTooltipsFromString(suffix, 2, ChatFormatting.GRAY));
        });
    }
}
