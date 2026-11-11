package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.effect.death.GeoDeathModelAdapter;
import org.confluence.mod.client.handler.DeathEffectManager;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.entity.boss.DestroyerPart;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.integration.geckolib.IGeoCube;
import org.confluence.mod.util.ClientUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.particlestorm.api.geckolib.RegisterLocatorPreTransformerEvent;
import org.mesdag.particlestorm.mixed.IPSEntity;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/// 由世界渲染事件绘制同一份整链快照，头部所在区段未加载时也能绘制可见体节。
public final class DestroyerRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/boss/destroyer.png");
    private static final ResourceLocation PHASE1_TEXTURE = Confluence.asResource("textures/entity/boss/destroyer_phase1.png");
    private static final ResourceLocation PHASE1_GLOW_TEXTURE = Confluence.asResource("textures/entity/boss/destroyer_phase1_glow.png");
    private static final ResourceLocation BLUE_GLOW_TEXTURE = Confluence.asResource("textures/entity/boss/destroyer_glow_blue.png");
    private static final ResourceLocation GLOW_TEXTURE = Confluence.asResource("textures/entity/boss/destroyer_glow.png");
    private static final ResourceLocation ANIMATION = Confluence.asResource("animations/entity/boss/destroyer.animation.json");
    private static final ResourceLocation HEAD = model("head"), BODY = model("body"), CONNECTOR = model("connector"), TAIL = model("tail");
    private static final ResourceLocation PHASE1_HEAD = phase1Model("head"), PHASE1_BODY = phase1Model("body"),
            PHASE1_CONNECTOR = phase1Model("connector"), PHASE1_TAIL = phase1Model("tail");

    // 每种发光材质独占缓冲，同一帧的所有可见体节写完后再统一提交。
    private static final class GlowBuffers {
        static final MultiBufferSource.BufferSource BODY_PHASE_ONE = MultiBufferSource.immediate(new BufferBuilder(65536));
        static final MultiBufferSource.BufferSource BODY_PHASE_TWO = MultiBufferSource.immediate(new BufferBuilder(65536));
        static final MultiBufferSource.BufferSource PHASE_ONE = MultiBufferSource.immediate(new BufferBuilder(65536));
        static final MultiBufferSource.BufferSource PHASE_TWO = MultiBufferSource.immediate(new BufferBuilder(65536));
        static final MultiBufferSource.BufferSource BLUE = MultiBufferSource.immediate(new BufferBuilder(65536));
    }

    private static final float MODEL_CENTER_Y = 14.0F / 16.0F;
    private static final float BODY_CENTER_Z = 35.61025F / 16.0F;
    private static final float CONNECTOR_CENTER_Z = 66.61025F / 16.0F;
    private static final float TAIL_CENTER_Z = 190.61025F / 16.0F;
    private static final float PHASE1_TAIL_CENTER_Z = 97.61025F / 16.0F;

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
                TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
                if (head.isPhaseOneSection(entity instanceof DestroyerPart part ? part.getIndex() : 0)) {
                    if (entity instanceof DestroyerPart part)
                        return part.isTail() ? PHASE1_TAIL : part.getIndex() % 2 == 0 ? PHASE1_CONNECTOR : PHASE1_BODY;
                    return PHASE1_HEAD;
                }
                if (entity instanceof DestroyerPart part)
                    return part.isTail() ? TAIL : part.getIndex() % 2 == 0 ? CONNECTOR : BODY;
                return HEAD;
            }

            @Override
            public ResourceLocation getTextureResource(T t) {
                TheDestroyer head = t instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) t;
                return head.isPhaseOneSection(t instanceof DestroyerPart part ? part.getIndex() : 0) ? PHASE1_TEXTURE : TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(T t) {
                return ANIMATION;
            }
        }, false, TheDestroyer.MODEL_SCALE, 0);
        addRenderLayer(new EntityGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(T entity) {
                TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
                return head.isPhaseOneSection(entity instanceof DestroyerPart part ? part.getIndex() : 0) ? PHASE1_GLOW_TEXTURE : GLOW_TEXTURE;
            }

            @Override
            public void render(PoseStack poses, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
                if (sectionGlow != null) return;
                MultiBufferSource glowBuffers = getTextureResource(entity).equals(PHASE1_GLOW_TEXTURE) ? GlowBuffers.PHASE_ONE : GlowBuffers.PHASE_TWO;
                super.render(poses, entity, model, type, glowBuffers, buffer, partialTick, light, overlay);
            }
        });
        addRenderLayer(new EntityGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(T entity) {return BLUE_GLOW_TEXTURE;}

            @Override
            public void render(PoseStack poses, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
                if (sectionGlow != null) return;
                TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
                int index = entity instanceof DestroyerPart part ? part.getIndex() : 0;
                if (head.isPhaseOneSection(index)) return;
                int tick = head.getBeamTick();
                float elapsed = tick + partialTick - (81 - index) * 100.0F / 82.0F;
                float alpha = tick < 0 ? 0 : Math.max(0, 1.0F - elapsed / 30.0F);
                if (elapsed < 0 || alpha <= 0) return;
                RenderType glow = getRenderType(entity);
                getRenderer().reRender(model, poses, GlowBuffers.BLUE, entity, glow, GlowBuffers.BLUE.getBuffer(glow), partialTick, LightTexture.FULL_BRIGHT, overlay, 1, 1, 1, alpha);
            }
        });
    }

    /// 每次只拆分当前体节的外壳，保持从头到尾的爆裂顺序。
    @SuppressWarnings("unchecked")
    public static void spawnPhaseShell(TheDestroyer boss, int index) {
        if (!(boss.level() instanceof ClientLevel level) || index >= TheDestroyer.SEGMENT_COUNT - 1)
            return;
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(boss);
        if (!(renderer instanceof DestroyerRenderer<?>)) return;
        DestroyerRenderer<TheDestroyer> geoRenderer = (DestroyerRenderer<TheDestroyer>) renderer;
        String section = index == 0 ? "head" : index % 2 == 0 ? "connector" : "body";
        BakedGeoModel model = geoRenderer.getGeoModel().getBakedModel(phase1Model(section));
        GeoBone bone = model.getBone(ClientUtils.DEATH_BONE_NAME).orElse(null);
        if (bone == null) return;
        DeathEffectManager.spawnFragments(boss, bone.getCubes().size(), () -> {
            Vec3 motion = boss.getDeltaMovement();
            RenderType renderType = geoRenderer.getRenderType(boss, PHASE1_TEXTURE, Minecraft.getInstance().renderBuffers().bufferSource(), 1.0F);
            float centerZ = index == 0 ? 0 : index % 2 == 0 ? CONNECTOR_CENTER_Z : BODY_CENTER_Z;
            for (GeoCube cube : bone.getCubes()) {
                if (!DeathEffectManager.canAddPart()) break;
                GeoCube copy = IGeoCube.of(cube).confluence$getCopy();
                if (copy == null) continue;
                float[] min = IGeoCube.of(copy).confluence$getMinCoords();
                float[] max = IGeoCube.of(copy).confluence$getMaxCoords();
                float x = (min[0] + max[0]) * 0.5F, y = min[1], z = (min[2] + max[2]) * 0.5F;
                GeoBone fragment = GeoDeathModelAdapter.snapshotBone(bone, null);
                fragment.getCubes().clear();
                fragment.getChildBones().clear();
                fragment.setPivotX(0);
                fragment.setPivotY(0);
                fragment.setPivotZ(0);
                fragment.setPosX(0);
                fragment.setPosY(0);
                fragment.setPosZ(0);
                fragment.setHidden(false);
                fragment.getCubes().add(copy);
                Matrix4f transform = new Matrix4f().translation(0, TheDestroyer.BODY_HEIGHT / (2.0F * TheDestroyer.MODEL_SCALE), 0)
                        .rotate(boss.getBody().getRenderRotation(index, 1.0F)).translate(-0.0F, -MODEL_CENTER_Y, -centerZ)
                        .translate(x, y, z);
                DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, boss, fragment, (float) motion.length());
                part.geoRenderData = new GeoDeathModelAdapter.RenderSnapshot(model, PHASE1_TEXTURE, renderType, transform);
                part.setPos(boss.getBody().getRenderPosition(index, 1.0F));
                part.setDeltaMovement(motion.scale(0.3).offsetRandom(level.random, 0.15F));
                DeathEffectManager.addPart(part);
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
        for (int index = 0; index < TheDestroyer.SEGMENT_COUNT; index++) {
            int trigger = (TheDestroyer.SEGMENT_COUNT - 1 - index) * 100 / (TheDestroyer.SEGMENT_COUNT - 1);
            if (last >= trigger || tick < trigger) continue;
            Entity section = boss.getBody().getSection(index);
            ParticleEmitter lightning = new ParticleEmitter(boss.level(), section.position(), Confluence.asResource("destroyer_attack_lightning"));
            lightning.attach(IPSEntity.of(section));
            MolangParticleEngine.INSTANCE.addEmitter(lightning);
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

    private static ResourceLocation phase1Model(String section) {
        return Confluence.asResource("geo/entity/boss/destroyer_phase1_" + section + ".geo.json");
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

    // 按骨骼复用局部几何，资源重载后旧骨骼及缓存可一同回收。
    private final Map<GeoBone, List<BakedFace>> bakedFaces = new com.google.common.collect.MapMaker().weakKeys().makeMap();
    private final Map<BakedGeoModel, java.util.Optional<GeoBone>> probeBones = new com.google.common.collect.MapMaker().weakKeys().makeMap();
    private final Vector3f faceNormal = new Vector3f();
    private VertexConsumer sectionGlow;
    private VertexConsumer sectionBlueGlow;
    private float sectionBlueAlpha;

    private record BakedFace(GeoCube cube, GeoQuad quad) {}

    private static List<BakedFace> bakeFaces(GeoBone bone) {
        List<BakedFace> faces = new ArrayList<>();
        for (GeoCube cube : bone.getCubes()) {
            PoseStack local = new PoseStack();
            RenderUtils.translateToPivotPoint(local, cube);
            RenderUtils.rotateMatrixAroundCube(local, cube);
            RenderUtils.translateAwayFromPivotPoint(local, cube);
            for (GeoQuad quad : cube.quads()) {
                if (quad == null) continue;
                GeoVertex[] vertices = new GeoVertex[quad.vertices().length];
                for (int i = 0; i < vertices.length; i++) {
                    GeoVertex source = quad.vertices()[i];
                    Vector3f position = local.last().pose().transformPosition(new Vector3f(source.position()));
                    vertices[i] = new GeoVertex(position, source.texU(), source.texV());
                }
                Vector3f normal = local.last().normal().transform(new Vector3f(quad.normal()));
                faces.add(new BakedFace(cube, new GeoQuad(vertices, normal, quad.direction())));
            }
        }
        return faces;
    }

    @Override
    public void renderCubesOfBone(PoseStack poses, GeoBone bone, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (bone.isHidden() || bone.getCubes().isEmpty()) return;
        List<BakedFace> faces = bakedFaces.computeIfAbsent(bone, DestroyerRenderer::bakeFaces);
        for (int i = 0; i < faces.size(); i++) {
            BakedFace face = faces.get(i);
            poses.last().normal().transform(face.quad().normal(), faceNormal);
            RenderUtils.fixInvertedFlatCube(face.cube(), faceNormal);
            Matrix4f pose = poses.last().pose();
            for (GeoVertex vertex : face.quad().vertices()) {
                Vector3f position = vertex.position();
                float x = pose.m00() * position.x + pose.m10() * position.y + pose.m20() * position.z + pose.m30();
                float y = pose.m01() * position.x + pose.m11() * position.y + pose.m21() * position.z + pose.m31();
                float z = pose.m02() * position.x + pose.m12() * position.y + pose.m22() * position.z + pose.m32();
                buffer.vertex(x, y, z, red, green, blue, alpha, vertex.texU(), vertex.texV(), overlay, light, faceNormal.x(), faceNormal.y(), faceNormal.z());
                if (sectionGlow != null)
                    sectionGlow.vertex(x, y, z, 1, 1, 1, 1, vertex.texU(), vertex.texV(), overlay, LightTexture.FULL_BRIGHT, faceNormal.x(), faceNormal.y(), faceNormal.z());
                if (sectionBlueGlow != null)
                    sectionBlueGlow.vertex(x, y, z, 1, 1, 1, sectionBlueAlpha, vertex.texU(), vertex.texV(), overlay, LightTexture.FULL_BRIGHT, faceNormal.x(), faceNormal.y(), faceNormal.z());
            }
        }
    }

    @Override
    public void createVerticesOfQuad(GeoQuad quad, Matrix4f pose, Vector3f normal, VertexConsumer buffer,
                                     int light, int overlay, float red, float green, float blue, float alpha) {
        for (GeoVertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            float x = position.x(), y = position.y(), z = position.z();
            buffer.vertex(pose.m00() * x + pose.m10() * y + pose.m20() * z + pose.m30(),
                    pose.m01() * x + pose.m11() * y + pose.m21() * z + pose.m31(),
                    pose.m02() * x + pose.m12() * y + pose.m22() * z + pose.m32(),
                    red, green, blue, alpha, vertex.texU(), vertex.texV(), overlay, light,
                    normal.x(), normal.y(), normal.z());
        }
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
                ? part.isTail() ? (head.isPhaseOneSection(index) ? PHASE1_TAIL_CENTER_Z : TAIL_CENTER_Z) : part.getIndex() % 2 == 0 ? CONNECTOR_CENTER_Z : BODY_CENTER_Z
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
        List<TheDestroyer> destroyers = new ArrayList<>();
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!(entity instanceof TheDestroyer head) || head.isRemoved()) continue;
            EntityRenderer<?> entityRenderer = minecraft.getEntityRenderDispatcher().getRenderer(head);
            if (!(entityRenderer instanceof DestroyerRenderer<?> renderer)) continue;
            destroyers.add(head);
            renderer.renderSection(head, event.getPoseStack(), buffers, event.getFrustum(), camera, partialTick);
            for (DestroyerPart part : head.getParts()) {
                renderer.renderSection(part, event.getPoseStack(), buffers, event.getFrustum(), camera, partialTick);
            }
        }
        // 先提交身体模型的深度，再绘制只写颜色的半透明激光。
        if (destroyers.isEmpty()) return;
        buffers.endBatch();
        GlowBuffers.BODY_PHASE_ONE.endBatch();
        GlowBuffers.BODY_PHASE_TWO.endBatch();
        GlowBuffers.PHASE_ONE.endBatch();
        GlowBuffers.PHASE_TWO.endBatch();
        GlowBuffers.BLUE.endBatch();
        for (TheDestroyer head : destroyers) {
            DestroyerBeamRenderer.render(head, event.getPoseStack(), buffers, camera, partialTick);
        }
        buffers.endBatch(RenderStateShardAccessor.TRANSLUCENT_LASER);
        buffers.endBatch(RenderStateShardAccessor.TRANSLUCENT_LASER_NO_CULL);
    }

    @SuppressWarnings("unchecked")
    private <E extends Entity & GeoEntity> void renderSection(E entity, PoseStack poses, MultiBufferSource buffers,
                                                              Frustum frustum, Vec3 camera, float partialTick) {
        TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
        int index = entity instanceof DestroyerPart part ? part.getIndex() : 0;
        Vec3 position = head.getBody().getRenderPosition(index, partialTick);
        /// 使用插值后的模型位置剔除，同时为侧翼和头甲预留空间。
        AABB box = entity.getBoundingBox();
        double margin = TheDestroyer.BODY_WIDTH * 2.0;
        double dx = position.x - entity.getX();
        double dy = position.y - entity.getY();
        double dz = position.z - entity.getZ();
        if (!frustum.isVisible(new AABB(box.minX + dx - margin, box.minY + dy - margin, box.minZ + dz - margin,
                box.maxX + dx + margin, box.maxY + dy + margin, box.maxZ + dz + margin))) return;
        poses.pushPose();
        poses.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        BakedGeoModel baked = getGeoModel().getBakedModel(getGeoModel().getModelResource((T) entity));
        GeoBone probe = entity instanceof DestroyerPart part
                ? probeBones.computeIfAbsent(baked, model -> model.getBone(part.isTail() ? "probe_3"
                : part.getIndex() % 2 == 0 ? "probe_2" : "probe_1")).orElse(null) : null;
        if (probe != null) probe.setHidden(head.isProbeReleased(index));
        boolean phaseOne = head.isPhaseOneSection(index);
        MultiBufferSource.BufferSource glowBuffers = phaseOne ? GlowBuffers.PHASE_ONE : GlowBuffers.PHASE_TWO;
        sectionGlow = glowBuffers.getBuffer(RenderStateShardAccessor.entityGlow(phaseOne ? PHASE1_GLOW_TEXTURE : GLOW_TEXTURE));
        int tick = head.getBeamTick();
        float elapsed = tick + partialTick - (81 - index) * 100.0F / 82.0F;
        sectionBlueAlpha = tick < 0 ? 0 : Math.max(0, 1.0F - elapsed / 30.0F);
        if (!phaseOne && elapsed >= 0 && sectionBlueAlpha > 0) {
            sectionBlueGlow = GlowBuffers.BLUE.getBuffer(RenderStateShardAccessor.entityGlow(BLUE_GLOW_TEXTURE));
        }
        try {
            Vec3 lightPosition = position.add(0, TheDestroyer.BODY_HEIGHT / 2.0, 0);
            int light = EntityLightSampler.sample(lightPosition,
                    pos -> getBlockLightLevel((T) entity, pos), pos -> getSkyLightLevel((T) entity, pos));
            MultiBufferSource.BufferSource bodyBuffers = phaseOne
                    ? GlowBuffers.BODY_PHASE_ONE : GlowBuffers.BODY_PHASE_TWO;
            renderWithSampledLight((T) entity, entity.getYRot(), partialTick, poses, bodyBuffers, light);
        } finally {
            sectionGlow = null;
            sectionBlueGlow = null;
            if (probe != null) probe.setHidden(false);
        }
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            LevelRenderer.renderLineBox(poses, buffers.getBuffer(RenderType.lines()),
                    entity.getBoundingBox().move(entity.position().scale(-1)),
                    entity instanceof DestroyerPart ? 0.25F : 1.0F, 1.0F,
                    entity instanceof DestroyerPart ? 0.0F : 1.0F, 1.0F);
        }
        poses.popPose();
    }
}
