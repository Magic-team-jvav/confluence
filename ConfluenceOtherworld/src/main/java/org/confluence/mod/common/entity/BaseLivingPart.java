package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyRules;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 活体子实体的共用所有权、网络同步、存档恢复和生命周期。
/// 服务端以 UUID 为权威身份；客户端同时校验同步 ID 和 UUID，避免实体 ID 复用后串绑。
/// 区块卸载只释放缓存，明确死亡或超过加载宽限才清理部件。
public abstract class BaseLivingPart<T extends LivingEntity> extends Monster implements PartHitTarget {
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    private static final String OWNER_TAG = "Owner";
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BaseLivingPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(BaseLivingPart.class, EntityDataSerializers.OPTIONAL_UUID);

    private @Nullable T owner;
    private @Nullable UUID ownerUuid;
    private int unresolvedOwnerTicks;
    private @Nullable CreatureDifficultyRules.Difficulty appliedDifficulty;
    private boolean deathNotified;
    private boolean releasingOwner;

    protected BaseLivingPart(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        noPhysics = true;
        noCulling = true;
        setNoGravity(true);
        setNoAi(true);
    }

    protected final boolean bindTo(T owner) {
        if (!isAlive() || releasingOwner || owner == null || owner == this || owner.level() != level() || !owner.isAlive()
                || ownerUuid != null && !ownerUuid.equals(owner.getUUID())) {
            return false;
        }
        boolean freshBinding = ownerUuid == null;
        this.owner = owner;
        ownerUuid = owner.getUUID();
        entityData.set(OWNER_ID, owner.getId());
        entityData.set(OWNER_UUID, Optional.of(ownerUuid));
        if (!attachToOwner(owner)) {
            releaseOwner(RemovalReason.DISCARDED);
            entityData.set(OWNER_ID, -1);
            if (freshBinding) {
                ownerUuid = null;
                entityData.set(OWNER_UUID, Optional.empty());
            }
            return false;
        }
        unresolvedOwnerTicks = 0;
        if (!level().isClientSide) {
            applyDifficulty(freshBinding);
            updateOwnerAttributes(owner);
        }
        return true;
    }

    public final boolean isOwnedBy(LivingEntity candidate) {
        UUID uuid = level().isClientSide ? entityData.get(OWNER_UUID).orElse(null) : ownerUuid;
        return candidate != null && candidate.level() == level() && candidate.getUUID().equals(uuid);
    }

    public final @Nullable T getOwner() {
        resolveOwner();
        return owner;
    }

    public final float getPartHealth() {
        return getHealth();
    }

    public final void setPartHealth(float health) {
        if (!Float.isFinite(health)) {
            return;
        }
        if (health > getMaxHealth()) {
            var maximumHealth = getAttribute(Attributes.MAX_HEALTH);
            if (maximumHealth != null) {
                maximumHealth.setBaseValue(health);
            }
        }
        setHealth(health);
    }

    public final void indicateHurt() {
        markHurt();
    }

    public final boolean isHurtFlashing() {
        return hurtTime > 0;
    }

    @Override
    public final Entity damageRecipient() {
        return this;
    }

    @Override
    public final Entity encounterOwner() {
        T resolvedOwner = getOwner();
        return resolvedOwner == null ? this : resolvedOwner;
    }

    @Override
    public final Entity dedupeIdentity() {
        return this;
    }

    @Override
    public final boolean acceptsDirectHit() {
        T resolvedOwner = getOwner();
        return isAlive() && resolvedOwner != null && resolvedOwner.isAlive();
    }

    protected abstract Class<T> getOwnerType();

    protected abstract void tickPart(T owner);

    protected void onPartDestroyed(T owner) {
    }

    protected void onPartHealthChanged(T owner, float remainingHealth) {
    }

    protected int ownerResolutionGraceTicks() {
        return OWNER_RESOLUTION_GRACE_TICKS;
    }

    protected String ownerSaveTag() {
        return OWNER_TAG;
    }

    protected boolean persistPart() {
        return true;
    }

    protected boolean discardImmediatelyOnDeath() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!Float.isFinite(amount) || amount <= 0.0F) {
            return false;
        }
        T resolvedOwner = getOwner();
        if (resolvedOwner == null || !resolvedOwner.isAlive() || !isAlive()
                || !attachToOwner(resolvedOwner)) {
            return false;
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide) {
            onPartHurt(resolvedOwner, source);
            if (isAlive()) {
                onPartHealthChanged(resolvedOwner, getHealth());
            }
        }
        return hurt;
    }

    @Override
    public final void die(DamageSource source) {
        if (deathNotified || isRemoved()) {
            return;
        }
        deathNotified = true;
        T resolvedOwner = getOwner();
        boolean immediate = discardImmediatelyOnDeath();
        if (immediate) {
            setHealth(0);
        } else {
            super.die(source);
            // Forge 可以取消死亡；只有原版确认死亡后才销毁本体中的部件槽位。
            if (!dead) {
                deathNotified = false;
                return;
            }
        }
        if (!level().isClientSide && resolvedOwner != null) {
            onPartHealthChanged(resolvedOwner, 0);
            onPartDestroyed(resolvedOwner);
        }
        if (immediate) {
            discard();
        }
    }

    /// 活体部件由本体定位，命中时不允许独立偏离本体。
    @Override
    public void knockback(double strength, double x, double z) {
    }

    /// 部件位置由 tickPart 更新，不能先让普通生物移动再叠加一次专用位移。
    @Override
    public void travel(Vec3 travelVector) {
    }

    @Override
    protected float tickHeadTurn(float bodyYaw, float animationSpeed) {
        LibEntityUtils.setEntityYaw(this, getYRot());
        return animationSpeed;
    }

    @Override
    public final void tick() {
        super.tick();
        if (!isAlive()) {
            return;
        }
        T resolvedOwner = getOwner();
        if (resolvedOwner == null) {
            if (!level().isClientSide && ++unresolvedOwnerTicks > ownerResolutionGraceTicks()) {
                discard();
            }
            return;
        }
        unresolvedOwnerTicks = 0;
        if (!resolvedOwner.isAlive() || !level().isClientSide && !attachToOwner(resolvedOwner)) {
            discard();
            return;
        }
        if (!level().isClientSide) {
            applyDifficulty(false);
            updateOwnerAttributes(resolvedOwner);
        }
        tickPart(resolvedOwner);
        // 部件完成位移和转向后，同步本刻的身体和头部朝向。
        LibEntityUtils.setEntityYaw(this, getYRot());
    }

    private void applyDifficulty(boolean freshBinding) {
        var difficulty = CreatureDifficultyScaling.difficulty(this);
        if (freshBinding || difficulty != appliedDifficulty) {
            CreatureDifficultyScaling.apply(this, freshBinding);
            appliedDifficulty = difficulty;
        }
    }

    /// 登记必须幂等；解析、受击和 tick 都会检查本体是否仍接纳该部件。
    protected boolean attachToOwner(T owner) {
        return true;
    }

    protected void detachFromOwner(T owner, RemovalReason reason) {
    }

    protected void updateOwnerAttributes(T owner) {
    }

    protected void onPartHurt(T owner, DamageSource source) {
    }

    protected void onPartLoaded() {
    }

    @Override
    protected final void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_ID, -1);
        entityData.define(OWNER_UUID, Optional.empty());
        definePartSynchedData();
    }

    protected void definePartSynchedData() {
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        releaseOwner(RemovalReason.UNLOADED_TO_CHUNK);
        ownerUuid = null;
        entityData.set(OWNER_ID, -1);
        entityData.set(OWNER_UUID, Optional.empty());
        super.readAdditionalSaveData(tag);
        readPartSaveData(tag);
        String key = ownerSaveTag();
        ownerUuid = tag.hasUUID(key) ? tag.getUUID(key) : null;
        entityData.set(OWNER_UUID, Optional.ofNullable(ownerUuid));
        unresolvedOwnerTicks = 0;
        deathNotified = false;
        appliedDifficulty = null;
        onPartLoaded();
        noPhysics = true;
        setNoGravity(true);
        setNoAi(true);
    }

    protected void readPartSaveData(CompoundTag tag) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID uuid = owner == null ? ownerUuid : owner.getUUID();
        if (uuid != null) {
            tag.putUUID(ownerSaveTag(), uuid);
        }
        addPartSaveData(tag);
    }

    protected void addPartSaveData(CompoundTag tag) {
    }

    private void resolveOwner() {
        if (isRemoved() || releasingOwner) {
            return;
        }
        if (owner != null) {
            if (!owner.isRemoved() && isOwnedBy(owner)) {
                return;
            }
            RemovalReason reason = owner.getRemovalReason();
            releaseOwner(reason == null ? RemovalReason.UNLOADED_TO_CHUNK : reason);
            if (reason != null && reason.shouldDestroy() && !level().isClientSide) {
                discard();
                return;
            }
        }
        UUID uuid = level().isClientSide ? entityData.get(OWNER_UUID).orElse(null) : ownerUuid;
        if (uuid == null) {
            return;
        }
        Entity candidate = level() instanceof ServerLevel server ? server.getEntity(uuid)
                : level().getEntity(entityData.get(OWNER_ID));
        if (!getOwnerType().isInstance(candidate) || !uuid.equals(candidate.getUUID())) {
            return;
        }
        T resolved = getOwnerType().cast(candidate);
        owner = resolved;
        if (!attachToOwner(resolved)) {
            discard();
            return;
        }
        if (!level().isClientSide) {
            entityData.set(OWNER_ID, resolved.getId());
        }
        unresolvedOwnerTicks = 0;
    }

    private void releaseOwner(RemovalReason reason) {
        T previous = owner;
        owner = null;
        if (previous != null) {
            // 解绑回调可能查询归属；在回调结束前禁止重新登记正在释放的部件。
            releasingOwner = true;
            try {
                detachFromOwner(previous, reason);
            } finally {
                releasingOwner = false;
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        releaseOwner(reason);
        super.remove(reason);
    }

    @Override
    public final boolean shouldBeSaved() {
        return persistPart() && super.shouldBeSaved();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        T resolvedOwner = getOwner();
        return getType().getDimensions().scale(resolvedOwner == null ? 1.0F : resolvedOwner.getScale());
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || getOwner() == entity;
    }
}
