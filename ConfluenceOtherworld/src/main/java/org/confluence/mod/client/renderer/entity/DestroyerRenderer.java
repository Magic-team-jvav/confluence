package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.DestroyerPart;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.mesdag.particlestorm.api.geckolib.RegisterLocatorPreTransformerEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Consumer;

/// 由世界渲染事件绘制同一份整链快照，头部所在区段未加载时也能绘制可见体节。
public final class DestroyerRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/boss/destroyer.png");
    private static final ResourceLocation ANIMATION = Confluence.asResource("animations/entity/boss/destroyer.animation.json");
    private static final ResourceLocation HEAD = model("head"), BODY = model("body"), CONNECTOR = model("connector"), TAIL = model("tail");

    private static final float MODEL_CENTER_Y = 14.0F / 16.0F;
    private static final float BODY_CENTER_Z = 35.61025F / 16.0F;
    private static final float CONNECTOR_CENTER_Z = 66.61025F / 16.0F;
    private static final float TAIL_CENTER_Z = 190.61025F / 16.0F;

    //别动 会被泛型擦除
    public static <E extends Entity & GeoEntity> RegisterLocatorPreTransformerEvent.Transformer<E> locatorTransformer() {
        return new RegisterLocatorPreTransformerEvent.Transformer<>() {
            @Override
            public void transform(GeoBone bone, E entity, Matrix4x3f matrix, float partialTick) {
                matrix.scale(TheDestroyer.MODEL_SCALE);
                applyRotations(entity, y -> matrix.translate(0, y, 0), matrix::rotate, z -> matrix.translate(0, -MODEL_CENTER_Y, z), partialTick);
                RegisterLocatorPreTransformerEvent.Transformer.defaultTransformer(bone, entity, matrix, partialTick);
            }
        };
    }

    public DestroyerRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(T entity) {
                if (entity instanceof DestroyerPart part)
                    return part.isTail() ? TAIL : part.getIndex() % 2 == 0 ? CONNECTOR : BODY;
                return HEAD;
            }

            @Override
            public ResourceLocation getTextureResource(T t) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(T t) {
                return ANIMATION;
            }
        }, false, TheDestroyer.MODEL_SCALE, 0);
    }

    private static ResourceLocation model(String section) {
        return Confluence.asResource("geo/entity/boss/destroyer_" + section + ".geo.json");
    }

    /// 头部与每个体节分别采样相邻格点光照，平滑跨越明暗边界。
    @Override
    protected boolean usesInterpolatedLight(T entity) {
        return true;
    }

    @Override
    public int getPackedOverlay(T entity, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    protected void applyRotations(T entity, PoseStack pose, float age, float yaw, float partialTick, float scale) {
        applyRotations(entity, y -> pose.translate(0, y, 0), pose::mulPose, z -> pose.translate(0, -MODEL_CENTER_Y, z), partialTick);
    }

    public static void applyRotations(Entity entity, FloatConsumer y, Consumer<Quaternionf> rotate, FloatConsumer z, float partialTick) {
        y.accept(TheDestroyer.BODY_HEIGHT / (2.0F * TheDestroyer.MODEL_SCALE));
        TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
        int index = entity instanceof DestroyerPart part ? part.getIndex() : 0;
        rotate.accept(head.getBody().getRenderRotation(index, partialTick));
        float centerZ = entity instanceof DestroyerPart part
                ? part.isTail() ? TAIL_CENTER_Z : part.getIndex() % 2 == 0 ? CONNECTOR_CENTER_Z : BODY_CENTER_Z
                : 0;
        z.accept(-centerZ);
    }

    /// 部件不在普通实体渲染列表中，整条身体统一从世界事件入口绘制。
    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {return false;}

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poses, MultiBufferSource buffers, int light) {}

    public static void renderDestroyers(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        Vec3 camera = event.getCamera().getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!(entity instanceof TheDestroyer head) || head.isRemoved()) continue;
            EntityRenderer<?> entityRenderer = minecraft.getEntityRenderDispatcher().getRenderer(head);
            if (!(entityRenderer instanceof DestroyerRenderer<?> renderer)) continue;
            renderer.renderSection(head, event.getPoseStack(), buffers, event.getFrustum(), camera, partialTick);
            for (DestroyerPart part : head.getParts()) {
                renderer.renderSection(part, event.getPoseStack(), buffers, event.getFrustum(), camera, partialTick);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Entity & GeoEntity> void renderSection(E entity, PoseStack poses, MultiBufferSource buffers,
                                                              Frustum frustum, Vec3 camera, float partialTick) {
        /// 侧翼和头甲比受击箱更宽，剔除范围预留模型外沿，避免画面边缘突然消失。
        if (!frustum.isVisible(entity.getBoundingBox().inflate(TheDestroyer.BODY_WIDTH * 2.0)))
            return;
        TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
        int index = entity instanceof DestroyerPart part ? part.getIndex() : 0;
        Vec3 position = head.getBody().getRenderPosition(index, partialTick);
        poses.pushPose();
        poses.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        super.render((T) entity, entity.getYRot(), partialTick, poses, buffers,
                Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, partialTick));
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            LevelRenderer.renderLineBox(poses, buffers.getBuffer(RenderType.lines()),
                    entity.getBoundingBox().move(entity.position().scale(-1)),
                    entity instanceof DestroyerPart ? 0.25F : 1.0F, 1.0F,
                    entity instanceof DestroyerPart ? 0.0F : 1.0F, 1.0F);
        }
        poses.popPose();
    }
}
