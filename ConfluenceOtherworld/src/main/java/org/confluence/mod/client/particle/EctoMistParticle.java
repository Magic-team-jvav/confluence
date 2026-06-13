package org.confluence.mod.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EctoMistParticle extends SimpleTextureSheetParticle {
    private final float nu1;
    private final float size;
    private final float ry;

    public EctoMistParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        this.gravity = 0;
        this.lifetime += 40;
        int shrink = random.nextInt(8);
        this.nu1 = sprite.getU1() - (sprite.getU1() - sprite.getU0()) * 0.125F * shrink;
        this.size = (float) (8 - shrink);
        this.ry = (float) Math.random() * Mth.TWO_PI;
        double v = (Math.random() - 0.5) * 0.01 + 0.01;
        float r = ry + (random.nextBoolean() ? Mth.HALF_PI : Mth.HALF_PI + Mth.PI);
        this.xd = v * Mth.sin(r);
        this.yd = 0;
        this.zd = v * Mth.cos(r);
        scale(4);
        setAlpha(0);
    }

    @Override
    public void tick() {
        super.tick();
        float delta = (float) age / lifetime;
        setAlpha(delta < 0.4F ? Mth.lerp(delta / 0.4F, 0, 1) : (delta > 0.6F ? Mth.lerp((1 - delta) / 0.4F, 0, 1) : 1));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected float getU1() {
        return nu1;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 vec3 = renderInfo.getPosition();
        float f = (float)(Mth.lerp(partialTicks, xo, x) - vec3.x());
        float f1 = (float)(Mth.lerp(partialTicks, yo, y) - vec3.y());
        float f2 = (float)(Mth.lerp(partialTicks, zo, z) - vec3.z());
        Quaternionf quaternionf = new Quaternionf().rotationY(ry);

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-size, -1.0F, 0.0F), new Vector3f(-size, 1.0F, 0.0F), new Vector3f(size, 1.0F, 0.0F), new Vector3f(size, -1.0F, 0.0F)};
        float f3 = getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = getU0();
        float f7 = getU1();
        float f4 = getV0();
        float f5 = getV1();
        int j = getLightColor(partialTicks);
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new EctoMistParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
