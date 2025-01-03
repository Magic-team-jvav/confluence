package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.common.item.paint.PaintScraperItem;
import org.confluence.mod.common.item.paint.PaintbrushItem;

import java.util.function.Supplier;

public class PaintItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<PaintbrushItem> PAINTBRUSH = ITEMS.register("paintbrush", PaintbrushItem::new);
    public static final Supplier<PaintScraperItem> PAINT_SCRAPER = ITEMS.register("paint_scraper", PaintScraperItem::new);

    public static final Supplier<PaintItem> RED_PAINT = ITEMS.register("red_paint", () -> new PaintItem(0xFF0000));
}
