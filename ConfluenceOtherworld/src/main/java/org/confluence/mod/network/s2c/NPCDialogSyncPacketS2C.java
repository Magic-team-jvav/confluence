package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientNPCDialogs;
import org.confluence.mod.common.entity.npc.dialog.NPCDialog;
import org.confluence.mod.common.entity.npc.dialog.NPCDialogLoader;

import java.util.Map;

public record NPCDialogSyncPacketS2C(Map<EntityType<?>, NPCDialog> dialogs) implements IPacketS2C {
    public static final Type<NPCDialogSyncPacketS2C> TYPE = Confluence.createType("npc_dialog_sync");
    public static final StreamCodec<ByteBuf, NPCDialogSyncPacketS2C> STREAM_CODEC = ByteBufCodecs.fromCodec(NPCDialog.MAP_CODEC)
            .map(NPCDialogSyncPacketS2C::new, NPCDialogSyncPacketS2C::dialogs);

    @Override
    public Type<NPCDialogSyncPacketS2C> type() {return TYPE;}

    @Override
    public void work(Player player) {ClientNPCDialogs.update(dialogs);}

    public static void sendToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new NPCDialogSyncPacketS2C(NPCDialogLoader.getInstance().getDialogs()));
    }
}
