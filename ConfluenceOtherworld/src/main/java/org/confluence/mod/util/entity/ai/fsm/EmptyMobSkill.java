package org.confluence.mod.util.entity.ai.fsm;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.RawAnimation;

public class EmptyMobSkill<T extends Entity> extends AbstractMobSkill<T> {
    /**
     * @param anim         动画名称
     * @param timeContinue 状态持续时间
     */
    public EmptyMobSkill(RawAnimation anim, int timeContinue) {
        super(anim, timeContinue, 0);
    }

    @Override
    public void start(T mob) {
    }

    @Override
    public void tick(T mob, int time) {
    }

    @Override
    public void stop(T mob) {
    }
}
