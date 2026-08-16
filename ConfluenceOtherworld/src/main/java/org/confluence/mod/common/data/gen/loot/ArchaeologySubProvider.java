package org.confluence.mod.common.data.gen.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.function.BiConsumer;

/// 考古刷取上下文使用的本体战利品表。
///
/// <p>单独使用考古上下文生成，避免把刷取奖励混入方块或宝箱战利品校验规则。</p>
public final class ArchaeologySubProvider implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        output.accept(Confluence.asResource("archaeology/opal_ore"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.OPAL.get()))));
    }
}
