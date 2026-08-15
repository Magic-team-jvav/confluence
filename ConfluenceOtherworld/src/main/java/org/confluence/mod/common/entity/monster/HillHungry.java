package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModSoundEvents;

public class HillHungry extends TheHungry {
    private static final double HORIZONTAL_LEASH_DISTANCE = 32.0;
    private static final double VERTICAL_LEASH_DISTANCE = 64.0;

    public HillHungry(EntityType<? extends HillHungry> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    /**
     * 肉丘嘴部的饿鬼只负责守卫单个嘴部锚点，活动范围比血肉墙饿鬼短。
     * 参数保留在具体实体中，避免共用父类再次把两种 Boss 的运动语义合并。
     */
    @Override
    protected double horizontalLeashDistance() {
        return HORIZONTAL_LEASH_DISTANCE;
    }

    @Override
    protected double verticalLeashDistance() {
        return VERTICAL_LEASH_DISTANCE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.THE_HUNGRY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.THE_HUNGRY_DEATH.get();
    }
}
