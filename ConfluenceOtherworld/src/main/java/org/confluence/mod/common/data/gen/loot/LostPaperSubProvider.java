package org.confluence.mod.common.data.gen.loot;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.BiConsumer;

/// 生成地牢散落纸张的文本与内嵌图像变体。
public record LostPaperSubProvider(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(Confluence.asResourceKey(Registries.LOOT_TABLE, "lost_paper/dungeon"), LootTable.lootTable().withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(ModItems.MYSTERIOUS_NOTE).setWeight(5)
                        .apply(noteLore(0, 0, 2, 5))
                        .apply(SetNameFunction.setName(Component.translatable("item.confluence.mysterious_note.name_structure_0").withStyle(style -> style.withItalic(false)), SetNameFunction.Target.CUSTOM_NAME)))
                .add(LootItem.lootTableItem(ModItems.MYSTERIOUS_NOTE).setWeight(5)
                        .apply(noteLore(2, 1, 1, 9))
                        .apply(SetNameFunction.setName(Component.translatable("item.confluence.mysterious_note.name_structure_0").withStyle(style -> style.withItalic(false)), SetNameFunction.Target.CUSTOM_NAME)))
                .add(LootItem.lootTableItem(Items.PAPER).setWeight(10))));
    }

    private static SetLoreFunction.Builder noteLore(int handwriting, int textVariant, int textLines, int emptyLines) {
        SetLoreFunction.Builder lore = SetLoreFunction.setLore().setMode(ListOperation.ReplaceAll.INSTANCE)
                .addLine(Component.translatable("lore.confluence.mysterious_note.handwriting_" + handwriting).withStyle(ChatFormatting.DARK_GRAY));
        for (int index = 0; index < textLines; index++) {
            lore.addLine(Component.translatable("lore.confluence.mysterious_note_structure_" + textVariant + "_" + index).withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false)));
        }
        for (int index = 0; index < emptyLines; index++) lore.addLine(CommonComponents.EMPTY);
        return lore.addLine(Component.literal(" ").append(Component.literal(Integer.toString(textVariant + 1)).withStyle(style -> style.withFont(Confluence.asResource("paper_image")).withItalic(false).withColor(0xFFFFFF))));
    }
}
