package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ThrowableDropSelfProjectile extends DamageSettableProjectile {
    protected static final EntityDataAccessor<Integer> DATA_FLY_TICKS = SynchedEntityData.defineId(ThrowableDropSelfProjectile.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(ThrowableDropSelfProjectile.class, EntityDataSerializers.ITEM_STACK);
    protected int penetrate;
    protected float deltaDamage;
    protected final Set<UUID> hitSet = new HashSet<>();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder.define(DATA_FLY_TICKS, 5).define(DATA_ITEM_STACK, ItemStack.EMPTY));
    }

    public ThrowableDropSelfProjectile(EntityType<? extends ThrowableDropSelfProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setOwner(@Nullable Entity player) {
        super.setOwner(player);
        if (player != null) {
            setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        }
    }

    public void setItem(ItemStack drop) {
        entityData.set(DATA_ITEM_STACK, drop);
    }

    public ItemStack getItem() {
        return entityData.get(DATA_ITEM_STACK);
    }

    public void setFlyTicks(int ticks) {
        entityData.set(DATA_FLY_TICKS, ticks);
    }

    public int getFlyTicks() {
        return entityData.get(DATA_FLY_TICKS);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity.hurt(getDamageSource(), getCalculatedDamage())) {
            hitSet.add(entity.getUUID());
            this.damage -= deltaDamage;
            VectorUtils.knockBackA2B(this, entity, 0.5, 0.2);
            if (penetrate >= 3) {
                if (random.nextBoolean()) {
                    LibUtils.createItemEntity(getItem(), getX(), getY(), getZ(), level(), 0);
                }
                discard();
            } else {
                ++this.penetrate;
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (random.nextBoolean()) {
            LibUtils.createItemEntity(getItem(), getX(), getY(), getZ(), level(), 0);
        }
        discard();
    }

    @Override
    public void setDamage(float damage) {
        this.damage = damage;
        this.deltaDamage = damage * 0.1F;
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !hitSet.contains(target.getUUID());
    }

    @Override
    protected void applyGravity() {
        if (tickCount > getFlyTicks()) {
            super.applyGravity();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }

        d0 *= 64.0;
        return distance < d0 * d0;
    }

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitresult)) {
            hitTargetOrDeflectSelf(hitresult);
        }

        checkInsideBlocks();
        Vec3 vec3 = getDeltaMovement();
        double d0 = getX() + vec3.x;
        double d1 = getY() + vec3.y;
        double d2 = getZ() + vec3.z;
        updateRotation();
        float f;
        if (isInWater()) {
            for (int i = 0; i < 4; i++) {
                level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25, d1 - vec3.y * 0.25, d2 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
        } else {
            f = 0.99F;
        }

        setDeltaMovement(vec3.scale(f));
        applyGravity();
        setPos(d0, d1, d2);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.08;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Item", getItem().save(registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Item", 10)) {
            setItem(ItemStack.parse(registryAccess(), compound.getCompound("Item")).orElse(ItemStack.EMPTY));
        } else {
            setItem(ItemStack.EMPTY);
        }
    }
}
