package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.drill.DrillItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class DrillItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<DrillItem> COBALT_DRILL = ITEMS.registerItem("cobalt_drill", properties -> new DrillItem(ModTiers.COBALT, 10, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<DrillItem> PALLADIUM_DRILL = ITEMS.registerItem("palladium_drill", properties -> new DrillItem(ModTiers.PALLADIUM, 12, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<DrillItem> MYTHRIL_DRILL = ITEMS.registerItem("mythril_drill", properties -> new DrillItem(ModTiers.MYTHRIL, 15, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<DrillItem> ORICHALCUM_DRILL = ITEMS.registerItem("orichalcum_drill", properties -> new DrillItem(ModTiers.ORICHALCUM, 17, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<DrillItem> ADAMANTITE_DRILL = ITEMS.registerItem("adamantite_drill", properties -> new DrillItem(ModTiers.ADAMANTITE, 20, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<DrillItem> TITANIUM_DRILL = ITEMS.registerItem("titanium_drill", properties -> new DrillItem(ModTiers.TITANIUM, 27, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<DrillItem> CHLOROPHYTE_DRILL = ITEMS.registerItem("chlorophyte_drill", properties -> new DrillItem(ModTiers.CHLOROPHYTE, 35, 2.7F, unbreakable(), attributes(0, 0.1), ModRarity.LIME));
    public static final DeferredItem<DrillItem> DRAX = ITEMS.registerItem("drax", properties -> new DrillItem(ModTiers.HALLOWED, 35, 2.5F, unbreakable(), attributes(-1, 0.475), ModRarity.LIGHT_RED));
    public static final DeferredItem<DrillItem> SOLAR_FLARE_DRILL = ITEMS.registerItem("solar_flare_drill", properties -> new DrillItem(ModTiers.LUMINITE, 50, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<DrillItem> VORTEX_DRILL = ITEMS.registerItem("vortex_drill", properties -> new DrillItem(ModTiers.LUMINITE, 50, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<DrillItem> NEBULA_DRILL = ITEMS.registerItem("nebula_drill", properties -> new DrillItem(ModTiers.LUMINITE, 50, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<DrillItem> STARDUST_DRILL = ITEMS.registerItem("stardust_drill", properties -> new DrillItem(ModTiers.LUMINITE, 50, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    // todo 激光钻头

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
