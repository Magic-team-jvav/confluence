package org.confluence.mod.common.summoner.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.particle.GenericParticleBuilder;
import org.confluence.mod.common.summoner.particle.ParticleHelper;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** 禁戒弹：命中时对半径 3.5 格内所有有效目标造成范围伤害。 */
public class ForbiddenOrb extends Projectile implements IEntityCollision<ForbiddenOrb>, IBlockCollision<ForbiddenOrb> {

    private boolean exploded;

    public ForbiddenOrb() {
        super(SummonerAttachmentEntityTypes.FORBIDDEN_ORB);
        setDrag(1.0F);
        setGravity(0.0F);
        setMaxTickCount(60);
    }

    @Override
    public void tick() {
        super.tick();
        ParticleHelper.create(getLevel())
                .generic(GenericParticleBuilder.create()
                        .centerColor(0xFFD700)
                        .edgeColor(0xFFA500)
                        .lifetime(5)
                        .lifetimeRandom(20)
                        .spin(0.25F)
                        .spinRandom(0.2F)
                        .friction(0.75F)
                        .scale(0.025F)
                        .scaleRandom(0.01F))
                .pos(getPos())
                .count(12)
                .offset(0.05)
                .velocity(getVelocity().normalize().scale(0.1))
                .emit();
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        if (!hitContexts.isEmpty()) {
            Vec3 hitPoint = hitContexts.get(0).hitPoint();
            explode(hitPoint);
            setCurrentPathNode(getCurrentPathNode().modifyPos(hitPoint));
            setRemove();
        }
    }

    @Override
    public void onBlockCollision(CollisionContext context) {
        explode(context.position());
        setCurrentPathNode(getCurrentPathNode().modifyPos(context.position()));
        setRemove();
    }

    @Override
    public void onRemove() {
        explode(getPos());
    }

    /** 在指定位置结算一次范围伤害并生成金色与白色粒子。 */
    private void explode(Vec3 hitPoint) {
        if (!exploded) {
            exploded = true;
            List<LivingEntity> entities = getTargetCache().getEntitiesInRadius(hitPoint, 3.5, target -> getTargetCache().isTarget(target));
            for (LivingEntity entity : entities) {
                attack(entity, getDamage(), 0);
            }
            ParticleHelper.create(getLevel())
                    .generic(GenericParticleBuilder.create()
                            .centerColor(0xFFD700)
                            .edgeColor(0xFFA500)
                            .lifetime(5)
                            .lifetimeRandom(5)
                            .spin(0.25F)
                            .spinRandom(0.1F)
                            .friction(0.75F)
                            .scale(0.04F)
                            .scaleRandom(0.01F))
                    .pos(hitPoint)
                    .offset(0.3)
                    .velocity(getVelocity())
                    .count(16)
                    .speed(0.7)
                    .spread(Mth.TWO_PI)
                    .emit();
        }
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15);
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return getHitbox();
    }
}
