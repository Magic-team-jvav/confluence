package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.DestroyerPart;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.particlestorm.api.geckolib.RegisterLocatorPreTransformerEvent;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/// 由世界渲染事件绘制同一份整链快照，头部所在区段未加载时也能绘制可见体节。
public final class DestroyerRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/boss/destroyer.png");
    private static final ResourceLocation BLUE_GLOW_TEXTURE = Confluence.asResource("textures/entity/boss/destroyer_glow_blue.png");
    private static final ResourceLocation GLOW_TEXTURE = Confluence.asResource("textures/entity/boss/destroyer_glow.png");
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
                matrix.translate(0, TheDestroyer.BODY_HEIGHT / (2.0F * TheDestroyer.MODEL_SCALE), 0);
                applyRotations(entity, matrix::rotate, z -> matrix.translate(0, -MODEL_CENTER_Y, z), partialTick);
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
        addRenderLayer(new EntityGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(T entity) {
                return GLOW_TEXTURE;
            }
        });
        addRenderLayer(new EntityGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(T entity) {return BLUE_GLOW_TEXTURE;}

            @Override
            public void render(PoseStack poses, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
                TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
                int index = entity instanceof DestroyerPart part ? part.getIndex() : 0;
                int tick = head.getBeamTick();
                float elapsed = tick + partialTick - (81 - index) * 100.0F / 82.0F;
                float alpha = tick < 0 ? 0 : Math.max(0, 1.0F - elapsed / 30.0F);
                if (elapsed < 0 || alpha <= 0) return;
                RenderType glow = getRenderType(entity);
                getRenderer().reRender(model, poses, buffers, entity, glow, buffers.getBuffer(glow), partialTick, LightTexture.FULL_BRIGHT, overlay, 1, 1, 1, alpha);
            }
        });
    }

    private static final Map<TheDestroyer, Integer> BEAM_LAST_TICK = new WeakHashMap<>();
    private static final Map<TheDestroyer, List<ParticleEmitter>> BEAM_EMITTERS = new WeakHashMap<>();

    public static void tickBeamEffects(TheDestroyer boss) {
        int tick = boss.getBeamTick();
        if (tick < 0 || boss.isRemoved()) {
            List<ParticleEmitter> old = BEAM_EMITTERS.remove(boss);
            if (old != null) old.forEach(ParticleEmitter::remove);
            BEAM_LAST_TICK.remove(boss);
            return;
        }
        int last = BEAM_LAST_TICK.getOrDefault(boss, -1);
        if (tick < last) {
            List<ParticleEmitter> old = BEAM_EMITTERS.remove(boss);
            if (old != null) old.forEach(ParticleEmitter::remove);
            last = -1;
        }
        List<ParticleEmitter> emitters = BEAM_EMITTERS.computeIfAbsent(boss, ignored -> new ArrayList<>());
        if (tick < 100 && tick != last) {
            int index = Math.min(81, 81 - tick * 82 / 100);
            if (index != Math.max(0, Math.min(81, 81 - last * 82 / 100))) {
                Vec3 pos = boss.getBody().getPosition(index);
                MolangParticleEngine.INSTANCE.addEmitter(new ParticleEmitter(boss.level(), pos, Confluence.asResource("destroyer_attack_lightning")));
            }
        }
        if (last < 100 && tick >= 100) {
            addBeamEmitter(boss, emitters, "ball");
            addBeamEmitter(boss, emitters, "inner2");
            addBeamEmitter(boss, emitters, "inner");
        }
        if (last < 143 && tick >= 143) addBeamEmitter(boss, emitters, "inner_round");
        if (last < 220 && tick >= 220) {
            addBeamEmitter(boss, emitters, "out");
            addBeamEmitter(boss, emitters, "ball1");
            addBeamEmitter(boss, emitters, "round");
        }
        if (last < 221 && tick >= 221) addBeamEmitter(boss, emitters, "laser");
        if (last < 420 && tick >= 420) {
            emitters.forEach(ParticleEmitter::remove);
            emitters.clear();
        }
        /// 发射点使用与头部模型相同的插值姿态，始终位于头部中心前方十格。
        Vector3f forward = boss.getBody().getRenderRotation(0, 1.0F).transform(new Vector3f(0, 0, -1));
        Vec3 direction = new Vec3(forward.x(), forward.y(), forward.z()).normalize();
        Vec3 origin = boss.getBody().getRenderPosition(0, 1.0F)
                .add(0, TheDestroyer.BODY_HEIGHT / 2.0, 0).add(direction.scale(10));
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), new Vector3f((float) direction.x, (float) direction.y, (float) direction.z));
        Matrix4x3f space = new Matrix4x3f().translation((float) origin.x, (float) origin.y, (float) origin.z).rotate(rotation);
        for (ParticleEmitter emitter : emitters) {
            emitter.setLocalSpace(space, false);
            emitter.setPos(origin);
        }
        BEAM_LAST_TICK.put(boss, tick);
    }

    private static void addBeamEmitter(TheDestroyer boss, List<ParticleEmitter> emitters, String name) {
        ParticleEmitter emitter = new ParticleEmitter(boss.level(), boss.beamOrigin(), Confluence.asResource("destroyer_attack_" + name));
        emitters.add(emitter);
        MolangParticleEngine.INSTANCE.addEmitter(emitter);
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
    protected void applyRotations(T entity, PoseStack pose, float age, float yaw, float partialTick) {
        pose.translate(0, TheDestroyer.BODY_HEIGHT / (2.0F * TheDestroyer.MODEL_SCALE), 0);
        applyRotations(entity, pose::mulPose, z -> pose.translate(0, -MODEL_CENTER_Y, z), partialTick);
    }

    private static void applyRotations(Entity entity, Consumer<Quaternionf> rotate, FloatConsumer z, float partialTick) {
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
        float partialTick = event.getPartialTick();
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
