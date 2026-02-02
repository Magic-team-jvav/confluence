package org.confluence.terra_guns.common.entity.bullet;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
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
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.util.VectorUtils;
import org.confluence.terra_guns.api.event.BulletEvent;
import org.confluence.terra_guns.common.init.TGDamageTypes;
import org.confluence.terra_guns.common.init.TGEntities;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terra_guns.common.item.bullet.BaseBullet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BaseBulletEntity extends Projectile {
    private static final EntityDataAccessor<String> COLOR_ID = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<ItemStack> BULLET = SynchedEntityData.defineId(BaseBulletEntity.class, EntityDataSerializers.ITEM_STACK);
    public float damage;
    public float knockback;
    public int hitBlockTimes;
    public int penetrate;
    private final List<Vec3> trails = new ArrayList<>();
    public double accelerationPower;

    public BaseBulletEntity(EntityType<? extends BaseBulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseBulletEntity(EntityType<? extends Projectile> entityType, Level level, double x, double y, double z, ItemStack bullet) {
        super(entityType, level);
        this.setPos(x, y, z);
        this.entityData.set(BULLET, bullet.is(Items.AIR) || bullet.isEmpty() ? getDefaultItem() : bullet);
        this.accelerationPower = 0.1;
    }

    public BaseBulletEntity(Level level, double x, double y, double z, ItemStack bullet) {
        this(TGEntities.BASE_BULLET_ENTITY.get(), level, x, y, z, bullet);
    }

    public BaseBulletEntity(EntityType<? extends Projectile> entityType, LivingEntity owner, ItemStack bullet) {
        this(entityType, owner.level(), owner.getX(), owner.getEyeY() - 0.1, owner.getZ(), bullet);
        setOwner(owner);
    }

    public BaseBulletEntity(LivingEntity owner, ItemStack bullet) {
        this(TGEntities.BASE_BULLET_ENTITY.get(), owner, bullet);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return distance < d0 * d0;
    }

    protected ClipContext.Block getClipType() {
        return ClipContext.Block.COLLIDER;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return !this.isInvulnerableTo(source);
    }

    public String getColorID() {
        if (!this.entityData.get(COLOR_ID).isEmpty()) {
            return this.entityData.get(COLOR_ID);
        } else if (!this.getBullet().colorID().isEmpty()) {
            return this.getBullet().colorID();
        }
        return BuiltInRegistries.ITEM.getKey(this.getBullet()).getPath();
    }

    public void setColorID(String colorID) {
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

    public BaseBullet getBullet() {
        Item item = this.getEntityData().get(BULLET).getItem();
        if (item instanceof BaseBullet bullet) {
            return bullet;
        }
        return (BaseBullet) getDefaultItem().getItem();
    }

    public DamageSource getDamageSource() {
        return TGDamageTypes.of(level(), TGDamageTypes.BULLET_DAMAGE, this, getOwner());
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        Vec3 vec3 = serverEntity.getPositionBase();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), vec3.x(), vec3.y(), vec3.z(), serverEntity.getLastSentXRot(), serverEntity.getLastSentYRot(), this.getType(), i, serverEntity.getLastSentMovement(), 0.0F);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Vec3 vec3 = new Vec3(packet.getXa(), packet.getYa(), packet.getZa());
        this.setDeltaMovement(vec3);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOR_ID, "");
        builder.define(BULLET, this.getDefaultItem());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("ColorID", CompoundTag.TAG_STRING)) {
            this.setColorID(compound.getString("ColorID"));
        }
        if (compound.contains("Item", 10)) {
            this.setBullet(ItemStack.parse(this.registryAccess(), compound.getCompound("Item")).orElse(this.getDefaultItem()));
        } else {
            this.setBullet(this.getDefaultItem());
        }
        if (compound.contains("Damage", CompoundTag.TAG_FLOAT)) {
            this.damage = compound.getFloat("Damage");
        }
        if (compound.contains("Knockback", CompoundTag.TAG_FLOAT)) {
            this.knockback = compound.getFloat("Knockback");
        }
        if (compound.contains("Penetrate", CompoundTag.TAG_INT)) {
            this.penetrate = compound.getInt("Penetrate");
        }
        if (compound.contains("HitBlockTime", CompoundTag.TAG_INT)) {
            this.hitBlockTimes = compound.getInt("HitBlockTime");
        }
        if (compound.contains("acceleration_power", CompoundTag.TAG_DOUBLE)) {
            this.accelerationPower = compound.getDouble("acceleration_power");
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putString("ColorID", this.getColorID());
        if (!getBulletStack().isEmpty()) {
            compound.put("Item", this.getBulletStack().save(this.registryAccess()));
        }
        compound.putFloat("Damage", this.damage);
        compound.putFloat("Knockback", this.knockback);
        compound.putInt("Penetrate", this.penetrate);
        compound.putInt("HitBlockTime", this.hitBlockTimes);
        compound.putDouble("acceleration_power", this.accelerationPower);
    }

    protected ItemStack getDefaultItem() {
        return TGItems.EMPTY_BULLET.toStack();
    }

    @Override
    public void tick() {
        NeoForge.EVENT_BUS.post(new BulletEvent.Tick.Pre(this, this.getBullet()));
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition()) && disToOwner() <= 256) {
            super.tick();

            this.getBullet().tick(this);
            this.saveTrailPos();

            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, this.getClipType());
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                this.onHitBlock((BlockHitResult) hitresult);
            }


            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double newX = this.getX() + vec3.x;
            double newY = this.getY() + vec3.y;
            double newZ = this.getZ() + vec3.z;
            this.setPos(newX, newY, newZ);
            float inertia = this.getInertia();
            this.setDeltaMovement(vec3.add(vec3.normalize().scale(this.accelerationPower)).scale(inertia));
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);

            AABB aabb = new AABB(getX(), getY(), getZ(), xo, yo, zo);
            this.level().getEntities(this, aabb, this::canHitEntity).forEach(hitEntity -> onHitEntity(new EntityHitResult(hitEntity)));
        } else {
            this.discard();
        }
        NeoForge.EVENT_BUS.post(new BulletEvent.Tick.Post(this, this.getBullet()));
    }

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
        if (getOwner() == null) return 256;
        return this.position().distanceTo(getOwner().position());
    }

    private void saveTrailPos() {
        if (this.level().isClientSide) {
            Vec3 currentPos = this.position();

            if (trails.isEmpty()) {
                trails.addLast(currentPos);
            }

            Vec3 lastPos = trails.getLast();
            double dist = lastPos.distanceTo(currentPos);

            double spacing = 0.4;
            if (dist > spacing) {
                int steps = Mth.floor(dist / spacing);
                Vec3 delta = currentPos.subtract(lastPos).scale(1.0 / steps);
                for (int i = 1; i <= steps; i++) {
                    trails.addLast(lastPos.add(delta.scale(i)));
                }
            } else {
                trails.addLast(currentPos);
            }

            while (trails.size() > 20) {
                trails.removeFirst();
            }
        }
    }


    public List<Vec3> getTrails() {
        return trails;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.canBeHitByProjectile()) {
            return false;
        } else {
            Entity entity = this.getOwner();
            return entity == null || !entity.isPassengerOfSameVehicle(target);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (NeoForge.EVENT_BUS.post(new BulletEvent.HitEvent.Entity(this, this.getBullet(), result)).isCanceled())
            return;

        Entity hit = result.getEntity();
        Entity shooter = this.getOwner();

        if (!level().isClientSide && hit != shooter && !this.isRemoved()) {
            BulletEvent.DamageEntityEvent damageEntityEvent = new BulletEvent.DamageEntityEvent(this, this.getBullet(), shooter, hit);
            NeoForge.EVENT_BUS.post(damageEntityEvent);

            this.getBullet().onHitEntity(this, result);
            if (this.knockback > 0) {
                BulletEvent.KnockbackEvent knockbackEvent = new BulletEvent.KnockbackEvent(this, this.getBullet(), knockback / 8, 0f);
                NeoForge.EVENT_BUS.post(knockbackEvent);

                VectorUtils.knockBackA2B(this, hit, knockbackEvent.getScale(), knockbackEvent.getMotionY());
            }

            BulletEvent.PenetrateEvent penetrateEvent = new BulletEvent.PenetrateEvent(this, this.getBullet(), penetrate);
            NeoForge.EVENT_BUS.post(penetrateEvent);
            int penetrate = penetrateEvent.getPenetrate();

            if (penetrate == -1) {
                return;
            } else if (penetrate == 0) {
                this.discard();
                return;
            }
            this.penetrate--;
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (NeoForge.EVENT_BUS.post(new BulletEvent.HitEvent.Block(this, this.getBullet(), result)).isCanceled())
            return;

        super.onHitBlock(result);
        this.getBullet().onHitBlock(this, result);

        this.hitBlockTimes++;
    }
}
