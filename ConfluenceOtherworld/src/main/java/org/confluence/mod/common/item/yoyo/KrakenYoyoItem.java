package org.confluence.mod.common.item.yoyo;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.KrakenWaveProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public final class KrakenYoyoItem extends YoyoItem {
    private final double hitRadius;
    private final float duplicateSpeedMultiplier;
    private final float bonusCriticalChance;
    private final int hitInterval;
    private final int waveCount;
    private final float projectileDamageMultiplier;
    private final double projectileSpeed;

    public KrakenYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int hitInterval, int waveCount, double hitRadius, float duplicateSpeedMultiplier, float bonusCriticalChance, float projectileDamageMultiplier, double projectileSpeed) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.hitRadius = hitRadius;
        this.duplicateSpeedMultiplier = duplicateSpeedMultiplier;
        this.bonusCriticalChance = bonusCriticalChance;
        this.hitInterval = hitInterval;
        this.waveCount = waveCount;
        this.projectileDamageMultiplier = projectileDamageMultiplier;
        this.projectileSpeed = projectileSpeed;
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        YoyoSession session = YoyoSession.of(owner);
        if (session.isSpecialHit(hitInterval)) {
            for (int i = 0; i < waveCount; i++) {
                Vec3 direction = target.getBoundingBox().getCenter().subtract(yoyo.position()).normalize().yRot((i - (waveCount - 1) * 0.5F) * 0.3F);
                new KrakenWaveProjectile(ModEntities.KRAKEN_WAVE.get(), yoyo.level()).shoot(yoyo, direction, target, projectileDamageMultiplier, projectileSpeed);
            }
        }
        session.countSpecialHit();
    }

    @Override
    public double hitRadius() {return hitRadius;}

    @Override
    public float duplicateSpeedMultiplier() {return duplicateSpeedMultiplier;}

    @Override
    public float bonusCriticalChance() {return bonusCriticalChance;}

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
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.waves", hitInterval, waveCount);}
}
