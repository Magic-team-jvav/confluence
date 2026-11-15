package org.confluence.mod.client.renderer.entity.bullet.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

public final class ChlorophyteImpactVfx implements BulletImpactVfx {
    public static final ChlorophyteImpactVfx INSTANCE = new ChlorophyteImpactVfx();
    private static final int COUNT = 9;

    @Override
    public void play(Vec3 position) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        for (int index = 0; index < COUNT; index++) {
            double angle = Math.PI * 2.0D * index / COUNT;
            double speed = 0.05D + level.random.nextDouble() * 0.045D;
            level.addParticle(ParticleTypes.ENCHANTED_HIT,
                    position.x + (level.random.nextDouble() - 0.5D) * 0.32D,
                    position.y + (level.random.nextDouble() - 0.5D) * 0.32D,
                    position.z + (level.random.nextDouble() - 0.5D) * 0.32D,
                    Math.cos(angle) * speed, 0.02D + level.random.nextDouble() * 0.08D, Math.sin(angle) * speed);
        }
    }

    private ChlorophyteImpactVfx() {}
}
