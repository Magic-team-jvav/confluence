package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BasePickaxeAxeItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class PickaxeAxeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BasePickaxeAxeItem> PICKAXE_AXE = ITEMS.registerItem("pickaxe_axe", properties -> new BasePickaxeAxeItem(ModTiers.HALLOWED, 35, 2.5F, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED));
    public static final RegistryObject<BasePickaxeAxeItem> SHROOMITE_DIGGING_CLAW = ITEMS.registerItem("shroomite_digging_claw", properties -> new BasePickaxeAxeItem(ModTiers.SHROOMITE, 45, 3.2F, unbreakable(), attributes(-1, 0.6), ModRarity.YELLOW));
    public static final RegistryObject<BasePickaxeAxeItem> PICKSAW = ITEMS.registerItem("picksaw", properties -> new BasePickaxeAxeItem(ModTiers.LIHZAHRD, 34, 2.8F, unbreakable(), attributes(1, 0.55), ModRarity.LIME));
}
