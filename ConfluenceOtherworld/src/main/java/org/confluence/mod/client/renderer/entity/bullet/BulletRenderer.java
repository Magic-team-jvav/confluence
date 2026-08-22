package org.confluence.mod.client.renderer.entity.bullet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class BulletRenderer extends EntityRenderer<BaseBulletEntity> {
    private static final double EPSILON = 1.0E-7D;
    private static final double HEAD_FADE_START_DISTANCE = 0.90D;
    private static final double HEAD_FULL_DISTANCE = 1.80D;
    private static final Vec3 UP = new Vec3(0.0D, 1.0D, 0.0D);
    private static final Vec3 RIGHT = new Vec3(1.0D, 0.0D, 0.0D);

    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull BaseBulletEntity entity) {
        return BuiltInRegistries.ITEM.getKey(entity.getBulletStack().getItem());
    }

    @Override
    public void render(BaseBulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        String bulletId = entity.getColorID();
        BulletTrailStyle style = BulletTrailStyles.get(bulletId);
        int color = GunTrailColors.getColor(bulletId);
        boolean chlorophyte = "chlorophyte_bullet".equals(bulletId);
        if (chlorophyte) {
            color = scaleRgb(color, chlorophytePulse(entity, partialTick));
        }

        Vec3 renderPosition = interpolatedPosition(entity, partialTick);
        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        renderTrail(entity.getTrails(), renderPosition, cameraPosition, poseStack, bufferSource, color, style, chlorophyte ? 0.65F : 2.0F);

        float headVisibility = headVisibility(renderPosition, cameraPosition);
        if (headVisibility > 0.0F) {
            renderHead(renderPosition, cameraPosition, poseStack, bufferSource, color, style, headVisibility);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private static void renderTrail(List<Vec3> trails, Vec3 entityPosition, Vec3 cameraPosition, PoseStack poseStack, MultiBufferSource bufferSource, int color, BulletTrailStyle style, float fadePower) {
        List<Vec3> points = TrailPathSmoother.smooth(trails, entityPosition, style.maxPoints());
        int pointCount = points.size();
        if (pointCount < 2) return;

        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        float colorOpacity = FastColor.ARGB32.alpha(color) / 255.0F;
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(BulletRenderTypes.trail(style.trailTexture(), style.additive()));

        double[] distance = cumulativeDistance(points);
        double totalDistance = distance[pointCount - 1];
        if (totalDistance <= EPSILON) return;

        Vec3[] sides = ribbonSides(points, cameraPosition);
        float[] progress = new float[pointCount];
        float[] widths = new float[pointCount];
        int[] colors = new int[pointCount];
        for (int index = 0; index < pointCount; index++) {
            progress[index] = (float) (distance[index] / totalDistance);
            widths[index] = widthAt(style, progress[index]);
            colors[index] = colorAt(red, green, blue, colorOpacity, style.opacity(), progress[index], fadePower);
        }

        for (int index = 1; index < pointCount; index++) {
            Vec3 relativeStart = points.get(index - 1).subtract(entityPosition);
            Vec3 relativeEnd = points.get(index).subtract(entityPosition);
            Vec3 startOffset = sides[index - 1].scale(widths[index - 1] * 0.5D);
            Vec3 endOffset = sides[index].scale(widths[index] * 0.5D);
            float startV = 1.0F - progress[index - 1];
            float endV = 1.0F - progress[index];

            addVertex(buffer, matrix, relativeStart.add(startOffset), colors[index - 1], 0.0F, startV);
            addVertex(buffer, matrix, relativeStart.subtract(startOffset), colors[index - 1], 1.0F, startV);
            addVertex(buffer, matrix, relativeEnd.subtract(endOffset), colors[index], 1.0F, endV);
            addVertex(buffer, matrix, relativeEnd.add(endOffset), colors[index], 0.0F, endV);
        }
    }

    private static double[] cumulativeDistance(List<Vec3> points) {
        double[] result = new double[points.size()];
        for (int index = 1; index < points.size(); index++) {
            result[index] = result[index - 1] + points.get(index - 1).distanceTo(points.get(index));
        }
        return result;
    }

    private static Vec3[] ribbonSides(List<Vec3> points, Vec3 cameraPosition) {
        Vec3[] result = new Vec3[points.size()];
        Vec3 previousSide = null;
        for (int index = 0; index < points.size(); index++) {
            Vec3 before = points.get(Math.max(0, index - 1));
            Vec3 after = points.get(Math.min(points.size() - 1, index + 1));
            Vec3 tangent = after.subtract(before).normalize();
            Vec3 side = tangent.cross(cameraPosition.subtract(points.get(index)));
            if (side.lengthSqr() <= EPSILON) {
                side = tangent.cross(Math.abs(tangent.y) < 0.95D ? UP : RIGHT);
            }
            side = side.normalize();
            if (previousSide != null && side.dot(previousSide) < 0.0D) {
                side = side.scale(-1.0D);
            }
            result[index] = side;
            previousSide = side;
        }
        return result;
    }

    private static void renderHead(Vec3 entityPosition, Vec3 cameraPosition, PoseStack poseStack, MultiBufferSource bufferSource, int color, BulletTrailStyle style, float visibility) {
        int alpha = Math.round(255.0F * style.opacity() * visibility * FastColor.ARGB32.alpha(color) / 255.0F);
        int headColor = FastColor.ARGB32.color(alpha, FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        renderSprite(entityPosition, entityPosition, cameraPosition, poseStack, bufferSource, style.headTexture(), headColor, style.headSize() * (0.65F + visibility * 0.35F), style.additive());
    }

    private static float headVisibility(Vec3 entityPosition, Vec3 cameraPosition) {
        double distance = entityPosition.distanceTo(cameraPosition);
        return Mth.clamp((float) ((distance - HEAD_FADE_START_DISTANCE) / (HEAD_FULL_DISTANCE - HEAD_FADE_START_DISTANCE)), 0.0F, 1.0F);
    }

    private static void renderSprite(Vec3 worldPosition, Vec3 entityPosition, Vec3 cameraPosition, PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, int color, float size, boolean additive) {
        Vec3 facing = cameraPosition.subtract(worldPosition);
        if (facing.lengthSqr() <= EPSILON) return;
        facing = facing.normalize();

        Vec3 up = Math.abs(facing.dot(UP)) > 0.98D ? RIGHT : UP;
        Vec3 right = facing.cross(up).normalize();
        up = right.cross(facing).normalize();
        double halfSize = size * 0.5D;
        Vec3 relativeCenter = worldPosition.subtract(entityPosition);
        Vec3 rightOffset = right.scale(halfSize);
        Vec3 upOffset = up.scale(halfSize);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(BulletRenderTypes.trail(texture, additive));
        addVertex(buffer, matrix, relativeCenter.subtract(rightOffset).subtract(upOffset), color, 0.0F, 1.0F);
        addVertex(buffer, matrix, relativeCenter.add(rightOffset).subtract(upOffset), color, 1.0F, 1.0F);
        addVertex(buffer, matrix, relativeCenter.add(rightOffset).add(upOffset), color, 1.0F, 0.0F);
        addVertex(buffer, matrix, relativeCenter.subtract(rightOffset).add(upOffset), color, 0.0F, 0.0F);
    }

    private static float widthAt(BulletTrailStyle style, float progress) {
        float clamped = Mth.clamp(progress, 0.0F, 1.0F);
        float eased = 1.0F - (1.0F - clamped) * (1.0F - clamped);
        return Mth.lerp(eased, style.tailWidth(), style.headWidth());
    }

    private static int colorAt(int red, int green, int blue, float baseOpacity, float opacity, float progress, float fadePower) {
        float eased = Mth.clamp(progress, 0.0F, 1.0F);
        int alpha = Math.round(255.0F * baseOpacity * opacity * (float) Math.pow(eased, fadePower));
        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    private static float chlorophytePulse(BaseBulletEntity entity, float partialTick) {
        return 0.55F + 0.45F * (0.5F + 0.5F * Mth.sin((entity.tickCount + partialTick) * 2.8F + entity.getId() * 0.63F));
    }

    private static int scaleRgb(int color, float scale) {
        return FastColor.ARGB32.color(FastColor.ARGB32.alpha(color),
                Mth.clamp(Math.round(FastColor.ARGB32.red(color) * scale), 0, 255),
                Mth.clamp(Math.round(FastColor.ARGB32.green(color) * scale), 0, 255),
                Mth.clamp(Math.round(FastColor.ARGB32.blue(color) * scale), 0, 255));
    }

    private static void addVertex(VertexConsumer buffer, Matrix4f matrix, Vec3 position, int color, float u, float v) {
        buffer.vertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .color(color)
                .uv(u, v)
                .uv2(0xF000F0)
                .endVertex();
    }

    private static Vec3 interpolatedPosition(BaseBulletEntity entity, float partialTick) {
        return new Vec3(Mth.lerp(partialTick, entity.xo, entity.getX()), Mth.lerp(partialTick, entity.yo, entity.getY()), Mth.lerp(partialTick, entity.zo, entity.getZ()));
    }
}
