package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.entity.projectile.PaladinHammerProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;

/// 以旋转重锤进行远程攻击的地牢圣骑士。
public final class Paladin extends RangedMonster {
    public Paladin(EntityType<? extends Paladin> type, Level level) {
        super(type, level, 50, 1.1);
    }

    /// 圣骑士不会隔着地牢墙体直接取得新目标；已进入战斗后的短暂记忆仍由原版目标行为维护。
    @Override
    protected boolean mustSeePlayerTarget() {
        return true;
    }

    @Override
    protected Projectile createProjectile(LivingEntity target) {
        PaladinHammerProjectile projectile = ModEntities.PALADIN_HAMMER_PROJECTILE.get().create(level());
        if (projectile == null) {
            return null;
        }
        projectile.configure(this, target, (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) * shotMultiplier()));
        return projectile;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.METAL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

}
