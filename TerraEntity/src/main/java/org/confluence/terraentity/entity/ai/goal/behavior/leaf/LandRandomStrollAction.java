
package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * 地面游走
 */
public class LandRandomStrollAction extends RandomStrollAction {

    public LandRandomStrollAction(PathfinderMob mob, double speedModifier, int interval) {
        super(mob, speedModifier, interval);
    }

    @Nullable
    protected Vec3 getPosition() {
        return LandRandomPos.getPos(this.mob, 15, 7);
    }

}
