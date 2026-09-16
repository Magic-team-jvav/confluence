package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.init.entity.MonsterEntities;

/**
 * 独立的史莱姆之母，死亡后分裂为史莱姆宝宝。
 */
public final class MotherSlime extends BaseSlime {
    private static final int SIZE = 4;

    public MotherSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false, SIZE);
    }

    @Override
    public void setSlimeSize(int size) {
        super.setSlimeSize(SIZE);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && !isRemoved() && isDeadOrDying()) {
            int babies = 1 + random.nextInt(3);
            float horizontalOffset = getBbWidth() / 4.0F;
            float verticalOffset = getBbHeight() / 8.0F;
            for (int i = 0; i < babies; i++) {
                BabySlime child = MonsterEntities.BABY_SLIME.get().create(level());
                if (child == null) continue;
                if (isPersistenceRequired()) child.setPersistenceRequired();
                if (hasCustomName()) child.setCustomName(getCustomName());
                child.setNoAi(isNoAi());
                child.setInvulnerable(isInvulnerable());
                float xOffset = (i % 2 - 0.5F) * horizontalOffset;
                float zOffset = (i / 2 - 0.5F) * horizontalOffset;
                child.moveTo(getX() + xOffset, getY() + verticalOffset, getZ() + zOffset, random.nextFloat() * 360.0F, 0.0F);
                LivingEntity target = getTarget();
                if (target != null && target.isAlive()) {
                    child.setTarget(target);
                    BossMinionCoordinator.faceTargetImmediately(child, target);
                }
                if (!level().addFreshEntity(child)) child.discard();
            }
        }
        super.remove(reason);
    }
}
