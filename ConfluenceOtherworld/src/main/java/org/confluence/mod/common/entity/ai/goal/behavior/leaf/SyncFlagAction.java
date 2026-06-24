package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import org.confluence.mod.api.entity.ISharedFlagControllerHolder;
import org.confluence.mod.api.entity.SharedFlagController;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;

/**
 * 同步共享状态
 */
public class SyncFlagAction<T extends ISharedFlagControllerHolder> extends BTNode {
    protected final T entity;
    protected final SharedFlagController.SharedFlag sharedFlag;
    protected final boolean isEnable;

    public SyncFlagAction(T entity,
                          SharedFlagController.SharedFlag sharedFlag,
                          boolean isEnable) {
        this.entity = entity;
        this.sharedFlag = sharedFlag;
        this.isEnable = isEnable;

    }

    @Override
    public BTNode.BTStatus execute() {
        return BTStatus.SUCCESS;
    }

    @Override
    public void start() {
        super.start();
        entity.getSharedFlagController().setFlag(sharedFlag, isEnable);
    }
}
