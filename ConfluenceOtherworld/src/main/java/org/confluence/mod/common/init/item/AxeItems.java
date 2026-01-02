package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseAxeItem;
import org.confluence.mod.common.item.common.StaffOfRegrowthItem;

import java.util.List;

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
    public static final DeferredItem<BaseAxeItem> WAR_AXE_OF_THE_NIGHT = ITEMS.register("war_axe_of_the_night", () -> new BaseAxeItem(ModTiers.DEMONITE, 10, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final DeferredItem<BaseAxeItem> BLOOD_LUST_CLUSTER = ITEMS.register("blood_lust_cluster", () -> new BaseAxeItem(ModTiers.CRIMTANE, 11, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());

    public static final DeferredItem<BaseAxeItem> COBALT_WARAXE = ITEMS.register("cobalt_waraxe", () -> new BaseAxeItem(ModTiers.COBALT, 15, 1.4F, unbreakable(), attributes(0, 0.55), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> PALLADIUM_WARAXE = ITEMS.register("palladium_waraxe", () -> new BaseAxeItem(ModTiers.PALLADIUM, 16, 1.4F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> MYTHRIL_WARAXE = ITEMS.register("mythril_waraxe", () -> new BaseAxeItem(ModTiers.MYTHRIL, 18, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> ORICHALCUM_WARAXE = ITEMS.register("orichalcum_waraxe", () -> new BaseAxeItem(ModTiers.ORICHALCUM, 19, 1.6F, unbreakable(), attributes(0, 0.65), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> ADAMANTITE_WARAXE = ITEMS.register("adamantite_waraxe", () -> new BaseAxeItem(ModTiers.ADAMANTITE, 21, 2.0F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> TITANIUM_WARAXE = ITEMS.register("titanium_waraxe", () -> new BaseAxeItem(ModTiers.TITANIUM, 23, 2.0F, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> CHLOROPHYTE_GREATAXE = ITEMS.register("chlorophyte_greataxe", () -> new BaseAxeItem(ModTiers.CHLOROPHYTE, 26, 2.5F, unbreakable(), attributes(1, 0.7), ModRarity.LIME));
    public static final DeferredItem<BaseAxeItem> LUCY_THE_AXE = ITEMS.register("lucy_the_axe", () -> new BaseAxeItem(Tiers.IRON, 11, 4, unbreakable(), attributes(0, 0.5), ModRarity.GREEN) {
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.lucy_the_axe.0").withStyle(ChatFormatting.GRAY));
        }
    });

    public static final DeferredItem<StaffOfRegrowthItem> STAFF_OF_REGROWTH = ITEMS.register("staff_of_regrowth", StaffOfRegrowthItem::new); // 再生法杖

    public static final DeferredItem<BaseAxeItem> AXE_OF_REGROWTH = ITEMS.register("axe_of_regrowth", () -> new BaseAxeItem(ModTiers.PLATINUM, 7, 1f, unbreakable(), ModRarity.LIGHT_RED)); // 再生之斧
}
