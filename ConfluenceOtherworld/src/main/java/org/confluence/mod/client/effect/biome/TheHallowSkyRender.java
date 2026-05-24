package org.confluence.mod.client.effect.biome;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.client.ClientConfigs;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TheHallowSkyRender {
    private static class Rainbow {
        float x, y, z;
        float radius; float width;
        int count; boolean reverse;

        Rainbow(float x, float y, float z, float radius, float width, int count, boolean reverse) {
            this.x = x; this.y = y; this.z = z;
            this.radius = radius; this.width = width;
            this.count = count; this.reverse = reverse;
        }
    }

    private static final List<Rainbow> RAINBOWS = new ArrayList<>();

    private static final int[] COLORS = new int[]{0xff1f1f, 0xff851f, 0xfff600, 0x18ff00, 0x00e2ff, 0x0700ff, 0xf000ff};

    private static void rainbowUpdate(int count, Vector3f centerPos, Vec3 playerSpeed, float maxDis) {
        double velX = playerSpeed.x;
        double velZ = playerSpeed.z;
        float radians = (float) (Mth.atan2(velZ, velX));

        Vector2f speed2f = new Vector2f((float) playerSpeed.x, (float) playerSpeed.z);

        RAINBOWS.removeIf(rainbow -> {
            Vector2f toRainbow = new Vector2f(rainbow.x - centerPos.x, rainbow.z - centerPos.z);
            float dis = Mth.sqrt(toRainbow.x * toRainbow.x + toRainbow.y * toRainbow.y);
            return (dis > maxDis) && (speed2f.dot(toRainbow) < 0);
        });

        if (RAINBOWS.size() == count) return;

        if (RAINBOWS.size() > count) {
            Collections.shuffle(RAINBOWS);
            RAINBOWS.subList(count, RAINBOWS.size()).clear();
            return;
        }

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            RandomSource random = level.random;
            while (RAINBOWS.size() < count) {
                float rotate = (random.nextFloat() - 0.5F) * Mth.PI * 0.75F + radians;
                float x = Mth.sin(rotate) * maxDis + centerPos.x;
                float z = Mth.cos(rotate) * maxDis + centerPos.z;
                float y = level.getSeaLevel();

                BlockPos pos = new BlockPos((int) x, 0, (int) z);
                if (level.isLoaded(pos)) {
                    y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
                }
                RAINBOWS.add(new Rainbow(x, y, z, random.nextFloat() * 40 + 60, random.nextFloat() * 25 + 25, (random.nextFloat() < 0.1) ? 2 : 1, (random.nextFloat() < 0.01)));
            }
        }
    }

    public static void render(LocalPlayer player, RenderLevelStageEvent event, float alphaMul) {
        if (alphaMul < 0.01F) return;

        boolean change = ClientConfigs.rainbowGradient;
        int rainbowCount = ClientConfigs.rainbowCount;

        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix());
        Matrix4f matrix4f = poseStack.last().pose();

        float playerX = (float) player.getX();
        float playerY = (float) player.getY();
        float playerZ = (float) player.getZ();
        Vec3 deltaMovement = player.getDeltaMovement();

        float midDis = 200F;
        float disRange = 100F;

        if (deltaMovement.x != 0 || deltaMovement.z != 0) rainbowUpdate(rainbowCount, new Vector3f(playerX, playerY, playerZ), deltaMovement, midDis + disRange);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int colorCount = COLORS.length;
        int step = 48;
        float stepRotate = Mth.PI * 2 / (float) step;

        for (Rainbow rainbow : RAINBOWS){
            int count = rainbow.count;
            boolean reverse = rainbow.reverse;
            float radius = rainbow.radius;
            float width = rainbow.width;
            float radiusStep = width / (float) colorCount;
            float offsetX = rainbow.x - playerX, offsetY = rainbow.y - playerY, offsetZ = rainbow.z - playerZ;
            float dis = Mth.sqrt(offsetX * offsetX + offsetZ * offsetZ), mX = -offsetZ / dis, mZ = offsetX / dis;

            float disN = (dis - midDis) / disRange;
            int alpha = Math.max(0, (int) (alphaMul * 255 * 0.25 * (1 - disN * disN)));

            for (int i = 0; i < count; i++) {
                float maxRadius = radius + width * 0.5F - radiusStep * 0.5F;
                for (int j = 0; j < (colorCount + 1); j++) {
                    boolean outermost = (j == 0), innermost = (j == colorCount);
                    float outerRadius = maxRadius - j * radiusStep + radiusStep * 0.5F;
                    float innerRadius = maxRadius - j * radiusStep - radiusStep * 0.5F;

                    int color1 = outermost ? 0xFFFFFF : (reverse ? COLORS[colorCount - j] : COLORS[j - 1]);
                    int color2 = color1;
                    if (change) {
                        color2 = reverse ? (innermost ? COLORS[0] : COLORS[colorCount - j - 1]) : (innermost ? COLORS[colorCount - 1] : COLORS[j]);
                    }

                    drawRoll(step, stepRotate, outerRadius, innerRadius, builder, matrix4f, mX, mZ, offsetX, offsetY, offsetZ, color1, color2, alpha, change ? (innermost ? 0 : alpha) : alpha);
                }
                drawRoll(step, stepRotate, (radius + width) * 1.5F, maxRadius + radiusStep * 0.5F, builder, matrix4f, mX, mZ, offsetX, offsetY, offsetZ, 0xFFFFFF, 0xFFFFFF, 0, alpha);
                radius += (width + 10);
            }
        }

        MeshData data = builder.build();
        if (data != null) {
            BufferUploader.drawWithShader(data);
        }
    }

    public static void drawRoll(int step, float stepRotate, float outerRadius, float innerRadius,
                                BufferBuilder builder, Matrix4f matrix4f, float mX, float mZ,
                                float offsetX, float offsetY, float offsetZ,
                                int color1, int color2, int alpha1, int alpha2) {
        int r1 = (color1 >> 16) & 0xFF, r2 = (color2 >> 16) & 0xFF;
        int g1 = (color1 >> 8)  & 0xFF, g2 = (color2 >> 8)  & 0xFF;
        int b1 =  color1        & 0xFF, b2 =  color2        & 0xFF;
        Vector2f outerPoint = null;
        Vector2f innerPoint = null;

        for (int i = 0; i < step; i++) {
            float fRotate = i * stepRotate, sRotate = fRotate + stepRotate;
            float w1, y1;
            float w2, y2;
            if (outerPoint == null) {
                w1 = Mth.sin(fRotate) * outerRadius;
                y1 = Mth.cos(fRotate) * outerRadius;
                w2 = Mth.sin(fRotate) * innerRadius;
                y2 = Mth.cos(fRotate) * innerRadius;
            } else {
                w1 = outerPoint.x;
                y1 = outerPoint.y;
                w2 = innerPoint.x;
                y2 = innerPoint.y;
            }
            float w3 = Mth.sin(sRotate) * innerRadius, y3 = Mth.cos(sRotate) * innerRadius;
            float w4 = Mth.sin(sRotate) * outerRadius, y4 = Mth.cos(sRotate) * outerRadius;

            builder.addVertex(matrix4f, w1 * mX + offsetX, y1 + offsetY, w1 * mZ + offsetZ).setColor(r1, g1, b1, alpha1);
            builder.addVertex(matrix4f, w2 * mX + offsetX, y2 + offsetY, w2 * mZ + offsetZ).setColor(r2, g2, b2, alpha2);
            builder.addVertex(matrix4f, w3 * mX + offsetX, y3 + offsetY, w3 * mZ + offsetZ).setColor(r2, g2, b2, alpha2);
            builder.addVertex(matrix4f, w4 * mX + offsetX, y4 + offsetY, w4 * mZ + offsetZ).setColor(r1, g1, b1, alpha1);

            outerPoint = new Vector2f(w4, y4);
            innerPoint = new Vector2f(w3, y3);
        }
    }
}
