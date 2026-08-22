package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.confluence.lib.api.entity.Boss;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 非生物型、短暂 Boss 部件的共享生命周期。
///
/// <p>Boss 本体是持久化权威，部件不独立进入区块存档。客户端通过同步的运行时实体 ID
/// 追踪本场 Boss，服务端额外保留 UUID 处理加载顺序不同的恢复窗口。Boss 加载后会按槽位重建部件，
/// 因此不会出现“Boss 已恢复，旧部件又从区块 NBT 复活”的双份实体。</p>
///
/// <p>可破坏部件拥有自己的同步生命值，但攻击仍可按倍率转发给 Boss。所有者在 100 tick
/// 宽限内仍无法解析时丢弃孤儿部件，防止损坏存档或非正常生成遗留永久实体。</p>
public abstract class BaseBossPart<T extends BaseBoss> extends Entity implements Boss.BossPart {
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    private static final String OWNER_TAG = "Owner";
    private static final String HEALTH_TAG = "PartHealth";

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BaseBossPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> PART_HEALTH = SynchedEntityData.defineId(BaseBossPart.class, EntityDataSerializers.FLOAT);

    private @Nullable T owner;
    private @Nullable UUID ownerUUID;
    private int unresolvedOwnerTicks;

    protected BaseBossPart(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    protected final void bindTo(T owner) {
        this.owner = owner;
        this.ownerUUID = owner.getUUID();
        this.entityData.set(OWNER_ID, owner.getId());
        // 只在首次绑定/新建部件时补满生命，不覆盖从存档恢复的剩余生命。
        if (isDestructible() && getPartHealth() <= 0.0F) {
            this.entityData.set(PART_HEALTH, getMaxPartHealth());
        }
        owner.addSubEntity(this);
    }

    public final @Nullable T getOwner() {
        resolveOwner();
        return owner;
    }

    public final float getPartHealth() {
        return entityData.get(PART_HEALTH);
    }

    public final void setPartHealth(float health) {
        if (!isDestructible()) return;
        entityData.set(PART_HEALTH, Math.max(0.0F, Math.min(health, getMaxPartHealth())));
    }

    protected float getMaxPartHealth() {
        return 0.0F;
    }

    protected final boolean isDestructible() {
        return getMaxPartHealth() > 0.0F;
    }

    protected abstract Class<T> getOwnerType();

    protected abstract void tickPart(T owner);

    protected void onPartDestroyed(T owner) {}

    protected void onPartHealthChanged(T owner, float remainingHealth) {}

    @Override
    public final void tick() {
        super.tick();
        T resolvedOwner = getOwner();
        if (resolvedOwner == null) {
            // 宽限用于容纳 Boss/部件的加载顺序差，而不是让孤儿部件无限期存活。
            if (!level().isClientSide && ++unresolvedOwnerTicks > OWNER_RESOLUTION_GRACE_TICKS) {
                discard();
            }
            return;
        }
        unresolvedOwnerTicks = 0;
        if (!resolvedOwner.isAlive()) {
            discard();
            return;
        }
        tickPart(resolvedOwner);
    }

    protected final boolean hurtOwnerAndPart(DamageSource source, float amount, float ownerMultiplier) {
        T resolvedOwner = getOwner();
        if (resolvedOwner == null || !resolvedOwner.isAlive() || isRemoved()) {
            return false;
        }

        boolean ownerHurt = resolvedOwner.hurt(source, amount * ownerMultiplier);
        if (!isDestructible() || isInvulnerableTo(source)) {
            return ownerHurt;
        }

        float remaining = Math.max(0.0F, getPartHealth() - amount);
        entityData.set(PART_HEALTH, remaining);
        onPartHealthChanged(resolvedOwner, remaining);
        if (remaining <= 0.0F) {
            onPartDestroyed(resolvedOwner);
            discard();
        }
        return true;
    }

    @Override
    protected final void defineSynchedData() {
        entityData.define(OWNER_ID, -1);
        entityData.define(PART_HEALTH, 0.0F);
        definePartSynchedData();
    }

    protected void definePartSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        ownerUUID = tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
        entityData.set(PART_HEALTH, tag.getFloat(HEALTH_TAG));
        readPartSaveData(tag);
    }

    protected void readPartSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        UUID uuid = owner == null ? ownerUUID : owner.getUUID();
        if (uuid != null) {
            tag.putUUID(OWNER_TAG, uuid);
        }
        tag.putFloat(HEALTH_TAG, getPartHealth());
        addPartSaveData(tag);
    }

    protected void addPartSaveData(CompoundTag tag) {}

    private void resolveOwner() {
        if (owner != null && !owner.isRemoved()) {
            return;
        }
        owner = null;

        // 先走客户端也能使用的运行时 ID 快速路径，服务端失败后再以 UUID 精确恢复。
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
        T resolvedOwner = owner;
        if (resolvedOwner != null) {
            resolvedOwner.removeSubEntity(this);
        }
        super.remove(reason);
    }

    @Override
    public final boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean canBeCollidedWith() {
        T resolvedOwner = getOwner();
        return resolvedOwner != null && resolvedOwner.isAlive();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions();
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || getOwner() == entity;
    }
}
