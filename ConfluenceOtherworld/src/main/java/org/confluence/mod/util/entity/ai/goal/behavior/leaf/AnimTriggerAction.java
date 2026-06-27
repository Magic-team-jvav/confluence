package org.confluence.mod.util.entity.ai.goal.behavior.leaf;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;
import software.bernie.geckolib.animatable.GeoEntity;

/**
 * 仅触发动画，而不需要同步给客户端状态
 */
public class AnimTriggerAction extends BTNode {

    final String controllerName;
    final String animationName;
    final GeoEntity entity;
    public AnimTriggerAction(GeoEntity entity,
                          String controllerName,
                          String animationName) {
        this.entity = entity;
        this.controllerName = controllerName;
        this.animationName = animationName;
    }

    @Override
    public BTStatus execute() {
        return BTStatus.SUCCESS;
    }

    @Override
    public void start() {
        super.start();
        entity.triggerAnim(controllerName, animationName);
    }
}
