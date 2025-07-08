package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.pickaxe.BasePickaxeItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class PickaxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<PickaxeItem> CACTUS_PICKAXE = ITEMS.register("cactus_pickaxe", () -> new BasePickaxeItem(ModTiers.CACTUS, 4, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> COPPER_PICKAXE = ITEMS.register("copper_pickaxe", () -> new BasePickaxeItem(ModTiers.COPPER, 4, 1.2F, new Item.Properties(), attributes(-1, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> TIN_PICKAXE = ITEMS.register("tin_pickaxe", () -> new BasePickaxeItem(ModTiers.TIN, 5, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> LEAD_PICKAXE = ITEMS.register("lead_pickaxe", () -> new BasePickaxeItem(ModTiers.LEAD, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> SILVER_PICKAXE = ITEMS.register("silver_pickaxe", () -> new BasePickaxeItem(ModTiers.SILVER, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> TUNGSTEN_PICKAXE = ITEMS.register("tungsten_pickaxe", () -> new BasePickaxeItem(ModTiers.TUNGSTEN, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> GOLDEN_PICKAXE = ITEMS.register("golden_pickaxe", () -> new BasePickaxeItem(ModTiers.GOLD, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> CANDY_CANE_PICKAXE = ITEMS.register("candy_cane_pickaxe", () -> new BasePickaxeItem(ModTiers.CANDY_CANE, 7, 1.2F, new Item.Properties(), attributes(0, 0.25), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> FOSSIL_PICKAXE = ITEMS.register("fossil_pickaxe", () -> new BasePickaxeItem(ModTiers.FOSSIL, 8, 1.2F, new Item.Properties(), attributes(0, 0.4), ModRarity.BLUE));
    public static final DeferredItem<PickaxeItem> BONE_PICKAXE = ITEMS.register("bone_pickaxe", () -> new BasePickaxeItem(ModTiers.BONE, 8, 1.2F, unbreakable(), attributes(0, 0.3), ModRarity.BLUE));
    public static final DeferredItem<PickaxeItem> PLATINUM_PICKAXE = ITEMS.register("platinum_pickaxe", () -> new BasePickaxeItem(ModTiers.PLATINUM, 7, 1.2F, new Item.Properties(),  attributes(0, 0.2), ModRarity.WHITE));
    public static final DeferredItem<PickaxeItem> REAVER_SHARK_PICKAXE = ITEMS.register("reaver_shark_pickaxe", () -> new BasePickaxeItem(ModTiers.REAVER_SHARK, 10, 1.2F, unbreakable(), attributes(0, 0.3), ModRarity.ORANGE));
    public static final DeferredItem<PickaxeItem> NIGHTMARE_PICKAXE = ITEMS.register("nightmare_pickaxe", () -> new BasePickaxeItem(ModTiers.DEMONITE, 9, 1.2F, unbreakable(), attributes(0, 0.3), ModRarity.BLUE));
    public static final DeferredItem<PickaxeItem> DEATHBRINGER_PICKAXE = ITEMS.register("deathbringer_pickaxe", () -> new BasePickaxeItem(ModTiers.CRIMTANE, 12, 1.2F, unbreakable(), attributes(0, 0.35), ModRarity.BLUE));
    public static final DeferredItem<PickaxeItem> MOLTEN_PICKAXE = ITEMS.register("molten_pickaxe", () -> new BasePickaxeItem(ModTiers.HELLSTONE, 12, 1.3F, unbreakable(), attributes(0, 0.2), ModRarity.ORANGE));

    public static final DeferredItem<PickaxeItem> COBALT_PICKAXE = ITEMS.register("cobalt_pickaxe", () -> new BasePickaxeItem(ModTiers.COBALT, 10, 1.4F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<PickaxeItem> PALLADIUM_PICKAXE = ITEMS.register("palladium_pickaxe", () -> new BasePickaxeItem(ModTiers.PALLADIUM, 12, 1.4F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<PickaxeItem> MYTHRIL_PICKAXE = ITEMS.register("mythril_pickaxe", () -> new BasePickaxeItem(ModTiers.MYTHRIL, 15, 1.6F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<PickaxeItem> ORICHALCUM_PICKAXE = ITEMS.register("orichalcum_pickaxe", () -> new BasePickaxeItem(ModTiers.ORICHALCUM, 17, 1.6F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<PickaxeItem> ADAMANTITE_PICKAXE = ITEMS.register("adamantite_pickaxe", () -> new BasePickaxeItem(ModTiers.ADAMANTITE, 20, 1.8F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<PickaxeItem> TITANIUM_PICKAXE = ITEMS.register("titanium_pickaxe", () -> new BasePickaxeItem(ModTiers.TITANIUM, 21, 1.8F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final DeferredItem<PickaxeItem> SPECTRE_PICKAXE = ITEMS.register("spectre_pickaxe", () -> new BasePickaxeItem(ModTiers.SPECTRE, 24, 2.2F, unbreakable(), attributes(3, 0.525), ModRarity.YELLOW));
    public static final DeferredItem<PickaxeItem> CHLOROPHYTE_PICKAXE = ITEMS.register("chlorophyte_pickaxe", () -> new BasePickaxeItem(ModTiers.CHLOROPHYTE, 26, 2.2F, unbreakable(), attributes(3, 0.5), ModRarity.LIME));
    public static final DeferredItem<PickaxeItem> SOLAR_FLARE_PICKAXE = ITEMS.register("solar_flare_pickaxe", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));
    public static final DeferredItem<PickaxeItem> VORTEX_PICKAXE = ITEMS.register("vortex_pickaxe", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));
    public static final DeferredItem<PickaxeItem> NEBULA_PICKAXE = ITEMS.register("nebula_pickaxe", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));
    public static final DeferredItem<PickaxeItem> STARDUST_PICKAXE = ITEMS.register("stardust_pickaxe", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
