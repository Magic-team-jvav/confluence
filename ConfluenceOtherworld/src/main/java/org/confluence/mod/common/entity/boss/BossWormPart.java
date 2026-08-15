package org.confluence.mod.common.entity.boss;

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
import org.confluence.mod.common.entity.monster.WormSegment;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/**
 * 由 {@link BaseWormBoss} 头部管理的临时身体或尾部实体。
 *
 * <p>体节把伤害转交给具有权威状态的头部；区块重新加载后也由头部重建整条体节链。
 * 因此体节只保存恢复归属所需的最小信息，不能独立决定 Boss 生命周期。</p>
 */
public class BossWormPart extends Entity implements WormSegment, GeoEntity {
    private static final float EATER_COLLISION_DAMAGE = 4.0F;
    private static final float DESTROYER_COLLISION_DAMAGE = 66.0F;
    private static final int COLLISION_COOLDOWN = 10;
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    private static final int EATER_SPIT_INTERVAL = 200;
    private static final int EATER_SPIT_RANDOM_DELAY = 50;
    private static final float EATER_SPIT_DAMAGE = 5.0F;
    private static final String OWNER_TAG = "Owner";
    private static final String INDEX_TAG = "SegmentIndex";
    private static final String TAIL_TAG = "Tail";
    private static final String HEALTH_TAG = "SegmentHealth";

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SEGMENT_HEALTH = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DESTROYER_PROBE_RELEASED = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESTROYER_FLAPS_OPEN = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DESTROYER_SEGMENT_ROLL = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private @Nullable BaseWormBoss owner;
    private @Nullable UUID ownerUUID;
    private int unresolvedOwnerTicks;
    private int hurtCooldown;
    private int eaterSpitCooldown = EATER_SPIT_INTERVAL;
    private boolean destroyerDimensions;
    private float previousSegmentRoll;

    public BossWormPart(EntityType<? extends BossWormPart> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public void bindTo(BaseWormBoss owner, int index, boolean tail) {
        if (index < 1) throw new IllegalArgumentException("Worm body indices start at 1");
        this.owner = owner;
        this.ownerUUID = owner.getUUID();
        this.entityData.set(OWNER_ID, owner.getId());
        this.entityData.set(INDEX, index);
        this.entityData.set(TAIL, tail);
        this.entityData.set(SEGMENT_HEALTH, owner.getInitialSegmentHealth(index));
        destroyerDimensions = owner instanceof TheDestroyer;
        refreshDimensions();
        if (owner instanceof TheDestroyer destroyer) {
            this.entityData.set(
                    DESTROYER_PROBE_RELEASED,
                    destroyer.hasReleasedProbe(index));
        }
        this.setPos(owner.position());
    }

    public @Nullable BaseWormBoss getOwner() {
        resolveOwner();
        return owner;
    }

    public boolean isTail() {
        return entityData.get(TAIL);
    }

    public float getPartHealth() {
        return entityData.get(SEGMENT_HEALTH);
    }

    public void setPartHealth(float health) {
        entityData.set(SEGMENT_HEALTH, Math.max(0.0F, health));
    }

    @Override
    public int getSegmentIndex() {
        return entityData.get(INDEX);
    }

    @Override
    public @Nullable WormSegment getPrev() {
        BaseWormBoss head = getOwner();
        return head == null ? null : head.getSegment(getSegmentIndex() - 1);
    }

    @Override
    public @Nullable WormSegment getNext() {
        BaseWormBoss head = getOwner();
        return head == null ? null : head.getSegment(getSegmentIndex() + 1);
    }

    @Override
    public void updateSegmentPosition() {
        BaseWormBoss head = getOwner();
        if (head == null) return;
        WormSegment previous = getPrev();
        if (!(previous instanceof Entity leader)) return;

        Vec3 previousPosition = position();
        Vec3 difference = previousPosition.subtract(leader.position());
        if (difference.lengthSqr() < 0.001) difference = new Vec3(0, 1, 0);
        Vec3 destination = leader.position().add(
                difference.normalize().scale(head.getSegmentSpacing()));

        double dx = destination.x - previousPosition.x;
        double dy = destination.y - previousPosition.y;
        double dz = destination.z - previousPosition.z;
        float yaw = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
        float horizontalDistance = Mth.sqrt((float) (dx * dx + dz * dz));
        float pitch = (float) (-Mth.atan2(dy, horizontalDistance) * (180F / Math.PI));

        setPos(destination.x, destination.y, destination.z);
        setRot(yaw, pitch);
        yRotO = yaw;
        xRotO = pitch;
    }

    @Override
    public void tick() {
        previousSegmentRoll = getSegmentRoll();
        super.tick();
        BaseWormBoss head = getOwner();
        if (head == null || !head.isAlive()) {
            if (++unresolvedOwnerTicks > OWNER_RESOLUTION_GRACE_TICKS) discard();
            return;
        }
        unresolvedOwnerTicks = 0;
        boolean shouldUseDestroyerDimensions = head instanceof TheDestroyer;
        if (destroyerDimensions != shouldUseDestroyerDimensions) {
            destroyerDimensions = shouldUseDestroyerDimensions;
            refreshDimensions();
        }

        if (!level().isClientSide) {
            updateSegmentPosition();
            tickCollisionAttack(head);
            tickEaterRangedAttack(head);
            if (head instanceof TheDestroyer destroyer) {
                updateDestroyerPresentation(destroyer);
            }
        }
    }

    /**
     * 同步毁灭者体节侧翼状态。体节是可随区块重建的临时实体，因此它只保存渲染所需
     * 的镜像；阶段和探测器释放记录始终以头部为准。
     */
    void updateDestroyerPresentation(TheDestroyer destroyer) {
        boolean insideSolid = level().getBlockState(blockPosition()).isSolid();
        boolean open = destroyer.getPhase() == TheDestroyer.Phase.SKY
                || destroyer.getPhase() == TheDestroyer.Phase.GROUND
                && !insideSolid;
        entityData.set(DESTROYER_FLAPS_OPEN, open);
        entityData.set(
                DESTROYER_PROBE_RELEASED,
                destroyer.hasReleasedProbe(getSegmentIndex()));
    }

    public boolean isDestroyerProbeSegment() {
        return getSegmentIndex() > 0
                && (getSegmentIndex() - 1) % 2 == 0;
    }

    public boolean hasReleasedDestroyerProbe() {
        return entityData.get(DESTROYER_PROBE_RELEASED);
    }

    public boolean areDestroyerFlapsOpen() {
        return entityData.get(DESTROYER_FLAPS_OPEN);
    }

    public float getSegmentRoll() {
        return entityData.get(DESTROYER_SEGMENT_ROLL);
    }

    public float getPreviousSegmentRoll() {
        return previousSegmentRoll;
    }

    void setSegmentRoll(float roll) {
        entityData.set(DESTROYER_SEGMENT_ROLL, roll);
    }

    void setReleasedDestroyerProbe(boolean released) {
        entityData.set(DESTROYER_PROBE_RELEASED, released);
    }

    private void tickCollisionAttack(BaseWormBoss head) {
        if (hurtCooldown > 0) {
            hurtCooldown--;
            return;
        }
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.5))) {
            if (target != head && head.canAttack(target)) {
                float damage = head instanceof TheDestroyer
                        ? DESTROYER_COLLISION_DAMAGE
                        : EATER_COLLISION_DAMAGE;
                target.hurt(damageSources().mobAttack(head), damage);
                hurtCooldown = COLLISION_COOLDOWN;
                return;
            }
        }
    }

    /**
     * 世界吞噬怪的每个体节都拥有独立射击冷却。冷却只在体节露天且主体持有
     * 有效目标时推进，保持与 1.21 侧相同的条件；弹幕归属主体，以便统一使用
     * Boss 的阵营过滤和伤害来源，但出生点仍取当前体节位置。
     */
    private void tickEaterRangedAttack(BaseWormBoss head) {
        if (!(head instanceof EaterOfWorlds eater)
                || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LivingEntity target = eater.getTarget();
        if (target == null
                || !target.isAlive()
                || !level().canSeeSky(blockPosition())) {
            return;
        }
        if (--eaterSpitCooldown > 0) {
            return;
        }
        eaterSpitCooldown = EATER_SPIT_INTERVAL
                + random.nextInt(EATER_SPIT_RANDOM_DELAY);

        HostileParticleProjectile projectile =
                ModEntities.VILE_SPIT_PROJECTILE.get().create(level());
        if (projectile == null) {
            return;
        }
        projectile.configure(eater, target, EATER_SPIT_DAMAGE);
        Vec3 origin = getBoundingBox().getCenter();
        projectile.setPos(origin);
        Vec3 aim = target.getEyePosition().subtract(origin);
        projectile.shoot(aim.x, aim.y, aim.z, 0.3F, 3.0F);
        serverLevel.addFreshEntity(projectile);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        BaseWormBoss head = getOwner();
        return head != null && head.isAlive() && head.hurtSegment(this, source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPickable() {return !isRemoved();}

    @Override
    public boolean canBeCollidedWith() {
        BaseWormBoss head = getOwner();
        return head != null && head.isAlive();
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER_ID, -1);
        entityData.define(INDEX, 0);
        entityData.define(TAIL, false);
        entityData.define(SEGMENT_HEALTH, 0.0F);
        entityData.define(DESTROYER_PROBE_RELEASED, false);
        entityData.define(DESTROYER_FLAPS_OPEN, false);
        entityData.define(DESTROYER_SEGMENT_ROLL, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        ownerUUID = tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
        entityData.set(INDEX, tag.getInt(INDEX_TAG));
        entityData.set(TAIL, tag.getBoolean(TAIL_TAG));
        entityData.set(SEGMENT_HEALTH, tag.getFloat(HEALTH_TAG));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        UUID uuid = owner == null ? ownerUUID : owner.getUUID();
        if (uuid != null) tag.putUUID(OWNER_TAG, uuid);
        tag.putInt(INDEX_TAG, getSegmentIndex());
        tag.putBoolean(TAIL_TAG, isTail());
        tag.putFloat(HEALTH_TAG, getPartHealth());
    }

    private void resolveOwner() {
        if (owner != null && !owner.isRemoved()) return;
        owner = null;

        Entity byNetworkId = level().getEntity(entityData.get(OWNER_ID));
        if (byNetworkId instanceof BaseWormBoss head) {
            owner = head;
            ownerUUID = head.getUUID();
            return;
        }
        if (!level().isClientSide && ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity byUuid = serverLevel.getEntity(ownerUUID);
            if (byUuid instanceof BaseWormBoss head) {
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
        return destroyerDimensions
                ? EntityDimensions.fixed(3.0F, 3.0F)
                : getType().getDimensions();
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
