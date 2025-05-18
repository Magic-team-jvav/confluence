package org.confluence.mod.common.entity.projectile.strip;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.ModUtils;

/**
 * 长条形射弹
 */
public abstract class StripedProjectile extends Projectile implements Immunity {
    private static final EntityDataAccessor<Boolean> DATA_IS_HEAD = SynchedEntityData.defineId(StripedProjectile.class, EntityDataSerializers.BOOLEAN);
    protected double distForHeadRemove = 10.0;
    protected double distForCreateBody = 0.95;
    protected int ticksForBodyRemove = 28;
    protected int frequencyForBodyCheckTouch = 5;
    private Vec3 startPos = Vec3.ZERO;
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
        this.startPos = pos;
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

            if (!level().isClientSide && getOwner() instanceof LivingEntity living) {
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() == HitResult.Type.ENTITY) {
                    onHitEntity((EntityHitResult) hitresult);
                }
                double dist = position().distanceTo(startPos);
                double delta = dist - distO;
                if (delta >= distForCreateBody) {
                    if (dist > distForHeadRemove) {
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

            checkInsideBlocks();
            double offX = getX() + vec3.x;
            double offY = getY() + vec3.y;
            double offZ = getZ() + vec3.z;
            setPos(offX, offY, offZ);
        } else if (!level().isClientSide) {
            if (tickCount > ticksForBodyRemove) {
                onRemove();
            } else {
                AABB boundingBox = getBoundingBox().inflate(1.0);
                EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F);
                if (hitResult != null) {
                    onTouchEntity(hitResult);
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity.hurt(getDamageSource(), 2.5f)) {
            VectorUtils.knockBackA2B(this, entity, 0.5, 0.2);
        }
    }

    protected DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
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
        return ModUtils.canHitEntity(target, getOwner());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("StartPos", Vec3.CODEC.encodeStart(NbtOps.INSTANCE, startPos).getOrThrow());
        compound.putInt("Age", tickCount);
        compound.putBoolean("IsHead", isHead());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.startPos = Vec3.CODEC.parse(NbtOps.INSTANCE, compound.get("StartPos")).getOrThrow();
        this.tickCount = compound.getInt("Age");
        setHead(compound.getBoolean("IsHead"));
    }

    @Override
    public Types confluence$getImmunityType(){
        return Types.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource){
        return frequencyForBodyCheckTouch;
    }
}
