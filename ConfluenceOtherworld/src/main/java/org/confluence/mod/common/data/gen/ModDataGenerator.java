package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
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
import org.confluence.mod.common.data.gen.tag.*;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataGenerator {
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
        lookup = generator.addProvider(server, new DatapackBuiltinEntriesProvider(output, lookup, ModDataProvider.DATA_BUILDER, Set.of(Confluence.MODID))).getRegistryProvider();
        ModBlockTagsProvider blockTagsProvider = generator.addProvider(server, new ModBlockTagsProvider(output, lookup, helper));
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
                CookingPotRecipeProvider::new,
                ShimmerTransmutationRecipeProvider::new,
                ModAchievementOffsetProvider::new,
                StonecuttingRecipeProvider::new,
                SawmillRecipeProvider::new,
                HardmodeAnvilRecipeProvider::new
        ));
        generator.addProvider(server, new ModDataMapProvider(output, lookup));
        generator.addProvider(server, new ModLootTableProvider(output, lookup));
        generator.addProvider(server, new ModEnchantmentTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModRecipeSerializerTagsProvider(output, lookup, helper));
    }
}
