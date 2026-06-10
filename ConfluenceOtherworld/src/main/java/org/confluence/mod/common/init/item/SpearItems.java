package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.spear.*;

public class SpearItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);
    // 矛参考数值为 泰拉wiki中的伤害÷2为基础值 击退值为一半
    public static final RegistryObject<SpearItem> SPEAR = ITEMS.register("spear", SpearItem::new);
    public static final RegistryObject<StormSpearItem> STORM_SPEAR = ITEMS.register("storm_spear", StormSpearItem::new);
    public static final RegistryObject<TheRottedForkItem> THE_ROTTED_FORK = ITEMS.register("the_rotted_fork", TheRottedForkItem::new);
    public static final RegistryObject<StreamstrikeHalberdItem> STREAMSTRIKE_HALBERD = ITEMS.register("streamstrike_halberd", StreamstrikeHalberdItem::new);
    public static final RegistryObject<DarkLanceItem> DARK_LANCE = ITEMS.register("dark_lance", DarkLanceItem::new);
    public static final RegistryObject<CobaltNaginataItem> COBALT_NAGINATA = ITEMS.register("cobalt_naginata", CobaltNaginataItem::new);
    public static final RegistryObject<CobaltNaginataItem> PALLADIUM_PIKE = ITEMS.register("palladium_pike", CobaltNaginataItem::new);
    public static final RegistryObject<OrichalcumHalberdItem> ORICHALCUM_HALBERD = ITEMS.register("orichalcum_halberd", OrichalcumHalberdItem::new);
    public static final RegistryObject<OrichalcumHalberdItem> MYTHRIL_HALBERD = ITEMS.register("mythril_halberd", OrichalcumHalberdItem::new);
    public static final RegistryObject<AdamantiteGlaiveItem> ADAMANTITE_GLAIVE = ITEMS.register("adamantite_glaive", AdamantiteGlaiveItem::new);
    public static final RegistryObject<AdamantiteGlaiveItem> TITANIUM_TRIDENT = ITEMS.register("titanium_trident", AdamantiteGlaiveItem::new);
    public static final RegistryObject<GungnirItem> GUNGNIR = ITEMS.register("gungnir", GungnirItem::new);
    public static final RegistryObject<ChlorophytePartisanItem> CHLOROPHYTE_PARTISAN = ITEMS.register("chlorophyte_partisan", ChlorophytePartisanItem::new);
    public static final RegistryObject<NorthPoleItem> NORTH_POLE = ITEMS.register("north_pole", NorthPoleItem::new);
    public static final RegistryObject<MushroomSpearItem> MUSHROOM_SPEAR = ITEMS.register("mushroom_spear", MushroomSpearItem::new);
    public static final RegistryObject<GhastlyglaiveItem> GHASTLY_GLAIVE = ITEMS.register("ghastly_glaive", GhastlyglaiveItem::new);
}
