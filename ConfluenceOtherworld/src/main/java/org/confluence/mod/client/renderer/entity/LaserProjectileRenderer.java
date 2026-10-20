package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.*;

import java.util.function.Function;

public final class LaserProjectileRenderer<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/proj/laser.geo.json");
    public static final double CAMERA_CLEARANCE = 1.5;
    private static final float LENGTH = 29.0F / 16.0F;
    private static final float RADIUS = 3.0F / 32.0F;
    private static final float TRAIL_OPACITY = 0.4F;
    private final Function<T, Style> styleProvider;

    // 固定颜色的普通激光保留默认尺寸，不启用拖尾或镜头裁剪。
    public LaserProjectileRenderer(EntityRendererProvider.Context context, int innerColor, int middleColor, int outerColor) {
        this(context, new Style(innerColor, middleColor, outerColor));
    }

    public LaserProjectileRenderer(EntityRendererProvider.Context context, Style style) {
        this(context, entity -> style);
    }

    // 样式由注册方提供，通用渲染器不识别具体实体或变种类型。
    public LaserProjectileRenderer(EntityRendererProvider.Context context, Function<T, Style> styleProvider) {
        super(context);
        this.styleProvider = styleProvider;
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        Style style = styleProvider.apply(entity);
        double extent = style.length() * 0.5 + style.trailLength() + style.radius();
        return entity.shouldRender(cameraX, cameraY, cameraZ)
                && frustum.isVisible(entity.getBoundingBox().inflate(extent));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        Vec3 direction = entity.getDeltaMovement();
        if (direction.lengthSqr() < 1.0E-8) direction = entity.getViewVector(partialTick);
        Vec3 unit = direction.normalize();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().subtract(entity.getPosition(partialTick));
        Style style = styleProvider.apply(entity);
        renderSection(poseStack, buffers, unit, camera, -style.length() * 0.5, style.length() * 0.5,
                style.radius(), style.innerColor(), style.middleColor(), style.outerColor(),
                style.cameraClearance() > 0 ? 1.0F : -1.0F, style.cameraClearance());
        renderSection(poseStack, buffers, unit, camera, -style.length() * 0.5 - style.trailLength(), -style.length() * 0.5,
                style.radius() * 0.6F, style.middleColor(), style.middleColor(), style.outerColor(), TRAIL_OPACITY, style.cameraClearance());
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    // 尾迹与本体共用镜头裁剪，避让距离为零时绘制完整线段。
    private static void renderSection(PoseStack poseStack, MultiBufferSource buffers, Vec3 direction, Vec3 camera,
                                      double from, double to, float radius, int inner, int middle, int outer, float opacity, float clearance) {
        if (to <= from) return;
        double projection = camera.dot(direction);
        double perpendicularSqr = camera.distanceToSqr(direction.scale(projection));
        if (clearance <= 0 || perpendicularSqr >= clearance * clearance) {
            renderPart(poseStack, buffers, direction, from, to, radius, inner, middle, outer, opacity);
        } else {
            double gap = Math.sqrt(clearance * clearance - perpendicularSqr);
            renderPart(poseStack, buffers, direction, from, Math.min(to, projection - gap), radius, inner, middle, outer, opacity);
            renderPart(poseStack, buffers, direction, Math.max(from, projection + gap), to, radius, inner, middle, outer, opacity);
        }
    }

    // 只调整外观长度与宽度，弹体的实体碰撞箱保持不变。
    private static void renderPart(PoseStack poseStack, MultiBufferSource buffers, Vec3 direction,
                                   double from, double to, float radius, int inner, int middle, int outer, float opacity) {
        if (to <= from) return;
        Vec3 offset = direction.scale((from + to) * 0.5);
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        renderBeam(poseStack, buffers, direction, (float) (to - from), radius, inner, middle, outer, opacity);
        poseStack.popPose();
    }

    // 注册时声明颜色、几何尺寸、尾迹长度与镜头避让距离；颜色也可随实体状态动态提供。
    public record Style(int innerColor, int middleColor, int outerColor, float length, float radius,
                        float trailLength, float cameraClearance) {
        public Style(int innerColor, int middleColor, int outerColor) {
            this(innerColor, middleColor, outerColor, LENGTH, RADIUS, 0, 0);
        }
    }

    // 模型以中心沿 Z 轴展开；持续光束由调用方将原点移至光束中点。颜色参数均为 RGB。
    public static void renderBeam(PoseStack poseStack, MultiBufferSource buffers, Vec3 direction, float length, float radius, int innerColor, int middleColor, int outerColor) {
        renderBeam(poseStack, buffers, direction, length, radius, innerColor, middleColor, outerColor, -1.0F);
    }

    public static void renderBeam(PoseStack poseStack, MultiBufferSource buffers, Vec3 direction, float length, float radius, int innerColor, int middleColor, int outerColor, float opacity) {
        if (opacity == 0.0F || length <= 0) return;
        BakedGeoModel model = GeckoLibCache.getBakedModels().get(MODEL);
        if (model == null || direction.lengthSqr() < 1.0E-8) return;
        GeoBone bone = model.getBone("bb_main").orElseThrow();
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotationTo(new Vector3f(0.0F, 0.0F, 1.0F), direction.normalize().toVector3f()));
        poseStack.scale(radius * 32.0F / 3.0F, radius * 32.0F / 3.0F, length * 16.0F / 29.0F);
        poseStack.translate(0.0, -11.0 / 16.0, 0.0);
        VertexConsumer consumer = buffers.getBuffer(opacity < 0 ? RenderStateShardAccessor.LASER : RenderStateShardAccessor.TRANSLUCENT_LASER);
        int layer = 0;
        for (GeoCube cube : bone.getCubes()) {
            int color = switch (layer++) {
                case 0 -> innerColor;
                case 1 -> middleColor;
                default -> outerColor;
            };
            int alpha = opacity < 0 ? 255 : Math.round((layer == 1 ? 220 : layer == 2 ? 65 : 35) * opacity);
            for (GeoQuad quad : cube.quads()) {
                if (quad == null) continue;
                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f position = vertex.position();
                    consumer.vertex(poseStack.last().pose(), position.x(), position.y(), position.z())
                            .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha).endVertex();
                }
            }
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return MODEL;
    }
}
