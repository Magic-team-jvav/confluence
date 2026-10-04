package org.confluence.mod.client.summoner.model.json;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;

/**
 * 以原版 JSON 模型形式注册的静态模型的渲染参数。
 *
 * <p>这里没有动画、骨骼与实体状态，只有模型位置、整体染色与光照。</p>
 */
public final class JsonModelRenderOptions {

    private final ModelResourceLocation model;
    private int color = -1;
    private int packedLight = LightTexture.FULL_BRIGHT;

    public JsonModelRenderOptions(ModelResourceLocation model) {
        this.model = model;
    }

    /** 整体染色（ARGB，{@code -1} = 不染色）。 */
    public JsonModelRenderOptions color(int argb) {
        this.color = argb;
        return this;
    }

    /** 打包光照（默认全亮）。 */
    public JsonModelRenderOptions light(int packedLight) {
        this.packedLight = packedLight;
        return this;
    }

    /** @return 模型存在并已提交顶点时 true；模型缺失时 false */
    public boolean render(PoseStack poseStack, MultiBufferSource bufferSource) {
        return JsonModelRenderer.render(model, poseStack, bufferSource, color, packedLight);
    }
}
