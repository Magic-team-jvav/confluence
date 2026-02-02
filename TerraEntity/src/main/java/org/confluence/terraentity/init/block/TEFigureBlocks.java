package org.confluence.terraentity.init.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.block.FigureBlock;
import org.confluence.terraentity.init.TEBlocks;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.function.Supplier;

import static org.confluence.terraentity.TerraEntity.MODID;

public class TEFigureBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);


    public static final DeferredBlock<FigureBlock> FIGURE = register("figure", TEMonsterEntities.NYMPH.getId());

    public static final DeferredBlock<FigureBlock> FIGURE2= register("figure2", TEMonsterEntities.BLOOD_ZOMBIE.getId());

    public static final DeferredBlock<FigureBlock> FIGURE3= register("figure3",  TEBossEntities.EYE_OF_CTHULHU.getId(),0.5f);


    public static final Supplier<BlockEntityType<FigureBlock.FigureBlockEntity>> FIGURE_BLOCK_ENTITY =
            TEBlocks.BLOCK_ENTITIES.register("figure_block_entity", () -> BlockEntityType.Builder.of(FigureBlock.FigureBlockEntity::new,
                    FIGURE.get(), FIGURE2.get(), FIGURE3.get()
            ).build(null));

    private static DeferredBlock<FigureBlock> register(String id, ResourceLocation entityId, float scale) {
        DeferredBlock<FigureBlock> object = BLOCKS.register(id, ()-> new FigureBlock(entityId, scale,  BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().destroyTime(1.5F)));
        TEBlocks.BLOCKITEMS.registerSimpleBlockItem(object);
        return object;
    }

    private static DeferredBlock<FigureBlock> register(String id, ResourceLocation entityId) {
        DeferredBlock<FigureBlock> object = BLOCKS.register(id, ()-> new FigureBlock(entityId, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().destroyTime(1.5F)));
        TEBlocks.BLOCKITEMS.registerSimpleBlockItem(object);
        return object;
    }


    public static void register(IEventBus bus) {
        TEFigureBlocks.BLOCKS.register(bus);

    }

}
