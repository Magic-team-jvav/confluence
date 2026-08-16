package org.confluence.mod.common.data.gen;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.confluence.lib.common.data.gen.CollectRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.recipe.*;
import org.confluence.mod.common.data.gen.tag.*;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = Confluence.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        boolean server = event.includeServer();
        lookup = generator.addProvider(server, new DatapackBuiltinEntriesProvider(output, lookup, ModDataProvider.DATA_BUILDER, Set.of(Confluence.MODID))).getRegistryProvider();
        lookup = lookup.thenApply(provider -> new HolderLookup.Provider() {
            @Override
            public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
                return provider.lookup(registryKey).map(LookupWrapper::new);
            }
        });
        CompletableFuture<HolderLookup.Provider> registries = lookup;

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
        generator.addProvider(server, new ModWorldPresetTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModEntityTypeTagsProvider(output, lookup, helper));
        generator.addProvider(server, new ModFluidTagsProvider(output, lookup, helper));
        generator.addProvider(server, new NPCShopProvider(output));
        generator.addProvider(server, new NPCMoodProvider(output));
        generator.addProvider(server, new NPCChatProvider(output));
        generator.addProvider(server, new CollectRecipeProvider(Confluence.asPlainId("server"), output, lookup,
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
        generator.addProvider(server, new ModLootTableProvider(output));
        generator.addProvider(server, new ModRecipeSerializerTagsProvider(output, lookup, helper));
        generator.addProvider(server, new EMILootDirectDropsProvider(output, lookup));
        generator.addProvider(server, new EMILootExcludedSyntheticLootModifierLootTablesProvider(output, lookup));
        generator.addProvider(server, new ModLootModifiersProvider(output));
    }

    private record LookupWrapper<T>(
            RegistryLookup<T> wrapped) implements HolderLookup.RegistryLookup<T> {

        @Override
        public ResourceKey<? extends Registry<? extends T>> key() {
            return wrapped.key();
        }

        @Override
        public Lifecycle registryLifecycle() {
            return wrapped.registryLifecycle();
        }

        @Override
        public Stream<Holder.Reference<T>> listElements() {
            return wrapped.listElements();
        }

        @Override
        public Stream<HolderSet.Named<T>> listTags() {
            return wrapped.listTags();
        }

        @Override
        public Optional<Holder.Reference<T>> get(ResourceKey<T> resourceKey) {
            return wrapped.get(resourceKey);
        }

        @Override
        public Optional<HolderSet.Named<T>> get(TagKey<T> tagKey) {
            return Optional.of(HolderSet.emptyNamed(wrapped, tagKey));
        }

        // by coremod from portlib
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LookupWrapper<?> wrapper && wrapper.wrapped.equals(wrapped)) {
                return true;
            }
            return wrapped.equals(obj) || obj.equals(this);
        }
    }
}
