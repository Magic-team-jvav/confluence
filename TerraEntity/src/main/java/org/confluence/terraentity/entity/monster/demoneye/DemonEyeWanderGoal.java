package org.confluence.terraentity.entity.monster.demoneye;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

/** 周围没有目标的AI，乱逛
 * @author voila */
public class DemonEyeWanderGoal extends DemonEyeSurroundTargetGoal {
    private double anchorY=Double.NaN;

    public DemonEyeWanderGoal(PathfinderMob mob){
        super(mob);
        maxSpeed=0.2;
    }
    public DemonEyeWanderGoal(PathfinderMob mob,float maxSpeed){
        super(mob);
        this.maxSpeed = maxSpeed;
    }

    @Override
    public boolean canUse(){
        return mob.getTarget() == null && mob.level().isNight();
    }

    @Override
    public boolean canContinueToUse(){
        return canUse() && ticksLeft > 0 &&
                mob.position().distanceToSqr(targetPos) > 2.25; // 1.5*1.5
    }

    @Override
    public void start(){
        locateCount++;
        mob.setDeltaMovement(mob.getDeltaMovement().with(Direction.Axis.Y, 0));
        if(Double.isNaN(anchorY)){
            anchorY = mob.position().y;
        }
        targetPos = getTargetPos(anchorY);
        ticksLeft = 30;
    }

    protected Vec3 getTargetPos(double anchorY){
        double x = random.nextDouble() * 10 - 5;
        double y = getOffsetY() + 5;
        double z = random.nextDouble() * 10 - 5;
        return new Vec3(x, 0, z).normalize().scale(15).add(mob.position()).with(Direction.Axis.Y, y + anchorY);
    }
}
