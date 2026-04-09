package org.confluence.mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import org.confluence.lib.network.IPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.data.saved.ConfluenceData;

public record AskForSoftcorePacket(boolean accept) implements IPacket {
    public static final Type<AskForSoftcorePacket> TYPE = Confluence.createType("ask_for_softcore");
    public static final StreamCodec<ByteBuf, AskForSoftcorePacket> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(AskForSoftcorePacket::new, AskForSoftcorePacket::accept);

    @Override
    public Type<AskForSoftcorePacket> type() {
        return TYPE;
    }

    @Override
    public void s2c(Player player) {
        ClientPacketHandler.setAskForSoftcoreLayer(true);
    }

    @Override
    public void c2s(ServerPlayer player) {
        ServerLevel overworld = player.server.overworld();
        if (accept) {
            overworld.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, player.server);
            player.sendSystemMessage(Component.translatable("confluence.difficulty_notice.sure.done"));
        } else {
            ConfluenceData.get(overworld).setStopAskForSoftcore(true);
            player.sendSystemMessage(Component.translatable("confluence.difficulty_notice.never.done"));
        }
    }
}
