package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.recipe.ConfluenceWorkshopProvider;
import org.confluence.mod.common.data.gen.recipe.HeavyWorkBenchRecipeProvider;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        boolean client = event.includeClient();
        generator.addProvider(client, new ModChineseProvider(output));
        generator.addProvider(client, new ModEnglishProvider(output));
        generator.addProvider(client, new ModBlockStateProvider(output, helper));
        generator.addProvider(client, new ModItemModelProvider(output, helper));

        boolean server = event.includeServer();
        ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(output, lookup, helper);
        generator.addProvider(server, blockTagsProvider);
        generator.addProvider(server, new ModItemTagsProvider(output, lookup, blockTagsProvider.contentsGetter(), helper));
        generator.addProvider(server, new ModLootTableProvider(output, lookup));
        generator.addProvider(server, new ModDamageTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModPoiTypeTagsProvider(output, lookup, helper));

        generator.addProvider(server, new ConfluenceWorkshopProvider(output));
        generator.addProvider(server, new HeavyWorkBenchRecipeProvider(output));
    }
}