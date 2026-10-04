package org.confluence.mod.client.summoner.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderer;
import org.confluence.mod.common.summoner.projectile.Zenith;

/**
 * 天顶剑飞剑的 JSON 模型注册。
 * <p>
 * 21 个剑型各自对应 {@code assets/confluence/lyra_model/json/projectile/zenith/<name>/<name>.json}，
 * 由 {@code processResources} 镜像到 {@code models/lyra_model/json/...} 后，这里把它们作为
 * 额外模型交给原版模型烘焙器。
 * </p>
 * <p>
 * 注意：Forge 1.20.1 的 {@code ModelEvent.RegisterAdditional} 以<b>普通 {@link ResourceLocation}</b>
 * 作为键（Forge 对 {@code ModelBakery} 的补丁使用 {@code put(rl, u)}），因此必须注册
 * {@link JsonModelRenderer#resourcePath(ResourceLocation)} 而不是 {@code ModelResourceLocation}，
 * 否则会静默解析到 missing model。
 * </p>
 */
public final class ZenithSwordModels {

    private ZenithSwordModels() {
    }

    /**
     * 单个剑型的模型 id（{@code projectile/zenith/<name>}）。
     * <p>
     * <b>不是</b> {@link Zenith.RenderType#getTexture()}：后者已是完整的
     * {@code lyra_model/json/...} 资源路径，再交给
     * {@link JsonModelRenderer#resourcePath(ResourceLocation)} 会被重复拼接前缀。
     * </p>
     */
    private static ResourceLocation modelId(Zenith.RenderType type) {
        return Confluence.asResource("projectile/zenith/" + type.textureName());
    }

    /** 把 21 个剑型全部登记为额外模型。 */
    public static void register(ModelEvent.RegisterAdditional event) {
        for (Zenith.RenderType type : Zenith.RenderType.values()) {
            event.register(JsonModelRenderer.resourcePath(modelId(type)));
        }
    }
}
