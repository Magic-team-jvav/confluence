package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BasePickaxeAxeItem;

import java.util.function.Function;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class PickaxeAxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BasePickaxeAxeItem> PICKAXE_AXE = registerItemWithProperties("pickaxe_axe", "镐斧", properties -> new BasePickaxeAxeItem(ModTiers.HALLOWED, 35, 2.5F, unbreakable(), attributes(0, 0.475), ModRarity.LIGHT_RED));
    public static final DeferredItem<BasePickaxeAxeItem> SHROOMITE_DIGGING_CLAW = registerItemWithProperties("shroomite_digging_claw", "蘑菇矿挖爪", properties -> new BasePickaxeAxeItem(ModTiers.SHROOMITE, 45, 3.2F, unbreakable(), attributes(-1, 0.6), ModRarity.YELLOW));
    public static final DeferredItem<BasePickaxeAxeItem> PICKSAW = registerItemWithProperties("picksaw", "锯刃镐", properties -> new BasePickaxeAxeItem(ModTiers.LIHZAHRD, 34, 2.8F, unbreakable(), attributes(1, 0.55), ModRarity.LIME));

    private static <T extends Item> DeferredItem<T> registerItemWithProperties(String registryId, String chineseName, Function<Item.Properties, T> factory) {
        DeferredItem<T> entry = ITEMS.registerItem(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
