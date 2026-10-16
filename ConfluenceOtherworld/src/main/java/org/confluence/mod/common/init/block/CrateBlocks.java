package org.confluence.mod.common.init.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CrateBlockItem;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class CrateBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    public static final DeferredBlock<Block> WOODEN_CRATE = register("wooden_crate", "木匣", ModLootTables.WOODEN_CRATE);
    public static final DeferredBlock<Block> IRON_CRATE = register("iron_crate", "铁匣", ModLootTables.IRON_CRATE);
    public static final DeferredBlock<Block> GOLDEN_CRATE = register("golden_crate", "金匣", ModLootTables.GOLDEN_CRATE);
    public static final DeferredBlock<Block> JUNGLE_CRATE = register("jungle_crate", "丛林匣", ModLootTables.JUNGLE_CRATE);
    public static final DeferredBlock<Block> SAVANNA_CRATE = register("savanna_crate", "热带草原匣", ModLootTables.SAVANNA_CRATE);
    public static final DeferredBlock<Block> SKY_CRATE = register("sky_crate", "天空匣", ModLootTables.SKY_CRATE);
    public static final DeferredBlock<Block> CORRUPT_CRATE = register("corrupt_crate", "腐化匣", ModLootTables.CORRUPT_CRATE);
    public static final DeferredBlock<Block> CRIMSON_CRATE = register("crimson_crate", "猩红匣", ModLootTables.CRIMSON_CRATE);
    public static final DeferredBlock<Block> HALLOWED_CRATE = register("hallowed_crate", "神圣匣", ModLootTables.HALLOWED_CRATE);
    public static final DeferredBlock<Block> DUNGEON_CRATE = register("dungeon_crate", "地牢匣", ModLootTables.DUNGEON_CRATE);
    public static final DeferredBlock<Block> FROZEN_CRATE = register("frozen_crate", "冰冻匣", ModLootTables.FROZEN_CRATE);
    public static final DeferredBlock<Block> OASIS_CRATE = register("oasis_crate", "绿洲匣", ModLootTables.OASIS_CRATE);
    public static final DeferredBlock<Block> OBSIDIAN_CRATE = register("obsidian_crate", "黑曜石匣", ModLootTables.OBSIDIAN_CRATE);
    public static final DeferredBlock<Block> OCEAN_CRATE = register("ocean_crate", "海洋匣", ModLootTables.OCEAN_CRATE);

    public static final DeferredBlock<Block> PEARLWOOD_CRATE = register("pearlwood_crate", "珍珠木匣", ModLootTables.PEARLWOOD_CRATE);
    public static final DeferredBlock<Block> MYTHRIL_CRATE = register("mythril_crate", "秘银匣", ModLootTables.MYTHRIL_CRATE);
    public static final DeferredBlock<Block> TITANIUM_CRATE = register("titanium_crate", "钛金匣", ModLootTables.TITANIUM_CRATE);
    public static final DeferredBlock<Block> BRAMBLE_CRATE = register("bramble_crate", "荆棘匣", ModLootTables.BRAMBLE_CRATE);
    public static final DeferredBlock<Block> WILD_CRATE = register("wild_crate", "野性匣", ModLootTables.WILD_CRATE);
    public static final DeferredBlock<Block> AZURE_CRATE = register("azure_crate", "天蓝匣", ModLootTables.AZURE_CRATE);
    public static final DeferredBlock<Block> DEFILED_CRATE = register("defiled_crate", "污损匣", ModLootTables.DEFILED_CRATE);
    public static final DeferredBlock<Block> HEMATIC_CRATE = register("hematic_crate", "血匣", ModLootTables.HEMATIC_CRATE);
    public static final DeferredBlock<Block> DIVINE_CRATE = register("divine_crate", "天赐匣", ModLootTables.DIVINE_CRATE);
    public static final DeferredBlock<Block> STOCKADE_CRATE = register("stockade_crate", "围栏匣", ModLootTables.STOCKADE_CRATE);
    public static final DeferredBlock<Block> BOREAL_CRATE = register("boreal_crate", "针叶木匣", ModLootTables.BOREAL_CRATE);
    public static final DeferredBlock<Block> MIRAGE_CRATE = register("mirage_crate", "幻象匣", ModLootTables.MIRAGE_CRATE);
    public static final DeferredBlock<Block> HELLSTONE_CRATE = register("hellstone_crate", "狱石匣", ModLootTables.HELLSTONE_CRATE);
    public static final DeferredBlock<Block> SEASIDE_CRATE = register("seaside_crate", "海边匣", ModLootTables.SEASIDE_CRATE);

    private static DeferredBlock<Block> register(String name, ResourceKey<LootTable> lootTable) {
        DeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
        ModItems.BLOCK_ITEMS.register(name, () -> new CrateBlockItem(block.get(), lootTable));
        return block;
    }

    private static DeferredBlock<Block> register(String registryId, String chineseName, ResourceKey<LootTable> lootTable) {
        DeferredBlock<Block> entry = register(registryId, lootTable);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
