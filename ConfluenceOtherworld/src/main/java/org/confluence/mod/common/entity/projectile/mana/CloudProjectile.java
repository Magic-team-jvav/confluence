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
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
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
    private EntityType<? extends RainProjectile> rainType;
    private int duration;

    public CloudProjectile(EntityType<? extends CloudProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public CloudProjectile(EntityType<? extends CloudProjectile> cloudType, EntityType<? extends RainProjectile> rainType, LivingEntity living, int duration) {
        this(cloudType, living.level());
        this.rainType = rainType;
        this.duration = duration;
        setOwner(living);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder.define(DATA_TARGET_ID, -114514));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && DATA_TARGET_ID.equals(key)) {
            Entity entity = level().getEntity(entityData.get(DATA_TARGET_ID));
            if (entity instanceof LivingEntity living) {
                this.target = living;
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
                    RainProjectile entity = new RainProjectile(rainType, owner, position().add(
                            (random.nextFloat() - 0.5F) * 2,
                            -1,
                            (random.nextFloat() - 0.5F) * 2
                    ));
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
                Vec3 added = motion.add(VectorUtils.getVectorA2B(this, target).scale(0.2).add(0, 0.2, 0));
                float maxVelocity = getDefaultVelocity();
                if (added.lengthSqr() > maxVelocity * maxVelocity) {
                    setDeltaMovement(added.normalize().scale(maxVelocity));
                }
            }
        } else {
            setTarget(null);
        }
    }

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
            this.rainType = ModEntities.BLOOD_RAIN_PROJECTILE.get();
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