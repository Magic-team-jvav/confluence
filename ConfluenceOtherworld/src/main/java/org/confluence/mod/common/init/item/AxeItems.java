package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
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

    public static final DeferredItem<BaseAxeItem> COPPER_AXE = ITEMS.register("copper_axe", () -> new BaseAxeItem(ModTiers.COPPER, 3, 60.0F / 21, new Item.Properties(), attributes(-1, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> TIN_AXE = ITEMS.register("tin_axe", () -> new BaseAxeItem(ModTiers.TIN, 4, 60.0F / 20, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> LEAD_AXE = ITEMS.register("lead_axe", () -> new BaseAxeItem(ModTiers.LEAD, 6, 60.0F / 19, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> SILVER_AXE = ITEMS.register("silver_axe", () -> new BaseAxeItem(ModTiers.SILVER, 6, 60.0F / 18, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> TUNGSTEN_AXE = ITEMS.register("tungsten_axe", () -> new BaseAxeItem(ModTiers.TUNGSTEN, 7, 60.0F / 18, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> GOLDEN_AXE = ITEMS.register("golden_axe", () -> new BaseAxeItem(ModTiers.GOLD, 7, 60.0F / 18, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> PLATINUM_AXE = ITEMS.register("platinum_axe", () -> new BaseAxeItem(ModTiers.PLATINUM, 8, 60.0F / 17, unbreakable(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> WAR_AXE_OF_THE_NIGHT = ITEMS.register("war_axe_of_the_night", () -> new BaseAxeItem(ModTiers.DEMONITE, 10, 1, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));
    public static final DeferredItem<BaseAxeItem> BLOOD_LUST_CLUSTER = ITEMS.register("blood_lust_cluster", () -> new BaseAxeItem(ModTiers.TR_CRIMSON, 11, 1, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));

    public static final DeferredItem<BaseAxeItem> COBALT_WARAXE = ITEMS.register("cobalt_waraxe", () -> new BaseAxeItem(ModTiers.COBALT, 33, 60.0F / 12, unbreakable(), attributes(0, 0.55), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> PALLADIUM_WARAXE = ITEMS.register("palladium_waraxe", () -> new BaseAxeItem(ModTiers.PALLADIUM, 36, 60.0F / 13, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> MYTHRIL_WARAXE = ITEMS.register("mythril_waraxe", () -> new BaseAxeItem(ModTiers.MYTHRIL, 39, 60.0F / 10, unbreakable(), attributes(0, 0.6), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> ORICHALCUM_WARAXE = ITEMS.register("orichalcum_waraxe", () -> new BaseAxeItem(ModTiers.ORICHALCUM, 41, 60.0F / 9, unbreakable(), attributes(0, 0.65), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> ADAMANTITE_WARAXE = ITEMS.register("adamantite_waraxe", () -> new BaseAxeItem(ModTiers.ADAMANTITE, 43, 60.0F / 8, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> TITANIUM_WARAXE = ITEMS.register("titanium_waraxe", () -> new BaseAxeItem(ModTiers.TITANIUM, 35, 60.0F / 7, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseAxeItem> CHLOROPHYTE_GREATAXE = ITEMS.register("chlorophyte_greataxe", () -> new BaseAxeItem(ModTiers.CHLOROPHYTE, 70, 60.0F / 7, unbreakable(), attributes(1, 0.7), ModRarity.LIME));
    // todo 路西斧

    public static final DeferredItem<Item> STAFF_OF_REGROWTH = ITEMS.register("staff_of_regrowth", () -> new Item(new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN).component(DataComponents.ATTRIBUTE_MODIFIERS, DiggerItem.createAttributes(ModTiers.PLATINUM, (3 - ModTiers.PLATINUM.getAttackDamageBonus() - 1), 1 - 4)))); // 再生法杖
    public static final DeferredItem<BaseAxeItem> AXE_OF_REGROWTH = ITEMS.register("axe_of_regrowth", () -> new BaseAxeItem(ModTiers.PLATINUM, 7, 1f, ModRarity.LIGHT_RED)); // 再生之斧

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(axe -> tag.add(axe.get()));
    }
}
