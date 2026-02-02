package org.confluence.terraentity.client.animation.api.context;

/**
 * 骨骼动画信息上下文
 */
public class AnimatorContext implements IAnimatorContext {
    public double usingTime;

    public AnimatorContext(double usingTime) {
        this.usingTime = usingTime;
    }

    public double getUsingTime() {
        return usingTime;
    }

    public void setUsingTime(double usingTime) {
        this.usingTime = usingTime;
    }


}
