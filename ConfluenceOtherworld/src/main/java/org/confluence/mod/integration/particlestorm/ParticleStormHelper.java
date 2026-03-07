package org.confluence.mod.integration.particlestorm;

import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class ParticleStormHelper {
    public static void rotateSpeed(ParticleEmitter emitter, Vector3f speed) {
        speed.rotateY(Mth.PI * 1.5F - emitter.rot.y);
    }
}
