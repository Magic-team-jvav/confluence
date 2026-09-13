package org.confluence.mod.common.entity.ai.bt;

public abstract class BTNode {
    public void start() {}
    public void stop() {}

    /// 只判断启动条件或准备路径，不修改实体的导航、运动和攻击状态。
    public boolean canStart() {
        return true;
    }

    public abstract BTStatus execute();
}
