package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.data.map.ImmunityDataMap;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;

/// 剑气实体的服务端运行时，负责运动、追踪、碰撞、伤害与快照同步。
public abstract class SwordProjectile extends AbstractHurtingProjectile implements IEntityAdditionalSpawnData, Immunity {
    private static final EntityDataAccessor<Vector3f> DATA_INITIAL_VELOCITY = SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> DATA_GRAVITY = SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Vector3f> DATA_DIRECTION = SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.INT);
    private static final byte EVENT_ENTITY_HIT = 61;
    private static final byte EVENT_BLOCK_HIT = 62;

    private @Nullable SwordProjectileComponent component;
    private ItemStack firedFromWeapon = ItemStack.EMPTY;
    private @Nullable LivingEntity trackingTarget;
    private float gravity;
    private float baseAttackDamage;
    private float baseKnockback;
    private int collisionInterval = 1;
    private int collisionCooldown = 1;
    private double collisionInflation = 0.5;

    protected Vec3 direction = Vec3.ZERO;
    protected int lifetime = 40;
    protected int remainingHits = 1;
    protected float bonusKnockback;
    protected boolean survivesBlockHit;

    protected SwordProjectile(EntityType<? extends SwordProjectile> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide) {
            direction = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            entityData.set(DATA_DIRECTION, direction.toVector3f());
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_INITIAL_VELOCITY, new Vector3f());
        entityData.define(DATA_GRAVITY, 0.0F);
        entityData.define(DATA_DIRECTION, new Vector3f());
        entityData.define(DATA_LIFETIME, lifetime);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (!level().isClientSide) return;
        if (data == DATA_INITIAL_VELOCITY) {
            setDeltaMovement(new Vec3(entityData.get(DATA_INITIAL_VELOCITY)));
        } else if (data == DATA_GRAVITY) {
            gravity = entityData.get(DATA_GRAVITY);
        } else if (data == DATA_DIRECTION) {
            direction = new Vec3(entityData.get(DATA_DIRECTION));
            float yaw = (float) Mth.atan2(direction.x, direction.z) * Mth.RAD_TO_DEG;
            setYRot(yaw);
            yRotO = yaw;
        } else if (data == DATA_LIFETIME) {
            lifetime = entityData.get(DATA_LIFETIME);
        }
    }

    public final void configure(LivingEntity owner, ItemStack weapon, SwordProjectileComponent component, float damage) {
        setOwner(owner);
        firedFromWeapon = weapon.copy();
        setProjectileComponent(component);
        baseAttackDamage = damage;
        AttributeInstance knockback = owner.getAttribute(Attributes.ATTACK_KNOCKBACK);
        baseKnockback = knockback == null ? 0.0F : (float) knockback.getValue();
    }

    public final void setProjectileComponent(SwordProjectileComponent component) {
        this.component = component;
        gravity = component.gravity();
        lifetime = component.existTicks();
        entityData.set(DATA_GRAVITY, gravity);
        entityData.set(DATA_LIFETIME, lifetime);
    }

    public final @Nullable SwordProjectileComponent getProjectileComponent() {
        return component;
    }

    @Override
    public @Nullable ItemStack getWeaponItem() {
        return firedFromWeapon;
    }

    public final int getLifetime() {
        return lifetime;
    }

    public final SwordProjectile addAttackDamage(float damage) {
        baseAttackDamage += damage;
        return this;
    }

    protected final void clearKnockback() {
        baseKnockback = 0.0F;
        bonusKnockback = 0.0F;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide && component != null && component.trackType().isPresent())
            acquireTrackingTarget();
    }

    private void acquireTrackingTarget() {
        if (!(getOwner() instanceof LivingEntity owner)) return;
        trackingTarget = level().getEntities(this, getBoundingBox().inflate(50.0), entity -> entity instanceof LivingEntity living && living.isAlive() && ProjectileHitRules.canHit(owner, living)).stream()
                .map(LivingEntity.class::cast)
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) SwordProjectileVisualBridge.tick(this);
        if (component != null) {
            applyGravity();
            updateTracking(component);
        }
        if (!level().isClientSide && tickCount >= lifetime) discard();
        if (!level().isClientSide && !isRemoved() && usesDefaultCollisionDamage())
            tickCollisionDamage();
    }

    protected boolean usesDefaultCollisionDamage() {
        return true;
    }

    private void tickCollisionDamage() {
        if (--collisionCooldown > 0) return;
        collisionCooldown = collisionInterval;
        for (Entity target : level().getEntities(this, getBoundingBox().inflate(collisionInflation), this::canHitEntity)) {
            hurtTarget(target);
        }
    }

    protected final void configureCollision(int interval, double inflation) {
        if (interval < 1 || !Double.isFinite(inflation) || inflation < 0.0) {
            throw new IllegalArgumentException("Invalid sword projectile collision settings");
        }
        collisionInterval = interval;
        collisionCooldown = interval;
        collisionInflation = inflation;
    }

    private void updateTracking(SwordProjectileComponent component) {
        if (trackingTarget == null || !trackingTarget.isAlive() || component.trackType().isEmpty())
            return;
        Vec3 motion = getDeltaMovement();
        Vec3 targetDirection = trackingTarget.getBoundingBox().getCenter().subtract(position()).normalize().scale(motion.length());
        double angle = LibMathUtils.angleBetween(motion, targetDirection);
        setDeltaMovement(component.trackType().get().calDeltaMovement(motion, targetDirection, angle));
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return remainingHits > 0 && ProjectileHitRules.canHit(getOwner(), target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide && usesDefaultCollisionDamage()) hurtTarget(result.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide) return;
        level().broadcastEntityEvent(this, EVENT_BLOCK_HIT);
        if (!survivesBlockHit) discard();
    }

    protected boolean hurtTarget(Entity target) {
        if (!canHitEntity(target)) return false;
        Entity impacted = ProjectileHitRules.impactedEntity(target);
        if (impacted instanceof LivingEntity living) {
            if (!Immunity.hurt(this, living, damageSource(), baseAttackDamage)) return false;
            LibEntityUtils.knockBackA2B(this, living, (baseKnockback + bonusKnockback) * 0.5, 0.2);
        } else if (!impacted.hurt(damageSource(), baseAttackDamage)) {
            return false;
        }
        applyHitEffect(impacted);
        level().broadcastEntityEvent(this, EVENT_ENTITY_HIT);
        if (--remainingHits <= 0) discard();
        return true;
    }

    protected void applyHitEffect(Entity target) {}

    public DamageSource damageSource() {
        return LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, getOwner());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ENTITY_HIT) SwordProjectileVisualBridge.entityHit(this);
        else if (id == EVENT_BLOCK_HIT) SwordProjectileVisualBridge.blockHit(this);
        else super.handleEntityEvent(id);
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float directionX = -Mth.sin(y * Mth.DEG_TO_RAD) * Mth.cos(x * Mth.DEG_TO_RAD);
        float directionY = -Mth.sin((x + z) * Mth.DEG_TO_RAD);
        float directionZ = Mth.cos(y * Mth.DEG_TO_RAD) * Mth.cos(x * Mth.DEG_TO_RAD);
        shoot(directionX, directionY, directionZ, velocity, inaccuracy);
        Vec3 ownerMovement = shooter.getKnownMovement().scale(0.25F);
        setDeltaMovement(getDeltaMovement().add(ownerMovement.x, shooter.onGround() ? 0.0 : ownerMovement.y, ownerMovement.z));
        direction = getDeltaMovement().normalize();
        entityData.set(DATA_DIRECTION, direction.toVector3f());
        entityData.set(DATA_INITIAL_VELOCITY, getDeltaMovement().toVector3f());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (component != null)
            SwordProjectileComponent.CODEC.encodeStart(NbtOps.INSTANCE, component).result().ifPresent(value -> tag.put("ProjectileComponent", value));
        if (!firedFromWeapon.isEmpty()) tag.put("Weapon", firedFromWeapon.save(new CompoundTag()));
        tag.putFloat("BaseDamage", baseAttackDamage);
        tag.putFloat("BaseKnockback", baseKnockback);
        tag.putFloat("BonusKnockback", bonusKnockback);
        tag.putInt("RemainingHits", remainingHits);
        tag.putBoolean("SurvivesBlockHit", survivesBlockHit);
        tag.putDouble("DirectionX", direction.x);
        tag.putDouble("DirectionY", direction.y);
        tag.putDouble("DirectionZ", direction.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ProjectileComponent"))
            SwordProjectileComponent.CODEC.parse(NbtOps.INSTANCE, tag.get("ProjectileComponent")).result().ifPresent(this::setProjectileComponent);
        firedFromWeapon = tag.contains("Weapon") ? ItemStack.of(tag.getCompound("Weapon")) : ItemStack.EMPTY;
        baseAttackDamage = tag.getFloat("BaseDamage");
        baseKnockback = tag.getFloat("BaseKnockback");
        bonusKnockback = tag.getFloat("BonusKnockback");
        remainingHits = tag.getInt("RemainingHits");
        survivesBlockHit = tag.getBoolean("SurvivesBlockHit");
        Vec3 savedDirection = new Vec3(tag.getDouble("DirectionX"), tag.getDouble("DirectionY"), tag.getDouble("DirectionZ"));
        if (!Double.isFinite(savedDirection.x) || !Double.isFinite(savedDirection.y) || !Double.isFinite(savedDirection.z)
                || savedDirection.lengthSqr() <= 1.0E-7) savedDirection = getDeltaMovement();
        if (savedDirection.lengthSqr() > 1.0E-7) direction = savedDirection.normalize();
        entityData.set(DATA_DIRECTION, direction.toVector3f());
        entityData.set(DATA_INITIAL_VELOCITY, getDeltaMovement().toVector3f());
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(component != null);
        if (component != null) SwordProjectileComponent.STREAM_CODEC.encode(buffer, component);
        buffer.writeItem(firedFromWeapon);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        if (buffer.readBoolean())
            setProjectileComponent(SwordProjectileComponent.STREAM_CODEC.decode(buffer));
        firedFromWeapon = buffer.readItem();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public Vec3 getProjectileDirection() {
        return direction;
    }

    @Override
    public double getDefaultGravity() {
        return gravity;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NO_TRAIL.get();
    }

    @Override
    public Type confluence$getImmunityType() {
        return ImmunityDataMap.getImmunityType(this);
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return ImmunityDataMap.getImmunityDuration(this, damageSource, source -> 1);
    }
}
