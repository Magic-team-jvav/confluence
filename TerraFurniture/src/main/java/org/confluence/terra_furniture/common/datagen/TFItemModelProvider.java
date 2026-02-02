package org.confluence.terra_furniture.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.init.TFBlocks;

public class TFItemModelProvider extends ItemModelProvider {
    public TFItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TerraFurniture.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleBlockItem(TFBlocks.GLASS_KILN.get());
        simpleBlockItem(TFBlocks.LIVING_LOOM.get());
        TFDataGenerator.GENERATORS.forEach((block, blockDataGenerator) -> invokeGenerator(block, blockDataGenerator, this));
    }

    @SuppressWarnings("unchecked")
    private <T extends Block> void invokeGenerator(Block block, BlockDataGenerator<?> generator, ItemModelProvider provider) {
        BlockDataGenerator<T> typedGenerator = (BlockDataGenerator<T>) generator;
        typedGenerator.buildItemWithTemplate((T) block, provider, existingFileHelper);
    }
}
