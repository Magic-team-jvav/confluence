package org.confluence.mod.common.entity.ai.bt;

import net.minecraft.world.entity.ai.goal.Goal;

/**
 * 行为树根节点，包装为 Forge Goal 注册到 goalSelector。
 */
public abstract class BTRoot extends Goal {
    protected BTNode rootNode;

    public BTRoot() {
        this.rootNode = createTree();
    }

    protected abstract BTNode createTree();

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void start() {
        rootNode.start();
    }

    @Override
    public void tick() {
        BTStatus status = rootNode.execute();
        if (status != BTStatus.RUNNING) {
            rootNode.stop();
            rootNode.start();
        }
    }

    @Override
    public void stop() {
        rootNode.stop();
    }
}
