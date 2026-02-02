package org.confluence.terraentity.entity.ai.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.monster.demoneye.DemonEyeWanderGoal;

public class LookForwardWanderFlyGoal extends DemonEyeWanderGoal {


    float offsetY;
    public LookForwardWanderFlyGoal(PathfinderMob mob, float speed, float offsetY) {
        super(mob,speed);
        this.offsetY = offsetY;

    }
    @Override
    public boolean canUse() {
        return mob.getTarget() == null;
    }

    @Override
    public float getOffsetY(){
        float period = 10f;
        float radians = Mth.TWO_PI * (locateCount % period) / period;
        return 2.57f * Mth.cos(radians)-3 + this.offsetY;
    }
    @Override
    public void tick(){
        super.tick();

        mob.getLookControl().setLookAt(mob.position().add(mob.getDeltaMovement().scale(20).add(0,1,0)));
        mob.setYRot(mob.getYHeadRot());
        double speed = mob.getDeltaMovement().length();
        if(speed > 0.3){ // 限制被攻击后失去目标导致速度不减
            mob.setDeltaMovement(mob.getDeltaMovement().normalize().scale(0.3 + (speed - 0.3) * 0.5));
        }

    }

    @Override
    protected Vec3 getTargetPos(double anchorY){
        Vec3 pos = AirRandomPos.getPosTowards(mob, 10, 5,1, mob.blockPosition().getBottomCenter(), Mth.PI * 0.1f);
        if(pos == null){
            // 找不到合适的目标，随机找一个
            return super.getTargetPos(anchorY);
        }
        return pos;
    }
}
