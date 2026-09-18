package org.confluence.mod.client.summoner.model.geo;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

/**
 * 从资源目录 {@code lyra_model/geo} 自动加载 .animation.json 动画文件。
 * <p>
 * 文件 {@code assets/ns/lyra_model/geo/foo/foo.animation.json} 对应模型 id {@code ns:foo}。
 * </p>
 */
public final class GeoAnimationManager extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();

    public static final GeoAnimationManager INSTANCE = new GeoAnimationManager();

    private Map<ResourceLocation, Map<String, AnimatedClip>> animations = new HashMap<>();

    private GeoAnimationManager() {
        super(GSON, "lyra_model/geo");
    }

    @Nullable
    AnimatedClip getClip(ResourceLocation fileId, String animationName) {
        Map<String, AnimatedClip> file = animations.get(fileId);
        return file == null ? null : file.get(animationName);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Map<String, AnimatedClip>> parsed = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            String path = entry.getKey().getPath();
            if (!path.endsWith(".animation")) {
                continue;
            }
            ResourceLocation fileId = modelId(entry.getKey(), ".animation");
            if (fileId == null) {
                LOGGER.warn("Ignored animation with invalid Lyra path {}", entry.getKey());
                continue;
            }

            try {
                JsonObject root = entry.getValue().getAsJsonObject();
                if (!root.has("animations") || !root.get("animations").isJsonObject()) {
                    continue;
                }
                JsonObject animationRoot = root.getAsJsonObject("animations");
                Map<String, AnimatedClip> clips = new HashMap<>();
                for (Map.Entry<String, JsonElement> clipEntry : animationRoot.entrySet()) {
                    clips.put(clipEntry.getKey(), parseClip(clipEntry.getKey(), clipEntry.getValue().getAsJsonObject()));
                }
                parsed.put(fileId, clips);
            } catch (Exception e) {
                LOGGER.warn("Failed to load animation file {}: {}", fileId, e.getMessage());
            }
        }

        this.animations = parsed;
        LOGGER.info("Loaded {} animated geo animation files", parsed.size());
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
        return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), base.substring(0, slash));
    }

    private AnimatedClip parseClip(String name, JsonObject obj) {
        double lengthSeconds = obj.has("animation_length") ? obj.get("animation_length").getAsDouble() : -1;
        boolean loop = isLooping(obj.get("loop"));

        List<AnimatedClip.BoneClip> boneClips = new ArrayList<>();
        if (obj.has("bones") && obj.get("bones").isJsonObject()) {
            JsonObject bones = obj.getAsJsonObject("bones");
            double lastTime = 0;

            for (Map.Entry<String, JsonElement> boneEntry : bones.entrySet()) {
                JsonObject boneObj = boneEntry.getValue().getAsJsonObject();
                AnimatedClip.Channel rotation = parseChannel(boneObj.get("rotation"), true);
                AnimatedClip.Channel position = parseChannel(boneObj.get("position"), false);
                AnimatedClip.Channel scale = parseChannel(boneObj.get("scale"), false);

                lastTime = Math.max(lastTime, channelLastTime(boneObj.get("rotation")));
                lastTime = Math.max(lastTime, channelLastTime(boneObj.get("position")));
                lastTime = Math.max(lastTime, channelLastTime(boneObj.get("scale")));

                if (rotation != null || position != null || scale != null) {
                    boneClips.add(new AnimatedClip.BoneClip(boneEntry.getKey(), rotation, position, scale));
                }
            }

            double lengthTicks = lengthSeconds >= 0 ? lengthSeconds * 20 : lastTime;
            return new AnimatedClip(name, lengthTicks, loop, boneClips);
        }

        return new AnimatedClip(name, lengthSeconds >= 0 ? lengthSeconds * 20 : 0, loop, boneClips);
    }

    private static double channelLastTime(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject() || element.getAsJsonObject().has("vector")) {
            return 0;
        }
        double last = 0;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            last = Math.max(last, parseTime(entry.getKey()));
        }
        return last * 20;
    }

    @Nullable
    private static AnimatedClip.Channel parseChannel(JsonElement element, boolean rotation) {
        if (element == null) {
            return null;
        }

        TreeMap<Double, VectorEntry> entries = new TreeMap<>();
        if (element.isJsonPrimitive() || element.isJsonArray()) {
            entries.put(0d, new VectorEntry(vector(element), "linear"));
        } else if (element.isJsonObject()) {
            JsonObject channelObj = element.getAsJsonObject();
            if (channelObj.has("vector")) {
                entries.put(0d, new VectorEntry(vector(channelObj.get("vector")), readEasing(channelObj)));
            } else {
                for (Map.Entry<String, JsonElement> frame : channelObj.entrySet()) {
                    double time = parseTime(frame.getKey());
                    JsonElement frameValue = frame.getValue();
                    JsonObject frameObj = frameValue.isJsonObject() ? frameValue.getAsJsonObject() : null;
                    JsonElement rawVector = frameValue;
                    String easing = "linear";
                    if (frameObj != null) {
                        if (frameObj.has("vector")) {
                            rawVector = frameObj.get("vector");
                            easing = readEasing(frameObj);
                        } else if (frameObj.has("post")) {
                            rawVector = frameObj.get("post");
                            easing = readBedrockLerp(frameObj);
                        } else if (frameObj.has("pre")) {
                            rawVector = frameObj.get("pre");
                            easing = "step";
                        }
                    }
                    entries.put(time, new VectorEntry(vector(rawVector), easing));
                }
            }
        }

        if (entries.isEmpty()) {
            return null;
        }

        List<AnimatedClip.Segment> x = new ArrayList<>();
        List<AnimatedClip.Segment> y = new ArrayList<>();
        List<AnimatedClip.Segment> z = new ArrayList<>();

        VectorEntry previous = null;
        double previousTime = 0;
        for (Map.Entry<Double, VectorEntry> entry : entries.entrySet()) {
            double time = entry.getKey();
            VectorEntry current = entry.getValue();

            if (previous == null) {
                double length = time > 0 ? time * 20 : 0;
                addSegment(rotation, x, current.vector[0], current.vector[0], length, current.easing, 0);
                addSegment(rotation, y, current.vector[1], current.vector[1], length, current.easing, 1);
                addSegment(rotation, z, current.vector[2], current.vector[2], length, current.easing, 2);
            } else {
                double length = (time - previousTime) * 20;
                addSegment(rotation, x, previous.vector[0], current.vector[0], length, current.easing, 0);
                addSegment(rotation, y, previous.vector[1], current.vector[1], length, current.easing, 1);
                addSegment(rotation, z, previous.vector[2], current.vector[2], length, current.easing, 2);
            }

            previous = current;
            previousTime = time;
        }

        return new AnimatedClip.Channel(x, y, z);
    }

    private static void addSegment(
            boolean rotation,
            List<AnimatedClip.Segment> segments,
            double startValue,
            double endValue,
            double length,
            String easing,
            int axis
    ) {
        if (rotation) {
            double factor = axis == 2 ? 1 : -1;
            startValue = Math.toRadians(startValue * factor);
            endValue = Math.toRadians(endValue * factor);
        }
        segments.add(new AnimatedClip.Segment(length, startValue, endValue, easing));
    }

    private static float[] vector(JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return new float[]{0, 0, 0};
        }
        JsonArray array = element.getAsJsonArray();
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
    }

    private static String readEasing(JsonObject obj) {
        if (obj.has("easing") && obj.get("easing").isJsonPrimitive()) {
            return obj.get("easing").getAsString();
        }
        return "linear";
    }

    private static String readBedrockLerp(JsonObject obj) {
        if (obj.has("lerp_mode") && obj.get("lerp_mode").isJsonPrimitive()) {
            String mode = obj.get("lerp_mode").getAsString();
            return switch (mode) {
                case "catmullrom" -> "catmullrom";
                case "step" -> "step";
                default -> "linear";
            };
        }
        return "linear";
    }

    private static double parseTime(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean isLooping(JsonElement element) {
        if (element == null) {
            return false;
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean();
        }
        return element.isJsonPrimitive()
                && "loop".equalsIgnoreCase(element.getAsString());
    }

    private record VectorEntry(float[] vector, String easing) {
    }
}
