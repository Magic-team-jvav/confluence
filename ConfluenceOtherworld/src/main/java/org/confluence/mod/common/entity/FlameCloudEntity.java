package org.confluence.mod.common.entity;

import PortLib.extensions.net.minecraft.world.phys.AABB.PortAABBExtension;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.util.TrapDamageHelper;

public class FlameCloudEntity extends Entity {
    private static final String RUNTIME_TAG = "ConfluenceFlameCloudRuntime";
    private static final int RUNTIME_VERSION = 1;
    private static final int MAX_AGE = 40;
    /**
     * 损坏存档必须在点燃或伤害附近实体前销毁。
     */
    private boolean invalidRuntimeState;

    public FlameCloudEntity(EntityType<FlameCloudEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FlameCloudEntity(Level level, double x, double y, double z) {
        super(ModEntities.FLAME_CLOUD.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        if (invalidRuntimeState) {
            if (!level().isClientSide) discard();
            return;
        }
        super.tick();

        if (level().isClientSide) {
            for (int i = 0; i < 20; i++) {
                Vec3 offset = position().offsetRandom(random, 5);
                level().addParticle(ParticleTypes.FLAME, offset.x, offset.y + 2.5, offset.z, 0, 0.1, 0);
            }
        } else {
            for (Entity entity : level().getEntities(this, PortAABBExtension.encapsulatingFullBlocks(blockPosition().offset(-2, -2, -2), blockPosition().offset(2, 2, 2)))) {
                if (!entity.fireImmune() || !entity.isInWaterRainOrBubble()) {
                    int fireTicks = 200;
                    if (entity instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.CHEST).is(VanityArmorItems.DEAD_MANS_SWEATER.get())) {
                        fireTicks /= 2;
                    }
                    entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), fireTicks));
                    float damage = LibUtils.switchByDifficulty(level(), blockPosition(), 8F, 16F, 24F);
                    if (entity instanceof LivingEntity living) {
                        damage = TrapDamageHelper.applyDeadMansSweaterReduction(living, damage);
                    }
                    entity.hurt(damageSources().inFire(), damage);
                }
            }
        }

        if (tickCount > MAX_AGE) discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        invalidRuntimeState = false;
        tickCount = 0;
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            invalidRuntimeState = true;
            return;
        }
        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Age", Tag.TAG_INT)) {
            invalidRuntimeState = true;
            return;
        }
        int savedAge = runtime.getInt("Age");
        if (savedAge < 0 || savedAge > MAX_AGE) {
            invalidRuntimeState = true;
            return;
        }
        tickCount = savedAge;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putInt("Age", tickCount);
        compound.put(RUNTIME_TAG, runtime);
    }
}
