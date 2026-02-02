package org.confluence.terraentity.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.pathfinder.Path;

public class JumpOverBlockGoal extends JumpGoal {
    protected final Mob mob;
    protected final float speedModifier;
    protected int delayJumpTicks;
    public JumpOverBlockGoal(Mob mob) {
        this(mob, 1.0f);
    }
    public JumpOverBlockGoal(Mob mob,float speedModifier) {
        super();
        this.mob = mob;
        this.speedModifier = speedModifier;
    }
    public boolean isInterruptable(){
        return false;
    }
    @Override
    public boolean canUse() {
        delayJumpTicks--;
        if(delayJumpTicks == 0) {
            mob.addDeltaMovement(mob.getForward().normalize().scale(mob.getSpeed() * speedModifier));
//            System.out.println("jumping");
        }
        if(!mob.onGround()) return false;
        Path path = mob.getNavigation().getPath();
        if(path==null) return false;
        boolean flag1 = path.getNodeCount() > path.getNextNodeIndex() + 1;
        if(!flag1) return false;
        float h = (float) (mob.getAttributeBaseValue(Attributes.JUMP_STRENGTH) * 4);

        boolean flag = path.getNextNode().y > mob.getY() &&
                path.getNextNode().y < mob.getY() + h;

//        System.out.println(flag + "  " + path.getNextNode().y +"   "+ mob.getY() +"  "+ h);
        return flag;
    }
    @Override
    public void start() {
        super.start();
        mob.setDeltaMovement(0,0,0);
        mob.jumpFromGround();
        delayJumpTicks = 2;
//        mob.addDeltaMovement(mob.getForward().normalize().scale(mob.getSpeed() * speedModifier));
    }
    @Override
    public void stop() {
        super.stop();
    }
}
