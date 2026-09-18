package org.confluence.mod.client.summoner.model;

import net.minecraftforge.registries.ForgeRegistries;
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

    public static JsonModelRenderOptions json(ModelResourceLocation model) {
        return new JsonModelRenderOptions(model);
    }

    public static JsonModelRenderOptions json(ResourceLocation modelId) {
        return new JsonModelRenderOptions(JsonModelRenderer.standaloneLocation(modelId));
    }

    public static ModelResourceLocation jsonLocation(ResourceLocation modelId) {
        return JsonModelRenderer.standaloneLocation(modelId);
    }

    public static GeoRenderOptions geo(ResourceLocation modelId) {
        return new GeoRenderOptions(modelId);
    }

    public static VirtualEntityRenderOptions virtualEntity(EntityType<?> entityType, float partialTick) {
        return new VirtualEntityRenderOptions(entityType, partialTick);
    }

    public static VirtualEntityRenderOptions virtualEntity(ResourceLocation entityTypeId, float partialTick) {
        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId);
        return virtualEntity(entityType, partialTick);
    }

    public static BBModelRenderOptions bbmodel(ResourceLocation modelId) {
        return new BBModelRenderOptions(modelId);
    }
}
