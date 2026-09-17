package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.GunItems;

public final class Werewolf extends BaseWarriorMonster {
    public Werewolf(EntityType<? extends Werewolf> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE, 1.2, true, DoorBehavior.OPEN);
    }

    @Override
    protected JumpProfile jumpProfile() {
        return new JumpProfile(4.0, 1.3, 40, 4);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean damaged = super.doHurtTarget(target);
        if (damaged && !level().isClientSide && target instanceof LivingEntity living && random.nextInt(8) == 0) {
            int duration = LibUtils.isMaster(level(), blockPosition()) ? 2250 : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 1800 : 900;
            living.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), duration), this);
        }
        return damaged;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof BaseBulletEntity bullet && bullet.getBulletStack().is(GunItems.SILVER_BULLET.get())) {
            amount *= 3.0F;
        }
        return super.hurt(source, amount);
    }

}
