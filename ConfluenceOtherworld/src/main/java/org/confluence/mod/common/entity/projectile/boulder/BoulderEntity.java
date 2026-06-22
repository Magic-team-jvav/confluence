package org.confluence.mod.common.entity.projectile.boulder;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.boulder.BoulderBlock;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.util.TrapDamageHelper;
import org.confluence.mod.common.worldgen.secret_seed.ForTheWorthy;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class BoulderEntity extends Projectile implements IPortProjectileExtension {
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

    // 可修改参数
    public float radius = 0.5F; // 半径
    public int maxRemoveTick = 1200; // 最大移除时间
    public int maxStillTick = 20; // 最大静止时间
    public double speed = 0.7; // 速度
    public double minRemoveSpeed = 0.007; // 最小移除速度
    public double bounceFactor = 0.3;
    public double frictionFactor = 0.9;
    public int generation = 0; // 分裂代数，0为原始巨石

    protected int stillTickCount; // 静止刻计时

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
        if (!(level() instanceof ServerLevel level)) {
            return;
        }
        ForTheWorthy.splitNormalBoulder(this, level);
        removeEffect(level);
        BlockPos blockPos = blockPosition();
        sendRemoveParticle(level, blockPos);
        playRemoveSound(level, blockPos);
        discard();
    }

    /// 移除前触发的效果
    protected void removeEffect(ServerLevel serverLevel) {}

    protected void sendRemoveParticle(ServerLevel serverLevel, BlockPos pos) {
        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()).setPos(pos), getX(), getY() + radius, getZ(), 175, 0.0, 0.0, 0.0, 0.15);
    }

    protected void playRemoveSound(ServerLevel serverLevel, BlockPos pos) {
        serverLevel.playSound(null, pos, getBlockState().getSoundType().getBreakSound(), SoundSource.BLOCKS, 5.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        moveAndUpdateNeighbors();

        Vec3 deltaMovement = getDeltaMovement().scale(0.99);
        setDeltaMovement(deltaMovement);
        rotate(deltaMovement);

        onHit(deltaMovement);

        if (tickCount >= maxRemoveTick || getDeltaMovement().length() < minRemoveSpeed && stillTickCount == maxStillTick) {
            onRemove();
            return;
        }

        if (getDeltaMovement().length() < minRemoveSpeed) {
            stillTickCount++;
        } else {
            stillTickCount = 0;
        }
    }

    protected void rotate(Vec3 deltaMovement) {
        float s = (float) deltaMovement.length();
        float r = s / radius;
        if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
        this.rotateO = rotate;
        this.rotate += r;
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

        if (hitResult instanceof BlockHitResult blockHitResult) {
            if (blockHitResult.getType() != HitResult.Type.MISS) {
                onHitBlock(blockHitResult);
            }
        } else if (hitResult instanceof EntityHitResult entityHitResult) {
            onHitEntity(entityHitResult);
        }
    }

    protected void moveAndUpdateNeighbors() {
        Vec3 deltaMovement = getDeltaMovement();
        setYRot((float) (Mth.atan2(deltaMovement.x, deltaMovement.z) * Mth.RAD_TO_DEG));
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

    protected static double getHorizontalVectorLength(Vec3 deltaMovement) {
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
    public double getDefaultGravity() {
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
        if (level() instanceof ServerLevel serverLevel) {
            playHitBlockSound(serverLevel);
        }
    }

    protected void playHitBlockSound(ServerLevel serverLevel) {
        serverLevel.playSound(null, blockPosition(), getBlockState().getSoundType().getFallSound(), SoundSource.BLOCKS, 5.0F, 1.0F);
    }

    protected void horizontalHitBlock(BlockHitResult blockHitResult, Direction direction) {
        onRemove();
    }

    protected void verticalHitBlock(BlockHitResult blockHitResult, Direction direction) {
        Level level = level();
        if (direction != Direction.UP) {
            return;
        }

        // 如果水平速度几乎为零则尝试添加水平向量
        if (getHorizontalVectorLength(getDeltaMovement()) < 0.0001) {
            // 先尝试获取最近的目标
            Player nearestPlayer = getNearestPlayer();
            if (nearestPlayer == null) {
                // 这里仅在服务端处理因为客户端的随机有可能于服务器的随机不同导致出现问题
                if (!level.isClientSide) {
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
            } else {
                targetTo(nearestPlayer);
            }
        }

        verticalHitRebound(blockHitResult, direction);
    }

    protected void verticalHitRebound(BlockHitResult blockHitResult, Direction direction) {
        if (fallDistance > 5) {
            Vec3 motion = LibMathUtils.relativeScale(getDeltaMovement(), blockHitResult.getDirection().getAxis(), -bounceFactor);
            if (Math.abs(motion.y) < 0.03) motion = new Vec3(motion.x, 0.0, motion.z);
            setDeltaMovement(motion.scale(frictionFactor));
            super.onHitBlock(blockHitResult);
            fallDistance = 0;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        UUID uuid1 = entity.getUUID();

        // TODO 需要重写
        int i = hitHistory.containsKey(uuid1) ? hitHistory.addTo(uuid1, -1) : 0;
        if (i <= 0) {
            float damage = 100.0F;
            if (entity instanceof LivingEntity living) {
                damage = TrapDamageHelper.applyDeadMansSweaterReduction(living, damage);
            }
            entity.hurt(ModDamageTypes.of(entity.level(), ModDamageTypes.BOULDER, this), damage);
            hitHistory.put(uuid1, 5);
        }
    }

    public void targetToPlayer() {
        targetTo(getNearestPlayer());
    }

    protected @Nullable Player getNearestPlayer() {
        return level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), SEARCH_RANGE, ENTITY_PREDICATE);
    }

    public void targetTo(@Nullable Entity entity) {
        Vec3 deltaMovement = getDeltaMovement();
        Vec3 vec3 = entity == null ? deltaMovement : entity.position().subtract(position());
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
    protected void defineSynchedData() {
        entityData.define(DATA_BLOCK_STATE, FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(DATA_BLOCK_STATE, PortDataResultExtension.getOrThrow(BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("BlockState"))));
        tickCount = tag.getInt("Age");
        stillTickCount = tag.getInt("StillAge");

        if (tag.contains("Radius")) radius = tag.getFloat("Radius");
        if (tag.contains("MaxRemoveTick")) maxRemoveTick = tag.getInt("MaxRemoveTick");
        if (tag.contains("MaxStillTick")) maxStillTick = tag.getInt("MaxStillTick");
        if (tag.contains("Speed")) speed = tag.getDouble("Speed");
        if (tag.contains("MinRemoveSpeed")) minRemoveSpeed = tag.getDouble("MinRemoveSpeed");
        if (tag.contains("Generation")) generation = tag.getInt("Generation");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", PortDataResultExtension.getOrThrow(BlockState.CODEC.encodeStart(NbtOps.INSTANCE, entityData.get(DATA_BLOCK_STATE))));
        tag.putInt("Age", tickCount);
        tag.putInt("StillAge", stillTickCount);
        tag.putFloat("Radius", radius);
        tag.putInt("MaxRemoveTick", maxRemoveTick);
        tag.putInt("MaxStillTick", maxStillTick);
        tag.putDouble("Speed", speed);
        tag.putDouble("MinRemoveSpeed", minRemoveSpeed);
        tag.putInt("Generation", generation);
    }
}
