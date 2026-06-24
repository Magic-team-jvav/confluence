package org.confluence.mod.common.entity.ai.goal.behavior;

import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

/**
 * 行为树节点基类
 */
public abstract class BTNode extends Goal {
    protected BTStatus status = BTStatus.READY;
    protected BehaviorTreeContext context;
    private String description;

    public abstract BTStatus execute();

    @Override
    public boolean canUse() {
        if (status == BTStatus.READY) {
            return true;
        }
        return status == BTStatus.RUNNING;
    }

    public boolean isReady() {
        return canUse() && status == BTStatus.READY;
    }

    public void tryStart(){
        if(isReady()){
            start();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return status == BTStatus.RUNNING;
    }

    @Override
    public void start() {
        this.status = BTStatus.RUNNING;
        this.context = createContext();
    }

    @Override
    public void tick() {
        if (status == BTStatus.RUNNING) {
            BTStatus result = execute();
            if (result != BTStatus.RUNNING) {
                status = result;
            }
        }
    }

    @Override
    public void stop() {
        cleanup();
        status = BTStatus.READY;
        context = null;
    }

    protected BehaviorTreeContext createContext() {
        return new BehaviorTreeContext();
    }

    protected void cleanup() {
        // 子类可重写清理逻辑
    }

    public BTNode setDesc(String desc) {
        this.description = desc;
        return this;
    }

    public @Nullable String getDesc() {
        return description;
    }

    @Override
    public String toString() {
        if(description!= null) {
            return description;
        }
        return super.toString();
    }

    public BTStatus getStatus() {
        return status;
    }

    public void setStatus(BTStatus status) {
        this.status = status;
    }

    public enum BTStatus {
        READY,      // 准备执行
        RUNNING,    // 执行中
        SUCCESS,    // 执行成功
        FAILURE     // 执行失败
    }

    public static class BehaviorTreeContext {
        private Object blackboard;

        public <T> T getData(String key) {
            // 实际实现需要存储上下文数据
            return null;
        }

        public void setData(String key, Object value) {
            // 存储上下文数据
        }
    }
}
