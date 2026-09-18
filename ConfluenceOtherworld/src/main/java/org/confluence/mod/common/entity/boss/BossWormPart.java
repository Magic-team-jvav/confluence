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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.monster.WormSegment;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

/// 由 {@link BaseWormBoss} 头部管理的临时身体或尾部实体。
///
/// 体节把伤害转交给具有权威状态的头部；区块重新加载后也由头部重建整条体节链。
/// 因此体节只保存恢复归属所需的最小信息，不能独立决定 Boss 生命周期。
public class BossWormPart extends Monster implements WormSegment, GeoEntity, PartHitTarget {
    // 一次接触命中后的冷却（tick）。
    private static final int COLLISION_COOLDOWN = 10;
    // 允许本体后到达的网络/区块加载宽限，单位为 tick。
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 100;
    // 世界吞噬者体节吐腐化物的基础周期、随机附加延迟（tick）和基础伤害。
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
    private static final EntityDataAccessor<Integer> HURT_FLASH_TICKS = SynchedEntityData.defineId(BossWormPart.class, EntityDataSerializers.INT);
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
    private @Nullable Vec3 contactSweepStart;
    private int clientLerpSteps;
    private double clientLerpX;
    private double clientLerpY;
    private double clientLerpZ;
    private float clientLerpYaw;
    private float clientLerpPitch;

    public BossWormPart(EntityType<? extends BossWormPart> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
        this.setNoGravity(true);
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
        this.setPos(owner.position());
    }

    public @Nullable BaseWormBoss getOwner() {
        resolveOwner();
        if (owner != null && level().isClientSide) owner.trackClientSegment(this);
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

    public void indicateHurt() {
        markHurt();
        entityData.set(HURT_FLASH_TICKS, 10);
    }

    public boolean isHurtFlashing() {
        BaseWormBoss head = getOwner();
        // 共享生命的链条跟随本体闪红，独立体节仍保留自身的受伤反馈。
        return head != null && head.sharesHurtAnimation() && head.hurtTime > 0 || entityData.get(HURT_FLASH_TICKS) > 0;
    }

    @Override
    public Entity damageRecipient() {
        return this;
    }

    @Override
    public Entity encounterOwner() {
        BaseWormBoss head = getOwner();
        return head == null ? this : head;
    }

    @Override
    public Entity dedupeIdentity() {
        BaseWormBoss head = getOwner();
        return getPartHealth() > 0.0F || head == null ? this : head;
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
        BaseWormBoss head = getOwner();
        return head == null ? null : head.getSegment(getSegmentIndex() - 1);
    }

    @Override
    public @Nullable WormSegment getNext() {
        BaseWormBoss head = getOwner();
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

    /// 体节只沿头部给出的链条位置移动，不再叠加生物惯性或受击位移。
    @Override
    public void travel(Vec3 travelVector) {}

    @Override
    public void tick() {
        previousSegmentRoll = getSegmentRoll();
        super.tick();
        if (level().isClientSide) {
            tickClientInterpolation();
        }
        if (!level().isClientSide && entityData.get(HURT_FLASH_TICKS) > 0) {
            entityData.set(HURT_FLASH_TICKS, entityData.get(HURT_FLASH_TICKS) - 1);
        }
        BaseWormBoss head = getOwner();
        if (head == null) {
            if (!level().isClientSide && ++unresolvedOwnerTicks > OWNER_RESOLUTION_GRACE_TICKS)
                discard();
            return;
        }
        if (!head.isAlive()) {
            // 已确认本体死亡，立即清理体节，不保留静止的链条。
            discard();
            return;
        }
        unresolvedOwnerTicks = 0;
        boolean shouldUseDestroyerDimensions = head instanceof TheDestroyer;
        if (destroyerDimensions != shouldUseDestroyerDimensions) {
            destroyerDimensions = shouldUseDestroyerDimensions;
            refreshDimensions();
        }

        if (!level().isClientSide) {
            tickCollisionAttack(head);
            tickEaterRangedAttack(head);
            if (head instanceof TheDestroyer destroyer) {
                updateDestroyerPresentation(destroyer);
            }
        }
    }

    /// 同步毁灭者体节侧翼状态。体节是可随区块重建的临时实体，因此它只保存渲染所需
    /// 的镜像；阶段和探测器释放记录始终以头部为准。
    void updateDestroyerPresentation(TheDestroyer destroyer) {
        boolean insideSolid = level().getBlockState(blockPosition()).isSolid();
        boolean open = destroyer.getPhase() == TheDestroyer.Phase.SKY
                || destroyer.getPhase() == TheDestroyer.Phase.GROUND
                && !insideSolid;
        entityData.set(DESTROYER_FLAPS_OPEN, open);
    }

    public boolean isDestroyerProbeSegment() {
        return getSegmentIndex() > 0
                && (getSegmentIndex() - 1) % 2 == 0;
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

    private void tickCollisionAttack(BaseWormBoss head) {
        if (head.getTarget() == null) return;
        Vec3 sweepStart = contactSweepStart;
        contactSweepStart = null;
        if (hurtCooldown > 0) {
            hurtCooldown--;
            return;
        }
        List<Entity> contacts = sweepStart == null
                ? SweptContactAttack.findTargets(this, 0.5D, SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && candidate != head && head.canAttack(living))
                : SweptContactAttack.findTargets(this, sweepStart, 0.5D, SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && candidate != head && head.canAttack(living));
        for (Entity entity : contacts) {
            if (entity instanceof LivingEntity target) {
                float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
                target.hurt(damageSources().mobAttack(head), damage);
                hurtCooldown = COLLISION_COOLDOWN;
                return;
            }
        }
    }

    /// 世界吞噬怪的每个体节都拥有独立射击冷却。冷却仅在体节露天且主体持有
    /// 有效目标时推进；弹幕归属主体，以便统一使用
    /// Boss 的阵营过滤和伤害来源，但出生点仍取当前体节位置。
    private void tickEaterRangedAttack(BaseWormBoss head) {
        if (!(head instanceof EaterOfWorlds eater) || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LivingEntity target = eater.getTarget();
        if (target == null || !target.isAlive() || !level().canSeeSky(blockPosition())) {
            return;
        }
        if (--eaterSpitCooldown > 0) {
            return;
        }
        eaterSpitCooldown = EATER_SPIT_INTERVAL
                + random.nextInt(EATER_SPIT_RANDOM_DELAY);

        HostileParticleProjectile projectile = ModEntities.VILE_SPIT_PROJECTILE.get().create(level());
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
        if (org.confluence.mod.common.entity.EnemyDamageRules.blocks(this, source)) return false;
        BaseWormBoss head = getOwner();
        return head != null && head.isAlive() && head.hurtSegment(this, source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return WormSegment.isWormDamage(source) || source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPickable() {return !isRemoved();}

    @Override
    public boolean isAttackable() {
        BaseWormBoss head = getOwner();
        return head != null && head.isAlive() && !isRemoved();
    }

    @Override
    public float getPickRadius() {
        return 1.0F;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {return false;}

    @Override
    public void push(Entity entity) {}

    /// 使用当前节与相邻连接点的平均位置采样光照，减弱长链跨越明暗边界时的逐节跳变。
    @Override
    public Vec3 getLightProbePosition(float partialTick) {
        Vec3 sum = getPosition(partialTick);
        int samples = 1;
        WormSegment previous = getPrev();
        if (previous instanceof Entity entity) {
            sum = sum.add(entity.getPosition(partialTick));
            samples++;
        }
        WormSegment next = getNext();
        if (next instanceof Entity entity) {
            sum = sum.add(entity.getPosition(partialTick));
            samples++;
        }
        return sum.scale(1.0D / samples).add(0.0D, getBbHeight() * 0.5D, 0.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_ID, -1);
        entityData.define(INDEX, 0);
        entityData.define(TAIL, false);
        entityData.define(SEGMENT_HEALTH, 0.0F);
        entityData.define(HURT_FLASH_TICKS, 0);
        entityData.define(DESTROYER_FLAPS_OPEN, false);
        entityData.define(DESTROYER_SEGMENT_ROLL, 0.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerUUID = tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
        entityData.set(INDEX, tag.getInt(INDEX_TAG));
        entityData.set(TAIL, tag.getBoolean(TAIL_TAG));
        entityData.set(SEGMENT_HEALTH, tag.getFloat(HEALTH_TAG));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
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
        // 每 tick 推进渲染插值起点；俯仰和偏航都按最短角路径跨越正负一百八十度。
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
        EntityDimensions base = destroyerDimensions
                ? EntityDimensions.fixed(3.0F, 3.0F)
                : getType().getDimensions();
        BaseWormBoss head = getOwner();
        return base.scale(head == null ? 1.0F : head.getScale());
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
