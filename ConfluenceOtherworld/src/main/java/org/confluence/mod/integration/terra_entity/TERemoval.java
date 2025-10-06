package org.confluence.mod.integration.terra_entity;

import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.confluence.terraentity.TerraEntity;

import java.util.Map;

public final class TERemoval {
    // 替换配方
    public static void processRecipes(Map<ResourceLocation, JsonElement> objectMap) {
        objectMap.entrySet().removeIf(entry -> TerraEntity.MODID.equals(entry.getKey().getNamespace()));
    }

    // 重定向战利品表
    public static void redirectLootTable() {
        for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (TerraEntity.MODID.equals(id.getNamespace())) {
                entry.getValue().lootTable = Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/" + id.getPath());
            }
        }
    }
}
