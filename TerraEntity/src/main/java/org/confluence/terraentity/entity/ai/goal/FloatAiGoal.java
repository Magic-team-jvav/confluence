package org.confluence.terraentity.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FloatAiGoal extends Goal {

    Mob mob;
    public FloatAiGoal(Mob mob) {
        super();
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return true;
    }
//    public boolean requiresUpdateEveryTick() {
//        return false;
//    }
    @Override
    public void tick(){
        this.mob.addDeltaMovement(new Vec3(0, Math.sin(this.mob.tickCount * 0.2f) * 0.008f, 0));

        LivingEntity target = mob.getTarget();
        if(target == null || this.mob.hurtTime > 0){
            return;
        }

        this.mob.lookAt(target, 10, 10);
        if(target.isAlive()){
            this.mob.setDeltaMovement(target.position().subtract(this.mob.position()).normalize()
                    .scale(this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED )* 0.8F));
        }
    }
}
