package org.confluence.mod.common.entity.boss;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.PartHitTarget;
import org.jetbrains.annotations.Nullable;


/// 非生物型、短暂 Boss 部件的共享生命周期。
///
/// Boss 本体是持久化权威，部件不独立进入区块存档。客户端通过同步的运行时实体 ID
/// 追踪本场 Boss，服务端额外保留 UUID 处理加载顺序不同的恢复窗口。Boss 加载后会按槽位重建部件，
/// 因此不会出现“Boss 已恢复，旧部件又从区块 NBT 复活”的双份实体。
///
/// 可破坏部件拥有自己的同步生命值，但攻击仍可按倍率转发给 Boss。所有者在 100 tick
/// 宽限内仍无法解析时丢弃孤儿部件，防止损坏存档或非正常生成遗留永久实体。
public abstract class BaseBossPart<T extends BaseBoss> extends Entity implements Boss.BossPart, PartHitTarget {
    // 客户端可能先收到部件再收到本体，允许 100 tick 的实体解析窗口后才清理孤儿部件。
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    private static final String OWNER_TAG = "Owner";
    private static final String HEALTH_TAG = "PartHealth";

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BaseBossPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> PART_HEALTH = SynchedEntityData.defineId(BaseBossPart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HURT_FLASH_TICKS = SynchedEntityData.defineId(BaseBossPart.class, EntityDataSerializers.INT);

    private @Nullable T owner;
    private @Nullable UUID ownerUUID;
    private int unresolvedOwnerTicks;
    private @Nullable Vec3 contactSweepStart;
    private long contactMovementTick = Long.MIN_VALUE;
    private int clientLerpSteps;
    private double clientLerpX;
    private double clientLerpY;
    private double clientLerpZ;
    private float clientLerpYaw;
    private float clientLerpPitch;

    protected BaseBossPart(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    protected final void bindTo(T owner) {
        this.owner = owner;
        this.ownerUUID = owner.getUUID();
        this.entityData.set(OWNER_ID, owner.getId());
        contactSweepStart = position();
        contactMovementTick = level().getGameTime();
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

    public final void indicateHurt() {
        markHurt();
        entityData.set(HURT_FLASH_TICKS, 10);
    }

    public final boolean isHurtFlashing() {
        return entityData.get(HURT_FLASH_TICKS) > 0;
    }

    protected float getMaxPartHealth() {
        return 0.0F;
    }

    protected final boolean isDestructible() {
        return getMaxPartHealth() > 0.0F;
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
        T resolvedOwner = getOwner();
        return isDestructible() || resolvedOwner == null ? this : resolvedOwner;
    }

    @Override
    public final boolean acceptsDirectHit() {
        return isDestructible() || isPickable();
    }

    protected abstract Class<T> getOwnerType();

    protected abstract void tickPart(T owner);

    protected void onPartDestroyed(T owner) {}

    protected void onPartHealthChanged(T owner, float remainingHealth) {}

    @Override
    public void setPos(double x, double y, double z) {
        long gameTime = level().getGameTime();
        if (contactMovementTick != gameTime) {
            contactSweepStart = position();
            contactMovementTick = gameTime;
        }
        super.setPos(x, y, z);
    }

    public final Vec3 getContactSweepStart() {
        return contactMovementTick == level().getGameTime() && contactSweepStart != null ? contactSweepStart : position();
    }

    /// 非生物实体默认收到位置包就跳到终点；部件按网络给出的窗口平滑移动。
    ///
    /// 1.20.1 的 `Entity#lerpTo` 带一个 **Forge 补丁**加的 `boolean teleport`
    /// （`forge-1.20.1-47.4.20` 的 `Entity.java:2115`），1.21.1 的 NeoForge 没有这个形参
    /// （`neoforge-21.1.219` 的 `Entity.java:2202` 是六参）。因此去掉该分支：
    /// 1.21.1 只在 `lerpSteps > 0` 时调用本方法，「是否瞬移」由调用方承担。
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        if (!level().isClientSide || steps <= 0) {
            clientLerpSteps = 0;
            super.lerpTo(x, y, z, yaw, pitch, steps);
            return;
        }
        clientLerpX = x;
        clientLerpY = y;
        clientLerpZ = z;
        clientLerpYaw = yaw;
        clientLerpPitch = pitch;
        clientLerpSteps = steps;
    }

    @Override
    public final void tick() {
        super.tick();
        /// 即使客户端尚未追踪到本体，也必须继续消费部件自身的位置同步。
        if (level().isClientSide && clientLerpSteps > 0) {
            double progress = 1.0D / clientLerpSteps;
            setPos(Mth.lerp(progress, getX(), clientLerpX),
                    Mth.lerp(progress, getY(), clientLerpY),
                    Mth.lerp(progress, getZ(), clientLerpZ));
            setYRot(getYRot() + Mth.wrapDegrees(clientLerpYaw - getYRot()) / clientLerpSteps);
            setXRot(getXRot() + (clientLerpPitch - getXRot()) / clientLerpSteps);
            --clientLerpSteps;
        }
        if (!level().isClientSide && entityData.get(HURT_FLASH_TICKS) > 0) {
            entityData.set(HURT_FLASH_TICKS, entityData.get(HURT_FLASH_TICKS) - 1);
        }
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
        if (EnemyDamageRules.blocks(this, source)) return false;
        if (level().isClientSide || amount <= 0.0F) return false;
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
        if (!ownerHurt) resolvedOwner.onEncounterHurt(source);
        indicateHurt();
        onPartHealthChanged(resolvedOwner, remaining);
        if (remaining <= 0.0F) {
            onPartDestroyed(resolvedOwner);
            discard();
        }
        return true;
    }

    /// 1.21.1 的 `Entity#defineSynchedData` 改为接收 `SynchedEntityData.Builder`
    /// （`Entity.java:342` 是 `protected abstract void defineSynchedData(SynchedEntityData.Builder)`），
    /// 原来的 `entityData.define(...)` 全部改成 `builder.define(...)`。
    @Override
    protected final void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, -1);
        builder.define(PART_HEALTH, 0.0F);
        builder.define(HURT_FLASH_TICKS, 0);
        definePartSynchedData(builder);
    }

    protected void definePartSynchedData(SynchedEntityData.Builder builder) {}

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
        if (owner != null && !owner.isRemoved() && owner.level() == level()) {
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
    public boolean isAttackable() {
        T resolvedOwner = getOwner();
        return !isRemoved() && resolvedOwner != null && resolvedOwner.isAlive();
    }

    @Override
    public boolean canBeHitByProjectile() {
        return isAttackable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {}

    /// 1.21.1 的 `Entity#getAddEntityPacket` 多一个 `ServerEntity` 形参
    /// （`Entity.java:3428`），构造包时一并传给它。
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        T resolvedOwner = getOwner();
        float scale = resolvedOwner == null ? 1.0F : resolvedOwner.getScale();
        return getType().getDimensions().scale(scale);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || getOwner() == entity;
    }
}
