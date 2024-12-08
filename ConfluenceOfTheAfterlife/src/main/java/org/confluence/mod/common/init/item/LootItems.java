package org.confluence.mod.common.init.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.item.common.LootItem;
import org.confluence.terra_curio.common.component.ModRarity;

import static org.confluence.terra_curio.common.component.ModRarity.BLUE;

public class LootItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredItem<LootItem> CLAM = register("clam", BLUE, ModLootTables.CLAM);
    public static DeferredItem<LootItem> register(String name, ModRarity rarity, ResourceKey<LootTable> lootTable) {
        return ITEMS.register(name, () -> new LootItem(rarity, lootTable));
    }
}
