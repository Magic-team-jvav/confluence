package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseDrillItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class DrillItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseDrillItem> COBALT_DRILL = ITEMS.registerItem("cobalt_drill", properties -> new BaseDrillItem(ModTiers.COBALT, 15, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> PALLADIUM_DRILL = ITEMS.registerItem("palladium_drill", properties -> new BaseDrillItem(ModTiers.PALLADIUM, 16, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> MYTHRIL_DRILL = ITEMS.registerItem("mythril_drill", properties -> new BaseDrillItem(ModTiers.MYTHRIL, 17, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> ORICHALCUM_DRILL = ITEMS.registerItem("orichalcum_drill", properties -> new BaseDrillItem(ModTiers.ORICHALCUM, 18, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> ADAMANTITE_DRILL = ITEMS.registerItem("adamantite_drill", properties -> new BaseDrillItem(ModTiers.ADAMANTITE, 19, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> TITANIUM_DRILL = ITEMS.registerItem("titanium_drill", properties -> new BaseDrillItem(ModTiers.TITANIUM, 20, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> CHLOROPHYTE_DRILL = ITEMS.registerItem("chlorophyte_drill", properties -> new BaseDrillItem(ModTiers.CHLOROPHYTE, 24, 2.7F, unbreakable(), attributes(0, 0.1), ModRarity.LIME));
    public static final DeferredItem<BaseDrillItem> DRAX = ITEMS.registerItem("drax", properties -> new BaseDrillItem(ModTiers.HALLOWED, 28, 2.5F, unbreakable(), attributes(-1, 0.475), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> SOLAR_FLARE_DRILL = ITEMS.registerItem("solar_flare_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<BaseDrillItem> VORTEX_DRILL = ITEMS.registerItem("vortex_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<BaseDrillItem> NEBULA_DRILL = ITEMS.registerItem("nebula_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<BaseDrillItem> STARDUST_DRILL = ITEMS.registerItem("stardust_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    // todo 激光钻头

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
