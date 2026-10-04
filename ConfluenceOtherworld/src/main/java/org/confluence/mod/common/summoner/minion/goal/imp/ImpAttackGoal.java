package org.confluence.mod.common.summoner.minion.goal.imp;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.ImpMinion;
import org.confluence.mod.common.summoner.projectile.ImpFireball;

public class ImpAttackGoal extends AttachmentEntityGoal<ImpMinion> {

    private int cooldown;
    private Vec3 offset = Vec3.ZERO;

    public ImpAttackGoal(ImpMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void start() {
        Vec3 targetPos = minion.getTarget().getBoundingBox().getCenter();
        Vec3 direction = minion.getPos().offsetRandom(minion.getRandom(), 1.0F).subtract(targetPos);
        offset = direction.lengthSqr() < 1.0E-6 ? Vec3.ZERO : direction.normalize().scale(4);
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        Vec3 targetPos = target.getBoundingBox().getCenter();
        Vec3 toHover = targetPos.add(offset).subtract(minion.getPos());
        if (toHover.lengthSqr() > 0.25) {
            minion.addVelocity(toHover.normalize().scale(0.04));
        } else if (minion.getVelocity().lengthSqr() < 0.01) {
            start();
        }
        minion.lookAtPos(targetPos);
        if (--cooldown <= 0) {
            cooldown = 20 + minion.getRandom().nextIntBetweenInclusive(0, 6);
            Vec3 direction = targetPos.subtract(minion.getPos()).normalize();
            ImpFireball fireball = new ImpFireball();
            fireball.setOwner(minion.getOwner());
            fireball.init(new PathNode(minion.getPos(), direction));
            fireball.copyAttributes(minion);
            fireball.setVelocity(direction);
            SummonerHelper.get(minion.getOwner()).add(fireball);
            minion.attackTime = minion.getTickCount();
            minion.getLevel().playSound(null, minion.getPos().x, minion.getPos().y, minion.getPos().z, SoundEvents.BLAZE_SHOOT, minion.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
