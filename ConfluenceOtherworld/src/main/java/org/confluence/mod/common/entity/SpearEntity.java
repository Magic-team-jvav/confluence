package org.confluence.mod.common.entity;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.SpearTrapBlock;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.TrapDamageHelper;

/**
 * 长矛机关的发射物
 */
public class SpearEntity extends Entity {
    private static final EntityDataAccessor<Direction> DATA_DIRECTION = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Boolean> DATA_OPENED = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_PROGRESS = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.FLOAT);
    /// 生成该实体的机关位置。由机关在生成时写入，并随实体存档恢复。
    public BlockPos trapPos;

    public SpearEntity(EntityType<SpearEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public SpearEntity(Level level, Direction direction) {
        this(ModEntities.SPEAR.get(), level);
        setDirection(direction);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && trapPos == null) {
            discard();
            return;
        }

        boolean opened = isOpened();
        float progress = getProgress();
        if (opened) {
            progress -= 0.05F;
            if (progress <= 0.0F) {
                opened = false;
                progress = 0.0F;
                if (!level().isClientSide) {
                    SpearTrapBlock block = FunctionalBlocks.SPEAR_TRAP.get();
                    BlockState trapState = level().getBlockState(trapPos);
                    if (!trapState.is(block)) {
                        // 来源机关已被破坏时不再改写其他方块，也不留下无主实体。
                        discard();
                        return;
                    }
                    level().setBlockAndUpdate(trapPos, trapState.setValue(BlockStateProperties.TRIGGERED, true));
                    level().scheduleTick(trapPos, block, block.delay());
                }
                discard();
                return;
            }
        } else {
            progress += 0.05F;
            if (progress >= 1.0F) {
                opened = true;
                progress = 1.0F;
            } else {
                BlockState blockState = level().getBlockState(blockPosition().relative(getDirection(), Mth.ceil(13 * progress)));
                if (!blockState.isAir() && !blockState.liquid()) {
                    opened = true;
                }
            }
        }

        // 伸缩阶段使用同步实体数据；服务端从存档恢复后，客户端不会从零重新播放。
        setOpened(opened);
        setProgress(progress);

        setBoundingBox(Shulker.getProgressAabb(getDirection(), 13 * progress).move(getX() - 0.5, getY(), getZ() - 0.5));

        Vec3 startVec = position().relative(getDirection().getOpposite(), 1);
        Vec3 endVec = position().relative(getDirection(), Mth.ceil(13 * progress));
        AABB boundingBox = getBoundingBox().inflate(0.3F);
        for (Entity entity1 : level().getEntities(this, boundingBox, entity -> entity instanceof LivingEntity)) {
            AABB aabb = entity1.getBoundingBox().inflate(0.3);
            if (aabb.clip(startVec, endVec).isPresent()) {
                float damage = LibUtils.switchByDifficulty(level(), blockPosition(), 24, 48, 72);
                entity1.hurt(LibDamageTypes.of(level(), DamageTypes.STING), TrapDamageHelper.applyDeadMansSweaterReduction((LivingEntity) entity1, damage));
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DIRECTION, Direction.NORTH);
        this.entityData.define(DATA_OPENED, false);
        this.entityData.define(DATA_PROGRESS, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setDirection(Direction.NORTH);
        if (compound.contains("Direction")) {
            PortDataResultExtension.ifSuccess(Direction.CODEC.parse(NbtOps.INSTANCE, compound.get("Direction")), this::setDirection);
        }
        this.trapPos = compound.contains("TrapPos", Tag.TAG_LONG) ? BlockPos.of(compound.getLong("TrapPos")) : null;
        boolean opened = compound.getBoolean("Opened");
        float savedProgress = compound.contains("Progress", Tag.TAG_ANY_NUMERIC)
                ? compound.getFloat("Progress")
                : 0.0F;
        // NaN 会令伸缩分支永久无法完成；损坏数据统一回退到收起状态。
        float progress = Float.isFinite(savedProgress) ? Mth.clamp(savedProgress, 0.0F, 1.0F) : 0.0F;
        if (progress == 0.0F) {
            opened = false;
        }
        setOpened(opened);
        setProgress(progress);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        PortDataResultExtension.ifSuccess(Direction.CODEC.encodeStart(NbtOps.INSTANCE, getDirection()), t -> compound.put("Direction", t));
        if (trapPos != null) {
            compound.putLong("TrapPos", trapPos.asLong());
        }
        compound.putBoolean("Opened", isOpened());
        float progress = getProgress();
        compound.putFloat("Progress", Float.isFinite(progress) ? Mth.clamp(progress, 0.0F, 1.0F) : 0.0F);
    }

    public void setDirection(Direction direction) {
        entityData.set(DATA_DIRECTION, direction);
    }

    private void setOpened(boolean opened) {
        entityData.set(DATA_OPENED, opened);
    }

    private boolean isOpened() {
        return entityData.get(DATA_OPENED);
    }

    private void setProgress(float progress) {
        entityData.set(DATA_PROGRESS, progress);
    }

    private float getProgress() {
        return entityData.get(DATA_PROGRESS);
    }

    @Override
    public Direction getDirection() {
        return entityData.get(DATA_DIRECTION);
    }
}
