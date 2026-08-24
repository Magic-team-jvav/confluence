package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.projectile.NPCProjectileEffects;
import org.confluence.mod.common.entity.projectile.NPCWeaponProjectile;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.EnumSet;
import java.util.List;

/// 护士按照自身优先级与严格生命阈值，向附近 NPC 投掷治疗药水。
public class NPCHealGoal extends Goal {
    private static final int SEARCH_INTERVAL_TICKS = 20;
    private final BaseNPC npc;
    private final double attackRange;
    private final double searchRange;
    private int cooldown;
    private int searchDelay;
    private int prepareTicks;
    private BaseNPC healTarget;

    /// 创建指定治疗射程的护士目标；搜索范围固定为治疗射程的两倍。
    public NPCHealGoal(BaseNPC npc, double range) {
        this.npc = npc;
        this.attackRange = range;
        this.searchRange = range * 2.0;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /// 按自身危急、盟友危急、自身受伤的顺序选择治疗目标。
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

    /// 目标仍满足原始生命阈值、距离和视线条件时继续准备治疗。
    @Override
    public boolean canContinueToUse() {
        if (healTarget == null || !healTarget.isAlive()) return false;
        if (healTarget == npc) return healTarget.getHealth() <= healTarget.getMaxHealth() * 0.9F;
        return healTarget.getHealth() < healTarget.getMaxHealth() * 0.5F
                && npc.distanceToSqr(healTarget) <= searchRange * searchRange && npc.getSensing().hasLineOfSight(healTarget);
    }

    /// 每次治疗随机准备 10 到 19 tick，与 Wiki 的治疗攻击准备范围一致。
    @Override
    public void start() {
        prepareTicks = 10 + npc.getRandom().nextInt(10);
    }

    /// 接近盟友并投出追踪治疗弹；治疗自己时直接结算，避免弹体无法命中所有者。
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
        if (healTarget != npc) {
            Vec3 destination = LandRandomPos.getPosTowards(npc, 3, 1, healTarget.position());
            if (destination != null)
                npc.getNavigation().moveTo(destination.x, destination.y, destination.z, 1.0);
        } else {
            npc.getNavigation().stop();
        }
        if (healTarget != npc && npc.distanceToSqr(healTarget) > attackRange * attackRange) {
            prepareTicks = 10 + npc.getRandom().nextInt(10);
            return;
        }
        if (--prepareTicks > 0) return;

        npc.getNavigation().stop();
        if (healTarget == npc) {
            npc.setHealth(Math.min(npc.getMaxHealth(), npc.getHealth() + 20));
        } else {
            NPCWeaponProjectile syringe = new NPCWeaponProjectile(npc, PotionItems.HEALING_POTION.toStack(), 0,
                    NPCProjectileEffects.HEAL);
            syringe.setHomingTarget(healTarget);
            syringe.shoot(healTarget.getX() - npc.getX(), healTarget.getEyeY() - npc.getEyeY(),
                    healTarget.getZ() - npc.getZ(), 0.8F, 1);
            npc.level().addFreshEntity(syringe);
        }
        npc.swing(InteractionHand.MAIN_HAND, true);
        cooldown = 5 + npc.getRandom().nextInt(10);
        healTarget = null;
    }

    /// 中断治疗时停止寻路并清理目标。
    @Override
    public void stop() {
        npc.getNavigation().stop();
        healTarget = null;
    }

    /// 搜索延迟、准备时间和弹体发射都需要逐 tick 更新。
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
