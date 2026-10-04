package org.confluence.mod.common.summoner.minion.goal.desert_tiger;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.DesertTigerMinion;

public class DesertTigerAttackGoal extends AttachmentEntityGoal<DesertTigerMinion> {

    private LivingEntity pounceTarget;
    private int pounceCooldown = 120;
    private int pounceTicks;

    public DesertTigerAttackGoal(DesertTigerMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void stop() {
        pounceTarget = null;
        minion.pouncing = false;
        minion.pounced.clear();
    }

    @Override
    public void tick() {
        if (pounceCooldown > 0) {
            pounceCooldown--;
        }
        if (pounceTarget != null) {
            Vec3 offset = pounceTarget.position().subtract(minion.getOwner().position());
            if (--pounceTicks <= 0 || Math.abs(offset.x) > 50.0 || Math.abs(offset.y) > 25.0 || Math.abs(offset.z) > 50.0 || !pounceTarget.isAlive() || !minion.getTargetCache().isTarget(pounceTarget)) {
                pounceTarget = null;
                minion.pouncing = false;
                minion.pounced.clear();
            }
        }
        if (pounceTarget == null && pounceCooldown == 0) {
            LivingEntity firstTarget = null;
            if (minion.getTarget() != null) {
                Vec3 offset = minion.getTarget().position().subtract(minion.getOwner().position());
                if (Math.abs(offset.x) <= 50.0 && Math.abs(offset.y) <= 25.0 && Math.abs(offset.z) <= 50.0) {
                    firstTarget = minion.getTarget();
                }
            }
            if (firstTarget == null) {
                firstTarget = minion.getOwner().level().getEntitiesOfClass(LivingEntity.class, minion.getOwner().getBoundingBox().inflate(50.0, 25.0, 50.0), candidate -> {
                    Vec3 offset = candidate.position().subtract(minion.getOwner().position());
                    return candidate != minion.getOwner() && candidate.isAlive() && minion.getTargetCache().isTarget(candidate)
                            && Math.abs(offset.x) <= 50.0 && Math.abs(offset.y) <= 25.0 && Math.abs(offset.z) <= 50.0;
                }).stream().min((first, second) -> Double.compare(first.distanceToSqr(minion.getPos()), second.distanceToSqr(minion.getPos()))).orElse(null);
            }
            if (firstTarget != null) {
                pounceTarget = firstTarget;
                pounceTicks = 100;
                pounceCooldown = minion.getSlotCost() >= 7 ? 80 : minion.getSlotCost() >= 4 ? 100 : 120;
                minion.pounced.clear();
            }
        }
        if (pounceTarget != null) {
            minion.pouncing = true;
            minion.setPhysics(false);
            Vec3 destination = pounceTarget.getBoundingBox().getCenter();
            Vec3 offset = destination.subtract(minion.getPos().add(0.0, 0.4, 0.0));
            if (offset.lengthSqr() > 1.0E-6) {
                minion.setPos(minion.getPos().add(offset.normalize().scale(Math.min(0.45, offset.length()))));
            }
            minion.lookAtPos(destination);
        } else {
            minion.pouncing = false;
            minion.setPhysics(true);
            Vec3 targetPos = minion.getTarget().position();
            minion.lookAtPos(minion.getTarget().getBoundingBox().getCenter());
            minion.moveTo(targetPos, 0.22F + Math.min(0.08F, (minion.getSlotCost() - 1) * 0.01F));
        }
    }
}
