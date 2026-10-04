package org.confluence.mod.common.init.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

///
public final class DevelopmentSpawnPolicy {
    private static final Set<ResourceLocation> DEVELOPMENT_ONLY = ConcurrentHashMap.newKeySet();

    private DevelopmentSpawnPolicy() {}

    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> developmentOnly(DeferredHolder<EntityType<?>, EntityType<T>> registration) {
        DEVELOPMENT_ONLY.add(registration.getId());
        return registration;
    }

    public static boolean allowsAutomaticSpawn(EntityType<?> type) {
        return Confluence.UNRELEASED_SPAWNS || !DEVELOPMENT_ONLY.contains(BuiltInRegistries.ENTITY_TYPE.getKey(type));
    }
}
