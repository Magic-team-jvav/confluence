package org.confluence.mod.client.summoner.model.geo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * 烘焙后的长方体。面、UV、法线均在 cube 局部空间生成，
 * 渲染时只做 cube pivot 旋转并交给当前骨骼 PoseStack。
 */
final class AnimatedCube {

    private record Vertex(float x, float y, float z, float u, float v) {
    }

    private record Quad(Vertex[] vertices, Vector3f normal) {
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

    private AnimatedCube(
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

    static AnimatedCube bake(
            float[] origin,
            float[] size,
            float[] pivot,
            float[] rotation,
            boolean mirror,
            float inflate,
            float textureWidth,
            float textureHeight,
            JsonElement uvElement
    ) {
        float oy = origin[1] / 16f;
        float oz = origin[2] / 16f;
        float ox = -(origin[0] + size[0]) / 16f;
        float w = size[0] / 16f;
        float h = size[1] / 16f;
        float d = size[2] / 16f;
        float inf = inflate / 16f;

        // 与 GeckoLib VertexSet 命名一致：bl/tl = back/top left 等。
        float[][] v = {
                {ox - inf, oy - inf, oz - inf},                                      // bottomLeftBack
                {ox - inf, oy - inf, oz + d + inf},                                  // bottomRightBack
                {ox - inf, oy + h + inf, oz - inf},                                  // topLeftBack
                {ox - inf, oy + h + inf, oz + d + inf},                              // topRightBack
                {ox + w + inf, oy + h + inf, oz - inf},                              // topLeftFront
                {ox + w + inf, oy + h + inf, oz + d + inf},                          // topRightFront
                {ox + w + inf, oy - inf, oz - inf},                                  // bottomLeftFront
                {ox + w + inf, oy - inf, oz + d + inf}                               // bottomRightFront
        };

        boolean boxUv = uvElement == null || !uvElement.isJsonObject();
        float[] boxUvOrigin = boxUv ? asFloatArray(uvElement != null ? uvElement.getAsJsonArray() : null) : new float[]{0, 0};
        JsonObject uvFaces = boxUv ? null : uvElement.getAsJsonObject();

        List<Quad> quads = new ArrayList<>(6);
        for (Direction direction : Direction.values()) {
            float[] uv;
            float[] uvSize;
            int uvRotation = 0;

            if (boxUv) {
                float[] boxSize = floorSize(size);
                float u = boxUvOrigin[0];
                float vv = boxUvOrigin[1];
                float[] coords = switch (direction) {
                    case WEST -> new float[]{u + boxSize[2] + boxSize[0], vv + boxSize[2], boxSize[2], boxSize[1]};
                    case EAST -> new float[]{u, vv + boxSize[2], boxSize[2], boxSize[1]};
                    case NORTH -> new float[]{u + boxSize[2], vv + boxSize[2], boxSize[0], boxSize[1]};
                    case SOUTH -> new float[]{u + boxSize[2] + boxSize[0] + boxSize[2], vv + boxSize[2], boxSize[0], boxSize[1]};
                    case UP -> new float[]{u + boxSize[2], vv, boxSize[0], boxSize[2]};
                    case DOWN -> new float[]{u + boxSize[2] + boxSize[0], vv + boxSize[2], boxSize[0], -boxSize[2]};
                };
                uv = new float[]{coords[0], coords[1]};
                uvSize = new float[]{coords[2], coords[3]};
            } else {
                JsonObject face = uvFaces.getAsJsonObject(direction.getName());
                if (face == null) {
                    continue;
                }
                uv = asFloatArray(face.has("uv") ? face.get("uv").getAsJsonArray() : null);
                uvSize = asFloatArray(face.has("uv_size") ? face.get("uv_size").getAsJsonArray() : null);
                uvRotation = face.has("uv_rotation") ? face.get("uv_rotation").getAsInt() : 0;
            }

            Vertex[] faceVertices = selectVertices(v, direction, boxUv, mirror);
            float[] normalizedUvs = normalizedFaceUvs(uv, uvSize, uvRotation, mirror, textureWidth, textureHeight);
            Quad quad = new Quad(
                    new Vertex[]{
                            new Vertex(faceVertices[0].x, faceVertices[0].y, faceVertices[0].z, normalizedUvs[0], normalizedUvs[1]),
                            new Vertex(faceVertices[1].x, faceVertices[1].y, faceVertices[1].z, normalizedUvs[2], normalizedUvs[3]),
                            new Vertex(faceVertices[2].x, faceVertices[2].y, faceVertices[2].z, normalizedUvs[4], normalizedUvs[5]),
                            new Vertex(faceVertices[3].x, faceVertices[3].y, faceVertices[3].z, normalizedUvs[6], normalizedUvs[7])
                    },
                    faceNormal(direction, mirror)
            );
            quads.add(quad);
        }

        float pvX = -defaultZero(pivot)[0];
        float pvY = defaultZero(pivot)[1];
        float pvZ = defaultZero(pivot)[2];
        float[] rot = defaultZero(rotation);
        float rX = (float) Math.toRadians(-rot[0]);
        float rY = (float) Math.toRadians(-rot[1]);
        float rZ = (float) Math.toRadians(rot[2]);

        return new AnimatedCube(quads, pvX, pvY, pvZ, rX, rY, rZ, size[0], size[1], size[2]);
    }

    void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color) {
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

    private static Vertex[] selectVertices(float[][] v, Direction direction, boolean boxUv, boolean mirror) {
        return switch (direction) {
            case WEST -> mirror ? toVertices(v[4], v[5], v[7], v[6]) : toVertices(v[3], v[2], v[0], v[1]);
            case EAST -> mirror ? toVertices(v[3], v[2], v[0], v[1]) : toVertices(v[4], v[5], v[7], v[6]);
            case NORTH -> toVertices(v[2], v[4], v[6], v[0]);
            case SOUTH -> toVertices(v[5], v[3], v[1], v[7]);
            case UP -> toVertices(v[3], v[5], v[4], v[2]);
            case DOWN -> mirror && !boxUv ? toVertices(v[3], v[5], v[4], v[2]) : toVertices(v[0], v[6], v[7], v[1]);
        };
    }

    private static Vertex[] toVertices(float[] a, float[] b, float[] c, float[] d) {
        return new Vertex[]{vertex(a), vertex(b), vertex(c), vertex(d)};
    }

    private static Vertex vertex(float[] pos) {
        return new Vertex(pos[0], pos[1], pos[2], 0, 0);
    }

    private static Vector3f faceNormal(Direction direction, boolean mirror) {
        Vector3f normal = direction.step();
        if (mirror) {
            normal.mul(-1, 1, 1);
        }
        return normal;
    }

    private static float[] normalizedFaceUvs(
            float[] uv,
            float[] uvSize,
            int uvRotation,
            boolean mirror,
            float textureWidth,
            float textureHeight
    ) {
        float[] safeUv = defaultZero(uv);
        float[] safeSize = defaultZero(uvSize);
        float right = (safeUv[0] + safeSize[0]) / textureWidth;
        float bottom = (safeUv[1] + safeSize[1]) / textureHeight;
        float left = safeUv[0] / textureWidth;
        float top = safeUv[1] / textureHeight;

        float u0 = left;
        float u1 = right;
        if (!mirror) {
            float tmp = u0;
            u0 = u1;
            u1 = tmp;
        }
        return rotateUvs(u0, top, u1, bottom, uvRotation);
    }

    private static float[] rotateUvs(float u0, float v0, float u1, float v1, int rotation) {
        return switch (((rotation % 360) + 360) % 360) {
            case 90 -> new float[]{u1, v0, u1, v1, u0, v1, u0, v0};
            case 180 -> new float[]{u1, v1, u0, v1, u0, v0, u1, v0};
            case 270 -> new float[]{u0, v1, u0, v0, u1, v0, u1, v1};
            default -> new float[]{u0, v0, u1, v0, u1, v1, u0, v1};
        };
    }

    private static float[] floorSize(float[] size) {
        return new float[]{(float) Math.floor(size[0]), (float) Math.floor(size[1]), (float) Math.floor(size[2])};
    }

    private static float[] asFloatArray(JsonArray array) {
        if (array == null) {
            return new float[]{0, 0};
        }
        float[] result = new float[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).getAsFloat();
        }
        return result;
    }

    private static float[] defaultZero(float[] input) {
        return input != null ? input : new float[]{0, 0, 0};
    }
}
