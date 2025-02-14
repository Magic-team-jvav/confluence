package org.confluence.mod.common.entity.projectile.strip;

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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.util.ModUtils;

/**
 * 长条形射弹
 */
public abstract class StripedProjectile extends Projectile {
    private static final EntityDataAccessor<Boolean> DATA_IS_HEAD = SynchedEntityData.defineId(StripedProjectile.class, EntityDataSerializers.BOOLEAN);
    public double distForHeadRemove = 10.0;
    protected double distForCreateBody = 1.0;
    public int ticksForBodyRemove = 28;
    protected int frequencyForBodyCheckTouch = 5;
    private BlockPos startPos = BlockPos.ZERO;
    private double distO = -0.5;
    @OnlyIn(Dist.CLIENT)
    public float[] rot;

    public StripedProjectile(EntityType<? extends StripedProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public StripedProjectile(EntityType<? extends StripedProjectile> entityType, LivingEntity living) {
        this(entityType, living, new Vec3(living.getX(), living.getEyeY() - 0.1, living.getZ()));
    }

    public StripedProjectile(EntityType<? extends StripedProjectile> entityType, LivingEntity living, Vec3 pos) {
        this(entityType, living.level());
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
                onHitEntity((EntityHitResult) hitresult);
            }

            if (!level().isClientSide && getOwner() instanceof LivingEntity living) {
                double dist = Math.sqrt(blockPosition().distSqr(startPos));
                double delta = dist - distO;
                if (delta >= distForCreateBody) {
                    if (delta > distForHeadRemove) {
                        onRemove();
                    } else {
                        StripedProjectile body = createBody(living);
                        body.setDeltaMovement(vec3);
                        body.setHead(false);
                        level().addFreshEntity(body);
                        this.distO = dist;
                    }
                }
            }

            double offX = getX() + vec3.x;
            double offY = getY() + vec3.y;
            double offZ = getZ() + vec3.z;
            setPos(offX, offY, offZ);
        } else if (tickCount > ticksForBodyRemove) {
            onRemove();
        } else if (tickCount % frequencyForBodyCheckTouch == 0) {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F);
            if (hitResult != null) {
                onTouchEntity(hitResult);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            Entity entity = result.getEntity();
            if (entity.hurt(damageSources().indirectMagic(this, getOwner()), 2.5f)) {
                ModUtils.knockBackA2B(this, entity, 0.5, 0.2);
            }
        }
    }

    protected void onRemove() {
        discard();
    }

    protected abstract void onTouchEntity(EntityHitResult result);

    protected abstract StripedProjectile createBody(LivingEntity shooter);

    @OnlyIn(Dist.CLIENT)
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

    public void setHead(boolean is) {
        entityData.set(DATA_IS_HEAD, is);
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
        compound.putBoolean("IsHead", isHead());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.startPos = NbtUtils.readBlockPos(compound, "StartPos").orElse(blockPosition());
        this.tickCount = compound.getInt("Age");
        setHead(compound.getBoolean("IsHead"));
    }
}
