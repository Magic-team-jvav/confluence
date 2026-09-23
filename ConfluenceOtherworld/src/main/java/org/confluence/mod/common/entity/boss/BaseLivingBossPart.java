package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.mod.common.entity.PartHitTarget;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 拥有独立生命和属性的 Boss 附属生物。
///
/// 生命、护甲和攻击等数值由实体属性注册事件提供；Boss 本体只负责归属、槽位与遭遇状态。
public abstract class BaseLivingBossPart<T extends BaseBoss> extends Monster implements Boss.BossPart, PartHitTarget {
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    private static final String OWNER_TAG = "Owner";
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BaseLivingBossPart.class, EntityDataSerializers.INT);

    private @Nullable T owner;
    private @Nullable UUID ownerUUID;
    private int unresolvedOwnerTicks;
    private double registeredMaxHealth = -1.0D;
    private double registeredAttackDamage = -1.0D;

    protected BaseLivingBossPart(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        noPhysics = true;
        noCulling = true;
        setNoGravity(true);
    }

    protected final void bindTo(T owner) {
        this.owner = owner;
        ownerUUID = owner.getUUID();
        entityData.set(OWNER_ID, owner.getId());
        owner.addSubEntity(this);
        synchronizeOwnerScaling(owner);
    }

    public final @Nullable T getOwner() {
        resolveOwner();
        return owner;
    }

    public final float getPartHealth() {
        return getHealth();
    }

    public final void setPartHealth(float health) {
        if (health > getMaxHealth()) {
            var maximumHealth = getAttribute(Attributes.MAX_HEALTH);
            if (maximumHealth != null) maximumHealth.setBaseValue(health);
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
        return true;
    }

    protected abstract Class<T> getOwnerType();

    protected abstract void tickPart(T owner);

    protected void onPartDestroyed(T owner) {}

    protected void onPartHealthChanged(T owner, float remainingHealth) {}

    @Override
    public boolean hurt(DamageSource source, float amount) {
        T resolvedOwner = getOwner();
        if (resolvedOwner == null || !resolvedOwner.isAlive() || isRemoved()) return false;
        boolean hurt = super.hurt(source, amount);
        if (hurt) resolvedOwner.onEncounterHurt(source);
        return hurt;
    }

    /// 活体部件由 Boss 定位，命中时不允许独立偏离本体。
    @Override
    public void knockback(double strength, double x, double z) {}

    /// 部件位置由 tickPart 更新，不能先让普通生物移动再叠加一次专用位移。
    @Override
    public void travel(Vec3 travelVector) {}

    @Override
    protected float tickHeadTurn(float bodyYaw, float animationSpeed) {
        yBodyRot = getYRot();
        yHeadRot = getYRot();
        return animationSpeed;
    }

    @Override
    public final void tick() {
        super.tick();
        if (!isAlive()) return;
        T resolvedOwner = getOwner();
        if (resolvedOwner == null) {
            if (!level().isClientSide && ++unresolvedOwnerTicks > OWNER_RESOLUTION_GRACE_TICKS)
                discard();
            return;
        }
        unresolvedOwnerTicks = 0;
        if (!resolvedOwner.isAlive()) {
            discard();
            return;
        }
        synchronizeOwnerScaling(resolvedOwner);
        tickPart(resolvedOwner);
    }

    private void synchronizeOwnerScaling(T owner) {
        var partMaximumHealth = getAttribute(Attributes.MAX_HEALTH);
        var ownerMaximumHealth = owner.getAttribute(Attributes.MAX_HEALTH);
        if (partMaximumHealth != null && ownerMaximumHealth != null && ownerMaximumHealth.getBaseValue() > 0.0D) {
            if (registeredMaxHealth < 0.0D) registeredMaxHealth = partMaximumHealth.getBaseValue();
            float healthRatio = getMaxHealth() <= 0.0F ? 1.0F : getHealth() / getMaxHealth();
            double scaledMaximum = registeredMaxHealth * ownerMaximumHealth.getValue() / ownerMaximumHealth.getBaseValue();
            if (scaledMaximum > 0.0D && Math.abs(partMaximumHealth.getBaseValue() - scaledMaximum) > 1.0E-4D) {
                partMaximumHealth.setBaseValue(scaledMaximum);
                setHealth((float) (getMaxHealth() * healthRatio));
            }
        }

        var partAttackDamage = getAttribute(Attributes.ATTACK_DAMAGE);
        var ownerAttackDamage = owner.getAttribute(Attributes.ATTACK_DAMAGE);
        if (partAttackDamage != null && ownerAttackDamage != null && ownerAttackDamage.getBaseValue() > 0.0D) {
            if (registeredAttackDamage < 0.0D)
                registeredAttackDamage = partAttackDamage.getBaseValue();
            double scaledDamage = registeredAttackDamage * ownerAttackDamage.getValue() / ownerAttackDamage.getBaseValue();
            if (scaledDamage >= 0.0D) partAttackDamage.setBaseValue(scaledDamage);
        }
    }

    @Override
    protected final void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_ID, -1);
        definePartSynchedData();
    }

    protected void definePartSynchedData() {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerUUID = tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
        readPartSaveData(tag);
    }

    protected void readPartSaveData(CompoundTag tag) {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID uuid = owner == null ? ownerUUID : owner.getUUID();
        if (uuid != null) tag.putUUID(OWNER_TAG, uuid);
        addPartSaveData(tag);
    }

    protected void addPartSaveData(CompoundTag tag) {}

    private void resolveOwner() {
        if (owner != null && !owner.isRemoved() && owner.level() == level()) return;
        owner = null;
        Entity byNetworkId = level().getEntity(entityData.get(OWNER_ID));
        if (getOwnerType().isInstance(byNetworkId)) {
            owner = getOwnerType().cast(byNetworkId);
            ownerUUID = owner.getUUID();
            owner.addSubEntity(this);
            return;
        }
        if (!level().isClientSide && ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity byUuid = serverLevel.getEntity(ownerUUID);
            if (getOwnerType().isInstance(byUuid)) {
                owner = getOwnerType().cast(byUuid);
                entityData.set(OWNER_ID, owner.getId());
                owner.addSubEntity(this);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (owner != null) owner.removeSubEntity(this);
        super.remove(reason);
    }

    @Override
    public final boolean shouldBeSaved() {
        return false;
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
    public void push(Entity entity) {}

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
