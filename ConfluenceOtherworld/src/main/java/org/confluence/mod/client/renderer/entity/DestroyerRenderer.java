package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
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

import java.io.IOException;
import java.util.*;
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

    // 各材质独占缓冲，同一帧内所有体节共用，待本体写入深度后提交。
    private static final MultiBufferSource.BufferSource PHASE1_GLOW_BATCH = MultiBufferSource.immediate(new BufferBuilder(65536));
    private static final MultiBufferSource.BufferSource GLOW_BATCH = MultiBufferSource.immediate(new BufferBuilder(65536));
    private static final MultiBufferSource.BufferSource BLUE_GLOW_BATCH = MultiBufferSource.immediate(new BufferBuilder(65536));

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
                ResourceLocation texture = getTextureResource(entity);
                var batch = PHASE1_GLOW_TEXTURE.equals(texture) ? PHASE1_GLOW_BATCH : GLOW_BATCH;
                activeMask = glowMask(texture);
                MeshKey solidKey = meshKey;
                Mesh solidMesh = cachedMesh;
                Matrix4f solidAnchor = meshAnchor;
                int solidLight = meshLight;
                List<MeshPiece> solidPieces = new ArrayList<>(meshPieces);
                if (solidKey != null) {
                    meshKey = new MeshKey(solidKey.model(), solidKey.released(), texture);
                    cachedMesh = MESHES.get(meshKey);
                    meshAnchor = null;
                    meshPieces.clear();
                }
                try {
                    super.render(poses, entity, model, type, batch, buffer, partialTick, light, overlay);
                    if (meshKey != null && meshAnchor != null) {
                        Mesh mesh = cachedMesh;
                        if (mesh == null && !meshPieces.isEmpty()) {
                            mesh = uploadMesh(meshPieces, meshAnchor);
                            MESHES.put(meshKey, mesh);
                        }
                        if (mesh != null) {
                            GLOW_DRAWS.computeIfAbsent(getRenderType(entity), ignored -> new IdentityHashMap<>())
                                    .computeIfAbsent(mesh, ignored -> new ArrayList<>())
                                    .add(new Draw(new Matrix4f(RenderSystem.getModelViewMatrix()).mul(meshAnchor), LightTexture.FULL_BRIGHT));
                        }
                    }
                } finally {
                    meshKey = solidKey;
                    cachedMesh = solidMesh;
                    meshAnchor = solidAnchor;
                    meshLight = solidLight;
                    meshPieces.clear();
                    meshPieces.addAll(solidPieces);
                    activeMask = null;
                }
            }
        });
        addRenderLayer(new EntityGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(T entity) {return BLUE_GLOW_TEXTURE;}

            @Override
            public void render(PoseStack poses, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
                TheDestroyer head = entity instanceof DestroyerPart part ? part.getParent() : (TheDestroyer) entity;
                int index = entity instanceof DestroyerPart part ? part.getIndex() : 0;
                if (head.isPhaseOneSection(index)) return;
                int tick = head.getBeamTick();
                float elapsed = tick + partialTick - (81 - index) * 100.0F / 82.0F;
                float alpha = tick < 0 ? 0 : Math.max(0, 1.0F - elapsed / 30.0F);
                if (elapsed < 0 || alpha <= 0) return;
                RenderType glow = getRenderType(entity);
                activeMask = glowMask(BLUE_GLOW_TEXTURE);
                MeshKey solidKey = meshKey;
                meshKey = null;
                try {
                    getRenderer().reRender(model, poses, BLUE_GLOW_BATCH, entity, glow, BLUE_GLOW_BATCH.getBuffer(glow), partialTick, LightTexture.FULL_BRIGHT, overlay, 1, 1, 1, alpha);
                } finally {
                    meshKey = solidKey;
                    activeMask = null;
                }
            }
        });
    }

    // 实体渲染器随客户端资源重载重新创建，纹理掩码和面缓存随实例失效。
    private final Map<ResourceLocation, GlowMask> masks = new HashMap<>();
    private GlowMask activeMask;

    private record MeshKey(ResourceLocation model, boolean released, ResourceLocation glow) {}

    private record MeshPiece(BoneMesh mesh, Matrix4f pose) {}

    private record Mesh(VertexBuffer buffer) {}

    private record Draw(Matrix4f pose, int light) {}

    private static final Map<MeshKey, Mesh> MESHES = new HashMap<>();
    private static final Map<RenderType, Map<Mesh, List<Draw>>> DRAWS = new HashMap<>();
    private static final Map<RenderType, Map<Mesh, List<Draw>>> GLOW_DRAWS = new HashMap<>();
    private static final Map<BakedGeoModel, Optional<GeoBone>> PROBE_BONES = new IdentityHashMap<>();
    private MeshKey meshKey;
    private Mesh cachedMesh;
    private Matrix4f meshAnchor;
    private int meshLight;
    private final List<MeshPiece> meshPieces = new ArrayList<>();

    public static void clearDestroyerMeshes() {
        MESHES.values().forEach(mesh -> mesh.buffer().close());
        MESHES.clear();
        DRAWS.clear();
        GLOW_DRAWS.clear();
        PROBE_BONES.clear();
    }

    private static Mesh uploadMesh(List<MeshPiece> pieces, Matrix4f anchor) {
        Matrix4f inverse = new Matrix4f(anchor).invert();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        for (MeshPiece piece : pieces) {
            piece.mesh().transform(new Matrix4f());
            Matrix4f transform = new Matrix4f(inverse).mul(piece.pose());
            org.joml.Matrix3f normalTransform = new org.joml.Matrix3f(transform).invert().transpose();
            for (GeoCube cube : piece.mesh().cubes()) {
                for (GeoQuad face : cube.quads()) {
                    Vector3f normal = normalTransform.transform(new Vector3f(face.normal()));
                    RenderUtils.fixInvertedFlatCube(cube, normal);
                    for (GeoVertex vertex : face.vertices()) {
                        Vector3f position = transform.transformPosition(vertex.position(), new Vector3f());
                        builder.vertex(position.x, position.y, position.z, 1, 1, 1, 1, vertex.texU(), vertex.texV(),
                                OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT, normal.x, normal.y, normal.z);
                    }
                }
            }
        }
        VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try {
            buffer.bind();
            buffer.upload(builder.end());
            return new Mesh(buffer);
        } catch (RuntimeException e) {
            buffer.close();
            throw e;
        } finally {
            VertexBuffer.unbind();
        }
    }

    private static void drawMeshes(Map<RenderType, Map<Mesh, List<Draw>>> draws) {
        try {
            for (var material : draws.entrySet()) {
                material.getKey().setupRenderState();
                try {
                    var shader = RenderSystem.getShader();
                    for (var entry : material.getValue().entrySet()) {
                        entry.getKey().buffer().bind();
                        org.lwjgl.opengl.GL20.glDisableVertexAttribArray(4);
                        try {
                            for (Draw draw : entry.getValue()) {
                                org.lwjgl.opengl.GL30.glVertexAttribI2i(4, draw.light() & 65535, draw.light() >>> 16 & 65535);
                                entry.getKey().buffer().drawWithShader(draw.pose(), RenderSystem.getProjectionMatrix(), shader);
                            }
                        } finally {
                            org.lwjgl.opengl.GL20.glEnableVertexAttribArray(4);
                        }
                    }
                } finally {
                    VertexBuffer.unbind();
                    material.getKey().clearRenderState();
                }
            }
        } finally {
            draws.clear();
        }
    }

    private final Map<GeoBone, BoneMesh> solidBones = new IdentityHashMap<>();

    private GlowMask glowMask(ResourceLocation texture) {
        return masks.computeIfAbsent(texture, GlowMask::load);
    }

    private record GlowMask(int width, int height, BitSet pixels, Map<GeoBone, BoneMesh> bones) {
        static GlowMask load(ResourceLocation texture) {
            try (var input = Minecraft.getInstance().getResourceManager().open(texture);
                 NativeImage image = NativeImage.read(input)) {
                int width = image.getWidth(), height = image.getHeight();
                BitSet pixels = new BitSet();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if ((image.getPixelRGBA(x, y) >>> 24) != 0) pixels.set(y * width + x);
                    }
                }
                return new GlowMask(width, height, pixels, new IdentityHashMap<>());
            } catch (IOException exception) {
                return new GlowMask(0, 0, new BitSet(), new IdentityHashMap<>());
            }
        }

        boolean visible(GeoQuad quad) {
            if (width == 0) return true;
            float u0 = 1, v0 = 1, u1 = 0, v1 = 0;
            for (GeoVertex vertex : quad.vertices()) {
                u0 = Math.min(u0, vertex.texU());
                v0 = Math.min(v0, vertex.texV());
                u1 = Math.max(u1, vertex.texU());
                v1 = Math.max(v1, vertex.texV());
            }
            int left = (int) Math.floor(u0 * width) - 1, right = (int) Math.ceil(u1 * width);
            int top = (int) Math.floor(v0 * height) - 1, bottom = (int) Math.ceil(v1 * height);
            // 边缘可能环绕采样，保守保留。
            boolean visible = left < 0 || top < 0 || right >= width || bottom >= height;
            for (int y = top; !visible && y <= bottom; y++) {
                int first = pixels.nextSetBit(y * width + left);
                visible = first >= 0 && first <= y * width + right;
            }
            return visible;
        }
    }

    // 只烘焙相对于骨骼的固定变换，保留骨骼动画、显隐和定位器更新。
    private record BoneMesh(GeoCube[] cubes, Vector3f[] localPositions,
                            Vector3f[] transformedPositions) {
        void transform(Matrix4f pose) {
            for (int i = 0; i < localPositions.length; i++) {
                pose.transformPosition(localPositions[i], transformedPositions[i]);
            }
        }
    }

    private static BoneMesh bakeBone(GeoBone bone, GlowMask mask) {
        var cubes = new ArrayList<GeoCube>();
        // UV 和法线仍按面保存；共享同一角点的面复用变换后的坐标。
        Map<Vector3f, Vector3f> corners = new java.util.LinkedHashMap<>();
        PoseStack poses = new PoseStack();
        for (GeoCube cube : bone.getCubes()) {
            if (cube.quads() == null) continue;
            poses.pushPose();
            RenderUtils.translateToPivotPoint(poses, cube);
            RenderUtils.rotateMatrixAroundCube(poses, cube);
            RenderUtils.translateAwayFromPivotPoint(poses, cube);
            var faces = new ArrayList<GeoQuad>();
            for (GeoQuad face : cube.quads()) {
                if (face == null || (mask != null && !mask.visible(face))) continue;
                GeoVertex[] vertices = new GeoVertex[face.vertices().length];
                for (int i = 0; i < vertices.length; i++) {
                    GeoVertex source = face.vertices()[i];
                    Vector3f position = poses.last().pose().transformPosition(source.position(), new Vector3f());
                    Vector3f transformed = corners.computeIfAbsent(position, ignored -> new Vector3f());
                    vertices[i] = new GeoVertex(transformed, source.texU(), source.texV());
                }
                Vector3f normal = poses.last().normal().transform(face.normal(), new Vector3f());
                faces.add(new GeoQuad(vertices, normal, face.direction()));
            }
            poses.popPose();
            if (!faces.isEmpty())
                cubes.add(new GeoCube(faces.toArray(GeoQuad[]::new), Vec3.ZERO, Vec3.ZERO,
                        cube.size(), cube.inflate(), cube.mirror()));
        }
        return new BoneMesh(cubes.toArray(GeoCube[]::new), corners.keySet().toArray(Vector3f[]::new),
                corners.values().toArray(Vector3f[]::new));
    }

    @Override
    public void renderCubesOfBone(PoseStack poses, GeoBone bone, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (bone.isHidden() || bone.getCubes().isEmpty()) return;
        if (meshKey != null && cachedMesh != null && meshKey.glow() == null) {
            if (meshAnchor == null) {
                meshAnchor = new Matrix4f(poses.last().pose());
                meshLight = light;
            }
            return;
        }
        Map<GeoBone, BoneMesh> cache = activeMask == null ? solidBones : activeMask.bones();
        BoneMesh mesh = cache.get(bone);
        if (mesh == null) {
            mesh = bakeBone(bone, activeMask);
            cache.put(bone, mesh);
        }
        if (meshKey != null) {
            if (mesh.cubes().length > 0) {
                if (cachedMesh == null) {
                    Matrix4f pose = new Matrix4f(poses.last().pose());
                    if (meshAnchor == null) {
                        meshAnchor = pose;
                        meshLight = light;
                    }
                    meshPieces.add(new MeshPiece(mesh, pose));
                } else if (meshAnchor == null) {
                    meshAnchor = new Matrix4f(poses.last().pose());
                    meshLight = light;
                }
            }
            return;
        }
        var pose = poses.last();
        Vector3f normal = new Vector3f();
        mesh.transform(pose.pose());
        for (GeoCube cube : mesh.cubes()) {
            for (GeoQuad face : cube.quads()) {
                pose.normal().transform(face.normal(), normal);
                RenderUtils.fixInvertedFlatCube(cube, normal);
                for (GeoVertex vertex : face.vertices()) {
                    Vector3f position = vertex.position();
                    buffer.vertex(position.x, position.y, position.z, red, green, blue, alpha, vertex.texU(), vertex.texV(),
                            overlay, light, normal.x, normal.y, normal.z);
                }
            }
        }
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
                part.setDeltaMovement(motion.scale(0.3).offsetRandom(level.random, 1.6F));
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
        drawMeshes(DRAWS);
        PHASE1_GLOW_BATCH.endBatch();
        GLOW_BATCH.endBatch();
        drawMeshes(GLOW_DRAWS);
        BLUE_GLOW_BATCH.endBatch();
        for (TheDestroyer head : destroyers) {
            if (!head.isRemoved())
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
        if (!frustum.isVisible(entity.getBoundingBox().move(position.x - entity.getX(),
                position.y - entity.getY(), position.z - entity.getZ()).inflate(TheDestroyer.BODY_WIDTH * 2.0)))
            return;
        poses.pushPose();
        poses.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        BakedGeoModel baked = getGeoModel().getBakedModel(getGeoModel().getModelResource((T) entity));
        GeoBone probe = null;
        if (entity instanceof DestroyerPart part) {
            Optional<GeoBone> cachedProbe = PROBE_BONES.get(baked);
            if (cachedProbe == null) {
                cachedProbe = baked.getBone(part.isTail() ? "probe_3" : part.getIndex() % 2 == 0 ? "probe_2" : "probe_1");
                PROBE_BONES.put(baked, cachedProbe);
            }
            probe = cachedProbe.orElse(null);
        }
        if (probe != null) probe.setHidden(head.isProbeReleased(index));
        if (entity instanceof DestroyerPart part && !part.isTail()) {
            meshKey = new MeshKey(getGeoModel().getModelResource((T) entity), head.isProbeReleased(index), null);
            cachedMesh = MESHES.get(meshKey);
            meshAnchor = null;
            meshPieces.clear();
        }
        try {
            // 父类会按插值位置采样光照，此处无需提前查询一次。
            super.render((T) entity, entity.getYRot(), partialTick, poses, buffers, 0);
            if (meshKey != null && meshAnchor != null) {
                Mesh mesh = cachedMesh;
                if (mesh == null && !meshPieces.isEmpty()) {
                    mesh = uploadMesh(meshPieces, meshAnchor);
                    MESHES.put(meshKey, mesh);
                }
                if (mesh != null) {
                    RenderType type = getRenderType((T) entity, getGeoModel().getTextureResource((T) entity), buffers, partialTick);
                    DRAWS.computeIfAbsent(type, ignored -> new IdentityHashMap<>())
                            .computeIfAbsent(mesh, ignored -> new ArrayList<>())
                            .add(new Draw(new Matrix4f(RenderSystem.getModelViewMatrix()).mul(meshAnchor), meshLight));
                }
            }
        } finally {
            meshKey = null;
            cachedMesh = null;
            meshAnchor = null;
            meshPieces.clear();
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
