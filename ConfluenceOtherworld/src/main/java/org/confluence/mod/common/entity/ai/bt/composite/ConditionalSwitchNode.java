package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/// 根据实时条件在两个行为分支之间切换。
///
/// <p>优先级选择器通过节点返回状态决定抢占关系；昼夜、阶段、环境等明确的二元状态
/// 切换则使用本节点，直接根据条件选中唯一分支。条件结果改变后，旧分支会先收到
/// {@link BTNode#stop()}，新分支再收到 {@link BTNode#start()}，避免两个分支同时保留
/// 运行状态。</p>
public final class ConditionalSwitchNode extends BTNode {
    private final BooleanSupplier condition;
    private final BTNode trueBranch;
    private final BTNode falseBranch;
    private BTNode activeBranch;

    public ConditionalSwitchNode(BooleanSupplier condition, BTNode trueBranch, BTNode falseBranch) {
        this.condition = Objects.requireNonNull(condition, "condition");
        this.trueBranch = Objects.requireNonNull(trueBranch, "trueBranch");
        this.falseBranch = Objects.requireNonNull(falseBranch, "falseBranch");
    }

    @Override
    public void start() {
        switchBranch(selectBranch());
    }

    @Override
    public BTStatus execute() {
        switchBranch(selectBranch());
        return activeBranch.execute();
    }

    @Override
    public void stop() {
        if (activeBranch != null) {
            activeBranch.stop();
            activeBranch = null;
        }
    }

    private BTNode selectBranch() {
        return condition.getAsBoolean() ? trueBranch : falseBranch;
    }

    private void switchBranch(BTNode selectedBranch) {
        if (activeBranch == selectedBranch) {
            return;
        }
        if (activeBranch != null) {
            activeBranch.stop();
        }
        activeBranch = selectedBranch;
        activeBranch.start();
    }
}
