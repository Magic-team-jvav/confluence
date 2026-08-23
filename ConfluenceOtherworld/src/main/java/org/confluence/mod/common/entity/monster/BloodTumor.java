package org.confluence.mod.common.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.MonsterEntities;

/// 血腥孢子爆裂后生成的静止肿瘤。
///
/// 肿瘤不会寻路或主动近战，而是在短暂孵化后随机转化为血爬虫、脸怪或猩红喀迈拉。
public final class BloodTumor extends BaseMonster {
    public BloodTumor(EntityType<? extends BloodTumor> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isAlive() && tickCount == 60 + Math.floorMod(getId(), 40)) {
            transform();
        }
    }

    private void transform() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        EntityType<? extends Entity> outcome = switch (random.nextInt(3)) {
            case 0 -> MonsterEntities.BLOOD_CRAWLER.get();
            case 1 -> MonsterEntities.FACE_MONSTER.get();
            default -> MonsterEntities.CRIMERA.get();
        };
        Entity replacement = outcome.create(serverLevel);
        if (replacement == null) {
            return;
        }
        replacement.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
        replacement.setDeltaMovement(0.0, 0.4, 0.0);
        serverLevel.addFreshEntity(replacement);
        kill();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }
}
