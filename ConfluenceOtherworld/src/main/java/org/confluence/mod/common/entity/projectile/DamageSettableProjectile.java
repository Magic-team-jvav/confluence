package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshotCarrier;
import org.jetbrains.annotations.Nullable;

/**
 * 允许武器在生成阶段设置基础伤害的弹幕基类。
 *
 * <p>统一发射事务安装快照后，{@link #getCalculatedDamage()} 只返回快照基础伤害；伤害通道倍率、
 * 暴击和护甲穿透由 MagicLib 的伤害链各应用一次。旧的“命中时读取所有者当前主手前缀”路径已删除。</p>
 */
public abstract class DamageSettableProjectile extends Projectile implements ProjectileCombatSnapshotCarrier {
    private static final String RUNTIME_TAG = "ConfluenceProjectileRuntime";
    private static final int RUNTIME_VERSION = 1;
    protected static final EntityDataAccessor<Float> DATA_DEFAULT_VELOCITY = SynchedEntityData.defineId(DamageSettableProjectile.class, EntityDataSerializers.FLOAT);
    private final ProjectileCombatState combatState = new ProjectileCombatState();
    protected float damage;
    private int ownerResolutionTicks;

    public DamageSettableProjectile(EntityType<? extends DamageSettableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_DEFAULT_VELOCITY, 0.0F);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getCalculatedDamage() {
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        return snapshot == null ? damage : snapshot.baseDamage();
    }

    public float getDamage() {
        return damage;
    }

    public @Nullable LivingEntity getLivingOwner() {
        return getOwner() instanceof LivingEntity living ? living : null;
    }

    public DamageSource getDamageSource() {
        return damageSources().mobProjectile(this, getLivingOwner());
    }

    @Override
    public boolean canHitEntity(Entity target) {
        Entity impacted = ProjectileHitRules.impactedEntity(target);
        return ProjectileHitRules.canHit(getOwner(), target)
                && combatState.canHit(impacted.getUUID(), false);
    }

    /**
     * 记录一次已经成功造成伤害的目标，供发射瞬间的额外命中补偿复用去重状态。
     */
    public final boolean recordSuccessfulHit(Entity target) {
        return combatState.recordSuccessfulHit(ProjectileHitRules.impactedEntity(target).getUUID());
    }

    /**
     * 记录武器发射事务冻结下来的初始速度。
     *
     * <p>该数值会被云分裂弹幕、蓄力弹幕和拖拽弹幕继续使用，因此不能允许 NaN、无穷大或
     * 不合理的负值进入后续向量计算。</p>
     */
    public void setDefaultVelocity(float defaultVelocity) {
        if (!isSupportedDefaultVelocity(defaultVelocity)) {
            throw new IllegalArgumentException("Default projectile velocity is outside the supported range");
        }
        entityData.set(DATA_DEFAULT_VELOCITY, defaultVelocity);
    }

    public float getDefaultVelocity() {
        return entityData.get(DATA_DEFAULT_VELOCITY);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        setDefaultVelocity(velocity);
        super.shoot(x, y, z, velocity, inaccuracy);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        combatState.readFrom(compound);
        if (combatState.isInvalid()) {
            return;
        }

        // 1.20 只接受本轮重写后的当前格式，不读取旧扁平 DefaultVelocity 字段。
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            combatState.invalidate("Missing or invalid projectile runtime state");
            return;
        }
        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("DefaultVelocity", Tag.TAG_FLOAT)) {
            combatState.invalidate("Malformed projectile runtime state");
            return;
        }
        float restoredVelocity = runtime.getFloat("DefaultVelocity");
        if (!isSupportedDefaultVelocity(restoredVelocity)) {
            combatState.invalidate("Default projectile velocity is outside the supported range");
            return;
        }
        entityData.set(DATA_DEFAULT_VELOCITY, restoredVelocity);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        combatState.writeTo(compound, -1, -1);
        float defaultVelocity = getDefaultVelocity();
        if (!isSupportedDefaultVelocity(defaultVelocity)) {
            throw new IllegalStateException("Default projectile velocity is outside the supported range");
        }
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putFloat("DefaultVelocity", defaultVelocity);
        compound.put(RUNTIME_TAG, runtime);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && combatState.discardIfInvalid(this)) {
            return;
        }
        if (!level().isClientSide && combatState.wasLoadedFromTag()
                && combatState.snapshot() != null) {
            Entity restoredOwner = getOwner();
            if (restoredOwner == null) {
                if (ownerResolutionTicks++ == 0) {
                    return;
                }
                combatState.invalidate("Projectile owner could not be resolved after loading");
                combatState.discardIfInvalid(this);
                return;
            }
            if (!(restoredOwner instanceof ServerPlayer)) {
                combatState.invalidate("Loaded player projectile owner is not a server player");
                combatState.discardIfInvalid(this);
                return;
            }
        }
        super.tick();
    }

    @Override
    public @Nullable ProjectileCombatSnapshot getProjectileCombatSnapshot() {
        return combatState.snapshot();
    }

    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        combatState.installSnapshot(snapshot);
        this.damage = snapshot.baseDamage();
    }

    /**
     * 供魔法弹等组合层共享 UUID 去重和保存状态。
     */
    protected final ProjectileCombatState combatState() {
        return combatState;
    }

    /**
     * 子类在调用 {@code super.tick()} 后判断是否必须立即返回。
     *
     * <p>除已销毁和坏状态外，存档恢复后的所有者 UUID 还可能需要一个 tick 才解析为实体。基类在
     * 该宽限 tick 主动返回，但 Java 不会因此终止子类覆盖方法，所以有自定义碰撞循环的子类必须
     * 查询本方法，避免无主弹幕继续移动或造成伤害。</p>
     */
    protected final boolean shouldAbortSubclassTick() {
        return isRemoved()
                || combatState.isInvalid()
                || !level().isClientSide
                && combatState.wasLoadedFromTag()
                && combatState.snapshot() != null
                && !(getOwner() instanceof ServerPlayer);
    }

    private static boolean isSupportedDefaultVelocity(float velocity) {
        return Float.isFinite(velocity) && velocity >= 0.0F;
    }
}
