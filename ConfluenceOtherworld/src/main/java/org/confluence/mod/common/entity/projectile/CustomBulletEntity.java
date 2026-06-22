package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.GunItems;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.NotNull;
import org.mesdag.portlib.wrapper.common.extensions.IPortEntityExtension;

public class CustomBulletEntity extends BaseBulletEntity implements ItemSupplier, IPortEntityExtension {
    protected static final EntityDataAccessor<ItemStack> GRAVITY_BULLET = SynchedEntityData.defineId(CustomBulletEntity.class, EntityDataSerializers.ITEM_STACK);
    protected float gravity = 0;

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, Level level) {
        super(type, level);
    }

    public CustomBulletEntity(LivingEntity owner, float gravity, ItemStack bullet) {
        this(ModEntities.GRAVITY_BULLET_ENTITY.get(), owner, gravity, bullet);
    }

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, LivingEntity owner, float gravity, ItemStack bullet) {
        super(type, owner, GunItems.EMPTY_BULLET.get().getDefaultInstance());
        this.gravity = gravity;
        this.entityData.set(GRAVITY_BULLET, bullet);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(GRAVITY_BULLET, this.getDefaultItem());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("GravityBullet", 10)) {
            ItemStack itemStack = ItemStack.of(compound.getCompound("GravityBullet"));
            this.setBullet(itemStack.isEmpty() ? getDefaultItem() : itemStack);
        } else {
            this.setBullet(this.getDefaultItem());
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("GravityBullet", this.getBulletStack().save(new CompoundTag()));
    }

    @Override
    public void tick() {
        super.tick();
        this.applyGravity();
    }

    @Override
    public double getDefaultGravity() {
        return gravity;
    }

    @Override
    public ItemStack getItem() {
        return this.entityData.get(GRAVITY_BULLET);
    }
}
