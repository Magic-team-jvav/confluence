package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.SpearTrapBlock;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.TrapDamageHelper;

/**
 * 长矛机关的发射物
 */
public class SpearEntity extends Entity {
    private static final EntityDataAccessor<Direction> DATA_DIRECTION = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.DIRECTION);
    private boolean opened = false;
    private float progress = 0;
    public transient BlockPos trapPos;

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

        if (opened) {
            this.progress -= 0.05F;
            if (progress <= 0.0F) {
                this.opened = false;
                this.progress = 0.0F;
                if (!level().isClientSide) {
                    level().setBlockAndUpdate(trapPos, level().getBlockState(trapPos).setValue(BlockStateProperties.TRIGGERED, true));
                    SpearTrapBlock block = FunctionalBlocks.SPEAR_TRAP.get();
                    level().scheduleTick(trapPos, block, block.delay());
                }
                discard();
                return;
            }
        } else {
            this.progress += 0.05F;
            if (progress >= 1.0F) {
                this.opened = true;
                this.progress = 1.0F;
            } else {
                BlockState blockState = level().getBlockState(blockPosition().relative(getDirection(), Mth.ceil(13 * progress)));
                if (!blockState.isAir() && !blockState.liquid()) {
                    this.opened = true;
                }
            }
        }

        setBoundingBox(Shulker.getProgressAabb(1.0F, getDirection(), 13 * progress).move(getX() - 0.5, getY(), getZ() - 0.5));

        Vec3 startVec = position().relative(getDirection().getOpposite(), 1);
        Vec3 endVec = position().relative(getDirection(), Mth.ceil(13 * progress));
        AABB boundingBox = getBoundingBox().inflate(0.3F);
        for (Entity entity1 : level().getEntities(this, boundingBox, entity -> entity instanceof LivingEntity)) {
            AABB aabb = entity1.getBoundingBox().inflate(0.3);
            if (aabb.clip(startVec, endVec).isPresent()) {
                float damage = LibUtils.switchByDifficulty(level(), blockPosition(), 24, 48, 72);
                entity1.hurt(ModDamageTypes.of(level(), DamageTypes.STING), TrapDamageHelper.applyDeadMansSweaterReduction((LivingEntity) entity1, damage));
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DIRECTION, Direction.NORTH);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setDirection(Direction.CODEC.parse(NbtOps.INSTANCE, compound.get("Direction")).getOrThrow());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("Direction", Direction.CODEC.encodeStart(NbtOps.INSTANCE, getDirection()).getOrThrow());
    }

    public void setDirection(Direction direction) {
        entityData.set(DATA_DIRECTION, direction);
    }

    @Override
    public Direction getDirection() {
        return entityData.get(DATA_DIRECTION);
    }
}
