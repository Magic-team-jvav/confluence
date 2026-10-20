package org.confluence.mod.common.entity.monster;

import net.minecraft.server.level.ServerPlayer;

/// 火星入侵的触发接口，探测器通过回调请求启动事件。
@FunctionalInterface
public interface MartianProbeEventTrigger {
    boolean requestStart(ServerPlayer player);
}
