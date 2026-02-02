package org.confluence.terra_curio.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.IAxisZRotate;

import java.util.HashSet;
import java.util.Set;

public class XBoneProjectile extends Projectile implements IAxisZRotate {
    public final Rotate rotate = new Rotate();
    private int collideCount = 0;
    private final Set<Entity> passThrough = new HashSet<>();

    public XBoneProjectile(EntityType<XBoneProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (!(getOwner() instanceof LivingEntity owner)) {
            discard();
            return;
        }
        super.tick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3.add(0.0, -getDefaultGravity(), 0.0));
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            if (this.collideCount++ >= 3) {
                discard();
                return;
            }
        }
        setDeltaMovement(motion.scale(0.96).add(0.0, -getDefaultGravity(), 0.0));

        if (level().isClientSide) {
            rotateZ(rotate, this::getDeltaMovement, (float) getDefaultGravity(), 0.125F);
        } else {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            if (ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F) instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                entity.hurt(damageSources().mobProjectile(this, owner), 5);
                if (passThrough.add(entity) && passThrough.size() >= 3) {
                    discard();
                }
            }
        }
    }

    @Override
    protected void updateRotation() {
        if (rotate.different()) {
            super.updateRotation();
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = getOwner();
        if (!target.canBeHitByProjectile() || target instanceof ArmorStand || target instanceof Npc) return false;
        return owner == null || (owner != target && !owner.isPassengerOfSameVehicle(target));
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.collideCount = compound.getInt("CollideCount");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CollideCount", collideCount);
    }
}
