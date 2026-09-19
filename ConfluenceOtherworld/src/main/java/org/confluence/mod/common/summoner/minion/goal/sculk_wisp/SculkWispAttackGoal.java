package org.confluence.mod.common.summoner.minion.goal.sculk_wisp;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.SculkWispMinion;

public class SculkWispAttackGoal extends AttachmentEntityGoal<SculkWispMinion> {

    private Vec3 offset = Vec3.ZERO;
    private int cooldown;
    private int castTicks;
    private LivingEntity castTarget;

    public SculkWispAttackGoal(SculkWispMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void start() {
        Vec3 direction = minion.getPos().offsetRandom(minion.getRandom(), 1.0F).subtract(minion.getTarget().getBoundingBox().getCenter());
        offset = direction.normalize().scale(4.0);
    }

    @Override
    public void stop() {
        castTicks = 0;
        castTarget = null;
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        Vec3 targetPos = target.getBoundingBox().getCenter();
        minion.lookAtPos(targetPos);
        Vec3 toDestination = targetPos.add(offset).subtract(minion.getPos());
        if (toDestination.lengthSqr() > 0.25) {
            minion.addVelocity(toDestination.normalize().scale(0.04));
        } else {
            minion.setVelocity(minion.getVelocity().scale(0.8));
            if (minion.getVelocity().lengthSqr() < 0.01) {
                start();
            }
        }
        if (castTicks > 0) {
            if (--castTicks == 0) {
                if (castTarget == target) {
                    Vec3 origin = minion.getPos().add(0.0, 0.5, 0.0);
                    Vec3 toTarget = target.getBoundingBox().getCenter().subtract(origin);
                    if (toTarget.lengthSqr() > 1.0E-6) {
                        Vec3 direction = toTarget.normalize();
                        if (minion.getLevel() instanceof ServerLevel level) {
                            for (int index = 1; index < (int) Math.floor(toTarget.length()) + 7; index++) {
                                Vec3 particle = origin.add(direction.scale(index));
                                level.sendParticles(ParticleTypes.SONIC_BOOM, particle.x, particle.y, particle.z, 1, 0.0, 0.0, 0.0, 0.0);
                            }
                            level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL, 3.0F, 1.0F);
                        }
                        minion.attack(target, minion.getDamage(), 10);
                        if (!(target instanceof BaseBoss)) {
                            double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                            double strength = Math.max(0.0, 1.0 - resistance) * minion.getKnockback();
                            target.push(direction.x * 2.5 * strength, direction.y * 0.5 * strength, direction.z * 2.5 * strength);
                        }
                    }
                }
                castTarget = null;
            }
        } else if (--cooldown <= 0) {
            cooldown = 30;
            castTicks = 19;
            castTarget = target;
            minion.castTime = minion.getTickCount();
        }
    }
}
