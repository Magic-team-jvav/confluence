package org.confluence.mod.client.summoner.model;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderOptions;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderer;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityRenderOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

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
        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId);
        return virtualEntity(entityType, partialTick);
    }
}
