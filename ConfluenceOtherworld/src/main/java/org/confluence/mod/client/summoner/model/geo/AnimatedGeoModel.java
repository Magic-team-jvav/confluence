package org.confluence.mod.client.summoner.model.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 无外部依赖的 Bedrock/Gecko 风格骨骼模型。
 * <p>
 * 模型是渲染线程内可变状态：每次渲染前 {@link #reset()}，
 * 采样动画后由 {@link #render} 递归写入顶点。
 * </p>
 */
public final class AnimatedGeoModel {

    /** 资源基础 id，例如 summoner:laser_minigun */
    private final ResourceLocation id;

    /** 默认纹理 */
    private final ResourceLocation texture;

    /** 顶层骨骼 */
    private final List<AnimatedBone> roots = new ArrayList<>();

    /** 名称索引 */
    private final Map<String, AnimatedBone> bones = new HashMap<>();

    AnimatedGeoModel(ResourceLocation id, ResourceLocation texture, List<AnimatedBone> roots) {
        this.id = id;
        this.texture = texture;
        this.roots.addAll(roots);
        for (AnimatedBone root : roots) {
            indexBone(root);
        }
    }

    private void indexBone(AnimatedBone bone) {
        this.bones.put(bone.getName(), bone);
        for (AnimatedBone child : bone.getChildren()) {
            indexBone(child);
        }
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Nullable
    public AnimatedBone getBone(String name) {
        return bones.get(name);
    }

    /** 清除上一帧写入的动画变换，恢复绑定姿态。 */
    public void reset() {
        for (AnimatedBone root : roots) {
            root.reset();
        }
    }

    /** 递归渲染全部骨骼。调用方负责传入对应纹理的 VertexConsumer。 */
    public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color) {
        for (AnimatedBone root : roots) {
            root.render(poseStack, consumer, packedLight, packedOverlay, color);
        }
    }
}
