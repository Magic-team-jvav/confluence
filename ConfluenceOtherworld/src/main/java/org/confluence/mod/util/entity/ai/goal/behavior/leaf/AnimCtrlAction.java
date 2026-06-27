package org.confluence.mod.util.entity.ai.goal.behavior.leaf;

import org.confluence.mod.api.entity.ISharedFlagControllerHolder;
import org.confluence.mod.api.entity.SharedFlagController;
import software.bernie.geckolib.animatable.GeoEntity;

/// geo动画状态触发器，并将状态绑定一个共享标志位，给客户端使用
public class AnimCtrlAction<T extends GeoEntity & ISharedFlagControllerHolder> extends SyncFlagAction<T> {
    private final String controllerName;
    private final String animationName;

    public AnimCtrlAction(
            T entity,
            String controllerName,
            String animationName,
            SharedFlagController.SharedFlag sharedFlag,
            boolean isEnable
    ) {
        super(entity, sharedFlag, isEnable);
        this.controllerName = controllerName;
        this.animationName = animationName;
    }

    @Override
    public void start() {
        super.start();
        if (isEnable) {
            entity.triggerAnim(controllerName, animationName);
        } else {
            entity.stopTriggeredAnimation(controllerName, animationName);
        }
    }
}
