package org.confluence.mod.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 1.20 侧复刻 1.21 的 ominous trial spawner detection 粒子。
public final class LuminiteImpactParticle extends TextureSheetParticle {
    private static final int BASE_LIFETIME = 8;
    private final SpriteSet sprites;

    private LuminiteImpactParticle(ClientLevel level, double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, 0.0D, 0.0D);
        this.sprites = sprites;
        friction = 0.96F;
        gravity = -0.1F;
        speedUpWhenYMotionIsBlocked = true;
        xd = xSpeed;
        yd = yd * 0.9D + ySpeed;
        zd = zSpeed;
        quadSize *= 1.125F;
        lifetime = Math.max((int) (BASE_LIFETIME
                / Mth.randomBetween(random, 0.5F, 1.0F) * 1.5F), 1);
        setSpriteFromAge(sprites);
        hasPhysics = true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);
    }

    @Override
    public float getQuadSize(float partialTick) {
        return quadSize * Mth.clamp((age + partialTick) / lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 cameraPosition = camera.getPosition();
        float x = (float) (Mth.lerp(partialTick, xo, this.x) - cameraPosition.x);
        float y = (float) (Mth.lerp(partialTick, yo, this.y) - cameraPosition.y);
        float z = (float) (Mth.lerp(partialTick, zo, this.z) - cameraPosition.z);
        Quaternionf cameraRotation = camera.rotation();
        Quaternionf rotation = new Quaternionf(0.0F, cameraRotation.y, 0.0F, cameraRotation.w);
        if (roll != 0.0F) {
            rotation.rotateZ(Mth.lerp(partialTick, oRoll, roll));
        }

        Vector3f[] vertices = {
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float size = getQuadSize(partialTick);
        for (Vector3f vertex : vertices) {
            vertex.rotate(rotation).mul(size).add(x, y, z);
        }
        int light = getLightColor(partialTick);
        vertex(buffer, vertices[0], getU1(), getV1(), light);
        vertex(buffer, vertices[1], getU1(), getV0(), light);
        vertex(buffer, vertices[2], getU0(), getV0(), light);
        vertex(buffer, vertices[3], getU0(), getV1(), light);
    }

    private void vertex(VertexConsumer buffer, Vector3f position, float u, float v, int light) {
        buffer.vertex(position.x(), position.y(), position.z())
                .uv(u, v)
                .color(rCol, gCol, bCol, alpha)
                .uv2(light)
                .endVertex();
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level,
                                                 double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed) {
            return new LuminiteImpactParticle(
                    level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
