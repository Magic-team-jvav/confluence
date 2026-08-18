package org.confluence.mod.client.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.component.SwordProjectileParticleEffect;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectileVisualBridge;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/// 处理剑气的客户端粒子与 ParticleStorm 发射器。
public final class SwordProjectileVisualHandler implements SwordProjectileVisualBridge.Handler {
    private static final SwordProjectileVisualHandler INSTANCE = new SwordProjectileVisualHandler();
    private static final Map<SwordProjectile, Set<ResourceLocation>> ACTIVE_EMITTERS = new WeakHashMap<>();

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

    private static void spawn(SwordProjectile projectile, SwordProjectileParticleEffect.Event event) {
        if (projectile.getProjectileComponent() == null) return;
        Vec3 motion = projectile.getDeltaMovement();
        for (SwordProjectileParticleEffect effect : projectile.getProjectileComponent().particleEffects()) {
            if (effect.event() != event || event == SwordProjectileParticleEffect.Event.TRAIL && projectile.tickCount % effect.interval() != 0)
                continue;
            if (event == SwordProjectileParticleEffect.Event.TRAIL)
                effect.emitter().ifPresent(id -> startEmitter(projectile, id));
            effect.particle().ifPresent(particle -> {
                for (int index = 0; index < Math.min(effect.count(), 64); index++) {
                    projectile.level().addParticle(particle,
                            projectile.getX() + projectile.getRandom().nextGaussian() * effect.spread(),
                            projectile.getY() + projectile.getRandom().nextGaussian() * effect.spread(),
                            projectile.getZ() + projectile.getRandom().nextGaussian() * effect.spread(),
                            motion.x * effect.velocityScale(), motion.y * effect.velocityScale(), motion.z * effect.velocityScale());
                }
            });
        }
    }

    private static void startEmitter(SwordProjectile projectile, ResourceLocation id) {
        if (!ACTIVE_EMITTERS.computeIfAbsent(projectile, key -> new HashSet<>()).add(id)) return;
        ParticleEmitter emitter = new ParticleEmitter(projectile.level(), projectile.position(), id);
        emitter.attachEntity(projectile);
        emitter.hideOutline = true;
        MolangParticleEngine.INSTANCE.addEmitter(emitter);
    }

    private SwordProjectileVisualHandler() {}
}
