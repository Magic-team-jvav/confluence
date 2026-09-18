package org.confluence.mod.client.summoner.model;

import org.confluence.mod.client.summoner.model.bbmodel.BBModelRenderOptions;
import org.confluence.mod.client.summoner.model.geo.GeoRenderOptions;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderOptions;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderer;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityRenderOptions;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

/**
 * Unified entry point for all Lyra model renderers.
 *
 * <p>Each factory returns a module-specific options type, so a caller can only
 * use operations supported by that model format.</p>
 */
public final class LyraModelRenderer {

    private LyraModelRenderer() {
    }

    public static JsonModelRenderOptions json(ModelResourceLocation model) {
        return new JsonModelRenderOptions(Objects.requireNonNull(model, "model"));
    }

    public static JsonModelRenderOptions json(ResourceLocation modelId) {
        return new JsonModelRenderOptions(JsonModelRenderer.standaloneLocation(Objects.requireNonNull(modelId, "modelId")));
    }

    public static ModelResourceLocation jsonLocation(ResourceLocation modelId) {
        return JsonModelRenderer.standaloneLocation(Objects.requireNonNull(modelId, "modelId"));
    }

    public static GeoRenderOptions geo(ResourceLocation modelId) {
        return new GeoRenderOptions(Objects.requireNonNull(modelId, "modelId"));
    }

    public static VirtualEntityRenderOptions virtualEntity(EntityType<?> entityType, float partialTick) {
        return new VirtualEntityRenderOptions(Objects.requireNonNull(entityType, "entityType"), partialTick);
    }

    public static VirtualEntityRenderOptions virtualEntity(ResourceLocation entityTypeId, float partialTick) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(Objects.requireNonNull(entityTypeId, "entityTypeId"));
        return virtualEntity(Objects.requireNonNull(entityType, "Unknown entity type " + entityTypeId), partialTick);
    }

    public static BBModelRenderOptions bbmodel(ResourceLocation modelId) {
        return new BBModelRenderOptions(Objects.requireNonNull(modelId, "modelId"));
    }
}
