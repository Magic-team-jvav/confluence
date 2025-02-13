package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.util.ModUtils;

public class VilethronProjectile extends Projectile {
    private static final EntityDataAccessor<Boolean> DATA_IS_HEAD = SynchedEntityData.defineId(VilethronProjectile.class, EntityDataSerializers.BOOLEAN);
    private BlockPos startPos = BlockPos.ZERO;
    private double distSqrO = -0.5;
    public float[] rot;

    public VilethronProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public VilethronProjectile(LivingEntity living) {
        this(living, new Vec3(living.getX(), living.getEyeY() - 0.1, living.getZ()));
    }

    public VilethronProjectile(LivingEntity living, Vec3 pos) {
        super(ModEntities.VILETHRON_PROJECTILE.get(), living.level());
        setOwner(living);
        setNoGravity(true);
        setPos(pos);
        this.startPos = blockPosition();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_IS_HEAD, true);
    }

    @Override
    public void tick() {
        super.tick();
        if (isHead()) {
            Vec3 vec3 = getDeltaMovement();
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            checkInsideBlocks();
            if (hitresult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitresult;
                if (level().isClientSide) return;
                Entity entity = entityHitResult.getEntity();
                if (entity.hurt(damageSources().indirectMagic(this, getOwner()), 2)) {
                    ModUtils.knockBackA2B(this, entity, 0.5, 0.2);
                }
            }

            if (!level().isClientSide && getOwner() instanceof LivingEntity living) {
                double distSqr = blockPosition().distSqr(startPos);
                double delta = distSqr - distSqrO;
                if (delta >= 1.0) {
                    if (delta > 100.0) {
                        discard();
                    } else {
                        VilethronProjectile vilethron = new VilethronProjectile(living, position());
                        vilethron.setDeltaMovement(vec3);
                        vilethron.entityData.set(DATA_IS_HEAD, false);
                        level().addFreshEntity(vilethron);
                        this.distSqrO = distSqr;
                    }
                }
            }

            double offX = getX() + vec3.x;
            double offY = getY() + vec3.y;
            double offZ = getZ() + vec3.z;
            setPos(offX, offY, offZ);
        } else if (tickCount > 28) {
            discard();
        } else if (!level().isClientSide && tickCount % 5 == 0) {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F);
            if (hitResult != null) {
                hitResult.getEntity().hurt(damageSources().indirectMagic(this, getOwner()), 2);
            }
        }
    }

    public float[] getRot() {
        if (rot == null) {
            updateRotation();
            setDeltaMovement(Vec3.ZERO);
            this.rot = new float[]{getYRot() * Mth.DEG_TO_RAD, getXRot() * Mth.DEG_TO_RAD};
        }
        return rot;
    }

    public boolean isHead() {
        return entityData.get(DATA_IS_HEAD);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target.canBeHitByProjectile() && target != getOwner();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("StartPos", NbtUtils.writeBlockPos(startPos));
        compound.putInt("Age", tickCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.startPos = NbtUtils.readBlockPos(compound, "StartPos").orElse(blockPosition());
        this.tickCount = compound.getInt("Age");
    }
}
