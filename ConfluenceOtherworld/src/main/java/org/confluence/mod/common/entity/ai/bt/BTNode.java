package org.confluence.mod.common.entity.ai.bt;

public abstract class BTNode {
    public void start() {}
    public void stop() {}
    public abstract BTStatus execute();
}
