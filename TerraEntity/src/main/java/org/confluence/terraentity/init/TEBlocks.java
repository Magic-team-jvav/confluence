package org.confluence.terraentity.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.block.TEFigureBlocks;

import static org.confluence.terraentity.TerraEntity.MODID;

public class TEBlocks {
    public static final DeferredRegister.Items BLOCKITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TerraEntity.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);


    public static void register(IEventBus bus) {
        TEFigureBlocks.register(bus);

        TEBlocks.BLOCKITEMS.register(bus);
        TEBlocks.BLOCKS.register(bus);
        TEBlocks.BLOCK_ENTITIES.register(bus);
    }
}
