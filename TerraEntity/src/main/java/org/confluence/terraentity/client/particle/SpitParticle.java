package org.confluence.terraentity.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpitParticle extends RisingParticle {
    private final SpriteSet sprites;
    protected boolean isGlowing;

    protected SpitParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.scale(1.5F);
        this.setSpriteFromAge(sprites);
        this.lifetime  = 8;
    }

    public int getLightColor(float partialTick) {
        return this.isGlowing ? 240 : super.getLightColor(partialTick);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.quadSize = 1 -  this.age / (float)this.lifetime;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SpitParticle soulparticle = new SpitParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            soulparticle.setAlpha(1.0F);
            return soulparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EmissiveProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public EmissiveProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SpitParticle soulparticle = new SpitParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            soulparticle.setAlpha(1.0F);
            soulparticle.isGlowing = true;
            return soulparticle;
        }
    }
}
