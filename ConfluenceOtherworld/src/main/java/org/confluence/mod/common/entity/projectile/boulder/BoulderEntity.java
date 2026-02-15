package org.confluence.mod.common.entity.projectile.boulder;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.boulder.BoulderBlock;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class BoulderEntity extends Projectile {
    public static final float SEARCH_RANGE = 31.5F;
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE = SynchedEntityData.defineId(BoulderEntity.class, EntityDataSerializers.BLOCK_STATE);
    public static final Predicate<Entity> ENTITY_PREDICATE = entity -> {
        if (!entity.isAlive()) {
            return false;
        }
        if (entity instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }
        return true;
    };
    private final Object2IntOpenHashMap<UUID> hitHistory = new Object2IntOpenHashMap<>();
    public float rotateO = 0.0F;
    public float rotate = 0.0F;
    public float radius = 0.5F;
    protected double speed = 0.7;
    protected double minimumBreakSpeed = 0.007;

    public BoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        this(ModEntities.BOULDER.get(), level, pos, blockState);
    }

    public BoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level, Vec3 pos, BlockState blockState) {
        super(entityType, level);
        setPos(pos);
        entityData.set(DATA_BLOCK_STATE, blockState);
    }

    public BlockState getBlockState() {
        return entityData.get(DATA_BLOCK_STATE);
    }

    public void onRemove() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = blockPosition();
        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()).setPos(pos),
                getX(), getY() + 0.5, getZ(), 175, 0.0, 0.0, 0.0, 0.15
        );
        serverLevel.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 5.0F, 1.0F);
        discard();
    }

    @Override
    public void tick() {
        if (tickCount > 100) {
            onRemove();
            return;
        }
        super.tick();
        moveAndUpdateNeighbors();

        Vec3 deltaMovement = getDeltaMovement().scale(0.99);
        setDeltaMovement(deltaMovement);
        float s = (float) deltaMovement.length();
        float r = s / radius;
        if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
        this.rotateO = rotate;
        this.rotate += r;

        onHit(deltaMovement);

        if (onGround() && getDeltaMovement().length() < minimumBreakSpeed) {
            onRemove();
        }
    }

    protected void onHit(Vec3 deltaMovement) {
        deltaMovement = deltaMovement.add(Mth.sign(deltaMovement.x) * radius, Mth.sign(deltaMovement.y) * radius, Mth.sign(deltaMovement.z) * radius);
        Vec3 start = position();
        Vec3 end = start.add(deltaMovement);
        HitResult hitResult = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            end = hitResult.getLocation();
        }

        HitResult hitResult1 = ProjectileUtil.getEntityHitResult(level(), this, start, end, getBoundingBox().expandTowards(deltaMovement).inflate(1.0), this::canHitEntity);
        if (hitResult1 != null) {
            hitResult = hitResult1;
        }

        switch (hitResult) {
            case BlockHitResult blockHitResult -> {
                if (blockHitResult.getType() != HitResult.Type.MISS) {
                    onHitBlock(blockHitResult);
                }
            }
            case EntityHitResult entityHitResult -> onHitEntity(entityHitResult);
            default -> {}
        }
    }

    protected void moveAndUpdateNeighbors() {
        Vec3 deltaMovement = getDeltaMovement();
        setYRot((float) (Mth.atan2(deltaMovement.x, deltaMovement.z) * Mth.RAD_TO_DEG));
        move();
        applyGravity();

        deltaMovement = getDeltaMovement();
        move(MoverType.SELF, deltaMovement);

        if (level().isClientSide) {
            return;
        }

        Vec3 motion = getDeltaMovement();
        if (motion.x != deltaMovement.x || motion.y != deltaMovement.y || motion.z != deltaMovement.z) {
            updateNeighbors();
        }
    }

    protected void move() {
    }

    protected static double horizontalVectorLength(Vec3 deltaMovement) {
        return Math.sqrt(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z);
    }

    protected void updateNeighbors() {
        for (Direction dir : LibUtils.DIRECTIONS) {
            BlockPos blockPos = blockPosition().relative(dir);
            BlockState blockState = level().getBlockState(blockPos);
            if (blockState.getBlock() instanceof BoulderBlock block) {
                block.onProjectileHit(level(), blockState, new BlockHitResult(blockPos.getCenter(), dir, blockPos, false), this);
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.08;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Direction direction = blockHitResult.getDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            verticalHitBlock(blockHitResult, direction);
        } else {
            horizontalHitBlock(blockHitResult, direction);
        }
    }

    protected void horizontalHitBlock(BlockHitResult blockHitResult, Direction direction) {
        onRemove();
    }

    protected void verticalHitBlock(BlockHitResult blockHitResult, Direction direction) {
        Level level = level();
        if (direction != Direction.UP) {
            return;
        }

        setDeltaMovement(getDeltaMovement().add(0, fallDistance * 0.1, 0));
        fallDistance = 0;

        if (horizontalVectorLength(getDeltaMovement()) >= minimumBreakSpeed) {
            return;
        }

        Player nearestPlayer = getNearestPlayer();
        if (nearestPlayer != null) {
            targetTo(nearestPlayer);
            return;
        }

        if (level.isClientSide) {
            return;
        }

        List<Direction> directions = new ArrayList<>();
        for (Direction direction1 : Direction.Plane.HORIZONTAL) {
            Vec3 position = position();
            BlockHitResult clip = level.clip(new ClipContext(position, position.relative(direction1, 1), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (clip.getType() == HitResult.Type.MISS) {
                directions.add(direction1);
            }
        }

        int directionsSize = directions.size();

        if (!directions.isEmpty()) {
            Direction direction1 = directions.get(directionsSize == 1 ? 0 : getRandom().nextIntBetweenInclusive(0, directionsSize - 1));
            setDeltaMovement(getDeltaMovement().relative(direction1, 1).scale(speed));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        UUID uuid1 = entity.getUUID();
        int i = hitHistory.containsKey(uuid1) ? hitHistory.addTo(uuid1, -1) : 0;
        if (i <= 0) {
            entity.hurt(ModDamageTypes.of(entity.level(), ModDamageTypes.BOULDER, this), 100.0F);
            hitHistory.put(uuid1, 5);
        }
    }

    public void targetTo() {
        targetTo(getNearestPlayer());
    }

    protected @Nullable Player getNearestPlayer() {
        return level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), SEARCH_RANGE, ENTITY_PREDICATE);
    }

    public void targetTo(@Nullable Player player) {
        targetToPlayer(player);
    }

    public void targetToPlayer(@Nullable Player player) {
        Vec3 deltaMovement = getDeltaMovement();
        Vec3 vec3 = player == null ? deltaMovement : player.position().subtract(position());
        vec3 = new Vec3(vec3.x, deltaMovement.y, vec3.z).normalize();
        setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
        setDeltaMovement(vec3.scale(speed));
        this.yRotO = getYRot();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLOCK_STATE, FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(DATA_BLOCK_STATE, BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("BlockState")).getOrThrow());
        this.tickCount = tag.getInt("Age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, entityData.get(DATA_BLOCK_STATE)).getOrThrow());
        tag.putInt("Age", tickCount);
    }
}
