package org.confluence.terraentity.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.confluence.lib.common.data.gen.CollectRecipeProvider;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.gen.loot.LinkageTCLootModifyProvider;
import org.confluence.terraentity.data.gen.loot.TELootModifyProvider;
import org.confluence.terraentity.data.gen.loot.TELootTableProvider;
import org.confluence.terraentity.data.gen.npc.NPCChatProvider;
import org.confluence.terraentity.data.gen.npc.NPCMoodProvider;
import org.confluence.terraentity.data.gen.npc.NPCNameProvider;
import org.confluence.terraentity.data.gen.npc.TENPCShopProvider;
import org.confluence.terraentity.data.gen.recipe.TERecipeProvider;
import org.confluence.terraentity.data.gen.tags.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.confluence.terraentity.TerraEntity.MODID;

@EventBusSubscriber(modid = MODID)
public class TEDataGenerator {
    public static Map<String, DataProvider> PROVIDERS = null;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, lookup, TERegisterDataPack.DATA_BUILDER, Set.of("minecraft", MODID));
        lookup = provider.getRegistryProvider();

        boolean server = event.includeServer();

        generator.addProvider(server, provider);
        generator.addProvider(server, new TELootModifyProvider(output, lookup, TerraEntity.MODID));
        generator.addProvider(server, new LinkageTCLootModifyProvider(output, lookup));

        generator.addProvider(server, new TEEntityTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new TEDamageTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new TEBiomeTagsProvider(output, lookup, helper));

        TEBlockTagsProvider blockTagsProvider = new TEBlockTagsProvider(output, lookup, helper);
        generator.addProvider(server, blockTagsProvider);
        generator.addProvider(server, new TEItemTagsProvider(output, lookup, blockTagsProvider.contentsGetter(), helper));
        generator.addProvider(server, new TEEnchantmentTagsProvider(output, lookup, helper));
        generator.addProvider(server, TELootTableProvider.getProvider(output, lookup));
        generator.addProvider(server, new CollectRecipeProvider("terra_entity:server", output, lookup, TERecipeProvider::new));
        generator.addProvider(server, new TENPCShopProvider(output, lookup));
        generator.addProvider(server, new NPCNameProvider(output, lookup));
        generator.addProvider(server, new NPCMoodProvider(output, lookup));
        generator.addProvider(server, new NPCChatProvider(output, lookup));
        generator.addProvider(server, new TEDataMapProvider(output, lookup));
        generator.addProvider(server, new MappedDataProvider(output, lookup));


//        generator.addProvider(server, new TENPCShopModifierProvider(output, lookup));  // 用来测试，发布时应该注释掉

        boolean client = event.includeClient();
        generator.addProvider(client, new TEChineseProvider(output));
        generator.addProvider(client, new TEEnglishProvider(output));
        generator.addProvider(client, new TEItemModelProvider(output, helper));


        PROVIDERS = generator.getProvidersView();

    }
}