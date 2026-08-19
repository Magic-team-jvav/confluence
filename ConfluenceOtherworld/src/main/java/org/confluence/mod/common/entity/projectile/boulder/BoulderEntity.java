package org.confluence.mod.common.entity.projectile.boulder;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.boulder.BoulderBlock;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.worldgen.secret_seed.ForTheWorthy;
import org.confluence.mod.util.TrapDamageHelper;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileExtension;

import java.util.*;
import java.util.function.Predicate;

/// 所有巨石变体共享的运动、碰撞和持久化基类。
///
/// <p>机关、特殊种子及子类都可以在生成后调整巨石参数，因此这些参数属于实体实例的玩法状态，
/// 不能只依赖构造器默认值。区块重载时还必须延续发射者、寿命和逐目标命中冷却；当前版本格式若
/// 损坏则直接使实体失效，避免用部分默认值继续造成伤害或触发移除效果。</p>
public class BoulderEntity extends Projectile implements IPortProjectileExtension {
    public static final float SEARCH_RANGE = 31.5F;

    /// 当前 1.20 实现专用的巨石运行状态根，不读取早期扁平字段。
    private static final String RUNTIME_TAG = "ConfluenceBoulderRuntime";
    private static final int RUNTIME_VERSION = 1;
    private static final float MIN_SAVED_RADIUS = 0.05F;

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
    /// 同一巨石对每个目标已经消耗的碰撞冷却。它不是缓存：丢失后目标会在重载后立即再次受伤。
    private final Object2IntOpenHashMap<UUID> hitHistory = new Object2IntOpenHashMap<>();

    /// 当前格式解析失败后仅允许实体无副作用地退出。
    private boolean invalidRuntimeState;

    public float rotateO = 0.0F;
    public float rotate = 0.0F;

    // 可修改参数
    public float radius = 0.5F;
    public int maxRemoveTick = 1200;
    public int maxStillTick = 20;
    public double speed = 0.7;
    public double minRemoveSpeed = 0.007;
    public double bounceFactor = 0.3;
    public double frictionFactor = 0.9;
    /// 分裂代数，0 表示原始巨石。
    public int generation = 0;

    // 分裂代数，0为原始巨石
    protected int stillTickCount;

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
        // 损坏实体不能调用 onRemove，否则可能产生分裂、爆炸、掉落或大批粒子等玩法副作用。
        if (invalidRuntimeState) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
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
            entity.hurt(LibDamageTypes.of(entity.level(), LibDamageTypes.BOULDER, this), damage);
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
        // Projectile 的实现负责恢复稳定 owner UUID；省略 super 会让机关归属和伤害来源在重载后丢失。
        super.readAdditionalSaveData(tag);
        invalidRuntimeState = false;
        tickCount = 0;
        stillTickCount = 0;
        hitHistory.clear();

        if (!tag.contains(RUNTIME_TAG)) {
            // 命令或代码新建的实体允许没有运行状态；1.20 不迁移早期扁平 NBT。
            return;
        }
        if (!tag.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            invalidRuntimeState = true;
            return;
        }

        CompoundTag runtime = tag.getCompound(RUNTIME_TAG);
        if (!hasCurrentRuntimeShape(runtime)) {
            invalidRuntimeState = true;
            return;
        }

        try {
            BlockState blockState = BlockState.CODEC.parse(NbtOps.INSTANCE, runtime.get("BlockState")).result().orElse(null);
            int savedAge = runtime.getInt("Age");
            int savedStillAge = runtime.getInt("StillAge");
            float savedRadius = runtime.getFloat("Radius");
            int savedMaxRemoveTick = runtime.getInt("MaxRemoveTick");
            int savedMaxStillTick = runtime.getInt("MaxStillTick");
            double savedSpeed = runtime.getDouble("Speed");
            double savedMinRemoveSpeed = runtime.getDouble("MinRemoveSpeed");
            double savedBounceFactor = runtime.getDouble("BounceFactor");
            double savedFrictionFactor = runtime.getDouble("FrictionFactor");
            int savedGeneration = runtime.getInt("Generation");

            if (blockState == null
                    || !Float.isFinite(savedRadius)
                    || savedRadius < MIN_SAVED_RADIUS
                    || savedMaxRemoveTick < 0
                    || savedMaxStillTick < 0
                    || savedAge < 0
                    || savedAge > savedMaxRemoveTick
                    || savedStillAge < 0
                    || savedStillAge > savedMaxStillTick
                    || !isFiniteNonNegative(savedSpeed)
                    || !isFiniteNonNegative(savedMinRemoveSpeed)
                    || !isFiniteNonNegative(savedBounceFactor)
                    || !isFiniteNonNegative(savedFrictionFactor)
                    || savedGeneration < 0
                    || !readHitHistory(runtime.getList("HitHistory", Tag.TAG_COMPOUND))) {
                invalidRuntimeState = true;
                hitHistory.clear();
                return;
            }

            entityData.set(DATA_BLOCK_STATE, blockState);
            tickCount = savedAge;
            stillTickCount = savedStillAge;
            radius = savedRadius;
            maxRemoveTick = savedMaxRemoveTick;
            maxStillTick = savedMaxStillTick;
            speed = savedSpeed;
            minRemoveSpeed = savedMinRemoveSpeed;
            bounceFactor = savedBounceFactor;
            frictionFactor = savedFrictionFactor;
            generation = savedGeneration;
        } catch (RuntimeException ignored) {
            // Codec 或 NBT 访问异常都视作当前格式损坏，区块加载本身仍应继续。
            invalidRuntimeState = true;
            hitHistory.clear();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.put("BlockState", BlockState.CODEC
                .encodeStart(NbtOps.INSTANCE, entityData.get(DATA_BLOCK_STATE))
                .result()
                .orElseThrow(() -> new IllegalStateException("Failed to encode boulder block state")));
        runtime.putInt("Age", tickCount);
        runtime.putInt("StillAge", stillTickCount);
        runtime.putFloat("Radius", radius);
        runtime.putInt("MaxRemoveTick", maxRemoveTick);
        runtime.putInt("MaxStillTick", maxStillTick);
        runtime.putDouble("Speed", speed);
        runtime.putDouble("MinRemoveSpeed", minRemoveSpeed);
        runtime.putDouble("BounceFactor", bounceFactor);
        runtime.putDouble("FrictionFactor", frictionFactor);
        runtime.putInt("Generation", generation);

        ListTag savedHitHistory = new ListTag();
        for (Object2IntOpenHashMap.Entry<UUID> entry : hitHistory.object2IntEntrySet()) {
            CompoundTag savedHit = new CompoundTag();
            savedHit.putUUID("Target", entry.getKey());
            savedHit.putInt("Cooldown", entry.getIntValue());
            savedHitHistory.add(savedHit);
        }
        runtime.put("HitHistory", savedHitHistory);
        tag.put(RUNTIME_TAG, runtime);
    }

    /// 当前版本要求字段和 NBT 类型完整，避免缺字段时静默混用构造器默认值。
    private static boolean hasCurrentRuntimeShape(CompoundTag runtime) {
        return runtime.contains("Version", Tag.TAG_INT)
                && runtime.getInt("Version") == RUNTIME_VERSION
                && runtime.contains("BlockState")
                && runtime.contains("Age", Tag.TAG_INT)
                && runtime.contains("StillAge", Tag.TAG_INT)
                && runtime.contains("Radius", Tag.TAG_FLOAT)
                && runtime.contains("MaxRemoveTick", Tag.TAG_INT)
                && runtime.contains("MaxStillTick", Tag.TAG_INT)
                && runtime.contains("Speed", Tag.TAG_DOUBLE)
                && runtime.contains("MinRemoveSpeed", Tag.TAG_DOUBLE)
                && runtime.contains("BounceFactor", Tag.TAG_DOUBLE)
                && runtime.contains("FrictionFactor", Tag.TAG_DOUBLE)
                && runtime.contains("Generation", Tag.TAG_INT)
                && runtime.contains("HitHistory", Tag.TAG_LIST);
    }

    private static boolean isFiniteNonNegative(double value) {
        return Double.isFinite(value) && value >= 0.0;
    }

    /// 先完整校验再提交到实例字段，防止半张有效表在后续校验失败时残留。
    private boolean readHitHistory(ListTag savedHistory) {
        Object2IntOpenHashMap<UUID> restoredHistory = new Object2IntOpenHashMap<>();
        Set<UUID> seenTargets = new HashSet<>();
        for (Tag savedTag : savedHistory) {
            if (!(savedTag instanceof CompoundTag savedHit) || !savedHit.hasUUID("Target") || !savedHit.contains("Cooldown", Tag.TAG_INT)) {
                return false;
            }
            UUID target = savedHit.getUUID("Target");
            int cooldown = savedHit.getInt("Cooldown");
            if (!seenTargets.add(target) || cooldown < 0) {
                return false;
            }
            restoredHistory.put(target, cooldown);
        }
        hitHistory.putAll(restoredHistory);
        return true;
    }
}
