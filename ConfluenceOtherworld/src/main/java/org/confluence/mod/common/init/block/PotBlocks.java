package org.confluence.mod.common.init.block;

import net.minecraft.world.level.block.Block;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BasePotBlock;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.registries.PortBlockRegistration;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class PotBlocks {
    public static void init() {}

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);

    public static final PortDeferredBlock<BasePotBlock> FOREST_POT = registerWithItem("forest_pot", 1.0F, 0.002F);
    public static final PortDeferredBlock<BasePotBlock> TUNDRA_POT = registerWithItem("tundra_pot", 1.25F, 0.002167F);
    public static final PortDeferredBlock<BasePotBlock> OCEAN_POT = registerWithItem("ocean_pot", 1.25F, 0.002167F);
    public static final PortDeferredBlock<BasePotBlock> SPIDER_NEST_POT = registerWithItem("spider_nest_pot", 3.5F, 0.003676F);
    public static final PortDeferredBlock<BasePotBlock> UNDERGROUND_DESERT_POT = registerWithItem("underground_desert_pot", 1.25F, 0.002169F);
    public static final PortDeferredBlock<BasePotBlock> JUNGLE_POT = registerWithItem("jungle_pot", 1.75F, 0.0025F);
    public static final PortDeferredBlock<BasePotBlock> MARBLE_CAVE_POT = registerWithItem("marble_cave_pot", 2.0F, 0.002667F);
    public static final PortDeferredBlock<BasePotBlock> CRIMSON_POT = registerWithItem("crimson_pot", 1.6F, 0.00274F);
    public static final PortDeferredBlock<BasePotBlock> PYRAMID_POT = registerWithItem("pyramid_pot", 10.0F, 0.008F);
    public static final PortDeferredBlock<BasePotBlock> CORRUPTION_POT = registerWithItem("corruption_pot", 1.6F, 0.00274F);
    public static final PortDeferredBlock<BasePotBlock> DUNGEON_POT = registerWithItem("dungeon_pot", 1.9F, 0.002604F);
    public static final PortDeferredBlock<BasePotBlock> UNDERWORLD_POT = registerWithItem("underworld_pot", 2.1F, 0.00274F);
    public static final PortDeferredBlock<BasePotBlock> LIHZAHRD_POT = registerWithItem("lihzahrd_pot", 4.0F, 0.004F);

    public static PortDeferredBlock<BasePotBlock> registerWithItem(String id, float moneyRatio, float moneyHoleChance) {
        PortDeferredBlock<BasePotBlock> object = BLOCKS.register(id, () -> new BasePotBlock(moneyRatio, moneyHoleChance, Block.box(2, 0, 2, 14, 12, 14)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object);
        return object;
    }
}
