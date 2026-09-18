package org.confluence.mod.common.summoner.minion.goal.hornet;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.HornetMinion;
import org.confluence.mod.common.summoner.projectile.HornetStinger;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public class HornetAttackGoal extends AttachmentEntityGoal<HornetMinion> {

    private int cooldown;
    private Vec3 offset = Vec3.ZERO;

    public HornetAttackGoal(HornetMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void start() {
        LivingEntity target = minion.getTarget();
        RandomSource random = minion.getRandom();
        Vec3 targetCenter = target.getBoundingBox().getCenter();
        Vec3 pos = targetCenter
                .add(random.nextIntBetweenInclusive(-100, 100) * 0.02F, target.getBbHeight() + 2, random.nextIntBetweenInclusive(-100, 100) * 0.02F)
                .add(Vec3.ZERO.offsetRandom(random, 0.25F));
        offset = pos.subtract(targetCenter);
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        Vec3 minionPos = minion.getPos();
        Vec3 targetPos = target.getBoundingBox().getCenter();
        Vec3 wanderPos = targetPos.add(offset);
        if (minionPos.distanceTo(wanderPos) > 1) {
            minion.addVelocity(wanderPos.subtract(minionPos).normalize().scale(0.02 * (1 + Math.min(wanderPos.distanceTo(minionPos) * 0.2, 4))));
        } else {
            minion.setVelocity(minion.getVelocity().scale(0.4));
            if (minion.getVelocity().length() < 0.1) {
                start();
            }
        }
        minion.lookAtPos(targetPos);

        if (--cooldown <= 0) {
            minion.attackTime = minion.getTickCount();
            Player owner = minion.getOwner();
            RandomSource random = minion.getRandom();
            cooldown = 13 + random.nextIntBetweenInclusive(0, 4);
            Vec3 direction = targetPos.subtract(minionPos).normalize();
            HornetStinger stinger = new HornetStinger();
            stinger.setOwner(owner);
            stinger.init(new PathNode(minionPos, direction));
            stinger.copyAttributes(minion);
            stinger.setVelocity(direction);
            SummonerHelper.get(owner).add(stinger);
            if (TCUtils.hasType(owner, TCItems.HIVE$PACK)) {
                stinger.setDamage(stinger.getDamage() * 1.15F);
                cooldown = (int) (cooldown * 0.67F);
            }
        }
    }
}
