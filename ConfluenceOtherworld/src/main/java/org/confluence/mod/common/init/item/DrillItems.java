package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseDrillItem;

import java.util.function.Function;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class DrillItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseDrillItem> COBALT_DRILL = registerItemWithProperties("cobalt_drill", "钴钻头", properties -> new BaseDrillItem(ModTiers.COBALT, 15, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> PALLADIUM_DRILL = registerItemWithProperties("palladium_drill", "钯金钻头", properties -> new BaseDrillItem(ModTiers.PALLADIUM, 16, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> MYTHRIL_DRILL = registerItemWithProperties("mythril_drill", "秘银钻头", properties -> new BaseDrillItem(ModTiers.MYTHRIL, 17, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> ORICHALCUM_DRILL = registerItemWithProperties("orichalcum_drill", "山铜钻头", properties -> new BaseDrillItem(ModTiers.ORICHALCUM, 18, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> ADAMANTITE_DRILL = registerItemWithProperties("adamantite_drill", "精金钻头", properties -> new BaseDrillItem(ModTiers.ADAMANTITE, 19, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> TITANIUM_DRILL = registerItemWithProperties("titanium_drill", "钛金钻头", properties -> new BaseDrillItem(ModTiers.TITANIUM, 20, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> CHLOROPHYTE_DRILL = registerItemWithProperties("chlorophyte_drill", "叶绿钻头", properties -> new BaseDrillItem(ModTiers.CHLOROPHYTE, 24, 2.7F, unbreakable(), attributes(0, 0.1), ModRarity.LIME));
    public static final DeferredItem<BaseDrillItem> DRAX = registerItemWithProperties("drax", "斧钻", properties -> new BaseDrillItem(ModTiers.HALLOWED, 28, 2.5F, unbreakable(), attributes(-1, 0.475), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseDrillItem> SOLAR_FLARE_DRILL = registerItemWithProperties("solar_flare_drill", "耀斑钻头", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<BaseDrillItem> VORTEX_DRILL = registerItemWithProperties("vortex_drill", "星旋钻头", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<BaseDrillItem> NEBULA_DRILL = registerItemWithProperties("nebula_drill", "星云钻头", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final DeferredItem<BaseDrillItem> STARDUST_DRILL = registerItemWithProperties("stardust_drill", "星尘钻头", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    // todo 激光钻头

    private static <T extends Item> DeferredItem<T> registerItemWithProperties(String registryId, String chineseName, Function<Item.Properties, T> factory) {
        DeferredItem<T> entry = ITEMS.registerItem(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
