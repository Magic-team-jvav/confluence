package org.confluence.mod.common.init.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.util.LibUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// 开发中生物在注册处声明自动生成限制；不影响注册、刷怪蛋和手动召唤。
public final class DevelopmentSpawnPolicy {
    private static final Set<ResourceLocation> DEVELOPMENT_ONLY = ConcurrentHashMap.newKeySet();

    private DevelopmentSpawnPolicy() {}

    public static <T extends Entity> RegistryObject<EntityType<T>> developmentOnly(RegistryObject<EntityType<T>> registration) {
        DEVELOPMENT_ONLY.add(registration.getId());
        return registration;
    }

    public static boolean allowsAutomaticSpawn(EntityType<?> type) {
        return LibUtils.isDev() || !DEVELOPMENT_ONLY.contains(BuiltInRegistries.ENTITY_TYPE.getKey(type));
    }
}
