package org.confluence.mod.client.handler;

import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectileVisualBridge;
import org.confluence.mod.common.item.sword.SwordProjectileParticleEffect;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;


/// 处理剑气的客户端粒子与 ParticleStorm 发射器。
public final class SwordProjectileVisualHandler implements SwordProjectileVisualBridge.Handler {
    private static final SwordProjectileVisualHandler INSTANCE = new SwordProjectileVisualHandler();

    public static void install() {
        SwordProjectileVisualBridge.install(INSTANCE);
    }

    @Override
    public void tick(SwordProjectile projectile) {
        spawn(projectile, SwordProjectileParticleEffect.Event.TRAIL);
    }

    @Override
    public void entityHit(SwordProjectile projectile) {
        spawn(projectile, SwordProjectileParticleEffect.Event.ENTITY_HIT);
    }

    @Override
    public void blockHit(SwordProjectile projectile) {
        spawn(projectile, SwordProjectileParticleEffect.Event.BLOCK_HIT);
    }

    private void spawn(SwordProjectile projectile, SwordProjectileParticleEffect.Event event) {
        if (projectile.getProjectileComponent() == null) return;
        for (SwordProjectileParticleEffect effect : projectile.getProjectileComponent().particleEffects()) {
            if (effect.event() != event) continue;
            ParticleEmitter emitter = new ParticleEmitter(projectile.level(), projectile.position(), effect.emitter());
            emitter.attachEntity(projectile);
            emitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
    }

    private SwordProjectileVisualHandler() {}
}
