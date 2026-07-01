package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.EnumSet;
import java.util.List;

/**
 * 护士治疗附近受伤 NPC。
 */
public class NPCHealGoal extends Goal {
    private final BaseNPC npc;
    private final double range;
    private int cooldown;
    private LivingEntity healTarget;

    public NPCHealGoal(BaseNPC npc, double range) {
        this.npc = npc;
        this.range = range;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        AABB box = npc.getBoundingBox().inflate(range);
        List<BaseNPC> nearby = npc.level().getEntitiesOfClass(BaseNPC.class, box, n -> n != npc && n.isAlive());
        for (BaseNPC other : nearby) {
            if (other.getHealth() < other.getMaxHealth() * 0.7f) {
                healTarget = other;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return healTarget != null && healTarget.isAlive()
                && healTarget.getHealth() < healTarget.getMaxHealth()
                && npc.distanceToSqr(healTarget) <= range * range;
    }

    @Override
    public void tick() {
        if (healTarget == null) return;
        npc.getLookControl().setLookAt(healTarget, 30, 30);
        if (npc.distanceToSqr(healTarget) > 4) {
            npc.getNavigation().moveTo(healTarget, 0.6);
        } else {
            npc.getNavigation().stop();
            healTarget.heal(1.0f);
            cooldown = 20; // 1 sec per heal tick
            if (healTarget.getHealth() >= healTarget.getMaxHealth() * 0.9f) {
                healTarget = null;
            }
        }
    }

    @Override
    public void stop() {
        healTarget = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
