package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientNPCDialogs;
import org.confluence.mod.common.entity.npc.dialog.NPCDialog;
import org.confluence.mod.common.entity.npc.dialog.NPCDialogLoader;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Map;

public record NPCDialogSyncPacketS2C(
        Map<EntityType<?>, NPCDialog> dialogs) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("npc_dialog_sync");
    public static final PortStreamCodec<ByteBuf, NPCDialogSyncPacketS2C> STREAM_CODEC = PortByteBufCodecs.fromCodec(NPCDialog.MAP_CODEC)
            .map(NPCDialogSyncPacketS2C::new, NPCDialogSyncPacketS2C::dialogs);

    @Override
    public ResourceLocation identifier() {return ID;}

    @Override
    public void handle(Context context) {
        context.enqueueWork(() -> ClientNPCDialogs.update(dialogs));
    }

    @Override
    public void work(Player player) {ClientNPCDialogs.update(dialogs);}

    public static void sendToClient(ServerPlayer player) {
        PortPacketDistributor.sendToPlayer(player, new NPCDialogSyncPacketS2C(NPCDialogLoader.getInstance().getDialogs()));
    }
}
