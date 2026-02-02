package org.confluence.terraentity.entity.proj;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.IAxisZRotate;

public class DemonScytheProj extends LineProj implements IAxisZRotate {
    public final Rotate rotate = new Rotate();

    public DemonScytheProj(EntityType<? extends LineProj> pEntityType, Level pLevel, MobEffectInstance effect) {
        super(pEntityType, pLevel, effect);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide) {
            rotateZ(rotate, this::getDeltaMovement, 0.0F, 0.125F); // 无重力影响
        }
    }

    @Override
    protected Vec3 warpSpeed(Vec3 speed) {
        if (tickCount > 10) {
            speed = getDeltaMovement();
            if (speed.lengthSqr() < 2.18300625) { // 60(mph) -> 44.325*2/3/20 = 1.4775(m/tick) -> 1.4775^2 = 2.18300625
                return speed.scale(1.1940371819652); // (1.06^70)^(1/23) = 1.1940371819652
            }
        }
        return speed;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
    }
}
