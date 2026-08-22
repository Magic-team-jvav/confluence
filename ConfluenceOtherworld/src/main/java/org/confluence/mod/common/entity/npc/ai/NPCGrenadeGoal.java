package org.confluence.mod.common.entity.npc.ai;

import PortLib.extensions.net.minecraft.world.level.Explosion.PortExplosionExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.damage.IgnoreThrowerExplosionDamageCalculator;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.projectile.bomb.BaseGrenadeEntity;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.util.TerraStyleExplosion;

import java.util.EnumSet;

/// 爆破专家瞄准目标后，在目标附近放置不会伤害友方的炸药。
public class NPCGrenadeGoal extends Goal {
    private static final int PREPARE_TICKS = 10;
    private static final int ATTACK_COOLDOWN_TICKS = 30;
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

        if (--attackTimer <= 0 && seeTime >= PREPARE_TICKS) {
            attackTimer = ATTACK_COOLDOWN_TICKS;
            seeTime = 0;
            performAttack(target);
        }
    }

    private void performAttack(LivingEntity target) {
        double distance = npc.distanceTo(target);
        Vec3 position = distance < 5.0
                ? target.getEyePosition()
                : target.position().subtract(npc.position()).normalize().scale(5.0).add(npc.getEyePosition());
        NPCExplosive grenade = new NPCExplosive(npc);
        grenade.setItem(ConsumableItems.BOMB.toStack());
        grenade.setPos(position);
        npc.level().addFreshEntity(grenade);
    }

    private static final class NPCExplosive extends BaseGrenadeEntity {
        private final BaseNPC thrower;

        private NPCExplosive(BaseNPC thrower) {
            super(thrower);
            this.thrower = thrower;
            delay = 80;
        }

        @Override
        protected void explodeFunction(ServerLevel level) {
            TerraStyleExplosion.terraExplode(level, this, PortExplosionExtension.getDefaultDamageSource(level, this),
                    getExplosionDamageCalculator(), getX(), getY(), getZ(), 4.0F, Level.ExplosionInteraction.NONE);
        }

        @Override
        protected void onHitEntity(EntityHitResult result) {}

        @Override
        protected ExplosionDamageCalculator getExplosionDamageCalculator() {
            return new IgnoreThrowerExplosionDamageCalculator(1.0F, thrower);
        }
    }
}
