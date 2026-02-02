package org.confluence.terraentity.entity.proj;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.boss.Skeletron;
import org.jetbrains.annotations.NotNull;

public class SkullProjectile extends BaseProj<SkullProjectile> {
    public Entity target;
    public float yHeadRot;
    public float yHeadRotO;
    public float xHeadRot;
    public float xHeadRotO;

    public SkullProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        this(pEntityType, pLevel, null);
    }
    public SkullProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel, Entity target) {
        super(pEntityType, pLevel, (MobEffectInstance) null);
        this.target = target;
        accelerationPower =0.03;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public int getLifetime() {
        return 100;
    }

    @Override
    public void tick() {
        if (target != null && !level().isClientSide) {
            Vec3 toTargetVec = target.position().add(0, 1, 0).subtract(position());
            Vec3 motion = getDeltaMovement();
            setDeltaMovement(toTargetVec.subtract(motion.scale(motion.dot(toTargetVec) / motion.dot(motion))).normalize().scale(0.025).add(motion));

            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                this.level(), this, position(), position().add(motion), this.getBoundingBox().expandTowards(motion).inflate(1), this::canHitEntity
            );
            if (entityHitResult != null) {
                entityHitResult.getEntity().hurt(level().damageSources().mobProjectile(this, (LivingEntity) getOwner()), damage);
            }
        }

        super.tick();
        Vec3 pos = position().add(getDeltaMovement());
        this.xHeadRotO = xHeadRot;
        this.yHeadRotO = yHeadRot;
        Vec3 vec3 = position();
        double d0 = pos.x - vec3.x;
        double d1 = pos.y - vec3.y;
        double d2 = pos.z - vec3.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        xHeadRot = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 180.0F / (float) Math.PI)));
        yHeadRot = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * 180.0F / (float) Math.PI) - 90.0F);
    }
    protected boolean canHitEntity(@NotNull Entity target) {
        return super.canHitEntity(target) && !(target instanceof Skeletron);
    }
}
