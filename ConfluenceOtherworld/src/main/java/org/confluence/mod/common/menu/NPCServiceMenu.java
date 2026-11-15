package org.confluence.mod.common.menu;

import org.confluence.mod.common.entity.npc.BaseNPC;
import org.jetbrains.annotations.Nullable;

/// NPC 服务菜单的来源；客户端未同步来源时可为空，服务端必须绑定真实 NPC。
public interface NPCServiceMenu {
    @Nullable BaseNPC getNPC();
}
