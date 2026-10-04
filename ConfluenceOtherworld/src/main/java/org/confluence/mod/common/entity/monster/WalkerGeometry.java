package org.confluence.mod.common.entity.monster;

import com.google.gson.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** 模型空间姿态与碰撞几何的共享实现；可在专用服务器上使用。 */
public final class WalkerGeometry {
    public static final float SCALE = 0.7F;
    // 旋转后的腿部链已将脚底置于模型 Y=0。
    // 渲染与碰撞几何共用此值；无需额外抬高。
    public static final float OFFSET_Y = 0.0F;
    private static final Map<String, JsonObject> BONES = new LinkedHashMap<>();
    private static final JsonObject ANIMATIONS;
    static {
        JsonObject animations = new JsonObject();
        try {
            JsonObject model = read("geo/entity/martian_walker.geo.json");
            for (JsonElement element : model.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonArray("bones")) {
                JsonObject bone = element.getAsJsonObject();
                BONES.put(bone.get("name").getAsString(), bone);
            }
            animations = read("animations/entity/martian_walker.animation.json").getAsJsonObject("animations");
        } catch (Exception exception) {
            Confluence.LOGGER.error("Cannot load Martian Walker collision geometry", exception);
        }
        ANIMATIONS = animations;
    }

    private static JsonObject read(String path) throws Exception {
        var stream = WalkerGeometry.class.getResourceAsStream("/assets/confluence/" + path);
        if (stream == null) throw new java.io.IOException("Missing Walker asset: " + path);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    public static Set<String> boneNames() { return BONES.keySet(); }
    public record Pose(double standSeconds, double walkSeconds, float walkWeight) {}

    /** 基岩版旋转通过反转 X、Y 转换为 Gecko 坐标。 */
    public static Vector3f rotation(String name, boolean moving, double seconds) {
        return rotation(name, new Pose(seconds, seconds, moving ? 1 : 0));
    }
    public static Vector3f rotation(String name, Pose pose) {
        JsonObject bone = BONES.get(name);
        Vector3f rotation = vector(bone == null ? null : bone.get("rotation"));
        rotation.add(blendedChannel(name, "rotation", pose));
        return rotation.mul((float) (-Math.PI / 180), (float) (-Math.PI / 180), (float) (Math.PI / 180));
    }

    public static Vector3f translation(String name, boolean moving, double seconds) {
        return translation(name, new Pose(seconds, seconds, moving ? 1 : 0));
    }
    public static Vector3f translation(String name, Pose pose) {
        return blendedChannel(name, "position", pose).mul(-1, 1, 1);
    }
    private static Vector3f blendedChannel(String name, String channel, Pose pose) {
        float weight = Float.isFinite(pose.walkWeight()) ? Math.max(0, Math.min(1, pose.walkWeight())) : 0;
        return channel(name, channel, false, pose.standSeconds())
                .lerp(channel(name, channel, true, pose.walkSeconds()), weight);
    }

    private static Vector3f channel(String name, String channel, boolean moving, double seconds) {
        JsonObject animation = ANIMATIONS.getAsJsonObject(moving ? "Walk" : "Stand");
        if (animation == null) return new Vector3f();
        JsonObject bones = animation.getAsJsonObject("bones");
        if (bones == null || !bones.has(name)) return new Vector3f();
        JsonElement track = bones.getAsJsonObject(name).get(channel);
        if (track == null) return new Vector3f();
        if (track.isJsonArray()) return vector(track);
        JsonObject keys = track.getAsJsonObject();
        if (keys.has("vector")) return vector(keys.get("vector"));
        List<Double> times = keys.keySet().stream().filter(key -> key.matches("[0-9]+(?:\\.[0-9]+)?"))
                .map(Double::parseDouble).sorted().toList();
        if (times.isEmpty()) return new Vector3f();
        double length = animation.get("animation_length").getAsDouble();
        double time = length > 0 ? Math.floorMod((long) (seconds * 1000000), (long) (length * 1000000)) / 1000000.0 : 0;
        int right = 0;
        while (right < times.size() && times.get(right) < time) right++;
        if (right == 0) return keyVector(keys, times.get(0));
        if (right == times.size()) return keyVector(keys, times.get(right - 1));
        double before = times.get(right - 1), after = times.get(right);
        float t = (float) ((time - before) / Math.max(1.0E-6, after - before));
        Vector3f a = keyVector(keys, before), b = keyVector(keys, after);
        JsonElement key = key(keys, after);
        if (key.isJsonObject() && key.getAsJsonObject().has("lerp_mode")
                && key.getAsJsonObject().get("lerp_mode").getAsString().equals("catmullrom")) {
            boolean loop = animation.has("loop") && animation.get("loop").isJsonPrimitive()
                    && animation.get("loop").getAsBoolean() && times.size() > 2
                    && times.get(0) == 0 && Math.abs(times.get(times.size() - 1) - length) < 1.0E-6
                    && keyVector(keys, times.get(0)).distanceSquared(keyVector(keys, times.get(times.size() - 1))) < 1.0E-6F;
            int previous = Math.max(0, right - 2), next = Math.min(times.size() - 1, right + 1);
            double previousTime = times.get(previous), nextTime = times.get(next);
            if (loop && right == 1) { previous = times.size() - 2; previousTime = times.get(previous) - length; }
            if (loop && right == times.size() - 1) { next = 1; nextTime = times.get(next) + length; }
            Vector3f p = keyVector(keys, times.get(previous));
            Vector3f q = keyVector(keys, times.get(next));
            Vector3f result = new Vector3f();
            for (int axis = 0; axis < 3; axis++) {
                float p0 = p.get(axis), p1 = a.get(axis), p2 = b.get(axis), p3 = q.get(axis);
                // 时间感知的 Catmull-Rom Hermite 形式：关键帧间距不均匀。
                float m1 = (float) ((p2 - p0) * (after - before) / Math.max(1.0E-6, after - previousTime));
                float m2 = (float) ((p3 - p1) * (after - before) / Math.max(1.0E-6, nextTime - before));
                float t2 = t*t, t3 = t2*t;
                result.setComponent(axis, (2*t3-3*t2+1)*p1 + (t3-2*t2+t)*m1
                        + (-2*t3+3*t2)*p2 + (t3-t2)*m2);
            }
            return result;
        }
        return a.lerp(b, t);
    }

    private static JsonElement key(JsonObject keys, double time) {
        for (var entry : keys.entrySet()) if (entry.getKey().matches("[0-9]+(?:\\.[0-9]+)?")
                && Double.parseDouble(entry.getKey()) == time) return entry.getValue();
        return new JsonArray();
    }
    private static Vector3f keyVector(JsonObject keys, double time) {
        JsonElement value = key(keys, time);
        if (value.isJsonObject()) {
            JsonObject object = value.getAsJsonObject();
            value = object.has("post") ? object.get("post") : object.get("vector");
            if (value != null && value.isJsonObject()) value = value.getAsJsonObject().get("vector");
        }
        return vector(value);
    }
    private static Vector3f vector(JsonElement value) {
        if (value == null || !value.isJsonArray() || value.getAsJsonArray().size() != 3) return new Vector3f();
        JsonArray array = value.getAsJsonArray();
        Vector3f result = new Vector3f();
        for (int i = 0; i < 3; i++) {
            try {
                float number = array.get(i).getAsFloat();
                result.setComponent(i, Float.isFinite(number) ? number : 0);
            } catch (RuntimeException ignored) {
                result.setComponent(i, 0);
            }
        }
        return result;
    }

    private static Matrix4f matrix(String name, Matrix4f root, Pose pose, Map<String, Matrix4f> cache) {
        if (cache.containsKey(name)) return cache.get(name);
        JsonObject bone = BONES.get(name);
        if (bone == null) return root;
        Matrix4f parent = bone.has("parent") ? matrix(bone.get("parent").getAsString(), root, pose, cache) : root;
        Vector3f pivot = vector(bone.get("pivot")).mul(-1, 1, 1);
        Vector3f rotation = rotation(name, pose);
        Vector3f translation = translation(name, pose);
        Matrix4f result = new Matrix4f(parent).translate(translation.x, translation.y, translation.z)
                .translate(pivot).rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x).translate(new Vector3f(pivot).negate());
        cache.put(name, result);
        return result;
    }

    public static List<Box> boxes(MartianWalker walker, boolean moving, double seconds) {
        return boxes(walker, new Pose(seconds, seconds, moving ? 1 : 0));
    }
    public static List<Box> boxes(MartianWalker walker, Pose pose) {
        Matrix4f root = new Matrix4f().translate((float) walker.getX(), (float) walker.getY(), (float) walker.getZ())
                .rotateY((float) Math.toRadians(180 - walker.yBodyRot)).scale(SCALE * walker.getScale())
                .translate(0, OFFSET_Y, 0).scale(1.0F / 16);
        Map<String, Matrix4f> cache = new HashMap<>();
        List<Box> boxes = new ArrayList<>();
        for (var entry : BONES.entrySet()) {
            JsonArray cubes = entry.getValue().getAsJsonArray("cubes");
            if (cubes == null) continue;
            for (JsonElement element : cubes) {
                JsonObject cube = element.getAsJsonObject();
                Vector3f origin = vector(cube.get("origin")), size = vector(cube.get("size"));
                Matrix4f transform = new Matrix4f(matrix(entry.getKey(), root, pose, cache));
                if (cube.has("rotation")) {
                    Vector3f pivot = vector(cube.get("pivot")).mul(-1, 1, 1);
                    Vector3f rot = vector(cube.get("rotation")).mul((float) (-Math.PI/180), (float) (-Math.PI/180), (float) (Math.PI/180));
                    transform.translate(pivot).rotateZ(rot.z).rotateY(rot.y).rotateX(rot.x).translate(new Vector3f(pivot).negate());
                }
                Vector3f center = origin.add(new Vector3f(size).mul(0.5F)).mul(-1, 1, 1);
                transform.translate(center);
                boxes.add(new Box(entry.getKey(), transform, size.absolute().mul(0.5F)));
            }
        }
        return boxes;
    }

    /** 带朝向的立方体，包含骨骼与立方体旋转。SAT 可避免在空棱角处造成伤害。 */
    public record Box(String bone, Matrix4f transform, Vector3f halfSize) {
        public AABB bounds() {
            Vector3f min = new Vector3f(Float.POSITIVE_INFINITY), max = new Vector3f(Float.NEGATIVE_INFINITY);
            for (int i = 0; i < 8; i++) {
                Vector3f point = transform.transformPosition(new Vector3f((i&1)==0 ? -halfSize.x : halfSize.x,
                        (i&2)==0 ? -halfSize.y : halfSize.y, (i&4)==0 ? -halfSize.z : halfSize.z));
                min.min(point); max.max(point);
            }
            return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
        }
        public boolean intersects(AABB target) {
            if (!bounds().intersects(target)) return false;
            Vector3f center = transform.getTranslation(new Vector3f());
            Vector3f[] edges = {transform.transformDirection(new Vector3f(halfSize.x,0,0)),
                    transform.transformDirection(new Vector3f(0,halfSize.y,0)), transform.transformDirection(new Vector3f(0,0,halfSize.z))};
            Vector3f[] world = {new Vector3f(1,0,0),new Vector3f(0,1,0),new Vector3f(0,0,1)};
            Vec3 c = target.getCenter();
            Vector3f delta = new Vector3f((float)c.x-center.x,(float)c.y-center.y,(float)c.z-center.z);
            Vector3f h = new Vector3f((float)target.getXsize()/2,(float)target.getYsize()/2,(float)target.getZsize()/2);
            List<Vector3f> axes = new ArrayList<>(List.of(world));
            for (Vector3f edge : edges) {
                axes.add(edge);
                for (Vector3f axis : world) axes.add(new Vector3f(edge).cross(axis));
            }
            for (Vector3f axis : axes) {
                if (axis.lengthSquared() < 1.0E-12) continue;
                float radius = Math.abs(edges[0].dot(axis))+Math.abs(edges[1].dot(axis))+Math.abs(edges[2].dot(axis))
                        + h.x*Math.abs(axis.x)+h.y*Math.abs(axis.y)+h.z*Math.abs(axis.z);
                if (Math.abs(delta.dot(axis)) > radius + 1.0E-5) return false;
            }
            return true;
        }
        public boolean leg() { return bone.matches("(front|left|right)_leg[0-4]"); }
    }
}
