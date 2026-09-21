package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.data.map.ImmunityDataMap;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.mixed.Immunity;

/// 养蜂人剑与蜂巢球共用的本体蜜蜂；飞行、穿透和无敌帧不借用剑气或饰品弹幕。
public class BeeKeeperProjectile extends Projectile implements Immunity {
    private static final EntityDataAccessor<Boolean> GIANT = SynchedEntityData.defineId(BeeKeeperProjectile.class, EntityDataSerializers.BOOLEAN);
    private float damage;
    private float criticalChance;
    private float knockback;
    private int remainingHits = 3;

    public BeeKeeperProjectile(EntityType<? extends BeeKeeperProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 固定发射时的伤害、暴击和击退，换武器不改变已经飞出的蜜蜂。
    public void configure(LivingEntity owner, float damage, float criticalChance, float weaponKnockback, boolean giant) {
        setOwner(owner);
        entityData.set(GIANT, giant);
        this.damage = damage + (giant ? 1 + random.nextInt(3) : random.nextBoolean() ? 1 : 0);
        this.criticalChance = criticalChance;
        this.knockback = giant ? 0.5F / 6 + 1.1F * weaponKnockback : 0;
        remainingHits = giant ? 4 : 3;
        shoot(random.nextGaussian(), 0.5 + random.nextDouble(), random.nextGaussian(), 0.45F, 0);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(GIANT, false);
    }

    public boolean isGiant() {return entityData.get(GIANT);}

    public float getCriticalChance() {return criticalChance;}

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = getType().getDimensions();
        return isGiant() ? dimensions.scale(1.5F) : dimensions;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (data == GIANT) refreshDimensions();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            setPos(position().add(getDeltaMovement()));
            updateRotation();
            return;
        }
        Entity owner = getOwner();
        if (owner == null || !owner.isAlive() || isInWater() || isInLava() || tickCount >= (isGiant() ? 220 : 200)) {
            discard();
            return;
        }
        if (tickCount >= 10) trackTarget();
        Vec3 from = position();
        Vec3 motion = getDeltaMovement();
        move(MoverType.SELF, motion);
        Vec3 end = position();
        Vec3 moved = end.subtract(from);
        boolean hitX = Math.abs(moved.x - motion.x) > 1.0E-7;
        boolean hitY = Math.abs(moved.y - motion.y) > 1.0E-7;
        boolean hitZ = Math.abs(moved.z - motion.z) > 1.0E-7;

        /// 检查实际轨迹，避免高速穿过一格水或熔岩而没有消失。
        BlockHitResult fluidHit = level().clip(new ClipContext(from, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this));
        if (fluidHit.getType() != HitResult.Type.MISS) {
            var fluid = level().getFluidState(fluidHit.getBlockPos());
            if (fluid.is(FluidTags.WATER) || fluid.is(FluidTags.LAVA)) {
                discard();
                return;
            }
        }
        for (Entity candidate : level().getEntities(this, new AABB(from, end).inflate(getBbWidth() * 0.5), this::canHitEntity)) {
            AABB box = candidate.getBoundingBox().inflate(getBbWidth() * 0.5);
            if (!box.contains(from) && box.clip(from, end).isEmpty()) continue;
            hurtTarget(candidate);
            if (isRemoved()) return;
        }
        /// 多个碰撞轴独立反转，不能在拐角用后一轴的计算覆盖前一轴。
        if (hitX || hitY || hitZ) {
            setDeltaMovement(new Vec3(hitX ? -motion.x : motion.x, hitY ? -motion.y : motion.y, hitZ ? -motion.z : motion.z));
            hasImpulse = true;
            consumeImpact();
        } else setDeltaMovement(motion);
        checkInsideBlocks();
        updateRotation();
    }

    private void trackTarget() {
        Entity target = null;
        double nearest = 50;
        /// 二维曼哈顿距离按三维移植；持续重选最近敌人，而不是永久锁定第一个目标。
        for (Entity candidate : level().getEntities(this, getBoundingBox().inflate(50), this::canHitEntity)) {
            Entity logical = ProjectileHitRules.encounterOwner(candidate);
            if (!(logical instanceof Enemy)) continue;
            Vec3 offset = candidate.getBoundingBox().getCenter().subtract(position());
            double distance = Math.abs(offset.x) + Math.abs(offset.y) + Math.abs(offset.z);
            if (distance < nearest) {
                nearest = distance;
                target = candidate;
            }
        }
        if (target == null) return;
        double speed = (isGiant() ? 25.5 : 22.5) / 20;
        double acceleration = (isGiant() ? 31.5 : 22.5) / 400;
        Vec3 desired = target.getBoundingBox().getCenter().subtract(position()).normalize().scale(speed);
        Vec3 motion = getDeltaMovement();
        Vec3 steered = new Vec3(steerAxis(motion.x, desired.x, acceleration),
                steerAxis(motion.y, desired.y, acceleration), steerAxis(motion.z, desired.z, acceleration));
        setDeltaMovement(steered.lengthSqr() > speed * speed ? steered.normalize().scale(speed) : steered);
    }

    /// 反向飞行时三倍加速度转向，到达期望速度后不再累加。
    private static double steerAxis(double current, double desired, double acceleration) {
        double step = current * desired < 0 ? acceleration * 3 : acceleration;
        if (Math.abs(desired - current) <= step) return desired;
        return current + Math.copySign(step, desired - current);
    }

    private void hurtTarget(Entity candidate) {
        LivingEntity logical = ProjectileHitRules.logicalLivingTarget(candidate);
        if (logical == null) return;
        DamageSource source = LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, getOwner());
        /// 静态配置使用实体类型作为键，普通蜂与巨蜂共享，不能每只蜂各算一份。
        Immunity cause = confluence$getImmunityType() == Type.STATIC ? (Immunity) getType() : this;
        if (!Immunity.hurt(cause, ProjectileHitRules.damageRecipient(candidate), logical, source, damage))
            return;
        if (knockback > 0) {
            Vec3 motion = getDeltaMovement();
            logical.knockback(knockback, -motion.x, -motion.z);
        }
        consumeImpact();
    }

    /// 命中与撞墙共用次数，普通蜂三次、巨蜂四次。
    private void consumeImpact() {
        if (--remainingHits == 0) discard();
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return remainingHits > 0 && target.isAlive() && !target.isSpectator() && ProjectileHitRules.canHit(getOwner(), target);
    }

    @Override
    public Type confluence$getImmunityType() {
        return ImmunityDataMap.getImmunityType(this);
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {
        return ImmunityDataMap.getImmunityDuration(this, source, ignored -> 4);
    }

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    public boolean canChangeDimensions() {return false;}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return NetworkHooks.getEntitySpawningPacket(this);}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {super.readAdditionalSaveData(tag);}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {super.addAdditionalSaveData(tag);}
}
