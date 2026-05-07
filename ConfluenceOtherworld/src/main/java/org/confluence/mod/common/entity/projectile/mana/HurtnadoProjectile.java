package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class HurtnadoProjectile extends AbstractManaProjectile implements Immunity {
    private Entity target;
    public float rotateO = 0.0F;
    public float rotate = 0.0F;

    public HurtnadoProjectile(EntityType<HurtnadoProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HurtnadoProjectile(LivingEntity living) {
        this(ModEntities.HURTNADO_PROJECTILE.get(), living.level());
    }

    @Override
    public void baseTick() {
        if (tickCount > 190) {
            discard();
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
        if (target != null) {
            setDeltaMovement(getDeltaMovement().scale(0.96).add(VectorUtils.getVectorA2B(this, target).scale(0.05)));
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
        double d0 = -1.0;
        Entity enemy = null;
        for (Entity entity : level().getEntities(this, new AABB(blockPosition()).inflate(15.5))) {
            if (entity instanceof Enemy) {
                double d1 = entity.distanceToSqr(getX(), getY(), getZ());
                if (d0 == -1.0 || d1 < d0) {
                    d0 = d1;
                    enemy = entity;
                }
            }
        }
        return enemy;
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
