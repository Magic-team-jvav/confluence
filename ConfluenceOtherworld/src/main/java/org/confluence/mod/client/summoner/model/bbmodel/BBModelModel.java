package org.confluence.mod.client.summoner.model.bbmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

final class BBModelModel {

    private final ResourceLocation id;
    private final List<ResourceLocation> textures;
    private final List<BBModelBone> roots = new ArrayList<>();
    private final Map<String, BBModelBone> bones = new HashMap<>();

    BBModelModel(ResourceLocation id, List<ResourceLocation> textures, List<BBModelBone> roots) {
        this.id = id;
        this.textures = List.copyOf(textures);
        this.roots.addAll(roots);
        for (BBModelBone root : roots) {
            indexBone(root);
        }
    }

    private void indexBone(BBModelBone bone) {
        bones.put(bone.getName(), bone);
        for (BBModelBone child : bone.getChildren()) {
            indexBone(child);
        }
    }

    ResourceLocation getId() {
        return id;
    }

    ResourceLocation getTexture(int index) {
        if (index < 0 || index >= textures.size()) {
            return textures.get(0);
        }
        return textures.get(index);
    }

    List<ResourceLocation> getTextures() {
        return textures;
    }

    @Nullable
    BBModelBone getBone(String name) {
        return bones.get(name);
    }

    void reset() {
        for (BBModelBone root : roots) {
            root.reset();
        }
    }

    void render(
            PoseStack poseStack,
            IntFunction<VertexConsumer> consumers,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        for (BBModelBone root : roots) {
            root.render(poseStack, consumers, packedLight, packedOverlay, color);
        }
    }
}
