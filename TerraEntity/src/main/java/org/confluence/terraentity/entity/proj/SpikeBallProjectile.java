package org.confluence.terraentity.entity.proj;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.entitiy.IBouncy;

public class SpikeBallProjectile extends BaseProj<SpikeBallProjectile> implements IBouncy {
    public Entity target;

    public SpikeBallProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        this(pEntityType, pLevel, null);
    }
    public SpikeBallProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel, Entity target) {
        super(pEntityType, pLevel, (MobEffectInstance) null);
        this.target = target;
        // 不然碰到方块没反弹就有1.4了
        canPenetrateBlock = true;
        penetration = 999999;
        accelerationPower = 0;
    }

    @Override
    public void tick() {
        super.tick();

        bounce(this::move, this::getDeltaMovement, this::setDeltaMovement, getDefaultGravity(), 0.99);
    }

    @Override
    public double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public int getLifetime() {
        return 200;
    }
}
