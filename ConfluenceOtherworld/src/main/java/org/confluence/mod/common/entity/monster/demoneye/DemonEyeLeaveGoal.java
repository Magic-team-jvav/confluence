package org.confluence.mod.common.entity.monster.demoneye;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/** 白天的AI
 * @author voila */
public class DemonEyeLeaveGoal extends Goal {
    protected final DemonEye mob;
    private Vec3 targetMotion;

    public DemonEyeLeaveGoal(DemonEye mob){
        this.mob = mob;
    }

    @Override
    public void start(){
        RandomSource random = mob.getRandom();
        double x = random.nextDouble() - 0.5;
        double y = 0.1 + (0.6 - 0.1) * random.nextDouble();  // 0.1-0.6
        double z = random.nextDouble() - 0.5;
        targetMotion = new Vec3(x, y, z).normalize().scale(0.25);
    }

    @Override
    public boolean canUse(){
        return mob.level().isDay();
    }

    @Override
    public void tick(){
        Vec3 motion = mob.getDeltaMovement();
        if(motion.length() < 0.5){
            mob.addDeltaMovement(targetMotion);
        }
        // 如果大于这个速度也不用管，不管哪个方向
    }
}
