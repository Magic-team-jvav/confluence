package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHamaxeItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HamaxeItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseHamaxeItem> METEOR_HAMAXE = ITEMS.register("meteor_hamaxe", () -> new BaseHamaxeItem(ModTiers.METEOR, 20, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.BLUE).hasImage());
    public static final PortDeferredItem<BaseHamaxeItem> MOLTEN_HAMAXE = ITEMS.register("molten_hamaxe", () -> new BaseHamaxeItem(ModTiers.HELLSTONE, 23, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.ORANGE).hasImage());
    public static final PortDeferredItem<BaseHamaxeItem> HAEMORRHAXE = ITEMS.register("haemorrhaxe", () -> new BaseHamaxeItem(ModTiers.CRIMTANE, 30, 0.9F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseHamaxeItem> SPECTRE_HAMAXE = ITEMS.register("spectre_hamaxe", () -> new BaseHamaxeItem(ModTiers.SPECTRE, 45, 1.0F, unbreakable(), attributes(3, 0.7), ModRarity.YELLOW));
    public static final PortDeferredItem<BaseHamaxeItem> SOLAR_FLARE_HAMAXE = ITEMS.register("solar_flare_hamaxe", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.2F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final PortDeferredItem<BaseHamaxeItem> VORTEX_HAMAXE = ITEMS.register("vortex_hamaxe", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.3F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final PortDeferredItem<BaseHamaxeItem> NEBULA_HAMAXE = ITEMS.register("nebula_hamaxe", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.4F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final PortDeferredItem<BaseHamaxeItem> STARDUST_HAMAXE = ITEMS.register("stardust_hamaxe", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.6F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final PortDeferredItem<BaseHamaxeItem> THE_AXE = ITEMS.register("the_axe", () -> new BaseHamaxeItem(ModTiers.CHLOROPHYTE, 72, 1.8F, unbreakable(), attributes(1, 0.725), ModRarity.YELLOW));
}
