package org.confluence.mod.common.init.block;

import net.minecraft.world.level.block.Block;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BasePotBlock;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.registries.PortBlockRegistration;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.IntSupplier;

public class PotBlocks {
    public static void init() {}

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);

    public static final PortDeferredBlock<BasePotBlock> FOREST_POT = registerWithItem("forest_pot", 1.0F, 500);
    public static final PortDeferredBlock<BasePotBlock> TUNDRA_POT = registerWithItem("tundra_pot", 1.25F, 461);
    public static final PortDeferredBlock<BasePotBlock> OCEAN_POT = registerWithItem("ocean_pot", 1.25F, 0);
    public static final PortDeferredBlock<BasePotBlock> SPIDER_NEST_POT = registerWithItem("spider_nest_pot", 3.5F, 272);
    public static final PortDeferredBlock<BasePotBlock> UNDERGROUND_DESERT_POT = registerWithItem("underground_desert_pot", 1.25F, 461);
    public static final PortDeferredBlock<BasePotBlock> JUNGLE_POT = registerWithItem("jungle_pot", 1.75F, 400);
    public static final PortDeferredBlock<BasePotBlock> MARBLE_CAVE_POT = registerWithItem("marble_cave_pot", 2.0F, 375);
    public static final PortDeferredBlock<BasePotBlock> CRIMSON_POT = registerWithItem("crimson_pot", 1.6F, 365);
    public static final PortDeferredBlock<BasePotBlock> PYRAMID_POT = registerWithItem("pyramid_pot", 10.0F, 125);
    public static final PortDeferredBlock<BasePotBlock> CORRUPTION_POT = registerWithItem("corruption_pot", 1.6F, 365);
    public static final PortDeferredBlock<BasePotBlock> DUNGEON_POT = registerWithItem("dungeon_pot", 1.9F, 384);
    public static final PortDeferredBlock<BasePotBlock> UNDERWORLD_POT = registerWithItem("underworld_pot", 2.1F, 365);
    public static final PortDeferredBlock<BasePotBlock> LIHZAHRD_POT = registerWithItem("lihzahrd_pot", 4.0F, () -> KillBoard.INSTANCE.getGamePhase().isHardmode() ? 250 : 500);

    public static PortDeferredBlock<BasePotBlock> registerWithItem(String id, float moneyRatio, IntSupplier invMoneyHoleChance) {
        PortDeferredBlock<BasePotBlock> object = BLOCKS.register(id, () -> new BasePotBlock(moneyRatio, invMoneyHoleChance, Block.box(2, 0, 2, 14, 12, 14)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object);
        return object;
    }

    public static PortDeferredBlock<BasePotBlock> registerWithItem(String id, float moneyRatio, int invMoneyHoleChance) {
        return registerWithItem(id, moneyRatio, () -> invMoneyHoleChance);
    }
}
