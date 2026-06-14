package org.confluence.mod.common.init.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CrateBlockItem;
import org.mesdag.portlib.registries.PortBlockRegistration;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class CrateBlocks {
    public static void init() {}

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);

    public static final PortDeferredBlock<Block> WOODEN_CRATE = register("wooden_crate", ModLootTables.WOODEN_CRATE);
    public static final PortDeferredBlock<Block> IRON_CRATE = register("iron_crate", ModLootTables.IRON_CRATE);
    public static final PortDeferredBlock<Block> GOLDEN_CRATE = register("golden_crate", ModLootTables.GOLDEN_CRATE);
    public static final PortDeferredBlock<Block> JUNGLE_CRATE = register("jungle_crate", ModLootTables.JUNGLE_CRATE);
    public static final PortDeferredBlock<Block> SAVANNA_CRATE = register("savanna_crate", ModLootTables.SAVANNA_CRATE);
    public static final PortDeferredBlock<Block> SKY_CRATE = register("sky_crate", ModLootTables.SKY_CRATE);
    public static final PortDeferredBlock<Block> CORRUPT_CRATE = register("corrupt_crate", ModLootTables.CORRUPT_CRATE);
    public static final PortDeferredBlock<Block> CRIMSON_CRATE = register("crimson_crate", ModLootTables.CRIMSON_CRATE);
    public static final PortDeferredBlock<Block> HALLOWED_CRATE = register("hallowed_crate", ModLootTables.HALLOWED_CRATE);
    public static final PortDeferredBlock<Block> DUNGEON_CRATE = register("dungeon_crate", ModLootTables.DUNGEON_CRATE);
    public static final PortDeferredBlock<Block> FROZEN_CRATE = register("frozen_crate", ModLootTables.FROZEN_CRATE);
    public static final PortDeferredBlock<Block> OASIS_CRATE = register("oasis_crate", ModLootTables.OASIS_CRATE);
    public static final PortDeferredBlock<Block> OBSIDIAN_CRATE = register("obsidian_crate", ModLootTables.OBSIDIAN_CRATE);
    public static final PortDeferredBlock<Block> OCEAN_CRATE = register("ocean_crate", ModLootTables.OCEAN_CRATE);

    public static final PortDeferredBlock<Block> PEARLWOOD_CRATE = register("pearlwood_crate", ModLootTables.PEARLWOOD_CRATE);
    public static final PortDeferredBlock<Block> MYTHRIL_CRATE = register("mythril_crate", ModLootTables.MYTHRIL_CRATE);
    public static final PortDeferredBlock<Block> TITANIUM_CRATE = register("titanium_crate", ModLootTables.TITANIUM_CRATE);
    public static final PortDeferredBlock<Block> BRAMBLE_CRATE = register("bramble_crate", ModLootTables.BRAMBLE_CRATE);
    public static final PortDeferredBlock<Block> WILD_CRATE = register("wild_crate", ModLootTables.WILD_CRATE);
    public static final PortDeferredBlock<Block> AZURE_CRATE = register("azure_crate", ModLootTables.AZURE_CRATE);
    public static final PortDeferredBlock<Block> DEFILED_CRATE = register("defiled_crate", ModLootTables.DEFILED_CRATE);
    public static final PortDeferredBlock<Block> HEMATIC_CRATE = register("hematic_crate", ModLootTables.HEMATIC_CRATE);
    public static final PortDeferredBlock<Block> DIVINE_CRATE = register("divine_crate", ModLootTables.DIVINE_CRATE);
    public static final PortDeferredBlock<Block> STOCKADE_CRATE = register("stockade_crate", ModLootTables.STOCKADE_CRATE);
    public static final PortDeferredBlock<Block> BOREAL_CRATE = register("boreal_crate", ModLootTables.BOREAL_CRATE);
    public static final PortDeferredBlock<Block> MIRAGE_CRATE = register("mirage_crate", ModLootTables.MIRAGE_CRATE);
    public static final PortDeferredBlock<Block> HELLSTONE_CRATE = register("hellstone_crate", ModLootTables.HELLSTONE_CRATE);
    public static final PortDeferredBlock<Block> SEASIDE_CRATE = register("seaside_crate", ModLootTables.SEASIDE_CRATE);

    private static PortDeferredBlock<Block> register(String name, ResourceLocation lootTable) {
        PortDeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
        ModItems.BLOCK_ITEMS.register(name, () -> new CrateBlockItem(block.get(), lootTable));
        return block;
    }
}
