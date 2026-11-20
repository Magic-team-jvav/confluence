package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.minion.goal.mini_destroyer.MiniDestroyerAttackGoal;
import org.confluence.mod.common.summoner.minion.worm.WormAttachmentEntity;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;

public class MiniDestroyerMinion extends WormAttachmentEntity {
    public MiniDestroyerMinion() {
        super(SummonerAttachmentEntityTypes.MINI_DESTROYER);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        super.registerGoals(goalSelector);
        goalSelector.addGoal(0, new MiniDestroyerAttackGoal(this));
    }

    @Override
    public double getSegmentDistance() {
        return 10.0 / 16;
    }

    @Override
    public @NotNull AABB getHitbox() {
        double halfWidth = isHead() ? 5.0 / 16 : 4.0 / 16;
        double minZ = getOrder() == getSameSize() - 1 ? -12.0 / 16 : -5.0 / 16;
        double maxZ = isHead() ? 12.0 / 16 : 5.0 / 16;
        return new AABB(-halfWidth, -2.5 / 16, minZ, halfWidth, 3.5 / 16, maxZ);
    }
}
