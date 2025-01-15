package org.confluence.mod.mixin.client.accessor;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {
    @Accessor
    void setRoll(float roll);

    @Accessor
    void setORoll(float oRoll);
}
