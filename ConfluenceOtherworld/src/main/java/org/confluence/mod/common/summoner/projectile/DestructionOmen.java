package org.confluence.mod.common.summoner.projectile;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.MiniDestroyerMinion;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DestructionOmen extends Projectile implements IEntityCollision<DestructionOmen> {
    private final AABB hitbox = new AABB(-0.15, -0.15, 0, 0.15, 0.15, 48);
    private MiniDestroyerMinion source;

    public DestructionOmen() {
        super(SummonerAttachmentEntityTypes.DESTRUCTION_OMEN);
        setPhysics(false);
        setMaxTickCount(60);
    }

    public void follow(MiniDestroyerMinion source) {
        this.source = source;
        init(getSourceNode());
    }

    private PathNode getSourceNode() {
        PathNode node = source.getCurrentPathNode();
        return node.modifyPos(node.pos().add(source.getLookAngle().scale(0.4)));
    }

    @Override
    public void tick() {
        super.tick();
        if (isAlive()) {
            setCurrentPathNode(getSourceNode());
        }
    }

    @Override
    public boolean isAlive() {
        return source != null && !source.isRemove();
    }

    @Override
    public boolean canCollideAttack() {
        return !isRemove() && isAlive();
    }

    @Override
    public @NotNull AABB getHitbox() {
        return hitbox;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            attack(hit.entity(), getDamage(), 1);
        }
    }
}
