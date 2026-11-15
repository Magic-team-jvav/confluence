package org.confluence.mod.client.summoner.model;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderOptions;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderer;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityRenderOptions;

/// Lyra 模型渲染入口，按 JSON 模型和虚拟生物模型选择对应参数。
public final class LyraModelRenderer {

    public static JsonModelRenderOptions json(ModelResourceLocation model) {
        return new JsonModelRenderOptions(model);
    }

    public static JsonModelRenderOptions json(ResourceLocation modelId) {
        return new JsonModelRenderOptions(JsonModelRenderer.standaloneLocation(modelId));
    }

    public static ModelResourceLocation jsonLocation(ResourceLocation modelId) {
        return JsonModelRenderer.standaloneLocation(modelId);
    }

    public static VirtualEntityRenderOptions virtualEntity(EntityType<?> entityType, float partialTick) {
        return new VirtualEntityRenderOptions(entityType, partialTick);
    }

    public static VirtualEntityRenderOptions virtualEntity(ResourceLocation entityTypeId, float partialTick) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
        return virtualEntity(entityType, partialTick);
    }
}
