package org.confluence.mod.common.summoner.projectile;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.init.ModAttachmentEntityTypes;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProbeLaser extends Projectile implements IEntityCollision<ProbeLaser> {
    public ProbeLaser() {
        super(ModAttachmentEntityTypes.PROBE_LASER);
        setDrag(1);
        setGravity(0);
        setMaxTickCount(100);
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        HitContext hit = hitContexts.getFirst();
        attack(hit.entity(), getDamage(), 0);
        setCurrentPathNode(getCurrentPathNode().modifyPos(hit.hitPoint()));
        setRemove();
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.03, -0.03, -1, 0.03, 0.03, 0);
    }
}
