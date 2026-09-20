package org.confluence.mod.common.summoner.minion.goal.spider;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.SpiderMinion;

public class SpiderAttackGoal extends AttachmentEntityGoal<SpiderMinion> {

    private LivingEntity latched;
    private Vec3 latchOffset = Vec3.ZERO;
    private boolean returning;
    private int biteCooldown;

    public SpiderAttackGoal(SpiderMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void stop() {
        latched = null;
        latchOffset = Vec3.ZERO;
        returning = false;
        biteCooldown = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        if (latched != null && (latched != target || !latched.isAlive())) {
            latched = null;
        }
        double ownerDistance = minion.getPos().distanceToSqr(minion.getOwner().position());
        double returnRange = 87.5 + minion.getOrder() * 2.5;
        if (ownerDistance > returnRange * returnRange) {
            returning = true;
        }
        if (returning && ownerDistance < 16.0) {
            returning = false;
        }
        if (returning) {
            latched = null;
            minion.setPhysics(false);
            Vec3 offset = minion.getOwner().position().add(0.0, 1.0, 0.0).subtract(minion.getPos());
            if (offset.lengthSqr() > 1.0E-6) {
                minion.setPos(minion.getPos().add(offset.normalize().scale(Math.min(1.0, offset.length()))));
            }
            minion.lookAtPos(minion.getOwner().position().add(0.0, 1.0, 0.0));
        } else if (latched != null) {
            float yaw = latched.yBodyRot;
            Vec3 offset = latchOffset.yRot(-yaw * Mth.DEG_TO_RAD);
            minion.setPhysics(false);
            RandomSource random = minion.getRandom();
            random.setSeed(minion.getOrder() * 43L);
            float height = latched.getBbHeight() / 2;
            minion.setPos(latched.getBoundingBox().getCenter().add(0, height * (1 - (2 * random.nextFloat())), 0).add(offset));
            minion.lookAtPos(latched.getBoundingBox().getCenter());
            if (biteCooldown > 0) {
                biteCooldown--;
            }
            if (biteCooldown == 0) {
                minion.attack(latched, minion.getDamage(), 5);
                latched.addEffect(new MobEffectInstance(ModEffects.ACID_VENOM.get(), 40 + minion.getRandom().nextInt(41)), minion.getOwner());
                biteCooldown = 7;
            }
        } else {
            minion.setPhysics(true);
            Vec3 targetPos = target.getBoundingBox().getCenter();
            minion.lookAtPos(targetPos);
            minion.moveTo(targetPos, 0.3F);
            if (minion.getBlockCollisionBox().move(minion.getPos()).inflate(0.15).intersects(target.getBoundingBox())) {
                latched = target;
                latchOffset = minion.getPos().subtract(target.position()).yRot(target.yBodyRot * Mth.DEG_TO_RAD);
                biteCooldown = 0;
            }
        }
    }
}
