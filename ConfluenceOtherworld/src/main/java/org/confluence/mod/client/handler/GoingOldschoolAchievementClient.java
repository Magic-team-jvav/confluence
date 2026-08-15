package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.c2s.GoingOldschoolPacketC2S;

/**
 * 保存标题界面触发的“致敬经典”成就请求。
 *
 * <p>玩家在创建或选择世界前也能拖动太阳、月亮，此时尚无服务端连接。请求会暂存在
 * 当前客户端进程中，并在进入世界后交给服务端的原版成就进度处理；已经位于世界中时则
 * 立即发送。客户端自身不创建或保存伪造的成就进度。
 */
public final class GoingOldschoolAchievementClient {
    private static boolean pending;

    private GoingOldschoolAchievementClient() {
    }

    public static void requestAward() {
        pending = true;
        flushPendingAward();
    }

    /**
     * 在玩法连接可用后发送尚未处理的请求。
     */
    public static void flushPendingAward() {
        if (!pending) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) {
            return;
        }
        pending = false;
        Confluence.NETWORK_HANDLER.sendToServer(GoingOldschoolPacketC2S.INSTANCE);
    }
}
