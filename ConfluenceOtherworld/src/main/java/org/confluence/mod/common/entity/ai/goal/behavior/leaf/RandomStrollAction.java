package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * 飞行生物游走
 */
public class RandomStrollAction extends BTNode{

    protected final PathfinderMob mob;
    protected final double speedModifier;
    protected int interval;
    protected final int _interval;

    public RandomStrollAction(PathfinderMob mob, double speedModifier, int interval) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this._interval = interval;
        this.interval = 0;

        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public BTStatus execute() {
        if(++this.interval > _interval) {
            return BTStatus.SUCCESS;
        }
//        System.out.println(this.interval);

        return BTStatus.RUNNING;
    }

    @Nullable
    protected Vec3 getPosition() {
        return AirRandomPos.getPosTowards(mob, 10, 5,1, mob.blockPosition().getBottomCenter(), Mth.PI * 0.1f);
    }


    @Override
    public void start() {
        super.start();
        this.interval = this.mob.getRandom().nextInt(this._interval / 4);
        Vec3 vec3 = this.getPosition();
        if (vec3 != null) {
            this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
        }
    }

    @Override
    public void stop() {
        this.interval = 0;
        this.mob.getNavigation().stop();
        super.stop();
    }

}
