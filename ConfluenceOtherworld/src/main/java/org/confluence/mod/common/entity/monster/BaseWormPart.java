package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

/// 蠕虫体节。每 tick 跟随前一个体节（或头部），保持固定间距。
public class BaseWormPart extends Entity implements WormSegment, GeoEntity, PartHitTarget {
    // 未命中时每 10 tick 重试；命中后给同一体节 20 tick 接触伤害冷却。
    private static final int COLLISION_DETECTION_INTERVAL = 10;
    private static final int COLLISION_ATTACK_INTERVAL = 20;
    // 仅暂时无法解析本体时保留加载宽限，已确认死亡的体节立即清理。
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    private static final String OWNER_TAG = "Owner";
    private static final String INDEX_TAG = "SegmentIndex";
    private static final String TAIL_TAG = "Tail";

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BaseWormPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(BaseWormPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(BaseWormPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HURT_FLASH_TICKS = SynchedEntityData.defineId(BaseWormPart.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private @Nullable BaseWormMonster owner;
    private @Nullable UUID ownerUUID;
    private int unresolvedOwnerTicks;
    private int hurtCooldown;
    private @Nullable Vec3 contactSweepStart;
    private int clientLerpSteps;
    private double clientLerpX;
    private double clientLerpY;
    private double clientLerpZ;
    private float clientLerpYaw;
    private float clientLerpPitch;

    public BaseWormPart(EntityType<? extends BaseWormPart> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public void bindTo(BaseWormMonster owner, int index, boolean tail) {
        if (index < 1) throw new IllegalArgumentException("Worm body indices start at 1");
        this.owner = owner;
        this.ownerUUID = owner.getUUID();
        this.entityData.set(OWNER_ID, owner.getId());
        this.entityData.set(INDEX, index);
        this.entityData.set(TAIL, tail);
        refreshDimensions();
        this.setPos(owner.position());
    }

    public @Nullable BaseWormMonster getOwner() {
        resolveOwner();
        if (owner != null && level().isClientSide) owner.trackClientSegment(this);
        return owner;
    }

    public boolean isTail() {
        return entityData.get(TAIL);
    }

    @Override
    public boolean fireImmune() {
        BaseWormMonster head = getOwner();
        return head != null && head.fireImmune() || super.fireImmune();
    }

    @Override
    public boolean displayFireAnimation() {
        return !fireImmune() && super.displayFireAnimation();
    }

    public boolean isHurtFlashing() {
        return entityData.get(HURT_FLASH_TICKS) > 0;
    }

    public void indicateHurt() {
        // 体节独立同步视觉状态，不依赖客户端是否正在追踪头部及其受伤事件。
        entityData.set(HURT_FLASH_TICKS, 10);
    }

    @Override
    public Entity damageRecipient() {
        return this;
    }

    @Override
    public Entity encounterOwner() {
        BaseWormMonster head = getOwner();
        return head == null ? this : head;
    }

    @Override
    public Entity dedupeIdentity() {
        return encounterOwner();
    }

    @Override
    public boolean acceptsDirectHit() {
        return isPickable();
    }

    @Override
    public int getSegmentIndex() {
        return entityData.get(INDEX);
    }

    @Override
    public @Nullable WormSegment getPrev() {
        BaseWormMonster head = getOwner();
        return head == null ? null : head.getSegment(getSegmentIndex() - 1);
    }

    @Override
    public @Nullable WormSegment getNext() {
        BaseWormMonster head = getOwner();
        return head == null ? null : head.getSegment(getSegmentIndex() + 1);
    }

    public void orientAlongChain(Vec3 tangent) {
        WormSegment.orientAlong(this, tangent);
    }

    public void moveToChainPosition(Vec3 destination) {
        Vec3 previousPosition = position();
        if (!level().isClientSide) contactSweepStart = previousPosition;
        setPos(destination.x, destination.y, destination.z);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && entityData.get(HURT_FLASH_TICKS) > 0)
            entityData.set(HURT_FLASH_TICKS, entityData.get(HURT_FLASH_TICKS) - 1);
        if (level().isClientSide) tickClientInterpolation();
        BaseWormMonster head = getOwner();
        if (head != null && !head.isAlive()) {
            // 已确认死亡，不再等待本体的死亡动画或所有权解析宽限。
            discard();
            return;
        }
        if (head == null) {
            /// 客户端可能先收到体节、后收到头部，因此真正未解析到所有者时仍保留较长宽限期。
            /// 该分支不能与“已确认头部死亡”共用时长，否则无 AI 蠕虫会留下可选中的体节尸体。
            if (!level().isClientSide && ++unresolvedOwnerTicks > OWNER_RESOLUTION_GRACE_TICKS)
                discard();
            return;
        }
        unresolvedOwnerTicks = 0;

        if (!level().isClientSide) tickCollisionAttack(head);
    }

    private void tickCollisionAttack(BaseWormMonster head) {
        Vec3 sweepStart = contactSweepStart;
        contactSweepStart = null;
        if (hurtCooldown > 0) {
            hurtCooldown--;
            return;
        }
        boolean attacked = false;
        List<Entity> contacts = sweepStart == null
                ? SweptContactAttack.findTargets(this, 0.0D, SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity target && target != head && head.canAttack(target))
                : SweptContactAttack.findTargets(this, sweepStart, 0.0D, SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity target && target != head && head.canAttack(target));
        for (Entity contact : contacts) {
            if (contact instanceof LivingEntity target) {
                attacked |= head.attackFromSegment(this, target);
            }
        }
        hurtCooldown = attacked ? COLLISION_ATTACK_INTERVAL : COLLISION_DETECTION_INTERVAL;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (org.confluence.mod.common.entity.EnemyDamageRules.blocks(this, source)) return false;
        BaseWormMonster head = getOwner();
        if (head == null || !head.isAlive() || !head.hurtSegment(this, source, amount))
            return false;
        markHurt();
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPickable() {return !isRemoved();}

    @Override
    public boolean canBeCollidedWith() {return false;}

    @Override
    public boolean isPushable() {return false;}

    @Override
    public void push(Entity entity) {}

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER_ID, -1);
        entityData.define(INDEX, 0);
        entityData.define(TAIL, false);
        entityData.define(HURT_FLASH_TICKS, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        ownerUUID = tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
        entityData.set(INDEX, tag.getInt(INDEX_TAG));
        entityData.set(TAIL, tag.getBoolean(TAIL_TAG));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        UUID uuid = owner == null ? ownerUUID : owner.getUUID();
        if (uuid != null) tag.putUUID(OWNER_TAG, uuid);
        tag.putInt(INDEX_TAG, getSegmentIndex());
        tag.putBoolean(TAIL_TAG, isTail());
    }

    private void resolveOwner() {
        if (owner != null) {
            /// 已经解析过的头部即使进入移除状态，也必须保留到 tick 中判断死亡。
            /// 若在这里先清空引用，体节会把“头部已死亡”误判成“客户端尚未收到头部”，
            /// 从而错误等待完整的网络解析宽限期。
            return;
        }
        owner = null;

        Entity byNetworkId = level().getEntity(entityData.get(OWNER_ID));
        if (byNetworkId instanceof BaseWormMonster head) {
            owner = head;
            ownerUUID = head.getUUID();
            refreshDimensions();
            return;
        }
        if (!level().isClientSide && ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity byUuid = serverLevel.getEntity(ownerUUID);
            if (byUuid instanceof BaseWormMonster head) {
                owner = head;
                entityData.set(OWNER_ID, head.getId());
                refreshDimensions();
            }
        }
    }

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch,
                       int steps, boolean teleport) {
        if (!level().isClientSide || teleport || distanceToSqr(x, y, z) > 4096.0D) {
            setPos(x, y, z);
            setRot(yaw, pitch);
            yRotO = yaw;
            xRotO = pitch;
            clientLerpSteps = 0;
            return;
        }
        clientLerpX = x;
        clientLerpY = y;
        clientLerpZ = z;
        clientLerpYaw = yaw;
        clientLerpPitch = pitch;
        clientLerpSteps = Math.max(1, steps);
    }

    private void tickClientInterpolation() {
        // 渲染帧使用旧角度到当前角度的 partial tick 插值；即使本 tick 没有新网络目标，
        // 也必须推进旧角度，否则会在同一小段旋转上反复播放。
        yRotO = getYRot();
        xRotO = getXRot();
        if (clientLerpSteps <= 0) return;
        double progress = 1.0D / clientLerpSteps;
        setPos(
                Mth.lerp(progress, getX(), clientLerpX),
                Mth.lerp(progress, getY(), clientLerpY),
                Mth.lerp(progress, getZ(), clientLerpZ));
        setRot(
                Mth.rotLerp((float) progress, getYRot(), clientLerpYaw),
                Mth.rotLerp((float) progress, getXRot(), clientLerpPitch));
        clientLerpSteps--;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return owner == null ? getType().getDimensions() : owner.segmentDimensions(isTail());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (TAIL.equals(accessor) && owner != null) refreshDimensions();
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || getOwner() == entity;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
