package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.data.gen.data_map.*;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.mixin.accessor.DataMapProviderAccessor;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        ValueSubProvider.gather(() -> (ValueSubProvider.Builder) ((DataMapProviderAccessor) this).getBuilders().computeIfAbsent(ModDataMaps.VALUE, ValueSubProvider.Builder::new));
        TreasureBagSubProvider.gather(() -> builder(ModDataMaps.TREASURE_BAG));
        ExtractinatorSubProvider.gather(() -> builder(ModDataMaps.EXTRACTINATOR));
        ChlorophyteExtractinatorSubProvider.gather(() -> builder(ModDataMaps.CHLOROPHYTE_EXTRACTINATOR));
        ImmunitySubProvider.gather(() -> builder(ModDataMaps.IMMUNITY));
    }

    @FunctionalInterface
    public interface Appender<T extends DataMapProvider.Builder<?, ?>> {
        T create();
    }
}
