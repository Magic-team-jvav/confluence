package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.hoe.BaseHoeItem;
import org.confluence.mod.common.item.shovel.BaseShovelItem;
import org.confluence.terra_curio.common.component.ModRarity;

public class ShovelItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseShovelItem> COPPER_SHOVEL = ITEMS.register("copper_shovel", () -> new BaseShovelItem(ModTiers.COPPER, 1, 3.5f,ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> TIN_SHOVEL = ITEMS.register("tin_shovel", () -> new BaseShovelItem(ModTiers.TIN, 1, 3.7f,ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel", () -> new BaseShovelItem(ModTiers.LEAD, 1, 4.0f,ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> SILVER_SHOVEL = ITEMS.register("silver_shovel", () -> new BaseShovelItem(ModTiers.SILVER, 1, 4.3f,ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> TUNGSTEN_SHOVEL = ITEMS.register("tungsten_shovel", () -> new BaseShovelItem(ModTiers.TUNGSTEN, 1, 4.7f,ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> GOLDEN_SHOVEL = ITEMS.register("golden_shovel", () -> new BaseShovelItem(ModTiers.GOLD, 1, 5.0f,ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> PLATINUM_SHOVEL = ITEMS.register("platinum_shovel", () -> new BaseShovelItem(ModTiers.PLATINUM, 1, 5.5f,ModRarity.COMMON));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(shovel -> tag.add(shovel.get()));
    }
}
