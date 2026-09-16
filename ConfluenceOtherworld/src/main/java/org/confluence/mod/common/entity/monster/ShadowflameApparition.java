package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

public class ShadowflameApparition extends BaseFlyingMonster {
    public ShadowflameApparition(EntityType<? extends ShadowflameApparition> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        LivingEntity target = getTarget();
                        if (target == null) {
                            setDeltaMovement(getDeltaMovement().scale(0.9).add(0, 0.03, 0));
                        } else {
                            Vec3 direction = target.getBoundingBox().getCenter().subtract(position()).normalize();
                            Vec3 velocity = getDeltaMovement().lerp(direction.scale(0.65), 0.1);
                            setDeltaMovement(velocity);
                            faceCombatPosition(position().add(velocity), 10, 10);
                        }
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }
}
