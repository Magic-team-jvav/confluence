package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.confluence.lib.common.data.gen.CollectRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.recipe.*;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModStructures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataGenerator {
    private static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::createDamageTypes)
            .add(Registries.BIOME, ModBiomes::boostrap)
            .add(Registries.STRUCTURE, ModStructures::boostrap);

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        boolean client = event.includeClient();
        generator.addProvider(client, new ModChineseProvider(output));
        generator.addProvider(client, new ModEnglishProvider(output));
        generator.addProvider(client, new ModBlockStateProvider(output, helper));
        generator.addProvider(client, new ModItemModelProvider(output, helper));


        boolean server = event.includeServer();

        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, lookup, DATA_BUILDER, Set.of(Confluence.MODID));
        lookup = provider.getRegistryProvider();
        generator.addProvider(server, provider);

        ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(output, lookup, helper);
        generator.addProvider(server, blockTagsProvider);
        generator.addProvider(server, new ModItemTagsProvider(output, lookup, blockTagsProvider.contentsGetter(), helper));
        generator.addProvider(server, new ModDamageTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModPoiTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModBiomeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModEntityTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new CollectRecipeProvider(output, lookup,
                NPCShopProvider::new,
                ModRecipeProvider::new,
                CraftingRecipeProvider::new,
                HeavyWorkBenchProvider::new,
                CookingPotProvider::new,
                ShimmerTransmutationProvider::new
        ));
        generator.addProvider(server, new ModDataMapProvider(output, lookup));
        generator.addProvider(server, new ModLootTableProvider(output, lookup));
    }
}
