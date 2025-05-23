package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ThrowableDropSelfProjectile extends DamageSettableProjectile {
    private static final EntityDataAccessor<Integer> DATA_FLY_TICKS = SynchedEntityData.defineId(ThrowableDropSelfProjectile.class, EntityDataSerializers.INT);
    private int penetrate = 0;
    private @NotNull ItemStack itemStack = ItemStack.EMPTY;
    private float damage = 4.2F;
    private float deltaDamage = damage * 0.1F;
    private final List<Entity> hitList = new ArrayList<>();

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FLY_TICKS, 5);
    }

    public ThrowableDropSelfProjectile(EntityType<? extends ThrowableDropSelfProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void setOwner(@Nullable Entity player) {
        super.setOwner(player);
        if (player != null) {
            setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        }
    }

    public void setItem(@NotNull ItemStack drop) {
        this.itemStack = drop;
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public void setFlyTicks(int ticks) {
        this.entityData.set(DATA_FLY_TICKS, ticks);
    }

    public int getFlyTicks() {
        return this.entityData.get(DATA_FLY_TICKS);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        if (canHitEntity(entity)) {
            hitList.add(entity);
            if (entity.hurt(getDamageSource(), damage)) {
                this.damage -= deltaDamage;
                VectorUtils.knockBackA2B(this, entity, 0.5, 0.2);
                if (penetrate == 3) {
                    if (random.nextBoolean()) {
                        LibUtils.createItemEntity(itemStack, getX(), getY(), getZ(), level(), 0);
                    }
                    discard();
                } else {
                    penetrate++;
                }
            }
        }
    }

    protected DamageSource getDamageSource() {
        return damageSources().mobProjectile(this, (LivingEntity) getOwner());
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (itemStack != null && random.nextBoolean()) {
            LibUtils.createItemEntity(itemStack, getX(), getY(), getZ(), level(), 0);
        }
        discard();
    }

    @Override
    public void setDamage(float damage) {
        this.damage = damage;
        this.deltaDamage = damage * 0.1F;
    }

    protected boolean canHitEntity(Entity target) {
        return ModUtils.canHitEntity(target, getOwner()) && !hitList.contains(target);
    }

    @Override
    protected void applyGravity() {
        if (tickCount > getFlyTicks()) {
            super.applyGravity();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.08;
    }
}
