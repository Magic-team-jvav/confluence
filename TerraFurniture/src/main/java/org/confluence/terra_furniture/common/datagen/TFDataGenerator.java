package org.confluence.terra_furniture.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.datagen.empowered.AutoGenBlockData;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.init.TFBlocks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = TerraFurniture.MODID)
public final class TFDataGenerator {
    public static final Map<Block, BlockDataGenerator<?>> GENERATORS = new HashMap<>();
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        TFBlocks.BLOCKS.getEntries().forEach(holder -> {
            if (holder.get() instanceof AutoGenBlockData<?> block && block.getGenerator() != null) {
                GENERATORS.put(holder.get(), block.getGenerator());
            }
        });

        boolean client = event.includeClient();
        generator.addProvider(client, new TFChineseProvider(output));
        generator.addProvider(client, new TFEnglishProvider(output));
        generator.addProvider(client, new TFBlockStateProvider(output, helper));
        generator.addProvider(client, new TFItemModelProvider(output, helper));


        boolean server = event.includeServer();
        TFBlockTagsProvider blockTagsProvider = new TFBlockTagsProvider(output, lookup, helper);
        generator.addProvider(server, blockTagsProvider);
        generator.addProvider(server, new TFItemTagsProvider(output, lookup, blockTagsProvider.contentsGetter(), helper));
        generator.addProvider(server, new TFLootTableProvider(output, lookup));
        generator.addProvider(server, new TFRecipeProvider(output, lookup));
    }
}
