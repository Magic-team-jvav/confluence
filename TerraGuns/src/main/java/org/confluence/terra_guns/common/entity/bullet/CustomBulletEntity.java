package org.confluence.terra_guns.common.entity.bullet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.terra_guns.common.init.TGEntities;
import org.confluence.terra_guns.common.init.TGItems;
import org.jetbrains.annotations.NotNull;

public class CustomBulletEntity extends BaseBulletEntity implements ItemSupplier {
    protected static final EntityDataAccessor<ItemStack> GRAVITY_BULLET = SynchedEntityData.defineId(CustomBulletEntity.class, EntityDataSerializers.ITEM_STACK);
    protected float gravity = 0;

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, Level level) {
        super(type, level);
    }

    public CustomBulletEntity(LivingEntity owner, float gravity, ItemStack bullet) {
        this(TGEntities.GRAVITY_BULLET_ENTITY.get(), owner, gravity, bullet);
    }

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, LivingEntity owner, float gravity, ItemStack bullet) {
        super(type, owner, TGItems.EMPTY_BULLET.toStack());
        this.gravity = gravity;
        this.entityData.set(GRAVITY_BULLET, bullet);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GRAVITY_BULLET, this.getDefaultItem());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("GravityBullet", 10)) {
            this.setBullet(ItemStack.parse(this.registryAccess(), compound.getCompound("GravityBullet")).orElse(this.getDefaultItem()));
        } else {
            this.setBullet(this.getDefaultItem());
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("GravityBullet", this.getBulletStack().save(this.registryAccess()));
    }

    @Override
    public void tick() {
        super.tick();
        this.applyGravity();
    }

    @Override
    protected double getDefaultGravity() {
        return gravity;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.entityData.get(GRAVITY_BULLET);
    }
}
