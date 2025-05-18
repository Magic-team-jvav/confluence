package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.hoe_shovel.HoeShovelItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HoeShovelItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredItem<HoeShovelItem> METEOR_HOE_SHOVEL = ITEMS.register("meteor_hoe_shovel", () -> new HoeShovelItem(ModTiers.METEOR, 12, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.BLUE));
    public static final DeferredItem<HoeShovelItem> MOLTEN_HOE_SHOVEL = ITEMS.register("molten_hoe_shovel", () -> new HoeShovelItem(ModTiers.HELLSTONE, 12, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.ORANGE));
    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
