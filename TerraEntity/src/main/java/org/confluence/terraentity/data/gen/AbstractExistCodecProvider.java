package org.confluence.terraentity.data.gen;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.data.format.JsonSorter;
import org.confluence.terraentity.data.format.OutputFormat;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractExistCodecProvider<T> implements DataProvider {
    protected PackOutput output;
    private  final List<Pair<ResourceLocation, T>> jsons;
    private final List<CompletableFuture<?>> futures;
    protected final Gson gson;
    protected CompletableFuture<HolderLookup.Provider> lookupProvider;
    List<String> orderedKeys;

    public AbstractExistCodecProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.output = output;
        this.jsons = new ArrayList<>();
        this.futures = new ArrayList<>();
        this.lookupProvider = lookupProvider;
        this.gson = this.createGson();
        this.orderedKeys = createOrderedKeys();
    }

    protected Gson createGson(){
        return new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * 子类可重写此方法，指定key输出的顺序
     */
    protected List<String> createOrderedKeys(){
        return List.of();
    }

    protected JsonElement sortJson(JsonElement json, Path path){
        if(orderedKeys.isEmpty()){
            return json;
        }
        Map<String, JsonElement> map = json.getAsJsonObject().asMap();
        // 按指定顺序重新排列
        Map<String, JsonElement> orderedMap = JsonSorter.reorderMap(map, orderedKeys);
        return gson.toJsonTree(orderedMap);
    }


    /**
     * 子类可重写此方法，修正输出格式
     */
    protected Function<String, String> outputFormat(Path path) {
        return OutputFormat.ORIGINAL;
    }

    protected abstract void run(HolderLookup.Provider provider);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.lookupProvider.thenCompose(provider -> {
            this.run(provider);
            this.jsons.forEach(pair -> {
                this.futures.add(saveStable(cachedOutput, provider, getCodec(), pair.getB(), getPath(pair.getA())));
            });
            return CompletableFuture.allOf(this.futures.toArray(new CompletableFuture[0]));
        });
    }

    CompletableFuture<?> saveStable(CachedOutput output, HolderLookup.Provider registries, Codec<T> codec, T value, Path path) {
        RegistryOps<JsonElement> registryops = registries.createSerializationContext(JsonOps.INSTANCE);
        JsonElement jsonelement = codec.encodeStart(registryops, value).getOrThrow();
        return saveStable(output, jsonelement, path);
    }

    @SuppressWarnings("all")
    CompletableFuture<?> saveStable(CachedOutput output, JsonElement json, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha256(), bytearrayoutputstream);
                OutputStreamWriter outputstreamwriter = new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8);
                JsonElement sortedJson = sortJson(json, path);
                String str = this.gson.toJson(sortedJson);
                String formatted = outputFormat(path).apply(str);
                outputstreamwriter.write(formatted);
                outputstreamwriter.close();

                output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException var10) {
                LOGGER.error("Failed to save file to {}", path, var10);
            }

        }, Util.backgroundExecutor());
    }


    protected abstract Codec<T> getCodec();

    protected void gen(ResourceLocation location, T data){
        this.jsons.add(new Pair<>(location, data));
    }

    protected Path getPath(ResourceLocation loc) {
        return this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(loc.getNamespace()).resolve(loc.getPath() + ".json");
    }

//    protected JsonElement parseCodec(DataResult<?> result){
//        return JsonParser.parseString(gson.toJson(result.getOrThrow()));
//    }
//
//    protected JsonElement decode(T value, HolderLookup.Provider provider){
//        return getCodec().encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), value).getOrThrow();
//    }
}
