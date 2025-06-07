package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class HurtnadoProjectile extends AbstractManaProjectile implements Immunity {
    private final Set<Entity> passThrough = new HashSet<>();
    private Entity target;
    public float rotateO = 0.0F;
    public float rotate = 0.0F;

    public HurtnadoProjectile(EntityType<HurtnadoProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HurtnadoProjectile(LivingEntity living) {
        this(ModEntities.HURTNADO_PROJECTILE.get(), living.level());
        setOwner(living);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    public void baseTick() {
        if (tickCount > 190) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3.add(0.0, -0.04, 0.0));
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
        }
        setDeltaMovement(motion.add(0.0, -0.04, 0.0));

        if (level().isClientSide) {
            if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
            this.rotateO = rotate;
            this.rotate += Mth.PI * 0.15F;
        } else {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            if (ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F) instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity.hurt(getDamagesource(), getCalculatedDamage())) {
                    VectorUtils.knockBackA2B(this, entity, 0.5, 0.2);
                }
                if (passThrough.add(entity) && passThrough.size() >= 14) {
                    discard();
                    return;
                }
            }
        }

        if (target == null || target.isRemoved()) {
            this.target = getNearestEnemy();
        }
        if (target != null) {
            setDeltaMovement(getDeltaMovement().scale(0.96).add(VectorUtils.getVectorA2B(this, target).scale(0.05)));
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
    public Types confluence$getImmunityType() {
        return Types.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 8;
    }
}
