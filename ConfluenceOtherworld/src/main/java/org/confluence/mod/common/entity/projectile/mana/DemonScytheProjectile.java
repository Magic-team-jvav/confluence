package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.mod.common.init.ModEntities;

public class DemonScytheProjectile extends AbstractManaProjectile implements IAxisZRotate {
    public final Rotate rotate = new Rotate();

    public DemonScytheProjectile(EntityType<DemonScytheProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public DemonScytheProjectile(LivingEntity living) {
        this(ModEntities.DEMON_SCYTHE_PROJECTILE.get(), living.level());
        setOwner(living);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (tickCount > 10) {
            Vec3 vec3 = getDeltaMovement();
            if (vec3.lengthSqr() < 2.18300625) { // 60(mph) -> 44.325*2/3/20 = 1.4775(m/tick) -> 1.4775^2 = 2.18300625
                setDeltaMovement(vec3.scale(1.1940371819652)); // (1.06^70)^(1/23) = 1.1940371819652
            }
        }
        if (level().isClientSide) {
            rotateZ(rotate, getDeltaMovement().lengthSqr(), 0.125F); // 无重力影响
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);

        if (tickCount > 200) discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.5, 0.2);
            doDiscardInMaxPenetrate(5);
        }
    }
}
