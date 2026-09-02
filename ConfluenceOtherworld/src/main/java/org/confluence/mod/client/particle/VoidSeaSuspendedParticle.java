package org.confluence.mod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import static org.confluence.mod.client.util.ClientVoidSeaConstants.*;

public class VoidSeaSuspendedParticle extends TextureSheetParticle {
    protected VoidSeaSuspendedParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = Mth.nextInt(this.random, SUSPENDED_PARTICLE_MIN_LIFETIME, SUSPENDED_PARTICLE_MAX_LIFETIME);
        this.quadSize = SUSPENDED_PARTICLE_SIZE;
        this.setColor(SUSPENDED_PARTICLE_RED, SUSPENDED_PARTICLE_GREEN, SUSPENDED_PARTICLE_BLUE);
        this.setAlpha(SUSPENDED_PARTICLE_ALPHA);
        this.setSprite(sprites.get(this.random));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new VoidSeaSuspendedParticle(level, x, y, z, sprites);
        }
    }
}
