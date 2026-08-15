package org.confluence.mod.common.entity.projectile;

import PortLib.extensions.java.util.List.PortListExtension;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshotCarrier;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.network.s2c.BulletImpactPacketS2C;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.entity.monster.BaseMimic;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.wrapper.world.entity.projectile.PortProjectileDeflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 枪械子弹实体基类。
 *
 * <p>伤害、击退、暴击、穿透预算和成功命中 UUID 来自 MagicLib 发射事务安装的冻结快照；子弹
 * 物品继续负责轨迹颜色、每 tick 扩展和命中扩展。额外的外观与运动字段使用独立版本根节点，
 * 因而区块卸载不会把加速度、方块命中次数或行为物品重置成构造默认值。</p>
 *
 * <p>任何战斗或运行状态损坏都会清空可伤害快照，并在下一次服务端 tick 入口销毁实体。</p>
 */
public class BaseBulletEntity extends Projectile implements ProjectileCombatSnapshotCarrier {
    private static final int MAX_LIFETIME = 200;
    private static final double MAX_OWNER_DISTANCE = 256.0D;
    private static final double MAX_RENDER_DISTANCE = 256.0D;
    private static final int MAX_ENTITY_COLLISIONS_PER_TICK = 32;
    private static final int MAX_TRAIL_POINTS = 64;
    private static final int CHLOROPHYTE_TRAIL_POINTS = 256;
    private static final double COLLISION_EPSILON = 0.08D;
    private static final double ENTITY_SWEEP_MARGIN = 0.10D;
    private static final double TRAIL_POINT_SPACING = 0.25D;
    private static final double TRAIL_POINT_EPSILON = 1.0E-6D;
    private static final EntityDataAccessor<String> COLOR_ID = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<ItemStack> BULLET = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> HOMING_TARGET_ID = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EFFECT_STATE = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IGNORE_BLOCK_COLLISION = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> INITIAL_VELOCITY = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> HAS_INITIAL_VELOCITY = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.BOOLEAN);
    public float damage;
    public float knockback;
    public int hitBlockTimes;
    public int penetrate;
    private final List<Vec3> trails = new ArrayList<>();
    private final Set<UUID> sweptEntityIds = new HashSet<>();
    private final Set<UUID> contactedEntityIds = new HashSet<>();
    private final ProjectileCombatState combatState = new ProjectileCombatState();
    public double accelerationPower;
    private int ownerResolutionTicks;
    private boolean appliedInitialVelocity;

    public BaseBulletEntity(EntityType<? extends BaseBulletEntity> entityType, Level level) {
        super(entityType, level);
        this.accelerationPower = 0.1;
    }

    public BaseBulletEntity(EntityType<? extends Projectile> entityType, Level level, double x, double y, double z, ItemStack bullet) {
        super(entityType, level);
        this.setPos(x, y, z);
        this.entityData.set(BULLET, bullet.is(Items.AIR) || bullet.isEmpty() ? getDefaultItem() : bullet);
        this.accelerationPower = 0.1;
    }

    public BaseBulletEntity(Level level, double x, double y, double z, ItemStack bullet) {
        this(ModEntities.BASE_BULLET_ENTITY.get(), level, x, y, z, bullet);
    }

    public BaseBulletEntity(EntityType<? extends Projectile> entityType, LivingEntity owner, ItemStack bullet) {
        this(entityType, owner.level(), owner.getX(), owner.getEyeY() - 0.1, owner.getZ(), bullet);
        setOwner(owner);
    }

    public BaseBulletEntity(LivingEntity owner, ItemStack bullet) {
        this(ModEntities.BASE_BULLET_ENTITY.get(), owner, bullet);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE;
    }

    protected ClipContext.Block getClipType() {
        return ClipContext.Block.COLLIDER;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source);
    }

    public String getColorID() {
        if (!this.entityData.get(COLOR_ID).isEmpty()) {
            return this.entityData.get(COLOR_ID);
        }
        String bulletColorId = this.getBullet().colorID();
        if (bulletColorId != null && !bulletColorId.isEmpty()) {
            return bulletColorId;
        }
        return BuiltInRegistries.ITEM.getKey(this.getBullet()).getPath();
    }

    public void colorID(String colorID) {
        this.entityData.set(COLOR_ID, colorID);
    }

    public void setBullet(ItemStack stack) {
        if (stack.isEmpty()) {
            this.getEntityData().set(BULLET, this.getDefaultItem());
        } else {
            this.getEntityData().set(BULLET, stack.copyWithCount(1));
        }
    }

    public ItemStack getBulletStack() {
        return this.getEntityData().get(BULLET);
    }

    public float getDamage() {
        return damage;
    }

    /**
     * 更新穿透后的基础伤害，并同步更新 MagicLib 快照中的派生伤害。
     */
    public void setDamage(float damage) {
        this.damage = Math.max(0.0F, damage);
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        if (snapshot != null) {
            combatState.installSnapshot(snapshot.derive(
                    this.damage, snapshot.resolvedVelocity(), snapshot.knockback()));
        }
    }

    public float getKnockback() {
        return knockback;
    }

    public void setKnockback(float knockback) {
        this.knockback = Math.max(0.0F, knockback);
    }

    public int getPenetrate() {
        return penetrate;
    }

    public void setPenetrate(int penetrate) {
        if (penetrate < -1)
            throw new IllegalArgumentException("Bullet penetration must be -1 or non-negative");
        this.penetrate = penetrate;
    }

    /**
     * 设置未经原版生成包压缩的初速度。
     *
     * <p>原版生成包会把每个速度分量限制在 3.9，泰拉枪械的高速弹会因此在客户端明显落后。
     * 独立同步原始向量后，客户端会在第一次 tick 前恢复准确速度。</p>
     */
    public void setInitialVelocity(Vec3 velocity) {
        setDeltaMovement(velocity);
        hasImpulse = false;
        if (!level().isClientSide) {
            entityData.set(INITIAL_VELOCITY, new Vector3f(
                    (float) velocity.x, (float) velocity.y, (float) velocity.z));
            entityData.set(HAS_INITIAL_VELOCITY, true);
        }
    }

    public int getEffectState() {
        return entityData.get(EFFECT_STATE);
    }

    public void setEffectState(int state) {
        entityData.set(EFFECT_STATE, Math.max(0, state));
    }

    public boolean ignoresBlockCollision() {
        return entityData.get(IGNORE_BLOCK_COLLISION);
    }

    public void setIgnoresBlockCollision(boolean ignore) {
        entityData.set(IGNORE_BLOCK_COLLISION, ignore);
    }

    public LivingEntity getHomingTarget() {
        int targetId = entityData.get(HOMING_TARGET_ID);
        if (targetId < 0) return null;
        Entity target = level().getEntity(targetId);
        return target instanceof LivingEntity living ? living : null;
    }

    public void setHomingTarget(LivingEntity target) {
        entityData.set(HOMING_TARGET_ID, target == null ? -1 : target.getId());
    }

    public void clearHomingTarget() {
        entityData.set(HOMING_TARGET_ID, -1);
    }

    public boolean canHitTarget(Entity target) {
        return canHitEntity(target);
    }

    /**
     * 为分裂效果创建继承同一发射快照的派生弹丸。
     */
    @SuppressWarnings("unchecked")
    public BaseBulletEntity createChild(Vec3 velocity, float damageMultiplier, int effectState) {
        return createChild(velocity, damageMultiplier, effectState, Vec3.ZERO);
    }

    public BaseBulletEntity createChild(Vec3 velocity, float damageMultiplier, int effectState, Vec3 offset) {
        EntityType<? extends BaseBulletEntity> type = (EntityType<? extends BaseBulletEntity>) getType();
        BaseBulletEntity child;
        if (this instanceof CustomBulletEntity customBullet) {
            child = new CustomBulletEntity(
                    type, level(), getX() + offset.x, getY() + offset.y, getZ() + offset.z,
                    getBulletStack(), customBullet.getBulletGravity());
        } else {
            child = new BaseBulletEntity(
                    type, level(), getX() + offset.x, getY() + offset.y, getZ() + offset.z,
                    getBulletStack());
        }
        child.setOwner(getOwner());
        child.colorID(getColorID());
        child.knockback = knockback;
        child.penetrate = penetrate;
        child.accelerationPower = accelerationPower;
        child.setEffectState(effectState);
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        float childDamage = getCalculatedDamage() * Math.max(0.0F, damageMultiplier);
        if (snapshot != null) {
            float childVelocity = Math.max(0.0001F, (float) velocity.length());
            child.setProjectileCombatSnapshot(snapshot.derive(childDamage, childVelocity, snapshot.knockback()));
        } else {
            child.damage = childDamage;
        }
        child.setInitialVelocity(velocity);
        return child;
    }

    public BaseBullet getBullet() {
        Item item = this.getEntityData().get(BULLET).getItem();
        if (item instanceof BaseBullet bullet) {
            return bullet;
        }
        return (BaseBullet) getDefaultItem().getItem();
    }

    public DamageSource getDamageSource() {
        return LibUtils.damageSource(level(), LibDamageTypes.GUN_BULLET, this, getOwner());
    }

    /**
     * 返回应用 MagicLib 主通道倍率之前的冻结基础伤害。
     */
    public float getCalculatedDamage() {
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        return snapshot == null ? damage : snapshot.baseDamage();
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        Vec3 vec3 = position();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), vec3.x(), vec3.y(), vec3.z(), getXRot(), getYRot(), this.getType(), i, getDeltaMovement(), 0.0F);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        setDeltaMovement(Vec3.ZERO);
        appliedInitialVelocity = false;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(COLOR_ID, "");
        entityData.define(BULLET, this.getDefaultItem());
        entityData.define(HOMING_TARGET_ID, -1);
        entityData.define(EFFECT_STATE, 0);
        entityData.define(IGNORE_BLOCK_COLLISION, false);
        entityData.define(INITIAL_VELOCITY, new Vector3f());
        entityData.define(HAS_INITIAL_VELOCITY, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        ProjectileCombatState.RestoredBudgets budgets = combatState.readFrom(compound);
        if (combatState.isInvalid()) {
            resetBulletRuntimeFields();
            return;
        }
        try {
            BulletRuntimeState.BaseState runtimeState = BulletRuntimeState.readBase(compound);
            ProjectileCombatSnapshot snapshot = combatState.snapshot();
            if (snapshot == null) {
                throw new IllegalStateException("Loaded bullet combat snapshot must not be null");
            }
            this.colorID(runtimeState.colorId());
            this.setBullet(runtimeState.bullet());
            this.hitBlockTimes = runtimeState.hitBlockTimes();
            this.accelerationPower = runtimeState.accelerationPower();
            this.setEffectState(runtimeState.effectState());
            this.setIgnoresBlockCollision(runtimeState.ignoreBlockCollision());
            this.damage = snapshot.baseDamage();
            this.knockback = snapshot.knockback();
            this.penetrate = budgets.remainingHits();
            this.ownerResolutionTicks = 0;
        } catch (RuntimeException exception) {
            invalidateRuntimeState(BulletRuntimeState.englishReason(
                    exception, "Malformed bullet runtime state"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        combatState.writeTo(compound, -1, penetrate);
        BulletRuntimeState.writeBase(
                compound,
                this.getColorID(),
                this.getBulletStack(),
                this.hitBlockTimes,
                this.accelerationPower,
                this.getEffectState(),
                this.ignoresBlockCollision()
        );
    }

    /**
     * 供子类的附加运行状态复用统一安全失效通道；原因必须是英文开发者诊断。
     */
    protected final void invalidateRuntimeState(String reason) {
        combatState.invalidate(reason);
        resetBulletRuntimeFields();
    }

    private void resetBulletRuntimeFields() {
        this.colorID("");
        this.setBullet(this.getDefaultItem());
        this.damage = 0.0F;
        this.knockback = 0.0F;
        this.penetrate = 0;
        this.hitBlockTimes = 0;
        this.accelerationPower = 0.0;
        this.ownerResolutionTicks = 0;
        this.setEffectState(0);
        this.setIgnoresBlockCollision(false);
        this.setHomingTarget(null);
        this.contactedEntityIds.clear();
    }

    protected ItemStack getDefaultItem() {
        return GunItems.DUMMY_BULLET.get().getDefaultInstance();
    }

    @Override
    public void tick() {
        applyInitialVelocity();
        if (!level().isClientSide && combatState.discardIfInvalid(this)) {
            return;
        }
        if (!level().isClientSide && combatState.wasLoadedFromTag()
                && combatState.snapshot() != null && getOwner() == null) {
            if (ownerResolutionTicks++ == 0) {
                return;
            }
            combatState.invalidate("Projectile owner could not be resolved after loading");
            combatState.discardIfInvalid(this);
            return;
        }
        PortEventHandler.postEvent(new BulletEvent.Tick.Pre(this, this.getBullet()));
        super.tick();
        if (shouldDiscard()) {
            this.discard();
            PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, this.getBullet()));
            return;
        }

        this.getBullet().tick(this);
        this.saveTrailPos();
        this.checkInsideBlocks();
        this.applyForces();
        this.sweptEntityIds.clear();

        Vec3 velocity = this.getDeltaMovement();
        Vec3 movement = velocity;
        boolean deflected = false;
        int entityCollisions = 0;
        while (!isRemoved() && movement.lengthSqr() > 1.0E-7D) {
            Vec3 segmentStart = position();
            Vec3 segmentEnd = segmentStart.add(movement);
            HitResult hitResult = findHitResult(segmentStart, segmentEnd);
            if (hitResult == null) {
                setPos(segmentEnd.x, segmentEnd.y, segmentEnd.z);
                break;
            }

            Vec3 hitPosition = hitResult.getLocation();
            setPos(hitPosition.x, hitPosition.y, hitPosition.z);
            if (hitResult instanceof BlockHitResult blockHit) {
                onHitBlock(blockHit);
                PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, this.getBullet()));
                return;
            }
            if (!(hitResult instanceof EntityHitResult entityHit)) {
                setPos(segmentEnd.x, segmentEnd.y, segmentEnd.z);
                break;
            }

            sweptEntityIds.add(entityHit.getEntity().getUUID());
            PortProjectileDeflection deflection = hitTargetOrDeflectSelf(entityHit);
            if (isRemoved()) {
                PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, this.getBullet()));
                return;
            }
            if (deflection != PortProjectileDeflection.NONE) {
                // 反射器已经给出新的飞行方向；不能再用碰撞前的局部速度覆盖它，
                // 也不能在同一 tick 沿原来的剩余线段继续碰撞。
                velocity = getDeltaMovement();
                deflected = true;
                break;
            }

            entityCollisions++;
            Vec3 remaining = segmentEnd.subtract(hitPosition);
            if (remaining.lengthSqr() <= 1.0E-7D || entityCollisions >= MAX_ENTITY_COLLISIONS_PER_TICK) {
                movement = Vec3.ZERO;
                break;
            }
            Vec3 continuation = remaining.normalize();
            double offset = Math.min(COLLISION_EPSILON, remaining.length() * 0.5D);
            setPos(hitPosition.add(continuation.scale(offset)));
            movement = segmentEnd.subtract(position());
        }

        setDeltaMovement(velocity);
        if (deflected) {
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, this.getBullet()));
            return;
        }
        float inertia = getInertia();
        Vec3 acceleration = velocity.lengthSqr() > 1.0E-7D
                ? velocity.normalize().scale(accelerationPower)
                : Vec3.ZERO;
        setDeltaMovement(velocity.add(acceleration).scale(inertia));
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, this.getBullet()));
    }

    private void applyInitialVelocity() {
        if (!level().isClientSide || appliedInitialVelocity || !entityData.get(HAS_INITIAL_VELOCITY)) {
            return;
        }
        Vector3f velocity = entityData.get(INITIAL_VELOCITY);
        setDeltaMovement(new Vec3(velocity.x(), velocity.y(), velocity.z()));
        appliedInitialVelocity = true;
    }

    /**
     * 在本 tick 的完整位移线段上选择最先发生的方块或实体碰撞。
     */
    private HitResult findHitResult(Vec3 start, Vec3 end) {
        EntityHitResult entityHit = findEntityHit(start, end);
        BlockHitResult blockHit = ignoresBlockCollision() ? null : findBlockHit(start, end);
        if (entityHit == null) return blockHit;
        if (blockHit == null) return entityHit;
        return start.distanceToSqr(entityHit.getLocation()) <= start.distanceToSqr(blockHit.getLocation())
                ? entityHit : blockHit;
    }

    private BlockHitResult findBlockHit(Vec3 start, Vec3 end) {
        BlockHitResult result = level().clip(new ClipContext(
                start, end, getClipType(), ClipContext.Fluid.NONE, this));
        return result.getType() == HitResult.Type.BLOCK ? result : null;
    }

    private EntityHitResult findEntityHit(Vec3 start, Vec3 end) {
        Vec3 movement = end.subtract(start);
        AABB searchBox = getBoundingBox().expandTowards(movement).inflate(ENTITY_SWEEP_MARGIN);
        List<Entity> candidates = level().getEntities(this, searchBox,
                target -> canHitEntity(target) && !sweptEntityIds.contains(target.getUUID()));
        EntityHitResult closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        double horizontalExtent = getBbWidth() * 0.5D;
        double verticalExtent = getBbHeight() * 0.5D;
        for (Entity candidate : candidates) {
            AABB collisionBox = candidate.getBoundingBox().inflate(
                    horizontalExtent, verticalExtent, horizontalExtent);
            Optional<Vec3> hitPosition = collisionBox.clip(start, end);
            if (hitPosition.isEmpty()) continue;
            double distance = start.distanceToSqr(hitPosition.get());
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = new EntityHitResult(candidate, hitPosition.get());
            }
        }
        return closest;
    }

    private boolean shouldDiscard() {
        if (tickCount >= MAX_LIFETIME) return true;
        if (level().isClientSide) return false;
        Entity owner = getOwner();
        if (!level().hasChunkAt(blockPosition())) return true;
        if (owner == null) return false;
        if (owner.isRemoved() || disToOwner() > MAX_OWNER_DISTANCE) return true;
        return position().add(getDeltaMovement()).distanceTo(owner.position()) > MAX_OWNER_DISTANCE;
    }

    /**
     * 供重力弹等变体在碰撞计算前应用本 tick 的外力。
     */
    protected void applyForces() {}

    protected float getInertia() {
        return 0.95F;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    public double disToOwner() {
        Entity owner = getOwner();
        return owner == null ? 256 : position().distanceTo(owner.position());
    }

    private void saveTrailPos() {
        if (this.level().isClientSide) {
            Vec3 currentPos = this.position();

            if (trails.isEmpty()) {
                PortListExtension.addLast(trails, currentPos);
                return;
            }

            Vec3 lastPos = PortListExtension.getLast(trails);
            double dist = lastPos.distanceTo(currentPos);

            if (dist <= TRAIL_POINT_EPSILON) return;
            if (dist > TRAIL_POINT_SPACING) {
                int steps = (int) Math.ceil(dist / TRAIL_POINT_SPACING);
                Vec3 delta = currentPos.subtract(lastPos).scale(1.0 / steps);
                for (int i = 1; i <= steps; i++) {
                    PortListExtension.addLast(trails, lastPos.add(delta.scale(i)));
                }
            } else {
                PortListExtension.addLast(trails, currentPos);
            }

            int maxTrailPoints = "chlorophyte_bullet".equals(getColorID())
                    ? CHLOROPHYTE_TRAIL_POINTS : MAX_TRAIL_POINTS;
            while (trails.size() > maxTrailPoints) {
                PortListExtension.removeFirst(trails);
            }
        }
    }


    public List<Vec3> getTrails() {
        return trails;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!ProjectileHitRules.canHit(getOwner(), target)) {
            return false;
        }
        Entity impacted = ProjectileHitRules.impactedEntity(target);
        UUID impactedId = impacted.getUUID();
        return !contactedEntityIds.contains(impactedId)
                && combatState.canHit(impactedId, false);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity impacted = ProjectileHitRules.impactedEntity(result.getEntity());
        contactedEntityIds.add(impacted.getUUID());
        if (!level().isClientSide) {
            BulletImpactPacketS2C.send(this, result.getLocation());
        }
        if (PortEventHandler.postEventWithReturn(new BulletEvent.HitEvent.Entity(this, this.getBullet(), result)).isCanceled())
            return;

        Entity hit = result.getEntity();
        Entity shooter = this.getOwner();

        if (!level().isClientSide && hit != shooter && !this.isRemoved()) {
            BulletEvent.DamageEntityEvent damageEntityEvent = new BulletEvent.DamageEntityEvent(this, this.getBullet(), shooter, hit);
            if (PortEventHandler.postEventWithReturn(damageEntityEvent).isCanceled()) {
                return;
            }

            if (!this.getBullet().onHitEntity(this, new EntityHitResult(impacted))) {
                return;
            }
            combatState.recordSuccessfulHit(impacted.getUUID());
            if (this.knockback > 0) {
                float resolvedScale = combatState.snapshot() == null ? knockback / 8.0F : knockback;
                BulletEvent.KnockbackEvent knockbackEvent = new BulletEvent.KnockbackEvent(
                        this, this.getBullet(), resolvedScale, 0.0F);
                PortEventHandler.postEvent(knockbackEvent);

                ProjectileHitRules.applyResolvedKnockback(
                        this, impacted, (float) knockbackEvent.getScale(), knockbackEvent.getMotionY());
            }

            BulletEvent.PenetrateEvent penetrateEvent = new BulletEvent.PenetrateEvent(this, this.getBullet(), penetrate);
            PortEventHandler.postEvent(penetrateEvent);
            int penetrate = penetrateEvent.getPenetrate();

            if (penetrate < 0) {
                return;
            } else if (penetrate <= 1) {
                this.discard();
                return;
            }
            this.penetrate = penetrate - 1;
        }
    }

    /**
     * 宝箱怪反射枪弹后接管弹幕，并将伤害与剩余穿透限制为泰拉的反射规格。
     * 所有权必须在服务端更换，否则弹回玩家时仍会被友方命中过滤拦截。
     */
    @Override
    public void onDeflection(@Nullable Entity entity, boolean deflectedByPlayer) {
        if (entity instanceof BaseMimic) {
            setOwner(entity);
            setDamage(getDamage() * 0.5F);
            setPenetrate(1);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (PortEventHandler.postEventWithReturn(new BulletEvent.HitEvent.Block(this, this.getBullet(), result)).isCanceled())
            return;

        if (!level().isClientSide) {
            BulletImpactPacketS2C.send(this, result.getLocation());
        }

        super.onHitBlock(result);
        this.getBullet().onHitBlock(this, result);

        this.hitBlockTimes++;
    }

    @Override
    public @org.jetbrains.annotations.Nullable ProjectileCombatSnapshot getProjectileCombatSnapshot() {
        return combatState.snapshot();
    }

    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        combatState.installSnapshot(snapshot);
        damage = snapshot.baseDamage();
        knockback = snapshot.knockback();
    }
}
