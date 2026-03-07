package org.confluence.mod.mixin.integration.particlestorm;

import com.llamalad7.mixinextras.sugar.Local;
import org.confluence.mod.integration.particlestorm.ParticleStormHelper;
import org.joml.Vector3f;
import org.mesdag.particlestorm.data.component.EmitterShape;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmitterShape.class)
public class EmitterShapeMixin {
    @Inject(method = "emittingParticle", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;mul(Lorg/joml/Vector3fc;)Lorg/joml/Vector3f;"))
    private void rotateSpeed(ParticleEmitter emitter, CallbackInfo ci, @Local(name = "speed") Vector3f speed) {
        ParticleStormHelper.rotateSpeed(emitter, speed);
    }
}
