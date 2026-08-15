package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModParticleTypes;

import javax.annotation.Nullable;

/**
 * 魔光剑剑气。
 *
 * <p>这里不再表现为一把随机游走的飞剑，而是在玩家视线前方短暂展开一段暗影判定。
 * 命中、击退和伤害仍然复用剑气通用快照；显示则交给客户端渲染器和粒子。</p>
 */
public class LightBaneProjectile extends AreaSwordProjectile {
    public LightBaneProjectile(EntityType<LightBaneProjectile> entityType, Level pLevel) {
        super(entityType, pLevel, 2.4, 1.45, 1.0, 0.65, -0.15);
        hitCount = 99999;
    }

    @Override
    protected boolean doHurt(Entity target) {
        if (super.doHurt(target)) {
            ((ServerLevel) level()).sendParticles(
                    ModParticleTypes.LIGHT_BANE.get(),
                    target.getX(),
                    target.getY() + target.getBbHeight() * 0.5,
                    target.getZ(),
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0);
            return true;
        }
        return false;
    }

    @Override
    protected void clientTickVisuals(Vec3 center, Vec3 forward, Vec3 right, Vec3 up) {
        if (tickCount % 2 != 0) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            double side = (random.nextDouble() - 0.5) * 2.4;
            double height = (random.nextDouble() - 0.5) * 1.1;
            double depth = (random.nextDouble() - 0.5) * 0.8;
            Vec3 position = center.add(right.scale(side)).add(up.scale(height)).add(forward.scale(depth));
            ParticleOptions particle = getTrailParticle();
            level().addParticle(
                    particle,
                    position.x,
                    position.y,
                    position.z,
                    -forward.x * 0.01,
                    0.0,
                    -forward.z * 0.01);
        }
    }

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return random.nextBoolean() ? ModParticleTypes.LIGHT_BANE_FADE.get() : ModParticleTypes.LIGHT_BANE_DUST.get();
    }
}
