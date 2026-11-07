package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 昏晓的「小日光 / 小月光」共用的飞行 → 附着逻辑。
///
/// 飞行阶段持续追踪最近的敌怪；命中敌怪后停在它头顶，按各自的节奏结算效果；
/// 宿主死亡时补发一次弹幕（日光补发的是不带效果的弹幕，月光则把自身再射向下一个目标）。
/// 同一敌怪头顶只会挂一个附着体，日光与月光互斥。
public abstract class AbstractLittleCelestialEntity extends AbstractManaProjectile implements GeoAnimatable {
    private final AnimatableInstanceCache geckolibCache = GeckoLibUtil.createInstanceCache(this);
    protected static final EntityDataAccessor<Integer> DATA_HOST_ID = SynchedEntityData.defineId(AbstractLittleCelestialEntity.class, EntityDataSerializers.INT);
    /// 追踪范围
    protected static final double SEEK_RANGE = 20.0;
    /// 飞行速度（格/tick）
    protected static final double SEEK_SPEED = 0.55;
    /// 悬停在宿主头顶的高度
    protected static final double HEAD_OFFSET = 0.45;
    /// 飞行阶段的兜底存在时间（tick）
    protected static final int FLIGHT_LIFETIME = 200;
    /// 找附着体时向宿主身上膨胀的范围
    private static final double ATTACHMENT_LOOKUP = 4.0;

    private int attachedTicks;
    private boolean inert;
    /// 是否自动索敌：玩家手动发射的弹幕只按玩家弹道直飞，不追踪
    private boolean homing;

    protected AbstractLittleCelestialEntity(EntityType<? extends AbstractLittleCelestialEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HOST_ID, -1);
    }

    /// 不带效果的弹幕（宿主死亡后补发的那种）：命中只结算伤害，不再附着
    public boolean isInert() {
        return inert;
    }

    public void setInert(boolean inert) {
        this.inert = inert;
    }

    /// 飞行阶段是否自动追踪最近的敌怪；只有宿主死亡后补发的弹幕需要追踪
    public boolean isHoming() {
        return homing;
    }

    public void setHoming(boolean homing) {
        this.homing = homing;
    }

    public boolean isAttached() {
        return entityData.get(DATA_HOST_ID) >= 0;
    }

    /// 当前附着的宿主；未附着或宿主已消失时返回 null
    public @Nullable LivingEntity getHost() {
        int id = entityData.get(DATA_HOST_ID);
        if (id < 0) return null;
        Entity entity = level().getEntity(id);
        return entity instanceof LivingEntity living && living.isAlive() ? living : null;
    }

    private void setHost(LivingEntity host) {
        entityData.set(DATA_HOST_ID, host.getId());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LivingEntity host = getHost();
        if (host != null) {
            followHost(host);
            if (!level().isClientSide) {
                this.attachedTicks++;
                if (attachedTicks > getAttachedLifetime()) {
                    onExpire();
                    return;
                }
                tickAttached(host, attachedTicks);
            }
            return;
        }
        if (isAttached()) {
            // 宿主死亡或消失
            if (!level().isClientSide) onHostLost();
            return;
        }
        if (homing) seekTarget();
        doSimpleMove();
        doAgeCheck(FLIGHT_LIFETIME);
    }

    /// 附着期间本体不参与碰撞，只跟随宿主头顶
    @Override
    protected void doHitCheck() {
        if (!isAttached()) super.doHitCheck();
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return !isAttached() && super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) return;
        Entity raw = result.getEntity();
        Entity entity = LibEntityUtils.dedupeIdentity(raw);
        if (inert) {
            // 不带效果的弹幕：只打伤害
            doHurtAndKnockback(entity, 0.0, 0.0);
            discard();
            return;
        }
        LivingEntity host = LibEntityUtils.effectTarget(entity);
        if (host == null || host == getOwner() || !host.isAlive() || !LibEntityUtils.canHit(getOwner(), host)) {
            discard();
            return;
        }
        // 命中先结算一次武器伤害，无论接下来是附着还是触发已有附着
        doHurtAndKnockback(host, 0.0, 0.0);
        AbstractLittleCelestialEntity existing = findAttachment(level(), host);
        if (existing != null) {
            // 命中已有附着：只触发那个附着的效果，不刷新时长也不替换，附着结束后才能更换
            existing.triggerEffect();
            discard();
            return;
        }
        attachTo(host);
    }

    /// 查找某个敌怪头顶已经挂着的附着体
    public static @Nullable AbstractLittleCelestialEntity findAttachment(Level level, LivingEntity host) {
        AABB area = host.getBoundingBox().inflate(ATTACHMENT_LOOKUP);
        for (AbstractLittleCelestialEntity attachment : level.getEntitiesOfClass(AbstractLittleCelestialEntity.class, area)) {
            if (attachment.isAttached() && attachment.getHost() == host) return attachment;
        }
        return null;
    }

    protected void attachTo(LivingEntity host) {
        setHost(host);
        this.attachedTicks = 0;
        followHost(host);
        setYRot(host.getYRot());
        setXRot(0.0F);
        onAttached(host);
    }

    protected void followHost(LivingEntity host) {
        setPos(host.getX(), host.getY() + host.getBbHeight() + HEAD_OFFSET, host.getZ());
        setDeltaMovement(Vec3.ZERO);
    }

    /// 飞行阶段：朝最近的敌怪头顶缓慢靠近
    protected void seekTarget() {
        LivingEntity target = findNearestEnemy(null);
        if (target == null) return;
        Vec3 aim = target.position().add(0.0, target.getBbHeight(), 0.0);
        Vec3 offset = aim.subtract(position());
        if (offset.lengthSqr() > 1.0E-4) {
            setDeltaMovement(offset.normalize().scale(SEEK_SPEED));
        }
    }

    /// 找离自己最近的、可以命中的敌怪
    protected @Nullable LivingEntity findNearestEnemy(@Nullable LivingEntity exclude) {
        LivingEntity nearest = null;
        double best = SEEK_RANGE * SEEK_RANGE;
        for (Entity candidate : LibEntityUtils.queryHitCandidates(level(), this, new AABB(blockPosition()).inflate(SEEK_RANGE), e -> LibEntityUtils.effectTarget(e) != null)) {
            LivingEntity living = LibEntityUtils.effectTarget(candidate);
            if (living == null || living == getOwner() || living == exclude || !living.isAlive() || living.isSpectator()) continue;
            if (!LibEntityUtils.canHit(getOwner(), living)) continue;
            double distance = living.distanceToSqr(this);
            if (distance < best) {
                best = distance;
                nearest = living;
            }
        }
        return nearest;
    }

    /// 宿主死亡：瞄准附近的敌怪补发一次弹幕，然后本体消失
    protected void fireAtNearest(@Nullable LivingEntity exclude, boolean inertChild) {
        LivingEntity target = findNearestEnemy(exclude);
        if (target != null) {
            AbstractLittleCelestialEntity child = createChild(inertChild);
            child.setHoming(true);
            child.setPos(getX(), getY(), getZ());
            Vec3 direction = target.getBoundingBox().getCenter().subtract(position());
            if (direction.lengthSqr() > 1.0E-4) {
                child.setDeltaMovement(direction.normalize().scale(SEEK_SPEED));
            }
            level().addFreshEntity(child);
        }
        discard();
    }

    /// 生成一个同类型的后继弹幕
    protected abstract AbstractLittleCelestialEntity createChild(boolean inert);

    /// 附着后能持续多少 tick
    protected abstract int getAttachedLifetime();

    /// 附着期间每 tick 调用的效果结算（仅服务端）
    protected abstract void tickAttached(LivingEntity host, int ticks);

    /// 被任意弹幕命中时立刻结算一次自身效果（日光 → 一次脉冲，月光 → 一次射线）
    public abstract void triggerEffect();

    protected abstract void onAttached(LivingEntity host);

    /// 持续时间结束
    protected abstract void onExpire();

    /// 宿主死亡
    protected abstract void onHostLost();

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("HostId", entityData.get(DATA_HOST_ID));
        compound.putInt("AttachedTicks", attachedTicks);
        compound.putBoolean("Inert", inert);
        compound.putBoolean("Homing", homing);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(DATA_HOST_ID, compound.getInt("HostId"));
        this.attachedTicks = compound.getInt("AttachedTicks");
        this.inert = compound.getBoolean("Inert");
        this.homing = compound.getBoolean("Homing");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public double getTick(Object object) {
        return tickCount;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geckolibCache;
    }
}
