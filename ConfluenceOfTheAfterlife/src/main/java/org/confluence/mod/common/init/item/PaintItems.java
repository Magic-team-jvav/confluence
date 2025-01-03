package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.common.item.paint.PaintRollerItem;
import org.confluence.mod.common.item.paint.PaintScraperItem;
import org.confluence.mod.common.item.paint.PaintbrushItem;

import java.util.function.Supplier;

public class PaintItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<PaintbrushItem> PAINTBRUSH = ITEMS.register("paintbrush", PaintbrushItem::new);
    public static final Supplier<PaintRollerItem> PAINT_ROLLER = ITEMS.register("paint_roller", PaintRollerItem::new);
    public static final Supplier<PaintScraperItem> PAINT_SCRAPER = ITEMS.register("paint_scraper", PaintScraperItem::new);

    public static final Supplier<PaintItem> RED_PAINT = ITEMS.register("red_paint", () -> new PaintItem(0xFF0000));
    public static final Supplier<PaintItem> DEEP_RED_PAINT = ITEMS.register("deep_red_paint", () -> new PaintItem(0x55151f));
    public static final Supplier<PaintItem> ORANGE_PAINT = ITEMS.register("orange_paint", () -> new PaintItem(0xE96701));
    public static final Supplier<PaintItem> DEEP_ORANGE_PAINT = ITEMS.register("deep_orange_paint", () -> new PaintItem(0x702604));
    public static final Supplier<PaintItem> YELLOW_PAINT = ITEMS.register("yellow_paint", () -> new PaintItem(0xF4E700));
    public static final Supplier<PaintItem> DEEP_YELLOW_PAINT = ITEMS.register("deep_yellow_paint", () -> new PaintItem(0x554a18));
    public static final Supplier<PaintItem> LIME_PAINT = ITEMS.register("lime_paint", () -> new PaintItem(0xB5F400));
    public static final Supplier<PaintItem> DEEP_LIME_PAINT = ITEMS.register("deep_lime_paint", () -> new PaintItem(0x05C41C));
    public static final Supplier<PaintItem> GREEN_PAINT = ITEMS.register("green_paint", () -> new PaintItem(0x00F40A));
    public static final Supplier<PaintItem> DEEP_GREEN_PAINT = ITEMS.register("deep_green_paint", () -> new PaintItem(0x096635));
    public static final Supplier<PaintItem> TEAL_PAINT = ITEMS.register("teal_paint", () -> new PaintItem(0xB5F400));
    public static final Supplier<PaintItem> DEEP_TEAL_PAINT = ITEMS.register("deep_teal_paint", () -> new PaintItem(0x087466));
    public static final Supplier<PaintItem> CYAN_PAINT = ITEMS.register("cyan_paint", () -> new PaintItem(0x00E2F4));
    public static final Supplier<PaintItem> DEEP_CYAN_PAINT = ITEMS.register("deep_cyan_paint", () -> new PaintItem(0x095566 ));
    public static final Supplier<PaintItem> SKY_BLUE_PAINT = ITEMS.register("sky_blue_paint", () -> new PaintItem(0x00A2F4));
    public static final Supplier<PaintItem> DEEP_SKY_BLUE_PAINT = ITEMS.register("deep_sky_blue_paint", () -> new PaintItem(0x057EC4));
    public static final Supplier<PaintItem> BLUE_PAINT = ITEMS.register("blue_paint", () -> new PaintItem(0x2900F4));
    public static final Supplier<PaintItem> DEEP_BLUE_PAINT = ITEMS.register("deep_blue_paint", () -> new PaintItem(0x0e0966));
    public static final Supplier<PaintItem> PURPLE_PAINT = ITEMS.register("purple_paint", () -> new PaintItem(0x9600F4));
    public static final Supplier<PaintItem> DEEP_PURPLE_PAINT = ITEMS.register("deep_purple_paint", () -> new PaintItem(0x400873));
    public static final Supplier<PaintItem> VIOLET_PAINT = ITEMS.register("violet_paint", () -> new PaintItem(0xF400F3));
    public static final Supplier<PaintItem> DEEP_VIOLET_PAINT = ITEMS.register("deep_violet_paint", () -> new PaintItem(0x5b0966));
    public static final Supplier<PaintItem> PINK_PAINT = ITEMS.register("pink_paint", () -> new PaintItem(0xF9649E));
    public static final Supplier<PaintItem> DEEP_PINK_PAINT = ITEMS.register("deep_pink_paint", () -> new PaintItem(0x650944));
    public static final Supplier<PaintItem> BLACK_PAINT = ITEMS.register("black_paint", () -> new PaintItem(0x1f1e26));
    public static final Supplier<PaintItem> GRAY_PAINT = ITEMS.register("gray_paint", () -> new PaintItem(0x787782));
    public static final Supplier<PaintItem> WHITE_PAINT = ITEMS.register("white_paint", () -> new PaintItem(0xE8E5F3));
    public static final Supplier<PaintItem> BROWN_PAINT = ITEMS.register("brown_paint", () -> new PaintItem(0x96795E));
}
