package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DemonScytheProjectile extends AbstractManaProjectile implements IAxisZRotate {
    public final Rotate rotate = new Rotate();
    protected final Set<UUID> penetrateSet = new HashSet<>();

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

        this.tickCount++;
        if (tickCount > 10) {
            Vec3 vec3 = getDeltaMovement();
            if (vec3.lengthSqr() < 2.18300625) { // 60(mph) -> 44.325*2/3/20 = 1.4775(m/tick) -> 1.4775^2 = 2.18300625
                setDeltaMovement(vec3.scale(1.1940371819652)); // (1.06^70)^(1/23) = 1.1940371819652
            }
        }
        if (level().isClientSide) {
            rotateZ(rotate, this::getDeltaMovement, 0.0F, 0.125F); // 无重力影响
        }
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            discard();
            return;
        } else if (hitresult$type == HitResult.Type.ENTITY && !level().isClientSide) {
            Entity entity = ((EntityHitResult) hitresult).getEntity();
            if (!penetrateSet.contains(entity.getUUID())) {
                if (entity.hurt(getDamagesource(), getCalculatedDamage())) {
                    VectorUtils.knockBackA2B(this, entity, 0.5, 0.2);
                }
                penetrateSet.add(entity.getUUID());
                if (penetrateSet.size() == 5) {
                    discard();
                    return;
                }
            }
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);

        if (tickCount > 200) discard();
    }
}
