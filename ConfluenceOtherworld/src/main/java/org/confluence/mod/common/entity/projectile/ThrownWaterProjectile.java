package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;

public class ThrownWaterProjectile extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> DATA_CONVERSION_TYPE =
            SynchedEntityData.defineId(ThrownWaterProjectile.class, EntityDataSerializers.INT);
    private static final String RUNTIME_TAG = "ConfluenceThrownWaterRuntime";
    private static final int CURRENT_VERSION = 1;

    public ThrownWaterProjectile(EntityType<ThrownWaterProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownWaterProjectile(LivingEntity shooter, ISpreadable.Type type) {
        super(ModEntities.THROWN_WATER.get(), shooter, shooter.level());
        setConversionType(type);
    }

    /// 水弹类型必须进入原版实体同步数据，因为客户端只会调用实体类型构造器，
    /// 不会重放服务端物品工厂携带的构造参数。
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_CONVERSION_TYPE, ISpreadable.Type.PURE.ordinal());
    }

    /// 设置当前水弹允许的三种环境转化类型。
    public void setConversionType(ISpreadable.Type type) {
        if (!isSupportedType(type)) {
            throw new IllegalArgumentException("Thrown water only supports pure, corrupt, or crimson conversion");
        }
        entityData.set(DATA_CONVERSION_TYPE, type.ordinal());
    }

    /// 返回经过白名单保护的同步类型；损坏的网络值不会获得其他环境转化能力。
    public ISpreadable.Type getConversionType() {
        int typeId = entityData.get(DATA_CONVERSION_TYPE);
        ISpreadable.Type[] values = ISpreadable.Type.values();
        if (typeId < 0 || typeId >= values.length || !isSupportedType(values[typeId])) {
            return ISpreadable.Type.PURE;
        }
        return values[typeId];
    }

    @Override
    protected Item getDefaultItem() {
        ISpreadable.Type type = getConversionType();
        if (type == ISpreadable.Type.CORRUPT) return ConsumableItems.UNHOLY_WATER.get();
        if (type == ISpreadable.Type.CRIMSON) return ConsumableItems.BLOOD_WATER.get();
        return ConsumableItems.HOLY_WATER.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            BlockPos blockPos = result.getBlockPos();
            ISpreadable.Type type = getConversionType();
            for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-2, -2, -2), blockPos.offset(2, 2, 2))) {
                if (pos.distSqr(blockPos) <= 25) type.spread(level(), pos, true);
            }
            level().levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, blockPosition(), getColor());
            discard();
        }
    }

    protected int getColor() {
        ISpreadable.Type type = getConversionType();
        if (type == ISpreadable.Type.CORRUPT) return 0xFF00FF;
        if (type == ISpreadable.Type.CRIMSON) return 0xFF0000;
        return 0x0000FF;
    }

    /// 只读取 1.20 当前版本根；缺失、错类型或未知版本都清空为安全的圣水状态，
    /// 不尝试迁移旧键，也不会保留复用实体对象上的陈旧类型。
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(DATA_CONVERSION_TYPE, ISpreadable.Type.PURE.ordinal());
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (runtime.getInt("Version") != CURRENT_VERSION || !runtime.contains("Type", Tag.TAG_INT)) {
            return;
        }
        int typeId = runtime.getInt("Type");
        ISpreadable.Type[] values = ISpreadable.Type.values();
        if (typeId >= 0 && typeId < values.length && isSupportedType(values[typeId])) {
            entityData.set(DATA_CONVERSION_TYPE, typeId);
        }
    }

    /// 保存水弹独有的最小当前格式，避免把环境玩法状态塞进 PortLib。
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", CURRENT_VERSION);
        runtime.putInt("Type", getConversionType().ordinal());
        compound.put(RUNTIME_TAG, runtime);
    }

    private static boolean isSupportedType(ISpreadable.Type type) {
        return type == ISpreadable.Type.PURE
                || type == ISpreadable.Type.CORRUPT
                || type == ISpreadable.Type.CRIMSON;
    }
}
