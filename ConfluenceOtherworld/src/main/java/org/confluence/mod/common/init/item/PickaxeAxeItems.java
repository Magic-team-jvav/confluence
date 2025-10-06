package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BasePickaxeAxeItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class PickaxeAxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BasePickaxeAxeItem> PICKAXE_AXE = ITEMS.registerItem("pickaxe_axe", properties -> new BasePickaxeAxeItem(ModTiers.HALLOWED, 35, 2.5F, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED));
    public static final DeferredItem<BasePickaxeAxeItem> SHROOMITE_DIGGING_CLAW = ITEMS.registerItem("shroomite_digging_claw", properties -> new BasePickaxeAxeItem(ModTiers.SHROOMITE, 45, 3.2F, unbreakable(), attributes(-1, 0.6), ModRarity.YELLOW));
    public static final DeferredItem<BasePickaxeAxeItem> PICKSAW = ITEMS.registerItem("picksaw", properties -> new BasePickaxeAxeItem(ModTiers.LIHZAHRD, 34, 2.8F, unbreakable(), attributes(1, 0.55), ModRarity.LIME));
}
