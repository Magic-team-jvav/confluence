package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.item.paint.*;

import java.util.function.Supplier;

public class PaintItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<PaintbrushItem> PAINTBRUSH = ITEMS.register("paintbrush", PaintbrushItem::new);
    public static final Supplier<PaintRollerItem> PAINT_ROLLER = ITEMS.register("paint_roller", PaintRollerItem::new);
    public static final Supplier<PaintScraperItem> PAINT_SCRAPER = ITEMS.register("paint_scraper", PaintScraperItem::new);
    public static final Supplier<EyedropperItem> EYEDROPPER = ITEMS.register("eyedropper", EyedropperItem::new);
    public static final Supplier<PaintItem> PAINT = ITEMS.register("paint", () -> new PaintItem(0x39C5BB));

    public static final Supplier<PaintItem> RED_PAINT = ITEMS.register("red_paint", () -> new PaintItem(0xdb0909));
    public static final Supplier<PaintItem> DEEP_RED_PAINT = ITEMS.register("deep_red_paint", () -> new PaintItem(0x55051d));
    public static final Supplier<PaintItem> ORANGE_PAINT = ITEMS.register("orange_paint", () -> new PaintItem(0xec5a07));
    public static final Supplier<PaintItem> DEEP_ORANGE_PAINT = ITEMS.register("deep_orange_paint", () -> new PaintItem(0x6f1c03));
    public static final Supplier<PaintItem> YELLOW_PAINT = ITEMS.register("yellow_paint", () -> new PaintItem(0xf0b007));
    public static final Supplier<PaintItem> DEEP_YELLOW_PAINT = ITEMS.register("deep_yellow_paint", () -> new PaintItem(0x764403));
    public static final Supplier<PaintItem> LIME_PAINT = ITEMS.register("lime_paint", () -> new PaintItem(0xb7e013));
    public static final Supplier<PaintItem> DEEP_LIME_PAINT = ITEMS.register("deep_lime_paint", () -> new PaintItem(0x446e06));
    public static final Supplier<PaintItem> GREEN_PAINT = ITEMS.register("green_paint", () -> new PaintItem(0x2cdf09));
    public static final Supplier<PaintItem> DEEP_GREEN_PAINT = ITEMS.register("deep_green_paint", () -> new PaintItem(0x04600a));
    public static final Supplier<PaintItem> TEAL_PAINT = ITEMS.register("teal_paint", () -> new PaintItem(0x03c75b));
    public static final Supplier<PaintItem> DEEP_TEAL_PAINT = ITEMS.register("deep_teal_paint", () -> new PaintItem(0x015335));
    public static final Supplier<PaintItem> CYAN_PAINT = ITEMS.register("cyan_paint", () -> new PaintItem(0x03c7a2));
    public static final Supplier<PaintItem> DEEP_CYAN_PAINT = ITEMS.register("deep_cyan_paint", () -> new PaintItem(0x045f5e));
    public static final Supplier<PaintItem> SKY_BLUE_PAINT = ITEMS.register("sky_blue_paint", () -> new PaintItem(0x0699ce));
    public static final Supplier<PaintItem> DEEP_SKY_BLUE_PAINT = ITEMS.register("deep_sky_blue_paint", () -> new PaintItem(0x053866));
    public static final Supplier<PaintItem> BLUE_PAINT = ITEMS.register("blue_paint", () -> new PaintItem(0x203ebe));
    public static final Supplier<PaintItem> DEEP_BLUE_PAINT = ITEMS.register("deep_blue_paint", () -> new PaintItem(0x0b084b));
    public static final Supplier<PaintItem> PURPLE_PAINT = ITEMS.register("purple_paint", () -> new PaintItem(0x6214d6));
    public static final Supplier<PaintItem> DEEP_PURPLE_PAINT = ITEMS.register("deep_purple_paint", () -> new PaintItem(0x26126b));
    public static final Supplier<PaintItem> VIOLET_PAINT = ITEMS.register("violet_paint", () -> new PaintItem(0xba14d6));
    public static final Supplier<PaintItem> DEEP_VIOLET_PAINT = ITEMS.register("deep_violet_paint", () -> new PaintItem(0x420757));
    public static final Supplier<PaintItem> PINK_PAINT = ITEMS.register("pink_paint", () -> new PaintItem(0xed10cc));
    public static final Supplier<PaintItem> DEEP_PINK_PAINT = ITEMS.register("deep_pink_paint", () -> new PaintItem(0x6a095e));
    public static final Supplier<PaintItem> BLACK_PAINT = ITEMS.register("black_paint", () -> new PaintItem(0x1e1d20));
    public static final Supplier<PaintItem> GRAY_PAINT = ITEMS.register("gray_paint", () -> new PaintItem(0x676764));
    public static final Supplier<PaintItem> WHITE_PAINT = ITEMS.register("white_paint", () -> new PaintItem(0xfffef6));
    public static final Supplier<PaintItem> BROWN_PAINT = ITEMS.register("brown_paint", () -> new PaintItem(0x8b653f));
    public static final Supplier<PaintItem> SHADOW_PAINT = ITEMS.register("shadow_paint", () -> new PaintItem(0x000000));
    public static final Supplier<PaintItem> NEGATIVE_PAINT = ITEMS.register("negative_paint", () -> new PaintItem(BrushData.NEGATIVE_COLOR));
    public static final Supplier<PaintItem> ILLUMINANT_COATING = ITEMS.register("illuminant_coating", () -> new PaintItem(BrushData.ILLUMINANT_COLOR));
    public static final Supplier<PaintItem> ECHO_COATING = ITEMS.register("echo_coating", () -> new PaintItem(BrushData.ECHO_COLOR)); // todo

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(entry -> tag.add(entry.get()));
    }
}

