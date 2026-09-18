package org.confluence.mod.client.summoner.model.geo;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从资源目录 {@code lyra_model/geo} 自动加载全部 Bedrock/Gecko .geo.json 模型。
 * <p>
 * 文件 {@code assets/ns/lyra_model/geo/foo/foo.geo.json} 对应模型 id {@code ns:foo}，
 * 纹理默认推导为 {@code lyra_model/geo/foo/foo.png}。
 * </p>
 */
public final class GeoModelManager extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();

    public static final GeoModelManager INSTANCE = new GeoModelManager();

    private Map<ResourceLocation, AnimatedGeoModel> models = new HashMap<>();

    private GeoModelManager() {
        super(GSON, "lyra_model/geo");
    }

    @Nullable
    public AnimatedGeoModel getModel(ResourceLocation modelId) {
        return models.get(modelId);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, AnimatedGeoModel> parsed = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            String path = fileId.getPath();
            if (!path.endsWith(".geo")) {
                continue;
            }
            ResourceLocation modelId = modelId(fileId, ".geo");
            if (modelId == null) {
                LOGGER.warn("Ignored geo model with invalid Lyra path {}", fileId);
                continue;
            }

            try {
                JsonObject root = entry.getValue().getAsJsonObject();
                AnimatedGeoModel model = parse(modelId, root);
                if (model != null) {
                    parsed.put(modelId, model);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load animated geo model {}: {}", modelId, e.getMessage());
            }
        }

        this.models = parsed;
        LOGGER.info("Loaded {} animated geo models", parsed.size());
    }

    @Nullable
    private AnimatedGeoModel parse(ResourceLocation modelId, JsonObject root) {
        JsonElement geometryElement = root.has("minecraft:geometry") ? root.get("minecraft:geometry") : root.get("geometry");
        if (geometryElement == null || !geometryElement.isJsonArray() || geometryElement.getAsJsonArray().isEmpty()) {
            LOGGER.warn("Geo model {} has no minecraft:geometry array", modelId);
            return null;
        }

        JsonObject geometry = geometryElement.getAsJsonArray().get(0).getAsJsonObject();
        JsonObject description = geometry.has("description") ? geometry.getAsJsonObject("description") : new JsonObject();
        float textureWidth = description.has("texture_width") ? description.get("texture_width").getAsFloat() : 64f;
        float textureHeight = description.has("texture_height") ? description.get("texture_height").getAsFloat() : 64f;
        JsonArray rawBones = description.has("bones")
                ? description.getAsJsonArray("bones")
                : (geometry.has("bones") ? geometry.getAsJsonArray("bones") : new JsonArray());

        List<AnimatedBone> bones = new ArrayList<>();
        Map<String, AnimatedBone> boneMap = new HashMap<>();
        Map<String, String> parents = new HashMap<>();

        for (JsonElement boneElement : rawBones) {
            JsonObject boneObj = boneElement.getAsJsonObject();
            String name = boneObj.get("name").getAsString();
            AnimatedBone bone = parseBone(name, boneObj, textureWidth, textureHeight);
            bones.add(bone);
            boneMap.put(name, bone);
            if (boneObj.has("parent")) {
                parents.put(name, boneObj.get("parent").getAsString());
            }
        }

        List<AnimatedBone> roots = new ArrayList<>();
        for (AnimatedBone bone : bones) {
            String parent = parents.get(bone.getName());
            if (parent == null) {
                roots.add(bone);
            } else {
                AnimatedBone parentBone = boneMap.get(parent);
                if (parentBone == null) {
                    LOGGER.warn("Bone '{}' in {} references missing parent '{}'", bone.getName(), modelId, parent);
                    roots.add(bone);
                } else {
                    parentBone.addChild(bone);
                }
            }
        }

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                modelId.getNamespace(),
                "lyra_model/geo/" + modelId.getPath() + "/" + fileName(modelId.getPath()) + ".png"
        );
        return new AnimatedGeoModel(modelId, texture, roots);
    }

    @Nullable
    private static ResourceLocation modelId(ResourceLocation fileId, String suffix) {
        String path = fileId.getPath();
        if (!path.endsWith(suffix)) {
            return null;
        }
        String base = path.substring(0, path.length() - suffix.length());
        int slash = base.lastIndexOf('/');
        if (slash <= 0) {
            return null;
        }
        String folder = base.substring(0, slash);
        return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), folder);
    }

    private static String fileName(String path) {
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private AnimatedBone parseBone(String name, JsonObject obj, float textureWidth, float textureHeight) {
        float[] pivot = vector(obj, "pivot", new float[]{0, 0, 0});
        float[] rotation = vector(obj, "rotation", new float[]{0, 0, 0});
        boolean neverRender = obj.has("neverRender")
                ? obj.get("neverRender").getAsBoolean()
                : (obj.has("never_render") && obj.get("never_render").getAsBoolean());
        float boneInflate = obj.has("inflate") ? obj.get("inflate").getAsFloat() : 0f;

        AnimatedBone bone = new AnimatedBone(
                name,
                -pivot[0],
                pivot[1],
                pivot[2],
                (float) Math.toRadians(-rotation[0]),
                (float) Math.toRadians(-rotation[1]),
                (float) Math.toRadians(rotation[2]),
                neverRender
        );

        if (obj.has("cubes")) {
            for (JsonElement cubeElement : obj.getAsJsonArray("cubes")) {
                JsonObject cubeObj = cubeElement.getAsJsonObject();
                float inflate = cubeObj.has("inflate")
                        ? cubeObj.get("inflate").getAsFloat()
                        : boneInflate;
                boolean mirror = cubeObj.has("mirror") && cubeObj.get("mirror").getAsBoolean();
                AnimatedCube cube = AnimatedCube.bake(
                        vector(cubeObj, "origin", new float[]{0, 0, 0}),
                        vector(cubeObj, "size", new float[]{1, 1, 1}),
                        vectorOrNull(cubeObj, "pivot"),
                        vectorOrNull(cubeObj, "rotation"),
                        mirror,
                        inflate,
                        textureWidth,
                        textureHeight,
                        cubeObj.get("uv")
                );
                bone.addCube(cube);
            }
        }

        return bone;
    }

    private static float[] vector(JsonObject obj, String key, float[] fallback) {
        if (!obj.has(key) || !obj.get(key).isJsonArray()) {
            return fallback;
        }
        JsonArray array = obj.getAsJsonArray(key);
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
    }

    private static float @Nullable [] vectorOrNull(JsonObject obj, String key) {
        if (!obj.has(key) || !obj.get(key).isJsonArray()) {
            return null;
        }
        JsonArray array = obj.getAsJsonArray(key);
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
    }
}
