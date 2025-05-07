package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.confluence.mod.common.data.gen.data_map.ValueSubProvider;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.mixin.accessor.DataMapProviderAccessor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        Map<DataMapType<?, ?>, Builder<?, ?>> builders = ((DataMapProviderAccessor) this).getBuilders();
        ValueSubProvider.gather(() -> (ValueSubProvider.Builder) builders.computeIfAbsent(ModDataMaps.VALUE, ValueSubProvider.Builder::new));
    }

    @FunctionalInterface
    public interface Appender<T extends DataMapProvider.Builder<?, ?>> {
        T create();
    }
}
