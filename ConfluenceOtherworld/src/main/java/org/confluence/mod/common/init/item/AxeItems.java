package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.axe.BaseAxeItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class AxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseAxeItem> COPPER_AXE = ITEMS.register("copper_axe", () -> new BaseAxeItem(ModTiers.COPPER, 3, 0.85F, new Item.Properties(), attributes(-1, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> TIN_AXE = ITEMS.register("tin_axe", () -> new BaseAxeItem(ModTiers.TIN, 4, 0.85F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> LEAD_AXE = ITEMS.register("lead_axe", () -> new BaseAxeItem(ModTiers.LEAD, 6, 0.95F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> SILVER_AXE = ITEMS.register("silver_axe", () -> new BaseAxeItem(ModTiers.SILVER, 6, 0.95F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> TUNGSTEN_AXE = ITEMS.register("tungsten_axe", () -> new BaseAxeItem(ModTiers.TUNGSTEN, 7, 0.97F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> GOLDEN_AXE = ITEMS.register("golden_axe", () -> new BaseAxeItem(ModTiers.GOLD, 7, 1, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> PLATINUM_AXE = ITEMS.register("platinum_axe", () -> new BaseAxeItem(ModTiers.PLATINUM, 8, 1.1F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> WAR_AXE_OF_THE_NIGHT = ITEMS.register("war_axe_of_the_night", () -> new BaseAxeItem(ModTiers.DEMONITE, 10, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));
    public static final DeferredItem<BaseAxeItem> BLOOD_LUST_CLUSTER = ITEMS.register("blood_lust_cluster", () -> new BaseAxeItem(ModTiers.CRIMTANE, 11, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));

    public static final DeferredItem<BaseAxeItem> COBALT_WARAXE = ITEMS.register("cobalt_waraxe", () -> new BaseAxeItem(ModTiers.COBALT, 15, 1.4F, unbreakable(), attributes(0, 0.55), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> PALLADIUM_WARAXE = ITEMS.register("palladium_waraxe", () -> new BaseAxeItem(ModTiers.PALLADIUM, 16, 1.4F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> MYTHRIL_WARAXE = ITEMS.register("mythril_waraxe", () -> new BaseAxeItem(ModTiers.MYTHRIL, 18, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> ORICHALCUM_WARAXE = ITEMS.register("orichalcum_waraxe", () -> new BaseAxeItem(ModTiers.ORICHALCUM, 19, 1.6F, unbreakable(), attributes(0, 0.65), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> ADAMANTITE_WARAXE = ITEMS.register("adamantite_waraxe", () -> new BaseAxeItem(ModTiers.ADAMANTITE, 21, 2.0F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> TITANIUM_WARAXE = ITEMS.register("titanium_waraxe", () -> new BaseAxeItem(ModTiers.TITANIUM, 23, 2.0F, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> CHLOROPHYTE_GREATAXE = ITEMS.register("chlorophyte_greataxe", () -> new BaseAxeItem(ModTiers.CHLOROPHYTE, 26, 2.5F, unbreakable(), attributes(1, 0.7), ModRarity.LIME));
    public static final DeferredItem<BaseAxeItem> LUCY_THE_AXE = ITEMS.register("lucy_the_axe", () -> new BaseAxeItem(Tiers.IRON, 11, 4, unbreakable(), attributes(0, 0.5), ModRarity.GREEN));

    public static final DeferredItem<Item> STAFF_OF_REGROWTH = ITEMS.register("staff_of_regrowth", () -> new Item(new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN).component(DataComponents.ATTRIBUTE_MODIFIERS, DiggerItem.createAttributes(ModTiers.PLATINUM, (3 - ModTiers.PLATINUM.getAttackDamageBonus() - 1), 1 - 4)))); // 再生法杖
    public static final DeferredItem<BaseAxeItem> AXE_OF_REGROWTH = ITEMS.register("axe_of_regrowth", () -> new BaseAxeItem(ModTiers.PLATINUM, 7, 1f, unbreakable(),ModRarity.LIGHT_RED)); // 再生之斧

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(axe -> tag.add(axe.get()));
    }
}
