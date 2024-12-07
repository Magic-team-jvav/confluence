package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.hammer.HammerItem;

public class HammerItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HammerItem> WOODEN_HAMMER = ITEMS.register("wooden_hammer", () -> new HammerItem(Tiers.WOOD, 1, 1));
    public static final DeferredItem<HammerItem> COPPER_HAMMER = ITEMS.register("copper_hammer", () -> new HammerItem(ModTiers.COPPER, 2, 1));
    public static final DeferredItem<HammerItem> TIN_HAMMER = ITEMS.register("tin_hammer", () -> new HammerItem(ModTiers.TIN, 2, 1));
    public static final DeferredItem<HammerItem> LEAD_HAMMER = ITEMS.register("lead_hammer", () -> new HammerItem(ModTiers.LEAD, 3, 1));
    public static final DeferredItem<HammerItem> SILVER_HAMMER = ITEMS.register("silver_hammer", () -> new HammerItem(ModTiers.SILVER, 3, 1));
    public static final DeferredItem<HammerItem> TUNGSTEN_HAMMER = ITEMS.register("tungsten_hammer", () -> new HammerItem(ModTiers.TUNGSTEN, 4, 1));
    public static final DeferredItem<HammerItem> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new HammerItem(ModTiers.GOLD, 5, 1));
    public static final DeferredItem<HammerItem> PLATINUM_HAMMER = ITEMS.register("platinum_hammer", () -> new HammerItem(ModTiers.PLATINUM, 5, 1));
    public static final DeferredItem<HammerItem> EBONY_HAMMER = ITEMS.register("ebony_hammer", () -> new HammerItem(ModTiers.EBONY, 6, 1));
    public static final DeferredItem<HammerItem> TR_CRIMSON_HAMMER = ITEMS.register("tr_crimson_hammer", () -> new HammerItem(ModTiers.TR_CRIMSON, 6, 1));
    public static final DeferredItem<HammerItem> PWNHAMMER = ITEMS.register("pwnhammer", () -> new HammerItem(ModTiers.HALLOWED, 7, 1, new Item.Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
