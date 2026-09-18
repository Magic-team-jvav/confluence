package org.confluence.mod.client.summoner.model.bbmodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.*;

public final class BBModelManager extends SimplePreparableReloadListener<BBModelManager.PreparedModels> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "lyra_model/bbmodel";

    public static final BBModelManager INSTANCE = new BBModelManager();

    private Map<ResourceLocation, BBModelAsset> models = Map.of();
    private Map<ResourceLocation, byte[]> embeddedTextures = Map.of();
    private final Set<ResourceLocation> registeredTextures = new HashSet<>();

    private BBModelManager() {
    }

    @Nullable
    BBModelAsset getModel(ResourceLocation modelId) {
        return models.get(modelId);
    }

    @Nullable
    ResourceLocation resolveTexture(@Nullable ResourceLocation texture) {
        if (texture == null || !embeddedTextures.containsKey(texture) || registeredTextures.contains(texture)) {
            return texture;
        }
        byte[] data = embeddedTextures.get(texture);
        try {
            DynamicTexture dynamicTexture = new DynamicTexture(NativeImage.read(data));
            Minecraft.getInstance().getTextureManager().register(texture, dynamicTexture);
            registeredTextures.add(texture);
            return texture;
        } catch (Exception exception) {
            registeredTextures.add(texture);
            LOGGER.warn("Failed to register embedded BBModel texture {}", texture, exception);
            return texture;
        }
    }

    @Override
    protected PreparedModels prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> resources = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry :
                resourceManager.listResources(DIRECTORY, location -> location.getPath().endsWith(".bbmodel")).entrySet()) {
            ResourceLocation fileId = entry.getKey();
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                if (json != null && json.isJsonObject()) {
                    resources.put(fileId, json);
                }
            } catch (Exception exception) {
                LOGGER.warn("Failed to read BBModel resource {}", fileId, exception);
            }
        }

        Map<ResourceLocation, BBModelAsset> parsedModels = new HashMap<>();
        Map<ResourceLocation, byte[]> parsedEmbeddedTextures = new HashMap<>();
        List<Map.Entry<ResourceLocation, JsonElement>> entries = new ArrayList<>(resources.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));
        for (Map.Entry<ResourceLocation, JsonElement> entry : entries) {
            ResourceLocation modelId = modelId(entry.getKey());
            try {
                BBModelLoader.ParsedModel parsed = BBModelLoader.parse(
                        modelId,
                        entry.getValue().getAsJsonObject(),
                        resourceManager
                );
                parsedModels.put(modelId, new BBModelAsset(parsed.model(), parsed.animations()));
                parsedEmbeddedTextures.putAll(parsed.embeddedTextures());
            } catch (Exception exception) {
                LOGGER.warn("Failed to load BBModel {}", modelId, exception);
            }
        }
        return new PreparedModels(parsedModels, parsedEmbeddedTextures);
    }

    @Override
    protected void apply(PreparedModels prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        releaseStaleTextures(prepared.embeddedTextures().keySet());
        this.models = prepared.models();
        this.embeddedTextures = prepared.embeddedTextures();
        this.registeredTextures.removeAll(prepared.embeddedTextures().keySet());
        LOGGER.info("Loaded {} BBModels", prepared.models().size());
    }

    private void releaseStaleTextures(Set<ResourceLocation> currentTextures) {
        for (ResourceLocation texture : List.copyOf(registeredTextures)) {
            if (!currentTextures.contains(texture)) {
                Minecraft.getInstance().getTextureManager().release(texture);
                registeredTextures.remove(texture);
            }
        }
    }

    private static ResourceLocation modelId(ResourceLocation fileId) {
        String path = fileId.getPath();
        if (path.startsWith(DIRECTORY + "/")) {
            path = path.substring(DIRECTORY.length() + 1);
        }
        if (path.endsWith(".bbmodel")) {
            path = path.substring(0, path.length() - ".bbmodel".length());
        }
        return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), path);
    }

    static final class BBModelAsset {

        private final BBModelModel model;
        private final Map<String, BBModelClip> animations;

        private BBModelAsset(BBModelModel model, Map<String, BBModelClip> animations) {
            this.model = model;
            this.animations = Map.copyOf(animations);
        }

        BBModelModel model() {
            return model;
        }

        @Nullable
        BBModelClip clip(String animationName) {
            return animations.get(animationName);
        }
    }

    record PreparedModels(
            Map<ResourceLocation, BBModelAsset> models,
            Map<ResourceLocation, byte[]> embeddedTextures
    ) {
    }
}
