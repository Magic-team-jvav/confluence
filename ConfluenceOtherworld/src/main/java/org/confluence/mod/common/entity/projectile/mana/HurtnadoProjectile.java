package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class HurtnadoProjectile extends AbstractManaProjectile implements Immunity {
    private static final WeaponTargeting TARGETING = new WeaponTargeting(TargetPolicy.ENEMY_ONLY);

    private Entity target;
    public float rotateO = 0.0F;
    public float rotate = 0.0F;

    public HurtnadoProjectile(EntityType<HurtnadoProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HurtnadoProjectile(LivingEntity living) {
        this(ModEntities.HURTNADO.get(), living.level());
    }

    @Override
    public void baseTick() {
        if (tickCount > 190) {
            if (!level().isClientSide) {
                discardInTicks(1);
            }
            return;
        }
        super.baseTick();
        doBouncyMove(true, this::doNothing, UnaryOperator.identity());

        if (level().isClientSide) {
            if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
            this.rotateO = rotate;
            this.rotate += Mth.PI * 0.15F;
        }

        if (target == null || target.isRemoved()) {
            this.target = getNearestEnemy();
        }
        if (TARGETING.canKeep(getOwner(), target)) {
            setDeltaMovement(getDeltaMovement().scale(0.96).add(LibMathUtils.getVectorA2B(this, target).scale(0.05)));
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void doHitCheck() {
        AABB boundingBox = getBoundingBox().inflate(1.0);
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F);
        if (hitResult == null) return;
        Entity entity = hitResult.getEntity();
        doHurtAndKnockback(entity, 0.5, 0.2);
        if (doPenetrateCheck(entity)) {
            doDiscardInMaxPenetrate(14);
        }
    }

    private @Nullable Entity getNearestEnemy() {
        return LibEntityUtils.nearest(level().getEntities(this, new AABB(blockPosition()).inflate(15.5)),
                candidate -> TARGETING.canAcquire(getOwner(), candidate),
                java.util.Comparator.comparingDouble(candidate -> candidate.distanceToSqr(getX(), getY(), getZ())));
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 8;
    }
}
