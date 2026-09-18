package org.confluence.mod.network.c2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * 只续期或关闭服务端通过真实交互建立的对话会话。
 */
public record NPCDialogSessionPacketC2S(int entityId, boolean open) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("npc_dialog_session");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, NPCDialogSessionPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, NPCDialogSessionPacketC2S::entityId,
            PortByteBufCodecs.BOOL, NPCDialogSessionPacketC2S::open, NPCDialogSessionPacketC2S::new);

    @Override
    public void work(ServerPlayer player) {
        if (player.level().getEntity(entityId) instanceof BaseNPC npc)
            npc.updateDialogSession(player, open);
    }

    @Override
    public ResourceLocation identifier() {return ID;}

    public static void send(int entityId, boolean open) {
        PortPacketDistributor.sendToServer(new NPCDialogSessionPacketC2S(entityId, open));
    }
}
