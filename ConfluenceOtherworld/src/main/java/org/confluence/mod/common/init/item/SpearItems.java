package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.spear.*;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class SpearItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    // 矛参考数值为 泰拉wiki中的伤害÷2为基础值 击退值为一半
    public static final DeferredItem<SpearItem> SPEAR = registerItem("spear", "长矛", SpearItem::new);
    public static final DeferredItem<StormSpearItem> STORM_SPEAR = registerItem("storm_spear", "风暴长矛", StormSpearItem::new);
    public static final DeferredItem<TheRottedForkItem> THE_ROTTED_FORK = registerItem("the_rotted_fork", "腐叉", TheRottedForkItem::new);
    public static final DeferredItem<StreamstrikeHalberdItem> STREAMSTRIKE_HALBERD = registerItem("streamstrike_halberd", "穿流戟", StreamstrikeHalberdItem::new);
    public static final DeferredItem<DarkLanceItem> DARK_LANCE = registerItem("dark_lance", "暗黑长枪", DarkLanceItem::new);
    public static final DeferredItem<CobaltNaginataItem> COBALT_NAGINATA = registerItem("cobalt_naginata", "钴薙刀", CobaltNaginataItem::new);
    public static final DeferredItem<CobaltNaginataItem> PALLADIUM_PIKE = registerItem("palladium_pike", "钯金刺矛", CobaltNaginataItem::new);
    public static final DeferredItem<OrichalcumHalberdItem> ORICHALCUM_HALBERD = registerItem("orichalcum_halberd", "山铜长戟", OrichalcumHalberdItem::new);
    public static final DeferredItem<OrichalcumHalberdItem> MYTHRIL_HALBERD = registerItem("mythril_halberd", "秘银长戟", OrichalcumHalberdItem::new);
    public static final DeferredItem<AdamantiteGlaiveItem> ADAMANTITE_GLAIVE = registerItem("adamantite_glaive", "精金关刀", AdamantiteGlaiveItem::new);
    public static final DeferredItem<AdamantiteGlaiveItem> TITANIUM_TRIDENT = registerItem("titanium_trident", "钛金三叉戟", AdamantiteGlaiveItem::new);
    public static final DeferredItem<GungnirItem> GUNGNIR = registerItem("gungnir", "永恒之枪", GungnirItem::new);
    public static final DeferredItem<ChlorophytePartisanItem> CHLOROPHYTE_PARTISAN = registerItem("chlorophyte_partisan", "叶绿镋", ChlorophytePartisanItem::new);
    public static final DeferredItem<NorthPoleItem> NORTH_POLE = registerItem("north_pole", "北极", NorthPoleItem::new);
    public static final DeferredItem<MushroomSpearItem> MUSHROOM_SPEAR = registerItem("mushroom_spear", "蘑菇长矛", MushroomSpearItem::new);
    public static final DeferredItem<GhastlyglaiveItem> GHASTLY_GLAIVE = registerItem("ghastly_glaive", "恐怖关刀", GhastlyglaiveItem::new);

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
