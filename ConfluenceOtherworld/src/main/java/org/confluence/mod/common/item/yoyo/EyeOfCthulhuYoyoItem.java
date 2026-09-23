package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.CthulhuEyeProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public final class EyeOfCthulhuYoyoItem extends YoyoItem {
    private final float specialDamageMultiplier;
    private final int hitInterval;
    private final float projectileDamageMultiplier;
    private final double projectileSpeed;
    private final double projectileTargetRange;

    public EyeOfCthulhuYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int hitInterval, float specialDamageMultiplier, float projectileDamageMultiplier, double projectileSpeed, double projectileTargetRange) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.specialDamageMultiplier = specialDamageMultiplier;
        this.hitInterval = hitInterval;
        this.projectileDamageMultiplier = projectileDamageMultiplier;
        this.projectileSpeed = projectileSpeed;
        this.projectileTargetRange = projectileTargetRange;
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        YoyoSession session = YoyoSession.of(owner);
        if (session.isSpecialHit(hitInterval))
            new CthulhuEyeProjectile(ModEntities.CTHULHU_EYE_PROJECTILE.get(), yoyo.level()).shootAtNearest(yoyo, target, projectileDamageMultiplier, projectileSpeed, projectileTargetRange);
        session.countSpecialHit();
    }

    @Override
    public float hitMultiplier(ServerPlayer owner) {return YoyoSession.of(owner).isSpecialHit(hitInterval) ? specialDamageMultiplier : 1;}

    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.afterimage", hitInterval);}
}
