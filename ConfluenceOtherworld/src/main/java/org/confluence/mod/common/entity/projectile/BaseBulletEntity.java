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
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.item.BaseBullet;
import org.joml.Vector3f;
import org.mesdag.portlib.event.PortEventHandler;

import java.util.*;

public class BaseBulletEntity extends Projectile {
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

    private final Set<UUID> hitEntityIds = new HashSet<>();
    private final List<Vec3> trails = new ArrayList<>();
    private boolean appliedInitialVelocity;
    private float damage;
    private float knockback;
    private int penetrate;
    private int hitBlockTimes;
    public double accelerationPower = 0.1D;

    public BaseBulletEntity(EntityType<? extends BaseBulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseBulletEntity(EntityType<? extends Projectile> entityType, Level level, double x, double y, double z, ItemStack bullet) {
        super(entityType, level);
        setPos(x, y, z);
        setBullet(bullet);
    }

    public BaseBulletEntity(Level level, double x, double y, double z, ItemStack bullet) {
        this(ModEntities.BASE_BULLET_ENTITY.get(), level, x, y, z, bullet);
    }

    public BaseBulletEntity(EntityType<? extends Projectile> entityType, LivingEntity owner, ItemStack bullet) {
        this(entityType, owner.level(), owner.getX(), owner.getEyeY() - 0.1D, owner.getZ(), bullet);
        setOwner(owner);
    }

    public BaseBulletEntity(LivingEntity owner, ItemStack bullet) {
        this(ModEntities.BASE_BULLET_ENTITY.get(), owner, bullet);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(COLOR_ID, "");
        entityData.define(BULLET, getDefaultItem());
        entityData.define(HOMING_TARGET_ID, -1);
        entityData.define(EFFECT_STATE, 0);
        entityData.define(IGNORE_BLOCK_COLLISION, false);
        entityData.define(INITIAL_VELOCITY, new Vector3f());
        entityData.define(HAS_INITIAL_VELOCITY, false);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = Math.max(0.0F, damage);
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
        this.penetrate = penetrate;
    }

    public int getEffectState() {
        return entityData.get(EFFECT_STATE);
    }

    public void setEffectState(int effectState) {
        entityData.set(EFFECT_STATE, Math.max(0, effectState));
    }

    public boolean ignoresBlockCollision() {
        return entityData.get(IGNORE_BLOCK_COLLISION);
    }

    public void setIgnoresBlockCollision(boolean ignoresBlockCollision) {
        entityData.set(IGNORE_BLOCK_COLLISION, ignoresBlockCollision);
    }

    public @org.jetbrains.annotations.Nullable LivingEntity getHomingTarget() {
        Entity target = level().getEntity(entityData.get(HOMING_TARGET_ID));
        return target instanceof LivingEntity living ? living : null;
    }

    public void setHomingTarget(@org.jetbrains.annotations.Nullable LivingEntity target) {
        entityData.set(HOMING_TARGET_ID, target == null ? -1 : target.getId());
    }

    public void clearHomingTarget() {
        entityData.set(HOMING_TARGET_ID, -1);
    }

    public boolean canHitTarget(Entity target) {
        return canHitEntity(target);
    }

    public void setInitialVelocity(Vec3 velocity) {
        setDeltaMovement(velocity);
        hasImpulse = false;
        if (!level().isClientSide) {
            entityData.set(INITIAL_VELOCITY, velocity.toVector3f());
            entityData.set(HAS_INITIAL_VELOCITY, true);
        }
    }

    @SuppressWarnings("unchecked")
    public BaseBulletEntity createChild(Vec3 velocity, float damageMultiplier, int effectState) {
        return createChild(velocity, damageMultiplier, effectState, Vec3.ZERO);
    }

    @SuppressWarnings("unchecked")
    public BaseBulletEntity createChild(Vec3 velocity, float damageMultiplier, int effectState, Vec3 spawnOffset) {
        EntityType<? extends BaseBulletEntity> type = (EntityType<? extends BaseBulletEntity>) getType();
        BaseBulletEntity child = this instanceof CustomBulletEntity custom
                ? new CustomBulletEntity(type, level(), getX() + spawnOffset.x, getY() + spawnOffset.y, getZ() + spawnOffset.z, getBulletStack(), custom.getBulletGravity())
                : new BaseBulletEntity(type, level(), getX() + spawnOffset.x, getY() + spawnOffset.y, getZ() + spawnOffset.z, getBulletStack());
        child.setOwner(getOwner());
        child.setColorID(getColorID());
        child.setDamage(damage * Math.max(0.0F, damageMultiplier));
        child.setKnockback(knockback);
        child.setPenetrate(penetrate);
        child.setEffectState(effectState);
        child.accelerationPower = accelerationPower;
        child.setInitialVelocity(velocity);
        return child;
    }

    public String getColorID() {
        String color = entityData.get(COLOR_ID);
        if (!color.isEmpty()) return color;
        String bulletColor = getBullet().colorID();
        return bulletColor == null || bulletColor.isEmpty()
                ? BuiltInRegistries.ITEM.getKey(getBulletStack().getItem()).getPath() : bulletColor;
    }

    public void setColorID(String colorID) {
        entityData.set(COLOR_ID, colorID == null ? "" : colorID);
    }

    public void setBullet(ItemStack stack) {
        entityData.set(BULLET, stack == null || stack.isEmpty() || stack.is(Items.AIR)
                ? getDefaultItem() : stack.copyWithCount(1));
    }

    public ItemStack getBulletStack() {
        return entityData.get(BULLET);
    }

    public BaseBullet getBullet() {
        Item item = getBulletStack().getItem();
        return item instanceof BaseBullet bullet ? bullet : (BaseBullet) getDefaultItem().getItem();
    }

    public DamageSource getDamageSource() {
        return LibDamageTypes.of(level(), LibDamageTypes.GUN_BULLET, this, getOwner());
    }

    @Override
    public void tick() {
        PortEventHandler.postEvent(new BulletEvent.Tick.Pre(this, getBullet()));
        applyInitialVelocity();
        super.tick();
        if (shouldDiscard()) {
            discard();
            PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, getBullet()));
            return;
        }

        getBullet().getBehavior().tick(this);
        saveTrailPos();
        checkInsideBlocks();
        applyForces();
        Vec3 velocity = getDeltaMovement();
        Vec3 movement = velocity;
        int entityCollisions = 0;
        while (!isRemoved() && movement.lengthSqr() > 1.0E-7D) {
            setDeltaMovement(movement);
            Vec3 segmentStart = position();
            Vec3 segmentEnd = segmentStart.add(movement);
            HitResult hitResult = findHitResult(segmentStart, segmentEnd);
            if (hitResult == null) {
                setPos(segmentEnd.x, segmentEnd.y, segmentEnd.z);
                break;
            }
            Vec3 hitPosition = hitResult.getLocation();
            setPos(hitPosition.x, hitPosition.y, hitPosition.z);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                onHitBlock((BlockHitResult) hitResult);
                PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, getBullet()));
                return;
            }
            if (hitResult.getType() != HitResult.Type.ENTITY) {
                setPos(segmentEnd.x, segmentEnd.y, segmentEnd.z);
                break;
            }
            onHitEntity((EntityHitResult) hitResult);
            if (isRemoved()) {
                PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, getBullet()));
                return;
            }
            entityCollisions++;
            Vec3 remaining = segmentEnd.subtract(hitPosition);
            if (remaining.lengthSqr() <= 1.0E-7D || entityCollisions >= MAX_ENTITY_COLLISIONS_PER_TICK)
                break;
            Vec3 continuation = remaining.normalize();
            double offset = Math.min(COLLISION_EPSILON, remaining.length() * 0.5D);
            setPos(hitPosition.x + continuation.x * offset, hitPosition.y + continuation.y * offset, hitPosition.z + continuation.z * offset);
            movement = segmentEnd.subtract(position());
        }
        setDeltaMovement(velocity);
        Vec3 acceleration = velocity.lengthSqr() > 1.0E-7D ? velocity.normalize().scale(accelerationPower) : Vec3.ZERO;
        setDeltaMovement(velocity.add(acceleration).scale(getInertia()));
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        PortEventHandler.postEvent(new BulletEvent.Tick.Post(this, getBullet()));
    }

    private HitResult findHitResult(Vec3 start, Vec3 end) {
        EntityHitResult entityHit = findEntityHit(start, end);
        BlockHitResult blockHit = ignoresBlockCollision() ? null : findBlockHit(start, end);
        if (entityHit == null) return blockHit;
        if (blockHit == null) return entityHit;
        return start.distanceToSqr(entityHit.getLocation()) <= start.distanceToSqr(blockHit.getLocation()) ? entityHit : blockHit;
    }

    private BlockHitResult findBlockHit(Vec3 start, Vec3 end) {
        BlockHitResult result = level().clip(new ClipContext(start, end, getClipType(), ClipContext.Fluid.NONE, this));
        return result.getType() == HitResult.Type.BLOCK ? result : null;
    }

    private EntityHitResult findEntityHit(Vec3 start, Vec3 end) {
        Vec3 movement = end.subtract(start);
        AABB searchBox = getBoundingBox().expandTowards(movement).inflate(ENTITY_SWEEP_MARGIN);
        EntityHitResult closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        double horizontalExtent = getBbWidth() * 0.5D;
        double verticalExtent = getBbHeight() * 0.5D;
        for (Entity candidate : level().getEntities(this, searchBox, this::canHitEntity)) {
            AABB collisionBox = candidate.getBoundingBox().inflate(horizontalExtent, verticalExtent, horizontalExtent);
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

    private void applyInitialVelocity() {
        if (!level().isClientSide || appliedInitialVelocity || !entityData.get(HAS_INITIAL_VELOCITY))
            return;
        Vector3f velocity = entityData.get(INITIAL_VELOCITY);
        setDeltaMovement(new Vec3(velocity.x(), velocity.y(), velocity.z()));
        appliedInitialVelocity = true;
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

    protected void applyForces() {}

    protected ClipContext.Block getClipType() {
        return ClipContext.Block.COLLIDER;
    }

    protected float getInertia() {
        return 0.95F;
    }

    private void saveTrailPos() {
        if (!level().isClientSide) return;
        Vec3 current = position();
        if (trails.isEmpty()) {
            PortListExtension.addLast(trails, current);
            return;
        }
        Vec3 last = PortListExtension.getLast(trails);
        double distance = last.distanceTo(current);
        if (distance <= TRAIL_POINT_EPSILON) return;
        if (distance > TRAIL_POINT_SPACING) {
            int steps = (int) Math.ceil(distance / TRAIL_POINT_SPACING);
            Vec3 delta = current.subtract(last).scale(1.0D / steps);
            for (int index = 1; index <= steps; index++)
                PortListExtension.addLast(trails, last.add(delta.scale(index)));
        } else {
            PortListExtension.addLast(trails, current);
        }
        int maximum = "chlorophyte_bullet".equals(getColorID()) ? CHLOROPHYTE_TRAIL_POINTS : MAX_TRAIL_POINTS;
        while (trails.size() > maximum) PortListExtension.removeFirst(trails);
    }

    public List<Vec3> getTrails() {
        return trails;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.canBeHitByProjectile()) return false;
        Entity owner = getOwner();
        return target != owner && !hitEntityIds.contains(target.getUUID())
                && (owner == null || !owner.isPassengerOfSameVehicle(target));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hit = result.getEntity();
        hitEntityIds.add(hit.getUUID());
        BulletEvent.HitEvent.Entity hitEvent = new BulletEvent.HitEvent.Entity(this, getBullet(), result);
        PortEventHandler.postEvent(hitEvent);
        if (hitEvent.isCanceled()) return;
        Entity shooter = getOwner();
        if (level().isClientSide || hit == shooter || isRemoved()) return;
        BulletEvent.DamageEntityEvent damageEvent = new BulletEvent.DamageEntityEvent(this, getBullet(), shooter, hit);
        PortEventHandler.postEvent(damageEvent);
        if (damageEvent.isCanceled()) return;
        hit.hurt(getDamageSource(), damage);
        getBullet().getBehavior().onHitEntity(this, result);
        if (knockback > 0.0F) {
            BulletEvent.KnockbackEvent knockbackEvent = new BulletEvent.KnockbackEvent(this, getBullet(), knockback / 8.0F, 0.0F);
            PortEventHandler.postEvent(knockbackEvent);
            LibEntityUtils.knockBackA2B(this, hit, knockbackEvent.getScale(), knockbackEvent.getMotionY());
        }
        BulletEvent.PenetrateEvent penetrateEvent = new BulletEvent.PenetrateEvent(this, getBullet(), penetrate);
        PortEventHandler.postEvent(penetrateEvent);
        int remaining = penetrateEvent.getPenetrate();
        if (remaining < 0) return;
        if (remaining <= 1) {
            discard();
            return;
        }
        penetrate = remaining - 1;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BulletEvent.HitEvent.Block event = new BulletEvent.HitEvent.Block(this, getBullet(), result);
        PortEventHandler.postEvent(event);
        if (event.isCanceled()) return;
        super.onHitBlock(result);
        if (!getBullet().getBehavior().onHitBlock(this, result)) discard();
        hitBlockTimes++;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ColorID", CompoundTag.TAG_STRING)) setColorID(tag.getString("ColorID"));
        setBullet(tag.contains("Item", CompoundTag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound("Item")) : getDefaultItem());
        damage = tag.getFloat("Damage");
        knockback = tag.getFloat("Knockback");
        penetrate = tag.getInt("Penetrate");
        setEffectState(tag.getInt("EffectState"));
        setIgnoresBlockCollision(tag.getBoolean("IgnoreBlockCollision"));
        hitBlockTimes = tag.getInt("HitBlockTime");
        if (tag.contains("acceleration_power", CompoundTag.TAG_DOUBLE))
            accelerationPower = tag.getDouble("acceleration_power");
        tickCount = tag.getInt("Lifetime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("ColorID", getColorID());
        if (!getBulletStack().isEmpty()) tag.put("Item", getBulletStack().save(new CompoundTag()));
        tag.putFloat("Damage", damage);
        tag.putFloat("Knockback", knockback);
        tag.putInt("Penetrate", penetrate);
        tag.putInt("EffectState", getEffectState());
        tag.putBoolean("IgnoreBlockCollision", ignoresBlockCollision());
        tag.putInt("HitBlockTime", hitBlockTimes);
        tag.putDouble("acceleration_power", accelerationPower);
        tag.putInt("Lifetime", tickCount);
    }

    protected ItemStack getDefaultItem() {
        return GunItems.DUMMY_BULLET.toStack();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity owner = getOwner();
        Vec3 position = position();
        return new ClientboundAddEntityPacket(getId(), getUUID(), position.x, position.y, position.z, getXRot(), getYRot(),
                getType(), owner == null ? 0 : owner.getId(), Vec3.ZERO, 0.0D);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        setDeltaMovement(Vec3.ZERO);
        appliedInitialVelocity = false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !isInvulnerableTo(source);
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
        return getOwner() == null ? MAX_OWNER_DISTANCE : position().distanceTo(getOwner().position());
    }
}
