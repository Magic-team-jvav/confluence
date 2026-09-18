package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.particle.BladeTrailEmitter;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.common.item.sword.Phasesaber;
import org.jetbrains.annotations.NotNull;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PhasebladeProjectile extends Projectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_WEAPON = SynchedEntityData.defineId(PhasebladeProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Byte> DATA_STATE = SynchedEntityData.defineId(PhasebladeProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(PhasebladeProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_KNOCKBACK = SynchedEntityData.defineId(PhasebladeProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_FALL_YAW = SynchedEntityData.defineId(PhasebladeProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_FALL_ROLL = SynchedEntityData.defineId(PhasebladeProjectile.class, EntityDataSerializers.FLOAT);
    private static final int FORWARD_TICKS = 10;
    private static final int FALL_ALIGNMENT_TICKS = 10;
    private static final int COLLISION_SAMPLES = 7;
    private static final int LOCAL_HIT_COOLDOWN_TICKS = 10;
    private static final BasePhasebladeItem.ProjectileGeometry DEFAULT_GEOMETRY = new BasePhasebladeItem.ProjectileGeometry(-0.125F, 0.0625F, 0.0F, 2.1875F, -0.125F, 0.0625F, 1.5F);
    private static final double RETURN_SPEED = 1.5D;
    private final Map<UUID, Integer> nextHitTicks = new HashMap<>();
    private int stuckTicks;
    private ItemStack clientRenderItem = ItemStack.EMPTY;
    private ParticleEmitter tipEmitter;
    private ParticleEmitter tailEmitter;
    private State observedState = State.FORWARD;
    private int stateTicks;

    public PhasebladeProjectile(EntityType<? extends PhasebladeProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_WEAPON, ItemStack.EMPTY);
        entityData.define(DATA_STATE, (byte) State.FORWARD.ordinal());
        entityData.define(DATA_DAMAGE, 1.0F);
        entityData.define(DATA_KNOCKBACK, 0.0F);
        entityData.define(DATA_FALL_YAW, 0.0F);
        entityData.define(DATA_FALL_ROLL, Mth.PI);
    }

    public void configure(Player owner, ItemStack weapon, float damage, float knockback) {
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - 0.2D, owner.getZ());
        entityData.set(DATA_WEAPON, BasePhasebladeItem.createProjectileStack(weapon));
        entityData.set(DATA_DAMAGE, damage);
        entityData.set(DATA_KNOCKBACK, knockback);
        setState(State.FORWARD);
        setDeltaMovement(owner.getLookAngle().normalize().scale(1.1D));
    }

    @Override
    public ItemStack getItem() {
        return entityData.get(DATA_WEAPON);
    }

    public ItemStack getRenderItem() {
        if (!level().isClientSide) return getItem();
        if (clientRenderItem.isEmpty()) {
            clientRenderItem = BasePhasebladeItem.createProjectileRenderStack(getItem(), getId());
        }
        return clientRenderItem;
    }

    public boolean belongsTo(Player player) {
        return getOwner() != null && getOwner().getUUID().equals(player.getUUID());
    }

    public boolean represents(ItemStack stack) {
        return stack.getItem() instanceof BasePhasebladeItem held && getItem().getItem() instanceof BasePhasebladeItem thrown && held.color() == thrown.color();
    }

    public void recall() {
        if (state() == State.RETURNING) return;
        setState(State.RETURNING);
        noPhysics = true;
        setNoGravity(true);
    }

    public State state() {
        int ordinal = Byte.toUnsignedInt(entityData.get(DATA_STATE));
        return ordinal < State.values().length ? State.values()[ordinal] : State.FORWARD;
    }

    public float fallingYaw() {
        return entityData.get(DATA_FALL_YAW);
    }

    public float fallingAlignment(float partialTick) {
        float progress = Mth.clamp((stateTicks + partialTick) / FALL_ALIGNMENT_TICKS, 0.0F, 1.0F);
        return progress * progress * (3.0F - 2.0F * progress);
    }

    public float fallingRoll(float partialTick) {
        float start = entityData.get(DATA_FALL_ROLL);
        float delta = Mth.wrapDegrees((Mth.PI - start) * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
        return start + delta * fallingAlignment(partialTick);
    }

    private void setState(State state) {
        entityData.set(DATA_STATE, (byte) state.ordinal());
        observedState = state;
        stateTicks = 0;
        noPhysics = state == State.RETURNING;
        setNoGravity(state == State.FORWARD || state == State.RETURNING || state == State.STUCK);
        refreshDimensionsKeepingCenter();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_STATE.equals(key)) {
            observedState = state();
            stateTicks = 0;
        }
        if (DATA_STATE.equals(key) || DATA_WEAPON.equals(key) || DATA_FALL_YAW.equals(key) || DATA_FALL_ROLL.equals(key)) {
            if (DATA_WEAPON.equals(key)) clientRenderItem = ItemStack.EMPTY;
            refreshDimensionsKeepingCenter();
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        if (observedState == null) return EntityDimensions.scalable(0.55F, 0.55F);
        BasePhasebladeItem.ProjectileGeometry geometry = projectileGeometry();
        if (observedState == State.STUCK) {
            return EntityDimensions.scalable(geometry.width(), geometry.length());
        }
        return EntityDimensions.scalable(geometry.length(), geometry.width());
    }

    public BasePhasebladeItem.ProjectileGeometry projectileGeometry() {
        ItemStack stack = getItem();
        return stack.getItem() instanceof BasePhasebladeItem item ? item.projectileGeometry() : DEFAULT_GEOMETRY;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        Vec3 center = getBoundingBox().getCenter();
        double radius = projectileGeometry().length() * 0.5D + 0.25D;
        return new AABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius, center.y + radius, center.z + radius);
    }

    private void refreshDimensionsKeepingCenter() {
        if (observedState == null) return;
        Vec3 center = getBoundingBox().getCenter();
        refreshDimensions();
        setPos(center.x, center.y - getBbHeight() * 0.5D, center.z);
    }

    private void beginFalling() {
        Vec3 motion = getDeltaMovement();
        entityData.set(DATA_FALL_YAW, (float) Math.atan2(motion.z, motion.x) + Mth.PI);
        float pitch = (float) Math.atan2(motion.y, motion.horizontalDistance());
        entityData.set(DATA_FALL_ROLL, pitch + tickCount * 0.8F);
        setState(State.FALLING);
    }

    public float visualYaw() {
        State state = state();
        if (state == State.FALLING || state == State.STUCK) return fallingYaw();
        Vec3 motion = getDeltaMovement();
        return motion.horizontalDistanceSqr() > 1.0E-8D ? (float) Math.atan2(motion.z, motion.x) + Mth.PI : getYRot() * Mth.DEG_TO_RAD;
    }

    public float visualRoll(float partialTick) {
        State state = state();
        if (state == State.STUCK) return Mth.PI;
        if (state == State.FALLING) return fallingRoll(partialTick);
        Vec3 motion = getDeltaMovement();
        float pitch = (float) Math.atan2(motion.y, motion.horizontalDistance());
        return pitch + (tickCount + partialTick) * 0.8F;
    }

    private Vec3 bladeDirection(float partialTick) {
        float roll = visualRoll(partialTick);
        return new Vec3(-Mth.sin(roll), Mth.cos(roll), 0.0D).yRot(-visualYaw());
    }

    private Vec3 collisionCenter() {
        return getBoundingBox().getCenter();
    }

    private ResourceLocation bladeParticle() {
        if (!(getItem().getItem() instanceof BasePhasebladeItem blade)) return null;
        String family = blade instanceof Phasesaber ? "phasesaber" : "phaseblade";
        return Confluence.asResource(blade.color().resourceName() + "_" + family);
    }

    private void tickClientEmitters(State state) {
        if (!level().isClientSide) return;
        ResourceLocation particle = bladeParticle();
        if (state == State.STUCK || particle == null) return;

        Vec3 axis = bladeDirection(0.0F);
        double halfLength = projectileGeometry().length() * 0.5D;
        Vec3 center = collisionCenter();
        Vec3 tip = center.add(axis.scale(halfLength));
        Vec3 tail = center.subtract(axis.scale(halfLength));
        if (tipEmitter == null || tipEmitter.isRemoved())
            tipEmitter = createEmitter(tip, particle, 1.0D);
        if (tailEmitter == null || tailEmitter.isRemoved())
            tailEmitter = createEmitter(tail, particle, -1.0D);
    }

    private ParticleEmitter createEmitter(Vec3 position, ResourceLocation particle, double end) {
        ParticleEmitter emitter = new BladeTrailEmitter(this, position, particle, end);
        MolangParticleEngine.INSTANCE.addEmitter(emitter);
        return emitter;
    }

    private BladeBlockHit findBladeBlockHit() {
        double bladeLength = projectileGeometry().length();
        double halfBladeLength = bladeLength * 0.5D;
        Vec3 center = collisionCenter();
        Vec3 nextCenter = center.add(getDeltaMovement());
        Vec3 currentAxis = bladeDirection(0.0F);
        Vec3 nextAxis = bladeDirection(1.0F);
        BladeBlockHit nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int index = 0; index < COLLISION_SAMPLES; index++) {
            double offset = -halfBladeLength + bladeLength * index / (COLLISION_SAMPLES - 1.0D);
            Vec3 from = center.add(currentAxis.scale(offset));
            Vec3 to = nextCenter.add(nextAxis.scale(offset));
            HitResult hit = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (!(hit instanceof BlockHitResult blockHit) || hit.getType() == HitResult.Type.MISS)
                continue;
            double distance = from.distanceToSqr(blockHit.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = new BladeBlockHit(blockHit, offset);
            }
        }
        return nearest;
    }

    /**
     * 单独检测真正的剑尖。通用七点扫描用于阻止整把剑穿墙，但不能用其中“最近的任意点”
     * 判断插地，否则护手或剑身先处于方块内时会永远错过剑尖落地。
     */
    private BlockHitResult findBladeTipHit() {
        double tipOffset = projectileGeometry().length() * 0.5D;
        Vec3 center = collisionCenter();
        Vec3 currentTip = center.add(bladeDirection(0.0F).scale(tipOffset));
        Vec3 nextTip = center.add(getDeltaMovement()).add(bladeDirection(1.0F).scale(tipOffset));
        HitResult hit = level().clip(new ClipContext(currentTip, nextTip, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        BlockHitResult directHit = hit instanceof BlockHitResult blockHit && hit.getType() != HitResult.Type.MISS ? blockHit : null;
        if (directHit != null && directHit.getDirection() == Direction.UP) return directHit;

        BlockPos pos = BlockPos.containing(nextTip);
        VoxelShape shape = level().getBlockState(pos).getCollisionShape(level(), pos);
        if (shape.isEmpty()) return directHit;
        double surfaceY = pos.getY() + shape.max(Direction.Axis.Y);
        if (nextTip.y > surfaceY + 1.0E-4D || getDeltaMovement().y > 0.0D) return directHit;
        return new BlockHitResult(new Vec3(nextTip.x, surfaceY, nextTip.z), Direction.UP, pos, false);
    }

    private void stickBladeFirst(BlockHitResult result) {
        Vec3 contact = result.getLocation();
        setDeltaMovement(Vec3.ZERO);
        setState(State.STUCK);
        // Entity 坐标是碰撞箱底部。只把实际剑刃长度的 1/3 埋入接触面，
        // 剑柄和余下剑刃均留在地面上方。
        setPos(contact.x, contact.y - projectileGeometry().insertionDepth(), contact.z);
    }

    private void handleBladeBlockHit(BladeBlockHit collision) {
        BlockHitResult result = collision.result();
        Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());

        boolean bladeTip = collision.axisOffset() >= projectileGeometry().length() * 0.325D;
        if (result.getDirection() == net.minecraft.core.Direction.UP && bladeTip && fallingAlignment(0.0F) >= 0.8F) {
            stickBladeFirst(result);
            return;
        }

        Vec3 movement = getDeltaMovement();
        double intoSurface = movement.dot(normal);
        Vec3 reflected = intoSurface < 0.0D ? movement.subtract(normal.scale(intoSurface * 1.25D)) : movement;
        if (state() == State.FORWARD) beginFalling();
        setDeltaMovement(reflected.scale(0.45D));
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        UUID hitIdentity = ProjectileHitRules.dedupeIdentity(target).getUUID();
        return target != getOwner() && tickCount >= nextHitTicks.getOrDefault(hitIdentity, 0) && ProjectileHitRules.canHit(getOwner(), target) && super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) return;
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity livingOwner)) return;
        DamageSource source = damageSources().mobProjectile(this, livingOwner);
        float damage = entityData.get(DATA_DAMAGE) * (state() == State.STUCK ? groundDamageMultiplier() : airDamageMultiplier());
        Entity damageRecipient = ProjectileHitRules.damageRecipient(target);
        int previousInvulnerableTime = damageRecipient instanceof LivingEntity living ? living.invulnerableTime : 0;
        if (damageRecipient instanceof LivingEntity living) living.invulnerableTime = 0;
        boolean hurt;
        try {
            // 泰拉式局部无敌帧不能被 LivingEntity 的全局受伤帧吞掉；每个投掷剑实例
            // 通过 nextHitTicks 独立限制同一目标 10 tick 内只能命中一次。
            hurt = damageRecipient.hurt(source, damage);
        } finally {
            if (damageRecipient instanceof LivingEntity living)
                living.invulnerableTime = previousInvulnerableTime;
        }
        if (hurt) {
            nextHitTicks.put(ProjectileHitRules.dedupeIdentity(target).getUUID(), tickCount + LOCAL_HIT_COOLDOWN_TICKS);
            float knockback = entityData.get(DATA_KNOCKBACK) * (state() == State.STUCK ? groundKnockbackMultiplier() : airKnockbackMultiplier());
            ProjectileHitRules.applyResolvedKnockback(this, target, knockback, knockback * 0.1F);
            livingOwner.setLastHurtMob(target);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (state() == State.RETURNING) return;
        handleBladeBlockHit(new BladeBlockHit(result, 0.0D));
    }

    @Override
    public void tick() {
        Entity owner = getOwner();
        if (!level().isClientSide && (owner == null || owner.isRemoved())) {
            discard();
            return;
        }
        super.tick();


        State state = state();
        if (state != observedState) {
            observedState = state;
            stateTicks = 0;
        } else {
            stateTicks++;
        }
        if (state == State.FORWARD && tickCount >= FORWARD_TICKS) {
            beginFalling();
            state = State.FALLING;
        }
        if (state == State.FORWARD || state == State.FALLING || state == State.RETURNING) {
            refreshDimensionsKeepingCenter();
        }
        tickClientEmitters(state);
        if (state == State.RETURNING && owner instanceof LivingEntity livingOwner) {
            Vec3 offset = livingOwner.getEyePosition().subtract(position());
            if (offset.lengthSqr() <= Mth.square(RETURN_SPEED * 0.75D)) {
                discard();
                return;
            }
            setDeltaMovement(offset.normalize().scale(RETURN_SPEED));
        }

        if (state == State.STUCK) {
            if (++stuckTicks >= 1200) {
                recall();
                return;
            }
            if (!level().isClientSide) {
                AABB damageArea = getBoundingBox().inflate(0.1D);
                for (Entity target : level().getEntities(this, damageArea, this::canHitEntity)) {
                    onHitEntity(new EntityHitResult(target));
                }
            }
            return;
        }

        if (state == State.FORWARD || state == State.FALLING) {
            if (state == State.FALLING && fallingAlignment(0.0F) >= 0.8F) {
                BlockHitResult tipHit = findBladeTipHit();
                if (tipHit != null && tipHit.getDirection() == Direction.UP) {
                    stickBladeFirst(tipHit);
                    return;
                }
            }
            BladeBlockHit bladeHit = findBladeBlockHit();
            if (bladeHit != null) {
                handleBladeBlockHit(bladeHit);
                if (state() == State.STUCK) return;
            }
        }

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS && !PortProjectileImpactEvent.onProjectileImpact(this, hit)) {
            hitTargetOrDeflectSelf(hit);
            state = state();
        }
        Vec3 movement = getDeltaMovement();
        setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);
        if (state == State.FALLING)
            setDeltaMovement(movement.x * 0.98D, movement.y - 0.08D, movement.z * 0.98D);
        updateRotation();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Weapon", getItem().save(new CompoundTag()));
        tag.putByte("State", entityData.get(DATA_STATE));
        tag.putFloat("Damage", entityData.get(DATA_DAMAGE));
        tag.putFloat("Knockback", entityData.get(DATA_KNOCKBACK));
        tag.putFloat("FallYaw", entityData.get(DATA_FALL_YAW));
        tag.putFloat("FallRoll", entityData.get(DATA_FALL_ROLL));
        tag.putInt("StuckTicks", stuckTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ItemStack savedWeapon = ItemStack.of(tag.getCompound("Weapon"));
        entityData.set(DATA_WEAPON, savedWeapon.isEmpty() ? ItemStack.EMPTY : BasePhasebladeItem.createProjectileStack(savedWeapon));
        entityData.set(DATA_DAMAGE, tag.getFloat("Damage"));
        entityData.set(DATA_KNOCKBACK, tag.getFloat("Knockback"));
        entityData.set(DATA_FALL_YAW, tag.getFloat("FallYaw"));
        entityData.set(DATA_FALL_ROLL, tag.getFloat("FallRoll"));
        stuckTicks = tag.getInt("StuckTicks");
        setState(State.values()[Mth.clamp(Byte.toUnsignedInt(tag.getByte("State")), 0, State.values().length - 1)]);
    }

    protected abstract float airDamageMultiplier();

    protected abstract float airKnockbackMultiplier();

    protected float groundDamageMultiplier() {return 0.75F;}

    protected float groundKnockbackMultiplier() {return 0.75F;}

    private record BladeBlockHit(BlockHitResult result, double axisOffset) {}

    public enum State {
        FORWARD,
        FALLING,
        STUCK,
        RETURNING
    }
}
