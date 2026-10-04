package org.confluence.mod.client.renderer.entity.bullet.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

public final class CrystalImpactVfx implements BulletImpactVfx {
    public static final CrystalImpactVfx INSTANCE = new CrystalImpactVfx();
    private static final int COUNT = 14;

    @Override
    public void play(Vec3 position) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        for (int index = 0; index < COUNT; index++) {
            double angle = Math.PI * 2.0D * index / COUNT;
            double speed = 0.025D + level.random.nextDouble() * 0.035D;
            level.addParticle(ParticleTypes.DOLPHIN,
                    position.x + (level.random.nextDouble() - 0.5D) * 0.18D,
                    position.y + (level.random.nextDouble() - 0.5D) * 0.18D,
                    position.z + (level.random.nextDouble() - 0.5D) * 0.18D,
                    Math.cos(angle) * speed, 0.015D + level.random.nextDouble() * 0.035D, Math.sin(angle) * speed);
        }
    }

    private CrystalImpactVfx() {}
}
