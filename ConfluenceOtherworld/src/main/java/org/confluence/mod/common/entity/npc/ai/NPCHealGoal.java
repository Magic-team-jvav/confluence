package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.EnumSet;
import java.util.List;

/// 护士按照自身优先级与严格生命阈值，向附近 NPC 投掷治疗药水。
public class NPCHealGoal extends Goal {
    private static final int PREPARE_TICKS = 10;
    private static final int COOLDOWN_TICKS = 30;
    private static final int SEARCH_INTERVAL_TICKS = 20;
    private final BaseNPC npc;
    private final double attackRange;
    private final double searchRange;
    private int cooldown;
    private int searchDelay;
    private int prepareTicks;
    private BaseNPC healTarget;

    public NPCHealGoal(BaseNPC npc, double range) {
        this.npc = npc;
        this.attackRange = range;
        this.searchRange = range * 2.0;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        if (searchDelay > 0) {
            searchDelay--;
            return false;
        }
        searchDelay = SEARCH_INTERVAL_TICKS - 1;
        float healthRatio = npc.getHealth() / npc.getMaxHealth();
        if (healthRatio <= 0.5F) {
            healTarget = npc;
            return true;
        }

        AABB box = npc.getBoundingBox().inflate(searchRange);
        List<BaseNPC> nearby = npc.level().getEntitiesOfClass(BaseNPC.class, box, other -> other != npc && other.isAlive()
                && npc.distanceToSqr(other) <= searchRange * searchRange && npc.getSensing().hasLineOfSight(other)
                && other.getHealth() < other.getMaxHealth() * 0.5F);
        healTarget = null;
        for (BaseNPC other : nearby) {
            if (healTarget == null || npc.distanceToSqr(other) < npc.distanceToSqr(healTarget))
                healTarget = other;
        }
        if (healTarget != null) return true;
        if (healthRatio <= 0.9F) {
            healTarget = npc;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (healTarget == null || !healTarget.isAlive()) return false;
        if (healTarget == npc) return healTarget.getHealth() <= healTarget.getMaxHealth() * 0.9F;
        return healTarget.getHealth() < healTarget.getMaxHealth() * 0.5F
                && npc.distanceToSqr(healTarget) <= searchRange * searchRange && npc.getSensing().hasLineOfSight(healTarget);
    }

    @Override
    public void start() {
        prepareTicks = PREPARE_TICKS;
    }

    @Override
    public void tick() {
        if (healTarget == null) return;
        if (healTarget == npc) {
            npc.getLookControl().setLookAt(npc.position());
            npc.lookAt(EntityAnchorArgument.Anchor.EYES, npc.position());
        } else {
            npc.getLookControl().setLookAt(healTarget, 10.0F, 10.0F);
            npc.lookAt(healTarget, 10.0F, 10.0F);
        }
        Vec3 destination = LandRandomPos.getPosTowards(npc, 3, 1, healTarget.position());
        if (destination != null)
            npc.getNavigation().moveTo(destination.x, destination.y, destination.z, 1.0);
        if (healTarget != npc && npc.distanceToSqr(healTarget) > attackRange * attackRange) {
            prepareTicks = PREPARE_TICKS;
            return;
        }
        if (--prepareTicks > 0) return;

        npc.getNavigation().stop();
        ThrownPotion potion = new ThrownPotion(npc.level(), npc);
        potion.setItem(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING));
        potion.shootFromRotation(npc, npc.getXRot(), npc.getYHeadRot(), -20.0F, 0.5F, 1.0F);
        npc.level().addFreshEntity(potion);
        npc.swing(InteractionHand.MAIN_HAND, true);
        cooldown = COOLDOWN_TICKS;
        healTarget = null;
    }

    @Override
    public void stop() {
        npc.getNavigation().stop();
        healTarget = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
