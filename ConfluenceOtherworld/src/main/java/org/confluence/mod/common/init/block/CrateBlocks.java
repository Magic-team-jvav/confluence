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
    public static final DeferredBlock<Block> TR_CRIMSON_CRATE = register("tr_crimson_crate", ModLootTables.TR_CRIMSON_CRATE);
    public static final DeferredBlock<Block> HALLOWED_CRATE = register("hallowed_crate", ModLootTables.SACRED_CRATE);
    public static final DeferredBlock<Block> DUNGEON_CRATE = register("dungeon_crate", ModLootTables.DUNGEON_CRATE);
    public static final DeferredBlock<Block> FREEZE_CRATE = register("freeze_crate", ModLootTables.FREEZE_CRATE);
    public static final DeferredBlock<Block> OASIS_CRATE = register("oasis_crate", ModLootTables.OASIS_CRATE);
    public static final DeferredBlock<Block> OBSIDIAN_CRATE = register("obsidian_crate", ModLootTables.OBSIDIAN_CRATE);
    public static final DeferredBlock<Block> OCEAN_CRATE = register("ocean_crate", ModLootTables.OCEAN_CRATE);

    public static final DeferredBlock<Block> PEARLWOOD_CRATE = register("pearlwood_crate", ModLootTables.PEARLWOOD_CRATE);
    public static final DeferredBlock<Block> MYTHRIL_CRATE = register("mythril_crate", ModLootTables.MYTHRIL_CRATE);
    public static final DeferredBlock<Block> TITANIUM_CRATE = register("titanium_crate", ModLootTables.TITANIUM_CRATE);
    public static final DeferredBlock<Block> THORNS_CRATE = register("thorns_crate", ModLootTables.THORNS_CRATE);
    public static final DeferredBlock<Block> WILD_CRATE = register("wild_crate", ModLootTables.WILD_CRATE);
    public static final DeferredBlock<Block> SPACE_CRATE = register("space_crate", ModLootTables.SPACE_CRATE);
    public static final DeferredBlock<Block> DEFACED_CRATE = register("defaced_crate", ModLootTables.DEFACED_CRATE);
    public static final DeferredBlock<Block> BLOOD_CRATE = register("blood_crate", ModLootTables.BLOOD_CRATE);
    public static final DeferredBlock<Block> PROVIDENTIAL_CRATE = register("providential_crate", ModLootTables.PROVIDENTIAL_CRATE);
    public static final DeferredBlock<Block> FENCING_CRATE = register("fencing_crate", ModLootTables.FENCING_CRATE);
    public static final DeferredBlock<Block> CONIFEROUS_WOOD_CRATE = register("coniferous_wood_crate", ModLootTables.CONIFEROUS_WOOD_CRATE);
    public static final DeferredBlock<Block> ILLUSION_CRATE = register("illusion_crate", ModLootTables.ILLUSION_CRATE);
    public static final DeferredBlock<Block> HELL_STONE_CRATE = register("hell_stone_crate", ModLootTables.HELL_STONE_CRATE);
    public static final DeferredBlock<Block> BEACH_CRATE = register("beach_crate", ModLootTables.BEACH_CRATE);

    private static DeferredBlock<Block> register(String name, ResourceKey<LootTable> lootTable) {
        DeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
        ModItems.BLOCK_ITEMS.register(name, () -> new CrateBlockItem(block.get(), lootTable));
        return block;
    }
}
