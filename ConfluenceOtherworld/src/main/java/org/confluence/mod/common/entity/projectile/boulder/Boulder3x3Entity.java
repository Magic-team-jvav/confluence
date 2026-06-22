package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.ArrayList;

public class Boulder3x3Entity extends BoulderEntity {
    private static final EntityDataAccessor<Direction> DATA_FACING = SynchedEntityData.defineId(Boulder3x3Entity.class, EntityDataSerializers.DIRECTION);
    private final ItemStack simulated = Items.IRON_PICKAXE.getDefaultInstance();
    private final int power = ModTiers.getPowerForVanillaTiers(Tiers.IRON);

    public Boulder3x3Entity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
        this.radius = 1.5F;
    }

    public Boulder3x3Entity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.BOULDER_3X.get(), level, pos, blockState);
        this.radius = 1.5F;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder.define(DATA_FACING, Direction.WEST));
    }

    @Override
    protected void moveAndUpdateNeighbors() {
        Vec3 vec3 = getDeltaMovement();
        setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
        move(MoverType.SELF, vec3.add(0, -getGravity(), 0));
        Vec3 motion = getDeltaMovement();
        if (motion.x != vec3.x || motion.y != vec3.y || motion.z != vec3.z) {
            ArrayList<BlockPos> toDestroy = new ArrayList<>();
            Direction facing = entityData.get(DATA_FACING);
            BlockPos blockPos = blockPosition().relative(facing, 2);
            for (BlockPos pos : BlockPos.betweenClosed(blockPos.relative(facing.getClockWise()), blockPos.above(2).relative(facing.getCounterClockWise()))) {
                BlockState blockState = level().getBlockState(pos);
                if (blockState.getCollisionShape(level(), pos, CollisionContext.of(this)).isEmpty())
                    continue;
                if (!ModTiers.isCorrectToolForDrops(power, simulated, blockState)) {
                    return;
                }
                toDestroy.add(pos.immutable());
            }
            if (!level().isClientSide) {
                for (BlockPos pos : toDestroy) {
                    level().destroyBlock(pos, true);
                }
            }
            setDeltaMovement(vec3.scale(1 - toDestroy.size() * 0.01));
        }
    }

    public void shoot(Direction facing, double speed) {
        setYRot(facing.toYRot());
        setDeltaMovement(new Vec3(facing.getStepX() * speed, facing.getStepY() * speed, facing.getStepZ() * speed));
        entityData.set(DATA_FACING, facing);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Facing")) {
            entityData.set(DATA_FACING, Direction.CODEC.parse(NbtOps.INSTANCE, tag.get("Facing")).result().orElse(Direction.WEST));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        Direction.CODEC.encodeStart(NbtOps.INSTANCE, entityData.get(DATA_FACING)).ifSuccess(nbt -> tag.put("Facing", nbt));
    }
}
