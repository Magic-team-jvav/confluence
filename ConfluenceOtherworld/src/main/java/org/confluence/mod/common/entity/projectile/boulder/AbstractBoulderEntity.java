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
import net.minecraft.world.entity.EntityType;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.boulder.AbstractBoulderBlock;
import org.confluence.mod.common.init.ModDamageTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 抽象巨石实体
 *
 * @author 尽
 */
public abstract class AbstractBoulderEntity extends Projectile {
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE =
            SynchedEntityData.defineId(AbstractBoulderEntity.class, EntityDataSerializers.BLOCK_STATE);

    protected final Object2IntOpenHashMap<UUID> hitHistory = new Object2IntOpenHashMap<>();

    /**
     * 粒子方块状态
     */
    private final BlockState particleBlockState;
    /**
     * 粒子类型
     */
    private final ParticleType<BlockParticleOption> particleType;
    /**
     * 音效
     */
    private final SoundEvent sound;
    /**
     * 音效类别
     */
    private final SoundSource soundCategory;
    /**
     * 最低被移除速度
     */
    private final double minRemoveSpeed;
    /**
     * 默认重力
     */
    private final double defaultGravity;

    /**
     * 旋转角度
     */
    private float rotate;

    public float rotateO;

    // 会持久化的数据
    /**
     * 着陆次数
     */
    private int landingCount;
    /**
     * 是否追踪
     */
    private boolean isTracking;
    /**
     * 移动速度
     */
    private double speed;
    /**
     * 大小半径
     */
    private final float sizeRadius;
    /**
     * 追踪范围
     */
    private float trackingRange;

    private int stillTick;

    /**
     * 构造函数系列，支持不同参数组合创建巨石实体
     *
     * @param entityType 实体类型
     * @param level      世界
     * @param blockState 方块状态
     */
    public AbstractBoulderEntity(EntityType<? extends AbstractBoulderEntity> entityType,
                                 Level level,
                                 BlockState blockState) {
        this(entityType, level, blockState, BoulderEntity.Builder.of());
    }

    /**
     * 构造函数
     *
     * @param entityType 实体类型
     * @param level      世界
     * @param blockState 方块状态
     * @param builder    构建器
     */
    public AbstractBoulderEntity(EntityType<? extends AbstractBoulderEntity> entityType,
                                 Level level,
                                 BlockState blockState,
                                 BoulderEntity.Builder builder) {
        this(entityType, level, Vec3.ZERO, blockState, builder);
    }

    /**
     * 构造函数
     *
     * @param entityType 实体类型
     * @param level      世界
     * @param pos        位置
     * @param blockState 方块状态
     */
    public AbstractBoulderEntity(EntityType<? extends AbstractBoulderEntity> entityType,
                                 Level level,
                                 Vec3 pos,
                                 BlockState blockState) {
        this(entityType, level, pos, blockState, BoulderEntity.Builder.of());
    }

    /**
     * 构造函数
     *
     * @param entityType 实体类型
     * @param level      世界
     * @param pos        位置
     * @param blockState 方块状态
     * @param builder    构建器
     */
    public AbstractBoulderEntity(EntityType<? extends AbstractBoulderEntity> entityType,
                                 Level level,
                                 Vec3 pos,
                                 BlockState blockState,
                                 BoulderEntity.Builder builder) {
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
    }

    /**
     * 主要游戏刻更新方法
     * 处理移动、旋转、碰撞等逻辑
     */
    @Override
    public void tick() {
        super.tick();

        move();
        updateNeighbors();

        scrollRotate();

        // 调用命中方法
        onHit(getHitResult());
        var clipped = level().clip(new ClipContext(position(), position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (clipped.isInside() || this.stillTick >= 20) {
            onRemoveBroken(true);
        }

        // 在地上速度过慢删除
        if (onGround() && getDeltaMovement().length() <= this.minRemoveSpeed) {
            this.stillTick++;
            return;
        }
        this.stillTick = 0;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        worldInit();
    }

    /**
     * 初始运行例如给巨石添加初始动量
     * 仅在添加进世界中触发
     */
    protected void worldInit() {
        var position = position();
        var context = new ClipContext(position, position.relative(Direction.DOWN, 1), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        if (level().clip(context).getType() == HitResult.Type.MISS) {
            return;
        }
        findLocation(position);
    }

    /**
     * 移除巨石
     * @param timeoutUrNot 是否因为超时导致触发
     */
    public void onRemoveBroken(boolean timeoutUrNot) {
        if (!(level() instanceof ServerLevel serverLevel) || isRemoved()) {
            return;
        }
        brokenFunction(serverLevel, timeoutUrNot);
        discard();
    }

    /**
     * 破碎时执行的功能
     *
     * @param serverLevel  服务端世界
     * @param timeoutUrNot 是否因为超时导致触发
     */
    protected void brokenFunction(final ServerLevel serverLevel, boolean timeoutUrNot) {
        var pos = blockPosition();
        brokenParticles(serverLevel, pos);
        brokenSound(serverLevel, pos);
    }

    /**
     * 破碎粒子效果
     *
     * @param serverLevel 服务端世界
     * @param pos         位置
     */
    public void brokenParticles(final ServerLevel serverLevel, final BlockPos pos) {
        var state = this.particleBlockState == null ? getBlockState() : this.particleBlockState;
        serverLevel.sendParticles(new BlockParticleOption(this.particleType, state).setPos(pos),
                getX(), getY() + 0.5, getZ(), 175, 0.0, 0.0, 0.0, 0.15);
    }

    /**
     * 破碎音效
     *
     * @param serverLevel 服务端世界
     * @param pos         位置
     */
    public void brokenSound(final ServerLevel serverLevel, final BlockPos pos) {
        serverLevel.playSound(null, pos, this.sound, this.soundCategory, 5.0F, 1.0F);
    }

    /**
     * 获取碰撞命中结果
     * 包括方块和实体的碰撞检测
     *
     * @return 碰撞结果
     */
    protected HitResult getHitResult() {
        var level = level();
        var delta = getDeltaMovement();
        delta = delta.add(Mth.sign(delta.x) * this.sizeRadius, Mth.sign(delta.y) * this.sizeRadius, Mth.sign(delta.z) * this.sizeRadius);
        var start = position();
        var end = start.add(delta);
        HitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            end = hitResult.getLocation();
        }
        var hitResult1 = ProjectileUtil.getEntityHitResult(level, this, start, end, getBoundingBox()
                .expandTowards(delta)
                .inflate(1.0), this::canHitEntity);
        if (hitResult1 != null) {
            hitResult = hitResult1;
        }
        return hitResult;
    }

    /**
     * 碰撞运算总方法
     * 根据碰撞类型分发到具体处理方法
     *
     * @param hitResult 碰撞结果
     */
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

    /**
     * 滚动旋转计算
     */
    protected void scrollRotate() {
        rotate();
        var delta = getDeltaMovement();
        float s = (float) delta.length();
        float r = s / this.sizeRadius;
        if (this.rotate > Mth.TWO_PI) {
            this.rotate -= Mth.TWO_PI;
        }
        this.rotateO = this.rotate;
        this.rotate += r;
    }

    /**
     * 更新面向方向（基于运动方向）
     */
    protected void rotate() {
        var v = getDeltaMovement();
        this.yRotO = getYRot();
        setYRot((float) (Mth.atan2(-v.x, v.z) * Mth.RAD_TO_DEG));
    }

    /**
     * 执行移动操作
     * 应用重力并移动实体
     */
    protected void move() {
        var vec3 = getDeltaMovement();
        setDeltaMovement(vec3.x, vec3.y - getGravity(), vec3.z);
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.99));
        rotate();
    }

    /**
     * 更新邻居方块
     * 检测周围是否有其他巨石方块并与之交互
     */
    protected void updateNeighbors() {
        if (!level().isClientSide) {
            for (var dir : LibUtils.DIRECTIONS) {
                var blockPos = blockPosition().relative(dir);
                var blockState = level().getBlockState(blockPos);
                if (!(blockState.getBlock() instanceof AbstractBoulderBlock<?> block)) {
                    continue;
                }
                var hitResult = new BlockHitResult(blockPos.getCenter(), dir, blockPos, false);
                block.onProjectileHit(level(), blockState, hitResult, this);
            }
        }

        setDeltaMovement(getDeltaMovement().scale(0.99));
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        var level = level();
        if (level instanceof ServerLevel serverLevel) {
            var pos = blockHitResult.getBlockPos();
            var vec3 = blockHitResult.getLocation();
            var blockState = level.getBlockState(pos);
            serverLevel.sendParticles(new BlockParticleOption(this.particleType, blockState).setPos(pos),
                    vec3.x, vec3.y, vec3.z, 175, 0.0, 0.0, 0.0, 0.15);
            serverLevel.playSound(null, pos, this.sound, this.soundCategory, 10.0F, 1.0F);
        }
        var axis = blockHitResult.getDirection().getAxis();
        if (axis == Direction.Axis.Y) {
            onVerticalBlockHit(blockHitResult);
            return;
        }
        onHorizontalBlockHit(blockHitResult);
    }

    /**
     * 水平碰撞处理（撞墙等）
     *
     * @param blockHitResult 方块碰撞结果
     */
    protected void onHorizontalBlockHit(final BlockHitResult blockHitResult) {
        onRemoveBroken(false);
    }

    /**
     * 垂直碰撞处理（着陆等）
     *
     * @param blockHitResult 方块碰撞结果
     */
    protected void onVerticalBlockHit(final BlockHitResult blockHitResult) {
        if (this.landingCount <= 0 || blockHitResult.getDirection() != Direction.UP) {
            onRemoveBroken(false);
            return;
        }
        var vec3 = blockHitResult.getLocation();
        findLocation(getAdjustMoveVector(vec3, -1));
        this.landingCount--;
    }

    protected Vec3 getAdjustMoveVector(final Vec3 vec3, double v) {
        return vec3.subtract(vec3.scale(Math.pow(vec3.length(), v * (getSizeRadius() * 2))));
    }

    /**
     * 寻找新位置
     * 当巨石弹跳或需要重新定位时调用
     *
     * @param pos 位置
     */
    protected void findLocation(final Vec3 pos) {
        var level = level();
        var position = position();
        var moveVector = getDeltaMovement();
        if (moveVector.x == 0 && moveVector.z == 0 &&
                targetPlayer(level().getNearestPlayer(this, this.trackingRange))) {
            return;
        }
        Vec3 targetPos = null;
        boolean isTranslation = false;
        for (int i = 1; i >= 0; i--) {
            if (i == 0) {
                isTranslation = true;
            }
            var list = getDirectionList(pos, level, i, position, i == 0);
            var size = list.size();
            if (list.isEmpty()) {
                continue;
            }

            if (size > 1) {
                var direction = list.get(getRandom().nextIntBetweenInclusive(0, size - 1));
                targetPos = position.relative(direction, 1);
                break;
            }
            targetPos = position.relative(list.getFirst(), 1);
            break;
        }

        Vec3 subtract = null;
        if (isTranslation) {
            if (targetPlayer(level().getNearestPlayer(this, this.trackingRange))) {
                return;
            }
        }
        if (targetPos != null) {
            subtract = new Vec3(targetPos.x, position.y, targetPos.z);
        }
        if (moveVector.x != 0 || moveVector.z != 0){
            return;
        }
        if (subtract == null) {
            return;
        }

        var orientation = orientationHorizontal(subtract).scale(this.speed);
        setDeltaMovement(new Vec3(orientation.x, orientation.y, orientation.z));
    }

    /**
     * 获取方向列表
     *
     * @param pos      位置
     * @param level    世界
     * @param finalY   Y坐标
     * @param position 当前位置
     * @return 方向列表
     */
    private List<Direction> getDirectionList(final Vec3 pos, final Level level, final int finalY, final Vec3 position, boolean isParallel) {
        return Direction.Plane.HORIZONTAL.stream().map(direction -> {
                    var from = isParallel ? position : position.relative(direction, 1);
                    var to = pos.relative(direction, 1).relative(Direction.DOWN, finalY);
                    var context = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this);
                    return Map.entry(direction, level.clip(context));
                })
                .filter(hitResult -> hitResult.getValue().getType() == HitResult.Type.MISS)
                .map(Map.Entry::getKey).toList();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        var lengthSqr = getDeltaMovement().length();
        if (lengthSqr <= 0.05) {
            return;
        }
        var entity = entityHitResult.getEntity();
        if (!entity.isAlive() || !entity.isAttackable() || entity.equals(getOwner())) {
            return;
        }
        var uuid = entity.getUUID();
        if ((this.hitHistory.containsKey(uuid) ? this.hitHistory.addTo(uuid, -1) : 0) > 0) {
            return;
        }
        var damageSource = ModDamageTypes.of(entity.level(), ModDamageTypes.BOULDER, this);
        entity.hurt(damageSource, 100.0F);
        this.hitHistory.put(uuid, 5);
        // 创到减速带了
        setDeltaMovement(getDeltaMovement().scale(0.9));
    }

    /**
     * 追踪玩家
     *
     * @param player 玩家
     * @return 是否成功追踪
     */
    public boolean targetPlayer(@Nullable Player player) {
        if (player == null || !player.isAlive() || player.equals(getOwner())) {
            return false;
        }

        var currentPos = position();
        var targetPos = player.position();

        // 如果玩家在巨石上方，则不进行追踪
        if (targetPos.y > currentPos.y) {
            return false;
        }

        // 构建3D曼哈顿路径（L型路径）
        Vec3 cornerPos1, cornerPos2;
        var dx = targetPos.x - currentPos.x;
        var dy = targetPos.y - currentPos.y;
        var dz = targetPos.z - currentPos.z;

        // 确定3D曼哈顿路径的拐点（先在两个维度上移动）
        // 选择距离最大的两个轴向进行移动
        var absDx = Math.abs(dx);
        var absDy = Math.abs(dy);
        var absDz = Math.abs(dz);

        if (absDx >= absDy && absDx >= absDz) {
            // X轴方向距离最大
            if (absDy >= absDz) {
                // Y轴方向次之，先沿X轴和Y轴移动
                cornerPos1 = new Vec3(targetPos.x, currentPos.y, currentPos.z);
                cornerPos2 = new Vec3(targetPos.x, targetPos.y, currentPos.z);
            } else {
                // Z轴方向次之，先沿X轴和Z轴移动
                cornerPos1 = new Vec3(targetPos.x, currentPos.y, currentPos.z);
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
        boolean pathBlocked =
                isPathBlocked(currentPos, targetPos) ||
                isPathBlocked(currentPos, cornerPos1) ||
                isPathBlocked(cornerPos1, cornerPos2) ||
                isPathBlocked(cornerPos2, targetPos);

        if (pathBlocked) {
            return false;
        }

        // 设置移动方向
        setDeltaMovement(orientationHorizontal(targetPos).scale(this.speed));
        return true;
    }

    /**
     * 检查两点间路径是否被阻挡
     */
    private boolean isPathBlocked(Vec3 start, Vec3 end) {
        return level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this)).getType() != HitResult.Type.MISS;
    }

    /**
     * 计算水平方向朝向向量
     *
     * @param pos 目标位置
     * @return 朝向向量
     */
    protected Vec3 orientationHorizontal(Vec3 pos) {
        return orientationHorizontal(position(), pos);
    }

    /**
     * 计算水平方向朝向向量
     *
     * @param pos  当前位置
     * @param pos1 目标位置
     * @return 朝向向量
     */
    protected Vec3 orientationHorizontal(Vec3 pos, Vec3 pos1) {
        var vec = new Vector2d(pos1.x, pos1.z)
                .sub(new Vector2d(pos.x, pos.z))
                .normalize();
        return new Vec3(vec.x, pos1.y, vec.y);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLOCK_STATE, getDefaultBlockState());
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
        tag.putInt("LandingCount", this.landingCount);
        tag.putBoolean("IsTracking", this.isTracking);
        tag.putDouble("Speed", this.speed);
        tag.putFloat("TrackingRange", this.trackingRange);
    }

    @Override
    protected double getDefaultGravity() {
        return this.defaultGravity;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    /**
     * 获取当前旋转角度
     *
     * @return 旋转角度
     */
    public float getRotate() {
        return rotate;
    }

    /**
     * 设置旋转角度
     *
     * @param rotate 旋转角度
     */
    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    /**
     * 获取方块状态
     *
     * @return 方块状态
     */
    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE);
    }

    public abstract BlockState getDefaultBlockState();

    /**
     * 设置方块状态
     *
     * @param blockState 方块状态
     */
    public void setBlockState(BlockState blockState) {
        this.entityData.set(DATA_BLOCK_STATE, blockState);
    }

    /**
     * 获取追踪范围
     *
     * @return 追踪范围
     */
    public float getTrackingRange() {
        return trackingRange;
    }

    /**
     * 设置追踪范围
     *
     * @param trackingRange 追踪范围
     */
    public void setTrackingRange(float trackingRange) {
        this.trackingRange = trackingRange;
    }

    /**
     * 获取移动速度
     *
     * @return 移动速度
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * 设置移动速度
     *
     * @param speed 移动速度
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * 获取最低被移除速度
     *
     * @return 最低被移除速度
     */
    public double getMinRemoveSpeed() {
        return minRemoveSpeed;
    }

    /**
     * 获取旋转半径/大小半径
     *
     * @return 大小半径
     */
    public float getSizeRadius() {
        return sizeRadius;
    }

    /**
     * 获取着陆次数
     *
     * @return 着陆次数
     */
    public int getLandingCount() {
        return landingCount;
    }

    /**
     * 设置着陆次数
     *
     * @param landingCount 着陆次数
     */
    public void setLandingCount(int landingCount) {
        this.landingCount = landingCount;
    }

    /**
     * 获取粒子方块状态
     *
     * @return 粒子方块状态
     */
    public BlockState getParticleBlockState() {
        return particleBlockState;
    }

    /**
     * 获取粒子类型
     *
     * @return 粒子类型
     */
    public ParticleType<BlockParticleOption> getParticleType() {
        return particleType;
    }

    /**
     * 获取音效
     *
     * @return 音效
     */
    public SoundEvent getSound() {
        return sound;
    }

    /**
     * 获取音效类别
     *
     * @return 音效类别
     */
    public SoundSource getSoundCategory() {
        return soundCategory;
    }

    /**
     * 是否启用追踪
     *
     * @return 是否启用追踪
     */
    public boolean isTracking() {
        return isTracking;
    }

    /**
     * 设置是否启用追踪
     *
     * @param tracking 是否启用追踪
     */
    public void setTracking(boolean tracking) {
        isTracking = tracking;
    }
}
