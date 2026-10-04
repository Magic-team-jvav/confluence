package org.confluence.mod.client.summoner.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.confluence.mod.common.summoner.particle.ZenithParticleOptions;
import org.jetbrains.annotations.NotNull;

public class ZenithParticleProvider implements ParticleProvider<ZenithParticleOptions> {

    private final SpriteSet spriteSet;

    public ZenithParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(@NotNull ZenithParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        return new ZenithParticle(level, x, y, z, vx, vy, vz, this.spriteSet, options);
    }
}
