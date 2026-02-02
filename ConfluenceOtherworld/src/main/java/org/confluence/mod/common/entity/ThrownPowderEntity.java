package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.ModEntities;

import java.util.HashSet;
import java.util.Set;

public class ThrownPowderEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_TYPE = SynchedEntityData.defineId(ThrownPowderEntity.class, EntityDataSerializers.INT);
    private BlockPos lastPos;
    private ISpreadable.Type type;
    //    private ParticleEmitter emitter;
    private final Set<BlockPos> coveredPos = new HashSet<>();

    public ThrownPowderEntity(EntityType<ThrownPowderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownPowderEntity(Level level, ISpreadable.Type type) {
        super(ModEntities.THROWN_POWDER.get(), level);
        this.type = type;
        setSpreadableType(type);
    }

    public void setSpreadableType(ISpreadable.Type type) {
        entityData.set(DATA_TYPE, type.ordinal());
    }

    public ISpreadable.Type getSpreadableType() {
        return ISpreadable.Type.byId(entityData.get(DATA_TYPE));
    }

    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity) {
        float cosX = Mth.cos(x * Mth.DEG_TO_RAD);
        float radY = y * Mth.DEG_TO_RAD;
        float f = -Mth.sin(radY) * cosX;
        float f1 = -Mth.sin((x + z) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(radY) * cosX;

        Vec3 vec3 = new Vec3(f, f1, f2).normalize().scale(velocity);
        setDeltaMovement(vec3);
        this.hasImpulse = true;
        double d0 = vec3.horizontalDistance();
        setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
        setXRot((float) (Mth.atan2(vec3.y, d0) * Mth.RAD_TO_DEG));
        this.yRotO = getYRot();
        this.xRotO = getXRot();

        Vec3 vec31 = shooter.getKnownMovement();
        setDeltaMovement(getDeltaMovement().add(vec31.x, 0.0, vec31.z));
    }

    @Override
    public void tick() {
//        if (level().isClientSide && emitter == null && type != null) {
//            int color = switch (type) {
//                case CORRUPT -> 0x0000FF;
//                case CRIMSON -> 0xFF0000;
//                default -> 0;
//            };
//            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("thrown_powder"), ParticleEffect.Type.PARTICLE_WITH_VELOCITY, new MolangExp("color", color));
//            emitter.attached = this;
//            PSGameClient.LOADER.addEmitter(emitter, false);
//        }
        Vec3 motion = getDeltaMovement();
        double x = getX() + motion.x;
        double y = getY() + motion.y;
        double z = getZ() + motion.z;
        float length = (float) motion.length();
        this.moveDist += length;
        if (moveDist >= 3.5F || length < Mth.EPSILON) {
            discard();
        } else {
            setPos(x, y, z);
            setDeltaMovement(motion.scale(0.96));
            if (!level().isClientSide) {
                if (lastPos == blockPosition()) return;
                this.lastPos = blockPosition();
                for (BlockPos blockPos : BlockPos.betweenClosed(blockPosition().offset(-2, -2, -2), blockPosition().offset(2, 2, 2))) {
                    BlockPos pos = blockPos.immutable();
                    if (!coveredPos.contains(pos) && type.spread(level(), pos, true)) {
                        coveredPos.add(pos);
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TYPE, ISpreadable.Type.PURE.ordinal());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.type = ISpreadable.Type.byId(compound.getInt("type"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("type", type.ordinal());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && DATA_TYPE.equals(key)) {
            this.type = getSpreadableType();
        }
    }
}
