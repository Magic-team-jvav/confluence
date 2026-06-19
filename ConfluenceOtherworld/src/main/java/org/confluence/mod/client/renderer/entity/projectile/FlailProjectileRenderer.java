package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.Flail.BaseFlailProjectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>连枷子投射物通用渲染器</h1>
 * 渲染所有 {@link BaseFlailProjectile} 子类，支持三种模式：
 * <ol>
 *   <li><b>BILLBOARD</b> — 始终面朝摄像机的四边形平面（默认）</li>
 *   <li><b>JSON_MODEL</b> — 标准 Minecraft {@link EntityModel} 模型</li>
 *   <li><b>GEO</b> — GeckoLib {@link GeoModel} 模型（支持动画）</li>
 * </ol>
 * <p>
 * 模式由 {@link BaseFlailProjectile#getRenderMode()} 决定。
 */
public class FlailProjectileRenderer extends EntityRenderer<BaseFlailProjectile> {

    private static final ResourceLocation DEFAULT_TEXTURE = Confluence.asResource("textures/entity/flail/flower_power.png");
    private static final ResourceLocation DEFAULT_GEO_MODEL = Confluence.asResource("geo/entity/flail/flail.geo.json");
    private static final Map<ResourceLocation, RenderType> RENDER_TYPE_CACHE = new ConcurrentHashMap<>();

    /** Billboard 四边形半边长 */
    private final float scale;
    /** 用于动态烘焙 JSON 模型 */
    private final EntityRendererProvider.Context context;
    /** JSON 模型的 ModelPart 根节点缓存 */
    private final Map<ModelLayerLocation, ModelPart> jsonModelPartCache = new ConcurrentHashMap<>();
    /** Geo 渲染委托缓存，按模型路径索引 */
    private final Map<ResourceLocation, GeoEntityRenderer<BaseFlailProjectile>> geoDelegateCache = new ConcurrentHashMap<>();

    public FlailProjectileRenderer(EntityRendererProvider.Context context) {
        this(context, 0.15F);
    }

    public FlailProjectileRenderer(EntityRendererProvider.Context context, float scale) {
        super(context);
        this.context = context;
        this.scale = scale;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BaseFlailProjectile entity) {
        return getTextureFor(entity);
    }

    /** 解析实体贴图：实体级优先，否则回退默认 */
    public static ResourceLocation getTextureFor(BaseFlailProjectile entity) {
        ResourceLocation custom = entity.getTexture();
        return custom != null ? custom : DEFAULT_TEXTURE;
    }

    @Override
    public void render(@NotNull BaseFlailProjectile entity, float entityYaw, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        BaseFlailProjectile.RenderMode mode = entity.getRenderMode();
        try {
            switch (mode) {
                case JSON_MODEL -> renderJsonModel(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                case GEO -> renderGeoModel(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                default -> renderBillboard(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            }
        } catch (RuntimeException e) {
            // 模型或贴图加载失败时回退到 billboard
            renderBillboard(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }

    // ── Billboard ──

    private void renderBillboard(BaseFlailProjectile entity, float entityYaw, float partialTick,
                                  PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ResourceLocation texture = getTextureFor(entity);
        RenderType renderType = RENDER_TYPE_CACHE.computeIfAbsent(texture, t -> RenderType.entityCutoutNoCull(t));
        VertexConsumer consumer = bufferSource.getBuffer(renderType);

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() * 0.5, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        float s = scale;
        Matrix4f matrix = poseStack.last().pose();
        consumer.addVertex(matrix, -s, -s, 0).setColor(255, 255, 255, 255)
                .setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(matrix, -s, +s, 0).setColor(255, 255, 255, 255)
                .setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(matrix, +s, +s, 0).setColor(255, 255, 255, 255)
                .setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(matrix, +s, -s, 0).setColor(255, 255, 255, 255)
                .setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 0, 1);

        poseStack.popPose();
    }

    // ── JSON Model ──

    private void renderJsonModel(BaseFlailProjectile entity, float entityYaw, float partialTick,
                                  PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ModelLayerLocation layer = entity.getModelLayerLocation();
        if (layer == null) return;
        ModelPart root = jsonModelPartCache.computeIfAbsent(layer,
                l -> context.getModelSet().bakeLayer(l));

        ResourceLocation texture = getTextureFor(entity);
        RenderType renderType = RENDER_TYPE_CACHE.computeIfAbsent(texture, t -> RenderType.entityCutoutNoCull(t));
        VertexConsumer consumer = bufferSource.getBuffer(renderType);

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() * 0.5, 0);
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        root.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    // ── Geo Model ──

    private void renderGeoModel(BaseFlailProjectile entity, float entityYaw, float partialTick,
                                 PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ResourceLocation modelLoc = entity.getGeoModelLocation();
        if (modelLoc == null) modelLoc = DEFAULT_GEO_MODEL;
        ResourceLocation texture = getTextureFor(entity);

        // 按模型路径缓存 GeoEntityRenderer 委托
        GeoEntityRenderer<BaseFlailProjectile> delegate = geoDelegateCache.computeIfAbsent(modelLoc,
                loc -> new GeoEntityRenderer<>(context, new ProjectileGeoModel(loc, texture)) {
                    @Override
                    public ResourceLocation getTextureLocation(BaseFlailProjectile a) {
                        return getTextureFor(a);
                    }
                });

        // 动态更新贴图和动画
        ProjectileGeoModel pModel = (ProjectileGeoModel) delegate.getGeoModel();
        pModel.texture = texture;
        pModel.animation = entity.getAnimationLocation();

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() * 0.5, 0);
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        delegate.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    // ── 内部 GeoModel ──

    private static class ProjectileGeoModel extends GeoModel<BaseFlailProjectile> {
        final ResourceLocation model;
        ResourceLocation texture;
        @Nullable
        ResourceLocation animation;

        ProjectileGeoModel(ResourceLocation model, ResourceLocation texture) {
            this.model = model;
            this.texture = texture;
        }

        @Override
        public ResourceLocation getModelResource(BaseFlailProjectile animatable) {
            return model;
        }

        @Override
        public ResourceLocation getTextureResource(BaseFlailProjectile animatable) {
            return texture;
        }

        @Override
        public ResourceLocation getAnimationResource(BaseFlailProjectile animatable) {
            return animation;
        }
    }
}
