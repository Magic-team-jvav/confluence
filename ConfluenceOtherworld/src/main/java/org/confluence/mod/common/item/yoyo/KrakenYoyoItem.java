package org.confluence.mod.common.item.yoyo;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEffectProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;

public final class KrakenYoyoItem extends YoyoItem {
    public KrakenYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.YELLOW, 30, 23.75F, 0xFFFFFFFF, 0, 4.3F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        YoyoSession session = YoyoSession.of(owner);
        if (session.isSpecialHit(4)) {
            for (int i = 0; i < 3; i++) {
                Vec3 direction = target.getBoundingBox().getCenter().subtract(yoyo.position()).normalize().yRot((i - 1) * 0.3F);
                YoyoEffectProjectile.shoot(yoyo, YoyoEffectProjectile.Kind.WAVE, direction, target);
            }
        }
        session.countSpecialHit();
    }

    @Override
    public double hitRadius() {return 1.85;}

    @Override
    public float duplicateSpeedMultiplier() {return 0.75F;}

    @Override
    public float bonusCriticalChance() {return 0.1F;}

    @Override
    public boolean fullBright() {return true;}

    @Override
    public void tickVisual(YoyoEntity yoyo) {
        if (yoyo.tickCount % 2 != 0) return;
        for (int i = 0; i < 12; i++) {
            double angle = yoyo.tickCount * 0.3 + i * Math.PI / 6;
            yoyo.level().addParticle(ParticleTypes.SPLASH, yoyo.getX() + Math.cos(angle) * 2.095,
                    yoyo.getY() + yoyo.getBbHeight() * 0.5, yoyo.getZ() + Math.sin(angle) * 2.095,
                    -Math.sin(angle) * 0.05, 0, Math.cos(angle) * 0.05);
        }
    }

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.waves";}
}
