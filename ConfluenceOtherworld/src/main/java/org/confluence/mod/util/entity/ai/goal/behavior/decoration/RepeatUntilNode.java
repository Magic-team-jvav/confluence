package org.confluence.mod.util.entity.ai.goal.behavior.decoration;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

/**
 * 重复执行直到成功或失败
 */
public class RepeatUntilNode extends DecorationNode {

    final BTStatus targetStatus;

    /**
     * @param targetStatus 若child返回targetStatus，则停止执行。取值为SUCCESS或FAILURE。
     */
    public RepeatUntilNode(BTStatus targetStatus, BTNode child) {
        super(child);
        this.targetStatus = targetStatus;
    }

    @Override
    public BTStatus execute() {

        this.child.tryStart();
        BTStatus status = this.child.execute();
        if (status == targetStatus) {
            return BTStatus.SUCCESS;
        }
        if (status == BTStatus.RUNNING) {
            return BTStatus.RUNNING;
        }
        this.child.stop();

        return BTStatus.RUNNING;
    }
}
