package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.monster.MartianOfficer;
import org.confluence.mod.common.entity.monster.OfficerShieldGeometry;
import org.joml.Vector3f;

/** 朝向内侧的护盾外壳，类似火星探测器顶盖，带有发光接缝、下降波纹与受击时的不透明度闪烁。 */
public final class MartianOfficerRenderer extends GeoNormalRenderer<MartianOfficer> {
    private static final int SEGMENTS = 24;
    private static final float RADIUS = OfficerShieldGeometry.RADIUS;
    private static final float[] HALF = {OfficerShieldGeometry.HALF_WIDTH, OfficerShieldGeometry.HALF_HEIGHT, OfficerShieldGeometry.HALF_WIDTH};
    private static final Vector3f[][][] FACES = buildFaces();
    private static final Vector3f[][] QUADS = buildQuads();
    private static final Vector3f[] OUTLINE_SURFACE = java.util.Arrays.stream(FACES).flatMap(java.util.Arrays::stream).flatMap(java.util.Arrays::stream).toArray(Vector3f[]::new);
    private static MultiBufferSource.BufferSource shieldBuffers;

    public MartianOfficerRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/martian_officer.geo.json"),
                Confluence.asResource("textures/entity/martian_officer.png"),
                Confluence.asResource("animations/entity/martian_officer.animation.json")), false, 1.3F, 0.0F);
    }

    @Override public boolean shouldRender(MartianOfficer entity, Frustum frustum, double x, double y, double z) {
        return super.shouldRender(entity, frustum, x, y, z)
                || entity.shouldRender(x, y, z) && frustum.isVisible(OfficerShieldGeometry.bounds(entity.position(), entity.getScale()).inflate(0.15));
    }

    @Override public void render(MartianOfficer entity, float yaw, float partialTick, PoseStack poses,
                                 MultiBufferSource buffers, int light) {
        super.render(entity, yaw, partialTick, poses, buffers, light);
        // 仅在所有实体材质绘制完成后再提交护盾外壳。
    }

    public static void renderShields(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (shieldBuffers == null) shieldBuffers = minecraft.renderBuffers().bufferSource();
        Vec3 camera = event.getCamera().getPosition();
        java.util.List<MartianOfficer> officers = new java.util.ArrayList<>();
        for (var entity : minecraft.level.entitiesForRendering()) {
            if (entity instanceof MartianOfficer officer && !officer.isRemoved() && !officer.isInvisible()
                    && officer.shouldRender(camera.x, camera.y, camera.z)
                    && event.getFrustum().isVisible(OfficerShieldGeometry.bounds(officer.position(), officer.getScale()).inflate(0.15))) officers.add(officer);
        }
        officers.sort(java.util.Comparator.comparingDouble((MartianOfficer officer) -> officer.distanceToSqr(camera)).reversed());
        for (MartianOfficer officer : officers) {
            if (!(minecraft.getEntityRenderDispatcher().getRenderer(officer) instanceof MartianOfficerRenderer renderer)) continue;
            PoseStack poses = event.getPoseStack();
            Vec3 origin = officer.getPosition(event.getPartialTick().getGameTimeDeltaPartialTick(false)).subtract(camera);
            poses.pushPose();
            poses.translate(origin.x, origin.y, origin.z);
            renderer.renderShield(officer, event.getPartialTick().getGameTimeDeltaPartialTick(false), poses, shieldBuffers);
            shieldBuffers.endBatch();
            poses.popPose();
        }
    }

    private void renderShield(MartianOfficer entity, float partialTick, PoseStack poses, MultiBufferSource buffers) {
        if (entity.isInvisible()) return;
        float formation = entity.shieldFormation(partialTick), dissolve = entity.shieldDissolve(partialTick);
        float visibility = entity.hasShield() ? formation : 1 - dissolve;
        if (visibility <= 0) return;
        float time = (entity.tickCount + partialTick) * 0.05F;
        float health = Mth.clamp(entity.getShieldHealth() / MartianOfficer.MAX_SHIELD, 0, 1);
        float impactAge = entity.shieldImpactAge(partialTick);
        // 恰好两次不透明度下降（第 3 与第 9 tick），并在第 12 tick 恢复正常。
        float hitFlicker = impactAge < 12 ? 0.5F + 0.5F * Mth.cos(impactAge * Mth.PI / 3) : 1;
        float opacity = visibility * health;
        poses.pushPose();
        poses.scale(entity.getScale(), entity.getScale(), entity.getScale());
        float size = entity.hasShield() ? 0.96F + formation * 0.04F : 1 + dissolve * 0.06F;
        // 将形成/消散缩放的锚点设在脚部而非护盾中心。
        poses.translate(0, OfficerShieldGeometry.BOTTOM_Y + HALF[1] * size, 0);
        poses.scale(size, size, size);

        // 与探测器的负尺寸顶盖立方体类似，实际外壳朝向内侧。
        // 背面剔除会移除其靠近相机的一面；远侧仍留在军官身后。
        VertexConsumer membrane = buffers.getBuffer(ShieldTypes.MEMBRANE);
        for (int f = 0; f < 6; f++) for (int u = 0; u < SEGMENTS; u++) for (int v = 0; v < SEGMENTS; v++) {
            for (Vector3f p : quad(f, u, v)) {
                // 正的时间相位使同相位的波峰向下移动。
                // 每三秒有两道柔和水平波纹穿过表面。
                float wave = 0.5F + 0.5F * Mth.sin(p.y * (Mth.TWO_PI / HALF[1]) + time * (Mth.TWO_PI / 3));
                float brightness = 0.42F + wave * 0.58F;
                float membraneOpacity = visibility * shieldOpacity(health, wave, impactAge);
                float build = entity.hasShield() ? Mth.clamp((formation * 2.6F - (p.y + HALF[1])) * 8, 0, 1) : 1;
                emit(membrane, poses, p, 1, (int) (80 * brightness),
                        (int) (210 * brightness), (int) (255 * brightness),
                        membraneOpacity * build);
            }
        }
        VertexConsumer glow = buffers.getBuffer(ShieldTypes.GLOW);
        // 将圆角体积投影到面向相机的单个平面上，然后仅描出其轮廓。
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 eyePosition = camera.subtract(entity.getPosition(partialTick)).scale(1.0 / entity.getScale())
                .subtract(0, OfficerShieldGeometry.BOTTOM_Y + HALF[1] * size, 0).scale(1.0 / size);
        Vector3f eye = new Vector3f((float) eyePosition.x, (float) eyePosition.y, (float) eyePosition.z);
        java.util.List<Vector3f> outline = ShieldOutlineProjection.outline(OUTLINE_SURFACE, eye);
        if (!outline.isEmpty()) {
            Vector3f planeNormal = new Vector3f(eye).normalize();
            for (int i = 0; i < outline.size(); i++) {
                Vector3f a = outline.get(i), b = outline.get((i + 1) % outline.size());
                ribbon(glow, poses, a, b, planeNormal, 0.021F, opacity * hitFlicker * 0.10F);
                ribbon(glow, poses, a, b, planeNormal, 0.004F, opacity * hitFlicker * (0.38F + 0.08F * Mth.sin(time * 1.8F)));
            }
        }
        poses.popPose();
    }

    private static Vector3f[] quad(int face, int u, int v) {
        return QUADS[(face * SEGMENTS + u) * SEGMENTS + v];
    }

    private static Vector3f[][] buildQuads() {
        Vector3f[][] quads = new Vector3f[6 * SEGMENTS * SEGMENTS][];
        for (int face = 0; face < 6; face++) for (int u = 0; u < SEGMENTS; u++) for (int v = 0; v < SEGMENTS; v++) {
        Vector3f[][] f = FACES[face];
        // 反转整个膜面，而不是额外添加第二层反向的装饰外壳。
        quads[(face * SEGMENTS + u) * SEGMENTS + v] = face % 2 == 0 ? new Vector3f[]{f[u][v + 1], f[u + 1][v + 1], f[u + 1][v], f[u][v]}
                : new Vector3f[]{f[u][v], f[u + 1][v], f[u + 1][v + 1], f[u][v + 1]};
        }
        return quads;
    }

    public static float shieldOpacity(float health, float wave, float impactAge) {
        float normal = Mth.clamp(0.85F * Mth.clamp(health, 0, 1) + (wave * 2 - 1) * 0.15F, 0, 1);
        float flicker = impactAge < 12 ? 0.5F + 0.5F * Mth.cos(impactAge * Mth.PI / 3) : 1;
        return Mth.lerp(flicker, Math.min(0.05F, normal), normal);
    }

    private static Vector3f core(Vector3f p) {
        Vector3f c = new Vector3f();
        for (int a = 0; a < 3; a++) c.setComponent(a, Mth.clamp(p.get(a), -HALF[a] + RADIUS, HALF[a] - RADIUS));
        return c;
    }
    private static Vector3f normal(Vector3f p) {
        Vector3f n = new Vector3f(p).sub(core(p));
        return n.lengthSquared() > 1.0E-8F ? n.normalize() : new Vector3f(0, 1, 0);
    }
    private static Vector3f project(Vector3f p) {
        Vector3f c = core(p);
        return normal(p).mul(RADIUS).add(c);
    }
    private static Vector3f[][][] buildFaces() {
        Vector3f[][][] faces = new Vector3f[6][SEGMENTS + 1][SEGMENTS + 1];
        for (int face = 0; face < 6; face++) {
            int axis = face / 2, ua = (axis + 1) % 3, va = (axis + 2) % 3;
            for (int u = 0; u <= SEGMENTS; u++) for (int v = 0; v <= SEGMENTS; v++) {
                Vector3f p = new Vector3f();
                p.setComponent(axis, HALF[axis] * (face % 2 == 0 ? 1 : -1));
                p.setComponent(ua, HALF[ua] * (2.0F * u / SEGMENTS - 1));
                p.setComponent(va, HALF[va] * (2.0F * v / SEGMENTS - 1));
                faces[face][u][v] = project(p);
            }
        }
        return faces;
    }

    private static void ribbon(VertexConsumer vertices, PoseStack poses, Vector3f a, Vector3f b, Vector3f planeNormal, float width, float alpha) {
        Vector3f side = new Vector3f(b).sub(a).cross(planeNormal);
        if (side.lengthSquared() < 1.0E-10F) return;
        side.normalize().mul(width);
        emit(vertices, poses, new Vector3f(a).add(side), 1.0F, 125, 225, 255, alpha);
        emit(vertices, poses, new Vector3f(b).add(side), 1.0F, 125, 225, 255, alpha);
        emit(vertices, poses, new Vector3f(b).sub(side), 1.0F, 125, 225, 255, alpha);
        emit(vertices, poses, new Vector3f(a).sub(side), 1.0F, 125, 225, 255, alpha);
    }
    private static void emit(VertexConsumer vertices, PoseStack poses, Vector3f p, float scale, int r, int g, int b, float alpha) {
        vertices.addVertex(poses.last().pose(), p.x * scale, p.y * scale + HALF[1] * (scale - 1), p.z * scale)
                .setColor(r, g, b, Mth.clamp((int) (alpha * 255), 0, 255));
    }

    private static final class ShieldTypes extends RenderStateShard {
        private static final RenderType MEMBRANE = type("negative_volume_membrane", true, false);
        private static final RenderType GLOW = type("projected_outline_glow", false, true);
        private static RenderType type(String name, boolean cull, boolean additive) {
            return RenderType.create("confluence_officer_shield_" + name,
                    DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 16384, false, true,
                    RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER)
                            .setTransparencyState(additive ? ADDITIVE_TRANSPARENCY : TRANSLUCENT_TRANSPARENCY)
                            .setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(cull ? CULL : NO_CULL)
                            .setWriteMaskState(COLOR_WRITE).createCompositeState(false));
        }
        private ShieldTypes() { super(null, null, null); }
    }
}
