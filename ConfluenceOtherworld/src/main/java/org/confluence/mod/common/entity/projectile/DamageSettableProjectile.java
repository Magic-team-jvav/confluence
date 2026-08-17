package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public abstract class DamageSettableProjectile extends Projectile {
    protected static final EntityDataAccessor<Float> DATA_DEFAULT_VELOCITY = SynchedEntityData.defineId(DamageSettableProjectile.class, EntityDataSerializers.FLOAT);
    protected float damage;

    public DamageSettableProjectile(EntityType<? extends DamageSettableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_DEFAULT_VELOCITY, 0.0F);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getCalculatedDamage() {
        if (!(getOwner() instanceof LivingEntity living)) return damage;
        ItemStack itemStack = living.getMainHandItem();
        if (itemStack.isEmpty()) return damage;
        PrefixComponent component = itemStack.get(ModDataComponentTypes.PREFIX);
        if (component == null) return damage;
        double d0 = damage;
        for (AttributeModifier modifier : component.modifiers().get().get(LibAttributes.getAttackDamage().get())) {
            if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                d0 += modifier.getAmount();
            }
            double d1 = d0;
            if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                d1 += d0 * modifier.getAmount();
            }
            if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                d1 *= 1.0 + modifier.getAmount();
            }
            d0 = d1;
        }
        return (float) LibAttributes.getAttackDamage().get().sanitizeValue(d0);
    }

    public float getDamage() {
        return damage;
    }

    public @Nullable LivingEntity getLivingOwner() {
        return getOwner() instanceof LivingEntity living ? living : null;
    }

    public DamageSource getDamageSource() {
        return damageSources().mobProjectile(this, getLivingOwner());
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return LibEntityUtils.canHitEntity(target, getOwner());
    }

    public void setDefaultVelocity(float defaultVelocity) {
        entityData.set(DATA_DEFAULT_VELOCITY, defaultVelocity);
    }

    public float getDefaultVelocity() {
        return entityData.get(DATA_DEFAULT_VELOCITY);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        setDefaultVelocity(velocity);
        super.shoot(x, y, z, velocity, inaccuracy);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.damage = compound.getFloat("Damage");
        setDefaultVelocity(compound.getFloat("DefaultVelocity"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", damage);
        compound.putFloat("DefaultVelocity", getDefaultVelocity());
    }
}
