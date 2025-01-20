package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.bow.BaseArrowItem;
import org.confluence.terra_curio.common.component.ModRarity;

public class ArrowItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseArrowItem> FLAMING_ARROW = ITEMS.register("flaming_arrow", () -> new BaseArrowItem(ModRarity.WHITE));
    public static final DeferredItem<BaseArrowItem> UNHOLY_ARROW = ITEMS.register("unholy_arrow", () -> new  BaseArrowItem(ModRarity.BLUE));
    public static final DeferredItem<BaseArrowItem> STAR_ARROW = ITEMS.register("star_arrow", () -> new  BaseArrowItem(ModRarity.BLUE));
    public static final DeferredItem<BaseArrowItem> HELLFIRE_ARROW = ITEMS.register("hellfire_arrow", () -> new  BaseArrowItem(ModRarity.GREEN));
    public static final DeferredItem<BaseArrowItem> FROSTBURN_ARROW = ITEMS.register("frostburn_arrow", () -> new  BaseArrowItem(ModRarity.WHITE));
    public static final DeferredItem<BaseArrowItem> BONE_ARROW = ITEMS.register("bone_arrow", () -> new  BaseArrowItem(ModRarity.WHITE));
    public static final DeferredItem<BaseArrowItem> SHIMMER_ARROW = ITEMS.register("shimmer_arrow", () -> new BaseArrowItem(ModRarity.WHITE));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
