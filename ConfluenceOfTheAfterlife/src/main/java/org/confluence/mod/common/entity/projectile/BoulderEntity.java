package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

public class BoulderEntity extends Projectile {
    public static final EntityDataAccessor<Boolean> DATA_VERTICAL = SynchedEntityData.defineId(BoulderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE = SynchedEntityData.defineId(BoulderEntity.class, EntityDataSerializers.BLOCK_STATE);
    public static final double SPEED = 0.7;
    public static final float DIAMETER = 1.0F;
    public static final float SEARCH_RANGE = 31.5F;
    public float rotateO = 0.0F;
    public float rotate = 0.0F;

    public BoulderEntity(EntityType<? extends BoulderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.BOULDER.get(), level);
        setPos(pos);
        entityData.set(DATA_BLOCK_STATE, blockState);
    }

    public boolean isVertical() {
        return entityData.get(DATA_VERTICAL);
    }

    public BlockState getBlockState() {
        return entityData.get(DATA_BLOCK_STATE);
    }

    public void remove() {
        if (level() instanceof ServerLevel serverLevel) {
            BlockPos pos = getOnPos().above();
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState())
                    .setPos(pos), getX(), getY() + 0.5, getZ(), 175, 0.0, 0.0, 0.0, 0.15);
            serverLevel.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 5.0F, 1.0F);
        }
        discard();
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = getDeltaMovement();
        setYRot((float) (Mth.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG));
        setDeltaMovement(motion.x, onGround() ? 0.0 : motion.y - 0.08, motion.z);
        move(MoverType.SELF, getDeltaMovement());

        Vec3 delta = getDeltaMovement().scale(0.99);
        setDeltaMovement(delta);
        float s = (float) delta.length();
        float r = 2.0F * s / DIAMETER;
        if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
        this.rotateO = rotate;
        this.rotate += r;

        Vec3 start = position();
        Vec3 end = start.add(delta);
        HitResult hitResult = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) end = hitResult.getLocation();
        HitResult hitResult1 = ProjectileUtil.getEntityHitResult(level(), this, start, end, getBoundingBox().expandTowards(delta).inflate(1.0), this::canHitEntity);
        if (hitResult1 != null) hitResult = hitResult1;

        if (hitResult instanceof BlockHitResult blockHitResult) {
            onHitBlock(blockHitResult);
        } else {
            onHitEntity((EntityHitResult) hitResult);
        }
        if (getDeltaMovement().length() < 0.007) {
            if (isVertical()) {
                targetTo(level().getNearestPlayer(this, SEARCH_RANGE));
                entityData.set(DATA_VERTICAL, false);
            } else {
                remove();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if (isVertical() && !getBlockStateOn().isAir() && blockHitResult.getDirection().getAxis() == Direction.Axis.Y) {
            targetTo(level().getNearestPlayer(this, SEARCH_RANGE));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        entity.hurt(ModDamageTypes.of(entity.level(), ModDamageTypes.BOULDER, this), 100.0F);
    }

    public void targetTo(@Nullable Player player) {
        Vec3 vec3 = player == null ? getDeltaMovement().normalize().scale(1.0 + SPEED) : player.position().subtract(position()).normalize();
        vec3 = new Vec3(vec3.x, 0.0, vec3.z);
        setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
        setDeltaMovement(vec3.scale(SPEED));
        this.yRotO = getYRot();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_VERTICAL, false);
        builder.define(DATA_BLOCK_STATE, FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}
}
