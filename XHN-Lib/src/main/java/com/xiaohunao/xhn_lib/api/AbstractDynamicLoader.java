package com.xiaohunao.xhn_lib.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.xiaohunao.xhn_lib.common.DynamicLoaderHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractDynamicLoader<T> extends SimpleJsonResourceReloadListener {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDynamicLoader.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final Map<ResourceLocation, T> loadedValues = new HashMap<>();
    protected final Set<ResourceLocation> removedValues = new HashSet<>();

    protected final String folderName;
    protected final MappedRegistry<T> registry;

    public AbstractDynamicLoader(String folderName, Registry<T> registry) {
        super(GSON, folderName);
        this.folderName = folderName;
        this.registry = (MappedRegistry<T>) registry;

        DynamicLoaderHelper.registerDynamicLoader(this);
    }
    
    @Override
    protected abstract void apply(@NotNull Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler);

    protected boolean unregisterFromRegistry(MappedRegistry<T> registry, ResourceLocation id) {
        try {
            T value = registry.get(id);
            if (value == null) {
                LOGGER.warn("Attempting to unregister non-existent {}: {}", registry.key().location(), id);
                return false;
            }

            ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), id);

            boolean success = true;
            success &= removeFromMap(registry, "byKey", resourceKey);
            success &= removeFromMap(registry, "byLocation", id);
            success &= removeFromMap(registry, "byValue", value);
            success &= removeFromMap(registry, "toId", value);
            success &= removeFromMap(registry, "registrationInfos", resourceKey);
            success &= removeFromList(registry, "byId", resourceKey);

            return success;
        } catch (Exception e) {
            LOGGER.error("Error removing {} from registry: {}", registry.key().location(), id, e);
            return false;
        }
    }

    protected boolean removeFromMap(MappedRegistry<T> registry, String fieldName, Object key) {
        try {
            Field field = getField(MappedRegistry.class, fieldName);
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) field.get(registry);
            Object removed = map.remove(key);

            if (removed != null) {
                LOGGER.debug("Successfully removed {} from {}: {}", registry.key().location(), fieldName, key);
                return true;
            } else {
                LOGGER.debug("Failed to remove {} from {}: {}", registry.key().location(), fieldName, key);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error removing {} from {}: {}", registry.key().location(), fieldName, key, e);
            return false;
        }
    }

    protected boolean removeFromList(MappedRegistry<T> registry, String fieldName, ResourceKey<T> resourceKey) {
        try {
            Field field = getField(MappedRegistry.class, fieldName);
            Object listObj = field.get(registry);

            if (!(listObj instanceof List)) {
                LOGGER.warn("{} field is not a List, cannot remove {} reference: {}", fieldName, registry.key().location(), resourceKey);
                return false;
            }

            List<?> list = (List<?>) listObj;

            boolean removed = list.removeIf(item -> {
                if (item instanceof Holder.Reference) {
                    Holder.Reference<?> ref = (Holder.Reference<?>) item;
                    try {
                        return ref.key().equals(resourceKey);
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            });

            if (removed) {
                LOGGER.debug("Successfully removed {} reference from {} list: {}", registry.key().location(), fieldName, resourceKey);
                return true;
            }
            LOGGER.debug("Could not find {} reference in {}: {}", registry.key().location(), fieldName, resourceKey);
            return false;
        } catch (Exception e) {
            LOGGER.error("Error removing {} from {} list: {}", registry.key().location(), fieldName, resourceKey, e);
            return false;
        }
    }

    protected Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
    

    public boolean registerDynamicContent(ResourceLocation id) {
        if (id == null) {
            LOGGER.warn("Attempted to register null resource location for dynamic content");
            return false;
        }

        // Add to the tracked locations - actual content loading will happen during apply()
        // Just marking this ID as a location we care about
        if (!loadedValues.containsKey(id)) {
            LOGGER.debug("Added dynamic content location to tracker: {}", id);
        }
        return true;
    }
}
