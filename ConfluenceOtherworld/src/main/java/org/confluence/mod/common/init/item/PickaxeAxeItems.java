package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BasePickaxeAxeItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class PickaxeAxeItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BasePickaxeAxeItem> PICKAXE_AXE = ITEMS.register("pickaxe_axe", () -> new BasePickaxeAxeItem(ModTiers.HALLOWED, 35, 2.5F, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BasePickaxeAxeItem> SHROOMITE_DIGGING_CLAW = ITEMS.register("shroomite_digging_claw", () -> new BasePickaxeAxeItem(ModTiers.SHROOMITE, 45, 3.2F, unbreakable(), attributes(-1, 0.6), ModRarity.YELLOW));
    public static final PortDeferredItem<BasePickaxeAxeItem> PICKSAW = ITEMS.register("picksaw", () -> new BasePickaxeAxeItem(ModTiers.LIHZAHRD, 34, 2.8F, unbreakable(), attributes(1, 0.55), ModRarity.LIME));
}
