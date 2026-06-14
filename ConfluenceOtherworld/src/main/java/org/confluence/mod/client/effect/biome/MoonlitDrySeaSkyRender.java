package org.confluence.mod.client.effect.biome;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.confluence.lib.color.IntegerRGB;
import org.confluence.mod.client.event.ModClientSetups;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoonlitDrySeaSkyRender {
    private static class BubblePoint {
        float x, z;
        float vx, vz;

        BubblePoint(float x, float z) {
            this.x = x;
            this.z = z;
            this.vx = 0;
            this.vz = 0;
        }
    }

    private static final List<BubblePoint> points = new ArrayList<>();
    private static boolean initialized = false;

    private static void updatePhysics(float side, float deltaTime) {
        if (points.isEmpty()) return;

        float repelRadius = 60.0F;
        float repelStrength = 500.0F;
        float damping = 0.95F;
        float maxSpeed = 15.0F;

        float flowStrength = 100.0F;
        long gameTime = 0;
        if (Minecraft.getInstance().level != null) {
            gameTime = Minecraft.getInstance().level.getGameTime();
        }
        float time = gameTime * 0.01F;

        for (int i = 0; i < points.size(); i++) {
            BubblePoint p1 = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                BubblePoint p2 = points.get(j);

                float dx = p2.x - p1.x;
                float dz = p2.z - p1.z;
                float distSq = dx * dx + dz * dz;
                float dist = (float) Math.sqrt(distSq);

                if (dist < repelRadius && dist > 0.1F) {
                    float force = (1.0F - dist / repelRadius) * repelStrength;
                    float fx = (dx / dist) * force;
                    float fz = (dz / dist) * force;

                    p1.vx -= fx * deltaTime;
                    p1.vz -= fz * deltaTime;
                    p2.vx += fx * deltaTime;
                    p2.vz += fz * deltaTime;
                }
            }
        }

        for (BubblePoint p : points) {

            float flowX = (float) Math.cos(p.z * 0.05 + time) * flowStrength;
            float flowZ = (float) Math.sin(p.x * 0.05 + time * 0.8) * flowStrength;

            p.vx += flowX * deltaTime;
            p.vz += flowZ * deltaTime;

            p.vx *= damping;
            p.vz *= damping;

            float speed = (float) Math.sqrt(p.vx * p.vx + p.vz * p.vz);
            if (speed > maxSpeed) {
                p.vx = (p.vx / speed) * maxSpeed;
                p.vz = (p.vz / speed) * maxSpeed;
            }

            p.x += p.vx * deltaTime;
            p.z += p.vz * deltaTime;

            float margin = 5.0F;
            if (p.x < -side + margin) {
                p.vx += repelStrength * 0.5F * deltaTime;
                if (p.x < -side) p.x = -side;
            }
            if (p.x > side - margin) {
                p.vx -= repelStrength * 0.5F * deltaTime;
                if (p.x > side) p.x = side;
            }
            if (p.z < -side + margin) {
                p.vz += repelStrength * 0.5F * deltaTime;
                if (p.z < -side) p.z = -side;
            }
            if (p.z > side - margin) {
                p.vz -= repelStrength * 0.5F * deltaTime;
                if (p.z > side) p.z = side;
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static int threeColor(IntegerRGB colorA, IntegerRGB colorB, IntegerRGB colorC, float time, float cycle) {
        float progress = (time % cycle) / cycle;

        float phase = progress * 3.0F;

        if (phase < 1.0F) {
            return colorA.mixture(colorB, phase).get();

        } else if (phase < 2.0F) {
            float blendRatio = phase - 1.0F;
            return colorB.mixture(colorC, blendRatio).get();

        } else {
            float blendRatio = phase - 2.0F;
            return colorC.mixture(colorA, blendRatio).get();
        }
    }

    public static void render(LocalPlayer player, PortRenderLevelStageEvent event, float alphaMul) {
        if (alphaMul < 0.01F) return;

        long gameTime = 0;
        if (Minecraft.getInstance().level != null) {
            gameTime = Minecraft.getInstance().level.getGameTime();
        }
        float time = event.getPartialTick().getGameTimeDeltaPartialTick(false) + gameTime;

        int dreamBubbleColor = threeColor(ModClientSetups.DREAM_BUBBLE_A, ModClientSetups.DREAM_BUBBLE_B, ModClientSetups.DREAM_BUBBLE_C, time, 500);

        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix().getUnnormalizedRotation(new Quaternionf()));
        Matrix4f matrix4f = poseStack.last().pose();

        int alpha = (int) (alphaMul * 255 * 0.5);
        int r = (dreamBubbleColor >> 16) & 0xFF;
        int g = (dreamBubbleColor >> 8) & 0xFF;
        int b = dreamBubbleColor & 0xFF;

        float playerY = (float) player.getY();
        float fixedWorldY = 250;
        float renderY = fixedWorldY - playerY;

        float side = 200F;
        float side2_3 = side * 0.667F;
        int cellCount = 48;

        if (!initialized) {
            for (int i = 0; i < cellCount; i++) {
                long seed = (long) i * 73856093L ^ ((long) i + 1337L) * 19349663L;
                seed = (seed ^ (seed >> 13)) * 1274126177L;
                seed = seed ^ (seed >> 16);

                float randX = ((seed & 0xFFFF) / 65535.0F);
                float randZ = (((seed >> 16) & 0xFFFF) / 65535.0F);

                float x = -side + randX * (2 * side);
                float z = -side + randZ * (2 * side);
                points.add(new BubblePoint(x, z));
            }
            initialized = true;
        }

        float deltaTime = (Minecraft.getInstance().getFrameTimeNs() / 1_000_000_000F) * 2F;
        updatePhysics(side, deltaTime);

        float[] seedX = new float[cellCount];
        float[] seedZ = new float[cellCount];
        for (int i = 0; i < cellCount; i++) {
            seedX[i] = points.get(i).x;
            seedZ[i] = points.get(i).z;
        }

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Set<Long> edges = new HashSet<>();
        List<int[]> edgeList = new ArrayList<>();
        for (int i = 0; i < cellCount; i++) {
            for (int j = i + 1; j < cellCount; j++) {
                long key = Math.min(i, j) | ((long) Math.max(i, j) << 32);
                if (!edges.contains(key)) {
                    edges.add(key);
                    edgeList.add(new int[]{i, j});
                }
            }
        }

        List<List<float[]>> cellCorners = new ArrayList<>();
        for (int i = 0; i < cellCount; i++) cellCorners.add(new ArrayList<>());

        for (int[] edge : edgeList) {
            int i = edge[0], j = edge[1];
            for (int k = 0; k < cellCount; k++) {
                if (k == i || k == j) continue;
                float cx = circumCenterX(seedX[i], seedZ[i], seedX[j], seedZ[j], seedX[k], seedZ[k]);
                if (Float.isNaN(cx)) continue;

                float cz = circumCenterZ(seedX[i], seedZ[i], seedX[j], seedZ[j], seedX[k], seedZ[k]);
                float distI = (cx - seedX[i]) * (cx - seedX[i]) + (cz - seedZ[i]) * (cz - seedZ[i]);
                boolean isValid = true;
                for (int m = 0; m < cellCount; m++) {
                    if (m == i || m == j || m == k) continue;
                    float distM = (cx - seedX[m]) * (cx - seedX[m]) + (cz - seedZ[m]) * (cz - seedZ[m]);
                    if (distM < distI - 0.001f) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    cellCorners.get(i).add(new float[]{cx, cz});
                    cellCorners.get(j).add(new float[]{cx, cz});
                }
            }
        }

        for (int i = 0; i < cellCount; i++) {
            List<float[]> corners = cellCorners.get(i);
            final float finalSeedX = seedX[i];
            final float finalSeedZ = seedZ[i];
            corners.sort((a, b1) -> {
                float angleA = (float) Math.atan2(a[1] - finalSeedZ, a[0] - finalSeedX);
                float angleB = (float) Math.atan2(b1[1] - finalSeedZ, b1[0] - finalSeedX);
                return Float.compare(angleA, angleB);
            });
        }

        float lineWidth = 2F;
        float wallHeight = 80F;

        for (int i = 0; i < cellCount; i++) {
            List<float[]> corners = cellCorners.get(i);
            if (corners.size() < 2) continue;

            for (int j = 0; j < corners.size(); j++) {
                float[] p1 = corners.get(j);
                float[] p2 = corners.get((j + 1) % corners.size());

                float x1 = p1[0], z1 = p1[1];
                float x2 = p2[0], z2 = p2[1];

                float dx = x2 - x1;
                float dz = z2 - z1;
                float len = (float) Math.sqrt(dx * dx + dz * dz);
                if (len < 0.001F) continue;

                float nx = (-dz / len) * lineWidth;
                float nz = (dx / len) * lineWidth;

                builder.vertex(matrix4f, x1 + nx, renderY, z1 + nz)
                        .color(r, g, b, calculateAlpha(x1 + nx, z1 + nz, alpha, side2_3));
                builder.vertex(matrix4f, x1 - nx, renderY, z1 - nz)
                        .color(r, g, b, calculateAlpha(x1 - nx, z1 - nz, alpha, side2_3));
                builder.vertex(matrix4f, x2 - nx, renderY, z2 - nz)
                        .color(r, g, b, calculateAlpha(x2 - nx, z2 - nz, alpha, side2_3));
                builder.vertex(matrix4f, x2 + nx, renderY, z2 + nz)
                        .color(r, g, b, calculateAlpha(x2 + nx, z2 + nz, alpha, side2_3));

                float topY = renderY + wallHeight;

                builder.vertex(matrix4f, x1, renderY, z1)
                        .color(r, g, b, calculateAlpha(x1, z1, alpha, side2_3));
                builder.vertex(matrix4f, x2, renderY, z2)
                        .color(r, g, b, calculateAlpha(x2, z2, alpha, side2_3));
                builder.vertex(matrix4f, x2, topY, z2)
                        .color(r, g, b, 0);
                builder.vertex(matrix4f, x1, topY, z1)
                        .color(r, g, b, 0);
            }
        }

        BufferUploader.drawWithShader(builder.end());
    }

    private static float circumCenterX(float x1, float z1, float x2, float z2, float x3, float z3) {
        float d = 2 * (x1 * (z2 - z3) + x2 * (z3 - z1) + x3 * (z1 - z2));
        if (Math.abs(d) < 0.0001F) return Float.NaN;
        return ((x1 * x1 + z1 * z1) * (z2 - z3) + (x2 * x2 + z2 * z2) * (z3 - z1) + (x3 * x3 + z3 * z3) * (z1 - z2)) / d;
    }

    private static float circumCenterZ(float x1, float z1, float x2, float z2, float x3, float z3) {
        float d = 2 * (x1 * (z2 - z3) + x2 * (z3 - z1) + x3 * (z1 - z2));
        if (Math.abs(d) < 0.0001F) return Float.NaN;
        return ((x1 * x1 + z1 * z1) * (x3 - x2) + (x2 * x2 + z2 * z2) * (x1 - x3) + (x3 * x3 + z3 * z3) * (x2 - x1)) / d;
    }

    private static int calculateAlpha(float x, float z, int baseAlpha, float side) {
        float disSq = x * x + z * z;
        float sideSq = side * side;
        float fadeOut = 1.0F - (disSq / sideSq);
        fadeOut = Math.max(0.0F, Math.min(1.0F, fadeOut));
        return (int) (baseAlpha * fadeOut);
    }
}
