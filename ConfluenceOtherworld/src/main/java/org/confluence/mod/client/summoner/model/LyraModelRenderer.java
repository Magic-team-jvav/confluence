package org.confluence.mod.client.summoner.model;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderOptions;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderer;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityRenderOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * Unified entry point for all Lyra model renderers.
 *
 * <p>Each factory returns a module-specific options type, so a caller can only
 * use operations supported by that model format.</p>
 */
public final class LyraModelRenderer {

    /** 已有烘焙结果查询键的 JSON 模型。 */
    public static JsonModelRenderOptions json(ModelResourceLocation model) {
        return new JsonModelRenderOptions(model);
    }

    /** 按模型 id 使用 JSON 模型（{@code a/b/c} → {@code lyra_model/json/a/b/c/c}）。 */
    public static JsonModelRenderOptions json(ResourceLocation modelId) {
        return new JsonModelRenderOptions(JsonModelRenderer.standaloneLocation(modelId));
    }

    /**
     * 模型 id → 烘焙结果查询键（JSON 模型注册/查询共用）。
     *
     * <p><b>注册注意</b>：Forge 1.20.1 的 {@code ModelEvent.RegisterAdditional} 走的是
     * {@code ModelBakery} 补丁，把额外模型按<b>普通 {@link ResourceLocation}</b> 存入顶层模型表，
     * 因此注册请用 {@link JsonModelRenderer#resourcePath(ResourceLocation)}（即
     * {@code lyra_model/json/<path>/<fileName>}），查询时由
     * {@link JsonModelRenderer} 内部兼容两种键。</p>
     */
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
