package org.confluence.terra_furniture.common.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.init.TFBlocks;

public class TFBlockStateProvider extends BlockStateProvider {
    private final ExistingFileHelper helper;

    public TFBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, TerraFurniture.MODID, helper);
        this.helper = helper;
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalDirectional(TFBlocks.GLASS_KILN.get());
        horizontalDirectional(TFBlocks.LIVING_LOOM.get());
        TFDataGenerator.GENERATORS.forEach((block, blockDataGenerator) -> invokeGenerator(block, blockDataGenerator, this));
    }

    private void horizontalDirectional(Block block) {
        ModelFile.ExistingModelFile modelFile = new ModelFile.ExistingModelFile(TerraFurniture.asResource("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath()), helper);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build());
    }

    @SuppressWarnings("unchecked")
    private <T extends Block> void invokeGenerator(Block block, BlockDataGenerator<?> generator, BlockStateProvider provider) {
        BlockDataGenerator<T> typedGenerator = (BlockDataGenerator<T>) generator;
        typedGenerator.buildBlockWithTemplate((T) block, provider, helper);
    }

}
