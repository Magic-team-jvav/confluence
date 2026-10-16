package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BasePickaxeItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class PickaxeItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<PickaxeItem> CACTUS_PICKAXE = registerItem("cactus_pickaxe", "仙人掌镐", () -> new BasePickaxeItem(ModTiers.CACTUS, 4, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> COPPER_PICKAXE = registerItem("copper_pickaxe", "铜镐", () -> new BasePickaxeItem(ModTiers.COPPER, 4, 1.2F, new Item.Properties(), attributes(-1, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> TIN_PICKAXE = registerItem("tin_pickaxe", "锡镐", () -> new BasePickaxeItem(ModTiers.TIN, 5, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> LEAD_PICKAXE = registerItem("lead_pickaxe", "铅镐", () -> new BasePickaxeItem(ModTiers.LEAD, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> SILVER_PICKAXE = registerItem("silver_pickaxe", "银镐", () -> new BasePickaxeItem(ModTiers.SILVER, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> TUNGSTEN_PICKAXE = registerItem("tungsten_pickaxe", "钨镐", () -> new BasePickaxeItem(ModTiers.TUNGSTEN, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> GOLDEN_PICKAXE = registerItem("golden_pickaxe", "金镐", () -> new BasePickaxeItem(ModTiers.GOLD, 6, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> CANDY_CANE_PICKAXE = registerItem("candy_cane_pickaxe", "糖棒镐", () -> new BasePickaxeItem(ModTiers.CANDY_CANE, 7, 1.2F, unbreakable(), attributes(0, 0.25), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> FOSSIL_PICKAXE = registerItem("fossil_pickaxe", "化石镐", () -> new BasePickaxeItem(ModTiers.FOSSIL, 8, 1.2F, unbreakable(), attributes(0, 0.4), ModRarity.BLUE));
    public static final PortDeferredItem<PickaxeItem> ABYSSAL_PICKAXE = registerItem("abyssal_pickaxe", "潜渊镐", () -> new BasePickaxeItem(ModTiers.FOSSIL, 9, 1.2F, unbreakable(), attributes(0, 0.4), ModRarity.BLUE));
    public static final PortDeferredItem<PickaxeItem> MIASMA_PICKAXE = registerItem("miasma_pickaxe", "凝瘴镐", () -> new BasePickaxeItem(ModTiers.FOSSIL, 9, 1.2F, unbreakable(), attributes(0, 0.4), ModRarity.BLUE));
    public static final PortDeferredItem<PickaxeItem> COLD_CRYSTAL_PICKAXE = registerItem("cold_crystal_pickaxe", "寒晶镐", () -> new BasePickaxeItem(ModTiers.FOSSIL, 9, 1.2F, unbreakable(), attributes(0, 0.4), ModRarity.BLUE));
    public static final PortDeferredItem<PickaxeItem> BONE_PICKAXE = registerItem("bone_pickaxe", "骨镐", () -> new BasePickaxeItem(ModTiers.BONE, 8, 1.2F, unbreakable(), attributes(0, 0.3), ModRarity.BLUE));
    public static final PortDeferredItem<PickaxeItem> PLATINUM_PICKAXE = registerItem("platinum_pickaxe", "铂金镐", () -> new BasePickaxeItem(ModTiers.PLATINUM, 7, 1.2F, new Item.Properties(), attributes(0, 0.2), ModRarity.WHITE));
    public static final PortDeferredItem<PickaxeItem> REAVER_SHARK_PICKAXE = registerItem("reaver_shark_pickaxe", "掠夺鲨", () -> new BasePickaxeItem(ModTiers.REAVER_SHARK, 10, 1.2F, unbreakable(), attributes(0, 0.3), ModRarity.ORANGE).hasImage());
    public static final PortDeferredItem<PickaxeItem> NIGHTMARE_PICKAXE = registerItem("nightmare_pickaxe", "梦魇镐", () -> new BasePickaxeItem(ModTiers.DEMONITE, 9, 1.2F, unbreakable(), attributes(0, 0.3), ModRarity.BLUE));
    public static final PortDeferredItem<PickaxeItem> DEATHBRINGER_PICKAXE = registerItem("deathbringer_pickaxe", "死亡使者镐", () -> new BasePickaxeItem(ModTiers.CRIMTANE, 12, 1.2F, unbreakable(), attributes(0, 0.35), ModRarity.BLUE));
    public static final PortDeferredItem<PickaxeItem> MOLTEN_PICKAXE = registerItem("molten_pickaxe", "熔岩镐", () -> new BasePickaxeItem(ModTiers.HELLSTONE, 12, 1.3F, unbreakable(), attributes(0, 0.2), ModRarity.ORANGE));

    public static final PortDeferredItem<PickaxeItem> COBALT_PICKAXE = registerItem("cobalt_pickaxe", "钴镐", () -> new BasePickaxeItem(ModTiers.COBALT, 10, 1.4F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<PickaxeItem> PALLADIUM_PICKAXE = registerItem("palladium_pickaxe", "钯金镐", () -> new BasePickaxeItem(ModTiers.PALLADIUM, 12, 1.4F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<PickaxeItem> MYTHRIL_PICKAXE = registerItem("mythril_pickaxe", "秘银镐", () -> new BasePickaxeItem(ModTiers.MYTHRIL, 15, 1.6F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<PickaxeItem> ORICHALCUM_PICKAXE = registerItem("orichalcum_pickaxe", "山铜镐", () -> new BasePickaxeItem(ModTiers.ORICHALCUM, 17, 1.6F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<PickaxeItem> ADAMANTITE_PICKAXE = registerItem("adamantite_pickaxe", "精金镐", () -> new BasePickaxeItem(ModTiers.ADAMANTITE, 20, 1.8F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<PickaxeItem> TITANIUM_PICKAXE = registerItem("titanium_pickaxe", "钛金镐", () -> new BasePickaxeItem(ModTiers.TITANIUM, 21, 1.8F, unbreakable(), attributes(0, 0.5), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<PickaxeItem> SPECTRE_PICKAXE = registerItem("spectre_pickaxe", "幽灵镐", () -> new BasePickaxeItem(ModTiers.SPECTRE, 24, 2.2F, unbreakable(), attributes(3, 0.525), ModRarity.YELLOW));
    public static final PortDeferredItem<PickaxeItem> CHLOROPHYTE_PICKAXE = registerItem("chlorophyte_pickaxe", "叶绿镐", () -> new BasePickaxeItem(ModTiers.CHLOROPHYTE, 26, 2.2F, unbreakable(), attributes(3, 0.5), ModRarity.LIME));
    public static final PortDeferredItem<PickaxeItem> SOLAR_FLARE_PICKAXE = registerItem("solar_flare_pickaxe", "耀斑镐", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));
    public static final PortDeferredItem<PickaxeItem> VORTEX_PICKAXE = registerItem("vortex_pickaxe", "星旋镐", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));
    public static final PortDeferredItem<PickaxeItem> NEBULA_PICKAXE = registerItem("nebula_pickaxe", "星云镐", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));
    public static final PortDeferredItem<PickaxeItem> STARDUST_PICKAXE = registerItem("stardust_pickaxe", "星尘镐", () -> new BasePickaxeItem(ModTiers.LUMINITE, 28, 3.2F, unbreakable(), attributes(3, 0.55), ModRarity.RED));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
