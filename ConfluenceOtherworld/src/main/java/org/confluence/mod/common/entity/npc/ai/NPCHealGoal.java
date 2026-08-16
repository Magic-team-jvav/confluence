package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.EnumSet;
import java.util.List;

/// 护士按自身危急、友方危急、自身受伤的顺序选择目标，并投掷治疗药水。
public class NPCHealGoal extends Goal {
    private final BaseNPC npc;
    private final double range;
    private int cooldown;
    private int prepareTicks;
    private LivingEntity healTarget;

    public NPCHealGoal(BaseNPC npc, double range) {
        this.npc = npc;
        this.range = range;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (npc.isPanicking()) return false;
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        double selfHealth = npc.getHealth() / npc.getMaxHealth();
        if (selfHealth <= 0.5) {
            healTarget = npc;
            return true;
        }
        AABB box = npc.getBoundingBox().inflate(range);
        List<BaseNPC> nearby = npc.level().getEntitiesOfClass(BaseNPC.class, box, n -> n != npc && n.isAlive());
        double nearestDistance = Double.MAX_VALUE;
        for (BaseNPC other : nearby) {
            double distance = npc.distanceToSqr(other);
            if (other.getHealth() < other.getMaxHealth() * 0.5F && distance <= range * range && npc.hasLineOfSight(other) && distance < nearestDistance) {
                healTarget = other;
                nearestDistance = distance;
            }
        }
        if (healTarget != null) return true;
        if (selfHealth <= 0.9) {
            healTarget = npc;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !npc.isPanicking() && healTarget != null && healTarget.isAlive()
                && healTarget.getHealth() < healTarget.getMaxHealth()
                && (healTarget == npc || npc.distanceToSqr(healTarget) <= range * range && npc.hasLineOfSight(healTarget));
    }

    @Override
    public void tick() {
        if (healTarget == null) return;
        npc.getLookControl().setLookAt(healTarget, 30, 30);
        if (healTarget != npc && npc.distanceToSqr(healTarget) > range * range * 0.36) {
            npc.getNavigation().moveTo(healTarget, 0.6);
            prepareTicks = 0;
            return;
        }
        npc.getNavigation().stop();
        if (++prepareTicks < 10) return;
        ThrownPotion potion = new ThrownPotion(npc.level(), npc);
        potion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.HEALING));
        if (healTarget == npc) {
            potion.shoot(0.0, -1.0, 0.0, 0.5F, 1.0F);
        } else {
            double dx = healTarget.getX() - npc.getX();
            double dy = healTarget.getY(0.5) - potion.getY();
            double dz = healTarget.getZ() - npc.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            potion.shoot(dx, dy + horizontal * 0.2, dz, 0.5F, 1.0F);
        }
        npc.level().addFreshEntity(potion);
        cooldown = 30;
        healTarget = null;
    }

    @Override
    public void stop() {
        prepareTicks = 0;
        healTarget = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
