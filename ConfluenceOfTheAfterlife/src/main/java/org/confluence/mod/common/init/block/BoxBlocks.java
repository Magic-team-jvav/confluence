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
import org.confluence.mod.common.item.common.BoxBlockItem;

public class BoxBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    public static final DeferredBlock<Block> WOODEN_BOX = register("wooden_box", ModLootTables.WOODEN_BOX);
    public static final DeferredBlock<Block> IRON_BOX = register("iron_box", ModLootTables.IRON_BOX);
    public static final DeferredBlock<Block> GOLDEN_BOX = register("golden_box", ModLootTables.GOLDEN_BOX);
    public static final DeferredBlock<Block> JUNGLE_BOX = register("jungle_box", ModLootTables.JUNGLE_BOX);
    public static final DeferredBlock<Block> SKY_BOX = register("sky_box", ModLootTables.SKY_BOX);
    public static final DeferredBlock<Block> CORRUPT_BOX = register("corrupt_box", ModLootTables.CORRUPT_BOX);
    public static final DeferredBlock<Block> TR_CRIMSON_BOX = register("tr_crimson_box", ModLootTables.TR_CRIMSON_BOX);
    public static final DeferredBlock<Block> SACRED_BOX = register("sacred_box", ModLootTables.SACRED_BOX);
    public static final DeferredBlock<Block> DUNGEON_BOX = register("dungeon_box", ModLootTables.DUNGEON_BOX);
    public static final DeferredBlock<Block> FREEZE_BOX = register("freeze_box", ModLootTables.FREEZE_BOX);
    public static final DeferredBlock<Block> OASIS_BOX = register("oasis_box", ModLootTables.OASIS_BOX);
    public static final DeferredBlock<Block> OBSIDIAN_BOX = register("obsidian_box", ModLootTables.OBSIDIAN_BOX);
    public static final DeferredBlock<Block> OCEAN_BOX = register("ocean_box", ModLootTables.OCEAN_BOX);

    public static final DeferredBlock<Block> PEARLWOOD_BOX = register("pearlwood_box", ModLootTables.PEARLWOOD_BOX);
    public static final DeferredBlock<Block> MITHRIL_BOX = register("mithril_box", ModLootTables.MITHRIL_BOX);
    public static final DeferredBlock<Block> TITANIUM_BOX = register("titanium_box", ModLootTables.TITANIUM_BOX);
    public static final DeferredBlock<Block> THORNS_BOX = register("thorns_box", ModLootTables.THORNS_BOX);
    public static final DeferredBlock<Block> SPACE_BOX = register("space_box", ModLootTables.SPACE_BOX);
    public static final DeferredBlock<Block> DEFACED_BOX = register("defaced_box", ModLootTables.DEFACED_BOX);
    public static final DeferredBlock<Block> BLOOD_BOX = register("blood_box", ModLootTables.BLOOD_BOX);
    public static final DeferredBlock<Block> PROVIDENTIAL_BOX = register("providential_box", ModLootTables.PROVIDENTIAL_BOX);
    public static final DeferredBlock<Block> FENCING_BOX = register("fencing_box", ModLootTables.FENCING_BOX);
    public static final DeferredBlock<Block> CONIFEROUS_WOOD_BOX = register("coniferous_wood_box", ModLootTables.CONIFEROUS_WOOD_BOX);
    public static final DeferredBlock<Block> ILLUSION_BOX = register("illusion_box", ModLootTables.ILLUSION_BOX);
    public static final DeferredBlock<Block> HELL_STONE_BOX = register("hell_stone_box", ModLootTables.HELL_STONE_BOX);
    public static final DeferredBlock<Block> BEACH_BOX = register("beach_box", ModLootTables.BEACH_BOX);

    private static DeferredBlock<Block> register(String name, ResourceKey<LootTable> lootTable) {
        DeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
        ModItems.BLOCK_ITEMS.register(name, () -> new BoxBlockItem(block.get(), lootTable));
        return block;
    }
}
