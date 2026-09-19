package org.confluence.mod.common.summoner.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 小鬼火球：直线飞行的附着弹幕，命中后点燃目标。
 */
public class ImpFireball extends Projectile implements IEntityCollision<ImpFireball>, IBlockCollision<ImpFireball> {

    public ImpFireball() {
        super(SummonerAttachmentEntityTypes.IMP_FIREBALL);
        setDrag(1.0F);
        setGravity(0);
    }

    @Override
    public void tick() {
        super.tick();
        if (getTickCount() > 100) {
            setRemove();
        }
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        HitContext context = hitContexts.get(0);
        LivingEntity target = context.entity();
        attack(target, getDamage(), 4);
        target.setSecondsOnFire(5);
        setCurrentPathNode(getCurrentPathNode().modifyPos(context.hitPoint()));
        setRemove();
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15);
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);
    }

    @Override
    public void onBlockCollision(CollisionContext context) {
        setRemove();
    }
}
