package org.confluence.mod.common.entity.projectile.boulder;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.boulder.AbstractBoulderBlock;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/// 抽象巨石实体
///
/// @author 尽
public abstract class AbstractBoulderEntity extends DamageSettableProjectile {
    protected static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE = SynchedEntityData.defineId(AbstractBoulderEntity.class, EntityDataSerializers.BLOCK_STATE);
    protected final Object2IntOpenHashMap<UUID> hitHistory = new Object2IntOpenHashMap<>();
    /// 粒子方块状态
    protected final transient BlockState particleBlockState;
    /// 粒子类型
    protected final ParticleType<BlockParticleOption> particleType;
    /// 音效
    protected final SoundEvent sound;
    /// 音效类别
    protected final SoundSource soundCategory;
    /// 最低被移除速度
    protected final double minRemoveSpeed;
    /// 默认重力
    protected final double defaultGravity;
    /// 大小半径
    protected final float sizeRadius;
    /// 旋转角度
    protected transient float rotate;
    public transient float rotateO;
    /// 速度低于移除阈值的时间
    protected transient int stillTick;
    /// 着陆次数
    protected int landingCount;
    /// 是否追踪
    protected boolean isTracking;
    /// 移动速度
    protected double speed;
    /// 追踪范围
    protected float trackingRange;

    /// 构造函数系列，支持不同参数组合创建巨石实体
    ///
    /// @param entityType 实体类型
    /// @param level      世界
    /// @param blockState 方块状态
    public AbstractBoulderEntity(
            EntityType<? extends AbstractBoulderEntity> entityType,
            Level level,
            BlockState blockState
    ) {
        this(entityType, level, blockState, BoulderEntity.Builder.of());
    }

    /// 构造函数
    ///
    /// @param entityType 实体类型
    /// @param level      世界
    /// @param blockState 方块状态
    /// @param builder    构建器
    public AbstractBoulderEntity(
            EntityType<? extends AbstractBoulderEntity> entityType,
            Level level,
            BlockState blockState,
            BoulderEntity.Builder builder
    ) {
        this(entityType, level, Vec3.ZERO, blockState, builder);
    }

    /// 构造函数
    ///
    /// @param entityType 实体类型
    /// @param level      世界
    /// @param pos        位置
    /// @param blockState 方块状态
    public AbstractBoulderEntity(
            EntityType<? extends AbstractBoulderEntity> entityType,
            Level level,
            Vec3 pos,
            BlockState blockState
    ) {
        this(entityType, level, pos, blockState, BoulderEntity.Builder.of());
    }

    /// 构造函数
    ///
    /// @param entityType 实体类型
    /// @param level      世界
    /// @param pos        位置
    /// @param blockState 方块状态
    /// @param builder    构建器
    public AbstractBoulderEntity(
            EntityType<? extends AbstractBoulderEntity> entityType,
            Level level,
            Vec3 pos,
            BlockState blockState,
            BoulderEntity.Builder builder
    ) {
        super(entityType, level);
        setPos(pos);
        this.trackingRange = builder.trackingRange;
        this.speed = builder.speed;
        this.minRemoveSpeed = builder.minRemoveSpeed;
        this.sizeRadius = builder.sizeRadius;
        this.landingCount = builder.landingCount;
        this.particleBlockState = builder.particleBlockState;
        this.particleType = builder.particleType;
        this.sound = builder.sound;
        this.soundCategory = builder.soundCategory;
        this.defaultGravity = builder.defaultGravity;
        setBlockState(blockState);
        setDamage(100);
        setDefaultVelocity((float) speed);
    }

    /// 主要游戏刻更新方法
    /// 处理移动、旋转、碰撞等逻辑
    @Override
    public void tick() {
        super.tick();

        move();
        updateNeighbors();

        scrollRotate();

        // 调用命中方法
        onHit(getHitResult());
        BlockHitResult clipped = level().clip(new ClipContext(position(), position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (clipped.isInside() || stillTick >= 20) {
            onRemoveBroken(true);
        }

        // 在地上速度过慢删除
        if (onGround() && getDeltaMovement().length() <= minRemoveSpeed) {
            this.stillTick++;
        } else {
            this.stillTick = 0;
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        worldInit();
    }

    /// 初始运行例如给巨石添加初始动量
    /// 仅在添加进世界中触发
    protected void worldInit() {
        Vec3 position = position();
        ClipContext context = new ClipContext(position, position.relative(Direction.DOWN, 1), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        if (level().clip(context).getType() == HitResult.Type.MISS) {
            return;
        }
        findLocation(position);
    }

    /// 移除巨石
    ///
    /// @param timeoutUrNot 是否因为超时导致触发
    public void onRemoveBroken(boolean timeoutUrNot) {
        if (!(level() instanceof ServerLevel serverLevel) || isRemoved()) {
            return;
        }
        brokenFunction(serverLevel, timeoutUrNot);
        discard();
    }

    /// 破碎时执行的功能
    ///
    /// @param serverLevel  服务端世界
    /// @param timeoutUrNot 是否因为超时导致触发
    protected void brokenFunction(final ServerLevel serverLevel, boolean timeoutUrNot) {
        BlockPos pos = blockPosition();
        brokenParticles(serverLevel, pos);
        brokenSound(serverLevel, pos);
    }

    /// 破碎粒子效果
    ///
    /// @param serverLevel 服务端世界
    /// @param pos         位置
    public void brokenParticles(final ServerLevel serverLevel, final BlockPos pos) {
        BlockState state = particleBlockState == null ? getBlockState() : particleBlockState;
        serverLevel.sendParticles(new BlockParticleOption(particleType, state).setPos(pos),
                getX(), getY() + 0.5, getZ(), 175, 0.0, 0.0, 0.0, 0.15);
    }

    /// 破碎音效
    ///
    /// @param serverLevel 服务端世界
    /// @param pos         位置
    public void brokenSound(final ServerLevel serverLevel, final BlockPos pos) {
        serverLevel.playSound(null, pos, sound, soundCategory, 5.0F, 1.0F);
    }

    /// 获取碰撞命中结果
    /// 包括方块和实体的碰撞检测
    ///
    /// @return 碰撞结果
    protected HitResult getHitResult() {
        Level level = level();
        Vec3 delta = getDeltaMovement();
        delta = delta.add(Mth.sign(delta.x) * sizeRadius, Mth.sign(delta.y) * sizeRadius, Mth.sign(delta.z) * sizeRadius);
        Vec3 start = position();
        Vec3 end = start.add(delta);
        HitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            end = hitResult.getLocation();
        }
        EntityHitResult hitResult1 = ProjectileUtil.getEntityHitResult(level, this, start, end, getBoundingBox()
                .expandTowards(delta)
                .inflate(1.0), this::canHitEntity);
        if (hitResult1 != null) {
            hitResult = hitResult1;
        }
        return hitResult;
    }

    /// 碰撞运算总方法
    /// 根据碰撞类型分发到具体处理方法
    ///
    /// @param hitResult 碰撞结果
    @Override
    protected void onHit(final HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        if (hitResult instanceof BlockHitResult blockHitResult) {
            if (blockHitResult.isInside()) {
                onRemoveBroken(false);
                return;
            }
            onHitBlock(blockHitResult);
            return;
        }
        if (hitResult instanceof EntityHitResult entityHitResult) {
            onHitEntity(entityHitResult);
        }
    }

    /// 滚动旋转计算
    protected void scrollRotate() {
        rotate();
        Vec3 delta = getDeltaMovement();
        float s = (float) delta.length();
        float r = s / sizeRadius;
        if (rotate > Mth.TWO_PI) {
            this.rotate -= Mth.TWO_PI;
        }
        this.rotateO = rotate;
        this.rotate += r;
    }

    /// 更新面向方向（基于运动方向）
    protected void rotate() {
        Vec3 v = getDeltaMovement();
        this.yRotO = getYRot();
        setYRot((float) (Mth.atan2(-v.x, v.z) * Mth.RAD_TO_DEG));
    }

    /// 执行移动操作
    /// 应用重力并移动实体
    protected void move() {
        Vec3 vec3 = getDeltaMovement();
        setDeltaMovement(vec3.x, vec3.y - getGravity(), vec3.z);
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.99));
        rotate();
    }

    /// 更新邻居方块
    /// 检测周围是否有其他巨石方块并与之交互
    protected void updateNeighbors() {
        if (!level().isClientSide) {
            for (Direction dir : LibUtils.DIRECTIONS) {
                BlockPos blockPos = blockPosition().relative(dir);
                BlockState blockState = level().getBlockState(blockPos);
                if (!(blockState.getBlock() instanceof AbstractBoulderBlock<?> block)) {
                    continue;
                }
                BlockHitResult hitResult = new BlockHitResult(blockPos.getCenter(), dir, blockPos, false);
                block.onProjectileHit(level(), blockState, hitResult, this);
            }
        }

        setDeltaMovement(getDeltaMovement().scale(0.99));
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Level level = level();
        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = blockHitResult.getBlockPos();
            Vec3 vec3 = blockHitResult.getLocation();
            BlockState blockState = level.getBlockState(pos);
            serverLevel.sendParticles(new BlockParticleOption(particleType, blockState).setPos(pos),
                    vec3.x, vec3.y, vec3.z, 175, 0.0, 0.0, 0.0, 0.15);
            serverLevel.playSound(null, pos, sound, soundCategory, 10.0F, 1.0F);
        }
        Direction.Axis axis = blockHitResult.getDirection().getAxis();
        if (axis == Direction.Axis.Y) {
            onVerticalBlockHit(blockHitResult);
            return;
        }
        onHorizontalBlockHit(blockHitResult);
    }

    /// 水平碰撞处理（撞墙等）
    ///
    /// @param blockHitResult 方块碰撞结果
    protected void onHorizontalBlockHit(final BlockHitResult blockHitResult) {
        onRemoveBroken(false);
    }

    /// 垂直碰撞处理（着陆等）
    ///
    /// @param blockHitResult 方块碰撞结果
    protected void onVerticalBlockHit(final BlockHitResult blockHitResult) {
        if (landingCount <= 0 || blockHitResult.getDirection() != Direction.UP) {
            onRemoveBroken(false);
            return;
        }
        Vec3 vec3 = blockHitResult.getLocation();
        findLocation(getAdjustMoveVector(vec3, -1));
        this.landingCount--;
    }

    protected Vec3 getAdjustMoveVector(final Vec3 vec3, double v) {
        return vec3.subtract(vec3.scale(Math.pow(vec3.length(), v * (getSizeRadius() * 2))));
    }

    /// 寻找新位置
    /// 当巨石弹跳或需要重新定位时调用
    ///
    /// @param pos 位置
    protected void findLocation(final Vec3 pos) {
        Level level = level();
        Vec3 position = position();
        Vec3 moveVector = getDeltaMovement();
        if (moveVector.x == 0 && moveVector.z == 0 &&
                targetPlayer(level().getNearestPlayer(this, trackingRange))) {
            return;
        }
        Vec3 targetPos = null;
        boolean isTranslation = false;
        for (int i = 1; i >= 0; i--) {
            if (i == 0) {
                isTranslation = true;
            }
            List<Direction> list = getDirectionList(pos, level, i, position, i == 0);
            int size = list.size();
            if (list.isEmpty()) {
                continue;
            }

            if (size > 1) {
                Direction direction = list.get(getRandom().nextIntBetweenInclusive(0, size - 1));
                targetPos = position.relative(direction, 1);
                break;
            }
            targetPos = position.relative(list.getFirst(), 1);
            break;
        }

        Vec3 subtract = null;
        if (isTranslation) {
            if (targetPlayer(level().getNearestPlayer(this, trackingRange))) {
                return;
            }
        }
        if (targetPos != null) {
            subtract = new Vec3(targetPos.x, position.y, targetPos.z);
        }
        if (moveVector.x != 0 || moveVector.z != 0) {
            return;
        }
        if (subtract == null) {
            return;
        }

        Vec3 orientation = orientationHorizontal(subtract).scale(speed);
        setDeltaMovement(new Vec3(orientation.x, orientation.y, orientation.z));
    }

    /// 获取方向列表
    ///
    /// @param pos      位置
    /// @param level    世界
    /// @param finalY   Y坐标
    /// @param position 当前位置
    /// @return 方向列表
    private List<Direction> getDirectionList(final Vec3 pos, final Level level, final int finalY, final Vec3 position, boolean isParallel) {
        return Direction.Plane.HORIZONTAL.stream().map(direction -> {
                    Vec3 from = isParallel ? position : position.relative(direction, 1);
                    Vec3 to = pos.relative(direction, 1).relative(Direction.DOWN, finalY);
                    ClipContext context = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this);
                    return Map.entry(direction, level.clip(context));
                })
                .filter(hitResult -> hitResult.getValue().getType() == HitResult.Type.MISS)
                .map(Map.Entry::getKey).toList();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        double lengthSqr = getDeltaMovement().length();
        if (lengthSqr <= 0.05) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (!entity.isAlive() || !entity.isAttackable() || entity.equals(getOwner())) {
            return;
        }
        UUID uuid = entity.getUUID();
        if ((hitHistory.containsKey(uuid) ? hitHistory.addTo(uuid, -1) : 0) > 0) {
            return;
        }
        entity.hurt(getDamageSource(), getCalculatedDamage());
        hitHistory.put(uuid, 5);
        // 创到减速带了
        setDeltaMovement(getDeltaMovement().scale(0.9));
    }

    @Override
    public DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.BOULDER, this);
    }

    /// 追踪玩家
    ///
    /// @param player 玩家
    /// @return 是否成功追踪
    public boolean targetPlayer(@Nullable Player player) {
        if (player == null || !player.isAlive() || player.equals(getOwner())) {
            return false;
        }

        Vec3 currentPos = position();
        Vec3 targetPos = player.position();

        // 如果玩家在巨石上方，则不进行追踪
        if (targetPos.y > currentPos.y) {
            return false;
        }

        // 构建3D曼哈顿路径（L型路径）
        Vec3 cornerPos1, cornerPos2;
        double dx = targetPos.x - currentPos.x;
        double dy = targetPos.y - currentPos.y;
        double dz = targetPos.z - currentPos.z;

        // 确定3D曼哈顿路径的拐点（先在两个维度上移动）
        // 选择距离最大的两个轴向进行移动
        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);
        double absDz = Math.abs(dz);

        if (absDx >= absDy && absDx >= absDz) {
            // X轴方向距离最大
            cornerPos1 = new Vec3(targetPos.x, currentPos.y, currentPos.z);
            if (absDy >= absDz) {
                // Y轴方向次之，先沿X轴和Y轴移动
                cornerPos2 = new Vec3(targetPos.x, targetPos.y, currentPos.z);
            } else {
                // Z轴方向次之，先沿X轴和Z轴移动
                cornerPos2 = new Vec3(targetPos.x, currentPos.y, targetPos.z);
            }
        } else if (absDy >= absDx && absDy >= absDz) {
            // Y轴方向距离最大
            if (absDx >= absDz) {
                // X轴方向次之，先沿Y轴和X轴移动
                cornerPos1 = new Vec3(currentPos.x, targetPos.y, currentPos.z);
                cornerPos2 = new Vec3(targetPos.x, targetPos.y, currentPos.z);
            } else {
                // Z轴方向次之，先沿Y轴和Z轴移动
                cornerPos1 = new Vec3(currentPos.x, targetPos.y, currentPos.z);
                cornerPos2 = new Vec3(currentPos.x, targetPos.y, targetPos.z);
            }
        } else {
            // Z轴方向距离最大
            if (absDx >= absDy) {
                // X轴方向次之，先沿Z轴和X轴移动
                cornerPos1 = new Vec3(currentPos.x, currentPos.y, targetPos.z);
                cornerPos2 = new Vec3(targetPos.x, currentPos.y, targetPos.z);
            } else {
                // Y轴方向次之，先沿Z轴和Y轴移动
                cornerPos1 = new Vec3(currentPos.x, currentPos.y, targetPos.z);
                cornerPos2 = new Vec3(currentPos.x, targetPos.y, targetPos.z);
            }
        }

        // 检查4段路径是否都被阻挡
        boolean pathBlocked = isPathBlocked(currentPos, targetPos) ||
                isPathBlocked(currentPos, cornerPos1) ||
                isPathBlocked(cornerPos1, cornerPos2) ||
                isPathBlocked(cornerPos2, targetPos);

        if (pathBlocked) {
            return false;
        }

        // 设置移动方向
        setDeltaMovement(orientationHorizontal(targetPos).scale(speed));
        return true;
    }

    /// 检查两点间路径是否被阻挡
    private boolean isPathBlocked(Vec3 start, Vec3 end) {
        return level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this)).getType() != HitResult.Type.MISS;
    }

    /// 计算水平方向朝向向量
    ///
    /// @param pos 目标位置
    /// @return 朝向向量
    protected Vec3 orientationHorizontal(Vec3 pos) {
        return orientationHorizontal(position(), pos);
    }

    /// 计算水平方向朝向向量
    ///
    /// @param pos  当前位置
    /// @param pos1 目标位置
    /// @return 朝向向量
    protected Vec3 orientationHorizontal(Vec3 pos, Vec3 pos1) {
        Vector2d vec = new Vector2d(pos1.x, pos1.z)
                .sub(new Vector2d(pos.x, pos.z))
                .normalize();
        return new Vec3(vec.x, pos1.y, vec.y);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder.define(DATA_BLOCK_STATE, getDefaultBlockState()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains("BlockState")) {
            tag.put("BlockState", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, getBlockState()).getOrThrow());
        }
        setBlockState(BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("BlockState")).getOrThrow());
        if (tag.contains("LandingCount")) {
            this.landingCount = tag.getInt("LandingCount");
        }
        if (tag.contains("IsTracking")) {
            this.isTracking = tag.getBoolean("IsTracking");
        }
        if (tag.contains("Speed")) {
            this.speed = tag.getDouble("Speed");
        }
        if (tag.contains("TrackingRange")) {
            this.trackingRange = tag.getFloat("TrackingRange");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("BlockState", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, getBlockState()).getOrThrow());
        tag.putInt("LandingCount", landingCount);
        tag.putBoolean("IsTracking", isTracking);
        tag.putDouble("Speed", speed);
        tag.putFloat("TrackingRange", trackingRange);
    }

    @Override
    protected double getDefaultGravity() {
        return defaultGravity;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    /// 获取当前旋转角度
    ///
    /// @return 旋转角度
    public float getRotate() {
        return rotate;
    }

    /// 设置旋转角度
    ///
    /// @param rotate 旋转角度
    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    /// 获取方块状态
    ///
    /// @return 方块状态
    public BlockState getBlockState() {
        return entityData.get(DATA_BLOCK_STATE);
    }

    public abstract BlockState getDefaultBlockState();

    /// 设置方块状态
    ///
    /// @param blockState 方块状态
    public void setBlockState(BlockState blockState) {
        entityData.set(DATA_BLOCK_STATE, blockState);
    }

    /// 获取追踪范围
    ///
    /// @return 追踪范围
    public float getTrackingRange() {
        return trackingRange;
    }

    /// 设置追踪范围
    ///
    /// @param trackingRange 追踪范围
    public void setTrackingRange(float trackingRange) {
        this.trackingRange = trackingRange;
    }

    /// 获取移动速度
    ///
    /// @return 移动速度
    public double getSpeed() {
        return speed;
    }

    /// 设置移动速度
    ///
    /// @param speed 移动速度
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /// 获取最低被移除速度
    ///
    /// @return 最低被移除速度
    public double getMinRemoveSpeed() {
        return minRemoveSpeed;
    }

    /// 获取旋转半径/大小半径
    ///
    /// @return 大小半径
    public float getSizeRadius() {
        return sizeRadius;
    }

    /// 获取着陆次数
    ///
    /// @return 着陆次数
    public int getLandingCount() {
        return landingCount;
    }

    /// 设置着陆次数
    ///
    /// @param landingCount 着陆次数
    public void setLandingCount(int landingCount) {
        this.landingCount = landingCount;
    }

    /// 获取粒子方块状态
    ///
    /// @return 粒子方块状态
    public BlockState getParticleBlockState() {
        return particleBlockState;
    }

    /// 获取粒子类型
    ///
    /// @return 粒子类型
    public ParticleType<BlockParticleOption> getParticleType() {
        return particleType;
    }

    /// 获取音效
    ///
    /// @return 音效
    public SoundEvent getSound() {
        return sound;
    }

    /// 获取音效类别
    ///
    /// @return 音效类别
    public SoundSource getSoundCategory() {
        return soundCategory;
    }

    /// 是否启用追踪
    ///
    /// @return 是否启用追踪
    public boolean isTracking() {
        return isTracking;
    }

    /// 设置是否启用追踪
    ///
    /// @param tracking 是否启用追踪
    public void setTracking(boolean tracking) {
        isTracking = tracking;
    }
}
