package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class CloudProjectile extends AbstractManaProjectile implements GeoEntity {
    protected static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(CloudProjectile.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    protected UUID targetUUID;
    protected transient LivingEntity target;
    protected transient double motionY;
    private EntityType<? extends RainProjectile> rainType;
    private int duration;
    private int maxPenetrate;

    public CloudProjectile(EntityType<? extends CloudProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public CloudProjectile(EntityType<? extends CloudProjectile> cloudType, EntityType<? extends RainProjectile> rainType, LivingEntity living, int duration, int maxPenetrate) {
        this(cloudType, living.level());
        this.rainType = rainType;
        this.duration = duration;
        this.maxPenetrate = maxPenetrate;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder.define(DATA_TARGET_ID, -114514));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_TARGET_ID.equals(key)) {
            if (level().isClientSide && level().getEntity(entityData.get(DATA_TARGET_ID)) instanceof LivingEntity living) {
                this.target = living;
            }
            if (target != null) {
                double d = target.distanceTo(this);
                double h = getY() - target.getY() - target.getBbHeight();
                double v0 = getDefaultVelocity();
                double vy = getDeltaMovement().y;
                this.motionY = 2 * v0 * v0 * (2 - h) / (d * d) - 2 * vy * v0 / d;
            }
        }
    }

    @Override
    public void baseTick() {
        if (tickCount > 5 * 60 * 20 || getOwner() == null || getOwner().position().distanceToSqr(position()) > 64 * 64) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3);
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            setTarget(null);
            motion = Vec3.ZERO;
        }
        setDeltaMovement(motion);

        if (motion.x == 0 && motion.y == 0 && motion.z == 0) {
            if (!level().isClientSide && (duration <= 1 || level().getGameTime() % duration == 0)) {
                LivingEntity owner = getLivingOwner();
                if (owner != null) {
                    float width = getDimensions(getPose()).width() * 1.2F;
                    RainProjectile entity = new RainProjectile(rainType, owner, position().add(
                            (random.nextFloat() - 0.5F) * width,
                            -1,
                            (random.nextFloat() - 0.5F) * width
                    ));
                    entity.setMaxPenetrate(maxPenetrate);
                    entity.setDamage(getDamage());
                    level().addFreshEntity(entity);
                }
            }
        } else if (getTarget() != null && !target.isRemoved()) {
            if (Mth.square(getX() - target.getX()) + Mth.square(getZ() - target.getZ()) < 4) {
                setPos(target.position().add(0, target.getBbHeight() + 2, 0));
                setDeltaMovement(Vec3.ZERO);
                setTarget(null);
            } else {
                setDeltaMovement(motion.add(0, motionY, 0));
            }
        } else {
            setTarget(null);
        }
    }

    @Override
    protected void doHitCheck() {}

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
        if (target == null) {
            this.targetUUID = null;
            entityData.set(DATA_TARGET_ID, -114514);
        } else {
            this.targetUUID = target.getUUID();
            entityData.set(DATA_TARGET_ID, target.getId());
        }
    }

    public @Nullable LivingEntity getTarget() {
        if (target == null && targetUUID != null && level() instanceof ServerLevel level) {
            Entity entity = level.getEntity(targetUUID);
            if (entity instanceof LivingEntity living) {
                this.target = living;
            }
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("TargetUUID")) {
            this.targetUUID = compound.getUUID("TargetUUID");
        }
        try {
            ResourceLocation type = ResourceLocation.tryParse(compound.getString("RainType"));
            this.rainType = (EntityType<? extends RainProjectile>) BuiltInRegistries.ENTITY_TYPE.get(type);
        } catch (Exception ignored) {
            this.rainType = ModEntities.BLOOD_RAIN.get();
        }
        this.duration = compound.getInt("Duration");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (targetUUID != null) {
            compound.putUUID("TargetUUID", targetUUID);
        }
        if (rainType == null) {
            compound.putString("RainType", "confluence:blood_rain_projectile");
        } else {
            compound.putString("RainType", BuiltInRegistries.ENTITY_TYPE.getKey(rainType).toString());
        }
        compound.putInt("Duration", duration);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}
