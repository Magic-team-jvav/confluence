package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class MoneyHoleEntity extends Entity {
    private int age;
    private ParticleEmitter emitter;

    public MoneyHoleEntity(EntityType<MoneyHoleEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.age = 0;
    }

    public MoneyHoleEntity(Level level, Vec3 position) {
        super(ModEntities.MONEY_HOLE.get(), level);
        setNoGravity(true);
        setPos(position);
        setDeltaMovement(0.0, 0.5, 0.0);
        this.age = 0;
    }

    @Override
    public void tick() {
        if (level().isClientSide && emitter == null) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("money_hole"));
            emitter.attached = this;
            PSGameClient.LOADER.addEmitter(emitter, false);
        }
        setDeltaMovement(getDeltaMovement().scale(0.96));
        move(MoverType.SELF, getDeltaMovement());
        if (!level().isClientSide && age > 20 && age % 10 == 0) {
            ModUtils.dropMoney(random.nextInt(99, 9802), getX(), getY(), getZ(), level());
        }
        if (this.age++ > 200) discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {}
}
