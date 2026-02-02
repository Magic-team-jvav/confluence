package org.confluence.terra_furniture.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TFBlockTagsProvider extends BlockTagsProvider {
    public TFBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TerraFurniture.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TFDataGenerator.GENERATORS.forEach((block, blockDataGenerator) -> invokeGenerator(block, blockDataGenerator, this));
        tag(TFTags.GLASS_FURNITURE)
                .add(TFBlocks.GLASS_CHAIR.get())
                .add(TFBlocks.GLASS_SOFA.get())
                .add(TFBlocks.GLASS_TOILET.get())
                .add(TFBlocks.GLASS_SINK.get())
                .add(TFBlocks.GLASS_DOOR.get())
                .add(TFBlocks.GLASS_CANDLE.get())
                .add(TFBlocks.GLASS_LANTERN.get())
                .add(TFBlocks.GLASS_LAMP.get())
                .add(TFBlocks.GLASS_CANDELABRAS.get())
                .add(TFBlocks.GLASS_CLOCK.get());
        tag(TFTags.DUNGEON_FURNITURE)
                .add(TFBlocks.BLUE_DUNGEON_SOFA.get())
                .add(TFBlocks.BLUE_DUNGEON_TOILET.get())
                .add(TFBlocks.BLUE_DUNGEON_SINK.get())
                .add(TFBlocks.BLUE_DUNGEON_DOOR.get())
                .add(TFBlocks.BLUE_DUNGEON_CANDLE.get())
                .add(TFBlocks.BLUE_DUNGEON_LANTERN.get())
                .add(TFBlocks.BLUE_DUNGEON_LAMP.get())
                .add(TFBlocks.BLUE_DUNGEON_CANDELABRAS.get())
                .add(TFBlocks.BLUE_DUNGEON_CLOCK.get());
        tag(TFTags.WOODEN_FURNITURE)
                .add(TFBlocks.WOODEN_TABLE.get())
                .add(TFBlocks.WOODEN_CHAIR.get());
        tag(TFTags.SINKS)
                .add(TFBlocks.BLUE_DUNGEON_SINK.get())
                .add(TFBlocks.GLASS_SINK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(TFBlocks.PLASTIC_CHAIR.get())
                .add(TFBlocks.TRASH_CAN.get())
                .addTag(TFTags.GLASS_FURNITURE)
                .addTag(TFTags.DUNGEON_FURNITURE);

        tag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(TFTags.WOODEN_FURNITURE);

        // 2025/11/2-19:07 TODO: Using inside sheets.
        tag(TFTags.HOUSE_CHAIR).add(
                TFBlocks.GLASS_CHAIR.get(),
                TFBlocks.BLUE_DUNGEON_CHAIR.get(),
                TFBlocks.WOODEN_CHAIR.get(),
                TFBlocks.GLASS_SOFA.get(),
                TFBlocks.BLUE_DUNGEON_SOFA.get(),
                TFBlocks.PLASTIC_CHAIR.get());

    }

    @SuppressWarnings("unchecked")
    private <T extends Block> void invokeGenerator(Block block, BlockDataGenerator<?> generator, BlockTagsProvider provider) {
        BlockDataGenerator<T> typedGenerator = (BlockDataGenerator<T>) generator;
        typedGenerator.getRegBlockTags((T) block, provider).forEach(tagKey -> tag(tagKey).add(block));
    }
}
