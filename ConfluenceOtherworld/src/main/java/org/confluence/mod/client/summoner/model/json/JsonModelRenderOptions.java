package org.confluence.mod.client.summoner.model.json;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;

/**
 * Render options for a static model registered as a vanilla JSON model.
 * Animation, bones and entity state are intentionally unavailable here.
 */
public final class JsonModelRenderOptions {

    private final ModelResourceLocation model;
    private int color = -1;
    private int packedLight = LightTexture.FULL_BRIGHT;

    public JsonModelRenderOptions(ModelResourceLocation model) {
        this.model = model;
    }

    /** Overall ARGB tint, -1 keeps the model colors unchanged. */
    public JsonModelRenderOptions color(int argb) {
        this.color = argb;
        return this;
    }

    public JsonModelRenderOptions light(int packedLight) {
        this.packedLight = packedLight;
        return this;
    }

    public boolean render(PoseStack poseStack, MultiBufferSource bufferSource) {
        return JsonModelRenderer.render(model, poseStack, bufferSource, color, packedLight);
    }
}
