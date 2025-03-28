package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.axe.BaseAxeItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

public class AxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseAxeItem> COPPER_AXE = ITEMS.register("copper_axe", () -> new BaseAxeItem(ModTiers.COPPER, 7, 0.8f));
    public static final DeferredItem<BaseAxeItem> TIN_AXE = ITEMS.register("tin_axe", () -> new BaseAxeItem(ModTiers.TIN, 7, 0.8f));
    public static final DeferredItem<BaseAxeItem> LEAD_AXE = ITEMS.register("lead_axe", () -> new BaseAxeItem(ModTiers.LEAD, 9, 0.9f));
    public static final DeferredItem<BaseAxeItem> SILVER_AXE = ITEMS.register("silver_axe", () -> new BaseAxeItem(ModTiers.SILVER, 9, 0.9f));
    public static final DeferredItem<BaseAxeItem> TUNGSTEN_AXE = ITEMS.register("tungsten_axe", () -> new BaseAxeItem(ModTiers.TUNGSTEN, 9, 0.9f));
    public static final DeferredItem<BaseAxeItem> GOLDEN_AXE = ITEMS.register("golden_axe", () -> new BaseAxeItem(ModTiers.GOLD, 9, 1));
    public static final DeferredItem<BaseAxeItem> PLATINUM_AXE = ITEMS.register("platinum_axe", () -> new BaseAxeItem(ModTiers.PLATINUM, 9, 1));
    public static final DeferredItem<BaseAxeItem> WAR_AXE_OF_THE_NIGHT = ITEMS.register("war_axe_of_the_night", () -> new BaseAxeItem(ModTiers.DEMONITE, 10, 1, new Item.Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE), ModRarity.BLUE));
    public static final DeferredItem<BaseAxeItem> BLOOD_LUST_CLUSTER = ITEMS.register("blood_lust_cluster", () -> new BaseAxeItem(ModTiers.TR_CRIMSON, 11, 1, new Item.Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE), ModRarity.BLUE));

    public static final DeferredItem<Item> STAFF_OF_REGROWTH = ITEMS.register("staff_of_regrowth", () -> new Item(new Item.Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.GREEN).component(DataComponents.ATTRIBUTE_MODIFIERS, DiggerItem.createAttributes(ModTiers.PLATINUM, (3 - ModTiers.PLATINUM.getAttackDamageBonus() - 1), 1 - 4)))); // 再生法杖
    public static final DeferredItem<Item> DRILL_OF_REGROWTH = ITEMS.register("drill_of_regrowth", () -> new BaseAxeItem(ModTiers.PLATINUM, 7, 1f)); // 再生之斧

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(axe -> tag.add(axe.get()));
    }
}
