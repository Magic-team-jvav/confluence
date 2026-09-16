package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.monster.slime.WinglessSlimer;
import org.confluence.mod.common.init.entity.MonsterEntities;

/// 困难模式飞行史莱姆，飞行形态死亡后失去翅膀并转为地面形态。
///
/// 翅膀状态决定移动能力与分裂时机，不能仅作为客户端外观状态处理。
public class Slimer extends SimpleFlyMonster {
    public Slimer(EntityType<? extends Slimer> type, Level level) {
        super(type, level, DashProfile.standard(0.65), 0.2, true);
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && releaseWinglessForm()) return;
        super.die(source);
    }

    private boolean releaseWinglessForm() {
        WinglessSlimer wingless = MonsterEntities.WINGLESS_SLIMER.get().create(level());
        if (wingless == null) {
            return false;
        }
        wingless.copyPosition(this);
        wingless.setHealth(wingless.getMaxHealth());
        wingless.setNoAi(isNoAi());
        wingless.setInvulnerable(isInvulnerable());
        if (isPersistenceRequired()) wingless.setPersistenceRequired();
        LivingEntity target = getTarget();
        if (target != null) {
            wingless.setTarget(target);
            BossMinionCoordinator.faceTargetImmediately(wingless, target);
        }
        if (hasCustomName()) {
            wingless.setCustomName(getCustomName());
            wingless.setCustomNameVisible(isCustomNameVisible());
        }
        if (level().addFreshEntity(wingless)) {
            discard();
            return true;
        } else {
            wingless.discard();
            return false;
        }
    }
}
