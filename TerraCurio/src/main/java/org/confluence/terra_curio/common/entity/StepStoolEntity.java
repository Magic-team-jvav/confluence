package org.confluence.terra_curio.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terra_curio.common.init.TCEntities;
import org.confluence.terra_curio.network.s2c.StepStoolSteppingPacketS2C;

import javax.annotation.Nullable;
import java.util.UUID;

public class StepStoolEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_STEP_ID = SynchedEntityData.defineId(StepStoolEntity.class, EntityDataSerializers.INT);
    private static final Vec3 GRAVITY = new Vec3(0.0, -0.08, 0.0);
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;

    public StepStoolEntity(EntityType<StepStoolEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.blocksBuilding = true;
    }

    public StepStoolEntity(Player player) {
        this(TCEntities.STEP_STOOL.get(), player.level());
        setPos(player.getX(), player.getY(), player.getZ());
        setOwner(player);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            Entity owner = getOwner();
            if (owner == null || owner.getVehicle() != null) {
                discard();
            }
        }
        addDeltaMovement(GRAVITY);
        move(MoverType.SELF, getDeltaMovement());
    }



    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        if (getOwner() instanceof ServerPlayer serverPlayer) {
            StepStoolSteppingPacketS2C.resetStep(serverPlayer);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_STEP_ID, 1);
    }

    public void setStep(int step) {
        entityData.set(DATA_STEP_ID, step);
    }

    public int getStep() {
        return entityData.get(DATA_STEP_ID);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_STEP_ID.equals(pKey)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    public void setOwner(@Nullable Entity pOwner) {
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
        this.cachedOwner = pOwner;
    }

    @Override
    @Nullable
    public Entity getOwner() {
        if (cachedOwner != null && !cachedOwner.isRemoved()) {
            return cachedOwner;
        } else if (ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            this.cachedOwner = serverLevel.getEntity(ownerUUID);
            return cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.cachedOwner = null;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (ownerUUID != null) {
            pCompound.putUUID("Owner", ownerUUID);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return super.getDimensions(pPose).scale(1.0F, getStep());
    }

    @Override
    public void updateFluidHeightAndDoFluidPushing() {}
}
