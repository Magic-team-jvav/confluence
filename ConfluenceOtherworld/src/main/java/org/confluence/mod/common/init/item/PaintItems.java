package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.BrushData;
import org.confluence.mod.common.item.paint.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class PaintItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final List<PaintItem> PAINT_ITEMS = new ArrayList<>();

    public static final PortDeferredItem<PaintbrushItem> PAINTBRUSH = registerTool("paintbrush", "漆刷", PaintbrushItem::new, false);
    public static final PortDeferredItem<PaintRollerItem> PAINT_ROLLER = registerTool("paint_roller", "涂漆滚刷", PaintRollerItem::new, false);
    public static final PortDeferredItem<PaintScraperItem> PAINT_SCRAPER = registerTool("paint_scraper", "漆铲", PaintScraperItem::new, false);
    public static final PortDeferredItem<PaintbrushItem> SPECTRE_PAINTBRUSH = registerTool("paintbrush", "幽灵漆刷", PaintbrushItem::new, true);
    public static final PortDeferredItem<PaintRollerItem> SPECTRE_PAINT_ROLLER = registerTool("paint_roller", "幽灵涂漆滚刷", PaintRollerItem::new, true);
    public static final PortDeferredItem<PaintScraperItem> SPECTRE_PAINT_SCRAPER = registerTool("paint_scraper", "幽灵漆铲", PaintScraperItem::new, true);
    public static final PortDeferredItem<EyedropperItem> EYEDROPPER = registerItem("eyedropper", "滴管", EyedropperItem::new);
    public static final PortDeferredItem<PaintItem> PAINT = registerPaint("paint", "油漆", 0x39C5BB);

    public static final PortDeferredItem<PaintItem> RED_PAINT = registerPaint("red_paint", "红漆", 0xdb0909);
    public static final PortDeferredItem<PaintItem> DEEP_RED_PAINT = registerPaint("deep_red_paint", "深红漆", 0x55051d);
    public static final PortDeferredItem<PaintItem> ORANGE_PAINT = registerPaint("orange_paint", "橙漆", 0xec5a07);
    public static final PortDeferredItem<PaintItem> DEEP_ORANGE_PAINT = registerPaint("deep_orange_paint", "深橙漆", 0x6f1c03);
    public static final PortDeferredItem<PaintItem> YELLOW_PAINT = registerPaint("yellow_paint", "黄漆", 0xf0b007);
    public static final PortDeferredItem<PaintItem> DEEP_YELLOW_PAINT = registerPaint("deep_yellow_paint", "深黄漆", 0x764403);
    public static final PortDeferredItem<PaintItem> LIME_PAINT = registerPaint("lime_paint", "橙绿漆", 0xb7e013);
    public static final PortDeferredItem<PaintItem> DEEP_LIME_PAINT = registerPaint("deep_lime_paint", "深橙绿漆", 0x446e06);
    public static final PortDeferredItem<PaintItem> GREEN_PAINT = registerPaint("green_paint", "绿漆", 0x2cdf09);
    public static final PortDeferredItem<PaintItem> DEEP_GREEN_PAINT = registerPaint("deep_green_paint", "深绿漆", 0x04600a);
    public static final PortDeferredItem<PaintItem> TEAL_PAINT = registerPaint("teal_paint", "青绿漆", 0x03c75b);
    public static final PortDeferredItem<PaintItem> DEEP_TEAL_PAINT = registerPaint("deep_teal_paint", "深青绿漆", 0x015335);
    public static final PortDeferredItem<PaintItem> CYAN_PAINT = registerPaint("cyan_paint", "青漆", 0x03c7a2);
    public static final PortDeferredItem<PaintItem> DEEP_CYAN_PAINT = registerPaint("deep_cyan_paint", "深青漆", 0x045f5e);
    public static final PortDeferredItem<PaintItem> SKY_BLUE_PAINT = registerPaint("sky_blue_paint", "天蓝漆", 0x0699ce);
    public static final PortDeferredItem<PaintItem> DEEP_SKY_BLUE_PAINT = registerPaint("deep_sky_blue_paint", "深天蓝漆", 0x053866);
    public static final PortDeferredItem<PaintItem> BLUE_PAINT = registerPaint("blue_paint", "蓝漆", 0x203ebe);
    public static final PortDeferredItem<PaintItem> DEEP_BLUE_PAINT = registerPaint("deep_blue_paint", "深蓝漆", 0x0b084b);
    public static final PortDeferredItem<PaintItem> PURPLE_PAINT = registerPaint("purple_paint", "紫漆", 0x6214d6);
    public static final PortDeferredItem<PaintItem> DEEP_PURPLE_PAINT = registerPaint("deep_purple_paint", "深紫漆", 0x26126b);
    public static final PortDeferredItem<PaintItem> VIOLET_PAINT = registerPaint("violet_paint", "蓝紫漆", 0xba14d6);
    public static final PortDeferredItem<PaintItem> DEEP_VIOLET_PAINT = registerPaint("deep_violet_paint", "深蓝紫漆", 0x420757);
    public static final PortDeferredItem<PaintItem> PINK_PAINT = registerPaint("pink_paint", "粉漆", 0xed10cc);
    public static final PortDeferredItem<PaintItem> DEEP_PINK_PAINT = registerPaint("deep_pink_paint", "深粉漆", 0x6a095e);
    public static final PortDeferredItem<PaintItem> BLACK_PAINT = registerPaint("black_paint", "黑漆", 0x1e1d20);
    public static final PortDeferredItem<PaintItem> GRAY_PAINT = registerPaint("gray_paint", "灰漆", 0x676764);
    public static final PortDeferredItem<PaintItem> WHITE_PAINT = registerPaint("white_paint", "白漆", 0xfffef6);
    public static final PortDeferredItem<PaintItem> BROWN_PAINT = registerPaint("brown_paint", "棕漆", 0x8b653f);
    public static final PortDeferredItem<PaintItem> SHADOW_PAINT = registerPaint("shadow_paint", "暗影漆", 0x000000);
    public static final PortDeferredItem<PaintItem> NEGATIVE_PAINT = registerPaint("negative_paint", "反色漆", BrushData.NEGATIVE_COLOR);
    public static final PortDeferredItem<PaintItem> ILLUMINANT_COATING = registerPaint("illuminant_coating", "夜明涂料", BrushData.ILLUMINANT_COLOR);
    public static final PortDeferredItem<PaintItem> ECHO_COATING = registerPaint("echo_coating", "回声涂料", BrushData.ECHO_COLOR);

    private static PortDeferredItem<PaintItem> registerPaint(String registryId, String chineseName, int color) {
        PortDeferredItem<PaintItem> entry = ITEMS.register(registryId, () -> {
            PaintItem paintItem = new PaintItem(color);
            PAINT_ITEMS.add(paintItem);
            return paintItem;
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerTool(String registryId, String chineseName, BiFunction<Item.Properties, ModRarity, T> factory, boolean spectre) {
        PortDeferredItem<T> entry = ITEMS.register(spectre ? "spectre_" + registryId : registryId, () -> {
            Item.Properties properties = new Item.Properties();
            if (spectre) properties.attributes(PortItemAttributeModifiers.builder().add(
                    Attributes.BLOCK_INTERACTION_RANGE, ModItems.BASE_BLOCK_INTERACTION_RANGE_ID, 3, PortAttributeModifier.Operation.ADD_VALUE, PortEquipmentSlotGroup.MAINHAND
            ).build());
            return factory.apply(properties, ModRarity.WHITE);
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
