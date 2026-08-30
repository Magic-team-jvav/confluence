package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModSoundEvents;

/// 发现目标后会明显加速追击的孢子僵尸。
///
/// 追击属性的安装与清理由通用陆行怪物统一负责；本类只保留孢子僵尸
/// 自身的数值与声音；模型动作由客户端僵尸动画族统一驱动。
public class SporeZombie extends BaseWarriorMonster {
    public SporeZombie(EntityType<? extends SporeZombie> type, Level level) {
        super(type, level, 0.25, LandAnimationProfile.WALK_RUN, LandSoundProfile.ZOMBIE, 0.8, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 93.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.08)
                .add(Attributes.FOLLOW_RANGE, 60.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.TR_ZOMBIE_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.TR_ZOMBIE_DEATH.get();
    }

}
