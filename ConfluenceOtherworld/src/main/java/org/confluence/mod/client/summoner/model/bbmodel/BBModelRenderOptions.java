package org.confluence.mod.client.summoner.model.bbmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class BBModelRenderOptions {

    private final ResourceLocation modelId;
    private String animationName;
    private float ageTicks;
    private ResourceLocation texture;
    private RenderType renderType;
    private int packedLight = LightTexture.FULL_BRIGHT;
    private int packedOverlay = OverlayTexture.NO_OVERLAY;
    private int color = -1;
    private float alpha = 1;
    private float scale = 1;
    private float translateX;
    private float translateY;
    private float translateZ;
    private float rotX;
    private float rotY;
    private float rotZ;
    private final Set<String> hiddenBones = new HashSet<>();

    public BBModelRenderOptions(ResourceLocation modelId) {
        this.modelId = modelId;
    }

    public BBModelRenderOptions animation(String name, float ageTicks) {
        this.animationName = name;
        this.ageTicks = ageTicks;
        return this;
    }

    public BBModelRenderOptions bindPose() {
        this.animationName = null;
        this.ageTicks = 0;
        return this;
    }

    public BBModelRenderOptions texture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public BBModelRenderOptions renderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public BBModelRenderOptions light(int packedLight) {
        this.packedLight = packedLight;
        return this;
    }

    public BBModelRenderOptions overlay(int packedOverlay) {
        this.packedOverlay = packedOverlay;
        return this;
    }

    public BBModelRenderOptions color(int argb) {
        this.color = argb;
        return this;
    }

    public BBModelRenderOptions alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public BBModelRenderOptions scale(float scale) {
        this.scale = scale;
        return this;
    }

    public BBModelRenderOptions translate(float x, float y, float z) {
        this.translateX = x;
        this.translateY = y;
        this.translateZ = z;
        return this;
    }

    public BBModelRenderOptions rotate(float xDegrees, float yDegrees, float zDegrees) {
        this.rotX = xDegrees;
        this.rotY = yDegrees;
        this.rotZ = zDegrees;
        return this;
    }

    public BBModelRenderOptions hideBone(String... boneNames) {
        if (boneNames != null) {
            for (String boneName : boneNames) {
                hiddenBones.add(boneName);
            }
        }
        return this;
    }

    public boolean render(PoseStack poseStack, MultiBufferSource bufferSource) {
        return BBModelRenderer.render(this, poseStack, bufferSource);
    }

    ResourceLocation modelId() {
        return modelId;
    }

    @Nullable
    String animationName() {
        return animationName;
    }

    float ageTicks() {
        return ageTicks;
    }

    @Nullable
    ResourceLocation texture() {
        return texture;
    }

    @Nullable
    RenderType renderType() {
        return renderType;
    }

    int packedLight() {
        return packedLight;
    }

    int packedOverlay() {
        return packedOverlay;
    }

    int color() {
        return color;
    }

    float alpha() {
        return alpha;
    }

    float scale() {
        return scale;
    }

    float translateX() {
        return translateX;
    }

    float translateY() {
        return translateY;
    }

    float translateZ() {
        return translateZ;
    }

    float rotX() {
        return rotX;
    }

    float rotY() {
        return rotY;
    }

    float rotZ() {
        return rotZ;
    }

    Set<String> hiddenBones() {
        return hiddenBones;
    }
}
