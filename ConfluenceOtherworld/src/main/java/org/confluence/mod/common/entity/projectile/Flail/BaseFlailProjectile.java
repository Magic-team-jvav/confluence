package org.confluence.mod.common.entity.projectile.Flail;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * <h1>连枷子投射物抽象基类</h1>
 * 由 {@link FlailStrategy} 在连枷状态机各阶段生成的附属投射物基类。
 * <p>
 * 子类需重写 {@link #subTick()} 实现自定义行为，并在构造函数中调用
 * {@link #BaseFlailProjectile(EntityType, Level, BaseFlailEntity, Player)} 传入父连枷和持有者。
 *
 * @see BaseFlailEntity
 * @see FlailStrategy
 */
public abstract class BaseFlailProjectile extends Projectile implements GeoAnimatable {
    /** 渲染模式 */
    public enum RenderMode { BILLBOARD, JSON_MODEL, GEO }

    protected static final EntityDataAccessor<Integer> DATA_LIFETIME =
            SynchedEntityData.defineId(BaseFlailProjectile.class, EntityDataSerializers.INT);

    /** 生成此投射物的连枷实体 */
    @Nullable
    private BaseFlailEntity parentFlail;

    /** 最大存活 tick 数，≤0 表示无限 */
    private int maxLifetime;

    /** 当前存活 tick 计数 */
    private int tickCount;

    /** 基础伤害值 */
    protected float baseDamage;

    /** GeckoLib 动画缓存 */
    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 返回此投射物的渲染模式。
     * <p>默认 {@link RenderMode#BILLBOARD}，子类可覆盖为 {@code JSON_MODEL} 或 {@code GEO}。
     */
    @NotNull
    public RenderMode getRenderMode() {
        return RenderMode.BILLBOARD;
    }

    /**
     * @param entityType  实体类型
     * @param level       世界
     * @param parentFlail 生成此投射物的连枷（可为 null）
     * @param owner       持有者玩家（设置为投射物所有者）
     */
    protected BaseFlailProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                                  @NotNull Level level,
                                  @Nullable BaseFlailEntity parentFlail,
                                  @Nullable Player owner) {
        super(entityType, level);
        this.parentFlail = parentFlail;
        if (owner != null) {
            setOwner(owner);
        }
    }

    /**
     * 无参构造器，供 {@link EntityType.Builder} 反射使用。
     * 子类必须提供此构造器并调用 super(entityType, level)。
     */
    protected BaseFlailProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                                  @NotNull Level level) {
        this(entityType, level, null, null);
    }

    // ── 数据同步 ──

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_LIFETIME, 0);
    }

    public int getLifetime() {
        return entityData.get(DATA_LIFETIME);
    }

    public void setLifetime(int lifetime) {
        entityData.set(DATA_LIFETIME, lifetime);
    }

    // ── 访问器 ──

    @Nullable
    public BaseFlailEntity getParentFlail() {
        return parentFlail;
    }

    public void setParentFlail(@Nullable BaseFlailEntity parentFlail) {
        this.parentFlail = parentFlail;
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    /**
     * 设置最大存活时间（tick），≤0 表示永不超时移除。
     */
    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public int getTickCount() {
        return tickCount;
    }

    public float getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
    }

    @Nullable
    public Player getOwnerAsPlayer() {
        Entity owner = getOwner();
        return owner instanceof Player player ? player : null;
    }

    /**
     * 返回此投射物的渲染贴图路径。
     * <p>默认返回 {@code null}，表示使用 {@link FlailProjectileRenderer} 的默认贴图。
     * 子类可覆盖以指定专属贴图。
     *
     * @return 贴图 ResourceLocation，null 使用默认贴图
     */
    @Nullable
    public ResourceLocation getTexture() {
        return null;
    }

    /**
     * 返回 JSON 模型的 {@link ModelLayerLocation}（{@link RenderMode#JSON_MODEL} 时使用）。
     * <p>默认返回 {@code null}。
     */
    @Nullable
    public ModelLayerLocation getModelLayerLocation() {
        return null;
    }

    /**
     * 返回 Geo 模型的 {@code .geo.json} 路径（{@link RenderMode#GEO} 时使用）。
     * <p>默认返回 {@code null}，回退到 {@link Confluence#asResource(String) "geo/entity/flail/flail.geo.json"}。
     */
    @Nullable
    public ResourceLocation getGeoModelLocation() {
        return null;
    }

    /**
     * 返回 Geo 模型的动画 {@code .animation.json} 路径（{@link RenderMode#GEO} 时使用）。
     * <p>默认返回 {@code null} 表示无动画。
     */
    @Nullable
    public ResourceLocation getAnimationLocation() {
        return null;
    }

    // ── Tick ──

    @Override
    public final void tick() {
        Entity owner = getOwner();
        // 所有者无效时移除
        if (!level().isClientSide() && (owner == null || owner.isRemoved())) {
            discard();
            return;
        }

        super.tick();

        // 碰撞检测：MC 1.21 的 Projectile.tick() 不再自动检测，需手动处理
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            if (!onProjectileBlockHit((BlockHitResult) hitResult)) {
                discard();
                return;
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        // 超时检查
        if (maxLifetime > 0 && ++tickCount >= maxLifetime) {
            if (!level().isClientSide()) {
                discard();
            }
            return;
        }

        // 委托子类行为
        subTick();
    }

    /**
     * 子类重写此方法实现自定义每帧行为。
     * 基类已处理：所有者有效性检查、超时移除、{@code super.tick()}。
     */
    protected abstract void subTick();

    // ── 碰撞 ──

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity target = result.getEntity();
        if (!(target instanceof LivingEntity living) || !target.isAlive()) return;
        onHitLiving(living);
    }

    /**
     * 子类重写以处理命中活体实体的逻辑。
     * <p>默认空实现，由子类决定伤害计算和效果施加。
     */
    protected void onHitLiving(@NotNull LivingEntity target) {}

    /**
     * 子类重写以处理命中方块的行为（如弹跳）。
     * <p>默认命中方块后移除投射物。返回 {@code true} 可阻止默认移除行为，子类应在此方法中自行处理反弹逻辑。
     * @param hitResult 方块碰撞结果
     * @return true 表示已处理（阻止移除），false 执行默认移除
     */
    protected boolean onProjectileBlockHit(@NotNull BlockHitResult hitResult) {
        return false;
    }

    /**
     * 快捷方法：使投射物面朝速度方向。
     */
    protected void faceVelocity() {
        Vec3 motion = getDeltaMovement();
        double hDist = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        setYRot((float) Math.toDegrees(Math.atan2(motion.x, motion.z)));
        setXRot((float) Math.toDegrees(Math.atan2(-motion.y, hDist)));
    }

    // ── 持久化 ──

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        if (tag.contains("MaxLifetime")) maxLifetime = tag.getInt("MaxLifetime");
        if (tag.contains("TickCount")) tickCount = tag.getInt("TickCount");
        if (tag.contains("BaseDamage")) baseDamage = tag.getFloat("BaseDamage");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("MaxLifetime", maxLifetime);
        tag.putInt("TickCount", tickCount);
        tag.putFloat("BaseDamage", baseDamage);
    }

    // ── GeoAnimatable ──

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }

    @Override
    public double getTick(Object animatable) {
        return tickCount;
    }
}
