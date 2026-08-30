package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.common.data.gen.CollectRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.recipe.*;
import org.confluence.mod.common.data.gen.tag.*;
import org.confluence.mod.common.init.ModTags;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Confluence.MODID)
public final class ModDataGenerator {
    public static final Ingredient INGOTS_IRON_AND_LEAD = fromTags(Tags.Items.INGOTS_IRON, LibTags.Items.INGOTS_LEAD);
    public static final Ingredient INGOTS_GOLD_AND_PLATINUM = fromTags(Tags.Items.INGOTS_GOLD, LibTags.Items.INGOTS_PLATINUM);
    public static final Ingredient INGOTS_EVIL = fromTags(ModTags.Items.INGOTS_CRIMTANE, ModTags.Items.INGOTS_DEMONITE);

    @SafeVarargs
    private static Ingredient fromTags(TagKey<Item>... tags) {
        return Ingredient.fromValues(Arrays.stream(tags).map(Ingredient.TagValue::new));
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        boolean server = event.includeServer();
        lookup = generator.addProvider(server, new DatapackBuiltinEntriesProvider(output, lookup, ModDataProvider.DATA_BUILDER, Set.of(Confluence.MODID))).getRegistryProvider();

        boolean client = event.includeClient();
        generator.addProvider(client, new ModChineseProvider(output));
        generator.addProvider(client, new ModEnglishProvider(output, lookup));
        generator.addProvider(client, new ModEnUdProvider(output, lookup));
        generator.addProvider(client, new ModBlockStateProvider(output, helper));
        generator.addProvider(client, new ModItemModelProvider(output, helper));
        generator.addProvider(client, new CollectRecipeProvider(Confluence.asPlainId("client"), output, lookup,
                ModClientBestiaryEntryProvider::new,
                ModAchievementOffsetProvider::client
        ));

        ModBlockTagsProvider blockTagsProvider = generator.addProvider(server, new ModBlockTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModItemTagsProvider(output, lookup, blockTagsProvider.contentsGetter(), helper));
        generator.addProvider(server, new ModDamageTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModPoiTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModBiomeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModEntityTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new CollectRecipeProvider(Confluence.asPlainId("server"), output, lookup,
                NPCShopProvider::new,
                ModRecipeProvider::new,
                CraftingRecipeProvider::new,
                HeavyWorkBenchProvider::new,
                CookingPotRecipeProvider::new,
                ShimmerTransmutationRecipeProvider::new,
                ModAchievementOffsetProvider::server,
                StonecuttingRecipeProvider::new,
                SawmillRecipeProvider::new,
                HardmodeAnvilRecipeProvider::new
        ));
        generator.addProvider(server, new ModDataMapProvider(output, lookup));
        generator.addProvider(server, new ModLootTableProvider(output, lookup));
        generator.addProvider(server, new ModEnchantmentTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModRecipeSerializerTagsProvider(output, lookup, helper));
        generator.addProvider(server, new EMILootDirectDropsProvider(output, lookup));
        generator.addProvider(server, new EMILootExcludedSyntheticLootModifierLootTablesProvider(output, lookup));
        generator.addProvider(server, new ModLootModifiersProvider(output, lookup));
    }
}
