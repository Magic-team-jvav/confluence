package org.confluence.mod.client.summoner.model.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 动画模型渲染请求。经 {@code LyraModelRenderer.geo(...)} 创建，
 * 所有方法返回 this 以便链式使用。
 */
public final class GeoRenderOptions {

    private final ResourceLocation modelId;
    private ResourceLocation animationId;
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

    public GeoRenderOptions(ResourceLocation modelId) {
        this.modelId = modelId;
        this.animationId = modelId;
    }

    public ResourceLocation modelId() {
        return modelId;
    }

    /** 播放模型同目录推导出的动画片段，动画时间使用 tick 域。 */
    public GeoRenderOptions animation(String name, float ageTicks) {
        return animation(modelId, name, ageTicks);
    }

    /** 指定动画文件 id（通常与模型 id 相同）与动画名。 */
    public GeoRenderOptions animation(ResourceLocation animationFileId, String name, float ageTicks) {
        this.animationId = animationFileId;
        this.animationName = name;
        this.ageTicks = ageTicks;
        return this;
    }

    /** 不播放动画，仅使用模型绑定姿态。 */
    public GeoRenderOptions bindPose() {
        this.animationName = null;
        this.ageTicks = 0;
        return this;
    }

    public GeoRenderOptions texture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public GeoRenderOptions renderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public GeoRenderOptions light(int packedLight) {
        this.packedLight = packedLight;
        return this;
    }

    public GeoRenderOptions overlay(int packedOverlay) {
        this.packedOverlay = packedOverlay;
        return this;
    }

    /** 整体 ARGB 染色，-1 不启用。 */
    public GeoRenderOptions color(int argb) {
        this.color = argb;
        return this;
    }

    public GeoRenderOptions alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public GeoRenderOptions scale(float scale) {
        this.scale = scale;
        return this;
    }

    public GeoRenderOptions translate(float x, float y, float z) {
        this.translateX = x;
        this.translateY = y;
        this.translateZ = z;
        return this;
    }

    public GeoRenderOptions rotate(float xDegrees, float yDegrees, float zDegrees) {
        this.rotX = xDegrees;
        this.rotY = yDegrees;
        this.rotZ = zDegrees;
        return this;
    }

    /** 隐藏指定骨骼及其子树。 */
    public GeoRenderOptions hideBone(String... boneNames) {
        if (boneNames != null) {
            hiddenBones.addAll(Arrays.asList(boneNames));
        }
        return this;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        GeoRenderer.render(this, poseStack, bufferSource);
    }

    @Nullable
    ResourceLocation animationId() {
        return animationId;
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
