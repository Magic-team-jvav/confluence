package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;

// 必须注册新的实体类型才能在客户端使用类型推断
public class HillHungry extends TheHungry {
    public int index = -1;

    public HillHungry(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        this.needLastPos = true;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("mouth_index")) {
            index = tag.getInt("mouth_index");
            if (this.minion_getOwnerUUID() != null && index >= 0) {
                if (((ServerLevel) this.level()).getEntity(this.minion_getOwnerUUID()) instanceof HillOfFlesh hill) {
                    hill.loadTheHungry(this.index, this);
                }
            } else {
                this.discard();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("mouth_index", index);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.THE_HUNGRY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.THE_HUNGRY_DEATH.get();
    }
}
