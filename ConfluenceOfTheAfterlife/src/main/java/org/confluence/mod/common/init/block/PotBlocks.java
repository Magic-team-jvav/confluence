package org.confluence.mod.common.init.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BasePotBlock;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Supplier;

public class PotBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    public static final Supplier<Block> FOREST_POT = registerWithItem("forest_pot", 1.0F, 0.002F);
    public static final Supplier<Block> TUNDRA_POT = registerWithItem("tundra_pot", 1.25F, 0.002167F);
    public static final Supplier<Block> OCEAN_POT = registerWithItem("ocean_pot", 1.25F, 0.002167F);
    public static final Supplier<Block> SPIDER_NEST_POT = registerWithItem("spider_nest_pot", 3.5F, 0.003676F);
    public static final Supplier<Block> UNDERGROUND_DESERT_POT = registerWithItem("underground_desert_pot", 1.25F, 0.002169F);
    public static final Supplier<Block> JUNGLE_POT = registerWithItem("jungle_pot", 1.75F, 0.0025F);
    public static final Supplier<Block> MARBLE_CAVE_POT = registerWithItem("marble_cave_pot", 2.0F, 0.002667F);
    public static final Supplier<Block> TR_CRIMSON_POT = registerWithItem("tr_crimson_pot", 1.6F, 0.00274F);
    public static final Supplier<Block> PYRAMID_POT = registerWithItem("pyramid_pot", 10.0F, 0.008F);
    public static final Supplier<Block> CORRUPTION_POT = registerWithItem("corruption_pot", 1.6F, 0.00274F);
    public static final Supplier<Block> DUNGEON_POT = registerWithItem("dungeon_pot", 1.9F, 0.002604F);
    public static final Supplier<Block> UNDERWORLD_POT = registerWithItem("underworld_pot", 2.1F, 0.00274F);
    public static final Supplier<Block> LIHZAHRD_POT = registerWithItem("lihzahrd_pot", 4.0F, 0.004F);

    public static Supplier<Block> registerWithItem(String id, float moneyRatio, float moneyHoleChance) {
        Supplier<Block> object = BLOCKS.register(id, () -> new BasePotBlock(moneyRatio, moneyHoleChance, Block.box(2, 0, 2, 14, 12, 14)));
        ModItems.BLOCK_ITEMS.register(id, () -> new BlockItem(object.get(), new Item.Properties()));
        return object;
    }
}
