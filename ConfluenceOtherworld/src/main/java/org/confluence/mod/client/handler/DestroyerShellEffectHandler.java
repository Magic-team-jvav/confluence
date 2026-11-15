package org.confluence.mod.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.entity.DestroyerRenderer;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.util.ArrayList;
import java.util.List;

public final class DestroyerShellEffectHandler {
    private static final int MAX_ACTIVE_PARTS = 2048;
    // 装甲片在空中保留的时间，单位 tick。
    private static final int LIFETIME_TICKS = 80;
    private static final float BURST_SPEED_SCALE = 0.15F;
    private static final float AIR_DRAG = 0.97F;
    // 装甲片每 tick 向下增加的速度。
    private static final double GRAVITY = 0.08;
    private static final float MODEL_CENTER_Y = 14.0F / 16.0F;
    public static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/boss/destroyer_phase1.png"));
    private static final List<ArmorPiece> active = new ArrayList<>();
    private static ClientLevel currentLevel;

    private DestroyerShellEffectHandler() {}

    public static void spawn(TheDestroyer boss, DestroyerRenderer<TheDestroyer> renderer, List<GeoCube> cubes,
                             int index, float centerZ, int limit, float speed) {
        if (!(boss.level() instanceof ClientLevel level)) return;
        useLevel(level);
        Quaternionf rotation = new Quaternionf(boss.getBody().getRenderRotation(index, 1.0F));
        Vec3 origin = boss.getBody().getRenderPosition(index, 1.0F);
        int count = Math.min(cubes.size(), limit);
        for (int i = 0; i < count && active.size() < MAX_ACTIVE_PARTS; i++) {
            GeoCube cube = cubes.get(i);
            // 从体节中心朝装甲片方向喷开，叠加少量随机偏移，避免整片沿同一方向飞走。
            Vector3f center = cubeCenter(cube);
            Vector3f direction = new Vector3f(center.x, center.y - MODEL_CENTER_Y, center.z - centerZ);
            direction.add((level.random.nextFloat() - 0.5F) * 0.5F,
                    (level.random.nextFloat() - 0.5F) * 0.5F,
                    (level.random.nextFloat() - 0.5F) * 0.5F);
            if (direction.lengthSquared() < 0.0001F) direction.set(0, 1, 0);
            direction.normalize().rotate(rotation).mul(speed * BURST_SPEED_SCALE * (0.75F + level.random.nextFloat() * 0.5F));
            Vec3 velocity = new Vec3(direction.x, direction.y, direction.z).add(boss.getDeltaMovement().scale(0.3));
            active.add(new ArmorPiece(renderer, cube, center, origin, velocity, rotation, centerZ,
                    (level.random.nextFloat() - 0.5F) * 0.13F,
                    (level.random.nextFloat() - 0.5F) * 0.13F));
        }
    }

    public static void tick(ClientLevel level) {
        useLevel(level);
        if (level == null) return;
        active.removeIf(part -> ++part.age >= LIFETIME_TICKS);
        for (ArmorPiece part : active) {
            part.previous = part.position;
            part.position = part.position.add(part.velocity);
            part.velocity = part.velocity.scale(AIR_DRAG).add(0, -GRAVITY, 0);
        }
    }

    public static void render(PoseStack poses, MultiBufferSource buffers, Frustum frustum, Vec3 camera, float partialTick) {
        if (active.isEmpty()) return;
        VertexConsumer buffer = buffers.getBuffer(RENDER_TYPE);
        for (ArmorPiece part : active) {
            Vec3 pos = part.previous.lerp(part.position, partialTick);
            if (!frustum.isVisible(new AABB(pos, pos).inflate(6))) continue;
            poses.pushPose();
            poses.translate(pos.x - camera.x, pos.y - camera.y, pos.z - camera.z);
            poses.mulPose(part.rotation);
            poses.scale(TheDestroyer.MODEL_SCALE, TheDestroyer.MODEL_SCALE, TheDestroyer.MODEL_SCALE);
            poses.translate(0, TheDestroyer.BODY_HEIGHT / (2.0F * TheDestroyer.MODEL_SCALE) - MODEL_CENTER_Y, -part.centerZ);
            float px = part.center.x;
            float py = part.center.y;
            float pz = part.center.z;
            poses.translate(px, py, pz);
            poses.mulPose(new Quaternionf().rotationXYZ(part.spinX * (part.age + partialTick), part.spinY * (part.age + partialTick), 0));
            poses.translate(-px, -py, -pz);
            part.renderer.renderCube(poses, part.cube, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);
            poses.popPose();
        }
    }

    private static Vector3f cubeCenter(GeoCube cube) {
        Vector3f center = new Vector3f();
        int count = 0;
        for (GeoQuad quad : cube.quads()) {
            if (quad == null) continue;
            for (GeoVertex vertex : quad.vertices()) {
                center.add(vertex.position());
                count++;
            }
        }
        return count == 0 ? center : center.div(count);
    }

    private static void useLevel(ClientLevel level) {
        if (currentLevel != level) {
            reset();
            currentLevel = level;
        }
    }

    public static void reset() {
        active.clear();
        currentLevel = null;
    }

    private static final class ArmorPiece {
        final DestroyerRenderer<TheDestroyer> renderer;
        final GeoCube cube;
        final Vector3f center;
        final Quaternionf rotation;
        final float centerZ, spinX, spinY;
        Vec3 previous, position, velocity;
        int age;

        ArmorPiece(DestroyerRenderer<TheDestroyer> renderer, GeoCube cube, Vector3f center, Vec3 position, Vec3 velocity,
                   Quaternionf rotation, float centerZ, float spinX, float spinY) {
            this.renderer = renderer;
            this.cube = cube;
            this.center = center;
            this.previous = this.position = position;
            this.velocity = velocity;
            this.rotation = rotation;
            this.centerZ = centerZ;
            this.spinX = spinX;
            this.spinY = spinY;
        }
    }
}
