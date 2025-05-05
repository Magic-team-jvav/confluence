package com.xiaohunao.xhn_lib.api;

import com.google.gson.JsonElement;
import com.xiaohunao.xhn_lib.common.serialization.IDynamicSerializer;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleDynamicLoader<T> extends AbstractDynamicLoader<T> {
    private final IDynamicSerializer<T> serializer;

    public SimpleDynamicLoader(String folderName, Registry<T> registry, IDynamicSerializer<T> serializer) {
        super(folderName, registry);
        this.serializer = serializer;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Set<ResourceLocation> previousValues = new HashSet<>(loadedValues.keySet());
        loadedValues.clear();
        removedValues.clear();

        registry.unfreeze();
        try {
            Set<ResourceLocation> currentResourceLocations = new HashSet<>(resources.keySet());
            loadNewValues(resources, registry);
            removeObsoleteValues(previousValues, currentResourceLocations, (MappedRegistry<T>)registry);
            LOGGER.info("{} loading complete, currently has {} entries", registry.key().location(), loadedValues.size());
        } catch (Exception e) {
            LOGGER.error("Error loading {}", registry.key().location(), e);
        } finally {
            registry.freeze();
        }
    }


    public void loadNewValues(Map<ResourceLocation, JsonElement> resources, Registry<T> registry) {
        resources.forEach((resourceLocation, jsonElement) -> {
            try {
                T value = serializer.read(resourceLocation, jsonElement);
                if (value != null) {
                    Registry.register(registry, resourceLocation, value);
                    loadedValues.put(resourceLocation, value);
                    LOGGER.debug("Successfully loaded {}: {}", registry.key().location(), resourceLocation);
                }
            } catch (Exception e) {
                LOGGER.error("Error loading {}: {}", registry.key().location(), resourceLocation, e);
            }
        });
    }

    public void removeObsoleteValues(Set<ResourceLocation> previousValues, Set<ResourceLocation> currentResourceLocations, MappedRegistry<T> registry) {
        previousValues.stream()
                .filter(id -> !currentResourceLocations.contains(id))
                .forEach(id -> {
                    removedValues.add(id);
                    if (unregisterFromRegistry(registry, id)) {
                        LOGGER.info("Removed no longer existing {}: {}", registry.key().location(), id);
                    }
                });
    }
}
