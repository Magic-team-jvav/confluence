package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.axe.AxeOfRegrowth;
import org.confluence.mod.common.item.axe.BaseAxeItem;
import org.confluence.mod.common.item.axe.LucyTheAxe;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class AxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseAxeItem> COPPER_AXE = registerItem("copper_axe", "铜斧", () -> new BaseAxeItem(ModTiers.COPPER, 3, 0.85F, new Item.Properties(), attributes(-1, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> TIN_AXE = registerItem("tin_axe", "锡斧", () -> new BaseAxeItem(ModTiers.TIN, 4, 0.85F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> LEAD_AXE = registerItem("lead_axe", "铅斧", () -> new BaseAxeItem(ModTiers.LEAD, 6, 0.95F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> SILVER_AXE = registerItem("silver_axe", "银斧", () -> new BaseAxeItem(ModTiers.SILVER, 6, 0.95F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> TUNGSTEN_AXE = registerItem("tungsten_axe", "钨斧", () -> new BaseAxeItem(ModTiers.TUNGSTEN, 7, 0.97F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> GOLDEN_AXE = registerItem("golden_axe", "金斧", () -> new BaseAxeItem(ModTiers.GOLD, 7, 1, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> PLATINUM_AXE = registerItem("platinum_axe", "铂金斧", () -> new BaseAxeItem(ModTiers.PLATINUM, 8, 1.1F, new Item.Properties(), attributes(0, 0.45), ModRarity.WHITE));
    public static final DeferredItem<BaseAxeItem> WAR_AXE_OF_THE_NIGHT = registerItemWithEnglishName("war_axe_of_the_night", "暗夜战斧", "War Axe of the Night", () -> new BaseAxeItem(ModTiers.DEMONITE, 10, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final DeferredItem<BaseAxeItem> BLOOD_LUST_CLUSTER = registerItem("blood_lust_cluster", "嗜血狂斧", () -> new BaseAxeItem(ModTiers.CRIMTANE, 11, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());

    public static final DeferredItem<BaseAxeItem> COBALT_WARAXE = registerItem("cobalt_waraxe", "钴战斧", () -> new BaseAxeItem(ModTiers.COBALT, 15, 1.4F, unbreakable(), attributes(0, 0.55), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> PALLADIUM_WARAXE = registerItem("palladium_waraxe", "钯金战斧", () -> new BaseAxeItem(ModTiers.PALLADIUM, 16, 1.4F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> MYTHRIL_WARAXE = registerItem("mythril_waraxe", "秘银战斧", () -> new BaseAxeItem(ModTiers.MYTHRIL, 18, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> ORICHALCUM_WARAXE = registerItem("orichalcum_waraxe", "山铜战斧", () -> new BaseAxeItem(ModTiers.ORICHALCUM, 19, 1.6F, unbreakable(), attributes(0, 0.65), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> ADAMANTITE_WARAXE = registerItem("adamantite_waraxe", "精金战斧", () -> new BaseAxeItem(ModTiers.ADAMANTITE, 21, 2.0F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> TITANIUM_WARAXE = registerItem("titanium_waraxe", "钛金战斧", () -> new BaseAxeItem(ModTiers.TITANIUM, 23, 2.0F, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED).hasImage());
    public static final DeferredItem<BaseAxeItem> CHLOROPHYTE_GREATAXE = registerItem("chlorophyte_greataxe", "叶绿巨斧", () -> new BaseAxeItem(ModTiers.CHLOROPHYTE, 26, 2.5F, unbreakable(), attributes(1, 0.7), ModRarity.LIME));
    public static final DeferredItem<BaseAxeItem> LUCY_THE_AXE = registerItemWithEnglishName("lucy_the_axe", "露西斧", "Lucy the Axe", LucyTheAxe::new);
    public static final DeferredItem<AxeOfRegrowth> AXE_OF_REGROWTH = registerItemWithEnglishName("axe_of_regrowth", "再生之斧", "Axe of Regrowth", AxeOfRegrowth::new); // 再生之斧

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
