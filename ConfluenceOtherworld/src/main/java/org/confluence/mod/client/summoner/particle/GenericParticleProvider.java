package org.confluence.mod.client.summoner.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.confluence.mod.common.summoner.particle.GenericParticleOptions;
import org.jetbrains.annotations.NotNull;

public class GenericParticleProvider implements ParticleProvider<GenericParticleOptions> {

    private final SpriteSet spriteSet;

    public GenericParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(@NotNull GenericParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        return GenericParticle.createWithOptions(level, x, y, z, vx, vy, vz, this.spriteSet, options);
    }
}
