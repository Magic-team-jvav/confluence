package org.confluence.mod.client.summoner.model.bbmodel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

final class BBModelCube {

    private record Vertex(float x, float y, float z, float u, float v) {
    }

    private record Quad(Vertex[] vertices, Vector3f normal, int textureIndex) {
    }

    private final List<Quad> quads = new ArrayList<>(6);
    private final float pivotX;
    private final float pivotY;
    private final float pivotZ;
    private final float rotX;
    private final float rotY;
    private final float rotZ;
    private final float sizeX;
    private final float sizeY;
    private final float sizeZ;

    private BBModelCube(
            List<Quad> quads,
            float pivotX,
            float pivotY,
            float pivotZ,
            float rotX,
            float rotY,
            float rotZ,
            float sizeX,
            float sizeY,
            float sizeZ
    ) {
        this.quads.addAll(quads);
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.pivotZ = pivotZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    static BBModelCube bake(
            JsonObject element,
            float textureWidth,
            float textureHeight,
            boolean inheritedMirror,
            Map<String, Integer> faceTextures
    ) {
        float[] from = vector(element, "from", new float[]{0, 0, 0});
        float[] to = vector(element, "to", new float[]{0, 0, 0});
        float[] baseMin = new float[3];
        float[] baseMax = new float[3];
        float[] baseSize = new float[3];
        for (int axis = 0; axis < 3; axis++) {
            baseMin[axis] = Math.min(from[axis], to[axis]);
            baseMax[axis] = Math.max(from[axis], to[axis]);
            baseSize[axis] = baseMax[axis] - baseMin[axis];
        }

        float inflate = number(element, "inflate", 0f);
        float[] stretch = vector(element, "stretch", new float[]{1, 1, 1});
        float[] min = baseMin.clone();
        float[] max = baseMax.clone();
        for (int axis = 0; axis < 3; axis++) {
            float center = (baseMin[axis] + baseMax[axis]) * 0.5f;
            float half = (baseSize[axis] * 0.5f + inflate) * stretch[axis];
            min[axis] = center - half;
            max[axis] = center + half;
        }

        float[][] vertices = {
                {min[0] / 16f, min[1] / 16f, min[2] / 16f},
                {min[0] / 16f, min[1] / 16f, max[2] / 16f},
                {min[0] / 16f, max[1] / 16f, min[2] / 16f},
                {min[0] / 16f, max[1] / 16f, max[2] / 16f},
                {max[0] / 16f, max[1] / 16f, min[2] / 16f},
                {max[0] / 16f, max[1] / 16f, max[2] / 16f},
                {max[0] / 16f, min[1] / 16f, min[2] / 16f},
                {max[0] / 16f, min[1] / 16f, max[2] / 16f}
        };

        boolean boxUv = element.has("box_uv") && element.get("box_uv").getAsBoolean();
        boolean mirror = inheritedMirror;
        if (element.has("mirror_uv")) {
            mirror ^= element.get("mirror_uv").getAsBoolean();
        }
        float[] uvOffset = vector2(element, "uv_offset", new float[]{0, 0});
        JsonObject faces = element.has("faces") && element.get("faces").isJsonObject()
                ? element.getAsJsonObject("faces")
                : new JsonObject();

        List<Quad> quads = new ArrayList<>(6);
        for (Direction direction : Direction.values()) {
            float[][] faceVertices = selectVertices(vertices, direction);
            float[] uvRect;
            float[] localMin;
            float[] localMax;
            int uvRotation = 0;
            int textureIndex = faceTextures.getOrDefault(direction.getName(), boxUv ? 0 : -1);
            if (textureIndex < 0) {
                continue;
            }

            if (boxUv) {
                uvRect = boxUvRect(direction, floorSize(baseSize), uvOffset, mirror);
                localMin = min;
                localMax = max;
            } else {
                if (!faces.has(direction.getName()) || !faces.get(direction.getName()).isJsonObject()) {
                    continue;
                }
                JsonObject face = faces.getAsJsonObject(direction.getName());
                uvRect = faceUvRect(face);
                if (uvRect == null) {
                    continue;
                }
                uvRotation = face.has("rotation") ? face.get("rotation").getAsInt() : 0;
                localMin = min;
                localMax = max;
            }

            Vertex[] quadVertices = new Vertex[4];
            for (int i = 0; i < 4; i++) {
                float[] position = faceVertices[i];
                float[] local = localUvPosition(direction, position, localMin, localMax);
                rotateLocal(local, uvRotation);
                float u = lerp(uvRect[0], uvRect[2], local[0]) / textureWidth;
                float v = lerp(uvRect[1], uvRect[3], local[1]) / textureHeight;
                quadVertices[i] = new Vertex(position[0], position[1], position[2], u, v);
            }
            quads.add(new Quad(quadVertices, direction.step(), textureIndex));
        }

        float[] pivot = vector(element, "origin", new float[]{0, 0, 0});
        float[] rotation = vector(element, "rotation", new float[]{0, 0, 0});
        return new BBModelCube(
                quads,
                pivot[0],
                pivot[1],
                pivot[2],
                (float) Math.toRadians(rotation[0]),
                (float) Math.toRadians(rotation[1]),
                (float) Math.toRadians(rotation[2]),
                max[0] - min[0],
                max[1] - min[1],
                max[2] - min[2]
        );
    }

    void render(
            PoseStack poseStack,
            IntFunction<VertexConsumer> consumers,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        poseStack.pushPose();
        poseStack.translate(pivotX / 16f, pivotY / 16f, pivotZ / 16f);
        if (rotZ != 0) {
            poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, rotZ));
        }
        if (rotY != 0) {
            poseStack.mulPose(new Quaternionf().rotationXYZ(0, rotY, 0));
        }
        if (rotX != 0) {
            poseStack.mulPose(new Quaternionf().rotationXYZ(rotX, 0, 0));
        }
        poseStack.translate(-pivotX / 16f, -pivotY / 16f, -pivotZ / 16f);

        var pose = poseStack.last();
        for (Quad quad : quads) {
            VertexConsumer consumer = consumers.apply(quad.textureIndex);
            Vector3f normal = new Vector3f(quad.normal).mul(pose.normal());
            fixInvertedFlatCube(normal);
            for (Vertex vertex : quad.vertices) {
                Vector3f position = pose.pose().transformPosition(vertex.x, vertex.y, vertex.z, new Vector3f());
                consumer.vertex(position.x(), position.y(), position.z())
                        .color(color)
                        .uv(vertex.u, vertex.v)
                        .overlayCoords(packedOverlay)
                        .uv2(packedLight)
                        .normal(normal.x(), normal.y(), normal.z())
                        .endVertex();
            }
        }
        poseStack.popPose();
    }

    private void fixInvertedFlatCube(Vector3f normal) {
        if (normal.x() < 0 && (sizeY == 0 || sizeZ == 0)) {
            normal.mul(-1, 1, 1);
        }
        if (normal.y() < 0 && (sizeX == 0 || sizeZ == 0)) {
            normal.mul(1, -1, 1);
        }
        if (normal.z() < 0 && (sizeX == 0 || sizeY == 0)) {
            normal.mul(1, 1, -1);
        }
    }

    private static float[][] selectVertices(float[][] v, Direction direction) {
        return switch (direction) {
            case WEST -> new float[][]{v[3], v[2], v[0], v[1]};
            case EAST -> new float[][]{v[4], v[5], v[7], v[6]};
            case NORTH -> new float[][]{v[2], v[4], v[6], v[0]};
            case SOUTH -> new float[][]{v[5], v[3], v[1], v[7]};
            case UP -> new float[][]{v[3], v[5], v[4], v[2]};
            case DOWN -> new float[][]{v[0], v[6], v[7], v[1]};
        };
    }

    private static float[] localUvPosition(Direction direction, float[] position, float[] min, float[] max) {
        float rangeX = max[0] - min[0];
        float rangeY = max[1] - min[1];
        float rangeZ = max[2] - min[2];
        float x = ratio(position[0] * 16f - min[0], rangeX);
        float y = ratio(max[1] - position[1] * 16f, rangeY);
        float z = ratio(position[2] * 16f - min[2], rangeZ);
        float zFromMax = ratio(max[2] - position[2] * 16f, rangeZ);
        float xFromMax = ratio(max[0] - position[0] * 16f, rangeX);
        return switch (direction) {
            case EAST -> new float[]{zFromMax, y};
            case WEST -> new float[]{z, y};
            case NORTH -> new float[]{xFromMax, y};
            case SOUTH -> new float[]{x, y};
            case UP -> new float[]{x, z};
            case DOWN -> new float[]{x, zFromMax};
        };
    }

    private static float ratio(float value, float range) {
        return range <= 1.0E-6f ? 0.5f : value / range;
    }

    private static void rotateLocal(float[] local, int rotation) {
        int steps = ((rotation % 360) + 360) % 360 / 90;
        for (int i = 0; i < steps; i++) {
            float x = local[0];
            local[0] = 1 - local[1];
            local[1] = x;
        }
    }

    private static float[] boxUvRect(Direction direction, float[] size, float[] offset, boolean mirror) {
        Direction uvDirection = direction;
        if (mirror) {
            if (direction == Direction.EAST) {
                uvDirection = Direction.WEST;
            } else if (direction == Direction.WEST) {
                uvDirection = Direction.EAST;
            }
        }

        float u = offset[0];
        float v = offset[1];
        float dx = size[0];
        float dy = size[1];
        float dz = size[2];
        float[] rect = switch (uvDirection) {
            case EAST -> new float[]{u, v + dz, u + dz, v + dz + dy};
            case WEST -> new float[]{u + dz + dx, v + dz, u + dz + dx + dz, v + dz + dy};
            case UP -> new float[]{u + dz + dx, v + dz, u + dz, v};
            case DOWN -> new float[]{u + dz + dx * 2, v, u + dz + dx, v + dz};
            case SOUTH -> new float[]{u + dz * 2 + dx, v + dz, u + dz * 2 + dx + dx, v + dz + dy};
            case NORTH -> new float[]{u + dz, v + dz, u + dz + dx, v + dz + dy};
        };
        if (mirror) {
            float width = rect[2] - rect[0];
            rect[0] += width;
            rect[2] -= width;
        }
        return rect;
    }

    private static float[] faceUvRect(JsonObject face) {
        if (!face.has("uv") || !face.get("uv").isJsonArray()) {
            return null;
        }
        JsonArray uv = face.getAsJsonArray("uv");
        if (uv.size() < 4) {
            return null;
        }
        float[] rect = new float[]{
                uv.get(0).getAsFloat(),
                uv.get(1).getAsFloat(),
                uv.get(2).getAsFloat(),
                uv.get(3).getAsFloat()
        };
        if (face.has("uv_size") && face.get("uv_size").isJsonArray()) {
            JsonArray size = face.getAsJsonArray("uv_size");
            if (size.size() >= 2) {
                rect[2] = rect[0] + size.get(0).getAsFloat();
                rect[3] = rect[1] + size.get(1).getAsFloat();
            }
        }
        return rect;
    }

    private static float[] floorSize(float[] size) {
        return new float[]{(float) Math.floor(size[0]), (float) Math.floor(size[1]), (float) Math.floor(size[2])};
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

    private static float[] vector2(JsonObject object, String key, float[] fallback) {
        if (!object.has(key) || !object.get(key).isJsonArray()) {
            return fallback;
        }
        JsonArray array = object.getAsJsonArray(key);
        if (array.size() < 2) {
            return fallback;
        }
        try {
            return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat()};
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static float lerp(float start, float end, float delta) {
        return start + (end - start) * delta;
    }
}
