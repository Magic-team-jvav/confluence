package org.confluence.mod.common.data.gen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.function.BiConsumer;

/// 考古刷取上下文使用的本体战利品表。
///
/// 单独使用考古上下文生成，避免把刷取奖励混入方块或宝箱战利品校验规则。
public record ArchaeologySubProvider(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.OPAL_BLOCK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.OPAL.get()))));
    }
}
