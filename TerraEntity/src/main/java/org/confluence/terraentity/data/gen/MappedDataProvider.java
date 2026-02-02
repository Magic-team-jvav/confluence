package org.confluence.terraentity.data.gen;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.format.OutputFormat;
import org.confluence.terraentity.registries.mappeddata.MappedData;
import org.confluence.terraentity.registries.mappeddata.MappedDataLoader;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MappedDataProvider extends AbstractExistCodecProvider<MappedData<?>> {

    public MappedDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    private ResourceLocation getLocation(ResourceLocation file) {
        return TerraEntity.space(MappedDataLoader.KEY).withSuffix("/" + file.getPath() + ".json");
    }

    @Override
    protected void run(HolderLookup.Provider provider) {
        MappedDataTypes.TYPES.getEntries().forEach(type -> {
            this.gen(getLocation(type.getId()), type.get().getDefaultValue());
        });
    }

    @Override
    protected Function<String, String> outputFormat(Path path) {
        return OutputFormat.COMPRESS_NUMBER_ARRAYS;
    }

    @Override
    protected List<String> createOrderedKeys() {
        return List.of("type", "comment", "data");
    }

    @Override
    protected Codec<MappedData<?>> getCodec() {
        return MappedData.CODEC;
    }

    @Override
    public @NotNull String getName() {
        return "MappedDataProvider";
    }

}
