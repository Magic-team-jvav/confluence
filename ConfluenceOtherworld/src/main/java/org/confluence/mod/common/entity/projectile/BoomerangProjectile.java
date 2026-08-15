package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BoomerangProjectile extends Projectile {
    private static final EntityDataAccessor<ItemStack> DATA_WEAPON =
            SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_RETURNING =
            SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_DAMAGE =
            SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.FLOAT);

    private final Set<UUID> hitEntities = new HashSet<>();
    /**
     * 仅用于错开不同回旋镖的视觉摆动相位，不参与服务端运动计算。
     */
    private final int visualRotationOffset = random.nextInt(114514);
    private float backSpeed = 1.5F;
    private int forwardTicks = 15;
    private int penetration = 1;
    private boolean fire;

    public BoomerangProjectile(EntityType<? extends BoomerangProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_WEAPON, ItemStack.EMPTY);
        entityData.define(DATA_RETURNING, false);
        entityData.define(DATA_DAMAGE, 1.0F);
    }

    public void configure(LivingEntity owner, ItemStack weapon, BoomerangItem.Settings settings) {
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - 0.15, owner.getZ());
        entityData.set(DATA_WEAPON, weapon.copyWithCount(1));
        entityData.set(DATA_DAMAGE, settings.damage());
        this.backSpeed = settings.backSpeed();
        this.forwardTicks = settings.forwardTicks();
        this.penetration = settings.penetration();
        this.fire = settings.fire();
        setNoGravity(true);
        if (fire) {
            setSecondsOnFire(4);
        }
    }

    public ItemStack getWeapon() {
        return entityData.get(DATA_WEAPON);
    }

    public boolean belongsTo(Player player) {
        Entity owner = getOwner();
        return owner != null && owner.getUUID().equals(player.getUUID());
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = getOwner();
        return target != owner
                && target.isAlive()
                && !hitEntities.contains(target.getUUID())
                && super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (owner instanceof LivingEntity livingOwner) {
            DamageSource source = damageSources().mobProjectile(this, livingOwner);
            if (target.hurt(source, entityData.get(DATA_DAMAGE))) {
                hitEntities.add(target.getUUID());
                if (target instanceof LivingEntity livingTarget) {
                    Vec3 push = getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(0.35);
                    livingTarget.push(push.x, 0.1, push.z);
                    livingOwner.setLastHurtMob(target);
                }
            }
        }
        if (--penetration <= 0) {
            startReturning();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!isReturning()) {
            playSound(SoundEvents.WOOD_HIT, 0.5F, 1.35F);
        }
        startReturning();
        super.onHitBlock(result);
    }

    @Override
    public void tick() {
        Entity owner = getOwner();
        if (!level().isClientSide && (owner == null || owner.isRemoved())) {
            discard();
            return;
        }
        super.tick();
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS && !PortProjectileImpactEvent.onProjectileImpact(this, hit)) {
            hitTargetOrDeflectSelf(hit);
        }
        if (!isReturning() && tickCount >= forwardTicks) {
            startReturning();
        }
        if (isReturning() && owner instanceof LivingEntity livingOwner) {
            Vec3 target = livingOwner.getEyePosition().subtract(0.0, 0.25, 0.0);
            Vec3 direction = target.subtract(position());
            if (direction.lengthSqr() < Mth.square(backSpeed * 0.75F)) {
                discard();
                return;
            }
            setDeltaMovement(direction.normalize().scale(backSpeed));
            noPhysics = true;
        }
        Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        updateRotation();
    }

    private void startReturning() {
        entityData.set(DATA_RETURNING, true);
        noPhysics = true;
        hitEntities.clear();
    }

    public boolean isReturning() {
        return entityData.get(DATA_RETURNING);
    }

    public int getVisualRotationOffset() {
        return visualRotationOffset;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Weapon", getWeapon().save(new CompoundTag()));
        tag.putBoolean("Returning", isReturning());
        tag.putFloat("Damage", entityData.get(DATA_DAMAGE));
        tag.putFloat("BackSpeed", backSpeed);
        tag.putInt("ForwardTicks", forwardTicks);
        tag.putInt("Penetration", penetration);
        tag.putBoolean("Fire", fire);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DATA_WEAPON, ItemStack.of(tag.getCompound("Weapon")));
        entityData.set(DATA_RETURNING, tag.getBoolean("Returning"));
        entityData.set(DATA_DAMAGE, Math.max(0.0F, tag.getFloat("Damage")));
        backSpeed = Math.max(0.1F, tag.getFloat("BackSpeed"));
        forwardTicks = Math.max(1, tag.getInt("ForwardTicks"));
        penetration = Math.max(1, tag.getInt("Penetration"));
        fire = tag.getBoolean("Fire");
        setNoGravity(true);
        noPhysics = isReturning();
    }
}
