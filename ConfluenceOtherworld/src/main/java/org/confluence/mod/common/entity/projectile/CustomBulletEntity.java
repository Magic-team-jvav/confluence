package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.wrapper.common.extensions.IPortEntityExtension;

public class CustomBulletEntity extends BaseBulletEntity implements ItemSupplier, IPortEntityExtension {
    private float gravity;

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, Level level) {
        super(type, level);
    }

    public CustomBulletEntity(LivingEntity owner, float gravity, ItemStack bullet) {
        this(ModEntities.GRAVITY_BULLET_ENTITY.get(), owner, gravity, bullet);
    }

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, LivingEntity owner, float gravity, ItemStack bullet) {
        super(type, owner, bullet);
        this.gravity = gravity;
    }

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, Level level, double x, double y, double z, ItemStack bullet, float gravity) {
        super(type, level, x, y, z, bullet);
        this.gravity = gravity;
    }

    public float getBulletGravity() {
        return gravity;
    }

    @Override
    protected void applyForces() {
        applyGravity();
    }

    @Override
    public double getDefaultGravity() {
        return gravity;
    }

    @Override
    public ItemStack getItem() {
        return getBulletStack();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        gravity = tag.getFloat("Gravity");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Gravity", gravity);
    }
}
