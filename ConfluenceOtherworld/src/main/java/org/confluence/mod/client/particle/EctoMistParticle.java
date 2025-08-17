package org.confluence.mod.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

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
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected float getU1() {
        return nu1;
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        float f = getQuadSize(partialTicks);
        float f1 = getU0();
        float f2 = getU1();
        float f3 = getV0();
        float f4 = getV1();
        int i = getLightColor(partialTicks);
        renderVertex(buffer, quaternion, x, y, z, size, -1.0F, f, f2, f4, i);
        renderVertex(buffer, quaternion, x, y, z, size, 1.0F, f, f2, f3, i);
        renderVertex(buffer, quaternion, x, y, z, -size, 1.0F, f, f1, f3, i);
        renderVertex(buffer, quaternion, x, y, z, -size, -1.0F, f, f1, f4, i);
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (quaternion, camera, partialTick) -> quaternion.rotationY(ry);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new EctoMistParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
