package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public final class NPCTradeGoal extends Goal {
    private final BaseNPC npc;

    /// 创建交易期间独占跳跃、移动和朝向控制的目标。
    public NPCTradeGoal(BaseNPC npc) {
        this.npc = npc;
        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));
    }

    /// 对话、商店和重铸共用服务端会话，有效交互距离为八格。
    @Override
    public boolean canUse() {
        return npc.isAlive() && npc.getInteractingPlayer() != null;
    }

    /// 交易持续条件与启动条件完全相同，防止菜单和实体状态脱节。
    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    /// 交易开始时立即停止原有路径。
    @Override
    public void start() {
        npc.stopForInteraction();
    }

    /// 交易期间保持静止并持续面向玩家。
    @Override
    public void tick() {
        npc.stopForInteraction();
        Player player = npc.getInteractingPlayer();
        if (player != null) npc.getLookControl().setLookAt(player, 30, 30);
    }

}
