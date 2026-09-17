package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.confluence.mod.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.boss.BaseWormBoss;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.common.entity.monster.WormSegment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoNormalRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {
    protected final boolean rotateAlongPitch;
    protected final float modelScale;
    protected final float modelOffsetY;
    protected float motionAnimThreshold = 0.01F;
    private BakedGeoModel cullingModel;
    private boolean needsFaceCulling;
    private boolean cutout;
    protected boolean noCull;
    private final java.util.Map<GeoCube, GeoCube> separatedPlanes = new java.util.WeakHashMap<>();

    @Override
    public void renderCube(PoseStack poses, GeoCube cube, VertexConsumer buffer, int light,
                           int overlay, float red, float green, float blue, float alpha) {
        var size = cube.size();
        if (noCull && cube.inflate() == 0 && (size.x == 0 || size.y == 0 || size.z == 0))
            cube = separatedPlanes.computeIfAbsent(cube, GeoNormalRenderer::separatePlaneFaces);
        super.renderCube(poses, cube, buffer, light, overlay, red, green, blue, alpha);
    }

    // 保留原模型两面的 UV；让零厚度薄片的正反面稍微分离，避免双面绘制时争抢同一深度。
    private static GeoCube separatePlaneFaces(GeoCube cube) {
        var quads = cube.quads().clone();
        var size = cube.size();
        for (int i = 0; i < quads.length; i++) {
            var quad = quads[i];
            if (quad == null) continue;
            var normal = quad.normal();
            Vector3f offset = new Vector3f(size.x == 0 ? normal.x() : 0,
                    size.y == 0 ? normal.y() : 0, size.z == 0 ? normal.z() : 0).mul(0.0005F);
            if (offset.lengthSquared() == 0) continue;
            var vertices = quad.vertices().clone();
            for (int j = 0; j < vertices.length; j++) {
                var vertex = vertices[j];
                vertices[j] = new software.bernie.geckolib.cache.object.GeoVertex(
                        new Vector3f(vertex.position()).add(offset), vertex.texU(), vertex.texV());
            }
            quads[i] = new software.bernie.geckolib.cache.object.GeoQuad(vertices, normal, quad.direction());
        }
        return new GeoCube(quads, cube.pivot(), cube.rotation(), size, cube.inflate(), cube.mirror());
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, path, false, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path, boolean rotateAlongPitch, float modelScale, float modelOffsetY) {
        this(context, new GeoNormalModel<>(path), rotateAlongPitch, modelScale, modelOffsetY);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        this(context, model, false, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model, boolean rotateAlongPitch, float modelScale, float modelOffsetY) {
        super(context, model);
        this.rotateAlongPitch = rotateAlongPitch;
        this.modelScale = modelScale;
        this.modelOffsetY = modelOffsetY;
        this.shadowRadius = 0.25F;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        int light = packedLight;
        if (usesInterpolatedLight(entity)) {
            light = EntityLightSampler.sample(entity.getLightProbePosition(partialTick),
                    pos -> getBlockLightLevel(entity, pos), pos -> getSkyLightLevel(entity, pos));
        }
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    @Override
    public RenderType getRenderType(T entity, ResourceLocation texture, @Nullable MultiBufferSource buffers, float partialTick) {
        if (noCull) return RenderType.entityTranslucent(texture);
        if (cutout) return RenderType.entityCutout(texture);
        RenderType type = super.getRenderType(entity, texture, buffers, partialTick);
        if (type == RenderType.entityCutoutNoCull(texture) || type == RenderType.entityTranslucent(texture)) {
            BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(entity));
            if (model != cullingModel) {
                cullingModel = model;
                needsFaceCulling = model.topLevelBones().stream().anyMatch(GeoNormalRenderer::needsFaceCulling);
            }
            if (needsFaceCulling)
                return type == RenderType.entityTranslucent(texture) ? RenderType.entityTranslucentCull(texture) : RenderType.entityCutout(texture);
        }
        return type;
    }

    public GeoNormalRenderer<T> withCutout() {
        cutout = true;
        return this;
    }

    /// 强制保留零厚度模型的正反两面，例如飞鱼成对的薄片翅膀。
    public GeoNormalRenderer<T> withNoCull() {
        noCull = true;
        return this;
    }

    /// 仅由跨方块的长模型或分段实体显式启用，普通实体沿用原版单点光照。
    protected boolean usesInterpolatedLight(T entity) {
        return false;
    }

    private static boolean needsFaceCulling(GeoBone bone) {
        return bone.getCubes().stream().anyMatch(GeoNormalRenderer::needsFaceCulling) || bone.getChildBones().stream().anyMatch(GeoNormalRenderer::needsFaceCulling);
    }

    private static boolean needsFaceCulling(GeoCube cube) {
        var size = cube.size();
        if (size.x < 0 || size.y < 0 || size.z < 0) return true;
        if (size.x != 0 && size.y != 0 && size.z != 0) return false;
        boolean front = false;
        boolean back = false;
        for (var quad : cube.quads()) {
            if (quad == null) continue;
            float normal = size.x == 0 ? quad.normal().x() : size.y == 0 ? quad.normal().y() : quad.normal().z();
            front |= normal > 0.5F;
            back |= normal < -0.5F;
        }
        return front && back;
    }

    /// 应用通用 Geo 渲染参数。
    ///
    /// 这里仅处理模型缩放、垂直偏移和沿俯仰方向旋转。它们只影响客户端显示，
    /// 不参与服务端实体碰撞箱、移动路径或伤害判定。
    @Override
    public void preRender(
            PoseStack poseStack,
            T animatable,
            BakedGeoModel model,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        float effectiveScale = getEffectiveModelScale(animatable) * getEncounterScale(animatable);
        poseStack.scale(effectiveScale, effectiveScale, effectiveScale);
        poseStack.translate(0.0F, modelOffsetY, 0.0F);
        if (shouldRotateAlongPitch(animatable) && getWormModelCenter(animatable) == null) {
            // 实体渲染原点位于碰撞箱脚底。直接在此处俯仰会把长模型绕脚底甩出链条，
            // 接近 90° 时尤其明显；先移到实际碰撞箱中心，旋转后再移回。
            float pivotY = animatable.getBbHeight() * 0.5F / effectiveScale - modelOffsetY;
            poseStack.translate(0.0F, pivotY, 0.0F);
            double yaw = getRenderYaw(animatable, partialTick) * Mth.DEG_TO_RAD;
            Vector3f axis = new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw));
            poseStack.mulPose(Axis.of(axis).rotationDegrees(getRenderPitch(animatable, partialTick)));
            poseStack.translate(0.0F, -pivotY, 0.0F);
        }
        adjustPose(poseStack, animatable, model, partialTick);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void adjustPose(PoseStack poseStack, T animatable, BakedGeoModel model, float partialTick) {}

    protected boolean shouldRotateAlongPitch(T animatable) {
        return rotateAlongPitch;
    }

    /// 模型主干中心，单位为方块；与碰撞箱大小无关。
    protected @Nullable Vector3f getWormModelCenter(T animatable) {
        return null;
    }

    protected float getRenderPitch(T animatable, float partialTick) {
        if (animatable instanceof WormSegment segment) {
            var tangent = segment.interpolatedChainTangent(partialTick);
            if (tangent.lengthSqr() > 1.0E-7)
                return (float) (-Mth.atan2(tangent.y, tangent.horizontalDistance()) * Mth.RAD_TO_DEG);
        }
        return Mth.rotLerp(partialTick, animatable.xRotO, animatable.getXRot());
    }

    protected float getRenderYaw(T animatable, float partialTick) {
        if (animatable instanceof WormSegment segment) {
            var tangent = segment.interpolatedChainTangent(partialTick);
            if (tangent.horizontalDistanceSqr() > 1.0E-10)
                return (float) (Mth.atan2(tangent.z, tangent.x) * Mth.RAD_TO_DEG) - 90.0F;
        }
        return Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot());
    }

    /// 返回当前实体实际使用的模型缩放。共用渲染器可按资源家族覆盖，旋转中心必须读取同一值。
    protected float getEffectiveModelScale(T animatable) {
        return modelScale;
    }

    /// 非生物部件没有自己的 scale 属性，渲染时继承遭遇主体的同步倍率。
    private float getEncounterScale(T animatable) {
        if (animatable instanceof PartHitTarget part && part.encounterOwner() instanceof LivingEntity owner)
            return owner.getScale();
        return animatable instanceof LivingEntity living ? living.getScale() : 1.0F;
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        Vector3f center = getWormModelCenter(animatable);
        if (center != null) {
            // 将主干中心放到统一链节点，再在同一个局部坐标系内执行偏航和俯仰。
            // 不能在 preRender 中按不同碰撞箱旋转后，又在这里分别绕模型原点偏航。
            poseStack.translate(0, center.y, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - getRenderYaw(animatable, partialTick)));
            poseStack.mulPose(Axis.XP.rotationDegrees(-getRenderPitch(animatable, partialTick)));
            poseStack.translate(-center.x, -center.y - 0.01F, -center.z);
            return;
        }
        if (!(animatable instanceof LivingEntity) || animatable instanceof WormSegment) {
            rotationYaw = getRenderYaw(animatable, partialTick);
        }
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        // 分段蠕虫不能只让头部执行原版侧翻，否则会与保留原姿态的体节断开。
        return animatable instanceof BaseWormMonster || animatable instanceof BaseWormBoss ? 0.0F : super.getDeathMaxRotation(animatable);
    }

    @Override
    public float getMotionAnimThreshold(T animatable) {
        return motionAnimThreshold;
    }

    @Override
    public GeoNormalRenderer<T> withScale(float scale) {
        super.withScale(scale);
        return this;
    }

    public GeoNormalRenderer<T> setMotionAnimThreshold(float threshold) {
        this.motionAnimThreshold = threshold;
        return this;
    }

    public GeoNormalRenderer<T> setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }
}
