package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.projectile.bomb.BaseGrenadeEntity;

import java.util.EnumSet;

/**
 * 爆破专家投掷手榴弹。
 */
public class NPCGrenadeGoal extends Goal {
    private final BaseNPC npc;
    private int attackTimer;
    private int seeTime;
    private final float attackRangeSqr;

    public NPCGrenadeGoal(BaseNPC npc, float attackRange) {
        this.npc = npc;
        this.attackRangeSqr = attackRange * attackRange;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = npc.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() || !npc.getNavigation().isDone();
    }

    @Override
    public void stop() {
        seeTime = 0;
        attackTimer = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = npc.getTarget();
        if (target == null) return;

        boolean canSee = npc.getSensing().hasLineOfSight(target);
        if (canSee) seeTime++; else seeTime = 0;

        npc.getLookControl().setLookAt(target, 30, 30);
        double distSqr = npc.distanceToSqr(target);

        if (distSqr > attackRangeSqr) {
            npc.getNavigation().moveTo(target, 0.8);
        } else {
            npc.getNavigation().stop();
        }

        if (--attackTimer <= 0 && seeTime >= 5) {
            attackTimer = 40;
            performAttack(target);
        }
    }

    private void performAttack(LivingEntity target) {
        BaseGrenadeEntity grenade = new BaseGrenadeEntity(npc);
        double dx = target.getX() - npc.getX();
        double dy = target.getY(0.333) - grenade.getY();
        double dz = target.getZ() - npc.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        grenade.shoot(dx, dy + dist * 0.2, dz, 1.2f, 1.0f);
        npc.playSound(SoundEvents.SNOWBALL_THROW, 1.0f, 1.0f / (npc.getRandom().nextFloat() * 0.4f + 0.8f));
        npc.level().addFreshEntity(grenade);
    }
}
