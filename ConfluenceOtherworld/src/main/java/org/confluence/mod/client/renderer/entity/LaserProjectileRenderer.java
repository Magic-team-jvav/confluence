package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.entity.projectile.TwinEyeProjectile;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.*;

public final class LaserProjectileRenderer<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/proj/laser.geo.json");
    public static final double CAMERA_CLEARANCE = 1.5;
    private static final float LENGTH = 29.0F / 16.0F;
    private static final float RADIUS = 3.0F / 32.0F;
    private final int innerColor;
    private final int middleColor;
    private final int outerColor;

    public LaserProjectileRenderer(EntityRendererProvider.Context context, int innerColor, int middleColor, int outerColor) {
        super(context);
        this.innerColor = innerColor;
        this.middleColor = middleColor;
        this.outerColor = outerColor;
    }

    /// 颜色改由实体动态提供（{@link MonsterLaser} 按变种取色）。
    public LaserProjectileRenderer(EntityRendererProvider.Context context) {
        this(context, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        return entity.shouldRender(cameraX, cameraY, cameraZ)
                && frustum.isVisible(entity.getBoundingBox().inflate(LENGTH * 0.5 + RADIUS));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        Vec3 direction = entity.getDeltaMovement();
        if (direction.lengthSqr() < 1.0E-8) direction = entity.getViewVector(partialTick);
        int inner = innerColor, middle = middleColor, outer = outerColor;
        if (entity instanceof MonsterLaser laser) {
            MonsterLaser.Variant variant = laser.variant();
            inner = variant.innerColor();
            middle = variant.middleColor();
            outer = variant.outerColor();
        }
        if (entity instanceof TwinEyeProjectile) {
            double distance = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.getPosition(partialTick));
            float opacity = Mth.clamp((float) (distance - CAMERA_CLEARANCE), 0.0F, 1.0F);
            renderBeam(poseStack, buffers, direction, LENGTH, RADIUS, inner, middle, outer, opacity);
        } else {
            renderBeam(poseStack, buffers, direction, LENGTH, RADIUS, inner, middle, outer);
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    /**
     * 模型以中心沿 Z 轴展开；持续光束由调用方将原点移至光束中点。颜色参数均为 RGB。
     */
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
