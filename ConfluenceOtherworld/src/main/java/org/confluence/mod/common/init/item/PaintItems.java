package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.function.TriFunction;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.item.paint.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.ArrayList;
import java.util.List;

public class PaintItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final List<PaintItem> PAINT_ITEMS = new ArrayList<>();

    public static final PortDeferredItem<PaintbrushItem> PAINTBRUSH = registerTool("paintbrush", PaintbrushItem::new, false);
    public static final PortDeferredItem<PaintRollerItem> PAINT_ROLLER = registerTool("paint_roller", PaintRollerItem::new, false);
    public static final PortDeferredItem<PaintScraperItem> PAINT_SCRAPER = registerTool("paint_scraper", PaintScraperItem::new, false);
    public static final PortDeferredItem<PaintbrushItem> SPECTRE_PAINTBRUSH = registerTool("paintbrush", PaintbrushItem::new, true);
    public static final PortDeferredItem<PaintRollerItem> SPECTRE_PAINT_ROLLER = registerTool("paint_roller", PaintRollerItem::new, true);
    public static final PortDeferredItem<PaintScraperItem> SPECTRE_PAINT_SCRAPER = registerTool("paint_scraper", PaintScraperItem::new, true);
    public static final PortDeferredItem<EyedropperItem> EYEDROPPER = ITEMS.register("eyedropper", EyedropperItem::new);
    public static final PortDeferredItem<PaintItem> PAINT = registerPaint("paint", 0x39C5BB);

    public static final PortDeferredItem<PaintItem> RED_PAINT = registerPaint("red_paint", 0xdb0909);
    public static final PortDeferredItem<PaintItem> DEEP_RED_PAINT = registerPaint("deep_red_paint", 0x55051d);
    public static final PortDeferredItem<PaintItem> ORANGE_PAINT = registerPaint("orange_paint", 0xec5a07);
    public static final PortDeferredItem<PaintItem> DEEP_ORANGE_PAINT = registerPaint("deep_orange_paint", 0x6f1c03);
    public static final PortDeferredItem<PaintItem> YELLOW_PAINT = registerPaint("yellow_paint", 0xf0b007);
    public static final PortDeferredItem<PaintItem> DEEP_YELLOW_PAINT = registerPaint("deep_yellow_paint", 0x764403);
    public static final PortDeferredItem<PaintItem> LIME_PAINT = registerPaint("lime_paint", 0xb7e013);
    public static final PortDeferredItem<PaintItem> DEEP_LIME_PAINT = registerPaint("deep_lime_paint", 0x446e06);
    public static final PortDeferredItem<PaintItem> GREEN_PAINT = registerPaint("green_paint", 0x2cdf09);
    public static final PortDeferredItem<PaintItem> DEEP_GREEN_PAINT = registerPaint("deep_green_paint", 0x04600a);
    public static final PortDeferredItem<PaintItem> TEAL_PAINT = registerPaint("teal_paint", 0x03c75b);
    public static final PortDeferredItem<PaintItem> DEEP_TEAL_PAINT = registerPaint("deep_teal_paint", 0x015335);
    public static final PortDeferredItem<PaintItem> CYAN_PAINT = registerPaint("cyan_paint", 0x03c7a2);
    public static final PortDeferredItem<PaintItem> DEEP_CYAN_PAINT = registerPaint("deep_cyan_paint", 0x045f5e);
    public static final PortDeferredItem<PaintItem> SKY_BLUE_PAINT = registerPaint("sky_blue_paint", 0x0699ce);
    public static final PortDeferredItem<PaintItem> DEEP_SKY_BLUE_PAINT = registerPaint("deep_sky_blue_paint", 0x053866);
    public static final PortDeferredItem<PaintItem> BLUE_PAINT = registerPaint("blue_paint", 0x203ebe);
    public static final PortDeferredItem<PaintItem> DEEP_BLUE_PAINT = registerPaint("deep_blue_paint", 0x0b084b);
    public static final PortDeferredItem<PaintItem> PURPLE_PAINT = registerPaint("purple_paint", 0x6214d6);
    public static final PortDeferredItem<PaintItem> DEEP_PURPLE_PAINT = registerPaint("deep_purple_paint", 0x26126b);
    public static final PortDeferredItem<PaintItem> VIOLET_PAINT = registerPaint("violet_paint", 0xba14d6);
    public static final PortDeferredItem<PaintItem> DEEP_VIOLET_PAINT = registerPaint("deep_violet_paint", 0x420757);
    public static final PortDeferredItem<PaintItem> PINK_PAINT = registerPaint("pink_paint", 0xed10cc);
    public static final PortDeferredItem<PaintItem> DEEP_PINK_PAINT = registerPaint("deep_pink_paint", 0x6a095e);
    public static final PortDeferredItem<PaintItem> BLACK_PAINT = registerPaint("black_paint", 0x1e1d20);
    public static final PortDeferredItem<PaintItem> GRAY_PAINT = registerPaint("gray_paint", 0x676764);
    public static final PortDeferredItem<PaintItem> WHITE_PAINT = registerPaint("white_paint", 0xfffef6);
    public static final PortDeferredItem<PaintItem> BROWN_PAINT = registerPaint("brown_paint", 0x8b653f);
    public static final PortDeferredItem<PaintItem> SHADOW_PAINT = registerPaint("shadow_paint", 0x000000);
    public static final PortDeferredItem<PaintItem> NEGATIVE_PAINT = registerPaint("negative_paint", BrushData.NEGATIVE_COLOR);
    public static final PortDeferredItem<PaintItem> ILLUMINANT_COATING = registerPaint("illuminant_coating", BrushData.ILLUMINANT_COLOR);
    public static final PortDeferredItem<PaintItem> ECHO_COATING = registerPaint("echo_coating", BrushData.ECHO_COLOR);

    private static PortDeferredItem<PaintItem> registerPaint(String name, int color) {
        return ITEMS.register(name, () -> {
            PaintItem paintItem = new PaintItem(color);
            PAINT_ITEMS.add(paintItem);
            return paintItem;
        });
    }

    private static <T extends Item> PortDeferredItem<T> registerTool(String suffix, TriFunction<Item.Properties, ModRarity, List<Component>, T> factory, boolean spectre) {
        return ITEMS.register(spectre ? "spectre_" + suffix : suffix, () -> {
            Item.Properties properties = new Item.Properties();
            if (spectre) properties.attributes(ItemAttributeModifiers.builder().add(
                    Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(ModItems.BASE_BLOCK_INTERACTION_RANGE_ID, 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            ).build());
            return factory.apply(properties, ModRarity.WHITE, TooltipItem.getTooltipsFromString(suffix, 2, ChatFormatting.GRAY));
        });
    }
}
