package org.confluence.mod.common.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModEntities;

public class FlameCloudEntity extends Entity {
    public FlameCloudEntity(EntityType<FlameCloudEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FlameCloudEntity(Level level, double x, double y, double z) {
        super(ModEntities.FLAME_CLOUD.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            for (int i = 0; i < 20; i++) {
                Vec3 offset = position().offsetRandom(random, 5);
                level().addParticle(ParticleTypes.FLAME, offset.x, offset.y + 2.5, offset.z, 0, 0.1, 0);
            }
        } else {
            for (Entity entity : level().getEntities(this, AABB.encapsulatingFullBlocks(blockPosition().offset(-2, -2, -2), blockPosition().offset(2, 2, 2)))) {
                if (!entity.fireImmune() || !entity.isInWaterRainOrBubble()) {
                    entity.setRemainingFireTicks(200);
                    entity.hurt(damageSources().inFire(), LibUtils.switchByDifficulty(level(), blockPosition(), 8F, 16F, 24F));
                }
            }
        }

        if (tickCount > 40) discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.tickCount = compound.getInt("Age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", tickCount);
    }
}
