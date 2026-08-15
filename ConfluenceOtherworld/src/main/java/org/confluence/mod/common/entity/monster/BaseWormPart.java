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
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/**
 * 由 {@link BaseWormMonster} 头部管理的临时身体或尾部实体。
 *
 * <p>头部是持久化权威来源，区块重新加载后由它重建完整体节链。
 * 体节仅保存重新解析归属所需的信息，避免多份实体同时维护相互冲突的链状态。</p>
 */
public class BaseWormPart extends Entity implements WormSegment, GeoEntity {
    private static final float SEGMENT_SPACING = 1.6F;
    private static final float COLLISION_DAMAGE = 5.0F;
    private static final int COLLISION_COOLDOWN = 10;
    private static final int DEAD_OWNER_REMOVAL_TICKS = 20;
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    private static final String OWNER_TAG = "Owner";
    private static final String INDEX_TAG = "SegmentIndex";
    private static final String TAIL_TAG = "Tail";

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BaseWormPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(BaseWormPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(BaseWormPart.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private @Nullable BaseWormMonster owner;
    private @Nullable UUID ownerUUID;
    private int unresolvedOwnerTicks;
    private int hurtCooldown;

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
        this.setPos(owner.position());
    }

    public @Nullable BaseWormMonster getOwner() {
        resolveOwner();
        return owner;
    }

    public boolean isTail() {
        return entityData.get(TAIL);
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

    @Override
    public void updateSegmentPosition() {
        WormSegment previous = getPrev();
        if (!(previous instanceof Entity leader)) return;

        Vec3 previousPosition = position();
        Vec3 difference = previousPosition.subtract(leader.position());
        if (difference.lengthSqr() < 0.001) difference = new Vec3(0, 1, 0);
        Vec3 destination = leader.position().add(difference.normalize().scale(SEGMENT_SPACING));

        double dx = destination.x - previousPosition.x;
        double dy = destination.y - previousPosition.y;
        double dz = destination.z - previousPosition.z;
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90F;
        float horizontalDistance = Mth.sqrt((float) (dx * dx + dz * dz));
        float pitch = (float) (-Mth.atan2(dy, horizontalDistance) * Mth.RAD_TO_DEG);

        setPos(destination.x, destination.y, destination.z);
        setRot(yaw, pitch);
        yRotO = yaw;
        xRotO = pitch;
    }

    @Override
    public void tick() {
        super.tick();
        BaseWormMonster head = getOwner();
        if (head != null && !head.isAlive()) {
            /*
             * 死亡头部已经完成过所有权解析，不需要等待网络实体的加入顺序。
             * 保留二十刻死亡阶段后清理体节，既避免尸体长期残留，也不会截断受伤死亡表现。
             */
            if (++unresolvedOwnerTicks > DEAD_OWNER_REMOVAL_TICKS) discard();
            return;
        }
        if (head == null) {
            /*
             * 客户端可能先收到体节、后收到头部，因此真正未解析到所有者时仍保留较长宽限期。
             * 该分支不能与“已确认头部死亡”共用时长，否则无 AI 蠕虫会留下可选中的体节尸体。
             */
            if (++unresolvedOwnerTicks > OWNER_RESOLUTION_GRACE_TICKS) discard();
            return;
        }
        unresolvedOwnerTicks = 0;

        if (!level().isClientSide) {
            updateSegmentPosition();
            tickCollisionAttack(head);
        }
    }

    private void tickCollisionAttack(BaseWormMonster head) {
        if (hurtCooldown > 0) {
            hurtCooldown--;
            return;
        }
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.5))) {
            if (target != head && head.canAttack(target)) {
                target.hurt(damageSources().mobAttack(head), COLLISION_DAMAGE);
                hurtCooldown = COLLISION_COOLDOWN;
                return;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        BaseWormMonster head = getOwner();
        return head != null && head.isAlive() && head.hurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPickable() {return !isRemoved();}

    @Override
    public boolean canBeCollidedWith() {
        BaseWormMonster head = getOwner();
        return head != null && head.isAlive();
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER_ID, -1);
        entityData.define(INDEX, 0);
        entityData.define(TAIL, false);
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
            /*
             * 已经解析过的头部即使进入移除状态，也必须保留到 tick 中判断死亡。
             * 若在这里先清空引用，体节会把“头部已死亡”误判成“客户端尚未收到头部”，
             * 从而错误等待完整的网络解析宽限期。
             */
            return;
        }
        owner = null;

        Entity byNetworkId = level().getEntity(entityData.get(OWNER_ID));
        if (byNetworkId instanceof BaseWormMonster head) {
            owner = head;
            ownerUUID = head.getUUID();
            return;
        }
        if (!level().isClientSide && ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity byUuid = serverLevel.getEntity(ownerUUID);
            if (byUuid instanceof BaseWormMonster head) {
                owner = head;
                entityData.set(OWNER_ID, head.getId());
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
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions();
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
