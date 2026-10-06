package org.confluence.mod.client.summoner.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.common.summoner.particle.ZenithParticleOptions;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ZenithParticle extends TextureSheetParticle {

    private static final int TRAIL_LENGTH = 7;

    private final SpriteSet spriteSet;
    private final float baseScale;
    private final int color;
    private final Vec3[] trail = new Vec3[TRAIL_LENGTH];
    private final Vec3[] corners = new Vec3[4];
    private final Vec3 axis;
    private final float rollSpeed;

    public ZenithParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet, ZenithParticleOptions options) {
        super(level, x, y, z, vx, vy, vz);
        this.spriteSet = spriteSet;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.friction = options.friction();
        this.gravity = 0.0F;
        this.quadSize = options.scale();
        this.baseScale = this.quadSize;
        this.lifetime = options.lifetime();
        this.color = options.color();
        this.alpha = 1F;
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
        Arrays.fill(this.trail, new Vec3(x, y, z));
        Vec3 direction = new Vec3(vx, vy, vz);
        direction = direction.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 1.0) : direction.normalize();
        Vec3 right = direction.cross(new Vec3(0.0, 1.0, 0.0));
        if (right.lengthSqr() < 1.0E-6) {
            right = direction.cross(new Vec3(1.0, 0.0, 0.0));
        }
        right = right.normalize();
        Vec3 up = right.cross(direction);
        this.axis = direction;
        this.corners[0] = right.add(up);
        this.corners[1] = up.subtract(right);
        this.corners[2] = this.corners[0].scale(-1.0);
        this.corners[3] = this.corners[1].scale(-1.0);
        this.rollSpeed = RandomSource.create().nextIntBetweenInclusive(9, 18);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
        System.arraycopy(this.trail, 0, this.trail, 1, TRAIL_LENGTH - 1);
        this.trail[0] = new Vec3(this.xo, this.yo, this.zo);
        this.oRoll = this.roll;
        this.roll += this.rollSpeed;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();

        float currantRoll = Mth.lerp(partialTick, this.oRoll, this.roll);
        float cosRoll = Mth.cos(currantRoll);
        float sinRoll = Mth.sin(currantRoll);
        float axisX = (float) this.axis.x, axisY = (float) this.axis.y, axisZ = (float) this.axis.z;
        float[] rolled = new float[12];
        for (int i = 0; i < 4; i++) {
            Vec3 corner = this.corners[i];
            float ox = (float) corner.x, oy = (float) corner.y, oz = (float) corner.z;
            float projection = axisX * ox + axisY * oy + axisZ * oz;
            rolled[i * 3] = ox * cosRoll + (axisY * oz - axisZ * oy) * sinRoll + axisX * projection * (1.0F - cosRoll);
            rolled[i * 3 + 1] = oy * cosRoll + (axisZ * ox - axisX * oz) * sinRoll + axisY * projection * (1.0F - cosRoll);
            rolled[i * 3 + 2] = oz * cosRoll + (axisX * oy - axisY * ox) * sinRoll + axisZ * projection * (1.0F - cosRoll);
        }
        double x = Mth.lerp(partialTick, this.xo, this.x);
        double y = Mth.lerp(partialTick, this.yo, this.y);
        double z = Mth.lerp(partialTick, this.zo, this.z);

        float hx = (float) (x - cameraPos.x);
        float hy = (float) (y - cameraPos.y);
        float hz = (float) (z - cameraPos.z);

        Vec3 oldest = this.trail[TRAIL_LENGTH - 1];
        Vec3 newer = this.trail[TRAIL_LENGTH - 2];
        float tx = (float) (Mth.lerp(partialTick, oldest.x, newer.x) - cameraPos.x);
        float ty = (float) (Mth.lerp(partialTick, oldest.y, newer.y) - cameraPos.y);
        float tz = (float) (Mth.lerp(partialTick, oldest.z, newer.z) - cameraPos.z);
        float progress = Mth.lerp(partialTick, this.age - 1, this.age) / Math.max(1, this.lifetime - 1);
        float zoom = Mth.clamp((1.0F - progress) * (1.0F - progress), 0.0F, 1.0F);
        float size = this.baseScale * zoom;
        float dx = (float) this.axis.x * size;
        float dy = (float) this.axis.y * size;
        float dz = (float) this.axis.z * size;
        tx = (hx - dx) + (tx - hx + dx) * zoom;
        ty = (hy - dy) + (ty - hy + dy) * zoom;
        tz = (hz - dz) + (tz - hz + dz) * zoom;
        float tailDepth = (tx - hx) * (float) this.axis.x + (ty - hy) * (float) this.axis.y + (tz - hz) * (float) this.axis.z;
        int argb = 0xFF000000 | this.color;
        int light = getLightColor(partialTick);
        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();

        DynamicLightDispatcher.INSTANCE.addLightSource(new Vec3(x, y, z), zoom * 0.5F);

        float[] front = new float[12];
        float[] back = new float[12];
        for (int i = 0; i < 4; i++) {
            float cx = hx + rolled[i * 3] * size;
            float cy = hy + rolled[i * 3 + 1] * size;
            float cz = hz + rolled[i * 3 + 2] * size;
            front[i * 3] = cx + dx;
            front[i * 3 + 1] = cy + dy;
            front[i * 3 + 2] = cz + dz;
            back[i * 3] = cx - dx;
            back[i * 3 + 1] = cy - dy;
            back[i * 3 + 2] = cz - dz;
        }
        for (int i = 0; i < 4; i++) {
            int from = i * 3;
            int to = ((i + 1) & 3) * 3;
            vertex(buffer, front[from], front[from + 1], front[from + 2], u0, v0, argb, light);
            vertex(buffer, front[to], front[to + 1], front[to + 2], u1, v0, argb, light);
            vertex(buffer, back[to], back[to + 1], back[to + 2], u1, v1, argb, light);
            vertex(buffer, back[from], back[from + 1], back[from + 2], u0, v1, argb, light);
        }
        vertex(buffer, front[0], front[1], front[2], u0, v0, argb, light);
        vertex(buffer, front[9], front[10], front[11], u1, v0, argb, light);
        vertex(buffer, front[6], front[7], front[8], u1, v1, argb, light);
        vertex(buffer, front[3], front[4], front[5], u0, v1, argb, light);
        if (tailDepth < -size) {
            for (int i = 0; i < 4; i++) {
                int from = i * 3;
                int to = ((i + 1) & 3) * 3;
                vertex(buffer, back[from], back[from + 1], back[from + 2], u0, v0, argb, light);
                vertex(buffer, back[to], back[to + 1], back[to + 2], u1, v0, argb, light);
                vertex(buffer, tx, ty, tz, u1, v1, argb, light);
                vertex(buffer, tx, ty, tz, u0, v1, argb, light);
            }
        } else {
            vertex(buffer, back[0], back[1], back[2], u0, v0, argb, light);
            vertex(buffer, back[3], back[4], back[5], u1, v0, argb, light);
            vertex(buffer, back[6], back[7], back[8], u1, v1, argb, light);
            vertex(buffer, back[9], back[10], back[11], u0, v1, argb, light);
        }
    }

    private static void vertex(VertexConsumer buffer, float x, float y, float z, float u, float v, int argb, int light) {
        buffer.vertex(x, y, z)
                .uv(u, v)
                .color(argb)
                .uv2(light)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }
}
