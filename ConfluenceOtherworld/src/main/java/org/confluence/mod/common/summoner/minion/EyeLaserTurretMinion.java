package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.goal.eye_laser_turret.EyeLaserTurretAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.eye_laser_turret.EyeLaserTurretIdleGoal;
import org.confluence.mod.common.summoner.particle.GenericParticleBuilder;
import org.confluence.mod.common.summoner.particle.ParticleHelper;
import org.confluence.mod.common.summoner.projectile.EyeFireball;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;

/** 眼球激光塔：底座固定，每秒向目标发射一枚火球。 */
public class EyeLaserTurretMinion extends MomentumMinion implements IBlockCollision<EyeLaserTurretMinion> {

    private int cooldown;

    public EyeLaserTurretMinion() {
        super(SummonerAttachmentEntityTypes.EYE_LASER_TURRET);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new EyeLaserTurretAttackGoal(this));
        goalSelector.addGoal(1, new EyeLaserTurretIdleGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && getPos().distanceToSqr(getOwner().position()) <= 128.0 * 128.0;
    }

    /** 从塔顶向目标发射一枚火球。 */
    public void shootFireball(LivingEntity target) {
        Vec3 start = getPos();
        Vec3 direction = target.getBoundingBox().getCenter().subtract(start).normalize();
        EyeFireball fireball = new EyeFireball();
        fireball.setOwner(getOwner());
        fireball.init(new PathNode(start, direction));
        fireball.copyAttributes(this);
        fireball.setVelocity(direction.scale(1.0));
        SummonerHelper.get(getOwner()).add(fireball);
        ParticleHelper.create(getLevel())
                .generic(GenericParticleBuilder.create()
                        .centerColor(0xFF6B6B)
                        .edgeColor(0xFF2A2A)
                        .lifetime(6)
                        .lifetimeRandom(5)
                        .spin(0.2F)
                        .spinRandom(0.1F)
                        .friction(0.7F)
                        .scale(0.03F)
                        .scaleRandom(0.008F))
                .pos(start)
                .velocity(direction)
                .count(4)
                .speed(0.5)
                .spread(0.3)
                .emit();
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.25, -1.6, -0.25, 0.25, 0.25, 0.25);
    }
}
