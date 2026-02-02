package org.confluence.terra_curio.common.init;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.block.WorkshopBlock;

public final class TCBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TerraCurio.MODID);

    public static final DeferredBlock<WorkshopBlock> WORKSHOP = BLOCKS.register("workshop", () -> new WorkshopBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE)));
}
