package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class TreasureBagItemEntity extends ItemEntity {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(TreasureBagItemEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public TreasureBagItemEntity(EntityType<TreasureBagItemEntity> entityType, Level level) {
        super(entityType, level);
        setGlowingTag(true);
        this.lifespan = 12000;
    }

    public TreasureBagItemEntity(Level level, Vec3 pos, ItemStack itemStack, @Nullable Player player) {
        this(ModEntities.TREASURE_BAG_ITEM_ENTITY.get(), level);
        setPos(pos);
        setDeltaMovement(level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
        setItem(itemStack);
        this.lifespan = itemStack.getEntityLifespan(level);
        if (player != null) {
            setThrower(player);
            setOwner(player);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER, Optional.empty());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypeTags.IS_EXPLOSION) && super.hurt(source, amount);
    }

    public void setOwner(@Nullable Player player) {
        entityData.set(DATA_OWNER, Optional.ofNullable(player == null ? null : player.getUUID()));
    }

    public boolean isOwner(@Nullable Player player) {
        if (player == null) return false;
        Optional<UUID> uuid = entityData.get(DATA_OWNER);
        return uuid.isEmpty() || uuid.get().equals(player.getUUID());
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    protected boolean isMergable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        entityData.get(DATA_OWNER).ifPresent(uuid -> compound.putUUID("ActuallyOwner", uuid));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ActuallyOwner")) {
            entityData.set(DATA_OWNER, Optional.of(compound.getUUID("ActuallyOwner")));
        }
    }

    public static void convert(ItemEntity itemEntity) {
        TreasureBagItemEntity entity = new TreasureBagItemEntity(itemEntity.level(), itemEntity.position(), itemEntity.getItem(), null);
        entity.setPickUpDelay(40);
        entity.setDeltaMovement(itemEntity.getDeltaMovement());
        itemEntity.level().addFreshEntity(entity);
        itemEntity.discard();
    }
}
