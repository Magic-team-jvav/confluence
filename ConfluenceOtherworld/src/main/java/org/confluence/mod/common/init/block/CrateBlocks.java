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

public class CrateBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    public static final DeferredBlock<Block> WOODEN_CRATE = register("wooden_crate", ModLootTables.WOODEN_CRATE);
    public static final DeferredBlock<Block> IRON_CRATE = register("iron_crate", ModLootTables.IRON_CRATE);
    public static final DeferredBlock<Block> GOLDEN_CRATE = register("golden_crate", ModLootTables.GOLDEN_CRATE);
    public static final DeferredBlock<Block> JUNGLE_CRATE = register("jungle_crate", ModLootTables.JUNGLE_CRATE);
    public static final DeferredBlock<Block> SAVANNA_CRATE = register("savanna_crate", ModLootTables.SAVANNA_CRATE);
    public static final DeferredBlock<Block> SKY_CRATE = register("sky_crate", ModLootTables.SKY_CRATE);
    public static final DeferredBlock<Block> CORRUPT_CRATE = register("corrupt_crate", ModLootTables.CORRUPT_CRATE);
    public static final DeferredBlock<Block> CRIMSON_CRATE = register("crimson_crate", ModLootTables.CRIMSON_CRATE);
    public static final DeferredBlock<Block> HALLOWED_CRATE = register("hallowed_crate", ModLootTables.HALLOWED_CRATE);
    public static final DeferredBlock<Block> DUNGEON_CRATE = register("dungeon_crate", ModLootTables.DUNGEON_CRATE);
    public static final DeferredBlock<Block> FROZEN_CRATE = register("frozen_crate", ModLootTables.FROZEN_CRATE);
    public static final DeferredBlock<Block> OASIS_CRATE = register("oasis_crate", ModLootTables.OASIS_CRATE);
    public static final DeferredBlock<Block> OBSIDIAN_CRATE = register("obsidian_crate", ModLootTables.OBSIDIAN_CRATE);
    public static final DeferredBlock<Block> OCEAN_CRATE = register("ocean_crate", ModLootTables.OCEAN_CRATE);

    public static final DeferredBlock<Block> PEARLWOOD_CRATE = register("pearlwood_crate", ModLootTables.PEARLWOOD_CRATE);
    public static final DeferredBlock<Block> MYTHRIL_CRATE = register("mythril_crate", ModLootTables.MYTHRIL_CRATE);
    public static final DeferredBlock<Block> TITANIUM_CRATE = register("titanium_crate", ModLootTables.TITANIUM_CRATE);
    public static final DeferredBlock<Block> BRAMBLE_CRATE = register("bramble_crate", ModLootTables.BRAMBLE_CRATE);
    public static final DeferredBlock<Block> WILD_CRATE = register("wild_crate", ModLootTables.WILD_CRATE);
    public static final DeferredBlock<Block> AZURE_CRATE = register("azure_crate", ModLootTables.AZURE_CRATE);
    public static final DeferredBlock<Block> DEFILED_CRATE = register("defiled_crate", ModLootTables.DEFILED_CRATE);
    public static final DeferredBlock<Block> HEMATIC_CRATE = register("hematic_crate", ModLootTables.HEMATIC_CRATE);
    public static final DeferredBlock<Block> DIVINE_CRATE = register("divine_crate", ModLootTables.DIVINE_CRATE);
    public static final DeferredBlock<Block> STOCKADE_CRATE = register("stockade_crate", ModLootTables.STOCKADE_CRATE);
    public static final DeferredBlock<Block> BOREAL_CRATE = register("boreal_crate", ModLootTables.BOREAL_CRATE);
    public static final DeferredBlock<Block> MIRAGE_CRATE = register("mirage_crate", ModLootTables.MIRAGE_CRATE);
    public static final DeferredBlock<Block> HELLSTONE_CRATE = register("hellstone_crate", ModLootTables.HELLSTONE_CRATE);
    public static final DeferredBlock<Block> SEASIDE_CRATE = register("seaside_crate", ModLootTables.SEASIDE_CRATE);

    private static DeferredBlock<Block> register(String name, ResourceKey<LootTable> lootTable) {
        DeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
        ModItems.BLOCK_ITEMS.register(name, () -> new CrateBlockItem(block.get(), lootTable));
        return block;
    }
}
