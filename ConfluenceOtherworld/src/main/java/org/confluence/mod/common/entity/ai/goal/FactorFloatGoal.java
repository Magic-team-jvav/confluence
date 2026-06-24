package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.phys.Vec3;

public class FactorFloatGoal extends FloatGoal {
    private final Mob mob;
    private double factor = 0.04;

    public FactorFloatGoal(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    public FactorFloatGoal(Mob mob, double factor) {
        this(mob);
        this.factor = factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    @Override
    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8f) {
            this.mob.addDeltaMovement(new Vec3(0, factor, 0));
        }
    }

}
