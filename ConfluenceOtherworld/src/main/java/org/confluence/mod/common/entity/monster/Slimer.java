package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.monster.slime.Slimeling;
import org.confluence.mod.common.init.entity.MonsterEntities;

/// 困难模式飞行史莱姆，脆弱的翅膀受击破坏后会释放地面小史莱姆。
///
/// <p>翅膀状态决定移动能力与分裂时机，不能仅作为客户端外观状态处理。</p>
public class Slimer extends SimpleFlyMonster {
    public Slimer(EntityType<? extends Slimer> type, Level level) {
        super(type, level, 0.65, 0.2);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide && isAlive()) {
            releaseSlimeling();
        }
        return damaged;
    }

    private void releaseSlimeling() {
        Slimeling slimeling = MonsterEntities.SLIMELING.get().create(level());
        if (slimeling == null) {
            return;
        }
        slimeling.copyPosition(this);
        slimeling.setHealth(Math.min(slimeling.getMaxHealth(), Math.max(1.0F, getHealth())));
        LivingEntity target = getTarget();
        if (target != null) {
            slimeling.setTarget(target);
        }
        if (hasCustomName()) {
            slimeling.setCustomName(getCustomName());
            slimeling.setCustomNameVisible(isCustomNameVisible());
        }
        if (level().addFreshEntity(slimeling)) {
            discard();
        }
    }
}
