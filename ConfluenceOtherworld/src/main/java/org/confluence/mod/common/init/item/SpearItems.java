package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.spear.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class SpearItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    // 矛参考数值为 泰拉wiki中的伤害÷2为基础值 击退值为一半
    public static final PortDeferredItem<SpearItem> SPEAR = registerItem("spear", "长矛", SpearItem::new);
    public static final PortDeferredItem<StormSpearItem> STORM_SPEAR = registerItem("storm_spear", "风暴长矛", StormSpearItem::new);
    public static final PortDeferredItem<TheRottedForkItem> THE_ROTTED_FORK = registerItem("the_rotted_fork", "腐叉", TheRottedForkItem::new);
    public static final PortDeferredItem<StreamstrikeHalberdItem> STREAMSTRIKE_HALBERD = registerItem("streamstrike_halberd", "穿流戟", StreamstrikeHalberdItem::new);
    public static final PortDeferredItem<DarkLanceItem> DARK_LANCE = registerItem("dark_lance", "暗黑长枪", DarkLanceItem::new);
    public static final PortDeferredItem<CobaltNaginataItem> COBALT_NAGINATA = registerItem("cobalt_naginata", "钴薙刀", CobaltNaginataItem::new);
    public static final PortDeferredItem<CobaltNaginataItem> PALLADIUM_PIKE = registerItem("palladium_pike", "钯金刺矛", CobaltNaginataItem::new);
    public static final PortDeferredItem<OrichalcumHalberdItem> ORICHALCUM_HALBERD = registerItem("orichalcum_halberd", "山铜长戟", OrichalcumHalberdItem::new);
    public static final PortDeferredItem<OrichalcumHalberdItem> MYTHRIL_HALBERD = registerItem("mythril_halberd", "秘银长戟", OrichalcumHalberdItem::new);
    public static final PortDeferredItem<AdamantiteGlaiveItem> ADAMANTITE_GLAIVE = registerItem("adamantite_glaive", "精金关刀", AdamantiteGlaiveItem::new);
    public static final PortDeferredItem<AdamantiteGlaiveItem> TITANIUM_TRIDENT = registerItem("titanium_trident", "钛金三叉戟", AdamantiteGlaiveItem::new);
    public static final PortDeferredItem<GungnirItem> GUNGNIR = registerItem("gungnir", "永恒之枪", GungnirItem::new);
    public static final PortDeferredItem<ChlorophytePartisanItem> CHLOROPHYTE_PARTISAN = registerItem("chlorophyte_partisan", "叶绿镋", ChlorophytePartisanItem::new);
    public static final PortDeferredItem<NorthPoleItem> NORTH_POLE = registerItem("north_pole", "北极", NorthPoleItem::new);
    public static final PortDeferredItem<MushroomSpearItem> MUSHROOM_SPEAR = registerItem("mushroom_spear", "蘑菇长矛", MushroomSpearItem::new);
    public static final PortDeferredItem<GhastlyglaiveItem> GHASTLY_GLAIVE = registerItem("ghastly_glaive", "恐怖关刀", GhastlyglaiveItem::new);

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
