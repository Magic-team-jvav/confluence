package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/// 净化粉、腐化粉等环境转化粉末的飞行实体。
///
/// 实体的最大飞行距离同时也是一次投掷能够执行环境转化的预算。区块卸载后必须继续使用
/// 已消耗的预算，否则玩家可以通过反复卸载区块让同一枚粉末无限延长作用范围。转化类型既保留
/// 服务端字段，也写入同步实体数据，保证重载后的客户端粒子和服务端方块转化使用同一类型。
public class ThrownPowderEntity extends Entity {
    private static final float MAX_TRAVEL_DISTANCE = 3.5F;
    private static final EntityDataAccessor<Integer> DATA_TYPE = SynchedEntityData.defineId(ThrownPowderEntity.class, EntityDataSerializers.INT);
    private BlockPos lastPos;
    private ISpreadable.Type type = ISpreadable.Type.PURE;
    //    private ParticleEmitter emitter;
    private final Set<BlockPos> coveredPos = new HashSet<>();

    public ThrownPowderEntity(EntityType<ThrownPowderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownPowderEntity(Level level, ISpreadable.Type type) {
        super(ModEntities.THROWN_POWDER.get(), level);
        setSpreadableType(type);
    }

    public void setSpreadableType(ISpreadable.Type type) {
        this.type = Objects.requireNonNull(type, "Spreadable type must not be null");
        entityData.set(DATA_TYPE, this.type.ordinal());
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
        if (moveDist >= MAX_TRAVEL_DISTANCE || length < Mth.EPSILON) {
            discard();
        } else {
            setPos(x, y, z);
            setDeltaMovement(motion.scale(0.96));
            if (!level().isClientSide) {
                if (lastPos == blockPosition()) return;
                this.lastPos = blockPosition();
                for (BlockPos blockPos : BlockPos.betweenClosed(blockPosition().offset(-5, -5, -5), blockPosition().offset(6, 6, 6))) {
                    BlockPos pos = blockPos.immutable();
                    if (!coveredPos.contains(pos) && type.spread(level(), pos, true)) {
                        coveredPos.add(pos);
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TYPE, ISpreadable.Type.PURE.ordinal());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        int typeId = compound.contains("Type", Tag.TAG_INT) ? compound.getInt("Type") : ISpreadable.Type.PURE.ordinal();
        ISpreadable.Type[] types = ISpreadable.Type.values();
        ISpreadable.Type restoredType = typeId >= 0 && typeId < types.length ? types[typeId] : ISpreadable.Type.PURE;
        setSpreadableType(restoredType);

        float savedDistance = compound.contains("MoveDistance", Tag.TAG_ANY_NUMERIC)
                ? compound.getFloat("MoveDistance")
                : 0.0F;
        // 非有限数会污染后续距离比较；当前格式损坏时从尚未飞行的安全状态恢复。
        this.moveDist = Float.isFinite(savedDistance)
                ? Mth.clamp(savedDistance, 0.0F, MAX_TRAVEL_DISTANCE)
                : 0.0F;
        this.lastPos = null;
        this.coveredPos.clear();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        ISpreadable.Type savedType = type == null ? getSpreadableType() : type;
        compound.putInt("Type", savedType.ordinal());
        compound.putFloat("MoveDistance", Float.isFinite(moveDist)
                ? Mth.clamp(moveDist, 0.0F, MAX_TRAVEL_DISTANCE)
                : 0.0F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && DATA_TYPE.equals(key)) {
            this.type = getSpreadableType();
        }
    }
}
