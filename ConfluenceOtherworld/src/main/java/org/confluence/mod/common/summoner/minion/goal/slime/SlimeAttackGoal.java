package org.confluence.mod.common.summoner.minion.goal.slime;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.PlannedPath;
import org.confluence.mod.common.summoner.minion.SlimeMinion;

import java.util.ArrayList;
import java.util.List;

public class SlimeAttackGoal extends AttachmentEntityGoal<SlimeMinion> {

    public SlimeAttackGoal(SlimeMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        Vec3 targetPos = target.getBoundingBox().getCenter();
        minion.lookAtPos(targetPos);
        minion.moveTo(targetPos, 0.4f);
    }
}
