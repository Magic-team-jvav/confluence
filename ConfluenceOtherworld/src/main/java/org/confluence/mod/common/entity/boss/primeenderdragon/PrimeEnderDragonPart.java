package org.confluence.mod.common.entity.boss.primeenderdragon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.api.entity.ICollisionAttackEntity;
import org.confluence.terraentity.api.entity.IMovablePartEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PrimeEnderDragonPart extends PartEntity<PrimeEnderDragon> implements ICollisionAttackEntity, IMovablePartEntity {
    PrimeEnderDragon parentMob;
    private final EntityDimensions size;
    public PrimeEnderDragonPart(PrimeEnderDragon parent, float width, float height) {
        super(parent);
        this.size = EntityDimensions.scalable(width, height);
        this.parentMob = parent;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public CollisionProperties getCollisionProperties() {
        return null;
    }

    @Override
    public boolean shouldDoCollision() {
        return true;
    }

    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.parentMob.hurt(this, source, amount);
    }

    @Override
    public boolean is(@NotNull Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    void tickPart(double offsetX, double offsetY, double offsetZ) {
        this.setPos(this.parentMob.getX() + offsetX, this.parentMob.getY() + offsetY, this.parentMob.getZ() + offsetZ);
    }
}
