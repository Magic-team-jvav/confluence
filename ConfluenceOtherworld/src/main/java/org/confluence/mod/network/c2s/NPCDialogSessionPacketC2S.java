package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;

/**
 * 只续期或关闭服务端通过真实交互建立的对话会话。
 *
 * 1.20 是 `implements IPortPacket.C2S`；1.21 的 `CustomPacketPayload` 是扁平接口、**没有 `C2S`**，
 * 原生对应物是本仓库 Magic-Lib 的 `IPacketC2S`（`type()` + `work(ServerPlayer)`）。
 */
public record NPCDialogSessionPacketC2S(int entityId, boolean open) implements IPacketC2S {
    public static final Type<NPCDialogSessionPacketC2S> TYPE = Confluence.createType("npc_dialog_session");
    public static final StreamCodec<RegistryFriendlyByteBuf, NPCDialogSessionPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, NPCDialogSessionPacketC2S::entityId,
            ByteBufCodecs.BOOL, NPCDialogSessionPacketC2S::open, NPCDialogSessionPacketC2S::new);

    @Override
    public void work(ServerPlayer player) {
        if (player.level().getEntity(entityId) instanceof BaseNPC npc)
            npc.updateDialogSession(player, open);
    }

    @Override
    public Type<NPCDialogSessionPacketC2S> type() {
        return TYPE;
    }

    public static void send(int entityId, boolean open) {
        PacketDistributor.sendToServer(new NPCDialogSessionPacketC2S(entityId, open));
    }
}
