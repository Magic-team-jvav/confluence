package org.confluence.mod.client.summoner.model.bbmodel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

final class BBModelLoader {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String[] FACE_NAMES = {"north", "south", "west", "east", "up", "down"};

    private BBModelLoader() {
    }

    static ParsedModel parse(ResourceLocation modelId, JsonObject root, ResourceManager resourceManager) {
        float textureWidth = 64f;
        float textureHeight = 64f;
        if (root.has("resolution") && root.get("resolution").isJsonObject()) {
            JsonObject resolution = root.getAsJsonObject("resolution");
            textureWidth = Math.max(1f, number(resolution, "width", 64f));
            textureHeight = Math.max(1f, number(resolution, "height", 64f));
        }

        TextureData textureData = parseTextures(modelId, root, resourceManager);
        List<ResourceLocation> textureLocations = textureData.bindings().stream()
                .map(TextureBinding::location)
                .toList();

        List<JsonObject> elements = new ArrayList<>();
        JsonArray rawElements = root.has("elements") && root.get("elements").isJsonArray()
                ? root.getAsJsonArray("elements")
                : root.has("cubes") && root.get("cubes").isJsonArray()
                ? root.getAsJsonArray("cubes")
                : new JsonArray();
        for (JsonElement element : rawElements) {
            if (element.isJsonObject()) {
                elements.add(element.getAsJsonObject());
            }
        }

        Map<String, JsonObject> groups = new HashMap<>();
        if (root.has("groups") && root.get("groups").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("groups")) {
                if (element.isJsonObject()) {
                    JsonObject group = element.getAsJsonObject();
                    groups.put(groupId(group, groups.size()), group);
                }
            }
        }

        boolean defaultBoxUv = root.has("meta")
                && root.getAsJsonObject("meta").has("box_uv")
                && root.getAsJsonObject("meta").get("box_uv").getAsBoolean();

        Map<String, JsonObject> elementsByUuid = new HashMap<>();
        List<String> elementKeys = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            JsonObject element = elements.get(i);
            String key = elementId(element, i);
            elementsByUuid.put(key, element);
            elementKeys.add(key);
        }

        BuildContext context = new BuildContext(
                textureWidth,
                textureHeight,
                defaultBoxUv,
                textureData.keys(),
                Math.max(1, textureLocations.size()),
                elementsByUuid,
                elementKeys
        );

        List<BBModelBone> roots = new ArrayList<>();
        Set<String> visitedGroups = new HashSet<>();
        Set<String> usedElements = new HashSet<>();
        JsonArray outliner = root.has("outliner") && root.get("outliner").isJsonArray()
                ? root.getAsJsonArray("outliner")
                : new JsonArray();
        for (JsonElement node : outliner) {
            BBModelBone bone = buildOutlinerNode(node, null, false, context, groups, visitedGroups, usedElements);
            if (bone != null && !roots.contains(bone)) {
                roots.add(bone);
            }
        }

        for (Map.Entry<String, JsonObject> entry : groups.entrySet()) {
            if (!visitedGroups.contains(entry.getKey())) {
                BBModelBone bone = buildGroup(entry.getKey(), entry.getValue(), null, context);
                if (bone != null) {
                    roots.add(bone);
                    visitedGroups.add(entry.getKey());
                    if (entry.getValue().has("children") && entry.getValue().get("children").isJsonArray()) {
                        boolean groupMirror = booleanValue(entry.getValue(), "mirror_uv");
                        for (JsonElement child : entry.getValue().getAsJsonArray("children")) {
                            buildOutlinerNode(child, bone, groupMirror, context, groups, visitedGroups, usedElements);
                        }
                    }
                }
            }
        }

        boolean hasLooseCubes = false;
        BBModelBone looseRoot = new BBModelBone("__root", 0, 0, 0, 0, 0, 0, false);
        for (String elementKey : elementKeys) {
            if (!usedElements.contains(elementKey)) {
                JsonObject element = elementsByUuid.get(elementKey);
                BBModelCube cube = parseCube(element, context, false);
                if (cube != null) {
                    looseRoot.addCube(cube);
                    hasLooseCubes = true;
                }
            }
        }
        if (hasLooseCubes) {
            roots.add(looseRoot);
        }
        if (roots.isEmpty()) {
            roots.add(new BBModelBone("__root", 0, 0, 0, 0, 0, 0, false));
        }

        BBModelModel model = new BBModelModel(modelId, textureLocations, roots);
        Map<String, BBModelClip> animations = parseAnimations(root, context.boneNamesByUuid());
        return new ParsedModel(model, animations, textureData.embeddedTextures());
    }

    @Nullable
    private static BBModelBone buildOutlinerNode(
            JsonElement node,
            @Nullable BBModelBone parent,
            boolean parentMirror,
            BuildContext context,
            Map<String, JsonObject> groups,
            Set<String> visitedGroups,
            Set<String> usedElements
    ) {
        if (node == null || node.isJsonNull()) {
            return null;
        }
        if (node.isJsonPrimitive()) {
            String key;
            if (node.getAsJsonPrimitive().isNumber()) {
                int index = Math.max(0, Math.min(context.elementKeys().size() - 1, node.getAsInt()));
                key = context.elementKeys().isEmpty() ? null : context.elementKeys().get(index);
            } else {
                key = node.getAsString();
            }
            addCube(key, parent, context, usedElements, parentMirror);
            return parent;
        }
        if (!node.isJsonObject()) {
            return null;
        }

        JsonObject nodeObject = node.getAsJsonObject();
        String uuid = nodeObject.has("uuid") ? nodeObject.get("uuid").getAsString() : null;
        JsonObject groupData = uuid == null ? null : groups.get(uuid);
        JsonObject mergedGroup = merge(groupData, nodeObject);
        BBModelBone bone = buildGroup(uuid, mergedGroup, parent, context);
        if (bone == null) {
            return parent;
        }
        if (uuid != null) {
            visitedGroups.add(uuid);
        }
        if (nodeObject.has("children") && nodeObject.get("children").isJsonArray()) {
            boolean groupMirror = parentMirror ^ booleanValue(mergedGroup, "mirror_uv");
            for (JsonElement child : nodeObject.getAsJsonArray("children")) {
                buildOutlinerNode(child, bone, groupMirror, context, groups, visitedGroups, usedElements);
            }
        }
        return bone;
    }

    @Nullable
    private static BBModelBone buildGroup(
            @Nullable String uuid,
            JsonObject groupObject,
            @Nullable BBModelBone parent,
            BuildContext context
    ) {
        if (groupObject == null) {
            return null;
        }
        String name = string(groupObject, "name", uuid == null ? "bone" : uuid);
        float[] origin = vector(groupObject, "origin", new float[]{0, 0, 0});
        float[] rotation = vector(groupObject, "rotation", new float[]{0, 0, 0});
        boolean visible = !groupObject.has("visibility") || groupObject.get("visibility").getAsBoolean();
        boolean exported = !groupObject.has("export") || groupObject.get("export").getAsBoolean();
        BBModelBone bone = new BBModelBone(
                name,
                origin[0],
                origin[1],
                origin[2],
                (float) Math.toRadians(rotation[0]),
                (float) Math.toRadians(rotation[1]),
                (float) Math.toRadians(rotation[2]),
                !visible || !exported
        );
        if (parent != null) {
            parent.addChild(bone);
        }
        if (uuid != null) {
            context.boneNamesByUuid().put(uuid, name);
        }
        return bone;
    }

    private static void addCube(
            @Nullable String elementKey,
            @Nullable BBModelBone bone,
            BuildContext context,
            Set<String> usedElements,
            boolean groupMirror
    ) {
        if (bone == null || elementKey == null || usedElements.contains(elementKey)) {
            return;
        }
        JsonObject element = context.elements().get(elementKey);
        if (element == null) {
            return;
        }
        BBModelCube cube = parseCube(element, context, groupMirror);
        if (cube != null) {
            bone.addCube(cube);
        }
        usedElements.add(elementKey);
    }

    @Nullable
    private static BBModelCube parseCube(JsonObject element, BuildContext context, boolean inheritedMirror) {
        if (element.has("export") && !element.get("export").getAsBoolean()) {
            return null;
        }
        if (element.has("visibility") && !element.get("visibility").getAsBoolean()) {
            return null;
        }
        String type = string(element, "type", "cube");
        if (!"cube".equals(type)) {
            return null;
        }

        boolean boxUv = element.has("box_uv")
                ? element.get("box_uv").getAsBoolean()
                : context.defaultBoxUv();
        Map<String, Integer> faceTextures = createFaceTextures(element, context, boxUv);
        return BBModelCube.bake(
                element,
                context.textureWidth(),
                context.textureHeight(),
                inheritedMirror,
                faceTextures
        );
    }

    private static Map<String, Integer> createFaceTextures(JsonObject element, BuildContext context, boolean boxUv) {
        Map<String, Integer> result = new HashMap<>();
        if (!element.has("faces") || !element.get("faces").isJsonObject()) {
            return result;
        }
        JsonObject faces = element.getAsJsonObject("faces");
        for (String faceName : FACE_NAMES) {
            if (!faces.has(faceName) || !faces.get(faceName).isJsonObject()) {
                if (boxUv) {
                    result.put(faceName, -1);
                }
                continue;
            }
            JsonObject face = faces.getAsJsonObject(faceName);
            if (!hasFaceTexture(face)) {
                if (boxUv) {
                    result.put(faceName, -1);
                }
                continue;
            }
            String key = face.get("texture").getAsString();
            Integer index = context.textureKeys().get(key);
            if (index == null) {
                try {
                    index = Integer.parseInt(key);
                } catch (NumberFormatException ignored) {
                    index = 0;
                }
            }
            result.put(faceName, Math.max(0, Math.min(context.textureCount() - 1, index)));
        }
        return result;
    }

    private static boolean hasFaceTexture(JsonObject face) {
        if (!face.has("texture") || face.get("texture").isJsonNull()) {
            return false;
        }
        JsonElement texture = face.get("texture");
        return !texture.isJsonPrimitive()
                || !texture.getAsJsonPrimitive().isBoolean()
                || texture.getAsBoolean();
    }

    private static TextureData parseTextures(
            ResourceLocation modelId,
            JsonObject root,
            ResourceManager resourceManager
    ) {
        List<TextureBinding> bindings = new ArrayList<>();
        Map<String, Integer> keys = new HashMap<>();
        if (root.has("textures") && root.get("textures").isJsonArray()) {
            JsonArray textures = root.getAsJsonArray("textures");
            for (int i = 0; i < textures.size(); i++) {
                if (!textures.get(i).isJsonObject()) {
                    continue;
                }
                JsonObject texture = textures.get(i).getAsJsonObject();
                String source = string(texture, "source", "");
                ResourceLocation location = null;
                byte[] embedded = null;
                if (source.startsWith("data:image/") && source.contains(";base64,")) {
                    try {
                        String encoded = source.substring(source.indexOf(";base64,") + 8);
                        embedded = Base64.getMimeDecoder().decode(encoded.getBytes(StandardCharsets.US_ASCII));
                        location = dynamicTextureLocation(modelId, i);
                    } catch (IllegalArgumentException exception) {
                        LOGGER.warn("Failed to decode embedded BBModel texture {} in {}", i, modelId, exception);
                    }
                }
                if (location == null) {
                    location = findExternalTexture(modelId, texture, resourceManager);
                }
                bindings.add(new TextureBinding(location, embedded));
                registerTextureKey(keys, texture, "id", i);
                registerTextureKey(keys, texture, "uuid", i);
                registerTextureKey(keys, texture, "name", i);
            }
        }

        if (bindings.isEmpty()) {
            ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(
                    modelId.getNamespace(),
                    "textures/item/entity/" + modelId.getPath() + ".png"
            );
            bindings.add(new TextureBinding(fallback, null));
        }
        return new TextureData(bindings, keys);
    }

    private static void registerTextureKey(Map<String, Integer> keys, JsonObject texture, String key, int index) {
        if (texture.has(key) && texture.get(key).isJsonPrimitive()) {
            keys.put(texture.get(key).getAsString(), index);
        }
    }

    private static ResourceLocation dynamicTextureLocation(ResourceLocation modelId, int index) {
        return ResourceLocation.fromNamespaceAndPath(
                modelId.getNamespace(),
                "bbmodel/" + modelId.getPath() + "/" + index
        );
    }

    private static ResourceLocation findExternalTexture(
            ResourceLocation modelId,
            JsonObject texture,
            ResourceManager resourceManager
    ) {
        Set<ResourceLocation> candidates = new LinkedHashSet<>();
        addTextureCandidates(candidates, modelId.getNamespace(), string(texture, "relative_path", null));
        addTextureCandidates(candidates, modelId.getNamespace(), string(texture, "path", null));
        addTextureCandidates(candidates, modelId.getNamespace(), string(texture, "name", null));
        for (ResourceLocation candidate : candidates) {
            if (resourceManager.getResource(candidate).isPresent()) {
                return candidate;
            }
        }

        String baseName = fileName(string(texture, "relative_path", string(texture, "name", "texture")))
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_.-]", "_");
        if (!baseName.endsWith(".png")) {
            baseName += ".png";
        }
        return ResourceLocation.fromNamespaceAndPath(
                modelId.getNamespace(),
                "textures/item/entity/" + baseName
        );
    }

    private static void addTextureCandidates(Set<ResourceLocation> candidates, String namespace, @Nullable String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return;
        }
        String path = rawPath.replace('\\', '/').replaceFirst("^[A-Za-z]:/", "");
        int assetIndex = path.lastIndexOf("/assets/" + namespace + "/");
        if (assetIndex >= 0) {
            path = path.substring(assetIndex + ("/assets/" + namespace + "/").length());
        } else if (path.startsWith("assets/" + namespace + "/")) {
            path = path.substring(("assets/" + namespace + "/").length());
        }
        path = path.replaceFirst("^\\./+", "").replaceFirst("^/+", "");
        int texturesIndex = path.indexOf("textures/");
        if (texturesIndex >= 0) {
            path = path.substring(texturesIndex);
        }

        addCandidate(candidates, namespace, path);
        addCandidate(candidates, namespace, "textures/" + path);
        String fileName = fileName(path);
        addCandidate(candidates, namespace, "textures/item/entity/" + fileName);
        addCandidate(candidates, namespace, "textures/entity/" + fileName);
        addCandidate(candidates, namespace, "textures/" + fileName);
    }

    private static void addCandidate(Set<ResourceLocation> candidates, String namespace, String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        String normalized = path.replace('\\', '/').replaceFirst("^/+", "");
        if (!normalized.contains("/")) {
            normalized = "textures/" + normalized;
        }
        if (!normalized.endsWith(".png")) {
            normalized += ".png";
        }
        try {
            candidates.add(ResourceLocation.fromNamespaceAndPath(namespace, normalized));
        } catch (RuntimeException ignored) {
        }
    }

    private static String fileName(String path) {
        int slash = path.replace('\\', '/').lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static Map<String, BBModelClip> parseAnimations(JsonObject root, Map<String, String> boneNames) {
        Map<String, BBModelClip> animations = new HashMap<>();
        if (!root.has("animations") || !root.get("animations").isJsonArray()) {
            return animations;
        }

        for (JsonElement element : root.getAsJsonArray("animations")) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject animation = element.getAsJsonObject();
            String name = string(animation, "name", "animation");
            boolean loop = isLooping(animation.get("loop"));
            double lengthTicks = number(animation, "length", 0f) * 20d;
            Map<String, List<Keyframe>> framesByBone = new LinkedHashMap<>();

            if (animation.has("animators") && animation.get("animators").isJsonObject()) {
                JsonObject animators = animation.getAsJsonObject("animators");
                for (Map.Entry<String, JsonElement> entry : animators.entrySet()) {
                    if (!entry.getValue().isJsonObject()) {
                        continue;
                    }
                    JsonObject animator = entry.getValue().getAsJsonObject();
                    String boneName = boneNames.getOrDefault(
                            entry.getKey(),
                            string(animator, "name", entry.getKey())
                    );
                    List<Keyframe> keyframes = parseKeyframes(animator);
                    if (!keyframes.isEmpty()) {
                        double lastKeyframe = keyframes.stream()
                                .mapToDouble(Keyframe::time)
                                .max()
                                .orElse(0);
                        lengthTicks = Math.max(lengthTicks, lastKeyframe * 20d);
                    }
                    framesByBone.put(boneName, keyframes);
                }
            }

            List<BBModelClip.BoneClip> boneClips = new ArrayList<>();
            for (Map.Entry<String, List<Keyframe>> entry : framesByBone.entrySet()) {
                List<Keyframe> keyframes = entry.getValue();
                BBModelClip.Channel rotation = channel(keyframes, "rotation", loop, lengthTicks);
                BBModelClip.Channel position = channel(keyframes, "position", loop, lengthTicks);
                BBModelClip.Channel scale = channel(keyframes, "scale", loop, lengthTicks);
                if (rotation != null || position != null || scale != null) {
                    boneClips.add(new BBModelClip.BoneClip(entry.getKey(), rotation, position, scale));
                }
            }
            if (boneClips.isEmpty()) {
                continue;
            }

            BBModelClip clip = new BBModelClip(name, lengthTicks, loop, boneClips);
            animations.put(name, clip);
            int lastDot = name.lastIndexOf('.');
            if (lastDot >= 0 && lastDot + 1 < name.length()) {
                animations.putIfAbsent(name.substring(lastDot + 1), clip);
            }
        }
        return animations;
    }

    private static List<Keyframe> parseKeyframes(JsonObject animator) {
        List<Keyframe> result = new ArrayList<>();
        if (animator.has("keyframes") && animator.get("keyframes").isJsonArray()) {
            for (JsonElement element : animator.getAsJsonArray("keyframes")) {
                if (element.isJsonObject()) {
                    JsonObject keyframe = element.getAsJsonObject();
                    result.add(parseKeyframe(keyframe, string(keyframe, "channel", "rotation")));
                }
            }
        }
        return result;
    }

    private static Keyframe parseKeyframe(JsonObject keyframe, String channel) {
        double time = number(keyframe, "time", 0d);
        double defaultValue = "scale".equals(channel) ? 1 : 0;
        JsonArray dataPoints = keyframe.has("data_points") && keyframe.get("data_points").isJsonArray()
                ? keyframe.getAsJsonArray("data_points")
                : new JsonArray();
        double[] pre = dataPoints.isEmpty()
                ? vector(keyframe, new double[]{defaultValue, defaultValue, defaultValue}, defaultValue)
                : vector(dataPoints.get(0), new double[]{defaultValue, defaultValue, defaultValue}, defaultValue);
        double[] post = dataPoints.isEmpty()
                ? vector(keyframe, new double[]{defaultValue, defaultValue, defaultValue}, defaultValue)
                : vector(dataPoints.get(dataPoints.size() - 1), new double[]{defaultValue, defaultValue, defaultValue}, defaultValue);
        return new Keyframe(
                time,
                pre,
                post,
                string(keyframe, "interpolation", "linear"),
                channel,
                optionalVector(keyframe, "bezier_right_time"),
                optionalVector(keyframe, "bezier_right_value"),
                optionalVector(keyframe, "bezier_left_time"),
                optionalVector(keyframe, "bezier_left_value")
        );
    }

    @Nullable
    private static BBModelClip.Channel channel(
            List<Keyframe> allKeyframes,
            String channelName,
            boolean loop,
            double animationLength
    ) {
        List<Keyframe> keyframes = allKeyframes.stream()
                .filter(frame -> frame.channel().equals(channelName))
                .sorted(Comparator.comparingDouble(Keyframe::time))
                .toList();
        if (keyframes.isEmpty()) {
            return null;
        }

        List<BBModelClip.Segment> x = new ArrayList<>();
        List<BBModelClip.Segment> y = new ArrayList<>();
        List<BBModelClip.Segment> z = new ArrayList<>();
        Keyframe first = keyframes.get(0);
        addSegment(x, first.pre()[0], first.pre()[0], first.time() * 20d, "linear");
        addSegment(y, first.pre()[1], first.pre()[1], first.time() * 20d, "linear");
        addSegment(z, first.pre()[2], first.pre()[2], first.time() * 20d, "linear");
        for (int i = 1; i < keyframes.size(); i++) {
            double length = Math.max(0, keyframes.get(i).time() - keyframes.get(i - 1).time()) * 20d;
            appendTransition(x, keyframes, i, 0, channelName, length, loop);
            appendTransition(y, keyframes, i, 1, channelName, length, loop);
            appendTransition(z, keyframes, i, 2, channelName, length, loop);
        }

        List<BBModelClip.Segment> wrapX = new ArrayList<>();
        List<BBModelClip.Segment> wrapY = new ArrayList<>();
        List<BBModelClip.Segment> wrapZ = new ArrayList<>();
        double firstTime = first.time() * 20d;
        double wrapStart = keyframes.get(keyframes.size() - 1).time() * 20d;
        double wrapLength = loop ? animationLength - wrapStart + firstTime : 0;
        boolean seamFallback = false;
        if (loop && wrapLength <= 1.0E-6) {
            double seam = Math.max(0.5, Math.min(2.0, animationLength * 0.2));
            wrapStart = Math.max(firstTime, animationLength - seam);
            wrapLength = animationLength - wrapStart + firstTime;
            seamFallback = true;
        }
        if (loop && wrapLength > 1.0E-6) {
            if (seamFallback) {
                addSegment(
                        wrapX,
                        sampleSegments(x, wrapStart),
                        transform(channelName, 0, first.pre()[0]),
                        wrapLength,
                        "linear"
                );
                addSegment(
                        wrapY,
                        sampleSegments(y, wrapStart),
                        transform(channelName, 1, first.pre()[1]),
                        wrapLength,
                        "linear"
                );
                addSegment(
                        wrapZ,
                        sampleSegments(z, wrapStart),
                        transform(channelName, 2, first.pre()[2]),
                        wrapLength,
                        "linear"
                );
            } else {
                appendWrapTransition(wrapX, keyframes, 0, channelName, wrapLength);
                appendWrapTransition(wrapY, keyframes, 1, channelName, wrapLength);
                appendWrapTransition(wrapZ, keyframes, 2, channelName, wrapLength);
            }
        }
        return new BBModelClip.Channel(
                x,
                y,
                z,
                wrapX,
                wrapY,
                wrapZ,
                wrapStart,
                wrapLength,
                firstTime,
                animationLength
        );
    }

    private static void appendTransition(
            List<BBModelClip.Segment> segments,
            List<Keyframe> keyframes,
            int index,
            int axis,
            String channel,
            double length,
            boolean loop
    ) {
        Keyframe previous = keyframes.get(index - 1);
        Keyframe current = keyframes.get(index);
        double start = transform(channel, axis, previous.post()[axis]);
        double end = transform(channel, axis, current.pre()[axis]);
        String interpolation = previous.interpolation();

        if ("catmullrom".equalsIgnoreCase(interpolation)) {
            double before = index >= 2
                    ? transform(channel, axis, keyframes.get(index - 2).post()[axis])
                    : loop ? transform(channel, axis, keyframes.get(keyframes.size() - 1).post()[axis]) : start;
            double after = index + 1 < keyframes.size()
                    ? transform(channel, axis, keyframes.get(index + 1).pre()[axis])
                    : loop ? transform(channel, axis, keyframes.get(0).pre()[axis]) : end;
            addSampledTransition(segments, length, value -> catmullRom(before, start, end, after, value));
        } else if ("bezier".equalsIgnoreCase(interpolation)) {
            double interval = Math.max(1.0E-6, current.time() - previous.time());
            double rightTime = arrayValue(previous.bezierRightTime(), axis, interval / 3d);
            double leftTime = arrayValue(current.bezierLeftTime(), axis, -interval / 3d);
            double rightValue = arrayValue(previous.bezierRightValue(), axis, 0d);
            double leftValue = arrayValue(current.bezierLeftValue(), axis, 0d);
            double control1 = transform(channel, axis, previous.post()[axis] + rightValue);
            double control2 = transform(channel, axis, current.pre()[axis] + leftValue);
            addSampledTransition(segments, length, value -> cubicBezier(
                    start,
                    end,
                    control1,
                    control2,
                    clamp01(rightTime / interval),
                    clamp01(1d + leftTime / interval),
                    value
            ));
        } else {
            addSegment(segments, start, end, length, "step".equalsIgnoreCase(interpolation) ? "step" : "linear");
        }
    }

    private static void appendWrapTransition(
            List<BBModelClip.Segment> segments,
            List<Keyframe> keyframes,
            int axis,
            String channel,
            double length
    ) {
        Keyframe previous = keyframes.get(keyframes.size() - 1);
        Keyframe current = keyframes.get(0);
        double start = transform(channel, axis, previous.post()[axis]);
        double end = transform(channel, axis, current.pre()[axis]);
        String interpolation = previous.interpolation();

        if ("catmullrom".equalsIgnoreCase(interpolation)) {
            double before = keyframes.size() >= 2
                    ? transform(channel, axis, keyframes.get(keyframes.size() - 2).post()[axis])
                    : start;
            double after = keyframes.size() >= 2
                    ? transform(channel, axis, keyframes.get(1).pre()[axis])
                    : end;
            addSampledTransition(segments, length, value -> catmullRom(before, start, end, after, value));
        } else if ("bezier".equalsIgnoreCase(interpolation)) {
            double interval = Math.max(1.0E-6, length / 20d);
            double rightTime = arrayValue(previous.bezierRightTime(), axis, interval / 3d);
            double leftTime = arrayValue(current.bezierLeftTime(), axis, -interval / 3d);
            double rightValue = arrayValue(previous.bezierRightValue(), axis, 0d);
            double leftValue = arrayValue(current.bezierLeftValue(), axis, 0d);
            double control1 = transform(channel, axis, previous.post()[axis] + rightValue);
            double control2 = transform(channel, axis, current.pre()[axis] + leftValue);
            addSampledTransition(segments, length, value -> cubicBezier(
                    start,
                    end,
                    control1,
                    control2,
                    clamp01(rightTime / interval),
                    clamp01(1d + leftTime / interval),
                    value
            ));
        } else {
            addSegment(segments, start, end, length, "step".equalsIgnoreCase(interpolation) ? "step" : "linear");
        }
    }

    private static double sampleSegments(List<BBModelClip.Segment> segments, double elapsed) {
        if (segments == null || segments.isEmpty()) {
            return 0;
        }
        double segmentStart = 0;
        int last = segments.size() - 1;
        for (int i = 0; i < segments.size(); i++) {
            BBModelClip.Segment segment = segments.get(i);
            double segmentEnd = segmentStart + segment.length();
            if (elapsed < segmentEnd || i == last) {
                double local = elapsed - segmentStart;
                if (segment.length() <= 0 || local >= segment.length()) {
                    return segment.endValue();
                }
                double t = local / segment.length();
                double eased = BBModelEasing.apply(segment.easing(), t);
                return net.minecraft.util.Mth.lerp(eased, segment.startValue(), segment.endValue());
            }
            segmentStart = segmentEnd;
        }
        return segments.get(segments.size() - 1).endValue();
    }

    private static void addSampledTransition(
            List<BBModelClip.Segment> segments,
            double length,
            Curve curve
    ) {
        int steps = (int) Math.max(8, Math.min(80, Math.ceil(length)));
        double stepLength = length / steps;
        double previous = curve.value(0);
        for (int step = 1; step <= steps; step++) {
            double current = curve.value(step / (double) steps);
            addSegment(segments, previous, current, stepLength, "linear");
            previous = current;
        }
    }

    private static double catmullRom(double before, double start, double end, double after, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        return 0.5 * (
                (2 * start)
                        + (-before + end) * t
                        + (2 * before - 5 * start + 4 * end - after) * t2
                        + (-before + 3 * start - 3 * end + after) * t3
        );
    }

    private static double cubicBezier(
            double start,
            double end,
            double control1,
            double control2,
            double control1X,
            double control2X,
            double t
    ) {
        double low = 0;
        double high = 1;
        for (int i = 0; i < 18; i++) {
            double middle = (low + high) * 0.5;
            if (bezier(middle, 0, control1X, control2X, 1) < t) {
                low = middle;
            } else {
                high = middle;
            }
        }
        return bezier((low + high) * 0.5, start, control1, control2, end);
    }

    private static double bezier(double t, double start, double control1, double control2, double end) {
        double inverse = 1 - t;
        return inverse * inverse * inverse * start
                + 3 * inverse * inverse * t * control1
                + 3 * inverse * t * t * control2
                + t * t * t * end;
    }

    private static void addSegment(
            List<BBModelClip.Segment> segments,
            double start,
            double end,
            double length,
            String easing
    ) {
        segments.add(new BBModelClip.Segment(length, start, end, easing));
    }

    private static double transform(String channel, int axis, double value) {
        if ("rotation".equals(channel)) {
            return Math.toRadians(value);
        }
        return value;
    }

    private static double arrayValue(@Nullable double[] array, int index, double fallback) {
        return array != null && index >= 0 && index < array.length ? array[index] : fallback;
    }

    private static double clamp01(double value) {
        return Math.max(0, Math.min(1, value));
    }

    private static boolean isLooping(@Nullable JsonElement loop) {
        if (loop == null || loop.isJsonNull()) {
            return false;
        }
        if (loop.isJsonPrimitive() && loop.getAsJsonPrimitive().isBoolean()) {
            return loop.getAsBoolean();
        }
        return loop.isJsonPrimitive() && "loop".equalsIgnoreCase(loop.getAsString());
    }

    private static String groupId(JsonObject object, int fallback) {
        return object.has("uuid") ? object.get("uuid").getAsString() : "__group_" + fallback;
    }

    private static String elementId(JsonObject object, int fallback) {
        return object.has("uuid") ? object.get("uuid").getAsString() : "__element_" + fallback;
    }

    private static JsonObject merge(@Nullable JsonObject base, JsonObject override) {
        JsonObject result = base == null ? new JsonObject() : base.deepCopy();
        for (Map.Entry<String, JsonElement> entry : override.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static float number(JsonObject object, String key, float fallback) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return object.get(key).getAsFloat();
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static double number(JsonObject object, String key, double fallback) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return object.get(key).getAsDouble();
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static String string(JsonObject object, String key, String fallback) {
        if (!object.has(key) || object.get(key).isJsonNull() || !object.get(key).isJsonPrimitive()) {
            return fallback;
        }
        return object.get(key).getAsString();
    }

    private static boolean booleanValue(JsonObject object, String key) {
        return object.has(key) && object.get(key).isJsonPrimitive() && object.get(key).getAsBoolean();
    }

    private static float[] vector(JsonObject object, String key, float[] fallback) {
        if (!object.has(key) || !object.get(key).isJsonArray()) {
            return fallback;
        }
        JsonArray array = object.getAsJsonArray(key);
        if (array.size() < 3) {
            return fallback;
        }
        try {
            return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static double @Nullable [] optionalVector(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonArray()) {
            return null;
        }
        JsonArray array = object.getAsJsonArray(key);
        if (array.size() < 3) {
            return null;
        }
        double[] result = new double[3];
        for (int axis = 0; axis < 3; axis++) {
            result[axis] = parseValue(array.get(axis), 0);
        }
        return result;
    }

    private static double[] vector(JsonElement element, double[] fallback, double defaultValue) {
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            return new double[]{
                    parseValue(object.get("x"), fallback[0]),
                    parseValue(object.get("y"), fallback[1]),
                    parseValue(object.get("z"), fallback[2])
            };
        }
        if (!element.isJsonArray()) {
            return fallback;
        }
        JsonArray array = element.getAsJsonArray();
        if (array.size() < 3) {
            return fallback;
        }
        double[] result = new double[3];
        for (int axis = 0; axis < 3; axis++) {
            result[axis] = parseValue(array.get(axis), defaultValue);
        }
        return result;
    }

    private static double parseValue(@Nullable JsonElement element, double fallback) {
        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return fallback;
        }
        try {
            if (element.getAsJsonPrimitive().isNumber()) {
                return element.getAsDouble();
            }
            return Double.parseDouble(element.getAsString());
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    record ParsedModel(
            BBModelModel model,
            Map<String, BBModelClip> animations,
            Map<ResourceLocation, byte[]> embeddedTextures
    ) {
    }

    private record TextureBinding(ResourceLocation location, byte @Nullable [] embeddedData) {
    }

    private record TextureData(
            List<TextureBinding> bindings,
            Map<String, Integer> keys,
            Map<ResourceLocation, byte[]> embeddedTextures
    ) {
        private TextureData(List<TextureBinding> bindings, Map<String, Integer> keys) {
            this(bindings, keys, createEmbeddedTextures(bindings));
        }

        private static Map<ResourceLocation, byte[]> createEmbeddedTextures(List<TextureBinding> bindings) {
            Map<ResourceLocation, byte[]> result = new HashMap<>();
            for (TextureBinding binding : bindings) {
                if (binding.embeddedData() != null) {
                    result.put(binding.location(), binding.embeddedData());
                }
            }
            return result;
        }
    }

    private record Keyframe(
            double time,
            double[] pre,
            double[] post,
            String interpolation,
            String channel,
            double @Nullable [] bezierRightTime,
            double @Nullable [] bezierRightValue,
            double @Nullable [] bezierLeftTime,
            double @Nullable [] bezierLeftValue
    ) {
    }

    @FunctionalInterface
    private interface Curve {
        double value(double t);
    }

    private static final class BuildContext {
        private final float textureWidth;
        private final float textureHeight;
        private final boolean defaultBoxUv;
        private final Map<String, Integer> textureKeys;
        private final int textureCount;
        private final Map<String, JsonObject> elements;
        private final List<String> elementKeys;
        private final Map<String, String> boneNamesByUuid = new HashMap<>();

        private BuildContext(
                float textureWidth,
                float textureHeight,
                boolean defaultBoxUv,
                Map<String, Integer> textureKeys,
                int textureCount,
                Map<String, JsonObject> elements,
                List<String> elementKeys
        ) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.defaultBoxUv = defaultBoxUv;
            this.textureKeys = textureKeys;
            this.textureCount = textureCount;
            this.elements = elements;
            this.elementKeys = List.copyOf(elementKeys);
        }

        float textureWidth() {
            return textureWidth;
        }

        float textureHeight() {
            return textureHeight;
        }

        boolean defaultBoxUv() {
            return defaultBoxUv;
        }

        Map<String, Integer> textureKeys() {
            return textureKeys;
        }

        int textureCount() {
            return textureCount;
        }

        Map<String, JsonObject> elements() {
            return elements;
        }

        List<String> elementKeys() {
            return elementKeys;
        }

        Map<String, String> boneNamesByUuid() {
            return boneNamesByUuid;
        }
    }
}
