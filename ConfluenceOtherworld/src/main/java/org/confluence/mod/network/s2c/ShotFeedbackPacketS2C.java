package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.GunEvent;

/// Server acknowledgement used to drive client-only fire feedback.
///
/// **枪械内联 G4′**：1.20 同名文件（`network/s2c/ShotFeedbackPacketS2C`）逐字搬运，
/// 原生改写 3 处（1.20 → 1.21，与批次 25 那几个包同一套）：
/// 1. `IPortPacket.S2C`（1.20 这一步还没转换到）→ Magic-Lib 的 `IPacketS2C`；
/// 2. `ResourceLocation ID` + `identifier()` → `Confluence.createType("shot_feedback")` + `type()`；
/// 3. `PortEventHandler.postEvent(...)` → 原生 `NeoForge.EVENT_BUS.post(...)`
///    （转换器这一处**没转**，属 `notes/WP4-BATCH25-WIP.md` 记的「只转换了一部分」缺陷）。
/// `work(Player)` 在客户端跑，`GunEvent.ShotConfirmed` 也是游戏总线事件（`GunEvent extends Event`）。
public enum ShotFeedbackPacketS2C implements IPacketS2C {
    INSTANCE;

    public static final Type<ShotFeedbackPacketS2C> TYPE = Confluence.createType("shot_feedback");
    public static final StreamCodec<RegistryFriendlyByteBuf, ShotFeedbackPacketS2C> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void work(Player player) {
        NeoForge.EVENT_BUS.post(new GunEvent.ShotConfirmed(player));
    }

    @Override
    public Type<ShotFeedbackPacketS2C> type() {
        return TYPE;
    }

    public static void sendTo(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, INSTANCE);
    }
}
