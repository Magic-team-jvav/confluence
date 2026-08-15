package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.mana.CloudRodItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

/**
 * 猩红魔杖与雨云魔杖生成的母云弹幕，负责追踪落点并按固定周期派生雨滴。
 *
 * <p>雨滴实体类型、周期、穿透上限和目标 UUID 都会影响服务端玩法，因此必须作为一份版本化
 * 运行状态原子恢复。运行时实体数字 ID 只用于双端同步；未知注册 ID、错误类型或越界数值会
 * 使当前存档实体失效，不能借助默认注册表继续生成其他实体。</p>
 */
public class CloudProjectile extends AbstractManaProjectile implements GeoEntity {
    protected static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(CloudProjectile.class, EntityDataSerializers.INT);
    private static final String RUNTIME_TAG = "ConfluenceCloudRuntime";
    private static final int RUNTIME_VERSION = 1;
    private static final int NO_TARGET_ID = -114514;

    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    protected @Nullable UUID targetUUID;
    protected transient @Nullable LivingEntity target;
    protected transient double motionY;
    private EntityType<? extends RainProjectile> rainType;
    private int duration;
    private int maxPenetrate;
    /**
     * 当前格式损坏后只允许无副作用地销毁，不再进入派生雨滴路径。
     */
    private boolean invalidRuntimeState;
    /**
     * 仅重载实体需要验证云杖引用；新生成实体会在同一事务的成功回调中登记。
     */
    private boolean requiresTrackingReference;

    public CloudProjectile(EntityType<? extends CloudProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        applyLocalDefaults(entityType);
    }

    public CloudProjectile(EntityType<? extends CloudProjectile> cloudType, EntityType<? extends RainProjectile> rainType, LivingEntity living, int duration, int maxPenetrate) {
        this(cloudType, living.level());
        configureRain(rainType, duration, maxPenetrate);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TARGET_ID, NO_TARGET_ID);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_TARGET_ID.equals(key)) {
            if (level().isClientSide && level().getEntity(entityData.get(DATA_TARGET_ID)) instanceof LivingEntity living) {
                this.target = living;
            }
            if (target != null) {
                double d = target.distanceTo(this);
                double h = getY() - target.getY() - target.getBbHeight();
                double v0 = getDefaultVelocity();
                double vy = getDeltaMovement().y;
                this.motionY = 2 * v0 * v0 * (2 - h) / (d * d) - 2 * vy * v0 / d;
            }
        }
    }

    @Override
    public void baseTick() {
        if (invalidRuntimeState) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        if (requiresTrackingReference
                && !level().isClientSide
                && getOwner() instanceof ServerPlayer player
                && !CloudRodItem.isTrackedCloud(player, this)) {
            discard();
            return;
        }
        Entity owner = getOwner();
        if (tickCount > 5 * 60 * 20 || owner == null || owner.position().distanceToSqr(position()) > 64 * 64) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3);
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            setTarget(null);
            motion = Vec3.ZERO;
        }
        setDeltaMovement(motion);

        if (motion.x == 0 && motion.y == 0 && motion.z == 0) {
            if (!level().isClientSide && (duration <= 1 || level().getGameTime() % duration == 0)) {
                if (getOwner() instanceof ServerPlayer player) {
                    ProjectileCombatSnapshot parentSnapshot = getProjectileCombatSnapshot();
                    if (parentSnapshot == null || rainType == null) {
                        return;
                    }
                    float width = getDimensions(getPose()).width() * 1.2F;
                    Vec3 spawnPosition = position().add(
                            (random.nextFloat() - 0.5F) * width,
                            -1,
                            (random.nextFloat() - 0.5F) * width
                    );
                    RainProjectile entity = new RainProjectile(rainType, player, spawnPosition);
                    entity.setMaxPenetrate(maxPenetrate);
                    // 雨滴原本以零初速生成，随后由自身 tick 施加向下加速度。快照保留父弹幕的
                    // 合法冻结弹速，单枚发射描述再以零倍率把实际初速度压为零；这样既不会重新
                    // 读取玩家当前装备，也不会为“静止生成”放宽所有战斗快照的数值约束。
                    ProjectileCombatSnapshot rainSnapshot = parentSnapshot.derive(
                            parentSnapshot.baseDamage(),
                            parentSnapshot.resolvedVelocity(),
                            0.0F);
                    ServerProjectileFireService.spawnDerived(
                            player,
                            rainSnapshot,
                            List.of(new ProjectileLaunch(
                                    entity,
                                    spawnPosition,
                                    new Vec3(0.0, -1.0, 0.0),
                                    0.0F)));
                }
            }
        } else if (getTarget() != null && !target.isRemoved()) {
            if (Mth.square(getX() - target.getX()) + Mth.square(getZ() - target.getZ()) < 4) {
                setPos(target.position().add(0, target.getBbHeight() + 2, 0));
                setDeltaMovement(Vec3.ZERO);
                setTarget(null);
            } else {
                setDeltaMovement(motion.add(0, motionY, 0));
            }
        } else {
            setTarget(null);
        }
    }

    @Override
    protected void doHitCheck() {}

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
        if (target == null) {
            this.targetUUID = null;
            entityData.set(DATA_TARGET_ID, NO_TARGET_ID);
        } else {
            this.targetUUID = target.getUUID();
            entityData.set(DATA_TARGET_ID, target.getId());
        }
    }

    public @Nullable LivingEntity getTarget() {
        if (target == null && targetUUID != null && level() instanceof ServerLevel level) {
            Entity entity = level.getEntity(targetUUID);
            if (entity instanceof LivingEntity living) {
                this.target = living;
                // UUID 是持久身份；解析成功后必须重新发布本进程的实体数字 ID 供客户端追踪。
                entityData.set(DATA_TARGET_ID, living.getId());
            }
        }
        return target;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        invalidRuntimeState = false;
        requiresTrackingReference = false;
        targetUUID = null;
        target = null;
        motionY = 0.0;
        entityData.set(DATA_TARGET_ID, NO_TARGET_ID);

        if (!compound.contains(RUNTIME_TAG)) {
            // 进入反序列化路径就代表正在加载实体；缺少当前根的旧数据必须直接失效。
            invalidRuntimeState = true;
            return;
        }
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            invalidRuntimeState = true;
            return;
        }

        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("RainType", Tag.TAG_STRING)
                || !runtime.contains("Duration", Tag.TAG_INT)
                || !runtime.contains("MaxPenetrate", Tag.TAG_INT)
                || (runtime.contains("Target") && !runtime.hasUUID("Target"))) {
            invalidRuntimeState = true;
            return;
        }

        ResourceLocation rainId = ResourceLocation.tryParse(runtime.getString("RainType"));
        EntityType<? extends RainProjectile> savedRainType = resolveRainType(rainId);
        int savedDuration = runtime.getInt("Duration");
        int savedMaxPenetrate = runtime.getInt("MaxPenetrate");
        if (savedRainType == null
                || savedDuration < 1
                || savedMaxPenetrate < 1) {
            invalidRuntimeState = true;
            return;
        }

        rainType = savedRainType;
        duration = savedDuration;
        maxPenetrate = savedMaxPenetrate;
        targetUUID = runtime.hasUUID("Target") ? runtime.getUUID("Target") : null;
        requiresTrackingReference = true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        if (targetUUID != null) {
            runtime.putUUID("Target", targetUUID);
        }
        ResourceLocation rainId = BuiltInRegistries.ENTITY_TYPE.getKey(rainType);
        if (rainId == null) {
            throw new IllegalStateException("Cloud rain type is not registered");
        }
        runtime.putString("RainType", rainId.toString());
        runtime.putInt("Duration", duration);
        runtime.putInt("MaxPenetrate", maxPenetrate);
        compound.put(RUNTIME_TAG, runtime);
    }

    /**
     * 根据母云自身类型提供 1.20 本地注册表默认值，不能复用 1.21 的 ID 字面量。
     */
    private void applyLocalDefaults(EntityType<? extends CloudProjectile> cloudType) {
        if (cloudType == ModEntities.RAIN_CLOUD.get()) {
            rainType = ModEntities.RAIN.get();
            duration = 2;
            maxPenetrate = 5;
        } else {
            rainType = ModEntities.BLOOD_RAIN.get();
            duration = 3;
            maxPenetrate = 2;
        }
    }

    /**
     * 公共构造路径尽早拒绝开发者传入的无效配置；异常文本保持英文。
     */
    private void configureRain(EntityType<? extends RainProjectile> type, int configuredDuration, int configuredMaxPenetrate) {
        if (type == null) {
            throw new IllegalArgumentException("Cloud rain type cannot be null");
        }
        if (configuredDuration < 1) {
            throw new IllegalArgumentException("Cloud rain duration must be positive");
        }
        if (configuredMaxPenetrate < 1) {
            throw new IllegalArgumentException("Cloud rain penetration must be positive");
        }
        rainType = type;
        duration = configuredDuration;
        maxPenetrate = configuredMaxPenetrate;
    }

    /**
     * 实体注册表有默认值，必须先走 Optional 查询；随后实例化一个未入世界的探针验证运行时类型，
     * 防止泛型擦除或外部数据把任意实体类型伪装成雨滴。
     */
    @SuppressWarnings("unchecked")
    private @Nullable EntityType<? extends RainProjectile> resolveRainType(@Nullable ResourceLocation id) {
        if (id == null) return null;
        EntityType<?> rawType = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (rawType == null) return null;
        Entity probe = rawType.create(level());
        boolean valid = probe instanceof RainProjectile;
        if (probe != null) probe.discard();
        return valid ? (EntityType<? extends RainProjectile>) rawType : null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}
